package com.example.uiapp.ui.slidetoconfirm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SlideToConfirmLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var lastActionResult by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.surface)
            .navigationBarsPadding(),
    ) {
        UiTopBar(
            title = "Slide to Confirm",
            subtitle = "Draggable pill, track trail & lock snap",
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Pengantar Komponen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .padding(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "📱 Gestur Klasik & Modern (Cash App / iOS / Uber)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "Slider gestur horizontal dengan hambatan elastis dan deteksi ambang batas (threshold 80%). Mengurangi kesalahan transaksi finansial tanpa dialog konfirmasi yang kaku.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = palette.textSecondary,
                    )
                }
            }

            // Pesan Konfirmasi Terakhir
            AnimatedVisibility(
                visible = lastActionResult != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                lastActionResult?.let { msg ->
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
                            Text("💸", fontSize = 18.sp)
                            Text(
                                text = msg,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF10B981),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            // Varian 1: Cash App Style (Slide to Pay)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. Pembayaran FinTech (Slide to Pay)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                )
                SlideToConfirmBar(
                    trackLabel = "Geser bayar Rp 350.000 ❯ ❯ ❯",
                    confirmedLabel = "Pembayaran Terkirim!",
                    accentColor = Color(0xFF0D9488),
                    thumbIcon = "→",
                    onConfirmed = {
                        lastActionResult = "Berhasil transfer Rp 350.000 ke 'Merchant Kafe Antariksa'."
                    },
                )
            }

            // Varian 2: Emergency / SOS Dispatch
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "2. Panggilan Darurat / SOS (Slide to Dispatch)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                )
                SlideToConfirmBar(
                    trackLabel = "Geser untuk Kirim Sinyal Darurat ❯ ❯ ❯",
                    confirmedLabel = "Sinyal Darurat Terkirim!",
                    accentColor = Color(0xFFE11D48),
                    thumbIcon = "🆘",
                    onConfirmed = {
                        lastActionResult = "Sinyal SOS & GPS darurat telah disiarkan ke kontak terdekat."
                    },
                )
            }

            // Varian 3: Order Fulfillment / Kurir Selesai Antar
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "3. Status Kurir (Slide to Complete Order)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                )
                SlideToConfirmBar(
                    trackLabel = "Geser jika paket sudah diterima ❯ ❯ ❯",
                    confirmedLabel = "Pesanan Selesai Diantar!",
                    accentColor = Color(0xFF2563EB),
                    thumbIcon = "📦",
                    onConfirmed = {
                        lastActionResult = "Paket #EXP-9921 telah ditandai selesai diterima pelanggan."
                    },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SlideToConfirmBar(
    trackLabel: String,
    confirmedLabel: String,
    accentColor: Color,
    thumbIcon: String,
    onConfirmed: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    var isConfirmed by remember { mutableStateOf(false) }
    val offsetX = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition()
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(CircleShape)
            .background(if (isConfirmed) accentColor else palette.surfaceMuted)
            .border(
                1.dp,
                if (isConfirmed) accentColor else palette.border,
                CircleShape,
            ),
    ) {
        val maxDragPx = with(density) { (maxWidth - 58.dp).toPx() }

        // Background progress fill saat menggeser
        if (!isConfirmed && offsetX.value > 0f) {
            val fillWidthDp = with(density) { (offsetX.value + 58.dp.toPx()).toDp() }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(0, 0) },
            ) {
                Box(
                    modifier = Modifier
                        .height(58.dp)
                        .fillMaxWidth(offsetX.value / maxDragPx)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f)),
                )
            }
        }

        // Teks petunjuk di tengah track
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (isConfirmed) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "✓",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = confirmedLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            } else {
                Text(
                    text = trackLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                    modifier = Modifier
                        .padding(start = 48.dp, end = 16.dp)
                        .alpha(shimmerAlpha * (1f - (offsetX.value / maxDragPx).coerceIn(0f, 1f))),
                )
            }
        }

        // Draggable Thumb Button
        if (!isConfirmed) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .size(58.dp)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(accentColor)
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            val target = (offsetX.value + delta).coerceIn(0f, maxDragPx)
                            scope.launch { offsetX.snapTo(target) }
                        },
                        onDragStopped = {
                            val ratio = offsetX.value / maxDragPx
                            if (ratio >= 0.82f) {
                                scope.launch {
                                    offsetX.animateTo(maxDragPx, tween(150, easing = FastOutSlowInEasing))
                                    isConfirmed = true
                                    onConfirmed()
                                }
                            } else {
                                scope.launch {
                                    offsetX.animateTo(0f, spring(dampingRatio = 0.7f, stiffness = 600f))
                                }
                            }
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = thumbIcon,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        } else {
            // Tombol Reset di ujung kanan ketika sukses
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable {
                            isConfirmed = false
                            scope.launch { offsetX.snapTo(0f) }
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "Reset ↺",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
        }
    }
}
