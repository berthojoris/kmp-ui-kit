package com.example.uiapp.ui.triptracker

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private enum class TripPhase(val label: String) {
    IDLE("Siap"),
    SEARCHING("Mencari Driver"),
    PICKUP("Driver Menuju Jemput"),
    TRIP("Dalam Perjalanan"),
    ARRIVED("Tiba di Tujuan"),
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TripTrackerLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var phase by remember { mutableStateOf(TripPhase.IDLE) }
    var eta by remember { mutableIntStateOf(5) }
    var rating by remember { mutableIntStateOf(0) }

    // Auto-advance through phases
    LaunchedEffect(phase) {
        when (phase) {
            TripPhase.SEARCHING -> { delay(3000); phase = TripPhase.PICKUP }
            TripPhase.PICKUP -> { delay(4000); phase = TripPhase.TRIP; eta = 8 }
            TripPhase.TRIP -> {
                while (eta > 0) { delay(1000); eta-- }
                phase = TripPhase.ARRIVED
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(title = "Trip Tracker", subtitle = "Simulasi pelacakan perjalanan", onBack = onBack)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            // Control buttons
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    onClick = { phase = TripPhase.SEARCHING; rating = 0 },
                    enabled = phase == TripPhase.IDLE || phase == TripPhase.ARRIVED,
                    shape = RoundedCornerShape(10.dp),
                    color = palette.primary,
                    shadowElevation = 0.dp, tonalElevation = 0.dp,
                ) {
                    Text("Mulai Simulasi", modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
                }
                Surface(
                    onClick = { phase = TripPhase.IDLE; eta = 5; rating = 0 },
                    shape = RoundedCornerShape(10.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp, tonalElevation = 0.dp,
                ) {
                    Text("Reset", modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                }
            }

            // Map area
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(16.dp),
            ) {
                val inf = rememberInfiniteTransition()
                val pulse by inf.animateFloat(
                    initialValue = 0f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing)),
                )
                val vehicleProgress by animateFloatAsState(
                    targetValue = when (phase) {
                        TripPhase.IDLE, TripPhase.SEARCHING -> 0f
                        TripPhase.PICKUP -> 0.3f
                        TripPhase.TRIP -> if (eta > 0) 0.3f + 0.7f * (1f - eta / 8f) else 1f
                        TripPhase.ARRIVED -> 1f
                    },
                    animationSpec = tween(800),
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width; val h = size.height
                    val start = Offset(w * 0.15f, h * 0.8f)
                    val end = Offset(w * 0.85f, h * 0.15f)
                    val mid1 = Offset(w * 0.3f, h * 0.4f)
                    val mid2 = Offset(w * 0.7f, h * 0.55f)

                    // Dashed route
                    drawLine(color = palette.textMuted, start = start, end = mid1, strokeWidth = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)), cap = StrokeCap.Round)
                    drawLine(color = palette.textMuted, start = mid1, end = mid2, strokeWidth = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)), cap = StrokeCap.Round)
                    drawLine(color = palette.textMuted, start = mid2, end = end, strokeWidth = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)), cap = StrokeCap.Round)

                    // Pickup point with pulse
                    if (phase != TripPhase.IDLE) {
                        drawCircle(palette.primary.copy(alpha = (1f - pulse) * 0.3f), radius = 30.dp.toPx() * pulse, center = start)
                        drawCircle(palette.primary, radius = 8.dp.toPx(), center = start)
                    }

                    // Destination
                    drawCircle(palette.danger, radius = 8.dp.toPx(), center = end)

                    // Vehicle position along route
                    if (phase != TripPhase.IDLE) {
                        val t = vehicleProgress
                        val vx: Float; val vy: Float
                        if (t < 0.33f) {
                            val lt = t / 0.33f
                            vx = start.x + (mid1.x - start.x) * lt
                            vy = start.y + (mid1.y - start.y) * lt
                        } else if (t < 0.66f) {
                            val lt = (t - 0.33f) / 0.33f
                            vx = mid1.x + (mid2.x - mid1.x) * lt
                            vy = mid1.y + (mid2.y - mid1.y) * lt
                        } else {
                            val lt = (t - 0.66f) / 0.34f
                            vx = mid2.x + (end.x - mid2.x) * lt
                            vy = mid2.y + (end.y - mid2.y) * lt
                        }
                        drawCircle(palette.primary, radius = 10.dp.toPx(), center = Offset(vx, vy))
                        drawCircle(palette.onPrimary, radius = 5.dp.toPx(), center = Offset(vx, vy))
                    }
                }

                // Labels on map
                Text("Jl. Sudirman No. 1", fontSize = 10.sp, color = palette.textMuted, modifier = Modifier.align(Alignment.BottomStart).padding(start = 4.dp, bottom = 4.dp))
                Text("Mall Grand Indonesia", fontSize = 10.sp, color = palette.textMuted, modifier = Modifier.align(Alignment.TopEnd).padding(end = 4.dp, top = 4.dp))
            }

            // Phase sheet
            Surface(
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                shadowElevation = 0.dp, tonalElevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(20.dp),
                ) {
                    // Phase indicator
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (phase) {
                            TripPhase.IDLE -> palette.surfaceMuted
                            TripPhase.SEARCHING -> palette.warning.copy(alpha = 0.15f)
                            TripPhase.PICKUP -> palette.info.copy(alpha = 0.15f)
                            TripPhase.TRIP -> palette.primary.copy(alpha = 0.15f)
                            TripPhase.ARRIVED -> palette.success.copy(alpha = 0.15f)
                        },
                        shadowElevation = 0.dp, tonalElevation = 0.dp,
                    ) {
                        Text(
                            text = phase.label,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp, fontWeight = FontWeight.Bold,
                            color = when (phase) {
                                TripPhase.IDLE -> palette.textMuted
                                TripPhase.SEARCHING -> palette.warning
                                TripPhase.PICKUP -> palette.info
                                TripPhase.TRIP -> palette.primary
                                TripPhase.ARRIVED -> palette.success
                            },
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    when (phase) {
                        TripPhase.IDLE -> {
                            Text("Ketuk \"Mulai Simulasi\" untuk memulai", fontSize = 14.sp, color = palette.textSecondary)
                        }
                        TripPhase.SEARCHING -> {
                            Text("Sedang mencari driver terdekat...", fontSize = 14.sp, color = palette.textPrimary)
                            Spacer(Modifier.height(4.dp))
                            Text("Mohon tunggu sebentar", fontSize = 12.sp, color = palette.textMuted)
                        }
                        TripPhase.PICKUP -> {
                            DriverCard()
                        }
                        TripPhase.TRIP -> {
                            Text("Estimasi tiba", fontSize = 12.sp, color = palette.textMuted)
                            Text("$eta menit", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                            Spacer(Modifier.height(8.dp))
                            DriverCard()
                        }
                        TripPhase.ARRIVED -> {
                            Text("Anda telah tiba!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = palette.success)
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Tarif: ", fontSize = 14.sp, color = palette.textSecondary)
                                Text("Rp 25.000", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                            }
                            Spacer(Modifier.height(12.dp))
                            Text("Beri Rating Driver", fontSize = 13.sp, color = palette.textSecondary)
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                for (i in 1..5) {
                                    Surface(
                                        onClick = { rating = i },
                                        shape = CircleShape,
                                        color = if (i <= rating) palette.warning else palette.surfaceMuted,
                                        border = BorderStroke(1.dp, if (i <= rating) palette.warning else palette.border),
                                        shadowElevation = 0.dp, tonalElevation = 0.dp,
                                        modifier = Modifier.size(36.dp),
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("★", fontSize = 16.sp, color = if (i <= rating) palette.onPrimary else palette.textMuted)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Simulasi — bukan layanan transportasi sungguhan", fontSize = 10.sp, color = palette.textMuted)
                }
            }
        }
    }
}

@Composable
private fun DriverCard() {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = palette.surfaceMuted,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp, tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(palette.primary),
                contentAlignment = Alignment.Center,
            ) {
                Text("B", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Pak Budi", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                Text("Honda Beat Putih · B 1234 XYZ", fontSize = 12.sp, color = palette.textSecondary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("★ 4.8", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.warning)
                    Text(" · 1.200+ perjalanan", fontSize = 11.sp, color = palette.textMuted)
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(shape = RoundedCornerShape(8.dp), color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                    Text("Telepon", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 11.sp, color = palette.primary, fontWeight = FontWeight.SemiBold)
                }
                Surface(shape = RoundedCornerShape(8.dp), color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                    Text("Chat", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 11.sp, color = palette.primary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
