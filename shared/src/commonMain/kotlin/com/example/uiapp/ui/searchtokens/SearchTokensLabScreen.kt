package com.example.uiapp.ui.searchtokens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatSecondaryButton
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.UiTopBar

private data class Token(val id: Int, val kunci: String, val nilai: String)

private data class Catatan(
    val id: Int,
    val judul: String,
    val pengirim: String,
    val tanggal: String,
    val relatif: String,
    val label: String,
    val isi: String,
)

private val SaranNilai: Map<String, List<String>> = mapOf(
    "dari" to listOf("Sari", "Budi", "Dewi", "Andi", "Rina"),
    "tanggal" to listOf("hari ini", "minggu ini", "bulan ini", "2026"),
    "label" to listOf("Penting", "Kerja", "Pribadi", "Faktur", "Rapat"),
)

private val SaranPopuler: List<String> = listOf(
    "dari:Sari",
    "label:Penting",
    "tanggal:bulan ini",
    "dari:Dewi label:Kerja",
)

private val ContohCatatan: List<Catatan> = listOf(
    Catatan(1, "Draft rapat mingguan", "Sari Rahmawati", "30 Sep 2026", "hari ini", "Rapat", "Agenda: demo prototipe dan pembagian tugas ruang Melati."),
    Catatan(2, "Rekap pengeluaran September", "Budi Santoso", "30 Sep 2026", "hari ini", "Faktur", "Lampiran berisi tiga nota pemeliharaan gedung kantor."),
    Catatan(3, "Perubahan jadwal rilis", "Sari Rahmawati", "29 Sep 2026", "minggu ini", "Penting", "Rilis dimajukan satu minggu, mohon tinjau ulang checklist."),
    Catatan(4, "Foto acara kantor", "Dewi Lestari", "28 Sep 2026", "minggu ini", "Pribadi", "Tiga foto ilustrasi gradien dari acara kantor sudah dibagikan."),
    Catatan(5, "Permintaan cuti", "Andi Wijaya", "28 Sep 2026", "minggu ini", "Kerja", "Pengajuan cuti dua hari pada pekan kedua Oktober."),
    Catatan(6, "Vendor cetak undangan", "Rina Marlina", "25 Sep 2026", "bulan ini", "Faktur", "Penawaran harga cetak undangan beserta contoh kertas."),
    Catatan(7, "Notulen rapat produk", "Sari Rahmawati", "22 Sep 2026", "bulan ini", "Rapat", "Keputusan: fitur token pencarian masuk sprint berikutnya."),
    Catatan(8, "Perpanjangan sewa kantor", "Budi Santoso", "18 Sep 2026", "bulan ini", "Penting", "Batas konfirmasi perpanjangan jatuh tempo akhir bulan ini."),
    Catatan(9, "Ide kuis belajar harian", "Dewi Lestari", "12 Sep 2026", "bulan ini", "Kerja", "Lima soal latihan harian untuk menjaga streak belajar."),
    Catatan(10, "Rekap langkah mingguan", "Rina Marlina", "08 Sep 2026", "bulan ini", "Pribadi", "Ringkasan langkah kaki mingguan dari data simulasi kesehatan."),
    Catatan(11, "Anggaran renovasi", "Andi Wijaya", "30 Agu 2026", "2026", "Faktur", "Rincian anggaran renovasi ruang tamu dan cat ulang."),
    Catatan(12, "Arsip rapat kuartal dua", "Sari Rahmawati", "14 Jul 2026", "2026", "Rapat", "Dokumen arsip keputusan kuartal dua untuk referensi."),
    Catatan(13, "Daftar tamu undangan", "Rina Marlina", "02 Jun 2026", "2026", "Penting", "Revisi ketiga daftar tamu undangan beserta nomor kursi."),
    Catatan(14, "Kontrak kerja sama vendor", "Budi Santoso", "21 Apr 2026", "2026", "Kerja", "Berkas kontrak kerja sama sudah ditandatangani kedua pihak."),
    Catatan(15, "Album kenangan liburan", "Dewi Lestari", "05 Mar 2026", "2026", "Pribadi", "Gradien ilustrasi liburan keluarga di pegunungan."),
    Catatan(16, "Laporan audit internal", "Andi Wijaya", "18 Jan 2026", "2026", "Faktur", "Ringkasan temuan audit internal awal tahun beserta tindak lanjut."),
)

private fun cocok(c: Catatan, tokens: List<Token>, teksBebas: String): Boolean {
    tokens.forEach { t ->
        if (t.nilai.isNotBlank()) {
            val ada = when (t.kunci) {
                "dari" -> c.pengirim.contains(t.nilai, ignoreCase = true)
                "tanggal" -> c.relatif.contains(t.nilai, ignoreCase = true) ||
                    c.tanggal.contains(t.nilai, ignoreCase = true)
                "label" -> c.label.contains(t.nilai, ignoreCase = true)
                else -> true
            }
            if (!ada) return false
        }
    }
    if (teksBebas.isNotBlank()) {
        val kata = teksBebas.trim()
        val ada = c.judul.contains(kata, ignoreCase = true) ||
            c.isi.contains(kata, ignoreCase = true) ||
            c.pengirim.contains(kata, ignoreCase = true)
        if (!ada) return false
    }
    return true
}

private fun teksSorot(teks: String, kata: List<String>, warnaTeks: Color, warnaSorot: Color): AnnotatedString =
    buildAnnotatedString {
        append(teks)
        val teksKecil = teks.lowercase()
        kata.filter { it.isNotBlank() }.forEach { k ->
            val kKecil = k.lowercase()
            var idx = teksKecil.indexOf(kKecil)
            while (idx >= 0) {
                addStyle(
                    SpanStyle(
                        color = warnaTeks,
                        background = warnaSorot,
                        fontWeight = FontWeight.Bold,
                    ),
                    start = idx,
                    end = idx + k.length,
                )
                idx = teksKecil.indexOf(kKecil, idx + k.length)
            }
        }
    }

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun SearchTokensLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    val tokens = remember { mutableStateListOf<Token>() }
    var idBerikut by remember { mutableIntStateOf(0) }
    var tokenAktifId by remember { mutableStateOf<Int?>(null) }
    var teksBebas by remember { mutableStateOf("") }

    val tokenAktif = tokens.firstOrNull { it.id == tokenAktifId }
    val teksBidang = tokenAktif?.nilai ?: teksBebas

    val hasil by remember {
        derivedStateOf { ContohCatatan.filter { cocok(it, tokens, teksBebas) } }
    }
    val kataSorot by remember {
        derivedStateOf {
            (tokens.map { it.nilai } + teksBebas)
                .map { it.trim() }
                .filter { it.length >= 2 }
                .distinct()
        }
    }
    val kueriKosong = tokens.isEmpty() && teksBebas.isBlank()

    fun tambahToken(kunci: String) {
        if (tokens.none { it.kunci == kunci }) {
            val token = Token(idBerikut, kunci, "")
            idBerikut++
            tokens.add(token)
            tokenAktifId = token.id
        }
    }

    fun terapkanSaran(s: String) {
        tokens.clear()
        teksBebas = ""
        tokenAktifId = null
        s.split(" ").forEach { segmen ->
            val dipisah = segmen.split(":")
            if (dipisah.size == 2) {
                val token = Token(idBerikut, dipisah[0], dipisah[1])
                idBerikut++
                tokens.add(token)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(palette.background)) {
        UiTopBar(
            title = "Search Token Field",
            subtitle = "Token kunci-nilai di dalam field",
            onBack = onBack,
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "intro") {
                LabIntroCard(
                    title = "Token Kunci-Nilai di Dalam Field",
                    body = "Field ini memuat token seperti dari:, tanggal:, dan label:. Ketuk token untuk mengubah " +
                        "nilainya, tekan backspace saat bidang kosong untuk menghapus token terakhir. Seluruh data " +
                        "adalah contoh lokal (Simulasi), bukan hasil pencarian berbasis server.",
                )
            }

            // Field pencarian berisi token
            item(key = "field-kueri") {
                LabCard {
                    Text(
                        text = "Kueri Pencarian",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = palette.textPrimary,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.surfaceMuted)
                            .border(
                                1.dp,
                                if (tokenAktif != null) palette.primary else palette.border,
                                RoundedCornerShape(12.dp),
                            )
                            .padding(10.dp),
                    ) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            tokens.forEach { t ->
                                val dipilih = t.id == tokenAktifId
                                Surface(
                                    onClick = { tokenAktifId = t.id },
                                    shape = RoundedCornerShape(50),
                                    color = if (dipilih) palette.primaryContainer else palette.surface,
                                    border = BorderStroke(
                                        1.dp,
                                        if (dipilih) palette.primary else palette.border,
                                    ),
                                    shadowElevation = 0.dp,
                                    tonalElevation = 0.dp,
                                    modifier = Modifier.padding(bottom = 6.dp),
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(
                                            start = 10.dp,
                                            top = 5.dp,
                                            bottom = 5.dp,
                                            end = 4.dp,
                                        ),
                                    ) {
                                        Text(
                                            text = "${t.kunci}:",
                                            fontSize = 12.sp,
                                            color = palette.textSecondary,
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = t.nilai.ifBlank { "…?" },
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.textPrimary,
                                        )
                                        if (t.nilai.isBlank()) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "belum lengkap",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = palette.warning,
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(palette.surfaceMuted)
                                                .border(1.dp, palette.border, CircleShape)
                                                .clickable {
                                                    if (tokenAktifId == t.id) tokenAktifId = null
                                                    tokens.remove(t)
                                                }
                                                .semantics {
                                                    role = Role.Button
                                                    contentDescription = "Hapus token ${t.kunci}"
                                                },
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text("✕", fontSize = 9.sp, color = palette.textSecondary)
                                        }
                                    }
                                }
                            }

                            // Bidang teks inline di dalam field
                            Box(modifier = Modifier.widthIn(min = 130.dp).padding(bottom = 6.dp)) {
                                if (teksBidang.isEmpty()) {
                                    Text(
                                        text = if (tokenAktif != null) {
                                            "nilai ${tokenAktif.kunci}…"
                                        } else {
                                            "ketik kata kunci…"
                                        },
                                        fontSize = 13.sp,
                                        color = palette.textMuted,
                                    )
                                }
                                BasicTextField(
                                    value = teksBidang,
                                    onValueChange = { baru ->
                                        if (tokenAktif != null) {
                                            val idx = tokens.indexOfFirst { it.id == tokenAktif.id }
                                            if (idx >= 0) {
                                                tokens[idx] = tokens[idx].copy(nilai = baru)
                                            }
                                        } else {
                                            teksBebas = baru
                                        }
                                    },
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        fontSize = 13.sp,
                                        color = palette.textPrimary,
                                        fontWeight = FontWeight.Medium,
                                    ),
                                    cursorBrush = SolidColor(palette.primary),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            if (tokenAktif != null) {
                                                tokenAktifId = null
                                            }
                                        },
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onPreviewKeyEvent { event ->
                                            if (
                                                event.key == Key.Backspace &&
                                                event.type == KeyEventType.KeyDown &&
                                                teksBidang.isEmpty() &&
                                                tokens.isNotEmpty()
                                            ) {
                                                val dihapus = tokens.removeAt(tokens.lastIndex)
                                                if (tokenAktifId == dihapus.id) {
                                                    tokenAktifId = null
                                                }
                                                true
                                            } else {
                                                false
                                            }
                                        },
                                )
                            }
                        }
                    }
                    Text(
                        text = if (tokenAktif != null) {
                            "Mengubah nilai token «${tokenAktif.kunci}:» — pilih saran atau tekan Selesai. " +
                                "Backspace kosong menghapus token terakhir."
                        } else {
                            "Ketuk token untuk mengubah nilai · backspace pada bidang kosong menghapus token terakhir"
                        },
                        fontSize = 10.sp,
                        color = palette.textMuted,
                    )

                    // Tambah token
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("dari", "tanggal", "label").forEach { kunci ->
                            val bisaDitambah = tokens.none { it.kunci == kunci }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(palette.surface)
                                    .border(1.dp, palette.border, RoundedCornerShape(50))
                                    .clickable(enabled = bisaDitambah) { tambahToken(kunci) }
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                                    .semantics {
                                        role = Role.Button
                                        contentDescription = "Tambah token $kunci"
                                    },
                            ) {
                                Text(
                                    text = "+ $kunci",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (bisaDitambah) palette.textPrimary else palette.textMuted,
                                )
                            }
                        }
                    }
                }
            }

            // Panel saran nilai saat token aktif
            if (tokenAktif != null) {
                item(key = "saran-nilai") {
                    val kandidat = SaranNilai[tokenAktif.kunci].orEmpty()
                        .filter { it.contains(teksBidang, ignoreCase = true) }
                    LabCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Saran nilai untuk «${tokenAktif.kunci}:»",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = palette.textPrimary,
                            )
                            Text(
                                text = "Selesai",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { tokenAktifId = null }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .semantics {
                                        role = Role.Button
                                        contentDescription = "Selesai mengubah token"
                                    },
                            )
                        }
                        if (tokenAktif.nilai.isBlank()) {
                            Text(
                                text = "Token belum lengkap — pilih salah satu saran di bawah atau ketik nilai bebas.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.warning,
                            )
                        }
                        if (kandidat.isEmpty() && teksBidang.isBlank()) {
                            Text(
                                text = "Tidak ada saran tersimpan untuk kunci ini.",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                            )
                        }
                        kandidat.forEach { saran ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(palette.surfaceMuted)
                                    .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                                    .clickable {
                                        val idx = tokens.indexOfFirst { it.id == tokenAktif.id }
                                        if (idx >= 0) {
                                            tokens[idx] = tokens[idx].copy(nilai = saran)
                                        }
                                        tokenAktifId = null
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .semantics {
                                        role = Role.Button
                                        contentDescription =
                                            "Pakai nilai $saran untuk token ${tokenAktif.kunci}"
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "${tokenAktif.kunci}: ",
                                    fontSize = 12.sp,
                                    color = palette.textMuted,
                                )
                                Text(
                                    text = saran,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary,
                                )
                            }
                        }
                        if (teksBidang.isNotBlank() && kandidat.none { it.equals(teksBidang, true) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(palette.primaryContainer)
                                    .border(1.dp, palette.primary, RoundedCornerShape(10.dp))
                                    .clickable {
                                        val idx = tokens.indexOfFirst { it.id == tokenAktif.id }
                                        if (idx >= 0) {
                                            tokens[idx] = tokens[idx].copy(nilai = teksBidang.trim())
                                        }
                                        tokenAktifId = null
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .semantics {
                                        role = Role.Button
                                        contentDescription = "Gunakan nilai bebas ${teksBidang.trim()}"
                                    },
                            ) {
                                Text(
                                    text = "Gunakan nilai bebas «${teksBidang.trim()}»",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.onPrimaryContainer,
                                )
                            }
                        }
                    }
                }
            } else if (kueriKosong) {
                // Saran populer saat kueri kosong
                item(key = "saran-populer") {
                    LabCard {
                        Text(
                            text = "Kueri kosong — saran populer",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = palette.textPrimary,
                        )
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SaranPopuler.forEach { saran ->
                                Box(
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(palette.surfaceMuted)
                                        .border(1.dp, palette.border, RoundedCornerShape(50))
                                        .clickable { terapkanSaran(saran) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .semantics {
                                            role = Role.Button
                                            contentDescription = "Cari dengan $saran"
                                        },
                                ) {
                                    Text(
                                        text = saran,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.textPrimary,
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Ketuk saran untuk memuatnya sebagai token, atau tambah token manual " +
                                "dengan tombol + di atas.",
                            fontSize = 10.sp,
                            color = palette.textMuted,
                        )
                    }
                }
            }

            // Header hasil
            item(key = "header-hasil") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (kueriKosong) "Belum ada kueri aktif" else "${hasil.size} hasil",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(palette.surfaceMuted)
                            .border(1.dp, palette.border, RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = "Simulasi · data lokal",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textMuted,
                        )
                    }
                }
            }

            when {
                kueriKosong -> {
                    item(key = "info-kosong") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Hasil akan muncul di sini secara real-time\nbegitu token atau kata kunci diisi.",
                                fontSize = 12.sp,
                                color = palette.textMuted,
                                lineHeight = 18.sp,
                            )
                        }
                    }
                }
                hasil.isEmpty() -> {
                    item(key = "hasil-kosong") {
                        LabCard {
                            Text(
                                text = "Tidak ada hasil",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = palette.textPrimary,
                            )
                            Text(
                                text = "Kueri saat ini terlalu sempit. Longgarkan token atau hapus salah satu filter.",
                                fontSize = 11.sp,
                                color = palette.textSecondary,
                            )
                            FlatSecondaryButton(
                                text = "Reset Pencarian",
                                onClick = {
                                    tokens.clear()
                                    teksBebas = ""
                                    tokenAktifId = null
                                },
                            )
                        }
                    }
                }
                else -> {
                    items(hasil, key = { it.id }) { c ->
                        val judulSorot = remember(c.id, kataSorot) {
                            teksSorot(c.judul, kataSorot, palette.primary, palette.primaryContainer)
                        }
                        val isiSorot = remember(c.id, kataSorot) {
                            teksSorot(c.isi, kataSorot, palette.primary, palette.primaryContainer)
                        }
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = palette.surface,
                            border = BorderStroke(1.dp, palette.border),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(
                                                    palette.primary.copy(alpha = 0.75f),
                                                    palette.info.copy(alpha = 0.45f),
                                                ),
                                            ),
                                        )
                                        .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = c.pengirim.first().toString(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.onPrimary,
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = judulSorot,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = isiSorot,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        color = palette.textSecondary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${c.pengirim} · ${c.tanggal}",
                                            fontSize = 10.sp,
                                            color = palette.textMuted,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f),
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .background(palette.surfaceMuted)
                                                .border(1.dp, palette.border, RoundedCornerShape(50))
                                                .padding(horizontal = 8.dp, vertical = 3.dp),
                                        ) {
                                            Text(
                                                text = c.label,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = palette.textSecondary,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item(key = "catatan-hasil") {
                        Text(
                            text = "Menampilkan ${hasil.size} dari ${ContohCatatan.size} catatan contoh (Simulasi).",
                            fontSize = 10.sp,
                            color = palette.textMuted,
                        )
                    }
                }
            }
        }
    }
}
