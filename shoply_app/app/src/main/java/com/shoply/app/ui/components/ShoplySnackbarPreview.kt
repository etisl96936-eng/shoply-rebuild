package com.shoply.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shoply.app.ui.theme.ShoplyAppTheme

@Preview(showBackground = true)
@Composable
private fun ShoplySnackbarPreview() {
    ShoplyAppTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { padding ->
            ShoplyButton(
                text = "Show Snackbar",
                onClick = { showShoplySnackbar(scope, snackbarHostState, "Saved!") },
                modifier = Modifier.padding(padding)
            )
        }
    }
}
