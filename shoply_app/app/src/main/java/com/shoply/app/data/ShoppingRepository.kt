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

    private fun shoppingListsCollection(uid: String) =
        firestore.collection("users")
            .document(uid)
            .collection("shopping_lists")

    private fun shoppingListItemsCollection(uid: String, listId: String) =
        shoppingListsCollection(uid)
            .document(listId)
            .collection("items")

    private fun normalizeQuantity(quantity: String): String {
        val parsed = quantity.trim().toIntOrNull()?.coerceAtLeast(1) ?: 1
        return parsed.toString()
    }

    private suspend fun updateShoppingListTimestamp(uid: String, listId: String) {
        shoppingListsCollection(uid)
            .document(listId)
            .update("updatedAt", System.currentTimeMillis())
            .await()
    }

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
        val mySnapshot = shoppingListsCollection(uid)
            .whereEqualTo("status", ShoppingList.STATUS_ACTIVE)
            .get()
            .await()

        val sharedSnapshot = firestore
            .collectionGroup("shopping_lists")
            .whereEqualTo("status", ShoppingList.STATUS_ACTIVE)
            .whereArrayContains("sharedWith", uid)
            .get()
            .await()

        val myLists = mySnapshot.documents.mapNotNull { doc ->
            doc.toObject(ShoppingList::class.java)?.copy(id = doc.id)
        }

        val sharedLists = sharedSnapshot.documents.mapNotNull { doc ->
            doc.toObject(ShoppingList::class.java)?.copy(id = doc.id)
        }

        (myLists + sharedLists)
            .distinctBy { "${it.ownerUid}_${it.id}" }
            .sortedByDescending { it.updatedAt }
    }

    suspend fun getShoppingListById(
        uid: String,
        listId: String
    ): Result<ShoppingList> = runCatching {
        val snapshot = shoppingListsCollection(uid)
            .document(listId)
            .get()
            .await()

        snapshot.toObject(ShoppingList::class.java)?.copy(id = snapshot.id)
            ?: throw IllegalStateException("הרשימה לא נמצאה")
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

    fun getShoppingListItemsFlow(uid: String, listId: String): Flow<List<ShoppingItem>> = callbackFlow {
        val subscription = shoppingListItemsCollection(uid, listId)
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

    suspend fun addItemToShoppingList(
        uid: String,
        listId: String,
        itemName: String,
        quantity: String
    ): Result<Unit> = runCatching {
        val newDoc = shoppingListItemsCollection(uid, listId).document()

        val item = ShoppingItem(
            id = newDoc.id,
            title = itemName.trim(),
            quantity = normalizeQuantity(quantity),
            category = "כללי",
            description = "",
            isChecked = false,
            timestamp = System.currentTimeMillis(),
            storePrices = emptyList()
        )

        newDoc.set(item).await()
        updateShoppingListTimestamp(uid, listId)
    }

    suspend fun addCatalogItemToShoppingList(
        uid: String,
        listId: String,
        catalogItem: ShoppingItem,
        quantity: String
    ): Result<Unit> = runCatching {
        val docId = if (catalogItem.id.isNotBlank()) {
            catalogItem.id
        } else {
            shoppingListItemsCollection(uid, listId).document().id
        }

        val docRef = shoppingListItemsCollection(uid, listId).document(docId)
        val snapshot = docRef.get().await()

        if (snapshot.exists()) {
            val existingItem = snapshot.toObject(ShoppingItem::class.java)
            val currentQty = existingItem?.quantity?.toIntOrNull()?.coerceAtLeast(1) ?: 1
            val addedQty = quantity.trim().toIntOrNull()?.coerceAtLeast(1) ?: 1
            val newQty = (currentQty + addedQty).toString()

            docRef.update(
                mapOf(
                    "quantity" to newQty,
                    "isChecked" to false,
                    "storePrices" to catalogItem.storePrices
                )
            ).await()
        } else {
            val itemToSave = catalogItem.copy(
                id = docId,
                quantity = normalizeQuantity(quantity),
                isChecked = false,
                isSelected = false,
                isPurchased = false,
                timestamp = System.currentTimeMillis(),
                storePrices = catalogItem.storePrices
            )

            docRef.set(itemToSave).await()
        }

        updateShoppingListTimestamp(uid, listId)
    }

    suspend fun updateItemCheckedInShoppingList(
        uid: String,
        listId: String,
        itemId: String,
        isChecked: Boolean
    ): Result<Unit> = runCatching {
        shoppingListItemsCollection(uid, listId)
            .document(itemId)
            .update("isChecked", isChecked)
            .await()

        updateShoppingListTimestamp(uid, listId)
    }

    suspend fun updateItemQuantityInShoppingList(
        uid: String,
        listId: String,
        itemId: String,
        quantity: String
    ): Result<Unit> = runCatching {
        shoppingListItemsCollection(uid, listId)
            .document(itemId)
            .update("quantity", normalizeQuantity(quantity))
            .await()

        updateShoppingListTimestamp(uid, listId)
    }

    suspend fun deleteItemFromShoppingList(
        uid: String,
        listId: String,
        itemId: String
    ): Result<Unit> = runCatching {
        shoppingListItemsCollection(uid, listId)
            .document(itemId)
            .delete()
            .await()

        updateShoppingListTimestamp(uid, listId)
    }

    suspend fun addToUserShoppingList(uid: String, item: ShoppingItem) {
        if (uid.isBlank() || item.id.isBlank()) return

        firestore.collection("users")
            .document(uid)
            .collection("shopping_list")
            .document(item.id)
            .set(item.copy(isChecked = false))
            .await()
    }

    suspend fun removeFromUserShoppingList(uid: String, itemId: String) {
        if (uid.isBlank() || itemId.isBlank()) return

        firestore.collection("users")
            .document(uid)
            .collection("shopping_list")
            .document(itemId)
            .delete()
            .await()
    }

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
        listId: String,
        listName: String,
        items: List<ShoppingItem>,
        selectedStore: String,
        totalAmount: Double
    ) {
        if (uid.isBlank() || listId.isBlank() || items.isEmpty() || selectedStore.isBlank()) return

        val completedListRef = firestore.collection("users")
            .document(uid)
            .collection("completed_lists")
            .document()

        val completedData = hashMapOf(
            "id" to completedListRef.id,
            "name" to listName,
            "completedAt" to System.currentTimeMillis(),
            "selectedStore" to selectedStore,
            "items" to items,
            "totalAmount" to totalAmount
        )

        completedListRef.set(completedData).await()

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

    suspend fun deleteCompletedList(uid: String, listId: String) {
        firestore.collection("users")
            .document(uid)
            .collection("completed_lists")
            .document(listId)
            .delete()
            .await()
    }

    suspend fun updateShoppingListSelectedStore(
        uid: String,
        listId: String,
        storeName: String
    ): Result<Unit> = runCatching {
        shoppingListsCollection(uid)
            .document(listId)
            .update(
                mapOf(
                    "selectedStore" to storeName,
                    "updatedAt" to System.currentTimeMillis()
                )
            )
            .await()
    }

    suspend fun markAllItemsAsPurchased(uid: String, listId: String) {
        val itemsSnapshot = firestore
            .collection("users")
            .document(uid)
            .collection("shopping_lists")
            .document(listId)
            .collection("items")
            .get()
            .await()

        val batch = firestore.batch()

        itemsSnapshot.documents.forEach { document ->
            batch.update(document.reference, "checked", true)
        }

        batch.commit().await()
    }
}