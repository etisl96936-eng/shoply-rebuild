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
    val userRole: String = "user",
    val displayName: String = ""
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
                val (role, displayName) = if (uid.isNotBlank()) loadUserData(uid) else ("user" to "")
                Triple(session, role, displayName)
            }

            result.onSuccess { (session, role, displayName) ->
                _uiState.value = AuthUiState(
                    isLoading = false,
                    isAuthenticated = true,
                    userSession = session,
                    userRole = role,
                    displayName = displayName
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
                    val (role, displayName) = if (uid.isNotBlank()) loadUserData(uid) else ("user" to "")

                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isAuthenticated = true,
                        userSession = session,
                        userRole = role,
                        displayName = displayName
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

    /**
     * אבן דרך 5.3 - הרשמת משתמש חדש
     * כוללת ולידציות: מייל תקין, סיסמה באורך 6+, אימות סיסמה, שם תצוגה
     */
    fun register(
        email: String,
        password: String,
        confirmPassword: String,
        displayName: String
    ) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()
        val trimmedConfirm = confirmPassword.trim()
        val trimmedName = displayName.trim()

        when {
            trimmedName.isBlank() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "חובה להזין שם תצוגה")
                return
            }
            trimmedName.length < 2 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "שם תצוגה קצר מדי (לפחות 2 תווים)")
                return
            }
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
            trimmedPassword.length < 6 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "סיסמה חייבת להיות באורך של 6 תווים לפחות")
                return
            }
            trimmedPassword != trimmedConfirm -> {
                _uiState.value = _uiState.value.copy(errorMessage = "הסיסמאות אינן תואמות")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.register(trimmedEmail, trimmedPassword, trimmedName)
                .onSuccess { session ->
                    val uid = auth.currentUser?.uid.orEmpty()
                    val (role, loadedName) = if (uid.isNotBlank()) loadUserData(uid) else ("user" to trimmedName)

                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isAuthenticated = true,
                        userSession = session,
                        userRole = role,
                        displayName = loadedName.ifBlank { trimmedName }
                    )
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "הרשמה נכשלה"
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
                    val (role, displayName) = if (uid.isNotBlank()) loadUserData(uid) else ("user" to "")

                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isAuthenticated = true,
                        userSession = session,
                        userRole = role,
                        displayName = displayName
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

    fun refreshUserData() {
        checkExistingSession()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState()
    }

    private suspend fun loadUserData(uid: String): Pair<String, String> {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            val role = document.getString("role") ?: "user"
            val displayName = document.getString("displayName") ?: ""
            role to displayName
        } catch (_: Exception) {
            "user" to ""
        }
    }
}