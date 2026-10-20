package com.example.uiapp.ui.flowviz

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.HapticKind
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private enum class ChartTab(val label: String) {
    SANKEY("Sankey"),
    TREEMAP("Treemap"),
    FUNNEL("Funnel"),
    COHORT("Cohort"),
}

private data class Seg(
    val id: String,
    val label: String,
    val value: Long,
    val rect: Rect,
    val colorIdx: Int,
)

// Data mock lokal (dalam ribuan sesi) — Sankey alur trafik.
private data class Flow(val from: String, val to: String, val value: Long)

private val SankeyFlows = listOf(
    Flow("Iklan", "Web", 42), Flow("Iklan", "Aplikasi", 18),
    Flow("Organik", "Web", 30), Flow("Organik", "Chat", 8),
    Flow("Referral", "Aplikasi", 14), Flow("Referral", "Chat", 6),
    Flow("Langsung", "Web", 12), Flow("Langsung", "Aplikasi", 10),
)

private val TreemapItems = listOf(
    "Beranda" to 34L, "Pencarian" to 22L, "Kategori" to 16L,
    "Promo" to 12L, "Akun" to 8L, "Bantuan" to 5L, "Lainnya" to 3L,
)

private val FunnelStages = listOf(
    "Kunjungan" to 100L, "Daftar" to 46L, "Aktif" to 28L,
    "Berlangganan" to 12L, "Retensi 90 hari" to 7L,
)

private val CohortData = listOf(
    listOf(100, 62, 48, 39, 33, 30),
    listOf(100, 58, 44, 36, 31, 28),
    listOf(100, 66, 51, 42, 36, 0),
    listOf(100, 61, 47, 0, 0, 0),
    listOf(100, 57, 0, 0, 0, 0),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FlowVizLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current
    val series = remember(palette) {
        listOf(palette.primary, palette.info, palette.success, palette.warning, palette.danger, palette.textSecondary)
    }

    var tab by remember { mutableStateOf(ChartTab.SANKEY) }
    var selected by remember { mutableStateOf<Seg?>(null) }
    val reveal = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    BackHandler(enabled = true) { onBack() }

    LaunchedEffect(tab) {
        reveal.snapTo(0f)
        reveal.animateTo(1f, tween(900))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background),
    ) {
        UiTopBar(title = "Flow Viz Pack II", subtitle = "Sankey, treemap, funnel & cohort", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            LabIntroCard(
                title = "Visualisasi alur & agregasi",
                body = "Empat grafik aliran: Sankey alur trafik, treemap proporsi halaman, funnel konversi, " +
                    "dan cohort retensi mingguan. Ketuk segmen untuk melihat rincian; tekan Putar ulang untuk mengulang animasi reveal.",
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChartTab.entries.forEach { t ->
                    val active = t == tab
                    Box(
                        modifier = Modifier
                            .background(
                                if (active) palette.primary else palette.surface,
                                RoundedCornerShape(999.dp),
                            )
                            .border(1.dp, if (active) palette.primary else palette.border, RoundedCornerShape(999.dp))
                            .clickable {
                                if (tab != t) {
                                    PerformHapticFeedback(HapticKind.SELECTION)
                                    tab = t
                                    selected = null
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text(
                            t.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (active) palette.onPrimary else palette.textSecondary,
                        )
                    }
                }
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
            ) {
                val density = LocalDensity.current
                val w = with(density) { maxWidth.toPx() }
                val h = with(density) { 260.dp.toPx() }

                val segments = remember(tab, w, h) { buildSegments(tab, w, h) }
                val p = reveal.value

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(palette.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(16.dp)),
                ) {
                    drawChart(tab, segments, series, p, selected?.id, size.width, size.height)
                }

                segments.forEach { seg ->
                    val (dw, dh) = with(density) {
                        seg.rect.width.toDp() to seg.rect.height.toDp()
                    }
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(seg.rect.left.roundToInt(), seg.rect.top.roundToInt()) }
                            .size(dw.coerceAtLeast(8.dp), dh.coerceAtLeast(14.dp))
                            .semantics { contentDescription = "Segmen ${seg.label}, nilai ${seg.value}" }
                            .clickable {
                                PerformHapticFeedback(HapticKind.SELECTION)
                                selected = if (selected?.id == seg.id) null else seg
                            },
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .background(palette.primary, RoundedCornerShape(10.dp))
                        .clickable {
                            PerformHapticFeedback(HapticKind.MEDIUM)
                            scope.launch {
                                reveal.snapTo(0f)
                                reveal.animateTo(1f, tween(900))
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text("Putar ulang", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.onPrimary)
                }
                Text(
                    "Animasi reveal: ${(reveal.value * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = palette.textSecondary,
                )
            }

            val sel = selected
            if (sel == null) {
                LabCard {
                    Text(
                        "Belum ada segmen terpilih — ketuk grafik untuk menampilkan tooltip.",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                }
            } else {
                LabCard {
                    Text(sel.label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    Text(
                        when (tab) {
                            ChartTab.SANKEY -> "Total alur masuk: ${sel.value} ribu sesi"
                            ChartTab.TREEMAP -> "Porsi tampilan: ${sel.value} dari 100"
                            ChartTab.FUNNEL -> "Nilai tahap: ${sel.value}% dari kunjungan"
                            ChartTab.COHORT -> "Retensi ${sel.value}% pada ${sel.label}"
                        },
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        "Warna segmen juga disertai label teks agar tidak bergantung warna saja.",
                        fontSize = 10.sp,
                        color = palette.textMuted,
                    )
                }
            }

            LabSectionTitle("Ringkasan data", "Legenda tiap grafik")

            LabCard {
                when (tab) {
                    ChartTab.SANKEY -> Text(
                        "Sumber: Iklan 60, Organik 38, Referral 20, Langsung 22 (ribu sesi). Tujuan: Web, Aplikasi, Chat.",
                        fontSize = 11.sp, color = palette.textSecondary,
                    )
                    ChartTab.TREEMAP -> Text(
                        "Beranda 34, Pencarian 22, Kategori 16, Promo 12, Akun 8, Bantuan 5, Lainnya 3 (porsi tampilan).",
                        fontSize = 11.sp, color = palette.textSecondary,
                    )
                    ChartTab.FUNNEL -> Text(
                        "Kunjungan 100% → Daftar 46% → Aktif 28% → Berlangganan 12% → Retensi 90 hari 7%.",
                        fontSize = 11.sp, color = palette.textSecondary,
                    )
                    ChartTab.COHORT -> Text(
                        "Lima cohort mingguan; kolom = minggu ke-N setelah gabung, sel bernilai 0 berarti data belum tersedia.",
                        fontSize = 11.sp, color = palette.textSecondary,
                    )
                }
            }
        }
    }
}

private fun buildSegments(tab: ChartTab, w: Float, h: Float): List<Seg> = when (tab) {
    ChartTab.SANKEY -> buildSankey(w, h)
    ChartTab.TREEMAP -> buildTreemap(w, h)
    ChartTab.FUNNEL -> buildFunnel(w, h)
    ChartTab.COHORT -> buildCohort(w, h)
}

private fun buildSankey(w: Float, h: Float): List<Seg> {
    val sources = SankeyFlows.map { it.from }.distinct()
    val targets = SankeyFlows.map { it.to }.distinct()
    val srcTotals = sources.map { s -> SankeyFlows.filter { it.from == s }.sumOf { it.value } }
    val dstTotals = targets.map { t -> SankeyFlows.filter { it.to == t }.sumOf { it.value } }
    val total = srcTotals.sum().toFloat()
    val gap = 8f
    val usable = h - gap * (sources.size - 1 + targets.size)
    val segs = mutableListOf<Seg>()
    var y = 0f
    sources.forEachIndexed { i, s ->
        val band = usable * (srcTotals[i] / total)
        segs += Seg("src-$s", s, srcTotals[i], Rect(0f, y, 12f, y + band), i)
        y += band + gap
    }
    y = 0f
    targets.forEachIndexed { i, t ->
        val band = usable * (dstTotals[i] / total)
        segs += Seg("dst-$t", t, dstTotals[i], Rect(w - 12f, y, w, y + band), (i + 2) % 6)
        y += band + gap
    }
    return segs
}

private fun buildTreemap(w: Float, h: Float): List<Seg> {
    val items = TreemapItems.sortedByDescending { it.second }
    val total = items.sumOf { it.second }.toFloat()
    val rects = mutableListOf<Pair<String, Long>>()
    val segs = mutableListOf<Seg>()

    fun split(
        list: List<Pair<String, Long>>,
        left: Float, top: Float, right: Float, bottom: Float,
        depth: Int,
    ) {
        if (list.isEmpty()) return
        if (list.size == 1) {
            val (label, value) = list[0]
            segs += Seg(
                "tm-$label", label, value,
                Rect(left + 2f, top + 2f, right - 2f, bottom - 2f),
                (depth * 2 + items.indexOfFirst { it.first == label }) % 6,
            )
            return
        }
        val sum = list.sumOf { it.second }
        var acc = 0L
        var idx = 0
        while (idx < list.size - 1 && acc + list[idx].second < sum / 2) {
            acc += list[idx].second
            idx++
        }
        if (acc == 0L) {
            acc = list[0].second
            idx = 1
        }
        val ratio = acc.toFloat() / sum
        val wide = right - left >= bottom - top
        if (wide) {
            val mid = left + (right - left) * ratio
            split(list.subList(0, idx), left, top, mid, bottom, depth + 1)
            split(list.subList(idx, list.size), mid, top, right, bottom, depth + 1)
        } else {
            val mid = top + (bottom - top) * ratio
            split(list.subList(0, idx), left, top, right, mid, depth + 1)
            split(list.subList(idx, list.size), left, mid, right, bottom, depth + 1)
        }
    }

    split(items, 0f, 0f, w, h, 0)
    segs.forEachIndexed { i, s -> segs[i] = s.copy(colorIdx = i % 6) }
    return segs
}

private fun buildFunnel(w: Float, h: Float): List<Seg> {
    val rows = FunnelStages.size
    val rowH = h / rows
    return FunnelStages.mapIndexed { i, (label, value) ->
        Seg(
            "fn-$label", label, value,
            Rect(0f, i * rowH + 3f, w * (value / 100f), (i + 1) * rowH - 3f),
            i,
        )
    }
}

private fun buildCohort(w: Float, h: Float): List<Seg> {
    val rows = CohortData.size
    val cols = CohortData[0].size
    val cell = minOf(w / cols, h / rows)
    val offX = (w - cell * cols) / 2f
    val segs = mutableListOf<Seg>()
    CohortData.forEachIndexed { r, row ->
        row.forEachIndexed { c, v ->
            if (v > 0) {
                segs += Seg(
                    "co-$r-$c", "Cohort ${r + 1} · minggu ${c + 1}", v.toLong(),
                    Rect(offX + c * cell + 2f, r * cell + 2f, offX + (c + 1) * cell - 2f, (r + 1) * cell - 2f),
                    c,
                )
            }
        }
    }
    return segs
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawChart(
    tab: ChartTab,
    segments: List<Seg>,
    series: List<Color>,
    progress: Float,
    selectedId: String?,
    w: Float,
    h: Float,
) {
    when (tab) {
        ChartTab.SANKEY -> {
            // Pita antar kolom: digambar sebagai kurva dari tiap sumber ke tujuan (perkiraan).
            val srcSegs = segments.filter { it.id.startsWith("src-") }
            val dstSegs = segments.filter { it.id.startsWith("dst-") }
            srcSegs.forEachIndexed { i, src ->
                val dst = dstSegs[i % dstSegs.size]
                val color = series[src.colorIdx].copy(alpha = 0.35f + 0.35f * progress)
                val path = Path().apply {
                    moveTo(src.rect.right, src.rect.top + src.rect.height * (1f - progress))
                    cubicTo(
                        w * 0.5f, src.rect.top,
                        w * 0.5f, dst.rect.top,
                        dst.rect.left, dst.rect.top + dst.rect.height * (1f - progress),
                    )
                    lineTo(dst.rect.left, dst.rect.bottom)
                    cubicTo(
                        w * 0.5f, dst.rect.bottom,
                        w * 0.5f, src.rect.bottom,
                        src.rect.right, src.rect.bottom,
                    )
                    close()
                }
                drawPath(path, color)
            }
            segments.forEach { seg ->
                val active = seg.id == selectedId
                drawRect(
                    color = series[seg.colorIdx].copy(alpha = if (active) 1f else 0.85f),
                    topLeft = Offset(seg.rect.left, seg.rect.top + seg.rect.height * (1f - progress)),
                    size = androidx.compose.ui.geometry.Size(seg.rect.width, seg.rect.height * progress),
                )
                if (active) {
                    drawRect(
                        color = series[seg.colorIdx],
                        topLeft = Offset(seg.rect.left, seg.rect.top + seg.rect.height * (1f - progress)),
                        size = androidx.compose.ui.geometry.Size(seg.rect.width, seg.rect.height * progress),
                        style = Stroke(width = 3f),
                    )
                }
            }
        }
        ChartTab.TREEMAP -> segments.forEach { seg ->
            val active = seg.id == selectedId
            drawRect(
                color = series[seg.colorIdx].copy(alpha = if (active) 0.95f else 0.55f * progress + 0.15f),
                topLeft = Offset(seg.rect.left, seg.rect.top),
                size = androidx.compose.ui.geometry.Size(seg.rect.width * progress, seg.rect.height),
            )
            if (active) {
                drawRect(
                    color = series[seg.colorIdx],
                    topLeft = Offset(seg.rect.left, seg.rect.top),
                    size = androidx.compose.ui.geometry.Size(seg.rect.width * progress, seg.rect.height),
                    style = Stroke(width = 3f),
                )
            }
        }
        ChartTab.FUNNEL -> segments.forEach { seg ->
            val active = seg.id == selectedId
            drawRect(
                color = series[seg.colorIdx].copy(alpha = if (active) 0.95f else 0.6f),
                topLeft = Offset(0f, seg.rect.top),
                size = androidx.compose.ui.geometry.Size(seg.rect.width * progress, seg.rect.height),
            )
        }
        ChartTab.COHORT -> segments.forEachIndexed { i, seg ->
            val appear = (i.toFloat() / segments.size) < progress
            if (appear) {
                val active = seg.id == selectedId
                drawRect(
                    color = series[0].copy(alpha = (seg.value / 100f * 0.85f + 0.05f).coerceIn(0.05f, 0.9f)),
                    topLeft = Offset(seg.rect.left, seg.rect.top),
                    size = androidx.compose.ui.geometry.Size(seg.rect.width, seg.rect.height),
                )
                if (active) {
                    drawRect(
                        color = series[3],
                        topLeft = Offset(seg.rect.left, seg.rect.top),
                        size = androidx.compose.ui.geometry.Size(seg.rect.width, seg.rect.height),
                        style = Stroke(width = 3f),
                    )
                }
            }
        }
    }
}
