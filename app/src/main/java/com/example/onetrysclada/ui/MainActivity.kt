package com.example.onetrysclada.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.widget.Button
import android.widget.TableLayout
import android.widget.TextView
import com.example.onetrysclada.data.models.Extradition
import com.example.onetrysclada.data.models.Product
import com.example.onetrysclada.data.models.ProductsCurrentQuantity
import com.example.onetrysclada.R
import com.example.onetrysclada.data.models.Shipment
import com.example.onetrysclada.TableAdapter
import com.example.onetrysclada.data.models.User
import com.example.onetrysclada.data.models.WriteOffOfProducts
import com.example.onetrysclada.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var loginButton: Button // Объявляем переменную для кнопки

    private lateinit var userButton: Button
    private lateinit var shipmentButton: Button
    private lateinit var productButton: Button
    private lateinit var writeOffProductsButton: Button
    private lateinit var productsCurrentQuantityButton: Button
    private lateinit var extraditionButton: Button



    private lateinit var tableTitle: TextView // TextView для вывода заголовка таблицы



    private lateinit var tableLayout: TableLayout
    private var users: MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Найдите кнопку по ID
        loginButton = findViewById(R.id.login_button) // Предполагаем, что у вас есть кнопка с этим ID

        // Установите слушатель нажатия на кнопку
        loginButton.setOnClickListener {
            // Меняем лейаут на table.xml
            setContentView(R.layout.table)

            // Найдите кнопки и TextView по ID
            userButton = findViewById(R.id.user_button)
            shipmentButton = findViewById(R.id.shipment_button)
            productButton = findViewById(R.id.product_button)
            writeOffProductsButton = findViewById(R.id.write_off_products_button)  // Initialize here
            productsCurrentQuantityButton = findViewById(R.id.products_current_quantity_button)
            extraditionButton = findViewById(R.id.extradition_button)
            tableTitle = findViewById(R.id.table_title)

            tableLayout = findViewById(R.id.tableLayout) // Убедитесь, что TableLayout инициализирован

            // Установите слушатели на кнопки
            userButton.setOnClickListener {
                fetchUsers()
            }

            shipmentButton.setOnClickListener {
                showShipmentTable()
            }

            productButton.setOnClickListener {
                showProductTable()
            }

            writeOffProductsButton.setOnClickListener {
                showWriteOffProducts()
            }

            productsCurrentQuantityButton.setOnClickListener {
                showProductsCurrentQuantity()
            }

            extraditionButton.setOnClickListener {
                showExtradition()
            }





        }
    }

//    private fun showUserTable() {
//        // Обновляем заголовок таблицы и выводим данные пользователей
//        tableTitle.text = "User Table"
//        fetchUsers()
//    }

    private fun showShipmentTable() {
        // Обновляем заголовок таблицы и выводим данные отгрузок
        tableTitle.text = "Shipment Table"
        fetchShipments() // Ваша логика получения данных отгрузок
    }

    private fun showProductTable() {
        // Обновляем заголовок таблицы и выводим данные продуктов
        tableTitle.text = "Product Table"
        fetchProducts() // Ваша логика получения данных продуктов
    }

    private fun showExtradition() {
        // Обновляем заголовок таблицы и выводим данные продуктов
        tableTitle.text = "Extradition"
        fetchExtradition() // Ваша логика получения данных продуктов
    }

    private fun showProductsCurrentQuantity() {
        // Обновляем заголовок таблицы и выводим данные продуктов
        tableTitle.text = "Products Current Quantity"
        Log.d("MainActivity", "Products Current Quantity button clicked")
        fetchProductsCurrentQuantity() // Ваша логика получения данных продуктов
    }

    private fun showWriteOffProducts() {
        // Обновляем заголовок таблицы и выводим данные продуктов
        tableTitle.text = "Write Off Products Table"
        fetchWriteOffProducts() // Ваша логика получения данных продуктов
    }


    private fun fetchUsers() {
        val call = RetrofitClient.apiService.getUsers()
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    users.clear()
                    response.body()?.let { users.addAll(it) }

                    // Создаём адаптер и заполняем таблицу
                    val tableAdapter = TableAdapter(this@MainActivity, tableLayout, users)
                    tableAdapter.populateTable("User")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                // Обработка ошибки
            }
        })
    }

    private fun fetchShipments() {
        val call = RetrofitClient.apiService.getShipments()
        call.enqueue(object : Callback<List<Shipment>> {
            override fun onResponse(call: Call<List<Shipment>>, response: Response<List<Shipment>>) {
                if (response.isSuccessful) {
                    response.body()?.let { shipments ->
                        val tableAdapter = TableAdapter(this@MainActivity, tableLayout, shipments)
                        tableAdapter.populateTable("Shipment")
                    }
                }
            }

            override fun onFailure(call: Call<List<Shipment>>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
            }
        })
    }

    private fun fetchProducts() {
        val call = RetrofitClient.apiService.getProducts()
        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    response.body()?.let { products ->
                        val tableAdapter = TableAdapter(this@MainActivity, tableLayout, products)
                        tableAdapter.populateTable("Product")
                    }
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
            }
        })
    }



    private fun fetchExtradition() {
        val call = RetrofitClient.apiService.getExtraditions() // Предположим, у вас есть эндпоинт для экстрадиции
        call.enqueue(object : Callback<List<Extradition>> {
            override fun onResponse(call: Call<List<Extradition>>, response: Response<List<Extradition>>) {
                if (response.isSuccessful) {
                    response.body()?.let { extraditions ->
                        val tableAdapter = TableAdapter(this@MainActivity, tableLayout, extraditions)
                        tableAdapter.populateTable("Extradition") // "Extradition" – это название для таблицы
                    }
                } else {
                    Log.e("MainActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Extradition>>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
            }
        })
    }

    private fun fetchProductsCurrentQuantity() {
        val call = RetrofitClient.apiService.getProductsCurrentQuantity() // Предположим, у вас есть эндпоинт для текущего количества продуктов
        call.enqueue(object : Callback<List<ProductsCurrentQuantity>> {
            override fun onResponse(call: Call<List<ProductsCurrentQuantity>>, response: Response<List<ProductsCurrentQuantity>>) {
                if (response.isSuccessful) {
                    // Логируем ответ для проверки данных
                    Log.d("MainActivity", "Response: ${response.body()}")

                    response.body()?.let { productsCurrentQuantities ->
                        val tableAdapter = TableAdapter(this@MainActivity, tableLayout, productsCurrentQuantities)
                        tableAdapter.populateTable("ProductsCurrentQuantity") // Название для таблицы текущего количества продуктов
                    }
                } else {
                    Log.e("MainActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ProductsCurrentQuantity>>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
            }
        })
    }

    private fun fetchWriteOffProducts() {
        val call = RetrofitClient.apiService.getWriteOffProducts() // Предположим, у вас есть эндпоинт для списания продуктов
        call.enqueue(object : Callback<List<WriteOffOfProducts>> {
            override fun onResponse(call: Call<List<WriteOffOfProducts>>, response: Response<List<WriteOffOfProducts>>) {
                if (response.isSuccessful) {
                    response.body()?.let { writeOffProducts ->
                        val tableAdapter = TableAdapter(this@MainActivity, tableLayout, writeOffProducts)
                        tableAdapter.populateTable("WriteOffProducts") // Название для таблицы списания продуктов
                    }
                } else {
                    Log.e("MainActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<WriteOffOfProducts>>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
            }
        })
    }

}
