package com.example.ffridge.domain.model

data class FoodNutritionInfo(
    val name: String,
    val calories: Double,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val imageUri: String? = null
)
