package com.example.ffridge.presentation.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ffridge.domain.model.Recipe
import com.example.ffridge.domain.usecase.recipe.GetRandomRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getRandomRecipeUseCase: GetRandomRecipeUseCase
) : ViewModel() {

    private val _recipeState = MutableStateFlow<RecipeState>(RecipeState.Empty)
    val recipeState: StateFlow<RecipeState> = _recipeState

    fun fetchRandomRecipe() {
        _recipeState.value = RecipeState.Loading
        viewModelScope.launch {
            getRandomRecipeUseCase()
                .onSuccess { recipe ->
                    _recipeState.value = RecipeState.Success(recipe)
                }
                .onFailure { error ->
                    _recipeState.value = RecipeState.Error(error.message ?: "Lỗi tải")
                }
        }
    }
}

// State Pattern cho màn hình
sealed class RecipeState {
    object Empty : RecipeState()
    object Loading : RecipeState()
    data class Success(val recipe: Recipe) : RecipeState()
    data class Error(val message: String) : RecipeState()
}