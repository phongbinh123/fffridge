package com.example.ffridge.domain.repository

import com.example.ffridge.domain.model.Food
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    // Lấy toàn bộ danh sách (Flow giúp tự động cập nhật UI)
    fun getAllFoods(): Flow<List<Food>>

    // Tìm kiếm trong Database
    fun searchFoods(query: String): Flow<List<Food>>

    // Thêm và Xóa
    suspend fun insertFood(food: Food)
    suspend fun deleteFood(food: Food)

    // Quét mã vạch (Gọi API -> Trả về kết quả)
    suspend fun getFoodFromBarcode(upc: String): Result<Food>

    // Tìm thông tin dinh dưỡng theo tên (cho tính năng nhập tay mới)
    suspend fun getFoodInfoByName(query: String): Result<Food>
}