package com.example.onetrysclada.data.models

data class WriteOffOfProducts(
    val id_product_write_off: Int,
    val product_write_off_date: String,
    val quantity: Int,
    val reason: String,
    val user: Int // Изменено с `User` на `Int`
)