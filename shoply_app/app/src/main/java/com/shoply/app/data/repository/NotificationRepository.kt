package com.shoply.app.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.shoply.app.data.AppNotification
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationRepository {

    private val db = FirebaseFirestore.getInstance()

    private fun getNotificationsCollection(userId: String) =
        db.collection("users")
            .document(userId)
            .collection("notifications")

    fun getNotifications(userId: String): Flow<List<AppNotification>> = callbackFlow {
        val listener = getNotificationsCollection(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val notifications = snapshot?.documents?.map { doc ->
                    AppNotification(
                        id = doc.id,
                        userId = doc.getString("userId") ?: userId,
                        title = doc.getString("title") ?: "",
                        message = doc.getString("message") ?: "",
                        type = doc.getString("type") ?: "",
                        relatedListId = doc.getString("relatedListId"),
                        read = (doc.getBoolean("read") == true) || (doc.getBoolean("isRead") == true),
                        createdAt = doc.getLong("createdAt") ?: 0L
                    )
                } ?: emptyList()

                trySend(notifications)
            }

        awaitClose {
            listener.remove()
        }
    }

    suspend fun addNotification(notification: AppNotification) {
        val docRef = getNotificationsCollection(notification.userId).document()

        val notificationWithId = notification.copy(
            id = docRef.id
        )

        docRef.set(notificationWithId).await()
    }

    suspend fun markAsRead(userId: String, notificationId: String) {
        if (notificationId.isBlank()) return

        getNotificationsCollection(userId)
            .document(notificationId)
            .update(
                mapOf(
                    "read" to true,
                    "isRead" to FieldValue.delete()
                )
            )
            .await()
    }
}