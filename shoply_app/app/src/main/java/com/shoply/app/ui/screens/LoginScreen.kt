////// קובץ דמה
package com.shoply.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shoply.app.ui.components.ShoplyButton
import com.shoply.app.viewmodel.ShoppingViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: ShoppingViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login Screen (placeholder)")
        Spacer(Modifier.height(16.dp))
        ShoplyButton(
            text = "Continue to Main",
            onClick = { navController.navigate("main") }
        )
    }
}
