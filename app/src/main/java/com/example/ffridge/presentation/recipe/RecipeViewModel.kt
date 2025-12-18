package com.example.ffridge.presentation.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.model.Recipe
import com.example.ffridge.domain.usecase.GetFoodsUseCase
import com.example.ffridge.domain.usecase.GetRandomRecipeUseCase
import com.example.ffridge.domain.usecase.GetRecipeSuggestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getRandomRecipeUseCase: GetRandomRecipeUseCase,
    private val getRecipeSuggestionUseCase: GetRecipeSuggestionUseCase,
    private val getFoodsUseCase: GetFoodsUseCase
) : ViewModel() {

    private val _recipeState = MutableStateFlow<RecipeState>(RecipeState.Loading)
    val recipeState: StateFlow<RecipeState> = _recipeState

    private val _fridgeFoods = MutableStateFlow<List<Food>>(emptyList())
    val fridgeFoods: StateFlow<List<Food>> = _fridgeFoods

    private val _smartSuggestion = MutableStateFlow<SmartSuggestionState>(SmartSuggestionState.Loading)
    val smartSuggestion: StateFlow<SmartSuggestionState> = _smartSuggestion

    private var allSuggestedRecipes: List<Recipe> = emptyList()
    private var currentSuggestionIndex = 0

    init {
        loadAllRecipes()
        loadFridgeItems()
    }

    private fun loadAllRecipes() {
        viewModelScope.launch {
            getRecipeSuggestionUseCase()
                .onSuccess { recipes ->
                    _recipeState.value = RecipeState.Success(recipes)
                }
                .onFailure { error ->
                    _recipeState.value = RecipeState.Error(error.message ?: "Lỗi tải danh sách")
                }
        }
    }

    private fun loadFridgeItems() {
        viewModelScope.launch {
            getFoodsUseCase().collect { foods ->
                _fridgeFoods.value = foods
                // Reload smart suggestion khi fridge thay đổi
                if (foods.isNotEmpty()) {
                    loadSmartSuggestion()
                } else {
                    _smartSuggestion.value = SmartSuggestionState.Empty
                }
            }
        }
    }

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

                    // Tính điểm match cho từng recipe
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

        // Chuyển sang suggestion tiếp theo
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

    fun fetchRandomRecipe() {
        _recipeState.value = RecipeState.Loading
        viewModelScope.launch {
            getRandomRecipeUseCase()
                .onSuccess { recipe ->
                    _recipeState.value = RecipeState.SingleRecipe(recipe)
                }
                .onFailure { error ->
                    _recipeState.value = RecipeState.Error(error.message ?: "Lỗi tải")
                }
        }
    }
}

sealed class RecipeState {
    object Loading : RecipeState()
    data class Success(val recipes: List<Recipe>) : RecipeState()
    data class SingleRecipe(val recipe: Recipe) : RecipeState()
    data class Error(val message: String) : RecipeState()
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
