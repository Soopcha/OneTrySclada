package com.example.onetrysclada

import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path


interface ApiService {

    @GET("api/users/") // Убедитесь, что путь соответствует вашему API
    fun getUsers(): Call<List<User>>

    @GET("api/shipments/") // Например, для отгрузок
    fun getShipments(): Call<List<Shipment>>

    @GET("api/products/") // Для продуктов
    fun getProducts(): Call<List<Product>>

    @GET("api/extraditions/") // Для экстрадиций
    fun getExtraditions(): Call<List<Extradition>>

    @GET("api/products-current-quantity/") // Для текущего количества продуктов
    fun getProductsCurrentQuantity(): Call<List<ProductsCurrentQuantity>>

    @GET("api/write-off-products/") // Для списания продуктов
    fun getWriteOffProducts(): Call<List<WriteOffOfProducts>>



    @PUT("api/users/{id}/")
    fun updateUser(@Path("id") userId: Int, @Body user: User): Call<User>

    @PUT("api/shipments/{id}/")
    fun updateShipment(@Path("id") shipmentId: Int, @Body shipment: Shipment): Call<Shipment>

    @PUT("api/products/{id}/")
    fun updateProduct(@Path("id") productId: Int, @Body product: Product): Call<Product>





    @DELETE("api/products/{id}/")
    fun deleteProduct(@Path("id") productId: Int): Call<Void>




    // Добавьте другие вызовы API по мере необходимости
}