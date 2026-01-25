package com.shoply.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shoply.app.ui.components.ShoplyButton
import com.shoply.app.ui.components.ShoplyTextField
import com.shoply.app.viewmodel.ShoppingViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: ShoppingViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "התחברות",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(20.dp))

        ShoplyTextField(
            value = username,
            onValueChange = {
                username = it
                showError = false
            },
            label = "שם משתמש"
        )

        Spacer(Modifier.height(12.dp))

        ShoplyTextField(
            value = password,
            onValueChange = {
                password = it
                showError = false
            },
            label = "סיסמה",
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        Spacer(Modifier.height(16.dp))

        if (showError) {
            Text(
                text = "שם משתמש או סיסמה שגויים",
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(12.dp))
        }

        ShoplyButton(
            text = "התחברות",
            onClick = {
                if (username == "admin" && password == "123456") {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    showError = true
                }
            }
        )
    }
}
