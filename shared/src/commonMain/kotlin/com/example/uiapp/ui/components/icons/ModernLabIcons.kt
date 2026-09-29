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
fun AiChatMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
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
    tint: Color = LightAppPalette.textPrimary,
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
    tint: Color = LightAppPalette.textPrimary,
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
    tint: Color = LightAppPalette.textPrimary,
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
    tint: Color = LightAppPalette.textPrimary,
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
    tint: Color = LightAppPalette.textPrimary,
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
    tint: Color = LightAppPalette.textPrimary,
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
    tint: Color = LightAppPalette.textPrimary,
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
    tint: Color = LightAppPalette.textPrimary,
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
