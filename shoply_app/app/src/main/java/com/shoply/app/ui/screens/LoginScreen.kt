package com.shoply.app.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.shoply.app.ui.components.ShoplyTextField
import com.shoply.app.ui.theme.Gray300
import com.shoply.app.ui.theme.Gray600
import com.shoply.app.ui.theme.ShoplyGreen
import com.shoply.app.ui.theme.ShoplyGreenLight
import com.shoply.app.ui.theme.ShoplySpacing
import com.shoply.app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(authState.isAuthenticated, authState.userRole) {
        if (authState.isAuthenticated) {
            val route = if (authState.userRole == "admin") "main/admin" else "main/user"

            navController.navigate(route) {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    LaunchedEffect(authState.errorMessage) {
        authState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = ShoplySpacing.large)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Shoply",
                style = MaterialTheme.typography.displayMedium,
                color = ShoplyGreen
            )

            Spacer(modifier = Modifier.height(ShoplySpacing.small))

            Text(
                text = "כניסה לרשימת הקניות",
                style = MaterialTheme.typography.bodyLarge,
                color = Gray600,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(ShoplySpacing.extraLarge))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
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
                        isPassword = true
                    )

                    Button(
                        onClick = {
                            authViewModel.login(email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !authState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = ShoplyGreenLight),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(text = "התחבר")
                    }
                }
            }

            Spacer(modifier = Modifier.height(ShoplySpacing.medium))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        try {
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(context.getString(com.shoply.app.R.string.default_web_client_id))
                                .setAutoSelectEnabled(false)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            val credentialManager = CredentialManager.create(context)
                            val result = credentialManager.getCredential(
                                context = context,
                                request = request
                            )

                            val credential = result.credential
                            if (
                                credential is CustomCredential &&
                                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                            ) {
                                val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                authViewModel.loginWithGoogle(googleCredential.idToken)
                            } else {
                                Toast.makeText(
                                    context,
                                    "סוג ההתחברות שהתקבל אינו נתמך",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (_: GoogleIdTokenParsingException) {
                            Toast.makeText(context, "שגיאה בקריאת פרטי Google", Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                e.localizedMessage ?: "התחברות עם Google נכשלה",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !authState.isLoading,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(text = "התחברות עם Google")
            }

            Spacer(modifier = Modifier.height(ShoplySpacing.small))

            HorizontalDivider(color = Gray300, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(ShoplySpacing.small))

            Text(
                text = "משתמש חדש? המשך למסך הרשמה",
                style = MaterialTheme.typography.bodyMedium,
                color = ShoplyGreen
            )
        }

        if (authState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}