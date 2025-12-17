package com.example.ffridge.presentation.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.ffridge.data.remote.FoodAnalysisResult
import com.example.ffridge.databinding.ActivityScanFoodBinding
import com.example.ffridge.presentation.addfood.AddFoodActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class ScanFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanFoodBinding
    private val viewModel: ScanFoodViewModel by viewModels()

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var capturedImageUri: Uri? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (checkCameraPermission()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setupClickListeners()
        observeViewModel()
    }

    private fun checkCameraPermission() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun setupClickListeners() {
        binding.btnCapture.setOnClickListener {
            takePhoto()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.btnCapture.isEnabled = false

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = imageProxyToBitmap(image)

                    // Lưu ảnh vào file
                    val file = File(cacheDir, "scanned_food_${System.currentTimeMillis()}.jpg")
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    }
                    capturedImageUri = Uri.fromFile(file)

                    image.close()

                    lifecycleScope.launch {
                        viewModel.analyzeFood(bitmap)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@ScanFoodActivity, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.btnCapture.isEnabled = true
                }
            }
        )
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        // Rotate if needed
        val matrix = Matrix()
        matrix.postRotate(image.imageInfo.rotationDegrees.toFloat())
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        return bitmap
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.analysisResult.collect { result ->
                result?.let {
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.btnCapture.isEnabled = true
                    showResultDialog(it)
                }
            }
        }
    }

    private fun showResultDialog(result: FoodAnalysisResult) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Food Detected! 🍎")
            .setMessage("""
                📝 Name: ${result.name}
                📦 Amount: ${result.amount}
                🔥 Calories: ${result.calories.toInt()} kcal
                📅 Expiry Date: ${result.expiryDate}
            """.trimIndent())
            .setPositiveButton("Add to Fridge") { _, _ ->
                navigateToAddFood(result)
            }
            .setNegativeButton("Scan Again") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun navigateToAddFood(result: FoodAnalysisResult) {
        val intent = Intent(this, AddFoodActivity::class.java).apply {
            // ĐỒNG BỘ CHÍNH XÁC với AddFoodActivity
            putExtra("SCANNED_NAME", result.name)           // → binding.etName
            putExtra("SCANNED_AMOUNT", result.amount)       // → binding.etQuantity
            putExtra("SCANNED_CALORIES", result.calories)   // → binding.etCalories
            putExtra("SCANNED_EXPIRY_DATE", result.expiryDate) // → binding.etExpiryDate
            putExtra("SCANNED_IMAGE_URI", capturedImageUri?.toString()) // → selectedImageUri
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
