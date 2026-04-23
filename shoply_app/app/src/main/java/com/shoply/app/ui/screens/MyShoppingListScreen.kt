package com.shoply.app.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shoply.app.data.ShoppingList
import com.shoply.app.ui.state.UiState
import com.shoply.app.viewmodel.ShoppingViewModel

@Composable
fun MyShoppingListScreen(
    viewModel: ShoppingViewModel
) {
    val shoppingListsState by viewModel.shoppingListsUiState.collectAsState()
    val actionState by viewModel.shoppingListActionState.collectAsState()

    var newListName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadActiveShoppingLists()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "הרשימות שלי",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = newListName,
            onValueChange = { newListName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("שם רשימה חדשה") },
            singleLine = true
        )

        Button(
            onClick = {
                viewModel.createShoppingList(newListName)
                newListName = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("צור רשימה")
        }

        when (val state = actionState) {
            is UiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            is UiState.Success -> {
                Text(
                    text = state.data,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            UiState.Loading -> {
                CircularProgressIndicator()
            }
            null -> Unit
        }

        when (val state = shoppingListsState) {
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
                    Text("עדיין אין רשימות פעילות")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data, key = { it.id }) { shoppingList ->
                            ShoppingListCard(
                                shoppingList = shoppingList,
                                onArchive = {
                                    viewModel.archiveShoppingList(shoppingList.id)
                                },
                                onDelete = {
                                    viewModel.deleteShoppingList(shoppingList.id)
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
private fun ShoppingListCard(
    shoppingList: ShoppingList,
    onArchive: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = shoppingList.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "סטטוס: ${shoppingList.status}",
                style = MaterialTheme.typography.bodyMedium
            )

            shoppingList.selectedStore?.let { store ->
                Text(
                    text = "חנות נבחרת: $store",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TextButton(onClick = onArchive) {
                    Text("העבר לארכיון")
                }

                TextButton(onClick = onDelete) {
                    Text("מחק רשימה")
                }
            }
        }
    }
}