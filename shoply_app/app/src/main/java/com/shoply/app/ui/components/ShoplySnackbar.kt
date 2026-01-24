package com.shoply.app.ui.components

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun showShoplySnackbar(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: String
) {
    scope.launch {
        snackbarHostState.showSnackbar(message)
    }
}
