package com.example.uiapp.ui.biometric

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private enum class BioState(val title: String, val message: String) {
    LOCKED("Buka dengan Face ID", "Pindai wajah Anda untuk mengakses akun"),
    SCANNING("Memindai\u2026", "Posisikan wajah di dalam bingkai"),
    FAILED("Wajah tidak dikenali", "Coba lagi atau gunakan PIN perangkat"),
    SUCCESS("Berhasil dibuka", "Selamat datang kembali, Ayu"),
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BiometricScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    BackHandler(enabled = true) { onBack() }

    var state by remember { mutableStateOf(BioState.LOCKED) }
    var failedOnce by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state == BioState.SCANNING) {
            delay(1400)
            state = if (failedOnce) BioState.SUCCESS else BioState.FAILED
            failedOnce = true
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Biometric",
                subtitle = "Face ID prompt sheet",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                if (state == BioState.SUCCESS) {
                    UnlockedContent()
                } else {
                    LockedContent()
                }
            }

            BiometricSheet(
                state = state,
                onScan = {
                    failedOnce = false
                    state = BioState.SCANNING
                },
                onRetry = { state = BioState.SCANNING },
                onPin = { state = BioState.LOCKED },
            )
        }
    }
}

@Composable
private fun LockedContent() {
    val palette = LocalAppPalette.current

    Column(
        modifier = Modifier.padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(palette.surfaceMuted)
                .border(1.dp, palette.border, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(38.dp)) {
                val stroke = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
                val w = size.width
                val h = size.height
                drawRoundRect(
                    color = palette.textPrimary,
                    topLeft = Offset(w * 0.24f, h * 0.38f),
                    size = androidx.compose.ui.geometry.Size(w * 0.52f, h * 0.44f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.08f, w * 0.08f),
                    style = stroke,
                )
                val shackle = Path().apply {
                    moveTo(w * 0.34f, h * 0.38f)
                    lineTo(w * 0.34f, h * 0.26f)
                    cubicTo(w * 0.34f, h * 0.06f, w * 0.66f, h * 0.06f, w * 0.66f, h * 0.26f)
                    lineTo(w * 0.66f, h * 0.38f)
                }
                drawPath(shackle, palette.textPrimary, style = stroke)
                drawCircle(color = palette.textPrimary, radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.6f))
            }
        }
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = "Konten terkunci",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = palette.textPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Aktifkan autentikasi biometrik untuk membuka data akun Anda.",
            fontSize = 13.sp,
            lineHeight = 20.sp,
            color = palette.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun UnlockedContent() {
    val palette = LocalAppPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(palette.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(42.dp)) {
                drawCircle(
                    color = palette.primary,
                    radius = size.minDimension * 0.44f,
                    style = Stroke(width = 2.6.dp.toPx()),
                )
                val check = Path().apply {
                    moveTo(size.width * 0.3f, size.height * 0.52f)
                    lineTo(size.width * 0.45f, size.height * 0.67f)
                    lineTo(size.width * 0.72f, size.height * 0.34f)
                }
                drawPath(
                    path = check,
                    color = palette.primary,
                    style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Terkunci dibuka",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = palette.textPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Data akun kini dapat diakses.",
            fontSize = 13.sp,
            color = palette.textSecondary,
        )
    }
}

@Composable
private fun BiometricSheet(
    state: BioState,
    onScan: () -> Unit,
    onRetry: () -> Unit,
    onPin: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(palette.border),
            )
            Spacer(modifier = Modifier.height(22.dp))

            FaceIdIcon(
                state = state,
                modifier = Modifier.size(72.dp),
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = state.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = if (state == BioState.FAILED) palette.danger else palette.textPrimary,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = state.message,
                fontSize = 13.sp,
                color = palette.textSecondary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(22.dp))

            when (state) {
                BioState.LOCKED -> {
                    FlatPrimaryButton(text = "Pindai Wajah", onClick = onScan)
                    Spacer(modifier = Modifier.height(10.dp))
                    FlatTextButton(text = "Gunakan PIN", onClick = onPin)
                }

                BioState.SCANNING -> {
                    FlatPrimaryButton(text = "Memindai\u2026", onClick = {}, enabled = false)
                }

                BioState.FAILED -> {
                    FlatPrimaryButton(text = "Coba Lagi", onClick = onRetry)
                    Spacer(modifier = Modifier.height(10.dp))
                    FlatTextButton(text = "Gunakan PIN", onClick = onPin)
                }

                BioState.SUCCESS -> {
                    FlatPrimaryButton(text = "Selesai", onClick = onPin)
                }
            }
        }
    }
}

@Composable
private fun FlatTextButton(text: String, onClick: () -> Unit) {
    val palette = LocalAppPalette.current

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 14.dp),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun FaceIdIcon(state: BioState, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current

    val transition = rememberInfiniteTransition(label = "faceScan")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "facePulse",
    )
    val tint = when (state) {
        BioState.SUCCESS -> palette.success
        BioState.FAILED -> palette.danger
        else -> palette.primary
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        if (state == BioState.SCANNING) {
            val radius = w * (0.34f + 0.5f * pulse)
            drawCircle(
                color = tint.copy(alpha = (1f - pulse) * 0.4f),
                radius = radius,
                center = Offset(w / 2, h / 2),
                style = Stroke(width = 2.dp.toPx()),
            )
        }

        val inset = w * 0.12f
        val corner = w * 0.2f
        listOf(
            Offset(inset, inset),
            Offset(w - inset, inset),
            Offset(inset, h - inset),
            Offset(w - inset, h - inset),
        ).forEachIndexed { index, point ->
            val dx = if (index % 2 == 0) 1f else -1f
            val dy = if (index < 2) 1f else -1f
            drawLine(tint, point, Offset(point.x + corner * dx, point.y), 2.4.dp.toPx(), StrokeCap.Round)
            drawLine(tint, point, Offset(point.x, point.y + corner * dy), 2.4.dp.toPx(), StrokeCap.Round)
        }

        drawCircle(color = tint, radius = w * 0.035f, center = Offset(w * 0.38f, h * 0.44f))
        drawCircle(color = tint, radius = w * 0.035f, center = Offset(w * 0.62f, h * 0.44f))
        drawLine(tint, Offset(w * 0.5f, h * 0.44f), Offset(w * 0.5f, h * 0.58f), 2.dp.toPx(), StrokeCap.Round)
        val smile = Path().apply {
            moveTo(w * 0.4f, h * 0.66f)
            cubicTo(w * 0.46f, h * 0.72f, w * 0.54f, h * 0.72f, w * 0.6f, h * 0.66f)
        }
        drawPath(smile, tint, style = stroke)
    }
}
