package com.example.ffridge.presentation.recipe

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ffridge.databinding.ActivityRecipeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeBinding
    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRandomRecipe.setOnClickListener {
            viewModel.fetchRandomRecipe()
        }

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.recipeState.collect { state ->
                when (state) {
                    is RecipeState.Empty -> {
                        binding.tvRecipeTitle.text = "Nhấn nút để lấy công thức"
                        binding.tvInstructions.text = ""
                    }
                    is RecipeState.Loading -> {
                        binding.tvRecipeTitle.text = "Đang tải..."
                    }
                    is RecipeState.Success -> {
                        binding.tvRecipeTitle.text = state.recipe.title
                        binding.tvInstructions.text = state.recipe.instructions
                    }
                    is RecipeState.Error -> {
                        binding.tvRecipeTitle.text = "Lỗi"
                        Toast.makeText(this@RecipeActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
