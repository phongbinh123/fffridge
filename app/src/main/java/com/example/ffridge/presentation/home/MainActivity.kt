package com.example.ffridge.presentation.home

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ffridge.R
import com.example.ffridge.databinding.ActivityMainBinding
import com.example.ffridge.databinding.DialogEditFoodBinding
import com.example.ffridge.domain.model.Food
import com.example.ffridge.presentation.addfood.AddFoodActivity
import com.example.ffridge.presentation.scan.ScanFoodActivity
import com.example.ffridge.presentation.recipe.RecipeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: FoodListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = FoodListAdapter(
            onDeleteClick = { food ->
                viewModel.deleteFood(food)
                Toast.makeText(this, "Đã xóa ${food.name}", Toast.LENGTH_SHORT).show()
            },
            onEditClick = { food -> showEditDialog(food) }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun showEditDialog(food: Food) {
        val dialogBinding = DialogEditFoodBinding.inflate(LayoutInflater.from(this))
        val calendar = Calendar.getInstance()
        calendar.time = food.storedDate

        // Điền dữ liệu hiện tại
        dialogBinding.etEditName.setText(food.name)
        dialogBinding.etEditAmount.setText(food.amount)
        dialogBinding.etEditCalories.setText(if (food.calories > 0) food.calories.toString() else "")
        dialogBinding.etEditDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(food.storedDate))

        // DatePicker cho ngày hết hạn
        dialogBinding.etEditDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    dialogBinding.etEditDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Nút Cancel
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        // Nút Save
        dialogBinding.btnSave.setOnClickListener {
            val newName = dialogBinding.etEditName.text.toString()
            val newAmount = dialogBinding.etEditAmount.text.toString()
            val newCalories = dialogBinding.etEditCalories.text.toString().toDoubleOrNull() ?: 0.0

            if (newName.isBlank() || newAmount.isBlank()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedFood = food.copy(
                name = newName,
                amount = newAmount,
                calories = newCalories,
                storedDate = calendar.time
            )

            viewModel.updateFood(updatedFood)
            dialog.dismiss()
            Toast.makeText(this, "Đã cập nhật ${newName}!", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.foods.collect { foods ->
                adapter.submitList(foods)
            }
        }
    }

    private fun setupClickListeners() {
        // Nút "Scan Item"
        binding.btnBigScan.setOnClickListener {
            startActivity(Intent(this, ScanFoodActivity::class.java))
        }

        // Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_fridge -> {
                    // Đang ở màn hình này rồi
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddFoodActivity::class.java))
                    true
                }
                R.id.nav_suggestions -> {
                    // MỞ MÀN HÌNH RECIPE
                    startActivity(Intent(this, RecipeActivity::class.java))
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

        // Search View
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // TODO: Implement search
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // TODO: Implement real-time search
                return false
            }
        })
    }
}
