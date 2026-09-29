package com.example.uiapp.ui.dataviz

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

// ---------- Data mock lokal ----------

private data class RadarAxis(val label: String, val seriesA: Float, val seriesB: Float)

private val RadarAxes = listOf(
    RadarAxis("Kecepatan", 0.86f, 0.60f),
    RadarAxis("Akurasi", 0.72f, 0.82f),
    RadarAxis("Konsistensi", 0.64f, 0.52f),
    RadarAxis("Retensi", 0.90f, 0.70f),
    RadarAxis("Kolaborasi", 0.58f, 0.88f),
)

private data class Candle(val id: Int, val open: Float, val close: Float, val high: Float, val low: Float)

private val Candles = listOf(
    Candle(0, 1240f, 1285f, 1305f, 1225f),
    Candle(1, 1285f, 1262f, 1298f, 1250f),
    Candle(2, 1262f, 1310f, 1322f, 1258f),
    Candle(3, 1310f, 1338f, 1350f, 1300f),
    Candle(4, 1338f, 1322f, 1345f, 1310f),
    Candle(5, 1322f, 1368f, 1380f, 1318f),
    Candle(6, 1368f, 1412f, 1425f, 1360f),
    Candle(7, 1412f, 1398f, 1420f, 1385f),
    Candle(8, 1398f, 1440f, 1452f, 1390f),
    Candle(9, 1440f, 1472f, 1486f, 1432f),
    Candle(10, 1472f, 1458f, 1478f, 1444f),
    Candle(11, 1458f, 1505f, 1518f, 1450f),
)

private data class WaterfallStep(val id: String, val label: String, val delta: Float)

private val WaterfallSteps = listOf(
    WaterfallStep("w1", "Pendapatan", 420f),
    WaterfallStep("w2", "Biaya Layanan", -120f),
    WaterfallStep("w3", "Marketing", -90f),
    WaterfallStep("w4", "Operasional", -70f),
    WaterfallStep("w5", "Pajak", -45f),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DataVizLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Data Viz Lanjutan",
                subtitle = "Radar, gauge, candlestick, dan waterfall",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LabIntroCard(
                title = "Visualisasi Data Canvas",
                body = "Keempat grafik digambar murni dengan Canvas tanpa library chart. Angka bersumber dari data mock; animasi reveal dijalankan sekali lalu dapat diputar ulang.",
            )

            LabSectionTitle("1. Radar / Spider Chart", "Bandingkan dua seri pada lima dimensi")
            RadarChartCard()

            LabSectionTitle("2. Gauge / Speedometer", "Nilai tunggal dengan zona ambang")
            GaugeCard()

            LabSectionTitle("3. Candlestick", "Buka, tutup, tertinggi, terendah per periode")
            CandlestickCard()

            LabSectionTitle("4. Waterfall", "Kontribusi tiap langkah menuju laba bersih")
            WaterfallCard()
        }
    }
}

// ---------- 1. Radar ----------

@Composable
private fun RadarChartCard() {
    val palette = LocalAppPalette.current
    var showA by remember { mutableStateOf(true) }
    var showB by remember { mutableStateOf(true) }
    var replayKey by remember { mutableIntStateOf(0) }

    val anim = remember { Animatable(1f) }
    LaunchedEffect(replayKey) {
        anim.snapTo(0f)
        anim.animateTo(1f, tween(durationMillis = 900))
    }
    val progress = anim.value

    LabCard {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SeriesToggle(
                label = "Tim A",
                color = palette.primary,
                selected = showA,
                onToggle = { showA = !showA },
            )
            SeriesToggle(
                label = "Tim B",
                color = palette.info,
                selected = showB,
                onToggle = { showB = !showB },
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .semantics {
                    contentDescription = "Radar chart lima dimensi, seri A dan seri B"
                },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(230.dp)) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val radius = size.minDimension / 2f - 6.dp.toPx()
                val n = RadarAxes.size

                fun pointFor(index: Int, scale: Float): Offset {
                    val angle = (-90f + index * 360f / n) * (PI / 180f)
                    return Offset(
                        cx + radius * scale * cos(angle).toFloat(),
                        cy + radius * scale * sin(angle).toFloat(),
                    )
                }

                // Cincin grid
                for (ring in 1..4) {
                    val scale = ring / 4f
                    val path = Path()
                    for (i in 0 until n) {
                        val p = pointFor(i, scale)
                        if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
                    }
                    path.close()
                    drawPath(
                        path = path,
                        color = palette.border,
                        style = Stroke(width = 1.dp.toPx()),
                    )
                }

                // Sumbu
                for (i in 0 until n) {
                    val p = pointFor(i, 1f)
                    drawLine(
                        color = palette.border,
                        start = Offset(cx, cy),
                        end = p,
                        strokeWidth = 1.dp.toPx(),
                    )
                }

                fun drawSeries(values: (Int) -> Float, color: androidx.compose.ui.graphics.Color) {
                    val path = Path()
                    for (i in 0 until n) {
                        val p = pointFor(i, values(i) * progress)
                        if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
                    }
                    path.close()
                    drawPath(path, color.copy(alpha = 0.22f))
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(width = 2.dp.toPx(), join = StrokeJoin.Round),
                    )
                    for (i in 0 until n) {
                        val p = pointFor(i, values(i) * progress)
                        drawCircle(color = color, radius = 3.dp.toPx(), center = p)
                    }
                }

                if (showA) drawSeries({ RadarAxes[it].seriesA }, palette.primary)
                if (showB) drawSeries({ RadarAxes[it].seriesB }, palette.info)
            }
        }

        if (!showA && !showB) {
            Text(
                text = "Tidak ada seri yang dipilih. Aktifkan minimal satu seri.",
                fontSize = 11.sp,
                color = palette.textMuted,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                RadarAxes.forEach { axis ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = axis.label,
                            modifier = Modifier.weight(1f),
                            fontSize = 12.sp,
                            color = palette.textSecondary,
                        )
                        if (showA) {
                            Text(
                                text = "${(axis.seriesA * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.primary,
                            )
                        }
                        if (showA && showB) Spacer(modifier = Modifier.width(14.dp))
                        if (showB) {
                            Text(
                                text = "${(axis.seriesB * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.info,
                            )
                        }
                    }
                }
            }
        }

        Surface(
            onClick = { replayKey += 1 },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Box(modifier = Modifier.padding(vertical = 11.dp), contentAlignment = Alignment.Center) {
                Text("Putar Ulang Animasi", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
            }
        }
    }
}

@Composable
private fun SeriesToggle(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    selected: Boolean,
    onToggle: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) color.copy(alpha = 0.16f) else palette.surface, RoundedCornerShape(20.dp))
            .border(1.dp, if (selected) color else palette.border, RoundedCornerShape(20.dp))
            .pointerInput(Unit) { detectTapGestures { onToggle() } }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(if (selected) color else palette.border),
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) palette.textPrimary else palette.textMuted,
        )
    }
}

// ---------- 2. Gauge ----------

@Composable
private fun GaugeCard() {
    val palette = LocalAppPalette.current
    var value by remember { mutableIntStateOf(68) }
    val animated by animateFloatAsState(
        targetValue = value / 100f,
        animationSpec = tween(500),
        label = "gauge_value",
    )

    val zoneLabel = when {
        value < 40 -> "Rendah"
        value < 70 -> "Sedang"
        else -> "Tinggi"
    }
    val zoneColor = when {
        value < 40 -> palette.danger
        value < 70 -> palette.warning
        else -> palette.success
    }

    LabCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .semantics { contentDescription = "Gauge kualitas layanan bernilai $value dari 100, zona $zoneLabel" },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(width = 230.dp, height = 150.dp)) {
                val stroke = 16.dp.toPx()
                val inset = stroke / 2f + 4.dp.toPx()
                val isDarkPalette = palette.background.luminance() < 0.5f
                val trackColor = if (isDarkPalette) palette.surfaceMuted else palette.border
                val arcSize = Size(size.width - inset * 2, size.height * 2 - inset * 2)
                val topLeft = Offset(inset, inset + 10.dp.toPx())

                drawArc(
                    color = trackColor,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                drawArc(
                    color = zoneColor,
                    startAngle = 135f,
                    sweepAngle = 270f * animated,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )

                // Jarum
                val cx = size.width / 2f
                val cy = topLeft.y + arcSize.height / 2f
                val radius = arcSize.width / 2f - 6.dp.toPx()
                val angleRad = (135f + 270f * animated) * (PI / 180f)
                val needle = Offset(cx + radius * cos(angleRad).toFloat(), cy + radius * sin(angleRad).toFloat())
                drawLine(
                    color = palette.textPrimary,
                    start = Offset(cx, cy),
                    end = needle,
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                drawCircle(color = palette.textPrimary, radius = 6.dp.toPx(), center = Offset(cx, cy))
                drawCircle(color = palette.surface, radius = 3.dp.toPx(), center = Offset(cx, cy))
            }

            Column(
                modifier = Modifier.padding(top = 46.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "$value",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )
                Text(
                    text = "Zona: $zoneLabel",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = zoneColor,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Skor Kualitas", modifier = Modifier.weight(1f), fontSize = 12.sp, color = palette.textSecondary)
            StepButton(label = "-") { value = (value - 5).coerceIn(0, 100) }
            Text("$value", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
            StepButton(label = "+") { value = (value + 5).coerceIn(0, 100) }
        }

        Text(
            text = "Ambang: 0-39 rendah, 40-69 sedang, 70-100 tinggi.",
            fontSize = 11.sp,
            color = palette.textMuted,
        )
    }
}

@Composable
private fun StepButton(label: String, onClick: () -> Unit) {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(10.dp))
            .pointerInput(Unit) { detectTapGestures { onClick() } },
        contentAlignment = Alignment.Center,
    ) {
        Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
    }
}

private fun androidx.compose.ui.graphics.Color.luminance(): Float {
    return 0.299f * red + 0.587f * green + 0.114f * blue
}

// ---------- 3. Candlestick ----------

@Composable
private fun CandlestickCard() {
    val palette = LocalAppPalette.current
    var selected by remember { mutableIntStateOf(Candles.size - 1) }
    var revealKey by remember { mutableIntStateOf(0) }

    val anim = remember { Animatable(1f) }
    LaunchedEffect(revealKey) {
        anim.snapTo(0f)
        anim.animateTo(1f, tween(durationMillis = 700))
    }
    val progress = anim.value

    val minPrice = Candles.minOf { it.low }
    val maxPrice = Candles.maxOf { it.high }
    val selectedCandle = Candles[selected]
    val isUp = selectedCandle.close >= selectedCandle.open

    LabCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .semantics { contentDescription = "Grafik candlestick ${Candles.size} periode, ketuk untuk memilih" }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val index = (offset.x / size.width * Candles.size).toInt().coerceIn(0, Candles.size - 1)
                        selected = index
                    }
                },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val topPad = 8.dp.toPx()
                val bottomPad = 8.dp.toPx()
                val usable = size.height - topPad - bottomPad
                fun yFor(price: Float): Float = topPad + (1f - (price - minPrice) / (maxPrice - minPrice)) * usable

                // Garis bantu
                for (i in 0..3) {
                    val y = topPad + usable * i / 3f
                    drawLine(
                        color = palette.border,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx(),
                    )
                }

                val slot = size.width / Candles.size
                val bodyWidth = slot * 0.5f
                Candles.forEach { candle ->
                    val up = candle.close >= candle.open
                    val color = if (up) palette.success else palette.danger
                    val centerX = candle.id * slot + slot / 2f
                    // Sumbu (wick)
                    drawLine(
                        color = color,
                        start = Offset(centerX, yFor(candle.high)),
                        end = Offset(centerX, yFor(candle.low)),
                        strokeWidth = 1.4.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                    val openY = yFor(candle.open)
                    val closeY = yFor(candle.close)
                    val topY = min(openY, closeY)
                    val heightPx = max(2.dp.toPx(), kotlin.math.abs(closeY - openY)) * progress
                    drawRect(
                        color = color,
                        topLeft = Offset(centerX - bodyWidth / 2f, topY),
                        size = Size(bodyWidth, heightPx),
                    )
                    if (candle.id == selected) {
                        drawRect(
                            color = palette.primary,
                            topLeft = Offset(centerX - bodyWidth / 2f - 3.dp.toPx(), topPad - 4.dp.toPx()),
                            size = Size(bodyWidth + 6.dp.toPx(), size.height - topPad),
                            style = Stroke(width = 1.5.dp.toPx()),
                        )
                    }
                }
            }
        }

        // Detail candle terpilih
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                .padding(12.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Periode ${selected + 1}",
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = if (isUp) "NAIK" else "TURUN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUp) palette.success else palette.danger,
                    )
                }
                DetailRow("Buka", selectedCandle.open)
                DetailRow("Tutup", selectedCandle.close)
                DetailRow("Tertinggi", selectedCandle.high)
                DetailRow("Terendah", selectedCandle.low)
            }
        }

        Surface(
            onClick = { revealKey += 1 },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Box(modifier = Modifier.padding(vertical = 11.dp), contentAlignment = Alignment.Center) {
                Text("Putar Ulang Animasi", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: Float) {
    val palette = LocalAppPalette.current
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f), fontSize = 12.sp, color = palette.textSecondary)
        Text("${value.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
    }
}

// ---------- 4. Waterfall ----------

@Composable
private fun WaterfallCard() {
    val palette = LocalAppPalette.current
    var revealKey by remember { mutableIntStateOf(0) }

    val anim = remember { Animatable(1f) }
    LaunchedEffect(revealKey) {
        anim.snapTo(0f)
        anim.animateTo(1f, tween(durationMillis = 800))
    }
    val progress = anim.value

    // Hitung kumulatif
    val bars = remember {
        val result = mutableListOf<Triple<String, Float, Float>>() // label, start, end
        var running = 0f
        WaterfallSteps.forEach { step ->
            val start = running
            running += step.delta
            result.add(Triple(step.label, start, running))
        }
        result.add(Triple("Laba Bersih", 0f, running))
        result
    }

    val minValue = min(0f, bars.minOf { min(it.second, it.third) })
    val maxValue = max(bars.maxOf { max(it.second, it.third) }, 1f)
    val totalProfit = bars.last().third

    LabCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .semantics { contentDescription = "Waterfall chart laba bersih ${totalProfit.toInt()} juta rupiah" },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val topPad = 8.dp.toPx()
                val bottomPad = 8.dp.toPx()
                val usable = size.height - topPad - bottomPad
                fun yFor(v: Float): Float = topPad + (1f - (v - minValue) / (maxValue - minValue)) * usable

                // Garis nol
                val zeroY = yFor(0f)
                drawLine(
                    color = palette.border,
                    start = Offset(0f, zeroY),
                    end = Offset(size.width, zeroY),
                    strokeWidth = 1.dp.toPx(),
                )

                val slot = size.width / bars.size
                val barWidth = slot * 0.5f
                bars.forEachIndexed { index, (label, start, end) ->
                    val isTotal = index == bars.lastIndex
                    val isUp = end >= start
                    val color = when {
                        isTotal -> palette.primary
                        isUp -> palette.success
                        else -> palette.danger
                    }
                    val centerX = index * slot + slot / 2f
                    val yStart = yFor(start)
                    val yEnd = yFor(end)
                    val topY = min(yStart, yEnd)
                    val fullHeight = max(2.dp.toPx(), kotlin.math.abs(yEnd - yStart))
                    // Animasi tumbuh dari garis nol
                    val h = fullHeight * progress
                    val barTop = if (isUp) topY + (fullHeight - h) else topY
                    drawRect(
                        color = color,
                        topLeft = Offset(centerX - barWidth / 2f, barTop),
                        size = Size(barWidth, h),
                    )
                    // Konektor ke bar berikutnya
                    if (index < bars.lastIndex && !isTotal) {
                        val nextStart = bars[index + 1].second
                        drawLine(
                            color = palette.textMuted,
                            start = Offset(centerX + barWidth / 2f, yFor(end)),
                            end = Offset((index + 1) * slot + slot / 2f - barWidth / 2f, yFor(nextStart)),
                            strokeWidth = 1.dp.toPx(),
                        )
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            bars.forEachIndexed { index, (label, start, end) ->
                val isTotal = index == bars.lastIndex
                val delta = end - start
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        fontSize = 12.sp,
                        fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
                        color = if (isTotal) palette.textPrimary else palette.textSecondary,
                    )
                    Text(
                        text = when {
                            isTotal -> "${delta.toInt()} jt"
                            delta >= 0 -> "+${delta.toInt()} jt"
                            else -> "${delta.toInt()} jt"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when {
                            isTotal -> palette.primary
                            delta >= 0 -> palette.success
                            else -> palette.danger
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Surface(
            onClick = { revealKey += 1 },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Box(modifier = Modifier.padding(vertical = 11.dp), contentAlignment = Alignment.Center) {
                Text("Putar Ulang Animasi", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
            }
        }
    }
}
