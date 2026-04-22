package com.shoply.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shoply.app.data.CompletedShoppingList
import com.shoply.app.ui.state.UiState
import com.shoply.app.ui.theme.ShoplySpacing
import com.shoply.app.viewmodel.ShoppingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CompletedListsScreen(
    viewModel: ShoppingViewModel,
    onListClick: ((CompletedShoppingList) -> Unit)? = null
) {
    val completedListsState by viewModel.completedListsUiState.collectAsStateWithLifecycle()

    when (val state = completedListsState) {
        is UiState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        is UiState.Success -> {
            val completedLists = state.data.sortedByDescending { it.completedAt }

            if (completedLists.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "אין עדיין רשימות שהושלמו",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(ShoplySpacing.medium),
                    verticalArrangement = Arrangement.spacedBy(ShoplySpacing.medium)
                ) {
                    item {
                        Text(
                            text = "ארכיון רשימות",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    items(completedLists, key = { it.id }) { completedList ->
                        CompletedListCard(
                            completedList = completedList,
                            onClick = {
                                onListClick?.invoke(completedList)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompletedListCard(
    completedList: CompletedShoppingList,
    onClick: () -> Unit
) {
    val purchasedCount = completedList.items.count { it.isChecked }
    val totalCount = completedList.items.size

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = completedList.selectedStore,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = formatCompletedDate(completedList.completedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "₪%.2f".format(completedList.totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "מוצרים: $totalCount",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "נרכשו: $purchasedCount",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "לחצי לצפייה בפרטי הרשימה",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatCompletedDate(timestamp: Long): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale("he")).format(Date(timestamp))
}