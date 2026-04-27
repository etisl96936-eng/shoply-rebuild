package com.shoply.app.data

data class AppNotification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "",
    val relatedListId: String? = null,
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)