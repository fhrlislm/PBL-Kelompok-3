package com.weatherwave.model

data class ApiResponse(
    val status: Boolean,
    val message: String?,
    val name: String?,
    val email: String?,
    val password: String?,
    val kode_kapal: String?,
    val domisili: String?,
    val no_telp: String?,
    val role_id: Int?,
    val user_id: Int?,
    val data: Users?
)

data class WeatherResponse(
    val current: Current,
    val location: Location
)

data class Current(
    val temp_c: Double,
    val wind_kph: Double,
    val humidity: Int,
    val precip_mm: Double,
    val condition: Condition
)

data class Condition(
    val text: String,
    val icon: String
)

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val localtime: String
)

data class LaporanRequest(
    val user_id: Int, // Menggunakan tipe data Int untuk user_id
    val jenis_masalah: String, // Jenis masalah yang dilaporkan
    val foto: String?, // Foto (base64) yang dikirimkan, bisa null jika tidak ada
//    val latitude: Double,  // Menambahkan latitude
//    val longitude: Double  // Menambahkan longitude
)
