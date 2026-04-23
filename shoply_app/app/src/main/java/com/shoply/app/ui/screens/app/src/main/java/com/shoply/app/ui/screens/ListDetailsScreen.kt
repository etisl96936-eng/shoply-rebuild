package com.shoply.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shoply.app.data.ShoppingItem
import com.shoply.app.data.ShoppingList
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
    val actionState by viewModel.shoppingListItemsActionState.collectAsState()

    LaunchedEffect(listId) {
        viewModel.setCurrentShoppingList(listId)
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
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "חזרה"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "פריטי הרשימה",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "כאן אפשר להוסיף פריטים ידנית, לשנות כמות, לסמן ולמחוק.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = newItemName,
                onValueChange = { newItemName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("שם פריט חדש") },
                singleLine = true
            )

            OutlinedTextField(
                value = newItemQuantity,
                onValueChange = { newValue ->
                    newItemQuantity = newValue.filter { it.isDigit() }.ifBlank { "" }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("כמות") },
                singleLine = true
            )

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
                Text("הוסף פריט ידנית")
            }

            Text(
                text = "מהקטלוג אפשר להוסיף לרשימה הזאת דרך המסך הראשי לאחר בחירתה כרשימה פעילה.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            when (val state = actionState) {
                is UiState.Error -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = state.message,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                is UiState.Success -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = state.data,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                UiState.Loading -> {
                    CircularProgressIndicator()
                }

                null -> Unit
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (val state = itemsState) {
                    UiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is UiState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is UiState.Success -> {
                        if (state.data.isEmpty()) {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = "אין עדיין פריטים ברשימה הזו",
                                    modifier = Modifier.padding(20.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.data, key = { it.id }) { item ->
                                    ShoppingListItemRow(
                                        item = item,
                                        onCheckedChange = {
                                            viewModel.toggleItemCheckedInCurrentShoppingList(listId, item)
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
                                        onDelete = {
                                            viewModel.deleteItemFromCurrentShoppingList(listId, item.id)
                                        }
                                    )
                                }

                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
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
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = { onCheckedChange() }
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = if (item.isChecked) {
                                TextDecoration.LineThrough
                            } else {
                                TextDecoration.None
                            }
                        )

                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "מחיקת פריט"
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "כמות:",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedButton(onClick = onDecreaseQuantity) {
                    Text("-")
                }

                Text(
                    text = item.quantity,
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedButton(onClick = onIncreaseQuantity) {
                    Text("+")
                }
            }
        }
    }
}