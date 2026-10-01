package com.example.uiapp

import androidx.compose.runtime.Composable

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

/**
 * Menyelaraskan warna ikon system bar (status & navigation bar) dengan tema aplikasi.
 *
 * Di Android, `enableEdgeToEdge()` bawaan hanya mengikuti setelan gelap sistem, sehingga
 * tema gelap/terang yang dipilih pengguna di `ThemeLabScreen` tidak tercermin pada ikon
 * system bar. Fungsi ini menutup celah tersebut. Di iOS tidak ada aksi: warna status bar
 * dikendalikan host SwiftUI.
 */
@Composable
expect fun ApplySystemBarAppearance(isDark: Boolean)