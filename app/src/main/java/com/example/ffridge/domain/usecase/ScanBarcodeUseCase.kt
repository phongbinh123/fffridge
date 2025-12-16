package com.example.ffridge.domain.usecase.food

import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.repository.FoodRepository

class ScanBarcodeUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(upc: String): Result<Food> {
        if (upc.isBlank()) {
            return Result.failure(Exception("Mã vạch trống"))
        }
        return repository.getFoodFromBarcode(upc)
    }
}