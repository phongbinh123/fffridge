package com.example.ffridge.data.repository

import com.example.ffridge.data.remote.NutritionApi
import com.example.ffridge.domain.model.FoodNutritionInfo
import com.example.ffridge.domain.repository.NutritionRepository
import javax.inject.Inject

class NutritionRepositoryImpl @Inject constructor(
    private val api: NutritionApi
) : NutritionRepository {

    override suspend fun searchFoodNutrition(query: String): Result<FoodNutritionInfo> {
        return try {
            val response = api.searchFood(query)

            if (response.isSuccessful && response.body() != null) {
                val foods = response.body()!!.foods

                if (foods.isNotEmpty()) {
                    val food = foods.first()

                    // Tìm calories trong danh sách nutrients
                    val calorieNutrient = food.foodNutrients.find {
                        it.nutrientName.contains("Energy", ignoreCase = true) ||
                                it.nutrientName.contains("Calories", ignoreCase = true)
                    }

                    val calories = calorieNutrient?.value ?: 0.0

                    Result.success(
                        FoodNutritionInfo(
                            name = food.description,
                            calories = calories,
                            imageUri = null // USDA API không có ảnh
                        )
                    )
                } else {
                    Result.failure(Exception("Không tìm thấy thông tin cho '${query}'"))
                }
            } else {
                Result.failure(Exception("Lỗi API: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }
}
