package com.shoply.app.data

data class ShoppingList(
    val id: String = "",
    val name: String = "",
    val ownerUid: String = "",
    val status: String = STATUS_ACTIVE,
    val selectedStore: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val sharedWith: List<String> = emptyList()
) {
    companion object {
        const val STATUS_ACTIVE = "active"
        const val STATUS_ARCHIVED = "archived"
        const val STATUS_COMPLETED = "completed"
    }
}