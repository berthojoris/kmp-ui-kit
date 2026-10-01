package com.example.uiapp.ui.syncedlyrics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private data class LyricLine(val timestamp: Int, val text: String)

private val LyricsData = listOf(
    LyricLine(0, "Di balik awan yang kelabu"),
    LyricLine(4, "Tersimpan mimpi yang membiru"),
    LyricLine(8, "Langkah kaki menyusuri jalan"),
    LyricLine(12, "Mengejar harapan yang terpendam"),
    LyricLine(17, "Bintang-bintang menyala terang"),
    LyricLine(21, "Menerangi malam yang panjang"),
    LyricLine(26, "Kita berlari tanpa henti"),
    LyricLine(30, "Memeluk waktu yang tak kembali"),
    LyricLine(35, "Angin berbisik di telinga"),
    LyricLine(39, "Cerita lama yang tak terlupa"),
    LyricLine(44, "Hujan turun membasahi bumi"),
    LyricLine(48, "Menyegarkan hati yang sepi"),
    LyricLine(53, "Di ujung pelangi yang indah"),
    LyricLine(57, "Kita temukan jalan pulang"),
    LyricLine(62, "Bersama langkah yang pasti"),
    LyricLine(66, "Kita raih mimpi abadi"),
    LyricLine(71, "Sampai akhir cerita ini"),
    LyricLine(75, "Kita tetap bersinar di sini"),
)

private const val TotalDuration = 80

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "${if (m < 10) "0$m" else "$m"}:${if (s < 10) "0$s" else "$s"}"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SyncedLyricsLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var isPlaying by remember { mutableStateOf(false) }
    var currentTime by remember { mutableIntStateOf(0) }
    var speed by remember { mutableFloatStateOf(1f) }
    val listState = rememberLazyListState()

    val currentLineIndex = remember(currentTime) {
        val idx = LyricsData.indexOfLast { it.timestamp <= currentTime }
        if (idx < 0) 0 else idx
    }

    // Auto-advance playback
    LaunchedEffect(isPlaying, speed) {
        while (isPlaying && currentTime < TotalDuration) {
            delay((1000 / speed).toLong())
            currentTime++
        }
        if (currentTime >= TotalDuration) isPlaying = false
    }

    // Auto-scroll to current line
    LaunchedEffect(currentLineIndex) {
        if (currentLineIndex > 0) {
            listState.animateScrollToItem(maxOf(0, currentLineIndex - 2))
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Lirik Sinkron", subtitle = "Simulasi karaoke scroller", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Song info header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Brush.linearGradient(listOf(palette.primary.copy(alpha = 0.15f), palette.info.copy(alpha = 0.1f)))),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 20.dp)) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(palette.primary, palette.info))),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("♪", fontSize = 28.sp, color = palette.onPrimary)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Mimpi dan Harapan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        Text("Senandung Senja", fontSize = 13.sp, color = palette.textSecondary)
                        Text("Puisi Indonesia • 2026", fontSize = 11.sp, color = palette.textMuted, fontStyle = FontStyle.Italic)
                    }
                }
            }

            // Lyrics
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 20.dp),
            ) {
                itemsIndexed(LyricsData, key = { idx, _ -> idx }) { idx, line ->
                    val isActive = idx == currentLineIndex
                    val isPast = idx < currentLineIndex
                    val scale by animateFloatAsState(if (isActive) 1.08f else 1f, tween(300))

                    Text(
                        text = line.text,
                        fontSize = if (isActive) 20.sp else 16.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = when {
                            isActive -> palette.primary
                            isPast -> palette.textMuted
                            else -> palette.textSecondary
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { scaleX = scale; scaleY = scale }
                            .clickable {
                                currentTime = line.timestamp
                                if (!isPlaying) isPlaying = true
                            }
                            .padding(vertical = 6.dp),
                    )
                }
            }

            // Playback controls
            Surface(
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp, tonalElevation = 0.dp,
            ) {
                Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp)) {
                    // Progress slider
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(formatTime(currentTime), fontSize = 11.sp, color = palette.textMuted)
                        Slider(
                            value = currentTime.toFloat(),
                            onValueChange = { currentTime = it.toInt() },
                            valueRange = 0f..TotalDuration.toFloat(),
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = palette.primary,
                                activeTrackColor = palette.primary,
                                inactiveTrackColor = palette.surfaceMuted,
                            ),
                        )
                        Text(formatTime(TotalDuration), fontSize = 11.sp, color = palette.textMuted)
                    }
                    // Play + speed
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Speed chips
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(0.5f, 1f, 1.5f, 2f).forEach { s ->
                                Surface(
                                    onClick = { speed = s },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (speed == s) palette.primary else palette.surfaceMuted,
                                    border = BorderStroke(1.dp, if (speed == s) palette.primary else palette.border),
                                    shadowElevation = 0.dp, tonalElevation = 0.dp,
                                ) {
                                    Text("${s}x", modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (speed == s) palette.onPrimary else palette.textSecondary)
                                }
                            }
                        }
                        // Play/Pause
                        Surface(
                            onClick = {
                                if (currentTime >= TotalDuration) currentTime = 0
                                isPlaying = !isPlaying
                            },
                            shape = CircleShape,
                            color = palette.primary,
                            shadowElevation = 0.dp, tonalElevation = 0.dp,
                            modifier = Modifier.size(44.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(if (isPlaying) "⏸" else "▶", fontSize = 18.sp, color = palette.onPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
