package com.example.onetrysclada.data.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("user_name") val user_name: String,
    @SerializedName("email") val email: String,
    @SerializedName("login") val login: String,
    @SerializedName("password") val password: String?,
    @SerializedName("phone_number") val phone_number: String?,
    @SerializedName("role") val role: String
)