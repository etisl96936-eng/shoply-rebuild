package com.shoply.app.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.shoply.app.data.CompletedShoppingList
import com.shoply.app.data.ShoppingItem
import com.shoply.app.data.ShoppingList
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
import com.shoply.app.data.StorePrice
import kotlinx.coroutines.tasks.await
import com.shoply.app.network.RetrofitClient
import kotlinx.coroutines.flow.flow
import com.google.firebase.firestore.FirebaseFirestore
import com.shoply.app.data.DEFAULT_COMPARISON_STORES
import com.shoply.app.data.getComparisonStores

class ShoppingViewModel : ViewModel() {
    private val repository = ShoppingRepository()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _comparisonStores = MutableStateFlow(DEFAULT_COMPARISON_STORES)
    val comparisonStores: StateFlow<List<String>> = _comparisonStores

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

    // =========================
    // Catalog
    // =========================

// טוען את המוצרים מה-API החיצוני (במקום מ-Firebase)
val catalogUiState: StateFlow<UiState<List<ShoppingItem>>> = flow {
    try {
        android.util.Log.d("API_TEST", "מתחבר ל-API...")
        val products = RetrofitClient.api.getProducts()
        android.util.Log.d("API_TEST", "קיבלתי ${products.size} מוצרים מה-API")
        emit(UiState.Success(products) as UiState<List<ShoppingItem>>)
    } catch (e: Exception) {
        android.util.Log.e("API_TEST", "שגיאה: ${e.message}", e)
        emit(UiState.Error("שגיאה בחיבור ל-API: ${e.message}"))
    }
}.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = UiState.Loading
)
    // =========================
    // Shopping list - מבנה ישן
    // =========================

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

    // =========================
    // Shopping lists - מבנה חדש
    // =========================

    private val _shoppingListsUiState =
        MutableStateFlow<UiState<List<ShoppingList>>>(UiState.Loading)
    val shoppingListsUiState: StateFlow<UiState<List<ShoppingList>>> = _shoppingListsUiState

    private val _shoppingListActionState =
        MutableStateFlow<UiState<String>?>(null)
    val shoppingListActionState: StateFlow<UiState<String>?> = _shoppingListActionState

    private val _currentShoppingListId = MutableStateFlow<String?>(null)
    private val _currentShoppingListOwnerUid = MutableStateFlow<String?>(null)

    private val _currentShoppingListInfoUiState =
        MutableStateFlow<UiState<ShoppingList>?>(null)
    val currentShoppingListInfoUiState: StateFlow<UiState<ShoppingList>?> =
        _currentShoppingListInfoUiState

    val shoppingListItemsUiState: StateFlow<UiState<List<ShoppingItem>>> =
        _currentShoppingListId
            .flatMapLatest { listId ->
                if (listId.isNullOrBlank()) {
                    flowOf(UiState.Success(emptyList()))
                } else {
                    currentUidFlow.flatMapLatest { uid ->
                        if (uid.isBlank()) {
                            flowOf(UiState.Error("לא נמצא משתמש מחובר"))
                        } else {
                            val ownerUid = _currentShoppingListOwnerUid.value ?: uid

                            repository.getShoppingListItemsFlow(ownerUid, listId)
                                .map { items ->
                                    UiState.Success(items) as UiState<List<ShoppingItem>>
                                }
                                .catch { e ->
                                    emit(UiState.Error(e.message ?: "שגיאה בטעינת פריטי הרשימה"))
                                }
                        }
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading
            )

    private val _shoppingListItemsActionState =
        MutableStateFlow<UiState<String>?>(null)
    val shoppingListItemsActionState: StateFlow<UiState<String>?> = _shoppingListItemsActionState

    fun loadActiveShoppingLists() {
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
        if (uid.isBlank()) {
            _shoppingListsUiState.value = UiState.Error("לא נמצא משתמש מחובר")
            return
        }

        viewModelScope.launch {
            _shoppingListsUiState.value = UiState.Loading

            val result = repository.getActiveShoppingLists(uid)
            _shoppingListsUiState.value = result.fold(
                onSuccess = { lists -> UiState.Success(lists) },
                onFailure = { error -> UiState.Error(error.message ?: "שגיאה בטעינת רשימות") }
            )
        }
    }

    fun createShoppingList(name: String) {
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
        if (uid.isBlank()) {
            _shoppingListActionState.value = UiState.Error("לא נמצא משתמש מחובר")
            return
        }

        viewModelScope.launch {
            _shoppingListActionState.value = UiState.Loading

            val result = repository.createShoppingList(uid, name)
            _shoppingListActionState.value = result.fold(
                onSuccess = {
                    loadActiveShoppingLists()
                    UiState.Success("הרשימה נוצרה בהצלחה")
                },
                onFailure = { error ->
                    UiState.Error(error.message ?: "שגיאה ביצירת רשימה")
                }
            )
        }
    }

    fun updateShoppingListName(listId: String, newName: String) {
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
        if (uid.isBlank()) {
            _shoppingListActionState.value = UiState.Error("לא נמצא משתמש מחובר")
            return
        }

        if (newName.isBlank()) {
            _shoppingListActionState.value = UiState.Error("שם רשימה לא יכול להיות ריק")
            return
        }

        viewModelScope.launch {
            _shoppingListActionState.value = UiState.Loading

            val result = repository.updateShoppingListName(uid, listId, newName)
            _shoppingListActionState.value = result.fold(
                onSuccess = {
                    loadActiveShoppingLists()
                    UiState.Success("שם הרשימה עודכן")
                },
                onFailure = { error ->
                    UiState.Error(error.message ?: "שגיאה בעדכון שם הרשימה")
                }
            )
        }
    }

    fun archiveShoppingList(listId: String) {
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
        if (uid.isBlank()) {
            _shoppingListActionState.value = UiState.Error("לא נמצא משתמש מחובר")
            return
        }

        viewModelScope.launch {
            _shoppingListActionState.value = UiState.Loading

            val result = repository.archiveShoppingList(uid, listId)
            _shoppingListActionState.value = result.fold(
                onSuccess = {
                    loadActiveShoppingLists()
                    UiState.Success("הרשימה הועברה לארכיון")
                },
                onFailure = { error ->
                    UiState.Error(error.message ?: "שגיאה בהעברת הרשימה לארכיון")
                }
            )
        }
    }

    fun deleteShoppingList(listId: String) {
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
        if (uid.isBlank()) {
            _shoppingListActionState.value = UiState.Error("לא נמצא משתמש מחובר")
            return
        }

        viewModelScope.launch {
            _shoppingListActionState.value = UiState.Loading

            val result = repository.deleteShoppingList(uid, listId)
            _shoppingListActionState.value = result.fold(
                onSuccess = {
                    loadActiveShoppingLists()
                    UiState.Success("הרשימה נמחקה")
                },
                onFailure = { error ->
                    UiState.Error(error.message ?: "שגיאה במחיקת הרשימה")
                }
            )
        }
    }

    fun clearShoppingListActionState() {
        _shoppingListActionState.value = null
    }

    fun setCurrentShoppingList(listId: String) {
        val list = (_shoppingListsUiState.value as? UiState.Success)
            ?.data
            ?.firstOrNull { it.id == listId }

        val ownerUid = list?.ownerUid?.takeIf { it.isNotBlank() } ?: currentUid

        _currentShoppingListOwnerUid.value = ownerUid
        _currentShoppingListId.value = listId

        loadCurrentShoppingListInfo(listId, ownerUid)
    }

    private fun loadCurrentShoppingListInfo(listId: String, ownerUid: String? = null) {
        val uid = ownerUid ?: currentUid
        if (uid.isBlank()) {
            _currentShoppingListInfoUiState.value = UiState.Error("לא נמצא משתמש מחובר")
            return
        }

        viewModelScope.launch {
            _currentShoppingListInfoUiState.value = UiState.Loading

            val result = repository.getShoppingListById(uid, listId)
            _currentShoppingListInfoUiState.value = result.fold(
                onSuccess = { shoppingList -> UiState.Success(shoppingList) },
                onFailure = { error ->
                    UiState.Error(error.message ?: "שגיאה בטעינת פרטי הרשימה")
                }
            )
        }
    }

    fun addItemToCurrentShoppingList(listId: String, itemName: String, quantity: String) {
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
        if (uid.isBlank()) {
            _shoppingListItemsActionState.value = UiState.Error("לא נמצא משתמש מחובר")
            return
        }

        if (itemName.isBlank()) {
            _shoppingListItemsActionState.value = UiState.Error("שם פריט לא יכול להיות ריק")
            return
        }

        viewModelScope.launch {
            _shoppingListItemsActionState.value = UiState.Loading

            val result = repository.addItemToShoppingList(
                uid = uid,
                listId = listId,
                itemName = itemName.trim(),
                quantity = quantity
            )

            _shoppingListItemsActionState.value = result.fold(
                onSuccess = { UiState.Success("הפריט נוסף לרשימה") },
                onFailure = { error ->
                    UiState.Error(error.message ?: "שגיאה בהוספת פריט לרשימה")
                }
            )

            loadActiveShoppingLists()
            loadCurrentShoppingListInfo(listId)
        }
    }

    fun addCatalogItemToShoppingList(listId: String, item: ShoppingItem, quantity: String) {
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
        if (uid.isBlank()) {
            _shoppingListItemsActionState.value = UiState.Error("לא נמצא משתמש מחובר")
            return
        }

        viewModelScope.launch {
            _shoppingListItemsActionState.value = UiState.Loading

            val result = repository.addCatalogItemToShoppingList(
                uid = uid,
                listId = listId,
                catalogItem = item,
                quantity = quantity
            )

            _shoppingListItemsActionState.value = result.fold(
                onSuccess = { UiState.Success("המוצר נוסף לרשימה") },
                onFailure = { error ->
                    UiState.Error(error.message ?: "שגיאה בהוספת מוצר מהקטלוג")
                }
            )

            loadActiveShoppingLists()
            loadCurrentShoppingListInfo(listId)
        }
    }

    fun toggleItemCheckedInCurrentShoppingList(listId: String, item: ShoppingItem) {
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
        if (uid.isBlank()) {
            _shoppingListItemsActionState.value = UiState.Error("לא נמצא משתמש מחובר")
            return
        }

        viewModelScope.launch {
            val result = repository.updateItemCheckedInShoppingList(
                uid = uid,
                listId = listId,
                itemId = item.id,
                isChecked = !item.isChecked
            )

            _shoppingListItemsActionState.value = result.fold(
                onSuccess = { null },
                onFailure = { error ->
                    UiState.Error(error.message ?: "שגיאה בעדכון הפריט")
                }
            )

            loadActiveShoppingLists()
            loadCurrentShoppingListInfo(listId)
        }
    }

    fun updateItemQuantityInCurrentShoppingList(listId: String, itemId: String, quantity: String) {
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
        if (uid.isBlank()) {
            _shoppingListItemsActionState.value = UiState.Error("לא נמצא משתמש מחובר")
            return
        }

        viewModelScope.launch {
            val result = repository.updateItemQuantityInShoppingList(
                uid = uid,
                listId = listId,
                itemId = itemId,
                quantity = quantity
            )

            _shoppingListItemsActionState.value = result.fold(
                onSuccess = { UiState.Success("הכמות עודכנה") },
                onFailure = { error ->
                    UiState.Error(error.message ?: "שגיאה בעדכון כמות")
                }
            )

            loadActiveShoppingLists()
            loadCurrentShoppingListInfo(listId)
        }
    }

    fun deleteItemFromCurrentShoppingList(listId: String, itemId: String) {
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
        if (uid.isBlank()) {
            _shoppingListItemsActionState.value = UiState.Error("לא נמצא משתמש מחובר")
            return
        }

        viewModelScope.launch {
            val result = repository.deleteItemFromShoppingList(uid, listId, itemId)
            _shoppingListItemsActionState.value = result.fold(
                onSuccess = { UiState.Success("הפריט נמחק") },
                onFailure = { error ->
                    UiState.Error(error.message ?: "שגיאה במחיקת פריט")
                }
            )

            loadActiveShoppingLists()
            loadCurrentShoppingListInfo(listId)
        }
    }

    fun clearShoppingListItemsActionState() {
        _shoppingListItemsActionState.value = null
    }

    // =========================
    // Completed lists
    // =========================

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

    // =========================
    // Catalog actions
    // =========================

    fun addItem(name: String, quantity: String, description: String, category: String) {
        viewModelScope.launch {
            val newItem = ShoppingItem(
                title = name,
                quantity = quantity,
                description = description,
                category = category,
                timestamp = System.currentTimeMillis(),

                storePrices = listOf(
                    StorePrice("שופרסל", 7.2),
                    StorePrice("רמי לוי", 6.9),
                    StorePrice("ויקטורי", 7.5)
                )
            )
            repository.addItem(newItem)
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }

    // =========================
    // Old personal shopping list actions
    // =========================

    fun toggleItemInMyList(item: ShoppingItem) {
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
        if (uid.isBlank()) return

        viewModelScope.launch {
            repository.toggleItemInUserShoppingList(uid, item)
        }
    }

    fun togglePurchasedInMyList(item: ShoppingItem) {
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
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
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
        if (uid.isBlank()) return

        viewModelScope.launch {
            repository.removeFromUserShoppingList(uid, itemId)
        }
    }

    fun completeShoppingList(
        listName: String,
        items: List<ShoppingItem>,
        selectedStore: String,
        totalAmount: Double
    ) {
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
        if (uid.isBlank()) return

        viewModelScope.launch {
            repository.completeShoppingList(
                uid = uid,
                listName = listName,
                items = items,
                selectedStore = selectedStore,
                totalAmount = totalAmount
            )
        }
    }

    fun deleteCompletedList(listId: String) {
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
        if (uid.isBlank()) return

        viewModelScope.launch {
            repository.deleteCompletedList(uid, listId)
        }
    }

    fun calculateTotalPerStore(
        items: List<ShoppingItem>,
        stores: List<String>
    ): Map<String, Double> {
        return stores.associateWith { store ->
            items.sumOf { item ->
                val quantity = item.quantity.toDoubleOrNull() ?: 1.0

                val priceForStore = item.storePrices
                    .find { it.storeName == store }
                    ?.price ?: 0.0

                priceForStore * quantity
            }
        }
    }

    fun selectStoreForShoppingList(listId: String, storeName: String) {
        val uid = _currentShoppingListOwnerUid.value ?: currentUid
        if (uid.isBlank()) return

        viewModelScope.launch {
            repository.updateShoppingListSelectedStore(
                uid = uid,
                listId = listId,
                storeName = storeName
            )

            loadCurrentShoppingListInfo(listId, uid)
            loadActiveShoppingLists()
        }
    }

    fun shareListWithUser(listId: String, email: String) {
        val currentUserId = currentUid
        if (currentUserId.isBlank()) return

        viewModelScope.launch {
            try {
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

                val userQuery = db.collection("users")
                    .whereEqualTo("email", email.trim())
                    .get()
                    .await()

                if (userQuery.isEmpty) {
                    _shoppingListActionState.value = UiState.Error("לא נמצא משתמש עם המייל הזה")
                    return@launch
                }

                val targetUserId = userQuery.documents.first().id

                if (targetUserId == currentUserId) {
                    _shoppingListActionState.value = UiState.Error("לא ניתן לשתף רשימה עם עצמך")
                    return@launch
                }

                val list = (_shoppingListsUiState.value as? UiState.Success)
                    ?.data
                    ?.firstOrNull { it.id == listId }

                val ownerUid = list?.ownerUid?.takeIf { it.isNotBlank() } ?: currentUserId

                db.collection("users")
                    .document(ownerUid)
                    .collection("shopping_lists")
                    .document(listId)
                    .update(
                        mapOf(
                            "sharedWith" to com.google.firebase.firestore.FieldValue.arrayUnion(targetUserId),
                            "sharedByEmail" to (auth.currentUser?.email ?: "")
                        )
                    )
                    .await()

                _shoppingListActionState.value = UiState.Success("הרשימה שותפה בהצלחה")

            } catch (e: Exception) {
                _shoppingListActionState.value = UiState.Error("שגיאה בשיתוף הרשימה")
            }
        }
    }

    fun getCurrentUserId(): String = currentUid

    fun loadComparisonStores() {
        val uid = currentUid
        if (uid.isBlank()) {
            _comparisonStores.value = DEFAULT_COMPARISON_STORES
            return
        }

        viewModelScope.launch {
            try {
                val doc = firestore.collection("users").document(uid).get().await()
                val preferredStores =
                    (doc.get("preferredStores") as? List<*>)?.filterIsInstance<String>() ?: emptyList()

                _comparisonStores.value = getComparisonStores(preferredStores)
            } catch (e: Exception) {
                _comparisonStores.value = DEFAULT_COMPARISON_STORES
            }
        }
    }
}

