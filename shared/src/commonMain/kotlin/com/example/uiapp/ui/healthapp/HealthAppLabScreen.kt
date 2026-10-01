package com.example.uiapp.ui.healthapp

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatToggle
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private enum class HealthPeriod(val label: String) {
    MINGGU("7 Hari"),
    BULAN("30 Hari"),
}

private enum class HealthSyncState { IDLE, LOADING, SUCCESS, ERROR }

private data class MetricDetail(
    val id: String,
    val title: String,
    val favoriteValue: String,
    val favoriteNote: String,
    val weekValues: List<Int>,
    val monthValues: List<Int>,
)

private val metricDetails = listOf(
    MetricDetail(
        id = "langkah",
        title = "Langkah",
        favoriteValue = "8.432",
        favoriteNote = "langkah hari ini",
        weekValues = listOf(6200, 8400, 7100, 9500, 8432, 5200, 10400),
        monthValues = listOf(
            6100, 7200, 8300, 5400, 9100, 8400, 7600,
            6800, 8100, 9200, 7300, 6400, 8800, 9500,
            5200, 7700, 8432, 6600, 9000, 8200, 7100,
            5900, 8600, 9400, 7800, 6900, 8300, 10400, 7500, 8432,
        ),
    ),
    MetricDetail(
        id = "tidur",
        title = "Tidur",
        favoriteValue = "7j 24m",
        favoriteNote = "tidur semalam",
        weekValues = listOf(390, 420, 404, 380, 444, 470, 430),
        monthValues = listOf(
            400, 415, 390, 430, 405, 448, 420,
            398, 410, 436, 402, 388, 452, 428,
            404, 418, 444, 396, 424, 440, 408,
            382, 426, 450, 412, 398, 434, 444, 420, 424,
        ),
    ),
    MetricDetail(
        id = "detak",
        title = "Selang Detak",
        favoriteValue = "62 bpm",
        favoriteNote = "detak istirahat",
        weekValues = listOf(64, 62, 66, 61, 62, 59, 63),
        monthValues = listOf(
            65, 64, 62, 66, 63, 61, 62,
            64, 63, 66, 62, 60, 61, 63,
            65, 62, 62, 64, 63, 66, 61,
            60, 62, 64, 63, 61, 62, 59, 63, 62,
        ),
    ),
)

private data class HealthHighlight(val id: String, val title: String, val body: String)

private val healthHighlights = listOf(
    HealthHighlight(
        id = "h1",
        title = "Langkah minggu ini 12% lebih banyak",
        body = "Rata-rata 7.890 langkah per hari, puncaknya hari Minggu dengan 10.400 langkah.",
    ),
    HealthHighlight(
        id = "h2",
        title = "Tidur lebih teratur",
        body = "Durasi tidur rata-rata 7 jam 5 menit, naik 18 menit dibanding bulan lalu.",
    ),
    HealthHighlight(
        id = "h3",
        title = "Detak istirahat stabil",
        body = "Selang detak istirahat konsisten di 59–66 bpm tanpa lonjakan berarti.",
    ),
)

private enum class TrendDir(val label: String) {
    NAIK("Naik"),
    TURUN("Turun"),
    TETAP("Tetap"),
}

private data class TrendEntry(
    val id: String,
    val label: String,
    val dir: TrendDir,
    val percentText: String,
    val level: String,
    val note: String,
)

private val trendEntries = listOf(
    TrendEntry("t1", "Langkah", TrendDir.NAIK, "+12%", "Level Tinggi", "30 hari terakhir"),
    TrendEntry("t2", "Tidur", TrendDir.NAIK, "+6%", "Level Sedang", "30 hari terakhir"),
    TrendEntry("t3", "Selang Detak", TrendDir.TURUN, "-3%", "Level Rendah", "30 hari terakhir"),
    TrendEntry("t4", "Kesadaran Penuh", TrendDir.TETAP, "0%", "Level Rendah", "belum banyak data"),
)

private data class ChartStats(val avg: Int, val min: Int, val max: Int)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HealthAppLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var permissionGranted by remember { mutableStateOf(true) }
    var simulateFailure by remember { mutableStateOf(false) }
    var syncState by remember { mutableStateOf(HealthSyncState.SUCCESS) }
    var syncTick by remember { mutableIntStateOf(0) }
    var selectedMetricId by remember { mutableStateOf("langkah") }
    var period by remember { mutableStateOf(HealthPeriod.MINGGU) }

    // Sinkronisasi simulasi: tick > 0 memicu loading lalu sukses/gagal.
    LaunchedEffect(syncTick) {
        if (syncTick > 0) {
            syncState = HealthSyncState.LOADING
            delay(1300)
            syncState = if (simulateFailure) HealthSyncState.ERROR else HealthSyncState.SUCCESS
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Health Summary & Trends",
                subtitle = "Klon UI, tanpa HealthKit — Simulasi",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                LabIntroCard(
                    title = "Ringkasan Kesehatan",
                    body = "Seluruh metrik berasal dari data mock lokal. Tidak ada HealthKit, sensor perangkat, atau akun kesehatan nyata.",
                    modifier = Modifier.weight(1f),
                )
                SimulasiPill()
            }

            // Kontrol izin & sinkronisasi
            LabCard {
                LabSectionTitle(title = "Kontrol Data", subtitle = "Izin & sinkronisasi mock")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Izin data kesehatan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = if (permissionGranted) {
                                "Status: diberikan — data mock ditampilkan"
                            } else {
                                "Status: tidak diberikan — data disembunyikan"
                            },
                            fontSize = 11.sp,
                            color = if (permissionGranted) palette.success else palette.warning,
                        )
                    }
                    FlatToggle(
                        checked = permissionGranted,
                        onCheckedChange = { granted ->
                            permissionGranted = granted
                            if (granted) syncTick++
                        },
                        modifier = Modifier.semantics {
                            contentDescription = "Izin data kesehatan"
                        },
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Simulasikan kegagalan sinkron",
                        fontSize = 13.sp,
                        color = palette.textSecondary,
                        modifier = Modifier.weight(1f),
                    )
                    FlatToggle(
                        checked = simulateFailure,
                        onCheckedChange = { simulateFailure = it },
                        modifier = Modifier.semantics {
                            contentDescription = "Simulasikan kegagalan sinkron"
                        },
                    )
                }

                SmallActionButton(
                    text = if (syncState == HealthSyncState.LOADING) "Sedang Menyinkronkan…" else "Segarkan Data (Simulasi)",
                    onClick = { syncTick++ },
                    enabled = permissionGranted && syncState != HealthSyncState.LOADING,
                )
                if (!permissionGranted) {
                    Text(
                        "Segarkan dinonaktifkan sampai izin diberikan (disabled).",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                }
            }

            if (!permissionGranted) {
                NoDataCard(onGrant = {
                    permissionGranted = true
                    syncTick++
                })
            } else when (syncState) {
                HealthSyncState.LOADING -> LoadingSummaryCard()
                HealthSyncState.ERROR -> ErrorSummaryCard(onRetry = { syncTick++ })
                else -> {
                    // Kartu favorit
                    LabCard {
                        LabSectionTitle(
                            title = "Kartu Favorit",
                            subtitle = "Ketuk metrik untuk membuka detail",
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            metricDetails.forEach { metric ->
                                FavoriteMetricCard(
                                    metric = metric,
                                    selected = metric.id == selectedMetricId,
                                    onClick = { selectedMetricId = metric.id },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }

                    // Highlights naratif
                    LabCard {
                        LabSectionTitle(
                            title = "Highlights",
                            subtitle = "Ringkasan naratif 7 hari terakhir",
                        )
                        healthHighlights.forEach { highlight ->
                            HighlightRow(highlight)
                        }
                    }

                    // Panel Tren
                    LabCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            LabSectionTitle(
                                title = "Tren",
                                subtitle = "Dibanding 30 hari sebelumnya",
                                modifier = Modifier.weight(1f),
                            )
                            StatusPill(text = "4 Tren", color = palette.info)
                        }
                        trendEntries.forEach { entry ->
                            TrendRow(entry)
                        }
                    }

                    // Detail per metrik
                    val metric = metricDetails.firstOrNull { it.id == selectedMetricId } ?: metricDetails.first()
                    MetricDetailCard(
                        metric = metric,
                        period = period,
                        onPeriodChange = { period = it },
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "Catatan pola UI:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = palette.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "• Semua angka adalah mock deterministik, bukan data kesehatan pengguna.",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        "• Status selalu memuat label teks (naik/turun/tetap), bukan warna saja.",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        "• Grafik digambar dengan Canvas tanpa library chart eksternal.",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun SimulasiPill() {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = palette.info.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, palette.info),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Text(
            text = "Simulasi",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = palette.info,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}

@Composable
private fun StatusPill(text: String, color: Color) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, color),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun SmallActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = if (enabled) palette.primary else palette.primary.copy(alpha = 0.4f),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = palette.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 11.dp, horizontal = 14.dp),
        )
    }
}

@Composable
private fun FavoriteMetricCard(
    metric: MetricDetail,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) palette.primaryContainer else palette.surface)
            .border(
                BorderStroke(if (selected) 2.dp else 1.dp, if (selected) palette.primary else palette.border),
                RoundedCornerShape(14.dp),
            )
            .clickable { onClick() }
            .padding(10.dp)
            .semantics { contentDescription = "Pilih metrik ${metric.title}" },
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = metric.title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) palette.onPrimaryContainer else palette.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = metric.favoriteValue,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (selected) palette.onPrimaryContainer else palette.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        MetricSparkline(
            values = metric.weekValues,
            tint = palette.primary,
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp),
        )
        Text(
            text = metric.favoriteNote,
            fontSize = 9.sp,
            color = palette.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun MetricSparkline(values: List<Int>, tint: Color, modifier: Modifier = Modifier) {
    val minV = remember(values) { values.minOrNull() ?: 0 }
    val maxV = remember(values) { (values.maxOrNull() ?: 1).coerceAtLeast(minV + 1) }
    Canvas(modifier = modifier) {
        if (values.size < 2) return@Canvas
        val stepX = size.width / (values.size - 1)
        val range = (maxV - minV).toFloat()
        var prev = Offset(0f, size.height - ((values[0] - minV) / range) * size.height)
        for (i in 1 until values.size) {
            val point = Offset(
                x = i * stepX,
                y = size.height - ((values[i] - minV) / range) * size.height,
            )
            drawLine(
                color = tint,
                start = prev,
                end = point,
                strokeWidth = 3f,
                cap = StrokeCap.Round,
            )
            prev = point
        }
    }
}

@Composable
private fun HighlightRow(highlight: HealthHighlight) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(palette.primary),
        )
        Column {
            Text(
                text = highlight.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
            )
            Text(
                text = highlight.body,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = palette.textSecondary,
            )
        }
    }
}

@Composable
private fun TrendRow(entry: TrendEntry) {
    val palette = LocalAppPalette.current
    val trendColor = when (entry.dir) {
        TrendDir.NAIK -> palette.success
        TrendDir.TURUN -> palette.danger
        TrendDir.TETAP -> palette.textMuted
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(trendColor.copy(alpha = 0.12f))
                .border(1.dp, trendColor, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            TrendArrowIcon(
                dir = entry.dir,
                tint = trendColor,
                modifier = Modifier.size(16.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${entry.dir.label} ${entry.percentText} · ${entry.note}",
                fontSize = 11.sp,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        StatusPill(text = entry.level, color = trendColor)
    }
}

@Composable
private fun TrendArrowIcon(dir: TrendDir, tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.14f, cap = StrokeCap.Round)
        val tipX = if (dir == TrendDir.TURUN) w * 0.25f else w * 0.75f
        val tipY = if (dir == TrendDir.TURUN) h * 0.75f else h * 0.25f
        val tailX = if (dir == TrendDir.TURUN) w * 0.75f else w * 0.25f
        val tailY = if (dir == TrendDir.TURUN) h * 0.25f else h * 0.75f
        if (dir == TrendDir.TETAP) {
            drawLine(
                color = tint,
                start = Offset(w * 0.2f, h * 0.5f),
                end = Offset(w * 0.8f, h * 0.5f),
                strokeWidth = w * 0.14f,
                cap = StrokeCap.Round,
            )
        } else {
            drawLine(color = tint, start = Offset(tailX, tailY), end = Offset(tipX, tipY), strokeWidth = stroke.width, cap = stroke.cap)
            drawLine(color = tint, start = Offset(tipX, tipY), end = Offset(tipX - w * 0.02f, tipY + (if (dir == TrendDir.TURUN) -h * 0.3f else h * 0.3f)), strokeWidth = stroke.width, cap = stroke.cap)
            drawLine(color = tint, start = Offset(tipX, tipY), end = Offset(tipX + (if (dir == TrendDir.TURUN) -w * 0.3f else w * 0.3f), tipY), strokeWidth = stroke.width, cap = stroke.cap)
        }
    }
}

@Composable
private fun MetricDetailCard(
    metric: MetricDetail,
    period: HealthPeriod,
    onPeriodChange: (HealthPeriod) -> Unit,
) {
    val palette = LocalAppPalette.current
    val values = if (period == HealthPeriod.MINGGU) metric.weekValues else metric.monthValues
    val stats = remember(values) {
        ChartStats(
            avg = values.average().toInt(),
            min = values.minOrNull() ?: 0,
            max = values.maxOrNull() ?: 0,
        )
    }

    LabCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LabSectionTitle(
                title = "Detail ${metric.title}",
                subtitle = "Grafik periode, rata-rata, dan rentang",
                modifier = Modifier.weight(1f),
            )
            StatusPill(text = "Aktif", color = palette.primary)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HealthPeriod.entries.forEach { option ->
                val selected = option == period
                Surface(
                    onClick = { onPeriodChange(option) },
                    shape = RoundedCornerShape(999.dp),
                    color = if (selected) palette.primary else palette.surfaceMuted,
                    border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier.semantics {
                        contentDescription = "Periode ${option.label}"
                    },
                ) {
                    Text(
                        text = option.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) palette.onPrimary else palette.textSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
        }

        BarChart(
            values = values,
            tint = palette.primary,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCell(
                label = "Rata-rata",
                value = "${stats.avg}",
                modifier = Modifier.weight(1f),
            )
            StatCell(
                label = "Terendah",
                value = "${stats.min}",
                modifier = Modifier.weight(1f),
            )
            StatCell(
                label = "Tertinggi",
                value = "${stats.max}",
                modifier = Modifier.weight(1f),
            )
        }

        Text(
            text = "Rentang ${metric.title.lowercase()}: ${stats.min}–${stats.max} (${period.label.lowercase()}).",
            fontSize = 11.sp,
            color = palette.textMuted,
        )
    }
}

@Composable
private fun StatCell(label: String, value: String, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(label, fontSize = 10.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun BarChart(values: List<Int>, tint: Color, modifier: Modifier = Modifier) {
    val maxV = remember(values) { (values.maxOrNull() ?: 1).coerceAtLeast(1).toFloat() }
    Canvas(modifier = modifier) {
        val slot = size.width / values.size
        val barWidth = (slot * 0.55f).coerceAtLeast(2f)
        values.forEachIndexed { index, value ->
            val barHeight = (value / maxV) * (size.height - 6f)
            val left = index * slot + (slot - barWidth) / 2f
            drawRoundRect(
                color = if (value >= maxV) tint else tint.copy(alpha = 0.35f),
                topLeft = Offset(left, size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 3f, barWidth / 3f),
            )
        }
        drawLine(
            color = tint.copy(alpha = 0.3f),
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = 2f,
        )
    }
}

@Composable
private fun LoadingSummaryCard() {
    val palette = LocalAppPalette.current
    val transition = rememberInfiniteTransition(label = "healthShimmer")
    val shimmerAlpha by transition.animateFloat(
        initialValue = 0.30f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "healthShimmerAlpha",
    )

    LabCard {
        LabSectionTitle(title = "Memuat Ringkasan", subtitle = "Sinkronisasi data mock berjalan…")
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceMuted.copy(alpha = shimmerAlpha))
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
            )
        }
        Text(
            "Sedang memuat kartu favorit, highlights, dan tren (loading).",
            fontSize = 11.sp,
            color = palette.textMuted,
        )
    }
}

@Composable
private fun ErrorSummaryCard(onRetry: () -> Unit) {
    val palette = LocalAppPalette.current
    LabCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatusPill(text = "Gagal", color = palette.danger)
            Text(
                "Gagal memuat ringkasan kesehatan",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
                modifier = Modifier.weight(1f),
            )
        }
        Text(
            "Sinkronisasi data mock gagal (simulasi). Matikan opsi kegagalan lalu coba lagi.",
            fontSize = 11.sp,
            lineHeight = 16.sp,
            color = palette.textSecondary,
        )
        SmallActionButton(text = "Coba Lagi", onClick = onRetry)
    }
}

@Composable
private fun NoDataCard(onGrant: () -> Unit) {
    val palette = LocalAppPalette.current
    LabCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            contentAlignment = Alignment.Center,
        ) {
            NoDataIcon(tint = palette.textMuted)
        }
        Text(
            "Tidak ada data",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textPrimary,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "Izin data kesehatan tidak diberikan, sehingga ringkasan, highlights, tren, dan detail metrik tidak dapat ditampilkan.",
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = palette.textSecondary,
        )
        SmallActionButton(text = "Beri Izin (Simulasi)", onClick = onGrant)
    }
}

@Composable
private fun NoDataIcon(tint: Color) {
    // Posisi bar dihitung sekali (fraksi), bukan dialokasikan tiap frame draw.
    val barXs = remember { floatArrayOf(0.2f, 0.44f, 0.68f) }
    val barHs = remember { floatArrayOf(0.28f, 0.44f, 0.34f) }
    Canvas(modifier = Modifier.size(72.dp)) {
        val w = this.size.width
        val h = this.size.height
        // Tiga bar data dicoret garis diagonal = "tidak ada data".
        val barW = w * 0.12f
        for (index in barXs.indices) {
            val x = barXs[index] * w
            val barHeight = barHs[index] * h
            drawRoundRect(
                color = tint.copy(alpha = 0.5f),
                topLeft = Offset(x, h * 0.75f - barHeight),
                size = Size(barW, barHeight),
                cornerRadius = CornerRadius(barW / 3f, barW / 3f),
            )
        }
        drawLine(
            color = tint,
            start = Offset(w * 0.12f, h * 0.2f),
            end = Offset(w * 0.88f, h * 0.85f),
            strokeWidth = w * 0.06f,
            cap = StrokeCap.Round,
        )
    }
}
