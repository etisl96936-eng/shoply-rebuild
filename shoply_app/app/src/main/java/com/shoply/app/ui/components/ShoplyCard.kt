package com.shoply.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shoply.app.ui.theme.ShoplyElevation
import com.shoply.app.ui.theme.ShoplySpacing

/**
 * כרטיס סטנדרטי של Shoply
 * מעודכן לעקביות עם Design System - אבן דרך 3.3
 *
 * @param modifier Modifier נוסף
 * @param title כותרת אופציונלית
 * @param onClick פעולה בלחיצה (אופציונלי)
 * @param padding padding פנימי של הכרטיס
 * @param content תוכן הכרטיס
 */
@Composable
fun ShoplyCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    onClick: (() -> Unit)? = null,
    padding: PaddingValues = PaddingValues(ShoplySpacing.medium),
    content: @Composable () -> Unit
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Card(
        modifier = clickableModifier,
        elevation = CardDefaults.cardElevation(defaultElevation = ShoplyElevation.small),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(padding)) {
            if (!title.isNullOrBlank()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = ShoplySpacing.small)
                )
            }
            content()
        }
    }
}