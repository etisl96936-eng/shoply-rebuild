package com.shoply.app.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.shoply.app.ui.components.ShoplyButton
import com.shoply.app.ui.components.ShoplyButtonSize
import com.shoply.app.ui.components.ShoplyTextField
import com.shoply.app.ui.theme.ShoplyResponsive
import com.shoply.app.ui.theme.ShoplySpacing
import com.shoply.app.ui.theme.rememberShoplyWindowSize
import com.shoply.app.viewmodel.AuthViewModel

/**
 * מסך הרשמה למשתמשים חדשים
 * אבן דרך 5.3 - Avigail
 *
 * כולל שדות: שם תצוגה, אימייל, סיסמה, אימות סיסמה
 * תומך ב-Responsive Design ובולידציות מלאות
 */
@Composable
fun RegisterScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    activity: Activity
) {
    val context = LocalContext.current
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    // אבן דרך 4.3 - Responsive Design
    val windowSize = rememberShoplyWindowSize(activity)
    val maxWidth = ShoplyResponsive.maxContentWidth(windowSize)

    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // ולידציות UI מקומיות (לצביעת שדות באדום)
    val passwordsMismatch = confirmPassword.isNotEmpty() && password != confirmPassword
    val passwordTooShort = password.isNotEmpty() && password.length < 6

    // אחרי הרשמה מוצלחת - מעבר למסך הראשי
    LaunchedEffect(authState.isAuthenticated, authState.userRole) {
        if (authState.isAuthenticated) {
            val route = if (authState.userRole == "admin") "main/admin" else "main/user"

            navController.navigate(route) {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    // הצגת שגיאות מ-ViewModel
    LaunchedEffect(authState.errorMessage) {
        authState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = ShoplyResponsive.screenPadding(windowSize))
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = maxWidth)
                .align(Alignment.Center)
                .padding(vertical = ShoplySpacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Shoply",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(ShoplySpacing.small))

            Text(
                text = "יצירת חשבון חדש",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(ShoplySpacing.extraLarge))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(ShoplySpacing.large),
                    verticalArrangement = Arrangement.spacedBy(ShoplySpacing.medium)
                ) {
                    ShoplyTextField(
                        value = displayName,
                        onValueChange = {
                            displayName = it
                            authViewModel.clearError()
                        },
                        label = "שם תצוגה",
                        keyboardType = KeyboardType.Text
                    )

                    ShoplyTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            authViewModel.clearError()
                        },
                        label = "אימייל",
                        keyboardType = KeyboardType.Email
                    )

                    ShoplyTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            authViewModel.clearError()
                        },
                        label = "סיסמה",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        isError = passwordTooShort,
                        supportingText = if (passwordTooShort) {
                            "סיסמה חייבת להיות באורך 6 תווים לפחות"
                        } else {
                            "לפחות 6 תווים"
                        }
                    )

                    ShoplyTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            authViewModel.clearError()
                        },
                        label = "אימות סיסמה",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        isError = passwordsMismatch,
                        supportingText = if (passwordsMismatch) {
                            "הסיסמאות אינן תואמות"
                        } else {
                            null
                        }
                    )

                    Spacer(modifier = Modifier.height(ShoplySpacing.small))

                    ShoplyButton(
                        text = "הירשם",
                        onClick = {
                            authViewModel.register(
                                email = email,
                                password = password,
                                confirmPassword = confirmPassword,
                                displayName = displayName
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !authState.isLoading,
                        size = ShoplyButtonSize.Large
                    )
                }
            }

            Spacer(modifier = Modifier.height(ShoplySpacing.medium))

            // חזרה למסך הכניסה
            TextButton(
                onClick = {
                    authViewModel.clearError()
                    navController.popBackStack()
                }
            ) {
                Text(
                    text = "יש לך כבר חשבון? חזרה להתחברות",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (authState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}