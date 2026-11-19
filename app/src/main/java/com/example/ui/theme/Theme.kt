package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// 4/8-pt Spacing Scale & Concentric Corner Radius Scale
data class GlassDesignTokens(
    val spaceXXS: Dp = 2.dp,
    val spaceXS: Dp = 4.dp,
    val spaceSM: Dp = 8.dp,
    val spaceMD: Dp = 12.dp,
    val spaceLG: Dp = 16.dp,
    val spaceXL: Dp = 24.dp,
    val spaceXXL: Dp = 32.dp,
    // Concentric radii: inner radius = outer radius - padding
    val radiusCapsule: Dp = 999.dp,
    val radiusSheet: Dp = 38.dp,
    val radiusCardOuter: Dp = 26.dp,
    val radiusCardInner: Dp = 18.dp, // 26.dp - 8.dp padding
    val radiusTile: Dp = 20.dp,
    val radiusRow: Dp = 14.dp,
    val radiusBadge: Dp = 10.dp,
    val minTouchTarget: Dp = 48.dp
) {
    fun concentricInnerRadius(outerRadius: Dp, padding: Dp): Dp =
        (outerRadius - padding).coerceAtLeast(4.dp)
}

val LocalGlassTokens = staticCompositionLocalOf { GlassDesignTokens() }

@Composable
fun GlasslyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accentColor: Color = GlassBlue,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = accentColor,
            onPrimary = Color.White,
            secondary = GlassCyan,
            tertiary = GlassPink,
            background = DarkBackground,
            onBackground = DarkTextPrimary,
            surface = DarkSurface,
            onSurface = DarkTextPrimary,
            surfaceVariant = DarkRowFill,
            onSurfaceVariant = DarkTextSecondary,
            error = GlassRed
        )
    } else {
        lightColorScheme(
            primary = accentColor,
            onPrimary = Color.White,
            secondary = GlassCyan,
            tertiary = GlassPink,
            background = LightBackground,
            onBackground = LightTextPrimary,
            surface = LightSurface,
            onSurface = LightTextPrimary,
            surfaceVariant = LightRowFill,
            onSurfaceVariant = LightTextSecondary,
            error = GlassRed
        )
    }

    CompositionLocalProvider(
        LocalGlassTokens provides GlassDesignTokens()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
