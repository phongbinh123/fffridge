package com.example.ffridge.presentation.recipe

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ffridge.R
import com.example.ffridge.data.local.LocalRecipeDataSource
import com.example.ffridge.databinding.ActivityRecipeDetailBinding

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding

    companion object {
        const val EXTRA_RECIPE_ID = "recipe_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recipeId = intent.getIntExtra(EXTRA_RECIPE_ID, -1)
        if (recipeId != -1) {
            loadRecipe(recipeId)
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID công thức", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadRecipe(recipeId: Int) {
        // Load từ LocalRecipeDataSource
        val recipe = LocalRecipeDataSource.getRecipeById(recipeId)

        if (recipe != null) {
            binding.apply {
                collapsingToolbar.title = recipe.title
                tvRecipeTitle.text = recipe.title
                tvCookingTime.text = "⏱️ ${recipe.cookingTime}"
                tvDifficulty.text = "📊 ${recipe.difficulty}"

                val ingredientText = recipe.ingredients.joinToString("\n") { "• $it" }
                tvIngredients.text = ingredientText

                tvDescription.text = recipe.description

                Glide.with(this@RecipeDetailActivity)
                    .load(recipe.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivRecipeImage)
            }
        } else {
            Toast.makeText(this, "Không tìm thấy công thức", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
