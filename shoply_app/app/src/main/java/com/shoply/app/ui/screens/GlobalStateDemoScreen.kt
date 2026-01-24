package com.shoply.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shoply.app.ui.components.ErrorView
import com.shoply.app.ui.components.LoadingView
import com.shoply.app.ui.components.ShoplyButton
import com.shoply.app.ui.state.UiState
import com.shoply.app.ui.theme.ShoplyAppTheme

@Composable
fun GlobalStateDemoScreen(modifier: Modifier = Modifier) {

    var state by remember { mutableStateOf<UiState<String>>(UiState.Loading) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val s = state) {
            is UiState.Loading -> LoadingView(message = "Loading globally...")

            is UiState.Error -> ErrorView(
                message = s.message,
                onRetry = { state = UiState.Loading }
            )

            is UiState.Success -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "Success state:")
                Text(text = s.data)
                ShoplyButton(text = "Show Error", onClick = { state = UiState.Error("Global error example") })
                ShoplyButton(text = "Show Loading", onClick = { state = UiState.Loading })
            }
        }
    }

    // דמו קצר: אחרי לחיצה על Retry חוזר ל-Loading, ואת יכולה לשנות ידנית ל-Success ב-Preview
}

@Preview(showBackground = true)
@Composable
private fun GlobalStateDemoScreenPreview() {
    ShoplyAppTheme {
        GlobalStateDemoScreen()
    }
}
