package com.example.ffridge.domain.usecase

import com.example.ffridge.domain.model.Recipe
import com.example.ffridge.domain.repository.RecipeRepository
import javax.inject.Inject

class GetRandomRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(): Result<Recipe> {
        return repository.getRandomRecipe()
    }
}
