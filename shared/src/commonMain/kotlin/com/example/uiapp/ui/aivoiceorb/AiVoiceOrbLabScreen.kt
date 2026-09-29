package com.example.uiapp.ui.aivoiceorb

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class AiVoiceState(val label: String, val badge: String) {
    LISTENING("Mendengarkan...", "🎙️"),
    THINKING("Memproses Solusi...", "🧠"),
    SPEAKING("Menjawab Pertanyaan...", "🔊"),
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AiVoiceOrbLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var currentState by remember { mutableStateOf(AiVoiceState.LISTENING) }
    var selectedPrompt by remember { mutableStateOf<String?>(null) }
    var isMicMuted by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()

    // Animasi fase waktu berkesinambungan
    val timePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (currentState) {
                    AiVoiceState.LISTENING -> 3500
                    AiVoiceState.THINKING -> 1200
                    AiVoiceState.SPEAKING -> 2000
                },
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
    )

    // Animasi breathing / pulsasi scale
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (currentState) {
                    AiVoiceState.LISTENING -> 1600
                    AiVoiceState.THINKING -> 700
                    AiVoiceState.SPEAKING -> 900
                },
                easing = FastOutSlowInEasing,
            ),
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
            title = "AI Voice Orb",
            subtitle = "ChatGPT Voice & Siri fluid organic intelligence",
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            // Deskripsi Tren Desain
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .padding(14.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "🌌 Trend AI Voice Interface 2025/2026",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "Visualisasi organik gelombang suara non-linear (sinusoidal orbital mesh) ala ChatGPT Voice, Siri iOS 18, dan Gemini Live. Merespons pergantian mode Mendengarkan, Berpikir, dan Menjawab.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                }
            }

            // State Selector Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AiVoiceState.values().forEach { state ->
                    val isSelected = currentState == state
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) palette.primary else palette.surfaceMuted)
                            .border(1.dp, if (isSelected) palette.primary else palette.border, RoundedCornerShape(8.dp))
                            .pointerInput(state) {
                                detectTapGestures { currentState = state }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "${state.badge} ${state.name.take(4)}",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else palette.textPrimary,
                        )
                    }
                }
            }

            // AREA UTAMA: ORB KANVAS BERWARNA ORGANIK
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.surfaceMuted)
                    .border(1.dp, palette.border, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                val primaryColor = when (currentState) {
                    AiVoiceState.LISTENING -> Color(0xFF0D9488)
                    AiVoiceState.THINKING -> Color(0xFF6366F1)
                    AiVoiceState.SPEAKING -> Color(0xFFEC4899)
                }
                val secondaryColor = when (currentState) {
                    AiVoiceState.LISTENING -> Color(0xFF10B981)
                    AiVoiceState.THINKING -> Color(0xFF8B5CF6)
                    AiVoiceState.SPEAKING -> Color(0xFFF43F5E)
                }

                Canvas(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(pulseScale),
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val baseRadius = size.width * 0.32f

                    // 1. Layer Glow Belakang
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(primaryColor.copy(alpha = 0.35f), Color.Transparent),
                            center = center,
                            radius = baseRadius * 1.5f,
                        ),
                        radius = baseRadius * 1.5f,
                        center = center,
                    )

                    // 2. Wave Mesh 1 (Sinusoidal orbital ring)
                    val path1 = Path()
                    val segments = 48
                    for (i in 0..segments) {
                        val theta = (i.toFloat() / segments) * 2 * PI
                        val waveOffset = sin(theta * 3 + timePhase) * (baseRadius * 0.14f)
                        val r = baseRadius + waveOffset
                        val x = center.x + (r * cos(theta)).toFloat()
                        val y = center.y + (r * sin(theta)).toFloat()
                        if (i == 0) path1.moveTo(x, y) else path1.lineTo(x, y)
                    }
                    path1.close()
                    drawPath(
                        path = path1,
                        brush = Brush.linearGradient(listOf(primaryColor, secondaryColor)),
                    )

                    // 3. Wave Mesh 2 (Counter-rotating accent ring)
                    val path2 = Path()
                    for (i in 0..segments) {
                        val theta = (i.toFloat() / segments) * 2 * PI
                        val waveOffset = cos(theta * 4 - timePhase * 1.4f) * (baseRadius * 0.12f)
                        val r = (baseRadius * 0.82f) + waveOffset
                        val x = center.x + (r * cos(theta)).toFloat()
                        val y = center.y + (r * sin(theta)).toFloat()
                        if (i == 0) path2.moveTo(x, y) else path2.lineTo(x, y)
                    }
                    path2.close()
                    drawPath(
                        path = path2,
                        color = Color.White.copy(alpha = 0.22f),
                    )

                    // 4. Inti Pusat Halus
                    drawCircle(
                        color = Color.White.copy(alpha = 0.88f),
                        radius = baseRadius * 0.28f,
                        center = center,
                    )
                }

                // Teks status di bagian bawah orb
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                ) {
                    Text(
                        text = currentState.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                }
            }

            // AUDIO REACTIVE WAVEFORM BARS
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.height(28.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val barCount = 14
                    for (i in 0 until barCount) {
                        val amplitudeMultiplier = if (isMicMuted) 0.15f else when (currentState) {
                            AiVoiceState.LISTENING -> (0.3f + 0.6f * sin(timePhase + i * 0.8f).coerceIn(0.1f, 1f))
                            AiVoiceState.THINKING -> 0.25f
                            AiVoiceState.SPEAKING -> (0.4f + 0.6f * cos(timePhase * 1.5f + i * 0.5f).coerceIn(0.1f, 1f))
                        }
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height((24 * amplitudeMultiplier).dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(palette.primary),
                        )
                    }
                }
                Text(
                    text = if (isMicMuted) "Mikrofon Dibisukan (Muted)" else "Frekuensi Suara Real-time (16 kHz)",
                    fontSize = 10.sp,
                    color = palette.textSecondary,
                )
            }

            // PROMPT SUGGESTION PILLS
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Pertanyaan Cepat (Pill Suggestion)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textSecondary,
                )
                listOf(
                    "Arsitektur Compose Multiplatform yang ideal?",
                    "Jelaskan prinsip Flat UI zero-shadow.",
                    "Bagaimana cara optimasi performa 60 FPS?",
                ).forEach { prompt ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.surfaceMuted)
                            .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                            .pointerInput(prompt) {
                                detectTapGestures {
                                    selectedPrompt = prompt
                                    currentState = AiVoiceState.SPEAKING
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "💬 \"$prompt\"",
                                fontSize = 11.sp,
                                color = palette.textPrimary,
                            )
                            Text("➔", fontSize = 11.sp, color = palette.primary)
                        }
                    }
                }
            }

            // BOTTOM ACTION CONTROLS (Mute & End)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isMicMuted) Color(0xFFEF4444) else palette.surfaceMuted)
                        .border(1.dp, if (isMicMuted) Color(0xFFEF4444) else palette.border, RoundedCornerShape(10.dp))
                        .pointerInput(isMicMuted) {
                            detectTapGestures { isMicMuted = !isMicMuted }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (isMicMuted) "Unmute Mic 🎙️" else "Mute Mic 🔇",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMicMuted) Color.White else palette.textPrimary,
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE11D48).copy(alpha = 0.12f))
                        .border(1.dp, Color(0xFFE11D48).copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .pointerInput(Unit) {
                            detectTapGestures {
                                onBack()
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Akhiri Sesi ✕",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE11D48),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
