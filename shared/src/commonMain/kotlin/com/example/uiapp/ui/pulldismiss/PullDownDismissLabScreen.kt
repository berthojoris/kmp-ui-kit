package com.example.uiapp.ui.pulldismiss

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private data class PhotoSample(val id: Int, val title: String, val subtitle: String, val bannerColor: Color)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PullDownDismissLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()

    val photos = remember {
        listOf(
            PhotoSample(1, "Alpine Architecture", "Switzerland Minimal", Color(0xFF0F2B26)),
            PhotoSample(2, "Nordic Interior", "Oslo Minimalist", Color(0xFF1E293B)),
            PhotoSample(3, "Kyoto Bamboo Zen", "Arashiyama Serenity", Color(0xFF14532D)),
            PhotoSample(4, "Icelandic Glacier", "Vatnajökull Ice", Color(0xFF0C4A6E)),
            PhotoSample(5, "Atelier Concrete Studio", "Tokyo Brutalism", Color(0xFF334155)),
            PhotoSample(6, "Sahara Golden Dunes", "Merzouga Sunset", Color(0xFF78350F)),
        )
    }

    var selectedPhotoIndex by remember { mutableStateOf<Int?>(null) }
    val dragOffsetY = remember { Animatable(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = palette.background,
            topBar = {
                UiTopBar(
                    title = "Pull-Down to Dismiss",
                    subtitle = "Gestur Geser Bawah, Lightbox Media",
                    onBack = onBack,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("iOS & Android Elastic Pull-Down Lightbox", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Ketuk salah satu kartu foto untuk membuka lightbox, lalu geser ke bawah untuk menutupnya dengan animasi fisika.", fontSize = 12.sp, color = palette.textMuted)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    itemsIndexed(photos) { index, photo ->
                        Surface(
                            onClick = {
                                selectedPhotoIndex = index
                                scope.launch { dragOffsetY.snapTo(0f) }
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = palette.surface,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1.1f)
                                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                        .background(photo.bannerColor),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("✦ FOTO #${photo.id} ✦", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(photo.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                                    Text(photo.subtitle, fontSize = 11.sp, color = palette.textMuted)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Full-screen Lightbox Overlay with Pull-Down Dismiss
        AnimatedVisibility(
            visible = selectedPhotoIndex != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            val photo = selectedPhotoIndex?.let { photos[it] }
            if (photo != null) {
                // Calculate dynamic alpha and scale from drag offset
                val currentOffset = dragOffsetY.value.coerceAtLeast(0f)
                val bgAlpha = (1f - (currentOffset / 600f)).coerceIn(0.2f, 1f)
                val imageScale = (1f - (currentOffset / 1400f)).coerceIn(0.7f, 1f)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.9f * bgAlpha))
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragEnd = {
                                    if (dragOffsetY.value > 160f) {
                                        // Dismiss threshold reached
                                        selectedPhotoIndex = null
                                    } else {
                                        // Snap back smoothly
                                        scope.launch {
                                            dragOffsetY.animateTo(0f, spring(dampingRatio = 0.8f))
                                        }
                                    }
                                },
                                onDragCancel = {
                                    scope.launch { dragOffsetY.animateTo(0f) }
                                },
                                onVerticalDrag = { change, dragAmount ->
                                    change.consume()
                                    scope.launch {
                                        dragOffsetY.snapTo(dragOffsetY.value + dragAmount)
                                    }
                                },
                            )
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    // Close button top right
                    Surface(
                        onClick = { selectedPhotoIndex = null },
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(20.dp),
                    ) {
                        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                            Text("✕", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    // Interactive Elastic Media Card
                    Column(
                        modifier = Modifier
                            .offset { IntOffset(0, currentOffset.roundToInt()) }
                            .graphicsLayer {
                                scaleX = imageScale
                                scaleY = imageScale
                            }
                            .fillMaxWidth(0.9f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = photo.bannerColor,
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp),
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("✦ ATELIER PORTFOLIO ✦", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(photo.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(photo.subtitle, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Text("↓ Tarik ke bawah untuk menutup", color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
