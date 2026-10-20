package com.example.uiapp

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.WindowInsetsController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

// Layanan getar application-scoped; tidak menyimpan Context/Activity/View (§11.6 AGENTS).
@Volatile
private var appVibrator: Vibrator? = null

/**
 * Getaran haptic nyata. minSdk 29 sehingga seluruh VibrationEffect.EFFECT_* tersedia.
 * ponytail: tanpa izin VIBRATE di AndroidManifest, vibrate() melempar SecurityException
 * dan di-swallow di sini — tambahkan <uses-permission android:name="android.permission.VIBRATE"/>
 * untuk mengaktifkannya.
 */
actual fun PerformHapticFeedback(kind: HapticKind) {
    val vibrator = appVibrator ?: return
    val effect = when (kind) {
        HapticKind.LIGHT -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
        HapticKind.MEDIUM -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
        HapticKind.HEAVY -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
        HapticKind.SELECTION -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
        HapticKind.SUCCESS -> VibrationEffect.createWaveform(longArrayOf(0, 30, 60, 30), intArrayOf(0, 120, 0, 120), -1)
        HapticKind.WARNING -> VibrationEffect.createWaveform(longArrayOf(0, 60, 70, 60), intArrayOf(0, 200, 0, 200), -1)
    }
    try {
        vibrator.vibrate(effect)
    } catch (_: SecurityException) {
        // Izin VIBRATE belum dideklarasikan; abaikan secara diam.
    }
}

@Composable
actual fun ApplySystemBarAppearance(isDark: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val activity = view.context.findActivity() ?: return

    LaunchedEffect(isDark) {
        // Ambil sekali layanan Vibrator (application-scoped, bukan referensi Context/Activity).
        if (appVibrator == null) {
            val vibrator = activity.getSystemService(Vibrator::class.java)
            if (vibrator?.hasVibrator() == true) appVibrator = vibrator
        }
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController ?: return@LaunchedEffect
            val lightBars = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            // Tema gelap -> ikon terang (mask 0); tema terang -> ikon gelap (mask penuh).
            controller.setSystemBarsAppearance(if (isDark) 0 else lightBars, lightBars)
        } else {
            @Suppress("DEPRECATION")
            val lightFlags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            @Suppress("DEPRECATION")
            val current = window.decorView.systemUiVisibility
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = if (isDark) {
                current and lightFlags.inv()
            } else {
                current or lightFlags
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}