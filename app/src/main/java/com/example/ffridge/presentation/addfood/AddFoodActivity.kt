package com.example.ffridge.presentation.addfood

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.example.ffridge.R
import com.example.ffridge.databinding.ActivityAddFoodBinding
import com.example.ffridge.presentation.scan.ScanFoodActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFoodBinding
    private val viewModel: AddFoodViewModel by viewModels()
    private val calendar = Calendar.getInstance()

    private var selectedImageUri: Uri? = null

    // Voice Input Launcher
    private val voiceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            spokenText?.let {
                binding.etName.setText(it)
                viewModel.searchFoodInfo(it)
                Toast.makeText(this, "Đã nhận diện: $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Gallery Picker Launcher
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.imgPreview.load(it) {
                crossfade(true)
            }
            binding.iconAddImage.visibility = View.GONE
            viewModel.setUserSelectedImage(it.toString())
        }
    }

    // Camera Launcher
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let { bitmap ->
                // Lưu bitmap vào file tạm
                val file = File(cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                selectedImageUri = Uri.fromFile(file)

                binding.imgPreview.load(bitmap) {
                    crossfade(true)
                }
                binding.iconAddImage.visibility = View.GONE
                viewModel.setUserSelectedImage(selectedImageUri.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
        handleScannedData()  // ← ĐÃ CÓ
    }

    private fun setupUI() {
        // --- 1. Xử lý Tabs ---
        binding.tabScan.setOnClickListener {
            startActivity(Intent(this, ScanFoodActivity::class.java))
        }

        binding.tabManual.setOnClickListener {
            updateTabStyles(isManual = true)
        }

        binding.tabVoice.setOnClickListener {
            updateTabStyles(isManual = false)
            startVoiceInput()
        }

        // --- 2. Xử lý chọn ảnh ---
        binding.btnPickFromGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnTakePhoto.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        }

        // Click vào ảnh để chọn lại
        binding.imgPreview.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // --- 3. Các nút chức năng khác ---
        binding.btnSearchInfo.setOnClickListener {
            val query = binding.etName.text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchFoodInfo(query)
                val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
            } else {
                Toast.makeText(this, "Vui lòng nhập tên thực phẩm", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val query = binding.etName.text.toString()
                if (query.isNotEmpty()) viewModel.searchFoodInfo(query)
            }
        }

        binding.etExpiryDate.setOnClickListener { showDatePicker() }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val amount = binding.etQuantity.text.toString()

            if (name.isBlank() || amount.isBlank() || binding.etExpiryDate.text.toString().isBlank()) {
                Toast.makeText(this, "Vui lòng nhập tên, số lượng và ngày hết hạn", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.saveFood(name, amount, calendar.time)
        }

        // --- 4. THÊM: Xử lý Bottom Navigation ---
        setupBottomNavigation()
    }

    // ← THÊM HÀM NÀY: Xử lý dữ liệu từ ScanFoodActivity
    private fun handleScannedData() {
        intent?.let {
            val scannedName = it.getStringExtra("SCANNED_NAME")
            val scannedAmount = it.getStringExtra("SCANNED_AMOUNT")
            val scannedCalories = it.getDoubleExtra("SCANNED_CALORIES", 0.0)
            val scannedExpiryDate = it.getStringExtra("SCANNED_EXPIRY_DATE")
            val scannedImageUri = it.getStringExtra("SCANNED_IMAGE_URI")

            // Chỉ fill data nếu có dữ liệu từ scan
            if (!scannedName.isNullOrEmpty()) {
                // 1. Fill name → binding.etName
                binding.etName.setText(scannedName)

                // 2. Fill amount → binding.etQuantity
                binding.etQuantity.setText(scannedAmount ?: "1 pcs")

                // 3. Fill calories → binding.etCalories
                if (scannedCalories > 0) {
                    binding.etCalories.setText(scannedCalories.toString())
                }

                // 4. Fill expiry date → binding.etExpiryDate
                if (!scannedExpiryDate.isNullOrEmpty()) {
                    binding.etExpiryDate.setText(scannedExpiryDate)
                    // Sync calendar với ngày đã scan
                    try {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        calendar.time = sdf.parse(scannedExpiryDate) ?: calendar.time
                    } catch (e: Exception) {
                        // Ignore parse error
                    }
                }

                // 5. Fill image → selectedImageUri
                if (!scannedImageUri.isNullOrEmpty()) {
                    selectedImageUri = Uri.parse(scannedImageUri)
                    binding.imgPreview.load(selectedImageUri) {
                        crossfade(true)
                    }
                    binding.iconAddImage.visibility = View.GONE
                    viewModel.setUserSelectedImage(scannedImageUri)
                }

                Toast.makeText(this, "✅ Scanned data loaded! Review and save", Toast.LENGTH_LONG).show()
            }
        }
    }

    // ← THÊM HÀM NÀY: Xử lý Bottom Navigation
    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_add  // Highlight tab Add

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_fridge -> {
                    // Quay về MainActivity
                    finish()
                    true
                }
                R.id.nav_add -> {
                    // Đang ở màn hình này rồi
                    true
                }
                R.id.nav_suggestions -> {
                    // TODO: Mở màn hình suggestions
                    Toast.makeText(this, "Suggestions coming soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_settings -> {
                    // TODO: Mở màn hình settings
                    Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Nói tên món ăn (ví dụ: 1 quả táo)")
        }
        try {
            voiceLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Thiết bị không hỗ trợ nhập giọng nói", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTabStyles(isManual: Boolean) {
        if (isManual) {
            binding.tabManual.setBackgroundResource(R.drawable.bg_tab_selected)
            binding.tabManual.setTextColor(Color.WHITE)
            binding.tabVoice.setBackgroundResource(R.drawable.bg_tab_unselected)
            binding.tabVoice.setTextColor(getColor(R.color.brand_teal))
        } else {
            binding.tabVoice.setBackgroundResource(R.drawable.bg_tab_selected)
            binding.tabVoice.setTextColor(Color.WHITE)
            binding.tabManual.setBackgroundResource(R.drawable.bg_tab_unselected)
            binding.tabManual.setTextColor(getColor(R.color.brand_teal))
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.etExpiryDate.setText(format.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    if (state.foundCalories > 0) {
                        binding.etCalories.setText(state.foundCalories.toString())
                    }

                    // Chỉ load ảnh từ API nếu người dùng chưa chọn ảnh
                    if (state.foundImageUri != null && selectedImageUri == null) {
                        binding.imgPreview.visibility = View.VISIBLE
                        binding.iconAddImage.visibility = View.GONE
                        binding.imgPreview.load(state.foundImageUri) {
                            crossfade(true)
                            placeholder(R.drawable.ic_default_food)
                            error(R.drawable.ic_default_food)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is AddFoodViewModel.UiEvent.SaveSuccess -> {
                            Toast.makeText(this@AddFoodActivity, "Đã lưu thành công!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        is AddFoodViewModel.UiEvent.ShowMessage -> {
                            Toast.makeText(this@AddFoodActivity, event.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
