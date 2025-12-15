package com.example.ffridge.domain.usecase.food

import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.repository.FoodRepository

class AddFoodUseCase(private val repository: FoodRepository) {
    @Throws(Exception::class) // Báo hiệu hàm này có thể ném lỗi
    suspend operator fun invoke(food: Food) {
        if (food.name.isBlank()) {
            throw Exception("Tên thực phẩm không được để trống")
        }
        repository.insertFood(food)
    }
}