package com.example.ffridge

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.ffridge.data.local.FoodItem
import com.example.ffridge.data.remote.RetrofitClient
import com.example.ffridge.databinding.ActivityScanCodeBinding
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class ScanCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanCodeBinding
    private var isProcessing = false // Tránh quét liên tục 1 mã

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    processImageProxy(imageProxy)
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Lỗi khởi tạo camera", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null && !isProcessing) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        if (rawValue != null) {
                            isProcessing = true // Dừng quét tiếp
                            fetchProductInfo(rawValue)
                            break
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun fetchProductInfo(upc: String) {
        runOnUiThread {
            Toast.makeText(this, "Đã quét mã: $upc. Đang tìm kiếm...", Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getFoodByUPC(upc)
                if (response.isSuccessful && response.body() != null) {
                    val product = response.body()!!
                    val name = product.itemName ?: "Sản phẩm không tên"
                    val amount = "${product.servingSizeQty ?: 1} ${product.servingSizeUnit ?: "phần"}"

                    saveToDatabase(name, amount)
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ScanCodeActivity, "Không tìm thấy sản phẩm!", Toast.LENGTH_SHORT).show()
                        isProcessing = false // Cho phép quét lại
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@ScanCodeActivity, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
                    isProcessing = false
                }
            }
        }
    }

    private fun saveToDatabase(name: String, amount: String) {
        val database = (application as FfridgeApplication).database
        val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
        val item = FoodItem(name = name, amount = amount, storedDate = currentDate)

        lifecycleScope.launch {
            database.foodDao().insert(item)
            runOnUiThread {
                Toast.makeText(this@ScanCodeActivity, "Đã thêm: $name", Toast.LENGTH_LONG).show()
                finish() // Quay về màn hình chính
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // Xử lý khi người dùng cho phép quyền camera
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Cần quyền Camera để hoạt động", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}