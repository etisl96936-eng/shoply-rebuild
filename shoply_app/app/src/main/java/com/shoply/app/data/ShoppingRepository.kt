package com.shoply.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.shoply.app.data.ShoppingItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class ShoppingRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val itemsCollection = firestore.collection("items")

    // פונקציה שמקשיבה לשינויים בזמן אמת (Real-time)
    fun getItemsFlow(): Flow<List<ShoppingItem>> = callbackFlow {
        val subscription = itemsCollection
            .orderBy("timestamp") // מיון לפי זמן הוספה
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                // הפיכת המסמכים מרשימת Firebase לאובייקטים של ShoppingItem
                val items = snapshot?.toObjects(ShoppingItem::class.java) ?: emptyList()
                trySend(items)
            }

        // סגירת המאזין כשהזרימה (Flow) מסתיימת כדי לחסוך במשאבים
        awaitClose { subscription.remove() }
    }

    // פונקציה להוספת מוצר - מעודכן לשמירת ה-ID בתוך המסמך
    suspend fun addItem(item: ShoppingItem) {
        try {
            // יוצרים מסמך חדש כדי לקבל ID אוטומטי
            val newDocRef = itemsCollection.document()

            // יוצרים עותק של המוצר עם ה-ID שנוצר
            val itemWithId = item.copy(id = newDocRef.id)

            // שומרים את המוצר המעודכן ב-Firebase
            newDocRef.set(itemWithId).await()
        } catch (e: Exception) {
            println("Error adding item: ${e.message}")
        }
    }

    // פונקציה למחיקת מוצר
    suspend fun deleteItem(itemId: String) {
        if (itemId.isNotEmpty()) {
            itemsCollection.document(itemId).delete().await()
        }
    }

    // פונקציה לעדכון מצב "isChecked"
    suspend fun toggleItemChecked(itemId: String, isChecked: Boolean) {
        if (itemId.isNotEmpty()) {
            itemsCollection.document(itemId)
                .update("isChecked", isChecked)
                .await()
        }
    }
}