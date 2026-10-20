package com.example.uiapp

import androidx.compose.runtime.Composable
import platform.UIKit.UIDevice
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType
import platform.UIKit.UISelectionFeedbackGenerator

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

// Generator dipertahankan agar iOS tidak membuat ulang taptic engine per getaran.
private val impactLight by lazy { UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight) }
private val impactMedium by lazy { UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium) }
private val impactHeavy by lazy { UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy) }
private val notificationGenerator by lazy { UINotificationFeedbackGenerator() }
private val selectionGenerator by lazy { UISelectionFeedbackGenerator() }

/** Getaran haptic nyata via UIKit feedback generator. */
actual fun PerformHapticFeedback(kind: HapticKind) {
    when (kind) {
        HapticKind.LIGHT -> impactLight.impactOccurred()
        HapticKind.MEDIUM -> impactMedium.impactOccurred()
        HapticKind.HEAVY -> impactHeavy.impactOccurred()
        HapticKind.SUCCESS -> notificationGenerator.notificationOccurred(
            UINotificationFeedbackType.UINotificationFeedbackTypeSuccess,
        )
        HapticKind.WARNING -> notificationGenerator.notificationOccurred(
            UINotificationFeedbackType.UINotificationFeedbackTypeWarning,
        )
        HapticKind.SELECTION -> selectionGenerator.selectionChanged()
    }
}

@Composable
actual fun ApplySystemBarAppearance(isDark: Boolean) {
    // Tidak ada aksi: gaya status bar iOS diatur oleh host SwiftUI, bukan dari Compose.
}