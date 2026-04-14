package com.shoply.app.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoply.app.auth.AuthRepository
import com.shoply.app.auth.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val userSession: UserSession? = null,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkExistingSession()
    }

    fun checkExistingSession() {
        if (!repository.isUserLoggedIn()) {
            _uiState.value = AuthUiState()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = runCatching { repository.loadCurrentUserSession() }
            result.onSuccess { session ->
                _uiState.value = AuthUiState(
                    isLoading = false,
                    isAuthenticated = true,
                    userSession = session
                )
            }.onFailure { error ->
                _uiState.value = AuthUiState(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "שגיאה בטעינת המשתמש"
                )
            }
        }
    }

    fun login(email: String, password: String) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        when {
            trimmedEmail.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "חובה להזין אימייל")
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "אימייל לא תקין")
                return
            }
            trimmedPassword.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "חובה להזין סיסמה")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.login(trimmedEmail, trimmedPassword)
                .onSuccess { session ->
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isAuthenticated = true,
                        userSession = session
                    )
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "התחברות נכשלה"
                    )
                }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.loginWithGoogle(idToken)
                .onSuccess { session ->
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isAuthenticated = true,
                        userSession = session
                    )
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "התחברות עם Google נכשלה"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState()
    }
}
