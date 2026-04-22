package com.shoply.app.data

data class ProductUiModel(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val quantityLabel: String = "1",
    val storePrices: List<StorePrice> = emptyList(),
    val isInMyList: Boolean = false
)