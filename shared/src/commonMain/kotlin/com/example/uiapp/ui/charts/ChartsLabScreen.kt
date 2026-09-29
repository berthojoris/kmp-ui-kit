package com.example.uiapp.ui.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LuxuryColors
import com.example.uiapp.ui.components.UiTopBar

private val Revenue = listOf(42f, 58f, 51f, 74f, 66f, 88f, 79f)
private val RevenueLabels = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

private data class BarGroup(val label: String, val a: Float, val b: Float)

private val ChannelBars = listOf(
    BarGroup("Q1", 62f, 44f),
    BarGroup("Q2", 78f, 52f),
    BarGroup("Q3", 55f, 70f),
    BarGroup("Q4", 88f, 61f),
)

private data class DonutSegment(val label: String, val value: Float, val color: Color)

private val DonutSegments = listOf(
    DonutSegment("Direct", 46f, LuxuryColors.TealPrimary),
    DonutSegment("Marketplace", 34f, LuxuryColors.AccentInfo),
    DonutSegment("Mitra", 20f, LuxuryColors.AccentWarning),
)

private val SparklineCards = listOf(
    Triple("Okupansi", listOf(0.4f, 0.62f, 0.48f, 0.7f, 0.58f, 0.84f, 0.78f), LuxuryColors.TealPrimary),
    Triple("Pesanan", listOf(0.3f, 0.45f, 0.6f, 0.52f, 0.7f, 0.66f, 0.9f), LuxuryColors.AccentInfo),
    Triple("Kepuasan", listOf(0.7f, 0.66f, 0.72f, 0.8f, 0.76f, 0.88f, 0.92f), LuxuryColors.Accent),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChartsLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        appear.animateTo(1f, animationSpec = tween(900, easing = FastOutSlowInEasing))
    }
    val progress = appear.value

    Scaffold(
        containerColor = LuxuryColors.Background,
        topBar = {
            UiTopBar(
                title = "Charts Lab",
                subtitle = "Line, bar, donut, dan sparkline",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            ChartCard(
                title = "Pendapatan",
                subtitle = "7 hari terakhir \u00B7 dalam juta",
            ) {
                LineChart(data = Revenue, progress = progress)
                Spacer(modifier = Modifier.height(10.dp))
                AxisLabels(RevenueLabels)
            }

            Spacer(modifier = Modifier.height(16.dp))

            ChartCard(
                title = "Kanal pemesanan",
                subtitle = "Direct vs marketplace per kuartal",
            ) {
                BarChart(groups = ChannelBars, progress = progress)
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    LegendDot(color = LuxuryColors.TealPrimary, label = "Direct")
                    Spacer(modifier = Modifier.width(16.dp))
                    LegendDot(color = LuxuryColors.AccentInfo, label = "Marketplace")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ChartCard(
                title = "Distribusi kanal",
                subtitle = "Kontribusi total pesanan",
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DonutChart(segments = DonutSegments, progress = progress)
                    Spacer(modifier = Modifier.width(20.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        DonutSegments.forEach { segment ->
                            LegendDot(
                                color = segment.color,
                                label = segment.label,
                                value = "${segment.value.toInt()}%",
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sparkline",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = LuxuryColors.TextPrimary,
                modifier = Modifier.padding(bottom = 10.dp),
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SparklineCards.forEach { (label, data, tint) ->
                    SparklineRow(label = label, data = data, tint = tint, progress = progress)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ChartCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = LuxuryColors.SurfaceWhite,
        border = BorderStroke(1.dp, LuxuryColors.SurfaceBorder),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = LuxuryColors.TextPrimary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = LuxuryColors.TextMuted,
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun AxisLabels(labels: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        labels.forEach { label ->
            Text(
                text = label,
                fontSize = 10.sp,
                color = LuxuryColors.TextMuted,
            )
        }
    }
}

@Composable
private fun LegendDot(
    color: Color,
    label: String,
    value: String? = null,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = LuxuryColors.TextSecondary,
            modifier = if (value != null) Modifier.weight(1f, fill = false) else Modifier,
        )
        if (value != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = LuxuryColors.TextPrimary,
            )
        }
    }
}

@Composable
private fun LineChart(data: List<Float>, progress: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
    ) {
        if (data.size < 2) return@Canvas
        val w = size.width
        val h = size.height
        val inset = 6.dp.toPx()
        val usableH = h - inset * 2
        val max = (data.max() * 1.12f).coerceAtLeast(1f)
        val stepX = w / (data.size - 1)

        repeat(4) { index ->
            val y = inset + usableH * (index / 3f)
            drawLine(
                color = LuxuryColors.SurfaceBorder.copy(alpha = 0.7f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1.dp.toPx(),
            )
        }

        val points = data.mapIndexed { index, value ->
            val x = stepX * index
            val ratio = (value / max) * progress
            Offset(x, inset + usableH * (1f - ratio))
        }

        val line = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }
        val area = Path().apply {
            addPath(line)
            lineTo(points.last().x, h)
            lineTo(points.first().x, h)
            close()
        }

        drawPath(
            path = area,
            brush = Brush.verticalGradient(
                colors = listOf(
                    LuxuryColors.TealPrimary.copy(alpha = 0.22f),
                    LuxuryColors.TealPrimary.copy(alpha = 0f),
                ),
            ),
        )
        drawPath(
            path = line,
            color = LuxuryColors.TealPrimary,
            style = Stroke(
                width = 2.6.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
        points.forEach { point ->
            drawCircle(color = LuxuryColors.SurfaceWhite, radius = 3.6.dp.toPx(), center = point)
            drawCircle(
                color = LuxuryColors.TealPrimary,
                radius = 3.6.dp.toPx(),
                center = point,
                style = Stroke(width = 2.dp.toPx()),
            )
        }
    }
}

@Composable
private fun BarChart(groups: List<BarGroup>, progress: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
    ) {
        if (groups.isEmpty()) return@Canvas
        val w = size.width
        val h = size.height
        val max = (groups.maxOf { maxOf(it.a, it.b) } * 1.15f).coerceAtLeast(1f)
        val groupWidth = w / groups.size
        val barWidth = groupWidth * 0.26f
        val gap = groupWidth * 0.1f

        drawLine(
            color = LuxuryColors.SurfaceBorder,
            start = Offset(0f, h),
            end = Offset(w, h),
            strokeWidth = 1.dp.toPx(),
        )

        groups.forEachIndexed { index, group ->
            val center = groupWidth * index + groupWidth / 2f
            val leftX = center - barWidth - gap / 2f
            val rightX = center + gap / 2f

            val aHeight = (group.a / max) * h * progress
            val bHeight = (group.b / max) * h * progress

            drawRoundRect(
                color = LuxuryColors.TealPrimary,
                topLeft = Offset(leftX, h - aHeight),
                size = Size(barWidth, aHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2f, barWidth / 2f),
            )
            drawRoundRect(
                color = LuxuryColors.AccentInfo,
                topLeft = Offset(rightX, h - bHeight),
                size = Size(barWidth, bHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2f, barWidth / 2f),
            )
        }
    }
}

@Composable
private fun DonutChart(segments: List<DonutSegment>, progress: Float) {
    val total = segments.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)
    Box(
        modifier = Modifier.size(128.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 16.dp.toPx()
            val inset = strokeWidth / 2f
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(inset, inset)

            drawArc(
                color = LuxuryColors.SurfaceMuted,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
            )

            var startAngle = -90f
            segments.forEach { segment ->
                val sweep = (segment.value / total) * 360f * progress
                drawArc(
                    color = segment.color,
                    startAngle = startAngle,
                    sweepAngle = sweep - 2f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                )
                startAngle += sweep
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = LuxuryColors.TextPrimary,
            )
            Text(
                text = "terisi",
                fontSize = 11.sp,
                color = LuxuryColors.TextMuted,
            )
        }
    }
}

@Composable
private fun SparklineRow(
    label: String,
    data: List<Float>,
    tint: Color,
    progress: Float,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = LuxuryColors.SurfaceWhite,
        border = BorderStroke(1.dp, LuxuryColors.SurfaceBorder),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.width(86.dp)) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LuxuryColors.TextPrimary,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${(data.last() * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = LuxuryColors.TextMuted,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
            ) {
                val w = size.width
                val h = size.height
                val stepX = w / (data.size - 1)
                val points = data.mapIndexed { index, value ->
                    Offset(stepX * index, h - value * h * progress)
                }
                val line = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    points.drop(1).forEach { lineTo(it.x, it.y) }
                }
                val area = Path().apply {
                    addPath(line)
                    lineTo(points.last().x, h)
                    lineTo(points.first().x, h)
                    close()
                }
                drawPath(
                    path = area,
                    brush = Brush.verticalGradient(
                        colors = listOf(tint.copy(alpha = 0.2f), tint.copy(alpha = 0f)),
                    ),
                )
                drawPath(
                    path = line,
                    color = tint,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }
        }
    }
}
