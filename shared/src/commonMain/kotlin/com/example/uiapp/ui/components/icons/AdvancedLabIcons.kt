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

@Composable
fun NavStructureMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Draw sidebar rail
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.08f, h * 0.12f),
            size = Size(w * 0.22f, h * 0.76f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = stroke,
        )
        // Main content area
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.36f, h * 0.12f),
            size = Size(w * 0.56f, h * 0.48f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = stroke,
        )
        // Floating pill bar at bottom
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.40f, h * 0.72f),
            size = Size(w * 0.48f, h * 0.16f),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
            style = stroke,
        )
    }
}

@Composable
fun AdvancedListMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // 3 horizontal reorder items with handle dots
        for (i in 0..2) {
            val y = h * (0.24f + i * 0.26f)
            drawCircle(color = tint, radius = 1.4.dp.toPx(), center = Offset(w * 0.16f, y))
            drawCircle(color = tint, radius = 1.4.dp.toPx(), center = Offset(w * 0.24f, y))
            drawLine(
                color = tint,
                start = Offset(w * 0.38f, y),
                end = Offset(w * (0.86f - i * 0.08f), y),
                strokeWidth = 1.8.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
fun DataCardsMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Calendar frame
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.12f, h * 0.16f),
            size = Size(w * 0.76f, h * 0.72f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = stroke,
        )
        // Top header line
        drawLine(
            color = tint,
            start = Offset(w * 0.12f, h * 0.38f),
            end = Offset(w * 0.88f, h * 0.38f),
            strokeWidth = 1.5.dp.toPx(),
        )
        // Event dots
        val dotRadius = 1.8.dp.toPx()
        drawCircle(color = tint, radius = dotRadius, center = Offset(w * 0.32f, h * 0.56f))
        drawCircle(color = tint, radius = dotRadius, center = Offset(w * 0.50f, h * 0.56f))
        drawCircle(color = tint, radius = dotRadius, center = Offset(w * 0.68f, h * 0.56f))
        drawCircle(color = tint, radius = dotRadius, center = Offset(w * 0.32f, h * 0.72f))
        drawCircle(color = tint, radius = dotRadius, center = Offset(w * 0.50f, h * 0.72f))
    }
}

@Composable
fun AdvancedInputMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Pen / stylus angled
        val penPath = Path().apply {
            moveTo(w * 0.78f, h * 0.14f)
            lineTo(w * 0.86f, h * 0.22f)
            lineTo(w * 0.36f, h * 0.72f)
            lineTo(w * 0.22f, h * 0.78f)
            lineTo(w * 0.28f, h * 0.64f)
            close()
        }
        drawPath(penPath, color = tint, style = stroke)
        // Signature wave line at bottom
        val sigPath = Path().apply {
            moveTo(w * 0.18f, h * 0.88f)
            cubicTo(w * 0.34f, h * 0.82f, w * 0.44f, h * 0.94f, w * 0.62f, h * 0.86f)
            lineTo(w * 0.82f, h * 0.88f)
        }
        drawPath(sigPath, color = tint, style = stroke)
    }
}

@Composable
fun OverlayMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Background card outline
        drawRoundRect(
            color = tint.copy(alpha = 0.4f),
            topLeft = Offset(w * 0.12f, h * 0.12f),
            size = Size(w * 0.76f, h * 0.76f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = stroke,
        )
        // Floating action sheet in foreground
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.20f, h * 0.46f),
            size = Size(w * 0.60f, h * 0.42f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = stroke,
        )
        // Spotlight ring
        drawCircle(
            color = tint,
            radius = w * 0.14f,
            center = Offset(w * 0.50f, h * 0.30f),
            style = stroke,
        )
    }
}

@Composable
fun MediaMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Display frame
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.10f, h * 0.16f),
            size = Size(w * 0.80f, h * 0.54f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = stroke,
        )
        // Play triangle inside
        val playPath = Path().apply {
            moveTo(w * 0.44f, h * 0.32f)
            lineTo(w * 0.62f, h * 0.43f)
            lineTo(w * 0.44f, h * 0.54f)
            close()
        }
        drawPath(playPath, color = tint)
        // Sound waveform bars at bottom
        val barHeights = listOf(0.12f, 0.22f, 0.14f, 0.26f, 0.16f)
        barHeights.forEachIndexed { i, bh ->
            val bx = w * (0.24f + i * 0.13f)
            drawLine(
                color = tint,
                start = Offset(bx, h * 0.88f - h * bh),
                end = Offset(bx, h * 0.88f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
fun SettingsMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Gear / setting switches
        val trackY1 = h * 0.32f
        val trackY2 = h * 0.68f
        drawLine(
            color = tint,
            start = Offset(w * 0.14f, trackY1),
            end = Offset(w * 0.86f, trackY1),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawCircle(color = tint, radius = 3.5.dp.toPx(), center = Offset(w * 0.38f, trackY1))
        drawLine(
            color = tint,
            start = Offset(w * 0.14f, trackY2),
            end = Offset(w * 0.86f, trackY2),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawCircle(color = tint, radius = 3.5.dp.toPx(), center = Offset(w * 0.68f, trackY2))
    }
}

@Composable
fun CommerceMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Shopping bag body
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.16f, h * 0.32f),
            size = Size(w * 0.68f, h * 0.56f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = stroke,
        )
        // Shopping bag handle
        val handlePath = Path().apply {
            moveTo(w * 0.32f, h * 0.32f)
            cubicTo(w * 0.32f, h * 0.12f, w * 0.68f, h * 0.12f, w * 0.68f, h * 0.32f)
        }
        drawPath(handlePath, color = tint, style = stroke)
        // Small tag/price mark
        drawLine(
            color = tint,
            start = Offset(w * 0.50f, h * 0.52f),
            end = Offset(w * 0.50f, h * 0.68f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun SystemPlatformMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // QR Viewfinder 4 corners
        val len = w * 0.22f
        // Top-left
        drawLine(tint, Offset(w * 0.12f, h * 0.16f), Offset(w * 0.12f + len, h * 0.16f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.12f, h * 0.16f), Offset(w * 0.12f, h * 0.16f + len), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        // Top-right
        drawLine(tint, Offset(w * 0.88f, h * 0.16f), Offset(w * 0.88f - len, h * 0.16f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.88f, h * 0.16f), Offset(w * 0.88f, h * 0.16f + len), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        // Bottom-left
        drawLine(tint, Offset(w * 0.12f, h * 0.84f), Offset(w * 0.12f + len, h * 0.84f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.12f, h * 0.84f), Offset(w * 0.12f, h * 0.84f - len), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        // Bottom-right
        drawLine(tint, Offset(w * 0.88f, h * 0.84f), Offset(w * 0.88f - len, h * 0.84f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.88f, h * 0.84f), Offset(w * 0.88f, h * 0.84f - len), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        // Scanning laser line in center
        drawLine(
            color = tint,
            start = Offset(w * 0.22f, h * 0.50f),
            end = Offset(w * 0.78f, h * 0.50f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}
