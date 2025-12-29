package com.shoply.app.data

data class ShoppingItem(
    val id: String = "",           // מזהה ייחודי מ-Firebase
    val name: String = "",         // שם המוצר (למשל: "חלב")
    val isChecked: Boolean = false,// האם כבר קנינו אותו?
    val category: String = "General" // קטגוריה (אופציונלי)
)