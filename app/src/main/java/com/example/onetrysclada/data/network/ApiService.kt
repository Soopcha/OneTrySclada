package com.example.onetrysclada.data.network

import com.example.onetrysclada.data.models.Extradition
import com.example.onetrysclada.data.models.Product
import com.example.onetrysclada.data.models.ProductsCurrentQuantity
import com.example.onetrysclada.data.models.Shipment
import com.example.onetrysclada.data.models.User
import com.example.onetrysclada.data.models.WriteOffOfProducts
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap


interface ApiService {

    //ЧТЕНИЕ или ПОЛУЧЕНИЕ

    @GET("api/users/") // Убедитесь, что путь соответствует вашему API
    fun getUsers(): Call<List<User>>

//    @GET("api/users/") // Убедитесь, что путь соответствует вашему API
//    fun getUsers(
//        @Query("ordering") ordering: String?, // Для сортировки
//        @Query("search") search: String?,    // Для поиска
//        @QueryMap filters: Map<String, String> = emptyMap()       // Дополнительное поле фильтрации
//    ): Call<List<User>>

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

    @GET("api/users/")
    fun getUsersFiltered(
        @Query("ordering") ordering: String?, // Для сортировки
        @Query("search") search: String?,    // Для поиска
        @QueryMap filters: Map<String, String> = emptyMap()       // Дополнительное поле фильтрации
    ): Call<List<User>>





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
    fun deleteWriteOffOfProducts(@Path("id") writeOffId: Int): Call<Void>

}
/*
Для проверки защиты от XSS, SQL-инъекций и других уязвимостей в вашем приложении, вы можете использовать тестовые строки, чтобы попытаться внедрить вредоносный код в различные поля ввода или запросы. Вот как можно это сделать:

1. Тестирование на XSS (Cross-Site Scripting):
Попробуйте ввести следующий код в текстовые поля (например, в таблице User или Product):

html
Копировать код
<script>alert('XSS');</script>
Ожидание:
Этот код должен отображаться как текст, а не запускаться.
Если он запускается, значит, приложение уязвимо для XSS.
2. Тестирование на SQL-инъекции:
Попробуйте ввести следующие строки в поля ввода, которые связаны с базой данных:

sql
Копировать код
' OR '1'='1'; --
или

sql
Копировать код
'; DROP TABLE users; --
Ожидание:
Валидация данных должна предотвратить выполнение SQL-инъекции.
Если запрос ломается или выполняет неожиданные действия, например, удаляет таблицу, то защита отсутствует.
3. Тестирование на HTML-инъекции:
Попробуйте ввести следующий HTML-код:

html
Копировать код
<b>Injected HTML</b>
Ожидание:
Этот код должен быть отображен как текст (<b>Injected HTML</b>), а не как отформатированный HTML (Injected HTML).
4. Тестирование на CSRF (Cross-Site Request Forgery):
Если у вас есть формы с POST-запросами, проверьте:

Присутствует ли скрытое поле CSRF-токена в исходном коде страницы.
Попробуйте отправить запрос через сторонний инструмент (например, Postman) без CSRF-токена.
Ожидание:
Запросы без CSRF-токена должны быть отклонены с ошибкой.
5. Общие тестовые строки:
Вот несколько универсальных строк для проверки различных инъекций:

XSS: <img src=x onerror=alert(1)>
SQL-инъекция: 1' OR '1'='1
HTML-инъекция: <div style="background:red;">Injected</div>
6. Тестирование защиты в Django:
Встроенные механизмы:
XSS: Django экранирует HTML автоматически.
SQL: Django ORM экранирует параметры запросов.
CSRF: Django middleware добавляет защиту от CSRF.
Как проверить:
Убедитесь, что вы используете Django ORM для работы с базой данных и не отключаете экранирование шаблонов.
Проверьте, что CSRF middleware включен в MIDDLEWARE:
python
Копировать код
MIDDLEWARE = [
    ...
    'django.middleware.csrf.CsrfViewMiddleware',
    ...
]
7. Автоматические инструменты проверки:
OWASP ZAP или Burp Suite: Эти инструменты помогут обнаружить XSS, CSRF и другие уязвимости.
Python Bandit: Анализирует ваш код на наличие ошибок безопасности.
bash
Копировать код
pip install bandit
bandit -r your_project/
Итог:
Попробуйте использовать описанные тестовые строки в вашем приложении, чтобы проверить его защиту. Если ваше приложение работает на Django и вы следуете стандартным практикам, например, используете Django ORM и шаблоны, оно уже защищено от большинства уязвимостей. Однако тестирование всегда помогает подтвердить это.
 */