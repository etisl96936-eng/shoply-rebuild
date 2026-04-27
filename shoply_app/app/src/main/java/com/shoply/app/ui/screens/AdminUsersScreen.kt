package com.shoply.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class AdminUserUi(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val isAdmin: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    navController: NavHostController
) {
    val db = remember { FirebaseFirestore.getInstance() }

    var users by remember { mutableStateOf<List<AdminUserUi>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val snapshot = db.collection("users").get().await()

            users = snapshot.documents.map { doc ->
                AdminUserUi(
                    uid = doc.id,
                    displayName = doc.getString("displayName") ?: "",
                    email = doc.getString("email") ?: "",
                    isAdmin = doc.getBoolean("isAdmin") ?: false
                )
            }

            isLoading = false
        } catch (e: Exception) {
            error = "שגיאה בטעינת משתמשים"
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ניהול משתמשים") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "חזרה")
                    }
                }
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(24.dp)
                    )
                }
            }

            error != null -> {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(users, key = { it.uid }) { user ->
                        UserManagementCard(
                            user = user,
                            onAdminChanged = { checked ->
                                db.collection("users")
                                    .document(user.uid)
                                    .update("isAdmin", checked)

                                users = users.map {
                                    if (it.uid == user.uid) it.copy(isAdmin = checked) else it
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserManagementCard(
    user: AdminUserUi,
    onAdminChanged: (Boolean) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = user.displayName.ifBlank { "משתמש ללא שם" },
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = user.email.ifBlank { "אין אימייל" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("הרשאת מנהל")
                Switch(
                    checked = user.isAdmin,
                    onCheckedChange = onAdminChanged
                )
            }
        }
    }
}