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
fun HoldToConfirmMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
        // Outer progress arc
        drawArc(
            color = tint,
            startAngle = -90f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(w * 0.12f, h * 0.12f),
            size = Size(w * 0.76f, h * 0.76f),
            style = stroke,
        )
        // Center press target dot
        drawCircle(
            color = tint,
            radius = w * 0.16f,
            center = Offset(w * 0.5f, h * 0.5f),
        )
    }
}

@Composable
fun SlideToConfirmMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
        // Pill track
        drawRoundRect(
            color = tint.copy(alpha = 0.35f),
            topLeft = Offset(w * 0.05f, h * 0.28f),
            size = Size(w * 0.9f, h * 0.44f),
            cornerRadius = CornerRadius(h * 0.22f, h * 0.22f),
            style = stroke,
        )
        // Draggable knob
        drawCircle(
            color = tint,
            radius = h * 0.18f,
            center = Offset(w * 0.26f, h * 0.5f),
        )
        // Arrow right
        val path = Path().apply {
            moveTo(w * 0.58f, h * 0.42f)
            lineTo(w * 0.68f, h * 0.5f)
            lineTo(w * 0.58f, h * 0.58f)
        }
        drawPath(path, tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun SplitComparisonMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        // Outer card
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.08f, h * 0.15f),
            size = Size(w * 0.84f, h * 0.7f),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
            style = stroke,
        )
        // Vertical split line
        drawLine(
            color = tint,
            start = Offset(w * 0.5f, h * 0.15f),
            end = Offset(w * 0.5f, h * 0.85f),
            strokeWidth = 1.5.dp.toPx(),
        )
        // Split slider knob
        drawCircle(
            color = tint,
            radius = 2.8.dp.toPx(),
            center = Offset(w * 0.5f, h * 0.5f),
        )
    }
}

@Composable
fun BentoGridMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        // Large left block
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.06f, h * 0.08f),
            size = Size(w * 0.42f, h * 0.84f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
        )
        // Top right block
        drawRoundRect(
            color = tint.copy(alpha = 0.5f),
            topLeft = Offset(w * 0.54f, h * 0.08f),
            size = Size(w * 0.4f, h * 0.38f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
        )
        // Bottom right block
        drawRoundRect(
            color = tint.copy(alpha = 0.75f),
            topLeft = Offset(w * 0.54f, h * 0.54f),
            size = Size(w * 0.4f, h * 0.38f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
        )
    }
}

@Composable
fun PerforatedTicketMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.08f, h * 0.16f),
            size = Size(w * 0.84f, h * 0.68f),
            cornerRadius = CornerRadius(w * 0.1f, w * 0.1f),
            style = stroke,
        )
        // Perforation dashed line
        var y = h * 0.24f
        while (y <= h * 0.76f) {
            drawLine(
                color = tint.copy(alpha = 0.6f),
                start = Offset(w * 0.62f, y),
                end = Offset(w * 0.62f, y + 2.dp.toPx()),
                strokeWidth = 1.2.dp.toPx(),
            )
            y += 5.dp.toPx()
        }
    }
}

@Composable
fun AiVoiceOrbMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
        // Outer concentric ring
        drawCircle(
            color = tint.copy(alpha = 0.3f),
            radius = w * 0.42f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = stroke,
        )
        // Mid ring
        drawCircle(
            color = tint.copy(alpha = 0.6f),
            radius = w * 0.28f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = stroke,
        )
        // Inner orb
        drawCircle(
            color = tint,
            radius = w * 0.14f,
            center = Offset(w * 0.5f, h * 0.5f),
        )
    }
}
