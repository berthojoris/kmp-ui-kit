package com.example.uiapp.ui.createlab

import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.AppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

// ---------- Data mock lokal (semua simulasi, tanpa kamera/mikrofon nyata) ----------

private data class PhotoFilter(val id: String, val label: String, val saturation: Float, val overlay: Color?)

private val PhotoFilters = listOf(
    PhotoFilter("normal", "Normal", 1f, null),
    PhotoFilter("mono", "Mono", 0f, null),
    PhotoFilter("sepia", "Sepia", 0.45f, Color(0x33A97142)),
    PhotoFilter("vivid", "Vivid", 1.6f, null),
    PhotoFilter("cool", "Cool", 1.1f, Color(0x223B82F6)),
    PhotoFilter("warm", "Warm", 1.1f, Color(0x22F59E0B)),
    PhotoFilter("fade", "Fade", 0.8f, Color(0x33FFFFFF)),
)

private data class Recording(val id: String, val name: String, val duration: Int)

private data class Sticker(val id: Int, val x: Float, val y: Float, val colorIndex: Int, val round: Boolean)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Media Creation",
                subtitle = "Filter foto, QR generator, perekam suara, editor",
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
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LabIntroCard(
                title = "Studio Mini (Simulasi)",
                body = "Tidak ada akses kamera, mikrofon, atau jaringan. Foto memakai gradien mock, QR dibangkitkan dari pola pseudo-acak, dan rekaman suara hanya menampilkan durasi tiruan.",
            )

            LabSectionTitle("1. Filter Foto", "Pratinjau langsung dengan tujuh preset")
            FilterCard()

            LabSectionTitle("2. QR Generator", "Bangkitkan pola QR dari teks (simulasi)")
            QrCard()

            LabSectionTitle("3. Perekam Suara", "Waveform langsung dan daftar rekaman")
            RecorderCard()

            LabSectionTitle("4. Editor Anotasi", "Tambah dan geser stiker di atas gambar")
            AnnotationCard()
        }
    }
}

// ---------- 1. Filter Foto ----------

@Composable
private fun FilterCard() {
    val palette = LocalAppPalette.current
    var selected by remember { mutableIntStateOf(0) }
    var applied by remember { mutableIntStateOf(0) }
    var processing by remember { mutableStateOf(false) }

    LaunchedEffect(selected) {
        if (selected == applied) return@LaunchedEffect
        processing = true
        delay(550)
        applied = selected
        processing = false
    }

    val filter = PhotoFilters[applied]
    val matrix = remember(filter.saturation) {
        ColorMatrix().apply { setToSaturation(filter.saturation) }
    }

    LabCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, palette.border, RoundedCornerShape(14.dp))
                .semantics { contentDescription = "Pratinjau foto dengan filter ${filter.label}" },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val brush = Brush.linearGradient(
                    listOf(palette.primary, palette.info, palette.success),
                )
                drawRect(
                    brush = brush,
                    colorFilter = ColorFilter.colorMatrix(matrix),
                )
                // Objek mock
                drawCircle(Color.White.copy(alpha = 0.20f), radius = size.minDimension * 0.22f, center = Offset(size.width * 0.68f, size.height * 0.34f))
                val hill = Path().apply {
                    moveTo(0f, size.height)
                    lineTo(size.width * 0.35f, size.height * 0.45f)
                    lineTo(size.width * 0.62f, size.height * 0.78f)
                    lineTo(size.width, size.height * 0.5f)
                    lineTo(size.width, size.height)
                    close()
                }
                drawPath(hill, Color.White.copy(alpha = 0.18f))
                filter.overlay?.let { overlay ->
                    drawRect(color = overlay)
                }
            }
            if (processing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(palette.surface.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Memproses...", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PhotoFilters.forEachIndexed { index, item ->
                val isSelected = index == selected
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) palette.primary else palette.surface, RoundedCornerShape(20.dp))
                        .border(1.dp, if (isSelected) palette.primary else palette.border, RoundedCornerShape(20.dp))
                        .clickable { selected = index }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) palette.onPrimary else palette.textSecondary,
                        maxLines = 1,
                    )
                }
            }
        }

        Text(
            text = "Filter aktif: ${filter.label} · saturasi ${(filter.saturation * 100).toInt()}%",
            fontSize = 11.sp,
            color = palette.textMuted,
        )
    }
}

// ---------- 2. QR Generator ----------

@Composable
private fun QrCard() {
    val palette = LocalAppPalette.current
    var text by remember { mutableStateOf("https://contoh.id/undangan") }
    val sizes = listOf(21, 25, 29)
    var sizeIndex by remember { mutableIntStateOf(0) }
    val levels = remember { listOf("L", "M", "Q", "H") }
    var levelIndex by remember { mutableIntStateOf(1) }

    val moduleCount = sizes[sizeIndex]
    val seed = remember(text, levelIndex) { seedOf(text) xor (levelIndex * 31) }

    LabCard {
        FlatInput(
            value = text,
            onValueChange = { text = it },
            placeholder = "Masukkan teks atau tautan",
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            sizes.forEachIndexed { index, n ->
                val isSelected = index == sizeIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) palette.primary else palette.surface, RoundedCornerShape(10.dp))
                        .border(1.dp, if (isSelected) palette.primary else palette.border, RoundedCornerShape(10.dp))
                        .clickable { sizeIndex = index }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$n modul",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) palette.onPrimary else palette.textSecondary,
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Koreksi galat", fontSize = 12.sp, color = palette.textSecondary)
            levels.forEachIndexed { index, level ->
                val isSelected = index == levelIndex
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) palette.primaryContainer else palette.surface, RoundedCornerShape(8.dp))
                        .border(1.dp, if (isSelected) palette.primary else palette.border, RoundedCornerShape(8.dp))
                        .clickable { levelIndex = index }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = level,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) palette.onPrimaryContainer else palette.textSecondary,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Pratinjau QR simulasi berisi $moduleCount kali $moduleCount modul" },
            contentAlignment = Alignment.Center,
        ) {
            val n = moduleCount
            Canvas(modifier = Modifier.size(220.dp)) {
                val module = size.minDimension / n
                // Latar terang agar kontras modul
                val moduleColor = palette.textPrimary
                for (y in 0 until n) {
                    for (x in 0 until n) {
                        if (qrModule(seed, x, y, n)) {
                            drawRect(
                                color = moduleColor,
                                topLeft = Offset(x * module, y * module),
                                size = Size(module + 0.5f, module + 0.5f),
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.surfaceMuted, RoundedCornerShape(10.dp))
                .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                .padding(10.dp),
        ) {
            Text(
                text = "Simulasi — pola ini bukan QR valid dan tidak dapat dipindai. Hanya untuk menguji tata letak.",
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = palette.textMuted,
            )
        }
    }
}

private fun seedOf(text: String): Int {
    var hash = -2128831035 // 0x811C9DC5
    for (ch in text) {
        hash = hash xor ch.code
        hash *= 16777619
    }
    return hash
}

private fun qrModule(seed: Int, x: Int, y: Int, n: Int): Boolean {
    val inTopLeft = x in 0..6 && y in 0..6
    val inTopRight = x in (n - 7)..(n - 1) && y in 0..6
    val inBottomLeft = x in 0..6 && y in (n - 7)..(n - 1)
    if (inTopLeft) return finderValue(x, y)
    if (inTopRight) return finderValue(x - (n - 7), y)
    if (inBottomLeft) return finderValue(x, y - (n - 7))
    // Pola waktu
    if (y == 6) return x % 2 == 0
    if (x == 6) return y % 2 == 0
    var v = seed xor (x * 73856093) xor (y * 19349663)
    v = v xor (v ushr 13)
    v *= 1274126177
    return (v and 1) == 1
}

private fun finderValue(lx: Int, ly: Int): Boolean {
    if (lx == 0 || lx == 6 || ly == 0 || ly == 6) return true
    if (lx in 2..4 && ly in 2..4) return true
    return false
}

// ---------- 3. Perekam Suara ----------

@Composable
private fun RecorderCard() {
    val palette = LocalAppPalette.current
    var recording by remember { mutableStateOf(false) }
    var elapsed by remember { mutableIntStateOf(0) }
    var recordings by remember { mutableStateOf(listOf<Recording>()) }
    var playingId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(recording) {
        while (recording) {
            delay(1000)
            elapsed += 1
        }
    }

    val wave = rememberInfiniteTransition(label = "wave")
    val phase by wave.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "wave_phase",
    )

    LabCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                .semantics { contentDescription = if (recording) "Sedang merekam" else "Waveform diam" },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val bars = 34
                val slot = size.width / bars
                val barWidth = slot * 0.5f
                for (i in 0 until bars) {
                    val fraction = if (recording) {
                        0.2f + 0.7f * abs(sin(i * 0.55f + phase * 6.2832f))
                    } else {
                        0.14f + 0.05f * (i % 3)
                    }
                    val barHeight = size.height * fraction
                    drawRoundRect(
                        color = if (recording) palette.primary else palette.textMuted,
                        topLeft = Offset(i * slot + (slot - barWidth) / 2f, (size.height - barHeight) / 2f),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2f, barWidth / 2f),
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (recording) "Merekam" else "Siap merekam",
                modifier = Modifier.weight(1f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (recording) palette.danger else palette.textPrimary,
            )
            Text(formatDuration(elapsed), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Surface(
                onClick = {
                    if (recording) {
                        recording = false
                        if (elapsed > 0) {
                            recordings = recordings + Recording(
                                id = "rec_${recordings.size + 1}",
                                name = "Rekaman ${recordings.size + 1}",
                                duration = elapsed,
                            )
                        }
                        elapsed = 0
                    } else {
                        recording = true
                        elapsed = 0
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                color = if (recording) palette.danger else palette.primary,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Box(modifier = Modifier.padding(vertical = 13.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (recording) "Berhenti" else "Mulai Rekam",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.onPrimary,
                    )
                }
            }
        }

        Text("Rekaman tersimpan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
        if (recordings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .padding(14.dp),
            ) {
                Text("Belum ada rekaman. Tekan Mulai Rekam untuk membuat rekaman tiruan.", fontSize = 12.sp, color = palette.textMuted)
            }
        } else {
            recordings.forEach { rec ->
                val playing = playingId == rec.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (playing) palette.primaryContainer else palette.surface, RoundedCornerShape(12.dp))
                        .border(1.dp, if (playing) palette.primary else palette.border, RoundedCornerShape(12.dp))
                        .clickable { playingId = if (playing) null else rec.id }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(palette.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("▶", fontSize = 12.sp, color = palette.onPrimaryContainer)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = rec.name,
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (playing) palette.onPrimaryContainer else palette.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = if (playing) "Memutar..." else formatDuration(rec.duration),
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                }
            }
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return (if (m < 10) "0$m" else "$m") + ":" + (if (s < 10) "0$s" else "$s")
}

// ---------- 4. Editor Anotasi ----------

@Composable
private fun AnnotationCard() {
    val palette = LocalAppPalette.current
    val stickerColors = remember(palette) {
        listOf(palette.primary, palette.info, palette.success, palette.warning, palette.danger)
    }
    var stickers by remember {
        mutableStateOf(
            listOf(
                Sticker(1, 40f, 30f, 0, true),
                Sticker(2, 140f, 70f, 1, false),
            ),
        )
    }
    var nextId by remember { mutableIntStateOf(3) }

    LabCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(listOf(palette.surfaceMuted, palette.border)),
                    RoundedCornerShape(14.dp),
                )
                .border(1.dp, palette.border, RoundedCornerShape(14.dp))
                .semantics { contentDescription = "Kanvas anotasi dengan ${stickers.size} stiker" },
        ) {
            stickers.forEach { sticker ->
                Box(
                    modifier = Modifier
                        .offset { IntOffset(sticker.x.toInt(), sticker.y.toInt()) }
                        .size(46.dp)
                        .pointerInput(sticker.id) {
                            detectDragGestures { change, drag ->
                                change.consume()
                                stickers = stickers.map { s ->
                                    if (s.id == sticker.id) s.copy(x = s.x + drag.x, y = s.y + drag.y) else s
                                }
                            }
                        },
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val color = stickerColors[sticker.colorIndex % stickerColors.size]
                        if (sticker.round) {
                            drawCircle(color = color, radius = size.minDimension * 0.42f)
                            drawCircle(color = Color.White.copy(alpha = 0.7f), radius = size.minDimension * 0.16f)
                        } else {
                            drawRect(
                                color = color,
                                topLeft = Offset(size.width * 0.1f, size.height * 0.1f),
                                size = Size(size.width * 0.8f, size.height * 0.8f),
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "Stiker aktif: ${stickers.size}. Geser stiker langsung di kanvas.",
            fontSize = 11.sp,
            color = palette.textMuted,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Surface(
                onClick = {
                    stickers = stickers + Sticker(
                        id = nextId,
                        x = Random.nextInt(20, 200).toFloat(),
                        y = Random.nextInt(20, 140).toFloat(),
                        colorIndex = nextId % stickerColors.size,
                        round = nextId % 2 == 0,
                    )
                    nextId += 1
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                color = palette.primary,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Box(modifier = Modifier.padding(vertical = 13.dp), contentAlignment = Alignment.Center) {
                    Text("Tambah Stiker", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
                }
            }
            Surface(
                onClick = { stickers = emptyList() },
                enabled = stickers.isNotEmpty(),
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 13.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Bersihkan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (stickers.isNotEmpty()) palette.textSecondary else palette.textMuted,
                    )
                }
            }
        }
    }
}

// ---------- Input Flat UI ----------

@Composable
private fun FlatInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val palette: AppPalette = LocalAppPalette.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surface, RoundedCornerShape(12.dp))
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (value.isEmpty()) {
            Text(placeholder, fontSize = 13.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(color = palette.textPrimary, fontSize = 13.sp),
            cursorBrush = SolidColor(palette.primary),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
