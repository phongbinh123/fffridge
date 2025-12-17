package com.example.ffridge.domain.usecase.food

import com.example.ffridge.domain.model.FoodNutritionInfo
import com.example.ffridge.domain.repository.NutritionRepository
import javax.inject.Inject

class SearchFoodNutritionUseCase @Inject constructor(
    private val repository: NutritionRepository
) {
    suspend operator fun invoke(query: String): Result<FoodNutritionInfo> {
        return repository.searchFoodNutrition(query)
    }
}
