
package com.example.ffridge.data.mapper

import com.example.ffridge.data.local.entity.FoodEntity
import com.example.ffridge.data.remote.NutritionixResponse // Đã sửa import
import com.example.ffridge.domain.model.Food
import java.util.Date

// Chuyển đổi từ Entity (Database) -> Domain Model (App sử dụng)
fun FoodEntity.toDomain(): Food {
    return Food(
        id = this.id,
        name = this.name,
        amount = this.amount,
        storedDate = Date(this.storedDate),
        calories = this.calories,
        imageUri = this.imageUri
    )
}

// Chuyển đổi ngược lại từ Domain -> Entity (Để lưu vào Database)
fun Food.toEntity(): FoodEntity {
    return FoodEntity(
        id = this.id,
        name = this.name,
        amount = this.amount,
        storedDate = this.storedDate.time,
        calories = this.calories,
        imageUri = this.imageUri
    )
}

// Chuyển đổi danh sách
fun List<FoodEntity>.toDomainList(): List<Food> {
    return this.map { it.toDomain() }
}

// Chuyển đổi từ kết quả API Nutritionix -> Domain Model
fun NutritionixResponse.toDomain(): Food {
    val name = this.itemName ?: "Sản phẩm chưa biết tên"

    // Sửa tên biến cho khớp với ApiModels.kt
    val amountStr = if (this.servingQty != null && this.servingUnit != null) {
        "${this.servingQty} ${this.servingUnit}"
    } else {
        "1 phần"
    }

    // Sửa tên biến calories
    val caloriesValue = this.calories ?: 0.0

    // Lấy ảnh (photo) từ API nếu có
    val imageUri = this.photo?.thumb

    return Food(
        id = 0,
        name = name,
        amount = amountStr,
        storedDate = Date(),
        calories = caloriesValue,
        imageUri = imageUri
    )
}