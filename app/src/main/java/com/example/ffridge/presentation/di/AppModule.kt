package com.example.ffridge.presentation.di

import android.content.Context
import androidx.room.Room
import com.example.ffridge.data.local.AppDatabase
import com.example.ffridge.data.local.dao.FoodDao
import com.example.ffridge.data.remote.ApiService
import com.example.ffridge.data.repository.FoodRepositoryImpl
import com.example.ffridge.data.repository.RecipeRepositoryImpl
import com.example.ffridge.domain.repository.FoodRepository
import com.example.ffridge.domain.repository.RecipeRepository
import com.example.ffridge.domain.usecase.food.*
import com.example.ffridge.domain.usecase.recipe.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- 1. Database ---
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "food_database"
        ).build()
    }

    @Provides
    fun provideFoodDao(database: AppDatabase): FoodDao = database.foodDao()

    // --- 2. Network ---
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                val req = it.request().newBuilder()
                    .addHeader("X-RapidAPI-Key", "YOUR_API_KEY") // <-- TODO: Thay bằng API Key của bạn
                    .addHeader("X-RapidAPI-Host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                    .build()
                it.proceed(req)
            }
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // --- 3. Repositories ---
    @Provides
    @Singleton
    fun provideFoodRepository(db: AppDatabase, api: ApiService): FoodRepository {
        return FoodRepositoryImpl(db.foodDao(), api)
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(api: ApiService): RecipeRepository {
        return RecipeRepositoryImpl(api)
    }

    // --- 4. UseCases (Food) ---
    @Provides
    fun provideGetFoodsUseCase(repo: FoodRepository) = GetFoodsUseCase(repo)

    @Provides
    fun provideAddFoodUseCase(repo: FoodRepository) = AddFoodUseCase(repo)

    @Provides
    fun provideDeleteFoodUseCase(repo: FoodRepository) = DeleteFoodUseCase(repo)

    @Provides
    fun provideSearchFoodUseCase(repo: FoodRepository) = SearchFoodUseCase(repo)

    @Provides
    fun provideScanBarcodeUseCase(repo: FoodRepository) = ScanBarcodeUseCase(repo)

    // --- 5. UseCases (Recipe) ---
    @Provides
    fun provideGetRandomRecipeUseCase(repo: RecipeRepository) = GetRandomRecipeUseCase(repo)
}