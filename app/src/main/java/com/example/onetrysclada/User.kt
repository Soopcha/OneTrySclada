package com.example.onetrysclada

data class User(
    val user_id: Int,
    val user_name: String,
    val email: String,
    val login: String,
    val password: String,
    val phone_number: String?,
    val role: String
)

data class Shipment(
    val shipment_id: Int,
    val quantity: Int,
    val date_of_shipment: String,
    val user: Int // Изменено с `User` на `Int`
)

data class WriteOffOfProducts(
    val id_product_write_off: Int,
    val product_write_off_date: String,
    val quantity: Int,
    val reason: String,
    val user: Int // Изменено с `User` на `Int`
)

data class Extradition(
    val extradition_id: Int,
    val date_of_extradition: String,
    val quantity: Int,
    val user: Int // Изменено с `User` на `Int`
)

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

data class ProductsCurrentQuantity(
    val product_current_quantity_id: Int,
    val quantity: Int,
    val product: Int // Изменено с `Product` на `Int`
)