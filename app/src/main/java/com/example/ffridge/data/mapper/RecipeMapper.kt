package com.example.ffridge.data.mapper

import com.example.ffridge.data.remote.RecipeDetail
import com.example.ffridge.data.remote.RecipeSummary
import com.example.ffridge.domain.model.Recipe

// Mapping từ chi tiết công thức (API) -> Domain Model
fun RecipeDetail.toDomain(): Recipe {
    return Recipe(
        id = this.id,
        title = this.title,
        description = this.instructions ?: "No instructions available.",
        ingredients = this.extendedIngredients?.mapNotNull { it.originalString } ?: emptyList(),
        imageUrl = null,
        cookingTime = "",
        difficulty = "Medium"
    )
}

// Mapping từ danh sách tóm tắt (khi search theo nguyên liệu) -> Domain Model
fun RecipeSummary.toDomain(): Recipe {
    return Recipe(
        id = this.id,
        title = this.title,
        description = "Click to view full recipe.",
        ingredients = emptyList(),
        imageUrl = this.image,
        cookingTime = "",
        difficulty = "Easy"
    )
}

fun List<RecipeSummary>.toDomainList(): List<Recipe> {
    return this.map { it.toDomain() }
}
