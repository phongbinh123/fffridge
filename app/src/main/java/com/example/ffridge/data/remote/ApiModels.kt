package com.example.ffridge.data.remote

import com.google.gson.annotations.SerializedName

// --- Model cho Nutritionix (Quét mã vạch) ---
data class NutritionixResponse(
    @SerializedName("item_name") val itemName: String?,
    @SerializedName("nf_calories") val calories: Double?, // Calo
    @SerializedName("nf_serving_size_qty") val servingQty: Double?,
    @SerializedName("nf_serving_size_unit") val servingUnit: String?,
    @SerializedName("photo") val photo: NutritionixPhoto? // Ảnh minh họa
)

// Model trả về từ endpoint /v2/natural/nutrients
data class NaturalNutrientsResponse(
    @SerializedName("foods") val foods: List<NaturalFoodItem>
)

data class NaturalFoodItem(
    @SerializedName("food_name") val foodName: String?,
    @SerializedName("nf_calories") val calories: Double?,
    @SerializedName("serving_qty") val servingQty: Double?,
    @SerializedName("serving_unit") val servingUnit: String?,
    @SerializedName("photo") val photo: NutritionixPhoto?
)

data class NutritionixPhoto(
    @SerializedName("thumb") val thumb: String?
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