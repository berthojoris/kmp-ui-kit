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
fun GamificationMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Trophy cup
        val cup = Path().apply {
            moveTo(w * 0.30f, h * 0.16f)
            lineTo(w * 0.70f, h * 0.16f)
            lineTo(w * 0.66f, h * 0.44f)
            cubicTo(w * 0.64f, h * 0.56f, w * 0.36f, h * 0.56f, w * 0.34f, h * 0.44f)
            close()
        }
        drawPath(cup, tint, style = stroke)
        // Handles
        drawArc(
            color = tint,
            startAngle = 90f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.10f, h * 0.20f),
            size = Size(w * 0.24f, h * 0.24f),
            style = stroke,
        )
        drawArc(
            color = tint,
            startAngle = 270f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.66f, h * 0.20f),
            size = Size(w * 0.24f, h * 0.24f),
            style = stroke,
        )
        // Stem & base
        drawLine(tint, Offset(w * 0.50f, h * 0.54f), Offset(w * 0.50f, h * 0.74f), strokeWidth = 1.7.dp.toPx(), cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.34f, h * 0.84f), Offset(w * 0.66f, h * 0.84f), strokeWidth = 1.9.dp.toPx(), cap = StrokeCap.Round)
        // Spark
        drawCircle(tint, radius = 1.6.dp.toPx(), center = Offset(w * 0.82f, h * 0.14f))
    }
}

@Composable
fun DataVizMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val cx = w * 0.5f
        val cy = h * 0.5f
        val radius = w * 0.40f
        // Radar axes (pentagon-ish spokes)
        repeat(5) { i ->
            val angle = (-90f + i * 72f) * (kotlin.math.PI / 180f).toFloat()
            val ex = cx + radius * kotlin.math.cos(angle)
            val ey = cy + radius * kotlin.math.sin(angle)
            drawLine(tint.copy(alpha = 0.35f), Offset(cx, cy), Offset(ex, ey), strokeWidth = 1.2.dp.toPx(), cap = StrokeCap.Round)
        }
        // Data polygon
        val data = listOf(0.9f, 0.55f, 0.75f, 0.45f, 0.85f)
        val poly = Path()
        data.forEachIndexed { i, f ->
            val angle = (-90f + i * 72f) * (kotlin.math.PI / 180f).toFloat()
            val px = cx + radius * f * kotlin.math.cos(angle)
            val py = cy + radius * f * kotlin.math.sin(angle)
            if (i == 0) poly.moveTo(px, py) else poly.lineTo(px, py)
        }
        poly.close()
        drawPath(poly, tint.copy(alpha = 0.55f))
        drawPath(poly, tint, style = stroke)
    }
}

@Composable
fun ScrollMotionMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        // Two text lines suggesting a marquee strip
        drawRoundRect(
            color = tint.copy(alpha = 0.75f),
            topLeft = Offset(w * 0.10f, h * 0.26f),
            size = Size(w * 0.80f, h * 0.14f),
            cornerRadius = CornerRadius(h * 0.07f, h * 0.07f),
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.45f),
            topLeft = Offset(w * 0.10f, h * 0.52f),
            size = Size(w * 0.56f, h * 0.14f),
            cornerRadius = CornerRadius(h * 0.07f, h * 0.07f),
        )
        // Directional chevrons
        val chevron = Path().apply {
            moveTo(w * 0.66f, h * 0.74f)
            lineTo(w * 0.78f, h * 0.86f)
            lineTo(w * 0.90f, h * 0.74f)
        }
        drawPath(chevron, tint, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun CreateMediaMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Three filter sliders
        val rows = listOf(0.28f to 0.34f, 0.5f to 0.70f, 0.72f to 0.46f)
        rows.forEach { (y, knob) ->
            drawLine(tint.copy(alpha = 0.5f), Offset(w * 0.12f, h * y), Offset(w * 0.88f, h * y), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
            drawCircle(tint, radius = 2.4.dp.toPx(), center = Offset(w * knob, h * y))
        }
        // Sparkle
        val spark = Path().apply {
            moveTo(w * 0.86f, h * 0.14f)
            lineTo(w * 0.90f, h * 0.22f)
            lineTo(w * 0.98f, h * 0.26f)
            lineTo(w * 0.90f, h * 0.30f)
            lineTo(w * 0.86f, h * 0.38f)
            lineTo(w * 0.82f, h * 0.30f)
            lineTo(w * 0.74f, h * 0.26f)
            lineTo(w * 0.82f, h * 0.22f)
            close()
        }
        drawPath(spark, tint.copy(alpha = 0.8f))
    }
}
