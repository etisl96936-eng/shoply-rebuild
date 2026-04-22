package com.shoply.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shoply.app.data.ProductUiModel
import com.shoply.app.data.StorePrice
import com.shoply.app.ui.components.ProductPriceCard
import androidx.compose.ui.tooling.preview.Preview
import com.shoply.app.ui.theme.ShoplyAppTheme


@Composable
fun ProductPriceDemoScreen(
    modifier: Modifier = Modifier
) {
    val mockProducts = listOf(
        ProductUiModel(
            id = "1",
            title = "חלב 3%",
            category = "מוצרי חלב וביצים",
            quantityLabel = "1 ליטר",
            storePrices = listOf(
                StorePrice("שופרסל", 7.20),
                StorePrice("רמי לוי", 6.90),
                StorePrice("ויקטורי", 7.50)
            ),
            isInMyList = true
        ),
        ProductUiModel(
            id = "2",
            title = "לחם אחיד",
            category = "מאפה ודגנים",
            quantityLabel = "1 יח'",
            storePrices = listOf(
                StorePrice("שופרסל", 6.40),
                StorePrice("רמי לוי", 5.90),
                StorePrice("ויקטורי", 6.10)
            ),
            isInMyList = false
        ),
        ProductUiModel(
            id = "3",
            title = "ביצים M",
            category = "מוצרי חלב וביצים",
            quantityLabel = "12 יח'",
            storePrices = listOf(
                StorePrice("שופרסל", 14.90),
                StorePrice("רמי לוי", 13.50),
                StorePrice("ויקטורי", 14.20)
            ),
            isInMyList = true
        )
    )

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(mockProducts) { product ->
            ProductPriceCard(
                product = product,
                isAdmin = true,
                onToggleInList = {},
                onDelete = {}
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ProductPriceDemoScreenPreview() {
    ShoplyAppTheme {
        ProductPriceDemoScreen()
    }
}