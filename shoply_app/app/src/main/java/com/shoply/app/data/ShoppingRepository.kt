package com.shoply.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.shoply.app.data.CompletedShoppingList
import com.shoply.app.data.ShoppingItem
import com.shoply.app.data.ShoppingList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ShoppingRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val itemsCollection = firestore.collection("items")

    private fun shoppingListsCollection(uid: String) =
        firestore.collection("users")
            .document(uid)
            .collection("shopping_lists")

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

    // רשימת קניות אישית של משתמש - המבנה הישן
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

    fun getCompletedShoppingListsFlow(uid: String): Flow<List<CompletedShoppingList>> = callbackFlow {
        val subscription = firestore
            .collection("users")
            .document(uid)
            .collection("completed_lists")
            .orderBy("completedAt")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val lists = snapshot?.toObjects(CompletedShoppingList::class.java) ?: emptyList()
                trySend(lists)
            }

        awaitClose { subscription.remove() }
    }

    // =========================
    // Shopping Lists - מבנה חדש
    // =========================

    suspend fun createShoppingList(
        uid: String,
        name: String
    ): Result<String> = runCatching {
        val newDoc = shoppingListsCollection(uid).document()

        val shoppingList = ShoppingList(
            id = newDoc.id,
            ownerUid = uid,
            name = name.ifBlank { "רשימה חדשה" },
            status = ShoppingList.STATUS_ACTIVE,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        newDoc.set(shoppingList).await()
        newDoc.id
    }

    suspend fun getActiveShoppingLists(uid: String): Result<List<ShoppingList>> = runCatching {
        val snapshot = shoppingListsCollection(uid)
            .whereEqualTo("status", ShoppingList.STATUS_ACTIVE)
            .get()
            .await()

        snapshot.documents.mapNotNull { doc ->
            doc.toObject(ShoppingList::class.java)?.copy(id = doc.id)
        }.sortedByDescending { it.updatedAt }
    }

    suspend fun updateShoppingListName(
        uid: String,
        listId: String,
        newName: String
    ): Result<Unit> = runCatching {
        shoppingListsCollection(uid)
            .document(listId)
            .update(
                mapOf(
                    "name" to newName,
                    "updatedAt" to System.currentTimeMillis()
                )
            )
            .await()
    }

    suspend fun archiveShoppingList(
        uid: String,
        listId: String
    ): Result<Unit> = runCatching {
        shoppingListsCollection(uid)
            .document(listId)
            .update(
                mapOf(
                    "status" to ShoppingList.STATUS_ARCHIVED,
                    "updatedAt" to System.currentTimeMillis()
                )
            )
            .await()
    }

    suspend fun deleteShoppingList(
        uid: String,
        listId: String
    ): Result<Unit> = runCatching {
        shoppingListsCollection(uid)
            .document(listId)
            .delete()
            .await()
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

    // הוספת מוצר לרשימה האישית - המבנה הישן
    suspend fun addToUserShoppingList(uid: String, item: ShoppingItem) {
        if (uid.isBlank() || item.id.isBlank()) return

        firestore.collection("users")
            .document(uid)
            .collection("shopping_list")
            .document(item.id)
            .set(item.copy(isChecked = false))
            .await()
    }

    // הסרת מוצר מהרשימה האישית - המבנה הישן
    suspend fun removeFromUserShoppingList(uid: String, itemId: String) {
        if (uid.isBlank() || itemId.isBlank()) return

        firestore.collection("users")
            .document(uid)
            .collection("shopping_list")
            .document(itemId)
            .delete()
            .await()
    }

    // סימון/ביטול מוצר ברשימה האישית - המבנה הישן
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

    // עדכון מצב נקנה בתוך הרשימה האישית - המבנה הישן
    suspend fun updatePurchasedInUserShoppingList(uid: String, itemId: String, isChecked: Boolean) {
        if (uid.isBlank() || itemId.isBlank()) return

        firestore.collection("users")
            .document(uid)
            .collection("shopping_list")
            .document(itemId)
            .update("isChecked", isChecked)
            .await()
    }

    suspend fun completeShoppingList(
        uid: String,
        items: List<ShoppingItem>,
        selectedStore: String,
        totalAmount: Double
    ) {
        if (uid.isBlank() || items.isEmpty() || selectedStore.isBlank()) return

        val completedListRef = firestore.collection("users")
            .document(uid)
            .collection("completed_lists")
            .document()

        val completedData = hashMapOf(
            "id" to completedListRef.id,
            "completedAt" to System.currentTimeMillis(),
            "selectedStore" to selectedStore,
            "items" to items,
            "totalAmount" to totalAmount
        )

        completedListRef.set(completedData).await()

        val shoppingListCollection = firestore.collection("users")
            .document(uid)
            .collection("shopping_list")

        items.forEach { item ->
            shoppingListCollection.document(item.id).delete().await()
        }
    }

    suspend fun deleteCompletedList(uid: String, listId: String) {
        firestore.collection("users")
            .document(uid)
            .collection("completed_lists")
            .document(listId)
            .delete()
            .await()
    }
}