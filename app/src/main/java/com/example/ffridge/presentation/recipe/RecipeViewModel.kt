package com.example.ffridge.presentation.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ffridge.data.local.LocalRecipeDataSource
import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.model.Recipe
import com.example.ffridge.domain.usecase.GetFoodsUseCase
import com.example.ffridge.domain.usecase.GetRecipeSuggestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getRecipeSuggestionUseCase: GetRecipeSuggestionUseCase,
    private val getFoodsUseCase: GetFoodsUseCase
) : ViewModel() {

    // All Recipes State - Hiển thị 5 món phổ biến từ LOCAL
    private val _allRecipes = MutableStateFlow<AllRecipesState>(AllRecipesState.Loading)
    val allRecipes: StateFlow<AllRecipesState> = _allRecipes

    // Smart Suggestion State - Dựa trên tủ lạnh từ API
    private val _smartSuggestion = MutableStateFlow<SmartSuggestionState>(SmartSuggestionState.Loading)
    val smartSuggestion: StateFlow<SmartSuggestionState> = _smartSuggestion

    private val _fridgeFoods = MutableStateFlow<List<Food>>(emptyList())
    val fridgeFoods: StateFlow<List<Food>> = _fridgeFoods

    private var allSuggestedRecipes: List<Recipe> = emptyList()
    private var currentSuggestionIndex = 0

    init {
        loadPopularRecipes()
        loadFridgeItems()
    }

    // Load 5 popular recipes từ LOCAL DATA cho "All Recipes"
    private fun loadPopularRecipes() {
        viewModelScope.launch {
            try {
                val popularRecipes = LocalRecipeDataSource.getPopularRecipes().take(5)
                _allRecipes.value = AllRecipesState.Success(popularRecipes)
            } catch (e: Exception) {
                _allRecipes.value = AllRecipesState.Error(e.message ?: "Lỗi tải danh sách")
            }
        }
    }

    private fun loadFridgeItems() {
        viewModelScope.launch {
            getFoodsUseCase().collect { foods ->
                _fridgeFoods.value = foods
                if (foods.isNotEmpty()) {
                    loadSmartSuggestion()
                } else {
                    _smartSuggestion.value = SmartSuggestionState.Empty
                }
            }
        }
    }

    // Smart Suggestion - Match nguyên liệu từ API
    private fun loadSmartSuggestion() {
        viewModelScope.launch {
            val foods = _fridgeFoods.value
            if (foods.isEmpty()) {
                _smartSuggestion.value = SmartSuggestionState.Empty
                return@launch
            }

            _smartSuggestion.value = SmartSuggestionState.Loading

            getRecipeSuggestionUseCase()
                .onSuccess { recipes ->
                    if (recipes.isEmpty()) {
                        _smartSuggestion.value = SmartSuggestionState.Empty
                        return@onSuccess
                    }

                    val fridgeIngredients = foods.map { it.name.lowercase() }
                    val recipesWithScore = recipes.map { recipe ->
                        val matchCount = recipe.ingredients.count { ingredient ->
                            fridgeIngredients.any { fridgeItem ->
                                ingredient.lowercase().contains(fridgeItem) ||
                                        fridgeItem.contains(ingredient.lowercase())
                            }
                        }
                        Pair(recipe, matchCount)
                    }.filter { it.second > 0 }
                        .sortedByDescending { it.second }

                    if (recipesWithScore.isEmpty()) {
                        _smartSuggestion.value = SmartSuggestionState.Empty
                        return@onSuccess
                    }

                    allSuggestedRecipes = recipesWithScore.map { it.first }
                    currentSuggestionIndex = 0

                    val topRecipe = recipesWithScore.first()
                    _smartSuggestion.value = SmartSuggestionState.Success(
                        recipe = topRecipe.first,
                        availableIngredients = fridgeIngredients,
                        matchCount = topRecipe.second,
                        totalIngredients = topRecipe.first.ingredients.size
                    )
                }
                .onFailure { error ->
                    _smartSuggestion.value = SmartSuggestionState.Error(error.message ?: "Lỗi")
                }
        }
    }

    fun refreshSmartSuggestion() {
        if (allSuggestedRecipes.isEmpty()) {
            loadSmartSuggestion()
            return
        }

        currentSuggestionIndex = (currentSuggestionIndex + 1) % allSuggestedRecipes.size
        val nextRecipe = allSuggestedRecipes[currentSuggestionIndex]

        val fridgeIngredients = _fridgeFoods.value.map { it.name.lowercase() }
        val matchCount = nextRecipe.ingredients.count { ingredient ->
            fridgeIngredients.any { fridgeItem ->
                ingredient.lowercase().contains(fridgeItem) ||
                        fridgeItem.contains(ingredient.lowercase())
            }
        }

        _smartSuggestion.value = SmartSuggestionState.Success(
            recipe = nextRecipe,
            availableIngredients = fridgeIngredients,
            matchCount = matchCount,
            totalIngredients = nextRecipe.ingredients.size
        )
    }
}

sealed class AllRecipesState {
    object Loading : AllRecipesState()
    data class Success(val recipes: List<Recipe>) : AllRecipesState()
    data class Error(val message: String) : AllRecipesState()
}

sealed class SmartSuggestionState {
    object Loading : SmartSuggestionState()
    object Empty : SmartSuggestionState()
    data class Success(
        val recipe: Recipe,
        val availableIngredients: List<String>,
        val matchCount: Int,
        val totalIngredients: Int
    ) : SmartSuggestionState()
    data class Error(val message: String) : SmartSuggestionState()
}
