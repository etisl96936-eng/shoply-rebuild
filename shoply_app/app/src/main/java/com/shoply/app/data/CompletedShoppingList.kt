package com.shoply.app.data

data class CompletedShoppingList(
    val id: String = "",
    val name: String = "",
    val completedAt: Long = 0L,
    val selectedStore: String = "",
    val items: List<ShoppingItem> = emptyList(),
    val totalAmount: Double = 0.0
)