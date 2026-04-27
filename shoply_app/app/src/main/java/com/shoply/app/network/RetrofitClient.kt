package com.shoply.app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// קובץ זה יוצר אינסטנס של Retrofit
object RetrofitClient {

    // כתובת השרת - API חיצוני פרוס על Render
    private const val BASE_URL = "https://shoply-api-os8y.onrender.com/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
