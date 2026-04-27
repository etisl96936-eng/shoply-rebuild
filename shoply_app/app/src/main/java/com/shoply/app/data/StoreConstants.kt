package com.shoply.app.data

val ALL_API_STORES = listOf(
    "שופרסל",
    "רמי לוי",
    "ויקטורי",
    "יוחננוף",
    "קרפור",
    "טיב טעם"
)

val DEFAULT_COMPARISON_STORES = listOf(
    "רמי לוי",
    "יוחננוף",
    "קרפור"
)

fun getComparisonStores(preferredStores: List<String>): List<String> {
    return if (preferredStores.isEmpty()) {
        DEFAULT_COMPARISON_STORES
    } else {
        preferredStores.take(3)
    }
}