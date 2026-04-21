package com.shoply.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.shoply.app.ui.components.ShoplyButton
import com.shoply.app.ui.components.ShoplyButtonSize
import com.shoply.app.ui.components.ShoplyTextField
import com.shoply.app.ui.theme.ShoplySpacing
import com.shoply.app.viewmodel.AuthViewModel
import com.shoply.app.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val stores = listOf(
        "שופרסל",
        "רמי לוי",
        "ויקטורי",
        "יוחננוף",
        "קרפור",
        "טיב טעם"
    )

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            authViewModel.refreshUserData()
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("פרופיל משתמש") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("חזרה")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(ShoplySpacing.medium)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(ShoplySpacing.medium),
                verticalArrangement = Arrangement.spacedBy(ShoplySpacing.medium)
            ) {
                ShoplyTextField(
                    value = state.profile.displayName,
                    onValueChange = { viewModel.updateDisplayName(it) },
                    label = "שם תצוגה"
                )

                ShoplyTextField(
                    value = state.profile.email,
                    onValueChange = {},
                    label = "אימייל"
                )

                Text(
                    text = "בחרי עד 3 סופרים מועדפים",
                    style = MaterialTheme.typography.titleMedium
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = ShoplySpacing.extraSmall),
                    horizontalArrangement = Arrangement.spacedBy(ShoplySpacing.small)
                ) {
                    items(stores) { store ->
                        FilterChip(
                            selected = state.profile.preferredStores.contains(store),
                            onClick = { viewModel.toggleStore(store) },
                            label = { Text(store) }
                        )
                    }
                }

                Text(
                    text = "נבחרו: ${state.profile.preferredStores.size}/3",
                    style = MaterialTheme.typography.bodyMedium
                )

                ShoplyButton(
                    text = if (state.isSaving) "שומר..." else "שמור",
                    onClick = { viewModel.saveProfile() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving,
                    size = ShoplyButtonSize.Large
                )
            }
        }
    }
}