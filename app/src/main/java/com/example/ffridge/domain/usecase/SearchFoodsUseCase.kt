package com.example.ffridge.domain.usecase.food

import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow

class SearchFoodUseCase(private val repository: FoodRepository) {
    operator fun invoke(query: String): Flow<List<Food>> {
        return repository.searchFoods(query)
    }
}