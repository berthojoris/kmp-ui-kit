package com.example.uiapp.ui.floatingdock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private data class FeedEntry(
    val id: Int,
    val title: String,
    val category: String,
    val excerpt: String,
    val meta: String,
)

private data class DockTab(val id: String, val label: String)

private val DockTabs = listOf(
    DockTab("beranda", "Beranda"),
    DockTab("jelajah", "Jelajah"),
    DockTab("aktivitas", "Aktivitas"),
    DockTab("profil", "Profil"),
)

private val FeedData = listOf(
    FeedEntry(1, "Sunrise Puncak Bromo Picu Antrean Sejak Dini Hari", "Destinasi", "Pos observasi penuh sejak pukul tiga pagi; pengelola menambah dua lahan parkir darurat akhir pekan ini.", "2 jam lalu"),
    FeedEntry(2, "Festival Layang-Layang Pasir Putih Digelar Bulan Depan", "Festival", "Lebih dari dua ratus peserta dari sembilan provinsi sudah mendaftar untuk kategori tradisional dan modern.", "3 jam lalu"),
    FeedEntry(3, "Jalur Trekking Rinjani Sembalun Ditutup Sementara", "Pengumuman", "Penutupan menyusul intensitas hujan tinggi di kawasan pos dua sampai pos tiga sepanjang pekan ini.", "5 jam lalu"),
    FeedEntry(4, "Nusa Penida Masuk Daftar Spot Sunrise Terpopuler", "Tren", "Pencarian kata kunci pantai Kelingking naik tiga kali lipat dibanding bulan lalu versi data platform wisata.", "6 jam lalu"),
    FeedEntry(5, "Warung Bu Ratna Buka Cabang Ketiga di Yogyakarta", "Kuliner", "Menu andalan gudeg kendil tetap dipertahankan, ditambah varian sarapan nasi godeg pagi buta.", "7 jam lalu"),
    FeedEntry(6, "Kabut Tebal Dieng Menyambut Pagi Pengunjung", "Cerita", "Suhu tujuh derajat pagi ini jadi tarikan sendiri bagi pemburu matahari di bukit Sikunir.", "9 jam lalu"),
    FeedEntry(7, "Villa Telaga Rakit Diskon Staycation Akhir Pekan", "Tren", "Paket dua malam termasuk sarapan dan sewa sepeda keliling desa dengan batas pemesanan akhir bulan.", "10 jam lalu"),
    FeedEntry(8, "Jalur Trek Komodo Dipersingkat Demi Keamanan Pengunjung", "Pengumuman", "Pemandu wajib mendampingi maksimal lima orang per grup sepanjang rute savana baru.", "11 jam lalu"),
    FeedEntry(9, "Kuliner Malam Pecenongan Kembali Ramai", "Kuliner", "Deretan gerobak jagung bakar dan martabak buka pukul enam sore sampai dua jam subuh.", "12 jam lalu"),
    FeedEntry(10, "Karimunjawa Terima Kapal Baru Jalur Jepara", "Pengumuman", "Kursi tambahan memangkas waktu tempuh empat puluh menit; jadwal pagi dimulai besok.", "13 jam lalu"),
    FeedEntry(11, "Senja Pantai Klayar, Sembilan Jam Perjalanan dari Jakarta", "Cerita", "Tebing karang dan deburan ombak jadi latar senja favorit fotografer akhir pekan ini.", "14 jam lalu"),
    FeedEntry(12, "Tujuh Warung Kopi Terbaik di Bandung Bagian Utara", "Kuliner", "Dari kopi susu tunggang hingga seduh dingin single origin, semua di bawah dua puluh ribu rupiah.", "16 jam lalu"),
    FeedEntry(13, "Wakatobi Buka Kelas Menyelam untuk Pemula", "Destinasi", "Kelas tiga hari mencakup kolam latihan dan dua penyelaman terpandu di lokasi rumah sirip.", "18 jam lalu"),
    FeedEntry(14, "Tana Toraja Siapkan Ritual Rambu Solo Musim Ini", "Festival", "Desa adat menyiapkan titik parkir tambahan dan jelajah kereta lembah untuk musim ceremony.", "20 jam lalu"),
    FeedEntry(15, "Trek Kawah Ijen Wajib Panduan Lokal Mulai Bulan Ini", "Pengumuman", "Aturan baru berlaku untuk rute paltuding demi mengurangi risiko jalur licin menjelang subuh.", "21 jam lalu"),
    FeedEntry(16, "Danau Toba Genjot Homestay Baru di Pulau Samosir", "Destinasi", "Empat puluh unit homestay rakyat direnovasi dengan dukungan pelatihan layanan tamu dasar.", "22 jam lalu"),
    FeedEntry(17, "Ubud Tambah Jalur Sepeda Sepanjang Dua Belas Kilometer", "Tren", "Jalur baru memisahkan pejalan kaki dan pesepeda dari kendaraan bermotor di kawasan sawah.", "23 jam lalu"),
    FeedEntry(18, "Pangandaran Gelar Lomba Surfing Nasional", "Festival", "Pendayung papan selancar mendaftar dari Bali, Jawa Barat, hingga Nusa Tenggara Barat.", "1 hari lalu"),
    FeedEntry(19, "Museum Batik Pekalongan Perbarui Ruang Interaktif", "Destinasi", "Pengunjung kini bisa mencoba colet malam dipandu perajin lintas generasi setiap akhir pekan.", "1 hari lalu"),
    FeedEntry(20, "Belitung Buka Ekowisata Pulau Lengkuas Sehari Penuh", "Destinasi", "Paket baru menggabungkan penyelaman dangkal dan tur mercusuar dengan kuota harian terbatas.", "1 hari lalu"),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FloatingDockScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current
    val dockExpanded = remember { mutableStateOf(true) }
    val selectedTab = remember { mutableIntStateOf(0) }

    BackHandler(enabled = true) {
        if (!dockExpanded.value) dockExpanded.value = true else onBack()
    }

    // Deteksi arah scroll: turun konten (delta y negatif) = minimize, naik = expand.
    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -1f) dockExpanded.value = false
                else if (available.y > 1f) dockExpanded.value = true
                return Offset.Zero
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(palette.background)) {
        UiTopBar(
            title = "Floating Dock",
            subtitle = "Dock meminimalkan diri saat scroll",
            onBack = onBack,
        )
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxSize().nestedScroll(scrollConnection),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 170.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(FeedData, key = { it.id }) { entry ->
                    FeedCard(entry = entry)
                }
            }
            DockBar(
                expanded = dockExpanded.value,
                selectedIndex = selectedTab.intValue,
                onSelect = { selectedTab.intValue = it },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun FeedCard(entry: FeedEntry) {
    val palette = LocalAppPalette.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = entry.category,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                )
                Text(
                    text = entry.meta,
                    fontSize = 11.sp,
                    color = palette.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 10.dp),
                )
            }
            Text(
                text = entry.title,
                modifier = Modifier.padding(top = 9.dp),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = entry.excerpt,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = palette.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun DockBar(
    expanded: Boolean,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Surface(
        modifier = modifier
            .navigationBarsPadding()
            .padding(bottom = 12.dp)
            .animateContentSize(spring(stiffness = Spring.StiffnessMediumLow)),
        shape = RoundedCornerShape(percent = 50),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DockTabs.forEachIndexed { index, tab ->
                AnimatedVisibility(
                    visible = expanded || index == selectedIndex,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally(),
                ) {
                    DockItem(
                        tab = tab,
                        selected = index == selectedIndex,
                        expanded = expanded,
                        onClick = { onSelect(index) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DockItem(
    tab: DockTab,
    selected: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val contentColor = if (selected) palette.primary else palette.textSecondary
    Box(
        modifier = Modifier
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(if (selected) palette.primaryContainer else Color.Transparent)
            .clickable(role = Role.Tab, onClickLabel = "Buka tab ${tab.label}") { onClick() }
            .semantics {
                stateDescription = if (selected) "Tab aktif" else "Tab tidak aktif"
                if (!expanded) contentDescription = tab.label
            },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp),
        ) {
            DockTabIcon(tabId = tab.id, tint = contentColor)
            AnimatedVisibility(visible = expanded) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .padding(start = 7.dp)
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(palette.primary),
                        )
                    }
                    Text(
                        text = tab.label,
                        modifier = Modifier.padding(start = 6.dp, end = 2.dp),
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun DockTabIcon(tabId: String, tint: Color) {
    when (tabId) {
        "beranda" -> HomeDockIcon(tint = tint)
        "jelajah" -> CompassDockIcon(tint = tint)
        "aktivitas" -> ActivityDockIcon(tint = tint)
        else -> PersonDockIcon(tint = tint)
    }
}

@Composable
private fun HomeDockIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val brush = Stroke(width = 1.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawLine(tint, Offset(0.5f * w, 0.06f * h), Offset(0.06f * w, 0.44f * h), brush.width, brush.cap)
        drawLine(tint, Offset(0.5f * w, 0.06f * h), Offset(0.94f * w, 0.44f * h), brush.width, brush.cap)
        drawLine(tint, Offset(0.18f * w, 0.38f * h), Offset(0.18f * w, 0.94f * h), brush.width, brush.cap)
        drawLine(tint, Offset(0.82f * w, 0.38f * h), Offset(0.82f * w, 0.94f * h), brush.width, brush.cap)
        val door = Path().apply {
            moveTo(0.42f * w, 0.94f * h)
            lineTo(0.42f * w, 0.66f * h)
            lineTo(0.58f * w, 0.66f * h)
            lineTo(0.58f * w, 0.94f * h)
        }
        drawPath(door, tint, style = brush)
    }
}

@Composable
private fun CompassDockIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(tint, radius = 0.42f * w, center = Offset(0.5f * w, 0.5f * h), style = stroke)
        drawLine(tint, Offset(0.36f * w, 0.64f * h), Offset(0.64f * w, 0.36f * h), stroke.width, stroke.cap)
        drawCircle(tint, radius = 0.045f * w, center = Offset(0.5f * w, 0.5f * h))
    }
}

@Composable
private fun ActivityDockIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val line = Path().apply {
            moveTo(0.06f * w, 0.64f * h)
            lineTo(0.30f * w, 0.64f * h)
            lineTo(0.42f * w, 0.26f * h)
            lineTo(0.58f * w, 0.82f * h)
            lineTo(0.70f * w, 0.54f * h)
            lineTo(0.94f * w, 0.54f * h)
        }
        drawPath(line, tint, style = Stroke(width = 1.8f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun PersonDockIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(tint, radius = 0.16f * w, center = Offset(0.5f * w, 0.30f * h), style = stroke)
        drawArc(
            color = tint,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(0.24f * w, 0.56f * h),
            size = Size(0.52f * w, 0.44f * h),
            style = stroke,
        )
    }
}
