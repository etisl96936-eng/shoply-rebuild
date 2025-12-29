package com.shoply.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shoply.app.ui.screens.LoginScreen
import com.shoply.app.ui.screens.MainScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("main")
            })
        }
        composable("main") {
            MainScreen()
        }
    }
}