package com.example.ffridge.domain.repository

import com.example.ffridge.domain.model.Recipe

interface RecipeRepository {
    // Lấy một công thức ngẫu nhiên
    suspend fun getRandomRecipe(): Result<Recipe>

    // Tìm công thức dựa trên danh sách nguyên liệu
    suspend fun findRecipesByIngredients(ingredients: List<String>): Result<List<Recipe>>
}