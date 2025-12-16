package com.example.ffridge.domain.usecase.food

import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.repository.FoodRepository

class GetFoodInfoUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(query: String): Result<Food> {
        return repository.getFoodInfoByName(query)
    }
}