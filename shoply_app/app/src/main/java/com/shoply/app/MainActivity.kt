package com.shoply.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.shoply.app.ui.navigation.SetupNavGraph
import com.shoply.app.ui.theme.ShoplyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoplyAppTheme {
                // יצירת בקר הניווט
                val navController = rememberNavController()

                // הפעלת הגרף שהגדרת בתחילת הדרך
                SetupNavGraph(navController = navController)
            }
        }
    }
}