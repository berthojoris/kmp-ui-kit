package com.example.uiapp.ui.continuity

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.FlatSecondaryButton
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private enum class PerangkatJenis { TV, SPEAKER, PONSEL }

private data class Perangkat(
    val id: String,
    val nama: String,
    val jenis: PerangkatJenis,
    val keterangan: String,
    val tersedia: Boolean,
    val rawanGagal: Boolean,
)

private val PerangkatOutput: List<Perangkat> = listOf(
    Perangkat("iphone_saya", "iPhone saya", PerangkatJenis.PONSEL, "Keluaran audio perangkat ini", tersedia = true, rawanGagal = false),
    Perangkat("tv_ruang", "TV Ruang Keluarga", PerangkatJenis.TV, "AirPlay 2 • Ruang keluarga", tersedia = true, rawanGagal = false),
    Perangkat("speaker_dapur", "Speaker Dapur", PerangkatJenis.SPEAKER, "AirPlay 2 • Dapur", tersedia = true, rawanGagal = false),
    Perangkat("speaker_teras", "Speaker Teras", PerangkatJenis.SPEAKER, "Sinyal Wi-Fi lemah di teras", tersedia = true, rawanGagal = true),
    Perangkat("tv_kamar", "TV Kamar Tamu", PerangkatJenis.TV, "Sedang dipakai anggota keluarga", tersedia = false, rawanGagal = false),
)

private enum class StatusKoneksi(val label: String) {
    TIDAK_ADA("Tidak ada perangkat terhubung"),
    MENGHUBUNGKAN("Menghubungkan…"),
    TERHUBUNG("Terhubung"),
    GAGAL("Gagal terhubung"),
}

private enum class StatusPencarian(val label: String) {
    SIAP("Daftar perangkat siap"),
    MENCARI("Mencari perangkat…"),
    KOSONG("Tidak ada perangkat ditemukan"),
    GAGAL("Pencarian perangkat gagal"),
}

private data class HandoffRiwayat(
    val id: String,
    val judul: String,
    val asal: String,
    val waktu: String,
    val aktif: Boolean,
)

private val HandoffRiwayatMock: List<HandoffRiwayat> = listOf(
    HandoffRiwayat("handoff_1", "Dokumen \"Proposal Villa Cendana\"", "dari iPad Cendana", "Baru saja", aktif = true),
    HandoffRiwayat("handoff_2", "Catatan rapat mingguan", "dari MacBook Cendana", "Kemarin, 20.14", aktif = false),
)

private const val DURASI_LAGU = 214

private fun formatWaktu(totalDetik: Int): String {
    val menit = totalDetik / 60
    val detik = totalDetik % 60
    val detikTeks = if (detik < 10) "0$detik" else "$detik"
    return "$menit:$detikTeks"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContinuityLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var saranVisible by remember { mutableStateOf(true) }
    var geserTarget by remember { mutableFloatStateOf(0f) }
    var handoffSelesai by remember { mutableStateOf(false) }
    var modalBuka by remember { mutableStateOf(false) }
    var pencarian by remember { mutableStateOf(StatusPencarian.SIAP) }
    var pencarianTarget by remember { mutableStateOf(StatusPencarian.SIAP) }
    var pencarianSeq by remember { mutableIntStateOf(0) }
    var koneksi by remember { mutableStateOf(StatusKoneksi.TIDAK_ADA) }
    var pilihanId by remember { mutableStateOf<String?>(null) }
    var terhubungId by remember { mutableStateOf<String?>(null) }
    var connectSeq by remember { mutableIntStateOf(0) }
    var connectTargetId by remember { mutableStateOf("iphone_saya") }
    var volume by remember { mutableIntStateOf(45) }
    var playing by remember { mutableStateOf(true) }
    var posisiDetik by remember { mutableIntStateOf(37) }
    var logPesan by remember { mutableStateOf("Pilih keluaran audio di pemilih AirPlay (Simulasi).") }

    val geserAnim by animateFloatAsState(
        targetValue = geserTarget,
        animationSpec = tween(180),
        label = "saranGeser",
    )

    // Pencarian perangkat: state mencari → daftar, kosong, atau gagal.
    LaunchedEffect(pencarianSeq) {
        if (pencarianSeq > 0) {
            pencarian = StatusPencarian.MENCARI
            delay(1600)
            pencarian = pencarianTarget
        }
    }

    // Proses koneksi: gagal untuk perangkat dengan Wi-Fi lemah, berhasil selainnya.
    LaunchedEffect(connectSeq) {
        if (connectSeq > 0) {
            koneksi = StatusKoneksi.MENGHUBUNGKAN
            delay(1300)
            val target = PerangkatOutput.firstOrNull { it.id == connectTargetId }
            if (target != null && target.rawanGagal) {
                koneksi = StatusKoneksi.GAGAL
                terhubungId = null
                logPesan = "Gagal terhubung ke ${target.nama} (Simulasi galat jaringan lokal)."
            } else if (target != null) {
                koneksi = StatusKoneksi.TERHUBUNG
                terhubungId = target.id
                logPesan = "Audio dialihkan ke ${target.nama}. Mini-player mengambil alih (Simulasi)."
            }
        }
    }

    // Progres lagu mini-player saat pemutaran berjalan.
    LaunchedEffect(koneksi, playing) {
        if (koneksi == StatusKoneksi.TERHUBUNG && playing) {
            while (posisiDetik < DURASI_LAGU) {
                delay(1000)
                posisiDetik++
            }
        }
    }

    val perangkatTerhubung = PerangkatOutput.firstOrNull { it.id == terhubungId }
    val perangkatDipilih = PerangkatOutput.firstOrNull { it.id == pilihanId }

    fun mulaiPencarian(target: StatusPencarian) {
        pencarianTarget = target
        pencarianSeq++
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Handoff & AirPlay Picker",
                subtitle = "Saran beranda & output audio — Simulasi",
                onBack = onBack,
            )
        },
        bottomBar = {
            if (koneksi == StatusKoneksi.TERHUBUNG && perangkatTerhubung != null) {
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
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Surface(
                                onClick = {
                                    playing = !playing
                                    logPesan = if (playing) "Pemutaran dilanjutkan (Simulasi)." else "Pemutaran dijeda (Simulasi)."
                                },
                                shape = RoundedCornerShape(50),
                                color = palette.primary,
                                contentColor = palette.onPrimary,
                                shadowElevation = 0.dp,
                                tonalElevation = 0.dp,
                                modifier = Modifier
                                    .size(38.dp)
                                    .semantics {
                                        role = Role.Button
                                        contentDescription = if (playing) "Jeda lagu" else "Putar lagu"
                                    },
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    if (playing) {
                                        IkonJeda(tint = palette.onPrimary)
                                    } else {
                                        IkonPutar(tint = palette.onPrimary)
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Senja di Batu — Band Cendana",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "Diputar di ${perangkatTerhubung?.nama ?: "perangkat"} • ${if (playing) "Memutar" else "Dijeda"}",
                                    fontSize = 11.sp,
                                    color = palette.textMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Text(
                                text = formatWaktu(posisiDetik),
                                fontSize = 11.sp,
                                color = palette.textMuted,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(palette.surfaceMuted)
                                .border(1.dp, palette.border, RoundedCornerShape(3.dp)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = (posisiDetik.toFloat() / DURASI_LAGU).coerceIn(0f, 1f))
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(palette.primary),
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FlatSecondaryButton(
                                text = "Ambil Alih di iPhone saya",
                                onClick = {
                                    connectTargetId = "iphone_saya"
                                    connectSeq++
                                },
                                modifier = Modifier.weight(1f),
                            )
                            FlatSecondaryButton(
                                text = "Putuskan",
                                onClick = {
                                    koneksi = StatusKoneksi.TIDAK_ADA
                                    terhubungId = null
                                    posisiDetik = 37
                                    logPesan = "Koneksi diputus. Tidak ada perangkat terhubung (Simulasi)."
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
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
                    title = "Kontinuitas Antarperangkat",
                    subtitle = "Handoff, saran beranda, dan pemilih output AirPlay",
                    modifier = Modifier.weight(1f),
                )
                SimulasiPill()
            }

            LabIntroCard(
                title = "Simulasi, tanpa layanan kontinuitas asli",
                body = "Panel saran, pemilih AirPlay, dan pemindahan handoff di lab ini sepenuhnya mock. " +
                    "Tidak ada perangkat nyata yang dicari, dihubungkan, atau dikendalikan volume-nya.",
            )

            // ===== Beranda aplikasi dengan saran berkelanjutan =====
            LabSectionTitle(
                title = "Beranda Aplikasi",
                subtitle = "Panel \"Berkelanjutan\" dapat dibuang dengan gestur geser",
            )

            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Jumat, 12 September • Beranda",
                        fontSize = 12.sp,
                        color = palette.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = if (saranVisible) "1 saran" else "0 saran",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textSecondary,
                    )
                }

                if (saranVisible) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = palette.surfaceMuted,
                        contentColor = palette.textPrimary,
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { translationX = geserAnim }
                            .pointerInput(saranVisible) {
                                detectDragGestures(
                                    onDragEnd = {
                                        if (geserTarget > 130f || geserTarget < -130f) {
                                            saranVisible = false
                                            geserTarget = 0f
                                            logPesan = "Saran \"Berkelanjutan\" dibuang lewat gestur geser (Simulasi)."
                                        } else {
                                            geserTarget = 0f
                                        }
                                    },
                                    onDragCancel = { geserTarget = 0f },
                                ) { _, dragAmount ->
                                    geserTarget += dragAmount.x
                                }
                            }
                            .semantics {
                                role = Role.Button
                                contentDescription =
                                    "Saran berkelanjutan dari MacBook Cendana. Geser kiri atau kanan untuk membuang."
                            },
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            IkonLanjutkan(tint = palette.primary)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Berkelanjutan",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textMuted,
                                )
                                Text(
                                    text = "Safari — Resep nasi goreng kampung",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "Dari MacBook Cendana • geser untuk membuang",
                                    fontSize = 11.sp,
                                    color = palette.textMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Surface(
                                onClick = {
                                    saranVisible = false
                                    geserTarget = 0f
                                    logPesan = "Saran \"Berkelanjutan\" dibuang lewat tombol Buang (Simulasi)."
                                },
                                shape = RoundedCornerShape(50),
                                color = palette.surface,
                                contentColor = palette.textSecondary,
                                shadowElevation = 0.dp,
                                tonalElevation = 0.dp,
                                border = BorderStroke(1.dp, palette.border),
                                modifier = Modifier.semantics {
                                    role = Role.Button
                                    contentDescription = "Buang saran berkelanjutan"
                                },
                            ) {
                                Text(
                                    text = "Buang",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.surfaceMuted)
                            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Tidak ada saran berkelanjutan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textSecondary,
                        )
                        Text(
                            text = "Saran dari perangkat terdekat akan muncul di sini saat tersedia.",
                            fontSize = 11.sp,
                            color = palette.textMuted,
                            textAlign = TextAlign.Center,
                        )
                        FlatSecondaryButton(
                            text = "Pulihkan Saran (Simulasi)",
                            onClick = {
                                saranVisible = true
                                geserTarget = 0f
                                logPesan = "Saran berkelanjutan dipulihkan (Simulasi)."
                            },
                        )
                    }
                }

                Text(
                    text = "Riwayat Handoff",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )

                HandoffRiwayatMock.forEach { riwayat ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = palette.surfaceMuted,
                        contentColor = palette.textPrimary,
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
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    StatusPil(
                                        teks = when {
                                            riwayat.aktif && !handoffSelesai -> "AKTIF"
                                            riwayat.aktif -> "SELESAI"
                                            else -> "RIWAYAT"
                                        },
                                        warna = when {
                                            riwayat.aktif && !handoffSelesai -> palette.info
                                            riwayat.aktif -> palette.success
                                            else -> palette.border
                                        },
                                    )
                                    Text(
                                        text = riwayat.waktu,
                                        fontSize = 10.sp,
                                        color = palette.textMuted,
                                    )
                                }
                                Text(
                                    text = riwayat.judul,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = riwayat.asal,
                                    fontSize = 11.sp,
                                    color = palette.textMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            if (riwayat.aktif && !handoffSelesai) {
                                Surface(
                                    onClick = {
                                        handoffSelesai = true
                                        logPesan = "Handoff dilanjutkan di perangkat ini (Simulasi)."
                                    },
                                    shape = RoundedCornerShape(50),
                                    color = palette.primary,
                                    contentColor = palette.onPrimary,
                                    shadowElevation = 0.dp,
                                    tonalElevation = 0.dp,
                                    modifier = Modifier.semantics {
                                        role = Role.Button
                                        contentDescription = "Lanjutkan ${riwayat.judul} di perangkat ini"
                                    },
                                ) {
                                    Text(
                                        text = "Lanjutkan",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    )
                                }
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
                        text = logPesan,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                        modifier = Modifier.padding(10.dp),
                    )
                }
            }

            // ===== Pemilih AirPlay =====
            LabSectionTitle(
                title = "Pemilih AirPlay",
                subtitle = "Output audio, ceklis perangkat, dan kontrol volume speaker",
            )

            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    IkonAirPlay(
                        tint = when (koneksi) {
                            StatusKoneksi.TERHUBUNG -> palette.success
                            StatusKoneksi.GAGAL -> palette.danger
                            else -> palette.textMuted
                        }
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = koneksi.label,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = when {
                                koneksi == StatusKoneksi.TERHUBUNG && perangkatTerhubung != null ->
                                    "Keluaran: ${perangkatTerhubung.nama}"
                                koneksi == StatusKoneksi.MENGHUBUNGKAN -> "Menyambung ke ${perangkatDipilih?.nama ?: "perangkat"}…"
                                else -> pencarian.label
                            },
                            fontSize = 11.sp,
                            color = palette.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                FlatPrimaryButton(
                    text = "Buka Pemilih AirPlay",
                    onClick = { modalBuka = true },
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FlatSecondaryButton(
                        text = "Mulai Pencarian",
                        onClick = { mulaiPencarian(StatusPencarian.SIAP) },
                        modifier = Modifier.weight(1f),
                    )
                    FlatSecondaryButton(
                        text = "Simulasi Kosong",
                        onClick = { mulaiPencarian(StatusPencarian.KOSONG) },
                        modifier = Modifier.weight(1f),
                    )
                }
                FlatSecondaryButton(
                    text = "Simulasi Galat Pencarian",
                    onClick = { mulaiPencarian(StatusPencarian.GAGAL) },
                )
            }

            // ===== Catatan =====
            LabSectionTitle(
                title = "Catatan Simulasi",
                subtitle = "Cakupan lab ini",
            )

            LabCard {
                Text("• Tidak ada perangkat asli yang dipindai lewat jaringan lokal.", fontSize = 12.sp, color = palette.textSecondary, lineHeight = 17.sp)
                Text("• Volume speaker dikendalikan dari mock, bukan dari perangkat AirPlay sungguhan.", fontSize = 12.sp, color = palette.textSecondary, lineHeight = 17.sp)
                Text("• Mini-player mengambil alih audio seperti pola lab media, tanpa pemutaran nyata.", fontSize = 12.sp, color = palette.textSecondary, lineHeight = 17.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ===== Modal pemilih AirPlay =====
    if (modalBuka) {
        BackHandler(enabled = true) { modalBuka = false }

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(palette.background.copy(alpha = 0.6f))
                    .clickable { modalBuka = false }
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    color = palette.surface,
                    contentColor = palette.textPrimary,
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(16.dp)
                            .heightIn(max = 560.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Pilih Output Audio",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                )
                                Text(
                                    text = pencarian.label,
                                    fontSize = 11.sp,
                                    color = palette.textMuted,
                                )
                            }
                            SimulasiPill()
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                onClick = { modalBuka = false },
                                shape = RoundedCornerShape(50),
                                color = palette.surfaceMuted,
                                contentColor = palette.textPrimary,
                                shadowElevation = 0.dp,
                                tonalElevation = 0.dp,
                                border = BorderStroke(1.dp, palette.border),
                                modifier = Modifier.semantics {
                                    role = Role.Button
                                    contentDescription = "Tutup pemilih AirPlay"
                                },
                            ) {
                                Text(
                                    text = "Tutup",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                )
                            }
                        }

                        when (pencarian) {
                            StatusPencarian.MENCARI -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    TigaTitik()
                                    Text(
                                        text = "Mencari perangkat di sekitar…",
                                        fontSize = 12.sp,
                                        color = palette.textSecondary,
                                    )
                                }
                                FlatSecondaryButton(
                                    text = "Berhenti Mencari",
                                    onClick = { pencarian = StatusPencarian.SIAP },
                                )
                            }
                            StatusPencarian.KOSONG -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        text = "Tidak ada perangkat ditemukan",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.textPrimary,
                                    )
                                    Text(
                                        text = "Pastikan TV atau speaker mendukung AirPlay dan berada di jaringan yang sama.",
                                        fontSize = 11.sp,
                                        color = palette.textMuted,
                                        textAlign = TextAlign.Center,
                                    )
                                    FlatSecondaryButton(
                                        text = "Cari Lagi",
                                        onClick = { mulaiPencarian(StatusPencarian.SIAP) },
                                    )
                                }
                            }
                            StatusPencarian.GAGAL -> {
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
                                        text = "Pencarian perangkat gagal (Simulasi galat). Periksa jaringan lokal lalu coba lagi.",
                                        fontSize = 12.sp,
                                        color = palette.textPrimary,
                                        modifier = Modifier.padding(12.dp),
                                    )
                                }
                                FlatSecondaryButton(
                                    text = "Coba Lagi",
                                    onClick = { mulaiPencarian(StatusPencarian.SIAP) },
                                )
                            }
                            StatusPencarian.SIAP -> {
                                PerangkatOutput.forEach { perangkat ->
                                    val terpilih = perangkat.id == pilihanId
                                    Surface(
                                        onClick = {
                                            if (perangkat.tersedia) {
                                                pilihanId = perangkat.id
                                                logPesan = "Keluaran dipilih: ${perangkat.nama} (Simulasi)."
                                            }
                                        },
                                        enabled = perangkat.tersedia,
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (terpilih) palette.primaryContainer else palette.surfaceMuted,
                                        contentColor = if (perangkat.tersedia) palette.textPrimary else palette.textMuted,
                                        shadowElevation = 0.dp,
                                        tonalElevation = 0.dp,
                                        border = BorderStroke(
                                            1.dp,
                                            if (terpilih) palette.primary else palette.border,
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .semantics {
                                                role = Role.Button
                                                contentDescription =
                                                    "${perangkat.nama}, ${perangkat.keterangan}, " +
                                                        when {
                                                            !perangkat.tersedia -> "tidak tersedia"
                                                            terpilih -> "terpilih"
                                                            else -> "tersedia"
                                                        }
                                            },
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        ) {
                                            IkonPerangkat(
                                                jenis = perangkat.jenis,
                                                tint = if (perangkat.tersedia) palette.textSecondary else palette.textMuted,
                                            )
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = perangkat.nama,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (perangkat.tersedia) palette.textPrimary else palette.textMuted,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                                Text(
                                                    text = if (perangkat.tersedia) perangkat.keterangan else "${perangkat.keterangan} • tidak tersedia",
                                                    fontSize = 11.sp,
                                                    color = palette.textMuted,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                            if (terpilih) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                ) {
                                                    IkonCentang(tint = palette.primary)
                                                    Text(
                                                        text = "Terpilih",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = palette.primary,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Pemisah "Kontrol Speaker Volume"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(1.dp)
                                            .background(palette.border)
                                    )
                                    Text(
                                        text = "Kontrol Speaker Volume",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.textMuted,
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(1.dp)
                                            .background(palette.border)
                                    )
                                }

                                val volumeAktif = koneksi == StatusKoneksi.TERHUBUNG
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Text(
                                        text = "Volume",
                                        fontSize = 12.sp,
                                        color = if (volumeAktif) palette.textSecondary else palette.textMuted,
                                    )
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    ) {
                                        for (i in 0 until 20) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(10.dp)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(
                                                        if (i < volume / 5) palette.primary else palette.surfaceMuted
                                                    )
                                                    .border(1.dp, palette.border, RoundedCornerShape(3.dp)),
                                            )
                                        }
                                    }
                                    Text(
                                        text = "$volume%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (volumeAktif) palette.textPrimary else palette.textMuted,
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    FlatSecondaryButton(
                                        text = "Volume − 5",
                                        onClick = { volume = (volume - 5).coerceAtLeast(0) },
                                        enabled = volumeAktif,
                                        modifier = Modifier.weight(1f),
                                    )
                                    FlatSecondaryButton(
                                        text = "Volume + 5",
                                        onClick = { volume = (volume + 5).coerceAtMost(100) },
                                        enabled = volumeAktif,
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                                if (!volumeAktif) {
                                    Text(
                                        text = "Kontrol volume aktif setelah perangkat terhubung.",
                                        fontSize = 10.sp,
                                        color = palette.textMuted,
                                    )
                                }

                                if (koneksi == StatusKoneksi.GAGAL) {
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
                                            text = "Sambungan terputus sebelum audio berpindah. Coba perangkat lain (Simulasi galat).",
                                            fontSize = 12.sp,
                                            color = palette.textPrimary,
                                            modifier = Modifier.padding(12.dp),
                                        )
                                    }
                                }

                                if (koneksi == StatusKoneksi.TERHUBUNG) {
                                    FlatSecondaryButton(
                                        text = "Putuskan Koneksi",
                                        onClick = {
                                            koneksi = StatusKoneksi.TIDAK_ADA
                                            terhubungId = null
                                            posisiDetik = 37
                                            logPesan = "Koneksi diputus dari pemilih (Simulasi)."
                                        },
                                    )
                                } else {
                                    FlatPrimaryButton(
                                        text = when (koneksi) {
                                            StatusKoneksi.MENGHUBUNGKAN -> "Menghubungkan…"
                                            else -> "Hubungkan ke ${perangkatDipilih?.nama ?: "perangkat"}"
                                        },
                                        onClick = {
                                            val target = pilihanId
                                            if (target != null) {
                                                connectTargetId = target
                                                connectSeq++
                                            }
                                        },
                                        enabled = pilihanId != null && koneksi != StatusKoneksi.MENGHUBUNGKAN,
                                    )
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
private fun StatusPil(teks: String, warna: Color) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(50),
        color = warna.copy(alpha = 0.15f),
        contentColor = palette.textSecondary,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, warna),
    ) {
        Text(
            text = teks,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textSecondary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
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
private fun TigaTitik() {
    val transition = rememberInfiniteTransition(label = "titikCari")
    val a1 by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(450), RepeatMode.Reverse),
        label = "titik1",
    )
    val a2 by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(450, delayMillis = 150), RepeatMode.Reverse),
        label = "titik2",
    )
    val a3 by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(450, delayMillis = 300), RepeatMode.Reverse),
        label = "titik3",
    )
    val palette = LocalAppPalette.current
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(palette.primary.copy(alpha = a1)))
        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(palette.primary.copy(alpha = a2)))
        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(palette.primary.copy(alpha = a3)))
    }
}

@Composable
private fun IkonLanjutkan(tint: Color, size: Dp = 28.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        drawCircle(
            color = tint.copy(alpha = 0.15f),
            radius = s / 2f,
        )
        drawCircle(
            color = tint,
            radius = s / 2f - 1.dp.toPx(),
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        // Dua panah melingkar sederhana (handoff).
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(s * 0.3f, s * 0.42f),
            end = androidx.compose.ui.geometry.Offset(s * 0.68f, s * 0.42f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(s * 0.55f, s * 0.30f),
            end = androidx.compose.ui.geometry.Offset(s * 0.70f, s * 0.42f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(s * 0.70f, s * 0.60f),
            end = androidx.compose.ui.geometry.Offset(s * 0.32f, s * 0.60f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(s * 0.45f, s * 0.72f),
            end = androidx.compose.ui.geometry.Offset(s * 0.30f, s * 0.60f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun IkonAirPlay(tint: Color, size: Dp = 28.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        // Segitiga pancaran (AirPlay) di dalam persegi layar.
        drawRoundRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(s * 0.12f, s * 0.16f),
            size = androidx.compose.ui.geometry.Size(s * 0.76f, s * 0.52f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(s * 0.32f, s * 0.88f),
            end = androidx.compose.ui.geometry.Offset(s * 0.5f, s * 0.62f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(s * 0.5f, s * 0.62f),
            end = androidx.compose.ui.geometry.Offset(s * 0.68f, s * 0.88f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun IkonPerangkat(jenis: PerangkatJenis, tint: Color, size: Dp = 24.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (jenis) {
            PerangkatJenis.TV -> {
                drawRoundRect(
                    color = tint,
                    topLeft = androidx.compose.ui.geometry.Offset(s * 0.08f, s * 0.14f),
                    size = androidx.compose.ui.geometry.Size(s * 0.84f, s * 0.52f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                    style = stroke,
                )
                drawLine(
                    color = tint,
                    start = androidx.compose.ui.geometry.Offset(s * 0.5f, s * 0.66f),
                    end = androidx.compose.ui.geometry.Offset(s * 0.5f, s * 0.82f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = tint,
                    start = androidx.compose.ui.geometry.Offset(s * 0.32f, s * 0.86f),
                    end = androidx.compose.ui.geometry.Offset(s * 0.68f, s * 0.86f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
            PerangkatJenis.SPEAKER -> {
                drawRoundRect(
                    color = tint,
                    topLeft = androidx.compose.ui.geometry.Offset(s * 0.24f, s * 0.08f),
                    size = androidx.compose.ui.geometry.Size(s * 0.52f, s * 0.84f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(5.dp.toPx(), 5.dp.toPx()),
                    style = stroke,
                )
                drawCircle(tint, radius = s * 0.1f, center = androidx.compose.ui.geometry.Offset(s * 0.5f, s * 0.32f), style = stroke)
                drawCircle(tint, radius = s * 0.07f, center = androidx.compose.ui.geometry.Offset(s * 0.5f, s * 0.68f), style = stroke)
            }
            PerangkatJenis.PONSEL -> {
                drawRoundRect(
                    color = tint,
                    topLeft = androidx.compose.ui.geometry.Offset(s * 0.3f, s * 0.08f),
                    size = androidx.compose.ui.geometry.Size(s * 0.4f, s * 0.84f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    style = stroke,
                )
                drawLine(
                    color = tint,
                    start = androidx.compose.ui.geometry.Offset(s * 0.42f, s * 0.82f),
                    end = androidx.compose.ui.geometry.Offset(s * 0.58f, s * 0.82f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
private fun IkonCentang(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(s * 0.18f, s * 0.52f),
            end = androidx.compose.ui.geometry.Offset(s * 0.42f, s * 0.76f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(s * 0.42f, s * 0.76f),
            end = androidx.compose.ui.geometry.Offset(s * 0.82f, s * 0.26f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun IkonPutar(tint: Color, size: Dp = 16.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(s * 0.3f, s * 0.2f),
            end = androidx.compose.ui.geometry.Offset(s * 0.78f, s * 0.5f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(s * 0.78f, s * 0.5f),
            end = androidx.compose.ui.geometry.Offset(s * 0.3f, s * 0.8f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(s * 0.3f, s * 0.8f),
            end = androidx.compose.ui.geometry.Offset(s * 0.3f, s * 0.2f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun IkonJeda(tint: Color, size: Dp = 16.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(s * 0.36f, s * 0.2f),
            end = androidx.compose.ui.geometry.Offset(s * 0.36f, s * 0.8f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(s * 0.64f, s * 0.2f),
            end = androidx.compose.ui.geometry.Offset(s * 0.64f, s * 0.8f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}
