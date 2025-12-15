package com.example.ffridge.domain.repository

import com.example.ffridge.domain.model.Food
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getAllFoods(): Flow<List<Food>>

    fun searchFoods(query: String): Flow<List<Food>>

    suspend fun insertFood(food: Food)

    suspend fun deleteFood(food: Food)

    // Trả về Result để xử lý thành công/thất bại ở ViewModel
    suspend fun getFoodFromBarcode(upc: String): Result<Food>
}