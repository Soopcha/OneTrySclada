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
    val user: User
)

data class WriteOffOfProducts(
    val id_product_write_off: Int,
    val product_write_off_date: String,
    val quantity: Int,
    val reason: String,
    val user: User
)

data class Extradition(
    val extradition_id: Int,
    val date_of_extradition: String,
    val quantity: Int,
    val user: User
)

data class Product(
    val product_id: Int,
    val product_name: String,
    val expire_date: String,
    val product_type: String,
    val manufacturer: String,
    val weight: Double,
    val shipment: Shipment,
    val write_off_of_products: WriteOffOfProducts?,
    val extradition: Extradition?
)

data class ProductsCurrentQuantity(
    val product_current_quantity_id: Int,
    val quantity: Int,
    val product: Product
)