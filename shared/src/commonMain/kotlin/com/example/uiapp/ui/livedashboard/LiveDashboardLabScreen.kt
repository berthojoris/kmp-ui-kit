package com.example.uiapp.ui.livedashboard

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.HapticKind
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.theme.LightAppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class FeedEvent(val id: Int, val title: String, val detail: String, val kind: String)

private val EventTemplates = listOf(
    "Pemesanan baru" to "Villa Tebu 2 malam • Rp2.400.000",
    "Check-in tamu" to "Keluarga Wijaya • 4 tamu",
    "Pembayaran masuk" to "Transfer BCA terverifikasi",
    "Ulasan 5 bintang" to "\"Pemandangan kolam luar biasa\"",
    "Permintaan layanan" to "Antar makan malam ke villa 3",
)

private val Ranges = listOf("1J", "24J", "7H")
private const val MaxPoints = 40

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LiveDashboardLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    val rng = remember { Random(7) }

    var live by remember { mutableStateOf(true) }
    var range by remember { mutableStateOf("24J") }
    var reconnecting by remember { mutableStateOf(false) }
    var ticks by remember { mutableIntStateOf(0) }

    // KPI + sparkline
    var revenue by remember { mutableIntStateOf(18_400_000) }
    var guests by remember { mutableIntStateOf(42) }
    var occupancy by remember { mutableStateOf(78) }
    val spark = remember {
        mutableStateListOf<Float>().apply { repeat(24) { i -> add(15_000_000f + i * 380_000f) } }
    }

    // Feed
    val feed = remember { mutableStateListOf<FeedEvent>() }
    var nextId by remember { mutableIntStateOf(1) }

    // Loop streaming simulasi
    LaunchedEffect(live, range, reconnecting) {
        while (true) {
            delay(when (range) { "1J" -> 700L; "24J" -> 1200L; else -> 2000L })
            if (reconnecting) continue
            if (live) {
                ticks++
                revenue += rng.nextInt(120_000, 900_000)
                guests += rng.nextInt(-2, 4)
                occupancy = (occupancy + rng.nextInt(-3, 4)).coerceIn(40, 100)
                spark.add(revenue.toFloat())
                if (spark.size > MaxPoints) spark.removeAt(0)
                if (ticks % 2 == 0) {
                    val tpl = EventTemplates[rng.nextInt(EventTemplates.size)]
                    feed.add(0, FeedEvent(nextId++, tpl.first, tpl.second, tpl.first))
                    if (feed.size > 12) feed.removeAt(feed.lastIndex)
                }
                // Simulasi koneksi terputus sesekali.
                if (ticks == 12) {
                    reconnecting = true
                    PerformHapticFeedback(HapticKind.WARNING)
                }
            }
        }
    }
    // Penyambungan ulang otomatis.
    LaunchedEffect(reconnecting) {
        if (reconnecting) {
            delay(2600)
            reconnecting = false
            PerformHapticFeedback(HapticKind.SUCCESS)
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Live Dashboard", subtitle = "Simulasi metrik streaming", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item(key = "intro") {
                    LabIntroCard(
                        title = "Dasbor operasional villa (simulasi)",
                        body = "Nilai KPI bertambah lewat coroutine lokal, sparkline bergulir, dan feed event masuk. " +
                            "Jeda koneksi sesekali memicu state menyambung ulang lalu pulih.",
                    )
                }
                item(key = "status") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (reconnecting) palette.warning.copy(alpha = 0.12f) else palette.success.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, if (reconnecting) palette.warning else palette.success),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            StatusPill(
                                text = if (reconnecting) "Menyambung ulang" else if (live) "Streaming" else "Dijeda",
                                color = when {
                                    reconnecting -> palette.warning
                                    live -> palette.success
                                    else -> palette.textMuted
                                },
                            )
                            Text(
                                if (reconnecting) "Koneksi simulasi terputus, mencoba sambung ulang…" else "Sumber data: generator lokal (bukan server)",
                                fontSize = 11.sp,
                                color = palette.textSecondary,
                                modifier = Modifier.weight(1f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                item(key = "spark") {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text("Pendapatan hari ini", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                Text("Rp${formatShort(revenue)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                            }
                            Sparkline(points = spark, color = palette.primary, modifier = Modifier.fillMaxWidth().height(64.dp))
                            Text("Sparkline bergulir ${spark.size} titik (maks $MaxPoints)", fontSize = 10.sp, color = palette.textMuted)
                        }
                    }
                }
                item(key = "kpis") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KpiCard("Tamu", "$guests", "+${(guests - 42).coerceAtLeast(0)} hari ini", LightAppPalette.info, Modifier.weight(1f))
                        KpiCard("Okupansi", "$occupancy%", if (occupancy >= 78) "Di atas target" else "Di bawah target", if (occupancy >= 78) LightAppPalette.success else LightAppPalette.warning, Modifier.weight(1f))
                    }
                }
                item(key = "feedhead") {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Feed event", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        Text("$ticks tick", fontSize = 11.sp, color = palette.textMuted)
                    }
                }
                if (feed.isEmpty()) {
                    item(key = "empty") {
                        EmptyFeedCard(live = live)
                    }
                } else {
                    items(feed, key = { it.id }) { ev ->
                        FeedRow(ev)
                    }
                }
                item(key = "space") { Spacer(Modifier.height(4.dp)) }
            }

            // Kontrol bawah: pause/live + rentang
            Surface(color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = { live = !live; PerformHapticFeedback(if (live) HapticKind.SUCCESS else HapticKind.SELECTION) },
                        enabled = !reconnecting,
                        colors = ButtonDefaults.buttonColors(containerColor = palette.primary, contentColor = palette.onPrimary),
                        modifier = Modifier.weight(1f),
                    ) { Text(if (live) "Jeda" else "Lanjut Live", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    Ranges.forEach { r ->
                        val selected = range == r
                        Surface(
                            onClick = { range = r; PerformHapticFeedback(HapticKind.SELECTION) },
                            shape = RoundedCornerShape(999.dp),
                            color = if (selected) palette.primary else palette.surface,
                            border = BorderStroke(1.dp, palette.border),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                        ) {
                            Text(
                                r,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) palette.onPrimary else palette.textSecondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(label: String, value: String, sub: String, accent: Color, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, fontSize = 11.sp, color = palette.textMuted)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = accent)
            Text(sub, fontSize = 9.sp, color = palette.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun Sparkline(points: List<Float>, color: Color, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Canvas(modifier = modifier.background(palette.surfaceMuted, RoundedCornerShape(10.dp))) {
        if (points.size < 2) return@Canvas
        val min = points.min()
        val max = points.max()
        val span = (max - min).takeIf { it > 1f } ?: 1f
        val stepX = size.width / (points.size - 1)
        val path = Path()
        points.forEachIndexed { i, v ->
            val x = stepX * i
            val y = size.height - ((v - min) / span) * (size.height - 8f) - 4f
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color = color, style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // Titik terakhir ditonjolkan.
        val lastX = stepX * (points.size - 1)
        val lastY = size.height - ((points.last() - min) / span) * (size.height - 8f) - 4f
        drawCircle(color, radius = 5f, center = Offset(lastX, lastY))
    }
}

@Composable
private fun FeedRow(ev: FeedEvent) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(palette.primary, RoundedCornerShape(999.dp)),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(ev.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(ev.detail, fontSize = 10.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text("baru", fontSize = 9.sp, color = palette.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EmptyFeedCard(live: Boolean) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = palette.surfaceMuted,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Belum ada event", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
            Text(if (live) "Menunggu tick pertama…" else "Streaming dijeda — tekan Lanjut Live", fontSize = 11.sp, color = palette.textMuted)
        }
    }
}

private fun formatShort(v: Int): String {
    val s = v.toString()
    val sb = StringBuilder()
    var c = 0
    for (i in s.indices.reversed()) {
        if (c > 0 && c % 3 == 0) sb.insert(0, '.')
        sb.insert(0, s[i])
        c++
    }
    return sb.toString()
}
