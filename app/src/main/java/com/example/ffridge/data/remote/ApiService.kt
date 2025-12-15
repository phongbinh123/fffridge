package com.example.ffridge.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // --- NUTRITIONIX API (Quét mã vạch) ---
    @GET("https://nutritionix-api.p.rapidapi.com/v1_1/item")
    suspend fun getFoodByUPC(
        @Query("upc") upc: String,
        @Header("X-RapidAPI-Key") apiKey: String = "fc6fbf058emsh24e3e2cb5fcda03p12ffd1jsn451c757b8d26",
        @Header("X-RapidAPI-Host") host: String = "nutritionix-api.p.rapidapi.com"
    ): Response<NutritionixResponse>

    // --- SPOONACULAR API (Công thức) ---
    // Tìm món ăn theo nguyên liệu
    @GET("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/findByIngredients")
    suspend fun findRecipesByIngredients(
        @Query("ingredients") ingredients: String,
        @Query("number") number: Int = 5,
        @Header("X-RapidAPI-Key") apiKey: String = "fc6fbf058emsh24e3e2cb5fcda03p12ffd1jsn451c757b8d26",
        @Header("X-RapidAPI-Host") host: String = "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com"
    ): Response<List<RecipeSummary>>

    // Lấy chi tiết món ăn theo ID
    @GET("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id") id: Int,
        @Header("X-RapidAPI-Key") apiKey: String = "fc6fbf058emsh24e3e2cb5fcda03p12ffd1jsn451c757b8d26",
        @Header("X-RapidAPI-Host") host: String = "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com"
    ): Response<RecipeDetail>

    // Lấy món ăn ngẫu nhiên
    @GET("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/random")
    suspend fun getRandomRecipe(
        @Query("number") number: Int = 1,
        @Header("X-RapidAPI-Key") apiKey: String = "fc6fbf058emsh24e3e2cb5fcda03p12ffd1jsn451c757b8d26",
        @Header("X-RapidAPI-Host") host: String = "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com"
    ): Response<RandomRecipeResponse>
}