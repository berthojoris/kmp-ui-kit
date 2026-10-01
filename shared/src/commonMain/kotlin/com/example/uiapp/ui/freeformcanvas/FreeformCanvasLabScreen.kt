package com.example.uiapp.ui.freeformcanvas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private const val BULK_OBJECTS = 50
private const val MIN_ZOOM = 0.35f
private const val MAX_ZOOM = 2.5f

private enum class ObjKind(val label: String) {
    NOTE("Catatan"),
    TEKS("Teks"),
    KOTAK("Kotak"),
    LINGKARAN("Lingkaran"),
}

private enum class CanvasTool(val label: String) {
    PINDAH("Pilih"),
    LASSO("Lasso"),
    KONEKTOR("Konektor"),
}

private data class CanvasObj(
    val id: Int,
    val kind: ObjKind,
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
    val colorIndex: Int,
    val text: String,
    val groupId: Int = 0,
)

private data class CanvasLink(val id: Int, val fromId: Int, val toId: Int)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FreeformCanvasLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val pxPerDp = LocalDensity.current.density

    val objects = remember {
        mutableStateListOf(
            CanvasObj(1, ObjKind.NOTE, 60f, 60f, 130f, 110f, 0, "Rencana rute liburan"),
            CanvasObj(2, ObjKind.TEKS, 240f, 80f, 160f, 44f, 1, "Judul papan: Liburan"),
            CanvasObj(3, ObjKind.KOTAK, 90f, 230f, 130f, 90f, 2, "Daftar hotel"),
            CanvasObj(4, ObjKind.LINGKARAN, 270f, 220f, 110f, 110f, 3, "Ide dadakan"),
        )
    }
    val links = remember { mutableStateListOf(CanvasLink(1, 1, 3)) }
    var selectedIds by remember { mutableStateOf(emptySet<Int>()) }
    var dragMoveIds by remember { mutableStateOf(emptySet<Int>()) }
    var tool by remember { mutableStateOf(CanvasTool.PINDAH) }
    var linkFromId by remember { mutableStateOf<Int?>(null) }
    var editingId by remember { mutableStateOf<Int?>(null) }
    var editorText by remember { mutableStateOf("") }
    var nextObjId by remember { mutableIntStateOf(100) }
    var nextLinkId by remember { mutableIntStateOf(100) }
    var nextGroupId by remember { mutableIntStateOf(10) }
    var bulkTick by remember { mutableIntStateOf(0) }
    var bulkLoading by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf<String?>("Kanvas siap — pan/zoom dengan gestur atau tombol") }
    var statusTick by remember { mutableIntStateOf(0) }
    var statusIsError by remember { mutableStateOf(false) }

    var panX by remember { mutableFloatStateOf(40f) }
    var panY by remember { mutableFloatStateOf(30f) }
    var zoom by remember { mutableFloatStateOf(1f) }

    var lassoActive by remember { mutableStateOf(false) }
    var lassoStart by remember { mutableStateOf(Offset.Zero) }
    var lassoEnd by remember { mutableStateOf(Offset.Zero) }

    fun notify(message: String, isError: Boolean = false) {
        statusText = message
        statusIsError = isError
        statusTick++
    }

    // Handler dibungkus remember agar identitasnya stabil (hemat recomposition 50+ obyek).
    val handleTap: (Int) -> Unit = remember {
        { id ->
            when (tool) {
                CanvasTool.KONEKTOR -> {
                    val from = linkFromId
                    if (from == null) {
                        linkFromId = id
                        selectedIds = setOf(id)
                        notify("Obyek sumber dipilih — ketuk obyek tujuan")
                    } else if (from == id) {
                        linkFromId = null
                        selectedIds = emptySet()
                        notify("Gagal — obyek tidak bisa dihubungkan ke dirinya sendiri", isError = true)
                    } else {
                        links.add(CanvasLink(nextLinkId, from, id))
                        nextLinkId++
                        linkFromId = null
                        selectedIds = emptySet()
                        notify("Selesai — konektor dibuat")
                    }
                }
                else -> {
                    val obj = objects.firstOrNull { it.id == id }
                    val group = obj?.groupId ?: 0
                    selectedIds = if (group != 0) {
                        objects.filter { it.groupId == group }.map { it.id }.toSet()
                    } else {
                        setOf(id)
                    }
                }
            }
        }
    }

    val handleDragStart: (Int) -> Unit = remember {
        { id ->
            val obj = objects.firstOrNull { it.id == id }
            val group = obj?.groupId ?: 0
            dragMoveIds = when {
                id in selectedIds && selectedIds.size > 1 -> selectedIds
                group != 0 -> objects.filter { it.groupId == group }.map { it.id }.toSet()
                else -> setOf(id)
            }
            if (id !in selectedIds) {
                selectedIds = dragMoveIds
            }
        }
    }

    val handleDrag: (Float, Float) -> Unit = remember {
        { dx, dy ->
            if (tool == CanvasTool.PINDAH) {
                val k = 1f / zoom
                val ids = dragMoveIds
                for (i in objects.indices) {
                    if (objects[i].id in ids) {
                        val obj = objects[i]
                        objects[i] = obj.copy(x = obj.x + dx * k, y = obj.y + dy * k)
                    }
                }
            }
        }
    }

    val handleDragEnd: () -> Unit = remember {
        {
            if (dragMoveIds.isNotEmpty()) {
                dragMoveIds = emptySet()
                notify("Selesai — obyek digeser")
            }
        }
    }

    val handleBackgroundTap: () -> Unit = remember {
        {
            when (tool) {
                CanvasTool.KONEKTOR -> {
                    linkFromId = null
                    notify("Pembuatan konektor dibatalkan")
                }
                CanvasTool.LASSO -> selectedIds = emptySet()
                else -> selectedIds = emptySet()
            }
        }
    }

    val handleSpawn: (ObjKind, Float, Float, Float) -> Unit = remember {
        { kind, vpW, vpH, dens ->
            val wDp = when (kind) {
                ObjKind.NOTE -> 130f
                ObjKind.TEKS -> 160f
                ObjKind.KOTAK -> 130f
                ObjKind.LINGKARAN -> 110f
            }
            val hDp = when (kind) {
                ObjKind.NOTE -> 110f
                ObjKind.TEKS -> 44f
                ObjKind.KOTAK -> 90f
                ObjKind.LINGKARAN -> 110f
            }
            val wPx = wDp * dens
            val hPx = hDp * dens
            val idx = objects.size
            val x = (vpW - panX) / zoom - wPx / 2f + (idx % 5) * 22f
            val y = (vpH - panY) / zoom - hPx / 2f + (idx % 5) * 18f
            val obj = CanvasObj(
                id = nextObjId,
                kind = kind,
                x = x,
                y = y,
                w = wDp,
                h = hDp,
                colorIndex = nextObjId % 4,
                text = when (kind) {
                    ObjKind.NOTE -> "Catatan baru"
                    ObjKind.TEKS -> "Teks baru"
                    ObjKind.KOTAK -> "Kotak"
                    ObjKind.LINGKARAN -> "Lingkaran"
                },
            )
            nextObjId++
            objects.add(obj)
            selectedIds = setOf(obj.id)
            notify("Selesai — " + kind.label + " ditambahkan")
        }
    }

    val handleDelete: () -> Unit = remember {
        {
            val ids = selectedIds
            objects.removeAll { it.id in ids }
            links.removeAll { it.fromId in ids || it.toId in ids }
            selectedIds = emptySet()
            notify("Selesai — obyek terpilih dihapus")
        }
    }

    val handleDuplicate: () -> Unit = remember {
        {
            val copies = objects.filter { it.id in selectedIds }.mapIndexed { idx, source ->
                source.copy(id = nextObjId + idx, x = source.x + 26f, y = source.y + 26f, groupId = 0)
            }
            nextObjId += copies.size
            objects.addAll(copies)
            selectedIds = copies.map { it.id }.toSet()
            notify("Selesai — obyek diduplikasi")
        }
    }

    val handleGroup: () -> Unit = remember {
        {
            if (selectedIds.size >= 2) {
                val gid = nextGroupId
                nextGroupId++
                for (i in objects.indices) {
                    if (objects[i].id in selectedIds) {
                        objects[i] = objects[i].copy(groupId = gid)
                    }
                }
                notify("Selesai — " + selectedIds.size + " obyek dikelompokkan")
            }
        }
    }

    val handleColor: () -> Unit = remember {
        {
            for (i in objects.indices) {
                if (objects[i].id in selectedIds) {
                    val obj = objects[i]
                    objects[i] = obj.copy(colorIndex = (obj.colorIndex + 1) % 4)
                }
            }
            notify("Selesai — warna obyek diganti")
        }
    }

    val handleEditOpen: () -> Unit = remember {
        {
            val id = selectedIds.firstOrNull()
            val obj = objects.firstOrNull { it.id == id }
            if (obj != null && (obj.kind == ObjKind.NOTE || obj.kind == ObjKind.TEKS)) {
                editingId = obj.id
                editorText = obj.text
            } else {
                notify("Gagal — hanya catatan/teks yang bisa diubah isinya", isError = true)
            }
        }
    }

    val handleEditSave: () -> Unit = remember {
        {
            val id = editingId
            val idx = objects.indexOfFirst { it.id == id }
            if (idx >= 0) {
                objects[idx] = objects[idx].copy(text = editorText)
                notify("Selesai — teks obyek diperbarui")
            }
            editingId = null
        }
    }

    val handleBulk: () -> Unit = remember { { bulkTick++ } }

    val handleClear: () -> Unit = remember {
        {
            objects.clear()
            links.clear()
            selectedIds = emptySet()
            editingId = null
            notify("Selesai — kanvas dikosongkan")
        }
    }

    // Uji beban: 50 obyek ringan ditambahkan setelah jeda loading singkat.
    LaunchedEffect(bulkTick) {
        if (bulkTick > 0) {
            bulkLoading = true
            delay(450)
            val baseId = nextObjId
            for (i in 0 until BULK_OBJECTS) {
                val col = i % 10
                val row = i / 10
                objects.add(
                    CanvasObj(
                        id = nextObjId,
                        kind = ObjKind.NOTE,
                        x = 40f + col * 150f,
                        y = 420f + row * 130f,
                        w = 130f,
                        h = 110f,
                        colorIndex = i % 4,
                        text = "Catatan uji " + (i + 1),
                    ),
                )
                nextObjId++
            }
            // Beberapa konektor untuk menguji gambar garis dalam jumlah besar.
            for (i in 0 until BULK_OBJECTS - 1) {
                links.add(CanvasLink(nextLinkId, baseId + i, baseId + i + 1))
                nextLinkId++
            }
            bulkLoading = false
            notify("Selesai — " + BULK_OBJECTS + " obyek uji ditambahkan (struktur ringan)")
        }
    }

    LaunchedEffect(statusTick) {
        if (statusText != null) {
            delay(2600)
            statusText = null
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Freeform Infinite Canvas",
                subtitle = "Sticky note, konektor, pan/zoom",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            val density = LocalDensity.current
            val vpW = with(density) { maxWidth.toPx() }
            val vpH = with(density) { maxHeight.toPx() }
            val dens = density.density

            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    CanvasViewport(
                        objects = objects,
                        links = links,
                        selectedIds = selectedIds,
                        tool = tool,
                        linkFromId = linkFromId,
                        panX = panX,
                        panY = panY,
                        zoom = zoom,
                        lassoActive = lassoActive,
                        lassoStart = lassoStart,
                        lassoEnd = lassoEnd,
                        onBackgroundTap = handleBackgroundTap,
                        onTransform = { dragPan, centroid, zoomChange ->
                            panX += dragPan.x
                            panY += dragPan.y
                            if (zoomChange != 1f) {
                                val newZoom = (zoom * zoomChange).coerceIn(MIN_ZOOM, MAX_ZOOM)
                                val k = newZoom / zoom
                                panX = centroid.x - (centroid.x - panX) * k
                                panY = centroid.y - (centroid.y - panY) * k
                                zoom = newZoom
                            }
                        },
                        onLassoStart = { point ->
                            lassoActive = true
                            lassoStart = point
                            lassoEnd = point
                        },
                        onLassoDrag = { delta -> lassoEnd = lassoEnd + delta },
                        onLassoEnd = {
                            val left = minOf(lassoStart.x, lassoEnd.x)
                            val top = minOf(lassoStart.y, lassoEnd.y)
                            val right = maxOf(lassoStart.x, lassoEnd.x)
                            val bottom = maxOf(lassoStart.y, lassoEnd.y)
                            val hit = objects.filter { obj ->
                                val l = panX + obj.x * zoom
                                val t = panY + obj.y * zoom
                                val r = l + obj.w * pxPerDp * zoom
                                val b = t + obj.h * pxPerDp * zoom
                                l < right && r > left && t < bottom && b > top
                            }.map { it.id }.toSet()
                            selectedIds = hit
                            lassoActive = false
                            notify("Selesai — " + hit.size + " obyek dipilih dengan lasso")
                        },
                        onObjectTap = handleTap,
                        onObjectDragStart = handleDragStart,
                        onObjectDrag = handleDrag,
                        onObjectDragEnd = handleDragEnd,
                    )

                    if (bulkLoading) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = palette.surface,
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
                        ) {
                            Text(
                                "Menyiapkan " + BULK_OBJECTS + " obyek…",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textSecondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            )
                        }
                    } else if (objects.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text("Kanvas Kosong", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                            Text(
                                "Tambahkan catatan/teks/shape dari toolbar bawah.",
                                fontSize = 12.sp,
                                color = palette.textSecondary,
                            )
                        }
                    }

                    if (statusText != null) {
                        StatusBanner(
                            message = statusText,
                            isError = statusIsError,
                            modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
                        )
                    }
                }

                CanvasToolbar(
                    tool = tool,
                    onToolChange = {
                        tool = it
                        linkFromId = null
                    },
                    zoom = zoom,
                    onZoom = { factor -> zoom = (zoom * factor).coerceIn(MIN_ZOOM, MAX_ZOOM) },
                    onResetView = {
                        panX = 40f
                        panY = 30f
                        zoom = 1f
                        notify("Tampilan dikembalikan ke pusat")
                    },
                    onSpawn = { kind -> handleSpawn(kind, vpW, vpH, dens) },
                    onBulk = handleBulk,
                    onClear = handleClear,
                    bulkLoading = bulkLoading,
                    selectionCount = selectedIds.size,
                    objectCount = objects.size,
                    linkCount = links.size,
                    onEdit = handleEditOpen,
                    onColor = handleColor,
                    onDuplicate = handleDuplicate,
                    onGroup = handleGroup,
                    onDelete = handleDelete,
                    onDeselect = { selectedIds = emptySet() },
                    editingId = editingId,
                    editorText = editorText,
                    onEditorTextChange = { editorText = it },
                    onEditSave = handleEditSave,
                    onEditCancel = { editingId = null },
                )
            }
        }
    }
}

@Composable
private fun CanvasViewport(
    objects: List<CanvasObj>,
    links: List<CanvasLink>,
    selectedIds: Set<Int>,
    tool: CanvasTool,
    linkFromId: Int?,
    panX: Float,
    panY: Float,
    zoom: Float,
    lassoActive: Boolean,
    lassoStart: Offset,
    lassoEnd: Offset,
    onBackgroundTap: () -> Unit,
    onTransform: (Offset, Offset, Float) -> Unit,
    onLassoStart: (Offset) -> Unit,
    onLassoDrag: (Offset) -> Unit,
    onLassoEnd: () -> Unit,
    onObjectTap: (Int) -> Unit,
    onObjectDragStart: (Int) -> Unit,
    onObjectDrag: (Float, Float) -> Unit,
    onObjectDragEnd: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Lapisan gestur latar (pan/zoom, tap kosong, lasso)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(palette.surfaceMuted)
                .pointerInput(tool) {
                    if (tool == CanvasTool.LASSO) {
                        detectDragGestures(
                            onDragStart = { onLassoStart(it) },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                onLassoDrag(dragAmount)
                            },
                            onDragEnd = { onLassoEnd() },
                        )
                    } else {
                        detectTransformGestures { centroid, pan, zoomChange, _ ->
                            onTransform(pan, centroid, zoomChange)
                        }
                    }
                }
                .pointerInput(tool) {
                    detectTapGestures(onTap = { onBackgroundTap() })
                },
        )

        // Lapisan grid + konektor (koordinat layar)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stepPx = 48.dp.toPx() * zoom
            val ox = panX % stepPx
            val oy = panY % stepPx
            var gx = ox
            while (gx < size.width) {
                drawLine(
                    color = palette.border,
                    start = Offset(gx, 0f),
                    end = Offset(gx, size.height),
                    strokeWidth = 1f,
                )
                gx += stepPx
            }
            var gy = oy
            while (gy < size.height) {
                drawLine(
                    color = palette.border,
                    start = Offset(0f, gy),
                    end = Offset(size.width, gy),
                    strokeWidth = 1f,
                )
                gy += stepPx
            }

            for (link in links) {
                val from = objects.firstOrNull { it.id == link.fromId } ?: continue
                val to = objects.firstOrNull { it.id == link.toId } ?: continue
                val start = Offset(
                    panX + (from.x + from.w.dp.toPx() * 0.5f) * zoom,
                    panY + (from.y + from.h.dp.toPx() * 0.5f) * zoom,
                )
                val end = Offset(
                    panX + (to.x + to.w.dp.toPx() * 0.5f) * zoom,
                    panY + (to.y + to.h.dp.toPx() * 0.5f) * zoom,
                )
                drawLine(
                    color = palette.info,
                    start = start,
                    end = end,
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                drawCircle(color = palette.info, radius = 4.dp.toPx(), center = start)
                drawCircle(color = palette.info, radius = 4.dp.toPx(), center = end)
            }
        }

        // Lapisan obyek dunia (transformasi pan/zoom tunggal agar 50+ obyek tetap ringan)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = zoom
                    scaleY = zoom
                    transformOrigin = TransformOrigin(0f, 0f)
                    translationX = panX
                    translationY = panY
                },
        ) {
            objects.forEach { obj ->
                CanvasObjectView(
                    obj = obj,
                    selected = obj.id in selectedIds,
                    linking = obj.id == linkFromId,
                    onTap = onObjectTap,
                    onDragStart = onObjectDragStart,
                    onDrag = onObjectDrag,
                    onDragEnd = onObjectDragEnd,
                )
            }
        }

        // Lapisan lasso (di atas obyek)
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (lassoActive) {
                val left = minOf(lassoStart.x, lassoEnd.x)
                val top = minOf(lassoStart.y, lassoEnd.y)
                val right = maxOf(lassoStart.x, lassoEnd.x)
                val bottom = maxOf(lassoStart.y, lassoEnd.y)
                drawRect(
                    color = palette.primary.copy(alpha = 0.10f),
                    topLeft = Offset(left, top),
                    size = Size(right - left, bottom - top),
                )
                drawRect(
                    color = palette.primary,
                    topLeft = Offset(left, top),
                    size = Size(right - left, bottom - top),
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
            }
        }
    }
}

@Composable
private fun CanvasObjectView(
    obj: CanvasObj,
    selected: Boolean,
    linking: Boolean,
    onTap: (Int) -> Unit,
    onDragStart: (Int) -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val accent = canvasAccent(palette, obj.colorIndex)
    val shape = if (obj.kind == ObjKind.LINGKARAN) CircleShape else RoundedCornerShape(12.dp)

    val faceColor = when (obj.kind) {
        ObjKind.NOTE -> accent.copy(alpha = 0.22f)
        ObjKind.TEKS -> Color.Transparent
        ObjKind.KOTAK -> accent.copy(alpha = 0.10f)
        ObjKind.LINGKARAN -> accent.copy(alpha = 0.14f)
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(obj.x.roundToInt(), obj.y.roundToInt()) }
            .size(obj.w.dp, obj.h.dp)
            .clip(shape)
            .background(faceColor)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = when {
                    linking -> palette.danger
                    selected -> palette.primary
                    obj.groupId != 0 -> palette.info
                    else -> accent
                },
                shape = shape,
            )
            .pointerInput(obj.id) {
                detectDragGestures(
                    onDragStart = { onDragStart(obj.id) },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    },
                    onDragEnd = { onDragEnd() },
                )
            }
            .pointerInput(obj.id) { detectTapGestures(onTap = { onTap(obj.id) }) }
            .padding(10.dp)
            .semantics {
                role = Role.Button
                contentDescription = obj.kind.label + " \"" + obj.text + "\"" +
                    (if (obj.groupId != 0) ", dalam grup" else "") +
                    (if (selected) ", terpilih" else "")
            },
    ) {
        Column {
            Text(
                text = obj.kind.label,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = obj.text,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun CanvasToolbar(
    tool: CanvasTool,
    onToolChange: (CanvasTool) -> Unit,
    zoom: Float,
    onZoom: (Float) -> Unit,
    onResetView: () -> Unit,
    onSpawn: (ObjKind) -> Unit,
    onBulk: () -> Unit,
    onClear: () -> Unit,
    bulkLoading: Boolean,
    selectionCount: Int,
    objectCount: Int,
    linkCount: Int,
    onEdit: () -> Unit,
    onColor: () -> Unit,
    onDuplicate: () -> Unit,
    onGroup: () -> Unit,
    onDelete: () -> Unit,
    onDeselect: () -> Unit,
    editingId: Int?,
    editorText: String,
    onEditorTextChange: (String) -> Unit,
    onEditSave: () -> Unit,
    onEditCancel: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(0.dp),
        color = palette.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier.fillMaxWidth().navigationBarsPadding().imePadding(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = objectCount.toString() + " obyek · " + linkCount + " konektor · zoom " +
                    (zoom * 100).roundToInt() + "%",
                fontSize = 10.sp,
                color = palette.textMuted,
            )

            if (editingId != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = palette.surfaceMuted,
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Ubah Teks Obyek", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        BasicTextField(
                            value = editorText,
                            onValueChange = onEditorTextChange,
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 14.sp, color = palette.textPrimary),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(palette.surface)
                                        .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 10.dp),
                                ) {
                                    if (editorText.isEmpty()) {
                                        Text("Tulis isi obyek…", fontSize = 13.sp, color = palette.textMuted)
                                    }
                                    innerTextField()
                                }
                            },
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ToolButton(
                                label = "Simpan Teks",
                                onClick = onEditSave,
                                modifier = Modifier.weight(1f),
                                accent = true,
                            )
                            ToolButton(
                                label = "Batal",
                                onClick = onEditCancel,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            if (selectionCount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = selectionCount.toString() + " dipilih",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                    )
                    ToolButton(label = "Teks", onClick = onEdit, enabled = selectionCount == 1)
                    ToolButton(label = "Warna", onClick = onColor)
                    ToolButton(label = "Duplikat", onClick = onDuplicate)
                    ToolButton(label = "Kelompokkan", onClick = onGroup, enabled = selectionCount >= 2)
                    ToolButton(label = "Hapus", onClick = onDelete, danger = true)
                    ToolButton(label = "Batal", onClick = onDeselect)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                CanvasTool.entries.forEach { item ->
                    ToolButton(
                        label = item.label,
                        onClick = { onToolChange(item) },
                        accent = item == tool,
                    )
                }
                ToolButton(label = "+ Catatan", onClick = { onSpawn(ObjKind.NOTE) }, enabled = !bulkLoading)
                ToolButton(label = "+ Teks", onClick = { onSpawn(ObjKind.TEKS) }, enabled = !bulkLoading)
                ToolButton(label = "+ Kotak", onClick = { onSpawn(ObjKind.KOTAK) }, enabled = !bulkLoading)
                ToolButton(label = "+ Lingkaran", onClick = { onSpawn(ObjKind.LINGKARAN) }, enabled = !bulkLoading)
            }

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ToolButton(label = "Perkecil", onClick = { onZoom(0.8f) }, modifier = Modifier.width(84.dp))
                ToolButton(label = "Perbesar", onClick = { onZoom(1.25f) }, modifier = Modifier.width(84.dp))
                ToolButton(label = "Pusatkan", onClick = onResetView, modifier = Modifier.width(84.dp))
                ToolButton(
                    label = if (bulkLoading) "Menyiapkan…" else "Uji " + BULK_OBJECTS + " Obyek",
                    onClick = onBulk,
                    enabled = !bulkLoading,
                    modifier = Modifier.width(132.dp),
                )
                ToolButton(label = "Bersihkan", onClick = onClear, modifier = Modifier.width(96.dp), danger = true)
            }
        }
    }
}

@Composable
private fun ToolButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accent: Boolean = false,
    danger: Boolean = false,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = when {
            accent -> palette.primary
            danger -> palette.danger.copy(alpha = 0.10f)
            else -> palette.surface
        },
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(
            1.dp,
            when {
                accent -> palette.primary
                danger -> palette.danger
                else -> palette.border
            },
        ),
        modifier = modifier.alpha(if (enabled) 1f else 0.5f),
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = when {
                accent -> palette.onPrimary
                danger -> palette.danger
                else -> palette.textPrimary
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
        )
    }
}

@Composable
private fun StatusBanner(message: String?, isError: Boolean, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    if (message != null) {
        val tint = if (isError) palette.danger else palette.success
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = palette.surface,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
            border = BorderStroke(1.dp, tint),
            modifier = modifier.semantics {
                contentDescription = (if (isError) "Status gagal: " else "Status sukses: ") + message
            },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                CheckGlyph(tint = tint)
                Text(
                    text = message,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(240.dp),
                )
            }
        }
    }
}

@Composable
private fun CheckGlyph(tint: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.18f, h * 0.52f)
            lineTo(w * 0.42f, h * 0.76f)
            lineTo(w * 0.82f, h * 0.28f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = w * 0.14f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

private fun canvasAccent(palette: com.example.uiapp.theme.AppPalette, colorIndex: Int): Color {
    return when (colorIndex % 4) {
        0 -> palette.warning
        1 -> palette.info
        2 -> palette.success
        else -> palette.primary
    }
}
