package com.shoply.app.network

import com.shoply.app.network.dto.LoginRequestDto
import com.shoply.app.network.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

// קובץ זה מגדיר את כל ה-endpoints שלנו
interface ApiService {

    @POST("login")  // HTTP POST לכתובת /login
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto
}