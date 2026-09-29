package com.example.uiapp.ui.bentogrid

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BentoGridLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    // State interaktif widget
    var performanceMode by remember { mutableStateOf("Turbo") }
    var isCharging by remember { mutableStateOf(false) }
    var isSyncing by remember { mutableStateOf(false) }
    var focusShieldActive by remember { mutableStateOf(true) }
    var syncCount by remember { mutableStateOf(142) }

    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.surface)
            .navigationBarsPadding(),
    ) {
        UiTopBar(
            title = "Bento Grid Dashboard",
            subtitle = "Apple & SaaS modular interactive cards",
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Intro Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .padding(14.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "🍱 Layout Bento Grid Modular",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "Tata letak modular asimetris ala Apple Keynote, Linear, dan Raycast. Menggabungkan kartu lebar penuh (2-span) dan widget kotak (1-span) dengan kontrol interaktif live.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                }
            }

            // TILE 1: Wide Hero Card (Span 2) - Neural & Performance Control
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.surfaceMuted)
                    .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                    .padding(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(palette.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("⚡", fontSize = 16.sp)
                            }
                            Column {
                                Text(
                                    text = "Neural Engine & CPU",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                )
                                Text(
                                    text = "Efisiensi 6-Core Apple/Snapdragon",
                                    fontSize = 11.sp,
                                    color = palette.textSecondary,
                                )
                            }
                        }

                        // Mode Selector Pills
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Eco", "Balanced", "Turbo").forEach { mode ->
                                val isSelected = performanceMode == mode
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) palette.primary else palette.surface)
                                        .border(1.dp, if (isSelected) palette.primary else palette.border, RoundedCornerShape(6.dp))
                                    .pointerInput(mode) {
                                        detectTapGestures { performanceMode = mode }
                                    }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                ) {
                                    Text(
                                        text = mode,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else palette.textPrimary,
                                    )
                                }
                            }
                        }
                    }

                    // CPU Metric Bar
                    val cpuLoad = when (performanceMode) {
                        "Eco" -> 0.28f
                        "Balanced" -> 0.58f
                        else -> 0.91f
                    }
                    val animatedCpu by animateFloatAsState(cpuLoad, tween(400, easing = FastOutSlowInEasing))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("Beban Sistem (${performanceMode})", fontSize = 11.sp, color = palette.textSecondary)
                            Text("${(animatedCpu * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        }
                        LinearProgressIndicator(
                            progress = { animatedCpu },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (animatedCpu > 0.8f) Color(0xFFEF4444) else palette.primary,
                            trackColor = palette.border,
                            strokeCap = StrokeCap.Round,
                        )
                    }
                }
            }

            // ROW 1: Two Square Cards (Span 1 each)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // TILE 2: Battery Ring Gauge
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                        .pointerInput(Unit) {
                            detectTapGestures { isCharging = !isCharging }
                        }
                        .padding(14.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Baterai",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textSecondary,
                        )

                        Box(
                            modifier = Modifier.size(68.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                progress = { 1f },
                                modifier = Modifier.size(68.dp),
                                color = palette.border,
                                strokeWidth = 5.dp,
                            )
                            CircularProgressIndicator(
                                progress = { if (isCharging) 0.94f else 0.86f },
                                modifier = Modifier.size(68.dp),
                                color = if (isCharging) Color(0xFF10B981) else Color(0xFF0D9488),
                                strokeWidth = 5.dp,
                                strokeCap = StrokeCap.Round,
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isCharging) "94%" else "86%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                )
                                Text(
                                    text = if (isCharging) "⚡ Mengisi" else "8j 20m",
                                    fontSize = 8.sp,
                                    color = palette.textSecondary,
                                )
                            }
                        }

                        Text(
                            text = if (isCharging) "Sentuh cabut kabel" else "Sentuh pasang cas",
                            fontSize = 9.sp,
                            color = palette.textSecondary,
                        )
                    }
                }

                // TILE 3: Cloud Sync & Pulse Radar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                        .pointerInput(Unit) {
                            detectTapGestures {
                                isSyncing = true
                                syncCount += 1
                            }
                        }
                        .padding(14.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Cloud Sync", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981).copy(alpha = pulseAlpha)),
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "$syncCount item",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                            )
                            Text(
                                text = "Tersinkron ke server",
                                fontSize = 10.sp,
                                color = palette.textSecondary,
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(palette.surface)
                                .border(1.dp, palette.border, RoundedCornerShape(6.dp))
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = if (isSyncing) "Sinkron... 🔄" else "Sync Sekarang",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.primary,
                            )
                        }
                    }
                }
            }

            // ROW 2: Two Square Cards (Span 1 each)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // TILE 4: Storage Breakdown
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Penyimpanan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                        Text("182 / 256 GB", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)

                        // Segmented bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                        ) {
                            Box(modifier = Modifier.weight(0.45f).fillMaxSize().background(Color(0xFF0D9488)))
                            Box(modifier = Modifier.weight(0.25f).fillMaxSize().background(Color(0xFF6366F1)))
                            Box(modifier = Modifier.weight(0.30f).fillMaxSize().background(palette.border))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("Aplikasi 45%", fontSize = 9.sp, color = Color(0xFF0D9488))
                            Text("Media 25%", fontSize = 9.sp, color = Color(0xFF6366F1))
                            Text("Sisa 30%", fontSize = 9.sp, color = palette.textSecondary)
                        }
                    }
                }

                // TILE 5: Focus Shield Toggle
                val focusBgColor by animateColorAsState(
                    targetValue = if (focusShieldActive) palette.primary.copy(alpha = 0.12f) else palette.surfaceMuted,
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(focusBgColor)
                        .border(1.dp, if (focusShieldActive) palette.primary.copy(alpha = 0.4f) else palette.border, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(if (focusShieldActive) "🛡️ Aktif" else "🛡️ Nonaktif", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                            Switch(
                                checked = focusShieldActive,
                                onCheckedChange = { focusShieldActive = it },
                                modifier = Modifier.scale(0.75f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = palette.primary,
                                    uncheckedThumbColor = palette.textSecondary,
                                    uncheckedTrackColor = palette.surface,
                                ),
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Mode Fokus",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = if (focusShieldActive) "Notifikasi disenyapkan" else "Semua notifikasi masuk",
                            fontSize = 9.sp,
                            color = palette.textSecondary,
                        )
                    }
                }
            }

            // TILE 6: Wide Footer Card (Span 2) - Security & Integrity
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.surfaceMuted)
                    .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("🔒", fontSize = 24.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Keamanan Flat UI Terverifikasi",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = "100% Zero-shadow compliance, memory leak free, dan fully responsive.",
                            fontSize = 11.sp,
                            color = palette.textSecondary,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text("LULUS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
