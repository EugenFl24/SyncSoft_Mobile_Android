package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class ThemeMode {
    LIGHT,
    DARK,
    HIGH_SUNLIGHT
}

private val DarkColorScheme = darkColorScheme(
    primary = SyncsoftPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = SyncsoftSecondaryLight,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = SyncsoftTertiary,
    background = SyncsoftBgDark,
    onBackground = SyncsoftOnSurfaceDark,
    surface = SyncsoftSurfaceDark,
    onSurface = SyncsoftOnSurfaceDark,
    surfaceVariant = SyncsoftSurfaceVariantDark,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = SyncsoftOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = SyncsoftPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = SyncsoftSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = SyncsoftTertiary,
    background = SyncsoftBgLight,
    onBackground = SyncsoftOnSurfaceLight,
    surface = SyncsoftSurfaceLight,
    onSurface = SyncsoftOnSurfaceLight,
    surfaceVariant = SyncsoftSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF64748B),
    outline = SyncsoftOutlineLight
)

// High Sunlight Outdoor Mode optimized for outdoor operatives with direct glare
private val HighSunlightColorScheme = lightColorScheme(
    primary = Color(0xFF000000),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE2E8F0),
    onPrimaryContainer = Color(0xFF000000),
    secondary = Color(0xFF004085),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCCE5FF),
    onSecondaryContainer = Color(0xFF000000),
    tertiary = Color(0xFF856404),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000),
    surfaceVariant = Color(0xFFCBD5E1),
    onSurfaceVariant = Color(0xFF000000),
    outline = Color(0xFF000000)
)

@Composable
fun SyncsoftTheme(
    themeMode: ThemeMode = ThemeMode.LIGHT,
    dynamicColor: Boolean = false, // Use strict brand colors for Syncsoft design language match
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.HIGH_SUNLIGHT -> HighSunlightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Compatibility wrapper
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val mode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT
    SyncsoftTheme(themeMode = mode, dynamicColor = dynamicColor, content = content)
}
