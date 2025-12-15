package com.example.ffridge.data.repository

import com.example.ffridge.data.mapper.toDomain
import com.example.ffridge.data.mapper.toDomainList
import com.example.ffridge.data.remote.RetrofitClient
import com.example.ffridge.domain.model.Recipe
import com.example.ffridge.domain.repository.RecipeRepository

class RecipeRepositoryImpl(
    private val apiService: com.example.ffridge.data.remote.ApiService = RetrofitClient.apiService
) : RecipeRepository {

    override suspend fun getRandomRecipe(): Result<Recipe> {
        return try {
            val response = apiService.getRandomRecipe()
            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                if (responseBody.recipes.isNotEmpty()) {
                    // Lấy công thức đầu tiên và map sang Domain
                    Result.success(responseBody.recipes[0].toDomain())
                } else {
                    Result.failure(Exception("Không có công thức nào"))
                }
            } else {
                Result.failure(Exception("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun findRecipesByIngredients(ingredients: List<String>): Result<List<Recipe>> {
        return try {
            val ingredientsString = ingredients.joinToString(",")
            val response = apiService.findRecipesByIngredients(ingredientsString)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomainList())
            } else {
                Result.failure(Exception("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}