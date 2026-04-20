package com.shoply.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shoply.app.ui.theme.ShoplyAppTheme
import androidx.compose.ui.Modifier


@Composable
fun ShoplyAlertDialog(
    title: String,
    message: String,
    confirmText: String = "OK",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            ShoplyButton(
                text = confirmText,
                onClick = onConfirm
            )
        },
        dismissButton = {
            ShoplyButton(
                text = dismissText,
                onClick = onDismiss,
                modifier = androidx.compose.ui.Modifier.padding(end = 8.dp)
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ShoplyAlertDialogPreview() {
    ShoplyAppTheme {
        ShoplyAlertDialog(
            title = "Delete item?",
            message = "Are you sure you want to delete this item?",
            confirmText = "Delete",
            dismissText = "Cancel",
            onConfirm = {},
            onDismiss = {}
        )
    }
}