package com.example.uiapp.ui.recurrencepicker

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.HapticKind
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar

private enum class Freq(val label: String) { DAILY("Harian"), WEEKLY("Mingguan"), MONTHLY("Bulanan") }
private enum class MonthlyMode(val label: String) { DATE("Tanggal"), NTH("Hari ke-n") }
private enum class EndMode(val label: String) { NEVER("Tanpa akhir"), COUNT("Sejumlah"), DATE("Sampai tanggal") }

private val DayNames = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
private val WeekOrdinals = listOf("Pertama", "Kedua", "Ketiga", "Keempat", "Terakhir")

private fun digitsOnly(s: String): String = s.filter { it.isDigit() }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RecurrencePickerLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current

    var freq by remember { mutableStateOf(Freq.WEEKLY) }
    var interval by remember { mutableStateOf("1") }
    var selectedDays by remember { mutableStateOf(setOf(0, 2)) } // Sen & Rab
    var monthlyMode by remember { mutableStateOf(MonthlyMode.NTH) }
    var nthWeek by remember { mutableStateOf(0) }
    var nthDay by remember { mutableStateOf(0) }
    var endMode by remember { mutableStateOf(EndMode.COUNT) }
    var endCount by remember { mutableStateOf("10") }
    var endDate by remember { mutableStateOf("2027-12-31") }
    var applied by remember { mutableStateOf(false) }

    val intervalN = interval.toIntOrNull() ?: 0
    val countN = endCount.toIntOrNull() ?: 0
    val intervalError = if (intervalN < 1) "Interval minimal 1" else if (intervalN > 30) "Interval maksimal 30" else null
    val countError = if (endMode == EndMode.COUNT && countN < 1) "Jumlah pengulangan minimal 1" else null
    val dateError = if (endMode == EndMode.DATE && !Regex("\\d{4}-\\d{2}-\\d{2}").matches(endDate)) "Format tanggal: YYYY-MM-DD" else null
    val daysError = if (freq == Freq.WEEKLY && selectedDays.isEmpty()) "Pilih minimal satu hari" else null
    val valid = intervalError == null && countError == null && dateError == null && daysError == null

    val summary = remember(freq, intervalN, selectedDays, monthlyMode, nthWeek, nthDay, endMode, countN, endDate) {
        buildSummary(freq, intervalN, selectedDays, monthlyMode, nthWeek, nthDay, endMode, countN, endDate)
    }

    Column(Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = palette.background,
            topBar = { UiTopBar(title = "Recurrence Picker", subtitle = "Aturan pengulangan jadwal (RRULE)", onBack = onBack) },
        ) { inner ->
            Column(
                Modifier.fillMaxSize().padding(inner).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                LabIntroCard(title = "Pemilih Pengulangan", body = "Susun aturan harian / mingguan / bulanan dengan interval kustom dan akhir pengulangan. Ringkasan kalimat diperbarui langsung saat aturan berubah.")

                LabSectionTitle(title = "Frekuensi", subtitle = "Jenis pengulangan dasar")
                Segmented(
                    options = Freq.entries.map { it.label },
                    selected = freq.ordinal,
                    onSelect = { freq = Freq.entries[it]; applied = false; PerformHapticFeedback(HapticKind.SELECTION) },
                )

                if (freq == Freq.WEEKLY) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Hari aktif", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            DayNames.forEachIndexed { i, d ->
                                val on = i in selectedDays
                                Box(
                                    Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (on) palette.primary else palette.surface)
                                        .border(1.dp, if (on) palette.primary else palette.border, CircleShape)
                                        .clickable(role = Role.Checkbox) {
                                            selectedDays = if (on) selectedDays - i else selectedDays + i
                                            applied = false; PerformHapticFeedback(HapticKind.SELECTION)
                                        }
                                        .semantics { contentDescription = "Hari $d ${if (on) "aktif" else "nonaktif"}" },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(d, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (on) palette.onPrimary else palette.textSecondary)
                                }
                            }
                        }
                        if (daysError != null) Text(daysError, fontSize = 11.sp, color = palette.danger)
                    }
                }

                if (freq == Freq.MONTHLY) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Mode bulanan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                        Segmented(
                            options = MonthlyMode.entries.map { it.label },
                            selected = monthlyMode.ordinal,
                            onSelect = { monthlyMode = MonthlyMode.entries[it]; applied = false; PerformHapticFeedback(HapticKind.SELECTION) },
                        )
                        if (monthlyMode == MonthlyMode.NTH) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CompactPicker(
                                    label = "Minggu ke",
                                    value = WeekOrdinals[nthWeek],
                                    onPrev = { if (nthWeek > 0) { nthWeek--; applied = false } },
                                    onNext = { if (nthWeek < WeekOrdinals.lastIndex) { nthWeek++; applied = false } },
                                    modifier = Modifier.weight(1f),
                                )
                                CompactPicker(
                                    label = "Hari",
                                    value = DayNames[nthDay],
                                    onPrev = { if (nthDay > 0) { nthDay--; applied = false } },
                                    onNext = { if (nthDay < DayNames.lastIndex) { nthDay++; applied = false } },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        } else {
                            Text("Contoh: tanggal 15 setiap bulan (ditentukan oleh jadwal sumber).", fontSize = 11.sp, color = palette.textMuted)
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Interval kustom", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                    Stepper(value = intervalN, suffix = "x", onChange = { interval = it.coerceIn(1, 30).toString(); applied = false })
                    if (intervalError != null) Text(intervalError, fontSize = 11.sp, color = palette.danger)
                }

                LabSectionTitle(title = "Akhir Pengulangan", subtitle = "Batas terjadwal")
                Segmented(
                    options = EndMode.entries.map { it.label },
                    selected = endMode.ordinal,
                    onSelect = { endMode = EndMode.entries[it]; applied = false; PerformHapticFeedback(HapticKind.SELECTION) },
                )
                when (endMode) {
                    EndMode.COUNT -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Stepper(value = countN, suffix = "kali", onChange = { endCount = it.coerceIn(1, 99).toString(); applied = false })
                        if (countError != null) Text(countError, fontSize = 11.sp, color = palette.danger)
                    }
                    EndMode.DATE -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        FlatDateField(value = endDate, onChange = { endDate = digitsOnly(it).take(8); applied = false }, error = dateError)
                    }
                    EndMode.NEVER -> Text("Pengulangan berjalan tanpa batas sampai dihentikan.", fontSize = 11.sp, color = palette.textMuted)
                }

                LabSectionTitle(title = "Ringkasan", subtitle = "Diperbarui langsung dari aturan di atas")
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (valid) palette.primaryContainer else palette.danger.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, if (valid) palette.primary else palette.danger),
                    shadowElevation = 0.dp, tonalElevation = 0.dp,
                ) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (valid) {
                            Text(summary, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.onPrimaryContainer)
                        } else {
                            Text("Aturan belum valid", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.danger)
                            Text(listOfNotNull(intervalError, daysError, countError, dateError).joinToString(" · "), fontSize = 11.sp, color = palette.textSecondary)
                        }
                    }
                }

                if (applied) {
                    Surface(shape = RoundedCornerShape(12.dp), color = palette.success.copy(alpha = 0.12f), border = BorderStroke(1.dp, palette.success), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                        Text(
                            "Jadwal disimpan: $summary",
                            fontSize = 12.sp, color = palette.textPrimary,
                            modifier = Modifier.padding(12.dp),
                        )
                    }
                }
                FlatPrimaryButton(text = "Terapkan Jadwal", onClick = { applied = true; PerformHapticFeedback(HapticKind.SUCCESS) }, enabled = valid)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

private fun buildSummary(
    freq: Freq, interval: Int, days: Set<Int>, mode: MonthlyMode,
    nthWeek: Int, nthDay: Int, endMode: EndMode, count: Int, endDate: String,
): String {
    val base = when (freq) {
        Freq.DAILY -> if (interval == 1) "Setiap hari" else "Setiap $interval hari"
        Freq.WEEKLY -> {
            val d = days.sorted().joinToString(" & ") { DayNames[it] }
            if (interval == 1) "Setiap $d" else "Setiap $interval minggu: $d"
        }
        Freq.MONTHLY -> {
            val m = if (mode == MonthlyMode.NTH) "${WeekOrdinals[nthWeek]} ${DayNames[nthDay]}" else "tanggal 15"
            if (interval == 1) "Setiap bulan, $m" else "Setiap $interval bulan, $m"
        }
    }
    return when (endMode) {
        EndMode.NEVER -> "$base, tanpa akhir"
        EndMode.COUNT -> "$base, $count kali"
        EndMode.DATE -> "$base, sampai $endDate"
    }
}

@Composable
private fun Segmented(options: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    val palette = LocalAppPalette.current
    Surface(shape = RoundedCornerShape(12.dp), color = palette.surfaceMuted, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
        Row(Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            options.forEachIndexed { i, label ->
                val on = i == selected
                Surface(
                    onClick = { onSelect(i) },
                    shape = RoundedCornerShape(9.dp),
                    color = if (on) palette.primary else palette.surfaceMuted,
                    border = BorderStroke(1.dp, if (on) palette.primary else palette.border),
                    shadowElevation = 0.dp, tonalElevation = 0.dp,
                    modifier = Modifier.weight(1f).semantics { contentDescription = "$label ${if (on) "dipilih" else "tidak dipilih"}" },
                ) {
                    Text(
                        label, fontSize = 12.sp, fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
                        color = if (on) palette.onPrimary else palette.textSecondary,
                        textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(vertical = 9.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun Stepper(value: Int, suffix: String, onChange: (Int) -> Unit) {
    val palette = LocalAppPalette.current
    Row(
        Modifier.fillMaxWidth().heightIn(min = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            onClick = { onChange(value - 1) },
            shape = RoundedCornerShape(10.dp), color = palette.surface,
            border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp,
            modifier = Modifier.size(48.dp).semantics { contentDescription = "Kurangi nilai $suffix" },
        ) { Text("−", fontSize = 18.sp, color = palette.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 12.dp)) }
        Text(
            "$value $suffix", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary,
            modifier = Modifier.weight(1f).semantics { contentDescription = "Nilai saat ini $value $suffix" },
        )
        Surface(
            onClick = { onChange(value + 1) },
            shape = RoundedCornerShape(10.dp), color = palette.surface,
            border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp,
            modifier = Modifier.size(48.dp).semantics { contentDescription = "Tambah nilai $suffix" },
        ) { Text("+", fontSize = 18.sp, color = palette.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 12.dp)) }
    }
}

@Composable
private fun CompactPicker(label: String, value: String, onPrev: () -> Unit, onNext: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontSize = 11.sp, color = palette.textMuted)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                onClick = onPrev, shape = RoundedCornerShape(8.dp), color = palette.surface,
                border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp,
                modifier = Modifier.size(40.dp).semantics { contentDescription = "$label sebelumnya" },
            ) { Text("‹", fontSize = 16.sp, color = palette.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 9.dp)) }
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Surface(
                onClick = onNext, shape = RoundedCornerShape(8.dp), color = palette.surface,
                border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp,
                modifier = Modifier.size(40.dp).semantics { contentDescription = "$label berikutnya" },
            ) { Text("›", fontSize = 16.sp, color = palette.textPrimary, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 9.dp)) }
        }
    }
}

@Composable
private fun FlatDateField(value: String, onChange: (String) -> Unit, error: String?) {
    val palette = LocalAppPalette.current
    val shown = remember(value) {
        when (value.length) {
            in 0..4 -> value
            in 5..6 -> "${value.substring(0, 4)}-${value.substring(4)}"
            else -> "${value.substring(0, 4)}-${value.substring(4, 6)}-${value.substring(6)}"
        }
    }
    Column {
        Text("Tanggal akhir", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary, modifier = Modifier.padding(bottom = 7.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .border(1.dp, if (error != null) palette.danger else palette.border, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(if (value.isEmpty()) "2027-12-31" else shown, fontSize = 14.sp, color = if (value.isEmpty()) palette.textMuted else palette.textPrimary, fontWeight = FontWeight.Medium)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("2027-06-30", "2027-12-31", "2028-01-31").forEach { preset ->
                Surface(
                    onClick = { onChange(preset.filter { it.isDigit() }) },
                    shape = RoundedCornerShape(999.dp), color = palette.surfaceMuted,
                    border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp,
                    modifier = Modifier.semantics { contentDescription = "Preset tanggal $preset" },
                ) { Text(preset, fontSize = 11.sp, color = palette.textSecondary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) }
            }
        }
        if (error != null) Text(error, fontSize = 11.sp, color = palette.danger, modifier = Modifier.padding(top = 6.dp))
    }
}
