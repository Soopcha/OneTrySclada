package com.example.onetrysclada.data.models

data class ProductsCurrentQuantity(
    val product_current_quantity_id: Int,
    val quantity: Int,
    val product: Int // Изменено с `Product` на `Int`
)