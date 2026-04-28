package com.shoply.app.ui.screens

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shoply.app.viewmodel.ProductViewModel
import com.shoply.app.data.ShoppingItem

@Composable
fun ProductListScreen() {
    val productViewModel: ProductViewModel = viewModel()
    var products by remember { mutableStateOf<List<ShoppingItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        productViewModel.loadProducts {
            products = it
        }
    }

    // הצגה בסיסית בלבד – בלי עיצוב
}
