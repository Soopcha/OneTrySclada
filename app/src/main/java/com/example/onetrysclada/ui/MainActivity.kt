package com.example.onetrysclada.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import com.example.onetrysclada.data.models.Extradition
import com.example.onetrysclada.data.models.Product
import com.example.onetrysclada.data.models.ProductsCurrentQuantity
import com.example.onetrysclada.R
import com.example.onetrysclada.data.models.Shipment
import com.example.onetrysclada.TableAdapter
import com.example.onetrysclada.data.models.User
import com.example.onetrysclada.data.models.WriteOffOfProducts
import com.example.onetrysclada.data.network.LoginRequest
import com.example.onetrysclada.data.network.LoginResponse
import com.example.onetrysclada.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var forgotPasswordTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Проверяем, есть ли сохранённый токен
        if (RetrofitClient.getToken(this) != null) {
            // Если токен есть, сразу переходим к TableActivity
            startActivity(Intent(this, TableActivity::class.java))
            finish()
            return
        }

        // Инициализация UI-элементов
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login_button)
        rememberMeCheckBox = findViewById(R.id.remember_me)
        forgotPasswordTextView = findViewById(R.id.forgot_password)

        // Обработчик кнопки логина
        loginButton.setOnClickListener {
            val login = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, введите логин и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Создаём запрос для логина
            val loginRequest = LoginRequest(login = login, password = password)
            RetrofitClient.getApiService(this).login(loginRequest)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val token = response.body()?.token
                            if (token != null) {
                                // Сохраняем токен
                                RetrofitClient.saveToken(this@MainActivity, token)

                                // Запрашиваем профиль пользователя для получения роли
                                RetrofitClient.getApiService(this@MainActivity).getProfile()
                                    .enqueue(object : Callback<User> {
                                        override fun onResponse(call: Call<User>, response: Response<User>) {
                                            if (response.isSuccessful) {
                                                val user = response.body()
                                                if (user != null) {
                                                    // Сохраняем роль в SharedPreferences
                                                    val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                                                    with(sharedPreferences.edit()) {
                                                        putString("user_role", user.role)
                                                        apply()
                                                    }
                                                    Log.d("MainActivity", "User role saved: ${user.role}")

                                                    Toast.makeText(this@MainActivity, "Вход успешен!", Toast.LENGTH_SHORT).show()

                                                    // Переходим на TableActivity
                                                    val intent = Intent(this@MainActivity, TableActivity::class.java)
                                                    startActivity(intent)
                                                    finish() // Закрываем MainActivity
                                                } else {
                                                    Toast.makeText(this@MainActivity, "Ошибка: данные пользователя не получены", Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                Toast.makeText(this@MainActivity, "Ошибка получения профиля: ${response.code()}", Toast.LENGTH_LONG).show()
                                                Log.e("ProfileError", "Response: ${response.errorBody()?.string()}")
                                            }
                                        }

                                        override fun onFailure(call: Call<User>, t: Throwable) {
                                            Toast.makeText(this@MainActivity, "Ошибка сети при получении профиля: ${t.message}", Toast.LENGTH_LONG).show()
                                            Log.e("ProfileError", "Failure: ${t.message}", t)
                                        }
                                    })
                            } else {
                                Toast.makeText(this@MainActivity, "Ошибка: токен не получен", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorMessage = when (response.code()) {
                                401 -> "Неверный логин или пароль"
                                400 -> "Некорректный запрос"
                                else -> "Ошибка сервера: ${response.code()}"
                            }
                            Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                            Log.e("LoginError", "Response: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_LONG).show()
                        Log.e("LoginError", "Failure: ${t.message}", t)
                    }
                })
        }

        // Обработчик "Forgot Password" (заглушка)
        forgotPasswordTextView.setOnClickListener {
            Toast.makeText(this, "Функция восстановления пароля пока не реализована", Toast.LENGTH_SHORT).show()
        }
    }
}
