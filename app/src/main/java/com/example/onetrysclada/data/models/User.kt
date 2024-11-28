package com.example.onetrysclada.data.models

data class User(
    val user_id: Int,
    val user_name: String,
    val email: String,
    val login: String,
    val password: String,
    val phone_number: String?,
    val role: String
)