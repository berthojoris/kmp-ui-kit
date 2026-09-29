package com.example.uiapp.ui.stats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LuxuryColors
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.roundToInt

private data class StatMetric(
    val label: String,
    val fraction: Float,
    val tint: Color,
)

private val Metrics = listOf(
    StatMetric("Okupansi", 0.78f, LuxuryColors.TealPrimary),
    StatMetric("Kepuasan", 0.92f, LuxuryColors.Accent),
    StatMetric("Penyelesaian", 0.64f, LuxuryColors.AccentInfo),
)

private data class ExpandableItem(
    val id: Int,
    val title: String,
    val summary: String,
    val detail: String,
)

private val Expandables = listOf(
    ExpandableItem(
        1,
        "Ringkasan pendapatan",
        "Pendapatan naik 18% dibanding bulan lalu.",
        "Kenaikan didorong oleh 32 reservasi baru pada segmen vila premium, " +
            "dengan rata-rata durasi menginap 4,6 malam. Tingkat pembatalan turun " +
            "ke 3,1% setelah kebijakan konfirmasi baru diterapkan.",
    ),
    ExpandableItem(
        2,
        "Performa kanal",
        "Direct booking menyumbang 46% total pesanan.",
        "Kanal langsung tumbuh paling cepat berkat program loyalitas. " +
            "Sementara itu kanal marketplace stabil di 38%, dan sisanya berasal " +
            "dari referensi mitra korporat.",
    ),
    ExpandableItem(
        3,
        "Catatan operasional",
        "2 unit memerlukan pemeliharaan minggu ini.",
        "Unit 12 dan 19 dijadwalkan untuk perawatan kolam dan pengecatan ulang " +
            "pada Selasa. Estimasi downtime masing-masing satu hari tanpa " +
            "memengaruhi reservasi yang sudah ada.",
    ),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StatsLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    Scaffold(
        containerColor = LuxuryColors.Background,
        topBar = {
            UiTopBar(
                title = "Stats Lab",
                subtitle = "Progress ring, count-up, card melipat",
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

            Text(
                text = "PROGRESS RING & COUNT-UP",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.6.sp,
                color = LuxuryColors.TextMuted,
            )
            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = LuxuryColors.SurfaceWhite,
                border = BorderStroke(1.dp, LuxuryColors.SurfaceBorder),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 22.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Metrics.forEach { metric ->
                        StatRing(metric)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "EXPANDABLE CARD",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.6.sp,
                color = LuxuryColors.TextMuted,
            )
            Spacer(modifier = Modifier.height(14.dp))

            Expandables.forEach { item ->
                ExpandableCard(item = item)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun StatRing(metric: StatMetric) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(metric) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = metric.fraction,
            animationSpec = tween(durationMillis = 1300, easing = FastOutSlowInEasing),
        )
    }

    val displayed = (progress.value * 100).roundToInt()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(92.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 8.dp.toPx()
                val inset = strokeWidth / 2
                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                drawArc(
                    color = LuxuryColors.SurfaceMuted,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
                drawArc(
                    color = metric.tint,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.value,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$displayed%",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = LuxuryColors.TextPrimary,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = metric.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = LuxuryColors.TextSecondary,
        )
    }
}

@Composable
private fun ExpandableCard(item: ExpandableItem) {
    var expanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "chevron",
    )

    Surface(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = LuxuryColors.SurfaceWhite,
        border = BorderStroke(1.dp, LuxuryColors.SurfaceBorder),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = LuxuryColors.TextPrimary,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.summary,
                        fontSize = 12.sp,
                        color = LuxuryColors.TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.size(12.dp))
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(LuxuryColors.SurfaceMuted)
                        .graphicsLayer { rotationZ = chevronRotation },
                    contentAlignment = Alignment.Center,
                ) {
                    ChevronGlyph(tint = LuxuryColors.TextPrimary)
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(tween(260)) + fadeIn(tween(200)),
                exit = shrinkVertically(tween(220)) + fadeOut(tween(150)),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(LuxuryColors.Divider),
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = item.detail,
                        fontSize = 13.sp,
                        lineHeight = 21.sp,
                        color = LuxuryColors.TextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChevronGlyph(tint: Color) {
    Canvas(modifier = Modifier.size(14.dp)) {
        val path = Path().apply {
            moveTo(size.width * 0.2f, size.height * 0.36f)
            lineTo(size.width * 0.5f, size.height * 0.66f)
            lineTo(size.width * 0.8f, size.height * 0.36f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(
                width = 1.8.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
}
