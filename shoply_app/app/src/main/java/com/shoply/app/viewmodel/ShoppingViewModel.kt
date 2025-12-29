package com.shoply.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoply.app.data.ShoppingItem
import com.shoply.app.data.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShoppingViewModel : ViewModel() {
    private val repository = ShoppingRepository()

    private val _items = MutableStateFlow<List<ShoppingItem>>(emptyList())
    val items: StateFlow<List<ShoppingItem>> = _items

    init {
        // נתוני בדיקה זמניים כדי לראות שה-UI מחובר ל-ViewModel
        _items.value = listOf(
            ShoppingItem(name = "חלב בדיקה", isChecked = false),
            ShoppingItem(name = "לחם בדיקה", isChecked = true)
        )
        loadItems() // זה ינסה למשוך גם מה-Firebase האמיתי
    }

    fun loadItems() {
        viewModelScope.launch {
            _items.value = repository.getItems()
        }
    }
}