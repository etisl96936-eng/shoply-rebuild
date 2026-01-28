package com.shoply.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.shoply.app.ui.state.UiState
import com.shoply.app.viewmodel.ShoppingViewModel
import com.shoply.app.data.ShoppingItem
import com.shoply.app.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: ShoppingViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // מצבי ניהול הדיאלוגים
    var showAddDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<ShoppingItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Shoply - הרשימות שלי") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "הוסף מוצר")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val s = state) {
                is UiState.Loading -> {
                    LoadingView(message = "טוען את הרשימה שלך...")
                }
                is UiState.Error -> {
                    ErrorView(
                        message = s.message,
                        onRetry = { /* אופציונלי: קריאה לטעינה מחדש */ }
                    )
                }
                is UiState.Success -> {
                    val shoppingItems = s.data

                    if (shoppingItems.isEmpty()) {
                        Text(
                            text = "אין פריטים ברשימה. לחצי על ה-+ להוספה",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(shoppingItems) { item ->
                                ShoplyCard {
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = item.title,
                                                style = if (item.isChecked)
                                                    MaterialTheme.typography.bodyLarge.copy(
                                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                                    )
                                                else
                                                    MaterialTheme.typography.bodyLarge
                                            )
                                        },
                                        supportingContent = { Text("${item.quantity} | ${item.category}") },
                                        leadingContent = {
                                            Checkbox(
                                                checked = item.isChecked,
                                                onCheckedChange = { viewModel.toggleItemChecked(item) }
                                            )
                                        },
                                        trailingContent = {
                                            IconButton(onClick = { itemToDelete = item }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "מחק",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        },
                                        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1. דיאלוג הוספת פריט
        if (showAddDialog) {
            AddItemDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, quantity, description, category ->
                    viewModel.addItem(name, quantity, description, category)
                    showAddDialog = false
                }
            )
        }

        // 2. דיאלוג אישור מחיקה (שימוש בקומפוננטה של אתי)
        itemToDelete?.let { item ->
            ShoplyAlertDialog(
                title = "מחיקת מוצר",
                message = "האם את בטוחה שברצונך למחוק את '${item.title}'?",
                confirmText = "מחק",
                dismissText = "ביטול",
                onConfirm = {
                    viewModel.deleteItem(item.id)
                    itemToDelete = null
                },
                onDismiss = { itemToDelete = null }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var description by remember { mutableStateOf("") }

    val categories = listOf("פירות וירקות", "מוצרי חלב וביצים", "ניקיון והיגיינה", "מאפה ודגנים", "שימורים ומזווה", "בשר ודגים", "מוצרי מקפיא", "אחר")
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("הוספת מוצר חדש", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                ShoplyTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "שם המוצר *"
                )
                Spacer(modifier = Modifier.height(8.dp))

                ShoplyTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = "כמות"
                )
                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("בחר קטגוריה") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                ShoplyTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "תיאור (אופציונלי)"
                )
            }
        },
        confirmButton = {
            ShoplyButton(
                text = "הוסף",
                onClick = { if (name.isNotBlank()) onConfirm(name, quantity, description, selectedCategory) }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ביטול")
            }
        }
    )
}