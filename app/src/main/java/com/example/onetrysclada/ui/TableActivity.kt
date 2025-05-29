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

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var tableTitle: TextView
    private lateinit var tableLayout: TableLayout
    private lateinit var sortFieldSpinner: Spinner
    private lateinit var sortOrderSpinner: Spinner
    private lateinit var searchEditText: EditText
    private lateinit var filterRoleSpinner: Spinner

    private var users: MutableList<User> = mutableListOf()
    private var shipments: MutableList<Shipment> = mutableListOf()
    private var products: MutableList<Product> = mutableListOf()
    private var writeOffProducts: MutableList<WriteOffOfProducts> = mutableListOf()
    private var productsCurrentQuantities: MutableList<ProductsCurrentQuantity> = mutableListOf()
    private var extraditions: MutableList<Extradition> = mutableListOf()

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
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu) // Иконка гамбургер-меню
        supportActionBar?.setDisplayShowTitleEnabled(false) // Отключаем заголовок Toolbar

        // Настройка NavigationView
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_users -> {
                    tableTitle.text = "User Table"
                    updateSortFieldSpinner(listOf("ID", "Name", "Email", "Login"))
                    setFilterSpinnerVisibility(true)

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

                    searchEditText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            filterUsers(s.toString())
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    })
                }
                R.id.nav_shipments -> {
                    setFilterSpinnerVisibility(false)
                    updateSortFieldSpinner(listOf("ID", "Date", "User"))
                    showShipmentTable()

                    searchEditText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            filterShipments(s.toString())
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    })
                }
                R.id.nav_products -> {
                    updateSortFieldSpinner(listOf("ID", "Name", "Shipment", "Manufacturer"))
                    setFilterSpinnerVisibility(true)

                    val productTypes = listOf("All", "Type A", "Type B", "Type C")
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, productTypes)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    filterRoleSpinner.adapter = adapter

                    filterRoleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            val selectedType = productTypes[position]
                            val filters = if (selectedType == "All") emptyMap() else mapOf("product_type" to selectedType)
                            fetchProducts(filters = filters)
                        }
                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }

                    showProductTable()

                    searchEditText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            filterProducts(s.toString())
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    })
                }
                R.id.nav_write_off -> {
                    updateSortFieldSpinner(listOf("ID", "User", "Quantity", "Date"))
                    setFilterSpinnerVisibility(true)

                    val filters = listOf("All", "Defective", "Expired")
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filters)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    filterRoleSpinner.adapter = adapter

                    filterRoleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            val selectedFilter = filters[position]
                            val filterParams = when (selectedFilter) {
                                "Defective" -> mapOf("reason" to "Брак")
                                "Expired" -> mapOf("reason" to "Просрочено")
                                else -> emptyMap()
                            }
                            fetchWriteOffProducts(filterParams)
                        }
                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }

                    showWriteOffProducts()

                    searchEditText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            filterWriteOffOfProducts(s.toString())
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    })
                }
                R.id.nav_quantity -> {
                    setFilterSpinnerVisibility(false)
                    updateSortFieldSpinner(listOf("ID", "Product Name", "Available Quantity"))
                    showProductsCurrentQuantity()

                    searchEditText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            filterProductsCurrentQuantity(s.toString())
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    })
                }
                R.id.nav_extradition -> {
                    setFilterSpinnerVisibility(false)
                    updateSortFieldSpinner(listOf("ID", "User", "Date", "Quantity"))
                    showExtradition()

                    searchEditText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            filterExtradition(s.toString())
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    })
                }
                R.id.nav_logout -> {
                    RetrofitClient.clearToken(this)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    Toast.makeText(this, "Выход выполнен", Toast.LENGTH_SHORT).show()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Инициализация Spinners
        val sortOrders = listOf("Ascending", "Descending")
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

        // Открытие меню при клике на иконку
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Загрузка таблицы Users по умолчанию
        navView.menu.findItem(R.id.nav_users).isChecked = true
        navView.menu.performIdentifierAction(R.id.nav_users, 0)
    }

    private fun updateSortFieldSpinner(fields: List<String>) {
        val fieldAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fields)
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortFieldSpinner.adapter = fieldAdapter
    }

    private fun updateSorting() {
        val selectedField = sortFieldSpinner.selectedItem?.toString() ?: return
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
            }
            "Shipment Table" -> {
                when (selectedField) {
                    "ID" -> shipments.sortBy { it.shipment_id }
                    "Date" -> shipments.sortBy { it.date_of_shipment }
                    "User" -> shipments.sortBy { it.user }
                }
                if (selectedOrder == "Descending") shipments.reverse()
                val tableAdapter = TableAdapter(this, tableLayout, shipments)
                tableAdapter.populateTable("Shipment")
            }
            "Product Table" -> {
                when (selectedField) {
                    "ID" -> products.sortBy { it.product_id }
                    "Name" -> products.sortBy { it.product_name }
                    "Shipment" -> products.sortBy { it.shipment }
                    "Manufacturer" -> products.sortBy { it.manufacturer }
                }
                if (selectedOrder == "Descending") products.reverse()
                val tableAdapter = TableAdapter(this, tableLayout, products)
                tableAdapter.populateTable("Product")
            }
            "Write Off Products Table" -> {
                when (selectedField) {
                    "ID" -> writeOffProducts.sortBy { it.id_product_write_off }
                    "User" -> writeOffProducts.sortBy { it.user }
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
                    "User" -> extraditions.sortBy { it.user }
                    "Date" -> extraditions.sortBy { it.date_of_extradition }
                    "Quantity" -> extraditions.sortBy { it.quantity }
                }
                if (selectedOrder == "Descending") extraditions.reverse()
                val tableAdapter = TableAdapter(this, tableLayout, extraditions)
                tableAdapter.populateTable("Extradition")
            }
        }
    }

    private fun showShipmentTable() {
        tableTitle.text = "Shipment Table"
        fetchShipments()
    }

    private fun showProductTable() {
        tableTitle.text = "Product Table"
        fetchProducts()
    }

    private fun showExtradition() {
        tableTitle.text = "Extradition"
        fetchExtradition()
    }

    private fun showProductsCurrentQuantity() {
        tableTitle.text = "Products Current Quantity"
        fetchProductsCurrentQuantity()
    }

    private fun showWriteOffProducts() {
        tableTitle.text = "Write Off Products Table"
        fetchWriteOffProducts()
    }

    private fun fetchUsers(search: String? = null, ordering: String? = null, filters: Map<String, String> = emptyMap()) {
        val call = RetrofitClient.getApiService(this).getUsersFiltered(ordering, search, filters)
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    users.clear()
                    response.body()?.let { users.addAll(it) }
                    val tableAdapter = TableAdapter(this@TableActivity, tableLayout, users)
                    tableAdapter.populateTable("User")
                } else {
                    handleErrorResponse(response.code())
                    Log.e("TableActivity", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(this@TableActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("TableActivity", "Failure: ${t.message}")
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
                    filterShipments(searchEditText.text.toString())
                } else {
                    handleErrorResponse(response.code())
                    Log.e("TableActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Shipment>>, t: Throwable) {
                Toast.makeText(this@TableActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("TableActivity", "Failure: ${t.message}")
            }
        })
    }

    private fun fetchProducts(filters: Map<String, String> = emptyMap()) {
        val call = RetrofitClient.getApiService(this).getProductsFiltered(filters = filters)
        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    products.clear()
                    response.body()?.let { products.addAll(it) }
                    val tableAdapter = TableAdapter(this@TableActivity, tableLayout, products)
                    tableAdapter.populateTable("Product")
                } else {
                    handleErrorResponse(response.code())
                    Log.e("TableActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@TableActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("TableActivity", "Failure: ${t.message}")
            }
        })
    }

    private fun fetchExtradition() {
        val call = RetrofitClient.getApiService(this).getExtraditions()
        call.enqueue(object : Callback<List<Extradition>> {
            override fun onResponse(call: Call<List<Extradition>>, response: Response<List<Extradition>>) {
                if (response.isSuccessful) {
                    extraditions.clear()
                    response.body()?.let { extraditions.addAll(it) }
                    val tableAdapter = TableAdapter(this@TableActivity, tableLayout, extraditions)
                    tableAdapter.populateTable("Extradition")
                } else {
                    handleErrorResponse(response.code())
                    Log.e("TableActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Extradition>>, t: Throwable) {
                Toast.makeText(this@TableActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("TableActivity", "Failure: ${t.message}")
            }
        })
    }

    private fun fetchProductsCurrentQuantity() {
        val call = RetrofitClient.getApiService(this).getProductsCurrentQuantity()
        call.enqueue(object : Callback<List<ProductsCurrentQuantity>> {
            override fun onResponse(call: Call<List<ProductsCurrentQuantity>>, response: Response<List<ProductsCurrentQuantity>>) {
                if (response.isSuccessful) {
                    productsCurrentQuantities.clear()
                    response.body()?.let { productsCurrentQuantities.addAll(it) }
                    val tableAdapter = TableAdapter(this@TableActivity, tableLayout, productsCurrentQuantities)
                    tableAdapter.populateTable("ProductsCurrentQuantity")
                } else {
                    handleErrorResponse(response.code())
                    Log.e("TableActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ProductsCurrentQuantity>>, t: Throwable) {
                Toast.makeText(this@TableActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("TableActivity", "Failure: ${t.message}")
            }
        })
    }

    private fun fetchWriteOffProducts(filters: Map<String, String> = emptyMap()) {
        val call = RetrofitClient.getApiService(this).getWriteOffProductsFiltered(filters)
        call.enqueue(object : Callback<List<WriteOffOfProducts>> {
            override fun onResponse(call: Call<List<WriteOffOfProducts>>, response: Response<List<WriteOffOfProducts>>) {
                if (response.isSuccessful) {
                    writeOffProducts.clear()
                    response.body()?.let { writeOffProducts.addAll(it) }
                    val tableAdapter = TableAdapter(this@TableActivity, tableLayout, writeOffProducts)
                    tableAdapter.populateTable("WriteOffProducts")
                } else {
                    handleErrorResponse(response.code())
                    Log.e("TableActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<WriteOffOfProducts>>, t: Throwable) {
                Toast.makeText(this@TableActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("TableActivity", "Failure: ${t.message}")
            }
        })
    }

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
        val tableAdapter = TableAdapter(this, tableLayout, filteredUsers)
        tableAdapter.populateTable("User")
    }

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
        val tableAdapter = TableAdapter(this, tableLayout, filteredShipments)
        tableAdapter.populateTable("Shipment")
    }

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
        val tableAdapter = TableAdapter(this, tableLayout, filteredProducts)
        tableAdapter.populateTable("Product")
    }

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
        val tableAdapter = TableAdapter(this, tableLayout, filteredWriteOffs)
        tableAdapter.populateTable("WriteOffProducts")
    }

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
        val tableAdapter = TableAdapter(this, tableLayout, filteredExtraditions)
        tableAdapter.populateTable("Extradition")
    }

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
        val tableAdapter = TableAdapter(this, tableLayout, filteredProductsCurrentQuantities)
        tableAdapter.populateTable("ProductsCurrentQuantity")
    }

    private fun setFilterSpinnerVisibility(isVisible: Boolean) {
        filterRoleSpinner.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun handleErrorResponse(code: Int) {
        when (code) {
            401 -> {
                Toast.makeText(this, "Не авторизован. Пожалуйста, войдите снова.", Toast.LENGTH_LONG).show()
                RetrofitClient.clearToken(this)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            403 -> Toast.makeText(this, "Доступ запрещён.", Toast.LENGTH_LONG).show()
            else -> Toast.makeText(this, "Ошибка сервера: $code", Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}