package com.shoply.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.shoply.app.ui.theme.ShoplyAppTheme

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
                onClick = onDismiss
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