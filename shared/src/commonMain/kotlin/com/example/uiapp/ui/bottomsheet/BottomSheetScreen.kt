package com.example.uiapp.ui.bottomsheet

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.icons.MgIosBackButton
import kotlinx.coroutines.launch

private enum class SheetAnchor(val label: String) {
    PEEK("Peek"),
    HALF("Half"),
    FULL("Full"),
}

private data class Stay(
    val id: Int,
    val name: String,
    val area: String,
    val type: String,
    val rating: String,
    val price: String,
    val start: Color,
    val end: Color,
)

private val Stays = listOf(
    Stay(1, "The Obsidian Escape", "Uluwatu", "Villa", "4.9", "Rp 8.4jt", Color(0xFF0F3D34), Color(0xFF05201B)),
    Stay(2, "Aurora Hillside", "Ubud", "Resort", "4.8", "Rp 5.1jt", Color(0xFF1E3A5F), Color(0xFF0B1B2E)),
    Stay(3, "Solstice Loft", "Senopati", "Loft", "4.7", "Rp 3.2jt", Color(0xFF4A2F27), Color(0xFF1F130F)),
    Stay(4, "Palm Cove Suite", "Sanur", "Suite", "4.6", "Rp 2.7jt", Color(0xFF2F5D4E), Color(0xFF0E2B23)),
    Stay(5, "Mirage Pavilion", "Nusa Dua", "Pavilion", "4.9", "Rp 6.9jt", Color(0xFF5A4632), Color(0xFF2A1F14)),
    Stay(6, "Northern Light Lodge", "Lembang", "Lodge", "4.8", "Rp 4.0jt", Color(0xFF3B3550), Color(0xFF171426)),
    Stay(7, "Terraced Rice Retreat", "Tegallalang", "Villa", "4.7", "Rp 5.6jt", Color(0xFF2C4A2E), Color(0xFF12210F)),
    Stay(8, "Harbor Glass House", "Benoa", "Villa", "4.8", "Rp 7.3jt", Color(0xFF26404D), Color(0xFF0E1E27)),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BottomSheetScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    var anchor by remember { mutableStateOf(SheetAnchor.PEEK) }

    BackHandler(enabled = true) {
        if (anchor != SheetAnchor.PEEK) anchor = SheetAnchor.PEEK else onBack()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background),
    ) {
        val density = LocalDensity.current
        val fullPx = with(density) { maxHeight.toPx() }
        val halfPx = with(density) { (maxHeight * 0.56f).toPx() }
        val peekPx = with(density) { (maxHeight * 0.30f).coerceAtLeast(196.dp).toPx() }
        val sheetShape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)

        val visible = remember { Animatable(peekPx) }
        var dragAccum by remember { mutableFloatStateOf(0f) }
        val scope = rememberCoroutineScope()

        val effective = (visible.value - dragAccum).coerceIn(peekPx, fullPx)
        val progress = ((effective - peekPx) / (fullPx - peekPx)).coerceIn(0f, 1f)

        fun anchorPx(target: SheetAnchor): Float = when (target) {
            SheetAnchor.PEEK -> peekPx
            SheetAnchor.HALF -> halfPx
            SheetAnchor.FULL -> fullPx
        }

        fun settle(target: Float) {
            scope.launch {
                val current = (visible.value - dragAccum).coerceIn(peekPx, fullPx)
                visible.snapTo(current)
                dragAccum = 0f
                visible.animateTo(
                    targetValue = target,
                    animationSpec = spring(
                        dampingRatio = 0.82f,
                        stiffness = Spring.StiffnessMediumLow,
                    ),
                )
            }
        }

        fun goTo(target: SheetAnchor) {
            anchor = target
            settle(anchorPx(target))
        }

        fun cycle() {
            val next = when {
                effective < halfPx - 4f -> SheetAnchor.HALF
                effective < fullPx - 4f -> SheetAnchor.FULL
                else -> SheetAnchor.PEEK
            }
            goTo(next)
        }

        val dragState = rememberDraggableState { delta ->
            dragAccum = (dragAccum + delta)
                .coerceIn(visible.value - fullPx, visible.value - peekPx)
        }

        MapBackdrop(modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = progress * 0.26f))
                .clickable(enabled = effective > peekPx + 4f) { goTo(SheetAnchor.PEEK) },
        )

        FloatingControls(
            anchor = anchor,
            effective = effective,
            peekPx = peekPx,
            halfPx = halfPx,
            fullPx = fullPx,
            onBack = onBack,
            onSelect = { goTo(it) },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationY = fullPx - effective }
                .background(palette.surface, sheetShape)
                .border(BorderStroke(1.dp, palette.border), sheetShape),
        ) {
            SheetHeader(
                dragState = dragState,
                onDragStopped = { velocity ->
                    val current = (visible.value - dragAccum).coerceIn(peekPx, fullPx)
                    val target = when {
                        velocity < -450f -> anchorsAbove(current, peekPx, halfPx, fullPx)
                        velocity > 450f -> anchorsBelow(current, peekPx, halfPx, fullPx)
                        else -> nearestAnchor(current, peekPx, halfPx, fullPx)
                    }
                    anchor = target
                    settle(anchorPx(target))
                },
                onClick = { cycle() },
            )

            StayList(modifier = Modifier.fillMaxSize())
        }
    }
}

private fun nearestAnchor(value: Float, peek: Float, half: Float, full: Float): SheetAnchor {
    val dPeek = kotlin.math.abs(value - peek)
    val dHalf = kotlin.math.abs(value - half)
    val dFull = kotlin.math.abs(value - full)
    return when {
        dPeek <= dHalf && dPeek <= dFull -> SheetAnchor.PEEK
        dHalf <= dFull -> SheetAnchor.HALF
        else -> SheetAnchor.FULL
    }
}

private fun anchorsAbove(value: Float, peek: Float, half: Float, full: Float): SheetAnchor = when {
    value < half - 4f -> SheetAnchor.HALF
    value < full - 4f -> SheetAnchor.FULL
    else -> SheetAnchor.FULL
}

private fun anchorsBelow(value: Float, peek: Float, half: Float, full: Float): SheetAnchor = when {
    value > half + 4f -> SheetAnchor.HALF
    value > peek + 4f -> SheetAnchor.PEEK
    else -> SheetAnchor.PEEK
}

@Composable
private fun SheetHeader(
    dragState: androidx.compose.foundation.gestures.DraggableState,
    onDragStopped: suspend (Float) -> Unit,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .draggable(
                state = dragState,
                orientation = Orientation.Vertical,
                onDragStopped = { velocity -> onDragStopped(velocity) },
            )
            .clickable(onClick = onClick)
            .padding(top = 12.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(44.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(palette.border),
        )
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Pilih Penginapan",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,
                    letterSpacing = (-0.3).sp,
                    color = palette.textPrimary,
                )
                Text(
                    text = "8 tersedia",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textMuted,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tarik ke atas untuk melihat lebih banyak",
                fontSize = 12.sp,
                color = palette.textSecondary,
            )
        }
    }
}

@Composable
private fun StayList(modifier: Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(Stays, key = { it.id }) { stay ->
            StayRow(stay)
        }
    }
}

@Composable
private fun StayRow(stay: Stay) {
    val palette = LocalAppPalette.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp)),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        brush = Brush.verticalGradient(listOf(stay.start, stay.end)),
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.14f),
                        radius = size.minDimension * 0.42f,
                        center = Offset(size.width * 0.74f, size.height * 0.3f),
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stay.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${stay.area} \u00B7 ${stay.type}",
                    fontSize = 12.sp,
                    color = palette.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stay.price,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\u2605 ${stay.rating}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.warning,
                )
            }
        }
    }
}

@Composable
private fun FloatingControls(
    anchor: SheetAnchor,
    effective: Float,
    peekPx: Float,
    halfPx: Float,
    fullPx: Float,
    onBack: () -> Unit,
    onSelect: (SheetAnchor) -> Unit,
) {
    val palette = LocalAppPalette.current

    val live = nearestAnchor(effective, peekPx, halfPx, fullPx)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MgIosBackButton(
                onClick = onBack,
                backgroundColor = palette.surface,
                borderColor = palette.border,
                iconTint = palette.textPrimary,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Bottom Sheet Bertingkat",
                modifier = Modifier
                    .clip(RoundedCornerShape(11.dp))
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(11.dp))
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SheetAnchor.entries.forEach { entry ->
                val selected = entry == live
                Text(
                    text = entry.label,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) palette.primary else palette.surface)
                        .border(
                            BorderStroke(1.dp, if (selected) palette.primary else palette.border),
                            RoundedCornerShape(20.dp),
                        )
                        .clickable { onSelect(entry) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selected) Color.White else palette.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun MapBackdrop(modifier: Modifier) {
    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFEEF3EF), Color(0xFFE1EAE4)),
            ),
        )

        drawRoundRect(
            color = Color(0xFFD6E6DC),
            topLeft = Offset(size.width * 0.05f, size.height * 0.10f),
            size = Size(size.width * 0.42f, size.height * 0.14f),
            cornerRadius = CornerRadius(28f, 28f),
        )
        drawRoundRect(
            color = Color(0xFFD6E6DC),
            topLeft = Offset(size.width * 0.58f, size.height * 0.22f),
            size = Size(size.width * 0.36f, size.height * 0.12f),
            cornerRadius = CornerRadius(28f, 28f),
        )

        val road = Color.White
        drawLine(road, Offset(0f, size.height * 0.34f), Offset(size.width, size.height * 0.20f), 22f, StrokeCap.Round)
        drawLine(road, Offset(size.width * 0.28f, 0f), Offset(size.width * 0.52f, size.height * 0.62f), 20f, StrokeCap.Round)
        drawLine(road, Offset(0f, size.height * 0.58f), Offset(size.width, size.height * 0.44f), 18f, StrokeCap.Round)
        drawLine(road, Offset(size.width * 0.78f, 0f), Offset(size.width * 0.62f, size.height * 0.60f), 16f, StrokeCap.Round)

        val pin = Color(0xFF0A332C)
        listOf(
            Offset(size.width * 0.30f, size.height * 0.26f),
            Offset(size.width * 0.66f, size.height * 0.38f),
            Offset(size.width * 0.18f, size.height * 0.50f),
            Offset(size.width * 0.74f, size.height * 0.52f),
        ).forEach { center ->
            drawCircle(color = Color.White, radius = 13f, center = center)
            drawCircle(color = pin, radius = 8f, center = center)
        }
    }
}
