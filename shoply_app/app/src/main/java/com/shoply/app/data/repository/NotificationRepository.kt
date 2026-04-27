package com.shoply.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.shoply.app.data.AppNotification
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NotificationRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getNotifications(userId: String): Flow<List<AppNotification>> = callbackFlow {
        val listener = getNotificationsCollection(userId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val notifications = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(AppNotification::class.java)
                } ?: emptyList()

                trySend(notifications)
            }

        awaitClose {
            listener.remove()
        }
    }
    private fun getNotificationsCollection(userId: String) =
        db.collection("users")
            .document(userId)
            .collection("notifications")

    suspend fun addNotification(notification: AppNotification) {
        val docRef = getNotificationsCollection(notification.userId).document()

        val notificationWithId = notification.copy(
            id = docRef.id
        )

        docRef.set(notificationWithId).await()
    }

    suspend fun markAsRead(userId: String, notificationId: String) {
        getNotificationsCollection(userId)
            .document(notificationId)
            .update("isRead", true)
            .await()
    }
}