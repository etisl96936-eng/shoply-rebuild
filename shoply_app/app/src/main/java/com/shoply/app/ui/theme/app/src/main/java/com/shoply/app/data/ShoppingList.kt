package com.shoply.app.data

import com.google.firebase.firestore.DocumentId

data class ShoppingList(
    @DocumentId
    val id: String = "",

    val ownerUid: String = "",
    val name: String = "",

    // active / archived / completed
    val status: String = STATUS_ACTIVE,

    val selectedStore: String? = null,

    // שיתוף
    val sharedWith: List<String> = emptyList(),
    val editableBy: List<String> = emptyList(),

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) {
    companion object {
        const val STATUS_ACTIVE = "active"
        const val STATUS_ARCHIVED = "archived"
        const val STATUS_COMPLETED = "completed"
    }
}