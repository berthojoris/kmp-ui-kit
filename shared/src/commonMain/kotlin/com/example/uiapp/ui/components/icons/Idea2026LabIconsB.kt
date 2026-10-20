package com.example.uiapp.ui.components.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.uiapp.theme.LightAppPalette

@Composable
fun RtlPreviewMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun A11yAuditMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun GettingStartedMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun WhatsNewMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun ReferralInviteMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun NpsSurveyMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun ReportFlowMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun SecureVaultMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun MedicationPlanMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun NutritionDiaryMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun SleepStagesMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun QrisPayMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun VirtualCardMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun CurrencyExchangeMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun LoanSimulatorMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun DragDropMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun CollabPresenceMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun LiveDashboardMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun OfflineResilienceMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun AiAgentStepsMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.28f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.15f,h*0.85f), end = Offset(w*0.85f,h*0.15f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}
