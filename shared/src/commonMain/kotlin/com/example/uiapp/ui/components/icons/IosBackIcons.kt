package com.example.uiapp.ui.components.icons

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
import com.example.uiapp.theme.LightAppPalette
import com.example.uiapp.theme.LocalAppPalette

@Composable
fun MgIosBackChevron(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    strokeWidth: Dp = 2.4.dp,
    tint: Color = LightAppPalette.textPrimary,
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
    backgroundColor: Color = LightAppPalette.surfaceMuted,
    borderColor: Color = LightAppPalette.border,
    iconTint: Color = LightAppPalette.textPrimary,
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
fun PlaceholderMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
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
