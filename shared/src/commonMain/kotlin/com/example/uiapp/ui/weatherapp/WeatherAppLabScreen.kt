package com.example.uiapp.ui.weatherapp

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.delay

private enum class WeatherCondition(val label: String) {
    CERAH("Cerah"),
    BERAWAN("Berawan"),
    HUJAN("Hujan"),
    BADAI("Badai Petir"),
}

private enum class LoadState { READY, LOADING, EMPTY, ERROR }

private enum class WeatherIcon { SUN, CLOUD, RAIN, STORM }

private data class HourPoint(val label: String, val temp: Int, val precip: Int)

private data class DayForecast(
    val id: Int,
    val day: String,
    val icon: WeatherIcon,
    val low: Int,
    val high: Int,
    val nowFrac: Float,
)

private val demoCities = listOf("Jakarta", "Bandung", "Surabaya")

private val dayNames = listOf("Hari ini", "Kam", "Jum", "Sab", "Min", "Sen", "Sel", "Rab", "Kam", "Jum")

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WeatherAppLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var city by remember { mutableStateOf(demoCities.first()) }
    var condition by remember { mutableStateOf(WeatherCondition.CERAH) }
    var loadState by remember { mutableStateOf(LoadState.LOADING) }
    var failNext by remember { mutableStateOf(false) }
    var reloadToken by remember { mutableIntStateOf(0) }

    // Simulasi jaringan "memuat kota".
    LaunchedEffect(city, reloadToken) {
        loadState = LoadState.LOADING
        delay(1400)
        loadState = if (failNext) LoadState.ERROR else LoadState.READY
    }

    val hourly = remember(condition) {
        val random = Random(42 + condition.ordinal)
        val base = when (condition) {
            WeatherCondition.CERAH -> 28f
            WeatherCondition.BERAWAN -> 26f
            WeatherCondition.HUJAN -> 24f
            WeatherCondition.BADAI -> 23f
        }
        List(24) { i ->
            val wave = sin(i / 24.0 * 2 * PI).toFloat() * 4f
            val h = if (i < 10) "0$i" else "$i"
            HourPoint(
                label = "$h.00",
                temp = (base + wave + random.nextFloat() * 1.5f).toInt(),
                precip = when (condition) {
                    WeatherCondition.CERAH -> random.nextInt(0, 15)
                    WeatherCondition.BERAWAN -> random.nextInt(5, 40)
                    WeatherCondition.HUJAN -> random.nextInt(30, 85)
                    WeatherCondition.BADAI -> random.nextInt(45, 95)
                },
            )
        }
    }

    val daily = remember(condition) {
        val random = Random(7 + condition.ordinal)
        List(10) { i ->
            val low = 22 + random.nextInt(0, 4)
            val high = low + 5 + random.nextInt(0, 5)
            DayForecast(
                id = i,
                day = dayNames[i],
                icon = when {
                    condition == WeatherCondition.BADAI -> WeatherIcon.STORM
                    condition == WeatherCondition.HUJAN && i % 2 == 0 -> WeatherIcon.RAIN
                    condition == WeatherCondition.CERAH && i % 3 != 2 -> WeatherIcon.SUN
                    i % 2 == 0 -> WeatherIcon.CLOUD
                    else -> WeatherIcon.RAIN
                },
                low = low,
                high = high,
                nowFrac = 0.3f + random.nextFloat() * 0.5f,
            )
        }
    }

    val headerBrush = remember(condition, palette) {
        val top = when (condition) {
            WeatherCondition.CERAH -> palette.info.copy(alpha = 0.55f)
            WeatherCondition.BERAWAN -> palette.textMuted.copy(alpha = 0.40f)
            WeatherCondition.HUJAN -> palette.primary.copy(alpha = 0.50f)
            WeatherCondition.BADAI -> palette.danger.copy(alpha = 0.35f)
        }
        val bottom = when (condition) {
            WeatherCondition.CERAH -> palette.warning.copy(alpha = 0.20f)
            WeatherCondition.BERAWAN -> palette.surfaceMuted
            WeatherCondition.HUJAN -> palette.info.copy(alpha = 0.15f)
            WeatherCondition.BADAI -> palette.surfaceMuted
        }
        Brush.linearGradient(listOf(top, bottom))
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Apple Weather",
                subtitle = "Klon UI, data mock lokal",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LabIntroCard(
                title = "Klon UI Apple Weather — Simulasi",
                body = "Seluruh suhu, presipitasi, dan kartu modul berasal dari data mock lokal yang deterministik. " +
                    "Proses \"memuat kota\" hanyalah simulasi jaringan tanpa API cuaca nyata.",
            )

            // Kontrol demo: kota, kondisi, state
            LabCard {
                LabSectionTitle(title = "Kontrol Demo", subtitle = "Ganti kota/kondisi atau pratinjau state pemuatan")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    demoCities.forEach { demoCity ->
                        WeatherChip(
                            label = demoCity,
                            selected = city == demoCity,
                            onClick = { city = demoCity },
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    WeatherCondition.entries.forEach { c ->
                        WeatherChip(
                            label = c.label,
                            selected = condition == c,
                            onClick = { condition = c },
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    WeatherChip(
                        label = if (failNext) "Galat berikutnya: AKTIF" else "Simulasikan galat muat",
                        selected = failNext,
                        modifier = Modifier.weight(1f),
                        onClick = { failNext = !failNext },
                    )
                    WeatherChip(
                        label = if (loadState == LoadState.LOADING) "Sedang memuat…" else "Muat ulang kota",
                        selected = false,
                        enabled = loadState != LoadState.LOADING,
                        modifier = Modifier.weight(1f),
                        onClick = { reloadToken++ },
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    WeatherChip(
                        label = "Pratinjau state kosong",
                        selected = loadState == LoadState.EMPTY,
                        modifier = Modifier.weight(1f),
                        onClick = { loadState = LoadState.EMPTY },
                    )
                    WeatherChip(
                        label = "Kembali ke data",
                        selected = loadState == LoadState.READY,
                        modifier = Modifier.weight(1f),
                        onClick = { loadState = LoadState.READY },
                    )
                }
            }

            when (loadState) {
                LoadState.LOADING -> LoadingCard(city = city)
                LoadState.ERROR -> ErrorCard(onRetry = { reloadToken++ })
                LoadState.EMPTY -> EmptyCard(onPick = { reloadToken++ })
                LoadState.READY -> {
                    // Header suhu besar dengan latar gradien sesuai kondisi
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(headerBrush)
                            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(city, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                            StatusLabel(text = "Simulasi", tone = palette.info)
                        }
                        Text("${hourly[8].temp}°", fontSize = 64.sp, fontWeight = FontWeight.Thin, color = palette.textPrimary)
                        Text(condition.label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                        Text(
                            text = "Tertinggi ${daily[0].high}° · Terendah ${daily[0].low}°",
                            fontSize = 12.sp,
                            color = palette.textMuted,
                        )
                        Text(
                            text = "Terasa seperti ${hourly[8].temp - 1}° · Data mock lokal",
                            fontSize = 11.sp,
                            color = palette.textMuted,
                        )
                    }

                    HourlyChartCard(hourly = hourly)

                    PrecipChartCard(hourly = hourly)

                    DailyForecastCard(daily = daily)

                    ModuleCardsGrid(condition = condition)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun HourlyChartCard(hourly: List<HourPoint>) {
    val palette = LocalAppPalette.current
    var chartSize by remember { mutableStateOf(IntSize.Zero) }
    val temps = remember(hourly) { hourly.map { it.temp.toFloat() } }
    val bounds = remember(temps) {
        val min = temps.min()
        val max = temps.max()
        if (max - min < 1f) min - 1f to max + 1f else min to max
    }
    val linePath = remember(chartSize, temps) { buildLinePath(temps, bounds.first, bounds.second, chartSize) }
    val dotBrush = remember(palette) {
        Brush.linearGradient(listOf(palette.warning.copy(alpha = 0.9f), palette.danger.copy(alpha = 0.7f)))
    }

    LabCard {
        LabSectionTitle(title = "Suhu per 24 Jam", subtitle = "Garis suhu + kapsul jam (data mock)")
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .onSizeChanged { chartSize = it },
        ) {
            for (g in 1 until 4) {
                val gy = size.height * g / 4f
                drawLine(
                    color = palette.border.copy(alpha = 0.55f),
                    start = Offset(0f, gy),
                    end = Offset(size.width, gy),
                    strokeWidth = 1f,
                )
            }
            drawPath(
                path = linePath,
                color = palette.warning,
                style = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
            val step = size.width / (temps.size - 1)
            for (i in temps.indices step 6) {
                val y = mapValueToY(temps[i], bounds.first, bounds.second, size.height)
                drawCircle(brush = dotBrush, radius = 5f, center = Offset(step * i, y))
                drawCircle(color = palette.surface, radius = 5f, center = Offset(step * i, y), style = Stroke(width = 2f))
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("${hourly[0].label} ${hourly[0].temp}°", fontSize = 10.sp, color = palette.textMuted)
            Text("${hourly[12].label} ${hourly[12].temp}°", fontSize = 10.sp, color = palette.textMuted)
            Text("${hourly[23].label} ${hourly[23].temp}°", fontSize = 10.sp, color = palette.textMuted)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            hourly.forEach { hour ->
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(999.dp))
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(hour.label, fontSize = 9.sp, color = palette.textMuted)
                    WeatherGlyph(icon = iconForPrecip(hour.precip), size = 14)
                    Text("${hour.temp}°", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    Text("${hour.precip}%", fontSize = 9.sp, color = palette.info)
                }
            }
        }
    }
}

@Composable
private fun PrecipChartCard(hourly: List<HourPoint>) {
    val palette = LocalAppPalette.current
    LabCard {
        LabSectionTitle(title = "Peluang Presipitasi", subtitle = "Persen hujan per jam (mock)")
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
        ) {
            val barStep = size.width / hourly.size
            val barWidth = barStep * 0.55f
            hourly.forEachIndexed { i, hour ->
                val h = size.height * (hour.precip / 100f).coerceIn(0f, 1f)
                drawLine(
                    color = palette.info.copy(alpha = 0.85f),
                    start = Offset(barStep * i + barStep / 2f, size.height),
                    end = Offset(barStep * i + barStep / 2f, size.height - h),
                    strokeWidth = barWidth,
                    cap = StrokeCap.Round,
                )
            }
            drawLine(
                color = palette.border,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 1f,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("0%", fontSize = 10.sp, color = palette.textMuted)
            Text("Legenda: batang biru = peluang hujan per jam", fontSize = 10.sp, color = palette.textSecondary)
            Text("100%", fontSize = 10.sp, color = palette.textMuted)
        }
    }
}

@Composable
private fun DailyForecastCard(daily: List<DayForecast>) {
    val palette = LocalAppPalette.current
    val globalLow = remember(daily) { daily.minOf { it.low }.toFloat() }
    val globalHigh = remember(daily) { daily.maxOf { it.high }.toFloat() }

    LabCard {
        LabSectionTitle(title = "Prakiraan 10 Hari", subtitle = "Bar hi/lo relatif terhadap rentang 10 hari")
        daily.forEach { day ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = day.day,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                    modifier = Modifier.width(64.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                WeatherGlyph(icon = day.icon, size = 18)
                Text("${day.low}°", fontSize = 12.sp, color = palette.textMuted, modifier = Modifier.width(30.dp))
                HiLoBar(day = day, globalLow = globalLow, globalHigh = globalHigh, modifier = Modifier.weight(1f))
                Text("${day.high}°", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, modifier = Modifier.width(34.dp))
            }
        }
    }
}

@Composable
private fun HiLoBar(day: DayForecast, globalLow: Float, globalHigh: Float, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    val brush = remember(palette) {
        Brush.horizontalGradient(listOf(palette.info.copy(alpha = 0.6f), palette.warning.copy(alpha = 0.9f)))
    }
    val span = (globalHigh - globalLow).coerceAtLeast(1f)
    Canvas(
        modifier = modifier
            .height(8.dp)
            .clip(RoundedCornerShape(999.dp)),
    ) {
        drawRect(color = palette.surfaceMuted)
        val startFrac = ((day.low - globalLow) / span).coerceIn(0f, 1f)
        val endFrac = ((day.high - globalLow) / span).coerceIn(0f, 1f)
        val x0 = size.width * startFrac
        val x1 = size.width * endFrac
        drawRect(
            brush = brush,
            topLeft = Offset(x0, 0f),
            size = androidx.compose.ui.geometry.Size(x1 - x0, size.height),
        )
        val nowX = size.width * (startFrac + (endFrac - startFrac) * day.nowFrac)
        drawCircle(color = palette.surface, radius = 5f, center = Offset(nowX, size.height / 2f))
        drawCircle(color = palette.textPrimary, radius = 5f, center = Offset(nowX, size.height / 2f), style = Stroke(width = 2f))
    }
}

@Composable
private fun ModuleCardsGrid(condition: WeatherCondition) {
    val palette = LocalAppPalette.current
    LabSectionTitle(title = "Kartu Modul Cuaca", subtitle = "Angin, UV, matahari, kualitas udara, kelembapan, visibilitas")
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val wide = maxWidth >= 340.dp
        if (wide) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    WindCard(condition = condition, modifier = Modifier.weight(1f))
                    UvCard(modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SunCard(modifier = Modifier.weight(1f))
                    AqiCard(modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HumidityCard(condition = condition, modifier = Modifier.weight(1f))
                    VisibilityCard(condition = condition, modifier = Modifier.weight(1f))
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                WindCard(condition = condition)
                UvCard()
                SunCard()
                AqiCard()
                HumidityCard(condition = condition)
                VisibilityCard(condition = condition)
            }
        }
    }
    Text(
        text = "Semua nilai modul adalah data mock — bukan pengukuran sensor nyata.",
        fontSize = 10.sp,
        color = palette.textMuted,
    )
}

@Composable
private fun ModuleShell(title: String, subtitle: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.textMuted)
        content()
        Text(subtitle, fontSize = 10.sp, color = palette.textMuted, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun WindCard(condition: WeatherCondition, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    val speed = when (condition) {
        WeatherCondition.CERAH -> 8
        WeatherCondition.BERAWAN -> 12
        WeatherCondition.HUJAN -> 18
        WeatherCondition.BADAI -> 32
    }
    ModuleShell(title = "Angin", subtitle = "Arah barat laut (mock)", modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Canvas(modifier = Modifier.size(52.dp)) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                drawCircle(color = palette.border, radius = cx - 2f, center = Offset(cx, cy), style = Stroke(width = 1.5f))
                val angle = -35.0 * PI / 180.0
                val dx = kotlin.math.cos(angle).toFloat()
                val dy = kotlin.math.sin(angle).toFloat()
                val r = cx - 8f
                drawLine(
                    color = palette.primary,
                    start = Offset(cx - dx * r, cy - dy * r),
                    end = Offset(cx + dx * r, cy + dy * r),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round,
                )
                drawCircle(color = palette.primary, radius = 4f, center = Offset(cx + dx * r, cy + dy * r))
            }
            Column {
                Text("$speed km/j", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                Text("Barat Laut", fontSize = 11.sp, color = palette.textSecondary)
            }
        }
    }
}

@Composable
private fun UvCard(modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    val uv = 6
    ModuleShell(title = "Indeks UV", subtitle = "Tinggi — gunakan pelindung matahari", modifier = modifier) {
        Text("$uv", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = palette.textPrimary)
        Text("Level: Tinggi", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.warning)
        Canvas(modifier = Modifier.fillMaxWidth().height(8.dp)) {
            val seg = size.width / 5f
            val tones = listOf(palette.success, palette.success, palette.warning, palette.danger, palette.danger)
            tones.forEachIndexed { i, tone ->
                drawLine(
                    color = tone.copy(alpha = if (i == 2) 1f else 0.35f),
                    start = Offset(seg * i + 2f, size.height / 2f),
                    end = Offset(seg * (i + 1) - 2f, size.height / 2f),
                    strokeWidth = size.height,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
private fun SunCard(modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    ModuleShell(title = "Matahari", subtitle = "Terbit 05.48 · Terbenam 17.52", modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxWidth().height(54.dp)) {
            val w = size.width
            val h = size.height
            // Busur parabola digambar sebagai segmen garis (alokasi nol per frame).
            val segments = 24
            var prevX = 0f
            var prevY = h - (4f * 0f * (1f - 0f)) * h * 1.55f
            for (i in 1..segments) {
                val t = i / segments.toFloat()
                val x = w * t
                val y = h - (4f * t * (1f - t)) * h * 1.55f
                drawLine(
                    color = palette.border,
                    start = Offset(prevX, prevY),
                    end = Offset(x, y),
                    strokeWidth = 1.5f,
                    cap = StrokeCap.Round,
                )
                prevX = x
                prevY = y
            }
            val t = 0.62f
            val bx = w * t
            val by = h - (4f * t * (1f - t)) * h * 1.55f
            drawCircle(color = palette.warning, radius = 6f, center = Offset(bx, by))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("↑ 05.48", fontSize = 11.sp, color = palette.textSecondary)
            Text("↓ 17.52", fontSize = 11.sp, color = palette.textSecondary)
        }
    }
}

@Composable
private fun AqiCard(modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    val aqi = 42
    ModuleShell(title = "Kualitas Udara", subtitle = "Indeks 42 — Baik (mock)", modifier = modifier) {
        Text("$aqi", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = palette.textPrimary)
        Text("Level: Baik", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.success)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            listOf(
                palette.success to "Baik",
                palette.warning to "Sedang",
                palette.danger to "Buruk",
            ).forEach { (tone, label) ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(tone.copy(alpha = 0.15f))
                        .border(1.dp, tone, RoundedCornerShape(6.dp))
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = tone)
                }
            }
        }
    }
}

@Composable
private fun HumidityCard(condition: WeatherCondition, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    val humidity = when (condition) {
        WeatherCondition.CERAH -> 58
        WeatherCondition.BERAWAN -> 68
        WeatherCondition.HUJAN -> 84
        WeatherCondition.BADAI -> 92
    }
    ModuleShell(title = "Kelembapan", subtitle = "Titik embun 21° (mock)", modifier = modifier) {
        Text("$humidity%", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = palette.textPrimary)
        Text(if (humidity > 80) "Terasa lembap" else "Nyaman", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.info)
    }
}

@Composable
private fun VisibilityCard(condition: WeatherCondition, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    val visibility = when (condition) {
        WeatherCondition.CERAH -> 16
        WeatherCondition.BERAWAN -> 10
        WeatherCondition.HUJAN -> 6
        WeatherCondition.BADAI -> 3
    }
    ModuleShell(title = "Visibilitas", subtitle = if (visibility < 5) "Jarak pandang rendah" else "Jarak pandang baik", modifier = modifier) {
        Text("$visibility km", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = palette.textPrimary)
        Text(
            text = if (visibility < 5) "Level: Rendah — hati-hati berkendara" else "Level: Baik",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (visibility < 5) palette.warning else palette.success,
        )
    }
}

@Composable
private fun WeatherGlyph(icon: WeatherIcon, size: Int) {
    val palette = LocalAppPalette.current
    Canvas(modifier = Modifier.size(size.dp)) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val r = this.size.minDimension / 2.4f
        when (icon) {
            WeatherIcon.SUN -> {
                drawCircle(color = palette.warning, radius = r * 0.65f, center = Offset(cx, cy))
                for (i in 0 until 8) {
                    val angle = i * PI / 4
                    val dx = kotlin.math.cos(angle).toFloat()
                    val dy = kotlin.math.sin(angle).toFloat()
                    drawLine(
                        color = palette.warning,
                        start = Offset(cx + dx * r * 0.85f, cy + dy * r * 0.85f),
                        end = Offset(cx + dx * r * 1.15f, cy + dy * r * 1.15f),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round,
                    )
                }
            }
            WeatherIcon.CLOUD -> {
                drawCircle(color = palette.textMuted, radius = r * 0.55f, center = Offset(cx - r * 0.2f, cy + r * 0.1f))
                drawCircle(color = palette.textMuted, radius = r * 0.45f, center = Offset(cx + r * 0.3f, cy + r * 0.15f))
                drawCircle(color = palette.textMuted, radius = r * 0.5f, center = Offset(cx, cy - r * 0.15f))
            }
            WeatherIcon.RAIN -> {
                drawCircle(color = palette.textMuted, radius = r * 0.5f, center = Offset(cx, cy - r * 0.15f))
                for (i in 0 until 3) {
                    val x = cx - r * 0.5f + i * r * 0.5f
                    drawLine(
                        color = palette.info,
                        start = Offset(x, cy + r * 0.35f),
                        end = Offset(x - 2f, cy + r * 0.8f),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round,
                    )
                }
            }
            WeatherIcon.STORM -> {
                drawCircle(color = palette.textMuted, radius = r * 0.5f, center = Offset(cx, cy - r * 0.2f))
                val bolt = Path()
                bolt.moveTo(cx + r * 0.1f, cy + r * 0.1f)
                bolt.lineTo(cx - r * 0.2f, cy + r * 0.7f)
                bolt.lineTo(cx + r * 0.05f, cy + r * 0.7f)
                bolt.lineTo(cx - r * 0.1f, cy + r * 1.1f)
                drawPath(bolt, color = palette.warning, style = Stroke(width = 2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
    }
}

@Composable
private fun WeatherChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(999.dp),
        color = when {
            !enabled -> palette.surfaceMuted
            selected -> palette.primary
            else -> palette.surface
        },
        border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
        modifier = modifier,
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = when {
                !enabled -> palette.textMuted
                selected -> palette.onPrimary
                else -> palette.textSecondary
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun StatusLabel(text: String, tone: androidx.compose.ui.graphics.Color) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = tone.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, tone),
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = tone,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun LoadingCard(city: String) {
    val palette = LocalAppPalette.current
    val shimmer by rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = 0.35f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "shimmerAlpha",
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Memuat kota $city…", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
        Text("Simulasi jaringan — tanpa API cuaca nyata", fontSize = 11.sp, color = palette.textMuted)
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceMuted.copy(alpha = shimmer))
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
            )
        }
    }
}

@Composable
private fun ErrorCard(onRetry: () -> Unit) {
    val palette = LocalAppPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surface)
            .border(1.dp, palette.danger, RoundedCornerShape(16.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("⚠ Gagal memuat data cuaca", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.danger)
        Text(
            text = "Permintaan mock gagal (status: gagal). Periksa lalu coba muat ulang kota.",
            fontSize = 11.sp,
            color = palette.textSecondary,
            textAlign = TextAlign.Center,
        )
        Surface(
            onClick = onRetry,
            shape = RoundedCornerShape(10.dp),
            color = palette.primary,
        ) {
            Text(
                text = "Coba Lagi",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = palette.onPrimary,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            )
        }
    }
}

@Composable
private fun EmptyCard(onPick: () -> Unit) {
    val palette = LocalAppPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Belum ada kota tersimpan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
        Text(
            text = "Tambahkan kota favorit untuk melihat prakiraan (Simulasi).",
            fontSize = 11.sp,
            color = palette.textSecondary,
            textAlign = TextAlign.Center,
        )
        Surface(
            onClick = onPick,
            shape = RoundedCornerShape(10.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
        ) {
            Text(
                text = "Pilih Kota Contoh",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textSecondary,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            )
        }
    }
}

private fun iconForPrecip(precip: Int): WeatherIcon = when {
    precip >= 60 -> WeatherIcon.RAIN
    precip >= 30 -> WeatherIcon.CLOUD
    else -> WeatherIcon.SUN
}

private fun mapValueToY(value: Float, min: Float, max: Float, height: Float): Float {
    val frac = ((value - min) / (max - min)).coerceIn(0f, 1f)
    return height - frac * height * 0.82f - height * 0.09f
}

private fun buildLinePath(values: List<Float>, min: Float, max: Float, size: IntSize): Path {
    val path = Path()
    if (size.width <= 0 || size.height <= 0 || values.size < 2) return path
    val w = size.width.toFloat()
    val h = size.height.toFloat()
    values.forEachIndexed { i, value ->
        val x = w * i / (values.size - 1)
        val y = mapValueToY(value, min, max, h)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    return path
}
