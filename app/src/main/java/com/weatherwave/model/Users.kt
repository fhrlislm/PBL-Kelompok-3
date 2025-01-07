package com.weatherwave.model

data class Users(
    val user_id: Int = 0,
    val nama: String,
    val email: String,
    val password: String,
    val kode_kapal: String = "",
    val domisili: String = "",
    val no_telp: String = "",
    val role_id: Int = 0
)

