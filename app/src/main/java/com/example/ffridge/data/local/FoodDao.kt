package com.example.ffridge.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    // Lấy toàn bộ thực phẩm, Flow giúp tự động cập nhật UI khi dữ liệu đổi
    @Query("SELECT * FROM food_table ORDER BY stored_date ASC")
    fun getAllFoods(): Flow<List<FoodItem>>

    // Lấy X thực phẩm cũ nhất (cho tính năng Notification)
    @Query("SELECT * FROM food_table ORDER BY stored_date ASC LIMIT :limit")
    suspend fun getOldestFoods(limit: Int): List<FoodItem>

    // Thêm món ăn
    @Insert
    suspend fun insert(food: FoodItem)

    // Xóa món ăn
    @Delete
    suspend fun delete(food: FoodItem)

    // Tìm kiếm (cho chức năng Search)
    @Query("SELECT * FROM food_table WHERE name LIKE '%' || :searchQuery || '%'")
    fun searchFoods(searchQuery: String): Flow<List<FoodItem>>
}