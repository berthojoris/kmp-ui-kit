package com.example.uiapp.ui.systemplatform

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private enum class SystemSubTab(val title: String) {
    QR_SCANNER("QR & Barcode"),
    MAP_SHEET("Peta & Sheet"),
    HAPTICS("Haptik"),
    DYNAMIC_ISLAND("Dynamic Island"),
    ACCESSIBILITY("Aksesibilitas"),
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SystemPlatformLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var activeSubTab by remember { mutableStateOf(SystemSubTab.QR_SCANNER) }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Sistem & Platform",
                subtitle = "QR, Map Sheet, Haptic, Live Activity, A11y",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface)
                    .border(BorderStroke(1.dp, palette.border))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SystemSubTab.entries.forEach { tab ->
                    val isSelected = activeSubTab == tab
                    Surface(
                        onClick = { activeSubTab = tab },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) palette.primary else palette.surfaceMuted,
                        border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                    ) {
                        Text(
                            text = tab.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else palette.textPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (activeSubTab) {
                    SystemSubTab.QR_SCANNER -> QrScannerView(palette)
                    SystemSubTab.MAP_SHEET -> MapSheetView(palette)
                    SystemSubTab.HAPTICS -> HapticsSimulatorView(palette)
                    SystemSubTab.DYNAMIC_ISLAND -> DynamicIslandPreviewView(palette)
                    SystemSubTab.ACCESSIBILITY -> AccessibilityScalingView(palette)
                }
            }
        }
    }
}

@Composable
private fun QrScannerView(palette: com.example.uiapp.theme.AppPalette) {
    val infiniteTransition = rememberInfiniteTransition()
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("QR & Barcode Scanner Viewfinder", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                Text("Simulasi kamera dengan laser scanning bergerak", fontSize = 12.sp, color = palette.textMuted)
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val cornerLen = 32.dp.toPx()
                        val pad = 36.dp.toPx()
                        val stroke = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)

                        // 4 Corners of viewfinder
                        // Top-Left
                        drawLine(Color.White, Offset(pad, pad), Offset(pad + cornerLen, pad), strokeWidth = 3.dp.toPx())
                        drawLine(Color.White, Offset(pad, pad), Offset(pad, pad + cornerLen), strokeWidth = 3.dp.toPx())
                        // Top-Right
                        drawLine(Color.White, Offset(w - pad, pad), Offset(w - pad - cornerLen, pad), strokeWidth = 3.dp.toPx())
                        drawLine(Color.White, Offset(w - pad, pad), Offset(w - pad, pad + cornerLen), strokeWidth = 3.dp.toPx())
                        // Bottom-Left
                        drawLine(Color.White, Offset(pad, h - pad), Offset(pad + cornerLen, h - pad), strokeWidth = 3.dp.toPx())
                        drawLine(Color.White, Offset(pad, h - pad), Offset(pad, h - pad - cornerLen), strokeWidth = 3.dp.toPx())
                        // Bottom-Right
                        drawLine(Color.White, Offset(w - pad, h - pad), Offset(w - pad - cornerLen, h - pad), strokeWidth = 3.dp.toPx())
                        drawLine(Color.White, Offset(w - pad, h - pad), Offset(w - pad, h - pad - cornerLen), strokeWidth = 3.dp.toPx())

                        // Animated Laser Line
                        val currentY = pad + (h - 2 * pad) * laserY
                        drawLine(
                            color = Color(0xFFEF4444),
                            start = Offset(pad + 10.dp.toPx(), currentY),
                            end = Offset(w - pad - 10.dp.toPx(), currentY),
                            strokeWidth = 2.dp.toPx(),
                        )
                    }

                    Text("Arahkan QR ke dalam bingkai", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp))
                }
            }
        }
    }
}

@Composable
private fun MapSheetView(palette: com.example.uiapp.theme.AppPalette) {
    var sheetExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Map Schematics Canvas
        Canvas(modifier = Modifier.fillMaxSize().background(palette.surfaceMuted)) {
            val w = size.width
            val h = size.height

            // Grid road lines
            drawLine(palette.border, Offset(0f, h * 0.3f), Offset(w, h * 0.35f), strokeWidth = 8.dp.toPx())
            drawLine(palette.border, Offset(w * 0.4f, 0f), Offset(w * 0.45f, h), strokeWidth = 10.dp.toPx())
            drawLine(palette.border, Offset(0f, h * 0.65f), Offset(w, h * 0.6f), strokeWidth = 6.dp.toPx())

            // Location Pin Dot
            drawCircle(color = palette.primary, radius = 12.dp.toPx(), center = Offset(w * 0.43f, h * 0.32f))
            drawCircle(color = Color.White, radius = 5.dp.toPx(), center = Offset(w * 0.43f, h * 0.32f))
        }

        // Draggable Maps Bottom Sheet
        Surface(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(if (sheetExpanded) 320.dp else 140.dp)
                .clickable { sheetExpanded = !sheetExpanded },
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(palette.border)
                        .align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Atelier Modern Gallery & Studio", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                Text("Jl. Sudirman No. 42 \u00B7 Sentuh untuk ${if (sheetExpanded) "tutup" else "ekspansi detail"}", fontSize = 12.sp, color = palette.textMuted)

                if (sheetExpanded) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Detail Tempat:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                    Text("• Jam Operasional: 09:00 - 21:00 WIB", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Estimasi Kedatangan: 12 menit perjalanan (4.8 km)", fontSize = 12.sp, color = palette.textSecondary)
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = palette.primary,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                            Text("Mulai Navigasi Arah", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HapticsSimulatorView(palette: com.example.uiapp.theme.AppPalette) {
    var lastHapticTriggered by remember { mutableStateOf<String?>(null) }
    val hapticPatterns = listOf("Light Impact", "Medium Impact", "Heavy Impact", "Success Notification", "Error Pattern")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Haptic Feedback & Sensory Simulator", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
        Text("Simulasi respons sensori getaran untuk feedback sentuhan", fontSize = 12.sp, color = palette.textMuted)

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                hapticPatterns.forEach { pattern ->
                    Surface(
                        onClick = { lastHapticTriggered = pattern },
                        shape = RoundedCornerShape(10.dp),
                        color = palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(pattern, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                            Text("Tes ⚡", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                        }
                    }
                }
            }
        }

        if (lastHapticTriggered != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = palette.primaryContainer,
                border = BorderStroke(1.dp, palette.primary),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Trigger Berhasil: $lastHapticTriggered (Haptic Pattern Dispatched)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
private fun DynamicIslandPreviewView(palette: com.example.uiapp.theme.AppPalette) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("iOS Dynamic Island & Live Activity", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
        Text("Ketuk pil di bawah untuk simulasi ekspansi status", fontSize = 12.sp, color = palette.textMuted)

        Surface(
            onClick = { isExpanded = !isExpanded },
            shape = RoundedCornerShape(32.dp),
            color = Color.Black,
            modifier = Modifier
                .width(if (isExpanded) 320.dp else 180.dp)
                .height(if (isExpanded) 80.dp else 36.dp),
        ) {
            if (isExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🍕", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Pesanan Makanan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Kurir tiba dalam 8 mnt", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                        }
                    }
                    Text("8 mnt", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("🍕 Makanan", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("8 mnt", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AccessibilityScalingView(palette: com.example.uiapp.theme.AppPalette) {
    var scaleMultiplier by remember { mutableFloatStateOf(1.0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Accessibility & Dynamic Font Scaling", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                Text("Skala font aktif: ${(scaleMultiplier * 100).toInt()}%", fontSize = 12.sp, color = palette.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                Slider(
                    value = scaleMultiplier,
                    onValueChange = { scaleMultiplier = it },
                    valueRange = 0.8f..1.5f,
                )
                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surfaceMuted)
                        .padding(14.dp),
                ) {
                    Column {
                        Text(
                            text = "Judul Teks Aksesibel",
                            fontSize = (16 * scaleMultiplier).sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ini adalah paragraf contoh untuk menguji keterbacaan teks bagi pengguna dengan kebutuhan Dynamic Type atau alat bantu screen reader.",
                            fontSize = (13 * scaleMultiplier).sp,
                            color = palette.textSecondary,
                        )
                    }
                }
            }
        }
    }
}
