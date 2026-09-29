package com.example.uiapp.ui.feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LightAppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class ToastKind(val accent: Color, val label: String) {
    SUCCESS(LightAppPalette.success, "Berhasil"),
    INFO(LightAppPalette.info, "Informasi"),
    WARNING(LightAppPalette.warning, "Peringatan"),
    DANGER(LightAppPalette.danger, "Gagal"),
}

private class ToastMessage(
    val id: Int,
    val text: String,
    val kind: ToastKind,
) {
    var visible by mutableStateOf(true)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FeedbackLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val toasts = remember { mutableStateListOf<ToastMessage>() }
    var nextId by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    var bannerKey by remember { mutableIntStateOf(0) }
    var bannerVisible by remember { mutableStateOf(false) }

    fun pushToast(kind: ToastKind, text: String) {
        val message = ToastMessage(nextId++, text, kind)
        toasts.add(message)
        scope.launch {
            delay(2700)
            message.visible = false
            delay(320)
            toasts.remove(message)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LuxuryColors.Background),
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                UiTopBar(
                    title = "Feedback Lab",
                    subtitle = "Toast bertumpuk & banner iOS",
                    onBack = onBack,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "TOAST BERTUMPUK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.6.sp,
                    color = LuxuryColors.TextMuted,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Ketuk beberapa kali berturut-turut untuk melihat antrean toast bertumpuk.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = LuxuryColors.TextSecondary,
                )
                Spacer(modifier = Modifier.height(14.dp))

                FeedbackButton(
                    label = "Tampilkan Toast Sukses",
                    accent = LuxuryColors.Accent,
                    onClick = { pushToast(ToastKind.SUCCESS, "Perubahan berhasil disimpan.") },
                )
                Spacer(modifier = Modifier.height(10.dp))
                FeedbackButton(
                    label = "Tampilkan Toast Informasi",
                    accent = LuxuryColors.AccentInfo,
                    onClick = { pushToast(ToastKind.INFO, "Sinkronisasi berjalan di latar belakang.") },
                )
                Spacer(modifier = Modifier.height(10.dp))
                FeedbackButton(
                    label = "Tampilkan Toast Peringatan",
                    accent = LuxuryColors.AccentWarning,
                    onClick = { pushToast(ToastKind.WARNING, "Kuota penyimpanan hampir penuh.") },
                )
                Spacer(modifier = Modifier.height(10.dp))
                FeedbackButton(
                    label = "Tampilkan Toast Gagal",
                    accent = LuxuryColors.AccentDanger,
                    onClick = { pushToast(ToastKind.DANGER, "Koneksi gagal. Coba lagi nanti.") },
                )

                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "NOTIFICATION BANNER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.6.sp,
                    color = LuxuryColors.TextMuted,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Banner gaya iOS turun dari atas dengan hitungan waktu.",
                    fontSize = 12.sp,
                    color = LuxuryColors.TextSecondary,
                )
                Spacer(modifier = Modifier.height(14.dp))
                FeedbackButton(
                    label = "Tampilkan Banner",
                    accent = LuxuryColors.TealPrimary,
                    onClick = {
                        bannerKey++
                        bannerVisible = true
                    },
                )
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        AnimatedVisibility(
            visible = bannerVisible,
            enter = slideInVertically(tween(320)) { -it } + fadeIn(tween(220)),
            exit = slideOutVertically(tween(260)) { -it } + fadeOut(tween(180)),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
        ) {
            NotificationBanner(
                key = bannerKey,
                onClose = { bannerVisible = false },
                onFinished = { bannerVisible = false },
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
                )
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            toasts.forEach { toast ->
                androidx.compose.animation.AnimatedVisibility(
                    visible = toast.visible,
                    enter = slideInVertically(tween(260)) { it } + fadeIn(tween(200)),
                    exit = slideOutVertically(tween(200)) { it } + fadeOut(tween(160)),
                ) {
                    ToastCard(toast = toast)
                }
            }
        }
    }
}

@Composable
private fun FeedbackButton(label: String, accent: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = LuxuryColors.SurfaceWhite,
        border = BorderStroke(1.dp, LuxuryColors.SurfaceBorder),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(accent),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = LuxuryColors.TextPrimary,
            )
            Text(
                text = "\u203A",
                fontSize = 18.sp,
                color = LuxuryColors.TextMuted,
            )
        }
    }
}

@Composable
private fun ToastCard(toast: ToastMessage) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = LuxuryColors.SurfaceWhite,
        border = BorderStroke(1.dp, LuxuryColors.SurfaceBorder),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(toast.kind.accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                FeedbackGlyph(kind = toast.kind, tint = toast.kind.accent)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = toast.kind.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = toast.kind.accent,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = toast.text,
                    fontSize = 12.sp,
                    color = LuxuryColors.TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { toast.visible = false },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "\u00D7",
                    fontSize = 16.sp,
                    color = LuxuryColors.TextMuted,
                )
            }
        }
    }
}

@Composable
private fun NotificationBanner(
    key: Int,
    onClose: () -> Unit,
    onFinished: () -> Unit,
) {
    val progress = remember { Animatable(1f) }

    LaunchedEffect(key) {
        progress.snapTo(1f)
        progress.animateTo(0f, animationSpec = tween(durationMillis = 4200))
        onFinished()
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        color = LuxuryColors.SurfaceWhite,
        border = BorderStroke(1.dp, LuxuryColors.SurfaceBorder),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(LuxuryColors.TealPrimary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "T",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Testing UI",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuxuryColors.TextPrimary,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "sekarang",
                            fontSize = 11.sp,
                            color = LuxuryColors.TextMuted,
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Reservasi baru dikonfirmasi untuk Villa Ubud pada 12 Sep.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = LuxuryColors.TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "\u00D7",
                        fontSize = 16.sp,
                        color = LuxuryColors.TextMuted,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 0.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.value)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(LuxuryColors.TealPrimary),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun FeedbackGlyph(kind: ToastKind, tint: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(
            width = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        when (kind) {
            ToastKind.SUCCESS -> {
                drawLine(
                    color = tint,
                    start = Offset(w * 0.16f, h * 0.54f),
                    end = Offset(w * 0.42f, h * 0.78f),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.42f, h * 0.78f),
                    end = Offset(w * 0.86f, h * 0.24f),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }

            ToastKind.INFO -> {
                drawCircle(color = tint, radius = w * 0.08f, center = Offset(w / 2, h * 0.24f))
                drawLine(
                    color = tint,
                    start = Offset(w / 2, h * 0.44f),
                    end = Offset(w / 2, h * 0.8f),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }

            ToastKind.WARNING, ToastKind.DANGER -> {
                drawCircle(color = tint, radius = w * 0.44f, center = Offset(w / 2, h / 2), style = stroke)
                drawLine(
                    color = tint,
                    start = Offset(w / 2, h * 0.26f),
                    end = Offset(w / 2, h * 0.56f),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                drawCircle(color = tint, radius = w * 0.06f, center = Offset(w / 2, h * 0.72f))
            }
        }
    }
}
