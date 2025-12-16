package com.example.ffridge.domain.repository

import com.example.ffridge.domain.model.Food
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getAllFoods(): Flow<List<Food>>
    fun searchFoods(query: String): Flow<List<Food>>
    suspend fun insertFood(food: Food)
    suspend fun deleteFood(food: Food)

    // Tìm thông tin món ăn qua UPC (Quét mã)
    suspend fun getFoodFromBarcode(upc: String): Result<Food>

    // Tìm thông tin món ăn qua Tên (Nhập tay -> Tự điền Calo/Ảnh)
    suspend fun getFoodInfoByName(query: String): Result<Food>
}