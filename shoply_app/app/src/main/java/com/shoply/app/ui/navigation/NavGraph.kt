package com.shoply.app.ui.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shoply.app.ui.screens.CompletedListsScreen
import com.shoply.app.ui.screens.ListDetailsScreen
import com.shoply.app.ui.screens.LoginScreen
import com.shoply.app.ui.screens.MainScreen
import com.shoply.app.ui.screens.MyShoppingListScreen
import com.shoply.app.ui.screens.ProfileScreen
import com.shoply.app.ui.screens.RegisterScreen
import com.shoply.app.ui.screens.StatsScreen
import com.shoply.app.viewmodel.AuthViewModel
import com.shoply.app.viewmodel.ShoppingViewModel
import com.shoply.app.ui.screens.CompletedListDetailsScreen
import com.shoply.app.ui.notifications.NotificationsScreen



/**
 * NavGraph - ניהול ניווט ראשי של האפליקציה
 * אבן דרך 1.3 - Avigail
 *
 * עודכן באבן דרך 4.3 לתמיכה ב-Responsive Design (העברת Activity למסכים)
 * עודכן באבן דרך 5.3 - הוספת מסך Register
 * עודכן - הוספת ניווט למסך My Lists ולמסך List Details
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
        composable("notifications") {
            NotificationsScreen(
                navController = navController
            )
        }
        composable(Screen.Stats.route) {
            StatsScreen(
                viewModel = shoppingViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("completed_lists") {
            CompletedListsScreen(
                viewModel = shoppingViewModel,
                onListClick = { completedList ->
                    navController.navigate("completed_list_details/${completedList.id}")
                }
            )
        }

        composable(
            route = "completed_list_details/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.StringType })
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId") ?: ""

            CompletedListDetailsScreen(
                listId = listId,
                navController = navController,
                viewModel = shoppingViewModel
            )
        }

        composable(Screen.MyLists.route) {
            MyShoppingListScreen(
                navController = navController,
                viewModel = shoppingViewModel
            )
        }

        composable(
            route = Screen.ListDetails.route,
            arguments = listOf(
                navArgument("listId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId") ?: ""

            ListDetailsScreen(
                listId = listId,
                navController = navController,
                viewModel = shoppingViewModel
            )
        }


        composable(
            route = "completed_list_details/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.StringType })
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId") ?: ""

            CompletedListDetailsScreen(
                listId = listId,
                navController = navController,
                viewModel = shoppingViewModel
            )
        }
    }
}
