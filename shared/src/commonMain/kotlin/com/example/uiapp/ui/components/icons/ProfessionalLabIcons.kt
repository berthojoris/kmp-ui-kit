package com.example.uiapp.ui.components.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.uiapp.theme.LightAppPalette

@Composable
fun FocusTimerMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        // Stopwatch body ring
        drawCircle(
            color = tint,
            radius = w * 0.36f,
            center = Offset(w * 0.5f, h * 0.58f),
            style = stroke,
        )
        // Crown button on top
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.42f, h * 0.06f),
            size = Size(w * 0.16f, h * 0.12f),
            cornerRadius = CornerRadius(w * 0.03f, w * 0.03f),
        )
        // Progress arc inside the ring
        drawArc(
            color = tint,
            startAngle = -90f,
            sweepAngle = 200f,
            useCenter = false,
            topLeft = Offset(w * 0.26f, h * 0.34f),
            size = Size(w * 0.48f, h * 0.48f),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
        )
        // Center dot
        drawCircle(tint, radius = 1.6.dp.toPx(), center = Offset(w * 0.5f, h * 0.58f))
    }
}

@Composable
fun MasonryGridMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Left column: tall card + short card
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.1f, h * 0.1f),
            size = Size(w * 0.36f, h * 0.5f),
            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f),
            style = stroke,
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.1f, h * 0.68f),
            size = Size(w * 0.36f, h * 0.22f),
            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f),
        )
        // Right column: short card + tall card
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.54f, h * 0.1f),
            size = Size(w * 0.36f, h * 0.24f),
            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f),
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.54f, h * 0.42f),
            size = Size(w * 0.36f, h * 0.48f),
            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f),
            style = stroke,
        )
    }
}

@Composable
fun AmountKeypadMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        // Display bar on top
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.12f, h * 0.08f),
            size = Size(w * 0.76f, h * 0.18f),
            cornerRadius = CornerRadius(w * 0.04f, w * 0.04f),
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round),
        )
        // Keypad dots 3x3
        val dotRadius = 1.7.dp.toPx()
        val columns = listOf(0.24f, 0.5f, 0.76f)
        val rows = listOf(0.42f, 0.62f, 0.82f)
        rows.forEach { rowY ->
            columns.forEach { colX ->
                drawCircle(tint, radius = dotRadius, center = Offset(w * colX, h * rowY))
            }
        }
    }
}

@Composable
fun CategoryScrollMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        // Chip row on top: active chip filled, others outlined
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.08f, h * 0.12f),
            size = Size(w * 0.34f, h * 0.18f),
            cornerRadius = CornerRadius(h * 0.09f, h * 0.09f),
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.48f, h * 0.12f),
            size = Size(w * 0.26f, h * 0.18f),
            cornerRadius = CornerRadius(h * 0.09f, h * 0.09f),
            style = stroke,
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.8f, h * 0.12f),
            size = Size(w * 0.12f, h * 0.18f),
            cornerRadius = CornerRadius(h * 0.09f, h * 0.09f),
            style = stroke,
        )
        // List rows below
        drawLine(tint, Offset(w * 0.08f, h * 0.52f), Offset(w * 0.92f, h * 0.52f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.08f, h * 0.68f), Offset(w * 0.7f, h * 0.68f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.08f, h * 0.84f), Offset(w * 0.82f, h * 0.84f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun DataTableMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Outer frame
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.08f, h * 0.12f),
            size = Size(w * 0.84f, h * 0.76f),
            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f),
            style = stroke,
        )
        // Header row fill
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.08f, h * 0.12f),
            size = Size(w * 0.84f, h * 0.2f),
            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f),
        )
        // Sticky first column divider
        drawLine(tint, Offset(w * 0.34f, h * 0.32f), Offset(w * 0.34f, h * 0.88f), strokeWidth = 1.5.dp.toPx())
        // Row separators
        drawLine(tint.copy(alpha = 0.6f), Offset(w * 0.08f, h * 0.5f), Offset(w * 0.92f, h * 0.5f), strokeWidth = 1.2.dp.toPx())
        drawLine(tint.copy(alpha = 0.6f), Offset(w * 0.08f, h * 0.69f), Offset(w * 0.92f, h * 0.69f), strokeWidth = 1.2.dp.toPx())
    }
}
