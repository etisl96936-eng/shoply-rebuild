package com.shoply.app.ui.screens

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shoply.app.data.Product
import com.shoply.app.viewmodel.ProductViewModel

@Composable
fun ProductListScreen() {
    val productViewModel: ProductViewModel = viewModel()
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }

    LaunchedEffect(Unit) {
        productViewModel.loadProducts {
            products = it
        }
    }

    // הצגה בסיסית בלבד – בלי עיצוב
}
