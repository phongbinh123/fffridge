package com.example.ffridge

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ffridge.data.local.FoodItem
import com.example.ffridge.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val foodAdapter = FoodListAdapter { foodItem -> deleteFood(foodItem) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupButtons()
        observeData()
        setupSearch()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = foodAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupButtons() {
        binding.btnNavAdd.setOnClickListener {
            startActivity(Intent(this, AddFoodActivity::class.java))
        }
        binding.btnNavScan.setOnClickListener {
            startActivity(Intent(this, ScanCodeActivity::class.java))
        }
        binding.btnNavRecipe.setOnClickListener {
            startActivity(Intent(this, RecipeActivity::class.java))
        }
    }

    private fun observeData() {
        val database = (application as FfridgeApplication).database
        lifecycleScope.launch {
            database.foodDao().getAllFoods().collect { foodList ->
                foodAdapter.submitList(foodList)
            }
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val database = (application as FfridgeApplication).database
                val queryStr = newText ?: ""
                lifecycleScope.launch {
                    database.foodDao().searchFoods(queryStr).collect {
                        foodAdapter.submitList(it)
                    }
                }
                return true
            }
        })
    }

    private fun deleteFood(food: FoodItem) {
        val database = (application as FfridgeApplication).database
        lifecycleScope.launch {
            database.foodDao().delete(food)
            Toast.makeText(this@MainActivity, "Đã xóa ${food.name}", Toast.LENGTH_SHORT).show()
        }
    }
}