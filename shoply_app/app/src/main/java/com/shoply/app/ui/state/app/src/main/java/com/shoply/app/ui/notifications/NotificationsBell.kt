package com.shoply.app.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotificationsBell(
    unreadCount: Int,
    onClick: () -> Unit
) {
    Box {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "התראות"
            )
        }

        if (unreadCount > 0) {
            Badge(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(
                    text = if (unreadCount > 99) "99+" else unreadCount.toString()
                )
            }
        }
    }
}