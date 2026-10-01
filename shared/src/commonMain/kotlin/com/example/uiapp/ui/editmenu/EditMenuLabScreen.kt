package com.example.uiapp.ui.editmenu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private const val EM_TEKS_AWAL = "Compose Multiplatform membuat penulisan UI sekali jalan untuk Android dan iOS."

private data class EmChar(
    val ch: Char,
    val tebal: Boolean = false,
    val miring: Boolean = false,
    val garisBawah: Boolean = false,
)

private enum class EmAsync { SIAP, MEMUAT, SUKSES, GAGAL }

private data class EmDefinisi(val kata: String, val kelas: String, val makna: String)

private val EM_KAMUS_TERJEMAHAN = mapOf(
    "compose" to "compose", "multiplatform" to "multiplatform", "membuat" to "makes",
    "penulisan" to "writing", "ui" to "UI", "sekali" to "once", "jalan" to "run",
    "untuk" to "for", "dan" to "and", "android" to "Android", "ios" to "iOS",
    "keyboard" to "keyboard", "teks" to "text", "seleksi" to "selection", "menu" to "menu",
    "kaca" to "glass", "pembesar" to "magnifier", "bidang" to "field", "aplikasi" to "application",
)

private val EM_DEFINISI_MOCK = listOf(
    EmDefinisi("compose", "kata kerja", "Menyusun elemen antarmuka menjadi satu kesatuan tampilan."),
    EmDefinisi("multiplatform", "kata sifat", "Dapat dijalankan di banyak sistem operasi dari satu basis kode."),
    EmDefinisi("penulisan", "kata benda", "Proses menulis; di sini merujuk penulisan kode UI."),
    EmDefinisi("keyboard", "kata benda", "Papan ketik untuk memasukkan teks ke bidang isian."),
    EmDefinisi("seleksi", "kata benda", "Rentang teks yang ditandai untuk diberi aksi lanjutan."),
    EmDefinisi("teks", "kata benda", "Deret karakter yang membentuk tulisan bermakna."),
)

private fun teksMurni(chars: List<EmChar>): String = chars.joinToString(separator = "") { it.ch.toString() }

private fun susunAnnotated(chars: List<EmChar>): AnnotatedString = buildAnnotatedString {
    chars.forEach { item ->
        withStyle(
            SpanStyle(
                fontWeight = if (item.tebal) FontWeight.Bold else FontWeight.Normal,
                fontStyle = if (item.miring) FontStyle.Italic else FontStyle.Normal,
                textDecoration = if (item.garisBawah) TextDecoration.Underline else null,
            ),
        ) {
            append(item.ch)
        }
    }
}

private fun cariKata(teks: String, kueri: String): List<IntRange> {
    if (kueri.isBlank()) return emptyList()
    val hasil = mutableListOf<IntRange>()
    var idx = teks.indexOf(kueri, ignoreCase = true)
    while (idx >= 0) {
        hasil.add(idx until idx + kueri.length)
        idx = teks.indexOf(kueri, startIndex = idx + kueri.length, ignoreCase = true)
    }
    return hasil
}

private fun kataDiIndeks(teks: String, indeks: Int): String {
    if (teks.isEmpty()) return ""
    val aman = indeks.coerceIn(0, teks.length - 1)
    var awal = aman
    while (awal > 0 && !teks[awal - 1].isWhitespace()) awal--
    var akhir = aman
    while (akhir < teks.length - 1 && !teks[akhir + 1].isWhitespace()) akhir++
    return teks.substring(awal, akhir + 1).trim(',', '.', '!', '?', ':', ';')
}

private fun terjemahkanMock(teks: String): String =
    teks.split(Regex("\\s+")).joinToString(" ") { kata ->
        val bersih = kata.trim(',', '.', '!', '?', ':', ';').lowercase()
        val padanan = EM_KAMUS_TERJEMAHAN[bersih]
        if (padanan != null) padanan else "≈${kata.trim(',', '.', '!', '?', ':', ';')}"
    }

private fun definisiMock(kata: String): List<EmDefinisi> {
    val cocok = EM_DEFINISI_MOCK.filter { it.kata.equals(kata, ignoreCase = true) }
    if (cocok.isNotEmpty()) return cocok
    return listOf(
        EmDefinisi(kata, "kata umum", "Definisi mock untuk \"$kata\" — sumber kamus tiruan (Simulasi)."),
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditMenuLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    val chars = remember {
        mutableStateListOf<EmChar>().apply { EM_TEKS_AWAL.forEach { add(EmChar(it)) } }
    }
    var versiTeks by remember { mutableIntStateOf(0) }
    var selAwal by remember { mutableIntStateOf(0) }
    var selAkhir by remember { mutableIntStateOf(0) }
    var caret by remember { mutableIntStateOf(0) }
    var loupeAktif by remember { mutableStateOf(false) }
    var posisiJari by remember { mutableStateOf(Offset.Zero) }
    var menuTampil by remember { mutableStateOf(false) }
    var teksDisalin by remember { mutableStateOf("") }
    var pesan by remember { mutableStateOf("Tekan lama teks untuk loupe & seleksi — Simulasi") }
    var tataLetak by remember { mutableStateOf<TextLayoutResult?>(null) }

    var kueri by remember { mutableStateOf("") }
    var cariAktif by remember { mutableStateOf(false) }

    var kunciTerjemahan by remember { mutableIntStateOf(0) }
    var kunciLookup by remember { mutableIntStateOf(0) }
    var sheetLookup by remember { mutableStateOf(false) }
    var terjemahanState by remember { mutableStateOf(EmAsync.SIAP) }
    var lookupState by remember { mutableStateOf(EmAsync.SIAP) }
    var kataLookup by remember { mutableStateOf("") }
    var hasilTerjemahan by remember { mutableStateOf("") }
    var jaringanSimulasi by remember { mutableStateOf(true) }

    val teks = remember(versiTeks) { teksMurni(chars.toList()) }
    val annotated = remember(versiTeks) { susunAnnotated(chars.toList()) }
    val daftarTemuan = remember(versiTeks, kueri, cariAktif) {
        if (cariAktif) cariKata(teks, kueri) else emptyList()
    }
    val adaSeleksi = selAwal != selAkhir

    LaunchedEffect(kunciTerjemahan) {
        if (kunciTerjemahan > 0) {
            terjemahanState = EmAsync.MEMUAT
            hasilTerjemahan = ""
            delay(750)
            if (jaringanSimulasi) {
                val sumber = if (adaSeleksi) {
                    teks.substring(minOf(selAwal, selAkhir), maxOf(selAwal, selAkhir))
                } else {
                    kataDiIndeks(teks, caret)
                }
                hasilTerjemahan = terjemahkanMock(sumber)
                terjemahanState = EmAsync.SUKSES
                pesan = "Terjemahan mock selesai (Simulasi)"
            } else {
                terjemahanState = EmAsync.GAGAL
                pesan = "Terjemahan gagal — mode luring simulasi aktif"
            }
        }
    }

    LaunchedEffect(kunciLookup) {
        if (kunciLookup > 0) {
            lookupState = EmAsync.MEMUAT
            delay(700)
            lookupState = if (jaringanSimulasi) EmAsync.SUKSES else EmAsync.GAGAL
        }
    }

    fun indeksDari(pos: Offset): Int {
        val tata = tataLetak ?: return caret
        return tata.getOffsetForPosition(pos).coerceIn(0, chars.size)
    }

    fun perbaruiSeleksi(nilai: Int) {
        caret = nilai
        selAkhir = nilai
    }

    fun geserHandleAwal(pos: Offset) {
        selAwal = indeksDari(pos)
        caret = selAwal
        posisiJari = pos
        loupeAktif = true
    }

    fun geserHandleAkhir(pos: Offset) {
        selAkhir = indeksDari(pos)
        caret = selAkhir
        posisiJari = pos
        loupeAktif = true
    }

    fun pilihKataDi(pos: Offset) {
        val idx = indeksDari(pos)
        val kata = kataDiIndeks(teks, idx)
        if (kata.isEmpty()) {
            selAwal = idx
            selAkhir = idx
        } else {
            val mulai = teks.indexOf(kata, startIndex = (idx - kata.length).coerceAtLeast(0), ignoreCase = true)
            selAwal = if (mulai >= 0) mulai else idx
            selAkhir = (selAwal + kata.length).coerceAtMost(chars.size)
        }
        caret = selAkhir
    }

    fun aksiSalin() {
        if (!adaSeleksi) return
        teksDisalin = teks.substring(minOf(selAwal, selAkhir), maxOf(selAwal, selAkhir))
        pesan = "Disalin ke papan klip simulasi (${teksDisalin.length} karakter)"
        menuTampil = false
    }

    fun aksiPotong() {
        if (!adaSeleksi) return
        val a = minOf(selAwal, selAkhir)
        val b = maxOf(selAwal, selAkhir)
        teksDisalin = teks.substring(a, b)
        chars.subList(a, b).clear()
        versiTeks += 1
        selAwal = a
        selAkhir = a
        caret = a
        menuTampil = false
        pesan = "Teks dipotong ke papan klip simulasi"
    }

    fun aksiTempel() {
        if (teksDisalin.isEmpty()) return
        val a = minOf(selAwal, selAkhir)
        val b = maxOf(selAwal, selAkhir)
        chars.subList(a, b).clear()
        chars.addAll(a, teksDisalin.map { EmChar(it) })
        versiTeks += 1
        selAwal = a
        selAkhir = a + teksDisalin.length
        caret = selAkhir
        menuTampil = false
        pesan = "Teks ditempel dari papan klip simulasi"
    }

    fun aksiGaya(properti: String) {
        if (!adaSeleksi) return
        val a = minOf(selAwal, selAkhir)
        val b = maxOf(selAwal, selAkhir)
        when (properti) {
            "tebal" -> {
                val baru = !chars[a].tebal
                for (i in a until b) chars[i] = chars[i].copy(tebal = baru)
                pesan = if (baru) "Seleksi ditebalkan" else "Tebal dibatalkan"
            }
            "miring" -> {
                val baru = !chars[a].miring
                for (i in a until b) chars[i] = chars[i].copy(miring = baru)
                pesan = if (baru) "Seleksi dimiringkan" else "Miring dibatalkan"
            }
            else -> {
                val baru = !chars[a].garisBawah
                for (i in a until b) chars[i] = chars[i].copy(garisBawah = baru)
                pesan = if (baru) "Garis bawah ditambahkan" else "Garis bawah dihapus"
            }
        }
        versiTeks += 1
    }

    fun aksiCari() {
        kueri = if (adaSeleksi) {
            teks.substring(minOf(selAwal, selAkhir), maxOf(selAwal, selAkhir))
        } else {
            kataDiIndeks(teks, caret)
        }
        cariAktif = true
        menuTampil = false
        pesan = if (kueri.isEmpty()) "Tidak ada kata untuk dicari" else "Mencari \"$kueri\" dalam dokumen"
    }

    fun aksiLookUp() {
        kataLookup = if (adaSeleksi) {
            teks.substring(minOf(selAwal, selAkhir), maxOf(selAwal, selAkhir)).trim()
        } else {
            kataDiIndeks(teks, caret)
        }
        if (kataLookup.isEmpty()) kataLookup = "teks"
        sheetLookup = true
        kunciLookup += 1
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Text Loupe & Edit Menu",
                subtitle = "Kaca pembesar, caret, menu teks",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = palette.surfaceMuted,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Text Loupe & Edit Menu", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        Text(
                            "Tekan lama teks untuk memunculkan kaca pembesar (loupe) yang mengikuti jari, geser dua handle seleksi, lalu jalankan aksi dari menu mengambang. Seluruh interaksi disimulasikan di dalam Compose.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = palette.textSecondary,
                        )
                        Text("Label: Simulasi", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.info)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Dokumen Teks (Ketuk / Tekan Lama)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.surfaceMuted)
                                .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = { pos ->
                                                val idx = indeksDari(pos)
                                                caret = idx
                                                selAwal = idx
                                                selAkhir = idx
                                                menuTampil = true
                                                pesan = "Caret dipindah ke karakter ke-${idx + 1}"
                                            },
                                            onDoubleTap = { pos ->
                                                pilihKataDi(pos)
                                                menuTampil = true
                                                pesan = "Kata dipilih otomatis"
                                            },
                                        )
                                    }
                                    .pointerInput(Unit) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = { pos ->
                                                val idx = indeksDari(pos)
                                                selAwal = idx
                                                selAkhir = idx
                                                caret = idx
                                                posisiJari = pos
                                                loupeAktif = true
                                                menuTampil = false
                                            },
                                            onDrag = { change, _ ->
                                                change.consume()
                                                posisiJari = change.position
                                                perbaruiSeleksi(indeksDari(change.position))
                                            },
                                            onDragEnd = {
                                                loupeAktif = false
                                                menuTampil = true
                                                pesan = "Seleksi aktif: ${maxOf(selAwal, selAkhir) - minOf(selAwal, selAkhir)} karakter"
                                            },
                                            onDragCancel = {
                                                loupeAktif = false
                                                pesan = "Seleksi dibatalkan"
                                            },
                                        )
                                    },
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val tata = tataLetak ?: return@Canvas
                                    daftarTemuan.forEach { rentang ->
                                        for (i in rentang) {
                                            if (i in 0 until chars.size) {
                                                val kotak = tata.getBoundingBox(i)
                                                drawRect(
                                                    color = palette.warning.copy(alpha = 0.28f),
                                                    topLeft = Offset(kotak.left, kotak.top),
                                                    size = Size(kotak.width, kotak.height),
                                                )
                                            }
                                        }
                                    }
                                    if (adaSeleksi) {
                                        val a = minOf(selAwal, selAkhir)
                                        val b = maxOf(selAwal, selAkhir)
                                        for (i in a until b) {
                                            if (i in 0 until chars.size) {
                                                val kotak = tata.getBoundingBox(i)
                                                drawRect(
                                                    color = palette.primary.copy(alpha = 0.22f),
                                                    topLeft = Offset(kotak.left, kotak.top),
                                                    size = Size(kotak.width, kotak.height),
                                                )
                                            }
                                        }
                                    }
                                    val rc = tata.getCursorRect(caret.coerceIn(0, chars.size))
                                    drawRect(
                                        color = palette.primary,
                                        topLeft = Offset(rc.left - 1.dp.toPx(), rc.top),
                                        size = Size(2.dp.toPx(), rc.height),
                                    )
                                }

                                Text(
                                    text = annotated,
                                    color = palette.textPrimary,
                                    style = TextStyle(fontSize = 16.sp, lineHeight = 26.sp),
                                    onTextLayout = { tataLetak = it },
                                    modifier = Modifier.fillMaxWidth(),
                                )

                                val tata = tataLetak
                                if (tata != null && adaSeleksi) {
                                    EmHandle(
                                        titik = tata.getCursorRect(minOf(selAwal, selAkhir).coerceIn(0, chars.size)).bottomLeft,
                                        label = "Handle seleksi awal",
                                        onGeser = { pos -> geserHandleAwal(pos) },
                                        onLepas = {
                                            loupeAktif = false
                                            if (selAwal > selAkhir) {
                                                val t = selAwal
                                                selAwal = selAkhir
                                                selAkhir = t
                                            }
                                            menuTampil = true
                                        },
                                    )
                                    EmHandle(
                                        titik = tata.getCursorRect(maxOf(selAwal, selAkhir).coerceIn(0, chars.size)).bottomRight,
                                        label = "Handle seleksi akhir",
                                        onGeser = { pos -> geserHandleAkhir(pos) },
                                        onLepas = {
                                            loupeAktif = false
                                            if (selAwal > selAkhir) {
                                                val t = selAwal
                                                selAwal = selAkhir
                                                selAkhir = t
                                            }
                                            menuTampil = true
                                        },
                                    )
                                }

                                if (loupeAktif && tata != null) {
                                    EmLoupe(indeks = caret, teks = teks, pusat = posisiJari)
                                }

                                if (menuTampil && tata != null) {
                                    val rc = tata.getCursorRect(caret.coerceIn(0, chars.size))
                                    EmMenuAksi(
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .offset {
                                                IntOffset(0, (rc.top - 96.dp.toPx()).coerceAtLeast(0f).toInt())
                                            },
                                        adaSeleksi = adaSeleksi,
                                        clipboardKosong = teksDisalin.isEmpty(),
                                        onPotong = { aksiPotong() },
                                        onSalin = { aksiSalin() },
                                        onTempel = { aksiTempel() },
                                        onTebal = { aksiGaya("tebal") },
                                        onMiring = { aksiGaya("miring") },
                                        onGarisBawah = { aksiGaya("garis") },
                                        onCari = { aksiCari() },
                                        onTerjemahkan = {
                                            menuTampil = false
                                            kunciTerjemahan += 1
                                        },
                                    )
                                }
                            }
                        }

                        Text(pesan, fontSize = 11.sp, color = palette.textSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            EmAksiKecil(label = "Look Up", enabled = true, onClick = { aksiLookUp() })
                            EmAksiKecil(
                                label = "Bersihkan Seleksi",
                                enabled = adaSeleksi,
                                onClick = {
                                    selAwal = caret
                                    selAkhir = caret
                                    menuTampil = false
                                    pesan = "Seleksi dibersihkan"
                                },
                            )
                            Text(
                                if (teksDisalin.isEmpty()) "Papan klip: kosong" else "Papan klip: ${teksDisalin.length} karakter",
                                fontSize = 10.sp,
                                color = palette.textMuted,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Mode luring (memicu state gagal)", fontSize = 11.sp, color = palette.textSecondary)
                            EmSakelar(aktif = !jaringanSimulasi, onToggle = { jaringanSimulasi = !jaringanSimulasi })
                        }
                    }
                }

                if (cariAktif) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    "Hasil Pencarian \"$kueri\"",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = palette.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f),
                                )
                                EmAksiKecil(
                                    label = "Tutup",
                                    enabled = true,
                                    onClick = {
                                        cariAktif = false
                                        pesan = "Pencarian ditutup"
                                    },
                                )
                            }

                            when {
                                kueri.isEmpty() -> Text("Masukkan kata kunci untuk mencari", fontSize = 11.sp, color = palette.textMuted)
                                daftarTemuan.isEmpty() -> Text(
                                    "Tidak ada hasil — state kosong",
                                    fontSize = 11.sp,
                                    color = palette.textMuted,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(palette.surfaceMuted)
                                        .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                )
                                else -> {
                                    Text("${daftarTemuan.size} temuan (sorot kuning pada dokumen)", fontSize = 11.sp, color = palette.textSecondary)
                                    daftarTemuan.take(3).forEachIndexed { idx, rentang ->
                                        val a = (rentang.first - 12).coerceAtLeast(0)
                                        val b = (rentang.last + 13).coerceAtMost(teks.length)
                                        Text(
                                            "${idx + 1}. …${teks.substring(a, b)}…",
                                            fontSize = 11.sp,
                                            color = palette.textSecondary,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Terjemahan (Simulasi)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                        when (terjemahanState) {
                            EmAsync.SIAP -> Text(
                                "Pilih teks lalu tekan \"Terjemahkan\" pada menu mengambang",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                            )
                            EmAsync.MEMUAT -> Text("Menerjemahkan… (Simulasi)", fontSize = 11.sp, color = palette.info)
                            EmAsync.SUKSES -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Berhasil — padanan mock:", fontSize = 10.sp, color = palette.success, fontWeight = FontWeight.Bold)
                                Text(hasilTerjemahan, fontSize = 12.sp, color = palette.textPrimary)
                            }
                            EmAsync.GAGAL -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Terjemahan gagal (mode luring simulasi)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.danger)
                                EmAksiKecil(label = "Coba Lagi", enabled = true, onClick = { kunciTerjemahan += 1 })
                            }
                        }
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
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Catatan Text Loupe & Edit Menu:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                        Text("• Loupe mengikuti jari saat drag, lengkap dengan caret dan dua handle seleksi", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Menu mengambang: Potong, Salin, Tempel, Tebal, Miring, Garis bawah, Cari, Terjemahkan", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Sheet Look Up menampilkan definisi mock — berbeda dari selection_toolbar yang menyeleksi item daftar", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Tanpa clipboard sistem, kamus, atau penerjemah sungguhan — seluruhnya Simulasi", fontSize = 12.sp, color = palette.textSecondary)
                    }
                }
            }

            if (sheetLookup) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(palette.textPrimary.copy(alpha = 0.45f))
                        .clickable { sheetLookup = false },
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .background(palette.surface)
                            .border(1.dp, palette.border, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .clickable { /* menahan tap agar tidak menutup sheet */ }
                            .navigationBarsPadding()
                            .imePadding()
                            .heightIn(max = 380.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text("Look Up", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                                Text("Kata: \"$kataLookup\" — Simulasi", fontSize = 11.sp, color = palette.textMuted)
                            }
                            EmAksiKecil(label = "Tutup", enabled = true, onClick = { sheetLookup = false })
                        }

                        when (lookupState) {
                            EmAsync.SIAP, EmAsync.MEMUAT -> Text("Mencari definisi… (Simulasi)", fontSize = 11.sp, color = palette.info)
                            EmAsync.GAGAL -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Definisi gagal dimuat (mode luring simulasi)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.danger)
                                EmAksiKecil(label = "Coba Lagi", enabled = true, onClick = { kunciLookup += 1 })
                            }
                            EmAsync.SUKSES -> definisiMock(kataLookup).forEach { def ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(palette.surfaceMuted)
                                        .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Text("${def.kata} — ${def.kelas}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                    Text(def.makna, fontSize = 11.sp, color = palette.textSecondary, lineHeight = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmLoupe(indeks: Int, teks: String, pusat: Offset) {
    val palette = LocalAppPalette.current
    val awal = (indeks - 4).coerceIn(0, (teks.length - 9).coerceAtLeast(0))
    val akhir = (awal + 9).coerceAtMost(teks.length)
    val potongan = if (teks.isEmpty()) "" else teks.substring(awal, akhir)
    val posisiCaret = (indeks - awal).coerceIn(0, potongan.length)

    Box(
        modifier = Modifier
            .offset { IntOffset((pusat.x - 55.dp.toPx()).toInt(), (pusat.y - 120.dp.toPx()).toInt()) }
            .size(110.dp)
            .clip(CircleShape)
            .background(palette.surface)
            .border(1.5.dp, palette.primary, CircleShape)
            .semantics { contentDescription = "Kaca pembesar teks" },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = buildAnnotatedString {
                append(potongan.substring(0, posisiCaret))
                withStyle(SpanStyle(background = palette.primary.copy(alpha = 0.30f))) {
                    append(if (posisiCaret < potongan.length) potongan[posisiCaret].toString() else " ")
                }
                if (posisiCaret < potongan.length) append(potongan.substring(posisiCaret + 1))
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
    }
}

@Composable
private fun EmHandle(
    titik: Offset,
    label: String,
    onGeser: (Offset) -> Unit,
    onLepas: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Box(
        modifier = Modifier
            .offset { IntOffset((titik.x - 22.dp.toPx()).toInt(), (titik.y - 22.dp.toPx()).toInt()) }
            .size(44.dp)
            .pointerInput(titik) {
                val dasar = Offset(titik.x - 22.dp.toPx(), titik.y - 22.dp.toPx())
                detectDragGestures(
                    onDragStart = { posisi -> onGeser(posisi + dasar) },
                    onDrag = { change, _ ->
                        change.consume()
                        onGeser(change.position + dasar)
                    },
                    onDragEnd = { onLepas() },
                    onDragCancel = { onLepas() },
                )
            }
            .semantics {
                role = Role.Button
                contentDescription = label
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(palette.primary)
                .border(1.dp, palette.surface, CircleShape),
        )
    }
}

@Composable
private fun EmMenuAksi(
    modifier: Modifier,
    adaSeleksi: Boolean,
    clipboardKosong: Boolean,
    onPotong: () -> Unit,
    onSalin: () -> Unit,
    onTempel: () -> Unit,
    onTebal: () -> Unit,
    onMiring: () -> Unit,
    onGarisBawah: () -> Unit,
    onCari: () -> Unit,
    onTerjemahkan: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                EmItemMenu("Potong", modifier = Modifier.weight(1f), enabled = adaSeleksi, onClick = onPotong)
                EmItemMenu("Salin", modifier = Modifier.weight(1f), enabled = adaSeleksi, onClick = onSalin)
                EmItemMenu("Tempel", modifier = Modifier.weight(1f), enabled = !clipboardKosong, onClick = onTempel)
                EmItemMenu("Tebal", modifier = Modifier.weight(1f), enabled = adaSeleksi, onClick = onTebal)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                EmItemMenu("Miring", modifier = Modifier.weight(1f), enabled = adaSeleksi, onClick = onMiring)
                EmItemMenu("Garis bawah", modifier = Modifier.weight(1f), enabled = adaSeleksi, onClick = onGarisBawah)
                EmItemMenu("Cari", modifier = Modifier.weight(1f), enabled = true, onClick = onCari)
                EmItemMenu("Terjemahkan", modifier = Modifier.weight(1f), enabled = true, onClick = onTerjemahkan)
            }
        }
    }
}

@Composable
private fun EmItemMenu(label: String, modifier: Modifier, enabled: Boolean, onClick: () -> Unit) {
    val palette = LocalAppPalette.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (enabled) palette.surfaceMuted else palette.background)
            .border(1.dp, palette.border, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .semantics {
                role = Role.Button
                contentDescription = label + if (enabled) "" else ", tidak tersedia"
            },
    ) {
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) palette.textPrimary else palette.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun EmAksiKecil(label: String, enabled: Boolean, onClick: () -> Unit) {
    val palette = LocalAppPalette.current

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (enabled) palette.primary else palette.surfaceMuted)
            .border(1.dp, if (enabled) palette.primary else palette.border, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .semantics {
                role = Role.Button
                contentDescription = label + if (enabled) "" else ", tidak tersedia"
            },
    ) {
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) palette.onPrimary else palette.textMuted,
        )
    }
}

@Composable
private fun EmSakelar(aktif: Boolean, onToggle: () -> Unit) {
    val palette = LocalAppPalette.current

    Box(
        modifier = Modifier
            .width(46.dp)
            .height(26.dp)
            .clip(CircleShape)
            .background(if (aktif) palette.danger.copy(alpha = 0.25f) else palette.surfaceMuted)
            .border(1.dp, if (aktif) palette.danger else palette.border, CircleShape)
            .clickable { onToggle() }
            .semantics {
                role = Role.Button
                contentDescription = if (aktif) "Matikan mode luring" else "Nyalakan mode luring"
            },
    ) {
        Box(
            modifier = Modifier
                .align(if (aktif) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(3.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(if (aktif) palette.danger else palette.textMuted),
        )
    }
}
