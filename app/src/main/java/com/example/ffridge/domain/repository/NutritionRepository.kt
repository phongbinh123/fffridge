package com.example.ffridge.domain.repository

import com.example.ffridge.domain.model.FoodNutritionInfo

interface NutritionRepository {
    suspend fun searchFoodNutrition(query: String): Result<FoodNutritionInfo>
}
