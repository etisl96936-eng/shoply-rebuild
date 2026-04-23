package com.shoply.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shoply.app.data.ShoppingItem
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

    val itemsState by viewModel.shoppingListItemsUiState.collectAsState()
    val actionState by viewModel.shoppingListItemsActionState.collectAsState()

    LaunchedEffect(listId) {
        viewModel.setCurrentShoppingList(listId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("פרטי רשימה") },
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
                        text = "כאן תוכלי לסמן, למחוק ולהוסיף פריטים לרשימה.",
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

            Button(
                onClick = {
                    if (newItemName.isNotBlank()) {
                        viewModel.addItemToCurrentShoppingList(listId, newItemName)
                        newItemName = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("הוסף פריט ידנית")
            }

            Text(
                text = "בשלב הבא נוסיף הוספה ישירה מהקטלוג אל הרשימה הזו.",
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

            when (val state = itemsState) {
                UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
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
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
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

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ShoppingListItemRow(
    item: ShoppingItem,
    onCheckedChange: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        text = "כמות: ${item.quantity}",
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
    }
}