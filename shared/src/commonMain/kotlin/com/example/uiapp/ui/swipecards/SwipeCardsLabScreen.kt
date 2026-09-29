package com.example.uiapp.ui.swipecards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
import kotlin.math.abs
import kotlin.math.roundToInt

private data class ProfileCard(
    val id: Int,
    val name: String,
    val age: Int,
    val role: String,
    val location: String,
    val tags: List<String>,
    val emoji: String,
    val accentColor: Color,
)

private val SAMPLE_PROFILES = listOf(
    ProfileCard(1, "Sophia Chen", 26, "Lead UI/UX Architect", "San Francisco, CA", listOf("Design Systems", "Figma", "SwiftUI"), "🎨", Color(0xFF6366F1)),
    ProfileCard(2, "Alex Rivera", 29, "Principal Mobile Engineer", "Austin, TX", listOf("Kotlin KMP", "Compose", "Rust"), "⚡", Color(0xFF10B981)),
    ProfileCard(3, "Elena Rostova", 27, "AI Research Scientist", "Zurich, CH", listOf("PyTorch", "LLMs", "Math"), "🧠", Color(0xFFEC4899)),
    ProfileCard(4, "Kenji Sato", 31, "Creative Product Director", "Tokyo, JP", listOf("Product", "Fintech", "Growth"), "🚀", Color(0xFFF59E0B)),
    ProfileCard(5, "Maya Hansen", 25, "Fullstack Distributed Dev", "Copenhagen, DK", listOf("Go", "Kubernetes", "GraphQL"), "🛡️", Color(0xFF06B6D4)),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SwipeCardsLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()

    val cardList = remember { mutableStateListOf<ProfileCard>().apply { addAll(SAMPLE_PROFILES) } }
    var lastSwipedAction by remember { mutableStateOf<String?>(null) }
    var likesCount by remember { mutableIntStateOf(0) }
    var nopesCount by remember { mutableIntStateOf(0) }

    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    fun swipeCard(like: Boolean) {
        if (cardList.isEmpty()) return
        scope.launch {
            val targetX = if (like) 1200f else -1200f
            offsetX.animateTo(targetX, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium))
            val swiped = cardList.removeAt(0)
            if (like) {
                likesCount++
                lastSwipedAction = "Liked ${swiped.name}"
            } else {
                nopesCount++
                lastSwipedAction = "Passed on ${swiped.name}"
            }
            offsetX.snapTo(0f)
            offsetY.snapTo(0f)
        }
    }

    fun resetStack() {
        cardList.clear()
        cardList.addAll(SAMPLE_PROFILES)
        likesCount = 0
        nopesCount = 0
        lastSwipedAction = "Kartu di-reset"
        scope.launch {
            offsetX.snapTo(0f)
            offsetY.snapTo(0f)
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Swipeable Decision Cards",
                subtitle = "Tinder Gesture Stack, Physics Rotation",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                ) {
                    Text(
                        text = "Passed: $nopesCount",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.danger,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                ) {
                    Text(
                        text = lastSwipedAction ?: "Geser kartu ke kanan / kiri",
                        fontSize = 11.sp,
                        color = palette.textSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                ) {
                    Text(
                        text = "Liked: $likesCount",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.success,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card Stack Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                if (cardList.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier.size(280.dp),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text("✨", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Semua Kartu Selesai!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Kamu telah meninjau seluruh kandidat", fontSize = 12.sp, color = palette.textMuted)
                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(
                                onClick = { resetStack() },
                                shape = RoundedCornerShape(10.dp),
                                color = palette.primary,
                            ) {
                                Text("Muat Ulang Kartu", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp))
                            }
                        }
                    }
                } else {
                    // Render up to 3 cards in stack (from back to front)
                    val visibleCards = cardList.take(3).reversed()
                    visibleCards.forEachIndexed { revIndex, profile ->
                        val isTop = revIndex == visibleCards.lastIndex
                        val stackLevel = visibleCards.lastIndex - revIndex
                        val cardScale = 1f - (stackLevel * 0.05f)
                        val cardOffsetY = (stackLevel * 14).dp

                        if (isTop) {
                            val dragDistance = offsetX.value
                            val rotation = (dragDistance / 25f).coerceIn(-20f, 20f)
                            val likeAlpha = (dragDistance / 250f).coerceIn(0f, 1f)
                            val nopeAlpha = (-dragDistance / 250f).coerceIn(0f, 1f)

                            Box(
                                modifier = Modifier
                                    .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                                    .rotate(rotation)
                                    .pointerInput(cardList.first().id) {
                                        detectDragGestures(
                                            onDragEnd = {
                                                scope.launch {
                                                    if (offsetX.value > 300f) {
                                                        swipeCard(like = true)
                                                    } else if (offsetX.value < -300f) {
                                                        swipeCard(like = false)
                                                    } else {
                                                        offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                                        offsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                                    }
                                                }
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                scope.launch {
                                                    offsetX.snapTo(offsetX.value + dragAmount.x)
                                                    offsetY.snapTo(offsetY.value + dragAmount.y * 0.4f)
                                                }
                                            },
                                        )
                                    },
                            ) {
                                SingleCardView(
                                    profile = profile,
                                    palette = palette,
                                    likeAlpha = likeAlpha,
                                    nopeAlpha = nopeAlpha,
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .offset(y = cardOffsetY)
                                    .scale(cardScale),
                            ) {
                                SingleCardView(
                                    profile = profile,
                                    palette = palette,
                                    likeAlpha = 0f,
                                    nopeAlpha = 0f,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Rewind / Reset
                Surface(
                    onClick = { resetStack() },
                    shape = CircleShape,
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.size(48.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🔄", fontSize = 18.sp)
                    }
                }

                // Dislike / Nope
                Surface(
                    onClick = { swipeCard(like = false) },
                    shape = CircleShape,
                    color = palette.surface,
                    border = BorderStroke(1.5.dp, palette.danger),
                    modifier = Modifier.size(62.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("✖️", fontSize = 22.sp, color = palette.danger)
                    }
                }

                // Super Like
                Surface(
                    onClick = { swipeCard(like = true) },
                    shape = CircleShape,
                    color = palette.surface,
                    border = BorderStroke(1.5.dp, palette.info),
                    modifier = Modifier.size(52.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("⭐", fontSize = 20.sp)
                    }
                }

                // Like
                Surface(
                    onClick = { swipeCard(like = true) },
                    shape = CircleShape,
                    color = palette.surface,
                    border = BorderStroke(1.5.dp, palette.success),
                    modifier = Modifier.size(62.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("❤️", fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SingleCardView(
    profile: ProfileCard,
    palette: com.example.uiapp.theme.AppPalette,
    likeAlpha: Float,
    nopeAlpha: Float,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier
            .fillMaxWidth()
            .height(440.dp)
            .padding(horizontal = 12.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Visual Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(profile.accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(profile.emoji, fontSize = 90.sp)
                }

                // Bottom Info Box
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${profile.name}, ${profile.age}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = palette.textPrimary,
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = profile.role,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = palette.primary,
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "📍 ${profile.location}",
                        fontSize = 12.sp,
                        color = palette.textMuted,
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        profile.tags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = palette.surfaceMuted,
                                border = BorderStroke(1.dp, palette.border),
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 11.sp,
                                    color = palette.textSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }
                }
            }

            // LIKE Stamp Overlay
            if (likeAlpha > 0.05f) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(24.dp)
                        .rotate(-15f)
                        .graphicsLayer { alpha = likeAlpha }
                        .border(3.dp, palette.success, RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                ) {
                    Text(
                        "LIKE",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = palette.success,
                    )
                }
            }

            // NOPE Stamp Overlay
            if (nopeAlpha > 0.05f) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp)
                        .rotate(15f)
                        .graphicsLayer { alpha = nopeAlpha }
                        .border(3.dp, palette.danger, RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                ) {
                    Text(
                        "NOPE",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = palette.danger,
                    )
                }
            }
        }
    }
}
