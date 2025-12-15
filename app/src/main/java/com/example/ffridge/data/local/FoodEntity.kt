package com.example.ffridge.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_table")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "amount")
    val amount: String,

    // Lưu dưới dạng Long (Timestamp) sẽ tốt hơn String để tính toán ngày hết hạn sau này
    // Tuy nhiên, nếu bạn muốn giữ logic cũ (String), hãy dùng String.
    @ColumnInfo(name = "stored_date")
    val storedDate: Long
)