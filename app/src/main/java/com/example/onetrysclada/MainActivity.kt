package com.example.onetrysclada

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var loginButton: Button // Объявляем переменную для кнопки

    private lateinit var userButton: Button
    private lateinit var shipmentButton: Button
    private lateinit var productButton: Button
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
            tableTitle = findViewById(R.id.table_title)

            tableLayout = findViewById(R.id.tableLayout) // Убедитесь, что TableLayout инициализирован

            // Установите слушатели на кнопки
            userButton.setOnClickListener {
                //showUserTable()

                // Инициализация TableLayout
                //tableLayout = findViewById(R.id.tableLayout)

                // Загружаем данные
                fetchUsers2()
            }

            shipmentButton.setOnClickListener {
                showShipmentTable()
            }

            productButton.setOnClickListener {
                showProductTable()
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


    private fun fetchUsers2() {
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

//    private fun fetchUsers() {
//        RetrofitClient.apiService.getUsers().enqueue(object : Callback<List<User>> {
//            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
//                if (response.isSuccessful) {
//                    val users = response.body() ?: listOf()
//                    val tableLayout = findViewById<TableLayout>(R.id.tableLayout) // Найдите ваш TableLayout по ID
//
//                    // Очистите предыдущие строки (если необходимо)
//                    tableLayout.removeViewsInLayout(1, tableLayout.childCount - 1)
//
//                    // Добавьте строки для каждого пользователя
//                    for (user in users) {
//                        val row = TableRow(this@MainActivity)
//                        row.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
//
//                        // Создайте TextView для каждой ячейки
//                        val idTextView = TextView(this@MainActivity).apply { text = user.user_id.toString() }
//                        val nameTextView = TextView(this@MainActivity).apply { text = user.user_name }
//                        val loginTextView = TextView(this@MainActivity).apply { text = user.login }
//                        val passwordTextView = TextView(this@MainActivity).apply { text = user.password }
//                        val emailTextView = TextView(this@MainActivity).apply { text = user.email }
//                        val phoneTextView = TextView(this@MainActivity).apply { text = user.phone_number }
//                        val roleTextView = TextView(this@MainActivity).apply { text = user.role }
//
//                        // Добавьте TextView в строку
//                        row.addView(idTextView)
//                        row.addView(nameTextView)
//                        row.addView(loginTextView)
//                        row.addView(passwordTextView)
//                        row.addView(emailTextView)
//                        row.addView(phoneTextView)
//                        row.addView(roleTextView)
//
//                        // Добавьте строку в TableLayout
//                        tableLayout.addView(row)
//                    }
//                } else {
//                    Log.e("MainActivity", "Error: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<List<User>>, t: Throwable) {
//                Log.e("MainActivity", "Failure: ${t.message}")
//            }
//        })
//    }

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
}
