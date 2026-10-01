package com.example.uiapp.ui.audioequalizer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

private val BandLabels = listOf("31", "62", "125", "250", "500", "1k", "2k", "4k", "8k", "16k")

private data class Preset(val name: String, val values: List<Float>)

private val Presets = listOf(
    Preset("Datar", List(10) { 0f }),
    Preset("Bass", listOf(10f, 8f, 6f, 3f, 0f, 0f, 0f, 0f, 0f, 0f)),
    Preset("Vokal", listOf(-2f, 0f, 2f, 6f, 8f, 8f, 6f, 2f, 0f, -2f)),
    Preset("Rock", listOf(8f, 6f, 2f, -2f, -4f, -2f, 2f, 6f, 8f, 10f)),
    Preset("Pop", listOf(2f, 4f, 6f, 4f, 2f, 2f, 4f, 6f, 4f, 2f)),
    Preset("Jazz", listOf(4f, 3f, 0f, 2f, -2f, -2f, 0f, 2f, 4f, 5f)),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AudioEqualizerLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var bands by remember { mutableStateOf(List(10) { 0f }) }
    var isActive by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(false) }
    var volume by remember { mutableFloatStateOf(0.75f) }
    var visualizerHeights by remember { mutableStateOf(List(32) { 0f }) }
    var selectedPreset by remember { mutableStateOf("Datar") }

    // Visualizer animation
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            visualizerHeights = List(32) { i ->
                val bandIdx = (i * 10f / 32f).toInt().coerceIn(0, 9)
                val boost = if (isActive) (bands[bandIdx] + 12f) / 24f else 0.5f
                (Random.nextFloat() * 0.6f + 0.1f) * boost * volume
            }
            delay(80)
        }
        if (!isPlaying) visualizerHeights = List(32) { 0f }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Audio Equalizer", subtitle = "Simulasi equalizer 10-band", onBack = onBack) },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()),
        ) {
            // Equalizer toggle
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(if (isActive) "Equalizer Aktif" else "Equalizer Nonaktif", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                Surface(
                    onClick = { isActive = !isActive },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isActive) palette.primary else palette.surfaceMuted,
                    border = BorderStroke(1.dp, if (isActive) palette.primary else palette.border),
                    shadowElevation = 0.dp, tonalElevation = 0.dp,
                ) {
                    Text(if (isActive) "AKTIF" else "NONAKTIF", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isActive) palette.onPrimary else palette.textMuted)
                }
            }

            // Preset chips
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            ) {
                items(Presets, key = { it.name }) { preset ->
                    Surface(
                        onClick = { bands = preset.values.toList(); selectedPreset = preset.name },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedPreset == preset.name) palette.primary else palette.surface,
                        border = BorderStroke(1.dp, if (selectedPreset == preset.name) palette.primary else palette.border),
                        shadowElevation = 0.dp, tonalElevation = 0.dp,
                    ) {
                        Text(preset.name, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (selectedPreset == preset.name) palette.onPrimary else palette.textSecondary)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // EQ Sliders
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp, tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        bands.forEachIndexed { idx, value ->
                            val animValue by animateFloatAsState(value, tween(300))
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("${animValue.roundToInt()}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                                Box(
                                    modifier = Modifier.weight(1f).width(24.dp).pointerInput(idx) {
                                        detectVerticalDragGestures { _, dragAmount ->
                                            val newVal = (bands[idx] - dragAmount / 8f).coerceIn(-12f, 12f)
                                            bands = bands.toMutableList().also { it[idx] = newVal }
                                            selectedPreset = ""
                                        }
                                    },
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val trackX = size.width / 2
                                        // Track
                                        drawLine(palette.border, Offset(trackX, 0f), Offset(trackX, size.height), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                                        // 0dB line
                                        drawLine(palette.textMuted, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), strokeWidth = 1.dp.toPx())
                                        // Thumb
                                        val norm = (12f - animValue) / 24f
                                        val thumbY = norm * size.height
                                        drawCircle(palette.primary, radius = 7.dp.toPx(), center = Offset(trackX, thumbY))
                                        // Active bar
                                        drawLine(palette.primary, Offset(trackX, size.height / 2), Offset(trackX, thumbY), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                                    }
                                }
                                Text(BandLabels[idx], fontSize = 8.sp, color = palette.textMuted)
                            }
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("-12 dB", fontSize = 9.sp, color = palette.textMuted)
                        Text("0 dB", fontSize = 9.sp, color = palette.textMuted)
                        Text("+12 dB", fontSize = 9.sp, color = palette.textMuted)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Visualizer
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp, tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Visualizer Spektrum", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                        Surface(
                            onClick = { isPlaying = !isPlaying },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isPlaying) palette.danger.copy(alpha = 0.15f) else palette.primary.copy(alpha = 0.15f),
                            shadowElevation = 0.dp, tonalElevation = 0.dp,
                        ) {
                            Text(if (isPlaying) "Berhenti" else "Putar", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isPlaying) palette.danger else palette.primary)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                        val barW = size.width / 34f
                        val gap = barW * 0.3f
                        visualizerHeights.forEachIndexed { i, h ->
                            val barH = h * size.height
                            val x = i * (barW + gap)
                            val lerp = i / 31f
                            val barColor = androidx.compose.ui.graphics.lerp(palette.primary, palette.info, lerp)
                            drawRoundRect(
                                color = barColor,
                                topLeft = Offset(x, size.height - barH),
                                size = Size(barW, barH.coerceAtLeast(2.dp.toPx())),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Volume
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Volume", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                Spacer(Modifier.width(12.dp))
                androidx.compose.material3.Slider(
                    value = volume,
                    onValueChange = { volume = it },
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(thumbColor = palette.primary, activeTrackColor = palette.primary, inactiveTrackColor = palette.surfaceMuted),
                )
                Spacer(Modifier.width(8.dp))
                Text("${(volume * 100).roundToInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.primary)
            }

            Spacer(Modifier.height(8.dp))
            Text("Simulasi — bukan pemroses audio sungguhan", fontSize = 10.sp, color = palette.textMuted, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        }
    }
}
