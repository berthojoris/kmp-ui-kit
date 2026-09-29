package com.example.uiapp.ui.holdtoconfirm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HoldToConfirmLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current
    var lastActionMessage by remember { mutableStateOf<String?>(null) }
    var holdDurationMs by remember { mutableStateOf(1500) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.surface)
            .navigationBarsPadding(),
    ) {
        UiTopBar(
            title = "Hold to Confirm",
            subtitle = "Long-press progress & anti-mistake action",
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Deskripsi & Value Proposition
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .padding(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "💡 Trend UX Mobile (Linear / Cash App / Apple)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "Menggantikan dialog popup konfirmasi ('Apakah Anda yakin?') yang mengganggu flow pengguna. Pengguna cukup menahan tombol dengan animasi progress visual. Jika dilepas sebelum 100%, aksi langsung dibatalkan secara alami.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = palette.textSecondary,
                    )
                }
            }

            // Durasi Setting
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Kecepatan Tahan (Hold Duration)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf(1000 to "Cepat (1.0s)", 1500 to "Ideal (1.5s)", 2500 to "Ketat (2.5s)").forEach { (ms, label) ->
                        val isSelected = holdDurationMs == ms
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) palette.primary else palette.surfaceMuted)
                                .border(1.dp, if (isSelected) palette.primary else palette.border, RoundedCornerShape(8.dp))
                                .pointerInput(ms) {
                                    detectTapGestures { holdDurationMs = ms }
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else palette.textPrimary,
                            )
                        }
                    }
                }
            }

            // Notifikasi Hasil Aksi Terakhir
            AnimatedVisibility(
                visible = lastActionMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                lastActionMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF10B981).copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text("✅", fontSize = 16.sp)
                            Text(
                                text = msg,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF10B981),
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = "Tutup",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textSecondary,
                                modifier = Modifier.pointerInput(Unit) {
                                    detectTapGestures { lastActionMessage = null }
                                },
                            )
                        }
                    }
                }
            }

            // Style 1: FinTech High-Value Transfer Button
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. FinTech Transfer Saldo (Background Fill)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                )
                HoldToConfirmFillButton(
                    idleLabel = "Tahan untuk Kirim Rp 5.000.000",
                    holdingLabel = "Tahan terus...",
                    confirmedLabel = "Berhasil Ditransfer!",
                    durationMs = holdDurationMs,
                    accentColor = Color(0xFF0D9488),
                    onConfirmed = {
                        lastActionMessage = "Transfer Rp 5.000.000 ke rekening tujuan berhasil!"
                    },
                )
            }

            // Style 2: Destructive Danger Button (Linear / GitHub Style)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "2. Aksi Berbahaya / Hapus Akun (Danger Red)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                )
                HoldToConfirmFillButton(
                    idleLabel = "Tahan untuk Hapus Proyek Permanen",
                    holdingLabel = "Menghapus dalam hitungan...",
                    confirmedLabel = "Proyek Dihapus!",
                    durationMs = holdDurationMs,
                    accentColor = Color(0xFFE11D48),
                    onConfirmed = {
                        lastActionMessage = "Proyek 'Production-Database' berhasil dihapus permanen."
                    },
                )
            }

            // Style 3: Circular Ring Hold Button (Apple Watch / Workout Style)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "3. Ring Circular Hold (Apple Fitness / Selesaikan Sesi)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                )
                HoldToConfirmCircularCard(
                    title = "Sesi Olahraga Lari",
                    subtitle = "Tahan tombol ⭕ di bawah untuk mengakhiri latihan",
                    durationMs = holdDurationMs,
                    onConfirmed = {
                        lastActionMessage = "Sesi Lari 5.42 km telah disimpan ke aktivitas Anda!"
                    },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HoldToConfirmFillButton(
    idleLabel: String,
    holdingLabel: String,
    confirmedLabel: String,
    durationMs: Int,
    accentColor: Color,
    onConfirmed: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()
    val progress = remember { Animatable(0f) }
    var isPressing by remember { mutableStateOf(false) }
    var isConfirmed by remember { mutableStateOf(false) }
    var holdJob by remember { mutableStateOf<Job?>(null) }

    val scale by animateFloatAsState(
        targetValue = if (isPressing) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isConfirmed) accentColor else palette.surfaceMuted)
            .border(
                1.dp,
                if (isConfirmed) accentColor else palette.border,
                RoundedCornerShape(12.dp),
            )
            .pointerInput(isConfirmed, durationMs) {
                if (isConfirmed) return@pointerInput
                detectTapGestures(
                    onPress = {
                        isPressing = true
                        holdJob = scope.launch {
                            progress.snapTo(0f)
                            progress.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(durationMillis = durationMs, easing = LinearEasing),
                            )
                            if (progress.value >= 1f) {
                                isConfirmed = true
                                isPressing = false
                                onConfirmed()
                            }
                        }
                        val released = tryAwaitRelease()
                        if (released && !isConfirmed) {
                            isPressing = false
                            holdJob?.cancel()
                            scope.launch {
                                progress.animateTo(0f, spring(dampingRatio = 0.8f, stiffness = 500f))
                            }
                        }
                    },
                )
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        // Progress fill layer
        if (!isConfirmed && progress.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.value)
                    .height(54.dp)
                    .background(accentColor.copy(alpha = 0.22f)),
            )
        }

        // Teks dan status konten
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = when {
                    isConfirmed -> confirmedLabel
                    isPressing -> holdingLabel
                    else -> idleLabel
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isConfirmed) Color.White else palette.textPrimary,
            )

            if (isConfirmed) {
                Text(
                    text = "Reset ↺",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures {
                            isConfirmed = false
                            scope.launch { progress.snapTo(0f) }
                        }
                    },
                )
            } else if (isPressing) {
                Text(
                    text = "${(progress.value * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                )
            } else {
                Text(
                    text = "Tahan",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun HoldToConfirmCircularCard(
    title: String,
    subtitle: String,
    durationMs: Int,
    onConfirmed: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()
    val progress = remember { Animatable(0f) }
    var isPressing by remember { mutableStateOf(false) }
    var isConfirmed by remember { mutableStateOf(false) }
    var holdJob by remember { mutableStateOf<Job?>(null) }

    val ringScale by animateFloatAsState(
        targetValue = if (isPressing) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                )
            }

            // Circular ring center
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .scale(ringScale)
                    .pointerInput(isConfirmed, durationMs) {
                        if (isConfirmed) return@pointerInput
                        detectTapGestures(
                            onPress = {
                                isPressing = true
                                holdJob = scope.launch {
                                    progress.snapTo(0f)
                                    progress.animateTo(
                                        targetValue = 1f,
                                        animationSpec = tween(durationMillis = durationMs, easing = LinearEasing),
                                    )
                                    if (progress.value >= 1f) {
                                        isConfirmed = true
                                        isPressing = false
                                        onConfirmed()
                                    }
                                }
                                val released = tryAwaitRelease()
                                if (released && !isConfirmed) {
                                    isPressing = false
                                    holdJob?.cancel()
                                    scope.launch {
                                        progress.animateTo(0f, spring(dampingRatio = 0.8f, stiffness = 500f))
                                    }
                                }
                            },
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                // Background Track
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(86.dp),
                    color = palette.border,
                    strokeWidth = 6.dp,
                )

                // Animated Progress Ring
                CircularProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier.size(86.dp),
                    color = if (isConfirmed) Color(0xFF10B981) else Color(0xFFE11D48),
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round,
                )

                // Center Icon/Text
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (isConfirmed) Color(0xFF10B981) else if (isPressing) Color(0xFFE11D48) else palette.surface)
                        .border(1.dp, palette.border, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isConfirmed) {
                        Text("✓", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    } else if (isPressing) {
                        Text(
                            text = "${(progress.value * 100).toInt()}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    } else {
                        Text("■", fontSize = 20.sp, color = Color(0xFFE11D48))
                    }
                }
            }

            if (isConfirmed) {
                Text(
                    text = "Aktivitas Dihentikan! Ketuk untuk reset ↺",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.primary,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures {
                            isConfirmed = false
                            scope.launch { progress.snapTo(0f) }
                        }
                    },
                )
            } else {
                Text(
                    text = if (isPressing) "Terus tahan hingga lingkaran penuh..." else "Tekan dan tahan ikon kotak merah",
                    fontSize = 11.sp,
                    color = palette.textSecondary,
                )
            }
        }
    }
}
