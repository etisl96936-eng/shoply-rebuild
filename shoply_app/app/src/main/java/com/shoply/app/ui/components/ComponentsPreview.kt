package com.shoply.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shoply.app.ui.theme.ShoplyAppTheme



@Preview(showBackground = true)
@Composable
fun ComponentsPreview() {
    ShoplyAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- Buttons ---
            ShoplyButton(
                text = "Primary Button",
                onClick = {}
            )

            ShoplyButton(
                text = "Disabled Button",
                onClick = {},
                enabled = false
            )

            // --- TextField ---
            var email by remember { mutableStateOf("") }
            ShoplyTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email"
            )

            // --- Card ---
            ShoplyCard(title = "Reusable Card") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "This is a reusable card")
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            }


            // --- Loading ---
            LoadingView()

            // --- Error ---
            ErrorView(
                message = "Something went wrong",
                onRetry = {}
            )
        }
    }
}
