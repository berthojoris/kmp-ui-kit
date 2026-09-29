package com.example.uiapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.uiapp.theme.LuxuryColors

@Composable
fun MgIosBackChevron(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    strokeWidth: Dp = 2.4.dp,
    tint: Color = LuxuryColors.TextPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val path = Path().apply {
            moveTo(this@Canvas.size.width * 0.64f, this@Canvas.size.height * 0.18f)
            lineTo(this@Canvas.size.width * 0.32f, this@Canvas.size.height * 0.50f)
            lineTo(this@Canvas.size.width * 0.64f, this@Canvas.size.height * 0.82f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
}

@Composable
fun MgIosBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    backgroundColor: Color = LuxuryColors.SurfaceMuted,
    borderColor: Color = LuxuryColors.SurfaceBorder,
    iconTint: Color = LuxuryColors.TextPrimary,
    contentDescription: String = "Kembali",
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .semantics {
                this.role = Role.Button
                this.contentDescription = contentDescription
            },
        contentAlignment = Alignment.Center,
    ) {
        MgIosBackChevron(size = 16.dp, strokeWidth = 2.4.dp, tint = iconTint)
    }
}

@Composable
fun AutoBlurScrollMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
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
fun PlaceholderMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val corner = CornerRadius(this.size.width * 0.22f, this.size.width * 0.22f)
        drawRoundRect(
            color = tint.copy(alpha = 0.55f),
            topLeft = Offset(this.size.width * 0.05f, this.size.height * 0.05f),
            size = Size(this.size.width * 0.9f, this.size.height * 0.9f),
            cornerRadius = corner,
            style = stroke,
        )
        drawLine(
            color = tint.copy(alpha = 0.55f),
            start = Offset(this.size.width * 0.28f, this.size.height * 0.5f),
            end = Offset(this.size.width * 0.72f, this.size.height * 0.5f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun StickyHeaderMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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

@Composable
fun SplashMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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

@Composable
fun NavStructureMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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

@Composable
fun TickerCounterMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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

@Composable
fun HoldToConfirmMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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

@Composable
fun AiChatMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Bubble path
        val bubble = Path().apply {
            moveTo(w * 0.12f, h * 0.2f)
            lineTo(w * 0.88f, h * 0.2f)
            lineTo(w * 0.88f, h * 0.68f)
            lineTo(w * 0.45f, h * 0.68f)
            lineTo(w * 0.22f, h * 0.88f)
            lineTo(w * 0.22f, h * 0.68f)
            lineTo(w * 0.12f, h * 0.68f)
            close()
        }
        drawPath(bubble, tint, style = stroke)
        // AI Sparkle star top-right
        drawLine(tint, Offset(w * 0.68f, h * 0.34f), Offset(w * 0.68f, h * 0.48f), strokeWidth = 1.2.dp.toPx(), cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.61f, h * 0.41f), Offset(w * 0.75f, h * 0.41f), strokeWidth = 1.2.dp.toPx(), cap = StrokeCap.Round)
        // Text line inside
        drawLine(tint.copy(alpha = 0.7f), Offset(w * 0.25f, h * 0.45f), Offset(w * 0.52f, h * 0.45f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun SelectionToolbarMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        // Background list items
        drawRoundRect(
            color = tint.copy(alpha = 0.3f),
            topLeft = Offset(w * 0.08f, h * 0.12f),
            size = Size(w * 0.84f, h * 0.26f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
        )
        // Floating action toolbar at bottom
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.08f, h * 0.52f),
            size = Size(w * 0.84f, h * 0.38f),
            cornerRadius = CornerRadius(h * 0.1f, h * 0.1f),
            style = stroke,
        )
        // Checkmark inside toolbar
        val check = Path().apply {
            moveTo(w * 0.28f, h * 0.7f)
            lineTo(w * 0.38f, h * 0.78f)
            lineTo(w * 0.54f, h * 0.62f)
        }
        drawPath(check, tint, style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        // Trash dot
        drawCircle(tint, radius = 1.8.dp.toPx(), center = Offset(w * 0.74f, h * 0.71f))
    }
}

@Composable
fun UndoQueueMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        // Curved undo arrow
        drawArc(
            color = tint,
            startAngle = 45f,
            sweepAngle = 260f,
            useCenter = false,
            topLeft = Offset(w * 0.18f, h * 0.18f),
            size = Size(w * 0.64f, h * 0.64f),
            style = stroke,
        )
        // Arrow head
        val arrow = Path().apply {
            moveTo(w * 0.22f, h * 0.42f)
            lineTo(w * 0.14f, h * 0.58f)
            lineTo(w * 0.34f, h * 0.58f)
        }
        drawPath(arrow, tint)
    }
}

@Composable
fun AdaptiveNavMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        // Foldable book frame
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.08f, h * 0.14f),
            size = Size(w * 0.84f, h * 0.72f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
            style = stroke,
        )
        // Center spine / hinge line
        drawLine(
            color = tint.copy(alpha = 0.5f),
            start = Offset(w * 0.5f, h * 0.14f),
            end = Offset(w * 0.5f, h * 0.86f),
            strokeWidth = 1.2.dp.toPx(),
        )
        // Navigation rail indicator on left pane
        drawLine(
            color = tint,
            start = Offset(w * 0.22f, h * 0.3f),
            end = Offset(w * 0.22f, h * 0.7f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun ExpressiveControlsMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        // Wavy line at top
        val wave = Path().apply {
            moveTo(w * 0.1f, h * 0.28f)
            quadraticTo(w * 0.3f, h * 0.16f, w * 0.5f, h * 0.28f)
            quadraticTo(w * 0.7f, h * 0.40f, w * 0.9f, h * 0.28f)
        }
        drawPath(wave, tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))

        // Asymmetrical pill at bottom
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.15f, h * 0.55f),
            size = Size(w * 0.7f, h * 0.32f),
            cornerRadius = CornerRadius(w * 0.1f, w * 0.04f),
        )
    }
}

@Composable
fun AccessibilityLabMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        // Outer universal access circle
        drawCircle(
            color = tint,
            radius = w * 0.42f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = stroke,
        )
        // Person head
        drawCircle(tint, radius = w * 0.08f, center = Offset(w * 0.5f, h * 0.32f))
        // Person arms outstretched
        drawLine(tint, Offset(w * 0.25f, h * 0.46f), Offset(w * 0.75f, h * 0.46f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
        // Person torso & legs
        drawLine(tint, Offset(w * 0.5f, h * 0.46f), Offset(w * 0.5f, h * 0.65f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.5f, h * 0.65f), Offset(w * 0.34f, h * 0.82f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.5f, h * 0.65f), Offset(w * 0.66f, h * 0.82f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun ActivityInboxMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Bell contour
        val bell = Path().apply {
            moveTo(w * 0.5f, h * 0.16f)
            lineTo(w * 0.72f, h * 0.65f)
            lineTo(w * 0.28f, h * 0.65f)
            close()
        }
        drawPath(bell, tint, style = stroke)
        // Bell clapper
        drawArc(
            color = tint,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.41f, h * 0.68f),
            size = Size(w * 0.18f, h * 0.14f),
            style = stroke,
        )
        // Unread badge dot on top right
        drawCircle(tint, radius = 2.2.dp.toPx(), center = Offset(w * 0.78f, h * 0.22f))
    }
}

@Composable
fun ResumeFormMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        // Form sheet rect
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.15f, h * 0.1f),
            size = Size(w * 0.7f, h * 0.8f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
            style = stroke,
        )
        // Form input lines
        drawLine(tint, Offset(w * 0.28f, h * 0.32f), Offset(w * 0.72f, h * 0.32f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.28f, h * 0.48f), Offset(w * 0.65f, h * 0.48f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
        // Autosave checkmark badge at bottom right
        val check = Path().apply {
            moveTo(w * 0.38f, h * 0.68f)
            lineTo(w * 0.48f, h * 0.76f)
            lineTo(w * 0.68f, h * 0.62f)
        }
        drawPath(check, tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun NativeSurfacesMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
        // Background card
        drawRoundRect(
            color = tint.copy(alpha = 0.3f),
            topLeft = Offset(w * 0.1f, h * 0.12f),
            size = Size(w * 0.65f, h * 0.55f),
            cornerRadius = CornerRadius(w * 0.1f, w * 0.1f),
        )
        // Floating foreground surface (translucent offset)
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.25f, h * 0.32f),
            size = Size(w * 0.65f, h * 0.55f),
            cornerRadius = CornerRadius(w * 0.1f, w * 0.1f),
            style = stroke,
        )
        // Floating pill inside
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.38f, h * 0.58f),
            size = Size(w * 0.4f, h * 0.16f),
            cornerRadius = CornerRadius(h * 0.08f, h * 0.08f),
        )
    }
}

@Composable
fun FocusTimerMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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

@Composable
fun GamificationMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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
    tint: Color = LuxuryColors.TextPrimary,
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




