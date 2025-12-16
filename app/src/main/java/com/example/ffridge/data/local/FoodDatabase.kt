package com.example.ffridge.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ffridge.data.local.dao.FoodDao
import com.example.ffridge.data.local.entity.FoodEntity

@Database(
    entities = [FoodEntity::class],
    version = 1
)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
}