package com.example.ffridge.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_table")
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "amount")
    val amount: String,

    @ColumnInfo(name = "stored_date")
    val storedDate: String // Format: yyyy/MM/dd (như logic cũ)
)