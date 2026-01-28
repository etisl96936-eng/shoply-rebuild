package com.shoply.app.ui.navigation

/**
 * הגדרת מסכים מתקדמת עם Sealed Class
 * אבן דרך 1.3 - Avigail
 *
 */
sealed class Screen(val route: String) {
    // Authentication screens (כבר קיימים ב-NavGraph)
    object Login : Screen("login")
    object Main : Screen("main")

    // Future screens - יתווספו בהמשך
    object Home : Screen("home")
    object MyLists : Screen("my_lists")
    object ListDetails : Screen("list_details/{listId}") {
        fun createRoute(listId: String) = "list_details/$listId"
    }
    object PriceComparison : Screen("price_comparison")
    object BudgetReports : Screen("budget_reports")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}