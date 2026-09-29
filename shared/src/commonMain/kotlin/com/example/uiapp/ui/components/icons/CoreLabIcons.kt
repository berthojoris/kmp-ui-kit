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
fun AutoBlurScrollMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val barHeight = this.size.height * 0.16f
        val gap = this.size.height * 0.12f
        val alphas = listOf(0.22f, 0.45f, 0.72f, 1f)
        var y = 0f
        alphas.forEach { alpha ->
            drawRoundRect(
                color = tint.copy(alpha = alpha),
                topLeft = Offset(0f, y),
                size = Size(this.size.width, barHeight),
                cornerRadius = CornerRadius(barHeight / 2f, barHeight / 2f),
            )
            y += barHeight + gap
        }
    }
}

@Composable
fun StickyHeaderMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawRoundRect(
            color = tint,
            topLeft = Offset(0f, 0f),
            size = Size(w, h * 0.24f),
            cornerRadius = CornerRadius(h * 0.06f, h * 0.06f),
        )

        val lineHeight = h * 0.13f
        val gap = h * 0.11f
        var y = h * 0.24f + gap
        repeat(3) { index ->
            drawRoundRect(
                color = tint.copy(alpha = 0.35f),
                topLeft = Offset(0f, y),
                size = Size(w * (if (index == 2) 0.6f else 1f), lineHeight),
                cornerRadius = CornerRadius(lineHeight / 2f, lineHeight / 2f),
            )
            y += lineHeight + gap
        }
    }
}

@Composable
fun ParallaxHeroMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawCircle(
            color = tint.copy(alpha = 0.5f),
            radius = h * 0.13f,
            center = Offset(w * 0.72f, h * 0.26f),
        )

        val back = Path().apply {
            moveTo(0f, h)
            lineTo(w * 0.32f, h * 0.5f)
            lineTo(w * 0.55f, h * 0.74f)
            lineTo(w * 0.74f, h * 0.5f)
            lineTo(w, h * 0.82f)
            lineTo(w, h)
            close()
        }
        drawPath(back, tint.copy(alpha = 0.75f))

        val front = Path().apply {
            moveTo(0f, h)
            lineTo(w * 0.4f, h * 0.72f)
            lineTo(w, h * 0.96f)
            lineTo(w, h)
            close()
        }
        drawPath(front, tint.copy(alpha = 0.4f))
    }
}

@Composable
fun ShimmerMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawRoundRect(
            color = tint.copy(alpha = 0.5f),
            topLeft = Offset(0f, 0f),
            size = Size(w, h),
            cornerRadius = CornerRadius(w * 0.14f, w * 0.14f),
            style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )

        drawRoundRect(
            color = tint.copy(alpha = 0.4f),
            topLeft = Offset(w * 0.16f, h * 0.3f),
            size = Size(w * 0.55f, h * 0.1f),
            cornerRadius = CornerRadius(h * 0.05f, h * 0.05f),
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.28f),
            topLeft = Offset(w * 0.16f, h * 0.5f),
            size = Size(w * 0.4f, h * 0.1f),
            cornerRadius = CornerRadius(h * 0.05f, h * 0.05f),
        )

        drawLine(
            color = tint,
            start = Offset(w * 0.1f, h * 0.78f),
            end = Offset(w * 0.9f, h * 0.22f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun SharedElementMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(
            width = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )

        drawRoundRect(
            color = tint.copy(alpha = 0.4f),
            topLeft = Offset(0f, 0f),
            size = Size(w * 0.64f, h * 0.64f),
            cornerRadius = CornerRadius(w * 0.16f, w * 0.16f),
            style = stroke,
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.36f, h * 0.36f),
            size = Size(w * 0.64f, h * 0.64f),
            cornerRadius = CornerRadius(w * 0.16f, w * 0.16f),
            style = stroke,
        )
    }
}

@Composable
fun BottomSheetMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(
            width = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )

        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.14f, h * 0.46f),
            size = Size(w * 0.72f, h * 0.46f),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
            style = stroke,
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.7f),
            topLeft = Offset(w * 0.4f, h * 0.56f),
            size = Size(w * 0.2f, h * 0.06f),
            cornerRadius = CornerRadius(h * 0.03f, h * 0.03f),
        )
        drawLine(
            color = tint.copy(alpha = 0.6f),
            start = Offset(w * 0.5f, h * 0.32f),
            end = Offset(w * 0.5f, h * 0.06f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint.copy(alpha = 0.6f),
            start = Offset(w * 0.5f, h * 0.06f),
            end = Offset(w * 0.36f, h * 0.18f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint.copy(alpha = 0.6f),
            start = Offset(w * 0.5f, h * 0.06f),
            end = Offset(w * 0.64f, h * 0.18f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun MotionLabMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        drawRoundRect(
            color = tint.copy(alpha = 0.85f),
            topLeft = Offset(w * 0.1f, h * 0.12f),
            size = Size(w * 0.8f, h * 0.42f),
            cornerRadius = CornerRadius(w * 0.14f, w * 0.14f),
        )
        drawLine(
            color = tint.copy(alpha = 0.5f),
            start = Offset(w * 0.5f, h * 0.62f),
            end = Offset(w * 0.5f, h * 0.86f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint.copy(alpha = 0.5f),
            start = Offset(w * 0.5f, h * 0.86f),
            end = Offset(w * 0.36f, h * 0.74f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint.copy(alpha = 0.5f),
            start = Offset(w * 0.5f, h * 0.86f),
            end = Offset(w * 0.64f, h * 0.74f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun FormLabMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawRoundRect(
            color = tint.copy(alpha = 0.7f),
            topLeft = Offset(w * 0.08f, h * 0.24f),
            size = Size(w * 0.84f, h * 0.52f),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
            style = stroke,
        )
        drawLine(
            color = tint.copy(alpha = 0.45f),
            start = Offset(w * 0.2f, h * 0.5f),
            end = Offset(w * 0.58f, h * 0.5f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.68f, h * 0.38f),
            end = Offset(w * 0.68f, h * 0.62f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun NavLabMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        drawRoundRect(
            color = tint.copy(alpha = 0.75f),
            topLeft = Offset(w * 0.26f, h * 0.08f),
            size = Size(w * 0.48f, h * 0.34f),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
        )
        drawLine(
            color = tint.copy(alpha = 0.3f),
            start = Offset(w * 0.06f, h * 0.62f),
            end = Offset(w * 0.94f, h * 0.62f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        listOf(0.22f, 0.5f, 0.78f).forEach { x ->
            drawCircle(
                color = tint.copy(alpha = 0.55f),
                radius = w * 0.07f,
                center = Offset(w * x, h * 0.82f),
            )
        }
    }
}

@Composable
fun AdaptiveMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val gap = w * 0.06f
        val cell = (w - gap * 2) / 3f
        repeat(3) { col ->
            repeat(2) { row ->
                drawRoundRect(
                    color = tint.copy(alpha = 0.35f + 0.2f * col),
                    topLeft = Offset(col * (cell + gap), row * (cell + gap)),
                    size = Size(cell, cell),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
                )
            }
        }
    }
}

@Composable
fun GalleryMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        drawRoundRect(
            color = tint.copy(alpha = 0.85f),
            topLeft = Offset(w * 0.14f, h * 0.26f),
            size = Size(w * 0.72f, h * 0.58f),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
            style = stroke,
        )
        drawCircle(
            color = tint.copy(alpha = 0.6f),
            radius = w * 0.08f,
            center = Offset(w * 0.36f, h * 0.44f),
        )
        val hill = Path().apply {
            moveTo(w * 0.18f, h * 0.8f)
            lineTo(w * 0.44f, h * 0.58f)
            lineTo(w * 0.62f, h * 0.72f)
            lineTo(w * 0.78f, h * 0.6f)
            lineTo(w * 0.84f, h * 0.8f)
            close()
        }
        drawPath(hill, tint.copy(alpha = 0.45f))
    }
}

@Composable
fun FeedbackMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        drawRoundRect(
            color = tint.copy(alpha = 0.85f),
            topLeft = Offset(w * 0.06f, h * 0.12f),
            size = Size(w * 0.88f, h * 0.34f),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
        )
        drawCircle(
            color = tint,
            radius = w * 0.07f,
            center = Offset(w * 0.74f, h * 0.29f),
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.4f),
            topLeft = Offset(w * 0.16f, h * 0.6f),
            size = Size(w * 0.68f, h * 0.14f),
            cornerRadius = CornerRadius(h * 0.07f, h * 0.07f),
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.25f),
            topLeft = Offset(w * 0.24f, h * 0.8f),
            size = Size(w * 0.52f, h * 0.12f),
            cornerRadius = CornerRadius(h * 0.06f, h * 0.06f),
        )
    }
}

@Composable
fun StatsMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        drawArc(
            color = tint.copy(alpha = 0.3f),
            startAngle = 0f,
            sweepAngle = 300f,
            useCenter = false,
            topLeft = Offset(w * 0.06f, h * 0.06f),
            size = Size(w * 0.7f, h * 0.7f),
            style = stroke,
        )
        drawArc(
            color = tint,
            startAngle = -90f,
            sweepAngle = 210f,
            useCenter = false,
            topLeft = Offset(w * 0.06f, h * 0.06f),
            size = Size(w * 0.7f, h * 0.7f),
            style = stroke,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.72f, h * 0.92f),
            end = Offset(w * 0.72f, h * 0.66f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint.copy(alpha = 0.6f),
            start = Offset(w * 0.86f, h * 0.92f),
            end = Offset(w * 0.86f, h * 0.78f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun EmptyStateMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(
            width = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )

        drawRoundRect(
            color = tint.copy(alpha = 0.6f),
            topLeft = Offset(w * 0.06f, h * 0.16f),
            size = Size(w * 0.88f, h * 0.68f),
            cornerRadius = CornerRadius(w * 0.14f, w * 0.14f),
            style = stroke,
        )
        drawLine(
            color = tint.copy(alpha = 0.6f),
            start = Offset(w * 0.06f, h * 0.38f),
            end = Offset(w * 0.94f, h * 0.38f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.5f, h * 0.52f),
            end = Offset(w * 0.5f, h * 0.76f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.38f, h * 0.64f),
            end = Offset(w * 0.62f, h * 0.64f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun ChartsMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        drawLine(
            color = tint.copy(alpha = 0.5f),
            start = Offset(w * 0.16f, h * 0.12f),
            end = Offset(w * 0.16f, h * 0.86f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint.copy(alpha = 0.5f),
            start = Offset(w * 0.16f, h * 0.86f),
            end = Offset(w * 0.9f, h * 0.86f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.4f),
            topLeft = Offset(w * 0.28f, h * 0.6f),
            size = Size(w * 0.14f, h * 0.26f),
            cornerRadius = CornerRadius(w * 0.05f, w * 0.05f),
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.7f),
            topLeft = Offset(w * 0.5f, h * 0.44f),
            size = Size(w * 0.14f, h * 0.42f),
            cornerRadius = CornerRadius(w * 0.05f, w * 0.05f),
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.72f, h * 0.28f),
            size = Size(w * 0.14f, h * 0.58f),
            cornerRadius = CornerRadius(w * 0.05f, w * 0.05f),
        )
    }
}

@Composable
fun InfiniteScrollMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        drawRoundRect(
            color = tint.copy(alpha = 0.75f),
            topLeft = Offset(w * 0.12f, h * 0.24f),
            size = Size(w * 0.72f, h * 0.09f),
            cornerRadius = CornerRadius(h * 0.05f, h * 0.05f),
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.5f),
            topLeft = Offset(w * 0.12f, h * 0.47f),
            size = Size(w * 0.72f, h * 0.09f),
            cornerRadius = CornerRadius(h * 0.05f, h * 0.05f),
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.3f),
            topLeft = Offset(w * 0.12f, h * 0.7f),
            size = Size(w * 0.44f, h * 0.09f),
            cornerRadius = CornerRadius(h * 0.05f, h * 0.05f),
        )
        val chevron = Path().apply {
            moveTo(w * 0.66f, h * 0.66f)
            lineTo(w * 0.78f, h * 0.8f)
            lineTo(w * 0.9f, h * 0.66f)
        }
        drawPath(
            path = chevron,
            color = tint,
            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

@Composable
fun MultiSelectMenuIcon(
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
            center = Offset(w * 0.24f, h * 0.32f),
            style = stroke,
        )
        val check = Path().apply {
            moveTo(w * 0.17f, h * 0.32f)
            lineTo(w * 0.22f, h * 0.38f)
            lineTo(w * 0.32f, h * 0.25f)
        }
        drawPath(check, tint, style = stroke)
        drawLine(
            color = tint.copy(alpha = 0.75f),
            start = Offset(w * 0.46f, h * 0.32f),
            end = Offset(w * 0.9f, h * 0.32f),
            strokeWidth = 1.7.dp.toPx(),
            cap = StrokeCap.Round,
        )

        drawCircle(
            color = tint.copy(alpha = 0.5f),
            radius = w * 0.13f,
            center = Offset(w * 0.24f, h * 0.68f),
            style = stroke,
        )
        drawLine(
            color = tint.copy(alpha = 0.5f),
            start = Offset(w * 0.46f, h * 0.68f),
            end = Offset(w * 0.9f, h * 0.68f),
            strokeWidth = 1.7.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun SearchMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        drawCircle(
            color = tint,
            radius = w * 0.28f,
            center = Offset(w * 0.42f, h * 0.42f),
            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round),
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.62f, h * 0.62f),
            end = Offset(w * 0.86f, h * 0.86f),
            strokeWidth = 1.9.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun ThemeMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val radius = w * 0.38f
        val center = Offset(w * 0.5f, h * 0.5f)

        drawArc(
            color = tint,
            startAngle = 90f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2f, radius * 2f),
        )
        drawCircle(
            color = tint,
            radius = radius,
            center = center,
            style = Stroke(width = 1.8.dp.toPx()),
        )
    }
}
