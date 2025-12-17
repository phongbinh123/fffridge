package com.example.ffridge.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NutritionApi {

    // Sử dụng USDA FoodData Central API (miễn phí)
    @GET("fdc/v1/foods/search")
    suspend fun searchFood(
        @Query("query") query: String,
        @Query("api_key") apiKey: String = "SLIlBtEjH1HJW4Q5HHfAcBvxx19EMQhCiKFHrv2J", // Thay bằng API key của bạn
        @Query("pageSize") pageSize: Int = 1
    ): Response<FoodSearchResponse>
}

// Response models
data class FoodSearchResponse(
    val foods: List<FoodItem>
)

data class FoodItem(
    val description: String,
    val foodNutrients: List<Nutrient>,
    val brandName: String? = null,
    val dataType: String? = null
)

data class Nutrient(
    val nutrientName: String,
    val value: Double,
    val unitName: String
)
