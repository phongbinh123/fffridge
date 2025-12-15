package com.example.ffridge.data.repository

import com.example.ffridge.data.local.dao.FoodDao
import com.example.ffridge.data.mapper.toDomain
import com.example.ffridge.data.mapper.toDomainList
import com.example.ffridge.data.mapper.toEntity
import com.example.ffridge.data.remote.ApiService
import com.example.ffridge.domain.model.Food
import com.example.ffridge.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepositoryImpl @Inject constructor(
    private val dao: FoodDao,
    private val apiService: ApiService
) : FoodRepository {

    override fun getAllFoods(): Flow<List<Food>> {
        return dao.getAllFoods().map { entities ->
            entities.toDomainList()
        }
    }

    override fun searchFoods(query: String): Flow<List<Food>> {
        return dao.searchFoods(query).map { entities ->
            entities.toDomainList()
        }
    }

    override suspend fun insertFood(food: Food) {
        dao.insert(food.toEntity())
    }

    override suspend fun deleteFood(food: Food) {
        dao.delete(food.toEntity())
    }

    override suspend fun getFoodFromBarcode(upc: String): Result<Food> {
        return try {
            val response = apiService.getFoodByUPC(upc)
            if (response.isSuccessful && response.body() != null) {
                val food = response.body()!!.toDomain()
                Result.success(food)
            } else {
                Result.failure(Exception("Không tìm thấy sản phẩm"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}