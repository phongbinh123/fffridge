package com.example.ffridge.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Vì chúng ta gọi 2 Host khác nhau nhưng cùng dùng Retrofit,
    // ta có thể dùng 1 instance chung với baseUrl mặc định (hoặc xử lý full URL trong @GET như trên).
    // Ở đây ta dùng base URL ảo vì trong ApiService ta đã viết full URL.

    private const val BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}