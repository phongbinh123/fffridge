package com.example.ffridge.data.mapper

import com.example.ffridge.data.remote.RecipeDetail
import com.example.ffridge.data.remote.RecipeSummary
import com.example.ffridge.domain.model.Recipe

// Mapping từ chi tiết công thức (API) -> Domain Model
fun RecipeDetail.toDomain(): Recipe {
    return Recipe(
        id = this.id,
        title = this.title,
        // API trả về HTML hoặc text dài, ta có thể xử lý sơ ở đây nếu cần
        instructions = this.instructions ?: "Không có hướng dẫn chi tiết.",
        // Lấy danh sách tên nguyên liệu từ list object phức tạp
        ingredients = this.extendedIngredients?.mapNotNull { it.originalString } ?: emptyList()
    )
}

// Mapping từ danh sách tóm tắt (khi search theo nguyên liệu) -> Domain Model
fun RecipeSummary.toDomain(): Recipe {
    return Recipe(
        id = this.id,
        title = this.title,
        instructions = "", // Summary thường không có hướng dẫn, cần gọi API chi tiết sau
        ingredients = emptyList(),
        imageUrl = this.image // Giả sử Domain Model có trường ảnh
    )
}

fun List<RecipeSummary>.toDomainList(): List<Recipe> {
    return this.map { it.toDomain() }
}