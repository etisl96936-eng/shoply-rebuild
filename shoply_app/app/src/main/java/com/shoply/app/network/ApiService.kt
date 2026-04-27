package com.shoply.app.network

import com.shoply.app.data.ShoppingItem
import com.shoply.app.network.dto.LoginRequestDto
import com.shoply.app.network.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// קובץ זה מגדיר את כל ה-endpoints שלנו
interface ApiService {

    // לוגין - נשאר כמו שהיה
    @POST("login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    // קבלת כל המוצרים מה-API
    @GET("products")
    suspend fun getProducts(): List<ShoppingItem>

    // סינון לפי קטגוריה
    @GET("products")
    suspend fun getProductsByCategory(@Query("category") category: String): List<ShoppingItem>

    // חיפוש לפי שם
    @GET("products")
    suspend fun searchProducts(@Query("search") search: String): List<ShoppingItem>

    // מוצר לפי id
    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): ShoppingItem

    // רשימת קטגוריות
    @GET("categories")
    suspend fun getCategories(): List<String>
}
