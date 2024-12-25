package com.example.onetrysclada.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
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
    private var shipments: MutableList<Shipment> = mutableListOf()
    private var products: MutableList<Product> = mutableListOf()
    private var writeOffProducts: MutableList<WriteOffOfProducts> = mutableListOf()
    private var productsCurrentQuantities: MutableList<ProductsCurrentQuantity> = mutableListOf()
    private var extraditions: MutableList<Extradition> = mutableListOf()


    private lateinit var sortFieldSpinner: Spinner
    private lateinit var sortOrderSpinner: Spinner

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
                tableTitle.text = "User Table"
                updateSortFieldSpinner(listOf("ID", "Name", "Email", "Login"))
                fetchUsers()
            }

            shipmentButton.setOnClickListener {
                updateSortFieldSpinner(listOf("ID", "Date", "User"))
                showShipmentTable()
            }

            productButton.setOnClickListener {
                updateSortFieldSpinner(listOf("ID", "Name", "Shipment", "Manufacturer"))
                showProductTable()
            }

            writeOffProductsButton.setOnClickListener {
                updateSortFieldSpinner(listOf("ID", "User", "Quantity", "Date"))
                showWriteOffProducts()
            }

            productsCurrentQuantityButton.setOnClickListener {
                updateSortFieldSpinner(listOf("ID", "Product Name", "Available Quantity"))
                showProductsCurrentQuantity()
            }

            extraditionButton.setOnClickListener {
                updateSortFieldSpinner(listOf("ID", "User", "Date", "Quantity"))
                showExtradition()
            }


            // Инициализируйте Spinner
            sortFieldSpinner = findViewById(R.id.sortFieldSpinner)
            sortOrderSpinner = findViewById(R.id.sortOrderSpinner)

            // Заполните Spinner данными
            val sortFields = listOf("ID", "Name", "Email", "Login") // Замените на поля из вашего API
            val sortOrders = listOf("Ascending", "Descending")

            val fieldAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortFields)
            fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sortFieldSpinner.adapter = fieldAdapter

            val orderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOrders)
            orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sortOrderSpinner.adapter = orderAdapter

            // Установите слушатели изменений Spinner
            sortFieldSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    updateSorting()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            sortOrderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    updateSorting()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }



        }
    }

    private fun updateSortFieldSpinner(fields: List<String>) {
        val fieldAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fields)
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortFieldSpinner.adapter = fieldAdapter
    }

    private fun updateSorting() {
        val selectedField = sortFieldSpinner.selectedItem.toString()
        val selectedOrder = sortOrderSpinner.selectedItem.toString()

        when (tableTitle.text) {
            "User Table" -> {
                when (selectedField) {
                    "ID" -> users.sortBy { it.user_id }
                    "Name" -> users.sortBy { it.user_name }
                    "Email" -> users.sortBy { it.email }
                    "Login" -> users.sortBy { it.login }
                }
                if (selectedOrder == "Descending") users.reverse()
                val tableAdapter = TableAdapter(this, tableLayout, users)
                tableAdapter.populateTable("User")
                Log.d("MainActivity", "you are here: ${tableTitle.text}")
            }

            "Shipment Table" -> {
                when (selectedField) {
                    "ID" -> shipments.sortBy { it.shipment_id }
                    "Date" -> shipments.sortBy { it.date_of_shipment }
                    "Destination" -> shipments.sortBy { it.user }
                }
                if (selectedOrder == "Descending") shipments.reverse()
                val tableAdapter = TableAdapter(this, tableLayout, shipments)
                tableAdapter.populateTable("Shipment")
            }

            "Product Table" -> {
                when (selectedField) {
                    "ID" -> products.sortBy { it.product_id }
                    "Name" -> products.sortBy { it.product_name }
                    "Category" -> products.sortBy { it.shipment }
                    "Price" -> products.sortBy { it.manufacturer }
                }
                if (selectedOrder == "Descending") products.reverse()
                val tableAdapter = TableAdapter(this, tableLayout, products)
                tableAdapter.populateTable("Product")
            }

            "Write Off Products Table" -> {
                when (selectedField) {
                    "ID" -> writeOffProducts.sortBy { it.id_product_write_off }
                    "Product Name" -> writeOffProducts.sortBy { it.user }
                    "Quantity" -> writeOffProducts.sortBy { it.quantity }
                    "Date" -> writeOffProducts.sortBy { it.product_write_off_date }
                }
                if (selectedOrder == "Descending") writeOffProducts.reverse()
                val tableAdapter = TableAdapter(this, tableLayout, writeOffProducts)
                tableAdapter.populateTable("WriteOffProducts")
            }

            "Products Current Quantity" -> {
                when (selectedField) {
                    "ID" -> productsCurrentQuantities.sortBy { it.product_current_quantity_id }
                    "Product Name" -> productsCurrentQuantities.sortBy { it.product }
                    "Available Quantity" -> productsCurrentQuantities.sortBy { it.quantity }
                }
                if (selectedOrder == "Descending") productsCurrentQuantities.reverse()
                val tableAdapter = TableAdapter(this, tableLayout, productsCurrentQuantities)
                tableAdapter.populateTable("ProductsCurrentQuantity")
            }

            "Extradition" -> {
                when (selectedField) {
                    "ID" -> extraditions.sortBy { it.extradition_id }
                    "Recipient" -> extraditions.sortBy { it.user }
                    "Date" -> extraditions.sortBy { it.date_of_extradition }
                    "Quantity" -> extraditions.sortBy { it.quantity }
                }
                if (selectedOrder == "Descending") extraditions.reverse()
                val tableAdapter = TableAdapter(this, tableLayout, extraditions)
                tableAdapter.populateTable("Extradition")
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
                    shipments.clear() // Очистите список перед добавлением новых данных
                    response.body()?.let { shipments.addAll(it) }

                    val tableAdapter = TableAdapter(this@MainActivity, tableLayout, shipments)
                    tableAdapter.populateTable("Shipment")
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
                    products.clear() // Очистите список перед добавлением новых данных
                    response.body()?.let { products.addAll(it) }

                    val tableAdapter = TableAdapter(this@MainActivity, tableLayout, products)
                    tableAdapter.populateTable("Product")
                } else {
                    Log.e("MainActivity", "Error: ${response.code()}")
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
                    extraditions.clear()
                    response.body()?.let { extraditions.addAll(it) }

                    val tableAdapter = TableAdapter(this@MainActivity, tableLayout, extraditions)
                    tableAdapter.populateTable("Extradition")
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
                    productsCurrentQuantities.clear()
                    response.body()?.let { productsCurrentQuantities.addAll(it) }

                    val tableAdapter = TableAdapter(this@MainActivity, tableLayout, productsCurrentQuantities)
                    tableAdapter.populateTable("ProductsCurrentQuantity")
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
                    writeOffProducts.clear()
                    response.body()?.let { writeOffProducts.addAll(it) }

                    val tableAdapter = TableAdapter(this@MainActivity, tableLayout, writeOffProducts)
                    tableAdapter.populateTable("WriteOffProducts")
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
