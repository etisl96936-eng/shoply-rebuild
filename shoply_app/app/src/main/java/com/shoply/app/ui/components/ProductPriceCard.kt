package com.shoply.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shoply.app.data.ProductUiModel

@Composable
fun ProductPriceCard(
    product: ProductUiModel,
    isAdmin: Boolean,
    onToggleInList: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cheapest = product.storePrices.minByOrNull { it.price }
    ShoplyCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                supportingContent = {
                    Text(
                        text = "${product.quantityLabel} | ${product.category}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingContent = {
                    Checkbox(
                        checked = product.isInMyList,
                        onCheckedChange = { onToggleInList() }
                    )
                },
                trailingContent = {
                    if (isAdmin) {
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "מחק",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                )
            )

            if (product.storePrices.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    product.storePrices.forEach { priceItem ->
                        val isCheapest = cheapest?.price == priceItem.price

                        StorePriceChip(
                            storeName = priceItem.storeName,
                            price = priceItem.price,
                            isHighlighted = cheapest?.price == priceItem.price
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductPriceCardPreviewContent() {
    ProductPriceCard(
        product = ProductUiModel(
            id = "1",
            title = "חלב 3%",
            category = "מוצרי חלב וביצים",
            quantityLabel = "1",
            storePrices = listOf(
                com.shoply.app.data.StorePrice("שופרסל", 7.20),
                com.shoply.app.data.StorePrice("רמי לוי", 6.90),
                com.shoply.app.data.StorePrice("ויקטורי", 7.50)
            ),
            isInMyList = true
        ),
        isAdmin = true,
        onToggleInList = {},
        onDelete = {}
    )
}