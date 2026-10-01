package com.example.uiapp.ui.keyboardaccessory

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToLong

private data class KaBidang(val id: Int, val label: String, val hint: String)

private enum class KaMode(val label: String) {
    TEKS("Teks"),
    ANGKA("Angka"),
}

private data class KaHitung(val sukses: Boolean, val hasil: String, val error: String? = null)

private val KA_BIDANG = listOf(
    KaBidang(0, "Nama Pemesan", "Contoh: Sari Wulandari"),
    KaBidang(1, "Catatan Pesanan", "Contoh: Tanpa pedas, extra kuah"),
    KaBidang(2, "Nominal Uang Muka", "Contoh: 25000"),
)

private val KA_BARIS_HURUF = listOf("QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM")

private val KA_KAMUS_SARAN = listOf(
    "keyboard", "keypad", "kustom", "komponen", "komposisi", "kotak",
    "aksesori", "antarmuka", "aplikasi", "android", "arsitektur",
    "prediktif", "pemesanan", "pesanan", "perhitungan", "panah",
    "bidang", "baris", "bold", "berikutnya", "berikut",
)

private val KA_RIWAYAT_AWAL = listOf("12000 + 5000 = 17000", "25000 ÷ 4 = 6250")

private fun saranKata(prefix: String): List<String> {
    val kata = prefix.trim()
    if (kata.isEmpty()) return KA_KAMUS_SARAN.take(3)
    return KA_KAMUS_SARAN.filter { it.startsWith(kata, ignoreCase = true) }.take(3)
}

private fun hitungEkspresi(ekspresi: String): KaHitung {
    val bersih = ekspresi.trim()
    if (bersih.isEmpty()) return KaHitung(false, "", "Ekspresi masih kosong")
    if (!bersih.last().isDigit()) return KaHitung(false, "", "Ekspresi belum lengkap")

    val angka = mutableListOf<Double>()
    val operator = mutableListOf<Char>()
    val buffer = StringBuilder()

    for (ch in bersih) {
        when {
            ch.isDigit() || ch == '.' -> buffer.append(ch)
            ch == '+' || ch == '-' || ch == '−' || ch == '×' || ch == '÷' -> {
                if (buffer.isEmpty()) return KaHitung(false, "", "Format ekspresi salah")
                val nilai = buffer.toString().toDoubleOrNull() ?: return KaHitung(false, "", "Format ekspresi salah")
                angka.add(nilai)
                buffer.clear()
                operator.add(
                    when (ch) {
                        '−' -> '-'
                        '×' -> '*'
                        '÷' -> '/'
                        else -> ch
                    },
                )
            }
            else -> return KaHitung(false, "", "Karakter tidak dikenal: '$ch'")
        }
    }

    if (buffer.isEmpty()) return KaHitung(false, "", "Format ekspresi salah")
    val nilaiAkhir = buffer.toString().toDoubleOrNull() ?: return KaHitung(false, "", "Format ekspresi salah")
    angka.add(nilaiAkhir)

    var hasil = angka.first()
    for (i in operator.indices) {
        val kanan = angka[i + 1]
        hasil = when (operator[i]) {
            '+' -> hasil + kanan
            '-' -> hasil - kanan
            '*' -> hasil * kanan
            '/' -> {
                if (abs(kanan) < 0.0000001) return KaHitung(false, "", "Tidak bisa dibagi nol")
                hasil / kanan
            }
            else -> return KaHitung(false, "", "Operator tidak dikenal")
        }
    }

    val teksHasil = if (abs(hasil - hasil.roundToLong()) < 0.0000001) {
        hasil.roundToLong().toString()
    } else {
        ((hasil * 100).roundToLong() / 100.0).toString()
    }
    return KaHitung(true, teksHasil)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun KeyboardAccessoryLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var bidangAktif by remember { mutableIntStateOf(0) }
    val teksBidang = remember { mutableStateListOf("", "", "") }
    var keyboardTerbuka by remember { mutableStateOf(true) }
    var mode by remember { mutableStateOf(KaMode.TEKS) }
    var tebalAktif by remember { mutableStateOf(false) }
    var miringAktif by remember { mutableStateOf(false) }
    var caretTampil by remember { mutableStateOf(true) }
    var ekspresi by remember { mutableStateOf("") }
    var hasilHitung by remember { mutableStateOf<KaHitung?>(null) }
    val riwayatHitung = remember { mutableStateListOf<String>().apply { addAll(KA_RIWAYAT_AWAL) } }
    var pesanStatus by remember { mutableStateOf("Ketuk bidang untuk memfokuskan — keyboard simulasi") }

    val kataAktif = if (bidangAktif in teksBidang.indices) teksBidang[bidangAktif].substringAfterLast(' ') else ""
    val daftarSaran = remember(kataAktif) { saranKata(kataAktif) }

    LaunchedEffect(bidangAktif, keyboardTerbuka) {
        caretTampil = true
        while (keyboardTerbuka && bidangAktif >= 0) {
            delay(520)
            caretTampil = !caretTampil
        }
    }

    fun ketikHuruf(ch: Char) {
        if (bidangAktif !in teksBidang.indices) return
        teksBidang[bidangAktif] = teksBidang[bidangAktif] + ch
        pesanStatus = "Mengetik pada \"${KA_BIDANG[bidangAktif].label}\" — Simulasi"
    }

    fun hapusHuruf() {
        if (bidangAktif !in teksBidang.indices) return
        val sekarang = teksBidang[bidangAktif]
        if (sekarang.isNotEmpty()) teksBidang[bidangAktif] = sekarang.dropLast(1)
        pesanStatus = "Menghapus karakter terakhir"
    }

    fun pilihSaran(saran: String) {
        if (bidangAktif !in teksBidang.indices) return
        val sekarang = teksBidang[bidangAktif]
        val potong = sekarang.substringBeforeLast(' ', missingDelimiterValue = "")
        teksBidang[bidangAktif] = if (potong.isEmpty()) saran else "$potong $saran "
        pesanStatus = "Saran \"$saran\" dipasang pada bidang aktif"
    }

    fun tekanTombolAngka(tombol: String) {
        when (tombol) {
            "C" -> {
                ekspresi = ""
                hasilHitung = null
                pesanStatus = "Ekspresi direset"
            }
            "⌫" -> {
                if (ekspresi.isNotEmpty()) ekspresi = ekspresi.dropLast(1)
                pesanStatus = "Satu simbol ekspresi dihapus"
            }
            "=" -> {
                val hasil = hitungEkspresi(ekspresi)
                hasilHitung = hasil
                if (hasil.sukses) {
                    riwayatHitung.add(0, "$ekspresi = ${hasil.hasil}")
                    pesanStatus = "Perhitungan berhasil: ${hasil.hasil}"
                } else {
                    pesanStatus = "Perhitungan gagal: ${hasil.error ?: "tidak diketahui"}"
                }
            }
            else -> {
                ekspresi += tombol
                pesanStatus = "Ekspresi: $ekspresi"
            }
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Keyboard Accessory & Keypad",
                subtitle = "Toolbar di atas keyboard — Simulasi",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
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
                        Text(
                            "Keyboard Accessory & Keypad Kustom",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                        )
                        Text(
                            "Seluruh keyboard, toolbar, dan keypad di lab ini adalah simulasi di dalam Compose, bukan keyboard sistem. Tidak ada input sungguhan ke sistem.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = palette.textSecondary,
                        )
                        Text(
                            "Label: Simulasi",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.info,
                        )
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
                        Text("Bidang Form (Demo)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)

                        KA_BIDANG.forEachIndexed { idx, bidang ->
                            KaBidangKartu(
                                label = bidang.label,
                                hint = bidang.hint,
                                nilai = teksBidang[idx],
                                aktif = bidangAktif == idx,
                                tebal = tebalAktif && bidangAktif == idx,
                                miring = miringAktif && bidangAktif == idx,
                                caretTampil = caretTampil && bidangAktif == idx && keyboardTerbuka,
                                onFokus = {
                                    bidangAktif = idx
                                    keyboardTerbuka = true
                                    pesanStatus = "Bidang \"${bidang.label}\" aktif — toolbar mengikutinya"
                                },
                            )
                        }

                        Text(
                            text = pesanStatus,
                            fontSize = 11.sp,
                            color = palette.textSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                if (mode == KaMode.ANGKA) {
                    KaRiwayatCard(
                        riwayat = riwayatHitung,
                        onKosongkan = {
                            riwayatHitung.clear()
                            pesanStatus = "Riwayat perhitungan dikosongkan"
                        },
                    )
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
                        Text("Manfaat Pola Keyboard Accessory:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                        Text("• Toolbar aksesori mengikuti bidang aktif dan tetap tampil di atas keyboard", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Saran prediktif mempercepat pengetikan dan dapat kosong bila tak ada padanan", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Keypad kustom mengikuti kebutuhan konten (angka + aritmetika + riwayat)", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Toolbar memakai imePadding sehingga tidak tertutup keyboard sungguhan di perangkat", fontSize = 12.sp, color = palette.textSecondary)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding(),
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))

                if (keyboardTerbuka) {
                    KaToolbar(
                        adaBidangAktif = bidangAktif >= 0,
                        mode = mode,
                        tebalAktif = tebalAktif,
                        miringAktif = miringAktif,
                        onSelesai = {
                            bidangAktif = -1
                            keyboardTerbuka = false
                            pesanStatus = "Keyboard simulasi ditutup — state keyboard tertutup"
                        },
                        onMundur = {
                            if (bidangAktif > 0) {
                                bidangAktif -= 1
                                pesanStatus = "Pindah ke \"${KA_BIDANG[bidangAktif].label}\""
                            }
                        },
                        onMaju = {
                            if (bidangAktif in 0 until KA_BIDANG.lastIndex) {
                                bidangAktif += 1
                                pesanStatus = "Pindah ke \"${KA_BIDANG[bidangAktif].label}\""
                            }
                        },
                        onTebal = {
                            tebalAktif = !tebalAktif
                            pesanStatus = "Format tebal ${if (tebalAktif) "aktif" else "nonaktif"}"
                        },
                        onMiring = {
                            miringAktif = !miringAktif
                            pesanStatus = "Format miring ${if (miringAktif) "aktif" else "nonaktif"}"
                        },
                        onGantiMode = {
                            mode = if (mode == KaMode.TEKS) KaMode.ANGKA else KaMode.TEKS
                            pesanStatus = "Berpindah ke keypad ${mode.label}"
                        },
                    )

                    when (mode) {
                        KaMode.TEKS -> {
                            KaSaranBar(
                                saran = daftarSaran,
                                onPilih = { pilihSaran(it) },
                            )
                            KaKeyboardHuruf(
                                onHuruf = { ketikHuruf(it) },
                                onHapus = { hapusHuruf() },
                                onSpasi = { ketikHuruf(' ') },
                            )
                        }
                        KaMode.ANGKA -> {
                            KaKeypadAngka(
                                ekspresi = ekspresi,
                                hasil = hasilHitung,
                                onTombol = { tekanTombolAngka(it) },
                            )
                        }
                    }
                } else {
                    KaPanelTertutup(onBuka = {
                        bidangAktif = if (bidangAktif < 0) 0 else bidangAktif
                        keyboardTerbuka = true
                        pesanStatus = "Keyboard simulasi dibuka kembali"
                    })
                }
            }
        }
    }
}

@Composable
private fun KaBidangKartu(
    label: String,
    hint: String,
    nilai: String,
    aktif: Boolean,
    tebal: Boolean,
    miring: Boolean,
    caretTampil: Boolean,
    onFokus: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (aktif) palette.primaryContainer.copy(alpha = 0.45f) else palette.surfaceMuted)
            .border(
                BorderStroke(if (aktif) 1.5.dp else 1.dp, if (aktif) palette.primary else palette.border),
                RoundedCornerShape(12.dp),
            )
            .clickable { onFokus() }
            .padding(12.dp)
            .semantics {
                role = Role.Button
                contentDescription = "Bidang $label, ${if (aktif) "aktif" else "tidak aktif"}"
            },
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (aktif) palette.primary else palette.textSecondary)
            if (aktif) {
                Text("Bidang aktif", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.primary)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (nilai.isEmpty()) {
                Text(hint, fontSize = 13.sp, color = palette.textMuted)
            } else {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = if (tebal) FontWeight.Bold else FontWeight.Normal,
                                fontStyle = if (miring) FontStyle.Italic else FontStyle.Normal,
                            ),
                        ) { append(nilai) }
                    },
                    fontSize = 14.sp,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
            if (caretTampil) {
                Spacer(modifier = Modifier.width(1.dp))
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(18.dp)
                        .background(palette.primary),
                )
            }
        }
    }
}

@Composable
private fun KaToolbar(
    adaBidangAktif: Boolean,
    mode: KaMode,
    tebalAktif: Boolean,
    miringAktif: Boolean,
    onSelesai: () -> Unit,
    onMundur: () -> Unit,
    onMaju: () -> Unit,
    onTebal: () -> Unit,
    onMiring: () -> Unit,
    onGantiMode: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.surface)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Surface(
            onClick = onSelesai,
            enabled = adaBidangAktif,
            shape = RoundedCornerShape(10.dp),
            color = if (adaBidangAktif) palette.primary else palette.surfaceMuted,
            contentColor = if (adaBidangAktif) palette.onPrimary else palette.textMuted,
            border = BorderStroke(1.dp, if (adaBidangAktif) palette.primary else palette.border),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
            modifier = Modifier.semantics {
                role = Role.Button
                contentDescription = "Selesai mengedit bidang aktif"
            },
        ) {
            Text(
                "Selesai",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (adaBidangAktif) palette.onPrimary else palette.textMuted,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }

        KaTombolIkon(label = "‹", deskripsi = "Bidang sebelumnya", enabled = adaBidangAktif, onClick = onMundur)
        KaTombolIkon(label = "›", deskripsi = "Bidang berikutnya", enabled = adaBidangAktif, onClick = onMaju)

        KaTombolIkon(
            label = "B",
            deskripsi = "Format tebal",
            enabled = adaBidangAktif && mode == KaMode.TEKS,
            aktif = tebalAktif,
            onClick = onTebal,
        )
        KaTombolIkon(
            label = "I",
            deskripsi = "Format miring",
            enabled = adaBidangAktif && mode == KaMode.TEKS,
            aktif = miringAktif,
            onClick = onMiring,
        )

        Spacer(modifier = Modifier.weight(1f))

        Surface(
            onClick = onGantiMode,
            shape = RoundedCornerShape(10.dp),
            color = palette.surfaceMuted,
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
            modifier = Modifier.semantics {
                role = Role.Button
                contentDescription = "Ganti ke keypad ${if (mode == KaMode.TEKS) "angka" else "huruf"}"
            },
        ) {
            Text(
                if (mode == KaMode.TEKS) "123" else "ABC",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun KaTombolIkon(
    label: String,
    deskripsi: String,
    enabled: Boolean,
    onClick: () -> Unit,
    aktif: Boolean = false,
) {
    val palette = LocalAppPalette.current
    val warnaLatar = when {
        aktif -> palette.primary
        enabled -> palette.surfaceMuted
        else -> palette.background
    }
    val warnaTeks = when {
        aktif -> palette.onPrimary
        enabled -> palette.textPrimary
        else -> palette.textMuted
    }

    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(warnaLatar)
            .border(1.dp, if (aktif) palette.primary else palette.border, RoundedCornerShape(10.dp))
            .clickable(enabled = enabled) { onClick() }
            .semantics {
                role = Role.Button
                contentDescription = deskripsi + if (enabled) "" else ", tidak tersedia"
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = warnaTeks)
    }
}

@Composable
private fun KaSaranBar(saran: List<String>, onPilih: (String) -> Unit) {
    val palette = LocalAppPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.surface)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (saran.isEmpty()) {
            Text(
                "Tidak ada saran — ketik kata lain",
                fontSize = 11.sp,
                color = palette.textMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.surfaceMuted)
                    .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                    .padding(vertical = 10.dp, horizontal = 12.dp)
                    .semantics { contentDescription = "Saran prediktif kosong" },
            )
        } else {
            saran.forEach { kata ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                        .clickable { onPilih(kata) }
                        .padding(vertical = 10.dp)
                        .semantics {
                            role = Role.Button
                            contentDescription = "Gunakan saran $kata"
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        kata,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun KaKeyboardHuruf(
    onHuruf: (Char) -> Unit,
    onHapus: () -> Unit,
    onSpasi: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.surfaceMuted)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        KA_BARIS_HURUF.forEach { baris ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                baris.forEach { huruf ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.surface)
                            .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                            .clickable { onHuruf(huruf) }
                            .semantics { role = Role.Button },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(huruf.toString(), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                    .clickable { onHapus() }
                    .semantics {
                        role = Role.Button
                        contentDescription = "Hapus satu karakter"
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text("⌫", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
            }

            Box(
                modifier = Modifier
                    .weight(4f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                    .clickable { onSpasi() }
                    .semantics {
                        role = Role.Button
                        contentDescription = "Spasi"
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text("spasi", fontSize = 12.sp, color = palette.textSecondary)
            }
        }
    }
}

@Composable
private fun KaKeypadAngka(
    ekspresi: String,
    hasil: KaHitung?,
    onTombol: (String) -> Unit,
) {
    val palette = LocalAppPalette.current
    val barisTombol = remember {
        listOf(
            listOf("7", "8", "9", "÷"),
            listOf("4", "5", "6", "×"),
            listOf("1", "2", "3", "−"),
            listOf("0", ",", "⌫", "+"),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.surfaceMuted)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(palette.surface)
                .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = if (ekspresi.isEmpty()) "0" else ekspresi,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (ekspresi.isEmpty()) palette.textMuted else palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
            when {
                hasil == null -> Text("Masukkan ekspresi aritmetika sederhana", fontSize = 10.sp, color = palette.textMuted)
                hasil.sukses -> Text("= ${hasil.hasil} (berhasil)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.success)
                else -> Text("${hasil.error} (gagal)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.danger)
            }
        }

        barisTombol.forEach { baris ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                baris.forEach { tombol ->
                    val operatorTombol = tombol == "÷" || tombol == "×" || tombol == "−" || tombol == "+"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (operatorTombol) palette.primaryContainer else palette.surface)
                            .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                            .clickable { onTombol(tombol) }
                            .semantics {
                                role = Role.Button
                                contentDescription = when (tombol) {
                                    "⌫" -> "Hapus satu simbol"
                                    "÷" -> "Bagi"
                                    "×" -> "Kali"
                                    "−" -> "Kurang"
                                    "+" -> "Tambah"
                                    "," -> "Koma"
                                    else -> "Angka $tombol"
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            tombol,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (operatorTombol) palette.onPrimaryContainer else palette.textPrimary,
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                    .clickable { onTombol("C") }
                    .semantics {
                        role = Role.Button
                        contentDescription = "Hapus semua ekspresi"
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text("C", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.danger)
            }

            Box(
                modifier = Modifier
                    .weight(3f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(palette.primary)
                    .border(1.dp, palette.primary, RoundedCornerShape(8.dp))
                    .clickable { onTombol("=") }
                    .semantics {
                        role = Role.Button
                        contentDescription = "Hitung ekspresi"
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text("=", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
            }
        }
    }
}

@Composable
private fun KaRiwayatCard(riwayat: List<String>, onKosongkan: () -> Unit) {
    val palette = LocalAppPalette.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Riwayat Perhitungan", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                        .clickable(enabled = riwayat.isNotEmpty()) { onKosongkan() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .semantics {
                            role = Role.Button
                            contentDescription = "Kosongkan riwayat perhitungan"
                        },
                ) {
                    Text(
                        "Kosongkan",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (riwayat.isNotEmpty()) palette.textPrimary else palette.textMuted,
                    )
                }
            }

            if (riwayat.isEmpty()) {
                Text(
                    "Belum ada riwayat perhitungan — coba keypad angka di bawah",
                    fontSize = 11.sp,
                    color = palette.textMuted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                )
            } else {
                riwayat.forEachIndexed { idx, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(palette.surfaceMuted)
                            .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            item,
                            fontSize = 12.sp,
                            color = palette.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Text("#${idx + 1}", fontSize = 10.sp, color = palette.textMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun KaPanelTertutup(onBuka: () -> Unit) {
    val palette = LocalAppPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Keyboard tertutup — aksesori ikut tersembunyi", fontSize = 11.sp, color = palette.textMuted)
        Surface(
            onClick = onBuka,
            shape = RoundedCornerShape(10.dp),
            color = palette.primary,
            contentColor = palette.onPrimary,
            border = BorderStroke(1.dp, palette.primary),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    role = Role.Button
                    contentDescription = "Buka keyboard simulasi"
                },
        ) {
            Text(
                "Buka Keyboard Simulasi",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = palette.onPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(vertical = 12.dp),
            )
        }
    }
}
