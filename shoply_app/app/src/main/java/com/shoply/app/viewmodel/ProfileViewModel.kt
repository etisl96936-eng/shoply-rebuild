package com.shoply.app.viewmodel
import com.google.firebase.firestore.SetOptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shoply.app.data.ProfileData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


data class ProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val profile: ProfileData = ProfileData(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile() {
        val uid = auth.currentUser?.uid ?: return
        val email = auth.currentUser?.email.orEmpty()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                val doc = firestore.collection("users").document(uid).get().await()

                val displayName = doc.getString("displayName") ?: ""
                val preferredStores =
                    (doc.get("preferredStores") as? List<*>)?.filterIsInstance<String>() ?: emptyList()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile = ProfileData(
                        displayName = displayName,
                        email = email,
                        preferredStores = preferredStores
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "שגיאה בטעינת הפרופיל"
                )
            }
        }
    }

    fun updateDisplayName(value: String) {
        _uiState.value = _uiState.value.copy(
            profile = _uiState.value.profile.copy(displayName = value),
            successMessage = null
        )
    }

    fun toggleStore(store: String) {
        val current = _uiState.value.profile.preferredStores.toMutableList()

        if (current.contains(store)) {
            current.remove(store)
        } else {
            if (current.size < 3) {
                current.add(store)
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "אפשר לבחור עד 3 סופרים מועדפים"
                )
                return
            }
        }

        _uiState.value = _uiState.value.copy(
            profile = _uiState.value.profile.copy(preferredStores = current),
            errorMessage = null,
            successMessage = null
        )
    }

    fun saveProfile() {
        val uid = auth.currentUser?.uid ?: return
        val email = auth.currentUser?.email.orEmpty()
        val profile = _uiState.value.profile

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                val data = mapOf(
                    "displayName" to profile.displayName,
                    "email" to email,
                    "preferredStores" to profile.preferredStores
                )

                firestore.collection("users").document(uid).set(data, SetOptions.merge()).await()

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successMessage = "הפרופיל נשמר בהצלחה"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.localizedMessage ?: "שמירת הפרופיל נכשלה"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}