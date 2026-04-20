package com.shoply.app.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shoply.app.auth.AuthRepository
import com.shoply.app.auth.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val userSession: UserSession? = null,
    val errorMessage: String? = null,
    val userRole: String = "user"
)

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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

            val result = runCatching {
                val session = repository.loadCurrentUserSession()
                val uid = auth.currentUser?.uid.orEmpty()
                val role = if (uid.isNotBlank()) loadUserRole(uid) else "user"
                session to role
            }

            result.onSuccess { (session, role) ->
                _uiState.value = AuthUiState(
                    isLoading = false,
                    isAuthenticated = true,
                    userSession = session,
                    userRole = role
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
                    val uid = auth.currentUser?.uid.orEmpty()
                    val role = if (uid.isNotBlank()) loadUserRole(uid) else "user"

                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isAuthenticated = true,
                        userSession = session,
                        userRole = role
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
                    val uid = auth.currentUser?.uid.orEmpty()
                    val role = if (uid.isNotBlank()) loadUserRole(uid) else "user"

                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isAuthenticated = true,
                        userSession = session,
                        userRole = role
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

    private suspend fun loadUserRole(uid: String): String {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            document.getString("role") ?: "user"
        } catch (_: Exception) {
            "user"
        }
    }
}