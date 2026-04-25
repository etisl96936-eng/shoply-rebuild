package com.shoply.app.ui.screens

import android.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
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
import com.shoply.app.ui.state.UiState
import com.shoply.app.ui.theme.ShoplySpacing
import com.shoply.app.viewmodel.ShoppingViewModel
import androidx.compose.runtime.setValue
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TextButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: ShoppingViewModel
) {
    val completedListsState by viewModel.completedListsUiState.collectAsStateWithLifecycle()
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

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
            val startBoundary = startDate?.let { pickerUtcMillisToLocalStartOfDay(it) }
            val endBoundary = endDate?.let { pickerUtcMillisToLocalEndOfDay(it) }

            val filteredLists = completedLists.filter { list ->
                val afterStart = startBoundary?.let { list.completedAt >= it } ?: true
                val beforeEnd = endBoundary?.let { list.completedAt <= it } ?: true
                afterStart && beforeEnd
            }

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
                                    Text(startDate?.let { formatTimestamp(it) } ?: "מתאריך")
                                }

                                OutlinedButton(
                                    onClick = { showEndDatePicker = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(endDate?.let { formatTimestamp(it) } ?: "עד תאריך")
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

                if (filteredLists.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "אין רשימות בטווח התאריכים שנבחר",
                                    style = MaterialTheme.typography.bodyLarge
                                )

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
                } else {
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
                            val maxStoreTotal = totalsByStore.values.maxOrNull() ?: 1.0

                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                totalsByStore.forEach { (store, total) ->
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(store)
                                            Text("₪%.2f".format(total))
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        LinearProgressIndicator(
                                            progress = (total / maxStoreTotal).toFloat(),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        SectionCard(title = "פילוח לפי קטגוריות") {

                            CategoryPieChart(
                                categoryTotals = categoryCounts.mapValues { it.value.toDouble() }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            val maxCategoryCount = categoryCounts.values.maxOrNull() ?: 1

                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                categoryCounts.forEach { (category, count) ->
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(category)
                                            Text("$count מוצרים")
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        LinearProgressIndicator(
                                            progress = count.toFloat() / maxCategoryCount.toFloat(),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
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



    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDate
        )

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
                TextButton(
                    onClick = { showStartDatePicker = false }
                ) {
                    Text("ביטול")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDate
        )

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
                TextButton(
                    onClick = { showEndDatePicker = false }
                ) {
                    Text("ביטול")
                }
            }
        ) {
            DatePicker(state = datePickerState)
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
@Composable
private fun CategoryPieChart(
    categoryTotals: Map<String, Double>
) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                setUsePercentValues(true)
                setEntryLabelTextSize(12f)
                legend.isEnabled = true
                animateY(1000)
            }
        },
        update = { chart ->
            val entries = categoryTotals.map { (category, total) ->
                PieEntry(total.toFloat(), category)
            }

            val dataSet = PieDataSet(entries, "פילוח לפי קטגוריות")

            dataSet.valueTextSize = 12f
            dataSet.colors = listOf(
                Color.rgb(76, 175, 80),
                Color.rgb(33, 150, 243),
                Color.rgb(255, 193, 7),
                Color.rgb(244, 67, 54),
                Color.rgb(156, 39, 176)
            )

            chart.data = PieData(dataSet)
            chart.invalidate()
        }
    )
}