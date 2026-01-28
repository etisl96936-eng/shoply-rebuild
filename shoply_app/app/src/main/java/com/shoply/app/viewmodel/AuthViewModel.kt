package com.shoply.app.viewmodel

import androidx.lifecycle.ViewModel
import com.shoply.app.auth.AuthRepository

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        repository.login(email, password, onResult)
    }

    fun register(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        repository.register(email, password, onResult)
    }

    fun logout() {
        repository.logout()
    }
}
