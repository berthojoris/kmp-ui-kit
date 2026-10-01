package com.example.uiapp

import androidx.compose.runtime.Composable
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

@Composable
actual fun ApplySystemBarAppearance(isDark: Boolean) {
    // Tidak ada aksi: gaya status bar iOS diatur oleh host SwiftUI, bukan dari Compose.
}