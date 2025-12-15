package com.example.ffridge.domain.model

data class Recipe(
    val id: Int,
    val title: String,
    val instructions: String,
    val ingredients: List<String>,
    val imageUrl: String? = null
)