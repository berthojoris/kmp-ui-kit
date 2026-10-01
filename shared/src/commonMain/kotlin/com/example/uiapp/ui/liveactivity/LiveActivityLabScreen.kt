package com.example.uiapp.ui.liveactivity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private enum class LaFase(val label: String) {
    SIAP("Siap Dimulai"),
    BERJALAN("Berjalan"),
    DIJEDA("Dijeda"),
    SELESAI("Selesai"),
    DIBATALKAN("Dibatalkan"),
}

private enum class LaPresentasi(val label: String, val keterangan: String) {
    COMPACT("Compact", "Pill ringkas di atas aplikasi"),
    MINIMAL("Minimal", "Ikon kecil di tepi layar"),
    EXPANDED("Expanded", "Kartu lengkap dengan tahapan"),
}

private enum class LaRiwayatStatus(val label: String) {
    SELESAI("Selesai"),
    DIBATALKAN("Dibatalkan"),
}

private enum class LaAksi { SIAP, MEMUAT, SUKSES }

private data class LaTahap(val id: String, val judul: String, val detail: String)

private data class LaRiwayat(val id: String, val judul: String, val waktu: String, val status: LaRiwayatStatus)

private val LA_TAHAP = listOf(
    LaTahap("t1", "Pesanan diterima", "Restoran menerima pesanan #A-1042"),
    LaTahap("t2", "Makanan disiapkan", "Nasi goreng & es jeruk sedang dimasak"),
    LaTahap("t3", "Kurir menjemput", "Kurir Rudi menuju restoran"),
    LaTahap("t4", "Dalam perjalanan", "Menuju Jl. Melati No. 8"),
    LaTahap("t5", "Sampai di tujuan", "Pesanan diserahkan ke penerima"),
)

private val LA_RIWAYAT_AWAL = listOf(
    LaRiwayat("h1", "Pesanan #A-1013 — Soto Betawi", "Kemarin, 19:20", LaRiwayatStatus.SELESAI),
    LaRiwayat("h2", "Perjalanan #P-2201 — Bandung → Cimahi", "Senin, 08:45", LaRiwayatStatus.DIBATALKAN),
)

private fun tahapDariProgres(progres: Float): Int =
    ((progres / 100f) * LA_TAHAP.size).toInt().coerceIn(0, LA_TAHAP.lastIndex)

private fun etaMenit(progres: Float): Int = (((100f - progres) / 100f) * 24f).roundToInt()

private fun statusTahap(indeksTahap: Int, indeks: Int, fase: LaFase): String = when {
    fase == LaFase.SELESAI -> "Selesai"
    indeks < indeksTahap -> "Selesai"
    indeks == indeksTahap -> if (fase == LaFase.DIJEDA) "Dijeda" else "Berlangsung"
    else -> "Menunggu"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LiveActivityLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var fase by remember { mutableStateOf(LaFase.SIAP) }
    var progres by remember { mutableFloatStateOf(0f) }
    var presentasi by remember { mutableStateOf(LaPresentasi.COMPACT) }
    val riwayat = remember { mutableStateListOf<LaRiwayat>().apply { addAll(LA_RIWAYAT_AWAL) } }
    var aksiKurir by remember { mutableStateOf(LaAksi.SIAP) }
    var kunciAksi by remember { mutableIntStateOf(0) }
    var pesan by remember { mutableStateOf("Tekan \"Mulai Pesanan\" untuk memulai — Simulasi") }

    val indeksTahap = remember(progres, fase) { tahapDariProgres(if (fase == LaFase.SELESAI) 100f else progres) }
    val eta = remember(progres, fase) { etaMenit(if (fase == LaFase.SELESAI) 100f else progres) }

    LaunchedEffect(fase) {
        if (fase == LaFase.BERJALAN) {
            while (progres < 100f) {
                delay(900)
                progres = (progres + 4f).coerceAtMost(100f)
                if (progres >= 100f) {
                    fase = LaFase.SELESAI
                    riwayat.add(
                        0,
                        LaRiwayat(
                            id = "h${riwayat.size + 1}-${riwayat.size + 3}",
                            judul = "Pesanan #A-1042 — Nasi Goreng Pak Budi",
                            waktu = "Baru saja",
                            status = LaRiwayatStatus.SELESAI,
                        ),
                    )
                    pesan = "Aktivitas selesai — tersimpan di riwayat (Simulasi)"
                }
            }
        }
    }

    LaunchedEffect(kunciAksi) {
        if (kunciAksi > 0) {
            aksiKurir = LaAksi.MEMUAT
            delay(700)
            aksiKurir = LaAksi.SUKSES
            pesan = "Panggilan suara ke kurir disimulasikan (tanpa telepon sungguhan)"
        }
    }

    fun tutupAktivitas(status: LaRiwayatStatus) {
        fase = if (status == LaRiwayatStatus.SELESAI) LaFase.SELESAI else LaFase.DIBATALKAN
        progres = if (status == LaRiwayatStatus.SELESAI) 100f else progres
        riwayat.add(
            0,
            LaRiwayat(
                id = "h${riwayat.size + 1}-manual",
                judul = "Pesanan #A-1042 — Nasi Goreng Pak Budi",
                waktu = "Baru saja",
                status = status,
            ),
        )
        pesan = if (status == LaRiwayatStatus.SELESAI) {
            "Aktivitas diselesaikan manual"
        } else {
            "Aktivitas dibatalkan — status gagal"
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Live Activity Lifecycle",
                subtitle = "Compact, minimal, expanded — Simulasi",
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
                    Text("Live Activity Lifecycle", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    Text(
                        "Siklus hidup satu aktivitas pesanan: dimulai → progres real-time → selesai, dengan tiga presentasi dan kartu layar kunci. Tidak ada integrasi ActivityKit — seluruhnya Simulasi.",
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
                        Text("Kendali Siklus Hidup", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                        LaPilStatus(fase = fase)
                    }

                    LaProgresUtama(progres = progres, indeksTahap = indeksTahap)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Tahap ${indeksTahap + 1}/${LA_TAHAP.size}", fontSize = 11.sp, color = palette.textSecondary, modifier = Modifier.weight(1f))
                        Text(
                            if (fase == LaFase.SELESAI) "Tiba" else "±$eta menit lagi",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.primary,
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val bisaMulai = fase == LaFase.SIAP || fase == LaFase.SELESAI || fase == LaFase.DIBATALKAN
                        LaTombol(
                            label = if (bisaMulai && fase != LaFase.SIAP) "Mulai Ulang" else "Mulai Pesanan",
                            enabled = bisaMulai,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                progres = 0f
                                fase = LaFase.BERJALAN
                                aksiKurir = LaAksi.SIAP
                                pesan = "Aktivitas dimulai — progres diperbarui otomatis"
                            },
                        )
                        LaTombol(
                            label = if (fase == LaFase.DIJEDA) "Lanjut" else "Jeda",
                            enabled = fase == LaFase.BERJALAN || fase == LaFase.DIJEDA,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                fase = if (fase == LaFase.BERJALAN) LaFase.DIJEDA else LaFase.BERJALAN
                                pesan = if (fase == LaFase.DIJEDA) "Aktivitas dijeda" else "Aktivitas dilanjutkan"
                            },
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val bisaProgres = fase == LaFase.BERJALAN || fase == LaFase.DIJEDA
                        LaTombol(
                            label = "+10% Progres",
                            enabled = bisaProgres,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                progres = (progres + 10f).coerceAtMost(100f)
                                if (progres >= 100f) {
                                    tutupAktivitas(LaRiwayatStatus.SELESAI)
                                } else {
                                    pesan = "Progres manual: ${progres.roundToInt()}%"
                                }
                            },
                        )
                        LaTombol(
                            label = "Selesaikan",
                            enabled = bisaProgres,
                            modifier = Modifier.weight(1f),
                            onClick = { tutupAktivitas(LaRiwayatStatus.SELESAI) },
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val bisaBatal = fase == LaFase.BERJALAN || fase == LaFase.DIJEDA
                        LaTombol(
                            label = "Batalkan Pesanan",
                            enabled = bisaBatal,
                            bahaya = true,
                            modifier = Modifier.weight(1f),
                            onClick = { tutupAktivitas(LaRiwayatStatus.DIBATALKAN) },
                        )
                        LaTombol(
                            label = "Reset",
                            enabled = fase != LaFase.SIAP,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                fase = LaFase.SIAP
                                progres = 0f
                                aksiKurir = LaAksi.SIAP
                                pesan = "Siklus direset ke state awal"
                            },
                        )
                    }

                    Text(pesan, fontSize = 11.sp, color = palette.textSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
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
                    Text("Tiga Presentasi", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        LaPresentasi.entries.forEach { item ->
                            val aktif = item == presentasi
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (aktif) palette.primary else palette.surfaceMuted)
                                    .border(BorderStroke(1.dp, if (aktif) palette.primary else palette.border), RoundedCornerShape(10.dp))
                                    .clickable { presentasi = item }
                                    .padding(vertical = 8.dp, horizontal = 4.dp)
                                    .semantics {
                                        role = Role.Button
                                        contentDescription = "Presentasi ${item.label}"
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    item.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (aktif) palette.onPrimary else palette.textPrimary,
                                )
                                Text(
                                    item.keterangan,
                                    fontSize = 9.sp,
                                    color = if (aktif) palette.onPrimary.copy(alpha = 0.8f) else palette.textMuted,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }

                    if (fase == LaFase.SIAP) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.surfaceMuted)
                                .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text("Tidak ada aktivitas berjalan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                            Text("State kosong — mulai pesanan untuk melihat presentasi", fontSize = 11.sp, color = palette.textMuted)
                        }
                    } else {
                        when (presentasi) {
                            LaPresentasi.MINIMAL -> LaPratinjauMinimal(indeksTahap = indeksTahap, progres = progres)
                            LaPresentasi.COMPACT -> LaPratinjauCompact(
                                indeksTahap = indeksTahap,
                                progres = progres,
                                eta = eta,
                                fase = fase,
                            )
                            LaPresentasi.EXPANDED -> LaPratinjauExpanded(
                                indeksTahap = indeksTahap,
                                progres = progres,
                                eta = eta,
                                fase = fase,
                            )
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
                    Text("Kartu Layar Kunci", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(listOf(palette.primaryContainer, palette.surfaceMuted)))
                            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Pesanan #A-1042", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                                Text("Nasi Goreng Pak Budi — Simulasi", fontSize = 11.sp, color = palette.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            LaPilStatus(fase = fase)
                        }

                        LaProgresUtama(progres = progres, indeksTahap = indeksTahap)

                        if (fase == LaFase.SIAP) {
                            Text("Aktivitas belum dimulai", fontSize = 11.sp, color = palette.textMuted)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                LA_TAHAP.forEachIndexed { idx, tahap ->
                                    val status = statusTahap(indeksTahap, idx, fase)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when (status) {
                                                        "Selesai" -> palette.success.copy(alpha = 0.20f)
                                                        "Berlangsung" -> palette.primary.copy(alpha = 0.20f)
                                                        "Dijeda" -> palette.warning.copy(alpha = 0.20f)
                                                        else -> palette.surfaceMuted
                                                    },
                                                )
                                                .border(
                                                    1.dp,
                                                    when (status) {
                                                        "Selesai" -> palette.success
                                                        "Berlangsung" -> palette.primary
                                                        "Dijeda" -> palette.warning
                                                        else -> palette.border
                                                    },
                                                    CircleShape,
                                                ),
                                            contentAlignment = Alignment.Center,
                                            ) {
                                                Text(
                                                    (idx + 1).toString(),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = palette.textPrimary,
                                                )
                                            }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(tahap.judul, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                                            Text(tahap.detail, fontSize = 10.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                        Text(status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.textSecondary)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    if (fase == LaFase.SELESAI) "Estimasi tiba: sudah tiba" else "Estimasi tiba: ±$eta menit",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.primary,
                                )
                                if (fase == LaFase.SELESAI) {
                                    Text("Tiba 12:40", fontSize = 11.sp, color = palette.textSecondary)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                val bisaAksi = fase == LaFase.BERJALAN || fase == LaFase.DIJEDA
                                LaTombol(
                                    label = when (aksiKurir) {
                                        LaAksi.MEMUAT -> "Menghubungi…"
                                        LaAksi.SUKSES -> "Kurir Dihubungi"
                                        else -> "Hubungi Kurir (Simulasi)"
                                    },
                                    enabled = bisaAksi && aksiKurir != LaAksi.MEMUAT,
                                    modifier = Modifier.weight(1f),
                                    onClick = { kunciAksi += 1 },
                                )
                                LaTombol(
                                    label = "Batalkan",
                                    enabled = bisaAksi,
                                    bahaya = true,
                                    modifier = Modifier.weight(1f),
                                    onClick = { tutupAktivitas(LaRiwayatStatus.DIBATALKAN) },
                                )
                            }

                            when (aksiKurir) {
                                LaAksi.MEMUAT -> Text("Membuka kanal suara tiruan…", fontSize = 10.sp, color = palette.info)
                                LaAksi.SUKSES -> Text("Kanal suara tiruan tersambung — Simulasi, tanpa telepon nyata", fontSize = 10.sp, color = palette.success)
                                else -> Text("Aksi kartu kunci berjalan di dalam aplikasi", fontSize = 10.sp, color = palette.textMuted)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Riwayat Aktivitas", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(palette.surfaceMuted)
                                .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                                .clickable(enabled = riwayat.isNotEmpty()) { riwayat.clear() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .semantics {
                                    role = Role.Button
                                    contentDescription = "Kosongkan riwayat aktivitas"
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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.surfaceMuted)
                                .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text("Belum ada riwayat aktivitas", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                            Text("Aktivitas yang selesai atau dibatalkan akan muncul di sini", fontSize = 11.sp, color = palette.textMuted)
                        }
                    } else {
                        riwayat.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(palette.surfaceMuted)
                                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.judul, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(item.waktu, fontSize = 10.sp, color = palette.textMuted)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (item.status == LaRiwayatStatus.SELESAI) palette.success.copy(alpha = 0.12f) else palette.danger.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, if (item.status == LaRiwayatStatus.SELESAI) palette.success else palette.danger),
                                    shadowElevation = 0.dp,
                                    tonalElevation = 0.dp,
                                ) {
                                    Text(
                                        item.status.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.status == LaRiwayatStatus.SELESAI) palette.success else palette.danger,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    )
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
                    Text("Catatan Live Activity Lifecycle:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                    Text("• Melengkapi pratinjau Dynamic Island statis di lab system_platform", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Tiga presentasi (compact, minimal, expanded) memakai data yang sama", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Kartu layar kunci memuat tahapan, estimasi tiba, dan tombol aksi", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Tanpa ActivityKit / push notification sungguhan — seluruhnya Simulasi", fontSize = 12.sp, color = palette.textSecondary)
                }
            }
        }
    }
}

@Composable
private fun LaPilStatus(fase: LaFase) {
    val palette = LocalAppPalette.current
    val (warna, latar) = when (fase) {
        LaFase.SIAP -> palette.textSecondary to palette.surfaceMuted
        LaFase.BERJALAN -> palette.primary to palette.primaryContainer
        LaFase.DIJEDA -> palette.warning to palette.warning.copy(alpha = 0.15f)
        LaFase.SELESAI -> palette.success to palette.success.copy(alpha = 0.15f)
        LaFase.DIBATALKAN -> palette.danger to palette.danger.copy(alpha = 0.15f)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = latar,
        border = BorderStroke(1.dp, warna),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Text(
            "Status: ${fase.label}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = warna,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun LaProgresUtama(progres: Float, indeksTahap: Int) {
    val palette = LocalAppPalette.current

    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
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
                    .fillMaxWidth(fraction = (progres / 100f).coerceIn(0f, 1f))
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(palette.primary),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            LA_TAHAP.forEachIndexed { idx, tahap ->
                Text(
                    if (idx == indeksTahap) "▶ ${tahap.judul}" else "",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
        }
    }
}

@Composable
private fun LaTombol(
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    bahaya: Boolean = false,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val latar = when {
        !enabled -> palette.surfaceMuted
        bahaya -> palette.danger.copy(alpha = 0.12f)
        else -> palette.primary
    }
    val teks = when {
        !enabled -> palette.textMuted
        bahaya -> palette.danger
        else -> palette.onPrimary
    }
    val borderWarna = when {
        !enabled -> palette.border
        bahaya -> palette.danger
        else -> palette.primary
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = latar,
        contentColor = teks,
        border = BorderStroke(1.dp, borderWarna),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = modifier.semantics {
            role = Role.Button
            contentDescription = label + if (enabled) "" else ", tidak tersedia"
        },
    ) {
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = teks,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 6.dp),
        )
    }
}

@Composable
private fun LaPratinjauMinimal(indeksTahap: Int, progres: Float) {
    val palette = LocalAppPalette.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(palette.textPrimary)
                .border(1.dp, palette.border, RoundedCornerShape(18.dp))
                .semantics { contentDescription = "Presentasi minimal Live Activity" },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(38.dp)) {
                val tebal = 5.dp.toPx()
                drawArc(
                    color = palette.surface.copy(alpha = 0.35f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = tebal),
                )
                drawArc(
                    color = palette.primary,
                    startAngle = -90f,
                    sweepAngle = 360f * (progres / 100f).coerceIn(0f, 1f),
                    useCenter = false,
                    style = Stroke(width = tebal),
                )
            }
            Text(
                "${indeksTahap + 1}/${LA_TAHAP.size}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = palette.surface,
            )
        }
    }
}

@Composable
private fun LaPratinjauCompact(indeksTahap: Int, progres: Float, eta: Int, fase: LaFase) {
    val palette = LocalAppPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .semantics { contentDescription = "Presentasi compact Live Activity" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(palette.primary.copy(alpha = 0.15f))
                .border(1.dp, palette.primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("${indeksTahap + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.primary)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Pesanan #A-1042", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
            Text(
                LA_TAHAP[indeksTahap].judul + " — " + if (fase == LaFase.SELESAI) "tiba" else "±$eta mnt",
                fontSize = 10.sp,
                color = palette.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(palette.surface)
                .border(1.dp, palette.border, RoundedCornerShape(3.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (progres / 100f).coerceIn(0.02f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(palette.primary),
            )
        }
    }
}

@Composable
private fun LaPratinjauExpanded(indeksTahap: Int, progres: Float, eta: Int, fase: LaFase) {
    val palette = LocalAppPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .semantics { contentDescription = "Presentasi expanded Live Activity" },
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Pesanan #A-1042", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
            Text(
                if (fase == LaFase.SELESAI) "Tiba 12:40" else "±$eta menit",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.primary,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(palette.surface)
                .border(1.dp, palette.border, RoundedCornerShape(4.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (progres / 100f).coerceIn(0f, 1f))
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(palette.primary),
            )
        }

        LA_TAHAP.forEachIndexed { idx, tahap ->
            val status = statusTahap(indeksTahap, idx, fase)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            when (status) {
                                "Selesai" -> palette.success
                                "Berlangsung" -> palette.primary
                                "Dijeda" -> palette.warning
                                else -> palette.border
                            },
                        ),
                )
                Text(
                    tahap.judul,
                    fontSize = 11.sp,
                    fontWeight = if (idx == indeksTahap) FontWeight.Bold else FontWeight.Normal,
                    color = if (idx <= indeksTahap) palette.textPrimary else palette.textMuted,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(status, fontSize = 10.sp, color = palette.textSecondary)
            }
        }
    }
}
