package com.example.uiapp.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SplashScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit = onBack,
    onNavigateToOnboarding: (() -> Unit)? = null,
) {
    val palette = LocalAppPalette.current
    var isFullscreenActive by remember { mutableStateOf(false) }
    var selectedDurationMs by remember { mutableIntStateOf(2200) }
    var replayTrigger by remember { mutableIntStateOf(0) }

    BackHandler(enabled = true) {
        if (isFullscreenActive) {
            isFullscreenActive = false
        } else {
            onBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Component Lab Screen
        Scaffold(
            containerColor = palette.background,
            topBar = {
                UiTopBar(
                    title = "Splash Screen",
                    subtitle = "Animated Brand Reveal & Lifecycle Lab",
                    onBack = onBack,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Interactive Preview Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Pratinjau Animasi Splash",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = palette.textPrimary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Brand reveal, spring scale physics, dan progress bar sinkron",
                            fontSize = 12.sp,
                            color = palette.textMuted,
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Scaled-down Splash Container Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(palette.primary)
                                .border(1.dp, palette.border, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            SplashAnimatedContent(
                                durationMs = selectedDurationMs,
                                replayKey = replayTrigger,
                                palette = palette,
                                compact = true,
                                onCompleted = { /* Preview loops or stays finished */ },
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Controls Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Surface(
                                onClick = { replayTrigger++ },
                                shape = RoundedCornerShape(10.dp),
                                color = palette.surfaceMuted,
                                border = BorderStroke(1.dp, palette.border),
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    text = "Putar Ulang 🔄",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                )
                            }

                            Surface(
                                onClick = { isFullscreenActive = true },
                                shape = RoundedCornerShape(10.dp),
                                color = palette.primary,
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    text = "Layar Penuh 📱",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                )
                            }
                        }
                    }
                }

                // Configuration Panel
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Pengaturan Durasi Loading:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            listOf(
                                1200 to "Cepat (1.2s)",
                                2200 to "Standar (2.2s)",
                                3500 to "Lambat (3.5s)",
                            ).forEach { (ms, label) ->
                                val isSelected = selectedDurationMs == ms
                                Surface(
                                    onClick = {
                                        selectedDurationMs = ms
                                        replayTrigger++
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) palette.primaryContainer else palette.surfaceMuted,
                                    border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) palette.primary else palette.textSecondary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                    )
                                }
                            }
                        }

                        if (onNavigateToOnboarding != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                onClick = onNavigateToOnboarding,
                                shape = RoundedCornerShape(10.dp),
                                color = palette.surfaceMuted,
                                border = BorderStroke(1.dp, palette.border),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column {
                                        Text("Uji Alur Onboarding Wizard", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = palette.textPrimary)
                                        Text("Lanjutkan eksplorasi ke Onboarding ➔ Auth ➔ Profil", fontSize = 10.sp, color = palette.textMuted)
                                    }
                                    Text("➔", fontSize = 16.sp, color = palette.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Architecture Guide Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Solusi Navigasi & Lifecycle Splash Screen:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "• Pembersihan Backstack Otomatis: Splash screen tidak boleh tertinggal di backstack. Saat berpindah ke layar utama, root disanitasi agar tombol Back tidak mengulang splash.",
                            fontSize = 12.sp,
                            color = palette.textSecondary,
                            lineHeight = 17.sp,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "• Anti-Infinite Loop: Membatalkan timer LaunchedEffect saat keluar agar tidak terjadi auto-navigation tak terduga.",
                            fontSize = 12.sp,
                            color = palette.textSecondary,
                            lineHeight = 17.sp,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "• Universal iOS/Android Back: Dilengkapi tombol tutup visual dan BackHandler terpusat.",
                            fontSize = 12.sp,
                            color = palette.textSecondary,
                            lineHeight = 17.sp,
                        )
                    }
                }
            }
        }

        // Immersive Fullscreen Splash Simulation Overlay
        AnimatedVisibility(
            visible = isFullscreenActive,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(200)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(palette.primary),
            ) {
                // Top close button for iOS & Android
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(16.dp)
                        .align(Alignment.TopStart),
                ) {
                    Surface(
                        onClick = { isFullscreenActive = false },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        modifier = Modifier.size(38.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("✕", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Fullscreen animated content
                SplashAnimatedContent(
                    durationMs = selectedDurationMs,
                    replayKey = replayTrigger,
                    palette = palette,
                    compact = false,
                    onCompleted = {
                        isFullscreenActive = false
                    },
                    onSkipClicked = {
                        isFullscreenActive = false
                    },
                )
            }
        }
    }
}

@Composable
private fun SplashAnimatedContent(
    durationMs: Int,
    replayKey: Int,
    palette: com.example.uiapp.theme.AppPalette,
    compact: Boolean,
    onCompleted: () -> Unit,
    onSkipClicked: (() -> Unit)? = null,
) {
    val logoScale = remember(replayKey) { Animatable(0.6f) }
    val fade = remember(replayKey) { Animatable(0f) }
    val progress = remember(replayKey) { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition()
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    LaunchedEffect(replayKey) {
        logoScale.snapTo(0.6f)
        fade.snapTo(0f)
        progress.snapTo(0f)

        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow),
            )
        }
        launch {
            fade.animateTo(1f, animationSpec = tween(500))
        }
        launch {
            progress.animateTo(1f, animationSpec = tween(durationMs, easing = LinearEasing))
            onCompleted()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Center Brand & Titles
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer { alpha = fade.value },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = logoScale.value * if (!compact) pulseGlow else 1f
                        scaleY = logoScale.value * if (!compact) pulseGlow else 1f
                    },
            ) {
                BrandMark(size = if (compact) 64.dp else 92.dp, tint = Color.White)
            }

            Spacer(modifier = Modifier.height(if (compact) 12.dp else 20.dp))

            Text(
                text = "Testing UI",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = if (compact) 22.sp else 30.sp,
                letterSpacing = (-0.5).sp,
                color = Color.White,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Kit eksperimen komponen Compose Multiplatform",
                fontSize = if (compact) 11.sp else 13.sp,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }

        // Bottom Loading Progress & Skip
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = if (compact) 20.dp else 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .width(if (compact) 120.dp else 160.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.25f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.value)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (progress.value < 1f) "Menyiapkan antarmuka\u2026 ${(progress.value * 100).toInt()}%" else "Siap!",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
            )

            if (onSkipClicked != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    onClick = onSkipClicked,
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                ) {
                    Text(
                        text = "Lewati",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun BrandMark(
    size: Dp,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(
            width = (w * 0.045f),
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = tint.copy(alpha = 0.9f),
            radius = w * 0.42f,
            center = Offset(w / 2, h / 2),
            style = stroke,
        )
        val north = Path().apply {
            moveTo(w * 0.50f, h * 0.20f)
            lineTo(w * 0.64f, h * 0.50f)
            lineTo(w * 0.36f, h * 0.50f)
            close()
        }
        drawPath(north, tint)
        val south = Path().apply {
            moveTo(w * 0.50f, h * 0.80f)
            lineTo(w * 0.36f, h * 0.50f)
            lineTo(w * 0.64f, h * 0.50f)
            close()
        }
        drawPath(south, tint.copy(alpha = 0.35f))
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w / 2, h / 2))
    }
}
