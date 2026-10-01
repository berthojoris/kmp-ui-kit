package com.example.uiapp.ui.widgetgallery

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.AppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private const val WG_SLOT_MAKS = 4

private enum class WgUkuran(val label: String, val keterangan: String) {
    SMALL("Small", "2×2"),
    MEDIUM("Medium", "4×2"),
    LARGE("Large", "4×4"),
    LOCK("Lock Screen", "Inline"),
}

private enum class WgTampilan(val label: String, val keterangan: String) {
    TERANG("Terang", "Permukaan terang"),
    GELAP("Gelap", "Kontras tinggi"),
    TINTED("Tinted", "Warna aksen"),
    CLEAR("Clear", "Translusen"),
}

private enum class WgJenis { TOGGLE, PROGRESS, TOMBOL, STAT }

private enum class WgMuat { MEMUAT, SIAP, GAGAL }

private enum class WgTambah { SIAP, MEMPROSES, SUKSES, GAGAL }

private data class WgWidget(
    val id: String,
    val nama: String,
    val sub: String,
    val jenis: WgJenis,
    val ukuran: List<WgUkuran>,
)

private data class WgKulit(
    val surface: Color,
    val konten: Color,
    val muted: Color,
    val aksen: Color,
    val border: Color,
)

private data class WgDemo(
    val lampu: Boolean,
    val tahapLaundry: Int,
    val fokus: Boolean,
    val sisaFokus: Int,
    val langkah: Int,
)

private val WG_WIDGET = listOf(
    WgWidget("wg_rumah", "Rumah Pintar", "Lampu teras & kipas", WgJenis.TOGGLE, listOf(WgUkuran.SMALL, WgUkuran.MEDIUM)),
    WgWidget("wg_laundry", "Pesanan Laundry", "Cuci, setrika, antar", WgJenis.PROGRESS, listOf(WgUkuran.SMALL, WgUkuran.MEDIUM, WgUkuran.LARGE)),
    WgWidget("wg_fokus", "Timer Fokus", "Sesi kerja 25 menit", WgJenis.TOMBOL, listOf(WgUkuran.SMALL, WgUkuran.MEDIUM, WgUkuran.LARGE)),
    WgWidget("wg_cuaca", "Cuaca Bandung", "Suhu & kelembapan", WgJenis.STAT, listOf(WgUkuran.SMALL, WgUkuran.MEDIUM, WgUkuran.LARGE, WgUkuran.LOCK)),
    WgWidget("wg_langkah", "Langkah Harian", "Target 10.000 langkah", WgJenis.PROGRESS, listOf(WgUkuran.MEDIUM, WgUkuran.LARGE, WgUkuran.LOCK)),
    WgWidget("wg_dompet", "Dompet Harian", "Sisa uang saku", WgJenis.STAT, listOf(WgUkuran.SMALL, WgUkuran.MEDIUM, WgUkuran.LOCK)),
)

private fun kulitUntuk(tampilan: WgTampilan, palette: AppPalette): WgKulit = when (tampilan) {
    WgTampilan.TERANG -> WgKulit(
        surface = palette.surface,
        konten = palette.textPrimary,
        muted = palette.textMuted,
        aksen = palette.primary,
        border = palette.border,
    )
    WgTampilan.GELAP -> WgKulit(
        surface = palette.textPrimary.copy(alpha = 0.92f),
        konten = palette.surface,
        muted = palette.surface.copy(alpha = 0.65f),
        aksen = palette.surface,
        border = palette.textSecondary,
    )
    WgTampilan.TINTED -> WgKulit(
        surface = palette.primaryContainer,
        konten = palette.onPrimaryContainer,
        muted = palette.onPrimaryContainer.copy(alpha = 0.65f),
        aksen = palette.primary,
        border = palette.primary.copy(alpha = 0.45f),
    )
    WgTampilan.CLEAR -> WgKulit(
        surface = palette.surfaceMuted.copy(alpha = 0.55f),
        konten = palette.textPrimary,
        muted = palette.textSecondary,
        aksen = palette.info,
        border = palette.border,
    )
}

private fun detikKeTeks(detik: Int): String {
    val menit = detik / 60
    val sisa = detik % 60
    return "$menit:" + (if (sisa < 10) "0" else "") + sisa
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WidgetGalleryLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var muatState by remember { mutableStateOf(WgMuat.MEMUAT) }
    var muatUlangKey by remember { mutableIntStateOf(0) }
    var gagalMuatSimulasi by remember { mutableStateOf(false) }

    var widgetDipilih by remember { mutableStateOf(WG_WIDGET.first().id) }
    var ukuranDipilih by remember { mutableStateOf(WgUkuran.SMALL) }
    var tampilanDipilih by remember { mutableStateOf(WgTampilan.TERANG) }
    var kedalaman by remember { mutableFloatStateOf(0.5f) }

    val terpasang = remember { mutableStateListOf<String>() }
    var tambahState by remember { mutableStateOf(WgTambah.SIAP) }
    var tambahKey by remember { mutableIntStateOf(0) }
    var pesanTambah by remember { mutableStateOf("Pilih widget lalu tekan Tambah Widget") }

    var lampuTeras by remember { mutableStateOf(false) }
    var tahapLaundry by remember { mutableIntStateOf(1) }
    var fokusBerjalan by remember { mutableStateOf(false) }
    var sisaFokus by remember { mutableIntStateOf(25 * 60) }
    var langkah by remember { mutableIntStateOf(6_400) }

    val widgetTerpilih = remember(widgetDipilih) { WG_WIDGET.first { it.id == widgetDipilih } }
    val kulit = remember(tampilanDipilih, palette) { kulitUntuk(tampilanDipilih, palette) }
    val demo = remember(lampuTeras, tahapLaundry, fokusBerjalan, sisaFokus, langkah) {
        WgDemo(lampuTeras, tahapLaundry, fokusBerjalan, sisaFokus, langkah)
    }
    val ukuranTersedia = remember(widgetTerpilih) { widgetTerpilih.ukuran }

    LaunchedEffect(muatUlangKey) {
        muatState = WgMuat.MEMUAT
        delay(900)
        muatState = if (gagalMuatSimulasi) WgMuat.GAGAL else WgMuat.SIAP
    }

    LaunchedEffect(tambahKey) {
        if (tambahKey > 0) {
            tambahState = WgTambah.MEMPROSES
            pesanTambah = "Menambahkan widget… (Simulasi)"
            delay(750)
            when {
                terpasang.size >= WG_SLOT_MAKS -> {
                    tambahState = WgTambah.GAGAL
                    pesanTambah = "Gagal: slot layar utama penuh (maks. $WG_SLOT_MAKS widget)"
                }
                widgetDipilih in terpasang -> {
                    tambahState = WgTambah.GAGAL
                    pesanTambah = "Gagal: widget sudah terpasang di layar utama"
                }
                else -> {
                    terpasang.add(widgetDipilih)
                    tambahState = WgTambah.SUKSES
                    pesanTambah = "Widget \"${widgetTerpilih.nama}\" ditambahkan ke layar utama (Simulasi)"
                }
            }
        }
    }

    LaunchedEffect(fokusBerjalan) {
        while (fokusBerjalan && sisaFokus > 0) {
            delay(1000)
            sisaFokus -= 1
        }
        if (sisaFokus == 0) fokusBerjalan = false
    }

    fun aksiWidget(aksi: String) {
        when (aksi) {
            "toggle_lampu" -> lampuTeras = !lampuTeras
            "perbarui_laundry" -> tahapLaundry = if (tahapLaundry >= 4) 1 else tahapLaundry + 1
            "toggle_fokus" -> fokusBerjalan = !fokusBerjalan
            "tambah_langkah" -> langkah = (langkah + 1_000).coerceAtMost(10_000)
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Widget Gallery & Sizes",
                subtitle = "Small–large, varian tampilan — Simulasi",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    Text("Widget Gallery & Sizes", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    Text(
                        "Galeri widget tiruan dengan empat ukuran dan empat varian tampilan. Tidak ada widget sistem sungguhan — seluruh data dan aksi adalah Simulasi.",
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Galeri Widget", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(palette.surfaceMuted)
                                .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                                .clickable {
                                    gagalMuatSimulasi = !gagalMuatSimulasi
                                    muatUlangKey += 1
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .semantics {
                                    role = Role.Button
                                    contentDescription = "Ubah mode muat galeri menjadi ${if (gagalMuatSimulasi) "berhasil" else "gagal"}"
                                },
                        ) {
                            Text(
                                if (gagalMuatSimulasi) "Mode: gagal" else "Mode: berhasil",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textSecondary,
                            )
                        }
                    }

                    when (muatState) {
                        WgMuat.MEMUAT -> {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(palette.surfaceMuted)
                                        .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
                                )
                            }
                            Text("Memuat galeri widget…", fontSize = 11.sp, color = palette.textMuted)
                        }
                        WgMuat.GAGAL -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(palette.danger.copy(alpha = 0.08f))
                                    .border(1.dp, palette.danger, RoundedCornerShape(12.dp))
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text("Gagal memuat galeri (Simulasi)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.danger)
                                Text("Katalog widget tidak tersedia. Coba muat ulang.", fontSize = 11.sp, color = palette.textSecondary)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(palette.primary)
                                        .border(1.dp, palette.primary, RoundedCornerShape(10.dp))
                                        .clickable { muatUlangKey += 1 }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .semantics {
                                            role = Role.Button
                                            contentDescription = "Coba lagi memuat galeri"
                                        },
                                ) {
                                    Text("Coba Lagi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
                                }
                            }
                        }
                        WgMuat.SIAP -> {
                            WG_WIDGET.forEach { widget ->
                                val dipilih = widget.id == widgetDipilih
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (dipilih) palette.primaryContainer.copy(alpha = 0.5f) else palette.surfaceMuted)
                                        .border(
                                            BorderStroke(if (dipilih) 1.5.dp else 1.dp, if (dipilih) palette.primary else palette.border),
                                            RoundedCornerShape(12.dp),
                                        )
                                        .clickable {
                                            widgetDipilih = widget.id
                                            if (ukuranDipilih !in widget.ukuran) ukuranDipilih = widget.ukuran.first()
                                        }
                                        .padding(12.dp)
                                        .semantics {
                                            role = Role.Button
                                            contentDescription = "Pilih widget ${widget.nama}"
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(palette.surface)
                                            .border(1.dp, palette.border, RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            widget.nama.first().toString(),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.primary,
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(widget.nama, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                                        Text(widget.sub, fontSize = 11.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Text(
                                        widget.ukuran.joinToString("/") { it.label },
                                        fontSize = 10.sp,
                                        color = palette.textMuted,
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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Ukuran & Varian Tampilan", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)

                    Text("Ukuran", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        WgUkuran.entries.forEach { ukuran ->
                            val tersedia = ukuran in ukuranTersedia
                            val aktif = ukuran == ukuranDipilih
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when {
                                            aktif -> palette.primary
                                            tersedia -> palette.surfaceMuted
                                            else -> palette.background
                                        },
                                    )
                                    .border(
                                        BorderStroke(1.dp, if (aktif) palette.primary else palette.border),
                                        RoundedCornerShape(10.dp),
                                    )
                                    .clickable(enabled = tersedia) {
                                        ukuranDipilih = ukuran
                                    }
                                    .padding(vertical = 8.dp, horizontal = 4.dp)
                                    .semantics {
                                        role = Role.Button
                                        contentDescription = "Ukuran ${ukuran.label}, ${if (tersedia) "tersedia" else "tidak tersedia"}"
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    ukuran.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (aktif) palette.onPrimary else if (tersedia) palette.textPrimary else palette.textMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    ukuran.keterangan,
                                    fontSize = 9.sp,
                                    color = if (aktif) palette.onPrimary.copy(alpha = 0.8f) else palette.textMuted,
                                )
                            }
                        }
                    }

                    Text("Varian tampilan (warna diturunkan dari palet aktif)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        WgTampilan.entries.forEach { tampilan ->
                            val aktif = tampilan == tampilanDipilih
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (aktif) palette.primary else palette.surfaceMuted)
                                    .border(BorderStroke(1.dp, if (aktif) palette.primary else palette.border), RoundedCornerShape(10.dp))
                                    .clickable { tampilanDipilih = tampilan }
                                    .padding(vertical = 8.dp, horizontal = 4.dp)
                                    .semantics {
                                        role = Role.Button
                                        contentDescription = "Varian tampilan ${tampilan.label}"
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    tampilan.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (aktif) palette.onPrimary else palette.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }

                    Text("Slider kedalaman lapisan", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                    WgSlider(
                        value = kedalaman,
                        onChange = { kedalaman = it },
                    )

                    // Pratinjau langsung dengan lapisan kedalaman
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        WgLapisanKedalaman(kedalaman = kedalaman, kulit = kulit) {
                            WgPratinjau(
                                widget = widgetTerpilih,
                                ukuran = ukuranDipilih,
                                kulit = kulit,
                                demo = demo,
                                onAksi = { aksiWidget(it) },
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val bisaTambah = muatState == WgMuat.SIAP && tambahState != WgTambah.MEMPROSES
                        Surface(
                            onClick = { tambahKey += 1 },
                            enabled = bisaTambah,
                            shape = RoundedCornerShape(10.dp),
                            color = if (bisaTambah) palette.primary else palette.surfaceMuted,
                            contentColor = if (bisaTambah) palette.onPrimary else palette.textMuted,
                            border = BorderStroke(1.dp, if (bisaTambah) palette.primary else palette.border),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .semantics {
                                    role = Role.Button
                                    contentDescription = "Tambah widget ke layar utama"
                                },
                        ) {
                            Text(
                                if (tambahState == WgTambah.MEMPROSES) "Menambahkan…" else "Tambah Widget",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (bisaTambah) palette.onPrimary else palette.textMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                            )
                        }
                    }

                    val warnaPesan = when (tambahState) {
                        WgTambah.SUKSES -> palette.success
                        WgTambah.GAGAL -> palette.danger
                        else -> palette.textMuted
                    }
                    Text(
                        text = pesanTambah,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = warnaPesan,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Layar Utama (Pratinjau)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                            Text("${terpasang.size}/$WG_SLOT_MAKS slot terisi", fontSize = 11.sp, color = palette.textMuted)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(palette.surfaceMuted)
                                .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                                .clickable(enabled = terpasang.isNotEmpty()) { terpasang.clear() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .semantics {
                                    role = Role.Button
                                    contentDescription = "Kosongkan layar utama"
                                },
                        ) {
                            Text(
                                "Kosongkan",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (terpasang.isNotEmpty()) palette.textPrimary else palette.textMuted,
                            )
                        }
                    }

                    if (terpasang.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(listOf(palette.primaryContainer.copy(alpha = 0.5f), palette.surfaceMuted)))
                                .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text("Belum ada widget terpasang", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                            Text("Tambahkan widget dari galeri di atas", fontSize = 11.sp, color = palette.textMuted)
                        }
                    } else {
                        terpasang.forEach { id ->
                            val widget = WG_WIDGET.first { it.id == id }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Brush.linearGradient(listOf(palette.primaryContainer.copy(alpha = 0.35f), palette.surfaceMuted)))
                                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(palette.surface)
                                        .border(1.dp, palette.border, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(widget.nama.first().toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(widget.nama, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                                    Text("Terpasang — Simulasi", fontSize = 10.sp, color = palette.textMuted)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(palette.danger.copy(alpha = 0.10f))
                                        .border(1.dp, palette.danger, RoundedCornerShape(8.dp))
                                        .clickable { terpasang.remove(id) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .semantics {
                                            role = Role.Button
                                            contentDescription = "Hapus widget ${widget.nama} dari layar utama"
                                        },
                                ) {
                                    Text("Hapus", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.danger)
                                }
                            }
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
                    Text("Catatan Widget Gallery:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                    Text("• Empat ukuran (small, medium, large, lock screen) dipratinjau langsung dengan data mock", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Varian Terang/Gelap/Tinted/Clear diturunkan dari palet aktif, tanpa warna hardcoded", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Kartu widget dapat ditoggle, menampilkan progress, dan tombol aksi tanpa berpindah layar", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Tidak ada widget sistem sungguhan — seluruh penambahan berlabel Simulasi", fontSize = 12.sp, color = palette.textSecondary)
                }
            }
        }
    }
}

@Composable
private fun WgLapisanKedalaman(
    kedalaman: Float,
    kulit: WgKulit,
    konten: @Composable () -> Unit,
) {
    val geser = (kedalaman * 14).roundToInt()

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset { IntOffset((geser * 2).dp.roundToPx(), (geser * 2).dp.roundToPx()) }
                .clip(RoundedCornerShape(20.dp))
                .background(kulit.surface.copy(alpha = 0.25f))
                .border(1.dp, kulit.border.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset { IntOffset(geser.dp.roundToPx(), geser.dp.roundToPx()) }
                .clip(RoundedCornerShape(20.dp))
                .background(kulit.surface.copy(alpha = 0.50f))
                .border(1.dp, kulit.border.copy(alpha = 0.85f), RoundedCornerShape(20.dp)),
        )
        konten()
    }
}

@Composable
private fun WgSlider(value: Float, onChange: (Float) -> Unit) {
    val palette = LocalAppPalette.current
    var lebarPx by remember { mutableIntStateOf(1) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
                .onSizeChanged { lebarPx = it.width.coerceAtLeast(1) }
                .pointerInput(lebarPx) {
                    detectTapGestures { offset ->
                        onChange((offset.x / lebarPx).coerceIn(0f, 1f))
                    }
                }
                .pointerInput(lebarPx) {
                    detectDragGestures(
                        onDragStart = { offset -> onChange((offset.x / lebarPx).coerceIn(0f, 1f)) },
                        onDrag = { change, _ ->
                            change.consume()
                            onChange((change.position.x / lebarPx).coerceIn(0f, 1f))
                        },
                    )
                }
                .semantics {
                    role = Role.Button
                    contentDescription = "Slider kedalaman lapisan, ${(value * 100).roundToInt()} persen"
                },
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(palette.surfaceMuted)
                    .border(1.dp, palette.border, RoundedCornerShape(3.dp)),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(fraction = value.coerceIn(0.02f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(palette.primary),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset {
                        val total = lebarPx - 22.dp.roundToPx()
                        IntOffset((value.coerceIn(0f, 1f) * total).toInt(), 0)
                    }
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(palette.surface)
                    .border(1.dp, palette.primary, CircleShape),
            )
        }

        Text(
            "${(value * 100).roundToInt()}%",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textSecondary,
        )
    }
}

@Composable
private fun WgPratinjau(
    widget: WgWidget,
    ukuran: WgUkuran,
    kulit: WgKulit,
    demo: WgDemo,
    onAksi: (String) -> Unit,
) {
    val tinggi = when (ukuran) {
        WgUkuran.SMALL -> 150.dp
        WgUkuran.MEDIUM -> 150.dp
        WgUkuran.LARGE -> 300.dp
        WgUkuran.LOCK -> 72.dp
    }
    val bentuk = if (ukuran == WgUkuran.LOCK) RoundedCornerShape(24.dp) else RoundedCornerShape(20.dp)

    Column(
        modifier = Modifier
            .then(
                if (ukuran == WgUkuran.SMALL) Modifier.size(150.dp) else Modifier.fillMaxWidth(),
            )
            .height(tinggi)
            .clip(bentuk)
            .background(kulit.surface)
            .border(1.dp, kulit.border, bentuk)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when (ukuran) {
            WgUkuran.LOCK -> WgKontenLock(widget, kulit, demo)
            else -> WgKontenIsi(widget, ukuran, kulit, demo, onAksi)
        }
    }
}

@Composable
private fun WgKontenLock(widget: WgWidget, kulit: WgKulit, demo: WgDemo) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(kulit.aksen.copy(alpha = 0.20f))
                .border(1.dp, kulit.aksen, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(widget.nama.first().toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = kulit.konten)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(widget.nama, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = kulit.konten, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(WgRingkasan(widget, demo), fontSize = 10.sp, color = kulit.muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun WgKontenIsi(
    widget: WgWidget,
    ukuran: WgUkuran,
    kulit: WgKulit,
    demo: WgDemo,
    onAksi: (String) -> Unit,
) {
    val luas = ukuran != WgUkuran.SMALL

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            widget.nama,
            fontSize = if (luas) 13.sp else 11.sp,
            fontWeight = FontWeight.Bold,
            color = kulit.konten,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text("Simulasi", fontSize = 9.sp, color = kulit.muted)
    }

    when (widget.jenis) {
        WgJenis.TOGGLE -> {
            Text(
                if (demo.lampu) "Lampu teras: Menyala" else "Lampu teras: Mati",
                fontSize = if (luas) 12.sp else 11.sp,
                color = kulit.muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Sakelar", fontSize = 10.sp, color = kulit.muted)
                WgSakelar(aktif = demo.lampu, kulit = kulit, onToggle = { onAksi("toggle_lampu") })
            }
        }
        WgJenis.PROGRESS -> {
            val (nilai, maks, judul) = if (widget.id == "wg_langkah") {
                Triple(demo.langkah, 10_000, "${demo.langkah} / 10.000 langkah")
            } else {
                Triple(demo.tahapLaundry, 4, "Tahap ${demo.tahapLaundry} dari 4")
            }
            Text(judul, fontSize = if (luas) 12.sp else 11.sp, color = kulit.konten, maxLines = 1, overflow = TextOverflow.Ellipsis)
            WgProgres(fraction = nilai.toFloat() / maks, kulit = kulit)
            if (ukuran == WgUkuran.LARGE) {
                Text("Berjalan: cuci → setrika → antar", fontSize = 10.sp, color = kulit.muted)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(kulit.aksen.copy(alpha = 0.18f))
                    .border(1.dp, kulit.aksen, RoundedCornerShape(8.dp))
                    .clickable { onAksi(if (widget.id == "wg_langkah") "tambah_langkah" else "perbarui_laundry") }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = if (widget.id == "wg_langkah") "Tambah seribu langkah" else "Perbarui tahap laundry"
                    },
            ) {
                Text(
                    if (widget.id == "wg_langkah") "+1.000 Langkah" else "Perbarui Tahap",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = kulit.konten,
                )
            }
        }
        WgJenis.TOMBOL -> {
            Text(
                if (demo.fokus) "Sesi berjalan: ${detikKeTeks(demo.sisaFokus)}" else "Sesi siap: 25:00",
                fontSize = if (luas) 12.sp else 11.sp,
                color = kulit.konten,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (ukuran == WgUkuran.LARGE) {
                WgProgres(fraction = 1f - demo.sisaFokus / (25f * 60f), kulit = kulit)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(kulit.aksen.copy(alpha = 0.20f))
                    .border(1.dp, kulit.aksen, RoundedCornerShape(10.dp))
                    .clickable { onAksi("toggle_fokus") }
                    .padding(vertical = 8.dp)
                    .semantics {
                        role = Role.Button
                        contentDescription = if (demo.fokus) "Jeda timer fokus" else "Mulai timer fokus"
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (demo.fokus) "Jeda Fokus" else "Mulai Fokus",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = kulit.konten,
                )
            }
        }
        WgJenis.STAT -> {
            val (nilai, satuan, keterangan) = when (widget.id) {
                "wg_cuaca" -> Triple("28°", "Cerah berawan", "Kelembapan 64%")
                else -> Triple("Rp 42.500", "Sisa uang saku", "Hemat 12% minggu ini")
            }
            Text(nilai, fontSize = if (luas) 26.sp else 18.sp, fontWeight = FontWeight.ExtraBold, color = kulit.konten)
            Text(satuan, fontSize = if (luas) 12.sp else 10.sp, color = kulit.muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (ukuran == WgUkuran.LARGE || ukuran == WgUkuran.MEDIUM) {
                Text(keterangan, fontSize = 10.sp, color = kulit.muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

private fun WgRingkasan(widget: WgWidget, demo: WgDemo): String = when (widget.jenis) {
    WgJenis.TOGGLE -> if (demo.lampu) "Lampu menyala" else "Lampu mati"
    WgJenis.PROGRESS -> if (widget.id == "wg_langkah") "${demo.langkah} langkah" else "Tahap ${demo.tahapLaundry}/4"
    WgJenis.TOMBOL -> if (demo.fokus) "Fokus ${detikKeTeks(demo.sisaFokus)}" else "Fokus siap"
    WgJenis.STAT -> if (widget.id == "wg_cuaca") "28° cerah berawan" else "Rp 42.500"
}

@Composable
private fun WgProgres(fraction: Float, kulit: WgKulit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(kulit.muted.copy(alpha = 0.25f))
            .border(1.dp, kulit.border, RoundedCornerShape(4.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = fraction.coerceIn(0f, 1f))
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(kulit.aksen),
        )
    }
}

@Composable
private fun WgSakelar(aktif: Boolean, kulit: WgKulit, onToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .width(46.dp)
            .height(26.dp)
            .clip(CircleShape)
            .background(if (aktif) kulit.aksen.copy(alpha = 0.35f) else kulit.muted.copy(alpha = 0.25f))
            .border(1.dp, if (aktif) kulit.aksen else kulit.border, CircleShape)
            .clickable { onToggle() }
            .semantics {
                role = Role.Button
                contentDescription = if (aktif) "Matikan lampu teras" else "Nyalakan lampu teras"
            },
    ) {
        Box(
            modifier = Modifier
                .align(if (aktif) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(3.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(if (aktif) kulit.aksen else kulit.muted),
        )
    }
}
