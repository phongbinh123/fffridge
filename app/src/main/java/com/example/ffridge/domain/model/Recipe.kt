package com.example.ffridge.domain.model

data class Recipe(
    val id: Int = 0,
    val title: String,
    val ingredients: List<String>,
    val description: String,
    val imageUrl: String? = null,
    val cookingTime: String = "",
    val difficulty: String = "Easy"
)
