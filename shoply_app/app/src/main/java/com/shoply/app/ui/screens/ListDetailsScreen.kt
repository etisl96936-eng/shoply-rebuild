package com.shoply.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailsScreen(
    listId: String,
    navController: NavHostController,
    viewModel: ShoppingViewModel
) {
    var newItemName by remember { mutableStateOf("") }
    var newItemQuantity by remember { mutableStateOf("1") }

    val listInfoState by viewModel.currentShoppingListInfoUiState.collectAsState()
    val itemsState by viewModel.shoppingListItemsUiState.collectAsState()

    LaunchedEffect(listId) {
        viewModel.setCurrentShoppingList(listId)
    }

    val items = (itemsState as? UiState.Success)?.data ?: emptyList()
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "חזרה")
                    }
                }
            )
        }
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
                        Card(modifier = Modifier.weight(1f)) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(store)
                                Text("₪${"%.2f".format(totals[store] ?: 0.0)}")
                            }
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("שם פריט") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = newItemQuantity,
                    onValueChange = { newItemQuantity = it.filter { char -> char.isDigit() } },
                    label = { Text("כמות") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Button(
                    onClick = {
                        if (newItemName.isNotBlank()) {
                            viewModel.addItemToCurrentShoppingList(
                                listId = listId,
                                itemName = newItemName,
                                quantity = if (newItemQuantity.isBlank()) "1" else newItemQuantity
                            )
                            newItemName = ""
                            newItemQuantity = "1"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("הוסף פריט")
                }
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
                        items(state.data, key = { it.id }) { item ->
                            ShoppingListItemRow(
                                item = item,
                                onCheckedChange = {
                                    viewModel.toggleItemCheckedInCurrentShoppingList(listId, item)
                                },
                                onDelete = {
                                    viewModel.deleteItemFromCurrentShoppingList(listId, item.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShoppingListItemRow(
    item: ShoppingItem,
    onCheckedChange: () -> Unit,
    onDelete: () -> Unit
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
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "מחיקה")
            }
        }
    }
}