package com.example.uiapp.ui.controlcenter

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.launch

private const val GRID_COLS = 4
private const val GRID_ROWS = 3

private data class ControlTile(
    val id: String,
    val name: String,
    val glyph: String,
    val large: Boolean,
)

private data class PlacedTile(
    val item: ControlTile,
    val col: Int,
    val row: Int,
)

/** Metrik grid dalam pixel untuk perhitungan drag & drop. */
private data class GridMetrics(
    val cellW: Float,
    val cellH: Float,
    val gap: Float,
    val pageStride: Float,
) {
    fun tileOrigin(pageIndex: Int, col: Int, row: Int): Offset = Offset(
        pageIndex * pageStride + col * (cellW + gap),
        row * (cellH + gap),
    )

    fun tileWidth(spanW: Int): Float = spanW * cellW + (spanW - 1) * gap

    fun tileHeight(spanH: Int): Float = spanH * cellH + (spanH - 1) * gap
}

private data class DragInfo(
    val item: ControlTile,
    val pageIndex: Int,
    val translation: Offset,
    val pointerContent: Offset,
)

private fun packPage(items: List<ControlTile>): Pair<List<PlacedTile>, List<ControlTile>> {
    val occupancy = Array(GRID_ROWS) { BooleanArray(GRID_COLS) }
    val placed = mutableListOf<PlacedTile>()
    val overflow = mutableListOf<ControlTile>()
    for (item in items) {
        val spanW = 2
        val spanH = if (item.large) 2 else 1
        var done = false
        for (r in 0..GRID_ROWS - spanH) {
            for (c in 0..GRID_COLS - spanW) {
                val free = (r until r + spanH).all { rr -> (c until c + spanW).all { cc -> !occupancy[rr][cc] } }
                if (free) {
                    (r until r + spanH).forEach { rr -> (c until c + spanW).forEach { cc -> occupancy[rr][cc] = true } }
                    placed += PlacedTile(item, c, r)
                    done = true
                    break
                }
            }
            if (done) break
        }
        if (!done) overflow += item
    }
    return placed to overflow
}

private val controlGallery = listOf(
    ControlTile("wifi", "Wi-Fi", "wifi", large = true),
    ControlTile("musik", "Musik", "musik", large = true),
    ControlTile("fokus", "Mode Fokus", "fokus", large = true),
    ControlTile("bluetooth", "Bluetooth", "bluetooth", large = false),
    ControlTile("kecerahan", "Kecerahan", "kecerahan", large = false),
    ControlTile("timer", "Timer", "timer", large = false),
    ControlTile("senter", "Lampu Senter", "senter", large = false),
    ControlTile("rotasi", "Kunci Rotasi", "rotasi", large = false),
    ControlTile("alarm", "Alarm", "alarm", large = false),
    ControlTile("kalkulator", "Kalkulator", "kalkulator", large = false),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ControlCenterLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    val pageOne = remember {
        mutableStateListOf(
            ControlTile("wifi", "Wi-Fi", "wifi", large = true),
            ControlTile("musik", "Musik", "musik", large = true),
            ControlTile("bluetooth", "Bluetooth", "bluetooth", large = false),
        )
    }
    val pageTwo = remember {
        mutableStateListOf(
            ControlTile("timer", "Timer", "timer", large = false),
            ControlTile("senter", "Lampu Senter", "senter", large = false),
            ControlTile("alarm", "Alarm", "alarm", large = false),
            ControlTile("rotasi", "Kunci Rotasi", "rotasi", large = false),
        )
    }
    val activeStates = remember { mutableStateMapOf("wifi" to true, "bluetooth" to false, "musik" to true) }

    var editMode by remember { mutableStateOf(false) }
    var galleryOpen by remember { mutableStateOf(false) }
    var selectedPage by remember { mutableStateOf(0) }
    var dragInfo by remember { mutableStateOf<DragInfo?>(null) }
    var statusNote by remember { mutableStateOf("Tekan lama salah satu kontrol untuk mode edit") }

    val pageScroll = rememberScrollState()
    val scope = rememberCoroutineScope()

    val packedOne by remember { derivedStateOf { packPage(pageOne) } }
    val packedTwo by remember { derivedStateOf { packPage(pageTwo) } }

    fun pageList(index: Int) = if (index == 0) pageOne else pageTwo

    fun placedList(index: Int) = if (index == 0) packedOne.first else packedTwo.first

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Control Center & Edit Mode",
                subtitle = "Grid kontrol, jiggle, drag & resize — Simulasi",
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
                color = palette.warning.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, palette.warning),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Simulasi", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    Text(
                        "Tiruan Control Center dan mode edit layar utama. Tidak ada pengaturan sistem yang diubah.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = palette.textSecondary,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val gridGap = 8.dp
                val cellW = (maxWidth - gridGap * (GRID_COLS - 1)) / GRID_COLS
                val cellH = 74.dp
                val gridHeight = cellH * GRID_ROWS + gridGap * (GRID_ROWS - 1)
                val pageGap = 14.dp
                val pageWidth = maxWidth
                val density = LocalDensity.current
                val metrics = remember(cellW, cellH, gridGap, pageGap, density) {
                    with(density) {
                        GridMetrics(
                            cellW = cellW.toPx(),
                            cellH = cellH.toPx(),
                            gap = gridGap.toPx(),
                            pageStride = (pageWidth + pageGap).toPx(),
                        )
                    }
                }

                val currentPage by remember(metrics) {
                    derivedStateOf {
                        if (editMode) selectedPage else floor(pageScroll.value / metrics.pageStride).toInt().coerceIn(0, 1)
                    }
                }

                fun hitIndex(pageIndex: Int, point: Offset): Int {
                    val localX = point.x - pageIndex * metrics.pageStride
                    val localY = point.y
                    return placedList(pageIndex).indexOfFirst { placed ->
                        val x0 = placed.col * (metrics.cellW + metrics.gap)
                        val y0 = placed.row * (metrics.cellH + metrics.gap)
                        val w = metrics.tileWidth(2)
                        val h = metrics.tileHeight(if (placed.item.large) 2 else 1)
                        localX >= x0 && localX <= x0 + w && localY >= y0 && localY <= y0 + h
                    }
                }

                fun onTileTap(item: ControlTile) {
                    if (editMode) return
                    activeStates[item.id] = !(activeStates[item.id] ?: false)
                    statusNote = "${item.name} kini ${if (activeStates[item.id] == true) "aktif" else "nonaktif"} (Simulasi)"
                }

                fun onTileLongPress() {
                    editMode = true
                    galleryOpen = false
                    statusNote = "Mode edit aktif: ikon bergetar (jiggle), seret untuk menyusun, − untuk menghapus"
                }

                fun onTileDragStart(item: ControlTile, pageIndex: Int, col: Int, row: Int, localPos: Offset) {
                    val origin = metrics.tileOrigin(pageIndex, col, row)
                    dragInfo = DragInfo(
                        item = item,
                        pageIndex = pageIndex,
                        translation = Offset.Zero,
                        pointerContent = origin + localPos,
                    )
                    statusNote = "Menyeret ${item.name} — lepaskan di halaman lain untuk memindahkan"
                }

                fun onTileDrag(dragAmount: Offset) {
                    val current = dragInfo ?: return
                    val nextPointer = current.pointerContent + dragAmount
                    dragInfo = current.copy(
                        translation = current.translation + dragAmount,
                        pointerContent = nextPointer,
                    )
                    val targetPage = floor(nextPointer.x / metrics.pageStride).toInt().coerceIn(0, 1)
                    if (targetPage != currentPage) {
                        scope.launch { pageScroll.animateScrollTo((targetPage * metrics.pageStride).toInt()) }
                    }
                    statusNote = "Menyeret ke halaman ${targetPage + 1} — lepaskan untuk memindahkan"
                }

                fun onTileDragEnd() {
                    val current = dragInfo ?: return
                    dragInfo = null
                    val targetPage = floor(current.pointerContent.x / metrics.pageStride).toInt().coerceIn(0, 1)
                    val targetIndex = hitIndex(targetPage, current.pointerContent)
                    val item = current.item
                    if (targetPage != current.pageIndex) {
                        pageList(current.pageIndex).removeAll { it.id == item.id }
                        val to = pageList(targetPage)
                        val insertAt = if (targetIndex >= 0) {
                            val targetId = placedList(targetPage)[targetIndex].item.id
                            to.indexOfFirst { it.id == targetId }
                        } else {
                            to.size
                        }
                        to.add(insertAt.coerceIn(0, to.size), item)
                        selectedPage = targetPage
                        statusNote = "${item.name} dipindahkan ke halaman ${targetPage + 1} (Simulasi)"
                    } else if (targetIndex >= 0) {
                        val list = pageList(current.pageIndex)
                        val from = list.indexOfFirst { it.id == item.id }
                        val targetId = placedList(current.pageIndex)[targetIndex].item.id
                        val to = list.indexOfFirst { it.id == targetId }
                        if (from in list.indices && to in list.indices && from != to) {
                            list.removeAt(from)
                            list.add(to.coerceIn(0, list.size), item)
                            statusNote = "Urutan ${item.name} disusun ulang (Simulasi)"
                        } else {
                            statusNote = "${item.name} tetap di halaman ${current.pageIndex + 1}"
                        }
                    } else {
                        statusNote = "${item.name} tetap di halaman ${current.pageIndex + 1}"
                    }
                }

                fun onTileRemove(item: ControlTile, pageIndex: Int) {
                    pageList(pageIndex).removeAll { it.id == item.id }
                    statusNote = "${item.name} dihapus dari halaman ${pageIndex + 1} — tersedia di galeri"
                }

                fun onTileResize(item: ControlTile, pageIndex: Int) {
                    val list = pageList(pageIndex)
                    val idx = list.indexOfFirst { it.id == item.id }
                    if (idx in list.indices) {
                        val resized = item.copy(large = !item.large)
                        list[idx] = resized
                        statusNote = "${item.name} diubah ukurannya menjadi ${if (resized.large) "2x2" else "2x1"} (resize Simulasi)"
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(pageScroll),
                                horizontalArrangement = Arrangement.spacedBy(pageGap),
                            ) {
                                ControlPageGrid(
                                    pageIndex = 0,
                                    placed = packedOne.first,
                                    overflow = packedOne.second,
                                    pageWidth = pageWidth,
                                    gridHeight = gridHeight,
                                    cellW = cellW,
                                    cellH = cellH,
                                    gridGap = gridGap,
                                    editMode = editMode,
                                    dragInfo = dragInfo,
                                    activeStates = activeStates,
                                    onTap = { onTileTap(it) },
                                    onLongPress = { onTileLongPress() },
                                    onDragStart = { item, col, row, localPos -> onTileDragStart(item, 0, col, row, localPos) },
                                    onDrag = { onTileDrag(it) },
                                    onDragEnd = { onTileDragEnd() },
                                    onRemove = { onTileRemove(it, 0) },
                                    onResize = { onTileResize(it, 0) },
                                )

                                ControlPageGrid(
                                    pageIndex = 1,
                                    placed = packedTwo.first,
                                    overflow = packedTwo.second,
                                    pageWidth = pageWidth,
                                    gridHeight = gridHeight,
                                    cellW = cellW,
                                    cellH = cellH,
                                    gridGap = gridGap,
                                    editMode = editMode,
                                    dragInfo = dragInfo,
                                    activeStates = activeStates,
                                    onTap = { onTileTap(it) },
                                    onLongPress = { onTileLongPress() },
                                    onDragStart = { item, col, row, localPos -> onTileDragStart(item, 1, col, row, localPos) },
                                    onDrag = { onTileDrag(it) },
                                    onDragEnd = { onTileDragEnd() },
                                    onRemove = { onTileRemove(it, 1) },
                                    onResize = { onTileResize(it, 1) },
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                repeat(2) { idx ->
                                    val dotActive = currentPage == idx
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 5.dp)
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (dotActive) palette.primary else palette.border)
                                            .clickable {
                                                selectedPage = idx
                                                scope.launch { pageScroll.animateScrollTo((idx * metrics.pageStride).toInt()) }
                                            }
                                            .semantics {
                                                role = Role.Button
                                                contentDescription = "Buka halaman kontrol ${idx + 1}"
                                            },
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val anyOverflow = packedOne.second.isNotEmpty() || packedTwo.second.isNotEmpty()
                        Surface(
                            shape = CircleShape,
                            color = when {
                                dragInfo != null -> palette.info.copy(alpha = 0.15f)
                                editMode -> palette.warning.copy(alpha = 0.15f)
                                anyOverflow -> palette.danger.copy(alpha = 0.15f)
                                else -> palette.surfaceMuted
                            },
                            border = BorderStroke(
                                1.dp,
                                when {
                                    dragInfo != null -> palette.info
                                    editMode -> palette.warning
                                    anyOverflow -> palette.danger
                                    else -> palette.border
                                },
                            ),
                        ) {
                            Text(
                                when {
                                    dragInfo != null -> "Status: menyeret"
                                    editMode -> "Status: editing"
                                    anyOverflow -> "Status: kontrol tak muat"
                                    else -> "Status: normal"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            )
                        }
                        Text(
                            "Halaman ${currentPage + 1}: ${pageList(currentPage).size} kontrol",
                            fontSize = 11.sp,
                            color = palette.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Text(statusNote, fontSize = 11.sp, lineHeight = 16.sp, color = palette.textSecondary)

                    if (packedOne.second.isNotEmpty() || packedTwo.second.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = palette.danger.copy(alpha = 0.10f),
                            border = BorderStroke(1.dp, palette.danger),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Halaman penuh — batas maksimum kontrol tercapai", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                Text(
                                    "Tidak muat: " + (packedOne.second + packedTwo.second).joinToString { it.name },
                                    fontSize = 11.sp,
                                    color = palette.textSecondary,
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Surface(
                            onClick = {
                                editMode = !editMode
                                galleryOpen = false
                                statusNote = if (editMode) "Mode edit aktif — ketuk Selesai bila rampung" else "Perubahan tata letak disimpan (Simulasi)"
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (editMode) palette.primary else palette.surface,
                            border = BorderStroke(1.dp, if (editMode) palette.primary else palette.border),
                            modifier = Modifier
                                .weight(1f)
                                .semantics {
                                    role = Role.Button
                                    contentDescription = if (editMode) "Selesai mengedit kontrol" else "Mulai mengedit kontrol"
                                },
                        ) {
                            Text(
                                if (editMode) "Selesai" else "Edit Kontrol",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (editMode) palette.onPrimary else palette.textPrimary,
                                modifier = Modifier.padding(vertical = 12.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Surface(
                            onClick = {
                                if (!editMode) editMode = true
                                galleryOpen = !galleryOpen
                                statusNote = if (galleryOpen) "Galeri kontrol dibuka — kontrol baru masuk ke halaman ${selectedPage + 1}" else "Galeri kontrol ditutup"
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = palette.surface,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier
                                .weight(1f)
                                .semantics {
                                    role = Role.Button
                                    contentDescription = "Buka galeri kontrol tambahan"
                                },
                        ) {
                            Text(
                                if (galleryOpen) "Tutup Galeri" else "+ Galeri Kontrol",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                                modifier = Modifier.padding(vertical = 12.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }

                    if (galleryOpen) {
                        val targetList = pageList(selectedPage)
                        val available by remember {
                            derivedStateOf {
                                val placedIds = (packedOne.first + packedTwo.first).map { it.item.id } +
                                    (packedOne.second + packedTwo.second).map { it.id }
                                controlGallery.filter { it.id !in placedIds }
                            }
                        }
                        val fitsMap by remember {
                            derivedStateOf { controlGallery.associate { it.id to (packPage(targetList + it).second.isEmpty()) } }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = palette.surface,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Galeri Kontrol (Simulasi)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                                Text(
                                    "Kontrol baru ditambahkan ke halaman ${selectedPage + 1}. Kapasitas maksimum 12 sel per halaman (enam kontrol 2x1 atau tiga kontrol 2x2).",
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = palette.textSecondary,
                                )
                                if (available.isEmpty()) {
                                    Text("Semua kontrol sudah terpasang (daftar kosong).", fontSize = 12.sp, color = palette.textMuted)
                                }
                                available.forEach { control ->
                                    val fits = fitsMap[control.id] ?: false
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
                                            ControlGlyph(name = control.glyph, tint = palette.textSecondary, modifier = Modifier.size(20.dp))
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(control.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                                            Text(
                                                if (control.large) "Ukuran 2x2" else "Ukuran 2x1",
                                                fontSize = 10.sp,
                                                color = palette.textMuted,
                                            )
                                        }
                                        Surface(
                                            onClick = {
                                                if (fits) {
                                                    targetList.add(control)
                                                    statusNote = "${control.name} ditambahkan ke halaman ${selectedPage + 1} (Simulasi)"
                                                } else {
                                                    statusNote = "Halaman ${selectedPage + 1} penuh — ${control.name} tidak dapat ditambahkan"
                                                }
                                            },
                                            enabled = fits,
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (fits) palette.primary else palette.surfaceMuted,
                                            border = BorderStroke(1.dp, if (fits) palette.primary else palette.border),
                                            modifier = Modifier.semantics {
                                                role = Role.Button
                                                contentDescription = if (fits) "Tambahkan ${control.name}" else "${control.name} tidak dapat ditambahkan, halaman penuh"
                                            },
                                        ) {
                                            Text(
                                                if (fits) "Tambah" else "Penuh",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (fits) palette.onPrimary else palette.textMuted,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Pola Control Center & Edit Mode:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• Grid 4 kolom dua halaman dengan kontrol 2x2 dan 2x1, ditandai page dots", fontSize = 12.sp, color = palette.textSecondary)
                            Text("• Tekan lama masuk mode edit: ikon bergetar halus (jiggle), tombol minus, dan handle resize", fontSize = 12.sp, color = palette.textSecondary)
                            Text("• Seret kontrol ke tepi halaman untuk memindahkan; kapasitas penuh ditolak dengan penjelasan", fontSize = 12.sp, color = palette.textSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun ControlPageGrid(
    pageIndex: Int,
    placed: List<PlacedTile>,
    overflow: List<ControlTile>,
    pageWidth: Dp,
    gridHeight: Dp,
    cellW: Dp,
    cellH: Dp,
    gridGap: Dp,
    editMode: Boolean,
    dragInfo: DragInfo?,
    activeStates: Map<String, Boolean>,
    onTap: (ControlTile) -> Unit,
    onLongPress: () -> Unit,
    onDragStart: (ControlTile, Int, Int, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onRemove: (ControlTile) -> Unit,
    onResize: (ControlTile) -> Unit,
) {
    val palette = LocalAppPalette.current

    Box(
        modifier = Modifier
            .width(pageWidth)
            .height(gridHeight),
    ) {
        if (placed.isEmpty() && overflow.isEmpty()) {
            Text(
                "Halaman ${pageIndex + 1} kosong — tambahkan kontrol dari galeri",
                fontSize = 11.sp,
                color = palette.textMuted,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        placed.forEach { placedTile ->
            val item = placedTile.item
            val x = (cellW + gridGap) * placedTile.col
            val y = (cellH + gridGap) * placedTile.row
            val w = cellW * 2 + gridGap
            val h = cellH * (if (item.large) 2 else 1) + gridGap
            val draggingThis = dragInfo?.item?.id == item.id
            val translation = if (draggingThis) dragInfo?.translation ?: Offset.Zero else Offset.Zero

            ControlTileCard(
                item = item,
                active = activeStates[item.id] ?: false,
                editMode = editMode,
                dragging = draggingThis,
                width = w,
                height = h,
                onTap = { onTap(item) },
                onLongPress = { onLongPress() },
                onDragStart = { localPos -> onDragStart(item, placedTile.col, placedTile.row, localPos) },
                onDrag = { onDrag(it) },
                onDragEnd = { onDragEnd() },
                onRemove = { onRemove(item) },
                onResize = { onResize(item) },
                modifier = Modifier
                    .offset {
                        IntOffset(x.roundToPx(), y.roundToPx()) + IntOffset(translation.x.roundToInt(), translation.y.roundToInt())
                    },
            )
        }

        if (overflow.isNotEmpty()) {
            Text(
                "Tidak muat: " + overflow.joinToString { it.name },
                fontSize = 10.sp,
                color = palette.danger,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun ControlTileCard(
    item: ControlTile,
    active: Boolean,
    editMode: Boolean,
    dragging: Boolean,
    width: Dp,
    height: Dp,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onRemove: () -> Unit,
    onResize: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current

    // Jiggle halus: dibaca di lambda graphicsLayer agar tanpa recomposition per frame.
    val rotation: () -> Float = if (editMode && !dragging) {
        val transition = rememberInfiniteTransition(label = "jiggle")
        val phase by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(320, easing = LinearEasing)),
            label = "jigglePhase",
        )
        ({ sin(phase * Math.PI.toFloat() * 2f) * 2f })
    } else {
        ({ 0f })
    }

    val gestureModifier = if (editMode) {
        Modifier.pointerInput(item.id) {
            detectDragGesturesAfterLongPress(
                onDragStart = { localPos -> onDragStart(localPos) },
                onDrag = { _, dragAmount -> onDrag(dragAmount) },
                onDragEnd = { onDragEnd() },
                onDragCancel = { onDragEnd() },
            )
        }
    } else {
        Modifier.pointerInput(item.id) {
            detectTapGestures(
                onLongPress = { onLongPress() },
                onTap = { onTap() },
            )
        }
    }

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .graphicsLayer { rotationZ = rotation() }
            .clip(RoundedCornerShape(16.dp))
            .background(if (active) palette.primaryContainer else palette.surface)
            .border(
                BorderStroke(if (dragging) 2.dp else 1.dp, if (dragging) palette.info else palette.border),
                RoundedCornerShape(16.dp),
            )
            .then(gestureModifier)
            .padding(10.dp)
            .semantics {
                role = Role.Button
                contentDescription = "${item.name}, ukuran ${if (item.large) "2x2" else "2x1"}, ${if (active) "aktif" else "nonaktif"} (Simulasi)"
            },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                ControlGlyph(
                    name = item.glyph,
                    tint = if (active) palette.onPrimaryContainer else palette.textSecondary,
                    modifier = Modifier.size(if (item.large) 26.dp else 20.dp),
                )
                if (editMode) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        EditModeButton(label = "−", description = "Hapus ${item.name}", tint = palette.danger, onClick = onRemove)
                        EditModeButton(label = "⤢", description = "Ubah ukuran ${item.name}", tint = palette.info, onClick = onResize)
                    }
                }
            }
            Text(
                item.name,
                fontSize = if (item.large) 13.sp else 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                if (editMode) "Seret untuk menyusun" else if (active) "Aktif" else "Nonaktif",
                fontSize = 9.sp,
                color = if (active) palette.primary else palette.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun EditModeButton(
    label: String,
    description: String,
    tint: Color,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(tint)
            .clickable { onClick() }
            .semantics {
                role = Role.Button
                contentDescription = description
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
    }
}

@Composable
private fun ControlGlyph(
    name: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (name) {
            "wifi" -> {
                (1..3).forEach { i ->
                    drawArc(
                        color = tint,
                        startAngle = 200f,
                        sweepAngle = 140f,
                        useCenter = false,
                        style = Stroke(width = 3f, cap = StrokeCap.Round),
                        topLeft = Offset(w / 2f - w * 0.18f * i, h * 0.78f - w * 0.18f * i),
                        size = Size(w * 0.36f * i, w * 0.36f * i),
                    )
                }
                drawCircle(tint, radius = w * 0.05f, center = Offset(w / 2f, h * 0.78f))
            }
            "musik" -> {
                drawLine(tint, Offset(w * 0.60f, h * 0.18f), Offset(w * 0.60f, h * 0.68f), 3f, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.60f, h * 0.18f), Offset(w * 0.82f, h * 0.26f), 3f, StrokeCap.Round)
                drawCircle(tint, radius = w * 0.14f, center = Offset(w * 0.44f, h * 0.72f), style = stroke)
            }
            "bluetooth" -> {
                val path = Path().apply {
                    moveTo(w * 0.36f, h * 0.16f)
                    lineTo(w * 0.64f, h * 0.40f)
                    lineTo(w * 0.36f, h * 0.64f)
                    moveTo(w * 0.36f, h * 0.36f)
                    lineTo(w * 0.64f, h * 0.60f)
                    lineTo(w * 0.36f, h * 0.84f)
                }
                drawPath(path, color = tint, style = stroke)
            }
            "kecerahan" -> {
                drawCircle(tint, radius = w * 0.18f, center = Offset(w / 2f, h / 2f), style = stroke)
                (0 until 8).forEach { i ->
                    val angle = Math.PI.toFloat() * 2f * i / 8f
                    val startPt = Offset(w / 2f + cos(angle) * w * 0.34f, h / 2f + sin(angle) * w * 0.34f)
                    val endPt = Offset(w / 2f + cos(angle) * w * 0.46f, h / 2f + sin(angle) * w * 0.46f)
                    drawLine(tint, startPt, endPt, 3f, StrokeCap.Round)
                }
            }
            "timer" -> {
                drawCircle(tint, radius = w * 0.30f, center = Offset(w / 2f, h * 0.55f), style = stroke)
                drawLine(tint, Offset(w / 2f, h * 0.55f), Offset(w / 2f, h * 0.36f), 3f, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.40f, h * 0.16f), Offset(w * 0.60f, h * 0.16f), 3f, StrokeCap.Round)
            }
            "senter" -> {
                val path = Path().apply {
                    moveTo(w * 0.30f, h * 0.18f)
                    lineTo(w * 0.70f, h * 0.18f)
                    lineTo(w * 0.58f, h * 0.42f)
                    lineTo(w * 0.58f, h * 0.82f)
                    lineTo(w * 0.42f, h * 0.82f)
                    lineTo(w * 0.42f, h * 0.42f)
                    close()
                }
                drawPath(path, color = tint, style = stroke)
            }
            "rotasi" -> {
                drawArc(
                    color = tint,
                    startAngle = 40f,
                    sweepAngle = 280f,
                    useCenter = false,
                    style = stroke,
                    topLeft = Offset(w * 0.14f, h * 0.14f),
                    size = Size(w * 0.60f, h * 0.60f),
                )
                drawLine(tint, Offset(w * 0.74f, h * 0.12f), Offset(w * 0.74f, h * 0.34f), 3f, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.63f, h * 0.23f), Offset(w * 0.85f, h * 0.23f), 3f, StrokeCap.Round)
            }
            "alarm" -> {
                drawCircle(tint, radius = w * 0.28f, center = Offset(w / 2f, h * 0.55f), style = stroke)
                drawLine(tint, Offset(w * 0.22f, h * 0.20f), Offset(w * 0.38f, h * 0.32f), 3f, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.78f, h * 0.20f), Offset(w * 0.62f, h * 0.32f), 3f, StrokeCap.Round)
            }
            "kalkulator" -> {
                val path = Path().apply {
                    moveTo(w * 0.22f, h * 0.16f)
                    lineTo(w * 0.78f, h * 0.16f)
                    lineTo(w * 0.78f, h * 0.84f)
                    lineTo(w * 0.22f, h * 0.84f)
                    close()
                }
                drawPath(path, color = tint, style = stroke)
                drawLine(tint, Offset(w * 0.32f, h * 0.32f), Offset(w * 0.68f, h * 0.32f), 3f, StrokeCap.Round)
                drawCircle(tint, radius = 2.5f, center = Offset(w * 0.38f, h * 0.62f))
                drawCircle(tint, radius = 2.5f, center = Offset(w * 0.62f, h * 0.62f))
            }
            "fokus" -> {
                drawCircle(tint, radius = w * 0.36f, center = Offset(w / 2f, h / 2f), style = stroke)
                drawCircle(tint, radius = w * 0.14f, center = Offset(w / 2f, h / 2f), style = stroke)
            }
            else -> {
                drawCircle(tint, radius = w * 0.30f, center = Offset(w / 2f, h / 2f), style = stroke)
            }
        }
    }
}
