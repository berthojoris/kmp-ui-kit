package com.example.uiapp.ui.gallery

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.ui.components.icons.MgIosBackButton
import kotlinx.coroutines.launch

private data class Photo(
    val id: Int,
    val caption: String,
    val place: String,
    val start: Color,
    val end: Color,
)

private val Photos = listOf(
    Photo(1, "Kolam tanpa batas", "Uluwatu", Color(0xFF0F3D34), Color(0xFF05201B)),
    Photo(2, "Panorama samudra", "Uluwatu", Color(0xFF1B4E63), Color(0xFF06222E)),
    Photo(3, "Suite utama", "Ubud", Color(0xFF4A2F27), Color(0xFF1F130F)),
    Photo(4, "Teras matahari", "Sanur", Color(0xFF2F5D4E), Color(0xFF0E2B23)),
    Photo(5, "Taman privat", "Nusa Dua", Color(0xFF5A4632), Color(0xFF2A1F14)),
    Photo(6, "Kabut pagi", "Lembang", Color(0xFF3B3550), Color(0xFF171426)),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GalleryScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val pagerState = rememberPagerState(pageCount = { Photos.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F0E)),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            ZoomablePhoto(photo = Photos[page])
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MgIosBackButton(
                onClick = onBack,
                backgroundColor = Color.White.copy(alpha = 0.14f),
                borderColor = Color.White.copy(alpha = 0.2f),
                iconTint = Color.White,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Galeri",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${pagerState.currentPage + 1} / ${Photos.size}",
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.14f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
        }

        Text(
            text = "Ketuk dua kali untuk zoom \u00B7 cubit untuk perbesar \u00B7 geser untuk ganti foto",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 58.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black.copy(alpha = 0.35f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.85f),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
                )
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = Photos[pagerState.currentPage].caption,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = Photos[pagerState.currentPage].place,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Photos.forEachIndexed { index, photo ->
                    val active = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .size(width = if (active) 26.dp else 10.dp, height = 6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (active) Color.White
                                else Color.White.copy(alpha = 0.35f),
                            )
                            .clickable {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            },
                    )
                }
            }
        }
    }
}

@Composable
private fun ZoomablePhoto(photo: Photo) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(photo.id) {
                    detectTapGestures(
                        onDoubleTap = {
                            scope.launch {
                                if (scale.value > 1.4f) {
                                    scale.animateTo(1f, spring(stiffness = Spring.StiffnessLow))
                                    offsetX.animateTo(0f)
                                    offsetY.animateTo(0f)
                                } else {
                                    scale.animateTo(
                                        targetValue = 2.3f,
                                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                                    )
                                }
                            }
                        },
                    )
                }
                .then(
                    if (scale.value > 1.05f) {
                        Modifier.pointerInput(photo.id) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scope.launch {
                                    scale.snapTo((scale.value * zoom).coerceIn(1f, 4f))
                                    offsetX.snapTo(offsetX.value + pan.x)
                                    offsetY.snapTo(offsetY.value + pan.y)
                                }
                            }
                        }
                    } else {
                        Modifier
                    },
                )
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    translationX = offsetX.value
                    translationY = offsetY.value
                },
        ) {
            PhotoArt(photo = photo)
        }

        if (scale.value > 1.05f) {
            Surface(
                onClick = {
                    scope.launch {
                        scale.animateTo(1f, spring(stiffness = Spring.StiffnessLow))
                        offsetX.animateTo(0f)
                        offsetY.animateTo(0f)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 130.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.16f),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Text(
                    text = "Reset",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun PhotoArt(photo: Photo) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(listOf(photo.start, photo.end)),
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.10f),
            radius = size.minDimension * 0.45f,
            center = Offset(size.width * 0.78f, size.height * 0.26f),
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.06f),
            radius = size.minDimension * 0.24f,
            center = Offset(size.width * 0.2f, size.height * 0.66f),
        )
    }
}
