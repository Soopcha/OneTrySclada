package com.example.onetrysclada.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/" // Для эмулятора Android
    private const val PREF_NAME = "auth_prefs"
    private const val KEY_TOKEN = "auth_token"

    // SharedPreferences для хранения токена
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Сохранение токена
    fun saveToken(context: Context, token: String) {
        getSharedPreferences(context).edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    // Получение токена
    fun getToken(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_TOKEN, null)
    }

    // Очистка токена (выход из системы)
    fun clearToken(context: Context) {
        getSharedPreferences(context).edit()
            .remove(KEY_TOKEN)
            .apply()
    }

    // OkHttpClient с интерцептором для добавления токена
    private fun getOkHttpClient(context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Для отладки запросов
        }

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val token = getToken(context)

            // Добавляем заголовок Authorization, если токен есть
            val request = if (token != null && !original.url.encodedPath.endsWith("/api/login/")) {
                original.newBuilder()
                    .header("Authorization", "Token $token")
                    .build()
            } else {
                original
            }
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    // Retrofit с контекст-зависимым OkHttpClient
    fun getRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient(context))
            .build()
    }

    // ApiService с контекстом
    fun getApiService(context: Context): ApiService {
        return getRetrofit(context).create(ApiService::class.java)
    }
}