package com.example.uiapp.ui.splitcomparison

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// Custom Shape yang memotong kontainer berdasarkan fraksi horizontal (0f..1f)
private class FractionClipShape(private val fraction: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val width = size.width * fraction.coerceIn(0f, 1f)
        return Outline.Rectangle(Rect(0f, 0f, width, size.height))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SplitComparisonLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()
    val splitFraction = remember { Animatable(0.5f) }
    var isAutoPlaying by remember { mutableStateOf(false) }
    var autoPlayJob by remember { mutableStateOf<Job?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.surface)
            .navigationBarsPadding(),
    ) {
        UiTopBar(
            title = "Split Comparison",
            subtitle = "Before & after interactive divider slider",
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Deskripsi Konsep
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .padding(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "✨ Trend UI/UX X.com & Design Engineering",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "Komponen pembanding visual interaktif (Before vs After) dengan handle drag vertikal. Sering digunakan pada aplikasi kamera AI, retouching, Figma plugins, dan komparasi Light vs Dark mode.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = palette.textSecondary,
                    )
                }
            }

            // Quick Preset Controls
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Posisi Cepat & Mode Otomatis",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf(
                        0.25f to "25% Before",
                        0.50f to "50% Seimbang",
                        0.75f to "75% After",
                    ).forEach { (frac, label) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(palette.surfaceMuted)
                                .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                                .pointerInput(frac) {
                                    detectTapGestures {
                                        autoPlayJob?.cancel()
                                        isAutoPlaying = false
                                        scope.launch {
                                            splitFraction.animateTo(frac, tween(300, easing = FastOutSlowInEasing))
                                        }
                                    }
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary,
                            )
                        }
                    }

                    // Tombol Auto Sweep Play/Pause
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isAutoPlaying) palette.primary else palette.surfaceMuted)
                            .border(1.dp, if (isAutoPlaying) palette.primary else palette.border, RoundedCornerShape(8.dp))
                            .pointerInput(isAutoPlaying) {
                                detectTapGestures {
                                    if (isAutoPlaying) {
                                        autoPlayJob?.cancel()
                                        isAutoPlaying = false
                                    } else {
                                        isAutoPlaying = true
                                        autoPlayJob = scope.launch {
                                            while (true) {
                                                splitFraction.animateTo(0.15f, tween(1600, easing = FastOutSlowInEasing))
                                                splitFraction.animateTo(0.85f, tween(1600, easing = FastOutSlowInEasing))
                                            }
                                        }
                                    }
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (isAutoPlaying) "Jeda ⏸" else "Sweep 🔁",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAutoPlaying) Color.White else palette.textPrimary,
                        )
                    }
                }
            }

            // Demo 1: AI Camera Enhancement (RAW vs AI Vivid)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. AI Visual Enhancer (RAW Kamera vs AI 4K)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                )
                InteractiveComparisonCard(
                    height = 240.dp,
                    fraction = splitFraction.value,
                    onFractionChange = { newFraction ->
                        if (isAutoPlaying) {
                            autoPlayJob?.cancel()
                            isAutoPlaying = false
                        }
                        scope.launch { splitFraction.snapTo(newFraction) }
                    },
                    beforeContent = {
                        // Sisi RAW (Warna Muted, Grayscale-ish, Kontras Rendah)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B))))
                                .padding(16.dp),
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                ) {
                                    Text(
                                        text = "SEBELUM (RAW 720p)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF94A3B8),
                                    )
                                }
                                Text(
                                    text = "Landscape Pegunungan",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFCBD5E1),
                                )
                                Text(
                                    text = "Detail sensor standar tanpa neural processing. Saturasi warna 52%, noise reduction 12%.",
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    color = Color(0xFF94A3B8),
                                )
                            }
                        }
                    },
                    afterContent = {
                        // Sisi AI Enhanced (Vivid Teal & Emerald Glow, Tajam)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(listOf(Color(0xFF0F766E), Color(0xFF064E3B))))
                                .padding(16.dp),
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF10B981).copy(alpha = 0.35f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                ) {
                                    Text(
                                        text = "SESUDAH (AI HDR 4K)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFA7F3D0),
                                    )
                                }
                                Text(
                                    text = "Landscape Pegunungan",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )
                                Text(
                                    text = "Neural Super-Resolution 4x aktif. Kontras dinamis 98%, warna HDR DCI-P3 100%.",
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    color = Color(0xFFA7F3D0),
                                )
                            }
                        }
                    },
                )
            }

            // Demo 2: Light Mode vs Dark Mode Interface Split
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "2. UI Theme Splitter (Light Theme vs Dark Theme)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                )
                InteractiveComparisonCard(
                    height = 190.dp,
                    fraction = splitFraction.value,
                    onFractionChange = { newFraction ->
                        if (isAutoPlaying) {
                            autoPlayJob?.cancel()
                            isAutoPlaying = false
                        }
                        scope.launch { splitFraction.snapTo(newFraction) }
                    },
                    beforeContent = {
                        // Tema Terang
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF8FAFC))
                                .padding(16.dp),
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("☀️ Light Theme Preview", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White, RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                        .padding(12.dp),
                                ) {
                                    Text("Kartu Komponen Putih Bersih (Zero Shadow)", fontSize = 12.sp, color = Color(0xFF475569))
                                }
                            }
                        }
                    },
                    afterContent = {
                        // Tema Gelap
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF090D16))
                                .padding(16.dp),
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("🌙 Dark Theme Preview", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF1F5F9))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF131A29), RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                        .padding(12.dp),
                                ) {
                                    Text("Kartu Komponen Hitam Pekat High-Contrast", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                }
                            }
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InteractiveComparisonCard(
    height: androidx.compose.ui.unit.Dp,
    fraction: Float,
    onFractionChange: (Float) -> Unit,
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
) {
    val palette = LocalAppPalette.current
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
    ) {
        val totalWidthPx = with(density) { maxWidth.toPx() }
        val currentDividerPx = totalWidthPx * fraction.coerceIn(0f, 1f)
        val currentDividerDp = with(density) { currentDividerPx.toDp() }

        // Layer Dasar (Before)
        Box(modifier = Modifier.fillMaxSize()) {
            beforeContent()
        }

        // Layer Atas (After) - Dipotong menggunakan custom clip shape
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(FractionClipShape(fraction)),
        ) {
            afterContent()
        }

        // Garis Pembatas Vertikal (1.5dp)
        Box(
            modifier = Modifier
                .offset { IntOffset(currentDividerPx.roundToInt(), 0) }
                .width(2.dp)
                .fillMaxHeight()
                .background(Color.White),
        )

        // Handle Drag Tengah (Pill dengan Panah)
        Box(
            modifier = Modifier
                .offset { IntOffset(currentDividerPx.roundToInt() - with(density) { 20.dp.toPx() }.roundToInt(), with(density) { (height / 2 - 20.dp).toPx() }.roundToInt()) }
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, Color(0xFF0F172A), CircleShape)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val newFraction = ((currentDividerPx + delta) / totalWidthPx).coerceIn(0.05f, 0.95f)
                        onFractionChange(newFraction)
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "◀ ▶",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
            )
        }
    }
}
