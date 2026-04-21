package com.shoply.app.ui.theme

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Window Size System - מערכת רספונסיבית
 * אבן דרך 4.3 - Avigail
 *
 * מחלקת את גדלי המסך ל-3 קטגוריות לפי תקן Material Design 3:
 * - Compact: טלפון רגיל בפורטרט (< 600dp)
 * - Medium: טאבלט קטן / טלפון בלנדסקייפ (600-840dp)
 * - Expanded: טאבלט גדול / Foldable פתוח (> 840dp)
 */

/**
 * Enum פשוט שמגדיר את גדלי המסך האפשריים באפליקציה
 */
enum class ShoplyWindowSize {
    Compact,   // טלפון פורטרט
    Medium,    // טאבלט קטן / לנדסקייפ
    Expanded;  // טאבלט גדול / Foldable

    /**
     * האם המסך קטן (טלפון פורטרט)
     */
    val isCompact: Boolean get() = this == Compact

    /**
     * האם המסך גדול (טאבלט / Foldable)
     */
    val isLarge: Boolean get() = this == Medium || this == Expanded
}

/**
 * Composable helper שמחזיר את גודל המסך הנוכחי.
 *
 * מקבל את ה-Activity כפרמטר (בדרך כלל מועבר מ-MainActivity דרך NavGraph).
 *
 * שימוש:
 * ```
 * val windowSize = rememberShoplyWindowSize(activity)
 * if (windowSize.isLarge) {
 *     // הצג רשת של 2 עמודות
 * } else {
 *     // הצג עמודה אחת
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberShoplyWindowSize(activity: Activity): ShoplyWindowSize {
    val windowSizeClass = calculateWindowSizeClass(activity)

    return remember(windowSizeClass.widthSizeClass) {
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> ShoplyWindowSize.Compact
            WindowWidthSizeClass.Medium -> ShoplyWindowSize.Medium
            WindowWidthSizeClass.Expanded -> ShoplyWindowSize.Expanded
            else -> ShoplyWindowSize.Compact
        }
    }
}

/**
 * Responsive Values - ערכים רספונסיביים מומלצים
 *
 * נותן ערכים שונים לפי גודל המסך.
 * שימוש: val padding = ShoplyResponsive.screenPadding(windowSize)
 */
object ShoplyResponsive {

    /**
     * מספר העמודות המומלץ לרשת של פריטים
     */
    fun gridColumns(windowSize: ShoplyWindowSize): Int = when (windowSize) {
        ShoplyWindowSize.Compact -> 1
        ShoplyWindowSize.Medium -> 2
        ShoplyWindowSize.Expanded -> 3
    }

    /**
     * Padding אופטימלי למסכים שלמים
     */
    fun screenPadding(windowSize: ShoplyWindowSize): Dp = when (windowSize) {
        ShoplyWindowSize.Compact -> ShoplySpacing.medium
        ShoplyWindowSize.Medium -> ShoplySpacing.large
        ShoplyWindowSize.Expanded -> ShoplySpacing.extraLarge
    }

    /**
     * רוחב מקסימלי לתוכן שאפשר למרכז
     * (למשל: טופס שאנחנו לא רוצים שימתח על כל הרוחב בטאבלט)
     *
     * ב-Compact אין הגבלה - התוכן ממלא את כל הרוחב
     */
    fun maxContentWidth(windowSize: ShoplyWindowSize): Dp = when (windowSize) {
        ShoplyWindowSize.Compact -> Dp.Unspecified
        ShoplyWindowSize.Medium -> 600.dp
        ShoplyWindowSize.Expanded -> 840.dp
    }
}