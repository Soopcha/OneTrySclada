package com.example.onetrysclada

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
// import android.telecom.Call
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowInsetsAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TableAdapter<T>(
    private val context: Context,
    private val tableLayout: TableLayout,
    private val data: List<T>
) {

    fun populateTable(dataType: String) {
        Log.d("TableAdapter", "Populating table with data: $data")
        // Очистка TableLayout перед заполнением
        tableLayout.removeAllViews()

        // Добавление заголовков для разных таблиц
        val headerRow = TableRow(context)
        when (dataType) {
            "User" -> {
                headerRow.addView(createTextView("ID"))
                headerRow.addView(createTextView("Name"))
                headerRow.addView(createTextView("Login"))
                headerRow.addView(createTextView("Password"))
                headerRow.addView(createTextView("Email"))
                headerRow.addView(createTextView("Phone Number"))
                headerRow.addView(createTextView("Role"))
            }
            "Shipment" -> {
                headerRow.addView(createTextView("Shipment ID"))
                headerRow.addView(createTextView("Quantity"))
                headerRow.addView(createTextView("Date of Shipment"))
                headerRow.addView(createTextView("User ID"))
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
                headerRow.addView(createTextView("User"))
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
                headerRow.addView(createTextView("User"))
            }
        }
        tableLayout.addView(headerRow)


        // Добавляем строки с данными для каждого элемента списка
        for (item in data) {
            val row = TableRow(context)
            when (item) {
                is User -> {
                    row.addView(createTextView(item.user_id.toString()))
                    row.addView(createTextView(item.user_name))
                    row.addView(createTextView(item.login))
                    row.addView(createTextView(item.password))
                    row.addView(createTextView(item.email))
                    row.addView(createTextView(item.phone_number ?: ""))
                    row.addView(createTextView(item.role))
                    val editButton = Button(context).apply {
                        text = "Edit"
                        setOnClickListener {
                            openEditDialog(item) // Функция открытия диалога
                        }
                        layoutParams = TableRow.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply {
                            // Устанавливаем выравнивание вправо
                            marginStart = 16 // Дополнительное пространство слева от кнопки
                        }
                    }
                    row.addView(editButton)
                }
                is Shipment -> {
                    row.addView(createTextView(item.shipment_id.toString()))
                    row.addView(createTextView(item.quantity.toString()))
                    row.addView(createTextView(item.date_of_shipment))
                    row.addView(createTextView(item.user.toString()))
                //раньше было item.user.user_id и обращение уже к юзеру но что-то не работало так
                }
                is Product -> {
                    row.addView(createTextView(item.product_id.toString()))
                    row.addView(createTextView(item.product_name))
                    row.addView(createTextView(item.expire_date))
                    row.addView(createTextView(item.product_type))
                    row.addView(createTextView(item.manufacturer))
                    row.addView(createTextView(item.weight.toString()))
                    row.addView(createTextView(item.shipment.toString()))
                    //раньше было item.shipment.shipment_id но что-то не работало так
                    row.addView(createTextView(item.write_off_of_products?.toString() ?: "N/A"))
                    row.addView(createTextView(item.extradition?.toString() ?: "N/A"))
                }
                is WriteOffOfProducts -> {
                    row.addView(createTextView(item.id_product_write_off.toString()))
                    row.addView(createTextView(item.product_write_off_date))
                    row.addView(createTextView(item.quantity.toString()))
                    row.addView(createTextView(item.reason))
                    row.addView(createTextView(item.user.toString()))
                }
                is Extradition -> {
                    row.addView(createTextView(item.extradition_id.toString()))
                    row.addView(createTextView(item.date_of_extradition))
                    row.addView(createTextView(item.quantity.toString()))
                    row.addView(createTextView(item.user.toString()))
                }
                is ProductsCurrentQuantity -> {

                    //Log.d("PopulateTable", "ProductCurrentQuantity Item: ${item.product_current_quantity_id}, Quantity: ${item.quantity}, Product: ${item.product}")

                    row.addView(createTextView(item.product_current_quantity_id.toString()))
                    row.addView(createTextView(item.quantity.toString()))
                    row.addView(createTextView(item.product.toString()))

//                    // Логирование ошибок, если данные пустые или невалидные
//                    if (item.product_current_quantity_id == null) {
//                        Log.e("PopulateTable", "Product Current Quantity ID is null for item: $item")
//                    }
//                    if (item.product == null) {
//                        Log.e("PopulateTable", "Product is null for item: $item")
//                    }

                }
            }
            tableLayout.addView(row)
        }
    }

    private fun createTextView(text: String): TextView {
        return TextView(context).apply {
            this.text = text
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.LTGRAY) // Добавьте для отладки
            setTextColor(Color.BLACK) // Устанавливаем черный цвет текста
            layoutParams = TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }


    private fun openEditDialog(user: User) {
        val dialog = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_user, null)
        dialog.setView(dialogView)

        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_user_name)
        val loginEditText = dialogView.findViewById<EditText>(R.id.edit_user_login)
        val emailEditText = dialogView.findViewById<EditText>(R.id.edit_user_email)
        val passwordEditText =  dialogView.findViewById<EditText>(R.id.edit_user_password)
        val phoneNumberEditText = dialogView.findViewById<EditText>(R.id.edit_user_phone_number)
        val roleEditText = dialogView.findViewById<EditText>(R.id.edit_user_role)

        //записываем в поля что было до будущих изменений
        nameEditText.setText(user.user_name)
        loginEditText.setText(user.login)
        emailEditText.setText(user.email)
        passwordEditText.setText(user.password)
        phoneNumberEditText.setText(user.phone_number)
        roleEditText.setText(user.role)

        dialog.setPositiveButton("Сохранить") { _, _ ->
            val newName = nameEditText.text.toString()
            val newLogin = loginEditText.text.toString()
            val newEmail = emailEditText.text.toString()
            val newPassword = passwordEditText.text.toString()
            val newPhoneNumber = phoneNumberEditText.text.toString()
            val newRole = roleEditText.text.toString()

            // Вызов API с новыми параметрами
            updateUser(user.user_id, newName, newEmail, newLogin, newPassword, newPhoneNumber, newRole)
        }

        dialog.setNegativeButton("Отмена", null)
        dialog.create().show()
    }

    private fun updateUser(user_id: Int, user_name: String, email: String, login: String, password: String, phone_number: String?, role: String) {

        val apiService = RetrofitClient.apiService

        //val call = apiService.updateUser(user_id, User(user_id, user_name, email, login,password, phone_number,role))
        val updatedUser = User(user_id = user_id, user_name = user_name, email = email, login = login, password = password, phone_number = phone_number, role = role)

        val call = apiService.updateUser(user_id, updatedUser)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    populateTable("User")
                } else {
                    Toast.makeText(context, "Failed to update user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



}
