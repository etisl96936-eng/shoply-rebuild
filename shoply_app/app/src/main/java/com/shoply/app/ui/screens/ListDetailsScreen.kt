package com.shoply.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shoply.app.data.ShoppingItem
import com.shoply.app.data.ShoppingList
import com.shoply.app.data.StorePrice
import com.shoply.app.ui.state.UiState
import com.shoply.app.viewmodel.ShoppingViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.ArrowForward


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailsScreen(
    listId: String,
    navController: NavHostController,
    viewModel: ShoppingViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val actionState by viewModel.shoppingListActionState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var newItemQuantity by remember { mutableStateOf("1") }
    var showSelectStoreDialog by remember { mutableStateOf(false) }

    val listInfoState by viewModel.currentShoppingListInfoUiState.collectAsState()
    val itemsState by viewModel.shoppingListItemsUiState.collectAsState()
    var showFinishDialog by remember { mutableStateOf(false) }
    var lastItemToComplete by remember { mutableStateOf<ShoppingItem?>(null) }
    var showShareDialog by remember { mutableStateOf(false) }
    var shareEmail by remember { mutableStateOf("") }

    LaunchedEffect(listId) {
        viewModel.setCurrentShoppingList(listId)
    }
    LaunchedEffect(actionState) {
        when (val state = actionState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar(state.data)
                viewModel.clearShoppingListActionState()
            }

            is UiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.clearShoppingListActionState()
            }

            else -> Unit
        }
    }

    val items = (itemsState as? UiState.Success)?.data ?: emptyList()
    val filteredItems = if (searchQuery.isBlank()) {
        items
    } else {
        val query = searchQuery.trim()

        val startsWithMatches = items.filter { item ->
            item.title.startsWith(query, ignoreCase = true)
        }

        val containsMatches = items.filter { item ->
            !item.title.startsWith(query, ignoreCase = true) &&
                    (
                            item.title.contains(query, ignoreCase = true) ||
                                    item.category.contains(query, ignoreCase = true)
                            )
        }

        startsWithMatches + containsMatches
    }

    val stores = listOf("שופרסל", "רמי לוי", "ויקטורי")

    val totals = stores.associateWith { store ->
        var total = 0.0

        items.forEach { item ->
            val quantity = item.quantity.toDoubleOrNull() ?: 1.0
            val price = item.storePrices
                .find { it.storeName == store }
                ?.price ?: 0.0

            total += price * quantity
        }

        total
    }
    val selectedStore = (listInfoState as? UiState.Success)?.data?.selectedStore

    if (showFinishDialog && lastItemToComplete != null) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("סיום קנייה") },
            text = { Text("האם ברצונך לסיים את הקנייה ולהעביר לארכיון?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFinishDialog = false

                        val item = lastItemToComplete!!

                        viewModel.toggleItemCheckedInCurrentShoppingList(listId, item)

                        val total = totals[selectedStore] ?: 0.0
                        val listName = (listInfoState as? UiState.Success)?.data?.name ?: ""
                        viewModel.completeShoppingList(
                            listName = listName,

                            items = items.map {
                                if (it.id == item.id) it.copy(isChecked = true) else it
                            },
                            selectedStore = selectedStore!!,
                            totalAmount = total
                        )

                        viewModel.archiveShoppingList(listId)

                        navController.popBackStack()
                    }
                ) {
                    Text("כן")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showFinishDialog = false
                    }
                ) {
                    Text("לא")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = when (val state = listInfoState) {
                        is UiState.Success<ShoppingList> -> state.data.name
                        else -> "פרטי רשימה"
                    }
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = "חזרה"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showShareDialog = true }) {
                        Icon(Icons.Default.Share, contentDescription = "שיתוף")
                    }
                }

            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "פריטי הרשימה",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    stores.forEach { store ->

                        val storeTotal = totals[store] ?: 0.0
                        val sortedTotals = totals.values.sorted()
                        val cheapest = sortedTotals.firstOrNull()
                        val mostExpensive = sortedTotals.lastOrNull()

                        val storeColor = when {
                            storeTotal == cheapest -> androidx.compose.ui.graphics.Color(0xFFDFF5E1)
                            storeTotal == mostExpensive -> androidx.compose.ui.graphics.Color(0xFFFFE0E0)
                            else -> androidx.compose.ui.graphics.Color(0xFFFFF1D6)
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = storeColor
                            ),
                            onClick = {
                                val newSelectedStore = if (selectedStore == store) "" else store

                                viewModel.selectStoreForShoppingList(
                                    listId = listId,
                                    storeName = newSelectedStore
                                )
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(store)
                                Text("₪${"%.2f".format(storeTotal)}")

                                if (selectedStore == store) {
                                    Text(
                                        text = "נבחר",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("חיפוש פריט ברשימה") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            when (val state = itemsState) {
                UiState.Loading -> {
                    item { CircularProgressIndicator() }
                }

                is UiState.Error -> {
                    item {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        item { Text("אין עדיין פריטים ברשימה") }
                    } else {
                        items(filteredItems, key = { it.id }) { item ->
                            ShoppingListItemRow(
                                item = item,
                                onCheckedChange = {
                                    val uncheckedItems = items.filter { !it.isChecked }
                                    val isLastUncheckedItem =
                                        uncheckedItems.size == 1 && uncheckedItems.first().id == item.id

                                    if (isLastUncheckedItem) {
                                        if (selectedStore.isNullOrBlank()) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("יש לבחור את הסופר בו התבצעה הקנייה")
                                            }
                                            return@ShoppingListItemRow
                                        }

                                        // לא מסמנים עדיין!
                                        lastItemToComplete = item
                                        showFinishDialog = true
                                        return@ShoppingListItemRow
                                    }

                                    viewModel.toggleItemCheckedInCurrentShoppingList(listId, item)
                                },
                                onDelete = {
                                    viewModel.deleteItemFromCurrentShoppingList(listId, item.id)
                                },
                                        onIncreaseQuantity = {
                                    val current = item.quantity.toIntOrNull()?.coerceAtLeast(1) ?: 1
                                    viewModel.updateItemQuantityInCurrentShoppingList(
                                        listId = listId,
                                        itemId = item.id,
                                        quantity = (current + 1).toString()
                                    )
                                },
                                onDecreaseQuantity = {
                                    val current = item.quantity.toIntOrNull()?.coerceAtLeast(1) ?: 1
                                    val next = (current - 1).coerceAtLeast(1)
                                    viewModel.updateItemQuantityInCurrentShoppingList(
                                        listId = listId,
                                        itemId = item.id,
                                        quantity = next.toString()
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showShareDialog) {
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = { Text("שיתוף רשימה") },
            text = {
                TextField(
                    value = shareEmail,
                    onValueChange = { shareEmail = it },
                    label = { Text("אימייל משתמש") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.shareListWithUser(listId, shareEmail)
                    showShareDialog = false
                    shareEmail = ""
                }) {
                    Text("שתף")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showShareDialog = false
                }) {
                    Text("ביטול")
                }
            }
        )
    }
}

@Composable
private fun ShoppingListItemRow(
    item: ShoppingItem,
    onCheckedChange: () -> Unit,
    onDelete: () -> Unit,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = { onCheckedChange() }
            )

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = item.title,
                    textDecoration = if (item.isChecked) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
                )

                Text(item.category)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("כמות: ${item.quantity}")

                    OutlinedButton(onClick = onDecreaseQuantity) {
                        Text("-")
                    }

                    OutlinedButton(onClick = onIncreaseQuantity) {
                        Text("+")
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "מחיקה")
            }
        }
    }
}