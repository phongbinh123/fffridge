package com.example.ffridge.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_table")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "amount") val amount: String,
    @ColumnInfo(name = "stored_date") val storedDate: Long,
    @ColumnInfo(name = "calories") val calories: Double, // Thay category bằng calories
    @ColumnInfo(name = "image_uri") val imageUri: String?
)