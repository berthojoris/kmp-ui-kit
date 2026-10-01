package com.example.uiapp.ui.screentime

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.FlatSecondaryButton
import com.example.uiapp.ui.components.FlatToggle
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private enum class RentangLaporan(val label: String) {
    HARIAN("Harian"),
    MINGGUAN("Mingguan"),
}

private enum class StatusLaporan(val label: String) {
    SIAP("Laporan siap"),
    MEMUAT("Memuat laporan…"),
    KOSONG("Belum ada data"),
    GAGAL("Gagal memuat"),
}

private data class BarWaktu(
    val label: String,
    val menit: Int,
    val catatan: String,
)

private val BarMingguan: List<BarWaktu> = listOf(
    BarWaktu("Sen", 245, "Kabar Sosial & Studio Kreatif"),
    BarWaktu("Sel", 190, "Kabar Sosial"),
    BarWaktu("Rab", 275, "Studio Kreatif"),
    BarWaktu("Kam", 320, "Putar Video"),
    BarWaktu("Jum", 260, "Kabar Sosial"),
    BarWaktu("Sab", 410, "Game Petualangan"),
    BarWaktu("Min", 355, "Putar Video"),
)

private val BarHarian: List<BarWaktu> = listOf(
    BarWaktu("06–09", 45, "Pesan & kabar pagi"),
    BarWaktu("09–12", 120, "Studio Kreatif"),
    BarWaktu("12–15", 85, "Kabar Sosial"),
    BarWaktu("15–18", 150, "Game Petualangan"),
    BarWaktu("18–21", 130, "Putar Video"),
    BarWaktu("21–24", 40, "Kabar Sosial"),
)

private enum class KategoriWarna { SOSIAL, PRODUKTIF, KREATIF, HIBURAN }

private data class KategoriPemakaian(
    val nama: String,
    val persen: Int,
    val menit: Int,
    val warna: KategoriWarna,
)

private val KategoriMingguan: List<KategoriPemakaian> = listOf(
    KategoriPemakaian("Sosial", 38, 781, KategoriWarna.SOSIAL),
    KategoriPemakaian("Produktivitas", 27, 555, KategoriWarna.PRODUKTIF),
    KategoriPemakaian("Kreativitas", 21, 432, KategoriWarna.KREATIF),
    KategoriPemakaian("Hiburan", 14, 287, KategoriWarna.HIBURAN),
)

private val KategoriHarian: List<KategoriPemakaian> = listOf(
    KategoriPemakaian("Sosial", 32, 182, KategoriWarna.SOSIAL),
    KategoriPemakaian("Produktivitas", 25, 143, KategoriWarna.PRODUKTIF),
    KategoriPemakaian("Kreativitas", 18, 103, KategoriWarna.KREATIF),
    KategoriPemakaian("Hiburan", 25, 142, KategoriWarna.HIBURAN),
)

private data class AplikasiBatas(
    val id: String,
    val nama: String,
    val kategori: String,
    val saranMenit: Int,
)

private val AplikasiMock: List<AplikasiBatas> = listOf(
    AplikasiBatas("kabar_sosial", "Kabar Sosial", "Sosial", 60),
    AplikasiBatas("putar_video", "Putar Video", "Hiburan", 90),
    AplikasiBatas("game_petualangan", "Game Petualangan", "Hiburan", 45),
    AplikasiBatas("studio_kreatif", "Studio Kreatif", "Kreativitas", 120),
)

private data class BatasTersimpan(
    val id: String,
    val nama: String,
    val menit: Int,
    val hari: String,
    val aktif: Boolean,
)

private data class AplikasiIzin(
    val id: String,
    val nama: String,
    val keterangan: String,
)

private val SelaluDiizinkanMock: List<AplikasiIzin> = listOf(
    AplikasiIzin("telepon", "Telepon", "Panggilan darurat tetap tersedia"),
    AplikasiIzin("pesan", "Pesan", "Obrolan keluarga diizinkan"),
    AplikasiIzin("peta", "Peta", "Navigasi diizinkan saat bepergian"),
    AplikasiIzin("musik", "Musik", "Pemutar musik offline"),
)

private val HariLabels = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

private fun formatMenit(total: Int): String {
    val jam = total / 60
    val sisa = total % 60
    return if (jam > 0) "$jam j $sisa m" else "$sisa m"
}

private val TINGGI_GRAFIK = 150.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScreenTimeLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var rentang by remember { mutableStateOf(RentangLaporan.MINGGUAN) }
    var laporanStatus by remember { mutableStateOf(StatusLaporan.SIAP) }
    var muatTarget by remember { mutableStateOf(StatusLaporan.SIAP) }
    var muatSeq by remember { mutableIntStateOf(0) }
    var barTerpilih by remember { mutableStateOf<BarWaktu?>(null) }

    var appTerpilih by remember { mutableStateOf<String?>(null) }
    var limitMenit by remember { mutableIntStateOf(60) }
    var hariAktif by remember { mutableStateOf(listOf(0, 1, 2, 3, 4)) }
    var menyimpan by remember { mutableStateOf(false) }
    var simpanSeq by remember { mutableIntStateOf(0) }
    var simpanBerhasil by remember { mutableStateOf(false) }
    val daftarBatas = remember {
        mutableStateListOf(
            BatasTersimpan("putar_video", "Putar Video", 90, "Sen–Jum", aktif = true),
            BatasTersimpan("game_petualangan", "Game Petualangan", 45, "Setiap hari", aktif = false),
        )
    }

    val izinSelalu = remember { mutableStateOf(setOf("telepon", "pesan")) }

    var downtimeAktif by remember { mutableStateOf(false) }
    var sisaDetik by remember { mutableIntStateOf(0) }
    var permintaanAktif by remember { mutableStateOf(false) }
    var tambahanSelesai by remember { mutableStateOf(false) }
    var logPesan by remember { mutableStateOf("Pilih aplikasi lalu tentukan batas waktu (Simulasi).") }

    val barAktif = if (rentang == RentangLaporan.MINGGUAN) BarMingguan else BarHarian
    val kategoriAktif = if (rentang == RentangLaporan.MINGGUAN) KategoriMingguan else KategoriHarian
    val maxMenit = remember(barAktif) { barAktif.maxOf { it.menit } }
    val totalMenit = remember(barAktif) { barAktif.sumOf { it.menit } }
    val rataRata = totalMenit / barAktif.size
    val fraksiRataRata = (rataRata.toFloat() / maxMenit).coerceIn(0.05f, 1f)

    // Pemuatan laporan: normal, kosong, atau gagal.
    LaunchedEffect(muatSeq) {
        if (muatSeq > 0) {
            laporanStatus = StatusLaporan.MEMUAT
            delay(950)
            laporanStatus = muatTarget
        }
    }

    // Simpan batas aplikasi dengan state menyimpan lalu sukses.
    LaunchedEffect(simpanSeq) {
        if (simpanSeq > 0) {
            menyimpan = true
            simpanBerhasil = false
            delay(800)
            val app = AplikasiMock.firstOrNull { it.id == appTerpilih }
            if (app != null) {
                val teksHari = when {
                    hariAktif.size == 7 -> "Setiap hari"
                    hariAktif.isEmpty() -> "Tanpa hari"
                    else -> hariAktif.sorted().joinToString(", ") { HariLabels[it] }
                }
                daftarBatas.removeAll { it.id == app.id }
                daftarBatas.add(
                    BatasTersimpan(
                        id = app.id,
                        nama = app.nama,
                        menit = limitMenit,
                        hari = teksHari,
                        aktif = true,
                    )
                )
                simpanBerhasil = true
                logPesan = "Batas ${app.nama} disimpan: ${formatMenit(limitMenit)} • $teksHari (Simulasi)."
            }
            menyimpan = false
        }
    }

    // Hitung mundur izin tambahan 30 detik.
    LaunchedEffect(permintaanAktif) {
        if (permintaanAktif) {
            while (sisaDetik > 0) {
                delay(1000)
                sisaDetik -= 1
            }
            if (sisaDetik == 0) {
                tambahanSelesai = true
                logPesan = "Waktu tambahan habis. Downtime berlaku lagi (Simulasi)."
            }
            permintaanAktif = false
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Screen Time Report & Limits",
                subtitle = "Waktu pakai, batas app, downtime — Simulasi",
                onBack = onBack,
            )
        },
        bottomBar = {
            Surface(
                color = palette.surface,
                contentColor = palette.textPrimary,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
                border = BorderStroke(1.dp, palette.border),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (downtimeAktif) {
                                "Downtime aktif • 22.00–06.30"
                            } else {
                                "Downtime tidak aktif • 22.00–06.30"
                            },
                            fontSize = 11.sp,
                            color = palette.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = when {
                                permintaanAktif -> "Sisa izin: $sisaDetik dtk"
                                tambahanSelesai -> "Izin habis"
                                else -> "Tanpa izin tambahan"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (permintaanAktif) palette.info else palette.textSecondary,
                        )
                    }
                    FlatPrimaryButton(
                        text = if (permintaanAktif) "Izin tambahan berjalan…" else "Satu Menit Lagi (30 detik)",
                        onClick = {
                            sisaDetik = 30
                            tambahanSelesai = false
                            permintaanAktif = true
                            logPesan = "Permintaan \"One More Minute\" dikirim: izin 30 detik (Simulasi)."
                        },
                        enabled = !permintaanAktif,
                    )
                }
            }
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LabSectionTitle(
                    title = "Waktu Layar",
                    subtitle = "Laporan pemakaian, batas aplikasi, dan downtime",
                    modifier = Modifier.weight(1f),
                )
                SimulasiPill()
            }

            LabIntroCard(
                title = "Simulasi, tanpa statistik sistem",
                body = "Seluruh angka waktu pakai berasal dari data mock lokal. Lab ini tidak membaca statistik " +
                    "layar perangkat dan tidak membatasi aplikasi lain secara nyata.",
            )

            // ===== Laporan =====
            LabSectionTitle(
                title = "Laporan Waktu Pakai",
                subtitle = "Bar waktu pakai, garis rata-rata, dan penyusun kategori",
            )

            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RentangLaporan.entries.forEach { opsi ->
                        FlatChip(
                            label = opsi.label,
                            selected = rentang == opsi,
                            onClick = {
                                rentang = opsi
                                barTerpilih = null
                                logPesan = "Laporan diubah ke ${opsi.label.lowercase()} (Simulasi)."
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                when (laporanStatus) {
                    StatusLaporan.MEMUAT -> {
                        Text(
                            text = "Memuat laporan waktu pakai…",
                            fontSize = 12.sp,
                            color = palette.textMuted,
                        )
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(palette.surfaceMuted)
                                    .border(1.dp, palette.border, RoundedCornerShape(10.dp)),
                            )
                        }
                    }
                    StatusLaporan.KOSONG -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "Belum ada data waktu pakai",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary,
                            )
                            Text(
                                text = "Laporan akan terisi setelah perangkat dipakai beberapa hari.",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                                textAlign = TextAlign.Center,
                            )
                            FlatSecondaryButton(
                                text = "Muat Contoh Data",
                                onClick = {
                                    muatTarget = StatusLaporan.SIAP
                                    muatSeq++
                                },
                            )
                        }
                    }
                    StatusLaporan.GAGAL -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = palette.danger.copy(alpha = 0.12f),
                            contentColor = palette.textPrimary,
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                            border = BorderStroke(1.dp, palette.danger),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = "Laporan gagal dimuat (Simulasi galat statistik). Data lokal tidak berubah.",
                                fontSize = 12.sp,
                                color = palette.textPrimary,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                        FlatSecondaryButton(
                            text = "Coba Lagi",
                            onClick = {
                                muatTarget = StatusLaporan.SIAP
                                muatSeq++
                            },
                        )
                    }
                    StatusLaporan.SIAP -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Total ${formatMenit(totalMenit)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                            )
                            Text(
                                text = "Rata-rata ${formatMenit(rataRata)}/hari",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textSecondary,
                            )
                        }

                        // Grafik bar + garis rata-rata
                        Box(modifier = Modifier.fillMaxWidth().height(TINGGI_GRAFIK)) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                barAktif.forEach { bar ->
                                    val fraksi = (bar.menit.toFloat() / maxMenit).coerceIn(0.06f, 1f)
                                    val dipilih = barTerpilih?.label == bar.label
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Bottom,
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(fraction = fraksi)
                                                .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                                .background(
                                                    if (dipilih) palette.primary else palette.info.copy(alpha = 0.55f)
                                                )
                                                .border(
                                                    1.dp,
                                                    if (dipilih) palette.primary else palette.border,
                                                    RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp),
                                                )
                                                .clickable(role = Role.Button, onClick = { barTerpilih = bar })
                                                .semantics {
                                                    contentDescription =
                                                        "${bar.label}, ${formatMenit(bar.menit)}, ${bar.catatan}"
                                                },
                                        )
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .offset(y = TINGGI_GRAFIK * (1f - fraksiRataRata))
                                    .background(palette.warning)
                            )
                            Text(
                                text = "Rata-rata",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.warning,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(y = TINGGI_GRAFIK * (1f - fraksiRataRata) - 14.dp),
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            barAktif.forEach { bar ->
                                Text(
                                    text = bar.label,
                                    fontSize = 9.sp,
                                    textAlign = TextAlign.Center,
                                    color = if (barTerpilih?.label == bar.label) palette.textPrimary else palette.textMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = palette.surfaceMuted,
                            contentColor = palette.textSecondary,
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = barTerpilih?.let {
                                    "${it.label} • ${formatMenit(it.menit)} • ${it.catatan}"
                                } ?: "Ketuk salah satu bar untuk detail hari / rentang jam.",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = palette.textSecondary,
                                modifier = Modifier.padding(10.dp),
                            )
                        }

                        // Penyusun kategori dengan legenda berlabel
                        Text(
                            text = "Penyusun Kategori",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .border(1.dp, palette.border, RoundedCornerShape(7.dp)),
                        ) {
                            kategoriAktif.forEach { kategori ->
                                Box(
                                    modifier = Modifier
                                        .weight(kategori.persen.toFloat())
                                        .fillMaxHeight()
                                        .background(warnaKategori(kategori.warna, palette)),
                                )
                            }
                        }
                        kategoriAktif.forEach { kategori ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(warnaKategori(kategori.warna, palette))
                                        .border(1.dp, palette.border, RoundedCornerShape(3.dp)),
                                )
                                Text(
                                    text = kategori.nama,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = "${kategori.persen}% • ${formatMenit(kategori.menit)}",
                                    fontSize = 11.sp,
                                    color = palette.textSecondary,
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FlatSecondaryButton(
                                text = "Muat Ulang",
                                onClick = {
                                    muatTarget = StatusLaporan.SIAP
                                    muatSeq++
                                },
                                modifier = Modifier.weight(1f),
                            )
                            FlatSecondaryButton(
                                text = "Simulasi Kosong",
                                onClick = {
                                    muatTarget = StatusLaporan.KOSONG
                                    muatSeq++
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        FlatSecondaryButton(
                            text = "Simulasi Galat Laporan",
                            onClick = {
                                muatTarget = StatusLaporan.GAGAL
                                muatSeq++
                            },
                        )
                    }
                }
            }

            // ===== Batas aplikasi =====
            LabSectionTitle(
                title = "Batas Aplikasi",
                subtitle = "Pilih aplikasi, atur waktu limit, dan jadwal hari",
            )

            LabCard {
                Text(
                    text = "Pilih aplikasi",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textSecondary,
                )
                AplikasiMock.forEach { app ->
                    val dipilih = appTerpilih == app.id
                    Surface(
                        onClick = {
                            appTerpilih = app.id
                            limitMenit = app.saranMenit
                            simpanBerhasil = false
                            logPesan = "Aplikasi dipilih: ${app.nama} (saran batas ${formatMenit(app.saranMenit)})."
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (dipilih) palette.primaryContainer else palette.surfaceMuted,
                        contentColor = palette.textPrimary,
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        border = BorderStroke(1.dp, if (dipilih) palette.primary else palette.border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                role = Role.Button
                                contentDescription = "${app.nama}, ${app.kategori}, ${if (dipilih) "terpilih" else "belum terpilih"}"
                            },
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.nama,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "${app.kategori} • saran ${formatMenit(app.saranMenit)}",
                                    fontSize = 11.sp,
                                    color = palette.textMuted,
                                )
                            }
                            Text(
                                text = if (dipilih) "Dipilih" else "Pilih",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dipilih) palette.primary else palette.textMuted,
                            )
                        }
                    }
                }

                Text(
                    text = "Waktu limit per hari",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textSecondary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    FlatSecondaryButton(
                        text = "− 15 m",
                        onClick = { limitMenit = (limitMenit - 15).coerceAtLeast(15) },
                        enabled = limitMenit > 15,
                        modifier = Modifier.weight(1f),
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = palette.surfaceMuted,
                        contentColor = palette.textPrimary,
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier.semantics {
                            contentDescription = "Waktu limit saat ini ${formatMenit(limitMenit)} per hari"
                        },
                    ) {
                        Text(
                            text = formatMenit(limitMenit),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .width(110.dp)
                                .padding(vertical = 12.dp),
                        )
                    }
                    FlatSecondaryButton(
                        text = "+ 15 m",
                        onClick = { limitMenit = (limitMenit + 15).coerceAtMost(240) },
                        enabled = limitMenit < 240,
                        modifier = Modifier.weight(1f),
                    )
                }

                Text(
                    text = "Jadwal berlaku",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textSecondary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    HariLabels.forEachIndexed { idx, nama ->
                        val aktif = hariAktif.contains(idx)
                        FlatChip(
                            label = nama,
                            selected = aktif,
                            onClick = {
                                hariAktif = if (aktif) {
                                    hariAktif.filterNot { it == idx }
                                } else {
                                    hariAktif + idx
                                }
                                simpanBerhasil = false
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                FlatSecondaryButton(
                    text = if (hariAktif.size == 7) "Sudah Setiap Hari" else "Terapkan Setiap Hari",
                    onClick = { hariAktif = listOf(0, 1, 2, 3, 4, 5, 6) },
                    enabled = hariAktif.size != 7,
                )

                FlatPrimaryButton(
                    text = if (menyimpan) "Menyimpan…" else "Simpan Batas Aplikasi",
                    onClick = { simpanSeq++ },
                    enabled = appTerpilih != null && !menyimpan && hariAktif.isNotEmpty(),
                )
                if (appTerpilih == null) {
                    Text(
                        text = "Pilih aplikasi terlebih dahulu untuk mengaktifkan tombol simpan.",
                        fontSize = 10.sp,
                        color = palette.textMuted,
                    )
                }
                if (simpanBerhasil) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = palette.success.copy(alpha = 0.12f),
                        contentColor = palette.textPrimary,
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        border = BorderStroke(1.dp, palette.success),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Batas tersimpan (Simulasi). Perubahan berlaku pada laporan berikutnya.",
                            fontSize = 12.sp,
                            color = palette.textPrimary,
                            modifier = Modifier.padding(12.dp),
                        )
                    }
                }

                Text(
                    text = "Batas Aktif",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textSecondary,
                )
                if (daftarBatas.isEmpty()) {
                    Text(
                        text = "Belum ada batas aplikasi yang disetel.",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                } else {
                    daftarBatas.forEach { batas ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.surfaceMuted)
                                .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = batas.nama,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "Batas ${formatMenit(batas.menit)} • ${batas.hari} • ${if (batas.aktif) "AKTIF" else "NONAKTIF"}",
                                    fontSize = 11.sp,
                                    color = palette.textMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            FlatToggle(
                                checked = batas.aktif,
                                onCheckedChange = { baru ->
                                    val idx = daftarBatas.indexOfFirst { it.id == batas.id }
                                    if (idx >= 0) {
                                        daftarBatas[idx] = daftarBatas[idx].copy(aktif = baru)
                                        logPesan = "Batas ${batas.nama} ${if (baru) "diaktifkan" else "dinonaktifkan"} (Simulasi)."
                                    }
                                },
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = palette.surfaceMuted,
                    contentColor = palette.textSecondary,
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = logPesan,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                        modifier = Modifier.padding(10.dp),
                    )
                }
            }

            // ===== Always Allowed =====
            LabSectionTitle(
                title = "Always Allowed",
                subtitle = "Aplikasi yang tetap dapat dipakai saat batas & downtime",
            )

            LabCard {
                SelaluDiizinkanMock.forEach { izin ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.surfaceMuted)
                            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = izin.nama,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = "${izin.keterangan} • ${if (izinSelalu.value.contains(izin.id)) "diizinkan" else "dibatasi"}",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        FlatToggle(
                            checked = izinSelalu.value.contains(izin.id),
                            onCheckedChange = { baru ->
                                izinSelalu.value = if (baru) {
                                    izinSelalu.value + izin.id
                                } else {
                                    izinSelalu.value - izin.id
                                }
                            },
                        )
                    }
                }
            }

            // ===== Downtime =====
            LabSectionTitle(
                title = "Downtime",
                subtitle = "Jadwal 22.00–06.30 dan permintaan tambahan waktu",
            )

            LabCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Aktifkan Downtime Sekarang",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = if (downtimeAktif) {
                                "Downtime aktif — aplikasi di luar Always Allowed dibatasi."
                            } else {
                                "Downtime nonaktif — jadwal tetap berlaku otomatis malam ini."
                            },
                            fontSize = 11.sp,
                            color = palette.textMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    FlatToggle(
                        checked = downtimeAktif,
                        onCheckedChange = { baru ->
                            downtimeAktif = baru
                            if (!baru) {
                                sisaDetik = 0
                                permintaanAktif = false
                            }
                            logPesan = if (baru) {
                                "Downtime diaktifkan manual (Simulasi)."
                            } else {
                                "Downtime dimatikan manual (Simulasi)."
                            }
                        },
                    )
                }

                if (downtimeAktif) {
                    Text(
                        text = "Saat downtime hanya Always Allowed yang dapat dibuka.",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )

                    Text(
                        text = "Permintaan Tambahan Waktu",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "Tombol \"Satu Menit Lagi\" memberi izin 30 detik dengan hitung mundur terlihat.",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                        lineHeight = 15.sp,
                    )

                    val progres = sisaDetik / 30f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(palette.surfaceMuted)
                            .border(1.dp, palette.border, RoundedCornerShape(5.dp)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = progres.coerceIn(0f, 1f))
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(if (permintaanAktif) palette.info else palette.success),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = when {
                                permintaanAktif -> "Izin berjalan: $sisaDetik detik tersisa"
                                tambahanSelesai -> "Status: waktu tambahan habis"
                                else -> "Status: tanpa izin tambahan"
                            },
                            fontSize = 11.sp,
                            color = palette.textSecondary,
                        )
                        Text(
                            text = if (permintaanAktif) "30 dtk" else "0 dtk",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textMuted,
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FlatPrimaryButton(
                            text = "Satu Menit Lagi (30 detik)",
                            onClick = {
                                sisaDetik = 30
                                tambahanSelesai = false
                                permintaanAktif = true
                                logPesan = "Permintaan \"One More Minute\" dikirim: izin 30 detik (Simulasi)."
                            },
                            enabled = !permintaanAktif,
                            modifier = Modifier.weight(1f),
                        )
                        FlatSecondaryButton(
                            text = "Batalkan",
                            onClick = {
                                permintaanAktif = false
                                sisaDetik = 0
                                logPesan = "Izin tambahan dibatalkan (Simulasi)."
                            },
                            enabled = permintaanAktif,
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    Text(
                        text = "Aktifkan downtime untuk mencoba permintaan tambahan waktu.",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                }

                Text(
                    text = "Semua pengaturan bersifat mock: tidak ada pembatasan aplikasi sistem yang dipasang.",
                    fontSize = 10.sp,
                    color = palette.textMuted,
                    lineHeight = 14.sp,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun warnaKategori(jenis: KategoriWarna, palette: com.example.uiapp.theme.AppPalette): Color = when (jenis) {
    KategoriWarna.SOSIAL -> palette.info
    KategoriWarna.PRODUKTIF -> palette.success
    KategoriWarna.KREATIF -> palette.warning
    KategoriWarna.HIBURAN -> palette.textMuted
}

@Composable
private fun SimulasiPill(modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(50),
        color = palette.warning.copy(alpha = 0.15f),
        contentColor = palette.textSecondary,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, palette.warning),
        modifier = modifier,
    ) {
        Text(
            text = "Simulasi",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textSecondary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun FlatChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) palette.primaryContainer else palette.surfaceMuted,
        contentColor = if (selected) palette.onPrimaryContainer else palette.textSecondary,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
        modifier = modifier.semantics {
            role = Role.Button
            contentDescription = "$label${if (selected) ", terpilih" else ""}"
        },
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
        )
    }
}
