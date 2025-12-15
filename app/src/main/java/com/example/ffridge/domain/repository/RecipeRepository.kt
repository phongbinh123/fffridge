package com.example.ffridge.domain.repository

import com.example.ffridge.domain.model.Recipe

interface RecipeRepository {
    suspend fun getRandomRecipe(): Result<Recipe>

    suspend fun findRecipesByIngredients(ingredients: List<String>): Result<List<Recipe>>
}