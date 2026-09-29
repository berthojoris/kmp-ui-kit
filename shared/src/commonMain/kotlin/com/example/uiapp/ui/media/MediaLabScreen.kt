package com.example.uiapp.ui.media

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private enum class MediaSubTab(val title: String) {
    VIDEO("Video & PiP"),
    MINI_PLAYER("Mini Player"),
    STORIES("Stories/Reels"),
    WAVEFORM("Waveform"),
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MediaLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var activeSubTab by remember { mutableStateOf(MediaSubTab.VIDEO) }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Media & Immersive",
                subtitle = "Video, Mini-Player, Stories, Waveform",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface)
                    .border(BorderStroke(1.dp, palette.border))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MediaSubTab.entries.forEach { tab ->
                    val isSelected = activeSubTab == tab
                    Surface(
                        onClick = { activeSubTab = tab },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) palette.primary else palette.surfaceMuted,
                        border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                    ) {
                        Text(
                            text = tab.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else palette.textPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (activeSubTab) {
                    MediaSubTab.VIDEO -> VideoPlayerView(palette)
                    MediaSubTab.MINI_PLAYER -> MiniPlayerView(palette)
                    MediaSubTab.STORIES -> StoriesVerticalPagerView(palette)
                    MediaSubTab.WAVEFORM -> AudioWaveformPlayerView(palette)
                }
            }
        }
    }
}

@Composable
private fun VideoPlayerView(palette: com.example.uiapp.theme.AppPalette) {
    var isPlaying by remember { mutableStateOf(false) }
    var videoProgress by remember { mutableFloatStateOf(0.35f) }
    var isPipActive by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Video Player & Overlay Controls", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                Text("Simulasi pemutar video interaktif dengan tombol PiP", fontSize = 12.sp, color = palette.textMuted)
                Spacer(modifier = Modifier.height(12.dp))

                // Video Display Screen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    // Center Play/Pause button
                    Surface(
                        onClick = { isPlaying = !isPlaying },
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                    ) {
                        Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isPlaying) "❚❚" else "▶",
                                fontSize = 20.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    // Bottom Control Overlay
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = if (isPlaying) "01:24 / 03:45" else "Dijeda",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                onClick = { isPipActive = !isPipActive },
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.2f),
                            ) {
                                Text(
                                    text = if (isPipActive) "Tutup PiP" else "Mode PiP",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Slider(
                    value = videoProgress,
                    onValueChange = { videoProgress = it },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // PiP Floating Box Preview
        AnimatedVisibility(visible = isPipActive) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(2.dp, palette.primary),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("PiP", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Picture-in-Picture Aktif", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                            Text("Video berjalan di jendela mengambang", fontSize = 11.sp, color = palette.textMuted)
                        }
                    }
                    Surface(
                        onClick = { isPipActive = false },
                        shape = RoundedCornerShape(6.dp),
                        color = palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                    ) {
                        Text("Tutup", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, color = palette.danger)
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniPlayerView(palette: com.example.uiapp.theme.AppPalette) {
    var showNowPlayingSheet by remember { mutableStateOf(false) }
    var isPlayingAudio by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Text("Sticky Mini-Player & Now Playing Sheet", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
            Text("Mini player di bawah layar yang bisa dibuka menjadi Now-Playing sheet", fontSize = 12.sp, color = palette.textMuted)
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Daftar Putar:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("1. Symphonie No. 5 in C Minor - Beethoven", fontSize = 12.sp, color = palette.textSecondary)
                    Text("2. Clair de Lune - Claude Debussy", fontSize = 12.sp, color = palette.textSecondary)
                    Text("3. Nocturne Op. 9 No. 2 - Chopin", fontSize = 12.sp, color = palette.textSecondary)
                }
            }
        }

        // Sticky Mini-Player bar at bottom
        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showNowPlayingSheet = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("♫", fontSize = 18.sp, color = palette.primary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Clair de Lune", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                        Text("Claude Debussy \u00B7 Sentuh untuk buka", fontSize = 11.sp, color = palette.textMuted)
                    }
                }
                Surface(
                    onClick = { isPlayingAudio = !isPlayingAudio },
                    shape = CircleShape,
                    color = palette.primary,
                ) {
                    Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isPlayingAudio) "❚❚" else "▶",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        // Now-Playing Bottom Sheet Modal
        AnimatedVisibility(
            visible = showNowPlayingSheet,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showNowPlayingSheet = false },
                contentAlignment = Alignment.BottomCenter,
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 8.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(palette.border),
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(palette.primaryContainer)
                                .border(BorderStroke(1.dp, palette.primary), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("♪ ♫ ♪", fontSize = 38.sp, color = palette.primary)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Clair de Lune", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = palette.textPrimary)
                        Text("Claude Debussy - Suite Bergamasque", fontSize = 13.sp, color = palette.textMuted)
                        Spacer(modifier = Modifier.height(20.dp))
                        Surface(
                            onClick = { showNowPlayingSheet = false },
                            shape = RoundedCornerShape(12.dp),
                            color = palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                                Text("Tutup Sheet", fontSize = 13.sp, color = palette.textPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoriesVerticalPagerView(palette: com.example.uiapp.theme.AppPalette) {
    var storyIndex by remember { mutableIntStateOf(0) }
    val stories = listOf(
        "KMP Multiplatform 100% Shared UI",
        "Flat UI Zero-Shadow Aesthetic",
        "Smooth 60fps Native Animations",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // Segmented timer progress indicators
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    stories.indices.forEach { idx ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (idx <= storyIndex) Color.White else Color.White.copy(alpha = 0.3f)),
                        )
                    }
                }

                // Story Content
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Story #${storyIndex + 1}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stories[storyIndex],
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }

                // Story Controls
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Surface(
                        onClick = { if (storyIndex > 0) storyIndex-- },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        enabled = storyIndex > 0,
                    ) {
                        Text("← Prev", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                    Surface(
                        onClick = { if (storyIndex < stories.size - 1) storyIndex++ else storyIndex = 0 },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.25f),
                    ) {
                        Text(
                            text = if (storyIndex < stories.size - 1) "Next →" else "Ulang ↺",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioWaveformPlayerView(palette: com.example.uiapp.theme.AppPalette) {
    var isPlayingWave by remember { mutableStateOf(false) }
    var scrubPosition by remember { mutableFloatStateOf(0.42f) }
    val waveformSamples = listOf(0.2f, 0.4f, 0.7f, 0.9f, 0.5f, 0.8f, 1f, 0.6f, 0.3f, 0.7f, 0.85f, 0.45f, 0.3f, 0.65f, 0.9f, 0.5f, 0.2f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Audio Waveform Voice Note Player", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                Text("Visualisasi bar gelombang suara dengan status putar", fontSize = 12.sp, color = palette.textMuted)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surfaceMuted)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(
                        onClick = { isPlayingWave = !isPlayingWave },
                        shape = CircleShape,
                        color = palette.primary,
                    ) {
                        Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isPlayingWave) "❚❚" else "▶",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    // Waveform Bars
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clickable { scrubPosition = if (scrubPosition > 0.8f) 0.2f else scrubPosition + 0.2f },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        waveformSamples.forEachIndexed { i, sample ->
                            val progressThreshold = i.toFloat() / waveformSamples.size
                            val isPlayed = progressThreshold <= scrubPosition
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height((sample * 36).dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (isPlayed) palette.primary else palette.border),
                            )
                        }
                    }

                    Text("0:24", fontSize = 11.sp, color = palette.textMuted, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
