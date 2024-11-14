package com.example.onetrysclada

import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface ApiService {

    //ЧТЕНИЕ или ПОЛУЧЕНИЕ

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



    //ИЗМЕНЕНИЕ или ОБНАВЛЕНИЕ

    @PUT("api/users/{id}/")
    fun updateUser(@Path("id") userId: Int, @Body user: User): Call<User>

    @PUT("api/shipments/{id}/")
    fun updateShipment(@Path("id") shipmentId: Int, @Body shipment: Shipment): Call<Shipment>

    @PUT("api/products/{id}/")
    fun updateProduct(@Path("id") productId: Int, @Body product: Product): Call<Product>

    @PUT("api/extraditions/{id}/")
    fun updateExtradition(@Path("id") extraditionId: Int, @Body extradition: Extradition): Call<Extradition>

    @PUT("api/products-current-quantity/{id}/")
    fun updateProductsCurrentQuantity(@Path("id") id: Int, @Body productsCurrentQuantity: ProductsCurrentQuantity): Call<ProductsCurrentQuantity>

    @PUT("api/write-off-products/{id}/")
    fun updateWriteOffProduct(@Path("id") writeOffId: Int, @Body writeOffOfProducts: WriteOffOfProducts): Call<WriteOffOfProducts>



    //ДОБАВЛЕНИЕ

    @POST("api/users/")
    fun addUser(@Body user: User): Call<User>

    @POST("api/shipments/")
    fun addShipment(@Body shipment: Shipment): Call<Shipment>

    @POST("api/products/")
    fun addProduct(@Body product: Product): Call<Product>

    @POST("api/extraditions/")
    fun addExtradition(@Body extradition: Extradition): Call<Extradition>

    @POST("api/products-current-quantity/")
    fun addProductsCurrentQuantity(@Body productsCurrentQuantity: ProductsCurrentQuantity): Call<ProductsCurrentQuantity>

    @POST("api/write-off-products/")
    fun addWriteOffProduct(@Body writeOffOfProducts: WriteOffOfProducts): Call<WriteOffOfProducts>



    //УДАЛЕНИЕ

    @DELETE("api/users/{id}/")
    fun deleteUser(@Path("id") userId: Int): Call<Void>

    @DELETE("api/shipments/{id}/")
    fun deleteShipment(@Path("id") shipmentId: Int): Call<Void>

    @DELETE("api/products/{id}/")
    fun deleteProduct(@Path("id") productId: Int): Call<Void>

    @DELETE("api/extraditions/{id}/")
    fun deleteExtradition(@Path("id") extraditionId: Int): Call<Void>

    @DELETE("api/products-current-quantity/{id}/")
    fun deleteProductsCurrentQuantity(@Path("id") id: Int): Call<Void>

    @DELETE("api/write-off-products/{id}/")
    fun deleteWriteOffProduct(@Path("id") writeOffId: Int): Call<Void>

}