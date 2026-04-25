package com.shoply.app.ui.stats

data class StatsData(
    val totalSpent: Double = 0.0,
    val completedListsCount: Int = 0,
    val categoryTotals: Map<String, Double> = emptyMap(),
    val listTotals: Map<String, Double> = emptyMap(),
    val topCategory: String = "",
    val highestListName: String = ""
)