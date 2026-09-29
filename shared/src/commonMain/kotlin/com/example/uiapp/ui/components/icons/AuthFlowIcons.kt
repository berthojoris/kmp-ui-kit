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
fun SplashMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.7.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(
            color = tint,
            radius = w * 0.26f,
            center = Offset(w / 2, h / 2),
            style = stroke,
        )
        val needle = Path().apply {
            moveTo(w * 0.5f, h * 0.34f)
            lineTo(w * 0.6f, h * 0.5f)
            lineTo(w * 0.4f, h * 0.5f)
            close()
        }
        drawPath(needle, tint)
        val needle2 = Path().apply {
            moveTo(w * 0.5f, h * 0.66f)
            lineTo(w * 0.4f, h * 0.5f)
            lineTo(w * 0.6f, h * 0.5f)
            close()
        }
        drawPath(needle2, tint.copy(alpha = 0.4f))
        listOf(
            Offset(w * 0.5f, h * 0.04f) to Offset(w * 0.5f, h * 0.14f),
            Offset(w * 0.5f, h * 0.86f) to Offset(w * 0.5f, h * 0.96f),
            Offset(w * 0.04f, h * 0.5f) to Offset(w * 0.14f, h * 0.5f),
            Offset(w * 0.86f, h * 0.5f) to Offset(w * 0.96f, h * 0.5f),
        ).forEach { (start, end) ->
            drawLine(tint.copy(alpha = 0.55f), start, end, 1.7.dp.toPx(), StrokeCap.Round)
        }
    }
}

@Composable
fun OnboardingMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.7.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.16f, h * 0.2f),
            size = Size(w * 0.68f, h * 0.44f),
            cornerRadius = CornerRadius(w * 0.1f, w * 0.1f),
            style = stroke,
        )
        drawLine(
            color = tint.copy(alpha = 0.5f),
            start = Offset(w * 0.3f, h * 0.48f),
            end = Offset(w * 0.7f, h * 0.48f),
            strokeWidth = 1.7.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawCircle(color = tint.copy(alpha = 0.5f), radius = w * 0.05f, center = Offset(w * 0.38f, h * 0.34f))
        listOf(0.38f, 0.5f, 0.62f).forEachIndexed { index, x ->
            drawCircle(
                color = if (index == 1) tint else tint.copy(alpha = 0.35f),
                radius = w * 0.045f,
                center = Offset(w * x, h * 0.8f),
            )
        }
    }
}

@Composable
fun AuthMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.7.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.26f, h * 0.44f),
            size = Size(w * 0.48f, h * 0.4f),
            cornerRadius = CornerRadius(w * 0.1f, w * 0.1f),
            style = stroke,
        )
        val shackle = Path().apply {
            moveTo(w * 0.36f, h * 0.44f)
            lineTo(w * 0.36f, h * 0.32f)
            cubicTo(w * 0.36f, h * 0.14f, w * 0.64f, h * 0.14f, w * 0.64f, h * 0.32f)
            lineTo(w * 0.64f, h * 0.44f)
        }
        drawPath(shackle, tint, style = stroke)
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.62f))
        drawLine(tint, Offset(w * 0.5f, h * 0.66f), Offset(w * 0.5f, h * 0.74f), 1.7.dp.toPx(), StrokeCap.Round)
    }
}

@Composable
fun BiometricMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.7.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val corner = w * 0.18f
        val inset = w * 0.12f
        listOf(
            Offset(inset, inset) to Pair(1f, 1f),
            Offset(w - inset, inset) to Pair(-1f, 1f),
            Offset(inset, h - inset) to Pair(1f, -1f),
            Offset(w - inset, h - inset) to Pair(-1f, -1f),
        ).forEach { (point, dir) ->
            drawLine(tint, point, Offset(point.x + corner * dir.first, point.y), 1.7.dp.toPx(), StrokeCap.Round)
            drawLine(tint, point, Offset(point.x, point.y + corner * dir.second), 1.7.dp.toPx(), StrokeCap.Round)
        }
        drawCircle(color = tint, radius = w * 0.035f, center = Offset(w * 0.38f, h * 0.44f))
        drawCircle(color = tint, radius = w * 0.035f, center = Offset(w * 0.62f, h * 0.44f))
        drawLine(tint, Offset(w * 0.5f, h * 0.44f), Offset(w * 0.5f, h * 0.56f), 1.5.dp.toPx(), StrokeCap.Round)
        val smile = Path().apply {
            moveTo(w * 0.4f, h * 0.64f)
            cubicTo(w * 0.46f, h * 0.7f, w * 0.54f, h * 0.7f, w * 0.6f, h * 0.64f)
        }
        drawPath(smile, tint, style = stroke)
    }
}

@Composable
fun PasscodeMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val columns = listOf(0.3f, 0.5f, 0.7f)
        val rows = listOf(0.28f, 0.5f, 0.72f)
        var index = 0
        rows.forEach { y ->
            columns.forEach { x ->
                val filled = index < 2
                if (filled) {
                    drawCircle(color = tint, radius = w * 0.09f, center = Offset(w * x, h * y))
                } else {
                    drawCircle(
                        color = tint.copy(alpha = 0.55f),
                        radius = w * 0.07f,
                        center = Offset(w * x, h * y),
                        style = Stroke(width = 1.6.dp.toPx()),
                    )
                }
                index += 1
            }
        }
    }
}

@Composable
fun PermissionsMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.7.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val shield = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            lineTo(w * 0.82f, h * 0.26f)
            lineTo(w * 0.82f, h * 0.5f)
            cubicTo(w * 0.82f, h * 0.72f, w * 0.5f, h * 0.88f, w * 0.5f, h * 0.88f)
            cubicTo(w * 0.5f, h * 0.88f, w * 0.18f, h * 0.72f, w * 0.18f, h * 0.5f)
            lineTo(w * 0.18f, h * 0.26f)
            close()
        }
        drawPath(shield, tint, style = stroke)
        val check = Path().apply {
            moveTo(w * 0.36f, h * 0.5f)
            lineTo(w * 0.46f, h * 0.62f)
            lineTo(w * 0.66f, h * 0.38f)
        }
        drawPath(check, tint, style = stroke)
    }
}

@Composable
fun ProfileSetupMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.7.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(
            color = tint,
            radius = w * 0.13f,
            center = Offset(w * 0.34f, h * 0.32f),
            style = stroke,
        )
        val body = Path().apply {
            moveTo(w * 0.12f, h * 0.82f)
            cubicTo(w * 0.14f, h * 0.6f, w * 0.54f, h * 0.6f, w * 0.56f, h * 0.82f)
        }
        drawPath(body, tint, style = stroke)

        drawLine(
            color = tint.copy(alpha = 0.55f),
            start = Offset(w * 0.64f, h * 0.38f),
            end = Offset(w * 0.92f, h * 0.38f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawCircle(color = tint, radius = w * 0.055f, center = Offset(w * 0.74f, h * 0.38f))
        drawLine(
            color = tint.copy(alpha = 0.55f),
            start = Offset(w * 0.64f, h * 0.64f),
            end = Offset(w * 0.92f, h * 0.64f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawCircle(color = tint, radius = w * 0.055f, center = Offset(w * 0.86f, h * 0.64f))
    }
}
