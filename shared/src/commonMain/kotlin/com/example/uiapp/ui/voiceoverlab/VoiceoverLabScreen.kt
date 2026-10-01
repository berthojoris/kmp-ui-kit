package com.example.uiapp.ui.voiceoverlab

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private enum class VoKind(val label: String) {
    JUDUL("Judul"),
    TEKS("Teks"),
    GAMBAR("Gambar"),
    TAUTAN("Tautan"),
    TOMBOL("Tombol"),
}

private data class VoItem(
    val id: String,
    val kind: VoKind,
    val content: String,
    val posisi: String,
    val hint: String,
    val isHeading: Boolean,
)

private val VoDemoItems: List<VoItem> = listOf(
    VoItem(
        id = "judul_promo",
        kind = VoKind.JUDUL,
        content = "Promo Villa Akhir Pekan",
        posisi = "Judul halaman, tingkat 1",
        hint = "Ketuk dua kali untuk membuka detail promo",
        isHeading = true,
    ),
    VoItem(
        id = "teks_intro",
        kind = VoKind.TEKS,
        content = "Nikmati diskon 25 persen untuk menginap dua malam di Villa Cendana, kawasan Batu.",
        posisi = "Paragraf, 14 kata",
        hint = "Geser ke bawah untuk membaca per baris",
        isHeading = false,
    ),
    VoItem(
        id = "gambar_kolam",
        kind = VoKind.GAMBAR,
        content = "Gambar: kolam renang villa dengan latar pegunungan",
        posisi = "Gambar, memiliki deskripsi",
        hint = "Ketuk dua kali untuk mendengar deskripsi gambar",
        isHeading = false,
    ),
    VoItem(
        id = "tautan_syarat",
        kind = VoKind.TAUTAN,
        content = "Lihat syarat dan ketentuan promo",
        posisi = "Tautan, halaman kebijakan",
        hint = "Ketuk dua kali untuk membuka tautan",
        isHeading = false,
    ),
    VoItem(
        id = "judul_fasilitas",
        kind = VoKind.JUDUL,
        content = "Fasilitas Termasuk",
        posisi = "Judul bagian, tingkat 2",
        hint = "Ketuk dua kali untuk melompat ke bagian",
        isHeading = true,
    ),
    VoItem(
        id = "teks_fasilitas",
        kind = VoKind.TEKS,
        content = "Sarapan untuk dua orang, akses kolam renang, dan antar jemput stasiun.",
        posisi = "Paragraf, 10 kata",
        hint = "Geser ke bawah untuk membaca per baris",
        isHeading = false,
    ),
    VoItem(
        id = "tombol_pesan",
        kind = VoKind.TOMBOL,
        content = "Pesan Sekarang",
        posisi = "Tombol, tersedia",
        hint = "Ketuk dua kali untuk memesan kamar",
        isHeading = false,
    ),
)

private enum class RotorMode(val label: String) {
    KARAKTER("Karakter"),
    KATA("Kata"),
    BARIS("Baris"),
    HEADING("Judul"),
}

private enum class VoSpeed(val label: String, val tickMs: Long) {
    LAMBAT("Lambat", 42L),
    SEDANG("Sedang", 24L),
    CEPAT("Cepat", 10L),
}

private enum class VoScreenStatus(val label: String) {
    SIAP("Siap"),
    MEMUAT("Memuat"),
    GAGAL("Gagal"),
}

private enum class VoSpeechStatus(val label: String) {
    DIAM("Diam"),
    MENGUCAPKAN("Membaca"),
    GALAT("Galat narasi"),
}

private fun potongBaris(text: String, lebar: Int): List<String> {
    val kata = text.split(" ").filter { it.isNotBlank() }
    val hasil = mutableListOf<String>()
    var baris = ""
    kata.forEach { k ->
        val calon = if (baris.isEmpty()) k else "$baris $k"
        if (calon.length > lebar && baris.isNotEmpty()) {
            hasil.add(baris)
            baris = k
        } else {
            baris = calon
        }
    }
    if (baris.isNotEmpty()) hasil.add(baris)
    return hasil
}

private fun unitUntuk(item: VoItem, mode: RotorMode): List<String> = when (mode) {
    RotorMode.KARAKTER -> item.content.map { it.toString() }
    RotorMode.KATA -> item.content.split(" ").filter { it.isNotBlank() }
    RotorMode.BARIS -> potongBaris(item.content, 34)
    RotorMode.HEADING -> listOf(item.content)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VoiceoverLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var voAktif by remember { mutableStateOf(true) }
    var fokusIndex by remember { mutableIntStateOf(0) }
    var rotor by remember { mutableStateOf(RotorMode.HEADING) }
    var unitIndex by remember { mutableIntStateOf(0) }
    var kurangiGerak by remember { mutableStateOf(false) }
    var tiraiAktif by remember { mutableStateOf(false) }
    var tiraiTaps by remember { mutableIntStateOf(0) }
    var kecepatan by remember { mutableStateOf(VoSpeed.SEDANG) }
    var layarStatus by remember { mutableStateOf(VoScreenStatus.SIAP) }
    var muatTarget by remember { mutableStateOf(VoScreenStatus.SIAP) }
    var muatSeq by remember { mutableIntStateOf(0) }
    var galatUcapan by remember { mutableStateOf(false) }
    var bicaraSeq by remember { mutableIntStateOf(0) }
    var spoken by remember { mutableStateOf("") }
    var ucapanStatus by remember { mutableStateOf(VoSpeechStatus.DIAM) }
    var aksiLog by remember { mutableStateOf("Pilih elemen dengan gestur geser atau tombol navigasi di bawah.") }

    // Denyut cincin fokus hanya untuk elemen kecil (cincin), bukan seluruh layar.
    val pulseTransition = rememberInfiniteTransition(label = "voPulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(650), RepeatMode.Reverse),
        label = "voPulseAlpha",
    )
    val ringAlpha = if (kurangiGerak) 1f else pulseAlpha

    val items = VoDemoItems
    val item = items[fokusIndex.coerceIn(0, items.size - 1)]
    val unitList = remember(item.id, rotor) { unitUntuk(item, rotor) }
    val unitAman = if (unitList.isEmpty()) 0 else unitIndex.coerceIn(0, unitList.size - 1)
    val unitTeks = unitList.getOrNull(unitAman) ?: "—"
    val judulList = remember { VoDemoItems.mapIndexedNotNull { i, vo -> if (vo.isHeading) i else null } }
    val urutanJudul = judulList.indexOf(fokusIndex) + 1
    val unitNarasi = when (rotor) {
        RotorMode.KARAKTER -> "Karakter ${unitAman + 1} dari ${unitList.size}: $unitTeks"
        RotorMode.KATA -> "Kata ${unitAman + 1} dari ${unitList.size}: $unitTeks"
        RotorMode.BARIS -> "Baris ${unitAman + 1} dari ${unitList.size}: $unitTeks"
        RotorMode.HEADING ->
            if (urutanJudul > 0) "Judul $urutanJudul dari ${judulList.size}: ${item.content}"
            else "Bukan elemen judul"
    }
    val narasiPenuh = "${item.kind.label}. ${item.content}. ${item.posisi}. $unitNarasi. Petunjuk: ${item.hint}"

    // Simulasi pembacaan narasi: teks diucapkan diuraikan per karakter.
    LaunchedEffect(narasiPenuh, kecepatan, kurangiGerak, bicaraSeq, galatUcapan, voAktif) {
        when {
            !voAktif -> {
                ucapanStatus = VoSpeechStatus.DIAM
                spoken = "VoiceOver nonaktif — tidak ada narasi yang dibacakan."
            }
            galatUcapan -> {
                ucapanStatus = VoSpeechStatus.GALAT
                spoken = ""
            }
            kurangiGerak -> {
                ucapanStatus = VoSpeechStatus.DIAM
                spoken = narasiPenuh
            }
            else -> {
                ucapanStatus = VoSpeechStatus.MENGUCAPKAN
                spoken = ""
                for (i in narasiPenuh.indices) {
                    spoken = narasiPenuh.substring(0, i + 1)
                    delay(kecepatan.tickMs)
                }
                ucapanStatus = VoSpeechStatus.DIAM
            }
        }
    }

    // Pemuatan ulang layar baca: normal (SIAP) atau gagal, dengan state MEMUAT.
    LaunchedEffect(muatSeq) {
        if (muatSeq > 0) {
            layarStatus = VoScreenStatus.MEMUAT
            delay(950)
            layarStatus = muatTarget
        }
    }

    fun pindahFokus(delta: Int) {
        val baru = fokusIndex + delta
        if (baru in items.indices) {
            fokusIndex = baru
            unitIndex = 0
            aksiLog = "Fokus pindah ke ${items[baru].kind.label}: ${items[baru].content}"
        } else {
            aksiLog = if (delta > 0) "Sudah di elemen terakhir layar." else "Sudah di elemen pertama layar."
        }
    }

    fun geserUnit(delta: Int) {
        if (rotor == RotorMode.HEADING) {
            val posisi = judulList.indexOf(fokusIndex)
            val tujuan = posisi + delta
            if (tujuan in judulList.indices) {
                fokusIndex = judulList[tujuan]
                unitIndex = 0
                aksiLog = "Rotor Judul: melompat ke \"${items[fokusIndex].content}\""
            } else {
                aksiLog = "Tidak ada judul berikutnya pada layar ini."
            }
        } else {
            val baru = unitAman + delta
            if (baru in unitList.indices) {
                unitIndex = baru
                aksiLog = "Rotor ${rotor.label}: membaca \"${unitList[baru]}\""
            } else {
                aksiLog = "Sudah di ujung unit ${rotor.label.lowercase()}."
            }
        }
    }

    fun aktifkanElemen() {
        aksiLog = "Ketuk dua kali (Simulasi): ${item.kind.label} \"${item.content}\" diaktifkan."
    }

    val gesturArea = Modifier
        .pointerInput(fokusIndex, rotor) {
            var akumX = 0f
            var akumY = 0f
            detectDragGestures(
                onDragEnd = { akumX = 0f; akumY = 0f },
                onDragCancel = { akumX = 0f; akumY = 0f },
            ) { _, dragAmount ->
                akumX += dragAmount.x
                akumY += dragAmount.y
                if (akumX > 56f) {
                    akumX = 0f
                    pindahFokus(-1)
                } else if (akumX < -56f) {
                    akumX = 0f
                    pindahFokus(1)
                }
                if (akumY > 56f) {
                    akumY = 0f
                    geserUnit(1)
                } else if (akumY < -56f) {
                    akumY = 0f
                    geserUnit(-1)
                }
            }
        }
        .pointerInput(item.id) {
            detectTapGestures(onDoubleTap = { aktifkanElemen() })
        }
        .semantics {
            role = Role.Button
            contentDescription =
                "Area layar baca simulator. Geser mendatar untuk memilih elemen, geser vertikal untuk unit rotor, ketuk dua kali untuk mengaktifkan."
        }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "VoiceOver Simulator",
                subtitle = "Rotor, focus ring, hint — Simulasi",
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    VoActionButton(
                        label = "Baca Ulang",
                        onClick = {
                            bicaraSeq++
                            aksiLog = "Narasi diucapkan ulang (Simulasi suara)."
                        },
                        enabled = voAktif,
                        modifier = Modifier.weight(1f),
                    )
                    VoActionButton(
                        label = if (tiraiAktif) "Matikan Tirai Layar" else "Tirai Layar",
                        onClick = {
                            tiraiAktif = !tiraiAktif
                            tiraiTaps = 0
                            aksiLog = if (tiraiAktif) {
                                "Screen Curtain aktif: layar dibekukan hitam. Ketuk tiga kali untuk mematikan."
                            } else {
                                "Screen Curtain dimatikan."
                            }
                        },
                        accent = tiraiAktif,
                        modifier = Modifier.weight(1f),
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
                    title = "Simulator Pembaca Layar",
                    subtitle = "Latihan pemakaian VoiceOver / TalkBack",
                    modifier = Modifier.weight(1f),
                )
                SimulasiPill()
            }

            LabIntroCard(
                title = "Simulator, bukan pembaca layar sungguhan",
                body = "Lab ini meniru pengalaman VoiceOver: cincin fokus, narasi, petunjuk (hint), rotor, dan gestur. " +
                    "Tidak ada teks yang benar-benar diucapkan dan tidak ada layanan aksesibilitas sistem yang dipanggil.",
            )

            // ===== Layar baca demo =====
            LabSectionTitle(
                title = "Layar Baca Demo",
                subtitle = "Geser mendatar = pilih elemen • Geser vertikal = unit rotor • Ketuk 2× = aktifkan",
            )

            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Status layar: ${layarStatus.label}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textSecondary,
                        modifier = Modifier.weight(1f),
                    )
                    VoActionButton(
                        label = "Muat Ulang",
                        onClick = {
                            muatTarget = VoScreenStatus.SIAP
                            muatSeq++
                        },
                        modifier = Modifier.width(110.dp),
                    )
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    when (layarStatus) {
                        VoScreenStatus.MEMUAT -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Text(
                                    text = "Memuat layar baca…",
                                    fontSize = 12.sp,
                                    color = palette.textMuted,
                                )
                                repeat(4) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(34.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(palette.surfaceMuted)
                                            .border(1.dp, palette.border, RoundedCornerShape(10.dp)),
                                    )
                                }
                            }
                        }
                        VoScreenStatus.GAGAL -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
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
                                        text = "Gagal memuat layar baca (Simulasi galat). Narasi tidak tersedia.",
                                        fontSize = 12.sp,
                                        color = palette.textPrimary,
                                        modifier = Modifier.padding(12.dp),
                                    )
                                }
                                VoActionButton(
                                    label = "Coba Lagi",
                                    onClick = {
                                        muatTarget = VoScreenStatus.SIAP
                                        muatSeq++
                                    },
                                    accent = true,
                                )
                            }
                        }
                        VoScreenStatus.SIAP -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(gesturArea)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(palette.surfaceMuted)
                                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items.forEach { vo ->
                                    VoDemoElement(
                                        item = vo,
                                        fokus = vo.id == item.id,
                                        ringAlpha = ringAlpha,
                                    )
                                }
                            }
                        }
                    }

                    if (tiraiAktif) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black)
                                .pointerInput(tiraiTaps) {
                                    detectTapGestures(onTap = {
                                        tiraiTaps++
                                        if (tiraiTaps >= 3) {
                                            tiraiAktif = false
                                            tiraiTaps = 0
                                            aksiLog = "Screen Curtain dimatikan lewat gestur ketuk tiga kali."
                                        }
                                    })
                                }
                                .semantics {
                                    role = Role.Button
                                    contentDescription = "Tirai layar aktif. Ketuk tiga kali untuk mematikan."
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(16.dp),
                            ) {
                                Text(
                                    text = "Tirai Layar Aktif",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textMuted,
                                )
                                Text(
                                    text = "Layar dibekukan hitam. Ketuk 3× untuk mematikan ($tiraiTaps/3).",
                                    fontSize = 11.sp,
                                    color = palette.textMuted,
                                    textAlign = TextAlign.Center,
                                )
                            }
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
                        text = aksiLog,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                        modifier = Modifier.padding(10.dp),
                    )
                }
            }

            // ===== Rotor =====
            LabSectionTitle(
                title = "Rotor & Navigasi Unit",
                subtitle = "Menentukan satuan baca saat pengguna menggeser naik/turun",
            )

            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    VoRotorGlyph(tint = palette.primary)
                    Text(
                        text = "Mode rotor aktif: ${rotor.label}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RotorMode.entries.forEach { mode ->
                        VoChip(
                            label = mode.label,
                            selected = rotor == mode,
                            onClick = {
                                rotor = mode
                                unitIndex = 0
                                aksiLog = "Rotor diubah ke mode ${mode.label}."
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                Text(
                    text = "Unit dibaca sekarang: $unitTeks",
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = unitNarasi,
                    fontSize = 11.sp,
                    color = palette.textMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    VoActionButton(
                        label = "◀ Elemen",
                        onClick = { pindahFokus(-1) },
                        enabled = fokusIndex > 0 && layarStatus == VoScreenStatus.SIAP,
                        modifier = Modifier.weight(1f),
                    )
                    VoActionButton(
                        label = "Elemen ▶",
                        onClick = { pindahFokus(1) },
                        enabled = fokusIndex < items.size - 1 && layarStatus == VoScreenStatus.SIAP,
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    VoActionButton(
                        label = "Unit −",
                        onClick = { geserUnit(-1) },
                        enabled = voAktif && layarStatus == VoScreenStatus.SIAP,
                        modifier = Modifier.weight(1f),
                    )
                    VoActionButton(
                        label = "Unit +",
                        onClick = { geserUnit(1) },
                        enabled = voAktif && layarStatus == VoScreenStatus.SIAP,
                        modifier = Modifier.weight(1f),
                    )
                    VoActionButton(
                        label = "Aktifkan",
                        onClick = { aktifkanElemen() },
                        enabled = voAktif && layarStatus == VoScreenStatus.SIAP,
                        accent = true,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // ===== Narasi =====
            LabSectionTitle(
                title = "Narasi Pembacaan",
                subtitle = "Teks narasi yang diuraikan per karakter (Simulasi suara)",
            )

            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Status ucapan: ${ucapanStatus.label}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = when (ucapanStatus) {
                            VoSpeechStatus.MENGUCAPKAN -> palette.info.copy(alpha = 0.15f)
                            VoSpeechStatus.GALAT -> palette.danger.copy(alpha = 0.15f)
                            VoSpeechStatus.DIAM -> palette.surfaceMuted
                        },
                        contentColor = palette.textSecondary,
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        border = BorderStroke(
                            1.dp,
                            when (ucapanStatus) {
                                VoSpeechStatus.MENGUCAPKAN -> palette.info
                                VoSpeechStatus.GALAT -> palette.danger
                                VoSpeechStatus.DIAM -> palette.border
                            },
                        ),
                    ) {
                        Text(
                            text = ucapanStatus.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                ) {
                    Text(
                        text = when {
                            ucapanStatus == VoSpeechStatus.GALAT ->
                                "Narasi gagal diucapkan (Simulasi galat mesin suara). Tekan Coba Lagi."
                            spoken.isEmpty() -> "Menyiapkan narasi…"
                            else -> spoken
                        },
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = palette.textPrimary,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    VoActionButton(
                        label = if (galatUcapan) "Coba Lagi" else "Simulasikan Galat",
                        onClick = {
                            galatUcapan = !galatUcapan
                            bicaraSeq++
                            aksiLog = if (galatUcapan) {
                                "Galat mesin suara disimulasikan."
                            } else {
                                "Narasi dipulihkan (Simulasi)."
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                    VoActionButton(
                        label = "Kosongkan Narasi",
                        onClick = {
                            voAktif = false
                            aksiLog = "VoiceOver dimatikan: tidak ada narasi (state kosong)."
                        },
                        enabled = voAktif,
                        modifier = Modifier.weight(1f),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Kecepatan baca:",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                    VoSpeed.entries.forEach { speed ->
                        VoChip(
                            label = speed.label,
                            selected = kecepatan == speed,
                            onClick = { kecepatan = speed },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // ===== Pengaturan simulator =====
            LabSectionTitle(
                title = "Pengaturan Simulator",
                subtitle = "Sakelar aksesibilitas untuk eksperimen",
            )

            LabCard {
                VoToggleRow(
                    label = "VoiceOver Aktif",
                    desc = if (voAktif) "Narasi dan fokus berjalan." else "Nonaktif — layar tanpa narasi.",
                    checked = voAktif,
                    onCheckedChange = { baru ->
                        voAktif = baru
                        aksiLog = if (baru) "VoiceOver diaktifkan (Simulasi)." else "VoiceOver dinonaktifkan (Simulasi)."
                    },
                )
                VoToggleRow(
                    label = "Kurangi Gerak",
                    desc = if (kurangiGerak) {
                        "Denyut cincin fokus dan animasi ucapan dimatikan."
                    } else {
                        "Cincin fokus berdenyut dan narasi diuraikan per karakter."
                    },
                    checked = kurangiGerak,
                    onCheckedChange = { kurangiGerak = it },
                )
                VoToggleRow(
                    label = "Screen Curtain (Tirai Layar)",
                    desc = if (tiraiAktif) {
                        "Layar dibekukan hitam — ketuk tiga kali untuk mematikan."
                    } else {
                        "Nonaktif — konten layar baca terlihat."
                    },
                    checked = tiraiAktif,
                    onCheckedChange = { baru ->
                        tiraiAktif = baru
                        tiraiTaps = 0
                    },
                )
            }

            // ===== Bantuan gerak =====
            LabSectionTitle(
                title = "Bantuan Gerak",
                subtitle = "Panduan gestur & akomodasi gerak",
            )

            LabCard {
                Text("• Geser mendatar (swipe kiri/kanan): memilih elemen berikutnya atau sebelumnya.", fontSize = 12.sp, color = palette.textSecondary, lineHeight = 17.sp)
                Text("• Geser vertikal (swipe atas/bawah): membaca per karakter, kata, atau baris sesuai rotor.", fontSize = 12.sp, color = palette.textSecondary, lineHeight = 17.sp)
                Text("• Ketuk dua kali pada area layar baca: mengaktifkan elemen yang sedang difokuskan.", fontSize = 12.sp, color = palette.textSecondary, lineHeight = 17.sp)
                Text("• Ketuk tiga kali saat tirai layar aktif: mematikan tirai, sama seperti VoiceOver asli.", fontSize = 12.sp, color = palette.textSecondary, lineHeight = 17.sp)
                Text("• Mode Kurangi Gerak: animasi dihentikan untuk pengguna yang sensitif gerak.", fontSize = 12.sp, color = palette.textSecondary, lineHeight = 17.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun VoDemoElement(item: VoItem, fokus: Boolean, ringAlpha: Float) {
    val palette = LocalAppPalette.current

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (fokus) palette.surface else Color.Transparent)
                    .padding(8.dp),
            ) {
                when (item.kind) {
                    VoKind.JUDUL -> Text(
                        text = item.content,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    VoKind.TEKS -> Text(
                        text = item.content,
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                        lineHeight = 17.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    VoKind.GAMBAR -> {
                        val gambarBrush = remember {
                            Brush.linearGradient(
                                listOf(
                                    palette.info.copy(alpha = 0.35f),
                                    palette.primary.copy(alpha = 0.30f),
                                )
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(width = 48.dp, height = 32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(gambarBrush)
                                    .border(1.dp, palette.border, RoundedCornerShape(8.dp)),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.content,
                                fontSize = 11.sp,
                                color = palette.textSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    VoKind.TAUTAN -> Text(
                        text = item.content,
                        fontSize = 12.sp,
                        color = palette.primary,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    VoKind.TOMBOL -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(palette.primary)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = item.content,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.onPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
            if (fokus) {
                // Cincin fokus indigo (palette.info) yang berdenyut di lapisan terpisah.
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer { alpha = ringAlpha }
                        .border(2.dp, palette.info, RoundedCornerShape(10.dp)),
                )
            }
        }
        if (fokus) {
            Text(
                text = "Petunjuk: ${item.hint}",
                fontSize = 10.sp,
                color = palette.info,
                lineHeight = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun VoRotorGlyph(tint: Color, size: Dp = 20.dp) {
    val arah = remember {
        listOf(
            0f to -1f,
            0.71f to -0.71f,
            1f to 0f,
            0.71f to 0.71f,
            0f to 1f,
            -0.71f to 0.71f,
            -1f to 0f,
            -0.71f to -0.71f,
        )
    }
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val r = s / 2f
        drawCircle(
            color = tint,
            radius = r * 0.62f,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        arah.forEach { (dx, dy) ->
            drawLine(
                color = tint,
                start = androidx.compose.ui.geometry.Offset(r + dx * r * 0.72f, r + dy * r * 0.72f),
                end = androidx.compose.ui.geometry.Offset(r + dx * r * 0.95f, r + dy * r * 0.95f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
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
private fun VoChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
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
                .padding(vertical = 8.dp, horizontal = 6.dp),
        )
    }
}

@Composable
private fun VoSwitch(checked: Boolean, label: String) {
    val palette = LocalAppPalette.current
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 18.dp else 2.dp,
        animationSpec = tween(180),
        label = "voSwitchThumb",
    )
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 26.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(if (checked) palette.primary else palette.surfaceMuted)
            .border(1.dp, if (checked) palette.primary else palette.border, RoundedCornerShape(13.dp))
            .padding(3.dp)
            .semantics {
                role = Role.Button
                contentDescription = "$label, ${if (checked) "aktif" else "nonaktif"}"
            },
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(18.dp)
                .clip(CircleShape)
                .background(if (checked) palette.onPrimary else palette.textMuted),
        )
    }
}

@Composable
private fun VoToggleRow(label: String, desc: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = { onCheckedChange(!checked) })
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = desc,
                fontSize = 11.sp,
                color = palette.textMuted,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        VoSwitch(checked = checked, label = label)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(palette.surface)
                .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text = if (checked) "AKTIF" else "MATI",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (checked) palette.primary else palette.textMuted,
            )
        }
    }
}

@Composable
private fun VoActionButton(
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
        shape = RoundedCornerShape(10.dp),
        color = when {
            !enabled -> palette.surfaceMuted
            accent -> palette.primary
            else -> palette.surface
        },
        contentColor = when {
            !enabled -> palette.textMuted
            accent -> palette.onPrimary
            else -> palette.textPrimary
        },
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(
            1.dp,
            when {
                !enabled -> palette.border
                accent -> palette.primary
                else -> palette.border
            },
        ),
        modifier = modifier,
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
        )
    }
}
