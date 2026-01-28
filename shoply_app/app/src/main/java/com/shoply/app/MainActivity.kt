package com.shoply.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.shoply.app.ui.navigation.NavGraph
import com.shoply.app.ui.theme.ShoplyAppTheme

/**
 * Activity ראשי
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoplyAppTheme {
                NavGraph()
            }
        }
    }
}