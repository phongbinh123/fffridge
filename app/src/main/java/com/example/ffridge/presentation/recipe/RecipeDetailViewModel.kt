package com.example.ffridge.presentation.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ffridge.domain.model.Recipe
import com.example.ffridge.domain.usecase.GetRecipeSuggestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getRecipeSuggestionUseCase: GetRecipeSuggestionUseCase
) : ViewModel() {

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe

    fun loadRecipe(recipeId: Int) {
        viewModelScope.launch {
            getRecipeSuggestionUseCase()
                .onSuccess { recipes ->
                    _recipe.value = recipes.find { it.id == recipeId }
                }
                .onFailure {
                    _recipe.value = null
                }
        }
    }
}
