package com.shoply.app.network.dto

data class LoginResponseDto(
    val success: Boolean,
    val token: String?,
    val message: String?
)