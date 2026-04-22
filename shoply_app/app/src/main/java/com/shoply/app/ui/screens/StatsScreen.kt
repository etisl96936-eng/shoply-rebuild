package com.shoply.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shoply.app.data.CompletedShoppingList
import com.shoply.app.ui.state.UiState
import com.shoply.app.ui.theme.ShoplySpacing
import com.shoply.app.viewmodel.ShoppingViewModel
import androidx.compose.runtime.setValue

@Composable
fun StatsScreen(
    viewModel: ShoppingViewModel
) {
    val completedListsState by viewModel.completedListsUiState.collectAsStateWithLifecycle()
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }

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
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        is UiState.Success -> {
            val completedLists = state.data
            val filteredLists = completedLists.filter { list ->
                val afterStart = startDate?.let { list.completedAt >= it } ?: true
                val beforeEnd = endDate?.let { list.completedAt <= it } ?: true
                afterStart && beforeEnd
            }

            if (filteredLists.isEmpty()) {
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
                val totalLists = filteredLists.size
                val totalSpent = filteredLists.sumOf { it.totalAmount }

                val totalsByStore = filteredLists
                    .groupBy { it.selectedStore }
                    .mapValues { (_, lists) -> lists.sumOf { it.totalAmount } }

                val purchasesCountByStore = filteredLists
                    .groupBy { it.selectedStore }
                    .mapValues { (_, lists) -> lists.size }

                val categoryCounts = filteredLists
                    .flatMap { it.items }
                    .groupBy { it.category }
                    .mapValues { (_, items) -> items.size }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(ShoplySpacing.medium),
                    verticalArrangement = Arrangement.spacedBy(ShoplySpacing.medium)
                ) {
                    item {
                        Text(
                            text = "סטטיסטיקות",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("טווח תאריכים:")
                                Text("הכל")
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "💰",
                                    style = MaterialTheme.typography.headlineLarge
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "סה\"כ הוצאות",
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    Text(
                                        text = "₪%.2f".format(totalSpent),
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    item {
                        SectionCard(title = "פילוח לפי סופרים") {
                            totalsByStore.forEach { (store, total) ->
                                val count = purchasesCountByStore[store] ?: 0

                                StatsRow(
                                    title = store,
                                    subtitle = "$count רשימות",
                                    value = "₪%.2f".format(total)
                                )
                            }
                        }
                    }

                    item {
                        SectionCard(title = "פילוח לפי קטגוריות") {
                            categoryCounts.forEach { (category, count) ->
                                StatsRow(
                                    title = category,
                                    subtitle = null,
                                    value = "$count"
                                )
                            }
                        }
                    }

                    item {
                        SectionCard(title = "רשימות שהושלמו") {
                            filteredLists.forEachIndexed { index, list ->
                                StatsRow(
                                    title = list.selectedStore,
                                    subtitle = formatTimestamp(list.completedAt),
                                    value = "₪%.2f".format(list.totalAmount)
                                )

                                if (index != filteredLists.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
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
private fun StatsSummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
private fun StatsRow(
    title: String,
    subtitle: String?,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("he"))
    return formatter.format(java.util.Date(timestamp))
}