package com.example.uiapp.ui.components.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.uiapp.theme.LightAppPalette
import com.example.uiapp.theme.LocalAppPalette

@Composable
fun TickerCounterMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // 3 Odometer slot reels
        for (i in 0..2) {
            val rx = w * (0.12f + i * 0.28f)
            drawRoundRect(
                color = tint,
                topLeft = Offset(rx, h * 0.16f),
                size = Size(w * 0.20f, h * 0.68f),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                style = stroke,
            )
            // Center digit dash
            drawLine(
                color = tint,
                start = Offset(rx + w * 0.04f, h * 0.50f),
                end = Offset(rx + w * 0.16f, h * 0.50f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
fun PullDismissMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Card frame
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.16f, h * 0.12f),
            size = Size(w * 0.68f, h * 0.52f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = stroke,
        )
        // Downward pull arrow
        val arrowPath = Path().apply {
            moveTo(w * 0.50f, h * 0.40f)
            lineTo(w * 0.50f, h * 0.86f)
            moveTo(w * 0.36f, h * 0.72f)
            lineTo(w * 0.50f, h * 0.86f)
            lineTo(w * 0.64f, h * 0.72f)
        }
        drawPath(arrowPath, color = tint, style = stroke)
    }
}

@Composable
fun CommandPaletteMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Magnifying glass lens
        drawCircle(
            color = tint,
            radius = w * 0.24f,
            center = Offset(w * 0.42f, h * 0.42f),
            style = stroke,
        )
        // Handle
        drawLine(
            color = tint,
            start = Offset(w * 0.59f, h * 0.59f),
            end = Offset(w * 0.84f, h * 0.84f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
        // Center cmd dot
        drawCircle(
            color = tint,
            radius = 2.dp.toPx(),
            center = Offset(w * 0.42f, h * 0.42f),
        )
    }
}

@Composable
fun ConfettiMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        // Party horn / cone
        val cone = Path().apply {
            moveTo(w * 0.15f, h * 0.85f)
            lineTo(w * 0.45f, h * 0.75f)
            lineTo(w * 0.25f, h * 0.55f)
            close()
        }
        drawPath(cone, tint.copy(alpha = 0.8f))
        // Bursting particles
        drawCircle(tint, radius = 2.dp.toPx(), center = Offset(w * 0.55f, h * 0.45f))
        drawCircle(tint, radius = 1.8.dp.toPx(), center = Offset(w * 0.75f, h * 0.35f))
        drawCircle(tint, radius = 2.4.dp.toPx(), center = Offset(w * 0.65f, h * 0.65f))
        drawCircle(tint, radius = 1.5.dp.toPx(), center = Offset(w * 0.85f, h * 0.55f))
        drawCircle(tint, radius = 2.dp.toPx(), center = Offset(w * 0.45f, h * 0.25f))
    }
}

@Composable
fun ReactionsMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Pill container
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.05f, h * 0.25f),
            size = Size(w * 0.9f, h * 0.5f),
            cornerRadius = CornerRadius(h * 0.25f, h * 0.25f),
            style = stroke,
        )
        // 3 reaction dots / faces
        drawCircle(tint.copy(alpha = 0.85f), radius = 2.dp.toPx(), center = Offset(w * 0.28f, h * 0.5f))
        drawCircle(tint.copy(alpha = 0.85f), radius = 2.8.dp.toPx(), center = Offset(w * 0.5f, h * 0.45f))
        drawCircle(tint.copy(alpha = 0.85f), radius = 2.dp.toPx(), center = Offset(w * 0.72f, h * 0.5f))
    }
}

@Composable
fun SwipeCardsMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Background card tilted left
        drawRoundRect(
            color = tint.copy(alpha = 0.35f),
            topLeft = Offset(w * 0.08f, h * 0.16f),
            size = Size(w * 0.65f, h * 0.75f),
            cornerRadius = CornerRadius(w * 0.1f, w * 0.1f),
            style = stroke,
        )
        // Foreground card tilted right
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.26f, h * 0.1f),
            size = Size(w * 0.65f, h * 0.75f),
            cornerRadius = CornerRadius(w * 0.1f, w * 0.1f),
            style = stroke,
        )
        // Heart symbol in foreground card
        drawCircle(tint, radius = 2.5.dp.toPx(), center = Offset(w * 0.58f, h * 0.42f))
    }
}

@Composable
fun SwipeActionsMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Main row card
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.05f, h * 0.22f),
            size = Size(w * 0.6f, h * 0.56f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
            style = stroke,
        )
        // Action box 1
        drawRoundRect(
            color = tint.copy(alpha = 0.45f),
            topLeft = Offset(w * 0.68f, h * 0.22f),
            size = Size(w * 0.13f, h * 0.56f),
            cornerRadius = CornerRadius(w * 0.04f, w * 0.04f),
        )
        // Action box 2
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.83f, h * 0.22f),
            size = Size(w * 0.13f, h * 0.56f),
            cornerRadius = CornerRadius(w * 0.04f, w * 0.04f),
        )
    }
}

@Composable
fun MorphingFabMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Circle base
        drawCircle(
            color = tint.copy(alpha = 0.35f),
            radius = w * 0.2f,
            center = Offset(w * 0.32f, h * 0.68f),
            style = stroke,
        )
        // Expanding card
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.35f, h * 0.12f),
            size = Size(w * 0.55f, h * 0.55f),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
            style = stroke,
        )
        // Plus inside card
        drawLine(tint, Offset(w * 0.625f, h * 0.28f), Offset(w * 0.625f, h * 0.52f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.505f, h * 0.40f), Offset(w * 0.745f, h * 0.40f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun PrivacyMaskingMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Shield path
        val shield = Path().apply {
            moveTo(w * 0.5f, h * 0.1f)
            lineTo(w * 0.85f, h * 0.22f)
            lineTo(w * 0.85f, h * 0.55f)
            cubicTo(w * 0.85f, h * 0.78f, w * 0.5f, h * 0.92f, w * 0.5f, h * 0.92f)
            cubicTo(w * 0.5f, h * 0.92f, w * 0.15f, h * 0.78f, w * 0.15f, h * 0.55f)
            lineTo(w * 0.15f, h * 0.22f)
            close()
        }
        drawPath(shield, tint, style = stroke)
        // Center lock dot
        drawCircle(tint, radius = 2.2.dp.toPx(), center = Offset(w * 0.5f, h * 0.48f))
    }
}

@Composable
fun BalanceMaskingMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Eye contour
        val eyeTop = Path().apply {
            moveTo(w * 0.1f, h * 0.5f)
            quadraticTo(w * 0.5f, h * 0.15f, w * 0.9f, h * 0.5f)
            quadraticTo(w * 0.5f, h * 0.85f, w * 0.1f, h * 0.5f)
        }
        drawPath(eyeTop, tint, style = stroke)
        // Diagonal slash through eye
        drawLine(tint, Offset(w * 0.18f, h * 0.82f), Offset(w * 0.82f, h * 0.18f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun ScratchCardMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Ticket rect
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.08f, h * 0.2f),
            size = Size(w * 0.84f, h * 0.6f),
            cornerRadius = CornerRadius(w * 0.1f, w * 0.1f),
            style = stroke,
        )
        // Diagonal scratch marks inside
        drawLine(tint.copy(alpha = 0.5f), Offset(w * 0.25f, h * 0.65f), Offset(w * 0.45f, h * 0.35f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(tint.copy(alpha = 0.8f), Offset(w * 0.40f, h * 0.65f), Offset(w * 0.60f, h * 0.35f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(tint.copy(alpha = 0.5f), Offset(w * 0.55f, h * 0.65f), Offset(w * 0.75f, h * 0.35f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun StreakHeatmapMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        // 3x3 Mini Grid with varied alphas
        val cell = w * 0.22f
        val gap = w * 0.07f
        val startX = w * 0.08f
        val startY = h * 0.08f
        val alphas = listOf(
            0.2f, 0.6f, 1.0f,
            0.4f, 0.9f, 0.3f,
            0.8f, 0.3f, 0.7f,
        )
        var i = 0
        for (r in 0..2) {
            for (c in 0..2) {
                drawRoundRect(
                    color = tint.copy(alpha = alphas[i]),
                    topLeft = Offset(startX + c * (cell + gap), startY + r * (cell + gap)),
                    size = Size(cell, cell),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                )
                i++
            }
        }
    }
}
