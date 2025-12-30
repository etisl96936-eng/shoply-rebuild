package com.shoply.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoply.app.data.ShoppingItem
import com.shoply.app.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingViewModel : ViewModel() {
    // אתחול ה-Repository
    private val repository = ShoppingRepository()

    /**
     * זוהי הדרך המקצועית ב-Compose:
     * ה-ViewModel "מאזין" ל-Flow מה-Repository והופך אותו ל-StateFlow.
     * כך המסך (UI) יתעדכן אוטומטית בכל פעם שיש שינוי ב-Firebase.
     */
    val items: StateFlow<List<ShoppingItem>> = repository.getItemsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList() // רשימה ריקה עד שהנתונים מגיעים מהענן
        )

    // פונקציה להוספת מוצר ( עטרה/אור)
    fun addItem(name: String, quantity: String) {
        viewModelScope.launch {
            val newItem = ShoppingItem(
                name = name,
                quantity = quantity,
                timestamp = System.currentTimeMillis() //  סדר כרונולוגי
            )
            repository.addItem(newItem)
        }
    }

    // פונקציה למחיקת מוצר ( עטרה)
    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }

    // פונקציה לסימון "בוצע" על מוצר ( אתי)
    fun toggleItemChecked(item: ShoppingItem) {
        viewModelScope.launch {
            repository.toggleItemChecked(item.id, !item.isChecked)
        }
    }
}