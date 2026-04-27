package com.shoply.app.ui.notifications
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.shoply.app.data.AppNotification
import com.shoply.app.data.repository.NotificationRepository
import com.shoply.app.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository = NotificationRepository()
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _notificationsUiState =
        MutableStateFlow<UiState<List<AppNotification>>>(UiState.Loading)

    val notificationsUiState: StateFlow<UiState<List<AppNotification>>> =
        _notificationsUiState

    fun loadNotifications() {
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            _notificationsUiState.value = UiState.Error("משתמש לא מחובר")
            return
        }

        viewModelScope.launch {
            repository.getNotifications(currentUserId)
                .collect { notifications ->
                    _notificationsUiState.value = UiState.Success(notifications)
                }
        }
    }

    fun sendTestNotification() {
        val currentUserId = auth.currentUser?.uid ?: return

        val notification = AppNotification(
            userId = currentUserId,
            title = "התראת בדיקה 🔔",
            message = "המערכת עובדת תקין",
            type = "TEST"
        )

        viewModelScope.launch {
            repository.addNotification(notification)
        }
    }

    fun markAsRead(notificationId: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        val currentState = _notificationsUiState.value
        if (currentState is UiState.Success) {
            _notificationsUiState.value = UiState.Success(
                currentState.data.map { notification ->
                    if (notification.id == notificationId) {
                        notification.copy(read = true)
                    } else {
                        notification
                    }
                }
            )
        }

        viewModelScope.launch {
            repository.markAsRead(
                userId = currentUserId,
                notificationId = notificationId
            )
        }
    }
}