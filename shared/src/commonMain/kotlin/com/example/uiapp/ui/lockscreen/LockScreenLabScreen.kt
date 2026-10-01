package com.example.uiapp.ui.lockscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private data class WallpaperOption(
    val id: String,
    val name: String,
    val colors: List<Color>,
)

private data class WidgetOption(
    val id: String,
    val name: String,
    val subtitle: String,
)

private data class NotifItem(
    val id: String,
    val app: String,
    val title: String,
    val body: String,
)

private enum class ClockFont(val label: String, val family: FontFamily) {
    KLASIK("Klasik", FontFamily.Serif),
    MODERN("Modern", FontFamily.SansSerif),
    MEKANIS("Mekanis", FontFamily.Monospace),
}

private enum class ClockSize(val label: String, val sizeSp: Int) {
    KECIL("Kecil", 46),
    SEDANG("Sedang", 56),
    BESAR("Besar", 68),
}

private data class SlotRef(val group: Int, val index: Int)

private val widgetGallery = listOf(
    WidgetOption("cuaca", "Cuaca", "28° Cerah"),
    WidgetOption("kalender", "Kalender", "Rab 30 Sep"),
    WidgetOption("baterai", "Baterai", "82% tersisa"),
    WidgetOption("aktivitas", "Aktivitas", "3 cincin"),
    WidgetOption("musik", "Musik", "Sedang diputar"),
    WidgetOption("pengingat", "Pengingat", "3 tugas hari ini"),
    WidgetOption("jamdunia", "Jam Dunia", "Tokyo 21:04"),
    WidgetOption("langkah", "Langkah", "6.240 langkah"),
)

private val mockNotifs = listOf(
    NotifItem("n1", "Pesan", "Rina Prameswari", "Fotonya sudah kukirim ya, cek di grup."),
    NotifItem("n2", "Kalender", "Rapat desain 14.00", "Ruang Kenari · 45 menit"),
    NotifItem("n3", "Belanja", "Pesanan dikirim", "Paket diperkirakan tiba 18.30"),
)

private val clockColorNames = listOf("Gelap", "Terang", "Utama", "Biru", "Hijau", "Kuning", "Merah", "Abu")

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LockScreenLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var locked by remember { mutableStateOf(true) }
    var alwaysOn by remember { mutableStateOf(false) }
    var clockFont by remember { mutableStateOf(ClockFont.KLASIK) }
    var clockSize by remember { mutableStateOf(ClockSize.SEDANG) }
    var clockColorIndex by remember { mutableIntStateOf(0) }
    var wallpaperIndex by remember { mutableIntStateOf(0) }
    var notifExpanded by remember { mutableStateOf(false) }
    var showNotif by remember { mutableStateOf(true) }
    var selectedSlot by remember { mutableStateOf(SlotRef(0, 0)) }
    var statusNote by remember { mutableStateOf("Ketuk slot widget lalu pilih widget dari galeri") }

    // Slot atas (4) dan slot bawah (2) — awalnya terisi sebagian, sisanya kosong.
    val aboveSlots = remember { mutableStateListOf<WidgetOption?>(widgetGallery[0], widgetGallery[1], null, null) }
    val belowSlots = remember { mutableStateListOf<WidgetOption?>(widgetGallery[3], null) }
    val reversedNotifs = remember { mockNotifs.reversed() }

    fun swatchColor(index: Int): Color = when (index) {
        0 -> palette.textPrimary
        1 -> palette.onPrimary
        2 -> palette.primary
        3 -> palette.info
        4 -> palette.success
        5 -> palette.warning
        6 -> palette.danger
        else -> palette.textSecondary
    }

    val wallpapers = remember {
        listOf(
            WallpaperOption("fajar", "Fajar", listOf(palette.info.copy(alpha = 0.85f), palette.primary.copy(alpha = 0.75f))),
            WallpaperOption("senja", "Senja", listOf(palette.warning.copy(alpha = 0.85f), palette.danger.copy(alpha = 0.65f))),
            WallpaperOption("hutan", "Hutan", listOf(palette.primary, palette.success.copy(alpha = 0.65f))),
            WallpaperOption("malam", "Malam", listOf(palette.textPrimary.copy(alpha = 0.92f), palette.surfaceMuted)),
            WallpaperOption("kabut", "Kabut", listOf(palette.surfaceMuted, palette.textMuted.copy(alpha = 0.55f))),
        )
    }

    val wallpaperBrush = remember(wallpaperIndex) {
        Brush.linearGradient(wallpapers[wallpaperIndex].colors)
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Lock Screen Customization",
                subtitle = "Jam, slot widget, wallpaper — Simulasi",
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
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = palette.info.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, palette.info),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Simulasi", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.info)
                    Text(
                        "Wallpaper, notifikasi, dan Live Activity di sini mock lokal. Tidak ada integrasi sistem sungguhan.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = palette.textSecondary,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // ===== Pratinjau layar kunci =====
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(wallpaperBrush),
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { alpha = if (alwaysOn) 0.55f else 1f }
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        // Status bar tiruan + Live Activity pill
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("09.41", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("Live Activity", fontSize = 8.sp, color = palette.onPrimary.copy(alpha = 0.85f))
                                Canvas(modifier = Modifier.size(14.dp)) {
                                    val w = size.width
                                    val h = size.height
                                    drawLine(palette.onPrimary, androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.35f), androidx.compose.ui.geometry.Offset(w * 0.85f, h * 0.35f), 2f, StrokeCap.Round)
                                    drawLine(palette.onPrimary, androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.65f), androidx.compose.ui.geometry.Offset(w * 0.65f, h * 0.65f), 2f, StrokeCap.Round)
                                }
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = palette.surface.copy(alpha = 0.92f),
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(palette.success),
                                )
                                Text(
                                    "Paket makan siang tiba 8 mnt",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f),
                                )
                                Text("Live", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = palette.success)
                            }
                        }

                        // Empat slot widget atas
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            repeat(4) { index ->
                                WidgetSlot(
                                    widget = aboveSlots[index],
                                    selected = selectedSlot == SlotRef(0, index),
                                    compact = true,
                                    onClick = {
                                        if (selectedSlot == SlotRef(0, index) && aboveSlots[index] != null) {
                                            aboveSlots[index] = null
                                            statusNote = "Slot atas ${index + 1} dikosongkan"
                                        } else {
                                            selectedSlot = SlotRef(0, index)
                                            statusNote = "Slot atas ${index + 1} dipilih — ketuk widget di galeri"
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Jam besar + tanggal
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                "Rabu 30 September",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = swatchColor(clockColorIndex).copy(alpha = 0.9f),
                            )
                            Text(
                                "09.41",
                                fontFamily = clockFont.family,
                                fontSize = clockSize.sizeSp.sp,
                                fontWeight = FontWeight.Bold,
                                color = swatchColor(clockColorIndex),
                                textAlign = TextAlign.Center,
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Dua slot widget bawah
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            repeat(2) { index ->
                                WidgetSlot(
                                    widget = belowSlots[index],
                                    selected = selectedSlot == SlotRef(1, index),
                                    compact = false,
                                    onClick = {
                                        if (selectedSlot == SlotRef(1, index) && belowSlots[index] != null) {
                                            belowSlots[index] = null
                                            statusNote = "Slot bawah ${index + 1} dikosongkan"
                                        } else {
                                            selectedSlot = SlotRef(1, index)
                                            statusNote = "Slot bawah ${index + 1} dipilih — ketuk widget di galeri"
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        if (showNotif) {
                            if (notifExpanded) {
                                Column(
                                    modifier = Modifier
                                        .height(140.dp)
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    mockNotifs.forEach { notif ->
                                        NotifCard(notif = notif)
                                    }
                                    Text(
                                        "Ketuk untuk menumpuk kembali",
                                        fontSize = 9.sp,
                                        color = palette.onPrimary.copy(alpha = 0.9f),
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .clickable { notifExpanded = false }
                                            .padding(4.dp),
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(108.dp)
                                        .clickable {
                                            notifExpanded = true
                                            statusNote = "Notifikasi dibuka (Simulasi)"
                                        }
                                        .semantics {
                                            role = Role.Button
                                            contentDescription = "Buka tumpukan 3 notifikasi simulasi"
                                        },
                                ) {
                                    reversedNotifs.forEachIndexed { revIndex, notif ->
                                        val stackIndex = mockNotifs.size - 1 - revIndex
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .offset(y = (stackIndex * 12).dp)
                                                .graphicsLayer { alpha = 1f - stackIndex * 0.18f },
                                        ) {
                                            NotifCard(notif = notif, singleLine = true)
                                        }
                                    }
                                    Text(
                                        "3 notifikasi · ketuk untuk membuka",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.onPrimary.copy(alpha = 0.95f),
                                        modifier = Modifier.align(Alignment.BottomCenter),
                                    )
                                }
                            }
                        }

                        // Footer layar kunci
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            LockGlyph(tint = palette.onPrimary, open = !locked)
                            Text(
                                if (locked) "Geser untuk membuka" else "Terbuka",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.onPrimary.copy(alpha = 0.95f),
                            )
                            Canvas(modifier = Modifier.size(18.dp)) {
                                val w = size.width
                                val h = size.height
                                drawCircle(palette.onPrimary.copy(alpha = 0.9f), radius = w * 0.30f, style = Stroke(2f, cap = StrokeCap.Round))
                                drawLine(palette.onPrimary.copy(alpha = 0.9f), androidx.compose.ui.geometry.Offset(w * 0.14f, h * 0.78f), androidx.compose.ui.geometry.Offset(w * 0.44f, h * 0.52f), 2f, StrokeCap.Round)
                            }
                        }
                    }

                    if (alwaysOn) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(palette.background.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = palette.surface.copy(alpha = 0.9f),
                                border = BorderStroke(1.dp, palette.border),
                            ) {
                                Text(
                                    "Selalu Aktif — konten meredup",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                )
                            }
                        }
                    }
                }
            }

            // ===== Kontrol state =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    onClick = { locked = !locked },
                    shape = RoundedCornerShape(10.dp),
                    color = if (locked) palette.primary else palette.surface,
                    border = BorderStroke(1.dp, if (locked) palette.primary else palette.border),
                    modifier = Modifier
                        .weight(1f)
                        .semantics {
                            role = Role.Button
                            contentDescription = if (locked) "Buka layar kunci simulasi" else "Kunci layar simulasi"
                        },
                ) {
                    Text(
                        if (locked) "Buka Layar" else "Kunci Layar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (locked) palette.onPrimary else palette.textPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                }
                Surface(
                    onClick = {
                        alwaysOn = !alwaysOn
                        statusNote = if (alwaysOn) "Varian Selalu Aktif: konten meredup (Simulasi)" else "Selalu Aktif dimatikan"
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = if (alwaysOn) palette.info.copy(alpha = 0.15f) else palette.surface,
                    border = BorderStroke(1.dp, if (alwaysOn) palette.info else palette.border),
                    modifier = Modifier
                        .weight(1f)
                        .semantics {
                            role = Role.Button
                            contentDescription = if (alwaysOn) "Matikan mode Selalu Aktif" else "Nyalakan mode Selalu Aktif"
                        },
                ) {
                    Text(
                        if (alwaysOn) "Selalu Aktif: ON" else "Selalu Aktif: OFF",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (locked) palette.warning.copy(alpha = 0.15f) else palette.success.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, if (locked) palette.warning else palette.success),
                ) {
                    Text(
                        if (locked) "Status: terkunci" else "Status: terbuka",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
                Text(statusNote, fontSize = 11.sp, color = palette.textSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }

            // ===== Kustomisasi jam =====
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Kustomisasi Jam", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)

                    Text("Font", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ClockFont.entries.forEach { font ->
                            val selected = clockFont == font
                            Surface(
                                onClick = { clockFont = font },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) palette.primary else palette.surface,
                                border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
                                modifier = Modifier.semantics {
                                    role = Role.Button
                                    contentDescription = "Font jam ${font.label}${if (selected) ", terpilih" else ""}"
                                },
                            ) {
                                Text(
                                    font.label,
                                    fontFamily = font.family,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selected) palette.onPrimary else palette.textSecondary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                )
                            }
                        }
                    }

                    Text("Ukuran", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ClockSize.entries.forEach { sizeOption ->
                            val selected = clockSize == sizeOption
                            Surface(
                                onClick = { clockSize = sizeOption },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) palette.primary else palette.surface,
                                border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
                                modifier = Modifier.semantics {
                                    role = Role.Button
                                    contentDescription = "Ukuran jam ${sizeOption.label}${if (selected) ", terpilih" else ""}"
                                },
                            ) {
                                Text(
                                    sizeOption.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selected) palette.onPrimary else palette.textSecondary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                )
                            }
                        }
                    }

                    Text("Warna (8 pilihan)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        for (index in 0..3) {
                            ColorSwatch(
                                name = clockColorNames[index],
                                color = swatchColor(index),
                                selected = clockColorIndex == index,
                                onClick = {
                                    clockColorIndex = index
                                    statusNote = "Warna jam diubah ke ${clockColorNames[index]}"
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        for (index in 4..7) {
                            ColorSwatch(
                                name = clockColorNames[index],
                                color = swatchColor(index),
                                selected = clockColorIndex == index,
                                onClick = {
                                    clockColorIndex = index
                                    statusNote = "Warna jam diubah ke ${clockColorNames[index]}"
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            // ===== Galeri widget =====
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Galeri Widget (Simulasi)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                    Text(
                        "Slot aktif: ${if (selectedSlot.group == 0) "atas" else "bawah"} ${selectedSlot.index + 1}. Ketuk widget untuk memasang; ketuk slot terisi yang sama sekali lagi untuk mengosongkan.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = palette.textSecondary,
                    )
                    widgetGallery.forEach { widget ->
                        val installed = aboveSlots.any { it?.id == widget.id } || belowSlots.any { it?.id == widget.id }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(palette.surfaceMuted)
                                    .border(1.dp, palette.border, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                WidgetGlyph(tint = palette.textSecondary)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(widget.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                                Text(widget.subtitle, fontSize = 10.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Surface(
                                onClick = {
                                    if (selectedSlot.group == 0) {
                                        aboveSlots[selectedSlot.index] = widget
                                    } else {
                                        belowSlots[selectedSlot.index] = widget
                                    }
                                    statusNote = "${widget.name} dipasang di slot ${if (selectedSlot.group == 0) "atas" else "bawah"} ${selectedSlot.index + 1} (Simulasi)"
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = palette.primary,
                                border = BorderStroke(1.dp, palette.primary),
                                modifier = Modifier.semantics {
                                    role = Role.Button
                                    contentDescription = "Pasang widget ${widget.name} ke slot terpilih"
                                },
                            ) {
                                Text(
                                    if (installed) "Pasang Ulang" else "Pasang",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.onPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                )
                            }
                        }
                    }
                }
            }

            // ===== Galeri wallpaper =====
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Galeri Wallpaper Gradien (Simulasi)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        wallpapers.forEachIndexed { index, wallpaper ->
                            val selected = wallpaperIndex == index
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        wallpaperIndex = index
                                        statusNote = "Wallpaper ${wallpaper.name} dipilih (Simulasi)"
                                    }
                                    .padding(4.dp)
                                    .semantics {
                                        role = Role.Button
                                        contentDescription = "Pilih wallpaper ${wallpaper.name}${if (selected) ", terpilih" else ""}"
                                    },
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 64.dp, height = 88.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Brush.linearGradient(wallpaper.colors))
                                        .border(
                                            BorderStroke(if (selected) 2.dp else 1.dp, if (selected) palette.primary else palette.border),
                                            RoundedCornerShape(12.dp),
                                        ),
                                )
                                Text(
                                    wallpaper.name,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selected) palette.textPrimary else palette.textMuted,
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showNotif = !showNotif
                                statusNote = if (showNotif) "Notifikasi ditampilkan (Simulasi)" else "Notifikasi disembunyikan"
                            }
                            .semantics {
                                role = Role.Button
                                contentDescription = if (showNotif) "Sembunyikan notifikasi tiruan" else "Tampilkan notifikasi tiruan"
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Tampilkan notifikasi bertumpuk", fontSize = 12.sp, color = palette.textSecondary)
                        Box(
                            modifier = Modifier
                                .size(width = 38.dp, height = 22.dp)
                                .clip(CircleShape)
                                .background(if (showNotif) palette.primary else palette.surfaceMuted)
                                .border(1.dp, palette.border, CircleShape),
                            contentAlignment = if (showNotif) Alignment.CenterEnd else Alignment.CenterStart,
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(if (showNotif) palette.onPrimary else palette.textMuted),
                            )
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Pola Lock Screen Customization:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Pratinjau langsung berubah mengikuti pemilih font, ukuran, dan warna jam", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Enam slot widget (empat atas, dua bawah) diisi dari galeri mock tanpa widget sistem", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Varian Selalu Aktif meredupkan konten; notifikasi dan Live Activity sepenuhnya simulasi", fontSize = 12.sp, color = palette.textSecondary)
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun WidgetSlot(
    widget: WidgetOption?,
    selected: Boolean,
    compact: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier
            .height(if (compact) 48.dp else 56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surface.copy(alpha = 0.92f))
            .border(
                BorderStroke(if (selected) 2.dp else 1.dp, if (selected) palette.primary else palette.border),
                RoundedCornerShape(12.dp),
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .semantics {
                role = Role.Button
                contentDescription = if (widget == null) {
                    "Slot kosong${if (selected) ", terpilih" else ""}"
                } else {
                    "Slot berisi ${widget.name}${if (selected) ", terpilih, ketuk lagi untuk mengosongkan" else ""}"
                }
            },
        verticalArrangement = Arrangement.Center,
    ) {
        if (widget == null) {
            Text(
                if (selected) "Pilih widget" else "Kosong",
                fontSize = 9.sp,
                color = palette.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        } else {
            Text(
                widget.name,
                fontSize = if (compact) 9.sp else 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                widget.subtitle,
                fontSize = if (compact) 8.sp else 9.sp,
                color = palette.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun NotifCard(notif: NotifItem, singleLine: Boolean = false) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = palette.surface.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                "${notif.app} · sekarang",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                notif.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!singleLine) {
                Text(
                    notif.body,
                    fontSize = 10.sp,
                    color = palette.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    name: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier.semantics {
            role = Role.Button
            contentDescription = "Warna jam $name${if (selected) ", terpilih" else ""}"
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                    BorderStroke(if (selected) 2.dp else 1.dp, if (selected) palette.primary else palette.border),
                    CircleShape,
                )
                .clickable { onClick() },
        )
        Text(
            name,
            fontSize = 9.sp,
            color = if (selected) palette.textPrimary else palette.textMuted,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun LockGlyph(tint: Color, open: Boolean) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Badan gembok
        val body = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.25f, h * 0.50f)
            lineTo(w * 0.75f, h * 0.50f)
            lineTo(w * 0.75f, h * 0.85f)
            lineTo(w * 0.25f, h * 0.85f)
            close()
        }
        drawPath(body, color = tint, style = stroke)
        // Busur gembok (terbuka bila tidak terkunci)
        val arc = androidx.compose.ui.graphics.Path().apply {
            if (open) {
                moveTo(w * 0.62f, h * 0.34f)
                lineTo(w * 0.62f, h * 0.26f)
                addArc(
                    androidx.compose.ui.geometry.Rect(w * 0.34f, h * 0.12f, w * 0.80f, h * 0.52f),
                    270f,
                    140f,
                )
            } else {
                addArc(
                    androidx.compose.ui.geometry.Rect(w * 0.30f, h * 0.12f, w * 0.70f, h * 0.52f),
                    180f,
                    180f,
                )
                moveTo(w * 0.30f, h * 0.34f)
                lineTo(w * 0.30f, h * 0.50f)
                moveTo(w * 0.70f, h * 0.34f)
                lineTo(w * 0.70f, h * 0.50f)
            }
        }
        drawPath(arc, color = tint, style = stroke)
    }
}

@Composable
private fun WidgetGlyph(tint: Color) {
    Canvas(modifier = Modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.18f, h * 0.18f)
            lineTo(w * 0.46f, h * 0.18f)
            lineTo(w * 0.46f, h * 0.46f)
            lineTo(w * 0.18f, h * 0.46f)
            close()
            moveTo(w * 0.54f, h * 0.18f)
            lineTo(w * 0.82f, h * 0.18f)
            lineTo(w * 0.82f, h * 0.82f)
            lineTo(w * 0.54f, h * 0.82f)
            close()
            moveTo(w * 0.18f, h * 0.54f)
            lineTo(w * 0.46f, h * 0.54f)
            lineTo(w * 0.46f, h * 0.82f)
            lineTo(w * 0.18f, h * 0.82f)
            close()
        }
        drawPath(path, color = tint, style = stroke)
    }
}
