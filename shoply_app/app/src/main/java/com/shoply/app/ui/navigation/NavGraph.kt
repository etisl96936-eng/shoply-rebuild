package com.shoply.app.ui.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shoply.app.ui.screens.CompletedListsScreen
import com.shoply.app.ui.screens.LoginScreen
import com.shoply.app.ui.screens.MainScreen
import com.shoply.app.ui.screens.MyShoppingListScreen
import com.shoply.app.ui.screens.ProfileScreen
import com.shoply.app.ui.screens.RegisterScreen
import com.shoply.app.ui.screens.StatsScreen
import com.shoply.app.viewmodel.AuthViewModel
import com.shoply.app.viewmodel.ShoppingViewModel

/**
 * NavGraph - ניהול ניווט ראשי של האפליקציה
 * אבן דרך 1.3 - Avigail
 *
 * עודכן באבן דרך 4.3 לתמיכה ב-Responsive Design (העברת Activity למסכים)
 * עודכן באבן דרך 5.3 - הוספת מסך Register
 */
@Composable
fun NavGraph(activity: Activity) {
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
                authViewModel = authViewModel,
                activity = activity
            )
        }

        composable("register") {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel,
                activity = activity
            )
        }

        composable("main/user") {
            MainScreen(
                navController = navController,
                viewModel = shoppingViewModel,
                authViewModel = authViewModel,
                isAdmin = false,
                displayName = authState.displayName,
                activity = activity
            )
        }

        composable("main/admin") {
            MainScreen(
                navController = navController,
                viewModel = shoppingViewModel,
                authViewModel = authViewModel,
                isAdmin = true,
                displayName = authState.displayName,
                activity = activity
            )
        }

        composable("profile") {
            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel,
                activity = activity
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen(viewModel = shoppingViewModel)
        }

        composable("completed_lists") {
            CompletedListsScreen(viewModel = shoppingViewModel)
        }

        composable(Screen.MyLists.route) {
            MyShoppingListScreen(viewModel = shoppingViewModel)
        }
    }
}