package com.example.onetrysclada.data.models

data class Product(
    val product_id: Int,
    val product_name: String,
    val expire_date: String,
    val product_type: String,
    val manufacturer: String,
    val weight: Double,
    val shipment: Int, // Изменено с `Shipment` на `Int`
    val write_off_of_products: Int?, // Изменено с `WriteOffOfProducts?` на `Int`
    val extradition: Int? // Изменено с `Extradition?` на `Int`
)