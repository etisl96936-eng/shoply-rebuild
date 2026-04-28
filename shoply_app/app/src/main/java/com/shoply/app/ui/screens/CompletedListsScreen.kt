package com.shoply.app.ui.screens

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shoply.app.data.CompletedShoppingList
import com.shoply.app.ui.state.UiState
import com.shoply.app.ui.theme.ShoplySpacing
import com.shoply.app.viewmodel.ShoppingViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.filled.ArrowForward

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedListsScreen(
    viewModel: ShoppingViewModel,
    onListClick: ((CompletedShoppingList) -> Unit)? = null,
    onBackClick: () -> Unit = {}
) {
    val completedListsState by viewModel.completedListsUiState.collectAsStateWithLifecycle()

    var selectedList by remember { mutableStateOf<CompletedShoppingList?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ארכיון רשימות") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "חזרה"
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (val state = completedListsState) {
            is UiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
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
                        .padding(padding)
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

                val startBoundary = startDate?.let { pickerUtcMillisToLocalStartOfDay(it) }
                val endBoundary = endDate?.let { pickerUtcMillisToLocalEndOfDay(it) }

                val filteredLists = completedLists.filter { list ->
                    val afterStart = startBoundary?.let { list.completedAt >= it } ?: true
                    val beforeEnd = endBoundary?.let { list.completedAt <= it } ?: true
                    afterStart && beforeEnd
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(ShoplySpacing.medium),
                    verticalArrangement = Arrangement.spacedBy(ShoplySpacing.medium)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "טווח תאריכים",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(ShoplySpacing.small)
                                ) {
                                    OutlinedButton(
                                        onClick = { showStartDatePicker = true },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(startDate?.let { formatCompletedDate(it) } ?: "מתאריך")
                                    }

                                    OutlinedButton(
                                        onClick = { showEndDatePicker = true },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(endDate?.let { formatCompletedDate(it) } ?: "עד תאריך")
                                    }
                                }

                                if (startDate != null || endDate != null) {
                                    TextButton(
                                        onClick = {
                                            startDate = null
                                            endDate = null
                                        }
                                    ) {
                                        Text("נקה סינון")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "סה\"כ רשימות בארכיון: ${filteredLists.size}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (completedLists.isEmpty()) {
                        item {
                            Text("אין עדיין רשימות שהושלמו")
                        }
                    } else if (filteredLists.isEmpty()) {
                        item {
                            Text("אין רשימות בטווח התאריכים שנבחר")
                        }
                    } else {
                        items(filteredLists, key = { it.id }) { completedList ->
                            CompletedListCard(
                                completedList = completedList,
                                onClick = { onListClick?.invoke(completedList) },
                                onLongClick = {
                                    selectedList = completedList
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && selectedList != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                selectedList = null
            },
            title = { Text("מחיקת רשימה") },
            text = { Text("האם הנך מעוניין להסיר את הרשימה מ-Shoply?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCompletedList(selectedList!!.id)
                        showDeleteDialog = false
                        selectedList = null
                    }
                ) {
                    Text("כן")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        selectedList = null
                    }
                ) {
                    Text("לא")
                }
            }
        )
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = startDate)

        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        startDate = datePickerState.selectedDateMillis
                        showStartDatePicker = false
                    }
                ) {
                    Text("אישור")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("ביטול")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = endDate)

        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        endDate = datePickerState.selectedDateMillis
                        showEndDatePicker = false
                    }
                ) {
                    Text("אישור")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("ביטול")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CompletedListCard(
    completedList: CompletedShoppingList,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val purchasedCount = completedList.items.count { it.isChecked }
    val totalCount = completedList.items.size

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            ),
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
                        text = completedList.name.ifBlank { "רשימה ללא שם" },
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = completedList.selectedStore,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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


        }
    }
}

private fun formatCompletedDate(timestamp: Long): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale("he")).format(Date(timestamp))
}


private fun pickerUtcMillisToLocalStartOfDay(millis: Long): Long {
    val utcCalendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = millis
    }

    val year = utcCalendar.get(java.util.Calendar.YEAR)
    val month = utcCalendar.get(java.util.Calendar.MONTH)
    val day = utcCalendar.get(java.util.Calendar.DAY_OF_MONTH)

    return java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.YEAR, year)
        set(java.util.Calendar.MONTH, month)
        set(java.util.Calendar.DAY_OF_MONTH, day)
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun pickerUtcMillisToLocalEndOfDay(millis: Long): Long {
    val utcCalendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = millis
    }

    val year = utcCalendar.get(java.util.Calendar.YEAR)
    val month = utcCalendar.get(java.util.Calendar.MONTH)
    val day = utcCalendar.get(java.util.Calendar.DAY_OF_MONTH)

    return java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.YEAR, year)
        set(java.util.Calendar.MONTH, month)
        set(java.util.Calendar.DAY_OF_MONTH, day)
        set(java.util.Calendar.HOUR_OF_DAY, 23)
        set(java.util.Calendar.MINUTE, 59)
        set(java.util.Calendar.SECOND, 59)
        set(java.util.Calendar.MILLISECOND, 999)
    }.timeInMillis
}