package com.example.onetrysclada.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.onetrysclada.R
import com.example.onetrysclada.TableAdapter
import com.example.onetrysclada.data.models.Extradition
import com.example.onetrysclada.data.models.Product
import com.example.onetrysclada.data.models.ProductsCurrentQuantity
import com.example.onetrysclada.data.models.Shipment
import com.example.onetrysclada.data.models.User
import com.example.onetrysclada.data.models.WriteOffOfProducts
import com.example.onetrysclada.data.network.RetrofitClient
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TableActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout // Layout для бокового меню
    private lateinit var navView: NavigationView // Навигационное меню
    private lateinit var toolbar: Toolbar // Панель инструментов
    private lateinit var tableTitle: TextView // Заголовок таблицы
    private lateinit var tableLayout: TableLayout // Layout для отображения таблицы
    private lateinit var sortFieldSpinner: Spinner // Спиннер для выбора поля сортировки
    private lateinit var sortOrderSpinner: Spinner // Спиннер для выбора порядка сортировки
    private lateinit var searchEditText: EditText // Поле для поиска
    private lateinit var filterRoleSpinner: Spinner // Спиннер для фильтрации (например, по роли)

    private var users: MutableList<User> = mutableListOf() // Список пользователей
    private var shipments: MutableList<Shipment> = mutableListOf() // Список поставок
    private var products: MutableList<Product> = mutableListOf() // Список продуктов
    private var writeOffProducts: MutableList<WriteOffOfProducts> = mutableListOf() // Список списанных продуктов
    private var productsCurrentQuantities: MutableList<ProductsCurrentQuantity> = mutableListOf() // Список текущих количеств продуктов
    private var extraditions: MutableList<Extradition> = mutableListOf() // Список выдач

    private var tableAdapter: TableAdapter<*>? = null // Адаптер для текущей таблицы
    private var cachedUsers: List<User> = emptyList() // Кэш пользователей для оптимизации запросов
    private var currentTableType: TableType = TableType.USER // Текущий тип отображаемой таблицы

    // Перечисление для отслеживания типа таблицы
    enum class TableType {
        USER, SHIPMENT, PRODUCT, WRITE_OFF, QUANTITY, EXTRADITION
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.table)

        // Инициализация UI-элементов
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        tableLayout = findViewById(R.id.tableLayout)
        tableTitle = findViewById(R.id.table_title)
        sortFieldSpinner = findViewById(R.id.sortFieldSpinner)
        sortOrderSpinner = findViewById(R.id.sortOrderSpinner)
        searchEditText = findViewById(R.id.searchEditText)
        filterRoleSpinner = findViewById(R.id.filterOtherSpinner)

        // Настройка Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Настройка NavigationView
        navView.setNavigationItemSelectedListener { menuItem ->
            // Удаляем предыдущий слушатель текстового поля, чтобы избежать наложения
            searchEditText.removeTextChangedListener(searchEditText.tag as? TextWatcher)
            when (menuItem.itemId) {
                R.id.nav_users -> {
                    tableTitle.text = "User Table"
                    currentTableType = TableType.USER
                    updateSortFieldSpinner(listOf("ID", "Name", "Email", "Login"))
                    setFilterSpinnerVisibility(true)

                    // Настройка спиннера для фильтрации по ролям
                    val roles = listOf("All", "admin", "user")
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    filterRoleSpinner.adapter = adapter

                    filterRoleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            val selectedRole = roles[position]
                            val filters = if (selectedRole == "All") emptyMap() else mapOf("role" to selectedRole)
                            fetchUsers(filters = filters)
                        }
                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }

                    fetchUsers()

                    // Добавляем слушатель для поиска
                    val textWatcher = object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            filterUsers(s.toString())
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    }
                    searchEditText.addTextChangedListener(textWatcher)
                    searchEditText.tag = textWatcher
                }
                R.id.nav_shipments -> {
                    tableTitle.text = "Shipment Table"
                    currentTableType = TableType.SHIPMENT
                    setFilterSpinnerVisibility(false)
                    updateSortFieldSpinner(listOf("ID", "Date", "Usuario"))
                    showShipmentTable()

                    // Добавляем слушатель для поиска
                    val textWatcher = object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            filterShipments(s.toString())
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    }
                    searchEditText.addTextChangedListener(textWatcher)
                    searchEditText.tag = textWatcher
                }
                R.id.nav_products -> {
                    tableTitle.text = "Product Table"
                    currentTableType = TableType.PRODUCT
                    updateSortFieldSpinner(listOf("ID", "Name", "Produto", "Manufacturer"))
                    setFilterSpinnerVisibility(true)

                    // Настройка спиннера для фильтрации по типам продуктов
                    val productTypes = listOf("All", "Todos", "Tipo A", "Tipo B")
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, productTypes)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    filterRoleSpinner.adapter = adapter

                    filterRoleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            val selectedType = productTypes[position]
                            val filters = if (selectedType == "All") emptyMap() else mapOf("product_type" to selectedType)
                            fetchProducts(filters)
                        }
                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }

                    showProduct()

                    // Добавляем слушатель для поиска
                    val textWatcher = object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            filterProducts(s.toString())
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    }
                    searchEditText.addTextChangedListener(textWatcher)
                    searchEditText.tag = textWatcher
                }
                R.id.nav_write_off -> {
                    tableTitle.text = "Write Off Products Table"
                    currentTableType = TableType.WRITE_OFF
                    updateSortFieldSpinner(listOf("ID", "Usuario", "Quantidade", "Data"))
                    setFilterSpinnerVisibility(true)

                    // Настройка спиннера для фильтрации по причинам списания
                    val filters = listOf("All", "Todos", "Defeituoso", "Expirado")
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filters)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    filterRoleSpinner.adapter = adapter

                    filterRoleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            val selectedFilter = filters[position]
                            val filterParams = when (selectedFilter) {
                                "Defeituoso" -> mapOf("Motivo" to "Defeito")
                                "Expirado" -> mapOf("Motivo" to "Expirado")
                                else -> emptyMap()
                            }
                            fetchWriteOffProducts(filterParams)
                        }
                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }

                    showWriteOffProducts()

                    // Добавляем слушатель для поиска
                    val textWatcher = object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            filterWriteOffOfProducts(s.toString())
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    }
                    searchEditText.addTextChangedListener(textWatcher)
                    searchEditText.tag = textWatcher
                }
                R.id.nav_quantity -> {
                    tableTitle.text = "Products Current Quantity"
                    currentTableType = TableType.QUANTITY
                    setFilterSpinnerVisibility(false)
                    updateSortFieldSpinner(listOf("ID", "Nome do Produto", "Quantidade Disponível"))
                    showProductsCurrentQuantity()

                    // Добавляем слушатель для поиска
                    val textWatcher = object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            filterProductsCurrentQuantity(s.toString())
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    }
                    searchEditText.addTextChangedListener(textWatcher)
                    searchEditText.tag = textWatcher
                }
                R.id.nav_extradition -> {
                    tableTitle.text = "Extradition"
                    currentTableType = TableType.EXTRADITION
                    setFilterSpinnerVisibility(false)
                    updateSortFieldSpinner(listOf("ID", "Usuario", "Data", "Quantidade"))
                    showExtradition()

                    // Добавляем слушатель для поиска
                    val textWatcher = object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            filterExtradition(s.toString())
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    }
                    searchEditText.addTextChangedListener(textWatcher)
                    searchEditText.tag = textWatcher
                }
                R.id.nav_logout -> {
                    // Выход из аккаунта
                    RetrofitClient.clearToken(this)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    Toast.makeText(this, "Logout realizado", Toast.LENGTH_SHORT).show()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Инициализация спиннеров для сортировки
        val sortOrders = listOf("Crescente", "Decrescente")
        val orderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOrders)
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortOrderSpinner.adapter = orderAdapter

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

        // Открытие бокового меню по клику на иконку
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Загрузка таблицы пользователей по умолчанию
        navView.menu.findItem(R.id.nav_users).isChecked = true
        navView.menu.performIdentifierAction(R.id.nav_users, 0)
    }

    // Обновление списка полей для сортировки в спиннере
    private fun updateSortFieldSpinner(fields: List<String>) {
        val fieldAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fields)
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortFieldSpinner.adapter = fieldAdapter
    }

    // Обновление сортировки таблицы в зависимости от выбранных параметров
    private fun updateSorting() {
        val selectedField = sortFieldSpinner.selectedItem?.toString() ?: return
        val selectedOrder = sortOrderSpinner.selectedItem.toString()

        when (currentTableType) {
            TableType.USER -> {
                when (selectedField) {
                    "ID" -> users.sortBy { it.user_id }
                    "Name" -> users.sortBy { it.user_name }
                    "Email" -> users.sortBy { it.email }
                    "Login" -> users.sortBy { it.login }
                }
                if (selectedOrder == "Decrescente") users.reverse()
                tableAdapter = TableAdapter(this, tableLayout, users)
                tableAdapter?.populateTable("User")
            }
            TableType.SHIPMENT -> {
                when (selectedField) {
                    "ID" -> shipments.sortBy { it.shipment_id }
                    "Date" -> shipments.sortBy { it.date_of_shipment }
                    "Usuario" -> shipments.sortBy { it.user }
                }
                if (selectedOrder == "Decrescente") shipments.reverse()
                tableAdapter = TableAdapter(this, tableLayout, shipments)
                fetchUsersAndUpdateAdapter(tableAdapter!!) { tableAdapter?.populateTable("Shipment") }
            }
            TableType.PRODUCT -> {
                when (selectedField) {
                    "ID" -> products.sortBy { it.product_id }
                    "Name" -> products.sortBy { it.product_name }
                    "Produto" -> products.sortBy { it.shipment }
                    "Manufacturer" -> products.sortBy { it.manufacturer }
                }
                if (selectedOrder == "Decrescente") products.reverse()
                tableAdapter = TableAdapter(this, tableLayout, products)
                tableAdapter?.populateTable("Product")
            }
            TableType.WRITE_OFF -> {
                when (selectedField) {
                    "ID" -> writeOffProducts.sortBy { it.id_product_write_off }
                    "Usuario" -> writeOffProducts.sortBy { it.user }
                    "Quantidade" -> writeOffProducts.sortBy { it.quantity }
                    "Data" -> writeOffProducts.sortBy { it.product_write_off_date }
                }
                if (selectedOrder == "Decrescente") writeOffProducts.reverse()
                tableAdapter = TableAdapter(this, tableLayout, writeOffProducts)
                fetchUsersAndUpdateAdapter(tableAdapter!!) { tableAdapter?.populateTable("WriteOffProducts") }
            }
            TableType.QUANTITY -> {
                when (selectedField) {
                    "ID" -> productsCurrentQuantities.sortBy { it.product_current_quantity_id }
                    "Nome do Produto" -> productsCurrentQuantities.sortBy { it.product }
                    "Quantidade Disponível" -> productsCurrentQuantities.sortBy { it.quantity }
                }
                if (selectedOrder == "Decrescente") productsCurrentQuantities.reverse()
                tableAdapter = TableAdapter(this, tableLayout, productsCurrentQuantities)
                tableAdapter?.populateTable("ProductsCurrentQuantity")
            }
            TableType.EXTRADITION -> {
                when (selectedField) {
                    "ID" -> extraditions.sortBy { it.extradition_id }
                    "Usuario" -> extraditions.sortBy { it.user }
                    "Data" -> extraditions.sortBy { it.date_of_extradition }
                    "Quantidade" -> extraditions.sortBy { it.quantity }
                }
                if (selectedOrder == "Decrescente") extraditions.reverse()
                tableAdapter = TableAdapter(this, tableLayout, extraditions)
                fetchUsersAndUpdateAdapter(tableAdapter!!) { tableAdapter?.populateTable("Extradition") }
            }
        }
    }

    // Отображение таблицы поставок
    private fun showShipmentTable() {
        tableTitle.text = "Shipment Table"
        currentTableType = TableType.SHIPMENT
        fetchShipments()
    }

    // Отображение таблицы продуктов
    private fun showProduct() {
        tableTitle.text = "Product Table"
        currentTableType = TableType.PRODUCT
        fetchProducts()
    }

    // Отображение таблицы выдач
    private fun showExtradition() {
        tableTitle.text = "Extradition"
        currentTableType = TableType.EXTRADITION
        fetchExtradition()
    }

    // Отображение таблицы текущих количеств продуктов
    private fun showProductsCurrentQuantity() {
        tableTitle.text = "Products Current Quantity"
        currentTableType = TableType.QUANTITY
        fetchProductsCurrentQuantity()
    }

    // Отображение таблицы списанных продуктов
    private fun showWriteOffProducts() {
        tableTitle.text = "Write Off Products Table"
        currentTableType = TableType.WRITE_OFF
        fetchWriteOffProducts()
    }

    // Загрузка пользователей с фильтрацией и сортировкой
    private fun fetchUsers(search: String? = null, ordering: String? = null, filters: Map<String, String> = emptyMap()) {
        val call = RetrofitClient.getApiService(this).getUsersFiltered(ordering, search, filters)
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    users.clear()
                    response.body()?.let { users.addAll(it) }
                    tableAdapter = TableAdapter(this@TableActivity, tableLayout, users)
                    tableAdapter?.populateTable("User")
                } else {
                    handleErrorResponse(response.code())
                    Log.e("TableActivity", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(this@TableActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("TableActivity", "Failure: ${t.message}")
            }
        })
    }

    // Загрузка пользователей для обновления адаптера (с кэшированием)
    private fun fetchUsersAndUpdateAdapter(tableAdapter: TableAdapter<*>, onUsersLoaded: () -> Unit) {
        if (cachedUsers.isNotEmpty()) {
            Log.d("TableActivity", "Using cached users: $cachedUsers")
            tableAdapter.updateUsers(cachedUsers)
            onUsersLoaded()
            return
        }

        val call = RetrofitClient.getApiService(this).getUsers()
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        cachedUsers = it
                        Log.d("TableActivity", "Fetched users: $it")
                        tableAdapter.updateUsers(it)
                        onUsersLoaded()
                    }
                } else {
                    Log.e("TableActivity", "Error fetching users: ${response.code()}")
                    Toast.makeText(this@TableActivity, "Erro ao carregar usuários: ${response.code()}", Toast.LENGTH_SHORT).show()
                    onUsersLoaded()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("TableActivity", "Failure fetching users: ${t.message}")
                Toast.makeText(this@TableActivity, "Erro de rede: ${t.message}", Toast.LENGTH_SHORT).show()
                onUsersLoaded()
            }
        })
    }

    private fun fetchShipments() {
        val call = RetrofitClient.getApiService(this).getShipments()
        call.enqueue(object : Callback<List<Shipment>> {
            override fun onResponse(call: Call<List<Shipment>>, response: Response<List<Shipment>>) {
                if (response.isSuccessful) {
                    shipments.clear()
                    response.body()?.let { shipments.addAll(it) }
                    tableAdapter = TableAdapter(this@TableActivity, tableLayout, shipments)
                    fetchUsersAndUpdateAdapter(tableAdapter!!) {
                        filterShipments(searchEditText.text.toString())
                    }
                } else {
                    handleErrorResponse(response.code())
                    Log.e("TableActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Shipment>>, t: Throwable) {
                Toast.makeText(this@TableActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("TableActivity", "Failure: ${t.message}")
            }
        })
    }

    // Загрузка продуктов с фильтрацией
    private fun fetchProducts(filters: Map<String, String> = emptyMap()) {
        val call = RetrofitClient.getApiService(this).getProductsFiltered(filters = filters)
        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    products.clear()
                    response.body()?.let { products.addAll(it) }
                    tableAdapter = TableAdapter(this@TableActivity, tableLayout, products)
                    tableAdapter?.populateTable("Product")
                } else {
                    handleErrorResponse(response.code())
                    Log.e("TableActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@TableActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("TableActivity", "Failure: ${t.message}")
            }
        })
    }

    // Загрузка выдач
    private fun fetchExtradition() {
        val call = RetrofitClient.getApiService(this).getExtraditions()
        call.enqueue(object : Callback<List<Extradition>> {
            override fun onResponse(call: Call<List<Extradition>>, response: Response<List<Extradition>>) {
                if (response.isSuccessful) {
                    extraditions.clear()
                    response.body()?.let { extraditions.addAll(it) }
                    tableAdapter = TableAdapter(this@TableActivity, tableLayout, extraditions)
                    fetchUsersAndUpdateAdapter(tableAdapter!!) {
                        tableAdapter?.populateTable("Extradition")
                    }
                } else {
                    handleErrorResponse(response.code())
                    Log.e("TableActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Extradition>>, t: Throwable) {
                Toast.makeText(this@TableActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("TableActivity", "Failure: ${t.message}")
            }
        })
    }

    // Загрузка текущих количеств продуктов
    private fun fetchProductsCurrentQuantity() {
        val call = RetrofitClient.getApiService(this).getProductsCurrentQuantity()
        call.enqueue(object : Callback<List<ProductsCurrentQuantity>> {
            override fun onResponse(call: Call<List<ProductsCurrentQuantity>>, response: Response<List<ProductsCurrentQuantity>>) {
                if (response.isSuccessful) {
                    productsCurrentQuantities.clear()
                    response.body()?.let { productsCurrentQuantities.addAll(it) }
                    tableAdapter = TableAdapter(this@TableActivity, tableLayout, productsCurrentQuantities)
                    tableAdapter?.populateTable("ProductsCurrentQuantity")
                } else {
                    handleErrorResponse(response.code())
                    Log.e("TableActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ProductsCurrentQuantity>>, t: Throwable) {
                Toast.makeText(this@TableActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("TableActivity", "Failure: ${t.message}")
            }
        })
    }

    // Загрузка списанных продуктов с фильтрацией
    private fun fetchWriteOffProducts(filters: Map<String, String> = emptyMap()) {
        val call = RetrofitClient.getApiService(this).getWriteOffProductsFiltered(filters)
        call.enqueue(object : Callback<List<WriteOffOfProducts>> {
            override fun onResponse(call: Call<List<WriteOffOfProducts>>, response: Response<List<WriteOffOfProducts>>) {
                if (response.isSuccessful) {
                    writeOffProducts.clear()
                    response.body()?.let { writeOffProducts.addAll(it) }
                    tableAdapter = TableAdapter(this@TableActivity, tableLayout, writeOffProducts)
                    fetchUsersAndUpdateAdapter(tableAdapter!!) {
                        tableAdapter?.populateTable("WriteOffProducts")
                    }
                } else {
                    handleErrorResponse(response.code())
                    Log.e("TableActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<WriteOffOfProducts>>, t: Throwable) {
                Toast.makeText(this@TableActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("TableActivity", "Failure: ${t.message}")
            }
        })
    }

    // Фильтрация пользователей по запросу
    private fun filterUsers(query: String) {
        val filteredUsers = if (query.isEmpty()) {
            users
        } else {
            users.filter {
                it.user_id.toString().contains(query, ignoreCase = true) ||
                        it.user_name.contains(query, ignoreCase = true) ||
                        it.email.contains(query, ignoreCase = true) ||
                        it.login.contains(query, ignoreCase = true)
            }
        }
        tableAdapter = TableAdapter(this, tableLayout, filteredUsers)
        tableAdapter?.populateTable("User")
    }

    // Фильтрация поставок по запросу
    private fun filterShipments(query: String) {
        val filteredShipments = if (query.isEmpty()) {
            shipments
        } else {
            shipments.filter {
                it.shipment_id.toString().contains(query, ignoreCase = true) ||
                        it.date_of_shipment.contains(query, ignoreCase = true) ||
                        it.user.toString().contains(query, ignoreCase = true)
            }
        }
        tableAdapter = TableAdapter(this, tableLayout, filteredShipments)
        fetchUsersAndUpdateAdapter(tableAdapter!!) { tableAdapter?.populateTable("Shipment") }
    }

    // Фильтрация продуктов по запросу
    private fun filterProducts(query: String) {
        val filteredProducts = if (query.isEmpty()) {
            products
        } else {
            products.filter {
                it.product_id.toString().contains(query, ignoreCase = true) ||
                        it.product_name.contains(query, ignoreCase = true) ||
                        it.shipment.toString().contains(query, ignoreCase = true) ||
                        it.manufacturer.contains(query, ignoreCase = true)
            }
        }
        tableAdapter = TableAdapter(this, tableLayout, filteredProducts)
        tableAdapter?.populateTable("Product")
    }

    // Фильтрация списанных продуктов по запросу
    private fun filterWriteOffOfProducts(query: String) {
        val filteredWriteOffs = if (query.isEmpty()) {
            writeOffProducts
        } else {
            writeOffProducts.filter {
                it.id_product_write_off.toString().contains(query, ignoreCase = true) ||
                        it.user.toString().contains(query, ignoreCase = true) ||
                        it.quantity.toString().contains(query, ignoreCase = true) ||
                        it.reason.contains(query, ignoreCase = true) ||
                        it.product_write_off_date.contains(query, ignoreCase = true)
            }
        }
        tableAdapter = TableAdapter(this, tableLayout, filteredWriteOffs)
        fetchUsersAndUpdateAdapter(tableAdapter!!) { tableAdapter?.populateTable("WriteOffProducts") }
    }

    // Фильтрация выдач по запросу
    private fun filterExtradition(query: String) {
        val filteredExtraditions = if (query.isEmpty()) {
            extraditions
        } else {
            extraditions.filter {
                it.extradition_id.toString().contains(query, ignoreCase = true) ||
                        it.quantity.toString().contains(query, ignoreCase = true) ||
                        it.date_of_extradition.contains(query, ignoreCase = true) ||
                        it.user.toString().contains(query, ignoreCase = true)
            }
        }
        tableAdapter = TableAdapter(this, tableLayout, filteredExtraditions)
        fetchUsersAndUpdateAdapter(tableAdapter!!) { tableAdapter?.populateTable("Extradition") }
    }

    // Фильтрация текущих количеств продуктов по запросу
    private fun filterProductsCurrentQuantity(query: String) {
        val filteredProductsCurrentQuantities = if (query.isEmpty()) {
            productsCurrentQuantities
        } else {
            productsCurrentQuantities.filter {
                it.product_current_quantity_id.toString().contains(query, ignoreCase = true) ||
                        it.product.toString().contains(query, ignoreCase = true) ||
                        it.quantity.toString().contains(query, ignoreCase = true)
            }
        }
        tableAdapter = TableAdapter(this, tableLayout, filteredProductsCurrentQuantities)
        tableAdapter?.populateTable("ProductsCurrentQuantity")
    }

    // Установка видимости спиннера фильтрации
    private fun setFilterSpinnerVisibility(isVisible: Boolean) {
        filterRoleSpinner.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    // Обработка ошибок API
    private fun handleErrorResponse(code: Int) {
        when (code) {
            401 -> {
                Toast.makeText(this, "Não autorizado. Por favor, faça login novamente.", Toast.LENGTH_LONG).show()
                RetrofitClient.clearToken(this)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            403 -> Toast.makeText(this, "Acesso proibido.", Toast.LENGTH_LONG).show()
            else -> Toast.makeText(this, "Erro do servidor: $code", Toast.LENGTH_LONG).show()
        }
    }

    // Обработка нажатия кнопки "Назад"
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}