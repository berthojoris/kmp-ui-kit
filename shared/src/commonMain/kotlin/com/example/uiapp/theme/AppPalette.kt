package com.example.uiapp.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppPalette(
    val background: Color,
    val surface: Color,
    val surfaceMuted: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
    val danger: Color,
)

val LightAppPalette = AppPalette(
    background = Color(0xFFF8F9FA),
    surface = Color(0xFFFFFFFF),
    surfaceMuted = Color(0xFFF3F4F6),
    border = Color(0xFFE5E7EB),
    textPrimary = Color(0xFF111827),
    textSecondary = Color(0xFF4B5563),
    textMuted = Color(0xFF9CA3AF),
    primary = Color(0xFF0A332C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE8F5E9),
    onPrimaryContainer = Color(0xFF0A332C),
    success = Color(0xFF10B981),
    warning = Color(0xFFF59E0B),
    info = Color(0xFF3B82F6),
    danger = Color(0xFFEF4444),
)

val DarkAppPalette = AppPalette(
    background = Color(0xFF0B0F14),
    surface = Color(0xFF131A22),
    surfaceMuted = Color(0xFF1B242E),
    border = Color(0xFF2A3644),
    textPrimary = Color(0xFFF3F6F9),
    textSecondary = Color(0xFFB7C2CE),
    textMuted = Color(0xFF7A8796),
    primary = Color(0xFF4FD1B5),
    onPrimary = Color(0xFF06231D),
    primaryContainer = Color(0xFF12332C),
    onPrimaryContainer = Color(0xFF8CEBD5),
    success = Color(0xFF34D399),
    warning = Color(0xFFFBBF24),
    info = Color(0xFF60A5FA),
    danger = Color(0xFFF87171),
)

val LocalAppPalette = staticCompositionLocalOf { LightAppPalette }

enum class ThemeMode(val label: String) {
    LIGHT("Terang"),
    DARK("Gelap"),
    SYSTEM("Sistem"),
}

interface ThemeController {
    val currentMode: ThemeMode
    fun setMode(mode: ThemeMode)
}

val LocalThemeController = staticCompositionLocalOf<ThemeController> {
    object : ThemeController {
        override val currentMode: ThemeMode = ThemeMode.LIGHT
        override fun setMode(mode: ThemeMode) {}
    }
}
