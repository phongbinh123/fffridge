package com.example.ffridge

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ffridge.data.local.FoodItem
import com.example.ffridge.databinding.ActivityAddFoodBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddFoodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddFoodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            saveFood()
        }
    }

    private fun saveFood() {
        val name = binding.etFoodName.text.toString()
        val amount = binding.etAmount.text.toString()

        if (name.isBlank() || amount.isBlank()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
        val newItem = FoodItem(name = name, amount = amount, storedDate = currentDate)

        val database = (application as FfridgeApplication).database
        lifecycleScope.launch {
            database.foodDao().insert(newItem)
            Toast.makeText(this@AddFoodActivity, "Đã thêm $name", Toast.LENGTH_SHORT).show()
            finish() // Quay về màn hình chính
        }
    }
}