package com.shoply.app.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier // חסר לך ה-import הזה
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.Text

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier // הוספנו את הפרמטר הזה כדי לקבל את ה-innerPadding
) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier // מחיל את ה-Padding כדי שהטקסט לא ייחתך למעלה
    ) {
        composable(route = "login") {
            Text(text = "זה מסך ההתחברות (של אור)")
        }
        composable(route = "home") {
            Text(text = "זה מסך הרשימות (של עטרה)")
        }
    }
}