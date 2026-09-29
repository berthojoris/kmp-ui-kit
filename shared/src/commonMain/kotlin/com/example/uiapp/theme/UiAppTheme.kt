package com.example.uiapp.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

private fun createColorScheme(palette: AppPalette, isDark: Boolean) = if (isDark) {
    darkColorScheme(
        primary = palette.primary,
        onPrimary = palette.onPrimary,
        primaryContainer = palette.primaryContainer,
        onPrimaryContainer = palette.onPrimaryContainer,
        background = palette.background,
        onBackground = palette.textPrimary,
        surface = palette.surface,
        onSurface = palette.textPrimary,
        surfaceVariant = palette.surfaceMuted,
        onSurfaceVariant = palette.textSecondary,
        outline = palette.border,
        error = palette.danger,
    )
} else {
    lightColorScheme(
        primary = palette.primary,
        onPrimary = palette.onPrimary,
        primaryContainer = palette.primaryContainer,
        onPrimaryContainer = palette.onPrimaryContainer,
        background = palette.background,
        onBackground = palette.textPrimary,
        surface = palette.surface,
        onSurface = palette.textPrimary,
        surfaceVariant = palette.surfaceMuted,
        onSurfaceVariant = palette.textSecondary,
        outline = palette.border,
        error = palette.danger,
    )
}

@Composable
fun UiAppTheme(
    initialMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    var themeMode by remember { mutableStateOf(initialMode) }
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemDark
    }

    val palette = if (isDark) DarkAppPalette else LightAppPalette
    val colorScheme = remember(isDark, palette) { createColorScheme(palette, isDark) }

    val controller = remember {
        object : ThemeController {
            override val currentMode: ThemeMode
                get() = themeMode
            override fun setMode(mode: ThemeMode) {
                themeMode = mode
            }
        }
    }

    CompositionLocalProvider(
        LocalAppPalette provides palette,
        LocalThemeController provides controller,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}

