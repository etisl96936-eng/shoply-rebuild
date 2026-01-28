package com.shoply.app.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shoply.app.ui.theme.ShoplyButtonHeight
import com.shoply.app.ui.theme.ShoplySpacing

/**
 * כפתור סטנדרטי של Shoply
 * מעודכן לעקביות עם Design System - אבן דרך 1.5
 *
 * @param text טקסט הכפתור
 * @param onClick פעולה בלחיצה
 * @param modifier Modifier נוסף
 * @param enabled האם הכפתור פעיל
 * @param size גודל הכפתור (small/medium/large)
 */
@Composable
fun ShoplyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: ShoplyButtonSize = ShoplyButtonSize.Medium
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(size.height),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        contentPadding = PaddingValues(
            horizontal = ShoplySpacing.medium,
            vertical = ShoplySpacing.small
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            style = when (size) {
                ShoplyButtonSize.Small -> MaterialTheme.typography.labelMedium
                ShoplyButtonSize.Medium -> MaterialTheme.typography.labelLarge
                ShoplyButtonSize.Large -> MaterialTheme.typography.titleMedium
            }
        )
    }
}

/**
 * גדלי כפתור
 */
enum class ShoplyButtonSize(val height: androidx.compose.ui.unit.Dp) {
    Small(ShoplyButtonHeight.small),
    Medium(ShoplyButtonHeight.medium),
    Large(ShoplyButtonHeight.large)
}