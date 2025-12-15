package com.example.ffridge.domain.usecase.food

import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.repository.FoodRepository

class DeleteFoodUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(food: Food) {
        repository.deleteFood(food)
    }
}