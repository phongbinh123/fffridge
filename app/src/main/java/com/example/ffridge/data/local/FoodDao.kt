package com.example.ffridge.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ffridge.data.local.entity.FoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    // Flow là tuyệt vời cho Clean Arch, nó giúp Data Layer tự bắn tín hiệu lên UI khi DB thay đổi
    @Query("SELECT * FROM food_table ORDER BY stored_date ASC")
    fun getAllFoods(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM food_table ORDER BY stored_date ASC LIMIT :limit")
    fun getOldestFoods(limit: Int): Flow<List<FoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: FoodEntity)

    @Delete
    suspend fun delete(food: FoodEntity)

    @Query("SELECT * FROM food_table WHERE name LIKE '%' || :query || '%'")
    fun searchFoods(query: String): Flow<List<FoodEntity>>
}