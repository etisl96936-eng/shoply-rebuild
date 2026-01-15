package com.shoply.app.data

import com.google.firebase.firestore.DocumentId

data class ShoppingItem(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "כללי",
    val imageRes: Int = 0,
    val videoUrl: String = "",
    val quantity: String = "1",
    val isChecked: Boolean = false,
    val addedBy: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    var isSelected: Boolean = false
)