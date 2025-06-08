package com.example.onetrysclada

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
// import android.telecom.Call
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.example.onetrysclada.data.models.Extradition
import com.example.onetrysclada.data.models.Product
import com.example.onetrysclada.data.models.ProductsCurrentQuantity
import com.example.onetrysclada.data.models.Shipment
import com.example.onetrysclada.data.models.User
import com.example.onetrysclada.data.models.WriteOffOfProducts
import com.example.onetrysclada.data.network.RetrofitClient
import com.google.android.material.internal.ViewUtils.hideKeyboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TableAdapter<T>(
    private val context: Context,
    private val tableLayout: TableLayout,
    private var data: List<T>
) {
    private var users: List<User> = emptyList()

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
    }

    fun populateTable(dataType: String, updatedData: List<T> = data) {
        val dataToUse = if (updatedData.isNotEmpty()) updatedData else data
        Log.d("TableAdapter", "Populating table with data: $dataToUse")
        tableLayout.removeAllViews()
        val headerRow = TableRow(context)
        when (dataType) {
            "User" -> {
                headerRow.addView(createTextView("ID"))
                headerRow.addView(createTextView("Name"))
                headerRow.addView(createTextView("Login"))
                headerRow.addView(createTextView("Email"))
                headerRow.addView(createTextView("Phone Number"))
                headerRow.addView(createTextView("Role"))
            }
            "Shipment" -> {
                headerRow.addView(createTextView("Shipment ID"))
                headerRow.addView(createTextView("Quantity"))
                headerRow.addView(createTextView("Date of Shipment"))
                headerRow.addView(createTextView("User Login"))
            }
            "Product" -> {
                headerRow.addView(createTextView("Product ID"))
                headerRow.addView(createTextView("Name"))
                headerRow.addView(createTextView("Expire Date"))
                headerRow.addView(createTextView("Type"))
                headerRow.addView(createTextView("Manufacturer"))
                headerRow.addView(createTextView("Weight"))
                headerRow.addView(createTextView("Shipment ID"))
                headerRow.addView(createTextView("Write off of products"))
                headerRow.addView(createTextView("Extradition"))
            }
            "Extradition" -> {
                headerRow.addView(createTextView("Extradition ID"))
                headerRow.addView(createTextView("Date of extradition"))
                headerRow.addView(createTextView("Quantity"))
                headerRow.addView(createTextView("User Login"))
            }
            "ProductsCurrentQuantity" -> {
                headerRow.addView(createTextView("Product current quantity ID"))
                headerRow.addView(createTextView("Quantity"))
                headerRow.addView(createTextView("Product"))
            }
            "WriteOffProducts" -> {
                headerRow.addView(createTextView("Product Write-off ID"))
                headerRow.addView(createTextView("Product_write_off_date"))
                headerRow.addView(createTextView("Quantity"))
                headerRow.addView(createTextView("Reason"))
                headerRow.addView(createTextView("User Login"))
            }
        }
        tableLayout.addView(headerRow)

        // Функция для получения логина по user_id
        fun getUserLogin(userId: Int): String {
            Log.d("TableAdapter", "Looking for user_id: $userId in users: $users")
            return users.find { it.user_id == userId }?.login ?: "Unknown"
        }

        // Добавляем строки с данными
        for (item in dataToUse) {
            val row = TableRow(context)
            when (item) {
                is User -> {
                    row.addView(createTextView(item.user_id.toString()))
                    row.addView(createTextView(item.user_name))
                    row.addView(createTextView(item.login))
                    row.addView(createTextView(item.email))
                    row.addView(createTextView(item.phone_number ?: ""))
                    row.addView(createTextView(item.role))

                    if (getCurrentUserRole() == "admin") {
                        row.addView(createActionCell(
                            onEditClick = { openEditUserDialog(item) },
                            onDeleteClick = { deleteUser(item.user_id) }
                        ))
                    } else {
                        row.addView(LinearLayout(context).apply {
                            layoutParams = TableRow.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply { marginStart = 16 }
                        })
                    }
                }
                is Shipment -> {
                    row.addView(createTextView(item.shipment_id.toString()))
                    row.addView(createTextView(item.quantity.toString()))
                    row.addView(createTextView(item.date_of_shipment))
                    row.addView(createTextView(getUserLogin(item.user))) // Используем логин вместо user_id

                    if (getCurrentUserRole() == "admin") {
                        row.addView(createActionCell(
                            onEditClick = { openEditShipmentDialog(item) },
                            onDeleteClick = { deleteShipment(item.shipment_id) }
                        ))
                    } else {
                        row.addView(LinearLayout(context).apply {
                            layoutParams = TableRow.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply { marginStart = 16 }
                        })
                    }
                }
                is Product -> {
                    row.addView(createTextView(item.product_id.toString()))
                    row.addView(createTextView(item.product_name))
                    row.addView(createTextView(item.expire_date))
                    row.addView(createTextView(item.product_type))
                    row.addView(createTextView(item.manufacturer))
                    row.addView(createTextView(item.weight.toString()))
                    row.addView(createTextView(item.shipment.toString()))
                    row.addView(createTextView(item.write_off_of_products?.toString() ?: "N/A"))
                    row.addView(createTextView(item.extradition?.toString() ?: "N/A"))

                    if (getCurrentUserRole() == "admin") {
                        row.addView(createActionCell(
                            onEditClick = { openEditProductDialog(item) },
                            onDeleteClick = { deleteProduct(item.product_id) }
                        ))
                    } else {
                        row.addView(LinearLayout(context).apply {
                            layoutParams = TableRow.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply { marginStart = 16 }
                        })
                    }
                }
                is WriteOffOfProducts -> {
                    row.addView(createTextView(item.id_product_write_off.toString()))
                    row.addView(createTextView(item.product_write_off_date))
                    row.addView(createTextView(item.quantity.toString()))
                    row.addView(createTextView(item.reason))
                    row.addView(createTextView(getUserLogin(item.user))) // Используем логин вместо user_id

                    if (getCurrentUserRole() == "admin") {
                        row.addView(createActionCell(
                            onEditClick = { openEditWriteOffProductsDialog(item) },
                            onDeleteClick = { deleteWriteOffOfProducts(item.id_product_write_off) }
                        ))
                    } else {
                        row.addView(LinearLayout(context).apply {
                            layoutParams = TableRow.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply { marginStart = 16 }
                        })
                    }
                }
                is Extradition -> {
                    row.addView(createTextView(item.extradition_id.toString()))
                    row.addView(createTextView(item.date_of_extradition))
                    row.addView(createTextView(item.quantity.toString()))
                    row.addView(createTextView(getUserLogin(item.user))) // Используем логин вместо user_id

                    if (getCurrentUserRole() == "admin") {
                        row.addView(createActionCell(
                            onEditClick = { openEditExtraditionDialog(item) },
                            onDeleteClick = { deleteExtradition(item.extradition_id) }
                        ))
                    } else {
                        row.addView(LinearLayout(context).apply {
                            layoutParams = TableRow.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply { marginStart = 16 }
                        })
                    }
                }
                is ProductsCurrentQuantity -> {
                    row.addView(createTextView(item.product_current_quantity_id.toString()))
                    row.addView(createTextView(item.quantity.toString()))
                    row.addView(createTextView(item.product.toString()))

                    if (getCurrentUserRole() == "admin") {
                        row.addView(createActionCell(
                            onEditClick = { openEditProductsCurrentQuantityDialog(item) },
                            onDeleteClick = { deleteProductsCurrentQuantity(item.product_current_quantity_id) }
                        ))
                    } else {
                        row.addView(LinearLayout(context).apply {
                            layoutParams = TableRow.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply { marginStart = 16 }
                        })
                    }
                }
            }
            tableLayout.addView(row)
        }

        if (getCurrentUserRole() == "admin") {
            when (dataType) {
                "User" -> addButtonToTable("Add User") { openAddUserDialog() }
                "Product" -> addButtonToTable("Add Product") { openAddProductDialog() }
                "Shipment" -> addButtonToTable("Add Shipment") { openAddShipmentDialog() }
                "WriteOffProducts" -> addButtonToTable("Add WriteOffProduct") { openAddWriteOffProductsDialog() }
                "ProductsCurrentQuantity" -> addButtonToTable("Add ProductsCurrentQuantity") { openAddProductsCurrentQuantityDialog() }
                "Extradition" -> addButtonToTable("Add Extradition") { openAddExtraditionDialog() }
            }
        }
    }


    // Функция для добавления кнопки с текстом и действием
    fun addButtonToTable(buttonText: String, onClickAction: () -> Unit) {
        val addButton = Button(context).apply {
            text = buttonText
            setOnClickListener { onClickAction() }
            layoutParams = TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            }
        }
        tableLayout.addView(addButton)
    }

    private fun createTextView(text: String, isHeader: Boolean = false): TextView {
        return TextView(context).apply {
            this.text = text
            setPadding(16, 16, 16, 16)
            setBackgroundColor(if (isHeader) Color.DKGRAY else Color.LTGRAY)
            setTextColor(Color.BLACK)
            layoutParams = TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                minimumWidth = 150
            }
            if (isHeader) {
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
        }
    }

    private fun createActionCell(onEditClick: () -> Unit, onDeleteClick: () -> Unit): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 16
            }

            // Кнопка "Редактировать" (карандаш)
            addView(ImageButton(context).apply {
                setImageResource(R.drawable.edit1) // Укажи свой drawable
                setBackgroundColor(Color.TRANSPARENT)
                contentDescription = "Edit"
                layoutParams = LinearLayout.LayoutParams(48, 48).apply {
                    marginEnd = 8
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
                setPadding(8, 8, 8, 8)
                setOnClickListener { onEditClick() }
            })

            // Кнопка "Удалить" (крестик)
            addView(ImageButton(context).apply {
                setImageResource(R.drawable.delete1) // Укажи свой drawable
                setBackgroundColor(Color.TRANSPARENT)
                contentDescription = "Delete"
                layoutParams = LinearLayout.LayoutParams(48, 48)
                scaleType = ImageView.ScaleType.FIT_CENTER
                setPadding(8, 8, 8, 8)
                setOnClickListener { onDeleteClick() }
            })
        }
    }

    private fun fetchProductsAndUpdateTable() {
        val apiService = RetrofitClient.getApiService(context)

        apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val products = response.body() ?: emptyList()
                    populateTableForProducts(products) // Обновляем таблицу с новыми данными
                } else {
                    Toast.makeText(context, "Failed to fetch products", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun populateTableForProducts(data: List<Product>) {
        populateTable("Product", data as List<T>)
    }

    private fun fetchProductsCurrentQuantityAndUpdateTable() {
        val apiService = RetrofitClient.getApiService(context)

        apiService.getProductsCurrentQuantity().enqueue(object : Callback<List<ProductsCurrentQuantity>> {
            override fun onResponse(call: Call<List<ProductsCurrentQuantity>>, response: Response<List<ProductsCurrentQuantity>>) {
                if (response.isSuccessful) {
                    val items = response.body() ?: emptyList()
                    populateTableForProductsCurrentQuantity(items) // Обновляем таблицу с новыми данными
                } else {
                    Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ProductsCurrentQuantity>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun populateTableForProductsCurrentQuantity(data: List<ProductsCurrentQuantity>) {
        populateTable("ProductsCurrentQuantity", data as List<T>)
    }

    private fun fetchWriteOffProductsAndUpdateTable() {
        val apiService = RetrofitClient.getApiService(context)
        apiService.getWriteOffProducts().enqueue(object : Callback<List<WriteOffOfProducts>> {
            override fun onResponse(
                call: Call<List<WriteOffOfProducts>>,
                response: Response<List<WriteOffOfProducts>>
            ) {
                if (response.isSuccessful) {
                    val writeOffProducts = response.body() ?: emptyList()
                    populateTableForWriteOffProducts(writeOffProducts) // Обновляем таблицу с новыми данными
                } else {
                    Toast.makeText(context, "Failed to fetch WriteOffProducts", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<WriteOffOfProducts>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun populateTableForWriteOffProducts(data: List<WriteOffOfProducts>) {
        populateTable("WriteOffProducts", data as List<T>)
    }

    // Функция для запроса данных Shipment и обновления таблицы
    private fun fetchShipmentsAndUpdateTable() {
        val apiService = RetrofitClient.getApiService(context)

        apiService.getShipments().enqueue(object : Callback<List<Shipment>> {
            override fun onResponse(call: Call<List<Shipment>>, response: Response<List<Shipment>>) {
                if (response.isSuccessful) {
                    val shipments = response.body() ?: emptyList()
                    populateTableForShipments(shipments) // Обновляем таблицу с новыми данными
                } else {
                    Toast.makeText(context, "Failed to fetch shipments", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Shipment>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Функция для обновления таблицы Shipment
    fun populateTableForShipments(data: List<Shipment>) {
        populateTable("Shipment", data as List<T>)
    }

    private fun fetchUsersAndUpdateTable() {
        val apiService = RetrofitClient.getApiService(context)

        apiService.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    populateTableForUsers(users) // Обновляем таблицу
                } else {
                    Toast.makeText(context, "Failed to fetch users", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun populateTableForUsers(data: List<User>) {
        populateTable("User", data as List<T>)
    }


    private fun fetchExtraditionsAndUpdateTable() {
        val apiService = RetrofitClient.getApiService(context)

        apiService.getExtraditions().enqueue(object : Callback<List<Extradition>> {
            override fun onResponse(call: Call<List<Extradition>>, response: Response<List<Extradition>>) {
                if (response.isSuccessful) {
                    val extraditions = response.body() ?: emptyList()
                    populateTableForExtraditions(extraditions) // Обновляем таблицу с новыми данными
                } else {
                    Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Extradition>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun populateTableForExtraditions(data: List<Extradition>) {
        populateTable("Extradition", data as List<T>)
    }



    private fun openEditUserDialog(user: User) {
        val dialogBuilder = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_user, null)
        dialogBuilder.setView(dialogView)

        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_user_name)
        val loginEditText = dialogView.findViewById<EditText>(R.id.edit_user_login)
        val emailEditText = dialogView.findViewById<EditText>(R.id.edit_user_email)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.edit_user_password)
        val phoneNumberEditText = dialogView.findViewById<EditText>(R.id.edit_user_phone_number)
        val roleEditText = dialogView.findViewById<EditText>(R.id.edit_user_role)
        val warningTextView = dialogView.findViewById<TextView>(R.id.warning_text)

        warningTextView.visibility = View.GONE

        nameEditText.setText(user.user_name)
        loginEditText.setText(user.login)
        emailEditText.setText(user.email)
        phoneNumberEditText.setText(user.phone_number)
        roleEditText.setText(user.role)

        dialogBuilder.setPositiveButton("Сохранить", null)
        dialogBuilder.setNegativeButton("Отмена", null)

        val dialog = dialogBuilder.create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.isEnabled = false

            val mandatoryFields = listOf(nameEditText, loginEditText, emailEditText, roleEditText)

            val watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val allMandatoryFieldsFilled = mandatoryFields.all { it.text.isNotBlank() }
                    saveButton.isEnabled = allMandatoryFieldsFilled

                    if (!allMandatoryFieldsFilled) {
                        warningTextView.visibility = View.VISIBLE
                        warningTextView.text = "Все обязательные поля должны быть заполнены."
                    } else {
                        warningTextView.visibility = View.GONE
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            }

            mandatoryFields.forEach { it.addTextChangedListener(watcher) }

            saveButton.setOnClickListener {
                val newName = nameEditText.text.toString()
                val newLogin = loginEditText.text.toString()
                val newEmail = emailEditText.text.toString()
                val newPassword = passwordEditText.text.toString().takeIf { it.isNotBlank() }
                val newPhoneNumber = phoneNumberEditText.text.toString().takeIf { it.isNotBlank() }
                val newRole = roleEditText.text.toString()

                Log.d("TableAdapter", "Updating user: id=$user.user_id, name=$newName, login=$newLogin, email=$newEmail, password=$newPassword, phone=$newPhoneNumber, role=$newRole")
                updateUser(user.user_id, newName, newEmail, newLogin, newPassword, newPhoneNumber, newRole)
                hideKeyboard(dialogView)
                dialog.dismiss()
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                hideKeyboard(dialogView)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun hideKeyboard(view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun updateUser(user_id: Int, user_name: String, email: String, login: String, password: String?, phone_number: String?, role: String) {
        val apiService = RetrofitClient.getApiService(context)
        val updatedUser = User(
            user_id = user_id,
            user_name = user_name,
            email = email,
            login = login,
            password = password,
            phone_number = phone_number,
            role = role
        )
        Log.d("TableAdapter", "Sending PUT request: $updatedUser")
        val call = apiService.updateUser(user_id, updatedUser)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "User updated successfully", Toast.LENGTH_SHORT).show()
                    fetchUsersAndUpdateTable()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("TableAdapter", "Failed to update user: ${response.code()} - $errorBody")
                    Toast.makeText(context, "Failed to update user: $errorBody", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("TableAdapter", "Error: ${t.message}")
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun fetchUsersAndPopulateSpinner(spinner: Spinner, onUserSelected: (Int) -> Unit) {
        val apiService = RetrofitClient.getApiService(context)
        apiService.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    // Создаем список строк вида "Name (Login)"
                    val userDisplayList = users.map { "${it.user_name} (${it.login})" }
                    // Создаем адаптер для Spinner
                    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, userDisplayList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                    // Устанавливаем обработчик выбора
                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            val selectedUserId = users[position].user_id
                            onUserSelected(selectedUserId)
                        }
                        override fun onNothingSelected(parent: AdapterView<*>) {
                            onUserSelected(0) // По умолчанию
                        }
                    }
                } else {
                    Toast.makeText(context, "Не удалось загрузить пользователей", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(context, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun openEditShipmentDialog(shipment: Shipment) {
        val dialog = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_shipment, null)
        dialog.setView(dialogView)

        val quantityEditText = dialogView.findViewById<EditText>(R.id.edit_shipment_quantity)
        val dateEditText = dialogView.findViewById<EditText>(R.id.edit_shipment_date)
        val userSpinner = dialogView.findViewById<Spinner>(R.id.edit_shipment_user)

        quantityEditText.setText(shipment.quantity.toString())
        dateEditText.setText(shipment.date_of_shipment)

        var selectedUserId = shipment.user // Сохраняем выбранный user_id

        // Загружаем пользователей в Spinner
        fetchUsersAndPopulateSpinner(userSpinner) { userId ->
            selectedUserId = userId
        }

        dialog.setPositiveButton("Сохранить") { _, _ ->
            val newQuantity = quantityEditText.text.toString().toIntOrNull() ?: shipment.quantity
            val newDate = dateEditText.text.toString()
            updateShipment(shipment.shipment_id, newQuantity, newDate, selectedUserId)
        }
        dialog.setNegativeButton("Отмена", null)
        dialog.create().show()
    }

    private fun updateShipment(shipmentId: Int, quantity: Int, date: String, userId: Int) {
        val apiService = RetrofitClient.getApiService(context)
        val updatedShipment = Shipment(shipmentId, quantity, date, userId)

        apiService.updateShipment(shipmentId, updatedShipment).enqueue(object : Callback<Shipment> {
            override fun onResponse(call: Call<Shipment>, response: Response<Shipment>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Shipment updated successfully", Toast.LENGTH_SHORT).show()
                    // Обновляем таблицу с новыми данными
                    fetchShipmentsAndUpdateTable()
                } else {
                    Toast.makeText(context, "Failed to update shipment", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Shipment>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun openEditProductDialog(product: Product) {
        val dialog = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_products, null)
        dialog.setView(dialogView)

        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_product_name)
        val expireDateEditText = dialogView.findViewById<EditText>(R.id.edit_product_expire_date)
        val typeEditText = dialogView.findViewById<EditText>(R.id.edit_product_type)
        val manufacturerEditText = dialogView.findViewById<EditText>(R.id.edit_product_manufacturer)
        val weightEditText = dialogView.findViewById<EditText>(R.id.edit_product_weight)
        val shipmentEditText = dialogView.findViewById<EditText>(R.id.edit_product_shipment)
        val writeOffEditText = dialogView.findViewById<EditText>(R.id.edit_product_write_off)
        val extraditionEditText = dialogView.findViewById<EditText>(R.id.edit_product_extradition)

        nameEditText.setText(product.product_name)
        expireDateEditText.setText(product.expire_date)
        typeEditText.setText(product.product_type)
        manufacturerEditText.setText(product.manufacturer)
        weightEditText.setText(product.weight.toString())
        shipmentEditText.setText(product.shipment.toString())
        writeOffEditText.setText(product.write_off_of_products?.toString() ?: "")
        extraditionEditText.setText(product.extradition?.toString() ?: "")

        dialog.setPositiveButton("Save") { _, _ ->
            val newName = nameEditText.text.toString()
            val newExpireDate = expireDateEditText.text.toString()
            val newType = typeEditText.text.toString()
            val newManufacturer = manufacturerEditText.text.toString()
            val newWeight = weightEditText.text.toString().toDoubleOrNull() ?: product.weight
            val newShipment = shipmentEditText.text.toString().toIntOrNull() ?: product.shipment
            val newWriteOff = writeOffEditText.text.toString().toIntOrNull()
            val newExtradition = extraditionEditText.text.toString().toIntOrNull()

            updateProduct(product.product_id, newName, newExpireDate, newType, newManufacturer, newWeight, newShipment, newWriteOff, newExtradition)
        }
        dialog.setNegativeButton("Cancel", null)
        dialog.create().show()
    }

    private fun updateProduct(productId: Int, name: String, expireDate: String, type: String, manufacturer: String, weight: Double, shipment: Int, writeOff: Int?, extradition: Int?) {
        val apiService = RetrofitClient.getApiService(context)
        val updatedProduct = Product(productId, name, expireDate, type, manufacturer, weight, shipment, writeOff, extradition)

        apiService.updateProduct(productId, updatedProduct).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Product updated successfully", Toast.LENGTH_SHORT).show()
                    fetchProductsAndUpdateTable()
                } else {
                    Toast.makeText(context, "Failed to update product", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openEditWriteOffProductsDialog(writeOffProduct: WriteOffOfProducts) {
        val dialog = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_write_off_products, null)
        dialog.setView(dialogView)

        val dateEditText = dialogView.findViewById<EditText>(R.id.edit_write_off_date)
        val quantityEditText = dialogView.findViewById<EditText>(R.id.edit_write_off_quantity)
        val reasonEditText = dialogView.findViewById<EditText>(R.id.edit_write_off_reason)
        val userSpinner = dialogView.findViewById<Spinner>(R.id.edit_write_off_user)

        dateEditText.setText(writeOffProduct.product_write_off_date)
        quantityEditText.setText(writeOffProduct.quantity.toString())
        reasonEditText.setText(writeOffProduct.reason)

        var selectedUserId = writeOffProduct.user

        fetchUsersAndPopulateSpinner(userSpinner) { userId ->
            selectedUserId = userId
        }

        dialog.setPositiveButton("Сохранить") { _, _ ->
            val updatedWriteOffProduct = WriteOffOfProducts(
                id_product_write_off = writeOffProduct.id_product_write_off,
                product_write_off_date = dateEditText.text.toString(),
                quantity = quantityEditText.text.toString().toIntOrNull() ?: 0,
                reason = reasonEditText.text.toString(),
                user = selectedUserId
            )
            updateWriteOffProducts(updatedWriteOffProduct)
        }

        dialog.setNegativeButton("Отмена", null)
        dialog.create().show()
    }

    private fun updateWriteOffProducts(writeOffProduct: WriteOffOfProducts) {
        val apiService = RetrofitClient.getApiService(context)
        apiService.updateWriteOffProduct(writeOffProduct.id_product_write_off, writeOffProduct)
            .enqueue(object : Callback<WriteOffOfProducts> {
                override fun onResponse(
                    call: Call<WriteOffOfProducts>,
                    response: Response<WriteOffOfProducts>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "WriteOffProduct updated successfully", Toast.LENGTH_SHORT).show()
                        fetchWriteOffProductsAndUpdateTable()
                    } else {
                        Toast.makeText(context, "Failed to update WriteOffProduct", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<WriteOffOfProducts>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun openEditExtraditionDialog(extradition: Extradition) {
        val dialog = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_extradition, null)
        dialog.setView(dialogView)

        val dateEditText = dialogView.findViewById<EditText>(R.id.edit_extradition_date)
        val quantityEditText = dialogView.findViewById<EditText>(R.id.edit_extradition_quantity)
        val userSpinner = dialogView.findViewById<Spinner>(R.id.edit_extradition_user)

        dateEditText.setText(extradition.date_of_extradition)
        quantityEditText.setText(extradition.quantity.toString())

        var selectedUserId = extradition.user

        fetchUsersAndPopulateSpinner(userSpinner) { userId ->
            selectedUserId = userId
        }

        dialog.setPositiveButton("Сохранить") { _, _ ->
            val updatedExtradition = Extradition(
                extradition_id = extradition.extradition_id,
                date_of_extradition = dateEditText.text.toString(),
                quantity = quantityEditText.text.toString().toIntOrNull() ?: extradition.quantity,
                user = selectedUserId
            )
            updateExtradition(updatedExtradition)
        }

        dialog.setNegativeButton("Отмена", null)
        dialog.create().show()
    }


    private fun updateExtradition(extradition: Extradition) {
        val apiService = RetrofitClient.getApiService(context)
        apiService.updateExtradition(extradition.extradition_id, extradition)
            .enqueue(object : Callback<Extradition> {
                override fun onResponse(call: Call<Extradition>, response: Response<Extradition>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Extradition updated successfully", Toast.LENGTH_SHORT).show()
                        fetchExtraditionsAndUpdateTable()
                    } else {
                        Toast.makeText(context, "Failed to update Extradition", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Extradition>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun openEditProductsCurrentQuantityDialog(item: ProductsCurrentQuantity) {
        val dialog = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_products_current_quantity, null)
        dialog.setView(dialogView)

        val quantityEditText = dialogView.findViewById<EditText>(R.id.edit_products_current_quantity)
        val productEditText = dialogView.findViewById<EditText>(R.id.edit_products_current_quantity_product)

        // Заполнение текущих данных
        quantityEditText.setText(item.quantity.toString())
        productEditText.setText(item.product.toString())

        dialog.setPositiveButton("Save") { _, _ ->
            val newQuantity = quantityEditText.text.toString().toIntOrNull() ?: item.quantity
            val newProduct = productEditText.text.toString().toIntOrNull() ?: item.product

            // Вызов API для обновления
            updateProductsCurrentQuantity(item.product_current_quantity_id, newQuantity, newProduct)
        }

        dialog.setNegativeButton("Cancel", null)
        dialog.create().show()
    }

    private fun updateProductsCurrentQuantity(id: Int, quantity: Int, product: Int) {
        val apiService = RetrofitClient.getApiService(context)
        val updatedItem = ProductsCurrentQuantity(product_current_quantity_id = id, quantity = quantity, product = product)

        apiService.updateProductsCurrentQuantity(id, updatedItem).enqueue(object : Callback<ProductsCurrentQuantity> {
            override fun onResponse(call: Call<ProductsCurrentQuantity>, response: Response<ProductsCurrentQuantity>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
                    fetchProductsCurrentQuantityAndUpdateTable()
                } else {
                    Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductsCurrentQuantity>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }




    // УДАЛЕНИЕ пошло
    private fun deleteProduct(productId: Int) {
        val apiService = RetrofitClient.getApiService(context)

        apiService.deleteProduct(productId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show()
                    //populateTable("Product")  // Обновляем таблицу после удаления
                    // Выполнить новый запрос данных
                    fetchProductsAndUpdateTable()
                } else {
                    Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun deleteShipment(shipmentId: Int) {
        val apiService = RetrofitClient.getApiService(context)

        apiService.deleteShipment(shipmentId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Shipment deleted successfully", Toast.LENGTH_SHORT).show()

                    // Выполнить новый запрос данных для обновления таблицы
                    fetchShipmentsAndUpdateTable()

                } else {
                    Toast.makeText(context, "Failed to delete shipment", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun deleteUser(userId: Int) {
        val apiService = RetrofitClient.getApiService(context)

        apiService.deleteUser(userId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show()
                    fetchUsersAndUpdateTable() // Обновляем таблицу после успешного удаления
                } else {
                    Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun deleteWriteOffOfProducts(writeOffId: Int) {
        val apiService = RetrofitClient.getApiService(context)

        apiService.deleteWriteOffOfProducts(writeOffId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Write-Off deleted successfully", Toast.LENGTH_SHORT).show()
                    fetchWriteOffProductsAndUpdateTable()  // Обновляем таблицу после удаления
                } else {
                    Toast.makeText(context, "Failed to delete Write-Off", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteExtradition(extraditionId: Int) {
        val apiService = RetrofitClient.getApiService(context)

        apiService.deleteExtradition(extraditionId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Extradition deleted successfully", Toast.LENGTH_SHORT).show()
                    fetchExtraditionsAndUpdateTable()  // Обновляем таблицу после удаления
                } else {
                    Toast.makeText(context, "Failed to delete Extradition", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteProductsCurrentQuantity(productsCurrentQuantityId: Int) {
        val apiService = RetrofitClient.getApiService(context)

        apiService.deleteProductsCurrentQuantity(productsCurrentQuantityId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Current Quantity deleted successfully", Toast.LENGTH_SHORT).show()
                    fetchProductsCurrentQuantityAndUpdateTable()  // Обновляем таблицу после удаления
                } else {
                    Toast.makeText(context, "Failed to delete Current Quantity", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }





    //ДОБАВЛЕНИЕ ПОШЛО
    private fun openAddUserDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_user, null)
        dialogBuilder.setView(dialogView)

        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_user_name)
        val loginEditText = dialogView.findViewById<EditText>(R.id.edit_user_login)
        val emailEditText = dialogView.findViewById<EditText>(R.id.edit_user_email)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.edit_user_password)
        val phoneNumberEditText = dialogView.findViewById<EditText>(R.id.edit_user_phone_number)
        val roleEditText = dialogView.findViewById<EditText>(R.id.edit_user_role)
        val warningTextView = dialogView.findViewById<TextView>(R.id.warning_text)

        warningTextView.visibility = View.GONE

        dialogBuilder.setPositiveButton("Save", null)
        dialogBuilder.setNegativeButton("Cancel", null)

        val dialog = dialogBuilder.create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.isEnabled = false

            val textFields = listOf(nameEditText, loginEditText, emailEditText, passwordEditText, roleEditText)

            val watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val allFieldsFilled = textFields.all { it.text.isNotBlank() }
                    saveButton.isEnabled = allFieldsFilled

                    if (!allFieldsFilled) {
                        warningTextView.visibility = View.VISIBLE
                        warningTextView.text = "Все обязательные поля должны быть заполнены."
                    } else {
                        warningTextView.visibility = View.GONE
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            }

            textFields.forEach { it.addTextChangedListener(watcher) }

            saveButton.setOnClickListener {
                val name = nameEditText.text.toString()
                val login = loginEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                val phoneNumber = phoneNumberEditText.text.toString().takeIf { it.isNotBlank() }
                val role = roleEditText.text.toString()

                // Валидация email
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    warningTextView.visibility = View.VISIBLE
                    warningTextView.text = "Неверный формат email."
                    return@setOnClickListener
                }

                // Валидация на HTML-теги
                if (name.contains("<") || login.contains("<") || role.contains("<")) {
                    warningTextView.visibility = View.VISIBLE
                    warningTextView.text = "Поля не должны содержать HTML-теги."
                    return@setOnClickListener
                }

                val newUser = User(
                    user_id = 0,
                    user_name = name,
                    login = login,
                    email = email,
                    password = password,
                    phone_number = phoneNumber,
                    role = role
                )
                Log.d("TableAdapter", "Adding user: $newUser")
                addUser(newUser)
                hideKeyboard(dialogView)
                dialog.dismiss()
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                hideKeyboard(dialogView)
                dialog.dismiss()
            }
        }

        dialog.show()
    }




    private fun addUser(user: User) {
        Log.d("TableAdapter", "Sending POST request: $user")
        val apiService = RetrofitClient.getApiService(context)
        apiService.addUser(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "User added successfully", Toast.LENGTH_SHORT).show()
                    fetchUsersAndUpdateTable()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("TableAdapter", "Failed to add user: ${response.code()} - $errorBody")
                    Toast.makeText(context, "Failed to add user: $errorBody", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("TableAdapter", "Error: ${t.message}")
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openAddProductDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_product, null)
        dialogBuilder.setView(dialogView)

        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_product_name)
        val expireDateEditText = dialogView.findViewById<EditText>(R.id.edit_product_expire_date)
        val typeEditText = dialogView.findViewById<EditText>(R.id.edit_product_type)
        val manufacturerEditText = dialogView.findViewById<EditText>(R.id.edit_product_manufacturer)
        val weightEditText = dialogView.findViewById<EditText>(R.id.edit_product_weight)
        val shipmentEditText = dialogView.findViewById<EditText>(R.id.edit_product_shipment)
        val writeOffEditText = dialogView.findViewById<EditText>(R.id.edit_product_write_off)
        val extraditionEditText = dialogView.findViewById<EditText>(R.id.edit_product_extradition)
        val warningTextView = dialogView.findViewById<TextView>(R.id.warning_text)

        warningTextView.visibility = View.GONE // Изначально скрываем предупреждение.

        dialogBuilder.setPositiveButton("Save", null) // Кнопка без Listener, Listener будет добавлен позже.
        dialogBuilder.setNegativeButton("Cancel", null)

        val dialog = dialogBuilder.create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.isEnabled = false // Изначально отключена.

            val textFields = listOf(nameEditText, expireDateEditText, typeEditText, manufacturerEditText, weightEditText, shipmentEditText, writeOffEditText,extraditionEditText)

            // Добавляем TextWatcher для проверки заполненности всех полей.
            val watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val allFieldsFilled = textFields.all { it.text.isNotBlank() }
                    saveButton.isEnabled = allFieldsFilled

                    if (!allFieldsFilled) {
                        warningTextView.visibility = View.VISIBLE
                        warningTextView.text = "Все обязательные поля должны быть заполнены."
                    } else {
                        warningTextView.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            }

            // Добавляем TextWatcher ко всем обязательным полям.
            textFields.forEach { it.addTextChangedListener(watcher) }

            saveButton.setOnClickListener {
                val newProduct = Product(
                    product_id = 0,
                    product_name = nameEditText.text.toString(),
                    expire_date = expireDateEditText.text.toString(),
                    product_type = typeEditText.text.toString(),
                    manufacturer = manufacturerEditText.text.toString(),
                    weight = weightEditText.text.toString().toDoubleOrNull() ?: 0.0,
                    shipment = shipmentEditText.text.toString().toIntOrNull() ?: 0,
                    write_off_of_products = writeOffEditText.text.toString().toIntOrNull(),
                    extradition = extraditionEditText.text.toString().toIntOrNull()
                )
                addProduct(newProduct)
                dialog.dismiss()
            }
        }

        dialog.show()
    }



    private fun addProduct(product: Product) {
        val apiService = RetrofitClient.getApiService(context)
        apiService.addProduct(product).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Product added successfully", Toast.LENGTH_SHORT).show()
                    fetchProductsAndUpdateTable()  // Обновляем таблицу после добавления
                } else {
                    Toast.makeText(context, "Failed to add product", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    // Метод для открытия диалога добавления новой записи Shipment
    private fun openAddShipmentDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_shipment, null)
        dialogBuilder.setView(dialogView)

        val quantityEditText = dialogView.findViewById<EditText>(R.id.edit_shipment_quantity)
        val dateEditText = dialogView.findViewById<EditText>(R.id.edit_shipment_date)
        val userSpinner = dialogView.findViewById<Spinner>(R.id.edit_shipment_user_id)
        val warningTextView = dialogView.findViewById<TextView>(R.id.warning_text)

        warningTextView.visibility = View.GONE

        var selectedUserId = 0 // Сохраняем выбранный user_id

        // Загружаем пользователей в Spinner
        fetchUsersAndPopulateSpinner(userSpinner) { userId ->
            selectedUserId = userId
        }

        dialogBuilder.setPositiveButton("Сохранить", null)
        dialogBuilder.setNegativeButton("Отмена", null)

        val dialog = dialogBuilder.create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.isEnabled = false

            val textFields = listOf(quantityEditText, dateEditText)

            val watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val allFieldsFilled = textFields.all { it.text.isNotBlank() } && selectedUserId != 0
                    saveButton.isEnabled = allFieldsFilled

                    if (!allFieldsFilled) {
                        warningTextView.visibility = View.VISIBLE
                        warningTextView.text = "Все поля должны быть заполнены."
                    } else {
                        warningTextView.visibility = View.GONE
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            }

            textFields.forEach { it.addTextChangedListener(watcher) }

            saveButton.setOnClickListener {
                val newShipment = Shipment(
                    shipment_id = 0,
                    quantity = quantityEditText.text.toString().toIntOrNull() ?: 0,
                    date_of_shipment = dateEditText.text.toString(),
                    user = selectedUserId
                )
                addShipment(newShipment)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    // Метод для добавления новой записи Shipment через API
    private fun addShipment(shipment: Shipment) {
        val apiService = RetrofitClient.getApiService(context)
        apiService.addShipment(shipment).enqueue(object : Callback<Shipment> {
            override fun onResponse(call: Call<Shipment>, response: Response<Shipment>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Shipment added successfully", Toast.LENGTH_SHORT).show()
                    // Обновляем таблицу с новыми данными
                    fetchShipmentsAndUpdateTable()
                } else {
                    Toast.makeText(context, "Failed to add shipment", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Shipment>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun openAddWriteOffProductsDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_write_off_products, null)
        dialogBuilder.setView(dialogView)

        val dateEditText = dialogView.findViewById<EditText>(R.id.edit_write_off_date)
        val quantityEditText = dialogView.findViewById<EditText>(R.id.edit_write_off_quantity)
        val reasonEditText = dialogView.findViewById<EditText>(R.id.edit_write_off_reason)
        val userSpinner = dialogView.findViewById<Spinner>(R.id.edit_write_off_user)
        val warningTextView = dialogView.findViewById<TextView>(R.id.warning_text)

        warningTextView.visibility = View.GONE

        var selectedUserId = 0

        fetchUsersAndPopulateSpinner(userSpinner) { userId ->
            selectedUserId = userId
        }

        dialogBuilder.setPositiveButton("Сохранить", null)
        dialogBuilder.setNegativeButton("Отмена", null)

        val dialog = dialogBuilder.create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.isEnabled = false

            val textFields = listOf(dateEditText, quantityEditText, reasonEditText)

            val watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val allFieldsFilled = textFields.all { it.text.isNotBlank() } && selectedUserId != 0
                    saveButton.isEnabled = allFieldsFilled

                    if (!allFieldsFilled) {
                        warningTextView.visibility = View.VISIBLE
                        warningTextView.text = "Все поля должны быть заполнены."
                    } else {
                        warningTextView.visibility = View.GONE
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            }

            textFields.forEach { it.addTextChangedListener(watcher) }

            saveButton.setOnClickListener {
                val newWriteOffProduct = WriteOffOfProducts(
                    id_product_write_off = 0,
                    product_write_off_date = dateEditText.text.toString(),
                    quantity = quantityEditText.text.toString().toIntOrNull() ?: 0,
                    reason = reasonEditText.text.toString(),
                    user = selectedUserId
                )
                addWriteOffProduct(newWriteOffProduct)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun addWriteOffProduct(writeOffProduct: WriteOffOfProducts) {
        val apiService = RetrofitClient.getApiService(context)
        apiService.addWriteOffProduct(writeOffProduct).enqueue(object : Callback<WriteOffOfProducts> {
            override fun onResponse(call: Call<WriteOffOfProducts>, response: Response<WriteOffOfProducts>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "WriteOffProduct added successfully", Toast.LENGTH_SHORT).show()
                    fetchWriteOffProductsAndUpdateTable()
                } else {
                    Toast.makeText(context, "Failed to add WriteOffProduct", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WriteOffOfProducts>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openAddProductsCurrentQuantityDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_products_current_quantity, null)
        dialogBuilder.setView(dialogView)

        val quantityEditText = dialogView.findViewById<EditText>(R.id.edit_products_current_quantity_quantity)
        val productEditText = dialogView.findViewById<EditText>(R.id.edit_products_current_quantity_product)
        val warningTextView = dialogView.findViewById<TextView>(R.id.warning_text)

        warningTextView.visibility = View.GONE

        dialogBuilder.setPositiveButton("Save", null)
        dialogBuilder.setNegativeButton("Cancel", null)

        val dialog = dialogBuilder.create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.isEnabled = false

            val textFields = listOf(quantityEditText, productEditText)

            val watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val allFieldsFilled = textFields.all { it.text.isNotBlank() }
                    saveButton.isEnabled = allFieldsFilled

                    if (!allFieldsFilled) {
                        warningTextView.visibility = View.VISIBLE
                        warningTextView.text = "Все поля должны быть заполнены."
                    } else {
                        warningTextView.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            }

            textFields.forEach { it.addTextChangedListener(watcher) }

            saveButton.setOnClickListener {
                val newProductsCurrentQuantity = ProductsCurrentQuantity(
                    product_current_quantity_id = 0,
                    quantity = quantityEditText.text.toString().toIntOrNull() ?: 0,
                    product = productEditText.text.toString().toIntOrNull() ?: 0
                )
                addProductsCurrentQuantity(newProductsCurrentQuantity)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun addProductsCurrentQuantity(productsCurrentQuantity: ProductsCurrentQuantity) {
        val apiService = RetrofitClient.getApiService(context)
        apiService.addProductsCurrentQuantity(productsCurrentQuantity).enqueue(object : Callback<ProductsCurrentQuantity> {
            override fun onResponse(call: Call<ProductsCurrentQuantity>, response: Response<ProductsCurrentQuantity>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "ProductsCurrentQuantity added successfully", Toast.LENGTH_SHORT).show()
                    fetchProductsCurrentQuantityAndUpdateTable()
                } else {
                    Toast.makeText(context, "Failed to add ProductsCurrentQuantity", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductsCurrentQuantity>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openAddExtraditionDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_extradition, null)
        dialogBuilder.setView(dialogView)

        val dateEditText = dialogView.findViewById<EditText>(R.id.edit_extradition_date)
        val quantityEditText = dialogView.findViewById<EditText>(R.id.edit_extradition_quantity)
        val userSpinner = dialogView.findViewById<Spinner>(R.id.edit_extradition_user)
        val warningTextView = dialogView.findViewById<TextView>(R.id.warning_text)

        warningTextView.visibility = View.GONE

        var selectedUserId = 0

        fetchUsersAndPopulateSpinner(userSpinner) { userId ->
            selectedUserId = userId
        }

        dialogBuilder.setPositiveButton("Сохранить", null)
        dialogBuilder.setNegativeButton("Отмена", null)

        val dialog = dialogBuilder.create()

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.isEnabled = false

            val textFields = listOf(dateEditText, quantityEditText)

            val watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val allFieldsFilled = textFields.all { it.text.isNotBlank() } && selectedUserId != 0
                    saveButton.isEnabled = allFieldsFilled

                    if (!allFieldsFilled) {
                        warningTextView.visibility = View.VISIBLE
                        warningTextView.text = "Все поля должны быть заполнены."
                    } else {
                        warningTextView.visibility = View.GONE
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            }

            textFields.forEach { it.addTextChangedListener(watcher) }

            saveButton.setOnClickListener {
                val newExtradition = Extradition(
                    extradition_id = 0,
                    date_of_extradition = dateEditText.text.toString(),
                    quantity = quantityEditText.text.toString().toIntOrNull() ?: 0,
                    user = selectedUserId
                )
                addExtradition(newExtradition)
                dialog.dismiss()
            }
        }

        dialog.show()
    }


    private fun addExtradition(extradition: Extradition) {
        val apiService = RetrofitClient.getApiService(context)
        apiService.addExtradition(extradition).enqueue(object : Callback<Extradition> {
            override fun onResponse(call: Call<Extradition>, response: Response<Extradition>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Extradition added successfully", Toast.LENGTH_SHORT).show()
                    fetchExtraditionsAndUpdateTable()
                } else {
                    Toast.makeText(context, "Failed to add Extradition", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Extradition>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCurrentUserRole(): String? {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_role", null)
    }
}




