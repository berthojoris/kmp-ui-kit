package com.example.uiapp.ui.focustimer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private val FocusPresets = listOf(15, 25, 45)
private const val BreakMinutes = 5

private enum class TimerPhase(val label: String) {
    FOCUS("Fokus"),
    BREAK("Istirahat"),
}

private fun formatClock(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val minuteText = if (minutes < 10) "0$minutes" else "$minutes"
    val secondText = if (seconds < 10) "0$seconds" else "$seconds"
    return "$minuteText:$secondText"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FocusTimerLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var selectedMinutes by remember { mutableIntStateOf(25) }
    var phase by remember { mutableStateOf(TimerPhase.FOCUS) }
    var secondsLeft by remember { mutableIntStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var completedSessions by remember { mutableIntStateOf(0) }
    var focusMinutesTotal by remember { mutableIntStateOf(0) }
    var sessionFinished by remember { mutableStateOf(false) }

    val totalSeconds = if (phase == TimerPhase.FOCUS) selectedMinutes * 60 else BreakMinutes * 60
    val progress by animateFloatAsState(
        targetValue = if (totalSeconds == 0) 0f else secondsLeft.toFloat() / totalSeconds.toFloat(),
        animationSpec = tween(durationMillis = 400),
        label = "ring_progress",
    )

    val statusText = when {
        sessionFinished -> "Sesi selesai"
        isRunning -> "Berjalan"
        secondsLeft < totalSeconds -> "Jeda"
        else -> "Siap"
    }

    LaunchedEffect(isRunning) {
        while (isRunning && secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }
        if (isRunning && secondsLeft <= 0) {
            isRunning = false
            sessionFinished = true
            if (phase == TimerPhase.FOCUS) {
                completedSessions += 1
                focusMinutesTotal += selectedMinutes
            }
        }
    }

    fun resetSession(newPhase: TimerPhase, minutes: Int) {
        isRunning = false
        sessionFinished = false
        phase = newPhase
        selectedMinutes = minutes
        secondsLeft = if (newPhase == TimerPhase.FOCUS) minutes * 60 else BreakMinutes * 60
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Focus Timer",
                subtitle = "Ring sesi & siklus fokus/istirahat",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Intro card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .padding(14.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Timer Fokus Ala Pomodoro",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "Ring progres digambar dengan Canvas dan dihitung dari coroutine timer, bukan thread blocking. Selesaikan sesi fokus untuk membuka fase istirahat.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                }
            }

            // Preset durasi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FocusPresets.forEach { minutes ->
                    val selected = phase == TimerPhase.FOCUS && selectedMinutes == minutes
                    Surface(
                        onClick = {
                            if (!isRunning) {
                                resetSession(TimerPhase.FOCUS, minutes)
                            }
                        },
                        enabled = !isRunning,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) palette.primary else palette.surface,
                        border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "$minutes mnt",
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) palette.onPrimary else palette.textSecondary,
                            )
                        }
                    }
                }
            }

            // Ring timer
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .semantics {
                        contentDescription = "Sisa waktu ${formatClock(secondsLeft)} pada fase ${phase.label}"
                    },
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 12.dp.toPx()
                    val inset = strokeWidth / 2f + 4.dp.toPx()
                    val topLeft = Offset(inset, inset)
                    val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
                    drawArc(
                        color = palette.border,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                    drawArc(
                        color = if (phase == TimerPhase.FOCUS) palette.primary else palette.info,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = phase.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (phase == TimerPhase.FOCUS) palette.primary else palette.info,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatClock(secondsLeft),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        color = palette.textMuted,
                    )
                }
            }

            // Kartu selesai sesi
            if (sessionFinished) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.primaryContainer, RoundedCornerShape(12.dp))
                        .border(1.dp, palette.primary, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (phase == TimerPhase.FOCUS) "Sesi fokus selesai. Saatnya istirahat $BreakMinutes menit." else "Istirahat selesai. Siap fokus lagi?",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.onPrimaryContainer,
                        )
                        Text(
                            text = "Pilih langkah berikutnya di bawah untuk melanjutkan siklus.",
                            fontSize = 11.sp,
                            color = palette.onPrimaryContainer,
                        )
                    }
                }
            }

            // Kontrol utama
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Surface(
                    onClick = {
                        if (sessionFinished) {
                            if (phase == TimerPhase.FOCUS) {
                                resetSession(TimerPhase.BREAK, selectedMinutes)
                            } else {
                                resetSession(TimerPhase.FOCUS, selectedMinutes)
                            }
                        } else {
                            isRunning = !isRunning
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = palette.primary,
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = when {
                                sessionFinished && phase == TimerPhase.FOCUS -> "Mulai Istirahat"
                                sessionFinished -> "Sesi Baru"
                                isRunning -> "Jeda"
                                secondsLeft < totalSeconds -> "Lanjutkan"
                                else -> "Mulai Fokus"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.onPrimary,
                            maxLines = 1,
                        )
                    }
                }
                Surface(
                    onClick = { resetSession(TimerPhase.FOCUS, selectedMinutes) },
                    enabled = isRunning || secondsLeft < totalSeconds || sessionFinished,
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Reset",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textSecondary,
                        )
                    }
                }
            }

            // Statistik sesi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                FocusStatCard(
                    title = "Sesi Selesai",
                    value = "$completedSessions",
                    modifier = Modifier.weight(1f),
                )
                FocusStatCard(
                    title = "Total Fokus",
                    value = "$focusMinutesTotal mnt",
                    modifier = Modifier.weight(1f),
                )
                FocusStatCard(
                    title = "Durasi Istirahat",
                    value = "$BreakMinutes mnt",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun FocusStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Box(
        modifier = modifier
            .background(palette.surface, RoundedCornerShape(12.dp))
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = palette.textMuted,
                maxLines = 1,
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
                maxLines = 1,
            )
        }
    }
}
