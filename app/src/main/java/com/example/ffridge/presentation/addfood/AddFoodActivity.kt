package com.example.ffridge.presentation.addfood

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ffridge.databinding.ActivityAddFoodBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddFoodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddFoodBinding
    private val viewModel: AddFoodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            val name = binding.etFoodName.text.toString()
            val amount = binding.etAmount.text.toString()
            viewModel.saveFood(name, amount)
        }

        observeEvents()
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is AddFoodViewModel.UiEvent.SaveSuccess -> {
                        Toast.makeText(this@AddFoodActivity, "Đã lưu!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is AddFoodViewModel.UiEvent.ShowError -> {
                        Toast.makeText(this@AddFoodActivity, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}