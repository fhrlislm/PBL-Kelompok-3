package com.weatherwave.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://tkj-3b.com/api/"  // Ganti dengan URL API Anda

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())  // Untuk konversi JSON ke objek Kotlin
            .build()

        retrofit.create(ApiService::class.java)
    }
}