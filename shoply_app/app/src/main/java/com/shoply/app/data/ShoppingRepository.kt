package com.shoply.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.shoply.app.data.ShoppingItem
import kotlinx.coroutines.tasks.await

class ShoppingRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val itemsCollection = firestore.collection("items")

    // פונקציה למשיכת כל המוצרים מהענן
    suspend fun getItems(): List<ShoppingItem> {
        return try {
            itemsCollection.get().await().toObjects(ShoppingItem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // פונקציה להוספת מוצר חדש (עבור עטרה)
    suspend fun addItem(item: ShoppingItem) {
        itemsCollection.add(item).await()
    }
}