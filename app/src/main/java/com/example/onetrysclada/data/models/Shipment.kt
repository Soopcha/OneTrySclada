package com.example.onetrysclada.data.models

data class Shipment(
    val shipment_id: Int,
    val quantity: Int,
    val date_of_shipment: String,
    val user: Int // Изменено с `User` на `Int`
)