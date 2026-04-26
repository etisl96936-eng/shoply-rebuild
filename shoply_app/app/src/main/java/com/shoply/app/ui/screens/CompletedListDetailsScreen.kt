package com.shoply.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.shoply.app.data.CompletedShoppingList
import com.shoply.app.data.ShoppingItem
import com.shoply.app.ui.state.UiState
import com.shoply.app.viewmodel.ShoppingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CompletedListDetailsScreen(
    listId: String,
    navController: NavController,
    viewModel: ShoppingViewModel
) {
    val completedListsState by viewModel.completedListsUiState.collectAsStateWithLifecycle()

    when (val state = completedListsState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(state.message)
            }
        }

        is UiState.Success -> {
            val completedList = state.data.find { it.id == listId }

            if (completedList == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("הרשימה לא נמצאה")
                }
            } else {
                CompletedListDetailsContent(
                    completedList = completedList,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompletedListDetailsContent(
    completedList: CompletedShoppingList,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("פרטי רשימה") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("חזרה")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = completedList.name.ifBlank { "רשימה ללא שם" },
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("סופר: ${completedList.selectedStore.ifBlank { "לא נבחר" }}")
                        Text("תאריך קנייה: ${formatCompletedDate(completedList.completedAt)}")
                        Text("סה״כ: ₪%.2f".format(completedList.totalAmount))
                        Text("מספר מוצרים: ${completedList.items.size}")
                    }
                }
            }

            item {
                Text(
                    text = "מוצרים שנקנו",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (completedList.items.isEmpty()) {
                item {
                    Text("אין פריטים ברשימה")
                }
            } else {
                items(completedList.items) { item ->
                    CompletedShoppingItemCard(
                        item = item,
                        selectedStore = completedList.selectedStore
                    )
                }
            }
        }
    }
}

@Composable
private fun CompletedShoppingItemCard(
    item: ShoppingItem,
    selectedStore: String
) {
    val purchasedPrice = item.storePrices
        .firstOrNull { it.storeName == selectedStore }
        ?.price

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = item.title.ifBlank { "פריט ללא שם" },
                style = MaterialTheme.typography.titleMedium
            )

            Text("כמות: ${item.quantity}")
            Text("קטגוריה: ${item.category}")

            Text(
                text = if (purchasedPrice != null) {
                    "מחיר ב$selectedStore: ₪%.2f".format(purchasedPrice)
                } else {
                    "מחיר: לא זמין"
                }
            )
        }
    }
}

private fun formatCompletedDate(timestamp: Long): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale("he")).format(Date(timestamp))
}