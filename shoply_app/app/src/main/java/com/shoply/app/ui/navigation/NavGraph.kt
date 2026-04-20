package com.shoply.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shoply.app.ui.screens.LoginScreen
import com.shoply.app.ui.screens.MainScreen
import com.shoply.app.ui.screens.ProfileScreen
import com.shoply.app.viewmodel.AuthViewModel
import com.shoply.app.viewmodel.ShoppingViewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val shoppingViewModel: ShoppingViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("main/user") {
            MainScreen(
                navController = navController,
                viewModel = shoppingViewModel,
                isAdmin = false,
                displayName = authState.displayName
            )
        }

        composable("main/admin") {
            MainScreen(
                navController = navController,
                viewModel = shoppingViewModel,
                isAdmin = true,
                displayName = authState.displayName
            )
        }

        composable("profile") {
            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}