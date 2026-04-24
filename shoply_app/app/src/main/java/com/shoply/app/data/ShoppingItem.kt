package com.shoply.app.data
import com.google.firebase.firestore.DocumentId

val storePrices: List<StorePrice> = emptyList()

data class ShoppingItem(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "כללי",
    val imageRes: Int = 0,
    val videoUrl: String = "",
    val quantity: String = "1",
    @field:JvmField
    val isChecked: Boolean = false,
    val addedBy: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isSelected: Boolean = false,
    val isPurchased: Boolean = false,

    // 👇 זה החדש
    val storePrices: List<StorePrice> = emptyList()
)