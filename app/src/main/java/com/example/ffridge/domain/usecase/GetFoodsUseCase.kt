package com.example.ffridge.domain.usecase

import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFoodsUseCase @Inject constructor(
    private val repository: FoodRepository
) {
    // Dùng operator invoke để có thể gọi useCase() như một hàm
    operator fun invoke(): Flow<List<Food>> {
        return repository.getAllFoods().map { list ->
            // Ví dụ Business Logic: Luôn sắp xếp thực phẩm mới nhất lên đầu
            list.sortedByDescending { it.storedDate }
        }
    }
}
