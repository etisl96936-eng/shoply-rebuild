package com.shoply.app.network

import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://shoply-api-os8y.onrender.com/"

    // 🔁 Interceptor שעושה retry אוטומטי
    class RetryInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var tryCount = 0
            val maxTry = 3
            var response: Response? = null
            var exception: IOException? = null

            while (tryCount < maxTry) {
                try {
                    response = chain.proceed(chain.request())
                    if (response.isSuccessful) {
                        return response
                    }
                } catch (e: IOException) {
                    exception = e
                }

                tryCount++
                Thread.sleep(1000) // ⏳ מחכה שניה לפני ניסיון נוסף
            }

            throw exception ?: IOException("Unknown network error")
        }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(RetryInterceptor()) // 👈 פה הקסם
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // 👈 חשוב
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}