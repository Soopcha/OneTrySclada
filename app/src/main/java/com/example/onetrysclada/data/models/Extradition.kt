package com.example.onetrysclada.data.models

data class Extradition(
    val extradition_id: Int,
    val date_of_extradition: String,
    val quantity: Int,
    val user: Int // Изменено с `User` на `Int`
)