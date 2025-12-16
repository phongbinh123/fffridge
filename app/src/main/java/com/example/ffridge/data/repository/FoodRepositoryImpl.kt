package com.example.ffridge.data.repository

import com.example.ffridge.data.local.dao.FoodDao
import com.example.ffridge.data.mapper.toDomain
import com.example.ffridge.data.mapper.toDomainList
import com.example.ffridge.data.mapper.toEntity
import com.example.ffridge.data.remote.RetrofitClient
import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FoodRepositoryImpl(
    private val dao: FoodDao,
    private val apiService: com.example.ffridge.data.remote.ApiService = RetrofitClient.apiService
) : FoodRepository {

    override fun getAllFoods(): Flow<List<Food>> {
        return dao.getAllFoods().map { it.toDomainList() }
    }

    override fun searchFoods(query: String): Flow<List<Food>> {
        return dao.searchFoods(query).map { it.toDomainList() }
    }

    override suspend fun insertFood(food: Food) {
        dao.insert(food.toEntity())
    }

    override suspend fun deleteFood(food: Food) {
        dao.delete(food.toEntity())
    }

    // Quét mã vạch (UPC)
    override suspend fun getFoodFromBarcode(upc: String): Result<Food> {
        return try {
            val response = apiService.getFoodByUPC(upc)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Không tìm thấy sản phẩm"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Tìm kiếm theo tên (Cho Manual Entry)
    override suspend fun getFoodInfoByName(query: String): Result<Food> {
        return try {
            // Gọi API Natural Nutrients (POST)
            val response = apiService.getNaturalNutrients(
                com.example.ffridge.data.remote.NaturalQuery(query)
            )

            if (response.isSuccessful && response.body() != null) {
                val foods = response.body()!!.foods
                if (foods.isNotEmpty()) {
                    val item = foods[0] // Lấy món đầu tiên
                    
                    // Map sang Domain Model
                    val food = Food(
                        id = 0,
                        name = item.foodName?.replaceFirstChar { it.uppercase() } ?: query,
                        amount = "${item.servingQty ?: 1} ${item.servingUnit ?: "serving"}",
                        storedDate = java.util.Date(),
                        calories = item.calories ?: 0.0,
                        imageUri = item.photo?.thumb // Lấy link ảnh thumbnail
                    )
                    Result.success(food)
                } else {
                    Result.failure(Exception("Không tìm thấy thông tin"))
                }
            } else {
                Result.failure(Exception("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}