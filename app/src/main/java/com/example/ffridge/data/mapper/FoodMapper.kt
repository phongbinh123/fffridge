package com.example.ffridge.data.mapper

import com.example.ffridge.data.local.entity.FoodEntity
import com.example.ffridge.data.remote.NutritionixResponse
import com.example.ffridge.domain.model.Food
import java.util.Date

// Chuyển đổi từ Entity (Database) -> Domain Model (App sử dụng)
fun FoodEntity.toDomain(): Food {
    return Food(
        id = this.id,
        name = this.name,
        amount = this.amount,
        storedDate = Date(this.storedDate) // Chuyển Long timestamp -> Date object
    )
}

// Chuyển đổi ngược lại từ Domain -> Entity (Để lưu vào Database)
fun Food.toEntity(): FoodEntity {
    return FoodEntity(
        id = this.id, // Nếu là thêm mới, id thường là 0 (Room tự sinh)
        name = this.name,
        amount = this.amount,
        storedDate = this.storedDate.time // Chuyển Date object -> Long timestamp
    )
}

// Chuyển đổi danh sách
fun List<FoodEntity>.toDomainList(): List<Food> {
    return this.map { it.toDomain() }
}

// Chuyển đổi từ kết quả API quét mã vạch -> Domain Model (để hiển thị review trước khi lưu)
fun NutritionixResponse.toDomain(): Food {
    val name = this.itemName ?: "Sản phẩm chưa biết tên"
    // Ghép số lượng và đơn vị (ví dụ: "100 grams")
    val amountStr = if (this.servingSizeQty != null && this.servingSizeUnit != null) {
        "${this.servingSizeQty} ${this.servingSizeUnit}"
    } else {
        "1 phần"
    }

    return Food(
        id = 0, // Chưa lưu nên chưa có ID
        name = name,
        amount = amountStr,
        storedDate = Date() // Mặc định là ngày hiện tại
    )
}