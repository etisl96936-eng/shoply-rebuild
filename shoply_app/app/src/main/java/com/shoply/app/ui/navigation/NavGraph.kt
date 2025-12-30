package com.shoply.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shoply.app.ui.screens.LoginScreen
import com.shoply.app.ui.screens.MainScreen
import com.shoply.app.viewmodel.ShoppingViewModel

@Composable
fun NavGraph() { // השם המעודכן של הקובץ שלך
    val navController = rememberNavController()
    val shoppingViewModel: ShoppingViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                navController = navController,
                viewModel = shoppingViewModel
            )
        }
        composable("main") {
            MainScreen(
                navController = navController,
                viewModel = shoppingViewModel
            )
        }
    }
}