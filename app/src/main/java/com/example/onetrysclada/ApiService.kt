package com.example.onetrysclada

import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path


interface ApiService {

    @GET("api/users/") // Убедитесь, что путь соответствует вашему API
    fun getUsers(): Call<List<User>>

    @GET("api/shipments/") // Например, для отгрузок
    fun getShipments(): Call<List<Shipment>>

    @GET("api/products/") // Для продуктов
    fun getProducts(): Call<List<Product>>

    @PUT("api/users/{id}/")
    fun updateUser(@Path("id") userId: Int, @Body user: User): Call<User>


    // Добавьте другие вызовы API по мере необходимости
}