package com.example.uiapp.ui.appointmentslots

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.HapticKind
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private enum class SlotStatus(val label: String) {
    AVAILABLE("Tersedia"),
    NEARLY("Hampir penuh"),
    FULL("Terisi"),
}

private data class DemoDate(val id: String, val dayName: String, val dayNum: String, val month: String)

private data class Slot(val id: String, val time: String, val status: SlotStatus, val risky: Boolean = false)

private data class Member(val id: Int, val name: String, val initials: String, val heat: List<Float>)

private data class Zone(val id: String, val label: String, val offset: String)

private val Dates = listOf(
    DemoDate("sen", "Sen", "13", "Okt"),
    DemoDate("sel", "Sel", "14", "Okt"),
    DemoDate("rab", "Rab", "15", "Okt"),
    DemoDate("kam", "Kam", "16", "Okt"),
    DemoDate("jum", "Jum", "17", "Okt"),
    DemoDate("sab", "Sab", "18", "Okt"),
    DemoDate("min", "Min", "19", "Okt"),
)

private val Times = listOf("09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00", "17:00")

// Ketersediaan per tanggal: indeks = posisi jam pada Times.
private fun slotsFor(dateId: String): List<Slot> = when (dateId) {
    "sen" -> listOf(
        Slot("sen-0", "09:00", SlotStatus.AVAILABLE), Slot("sen-1", "10:00", SlotStatus.FULL),
        Slot("sen-2", "11:00", SlotStatus.AVAILABLE), Slot("sen-3", "13:00", SlotStatus.NEARLY),
        Slot("sen-4", "14:00", SlotStatus.FULL), Slot("sen-5", "15:00", SlotStatus.AVAILABLE),
        Slot("sen-6", "16:00", SlotStatus.AVAILABLE), Slot("sen-7", "17:00", SlotStatus.FULL),
    )
    "sel" -> listOf(
        Slot("sel-0", "09:00", SlotStatus.AVAILABLE, risky = true), Slot("sel-1", "10:00", SlotStatus.AVAILABLE),
        Slot("sel-2", "11:00", SlotStatus.NEARLY), Slot("sel-3", "13:00", SlotStatus.AVAILABLE),
        Slot("sel-4", "14:00", SlotStatus.FULL), Slot("sel-5", "15:00", SlotStatus.FULL),
        Slot("sel-6", "16:00", SlotStatus.AVAILABLE), Slot("sel-7", "17:00", SlotStatus.NEARLY),
    )
    "rab" -> List(8) { i -> Slot("rab-$i", Times[i], if (i % 3 == 1) SlotStatus.FULL else SlotStatus.AVAILABLE) }
    "kam" -> List(8) { i -> Slot("kam-$i", Times[i], if (i < 5) SlotStatus.FULL else SlotStatus.NEARLY) }
    else -> List(8) { i -> Slot("$dateId-$i", Times[i], if (i % 2 == 0) SlotStatus.AVAILABLE else SlotStatus.NEARLY) }
}

private val Members = listOf(
    Member(1, "Ayu Prameswari", "AP", listOf(1f, 0.8f, 0.5f, 0.2f, 0f, 0.6f, 0.9f, 0.4f)),
    Member(2, "Bagas Nugroho", "BN", listOf(0.3f, 1f, 1f, 0.7f, 0.5f, 0f, 0.2f, 0.8f)),
    Member(3, "Citra Wulandari", "CW", listOf(0f, 0.4f, 0.9f, 1f, 1f, 0.8f, 0.5f, 0.1f)),
)

private val Zones = listOf(
    Zone("wib", "WIB (Jakarta)", "UTC+7"),
    Zone("wita", "WITA (Makassar)", "UTC+8"),
    Zone("wit", "WIT (Jayapura)", "UTC+9"),
)

private enum class BookingPhase { IDLE, LOADING, CONFLICT, SUCCESS }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppointmentSlotsLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    var dateId by remember { mutableStateOf("sen") }
    var zoneId by remember { mutableStateOf("wib") }
    var slotId by remember { mutableStateOf<String?>(null) }
    val selectedMembers = remember { mutableStateOf(setOf(1)) }
    var sheetOpen by remember { mutableStateOf(false) }
    var phase by remember { mutableStateOf(BookingPhase.IDLE) }

    val slots = remember(dateId) { slotsFor(dateId) }
    val slot = slots.firstOrNull { it.id == slotId && it.status != SlotStatus.FULL }

    BackHandler(enabled = true) {
        if (sheetOpen) sheetOpen = false else onBack()
    }

    fun tryBook() {
        phase = BookingPhase.LOADING
        PerformHapticFeedback(HapticKind.MEDIUM)
    }

    LaunchedEffect(phase) {
        if (phase != BookingPhase.LOADING) return@LaunchedEffect
        delay(1000)
        val risky = slot?.risky == true
        phase = if (risky) BookingPhase.CONFLICT else BookingPhase.SUCCESS
        PerformHapticFeedback(if (risky) HapticKind.WARNING else HapticKind.SUCCESS)
        if (!risky) sheetOpen = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            UiTopBar(title = "Appointment Slots", subtitle = "Pemilih jadwal, zona waktu & ketersediaan", onBack = onBack)

            LabIntroCard(
                title = "Data mock lokal",
                body = "Ketersediaan, heatstrip tim, dan konflik jadwal seluruhnya simulasi. " +
                    "Slot bertanda \u201champir penuh\u201d dan salah satu slot Selasa mendemonstrasikan konflik saat konfirmasi.",
            )

            LabSectionTitle("Pilih tanggal", "Gulir ke samping untuk 7 hari ke depan")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Dates, key = { it.id }) { d ->
                    val active = d.id == dateId
                    Column(
                        modifier = Modifier
                            .background(
                                if (active) palette.primary else palette.surface,
                                RoundedCornerShape(12.dp),
                            )
                            .border(1.dp, if (active) palette.primary else palette.border, RoundedCornerShape(12.dp))
                            .clickable {
                                PerformHapticFeedback(HapticKind.SELECTION)
                                dateId = d.id
                                slotId = null
                                phase = BookingPhase.IDLE
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(d.dayName, fontSize = 10.sp, color = if (active) palette.onPrimary.copy(alpha = 0.8f) else palette.textMuted)
                        Text(d.dayNum, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (active) palette.onPrimary else palette.textPrimary)
                        Text(d.month, fontSize = 10.sp, color = if (active) palette.onPrimary.copy(alpha = 0.8f) else palette.textMuted)
                    }
                }
            }

            LabSectionTitle("Zona waktu", "Jam ditampilkan mengikuti zona terpilih")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Zones.forEach { z ->
                    val active = z.id == zoneId
                    Box(
                        modifier = Modifier
                            .background(
                                if (active) palette.primaryContainer else palette.surface,
                                RoundedCornerShape(999.dp),
                            )
                            .border(1.dp, if (active) palette.primary else palette.border, RoundedCornerShape(999.dp))
                            .clickable {
                                PerformHapticFeedback(HapticKind.SELECTION)
                                zoneId = z.id
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Text(
                            "${z.label} · ${z.offset}",
                            fontSize = 11.sp,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                            color = if (active) palette.onPrimaryContainer else palette.textSecondary,
                        )
                    }
                }
            }

            LabSectionTitle("Slot jam", "Ketuk slot hijau untuk memilih; slot terisi nonaktif")
            LabCard {
                val rows = slots.chunked(4)
                rows.forEach { rowSlots ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowSlots.forEach { s ->
                            val active = s.id == slotId
                            val enabled = s.status != SlotStatus.FULL
                            val (bg, fg, border) = when (s.status) {
                                SlotStatus.AVAILABLE -> Triple(palette.success.copy(alpha = 0.12f), palette.success, palette.success.copy(alpha = 0.5f))
                                SlotStatus.NEARLY -> Triple(palette.warning.copy(alpha = 0.12f), palette.warning, palette.warning.copy(alpha = 0.5f))
                                SlotStatus.FULL -> Triple(palette.surfaceMuted, palette.textMuted, palette.border)
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if ( active && enabled) palette.primary else bg, RoundedCornerShape(10.dp))
                                    .border(1.dp, if (active && enabled) palette.primary else border, RoundedCornerShape(10.dp))
                                    .clickable(enabled = enabled) {
                                        PerformHapticFeedback(HapticKind.SELECTION)
                                        slotId = s.id
                                        phase = BookingPhase.IDLE
                                    }
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    s.time,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active && enabled) palette.onPrimary else fg,
                                )
                                Text(
                                    if (active && enabled) "Dipilih" else s.status.label,
                                    fontSize = 9.sp,
                                    color = if (active && enabled) palette.onPrimary.copy(alpha = 0.85f) else fg,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
                Text(
                    "Status ditulis sebagai teks, bukan hanya warna.",
                    fontSize = 10.sp,
                    color = palette.textMuted,
                )
            }

            LabSectionTitle("Ketersediaan tim", "Heatstrip 8 blok jam; gelap = semakin sibuk")
            LabCard {
                Members.forEach { m ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(vertical = 4.dp),
                    ) {
                        val chosen = m.id in selectedMembers.value
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(
                                    if (chosen) palette.primary else palette.surfaceMuted,
                                    CircleShape,
                                )
                                .border(1.dp, if (chosen) palette.primary else palette.border, CircleShape)
                                .clickable {
                                    PerformHapticFeedback(HapticKind.SELECTION)
                                    selectedMembers.value =
                                        if (chosen) selectedMembers.value - m.id
                                        else selectedMembers.value + m.id
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(m.initials, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (chosen) palette.onPrimary else palette.textSecondary)
                        }
                        Column(Modifier.weight(1f)) {
                            Text(m.name, fontSize = 12.sp, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                m.heat.forEachIndexed { i, v ->
                                    Box(
                                        Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                            .background(heatColor(v), RoundedCornerShape(2.dp)),
                                    )
                                }
                            }
                        }
                        Text(
                            if (m.id in selectedMembers.value) "Diundang" else "—",
                            fontSize = 9.sp,
                            color = palette.textMuted,
                        )
                    }
                }
                Text(
                    "Ketuk inisial untuk mengundang/membatalkan peserta.",
                    fontSize = 10.sp,
                    color = palette.textMuted,
                )
            }

            when (phase) {
                BookingPhase.CONFLICT -> StatusPill("Konflik: slot baru saja terisi peserta lain", palette.danger)
                BookingPhase.SUCCESS -> StatusPill("Sukses: undangan jadwal terkirim (simulasi)", palette.success)
                BookingPhase.IDLE, BookingPhase.LOADING -> {}
            }
        }

        // Bar aksi bawah tetap terlihat; nonaktif sampai pilihan lengkap.
        val zone = Zones.first { it.id == zoneId }
        val canBook = slot != null && selectedMembers.value.isNotEmpty() && phase != BookingPhase.LOADING
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(palette.surface)
                .border(1.dp, palette.border)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        slot?.let { "${it.time} ${zone.label}" } ?: "Belum memilih slot",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        if (selectedMembers.value.isEmpty()) "Pilih minimal satu peserta" else "${selectedMembers.value.size} peserta diundang",
                        fontSize = 11.sp,
                        color = palette.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (phase == BookingPhase.LOADING) {
                    CircularProgressIndicator(strokeWidth = 2.dp, color = palette.primary, modifier = Modifier.size(20.dp))
                    Text("Memesan…", fontSize = 11.sp, color = palette.textSecondary)
                } else {
                    Box(
                        modifier = Modifier
                            .background(
                                if (canBook) palette.primary else palette.surfaceMuted,
                                RoundedCornerShape(10.dp),
                            )
                            .border(
                                1.dp,
                                if (canBook) palette.primary else palette.border,
                                RoundedCornerShape(10.dp),
                            )
                            .clickable(enabled = canBook) { sheetOpen = true }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    ) {
                        Text(
                            "Pesan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (canBook) palette.onPrimary else palette.textMuted,
                        )
                    }
                }
            }
        }

        if (sheetOpen && slot != null) {
            BackHandler(enabled = true) { sheetOpen = false }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .navigationBarsPadding()
                    .padding(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Ringkasan pemesanan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        Text(
                            "Tutup",
                            fontSize = 12.sp,
                            color = palette.primary,
                            modifier = Modifier
                                .clickable { sheetOpen = false }
                                .padding(4.dp),
                        )
                    }
                    SummaryRow("Tanggal", Dates.first { it.id == dateId }.let { "${it.dayName}, ${it.dayNum} ${it.month} 2026" })
                    SummaryRow("Jam", "${slot.time} ${zone.label}")
                    SummaryRow("Peserta", Members.filter { it.id in selectedMembers.value }.joinToString { it.name })
                    SummaryRow("Status", if (slot.status == SlotStatus.NEARLY) "Hampir penuh — segera konfirmasi" else slot.status.label)
                    if (phase == BookingPhase.CONFLICT) {
                        StatusPill("Slot bentrok — silakan pilih jam lain", palette.danger)
                        Text(
                            "Coba slot berikutnya pada tanggal yang sama; ketersediaan tim diperbarui otomatis (simulasi).",
                            fontSize = 11.sp,
                            color = palette.textSecondary,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (phase == BookingPhase.CONFLICT) palette.surfaceMuted else palette.primary,
                                RoundedCornerShape(12.dp),
                            )
                            .border(
                                1.dp,
                                if (phase == BookingPhase.CONFLICT) palette.border else palette.primary,
                                RoundedCornerShape(12.dp),
                            )
                            .clickable(enabled = phase != BookingPhase.CONFLICT) { tryBook() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (phase == BookingPhase.CONFLICT) "Konfirmasi dinonaktifkan (bentrok)" else "Konfirmasi pemesanan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (phase == BookingPhase.CONFLICT) palette.textMuted else palette.onPrimary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, fontSize = 11.sp, color = palette.textMuted)
        Text(
            value,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun heatColor(v: Float): Color {
    val palette = LocalAppPalette.current
    return palette.danger.copy(alpha = (0.10f + v * 0.75f).coerceIn(0.05f, 0.85f))
}
