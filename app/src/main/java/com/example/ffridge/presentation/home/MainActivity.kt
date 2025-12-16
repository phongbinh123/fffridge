package com.example.ffridge.presentation.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ffridge.R
import com.example.ffridge.databinding.ActivityMainBinding
import com.example.ffridge.presentation.addfood.AddFoodActivity
import com.example.ffridge.presentation.recipe.RecipeActivity
import com.example.ffridge.presentation.scan.ScanCodeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var foodAdapter: FoodListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupBottomNavigation()
        setupSearch()
        observeData()
    }

    private fun setupRecyclerView() {
        foodAdapter = FoodListAdapter(
            // Logic Xóa
            onDeleteClick = { food ->
                viewModel.deleteFood(food)
                Toast.makeText(this, "Đã xóa ${food.name}", Toast.LENGTH_SHORT).show()
            },

            // Logic Sửa (MỚI)
            onEditClick = { food ->
                val intent = Intent(this, AddFoodActivity::class.java)
                // Truyền dữ liệu sang màn hình AddFood để hiển thị lại
                intent.putExtra("food_id", food.id)
                intent.putExtra("food_name", food.name)
                intent.putExtra("food_amount", food.amount)
                intent.putExtra("food_calories", food.calories)
                intent.putExtra("food_date", food.storedDate.time) // Truyền Long
                intent.putExtra("food_image", food.imageUri)

                startActivity(intent)
            },

            // Logic Click vào thẻ
            onRootClick = { food ->
                // For now, just show a toast
                Toast.makeText(this, "Clicked on ${food.name}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerView.apply {
            adapter = foodAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupBottomNavigation() {
        binding.btnBigScan.setOnClickListener {
            startActivity(Intent(this, ScanCodeActivity::class.java))
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_fridge -> {
                    binding.recyclerView.smoothScrollToPosition(0)
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddFoodActivity::class.java))
                    false
                }
                R.id.nav_suggestions -> {
                    startActivity(Intent(this, RecipeActivity::class.java))
                    false
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onSearchQueryChanged(newText ?: "")
                return true
            }
        })
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.foodListState.collect { foods ->
                    foodAdapter.submitList(foods)
                }
            }
        }
    }
}