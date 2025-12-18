package com.example.ffridge.presentation.recipe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ffridge.R
import com.example.ffridge.databinding.ActivityRecipeBinding
import com.example.ffridge.domain.model.Recipe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeBinding
    private val viewModel: RecipeViewModel by viewModels()
    private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        observeState()
        observeSmartSuggestion()
    }

    private fun setupRecyclerView() {
        adapter = RecipeAdapter { recipe ->
            showRecipeDetail(recipe)
        }
        binding.rvRecipes.layoutManager = LinearLayoutManager(this)
        binding.rvRecipes.adapter = adapter
    }

    private fun setupClickListeners() {
        // Random recipe FAB
        binding.fabRandom.setOnClickListener {
            viewModel.fetchRandomRecipe()
        }

        // Search Google
        binding.btnSearch.setOnClickListener {
            searchRecipeOnGoogle()
        }

        binding.etSearchRecipe.setOnEditorActionListener { _, _, _ ->
            searchRecipeOnGoogle()
            true
        }

        // Refresh smart suggestion
        binding.btnRefreshSuggestion.setOnClickListener {
            viewModel.refreshSmartSuggestion()
        }

        // View suggested recipe detail
        binding.btnViewSuggestedRecipe.setOnClickListener {
            val state = viewModel.smartSuggestion.value
            if (state is SmartSuggestionState.Success) {
                showRecipeDetail(state.recipe)
            }
        }
    }

    private fun searchRecipeOnGoogle() {
        val query = binding.etSearchRecipe.text.toString().trim()
        if (query.isEmpty()) {
            Toast.makeText(this, "Enter recipe name to search", Toast.LENGTH_SHORT).show()
            return
        }

        val searchUrl = "https://www.google.com/search?q=${Uri.encode("$query recipe")}"
        CustomTabsIntent.Builder()
            .setToolbarColor(resources.getColor(R.color.brand_teal, null))
            .build()
            .launchUrl(this, Uri.parse(searchUrl))
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.recipeState.collect { state ->
                when (state) {
                    is RecipeState.Loading -> {
                        // TODO: Show loading
                    }
                    is RecipeState.Success -> {
                        adapter.submitList(state.recipes)
                    }
                    is RecipeState.SingleRecipe -> {
                        showRecipeDetail(state.recipe)
                    }
                    is RecipeState.Error -> {
                        Toast.makeText(this@RecipeActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun observeSmartSuggestion() {
        lifecycleScope.launch {
            viewModel.smartSuggestion.collect { state ->
                when (state) {
                    is SmartSuggestionState.Loading -> {
                        binding.tvAvailableIngredients.text = "Loading..."
                        binding.tvSuggestedRecipeTitle.text = "Analyzing your fridge..."
                        binding.tvMatchScore.text = ""
                    }
                    is SmartSuggestionState.Empty -> {
                        binding.tvAvailableIngredients.text = "Your fridge is empty.\nAdd ingredients to get suggestions!"
                        binding.tvSuggestedRecipeTitle.text = "No suggestions"
                        binding.tvMatchScore.text = ""
                        binding.btnViewSuggestedRecipe.isEnabled = false
                    }
                    is SmartSuggestionState.Success -> {
                        // Hiển thị ingredients
                        val ingredientList = state.availableIngredients.joinToString("\n") { "• $it" }
                        binding.tvAvailableIngredients.text = ingredientList

                        // Hiển thị recipe
                        binding.tvSuggestedRecipeTitle.text = state.recipe.title
                        binding.tvMatchScore.text = "✓ ${state.matchCount}/${state.totalIngredients} ingredients match"

                        // Load image
                        Glide.with(this@RecipeActivity)
                            .load(state.recipe.imageUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(binding.ivSuggestedRecipe)

                        binding.btnViewSuggestedRecipe.isEnabled = true
                    }
                    is SmartSuggestionState.Error -> {
                        binding.tvAvailableIngredients.text = "Error loading suggestions"
                        Toast.makeText(this@RecipeActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showRecipeDetail(recipe: Recipe) {
        val intent = Intent(this, RecipeDetailActivity::class.java)
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.id)
        startActivity(intent)
    }


}
