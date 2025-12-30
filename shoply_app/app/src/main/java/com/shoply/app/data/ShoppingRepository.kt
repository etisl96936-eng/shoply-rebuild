package com.shoply.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.shoply.app.data.ShoppingItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class ShoppingRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val itemsCollection = firestore.collection("items")

    // פונקציה שמקשיבה לשינויים בזמן אמת (Real-time)
    //  מאפשר להציג רשימה שמתעדכנת לבד אצל כולם
    fun getItemsFlow(): Flow<List<ShoppingItem>> = callbackFlow {
        val subscription = itemsCollection
            .orderBy("timestamp") // מיון
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.toObjects(ShoppingItem::class.java) ?: emptyList()
                trySend(items)
            }
        awaitClose { subscription.remove() }
    }

    // פונקציה להוספת מוצר (עטרה/אתי)
    suspend fun addItem(item: ShoppingItem) {
        try {
            itemsCollection.add(item).await()
        } catch (e: Exception) {
            // כאן את יכולה להוסיף לוג של שגיאה אם תרצי
        }
    }

    // פונקציה למחיקת מוצר ( עטרה)
    suspend fun deleteItem(itemId: String) {
        if (itemId.isNotEmpty()) {
            itemsCollection.document(itemId).delete().await()
        }
    }

    // פונקציה לעדכון מצב "נקנה" ( אתי)
    suspend fun toggleItemChecked(itemId: String, isChecked: Boolean) {
        itemsCollection.document(itemId).update("checked", isChecked).await()
    }
}