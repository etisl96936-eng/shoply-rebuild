package com.shoply.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shoply.app.ui.theme.ShoplyAppTheme

@Preview(showBackground = true)
@Composable
private fun ShoplyModalBottomSheetPreview() {
    ShoplyAppTheme {
        var showSheet by remember { mutableStateOf(true) }

        if (showSheet) {
            ShoplyModalBottomSheet(
                title = "Quick Actions",
                onDismiss = { showSheet = false }
            ) {
                Column {
                    Text("Action 1")
                    Spacer(Modifier.height(8.dp))
                    Text("Action 2")
                    Spacer(Modifier.height(16.dp))
                    ShoplyButton(text = "Close", onClick = { showSheet = false })
                }
            }
        }
    }
}
