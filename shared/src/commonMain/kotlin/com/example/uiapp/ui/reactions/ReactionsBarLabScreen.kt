package com.example.uiapp.ui.reactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val AVAILABLE_EMOJIS = listOf("👍", "❤️", "🎉", "🔥", "🚀", "💡")

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReactionsBarLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var showDock by remember { mutableStateOf(false) }
    var selectedEmoji by remember { mutableStateOf<String?>(null) }
    val counts = remember {
        mutableStateMapOf(
            "👍" to 14,
            "❤️" to 32,
            "🎉" to 9,
            "🔥" to 21,
            "🚀" to 17,
            "💡" to 6,
        )
    }

    // Floating reaction animation state
    var floatingEmoji by remember { mutableStateOf<String?>(null) }
    val floatingOffsetY = remember { Animatable(0f) }
    val floatingAlpha = remember { Animatable(1f) }

    fun triggerReaction(emoji: String) {
        if (selectedEmoji == emoji) {
            counts[emoji] = (counts[emoji] ?: 1) - 1
            selectedEmoji = null
        } else {
            if (selectedEmoji != null) {
                counts[selectedEmoji!!] = (counts[selectedEmoji!!] ?: 1) - 1
            }
            counts[emoji] = (counts[emoji] ?: 0) + 1
            selectedEmoji = emoji
        }
        showDock = false

        // Launch floating emoji bubble
        floatingEmoji = emoji
        scope.launch {
            floatingOffsetY.snapTo(0f)
            floatingAlpha.snapTo(1f)
            launch {
                floatingOffsetY.animateTo(
                    targetValue = -90f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
                )
            }
            launch {
                delay(400)
                floatingAlpha.animateTo(0f, animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                floatingEmoji = null
            }
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Interactive Reactions",
                subtitle = "Bouncy Spring Emoji Dock",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Social Feed Post Card Preview
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Author Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(palette.primaryContainer)
                                .border(1.dp, palette.primary, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("✨", fontSize = 20.sp)
                        }
                        Column {
                            Text("Compose Multiplatform Team", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                            Text("2 jam yang lalu • Publik", fontSize = 11.sp, color = palette.textMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "🚀 Memperkenalkan fitur baru Interactive Reactions Bar dengan animasi Spring Pop physics dan respons haptic interaktif tanpa lag!",
                        fontSize = 13.sp,
                        color = palette.textPrimary,
                        lineHeight = 19.sp,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reaction summary pill list
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        AVAILABLE_EMOJIS.forEach { emoji ->
                            val count = counts[emoji] ?: 0
                            val isSelected = selectedEmoji == emoji
                            if (count > 0 || isSelected) {
                                Surface(
                                    onClick = { triggerReaction(emoji) },
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) palette.primaryContainer else palette.surfaceMuted,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) palette.primary else palette.border,
                                    ),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Text(emoji, fontSize = 13.sp)
                                        Text(
                                            text = "$count",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) palette.primary else palette.textSecondary,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Bottom Action Bar with Floating Dock Anchor
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Floating Dock
                        androidx.compose.animation.AnimatedVisibility(
                            visible = showDock,
                            enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)) + scaleIn(
                                initialScale = 0.6f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                            ),
                            exit = fadeOut() + scaleOut(targetScale = 0.7f),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(y = (-58).dp),
                        ) {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = palette.surface,
                                border = BorderStroke(1.dp, palette.border),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    AVAILABLE_EMOJIS.forEachIndexed { index, emoji ->
                                        var isHovered by remember { mutableStateOf(false) }
                                        val scale by animateFloatAsState(
                                            targetValue = if (isHovered) 1.45f else 1.0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMedium,
                                            ),
                                        )

                                        Box(
                                            modifier = Modifier
                                                .scale(scale)
                                                .clip(CircleShape)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null,
                                                ) {
                                                    triggerReaction(emoji)
                                                }
                                                .padding(6.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(emoji, fontSize = 22.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Floating Emoji when tapped
                        floatingEmoji?.let { emoji ->
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset { IntOffset(x = 30, y = floatingOffsetY.value.roundToInt()) }
                                    .scale(1.2f),
                            ) {
                                Text(
                                    emoji,
                                    fontSize = 28.sp,
                                    modifier = Modifier.alpha(floatingAlpha.value),
                                )
                            }
                        }

                        // Post Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Surface(
                                onClick = { showDock = !showDock },
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedEmoji != null) palette.primaryContainer else palette.surface,
                                border = BorderStroke(
                                    1.dp,
                                    if (selectedEmoji != null) palette.primary else palette.border,
                                ),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Text(
                                        text = selectedEmoji ?: "👍",
                                        fontSize = 15.sp,
                                    )
                                    Text(
                                        text = if (selectedEmoji != null) "Bereaksi" else "Reaksi",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedEmoji != null) palette.primary else palette.textPrimary,
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = palette.surfaceMuted,
                                    border = BorderStroke(1.dp, palette.border),
                                ) {
                                    Text(
                                        "💬 8 Komentar",
                                        fontSize = 12.sp,
                                        color = palette.textSecondary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = palette.surfaceMuted,
                                    border = BorderStroke(1.dp, palette.border),
                                ) {
                                    Text(
                                        "↗️ Bagikan",
                                        fontSize = 12.sp,
                                        color = palette.textSecondary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Specs Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Fitur Arsitektur Reactions Bar:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Spring Physics: Pop-in and pop-out dengan damping bounciness", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Zero Shadow: 100% Flat UI dengan border tegas 1px", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Multi-state Toggle: Reaksi dapat dipilih ulang atau dibatalkan", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Floating Particle: Efek emoji terbang ke atas saat bereaksi", fontSize = 12.sp, color = palette.textSecondary)
                }
            }
        }
    }
}
