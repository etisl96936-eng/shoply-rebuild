package com.shoply.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import com.shoply.app.ui.components.ShoplyButton
import com.shoply.app.ui.components.ShoplyTextField
import com.shoply.app.ui.theme.ShoplySpacing
import com.shoply.app.viewmodel.ShoppingViewModel

/**
 * מסך התחברות
 * מעודכן לעקביות עם Design System - אבן דרך 1.5
 */
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
            .padding(ShoplySpacing.large),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // לוגו/כותרת
        Text(
            text = "Shoply",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(ShoplySpacing.extraSmall))

        Text(
            text = "מנהל הקניות החכם שלך",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(ShoplySpacing.huge))

        // כותרת התחברות
        Text(
            text = "התחברות",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(ShoplySpacing.large))

        // שדה שם משתמש
        ShoplyTextField(
            value = username,
            onValueChange = {
                username = it
                showError = false
            },
            label = "שם משתמש",
            isError = showError,
            supportingText = if (showError) null else "admin"
        )

        Spacer(Modifier.height(ShoplySpacing.medium))

        // שדה סיסמה
        ShoplyTextField(
            value = password,
            onValueChange = {
                password = it
                showError = false
            },
            label = "סיסמה",
            keyboardType = KeyboardType.Password,
            isPassword = true,
            isError = showError,
            supportingText = if (showError) null else "123456"
        )

        Spacer(Modifier.height(ShoplySpacing.medium))

        // הודעת שגיאה
        if (showError) {
            Text(
                text = "שם משתמש או סיסמה שגויים",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(ShoplySpacing.medium))
        }

        // כפתור התחברות
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
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(ShoplySpacing.medium))

        // טקסט עזר
        Text(
            text = "נסה: admin / 123456",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}