package com.weatherwave.network

import com.weatherwave.model.ApiResponse
import com.weatherwave.model.LaporanRequest
import com.weatherwave.model.WeatherResponse
import com.weatherwave.model.Users
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @POST("kelompok_3/WeatherWave/reg.php")
    fun registerUser(@Body user: Users): Call<ApiResponse>

    @POST("kelompok_3/WeatherWave/log.php")
    fun loginUser(@Body user: Users): Call<ApiResponse>

    @POST("kelompok_3/WeatherWave/upd.php")
    fun updateUser(@Body user: Users): Call<ApiResponse>

    @POST("kelompok_3/WeatherWave/add_laporan.php")
    fun addLaporan(@Body laporanRequest: LaporanRequest): Call<ApiResponse>

//    @GET("kelompok_3/WeatherWave/get_laporan.php")
//    fun getLaporan(): Call<List<Laporan>> // Mengambil daftar laporan dari API

    @GET("current.json")
    fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String
    ): Call<WeatherResponse>
}



