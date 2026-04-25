package com.shoply.app.ui.screens

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.shoply.app.data.CompletedShoppingList
import com.shoply.app.data.ShoppingItem
import com.shoply.app.ui.state.UiState
import com.shoply.app.ui.theme.ShoplySpacing
import com.shoply.app.viewmodel.ShoppingViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
            val averageListAmount = if (totalLists > 0) totalSpent / totalLists else 0.0

            val totalsByStore = filteredLists
                .groupBy { it.selectedStore.ifBlank { "לא נבחר סופר" } }
                .mapValues { (_, lists) -> lists.sumOf { it.totalAmount } }

            val categoryCounts = filteredLists
                .flatMap { it.items }
                .groupBy { it.category.ifBlank { "כללי" } }
                .mapValues { (_, items) -> items.size }

            val categoryTotals = filteredLists
                .flatMap { list ->
                    list.items.map { item ->
                        val category = item.category.ifBlank { "כללי" }
                        val quantity = item.quantity.toDoubleOrNull() ?: 1.0
                        val price = getSelectedStoreItemPrice(item, list.selectedStore)
                        category to price * quantity
                    }
                }
                .groupBy({ it.first }, { it.second })
                .mapValues { (_, values) -> values.sum() }
                .filterValues { it > 0.0 }

            val topCategory = categoryTotals.maxByOrNull { it.value }?.key
                ?: categoryCounts.maxByOrNull { it.value }?.key
                ?: "אין נתונים"

            val highestList = filteredLists.maxByOrNull { it.totalAmount }

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

                                Text(
                                    text = "כדי לראות סטטיסטיקות, השלימי רשימת קניות או הרחיבי את טווח התאריכים.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(ShoplySpacing.small)
                        ) {
                            StatsSummaryCard(
                                title = "סה\"כ",
                                value = formatCurrency(totalSpent),
                                modifier = Modifier.weight(1f)
                            )

                            StatsSummaryCard(
                                title = "רשימות",
                                value = totalLists.toString(),
                                modifier = Modifier.weight(1f)
                            )

                            StatsSummaryCard(
                                title = "ממוצע",
                                value = formatCurrency(averageListAmount),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        SectionCard(title = "תובנות מהירות") {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "הקטגוריה המובילה שלך היא: $topCategory",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                highestList?.let { list ->
                                    Text(
                                        text = "הרשימה היקרה ביותר הייתה ב-${formatTimestamp(list.completedAt)} בסכום ${formatCurrency(list.totalAmount)}",
                                        style = MaterialTheme.typography.bodyMedium
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
                                            Text(formatCurrency(total))
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
                            val pieChartData = if (categoryTotals.isNotEmpty()) {
                                categoryTotals
                            } else {
                                categoryCounts.mapValues { it.value.toDouble() }
                            }

                            CategoryPieChart(
                                categoryTotals = pieChartData
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
                            CompletedListsBarChart(
                                lists = filteredLists
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            filteredLists.forEachIndexed { index, list ->
                                StatsRow(
                                    title = list.selectedStore.ifBlank { "לא נבחר סופר" },
                                    subtitle = formatTimestamp(list.completedAt),
                                    value = formatCurrency(list.totalAmount)
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
                setNoDataText("אין נתוני קטגוריות להצגה")
                animateY(1000)
            }
        },
        update = { chart ->
            if (categoryTotals.isEmpty() || categoryTotals.values.all { it <= 0.0 }) {
                chart.clear()
                chart.invalidate()
                return@AndroidView
            }

            val entries = categoryTotals.map { (category, total) ->
                PieEntry(total.toFloat(), category)
            }

            val dataSet = PieDataSet(entries, "פילוח לפי קטגוריות").apply {
                valueTextSize = 12f
                colors = listOf(
                    AndroidColor.rgb(76, 175, 80),
                    AndroidColor.rgb(33, 150, 243),
                    AndroidColor.rgb(255, 193, 7),
                    AndroidColor.rgb(244, 67, 54),
                    AndroidColor.rgb(156, 39, 176),
                    AndroidColor.rgb(0, 150, 136),
                    AndroidColor.rgb(255, 87, 34)
                )
            }

            chart.data = PieData(dataSet)
            chart.invalidate()
        }
    )
}

@Composable
private fun CompletedListsBarChart(
    lists: List<CompletedShoppingList>
) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setFitBars(true)
                setNoDataText("אין רשימות שהושלמו להצגה")
                animateY(1000)

                axisRight.isEnabled = false
                axisLeft.valueFormatter = CurrencyAxisFormatter()

                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                xAxis.labelRotationAngle = -30f
            }
        },
        update = { chart ->
            if (lists.isEmpty()) {
                chart.clear()
                chart.invalidate()
                return@AndroidView
            }

            val entries = lists.mapIndexed { index, list ->
                BarEntry(index.toFloat(), list.totalAmount.toFloat())
            }

            val labels = lists.map { list ->
                formatShortDate(list.completedAt)
            }

            val dataSet = BarDataSet(entries, "סכום לפי רשימה").apply {
                valueTextSize = 12f
                valueFormatter = CurrencyValueFormatter()
                colors = listOf(AndroidColor.rgb(76, 175, 80))
            }

            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            chart.data = BarData(dataSet)
            chart.invalidate()
        }
    )
}

private class CurrencyValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "₪%.0f".format(value)
    }
}

private class CurrencyAxisFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "₪%.0f".format(value)
    }
}

private fun getSelectedStoreItemPrice(
    item: ShoppingItem,
    selectedStore: String
): Double {
    return item.storePrices
        .firstOrNull { it.storeName == selectedStore }
        ?.price ?: item.storePrices.firstOrNull()?.price ?: 0.0
}

private fun formatCurrency(amount: Double): String {
    return "₪%.2f".format(amount)
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("he"))
    return formatter.format(Date(timestamp))
}

private fun formatShortDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd/MM", Locale("he"))
    return formatter.format(Date(timestamp))
}

private fun pickerUtcMillisToLocalStartOfDay(millis: Long): Long {
    val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = millis
    }

    val year = utcCalendar.get(Calendar.YEAR)
    val month = utcCalendar.get(Calendar.MONTH)
    val day = utcCalendar.get(Calendar.DAY_OF_MONTH)

    return Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun pickerUtcMillisToLocalEndOfDay(millis: Long): Long {
    val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = millis
    }

    val year = utcCalendar.get(Calendar.YEAR)
    val month = utcCalendar.get(Calendar.MONTH)
    val day = utcCalendar.get(Calendar.DAY_OF_MONTH)

    return Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis
}
