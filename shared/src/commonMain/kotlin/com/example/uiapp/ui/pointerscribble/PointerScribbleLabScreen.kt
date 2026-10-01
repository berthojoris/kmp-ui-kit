package com.example.uiapp.ui.pointerscribble

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
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
import com.example.uiapp.ui.components.FlatTextField
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private enum class ModeAlat(val label: String) {
    TRACKPAD("Trackpad"),
    PENCIL("Apple Pencil"),
}

private enum class GayaPointer(val label: String) {
    TITIK("Titik"),
    LINGKARAN("Lingkaran"),
    PANAH("Panah"),
    PRESISI("Presisi"),
}

private enum class AlatPencil(val label: String) {
    PEN("Pena"),
    MARKER("Stabilo"),
    PENGHAPUS("Penghapus"),
}

private enum class StatusKanvas(val label: String) {
    SIAP("Kanvas siap"),
    KALIBRASI("Mengkalibrasi kursor…"),
    GAGAL("Trackpad gagal"),
}

private data class TargetKlik(
    val id: String,
    val label: String,
    val cx: Float,
    val cy: Float,
    val tersedia: Boolean,
)

private val TargetMock: List<TargetKlik> = listOf(
    TargetKlik("bagikan", "Bagikan", 0.28f, 0.28f, tersedia = true),
    TargetKlik("simpan", "Simpan", 0.72f, 0.24f, tersedia = true),
    TargetKlik("hapus", "Hapus", 0.50f, 0.66f, tersedia = true),
    TargetKlik("cetak", "Cetak", 0.18f, 0.84f, tersedia = false),
)

private data class Coretan(
    val id: Int,
    val alat: AlatPencil,
    val titik: List<Offset>,
)

private val KataScribble: List<String> = listOf(
    "villa", "cendana", "nyaman", "liburan", "keluarga",
    "kolam", "pegunungan", "sarapan", "damai", "senja",
)

private const val ParagrafPilihan = "Villa Cendana nyaman untuk liburan keluarga besar"

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun PointerScribbleLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val density = LocalDensity.current

    var modeAlat by remember { mutableStateOf(ModeAlat.TRACKPAD) }
    var gayaPointer by remember { mutableStateOf(GayaPointer.TITIK) }
    var kanvasStatus by remember { mutableStateOf(StatusKanvas.SIAP) }
    var kalibrasiSeq by remember { mutableIntStateOf(0) }
    var galatKanvas by remember { mutableStateOf(false) }
    var tampilkanTarget by remember { mutableStateOf(true) }
    var pointerPos by remember { mutableStateOf(Offset(140f, 96f)) }
    var hoverId by remember { mutableStateOf<String?>(null) }
    var menuBuka by remember { mutableStateOf(false) }
    var alatAktif by remember { mutableStateOf(AlatPencil.PEN) }
    var idBerikutnya by remember { mutableIntStateOf(1) }
    var teksScribble by remember { mutableStateOf("") }
    var pilihAwal by remember { mutableIntStateOf(-1) }
    var pilihAkhir by remember { mutableIntStateOf(-1) }
    var aksiLog by remember { mutableStateOf("Gerakkan kursor dengan drag pada kanvas, lalu ketuk untuk mengklik target.") }

    val coretan = remember { mutableStateListOf<Coretan>() }
    val jalurCoretan = remember { Path() }
    val kataParagraf = remember { ParagrafPilihan.split(" ") }

    // Ukuran kanvas px dari BoxWithConstraints — dipakai saat kursor dipindahkan di luar scope kanvas.
    var lebarKanvasPx by remember { mutableStateOf(0f) }
    var tinggiKanvasPx by remember { mutableStateOf(0f) }

    // Kalibrasi kursor: state memuat → siap, atau gagal bila galat disimulasikan.
    LaunchedEffect(kalibrasiSeq) {
        if (kalibrasiSeq > 0) {
            kanvasStatus = StatusKanvas.KALIBRASI
            delay(900)
            kanvasStatus = if (galatKanvas) StatusKanvas.GAGAL else StatusKanvas.SIAP
        }
    }

    fun targetPada(pos: Offset, lebarPx: Float, tinggiPx: Float): TargetKlik? {
        if (!tampilkanTarget) return null
        var terdekat: TargetKlik? = null
        var jarakTerbaik = 70f * density.density
        TargetMock.forEach { target ->
            val tx = target.cx * lebarPx
            val ty = target.cy * tinggiPx
            val dx = pos.x - tx
            val dy = pos.y - ty
            val jarak2 = dx * dx + dy * dy
            if (jarak2 < jarakTerbaik * jarakTerbaik) {
                jarakTerbaik = kotlin.math.sqrt(jarak2)
                terdekat = target
            }
        }
        return terdekat
    }

    fun klikTargetAktif() {
        val target = TargetMock.firstOrNull { it.id == hoverId }
        aksiLog = when {
            target == null -> "Tidak ada target di bawah kursor. Dekatkan kursor ke sebuah tombol."
            !target.tersedia -> "\"${target.label}\" tidak tersedia (disabled) — klik diabaikan."
            else -> "\"${target.label}\" diklik lewat kursor trackpad (Simulasi klik)."
        }
    }

    fun gantiAlat() {
        alatAktif = when (alatAktif) {
            AlatPencil.PEN -> AlatPencil.MARKER
            AlatPencil.MARKER -> AlatPencil.PENGHAPUS
            AlatPencil.PENGHAPUS -> AlatPencil.PEN
        }
        aksiLog = "Double-tap: alat berubah menjadi ${alatAktif.label} (Simulasi Pencil)."
    }

    fun kenaliTulisan() {
        val kata = KataScribble[(coretan.size * 3 + teksScribble.length) % KataScribble.size]
        teksScribble = if (teksScribble.isEmpty()) kata else "$teksScribble $kata"
        aksiLog = "Scribble mengenali coretan sebagai \"$kata\" (perkiraan, Simulasi)."
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "iPad Pointer & Scribble",
                subtitle = "Kursor trackpad & coretan Pencil",
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
                ) {
                    FlatSecondaryButton(
                        text = "Bersihkan Coretan",
                        onClick = {
                            coretan.clear()
                            aksiLog = "Semua coretan Pencil dibersihkan (Simulasi)."
                        },
                        enabled = coretan.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                    )
                    FlatPrimaryButton(
                        text = "Kirim Teks",
                        onClick = {
                            aksiLog = "Teks \"$teksScribble\" dikirim dari field Scribble (Simulasi)."
                            teksScribble = ""
                            coretan.clear()
                        },
                        enabled = teksScribble.isNotBlank(),
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
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LabSectionTitle(
                    title = "Pointer & Coretan",
                    subtitle = "Kursor ala iPadOS dengan target klik adaptif",
                    modifier = Modifier.weight(1f),
                )
                SimulasiPill()
            }

            LabIntroCard(
                title = "Simulasi murni Compose Canvas",
                body = "Kursor, target klik, dan coretan Pencil digambar dengan Canvas — tanpa PencilKit, UIKit, " +
                    "atau akses perangkat keras. Pengenalan tulisan tangan hanya perkiraan dari data mock.",
            )

            // ===== Kursor trackpad =====
            LabSectionTitle(
                title = "Kursor Trackpad & Click Target",
                subtitle = "Target otomatis membesar dan berkontras saat kursor mendekat",
            )

            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ModeAlat.entries.forEach { mode ->
                        FlatChip(
                            label = mode.label,
                            selected = modeAlat == mode,
                            onClick = {
                                modeAlat = mode
                                aksiLog = "Mode alat: ${mode.label}."
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = kanvasStatus.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textSecondary,
                        modifier = Modifier.weight(1f),
                    )
                    FlatChip(
                        label = "Kalibrasi Ulang",
                        selected = false,
                        onClick = {
                            galatKanvas = false
                            kalibrasiSeq++
                        },
                        modifier = Modifier.width(130.dp),
                    )
                }

                when (kanvasStatus) {
                    StatusKanvas.KALIBRASI -> {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Mengkalibrasi kursor dan target klik…",
                                fontSize = 12.sp,
                                color = palette.textMuted,
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(palette.surfaceMuted)
                                    .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
                            )
                        }
                    }
                    StatusKanvas.GAGAL -> {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                    text = "Perangkat penunjuk gagal dipasangkan (Simulasi galat trackpad).",
                                    fontSize = 12.sp,
                                    color = palette.textPrimary,
                                    modifier = Modifier.padding(12.dp),
                                )
                            }
                            FlatSecondaryButton(
                                text = "Coba Lagi",
                                onClick = {
                                    galatKanvas = false
                                    kalibrasiSeq++
                                },
                            )
                        }
                    }
                    StatusKanvas.SIAP -> {
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp),
                        ) {
                            val lebarPx = with(density) { maxWidth.toPx() }
                            val tinggiPx = with(density) { maxHeight.toPx() }
                            val lebarKanvas = maxWidth
                            val tinggiKanvas = maxHeight
                            lebarKanvasPx = lebarPx
                            tinggiKanvasPx = tinggiPx
                            val menuX = with(density) { pointerPos.x.toDp() }.coerceAtMost(maxWidth - 180.dp)
                            val menuY = with(density) { pointerPos.y.toDp() }.coerceAtMost(maxHeight - 170.dp)

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(palette.surfaceMuted)
                                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                                    .pointerInput(tampilkanTarget) {
                                        detectDragGestures(
                                            onDrag = { change, _ ->
                                                pointerPos = change.position
                                                hoverId = targetPada(change.position, lebarPx, tinggiPx)?.id
                                            },
                                        )
                                    }
                                    .pointerInput(tampilkanTarget) {
                                        detectTapGestures(
                                            onTap = { pos ->
                                                pointerPos = pos
                                                hoverId = targetPada(pos, lebarPx, tinggiPx)?.id
                                                klikTargetAktif()
                                            },
                                            onLongPress = {
                                                menuBuka = true
                                                aksiLog = "Menu konteks dibuka (Simulasi klik kanan / tekan lama)."
                                            },
                                        )
                                    }
                                    .semantics {
                                        role = Role.Button
                                        contentDescription =
                                            "Kanvas kursor. Drag untuk menggerakkan kursor, ketuk untuk mengklik, tekan lama untuk menu konteks."
                                    },
                            ) {
                                if (!tampilkanTarget) {
                                    Text(
                                        text = "Tidak ada target klik di kanvas",
                                        fontSize = 12.sp,
                                        color = palette.textMuted,
                                        modifier = Modifier.align(Alignment.Center),
                                    )
                                } else {
                                    TargetMock.forEach { target ->
                                        val dihover = hoverId == target.id
                                        val skala by animateFloatAsState(
                                            targetValue = if (dihover) 1.10f else 1f,
                                            animationSpec = tween(140),
                                            label = "skalaTarget",
                                        )
                                        Surface(
                                            onClick = {
                                                pointerPos = Offset(target.cx * lebarPx, target.cy * tinggiPx)
                                                hoverId = target.id
                                                if (target.tersedia) {
                                                    aksiLog = "\"${target.label}\" diklik (Simulasi)."
                                                } else {
                                                    aksiLog = "\"${target.label}\" tidak tersedia (disabled)."
                                                }
                                            },
                                            enabled = target.tersedia,
                                            shape = RoundedCornerShape(10.dp),
                                            color = when {
                                                !target.tersedia -> palette.background
                                                dihover -> palette.primary
                                                else -> palette.surface
                                            },
                                            contentColor = when {
                                                !target.tersedia -> palette.textMuted
                                                dihover -> palette.onPrimary
                                                else -> palette.textPrimary
                                            },
                                            shadowElevation = 0.dp,
                                            tonalElevation = 0.dp,
                                            border = BorderStroke(
                                                if (dihover) 2.dp else 1.dp,
                                                when {
                                                    !target.tersedia -> palette.border
                                                    dihover -> palette.primary
                                                    else -> palette.border
                                                },
                                            ),
                                            modifier = Modifier
                                                .offset(
                                                    x = lebarKanvas * target.cx - 45.dp,
                                                    y = tinggiKanvas * target.cy - 18.dp,
                                                )
                                                .size(width = 90.dp, height = 36.dp)
                                                .graphicsLayer {
                                                    scaleX = skala
                                                    scaleY = skala
                                                }
                                                .semantics {
                                                    role = Role.Button
                                                    contentDescription =
                                                        "Target ${target.label}, " +
                                                            if (target.tersedia) "tersedia" else "tidak tersedia"
                                                },
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Text(
                                                    text = target.label,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                        }
                                    }
                                }

                                // Kursor digambar penuh dengan Canvas.
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val c = pointerPos
                                    val membesar = hoverId != null
                                    when (gayaPointer) {
                                        GayaPointer.TITIK -> {
                                            drawCircle(
                                                color = palette.onPrimary,
                                                radius = if (membesar) 12.dp.toPx() else 8.dp.toPx(),
                                                center = c,
                                            )
                                            drawCircle(
                                                color = palette.primary,
                                                radius = if (membesar) 12.dp.toPx() else 8.dp.toPx(),
                                                center = c,
                                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
                                            )
                                        }
                                        GayaPointer.LINGKARAN -> {
                                            drawCircle(
                                                color = palette.primary,
                                                radius = if (membesar) 24.dp.toPx() else 18.dp.toPx(),
                                                center = c,
                                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
                                            )
                                            drawCircle(color = palette.primary, radius = 2.dp.toPx(), center = c)
                                        }
                                        GayaPointer.PANAH -> {
                                            drawLine(
                                                color = palette.primary,
                                                start = c,
                                                end = Offset(c.x + 18.dp.toPx(), c.y + 6.dp.toPx()),
                                                strokeWidth = 3.dp.toPx(),
                                                cap = StrokeCap.Round,
                                            )
                                            drawLine(
                                                color = palette.primary,
                                                start = c,
                                                end = Offset(c.x + 6.dp.toPx(), c.y + 18.dp.toPx()),
                                                strokeWidth = 3.dp.toPx(),
                                                cap = StrokeCap.Round,
                                            )
                                            drawLine(
                                                color = palette.primary,
                                                start = Offset(c.x + 18.dp.toPx(), c.y + 6.dp.toPx()),
                                                end = Offset(c.x + 6.dp.toPx(), c.y + 18.dp.toPx()),
                                                strokeWidth = 3.dp.toPx(),
                                                cap = StrokeCap.Round,
                                            )
                                        }
                                        GayaPointer.PRESISI -> {
                                            drawCircle(
                                                color = palette.primary,
                                                radius = 10.dp.toPx(),
                                                center = c,
                                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
                                            )
                                            drawLine(
                                                color = palette.primary,
                                                start = Offset(c.x - 16.dp.toPx(), c.y),
                                                end = Offset(c.x + 16.dp.toPx(), c.y),
                                                strokeWidth = 2.dp.toPx(),
                                                cap = StrokeCap.Round,
                                            )
                                            drawLine(
                                                color = palette.primary,
                                                start = Offset(c.x, c.y - 16.dp.toPx()),
                                                end = Offset(c.x, c.y + 16.dp.toPx()),
                                                strokeWidth = 2.dp.toPx(),
                                                cap = StrokeCap.Round,
                                            )
                                        }
                                    }
                                }

                                if (menuBuka) {
                                    BackHandler(enabled = true) { menuBuka = false }
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = palette.surface,
                                        contentColor = palette.textPrimary,
                                        shadowElevation = 0.dp,
                                        tonalElevation = 0.dp,
                                        border = BorderStroke(1.dp, palette.border),
                                        modifier = Modifier
                                            .offset(x = menuX.coerceAtLeast(0.dp), y = menuY.coerceAtLeast(0.dp))
                                            .width(176.dp),
                                    ) {
                                        Column {
                                            Text(
                                                text = "Menu Konteks",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = palette.textMuted,
                                                modifier = Modifier.padding(start = 12.dp, top = 10.dp, bottom = 4.dp),
                                            )
                                            listOf("Salin", "Pilih Semua", "Bagikan", "Gaya Teks").forEach { item ->
                                                Text(
                                                    text = item,
                                                    fontSize = 13.sp,
                                                    color = palette.textPrimary,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable(role = Role.Button, onClick = {
                                                            menuBuka = false
                                                            aksiLog = "Menu konteks \"$item\" dipilih (Simulasi klik kanan)."
                                                        })
                                                        .padding(horizontal = 12.dp, vertical = 9.dp),
                                                )
                                            }
                                            Text(
                                                text = "Tutup",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = palette.primary,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable(role = Role.Button, onClick = { menuBuka = false })
                                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Dock Gaya Pointer",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textSecondary,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            GayaPointer.entries.forEach { gaya ->
                                FlatChip(
                                    label = gaya.label,
                                    selected = gayaPointer == gaya,
                                    onClick = { gayaPointer = gaya },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            FlatSecondaryButton(
                                text = "Klik Target",
                                onClick = { klikTargetAktif() },
                                enabled = hoverId != null,
                                modifier = Modifier.weight(1f),
                            )
                            FlatSecondaryButton(
                                text = "Klik Kanan",
                                onClick = {
                                    menuBuka = true
                                    aksiLog = "Menu konteks dibuka lewat tombol klik kanan (Simulasi)."
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            FlatSecondaryButton(
                                text = "Pindah Target",
                                onClick = {
                                    val idxSekarang = TargetMock.indexOfFirst { it.id == hoverId }
                                    val berikut = TargetMock[(idxSekarang + 1) % TargetMock.size]
                                    hoverId = berikut.id
                                    pointerPos = Offset(berikut.cx * lebarKanvasPx, berikut.cy * tinggiKanvasPx)
                                    aksiLog = "Kursor dipindahkan ke target \"${berikut.label}\" (navigasi bantu)."
                                },
                                enabled = tampilkanTarget,
                                modifier = Modifier.weight(1f),
                            )
                            FlatSecondaryButton(
                                text = "Simulasi Galat Trackpad",
                                onClick = {
                                    galatKanvas = true
                                    kalibrasiSeq++
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }

                        ToggleBaris(
                            label = "Tampilkan Target Klik",
                            desc = if (tampilkanTarget) {
                                "Empat target mock tampil di kanvas."
                            } else {
                                "Kanvas kosong — state tanpa target klik."
                            },
                            checked = tampilkanTarget,
                            onCheckedChange = {
                                tampilkanTarget = it
                                hoverId = null
                            },
                        )
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

            // ===== Pencil & Scribble =====
            LabSectionTitle(
                title = "Apple Pencil & Scribble",
                subtitle = "Double-tap ganti alat, coretan jadi teks, geser pilih teks",
            )

            LabCard {
                if (modeAlat != ModeAlat.PENCIL) {
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
                            text = "Mode Apple Pencil nonaktif",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = "Alihkan ke mode Apple Pencil di atas untuk mencoba coretan, penghapus, dan Scribble.",
                            fontSize = 11.sp,
                            color = palette.textMuted,
                            textAlign = TextAlign.Center,
                        )
                        FlatSecondaryButton(
                            text = "Alihkan ke Apple Pencil",
                            onClick = { modeAlat = ModeAlat.PENCIL },
                        )
                    }
                } else {
                    Text(
                        text = "Dock Alat (double-tap untuk ganti)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textSecondary,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AlatPencil.entries.forEach { alat ->
                            FlatChip(
                                label = alat.label,
                                selected = alatAktif == alat,
                                onClick = { alatAktif = alat },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(palette.surfaceMuted)
                                .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                                .pointerInput(Unit) {
                                    detectTapGestures(onDoubleTap = { gantiAlat() })
                                }
                                .semantics {
                                    role = Role.Button
                                    contentDescription = "Area ketuk dua kali untuk mengganti alat Pencil"
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Ketuk 2× area ini: ganti alat",
                                fontSize = 11.sp,
                                color = palette.textSecondary,
                            )
                        }
                        FlatSecondaryButton(
                            text = "Double-Tap",
                            onClick = { gantiAlat() },
                            modifier = Modifier.width(120.dp),
                        )
                    }

                    Text(
                        text = "Kanvas Coretan — alat aktif: ${alatAktif.label}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textSecondary,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.surface)
                            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                            .pointerInput(alatAktif) {
                                detectDragGestures(
                                    onDragStart = { pos ->
                                        if (alatAktif == AlatPencil.PENGHAPUS) {
                                            coretan.removeAll { c -> c.titik.any { dekat(it, pos) } }
                                        } else {
                                            coretan.add(Coretan(idBerikutnya, alatAktif, listOf(pos)))
                                            idBerikutnya++
                                        }
                                    },
                                    onDrag = { change, _ ->
                                        if (alatAktif == AlatPencil.PENGHAPUS) {
                                            coretan.removeAll { c -> c.titik.any { dekat(it, change.position) } }
                                        } else {
                                            val idx = coretan.indexOfFirst { it.id == idBerikutnya - 1 }
                                            if (idx >= 0) {
                                                coretan[idx] = coretan[idx].copy(
                                                    titik = coretan[idx].titik + change.position,
                                                )
                                            }
                                        }
                                    },
                                    onDragEnd = {
                                        if (alatAktif != AlatPencil.PENGHAPUS) {
                                            kenaliTulisan()
                                        }
                                    },
                                )
                            }
                            .semantics {
                                role = Role.Button
                                contentDescription =
                                    "Kanvas coretan Pencil. Drag untuk menggambar dengan ${alatAktif.label}."
                            },
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            coretan.forEach { c ->
                                val warna = when (c.alat) {
                                    AlatPencil.PEN -> palette.textPrimary
                                    AlatPencil.MARKER -> palette.primary.copy(alpha = 0.45f)
                                    AlatPencil.PENGHAPUS -> palette.textMuted
                                }
                                val tebal = when (c.alat) {
                                    AlatPencil.PEN -> 3.dp.toPx()
                                    AlatPencil.MARKER -> 9.dp.toPx()
                                    AlatPencil.PENGHAPUS -> 2.dp.toPx()
                                }
                                if (c.titik.size > 1) {
                                    jalurCoretan.reset()
                                    jalurCoretan.moveTo(c.titik[0].x, c.titik[0].y)
                                    for (i in 1 until c.titik.size) {
                                        jalurCoretan.lineTo(c.titik[i].x, c.titik[i].y)
                                    }
                                    drawPath(
                                        path = jalurCoretan,
                                        color = warna,
                                        style = Stroke(width = tebal, cap = StrokeCap.Round, join = StrokeJoin.Round),
                                    )
                                } else if (c.titik.isNotEmpty()) {
                                    drawCircle(color = warna, radius = tebal / 2f, center = c.titik[0])
                                }
                            }
                            if (coretan.isEmpty()) {
                                drawLine(
                                    color = palette.border,
                                    start = Offset(24.dp.toPx(), size.height - 24.dp.toPx()),
                                    end = Offset(size.width - 24.dp.toPx(), size.height - 24.dp.toPx()),
                                    strokeWidth = 1.dp.toPx(),
                                    cap = StrokeCap.Round,
                                )
                            }
                        }
                        if (coretan.isEmpty()) {
                            Text(
                                text = "Coret dengan drag untuk mencoba Scribble",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }

                    Text(
                        text = "Jumlah coretan: ${coretan.size} • Perkiraan tulisan tangan: Simulasi",
                        fontSize = 10.sp,
                        color = palette.textMuted,
                    )

                    FlatTextField(
                        label = "Hasil Scribble (teks diperluas dari tulisan tangan)",
                        value = teksScribble,
                        onValueChange = { teksScribble = it },
                        placeholder = "Kata hasil coretan muncul di sini…",
                        singleLine = true,
                    )
                }
            }

            // ===== Pilih teks =====
            LabSectionTitle(
                title = "Pilih Teks dengan Geser",
                subtitle = "Geser mendatar untuk memperluas pilihan kata",
            )

            LabCard {
                val sedangMemilih = pilihAwal in kataParagraf.indices && pilihAkhir in kataParagraf.indices

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                        .pointerInput(pilihAwal, pilihAkhir) {
                            var akum = 0f
                            detectDragGestures(
                                onDragStart = {
                                    if (pilihAwal !in kataParagraf.indices) {
                                        pilihAwal = 0
                                        pilihAkhir = 0
                                    }
                                    akum = 0f
                                },
                                onDragEnd = {
                                    aksiLog = if (sedangMemilih) {
                                        "Pilihan teks: \"${teksTerpilih(kataParagraf, pilihAwal, pilihAkhir)}\" (Simulasi)."
                                    } else {
                                        aksiLog
                                    }
                                },
                            ) { _, dragAmount ->
                                akum += dragAmount.x
                                if (akum > 42f) {
                                    akum = 0f
                                    pilihAkhir = (pilihAkhir + 1).coerceAtMost(kataParagraf.size - 1)
                                } else if (akum < -42f) {
                                    akum = 0f
                                    pilihAkhir = (pilihAkhir - 1).coerceAtLeast(pilihAwal)
                                }
                            }
                        }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    kataParagraf.forEachIndexed { idx, kata ->
                        val terpilih = sedangMemilih && idx in pilihAwal..pilihAkhir
                        Text(
                            text = kata,
                            fontSize = 14.sp,
                            fontWeight = if (terpilih) FontWeight.Bold else FontWeight.Normal,
                            color = if (terpilih) palette.onPrimaryContainer else palette.textPrimary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (terpilih) palette.primaryContainer else Color.Transparent)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .semantics {
                                    contentDescription =
                                        "Kata $kata${if (terpilih) ", terpilih" else ""}"
                                },
                        )
                    }
                }

                if (sedangMemilih) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        listOf("Salin", "Gaya", "Bagikan").forEach { aksi ->
                            FlatChip(
                                label = aksi,
                                selected = false,
                                onClick = {
                                    aksiLog =
                                        "\"$aksi\" pada pilihan \"${teksTerpilih(kataParagraf, pilihAwal, pilihAkhir)}\" (Simulasi)."
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FlatSecondaryButton(
                        text = "Perluas Pilihan",
                        onClick = {
                            if (pilihAwal !in kataParagraf.indices) {
                                pilihAwal = 0
                                pilihAkhir = 0
                            } else {
                                pilihAkhir = (pilihAkhir + 1).coerceAtMost(kataParagraf.size - 1)
                            }
                        },
                        enabled = pilihAkhir < kataParagraf.size - 1 || pilihAwal !in kataParagraf.indices,
                        modifier = Modifier.weight(1f),
                    )
                    FlatSecondaryButton(
                        text = "Kecilkan Pilihan",
                        onClick = { pilihAkhir = (pilihAkhir - 1).coerceAtLeast(pilihAwal) },
                        enabled = sedangMemilih && pilihAkhir > pilihAwal,
                        modifier = Modifier.weight(1f),
                    )
                    FlatSecondaryButton(
                        text = "Hapus Pilihan",
                        onClick = {
                            pilihAwal = -1
                            pilihAkhir = -1
                            aksiLog = "Pilihan teks dibersihkan."
                        },
                        enabled = sedangMemilih,
                        modifier = Modifier.weight(1f),
                    )
                }

                Text(
                    text = if (sedangMemilih) {
                        "Terpilih ${pilihAkhir - pilihAwal + 1} kata: \"${teksTerpilih(kataParagraf, pilihAwal, pilihAkhir)}\""
                    } else {
                        "Belum ada kata terpilih. Geser mendatar di atas paragraf atau gunakan tombol."
                    },
                    fontSize = 11.sp,
                    color = palette.textSecondary,
                    lineHeight = 16.sp,
                )

                Text(
                    text = "Semua interaksi pointer, coretan, dan menu di lab ini digambar dengan Compose Canvas.",
                    fontSize = 10.sp,
                    color = palette.textMuted,
                    lineHeight = 14.sp,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun dekat(a: Offset, b: Offset, ambang: Float = 36f): Boolean {
    val dx = a.x - b.x
    val dy = a.y - b.y
    return dx * dx + dy * dy < ambang * ambang
}

private fun teksTerpilih(kata: List<String>, awal: Int, akhir: Int): String {
    if (awal !in kata.indices || akhir !in kata.indices || akhir < awal) return ""
    return kata.subList(awal, akhir + 1).joinToString(" ")
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
private fun FlatChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
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
                .padding(vertical = 8.dp, horizontal = 4.dp),
        )
    }
}

@Composable
private fun ToggleBaris(label: String, desc: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val palette = LocalAppPalette.current
    val thumbOffset by animateFloatAsState(
        targetValue = if (checked) 18f else 2f,
        animationSpec = tween(180),
        label = "toggleThumb",
    )
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp,
            )
        }
        Text(
            text = if (checked) "AKTIF" else "MATI",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (checked) palette.primary else palette.textMuted,
        )
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 26.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(if (checked) palette.primary else palette.background)
                .border(1.dp, if (checked) palette.primary else palette.border, RoundedCornerShape(13.dp))
                .padding(3.dp)
                .semantics {
                    role = Role.Button
                    contentDescription = "$label, ${if (checked) "aktif" else "nonaktif"}"
                },
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (checked) palette.onPrimary else palette.textMuted),
            )
        }
    }
}
