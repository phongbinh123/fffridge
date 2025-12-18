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
        observeAllRecipes()
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
        // Random recipe FAB - Mở trực tiếp link công thức ngẫu nhiên
        binding.fabRandom.setOnClickListener {
            openRandomRecipeUrl()
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
            Toast.makeText(this, "Nhập tên món ăn để tìm kiếm", Toast.LENGTH_SHORT).show()
            return
        }

        val searchUrl = "https://www.google.com/search?q=${Uri.encode("$query công thức nấu ăn")}"
        openUrlInCustomTab(searchUrl)
    }

    // Mở công thức ngẫu nhiên - Trực tiếp trang web công thức
    private fun openRandomRecipeUrl() {
        val randomRecipeUrls = listOf(
            "https://cooky.vn/cong-thuc/com-chien-duong-chau-22771",
            "https://cooky.vn/cong-thuc/ga-ran-gion-tan-11063",
            "https://cooky.vn/cong-thuc/canh-chua-ca-loc-3851",
            "https://cooky.vn/cong-thuc/mi-xao-bo-5478",
            "https://cooky.vn/cong-thuc/trung-chien-la-chanh-2963",
            "https://cooky.vn/cong-thuc/bun-bo-hue-25",
            "https://cooky.vn/cong-thuc/pho-bo-ha-noi-11",
            "https://cooky.vn/cong-thuc/banh-mi-thit-nuong-4256",
            "https://cooky.vn/cong-thuc/thit-kho-trung-30",
            "https://cooky.vn/cong-thuc/canh-chua-tom-39",
            "https://cooky.vn/cong-thuc/rau-muong-xao-toi-2485",
            "https://cooky.vn/cong-thuc/com-tam-suon-nuong-50",
            "https://cooky.vn/cong-thuc/ca-kho-to-20",
            "https://cooky.vn/cong-thuc/bun-cha-ha-noi-15",
            "https://cooky.vn/cong-thuc/canh-rau-muong-nau-miso-8745"
        )

        val randomUrl = randomRecipeUrls.random()
        openUrlInCustomTab(randomUrl)
    }

    private fun openUrlInCustomTab(url: String) {
        try {
            CustomTabsIntent.Builder()
                .setToolbarColor(resources.getColor(R.color.brand_teal, null))
                .setShowTitle(true)
                .build()
                .launchUrl(this, Uri.parse(url))
        } catch (e: Exception) {
            Toast.makeText(this, "Không thể mở trình duyệt", Toast.LENGTH_SHORT).show()
        }
    }

    // Observe All Recipes (5 món phổ biến từ Local)
    private fun observeAllRecipes() {
        lifecycleScope.launch {
            viewModel.allRecipes.collect { state ->
                when (state) {
                    is AllRecipesState.Loading -> {
                        // Show loading if needed
                    }
                    is AllRecipesState.Success -> {
                        adapter.submitList(state.recipes)
                    }
                    is AllRecipesState.Error -> {
                        Toast.makeText(this@RecipeActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Observe Smart Suggestions (dựa trên tủ lạnh)
    private fun observeSmartSuggestion() {
        lifecycleScope.launch {
            viewModel.smartSuggestion.collect { state ->
                when (state) {
                    is SmartSuggestionState.Loading -> {
                        binding.tvAvailableIngredients.text = "Đang phân tích..."
                        binding.tvSuggestedRecipeTitle.text = "Đang tìm công thức phù hợp..."
                        binding.tvMatchScore.text = ""
                        binding.btnViewSuggestedRecipe.isEnabled = false
                    }
                    is SmartSuggestionState.Empty -> {
                        binding.tvAvailableIngredients.text = "Tủ lạnh trống.\nThêm nguyên liệu để nhận gợi ý!"
                        binding.tvSuggestedRecipeTitle.text = "Chưa có gợi ý"
                        binding.tvMatchScore.text = ""
                        binding.ivSuggestedRecipe.setImageResource(R.drawable.ic_launcher_background)
                        binding.btnViewSuggestedRecipe.isEnabled = false
                    }
                    is SmartSuggestionState.Success -> {
                        // Hiển thị nguyên liệu có trong tủ lạnh
                        val ingredientList = if (state.availableIngredients.isNotEmpty()) {
                            "Nguyên liệu bạn có:\n" +
                                    state.availableIngredients.joinToString("\n") { "• $it" }
                        } else {
                            "Không tìm thấy nguyên liệu"
                        }
                        binding.tvAvailableIngredients.text = ingredientList

                        // Hiển thị recipe được gợi ý
                        binding.tvSuggestedRecipeTitle.text = state.recipe.title

                        // Hiển thị tỷ lệ match
                        val matchPercentage = (state.matchCount.toFloat() / state.totalIngredients * 100).toInt()
                        binding.tvMatchScore.text = "✓ ${state.matchCount}/${state.totalIngredients} nguyên liệu ($matchPercentage%)"

                        // Load image
                        Glide.with(this@RecipeActivity)
                            .load(state.recipe.imageUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .centerCrop()
                            .into(binding.ivSuggestedRecipe)

                        binding.btnViewSuggestedRecipe.isEnabled = true
                    }
                    is SmartSuggestionState.Error -> {
                        binding.tvAvailableIngredients.text = "Lỗi tải gợi ý"
                        binding.tvSuggestedRecipeTitle.text = "Vui lòng thử lại"
                        binding.tvMatchScore.text = ""
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
