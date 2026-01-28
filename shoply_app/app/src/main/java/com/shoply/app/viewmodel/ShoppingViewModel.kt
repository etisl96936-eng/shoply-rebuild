package com.shoply.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoply.app.data.ShoppingItem
import com.shoply.app.data.repository.ShoppingRepository
import com.shoply.app.ui.state.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ShoppingViewModel : ViewModel() {
    private val repository = ShoppingRepository()

    /**
     * שדרוג: אנחנו הופכים את ה-Flow של ה-Repository ל-Flow של UiState.
     * כך המסך ידע אם להציג Spinner טעינה, הודעת שגיאה או את הרשימה.
     */
    val uiState: StateFlow<UiState<List<ShoppingItem>>> = repository.getItemsFlow()
        .map { items ->
            // אם הנתונים הגיעו בהצלחה
            UiState.Success(items) as UiState<List<ShoppingItem>>
        }
        .catch { e ->
            // אם קרתה שגיאה בדרך
            emit(UiState.Error(e.message ?: "שגיאה לא ידועה"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading // הסטייט ההתחלתי הוא טעינה
        )

    // פונקציה להוספת מוצר
    fun addItem(name: String, quantity: String, description: String, category: String) {
        viewModelScope.launch {
            val newItem = ShoppingItem(
                title = name,
                quantity = quantity,
                description = description,
                category = category,
                timestamp = System.currentTimeMillis()
            )
            repository.addItem(newItem)
        }
    }

    // פונקציה למחיקת מוצר
    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }

    // פונקציה לסימון "בוצע"
    fun toggleItemChecked(item: ShoppingItem) {
        viewModelScope.launch {
            // ב-Repository שלך הפונקציה מוגדרת לקבל ID ו-Boolean חדש
            // לכן אנחנו שולחים את item.id ואת הערך ההפוך ממה שיש עכשיו
            repository.toggleItemChecked(item.id, !item.isChecked)
        }
    }
}