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
fun AiInlineEditMenuIcon(
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
fun DesignTokensMenuIcon(
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
fun PerfhudMenuIcon(
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
fun AppointmentSlotsMenuIcon(
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
fun FlowVizMenuIcon(
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
fun GraphExplorerMenuIcon(
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
fun StoreLocatorMenuIcon(
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
fun PaymentFormMenuIcon(
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
fun AddressInputMenuIcon(
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
fun RecurrencePickerMenuIcon(
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
fun LiveAuctionMenuIcon(
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
fun FlashSaleMenuIcon(
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
fun SubscriptionMgmtMenuIcon(
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
fun QueueTicketMenuIcon(
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
fun PostComposerMenuIcon(
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
fun StoryCreatorMenuIcon(
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
fun DocScannerMenuIcon(
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
fun PdfViewerMenuIcon(
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
fun CodeEditorMenuIcon(
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
fun VersionHistoryMenuIcon(
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
