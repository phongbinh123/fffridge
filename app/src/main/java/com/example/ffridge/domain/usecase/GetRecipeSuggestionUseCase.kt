package com.example.ffridge.domain.usecase.recipe

import com.example.ffridge.domain.model.Recipe
import com.example.ffridge.domain.repository.FoodRepository
import com.example.ffridge.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.first

class GetRecipeSuggestionUseCase(
    private val foodRepository: FoodRepository,
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(): Result<List<Recipe>> {
        // 1. Lấy danh sách thực phẩm hiện có trong tủ (lấy snapshot hiện tại bằng .first())
        val currentFoods = foodRepository.getAllFoods().first()

        if (currentFoods.isEmpty()) {
            return Result.failure(Exception("Tủ lạnh đang trống, không thể gợi ý món ăn."))
        }

        // 2. Lấy tên các nguyên liệu, ghép thành chuỗi (ví dụ: "eggs,milk,tomatoes")
        // Chỉ lấy tối đa 5 món để tìm kiếm cho chính xác
        val ingredients = currentFoods.take(5).map { it.name }

        // 3. Gọi API tìm công thức
        return recipeRepository.findRecipesByIngredients(ingredients)
    }
}