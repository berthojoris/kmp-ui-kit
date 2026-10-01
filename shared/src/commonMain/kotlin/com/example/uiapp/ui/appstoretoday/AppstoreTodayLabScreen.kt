package com.example.uiapp.ui.appstoretoday

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.AppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private const val SCREENSHOT_COUNT = 4
private const val FAIL_ONCE_APP_ID = 3

private enum class StoreTab(val label: String) {
    TODAY("Hari Ini"),
    GAMES("Game"),
    APPS("Aplikasi"),
    ARCADE("Arcade"),
    SEARCH("Cari"),
}

private enum class GetState(val label: String) {
    SIAP("GET"),
    MENGUNDUH("Mengunduh"),
    TERPASANG("BUKA"),
    GAGAL("Gagal"),
}

private data class StoreApp(
    val id: Int,
    val name: String,
    val developer: String,
    val category: String,
    val tagline: String,
    val description: String,
    val rating: Float,
    val ratingsLabel: String,
    val histogram: List<Int>,
    val version: String,
    val versionNotes: String,
    val sizeLabel: String,
    val ageLabel: String,
    val rank: Int,
)

private data class EditorialStory(
    val id: Int,
    val kicker: String,
    val title: String,
    val subtitle: String,
    val appId: Int,
)

private val STORE_APPS = listOf(
    StoreApp(
        id = 1,
        name = "Kopi Senja: Resep Harian",
        developer = "Studio Rasa",
        category = "Makanan & Minuman",
        tagline = "Resep kopi rumahan tiap pagi",
        description = "Kumpulan resep kopi dan teh dengan panduan langkah demi langkah, catatan biji favorit, " +
            "dan pengingat seduh harian. Seluruh konten demo ini lokal tanpa unduhan dari internet.",
        rating = 4.8f,
        ratingsLabel = "12,4 rb",
        histogram = listOf(9800, 1800, 520, 180, 120),
        version = "Versi 3.2.1",
        versionNotes = "• Tiga resep dingin baru untuk musim kemarau\n• Perbaikan catatan seduh yang hilang\n• Mode gelap lebih nyaman",
        sizeLabel = "48,2 MB",
        ageLabel = "4+",
        rank = 1,
    ),
    StoreApp(
        id = 2,
        name = "Jejak Langkah: Pedometer",
        developer = "Nadi Studio",
        category = "Kesehatan & Kebugaran",
        tagline = "Hitung langkah harian tanpa sensor aneh",
        description = "Pedometer demo dengan target langkah, ringkasan mingguan, dan lencana pencapaian. " +
            "Tidak membaca sensor perangkat — seluruh angka adalah simulasi.",
        rating = 4.5f,
        ratingsLabel = "3,1 rb",
        histogram = listOf(2100, 620, 240, 90, 60),
        version = "Versi 1.9.0",
        versionNotes = "• Target langkah mingguan\n• Lencana pencapaian baru\n• Ringkasan lebih ringkas",
        sizeLabel = "21,7 MB",
        ageLabel = "4+",
        rank = 2,
    ),
    StoreApp(
        id = 3,
        name = "Bahasa Cepat: Kamus Visual",
        developer = "Kata Studio",
        category = "Pendidikan",
        tagline = "Kamus gambar harian lima kata",
        description = "Kamus visual berisi kartu kata bergambar, latihan pendengaran, dan ulangan harian. " +
            "Konten demo, tanpa layanan terjemahan nyata.",
        rating = 4.2f,
        ratingsLabel = "870",
        histogram = listOf(410, 240, 130, 55, 35),
        version = "Versi 2.4.0",
        versionNotes = "• 120 kartu kata baru\n• Latihan pengucapan sederhana\n• Perbaikan tata letak tablet",
        sizeLabel = "63,9 MB",
        ageLabel = "9+",
        rank = 5,
    ),
    StoreApp(
        id = 4,
        name = "Layar Fokus: Blokir Distraksi",
        developer = "Tenang Apps",
        category = "Produktivitas",
        tagline = "Sesi fokus dengan laporan sederhana",
        description = "Atur sesi fokus, daftar aplikasi yang dibisukan, dan lihat laporan mingguan. " +
            "Pemblokiran aplikasi disimulasikan untuk keperluan demo UI.",
        rating = 4.7f,
        ratingsLabel = "5,6 rb",
        histogram = listOf(4100, 900, 340, 160, 100),
        version = "Versi 5.0.2",
        versionNotes = "• Sesi fokus berantai\n• Laporan mingguan baru\n• Perbaikan pengingat",
        sizeLabel = "15,3 MB",
        ageLabel = "4+",
        rank = 3,
    ),
    StoreApp(
        id = 5,
        name = "Nada Petang: Piano Santai",
        developer = "Gemala Audio",
        category = "Musik",
        tagline = "Piano tenang untuk menemani senja",
        description = "Aplikasi piano sederhana dengan koleksi nada petang, mode relaksasi, dan rekaman main sendiri. " +
            "Semua nada dihasilkan lokal di perangkat demo.",
        rating = 4.9f,
        ratingsLabel = "21,0 rb",
        histogram = listOf(18000, 2100, 600, 200, 100),
        version = "Versi 4.1.3",
        versionNotes = "• Dua set nada petang baru\n• Rekaman lebih panjang\n• Latensi sentuh diperbaiki",
        sizeLabel = "88,4 MB",
        ageLabel = "4+",
        rank = 4,
    ),
    StoreApp(
        id = 6,
        name = "Tanaman Mini: Rawat Harian",
        developer = "Kebun Kita",
        category = "Gaya Hidup",
        tagline = "Rawat tanaman kecil di meja kerja",
        description = "Catatan penyiraman, jadwal pupuk, dan galeri pertumbuhan tanaman mini. " +
            "Foto tanaman diganti ilustrasi vektor untuk demo ini.",
        rating = 4.4f,
        ratingsLabel = "1,2 rb",
        histogram = listOf(720, 310, 110, 45, 30),
        version = "Versi 1.2.0",
        versionNotes = "• Jadwal pupuk otomatis\n• Galeri pertumbuhan\n• Perbaikan notifikasi",
        sizeLabel = "32,0 MB",
        ageLabel = "4+",
        rank = 6,
    ),
)

private val EDITORIAL_STORIES = listOf(
    EditorialStory(1, "KISAH HARI INI", "Senja dalam Cangkir", "Cara sederhana menyeduh kopi rumahan", 1),
    EditorialStory(2, "PILIHAN EDITOR", "Langkah Kecil Tiap Hari", "Membangun rutinitas bergerak tanpa paksaan", 2),
    EditorialStory(3, "KREATIFITAS", "Nada untuk Waktu Tenang", "Piano santai yang menemani sore", 5),
)

private val GENRE_CHIPS = listOf("Semua", "Kasual", "Edukasi", "Produktivitas", "Musik", "Gaya Hidup")
private val TRENDING_SEARCHES = listOf("kopi", "fokus", "piano", "kamus", "tanaman", "pedometer")

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppstoreTodayLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var tab by remember { mutableStateOf(StoreTab.TODAY) }
    var openAppId by remember { mutableStateOf<Int?>(null) }
    val getStates = remember { mutableStateMapOf<Int, GetState>() }
    var downloadingId by remember { mutableStateOf<Int?>(null) }
    var progress by remember { mutableIntStateOf(0) }
    val failedOnce = remember { mutableStateListOf<Int>() }
    var searchQuery by remember { mutableStateOf("") }
    var arcadeState by remember { mutableStateOf("Coba Gratis 1 Bulan") }

    fun stateOf(appId: Int): GetState = getStates[appId] ?: GetState.SIAP

    fun startDownload(appId: Int) {
        if (downloadingId != null) return
        progress = 0
        getStates[appId] = GetState.MENGUNDUH
        downloadingId = appId
    }

    // Satu unduhan berjalan pada satu waktu: progress -> terpasang, atau gagal sekali untuk app #3.
    LaunchedEffect(downloadingId) {
        val id = downloadingId
        if (id != null) {
            while (progress < 100) {
                delay(60)
                progress = (progress + 4).coerceAtMost(100)
                if (id == FAIL_ONCE_APP_ID && progress >= 56 && !failedOnce.contains(id)) {
                    failedOnce.add(id)
                    getStates[id] = GetState.GAGAL
                    downloadingId = null
                    return@LaunchedEffect
                }
            }
            getStates[id] = GetState.TERPASANG
            downloadingId = null
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "App Store Today & Product",
                subtitle = "Kartu editorial, pager screenshot",
                onBack = onBack,
                action = { MockBadge() },
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (tab) {
                        StoreTab.TODAY -> TodayContent(
                            stateOf = ::stateOf,
                            downloadingId = downloadingId,
                            onGet = { startDownload(it) },
                            onOpenApp = { openAppId = it },
                        )
                        StoreTab.GAMES -> GamesContent(
                            stateOf = ::stateOf,
                            downloadingId = downloadingId,
                            onGet = { startDownload(it) },
                            onOpenApp = { openAppId = it },
                        )
                        StoreTab.APPS -> AppsContent(
                            stateOf = ::stateOf,
                            downloadingId = downloadingId,
                            onGet = { startDownload(it) },
                            onOpenApp = { openAppId = it },
                        )
                        StoreTab.ARCADE -> ArcadeContent(
                            arcadeState = arcadeState,
                            onSubscribe = {
                                if (arcadeState != "Memproses…") {
                                    arcadeState = "Memproses…"
                                }
                            },
                        )
                        StoreTab.SEARCH -> SearchContent(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            stateOf = ::stateOf,
                            downloadingId = downloadingId,
                            onGet = { startDownload(it) },
                            onOpenApp = { openAppId = it },
                        )
                    }

                    val openId = openAppId
                    if (openId != null) {
                        val app = STORE_APPS.firstOrNull { it.id == openId }
                        if (app != null) {
                            ProductOverlay(
                                app = app,
                                state = stateOf(app.id),
                                progress = progress,
                                busyWithOther = downloadingId != null && downloadingId != app.id,
                                onGet = { startDownload(app.id) },
                                onClose = { openAppId = null },
                            )
                        }
                    }
                }

                StoreTabBar(
                    selected = tab,
                    onSelect = { tab = it },
                )
            }
        }
    }

    // Proses langganan Arcade: loading -> sukses (disabled sesudahnya).
    LaunchedEffect(arcadeState) {
        if (arcadeState == "Memproses…") {
            delay(1400)
            arcadeState = "Langganan aktif — Simulasi"
        }
    }
}

@Composable
private fun TodayContent(
    stateOf: (Int) -> GetState,
    downloadingId: Int?,
    onGet: (Int) -> Unit,
    onOpenApp: (Int) -> Unit,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            Text("Selasa, 30 September", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textMuted)
            Text(
                "Hari Ini",
                fontSize = 30.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
            )
        }
        EditorialStack(onOpenApp = onOpenApp)
        LabSectionTitle(title = "Top Gratis", subtitle = "Peringkat aplikasi minggu ini")
        STORE_APPS.sortedBy { it.rank }.forEachIndexed { index, app ->
            RankedRow(
                app = app,
                rank = index + 1,
                onOpenApp = onOpenApp,
                state = stateOf(app.id),
                enabled = downloadingId == null || downloadingId == app.id,
                onGet = { onGet(app.id) },
            )
        }
    }
}

@Composable
private fun EditorialStack(onOpenApp: (Int) -> Unit) {
    val palette = LocalAppPalette.current
    var storyIndex by remember { mutableIntStateOf(0) }
    val story = EDITORIAL_STORIES[storyIndex % EDITORIAL_STORIES.size]
    val app = STORE_APPS.firstOrNull { it.id == story.appId } ?: STORE_APPS[0]

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(264.dp)) {
            // Dua kartu kecil menyorot di belakang (kartu editorial bertumpuk).
            for (backIdx in 2 downTo 1) {
                val behind = EDITORIAL_STORIES[(storyIndex + backIdx) % EDITORIAL_STORIES.size]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = (backIdx * 14).dp, start = (backIdx * 6).dp, end = (backIdx * 6).dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = behind.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            EditorialBanner(
                story = story,
                app = app,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
                    .height(232.dp),
                onClick = { onOpenApp(story.appId) },
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PillButton(
                label = "Cerita Berikutnya",
                onClick = { storyIndex = (storyIndex + 1) % EDITORIAL_STORIES.size },
                modifier = Modifier.weight(1f),
            )
            PillButton(
                label = "Buka Produk",
                onClick = { onOpenApp(story.appId) },
                modifier = Modifier.weight(1f),
                accent = true,
            )
        }
    }
}

@Composable
private fun EditorialBanner(
    story: EditorialStory,
    app: StoreApp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val accent = accentFor(palette, app.id)
    val brush = remember(app.id) {
        Brush.linearGradient(
            colors = listOf(accent.copy(alpha = 0.24f), accent.copy(alpha = 0.06f)),
        )
    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, palette.border),
        modifier = modifier
            .clickable { onClick() }
            .semantics {
                role = Role.Button
                contentDescription = "Kartu editorial " + story.title + ", buka halaman produk " + app.name
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
                .padding(18.dp),
        ) {
            DecorShapes(tint = accent)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(story.kicker, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = accent, letterSpacing = 1.2.sp)
                Text(
                    text = story.title,
                    fontSize = 26.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = story.subtitle,
                    fontSize = 13.sp,
                    color = palette.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    AppIcon(app = app, size = 44.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = app.tagline,
                            fontSize = 11.sp,
                            color = palette.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    GetButtonSmall(state = GetState.SIAP, enabled = false, progress = 0, onClick = {})
                }
            }
        }
    }
}

@Composable
private fun DecorShapes(tint: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = this.size.width
        val h = this.size.height
        drawCircle(color = tint.copy(alpha = 0.35f), radius = w * 0.22f, center = Offset(w * 0.86f, h * 0.16f))
        drawArc(
            color = tint.copy(alpha = 0.30f),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.55f, h * 0.62f),
            size = Size(w * 0.4f, h * 0.5f),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
        )
        drawLine(
            color = tint.copy(alpha = 0.35f),
            start = Offset(w * 0.05f, h * 0.88f),
            end = Offset(w * 0.34f, h * 0.88f),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun GamesContent(
    stateOf: (Int) -> GetState,
    downloadingId: Int?,
    onGet: (Int) -> Unit,
    onOpenApp: (Int) -> Unit,
) {
    val palette = LocalAppPalette.current
    var chipIndex by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Game", fontSize = 28.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = palette.textPrimary)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GENRE_CHIPS.forEachIndexed { index, chip ->
                FilterChipPill(
                    label = chip,
                    selected = index == chipIndex,
                    onClick = { chipIndex = index },
                )
            }
        }
        LabSectionTitle(title = "Game Unggulan", subtitle = "Kurasi minggu ini — konten mock")
        STORE_APPS.take(3).forEach { app ->
            FeaturedRow(app = app, stateOf = stateOf, downloadingId = downloadingId, onGet = onGet, onOpenApp = onOpenApp)
        }
    }
}

@Composable
private fun AppsContent(
    stateOf: (Int) -> GetState,
    downloadingId: Int?,
    onGet: (Int) -> Unit,
    onOpenApp: (Int) -> Unit,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Aplikasi", fontSize = 28.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = palette.textPrimary)
        LabSectionTitle(title = "Top Gratis", subtitle = "Peringkat dengan tombol GET/BUKA")
        STORE_APPS.sortedBy { it.rank }.forEachIndexed { index, app ->
            RankedRow(
                app = app,
                rank = index + 1,
                onOpenApp = onOpenApp,
                state = stateOf(app.id),
                enabled = downloadingId == null || downloadingId == app.id,
                onGet = { onGet(app.id) },
            )
        }
    }
}

@Composable
private fun ArcadeContent(arcadeState: String, onSubscribe: () -> Unit) {
    val palette = LocalAppPalette.current
    val brush = remember {
        Brush.linearGradient(
            colors = listOf(palette.info.copy(alpha = 0.22f), palette.primary.copy(alpha = 0.10f)),
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Arcade", fontSize = 28.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = palette.textPrimary)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(modifier = Modifier.fillMaxWidth().background(brush).padding(18.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Uji Coba Langganan", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = palette.info, letterSpacing = 1.1.sp)
                    Text(
                        "Mainkan Semua Tanpa Iklan",
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        "Langganan game simulasi — tanpa penagihan nyata dan tanpa konten unduhan.",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                    PillButton(
                        label = arcadeState,
                        onClick = { if (arcadeState == "Coba Gratis 1 Bulan") onSubscribe() },
                        modifier = Modifier.fillMaxWidth(),
                        accent = arcadeState == "Coba Gratis 1 Bulan",
                        enabled = arcadeState == "Coba Gratis 1 Bulan",
                    )
                }
            }
        }
        LabSectionTitle(title = "Pilihan Arcade", subtitle = "Tiga game contoh")
        STORE_APPS.take(3).forEach { app ->
            FeaturedRow(app = app, stateOf = { GetState.SIAP }, downloadingId = null, onGet = {}, onOpenApp = {})
        }
    }
}

@Composable
private fun SearchContent(
    query: String,
    onQueryChange: (String) -> Unit,
    stateOf: (Int) -> GetState,
    downloadingId: Int?,
    onGet: (Int) -> Unit,
    onOpenApp: (Int) -> Unit,
) {
    val palette = LocalAppPalette.current
    val results = remember(query) {
        if (query.isBlank()) {
            emptyList()
        } else {
            STORE_APPS.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.developer.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Cari", fontSize = 28.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = palette.textPrimary)

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(fontSize = 14.sp, color = palette.textPrimary),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                ) {
                    if (query.isEmpty()) {
                        Text("Cari aplikasi & game", fontSize = 14.sp, color = palette.textMuted)
                    }
                    innerTextField()
                }
            },
        )

        if (query.isBlank()) {
            LabSectionTitle(title = "Pencarian Populer", subtitle = "Ketuk untuk mengisi kolom pencarian")
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TRENDING_SEARCHES.forEach { word ->
                    FilterChipPill(label = word, selected = false, onClick = { onQueryChange(word) })
                }
            }
        } else if (results.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    SearchGlyph(tint = palette.textMuted, size = 32.dp)
                    Text("Tidak ada hasil", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    Text(
                        "Tidak ada aplikasi yang cocok dengan \"" + query + "\".",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                }
            }
        } else {
            results.forEachIndexed { index, app ->
                RankedRow(
                    app = app,
                    rank = index + 1,
                    onOpenApp = onOpenApp,
                    state = stateOf(app.id),
                    enabled = downloadingId == null || downloadingId == app.id,
                    onGet = { onGet(app.id) },
                )
            }
        }
    }
}

@Composable
private fun ProductOverlay(
    app: StoreApp,
    state: GetState,
    progress: Int,
    busyWithOther: Boolean,
    onGet: () -> Unit,
    onClose: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val pagerState = rememberPagerState(pageCount = { SCREENSHOT_COUNT })
    val accent = accentFor(palette, app.id)

    Surface(
        color = palette.background,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = app.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                )
                PillButton(label = "Tutup", onClick = onClose, modifier = Modifier.width(88.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    AppIcon(app = app, size = 72.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.textPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = app.tagline,
                            fontSize = 12.sp,
                            color = palette.textSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(app.developer, fontSize = 12.sp, color = accent, fontWeight = FontWeight.SemiBold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    StatCell(title = app.ratingsLabel, subtitle = "Penilaian", modifier = Modifier.weight(1f))
                    StatDivider()
                    StatCell(title = app.ageLabel, subtitle = "Usia", modifier = Modifier.weight(1f))
                    StatDivider()
                    StatCell(title = app.sizeLabel, subtitle = "Ukuran", modifier = Modifier.weight(1f))
                    StatDivider()
                    StatCell(title = "#" + app.rank, subtitle = "Peringkat", modifier = Modifier.weight(1f))
                }

                GetButtonLarge(state = state, progress = progress, enabled = !busyWithOther, onClick = onGet)
                if (busyWithOther) {
                    Text(
                        "Unduhan lain sedang berjalan — tombol dinonaktifkan sementara.",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                }

                LabSectionTitle(title = "Tangkapan Layar", subtitle = "Geser untuk melihat " + SCREENSHOT_COUNT + " gambar")
                HorizontalPager(
                    state = pagerState,
                    pageSpacing = 12.dp,
                    modifier = Modifier.fillMaxWidth().height(280.dp),
                ) { page ->
                    ScreenshotFrame(pageIndex = page, accent = accent)
                }

                LabSectionTitle(title = "Penilaian & Ulasan", subtitle = "Ringkasan bintang dan histogram")
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(end = 16.dp),
                        ) {
                            Text(
                                text = ratingLabel(app.rating),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = palette.textPrimary,
                            )
                            StarRow(rating = app.rating)
                            Text(app.ratingsLabel + " penilaian", fontSize = 10.sp, color = palette.textMuted)
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            for (star in 5 downTo 1) {
                                HistogramRow(star = star, count = app.histogram[5 - star], total = app.histogram.sum())
                            }
                        }
                    }
                }

                LabSectionTitle(title = "Apa yang Baru", subtitle = app.version + " · 3 hari lalu")
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = app.versionNotes,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = palette.textSecondary,
                        modifier = Modifier.padding(16.dp),
                    )
                }

                LabSectionTitle(title = "Deskripsi", subtitle = app.category)
                Text(app.description, fontSize = 12.sp, lineHeight = 18.sp, color = palette.textSecondary)
            }
        }
    }
}

@Composable
private fun ScreenshotFrame(pageIndex: Int, accent: Color) {
    val palette = LocalAppPalette.current
    val brush = remember(pageIndex) {
        Brush.linearGradient(
            colors = listOf(accent.copy(alpha = 0.20f), palette.surfaceMuted),
        )
    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "Tangkapan layar contoh " + (pageIndex + 1) },
    ) {
        Box(modifier = Modifier.fillMaxSize().background(brush).padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MockBar(width = 96.dp, tint = accent)
                MockBar(width = 160.dp, tint = palette.textMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MockBlock(size = 64.dp, tint = accent)
                    MockBlock(size = 64.dp, tint = palette.textMuted)
                    MockBlock(size = 64.dp, tint = accent)
                }
                MockBar(width = 130.dp, tint = palette.textMuted)
                MockBar(width = 180.dp, tint = palette.textMuted)
            }
            Text(
                text = "Contoh " + (pageIndex + 1),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textMuted,
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }
    }
}

@Composable
private fun MockBar(width: Dp, tint: Color) {
    Box(
        modifier = Modifier
            .width(width)
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(tint.copy(alpha = 0.25f)),
    )
}

@Composable
private fun MockBlock(size: Dp, tint: Color) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(tint.copy(alpha = 0.18f))
            .border(1.dp, tint.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
    )
}

@Composable
private fun HistogramRow(star: Int, count: Int, total: Int) {
    val palette = LocalAppPalette.current
    val safeTotal = if (total <= 0) 1 else total
    val fraction = count.toFloat() / safeTotal.toFloat()
    val percent = count * 100 / safeTotal
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("$star ★", fontSize = 10.sp, color = palette.textMuted, modifier = Modifier.width(32.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(palette.surfaceMuted),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(palette.warning),
            )
        }
        Text("$percent%", fontSize = 10.sp, color = palette.textMuted, modifier = Modifier.width(34.dp))
    }
}

@Composable
private fun StarRow(rating: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 0 until 5) {
            val fill = (rating - i).coerceIn(0f, 1f)
            StarGlyph(fill = fill)
        }
    }
}

@Composable
private fun StarGlyph(fill: Float) {
    val palette = LocalAppPalette.current
    Canvas(modifier = Modifier.size(14.dp)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.06f)
            lineTo(w * 0.63f, h * 0.38f)
            lineTo(w * 0.97f, h * 0.4f)
            lineTo(w * 0.71f, h * 0.62f)
            lineTo(w * 0.8f, h * 0.95f)
            lineTo(w * 0.5f, h * 0.76f)
            lineTo(w * 0.2f, h * 0.95f)
            lineTo(w * 0.29f, h * 0.62f)
            lineTo(w * 0.03f, h * 0.4f)
            lineTo(w * 0.37f, h * 0.38f)
            close()
        }
        if (fill > 0f) {
            drawPath(path = path, color = palette.warning)
        }
        if (fill < 1f) {
            // Bagian bintang yang belum terisi ditutup permukaan kartu lalu diberi outline.
            drawRect(
                color = palette.surface,
                topLeft = Offset(w * fill, 0f),
                size = Size(w * (1f - fill), h),
            )
        }
        drawPath(
            path = path,
            color = palette.border,
            style = Stroke(width = w * 0.06f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

@Composable
private fun GetButtonSmall(state: GetState, enabled: Boolean, progress: Int, onClick: () -> Unit) {
    val palette = LocalAppPalette.current
    val label = when (state) {
        GetState.SIAP -> if (enabled) "GET" else "GET"
        GetState.MENGUNDUH -> progress.toString() + "%"
        GetState.TERPASANG -> "BUKA"
        GetState.GAGAL -> "COBA LAGI"
    }
    Surface(
        onClick = onClick,
        enabled = enabled && state != GetState.MENGUNDUH,
        shape = RoundedCornerShape(999.dp),
        color = when (state) {
            GetState.TERPASANG -> palette.surfaceMuted
            GetState.GAGAL -> palette.danger.copy(alpha = 0.10f)
            else -> palette.primary
        },
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, when (state) {
            GetState.TERPASANG -> palette.border
            GetState.GAGAL -> palette.danger
            else -> palette.primary
        }),
        modifier = Modifier.alpha(if (enabled) 1f else 0.5f),
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = when (state) {
                GetState.TERPASANG -> palette.textPrimary
                GetState.GAGAL -> palette.danger
                else -> palette.onPrimary
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun GetButtonLarge(state: GetState, progress: Int, enabled: Boolean, onClick: () -> Unit) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        enabled = enabled && state != GetState.MENGUNDUH,
        shape = RoundedCornerShape(999.dp),
        color = when (state) {
            GetState.TERPASANG -> palette.surfaceMuted
            GetState.GAGAL -> palette.danger.copy(alpha = 0.10f)
            else -> palette.primary
        },
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, when (state) {
            GetState.TERPASANG -> palette.border
            GetState.GAGAL -> palette.danger
            else -> palette.primary
        }),
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .semantics {
                role = Role.Button
                contentDescription = when (state) {
                    GetState.SIAP -> "Tombol GET, unduh aplikasi"
                    GetState.MENGUNDUH -> "Sedang mengunduh " + progress + " persen"
                    GetState.TERPASANG -> "Tombol BUKA, jalankan aplikasi"
                    GetState.GAGAL -> "Unduhan gagal, tombol coba lagi"
                }
            },
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (state) {
                GetState.MENGUNDUH -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DownloadRing(progress = progress)
                    Text(
                        "Mengunduh " + progress + "%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textSecondary,
                    )
                }
                GetState.GAGAL -> Text("COBA LAGI", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.danger)
                GetState.TERPASANG -> Text("BUKA", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                GetState.SIAP -> Text("GET", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
            }
        }
    }
}

@Composable
private fun DownloadRing(progress: Int) {
    val palette = LocalAppPalette.current
    Canvas(modifier = Modifier.size(20.dp)) {
        val w = this.size.width
        drawArc(
            color = palette.border,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(w * 0.08f, w * 0.08f),
            size = Size(w * 0.84f, w * 0.84f),
            style = Stroke(width = w * 0.1f, cap = StrokeCap.Round),
        )
        drawArc(
            color = palette.primary,
            startAngle = -90f,
            sweepAngle = 360f * progress / 100,
            useCenter = false,
            topLeft = Offset(w * 0.08f, w * 0.08f),
            size = Size(w * 0.84f, w * 0.84f),
            style = Stroke(width = w * 0.1f, cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun FeaturedRow(
    app: StoreApp,
    stateOf: (Int) -> GetState,
    downloadingId: Int?,
    onGet: (Int) -> Unit,
    onOpenApp: (Int) -> Unit,
) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenApp(app.id) }
            .semantics { contentDescription = "Buka produk " + app.name },
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppIcon(app = app, size = 54.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = app.tagline,
                    fontSize = 11.sp,
                    color = palette.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = ratingLabel(app.rating) + " bintang · " + app.category,
                    fontSize = 10.sp,
                    color = palette.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            GetButtonSmall(
                state = stateOf(app.id),
                enabled = downloadingId == null || downloadingId == app.id,
                progress = 0,
                onClick = { onGet(app.id) },
            )
        }
    }
}

@Composable
private fun RankedRow(
    app: StoreApp,
    rank: Int,
    onOpenApp: (Int) -> Unit,
    state: GetState = GetState.SIAP,
    enabled: Boolean = true,
    onGet: () -> Unit = {},
) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = rank.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = palette.textMuted,
                modifier = Modifier.width(24.dp),
            )
            Box(modifier = Modifier.clickable { onOpenApp(app.id) }.semantics {
                role = Role.Button
                contentDescription = "Buka produk " + app.name
            }) {
                AppIcon(app = app, size = 48.dp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = app.category,
                    fontSize = 11.sp,
                    color = palette.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (state == GetState.GAGAL) {
                    Text("Unduhan gagal (Simulasi)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.danger)
                }
            }
            GetButtonSmall(state = state, enabled = enabled, progress = 0, onClick = onGet)
        }
    }
}

@Composable
private fun StatCell(title: String, subtitle: String, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(subtitle, fontSize = 10.sp, color = palette.textMuted)
    }
}

@Composable
private fun StatDivider() {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(28.dp)
            .background(palette.border),
    )
}

@Composable
private fun PillButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
    enabled: Boolean = true,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(999.dp),
        color = if (accent) palette.primary else palette.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, if (accent) palette.primary else palette.border),
        modifier = modifier.alpha(if (enabled) 1f else 0.5f),
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (accent) palette.onPrimary else palette.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp),
        )
    }
}

@Composable
private fun FilterChipPill(label: String, selected: Boolean, onClick: () -> Unit) {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) palette.primary.copy(alpha = 0.14f) else palette.surfaceMuted)
            .border(
                1.dp,
                if (selected) palette.primary else palette.border,
                RoundedCornerShape(999.dp),
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .semantics {
                role = Role.Button
                contentDescription = "Filter " + label + (if (selected) ", terpilih" else "")
            },
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) palette.primary else palette.textSecondary,
        )
    }
}

@Composable
private fun StoreTabBar(selected: StoreTab, onSelect: (StoreTab) -> Unit) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(0.dp),
        color = palette.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StoreTab.entries.forEach { tab ->
                val isSel = tab == selected
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelect(tab) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .semantics {
                            role = Role.Tab
                            contentDescription = "Tab " + tab.label + (if (isSel) ", aktif" else "")
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    when (tab) {
                        StoreTab.TODAY -> CalendarGlyph(tint = if (isSel) palette.primary else palette.textMuted)
                        StoreTab.GAMES -> GamepadGlyph(tint = if (isSel) palette.primary else palette.textMuted)
                        StoreTab.APPS -> GridGlyph(tint = if (isSel) palette.primary else palette.textMuted)
                        StoreTab.ARCADE -> StarOutlineGlyph(tint = if (isSel) palette.primary else palette.textMuted)
                        StoreTab.SEARCH -> SearchGlyph(tint = if (isSel) palette.primary else palette.textMuted)
                    }
                    Text(
                        text = tab.label,
                        fontSize = 9.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSel) palette.primary else palette.textMuted,
                    )
                }
            }
        }
    }
}

@Composable
private fun MockBadge() {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(palette.info.copy(alpha = 0.12f))
            .border(1.dp, palette.info, RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text("Mock", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = palette.info)
    }
}

@Composable
private fun AppIcon(app: StoreApp, size: Dp) {
    val palette = LocalAppPalette.current
    val accent = accentFor(palette, app.id)
    val brush = remember(app.id) {
        Brush.linearGradient(colors = listOf(accent.copy(alpha = 0.55f), accent.copy(alpha = 0.20f)))
    }
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size / 4))
            .background(brush)
            .border(1.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(size / 4)),
        contentAlignment = Alignment.Center,
    ) {
        AppIconGlyph(motif = app.id % 3, tint = palette.surface, size = size / 2)
    }
}

@Composable
private fun AppIconGlyph(motif: Int, tint: Color, size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (motif) {
            0 -> drawCircle(color = tint, radius = w * 0.34f, center = Offset(w * 0.5f, h * 0.5f), style = stroke)
            1 -> {
                val path = Path().apply {
                    moveTo(w * 0.5f, h * 0.14f)
                    lineTo(w * 0.88f, h * 0.82f)
                    lineTo(w * 0.12f, h * 0.82f)
                    close()
                }
                drawPath(path = path, color = tint, style = stroke)
            }
            else -> {
                val path = Path().apply {
                    moveTo(w * 0.16f, h * 0.16f)
                    lineTo(w * 0.84f, h * 0.16f)
                    lineTo(w * 0.84f, h * 0.84f)
                    lineTo(w * 0.16f, h * 0.84f)
                    close()
                }
                drawPath(path = path, color = tint, style = stroke)
            }
        }
    }
}

@Composable
private fun CalendarGlyph(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val path = Path().apply {
            moveTo(w * 0.12f, h * 0.2f)
            lineTo(w * 0.88f, h * 0.2f)
            lineTo(w * 0.88f, h * 0.86f)
            lineTo(w * 0.12f, h * 0.86f)
            close()
        }
        drawPath(path = path, color = tint, style = stroke)
        drawLine(tint, Offset(w * 0.12f, h * 0.4f), Offset(w * 0.88f, h * 0.4f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.32f, h * 0.1f), Offset(w * 0.32f, h * 0.28f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.68f, h * 0.1f), Offset(w * 0.68f, h * 0.28f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
    }
}

@Composable
private fun GamepadGlyph(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val path = Path().apply {
            moveTo(w * 0.2f, h * 0.32f)
            lineTo(w * 0.8f, h * 0.32f)
            lineTo(w * 0.92f, h * 0.72f)
            lineTo(w * 0.72f, h * 0.72f)
            lineTo(w * 0.62f, h * 0.58f)
            lineTo(w * 0.38f, h * 0.58f)
            lineTo(w * 0.28f, h * 0.72f)
            lineTo(w * 0.08f, h * 0.72f)
            close()
        }
        drawPath(path = path, color = tint, style = stroke)
    }
}

@Composable
private fun GridGlyph(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val path1 = Path().apply {
            moveTo(w * 0.14f, h * 0.14f)
            lineTo(w * 0.44f, h * 0.14f)
            lineTo(w * 0.44f, h * 0.44f)
            lineTo(w * 0.14f, h * 0.44f)
            close()
        }
        val path2 = Path().apply {
            moveTo(w * 0.56f, h * 0.14f)
            lineTo(w * 0.86f, h * 0.14f)
            lineTo(w * 0.86f, h * 0.44f)
            lineTo(w * 0.56f, h * 0.44f)
            close()
        }
        val path3 = Path().apply {
            moveTo(w * 0.14f, h * 0.56f)
            lineTo(w * 0.44f, h * 0.56f)
            lineTo(w * 0.44f, h * 0.86f)
            lineTo(w * 0.14f, h * 0.86f)
            close()
        }
        val path4 = Path().apply {
            moveTo(w * 0.56f, h * 0.56f)
            lineTo(w * 0.86f, h * 0.56f)
            lineTo(w * 0.86f, h * 0.86f)
            lineTo(w * 0.56f, h * 0.86f)
            close()
        }
        drawPath(path1, color = tint, style = stroke)
        drawPath(path2, color = tint, style = stroke)
        drawPath(path3, color = tint, style = stroke)
        drawPath(path4, color = tint, style = stroke)
    }
}

@Composable
private fun StarOutlineGlyph(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.08f)
            lineTo(w * 0.63f, h * 0.38f)
            lineTo(w * 0.95f, h * 0.42f)
            lineTo(w * 0.71f, h * 0.63f)
            lineTo(w * 0.79f, h * 0.94f)
            lineTo(w * 0.5f, h * 0.77f)
            lineTo(w * 0.21f, h * 0.94f)
            lineTo(w * 0.29f, h * 0.63f)
            lineTo(w * 0.05f, h * 0.42f)
            lineTo(w * 0.37f, h * 0.38f)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

@Composable
private fun SearchGlyph(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        drawCircle(
            color = tint,
            radius = w * 0.28f,
            center = Offset(w * 0.42f, h * 0.42f),
            style = Stroke(width = w * 0.09f, cap = StrokeCap.Round),
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.63f, h * 0.63f),
            end = Offset(w * 0.88f, h * 0.88f),
            strokeWidth = w * 0.09f,
            cap = StrokeCap.Round,
        )
    }
}

private fun accentFor(palette: AppPalette, id: Int): Color {
    return when (id % 4) {
        0 -> palette.primary
        1 -> palette.info
        2 -> palette.success
        else -> palette.warning
    }
}

private fun ratingLabel(rating: Float): String {
    val whole = rating.toInt()
    val decimal = ((rating - whole) * 10).toInt().coerceIn(0, 9)
    return whole.toString() + "," + decimal
}
