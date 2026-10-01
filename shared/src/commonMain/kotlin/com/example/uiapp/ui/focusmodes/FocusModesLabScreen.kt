package com.example.uiapp.ui.focusmodes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.AppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.FlatSecondaryButton
import com.example.uiapp.ui.components.FlatTextField
import com.example.uiapp.ui.components.FlatToggle
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class FocusIkon { KERJA, TIDUR, PRIBADI, MENGEMUDI, FITNESS, CUSTOM }

private enum class FilterPercakapan(val label: String) {
    SEMUA("Semua Orang"),
    PENTING("Orang Terpenting"),
    TIDAK_ADA("Tidak Ada"),
}

private data class FocusMode(
    val id: String,
    val nama: String,
    val desc: String,
    val ikon: FocusIkon,
    val dibisukan: Int,
    val appDiizinkan: List<String>,
    val orangDiizinkan: List<String>,
)

private data class MockNotif(val id: String, val app: String, val judul: String, val isi: String)

private val fokusAwal = listOf(
    FocusMode("kerja", "Kerja", "Bisukan notifikasi pribadi selama jam kerja", FocusIkon.KERJA, 12, listOf("Surel", "Kalender", "Tugas"), listOf("Nadia", "Pak Budi")),
    FocusMode("tidur", "Tidur", "Layar tenang sepanjang malam", FocusIkon.TIDUR, 21, listOf("Alarm", "Cuaca"), listOf("Keluarga")),
    FocusMode("pribadi", "Pribadi", "Waktu untuk diri sendiri tanpa gangguan", FocusIkon.PRIBADI, 8, listOf("Musik", "Catatan"), listOf("Dina", "Rian", "Ibu")),
    FocusMode("mengemudi", "Mengemudi", "Fokus ke jalan, balasan otomatis aktif", FocusIkon.MENGEMUDI, 15, listOf("Peta", "Telepon"), listOf("Keluarga")),
    FocusMode("fitness", "Fitness", "Sesi latihan tanpa notifikasi", FocusIkon.FITNESS, 6, listOf("Musik", "Kesehatan"), listOf("Pelatih")),
)

private val notifAwal = listOf(
    MockNotif("n1", "Surel", "Rapat proyek Nusantara", "Rencana revisi menunggu persetujuanmu."),
    MockNotif("n2", "Pesan", "Dina", "Bisa tolong kirim filenya hari ini?"),
    MockNotif("n3", "Toko", "Promo akhir pekan", "Diskon 20% untuk pelanggan setia."),
    MockNotif("n4", "Kalender", "Tinjauan desain", "Besok pukul 09.00 di Ruang Cendana."),
    MockNotif("n5", "Sosial", "Rian menyukai fotomu", "2 menit yang lalu."),
)

private val hariMinggu = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

private val jamRingkasan = listOf("12.30", "17.00", "21.00")

private fun warnaFokus(ikon: FocusIkon, palette: AppPalette): Color {
    return when (ikon) {
        FocusIkon.KERJA -> palette.info
        FocusIkon.TIDUR -> palette.primary
        FocusIkon.PRIBADI -> palette.danger
        FocusIkon.MENGEMUDI -> palette.warning
        FocusIkon.FITNESS -> palette.success
        FocusIkon.CUSTOM -> palette.textSecondary
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun FocusModesLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()

    val modes = remember { mutableStateListOf<FocusMode>().apply { addAll(fokusAwal) } }
    val notifList = remember { mutableStateListOf<MockNotif>().apply { addAll(notifAwal) } }
    val hariAktif = remember { mutableStateListOf("Sen", "Rab", "Jum") }

    var selectedId by remember { mutableStateOf("kerja") }
    var activeId by remember { mutableStateOf<String?>(null) }
    var mengaktifkan by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf(FilterPercakapan.PENTING) }
    var ulangiJadwal by remember { mutableStateOf(true) }
    var ringkasanOn by remember { mutableStateOf(false) }
    var jamRingkasanAktif by remember { mutableStateOf("21.00") }
    var banner by remember { mutableStateOf<String?>(null) }

    var dialogCustom by remember { mutableStateOf(false) }
    var namaCustom by remember { mutableStateOf("") }
    var errorCustom by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(banner) {
        if (banner != null) {
            delay(2200)
            banner = null
        }
    }

    val selected = modes.firstOrNull { it.id == selectedId }
    val aktif = modes.firstOrNull { it.id == activeId }
    val notifPreview = remember { notifList.take(3) }
    val orangLolosSelected = remember(selected, filter) {
        when (filter) {
            FilterPercakapan.SEMUA -> selected?.orangDiizinkan ?: emptyList()
            FilterPercakapan.PENTING -> selected?.orangDiizinkan?.take(1) ?: emptyList()
            FilterPercakapan.TIDAK_ADA -> emptyList()
        }
    }
    val orangLolosAktif = remember(aktif, filter) {
        when (filter) {
            FilterPercakapan.SEMUA -> aktif?.orangDiizinkan ?: emptyList()
            FilterPercakapan.PENTING -> aktif?.orangDiizinkan?.take(1) ?: emptyList()
            FilterPercakapan.TIDAK_ADA -> emptyList()
        }
    }

    fun aktifkanFokus(mode: FocusMode) {
        scope.launch {
            mengaktifkan = true
            delay(600)
            activeId = mode.id
            mengaktifkan = false
            banner = "Focus ${mode.nama} aktif (Simulasi — tanpa integrasi sistem)"
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = palette.background,
            topBar = {
                UiTopBar(
                    title = "Focus Modes & Filtering",
                    subtitle = "DND, filter notifikasi — Simulasi",
                    onBack = onBack,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                LabIntroCard(
                    title = "Focus Modes & Notification Filtering — Simulasi",
                    body = "Jelajahi mode fokus lengkap dengan pratinjau beranda, jadwal berulang, ringkasan notifikasi, dan filter percakapan. Tidak ada integrasi sistem DND/Notifikasi sungguhan.",
                )

                // ===== Daftar focus =====
                LabCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LabSectionTitle("Daftar Focus", "Ketuk untuk melihat detail")
                        SimulasiPill("Simulasi")
                    }
                    modes.forEach { mode ->
                        val dipilih = mode.id == selectedId
                        val sedangAktif = mode.id == activeId
                        val tint = warnaFokus(mode.ikon, palette)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (dipilih) palette.primaryContainer else palette.surfaceMuted,
                                    RoundedCornerShape(12.dp),
                                )
                                .border(
                                    1.dp,
                                    if (dipilih) palette.primary else palette.border,
                                    RoundedCornerShape(12.dp),
                                )
                                .clickable {
                                    selectedId = mode.id
                                    banner = "Focus ${mode.nama} dipilih"
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(tint.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .border(1.dp, tint, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                FocusIcon(mode.ikon, tint)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(mode.nama, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                Text(
                                    mode.desc,
                                    fontSize = 11.sp,
                                    color = palette.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    "${mode.dibisukan} notifikasi dibisukan · ${mode.appDiizinkan.size} app diizinkan",
                                    fontSize = 10.sp,
                                    color = palette.textMuted,
                                )
                            }
                            if (sedangAktif) {
                                StatusPill("AKTIF", palette.success)
                            } else if (dipilih) {
                                StatusPill("DIPILIH", palette.info)
                            }
                        }
                    }
                    FlatSecondaryButton(
                        text = "+ Buat Focus Custom",
                        onClick = {
                            dialogCustom = true
                            namaCustom = ""
                            errorCustom = null
                        },
                    )
                }

                // ===== Detail focus terpilih =====
                if (selected != null) {
                    LabCard {
                        val tint = warnaFokus(selected.ikon, palette)
                        LabSectionTitle("Detail: ${selected.nama}", "Aplikasi & orang yang diizinkan")
                        Text("Aplikasi yang diizinkan", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            selected.appDiizinkan.forEach { app ->
                                ChipKecil(app, tint)
                            }
                        }
                        Text("Orang yang diizinkan", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val orang = orangLolosSelected
                            if (orang.isEmpty()) {
                                Text(
                                    "Tidak ada percakapan yang lolos filter (status: dibisukan semua).",
                                    fontSize = 11.sp,
                                    color = palette.textMuted,
                                )
                            } else {
                                orang.forEach { nama -> ChipKecil(nama, tint) }
                            }
                        }
                        FlatPrimaryButton(
                            text = when {
                                mengaktifkan -> "Mengaktifkan…"
                                activeId == selected.id -> "Nonaktifkan Focus"
                                else -> "Aktifkan Focus"
                            },
                            onClick = {
                                if (activeId == selected.id) {
                                    activeId = null
                                    banner = "Focus dinonaktifkan"
                                } else {
                                    aktifkanFokus(selected)
                                }
                            },
                            enabled = !mengaktifkan,
                        )
                    }
                }

                // ===== Pratinjau beranda & layar kunci =====
                LabCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LabSectionTitle("Pratinjau Beranda & Layar Kunci", "Notifikasi yang dibisukan tampil di sini")
                        SimulasiPill("Simulasi")
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text("09.41", fontSize = 40.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                            Text("Selasa, 30 September", fontSize = 12.sp, color = palette.textMuted)
                            if (aktif != null) {
                                val tint = warnaFokus(aktif.ikon, palette)
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = tint.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, tint),
                                    shadowElevation = 0.dp,
                                    tonalElevation = 0.dp,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    ) {
                                        FocusIcon(aktif.ikon, tint)
                                        Text(
                                            "${aktif.nama} Aktif",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = tint,
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    "Tidak ada focus aktif — semua notifikasi tampil.",
                                    fontSize = 11.sp,
                                    color = palette.textMuted,
                                )
                            }

                            if (aktif != null) {
                                Text(
                                    text = "${notifList.size} notifikasi dibisukan · filter: ${filter.label}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textSecondary,
                                )
                            }

                            notifPreview.forEach { notif ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(palette.surface, RoundedCornerShape(12.dp))
                                        .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${notif.app} · ${notif.judul}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                        Text(notif.isi, fontSize = 10.sp, color = palette.textSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    }
                                    if (aktif != null) {
                                        StatusPill("DIBISUKAN", palette.textMuted)
                                    }
                                }
                            }

                            if (aktif != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("App diizinkan", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.textMuted)
                                        Text(aktif.appDiizinkan.joinToString(", "), fontSize = 10.sp, color = palette.textSecondary)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Orang diizinkan", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.textMuted)
                                        val orang = orangLolosAktif
                                        Text(
                                            if (orang.isEmpty()) "Tidak ada" else orang.joinToString(", "),
                                            fontSize = 10.sp,
                                            color = palette.textSecondary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ===== Ulangi jadwal =====
                LabCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            LabSectionTitle("Ulangi Jadwal", "Focus aktif otomatis pada hari terpilih")
                        }
                        Box(modifier = Modifier.semantics(mergeDescendants = true) {
                            role = Role.Switch
                            contentDescription = "Ulangi jadwal focus"
                        }) {
                            FlatToggle(checked = ulangiJadwal, onCheckedChange = {
                                ulangiJadwal = it
                                banner = if (it) "Jadwal berulang diaktifkan" else "Jadwal berulang dimatikan"
                            })
                        }
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        hariMinggu.forEach { hari ->
                            val aktifHari = hariAktif.contains(hari)
                            Surface(
                                onClick = {
                                    if (aktifHari) hariAktif.remove(hari) else hariAktif.add(hari)
                                },
                                enabled = ulangiJadwal,
                                shape = RoundedCornerShape(999.dp),
                                color = if (aktifHari) palette.primaryContainer else palette.surface,
                                border = BorderStroke(1.dp, if (aktifHari) palette.primary else palette.border),
                                shadowElevation = 0.dp,
                                tonalElevation = 0.dp,
                            ) {
                                Text(
                                    text = hari,
                                    fontSize = 11.sp,
                                    fontWeight = if (aktifHari) FontWeight.Bold else FontWeight.Normal,
                                    color = if (aktifHari) palette.onPrimaryContainer else palette.textSecondary,
                                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                                )
                            }
                        }
                    }
                    Text(
                        text = if (ulangiJadwal && hariAktif.isNotEmpty()) {
                            "Berulang setiap ${hariAktif.joinToString(", ")} · 08.00–17.00"
                        } else if (ulangiJadwal) {
                            "Belum ada hari dipilih (state: kosong)."
                        } else {
                            "Status: mati — focus hanya aktif manual."
                        },
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                }

                // ===== Notification summary =====
                LabCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            LabSectionTitle("Notification Summary", "Notifikasi dibisukan dikirim bertahap")
                        }
                        Box(modifier = Modifier.semantics(mergeDescendants = true) {
                            role = Role.Switch
                            contentDescription = "Ringkasan notifikasi"
                        }) {
                            FlatToggle(checked = ringkasanOn, onCheckedChange = {
                                ringkasanOn = it
                                banner = if (it) "Ringkasan notifikasi aktif (Simulasi)" else "Ringkasan notifikasi dimatikan"
                            })
                        }
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        jamRingkasan.forEach { jam ->
                            val dipilih = jamRingkasanAktif == jam
                            Surface(
                                onClick = { jamRingkasanAktif = jam },
                                enabled = ringkasanOn,
                                shape = RoundedCornerShape(999.dp),
                                color = if (dipilih) palette.primaryContainer else palette.surface,
                                border = BorderStroke(1.dp, if (dipilih) palette.primary else palette.border),
                                shadowElevation = 0.dp,
                                tonalElevation = 0.dp,
                            ) {
                                Text(
                                    text = jam,
                                    fontSize = 11.sp,
                                    fontWeight = if (dipilih) FontWeight.Bold else FontWeight.Normal,
                                    color = if (dipilih) palette.onPrimaryContainer else palette.textSecondary,
                                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                                )
                            }
                        }
                    }
                    Text(
                        text = if (ringkasanOn) {
                            "Ringkasan dikirim pukul $jamRingkasanAktif berisi ${notifList.size} notifikasi (Simulasi)."
                        } else {
                            "Status: mati — ringkasan tidak dijadwalkan."
                        },
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                }

                // ===== Filter percakapan =====
                LabCard {
                    LabSectionTitle("Filter Percakapan", "Tentukan siapa yang tetap menembus focus")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterPercakapan.entries.forEach { opsi ->
                            val dipilih = filter == opsi
                            Surface(
                                onClick = {
                                    filter = opsi
                                    banner = "Filter percakapan: ${opsi.label}"
                                },
                                shape = RoundedCornerShape(999.dp),
                                color = if (dipilih) palette.primaryContainer else palette.surface,
                                border = BorderStroke(1.dp, if (dipilih) palette.primary else palette.border),
                                shadowElevation = 0.dp,
                                tonalElevation = 0.dp,
                            ) {
                                Text(
                                    text = opsi.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (dipilih) FontWeight.Bold else FontWeight.Normal,
                                    color = if (dipilih) palette.onPrimaryContainer else palette.textSecondary,
                                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                                )
                            }
                        }
                    }
                    Text(
                        text = when (filter) {
                            FilterPercakapan.SEMUA -> "Semua orang tetap bisa mengirim pesan (status: lolos semua)."
                            FilterPercakapan.PENTING -> "Hanya orang terpenting yang menembus focus (status: terbatas)."
                            FilterPercakapan.TIDAK_ADA -> "Seluruh percakapan dibisukan (status: hening penuh)."
                        },
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                }

                // ===== Banner =====
                AnimatedVisibility(visible = banner != null, enter = fadeIn(), exit = fadeOut()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = palette.info.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, palette.info),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = banner ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.info,
                            modifier = Modifier.padding(10.dp),
                        )
                    }
                }

                // ===== Manfaat =====
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Manfaat Pola Focus Modes:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                        Text("• Warna + ikon khas per mode mempercepat pengenalan tanpa membaca label", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Pratinjau beranda memperlihatkan dampak focus sebelum diaktifkan", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Status selalu disertai label teks (AKTIF, DIBISUKAN), bukan warna saja", fontSize = 12.sp, color = palette.textSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // ===== Dialog Focus Custom =====
        AnimatedVisibility(visible = dialogCustom, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(palette.textPrimary.copy(alpha = 0.45f))
                    .clickable { dialogCustom = false },
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .padding(24.dp)
                        .imePadding()
                        .pointerInput(Unit) { detectTapGestures { /* serap ketukan agar dialog tidak tertutup */ } },
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        LabSectionTitle("Buat Focus Custom", "Simulasi tanpa akses pengaturan sistem")
                        FlatTextField(
                            label = "Nama Focus",
                            value = namaCustom,
                            onValueChange = {
                                namaCustom = it
                                errorCustom = null
                            },
                            placeholder = "mis. Belajar",
                            error = errorCustom,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FlatSecondaryButton(
                                text = "Batal",
                                onClick = { dialogCustom = false },
                                modifier = Modifier.weight(1f),
                            )
                            FlatPrimaryButton(
                                text = "Buat Focus",
                                onClick = {
                                    val nama = namaCustom.trim()
                                    if (nama.isEmpty()) {
                                        errorCustom = "Nama focus wajib diisi"
                                    } else {
                                        val baru = FocusMode(
                                            id = "custom-${modes.size + 1}",
                                            nama = nama,
                                            desc = "Focus buatan sendiri",
                                            ikon = FocusIkon.CUSTOM,
                                            dibisukan = 0,
                                            appDiizinkan = listOf("Pesan"),
                                            orangDiizinkan = listOf("Keluarga"),
                                        )
                                        modes.add(baru)
                                        selectedId = baru.id
                                        dialogCustom = false
                                        banner = "Focus $nama dibuat (Simulasi)"
                                    }
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimulasiPill(text: String) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = palette.info.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, palette.info),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = palette.info,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun StatusPill(label: String, tint: Color) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = tint.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, tint),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = tint,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun ChipKecil(label: String, tint: Color) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, tint),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = palette.textPrimary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun FocusIcon(ikon: FocusIkon, tint: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val w = size.width
        val h = size.height
        val garis = Stroke(width = 2.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (ikon) {
            FocusIkon.KERJA -> {
                drawRoundRect(
                    color = tint,
                    topLeft = androidx.compose.ui.geometry.Offset(w * 0.14f, h * 0.32f),
                    size = androidx.compose.ui.geometry.Size(w * 0.72f, h * 0.5f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
                    style = garis,
                )
                val pegangan = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.38f, h * 0.32f)
                    lineTo(w * 0.38f, h * 0.2f)
                    lineTo(w * 0.62f, h * 0.2f)
                    lineTo(w * 0.62f, h * 0.32f)
                }
                drawPath(pegangan, color = tint, style = garis)
            }
            FocusIkon.TIDUR -> {
                val bulan = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.68f, h * 0.16f)
                    quadraticBezierTo(w * 0.3f, h * 0.22f, w * 0.3f, h * 0.62f)
                    quadraticBezierTo(w * 0.32f, h * 0.88f, w * 0.62f, h * 0.84f)
                    quadraticBezierTo(w * 0.44f, h * 0.72f, w * 0.44f, h * 0.5f)
                    quadraticBezierTo(w * 0.46f, h * 0.28f, w * 0.68f, h * 0.16f)
                    close()
                }
                drawPath(bulan, color = tint)
            }
            FocusIkon.PRIBADI -> {
                val hati = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.5f, h * 0.8f)
                    cubicTo(w * 0.08f, h * 0.5f, w * 0.26f, h * 0.12f, w * 0.5f, h * 0.32f)
                    cubicTo(w * 0.74f, h * 0.12f, w * 0.92f, h * 0.5f, w * 0.5f, h * 0.8f)
                    close()
                }
                drawPath(hati, color = tint)
            }
            FocusIkon.MENGEMUDI -> {
                drawRoundRect(
                    color = tint,
                    topLeft = androidx.compose.ui.geometry.Offset(w * 0.12f, h * 0.36f),
                    size = androidx.compose.ui.geometry.Size(w * 0.76f, h * 0.3f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f, 5f),
                    style = garis,
                )
                drawLine(tint, androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.28f), androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.28f), 2.2f, StrokeCap.Round)
                drawCircle(tint, radius = w * 0.07f, center = androidx.compose.ui.geometry.Offset(w * 0.3f, h * 0.72f))
                drawCircle(tint, radius = w * 0.07f, center = androidx.compose.ui.geometry.Offset(w * 0.7f, h * 0.72f))
            }
            FocusIkon.FITNESS -> {
                drawLine(tint, androidx.compose.ui.geometry.Offset(w * 0.28f, h * 0.5f), androidx.compose.ui.geometry.Offset(w * 0.72f, h * 0.5f), 2.4f, StrokeCap.Round)
                drawRoundRect(
                    color = tint,
                    topLeft = androidx.compose.ui.geometry.Offset(w * 0.1f, h * 0.32f),
                    size = androidx.compose.ui.geometry.Size(w * 0.12f, h * 0.36f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f),
                )
                drawRoundRect(
                    color = tint,
                    topLeft = androidx.compose.ui.geometry.Offset(w * 0.78f, h * 0.32f),
                    size = androidx.compose.ui.geometry.Size(w * 0.12f, h * 0.36f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f),
                )
            }
            FocusIkon.CUSTOM -> {
                drawLine(tint, androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.2f), androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.8f), 2.4f, StrokeCap.Round)
                drawLine(tint, androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.5f), androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.5f), 2.4f, StrokeCap.Round)
            }
        }
    }
}
