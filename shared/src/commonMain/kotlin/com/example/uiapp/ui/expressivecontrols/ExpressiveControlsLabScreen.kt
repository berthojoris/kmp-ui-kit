package com.example.uiapp.ui.expressivecontrols

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

enum class MotionIntensity(val title: String) {
    SUBTLE("Subtle (Rendah)"),
    EXPRESSIVE("Expressive (Tinggi)"),
}

@Composable
fun ExpressiveControlsLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()

    var intensity by remember { mutableStateOf(MotionIntensity.EXPRESSIVE) }
    var selectedSegment by remember { mutableStateOf(1) }
    var sliderValue by remember { mutableStateOf(65f) }
    var isLoadingButton by remember { mutableStateOf(false) }

    // Error shake animation state
    val shakeOffset = remember { Animatable(0f) }

    fun triggerShake() {
        scope.launch {
            shakeOffset.snapTo(0f)
            val amplitude = if (intensity == MotionIntensity.EXPRESSIVE) 16f else 8f
            shakeOffset.animateTo(
                targetValue = amplitude,
                animationSpec = tween(50),
            )
            shakeOffset.animateTo(
                targetValue = -amplitude,
                animationSpec = tween(50),
            )
            shakeOffset.animateTo(
                targetValue = amplitude / 2f,
                animationSpec = tween(50),
            )
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(50),
            )
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Material 3 Expressive",
                subtitle = "Bentuk dinamis, wavy loader, & tactile slider",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Motion Intensity Selector
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Intensitas Animasi:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                            )
                            Text(
                                text = if (intensity == MotionIntensity.EXPRESSIVE) "Bouncy spring & wavy flow aktif" else "Transisi presisi minimalis",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MotionIntensity.entries.forEach { opt ->
                                val isSel = intensity == opt
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) palette.primary else palette.surfaceMuted)
                                        .clickable { intensity = opt }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                ) {
                                    Text(
                                        text = opt.title.split(" ")[0],
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) palette.onPrimary else palette.textSecondary,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 1: Expressive Asymmetrical Shape Buttons
            item {
                SectionCard(title = "1. Variasi Bentuk & Interaksi Tombol", palette = palette) {
                    Text(
                        text = "Eksplorasi kontur asimetris (squircle, asymmetrical pill, pill berbadge) dengan multi-state:",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // Asymmetric Pill 1 (Top-Left 24dp, Bottom-Right 24dp)
                        var isPressed1 by remember { mutableStateOf(false) }
                        val scale1 by animateFloatAsState(
                            targetValue = if (isPressed1) 0.94f else 1f,
                            animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
                        )
                        Surface(
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 6.dp, bottomStart = 6.dp, bottomEnd = 24.dp),
                            color = palette.primary,
                            border = BorderStroke(1.dp, palette.primary),
                            modifier = Modifier
                                .weight(1f)
                                .scale(scale1)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            isPressed1 = true
                                            tryAwaitRelease()
                                            isPressed1 = false
                                        },
                                    )
                                },
                            shadowElevation = 0.dp,
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Asimetris Pill",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.onPrimary,
                                )
                            }
                        }

                        // Squircle Button with Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.weight(1f),
                            shadowElevation = 0.dp,
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    text = "Squircle",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(palette.primary)
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                ) {
                                    Text("NEW", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // Loading State Button
                        Surface(
                            onClick = {
                                isLoadingButton = !isLoadingButton
                                if (isLoadingButton) {
                                    scope.launch {
                                        kotlinx.coroutines.delay(2000)
                                        isLoadingButton = false
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = palette.surface,
                            border = BorderStroke(1.dp, palette.primary),
                            modifier = Modifier.weight(1f),
                            shadowElevation = 0.dp,
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 11.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (isLoadingButton) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = palette.primary,
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Memproses...", fontSize = 11.sp, color = palette.primary, fontWeight = FontWeight.Bold)
                                } else {
                                    Text("State Loading", fontSize = 12.sp, color = palette.primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Error Shake Button
                        Surface(
                            onClick = { triggerShake() },
                            shape = RoundedCornerShape(12.dp),
                            color = palette.danger.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, palette.danger),
                            modifier = Modifier
                                .weight(1f)
                                .offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
                            shadowElevation = 0.dp,
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 11.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "State Gagal (Shake)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.danger,
                                )
                            }
                        }
                    }
                }
            }

            // Section 2: Expressive Segmented Buttons
            item {
                SectionCard(title = "2. Segmented Dynamic Morpher", palette = palette) {
                    Text(
                        text = "Segmented control dengan highlight adaptif yang berpindah mulus:",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(palette.surfaceMuted)
                            .border(1.dp, palette.border, RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        listOf("Harian", "Mingguan", "Bulanan", "Tahunan").forEachIndexed { idx, title ->
                            val isSel = selectedSegment == idx
                            Surface(
                                onClick = { selectedSegment = idx },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) palette.primary else Color.Transparent,
                                border = if (isSel) BorderStroke(1.dp, palette.primary) else null,
                                modifier = Modifier.weight(1f),
                                shadowElevation = 0.dp,
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) palette.onPrimary else palette.textSecondary,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Wavy & Segmented Progress Indicators
            item {
                SectionCard(title = "3. Expressive Wavy Progress & Dots", palette = palette) {
                    Text(
                        text = "Indikator progress gelombang sinus (Wavy Progress) khas Material 3 Expressive:",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Wavy Canvas Indicator
                    val transition = rememberInfiniteTransition()
                    val wavePhase by transition.animateFloat(
                        initialValue = 0f,
                        targetValue = (2 * PI).toFloat(),
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = if (intensity == MotionIntensity.EXPRESSIVE) 1200 else 2400,
                                easing = LinearEasing,
                            ),
                            repeatMode = RepeatMode.Restart,
                        ),
                    )

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp),
                    ) {
                        val w = size.width
                        val h = size.height
                        val midY = h / 2f
                        val wavePath = Path()
                        val waveLength = 48.dp.toPx()
                        val waveHeight = 6.dp.toPx()

                        wavePath.moveTo(0f, midY)
                        var x = 0f
                        while (x <= w) {
                            val y = midY + sin((x / waveLength) * 2 * PI + wavePhase).toFloat() * waveHeight
                            wavePath.lineTo(x, y)
                            x += 4f
                        }

                        // Background track
                        drawLine(
                            color = palette.border,
                            start = Offset(0f, midY),
                            end = Offset(w, midY),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                        )

                        // Foreground wave
                        drawPath(
                            path = wavePath,
                            color = palette.primary,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round,
                            ),
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Segmented Dot Stage Progress:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textMuted,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Segmented Dots
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        for (i in 0..5) {
                            val isCompleted = i <= 3
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .padding(horizontal = 2.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (isCompleted) palette.primary else palette.surfaceMuted)
                                    .border(1.dp, if (isCompleted) palette.primary else palette.border, RoundedCornerShape(3.dp)),
                            )
                        }
                    }
                }
            }

            // Section 4: Tactile Slider with Value Badge
            item {
                SectionCard(title = "4. Tactile Slider & Floating Badge", palette = palette) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Level Kapasitas Memory Cache:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary,
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(palette.primary)
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        ) {
                            Text(
                                text = "${sliderValue.roundToInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.onPrimary,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = palette.primary,
                            activeTrackColor = palette.primary,
                            inactiveTrackColor = palette.surfaceMuted,
                        ),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("0% (Minimal)", fontSize = 10.sp, color = palette.textMuted)
                        Text("50% (Standard)", fontSize = 10.sp, color = palette.textMuted)
                        Text("100% (Maksimal)", fontSize = 10.sp, color = palette.textMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    palette: com.example.uiapp.theme.AppPalette,
    content: @Composable () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
