package com.shoply.app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// קובץ זה יוצר אינסטנס של Retrofit
object RetrofitClient {

    private const val BASE_URL = "https://api.shoply.com/" // כתובת בסיס לדוגמה

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // המרה JSON ↔ Kotlin
            .build()
            .create(ApiService::class.java)
    }
}
