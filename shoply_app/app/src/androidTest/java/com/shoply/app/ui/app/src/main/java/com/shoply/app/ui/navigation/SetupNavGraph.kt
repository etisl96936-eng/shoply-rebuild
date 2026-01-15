package com.shoply.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"  // אמסך התחלתי
    ) {
        composable("home") {
            // כאן המסך הראשי
            // לדוגמה: HomeScreen(navController)
        }

        //  עוד מסכים כאן
    }
}