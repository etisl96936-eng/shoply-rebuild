package com.shoply.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.shoply.app.data.ShoppingItem
import com.shoply.app.data.repository.ShoppingRepository
import com.shoply.app.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.shoply.app.data.CompletedShoppingList

class ShoppingViewModel : ViewModel() {
    private val repository = ShoppingRepository()
    private val auth = FirebaseAuth.getInstance()

    private val currentUidFlow = MutableStateFlow(auth.currentUser?.uid.orEmpty())

    init {
        auth.addAuthStateListener { firebaseAuth ->
            currentUidFlow.value = firebaseAuth.currentUser?.uid.orEmpty()
        }
    }

    private val currentUid: String
        get() = auth.currentUser?.uid.orEmpty()

    private val _selectedStore = MutableStateFlow<String?>(null)
    val selectedStore: StateFlow<String?> = _selectedStore

    fun selectStore(storeName: String) {
        _selectedStore.value = storeName
    }

    val catalogUiState: StateFlow<UiState<List<ShoppingItem>>> = repository.getItemsFlow()
        .map { items ->
            UiState.Success(items) as UiState<List<ShoppingItem>>
        }
        .catch { e ->
            emit(UiState.Error(e.message ?: "שגיאה לא ידועה"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    val shoppingListUiState: StateFlow<UiState<List<ShoppingItem>>> =
        currentUidFlow
            .flatMapLatest { uid ->
                if (uid.isBlank()) {
                    flowOf(UiState.Error("לא נמצא משתמש מחובר"))
                } else {
                    repository.getUserShoppingListFlow(uid)
                        .map { items ->
                            UiState.Success(items) as UiState<List<ShoppingItem>>
                        }
                        .catch { e ->
                            emit(UiState.Error(e.message ?: "שגיאה לא ידועה"))
                        }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading
            )

    val completedListsUiState: StateFlow<UiState<List<CompletedShoppingList>>> =
        currentUidFlow
            .flatMapLatest { uid ->
                if (uid.isBlank()) {
                    flowOf(UiState.Error("לא נמצא משתמש מחובר"))
                } else {
                    repository.getCompletedShoppingListsFlow(uid)
                        .map { lists ->
                            UiState.Success(lists) as UiState<List<CompletedShoppingList>>
                        }
                        .catch { e ->
                            emit(UiState.Error(e.message ?: "שגיאה לא ידועה"))
                        }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading
            )

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

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }

    fun toggleItemInMyList(item: ShoppingItem) {
        val uid = currentUid
        if (uid.isBlank()) return

        viewModelScope.launch {
            repository.toggleItemInUserShoppingList(uid, item)
        }
    }

    fun togglePurchasedInMyList(item: ShoppingItem) {
        val uid = currentUid
        if (uid.isBlank()) return

        viewModelScope.launch {
            repository.updatePurchasedInUserShoppingList(
                uid = uid,
                itemId = item.id,
                isChecked = !item.isChecked
            )
        }
    }

    fun removeFromMyList(itemId: String) {
        val uid = currentUid
        if (uid.isBlank()) return

        viewModelScope.launch {
            repository.removeFromUserShoppingList(uid, itemId)
        }
    }

    fun completeShoppingList(
        items: List<ShoppingItem>,
        selectedStore: String,
        totalAmount: Double
    ) {
        val uid = currentUid
        if (uid.isBlank()) return

        viewModelScope.launch {
            repository.completeShoppingList(
                uid = uid,
                items = items,
                selectedStore = selectedStore,
                totalAmount = totalAmount
            )
        }
    }

    fun deleteCompletedList(listId: String) {
        val uid = currentUid
        if (uid.isBlank()) return

        viewModelScope.launch {
            repository.deleteCompletedList(uid, listId)
        }
    }
}