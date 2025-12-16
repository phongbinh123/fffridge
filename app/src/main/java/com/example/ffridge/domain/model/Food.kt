package com.example.ffridge.domain.model

import java.util.Date

data class Food(
    val id: Int,
    val name: String,
    val amount: String,
    val storedDate: Date,
    val calories: Double, // Sửa ở đây
    val imageUri: String?
)
