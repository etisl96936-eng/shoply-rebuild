package com.shoply.app.data

import com.google.firebase.firestore.DocumentId

data class ShoppingItem(
    @DocumentId // אנוטציה שאומרת ל-Firebase להכניס לכאן את ה-ID של המסמך אוטומטית
    val id: String = "",
    val name: String = "",
    val quantity: String = "1",      // כדאי להוסיף כמות (למשל: "2 חבילות")
    val isChecked: Boolean = false,
    val category: String = "כללי",
    val addedBy: String = "",        // מי הוסיף את המוצר
    val timestamp: Long = System.currentTimeMillis() // למיין לפי זמן הוספה
)