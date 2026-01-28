package com.shoply.app.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Design System עבור Shoply
 * אבן דרך 1.5 - Avigail
 *
 * מגדיר ערכים קבועים לשימוש עקבי בכל האפליקציה
 */

/**
 * Spacing System - מרווחים סטנדרטיים
 * שימוש: padding(ShoplySpacing.medium)
 */
object ShoplySpacing {
    val none: Dp = 0.dp
    val extraSmall: Dp = 4.dp
    val small: Dp = 8.dp
    val medium: Dp = 16.dp
    val large: Dp = 24.dp
    val extraLarge: Dp = 32.dp
    val huge: Dp = 48.dp
}

/**
 * Corner Radius - עיגולי פינות
 * שימוש: RoundedCornerShape(ShoplyRadius.medium)
 */
object ShoplyRadius {
    val none: Dp = 0.dp
    val small: Dp = 4.dp
    val medium: Dp = 8.dp
    val large: Dp = 12.dp
    val extraLarge: Dp = 16.dp
    val round: Dp = 24.dp
}

/**
 * Elevation - צלליות
 * שימוש: tonalElevation = ShoplyElevation.small
 */
object ShoplyElevation {
    val none: Dp = 0.dp
    val small: Dp = 2.dp
    val medium: Dp = 4.dp
    val large: Dp = 8.dp
}

/**
 * Icon Sizes - גדלי אייקונים
 */
object ShoplyIconSize {
    val small: Dp = 16.dp
    val medium: Dp = 24.dp
    val large: Dp = 32.dp
    val extraLarge: Dp = 48.dp
}

/**
 * Button Heights - גבהי כפתורים
 */
object ShoplyButtonHeight {
    val small: Dp = 36.dp
    val medium: Dp = 48.dp
    val large: Dp = 56.dp
}

/**
 * Card Sizes - גדלי כרטיסים
 */
object ShoplyCardSize {
    val itemHeight: Dp = 80.dp
    val imageSize: Dp = 64.dp
    val minHeight: Dp = 120.dp
}