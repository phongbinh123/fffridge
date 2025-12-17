package com.example.ffridge.data.remote

data class FoodAnalysisResult(
    val name: String,              // → binding.etName
    val amount: String,            // → binding.etQuantity (đổi từ quantity)
    val calories: Double,          // → binding.etCalories
    val expiryDate: String,        // → binding.etExpiryDate (đổi từ freshnessDays)
    val imageUri: String? = null   // → selectedImageUri
)
