package com.example.ffridge.presentation.addfood

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.example.ffridge.presentation.scan.ScanCodeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFoodBinding
    private val viewModel: AddFoodViewModel by viewModels()
    private val calendar = Calendar.getInstance()

    // Launcher cho Voice Input
    private val voiceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            spokenText?.let {
                binding.etName.setText(it)
                // Tự động tìm kiếm sau khi nói xong
                viewModel.searchFoodInfo(it)
                Toast.makeText(this, "Đã nhận diện: $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // --- 1. Xử lý Tabs ---
        
        // Tab Scan: Chuyển sang màn hình Scan
        binding.tabScan.setOnClickListener {
            startActivity(Intent(this, ScanCodeActivity::class.java))
            // Không gọi finish() để người dùng có thể back lại nếu muốn
        }

        // Tab Manual: (Đang ở đây) - Chỉ cập nhật UI cho đẹp
        binding.tabManual.setOnClickListener {
            updateTabStyles(isManual = true)
        }

        // Tab Voice: Gọi Google Voice Input
        binding.tabVoice.setOnClickListener {
            updateTabStyles(isManual = false) // Highlight tab Voice
            startVoiceInput()
        }

        // --- 2. Các nút chức năng khác ---
        
        // Nút Search thủ công
        binding.btnSearchInfo.setOnClickListener {
            val query = binding.etName.text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchFoodInfo(query)
                // Ẩn bàn phím
                val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
            }
        }
        
        // Tự động search khi ô tên mất focus (Debounce đơn giản)
        binding.etName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val query = binding.etName.text.toString()
                if (query.isNotEmpty()) viewModel.searchFoodInfo(query)
            }
        }

        // Date Picker
        binding.etExpiryDate.setOnClickListener { showDatePicker() }

        // Save Button
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val amount = binding.etQuantity.text.toString()
            
            if (name.isBlank() || amount.isBlank() || binding.etExpiryDate.text.toString().isBlank()) {
                Toast.makeText(this, "Vui lòng nhập tên, số lượng và ngày hết hạn", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.saveFood(name, amount, calendar.time)
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
        // Logic đổi màu nền Tab đơn giản để người dùng biết đang chọn cái nào
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
                    
                    if (state.foundImageUri != null) {
                        binding.imgPreview.visibility = View.VISIBLE
                        // Load ảnh bằng Coil
                        binding.imgPreview.load(state.foundImageUri) {
                            crossfade(true)
                            placeholder(R.mipmap.ic_launcher)
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