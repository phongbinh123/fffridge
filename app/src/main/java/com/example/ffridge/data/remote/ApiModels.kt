package com.example.ffridge.data.remote

import com.google.gson.annotations.SerializedName

// --- Model cho Nutritionix (Quét mã vạch) ---
data class NutritionixResponse(
    @SerializedName("item_name") val itemName: String?,
    @SerializedName("nf_servings_per_container") val servings: Double?,
    @SerializedName("nf_serving_size_qty") val servingSizeQty: Double?,
    @SerializedName("nf_serving_size_unit") val servingSizeUnit: String?
)

// --- Model cho Spoonacular (Công thức nấu ăn) ---
// 1. Kết quả tìm kiếm theo nguyên liệu
data class RecipeSummary(
    val id: Int,
    val title: String,
    val image: String?
)

// 2. Chi tiết công thức
data class RecipeDetail(
    val id: Int,
    val title: String,
    val instructions: String?,
    val extendedIngredients: List<Ingredient>?
)

data class Ingredient(
    val originalString: String?
)

// 3. Kết quả random recipe
data class RandomRecipeResponse(
    val recipes: List<RecipeDetail>
)