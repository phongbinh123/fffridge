package com.example.ffridge.domain.usecase.recipe

import com.example.ffridge.domain.model.Recipe
import com.example.ffridge.domain.repository.RecipeRepository

class GetRandomRecipeUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(): Result<Recipe> {
        return repository.getRandomRecipe()
    }
}