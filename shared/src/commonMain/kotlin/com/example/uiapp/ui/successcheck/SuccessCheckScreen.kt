package com.example.uiapp.ui.successcheck

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private enum class TransferPhase { IDLE, LOADING, SUCCESS, FAILED }

private val TransferAmount = "Rp 2.500.000"
private val TransferRecipient = "Andini Prameswari"
private val TransferBank = "BCA"
private val TransferAccount = "8820 1133 55"
private val TransferNote = "Pembayaran kos bulan Oktober"

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SuccessCheckScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var phase by remember { mutableStateOf(TransferPhase.IDLE) }
    var willFail by remember { mutableStateOf(false) }

    // Loading singkat lalu masuk ke overlay hasil
    LaunchedEffect(phase) {
        if (phase == TransferPhase.LOADING) {
            delay(600)
            phase = if (willFail) TransferPhase.FAILED else TransferPhase.SUCCESS
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(palette.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
        ) {
            UiTopBar(
                title = "Success Check",
                subtitle = "Centang sukses progresif ala aplikasi bank",
                onBack = onBack,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "Setelah transfer dikirim, indikator loading sebentar berubah menjadi tanda centang yang digambar progresif. Pola ini memberi konfirmasi visual yang memuaskan tanpa memperlambat flow.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = palette.textSecondary,
                )

                // Detail transfer (mock, simulasi — tidak ada dana sungguhan)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.surface)
                        .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Detail Transfer",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                    )
                    DetailRow(label = "Penerima", value = TransferRecipient)
                    DetailRow(label = "Bank", value = TransferBank)
                    DetailRow(label = "No. Rekening", value = TransferAccount)
                    DetailRow(label = "Nominal", value = TransferAmount, emphasize = true)
                    DetailRow(label = "Berita", value = TransferNote)
                    Text(
                        text = "Simulasi — tidak ada dana sungguhan yang dipindahkan.",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                }

                val isLoading = phase == TransferPhase.LOADING

                PrimaryActionButton(
                    label = if (isLoading && !willFail) "Memproses transfer..." else "Kirim Transfer",
                    loading = isLoading && !willFail,
                    enabled = !isLoading,
                    onClick = {
                        willFail = false
                        phase = TransferPhase.LOADING
                    },
                )

                SecondaryActionButton(
                    label = if (isLoading && willFail) "Memproses..." else "Simulasikan Gagal",
                    enabled = !isLoading,
                    onClick = {
                        willFail = true
                        phase = TransferPhase.LOADING
                    },
                )
            }
        }

        AnimatedVisibility(
            visible = phase == TransferPhase.SUCCESS || phase == TransferPhase.FAILED,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            ResultOverlay(
                isSuccess = phase == TransferPhase.SUCCESS,
                onDismiss = { phase = TransferPhase.IDLE },
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    emphasize: Boolean = false,
) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = palette.textSecondary,
        )
        Text(
            text = value,
            fontSize = if (emphasize) 14.sp else 12.sp,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.SemiBold,
            color = if (emphasize) palette.primary else palette.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun PrimaryActionButton(
    label: String,
    loading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) palette.primary else palette.surfaceMuted)
            .border(1.dp, if (enabled) palette.primary else palette.border, RoundedCornerShape(12.dp))
            .then(if (enabled) Modifier.pointerInput(Unit) { detectTapGestures { onClick() } } else Modifier)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = palette.onPrimary,
                strokeWidth = 2.dp,
            )
        }
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) palette.onPrimary else palette.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SecondaryActionButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surface)
            .border(1.dp, if (enabled) palette.danger else palette.border, RoundedCornerShape(12.dp))
            .then(if (enabled) Modifier.pointerInput(Unit) { detectTapGestures { onClick() } } else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) palette.danger else palette.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// ponytail: trim manual 2 segmen garis; upgrade ke PathMeasure bila butuh path kompleks.
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ResultOverlay(
    isSuccess: Boolean,
    onDismiss: () -> Unit,
) {
    // Back menutup overlay dulu, bukan layar
    BackHandler(enabled = true) { onDismiss() }

    val palette = LocalAppPalette.current
    val circleProgress = remember { Animatable(0f) }
    val markProgress = remember { Animatable(0f) }

    // Lingkaran 0->1, lalu centang/silang 0->1, lalu auto-tutup 2,5 detik
    LaunchedEffect(isSuccess) {
        circleProgress.snapTo(0f)
        markProgress.snapTo(0f)
        circleProgress.animateTo(1f, tween(durationMillis = 400, easing = FastOutSlowInEasing))
        markProgress.animateTo(1f, tween(durationMillis = 280))
        delay(2500)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background.copy(alpha = 0.9f))
            .navigationBarsPadding()
            .pointerInput(Unit) { detectTapGestures { onDismiss() } }
            .semantics { contentDescription = "Ketuk di mana saja untuk menutup" },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(palette.surface)
                .border(1.dp, palette.border, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            val markColor = if (isSuccess) palette.success else palette.danger
            val markDescription = if (isSuccess) "Ikon centang sukses" else "Ikon silang gagal"

            Canvas(
                modifier = Modifier
                    .size(104.dp)
                    .semantics { contentDescription = markDescription },
            ) {
                val sizePx = size.width
                val stroke = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)

                // Lingkaran digambar progresif (mulai dari atas, searah jarum jam)
                drawArc(
                    color = markColor,
                    startAngle = -90f,
                    sweepAngle = 360f * circleProgress.value,
                    useCenter = false,
                    style = stroke,
                )

                // Centang/silang: 2 segmen, trim manual per segmen
                val m = markProgress.value
                if (m > 0f) {
                    val t1 = (m * 2f).coerceAtMost(1f)
                    val t2 = ((m - 0.5f) * 2f).coerceIn(0f, 1f)
                    val start: Offset
                    val mid: Offset
                    val end: Offset
                    if (isSuccess) {
                        start = Offset(sizePx * 0.26f, sizePx * 0.52f)
                        mid = Offset(sizePx * 0.44f, sizePx * 0.70f)
                        end = Offset(sizePx * 0.75f, sizePx * 0.33f)
                    } else {
                        start = Offset(sizePx * 0.30f, sizePx * 0.30f)
                        mid = Offset(sizePx * 0.70f, sizePx * 0.70f)
                        end = Offset(sizePx * 0.70f, sizePx * 0.30f)
                    }
                    if (t1 > 0f) {
                        drawLine(
                            color = markColor,
                            start = start,
                            end = lerp(start, mid, t1),
                            strokeWidth = 6.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                    if (t2 > 0f) {
                        val secondStart = if (isSuccess) mid else Offset(sizePx * 0.30f, sizePx * 0.70f)
                        val secondEnd = end
                        drawLine(
                            color = markColor,
                            start = secondStart,
                            end = lerp(secondStart, secondEnd, t2),
                            strokeWidth = 6.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.graphicsLayer { alpha = markProgress.value },
            ) {
                Text(
                    text = if (isSuccess) "Transfer Berhasil" else "Transfer Gagal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSuccess) palette.success else palette.danger,
                )
                Text(
                    text = if (isSuccess) {
                        "$TransferAmount telah dikirim ke $TransferRecipient ($TransferBank $TransferAccount)."
                    } else {
                        "Transaksi $TransferAmount ke $TransferRecipient ditolak bank. Coba lagi atau hubungi layanan pelanggan."
                    },
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = palette.textSecondary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.primary)
                    .border(1.dp, palette.primary, RoundedCornerShape(12.dp))
                    .pointerInput(Unit) { detectTapGestures { onDismiss() } },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Tutup",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.onPrimary,
                    maxLines = 1,
                )
            }
        }
    }
}
