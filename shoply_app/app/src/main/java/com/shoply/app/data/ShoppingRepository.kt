package com.shoply.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.shoply.app.data.ShoppingItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ShoppingRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val itemsCollection = firestore.collection("items")

    // קטלוג גלובלי
    fun getItemsFlow(): Flow<List<ShoppingItem>> = callbackFlow {
        val subscription = itemsCollection
            .orderBy("timestamp")
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

    // רשימת קניות אישית של משתמש
    fun getUserShoppingListFlow(uid: String): Flow<List<ShoppingItem>> = callbackFlow {
        val subscription = firestore
            .collection("users")
            .document(uid)
            .collection("shopping_list")
            .orderBy("timestamp")
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

    // הוספת מוצר לקטלוג הגלובלי
    suspend fun addItem(item: ShoppingItem) {
        try {
            val newDocRef = itemsCollection.document()
            val itemWithId = item.copy(id = newDocRef.id)
            newDocRef.set(itemWithId).await()
        } catch (e: Exception) {
            println("Error adding item: ${e.message}")
        }
    }

    // מחיקת מוצר מהקטלוג הגלובלי
    suspend fun deleteItem(itemId: String) {
        if (itemId.isNotEmpty()) {
            itemsCollection.document(itemId).delete().await()
        }
    }

    // הוספת מוצר לרשימה האישית
    suspend fun addToUserShoppingList(uid: String, item: ShoppingItem) {
        if (uid.isBlank() || item.id.isBlank()) return

        firestore.collection("users")
            .document(uid)
            .collection("shopping_list")
            .document(item.id)
            .set(item.copy(isChecked = true))
            .await()
    }

    // הסרת מוצר מהרשימה האישית
    suspend fun removeFromUserShoppingList(uid: String, itemId: String) {
        if (uid.isBlank() || itemId.isBlank()) return

        firestore.collection("users")
            .document(uid)
            .collection("shopping_list")
            .document(itemId)
            .delete()
            .await()
    }

    // סימון/ביטול מוצר ברשימה האישית
    suspend fun toggleItemInUserShoppingList(uid: String, item: ShoppingItem) {
        if (uid.isBlank() || item.id.isBlank()) return

        val docRef = firestore.collection("users")
            .document(uid)
            .collection("shopping_list")
            .document(item.id)

        val snapshot = docRef.get().await()

        if (snapshot.exists()) {
            docRef.delete().await()
        } else {
            docRef.set(item.copy(isChecked = false)).await()
        }
    }

    // עדכון מצב נקנה בתוך הרשימה האישית
    suspend fun updatePurchasedInUserShoppingList(uid: String, itemId: String, isChecked: Boolean) {
        if (uid.isBlank() || itemId.isBlank()) return

        firestore.collection("users")
            .document(uid)
            .collection("shopping_list")
            .document(itemId)
            .update("isChecked", isChecked)
            .await()
    }
}