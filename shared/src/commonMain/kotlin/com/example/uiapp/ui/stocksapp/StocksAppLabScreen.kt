package com.example.uiapp.ui.stocksapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
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
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import kotlin.random.Random
import kotlinx.coroutines.delay

private enum class ChartRange(val label: String, val points: Int) {
    D1("1D", 48),
    M1("1M", 30),
    M3("3M", 45),
    M6("6M", 60),
    Y1("1Y", 72),
    Y2("2Y", 84),
    Y5("5Y", 100),
}

private enum class LoadState { READY, LOADING, ERROR }

private data class Stock(
    val id: Int,
    val symbol: String,
    val name: String,
    val price: Float,
    val changePct: Float,
)

private data class NewsItem(val id: Int, val title: String, val source: String, val time: String, val summary: String)

private data class ChartSeries(val values: List<Float>, val labels: List<String>, val min: Float, val max: Float)

private val mockStocks = listOf(
    Stock(1, "BBCA", "Bank Central Asia", 9750f, 1.24f),
    Stock(2, "TLKM", "Telkom Indonesia", 2980f, -0.67f),
    Stock(3, "ASII", "Astra International", 4525f, 0.89f),
    Stock(4, "BBRI", "Bank Rakyat Indonesia", 4210f, -1.13f),
    Stock(5, "UNVR", "Unilever Indonesia", 2310f, 0.35f),
    Stock(6, "GOTO", "GoTo Gojek Tokopedia", 68f, -2.17f),
    Stock(7, "ANTM", "Aneka Tambang", 1885f, 2.06f),
)

private val mockNews = mapOf(
    1 to listOf(
        NewsItem(1, "BCA Catat Pertumbuhan Laba Dua Digit", "Kontan Bisnis", "2 jam lalu", "Kinerja kuartalan mengalahkan ekspektasi analis dengan pertumbuhan kredit yang sehat."),
        NewsItem(2, "Ekspansi Layanan Digital Perbankan Dipercepat", "Nusantara Daily", "5 jam lalu", "Aplikasi mobile banking menambah fitur investasi terpadu untuk nasabah retail."),
        NewsItem(3, "Sektor Perbankan Masih Jadi Favorit Investor", "Pasar Pagi", "Kemarin", "Aliran dana asing tercatat masuk ke saham bank berkapitalisasi besar."),
    ),
    2 to listOf(
        NewsItem(4, "Telkom Perluas Jaringan Fiber ke Wilayah Timur", "Kontan Bisnis", "3 jam lalu", "Investasi infrastruktur ditargetkan selesai akhir tahun dengan cakupan lebih luas."),
        NewsItem(5, "Pendapatan Data Tumbuh Stabil", "Nusantara Daily", "Kemarin", "ARPU membaik meski persaingan tarif di segmen seluler masih ketat."),
    ),
    3 to listOf(
        NewsItem(6, "Astra Luncurkan Lini Kendaraan Listrik", "Pasar Pagi", "1 jam lalu", "Peluncuran model pertama menandai masuknya perseroan ke era elektrifikasi."),
        NewsItem(7, "Dividen Interim Dibagikan Tahun Ini", "Kontan Bisnis", "Kemarin", "Rasio pembayaran dividen dipertahankan di kisaran 50 persen."),
    ),
    4 to listOf(
        NewsItem(8, "BRI Tambah Kredit UMKM Hijau", "Nusantara Daily", "4 jam lalu", "Program pembiayaan berkelanjutan menyasar pelaku usaha kecil di daerah."),
        NewsItem(9, "Kualitas Aset Terjaga di Tengah Perlambatan", "Pasar Pagi", "Kemarin", "Rasio NPL turun tipis berkat pengetatan manajemen risiko."),
    ),
    5 to listOf(
        NewsItem(10, "Unilever Perluas Pabrik Produksi Baru", "Kontan Bisnis", "6 jam lalu", "Kapasitas produksi ditambah untuk memenuhi permintaan domestik."),
    ),
    6 to listOf(
        NewsItem(11, "GoTo Fokus Pada Jalan Menuju Profitabilitas", "Nusantara Daily", "2 jam lalu", "Efisiensi biaya operasional mulai menunjukkan hasil pada segmen on-demand."),
        NewsItem(12, "Volume Transaksi Fintech Naik Kuartal Ini", "Pasar Pagi", "Kemarin", "Ekosistem pembayaran digital mencatat rekor nilai transaksi baru."),
    ),
    7 to listOf(
        NewsItem(13, "Harga Komoditas Logam Menguat", "Kontan Bisnis", "1 jam lalu", "Kenaikan harga nikel dan emas menopang prospek pendapatan perseroan."),
        NewsItem(14, "Antam Tambah Kapasitas Smelter", "Nusantara Daily", "Kemarin", "Investasi hilirisasi sejalan dengan program pemerintah."),
    ),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StocksAppLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var selectedId by remember { mutableIntStateOf(mockStocks.first().id) }
    var range by remember { mutableStateOf(ChartRange.D1) }
    var loadState by remember { mutableStateOf(LoadState.LOADING) }
    var failNext by remember { mutableStateOf(false) }
    var marketOpen by remember { mutableStateOf(true) }
    var reloadToken by remember { mutableIntStateOf(0) }
    var crosshairIdx by remember { mutableStateOf<Int?>(null) }

    val stock = remember(selectedId) { mockStocks.first { it.id == selectedId } }

    // Simulasi pemuatan kuotasi.
    LaunchedEffect(reloadToken) {
        loadState = LoadState.LOADING
        crosshairIdx = null
        delay(1200)
        loadState = if (failNext) LoadState.ERROR else LoadState.READY
    }

    val series = remember(stock, range) { buildSeries(stock, range) }
    val news = remember(stock) { mockNews[stock.id].orEmpty() }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Apple Stocks",
                subtitle = "Klon UI, kuotasi simulasi",
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
                title = "Klon UI Apple Stocks — bukan data pasar nyata",
                body = "Seluruh kuotasi, grafik, dan berita di lab ini adalah SIMULASI dari data mock lokal yang deterministik. " +
                    "Tidak ada koneksi ke bursa, broker, atau layanan data pasar mana pun.",
            )

            // Toolbar: status bursa + perbarui
            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Status Bursa", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.textMuted)
                        Text(
                            text = if (marketOpen) "Bursa Buka" else "Bursa Tutup",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (marketOpen) palette.success else palette.textSecondary,
                        )
                    }
                    StatusPill(
                        text = if (marketOpen) "Buka · Simulasi" else "Tutup · Simulasi",
                        color = if (marketOpen) palette.success else palette.textMuted,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StocksChip(
                        label = if (marketOpen) "Tutup bursa" else "Buka bursa",
                        selected = false,
                        modifier = Modifier.weight(1f),
                        onClick = { marketOpen = !marketOpen },
                    )
                    StocksChip(
                        label = if (failNext) "Galat berikutnya: AKTIF" else "Simulasikan galat",
                        selected = failNext,
                        modifier = Modifier.weight(1f),
                        onClick = { failNext = !failNext },
                    )
                    StocksChip(
                        label = if (loadState == LoadState.LOADING) "Memuat…" else "Perbarui",
                        selected = false,
                        enabled = loadState != LoadState.LOADING,
                        modifier = Modifier.weight(1f),
                        onClick = { reloadToken++ },
                    )
                }
            }

            when (loadState) {
                LoadState.LOADING -> LoadingQuotesCard()
                LoadState.ERROR -> ErrorQuoteCard(onRetry = { reloadToken++ })
                LoadState.READY -> {
                    // Kartu chart dengan crosshair
                    LabCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            LabSectionTitle(title = stock.symbol, subtitle = stock.name)
                            ChangePill(changePct = stock.changePct)
                        }
                        Text(
                            text = formatPrice(stock.price),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = if (!marketOpen) "Bursa tutup — harga terakhir (Simulasi)" else "Kuotasi simulasi — bukan data pasar nyata",
                            fontSize = 10.sp,
                            color = palette.textMuted,
                        )

                        val idx = crosshairIdx
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = if (idx == null) {
                                    "Ketuk atau seret grafik untuk mengaktifkan crosshair"
                                } else {
                                    val v = series.values[idx]
                                    val delta = ((v - stock.price) / stock.price) * 100f
                                    "${series.labels[idx]} — ${formatPrice(v)} (${formatPct(delta)})"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (idx == null) palette.textMuted else palette.textPrimary,
                                modifier = Modifier.padding(10.dp),
                            )
                        }

                        StockChart(
                            series = series,
                            rising = stock.changePct >= 0f,
                            crosshairIdx = crosshairIdx,
                            onCrosshair = { crosshairIdx = it },
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            ChartRange.entries.forEach { r ->
                                StocksChip(
                                    label = r.label,
                                    selected = range == r,
                                    onClick = {
                                        range = r
                                        crosshairIdx = null
                                    },
                                )
                            }
                        }
                    }

                    // Watchlist
                    LabCard {
                        LabSectionTitle(title = "Watchlist", subtitle = "Pilih baris untuk memuat grafik & berita")
                        mockStocks.forEach { item ->
                            WatchlistRow(
                                stock = item,
                                selected = item.id == selectedId,
                                marketOpen = marketOpen,
                                onClick = {
                                    selectedId = item.id
                                    crosshairIdx = null
                                },
                            )
                        }
                    }

                    // Berita
                    LabCard {
                        LabSectionTitle(title = "Berita ${stock.symbol}", subtitle = "Artikel mock lokal (Simulasi)")
                        news.forEach { item ->
                            NewsCard(item = item)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun StockChart(
    series: ChartSeries,
    rising: Boolean,
    crosshairIdx: Int?,
    onCrosshair: (Int?) -> Unit,
) {
    val palette = LocalAppPalette.current
    var chartSize by remember { mutableStateOf(IntSize.Zero) }
    val lineColor = if (rising) palette.success else palette.danger
    val linePath = remember(chartSize, series) { buildLinePath(series.values, series.min, series.max, chartSize) }
    val areaPath = remember(chartSize, series) { buildAreaPath(series.values, series.min, series.max, chartSize) }
    val areaBrush = remember(lineColor) {
        Brush.verticalGradient(listOf(lineColor.copy(alpha = 0.22f), lineColor.copy(alpha = 0.02f)))
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .onSizeChanged { chartSize = it }
            .pointerInput(series) {
                detectTapGestures { pos ->
                    onCrosshair(indexFromX(pos.x, size.width, series.values.size))
                }
            }
            .pointerInput(series) {
                detectDragGestures(
                    onDragStart = { pos -> onCrosshair(indexFromX(pos.x, size.width, series.values.size)) },
                    onDragEnd = { onCrosshair(null) },
                    onDragCancel = { onCrosshair(null) },
                    onDrag = { change, _ -> onCrosshair(indexFromX(change.position.x, size.width, series.values.size)) },
                )
            },
    ) {
        drawPath(path = areaPath, brush = areaBrush)
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        val idx = crosshairIdx
        if (idx != null && idx in series.values.indices && series.values.size > 1) {
            val x = size.width * idx / (series.values.size - 1)
            val y = mapValueToY(series.values[idx], series.min, series.max, size.height)
            drawLine(
                color = palette.textMuted,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = palette.textMuted,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.5f,
                cap = StrokeCap.Round,
            )
            drawCircle(color = lineColor, radius = 6f, center = Offset(x, y))
            drawCircle(color = palette.surface, radius = 6f, center = Offset(x, y), style = Stroke(width = 2f))
        }
    }
}

@Composable
private fun WatchlistRow(stock: Stock, selected: Boolean, marketOpen: Boolean, onClick: () -> Unit) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) palette.surfaceMuted else palette.surface)
            .border(1.dp, if (selected) palette.primary else palette.border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .semantics {
                role = Role.Button
                contentDescription = "Pilih saham ${stock.name}, harga ${formatPrice(stock.price)}"
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(stock.symbol, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
            Text(
                text = stock.name,
                fontSize = 11.sp,
                color = palette.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!marketOpen) {
                Text("Bursa tutup — harga terakhir", fontSize = 9.sp, color = palette.textMuted)
            }
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(formatPrice(stock.price), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
            ChangePill(changePct = stock.changePct)
        }
    }
}

@Composable
private fun ChangePill(changePct: Float) {
    val palette = LocalAppPalette.current
    val up = changePct >= 0f
    val tone = if (up) palette.success else palette.danger
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = tone.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, tone),
    ) {
        Text(
            text = if (up) "▲ Naik ${formatPct(changePct)}" else "▼ Turun ${formatPct(-changePct)}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = tone,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun NewsCard(item: NewsItem) {
    val palette = LocalAppPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, lineHeight = 18.sp)
        Text(
            text = "${item.source} · ${item.time}",
            fontSize = 10.sp,
            color = palette.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(item.summary, fontSize = 11.sp, color = palette.textSecondary, lineHeight = 16.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun StocksChip(
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
private fun LoadingQuotesCard() {
    val palette = LocalAppPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Memuat kuotasi…", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
        Text("Simulasi pemuatan — bukan data pasar nyata", fontSize = 11.sp, color = palette.textMuted)
        repeat(4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(10.dp)),
                )
                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(10.dp)),
                )
            }
        }
    }
}

@Composable
private fun ErrorQuoteCard(onRetry: () -> Unit) {
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
        Text("Tidak dapat memuat kuotasi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.danger)
        Text(
            text = "Permintaan kuotasi simulasi gagal. Periksa lalu coba perbarui kembali.",
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

private fun buildSeries(stock: Stock, range: ChartRange): ChartSeries {
    val random = Random(stock.id * 31 + range.ordinal)
    val n = range.points
    val values = ArrayList<Float>(n)
    var current = stock.price * (0.9f + random.nextFloat() * 0.08f)
    repeat(n) {
        current *= 1f + (random.nextFloat() - 0.48f) * 0.02f
        values.add(current)
    }
    values[values.size - 1] = stock.price
    val labels = ArrayList<String>(n)
    for (i in 0 until n) {
        labels.add(labelFor(range, i, n))
    }
    var min = values.min()
    var max = values.max()
    if (max - min < 1f) {
        min -= 1f
        max += 1f
    }
    return ChartSeries(values = values, labels = labels, min = min, max = max)
}

private fun labelFor(range: ChartRange, i: Int, n: Int): String = when (range) {
    ChartRange.D1 -> {
        val hour = 9 + (i * 15 / 60)
        val minute = (i * 15) % 60
        val hh = if (hour < 10) "0$hour" else "$hour"
        val mm = if (minute < 10) "0$minute" else "$minute"
        "$hh.$mm"
    }
    else -> "H-${n - i}"
}

private fun mapValueToY(value: Float, min: Float, max: Float, height: Float): Float {
    val frac = ((value - min) / (max - min)).coerceIn(0f, 1f)
    return height - frac * height * 0.84f - height * 0.08f
}

private fun indexFromX(x: Float, width: Int, count: Int): Int? {
    if (width <= 0 || count <= 1) return null
    return ((x / width) * (count - 1)).toInt().coerceIn(0, count - 1)
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

private fun buildAreaPath(values: List<Float>, min: Float, max: Float, size: IntSize): Path {
    val path = Path()
    if (size.width <= 0 || size.height <= 0 || values.size < 2) return path
    val w = size.width.toFloat()
    val h = size.height.toFloat()
    path.moveTo(0f, h)
    values.forEachIndexed { i, value ->
        val x = w * i / (values.size - 1)
        val y = mapValueToY(value, min, max, h)
        path.lineTo(x, y)
    }
    path.lineTo(w, h)
    path.close()
    return path
}

private fun formatPrice(value: Float): String {
    val intPart = value.toInt()
    val digits = intPart.toString()
    val sb = StringBuilder()
    for (i in digits.indices) {
        if (i > 0 && (digits.length - i) % 3 == 0) sb.append('.')
        sb.append(digits[i])
    }
    return sb.toString()
}

private fun formatPct(value: Float): String {
    val tenths = (value * 10).toInt()
    val sign = if (tenths < 0) "-" else ""
    val absTenths = if (tenths < 0) -tenths else tenths
    return "$sign${absTenths / 10},${absTenths % 10}%"
}
