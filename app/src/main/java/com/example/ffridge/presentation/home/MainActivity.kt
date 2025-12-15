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
import com.example.ffridge.databinding.ActivityMainBinding
import com.example.ffridge.presentation.addfood.AddFoodActivity
import com.example.ffridge.presentation.recipe.RecipeActivity
import com.example.ffridge.presentation.scan.ScanCodeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint // <-- QUAN TRỌNG
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Hilt tự động inject ViewModel
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var foodAdapter: FoodListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupButtons()
        setupSearch()
        observeData()
    }

    private fun setupRecyclerView() {
        foodAdapter = FoodListAdapter { food ->
            viewModel.deleteFood(food)
            Toast.makeText(this, "Đã xóa ${food.name}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.apply {
            adapter = foodAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupButtons() {
        binding.btnNavAdd.setOnClickListener { startActivity(Intent(this, AddFoodActivity::class.java)) }
        binding.btnNavScan.setOnClickListener { startActivity(Intent(this, ScanCodeActivity::class.java)) }
        binding.btnNavRecipe.setOnClickListener { startActivity(Intent(this, RecipeActivity::class.java)) }
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