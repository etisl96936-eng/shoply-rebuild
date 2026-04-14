package com.shoply.app.auth

data class UserSession(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val role: String = "user"
) {
    val isAdmin: Boolean
        get() = role == "admin"
}
