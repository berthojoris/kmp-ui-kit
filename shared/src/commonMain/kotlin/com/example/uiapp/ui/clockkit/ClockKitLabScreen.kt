package com.example.uiapp.ui.clockkit

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatToggle
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

// ---------------------------------------------------------------------------
// Data mock
// ---------------------------------------------------------------------------

private data class WorldCity(
    val id: String,
    val city: String,
    val zoneLabel: String,
    val baseSeconds: Int,
    val dayOffsetLabel: String,
)

private val homeCities = listOf(
    WorldCity("jkt", "Jakarta", "WIB", 14 * 3600 + 35 * 60, "Hari ini"),
    WorldCity("tyo", "Tokyo", "JST", 16 * 3600 + 35 * 60, "Hari ini"),
    WorldCity("lon", "London", "GMT", 8 * 3600 + 35 * 60, "Hari ini"),
    WorldCity("nyc", "New York", "EST", 3 * 3600 + 35 * 60, "Hari ini"),
)

private val extraCityPool = listOf(
    WorldCity("syd", "Sydney", "AEDT", 17 * 3600 + 35 * 60, "Besok"),
    WorldCity("par", "Paris", "CET", 9 * 3600 + 35 * 60, "Hari ini"),
)

private data class AlarmState(
    val id: String,
    val time: String,
    val label: String,
    val repeatLabel: String,
    val enabled: Boolean,
)

private val alarmSeed = listOf(
    AlarmState("a1", "05:30", "Subuh", "Sen–Jum", true),
    AlarmState("a2", "07:00", "Olahraga Pagi", "Sen, Rab, Jum", true),
    AlarmState("a3", "13:00", "Istirahat Siang", "Setiap hari", false),
    AlarmState("a4", "21:30", "Waktunya Tidur", "Setiap hari", true),
)

private val alarmPool = listOf(
    AlarmState("a5", "04:45", "Tahajud", "Sab–Min", false),
    AlarmState("a6", "17:45", "Persiapan Buka", "Selama Ramadan", false),
)

private data class LapEntry(val id: Int, val totalMs: Long, val splitMs: Long)

private enum class AlarmSyncState { IDLE, LOADING, SUCCESS, ERROR }

// ---------------------------------------------------------------------------
// Format helper (multiplatform, tanpa String.format)
// ---------------------------------------------------------------------------

private fun pad2(value: Long): String = if (value < 10) "0$value" else "$value"

private fun formatClockTime(totalSeconds: Int): String {
    val secOfDay = ((totalSeconds % 86400) + 86400) % 86400
    val h = (secOfDay / 3600).toLong()
    val m = ((secOfDay % 3600) / 60).toLong()
    val s = (secOfDay % 60).toLong()
    return "${pad2(h)}:${pad2(m)}:${pad2(s)}"
}

private fun formatStopwatch(ms: Long): String {
    val centis = (ms % 1000) / 10
    val totalSec = ms / 1000
    val s = totalSec % 60
    val m = (totalSec / 60) % 60
    val h = totalSec / 3600
    return if (h > 0) {
        "${pad2(h)}:${pad2(m)}:${pad2(s)},${pad2(centis)}"
    } else {
        "${pad2(m)}:${pad2(s)},${pad2(centis)}"
    }
}

private fun formatTimer(sec: Int): String {
    val m = (sec / 60).toLong()
    val s = (sec % 60).toLong()
    return "${pad2(m)}:${pad2(s)}"
}

private fun formatDelta(absMs: Long): String {
    val sec = absMs / 100
    val whole = sec / 10
    val tenth = sec % 10
    return "$whole,${tenth} dtk"
}

// ---------------------------------------------------------------------------
// Layar utama
// ---------------------------------------------------------------------------

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClockKitLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    // --- World clock ---
    var clockSeconds by remember { mutableIntStateOf(0) }
    val cities = remember { mutableStateListOf<WorldCity>().apply { addAll(homeCities) } }
    val cityPool = remember { mutableStateListOf<WorldCity>().apply { addAll(extraCityPool) } }

    // --- Alarm ---
    val alarms = remember { mutableStateListOf<AlarmState>().apply { addAll(alarmSeed) } }
    val deletedAlarms = remember { mutableStateListOf<AlarmState>() }
    var simulateSyncFail by remember { mutableStateOf(false) }
    var alarmSyncState by remember { mutableStateOf(AlarmSyncState.IDLE) }
    var alarmSyncTick by remember { mutableIntStateOf(0) }

    // --- Stopwatch ---
    var swRunning by remember { mutableStateOf(false) }
    var swElapsedMs by remember { mutableLongStateOf(0L) }
    var lapCounter by remember { mutableIntStateOf(0) }
    val laps = remember { mutableStateListOf<LapEntry>() }

    // --- Timer ---
    var timerTotalSec by remember { mutableIntStateOf(5 * 60) }
    var timerRemainingSec by remember { mutableIntStateOf(5 * 60) }
    var timerRunning by remember { mutableStateOf(false) }
    var timerDone by remember { mutableStateOf(false) }

    // Tick jam dunia (detik) — dibatalkan otomatis saat layar ditinggalkan.
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            clockSeconds++
        }
    }

    // Sinkronisasi alarm simulasi.
    LaunchedEffect(alarmSyncTick) {
        if (alarmSyncTick > 0) {
            alarmSyncState = AlarmSyncState.LOADING
            delay(1200)
            alarmSyncState = if (simulateSyncFail) AlarmSyncState.ERROR else AlarmSyncState.SUCCESS
        }
    }

    // Tick stopwatch 50 ms hanya saat berjalan.
    LaunchedEffect(swRunning) {
        while (swRunning) {
            delay(50)
            swElapsedMs += 50L
        }
    }

    // Tick timer 1 detik hanya saat berjalan.
    LaunchedEffect(timerRunning) {
        while (timerRunning && timerRemainingSec > 0) {
            delay(1000)
            timerRemainingSec -= 1
            if (timerRemainingSec <= 0) {
                timerRemainingSec = 0
                timerRunning = false
                timerDone = true
            }
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Clock Kit",
                subtitle = "World clock, alarm, stopwatch, timer",
                onBack = onBack,
            )
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
            // ---------------- World Clock ----------------
            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LabSectionTitle(
                        title = "World Clock",
                        subtitle = "Slot matahari / hari-malam per kota",
                        modifier = Modifier.weight(1f),
                    )
                    SimulasiPill()
                }

                cities.forEach { city ->
                    WorldClockRow(city = city, seconds = clockSeconds)
                }

                SmallActionButton(
                    text = "Tambah Kota (Simulasi)",
                    onClick = {
                        val next = cityPool.removeFirstOrNull()
                        if (next != null) cities.add(next)
                    },
                    enabled = cityPool.isNotEmpty(),
                )
                if (cityPool.isEmpty()) {
                    Text(
                        "Semua kota sudah ditambahkan — tombol dinonaktifkan (disabled).",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                }
            }

            // ---------------- Alarm ----------------
            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LabSectionTitle(
                        title = "Alarm",
                        subtitle = "Toggle jadwal mati/hidup dengan cepat",
                        modifier = Modifier.weight(1f),
                    )
                    val activeCount = alarms.count { it.enabled }
                    StatusPill(
                        text = "$activeCount/${alarms.size} hidup",
                        color = if (activeCount > 0) palette.success else palette.textMuted,
                    )
                }

                if (alarms.isEmpty()) {
                    Text(
                        "Belum ada alarm — semua alarm dihapus.",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                }
                alarms.forEach { alarm ->
                    AlarmRow(
                        alarm = alarm,
                        onToggle = { enabled ->
                            val index = alarms.indexOfFirst { it.id == alarm.id }
                            if (index >= 0) alarms[index] = alarms[index].copy(enabled = enabled)
                        },
                        onDelete = {
                            alarms.removeAll { it.id == alarm.id }
                            deletedAlarms.add(alarm)
                        },
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallActionButton(
                        text = "Nonaktifkan Semua",
                        onClick = {
                            for (i in alarms.indices) {
                                alarms[i] = alarms[i].copy(enabled = false)
                            }
                        },
                        enabled = alarms.any { it.enabled },
                        modifier = Modifier.weight(1f),
                    )
                    SmallActionButton(
                        text = "Pulihkan Alarm",
                        onClick = {
                            val restored = deletedAlarms.removeFirstOrNull()
                            if (restored != null) alarms.add(restored.copy(enabled = false))
                        },
                        enabled = deletedAlarms.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                    )
                }

                // Sinkronisasi alarm (simulasi)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Simulasikan gagal sinkron",
                            fontSize = 12.sp,
                            color = palette.textSecondary,
                            modifier = Modifier.weight(1f),
                        )
                        FlatToggle(
                            checked = simulateSyncFail,
                            onCheckedChange = { simulateSyncFail = it },
                            modifier = Modifier.semantics {
                                contentDescription = "Simulasikan gagal sinkron alarm"
                            },
                        )
                    }
                    val syncLabel = when (alarmSyncState) {
                        AlarmSyncState.IDLE -> "Belum pernah disinkronkan."
                        AlarmSyncState.LOADING -> "Menyinkronkan alarm… (loading)"
                        AlarmSyncState.SUCCESS -> "Alarm tersinkron (simulasi sukses)."
                        AlarmSyncState.ERROR -> "Gagal menyinkronkan alarm (simulasi gagal)."
                    }
                    val syncColor = when (alarmSyncState) {
                        AlarmSyncState.ERROR -> palette.danger
                        AlarmSyncState.SUCCESS -> palette.success
                        else -> palette.textMuted
                    }
                    Text(syncLabel, fontSize = 11.sp, color = syncColor)
                    SmallActionButton(
                        text = if (alarmSyncState == AlarmSyncState.LOADING) "Menyinkronkan…" else "Sinkronkan Alarm (Simulasi)",
                        onClick = { alarmSyncTick++ },
                        enabled = alarmSyncState != AlarmSyncState.LOADING,
                    )
                }
            }

            // ---------------- Stopwatch ----------------
            LabCard {
                LabSectionTitle(
                    title = "Stopwatch",
                    subtitle = "Waktu ketuk & daftar lap dengan delta",
                )

                Text(
                    text = formatStopwatch(swElapsedMs),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = palette.textPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Waktu stopwatch ${formatStopwatch(swElapsedMs)}" },
                )
                StatusPill(
                    text = if (swRunning) "Status: berjalan" else if (swElapsedMs > 0) "Status: dijeda" else "Status: siap",
                    color = if (swRunning) palette.success else palette.textMuted,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallActionButton(
                        text = if (swRunning) "Jeda" else "Mulai",
                        onClick = { swRunning = !swRunning },
                        modifier = Modifier.weight(1f),
                    )
                    SmallActionButton(
                        text = "Lap",
                        onClick = {
                            val total = swElapsedMs
                            val prevTotal = laps.lastOrNull()?.totalMs ?: 0L
                            lapCounter += 1
                            laps.add(LapEntry(id = lapCounter, totalMs = total, splitMs = total - prevTotal))
                        },
                        enabled = swRunning,
                        modifier = Modifier.weight(1f),
                    )
                    SmallActionButton(
                        text = "Setel Ulang",
                        onClick = {
                            swRunning = false
                            swElapsedMs = 0L
                            laps.clear()
                        },
                        enabled = swElapsedMs > 0L || laps.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                    )
                }

                val longestSplitMs = remember(laps.size) {
                    if (laps.isEmpty()) null else laps.maxOf { it.splitMs }
                }

                if (laps.isEmpty()) {
                    Text(
                        "Belum ada lap — tekan Lap saat stopwatch berjalan (kosong).",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                } else {
                    for (index in laps.size - 1 downTo 0) {
                        val lap = laps[index]
                        val prevSplit = if (index > 0) laps[index - 1].splitMs else null
                        StopwatchLapRow(
                            lapNumber = index + 1,
                            lap = lap,
                            previousSplitMs = prevSplit,
                            isLongest = laps.size > 1 && longestSplitMs != null && lap.splitMs == longestSplitMs,
                        )
                    }
                }
            }

            // ---------------- Timer ----------------
            LabCard {
                LabSectionTitle(
                    title = "Timer",
                    subtitle = "Lingkaran memanjang saat berjalan, aksi hapus/bekukan",
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1, 5, 10, 15).forEach { minute ->
                        val selected = timerTotalSec == minute * 60
                        Surface(
                            onClick = {
                                timerTotalSec = minute * 60
                                timerRemainingSec = minute * 60
                                timerRunning = false
                                timerDone = false
                            },
                            shape = RoundedCornerShape(999.dp),
                            color = if (selected) palette.primary else palette.surfaceMuted,
                            border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                            modifier = Modifier.semantics {
                                contentDescription = "Preset timer $minute menit"
                            },
                        ) {
                            Text(
                                text = "${minute}m",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) palette.onPrimary else palette.textSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            )
                        }
                    }
                }

                TimerRing(
                    totalSec = timerTotalSec,
                    remainingSec = timerRemainingSec,
                    running = timerRunning,
                )

                val timerStatus = when {
                    timerDone -> "Status: selesai — waktu habis"
                    timerRemainingSec == 0 -> "Status: timer dihapus / belum diatur"
                    timerRunning -> "Status: berjalan"
                    else -> "Status: dibekukan"
                }
                val timerStatusColor = when {
                    timerDone -> palette.success
                    timerRemainingSec == 0 -> palette.textMuted
                    timerRunning -> palette.info
                    else -> palette.warning
                }
                StatusPill(text = timerStatus, color = timerStatusColor)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallActionButton(
                        text = if (timerRunning) "Bekukan" else "Mulai",
                        onClick = {
                            if (!timerRunning && timerDone) {
                                timerRemainingSec = timerTotalSec
                                timerDone = false
                            }
                            timerRunning = !timerRunning
                        },
                        enabled = timerRemainingSec > 0 || timerDone,
                        modifier = Modifier.weight(1f),
                    )
                    SmallActionButton(
                        text = "Hapus",
                        onClick = {
                            timerRunning = false
                            timerRemainingSec = 0
                            timerDone = false
                        },
                        enabled = timerRunning || timerRemainingSec > 0,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (timerRemainingSec == 0 && !timerDone) {
                    Text(
                        "Timer kosong — tombol Mulai dinonaktifkan sampai preset dipilih (disabled).",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
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
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "Catatan:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = palette.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "• Waktu bersumber dari tick coroutine mock, bukan jam sistem perangkat.",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        "• Delta lap ditandai dengan teks (Lebih cepat / Lebih lambat), bukan warna saja.",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        "• Sinkronisasi alarm adalah simulasi tanpa penyimpanan sistem.",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Sub-composable privat
// ---------------------------------------------------------------------------

@Composable
private fun SimulasiPill() {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = palette.info.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, palette.info),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Text(
            text = "Simulasi",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = palette.info,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}

@Composable
private fun SmallActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = if (enabled) palette.primary else palette.primary.copy(alpha = 0.4f),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = palette.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 11.dp, horizontal = 12.dp),
        )
    }
}

@Composable
private fun WorldClockRow(city: WorldCity, seconds: Int) {
    val palette = LocalAppPalette.current
    val secOfDay = (city.baseSeconds + seconds) % 86400
    val hour = secOfDay / 3600
    val isDay = hour in 6..17

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .semantics {
                contentDescription =
                    "Jam dunia ${city.city}, ${formatClockTime(secOfDay)}, ${if (isDay) "siang" else "malam"}"
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f),
        ) {
            SunMoonIcon(isDay = isDay, tint = if (isDay) palette.warning else palette.info)
            Column {
                Text(
                    city.city,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${city.zoneLabel} · ${city.dayOffsetLabel} · ${if (isDay) "Siang" else "Malam"}",
                    fontSize = 10.sp,
                    color = palette.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Text(
            text = formatClockTime(secOfDay),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textPrimary,
        )
    }
}

@Composable
private fun SunMoonIcon(isDay: Boolean, tint: Color) {
    Canvas(modifier = Modifier.size(30.dp)) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w / 2f, h / 2f)
        if (isDay) {
            drawCircle(color = tint, radius = w * 0.2f, center = center)
            for (ray in 0 until 8) {
                val angle = kotlin.math.PI / 4 * ray
                val start = Offset(
                    (center.x + w * 0.32f * kotlin.math.cos(angle)).toFloat(),
                    (center.y + h * 0.32f * kotlin.math.sin(angle)).toFloat(),
                )
                val end = Offset(
                    (center.x + w * 0.46f * kotlin.math.cos(angle)).toFloat(),
                    (center.y + h * 0.46f * kotlin.math.sin(angle)).toFloat(),
                )
                drawLine(color = tint, start = start, end = end, strokeWidth = w * 0.08f, cap = StrokeCap.Round)
            }
        } else {
            drawArc(
                color = tint,
                startAngle = 30f,
                sweepAngle = 300f,
                useCenter = false,
                topLeft = Offset(w * 0.15f, h * 0.15f),
                size = Size(w * 0.7f, h * 0.7f),
                style = Stroke(width = w * 0.12f, cap = StrokeCap.Round),
            )
            drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.78f, h * 0.22f))
        }
    }
}

@Composable
private fun AlarmRow(alarm: AlarmState, onToggle: (Boolean) -> Unit, onDelete: () -> Unit) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                alarm.time,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (alarm.enabled) palette.textPrimary else palette.textMuted,
            )
            Text(
                "${alarm.label} · ${alarm.repeatLabel}",
                fontSize = 11.sp,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (alarm.enabled) "Status: Hidup" else "Status: Mati",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (alarm.enabled) palette.success else palette.textMuted,
            )
        }
        FlatToggle(
            checked = alarm.enabled,
            onCheckedChange = onToggle,
            modifier = Modifier.semantics {
                contentDescription = "Alarm ${alarm.label} ${alarm.time}"
            },
        )
        Text(
            text = "Hapus",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = palette.danger,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onDelete() }
                .padding(6.dp)
                .semantics { contentDescription = "Hapus alarm ${alarm.label}" },
        )
    }
}

@Composable
private fun StopwatchLapRow(
    lapNumber: Int,
    lap: LapEntry,
    previousSplitMs: Long?,
    isLongest: Boolean,
) {
    val palette = LocalAppPalette.current
    val diff = previousSplitMs?.let { lap.splitMs - it }
    val deltaColor = when {
        diff == null -> palette.textMuted
        diff < 0 -> palette.success
        diff > 0 -> palette.danger
        else -> palette.textMuted
    }
    val deltaText = when {
        diff == null -> "Lap awal"
        diff < 0 -> "Lebih cepat ${formatDelta(-diff)}"
        diff > 0 -> "Lebih lambat ${formatDelta(diff)}"
        else -> "Sama dengan lap sebelumnya"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .semantics {
                contentDescription = "Lap $lapNumber, ${formatStopwatch(lap.splitMs)}, $deltaText"
            },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Lap $lapNumber",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textSecondary,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = deltaText,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = deltaColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (isLongest) {
                Spacer(modifier = Modifier.height(3.dp))
                StatusPill(text = "Lap Terpanjang", color = palette.warning)
            }
        }
        Text(
            text = formatStopwatch(lap.splitMs),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textPrimary,
        )
    }
}

@Composable
private fun TimerRing(totalSec: Int, remainingSec: Int, running: Boolean) {
    val palette = LocalAppPalette.current
    val progress = if (totalSec > 0) remainingSec.toFloat() / totalSec.toFloat() else 0f

    val transition = rememberInfiniteTransition(label = "timerPulse")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "timerPulseValue",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .size(150.dp)
                .graphicsLayer {
                    // Lingkaran "memanjang" (sedikit lebih tinggi) saat berjalan.
                    val stretch = if (running) 0.05f * pulse else 0f
                    scaleY = 1f + stretch
                    scaleX = 1f - stretch / 2f
                },
        ) {
            val strokeStyle = Stroke(width = 14f, cap = StrokeCap.Round)
            drawArc(
                color = palette.surfaceMuted,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(12f, 12f),
                size = Size(this.size.width - 24f, this.size.height - 24f),
                style = strokeStyle,
            )
            if (progress > 0f) {
                drawArc(
                    color = palette.primary,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = Offset(12f, 12f),
                    size = Size(this.size.width - 24f, this.size.height - 24f),
                    style = strokeStyle,
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatTimer(remainingSec),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = palette.textPrimary,
                modifier = Modifier.semantics {
                    contentDescription = "Sisa waktu $remainingSec detik"
                },
            )
            Text(
                text = "dari ${formatTimer(totalSec)}",
                fontSize = 11.sp,
                color = palette.textMuted,
            )
        }
    }
}
