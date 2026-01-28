package com.shoply.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Color Schemes
 */
private val LightColors = lightColorScheme(
    primary = ShoplyGreen,
    onPrimary = White,
    primaryContainer = ShoplyGreenLight,
    onPrimaryContainer = ShoplyGreenDark,

    secondary = ShoplyGreenDark,
    onSecondary = White,
    secondaryContainer = ShoplyGreenLight,
    onSecondaryContainer = ShoplyGreenDark,

    tertiary = InfoBlue,
    onTertiary = White,

    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRedLight,
    onErrorContainer = ErrorRed,

    background = BackgroundWhite,
    onBackground = TextPrimary,

    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = Gray100,
    onSurfaceVariant = TextSecondary,

    outline = Gray300,
    outlineVariant = Gray200
)

private val DarkColors = darkColorScheme(
    primary = ShoplyGreenLight,
    onPrimary = Black,
    primaryContainer = ShoplyGreen,
    onPrimaryContainer = White,

    secondary = ShoplyGreenLight,
    onSecondary = Black,

    error = ErrorRedLight,
    onError = Black,

    background = Gray900,
    onBackground = White,

    surface = Gray800,
    onSurface = White,
    surfaceVariant = Gray700,
    onSurfaceVariant = Gray300
)

/**
 * Shapes System - עיגולי פינות
 */
private val ShoplyShapes = Shapes(
    extraSmall = RoundedCornerShape(ShoplyRadius.small),
    small = RoundedCornerShape(ShoplyRadius.medium),
    medium = RoundedCornerShape(ShoplyRadius.large),
    large = RoundedCornerShape(ShoplyRadius.extraLarge),
    extraLarge = RoundedCornerShape(ShoplyRadius.round)
)

/**
 * Shoply Theme - ערכת נושא מלאה
 * אבן דרך 1.5 - Avigail
 */
@Composable
fun ShoplyAppTheme(
    darkTheme: Boolean = false, // לעתיד - Dark Mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = ShoplyShapes,
        content = content
    )
}