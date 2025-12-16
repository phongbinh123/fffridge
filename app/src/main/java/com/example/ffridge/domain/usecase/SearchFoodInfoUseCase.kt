package com.example.ffridge.domain.usecase.food

import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.repository.FoodRepository

class SearchFoodInfoUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(query: String): Result<Food> {
        if (query.isBlank()) {
            return Result.failure(Exception("Từ khóa tìm kiếm trống"))
        }
        return repository.getFoodInfoByName(query)
    }
}