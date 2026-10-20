package com.example.uiapp.ui.graphexplorer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
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
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

private enum class NodeKind(val label: String) {
    ORANG("Orang"),
    AKUN("Akun"),
    PERANGKAT("Perangkat"),
    PERUSAHAAN("Perusahaan"),
}

private data class GNode(val id: Int, val name: String, val kind: NodeKind)
private data class GEdge(val a: Int, val b: Int)
private data class NodePos(val x: Float, val y: Float)

private val Nodes = listOf(
    GNode(0, "Rina Kusuma", NodeKind.ORANG),
    GNode(1, "Bagas Wibowo", NodeKind.ORANG),
    GNode(2, "Dewi Anggraini", NodeKind.ORANG),
    GNode(3, "akun.rina@mail.test", NodeKind.AKUN),
    GNode(4, "akun.bagas@mail.test", NodeKind.AKUN),
    GNode(5, "cs.dewi@mail.test", NodeKind.AKUN),
    GNode(6, "HP-Samsung-01", NodeKind.PERANGKAT),
    GNode(7, "iPhone-12-Dev", NodeKind.PERANGKAT),
    GNode(8, "Router-Kantor", NodeKind.PERANGKAT),
    GNode(9, "PT Sinar Data", NodeKind.PERUSAHAAN),
    GNode(10, "CV Mitra Retail", NodeKind.PERUSAHAAN),
)

private val Edges = listOf(
    GEdge(0, 3), GEdge(0, 6), GEdge(0, 9),
    GEdge(1, 4), GEdge(1, 7), GEdge(1, 9),
    GEdge(2, 5), GEdge(2, 8), GEdge(2, 10),
    GEdge(3, 6), GEdge(4, 7), GEdge(5, 8),
    GEdge(9, 10), GEdge(0, 10),
)

// Relasi silang tambahan untuk mode "terlalu padat".
private val DenseEdges = listOf(
    GEdge(0, 4), GEdge(0, 5), GEdge(1, 3), GEdge(1, 5),
    GEdge(2, 3), GEdge(2, 4), GEdge(3, 8), GEdge(4, 6),
    GEdge(5, 7), GEdge(6, 9), GEdge(7, 10), GEdge(6, 10), GEdge(8, 9),
)

/** Tata letak gaya force sederhana: tolakan antar node + pegas pada edge + gravitasi pusat. */
private fun forceLayout(nodes: List<GNode>, edges: List<GEdge>, steps: Int = 160): Map<Int, NodePos> {
    val rnd = Random(42)
    var pos = nodes.associate { it.id to NodePos(rnd.nextFloat(), rnd.nextFloat()) }
    val degree = nodes.associate { n -> n.id to edges.count { it.a == n.id || it.b == n.id } + 1 }
    repeat(steps) {
        val next = HashMap<Int, NodePos>(pos.size)
        nodes.forEach { n ->
            var fx = 0f
            var fy = 0f
            val p = pos.getValue(n.id)
            nodes.forEach { m ->
                if (m.id == n.id) return@forEach
                val q = pos.getValue(m.id)
                val dx = p.x - q.x
                val dy = p.y - q.y
                val d2 = dx * dx + dy * dy + 1e-4f
                if (d2 < 0.09f) {
                    val f = 0.012f / d2
                    fx += dx * f
                    fy += dy * f
                }
            }
            edges.forEach { e ->
                val other = when (n.id) {
                    e.a -> e.b
                    e.b -> e.a
                    else -> -1
                }
                if (other >= 0) {
                    val q = pos.getValue(other)
                    val dx = q.x - p.x
                    val dy = q.y - p.y
                    val d = sqrt(dx * dx + dy * dy)
                    if (d > 0.2f) {
                        fx += dx * 0.02f
                        fy += dy * 0.02f
                    }
                }
            }
            // Gravitasi ke pusat, node berderajat tinggi lebih stabil.
            val g = 0.004f * degree.getValue(n.id)
            fx += (0.5f - p.x) * g
            fy += (0.5f - p.y) * g
            next[n.id] = NodePos(
                (p.x + fx).coerceIn(0.05f, 0.95f),
                (p.y + fy).coerceIn(0.05f, 0.95f),
            )
        }
        pos = next
    }
    return pos
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GraphExplorerLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current
    val kindColors = remember(palette) {
        mapOf(
            NodeKind.ORANG to palette.primary,
            NodeKind.AKUN to palette.info,
            NodeKind.PERANGKAT to palette.warning,
            NodeKind.PERUSAHAAN to palette.success,
        )
    }

    var dense by remember { mutableStateOf(false) }
    var selectedId by remember { mutableStateOf<Int?>(null) }
    var query by remember { mutableStateOf("") }
    val activeKinds = remember { mutableStateOf(NodeKind.entries.toSet()) }
    val allEdges = remember(dense) { if (dense) Edges + DenseEdges else Edges }
    val layout = remember(allEdges) { forceLayout(Nodes, allEdges) }

    var scale by remember { mutableFloatStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.6f, 3f)
        pan += panChange
    }

    BackHandler(enabled = true) { onBack() }

    val visibleNodes = Nodes.filter { it.kind in activeKinds.value && (query.isBlank() || it.name.contains(query, ignoreCase = true)) }
    val neighborIds = allEdges
        .filter { it.a == selectedId || it.b == selectedId }
        .map { if (it.a == selectedId) it.b else it.a }
        .toSet()
    val selected = Nodes.firstOrNull { it.id == selectedId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background),
    ) {
        UiTopBar(
            title = "Graph Explorer",
            subtitle = "Graf relasi node-link — data mock lokal",
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            LabIntroCard(
                title = "Seluruh graf adalah simulasi",
                body = "Relasi orang–akun–perangkat–perusahaan berupa data mock. Cubit untuk zoom, seret untuk pan, " +
                    "ketuk node untuk menyorot tetangganya.",
            )

            val fieldShape = RoundedCornerShape(10.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface, fieldShape)
                    .border(1.dp, palette.border, fieldShape)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text("Cari", fontSize = 12.sp, color = palette.textMuted)
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = TextStyle(color = palette.textPrimary, fontSize = 13.sp),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        Box {
                            if (query.isBlank()) {
                                Text("Nama node…", fontSize = 13.sp, color = palette.textMuted)
                            }
                            inner()
                        }
                    },
                )
                if (query.isNotEmpty()) {
                    Text(
                        "Hapus",
                        fontSize = 11.sp,
                        color = palette.primary,
                        modifier = Modifier
                            .clickable { query = "" }
                            .padding(4.dp),
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                NodeKind.entries.forEach { k ->
                    val active = k in activeKinds.value
                    Box(
                        modifier = Modifier
                            .background(
                                if (active) kindColors.getValue(k).copy(alpha = 0.14f) else palette.surface,
                                RoundedCornerShape(999.dp),
                            )
                            .border(
                                1.dp,
                                if (active) kindColors.getValue(k) else palette.border,
                                RoundedCornerShape(999.dp),
                            )
                            .clickable {
                                PerformHapticFeedback(HapticKind.SELECTION)
                                activeKinds.value =
                                    if (active) activeKinds.value - k else activeKinds.value + k
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text(
                            k.label,
                            fontSize = 10.sp,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                            color = if (active) kindColors.getValue(k) else palette.textMuted,
                        )
                    }
                }
            }

            if (visibleNodes.isEmpty()) {
                LabCard {
                    Text(
                        "Tidak ada node yang cocok",
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary,
                    )
                    Text(
                        "Kosongkan pencarian atau aktifkan kembali filter tipe node.",
                        fontSize = 11.sp, color = palette.textSecondary,
                    )
                }
            } else {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(palette.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(16.dp)),
                ) {
                    val density = LocalDensity.current
                    val cw = with(density) { maxWidth.toPx() }
                    val chh = with(density) { 300.dp.toPx() }
                    val hit = with(density) { 40.dp.toPx() }

                    fun mapX(p: NodePos): Float = cw / 2f + (p.x - 0.5f) * cw * 0.8f * scale + pan.x
                    fun mapY(p: NodePos): Float = chh / 2f + (p.y - 0.5f) * chh * 0.8f * scale + pan.y

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .transformable(transformState),
                    ) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        fun mapPos(id: Int): Offset {
                            val p = layout.getValue(id)
                            return Offset(
                                cx + (p.x - 0.5f) * size.width * 0.8f * scale + pan.x,
                                cy + (p.y - 0.5f) * size.height * 0.8f * scale + pan.y,
                            )
                        }
                        allEdges.forEach { e ->
                            val a = Nodes.firstOrNull { it.id == e.a }
                            val b = Nodes.firstOrNull { it.id == e.b }
                            if (a != null && b != null && a.kind in activeKinds.value && b.kind in activeKinds.value) {
                                val linked = selectedId != null && (e.a == selectedId || e.b == selectedId)
                                drawLine(
                                    color = if (linked) palette.primary else palette.border,
                                    start = mapPos(e.a),
                                    end = mapPos(e.b),
                                    strokeWidth = if (linked) 3f else 1.5f,
                                    cap = StrokeCap.Round,
                                )
                            }
                        }
                        visibleNodes.forEach { n ->
                            val c = mapPos(n.id)
                            val r = (if (n.id == selectedId) 16f else 11f) * min(scale, 2f)
                            drawCircle(kindColors.getValue(n.kind), radius = r, center = c)
                            if (n.id == selectedId) {
                                drawCircle(palette.textPrimary.copy(alpha = 0.6f), radius = r + 5f, center = c, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
                            } else if (n.id in neighborIds) {
                                drawCircle(palette.primary, radius = r + 4f, center = c, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
                            }
                        }
                    }

                    // Overlay target sentuh per node (aksesibel, tanpa hit-test manual).
                    visibleNodes.forEach { n ->
                        val p = layout.getValue(n.id)
                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        (mapX(p) - hit / 2f).roundToInt(),
                                        (mapY(p) - hit / 2f).roundToInt(),
                                    )
                                }
                                .size(with(density) { hit.toDp() })
                                .semantics { contentDescription = "Node ${n.name}, tipe ${n.kind.label}" }
                                .clickable {
                                    PerformHapticFeedback(HapticKind.SELECTION)
                                    selectedId = if (selectedId == n.id) null else n.id
                                },
                        )
                    }
                }
            }

            if (dense) {
                StatusPill("Graf terlalu padat — saring tipe node agar mudah dibaca", palette.warning)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .background(
                            if (dense) palette.primary else palette.surface,
                            RoundedCornerShape(10.dp),
                        )
                        .border(1.dp, if (dense) palette.primary else palette.border, RoundedCornerShape(10.dp))
                        .clickable {
                            PerformHapticFeedback(HapticKind.SELECTION)
                            dense = !dense
                            selectedId = null
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        if (dense) "Mode padat: aktif" else "Mode padat: mati",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (dense) palette.onPrimary else palette.textSecondary,
                    )
                }
                Box(
                    modifier = Modifier
                        .background(palette.surface, RoundedCornerShape(10.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                        .clickable {
                            PerformHapticFeedback(HapticKind.SELECTION)
                            scale = 1f
                            pan = Offset.Zero
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text("Reset tampilan", fontSize = 11.sp, color = palette.textPrimary)
                }
                Text(
                    "Zoom ${(scale * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = palette.textSecondary,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }

            LabSectionTitle("Detail node", "Ketuk node pada graf")
            if (selected == null) {
                Text("Belum ada node terpilih.", fontSize = 12.sp, color = palette.textSecondary)
            } else {
                LabCard {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            Modifier
                                .size(30.dp)
                                .background(kindColors.getValue(selected.kind), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                selected.name.take(1),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.surface,
                            )
                        }
                        Column {
                            Text(selected.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("Tipe: ${selected.kind.label}", fontSize = 11.sp, color = palette.textSecondary)
                        }
                    }
                    val neighborNames = neighborIds.mapNotNull { id -> Nodes.firstOrNull { it.id == id }?.name }
                    Text(
                        if (neighborNames.isEmpty()) "Tetangga: tidak ada" else "Tetangga (${neighborNames.size}): ${neighborNames.joinToString()}",
                        fontSize = 11.sp,
                        color = palette.textSecondary,
                    )
                }
            }

            LabSectionTitle("Daftar node", "Ketuk untuk memilih tanpa menyentuh graf")
            visibleNodes.forEach { n ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.surface, RoundedCornerShape(10.dp))
                        .border(
                            1.dp,
                            if (n.id == selectedId) palette.primary else palette.border,
                            RoundedCornerShape(10.dp),
                        )
                        .clickable {
                            PerformHapticFeedback(HapticKind.SELECTION)
                            selectedId = if (selectedId == n.id) null else n.id
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(n.name, fontSize = 12.sp, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    StatusPill(n.kind.label, kindColors.getValue(n.kind))
                }
            }
        }
    }
}
