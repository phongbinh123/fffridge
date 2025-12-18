package com.example.ffridge.data.repository

import com.example.ffridge.data.local.LocalRecipeDataSource
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
                    Result.success(responseBody.recipes[0].toDomain())
                } else {
                    // Fallback local
                    Result.success(LocalRecipeDataSource.getPopularRecipes().random())
                }
            } else {
                // Fallback local
                Result.success(LocalRecipeDataSource.getPopularRecipes().random())
            }
        } catch (e: Exception) {
            // Fallback local khi lỗi API
            Result.success(LocalRecipeDataSource.getPopularRecipes().random())
        }
    }

    override suspend fun findRecipesByIngredients(ingredients: List<String>): Result<List<Recipe>> {
        return try {
            val ingredientsString = ingredients.joinToString(",")
            val response = apiService.findRecipesByIngredients(ingredientsString)

            if (response.isSuccessful && response.body() != null) {
                val apiRecipes = response.body()!!.toDomainList()
                if (apiRecipes.isNotEmpty()) {
                    Result.success(apiRecipes)
                } else {
                    // Fallback local
                    Result.success(LocalRecipeDataSource.getPopularRecipes())
                }
            } else {
                // Fallback local
                Result.success(LocalRecipeDataSource.getPopularRecipes())
            }
        } catch (e: Exception) {
            // Fallback local khi lỗi API
            Result.success(LocalRecipeDataSource.getPopularRecipes())
        }
    }
}
