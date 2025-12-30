package com.shoply.app.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.shoply.app.viewmodel.ShoppingViewModel

@Composable
fun LoginScreen(navController: NavHostController,
                viewModel: ShoppingViewModel
) {
    Text(text = "כאן יהיה מסך הלוגין של אור")
}