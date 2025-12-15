package com.example.ffridge.presentation.scan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
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
import com.example.ffridge.databinding.ActivityScanCodeBinding
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@AndroidEntryPoint // Kích hoạt Hilt cho Activity này
class ScanCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanCodeBinding

    // Inject ScanViewModel
    private val viewModel: ScanViewModel by viewModels()

    // Cờ để ngăn việc gọi API liên tục khi quét cùng một mã
    private var isProcessing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bắt đầu quy trình xin quyền và khởi tạo camera
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        observeViewModel()
    }

    // Lắng nghe các sự kiện (thành công, thất bại, đang tải) từ ViewModel
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.scanEvent.collect { event ->
                when (event) {
                    is ScanViewModel.ScanEvent.Loading -> {
                        // Tạm thời hiển thị loading, thường là trên giao diện.
                        Toast.makeText(this@ScanCodeActivity, "Đang xử lý...", Toast.LENGTH_SHORT).show()
                    }
                    is ScanViewModel.ScanEvent.Success -> {
                        Toast.makeText(this@ScanCodeActivity, "Đã thêm: ${event.productName}", Toast.LENGTH_LONG).show()
                        isProcessing = false // Reset cờ để cho phép quét tiếp
                        finish() // Hoàn thành và quay lại màn hình chính
                    }
                    is ScanViewModel.ScanEvent.Error -> {
                        Toast.makeText(this@ScanCodeActivity, event.message, Toast.LENGTH_SHORT).show()
                        isProcessing = false // Reset cờ
                    }
                }
            }
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
                // Tận dụng Executor của CameraX để phân tích hình ảnh
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
                            // Dùng cờ isProcessing để tránh quét 1 mã nhiều lần trong 1 giây
                            isProcessing = true

                            // Gửi mã UPC lên ViewModel để xử lý logic API và DB
                            viewModel.onBarcodeScanned(rawValue)
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

    // --- Xử lý quyền truy cập ---
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

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