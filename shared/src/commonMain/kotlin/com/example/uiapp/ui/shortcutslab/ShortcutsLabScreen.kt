package com.example.uiapp.ui.shortcutslab

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatTextField
import com.example.uiapp.ui.components.FlatToggle
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

// ---------------------------------------------------------------------------
// Data mock lokal (deterministik, tanpa jaringan)
// ---------------------------------------------------------------------------

private enum class ShortcutTab(val label: String) {
    EDITOR("Editor"),
    OTOMASI("Otomasi"),
    PRATINJAU("Pratinjau"),
}

private enum class CreateState { IDLE, LOADING, SUCCESS, ERROR }

private enum class IzinState(val label: String) {
    BELUM("Belum diminta"),
    MEMINTA("Sedang meminta izin…"),
    DIBERIKAN("Izin diberikan"),
    DITOLAK("Izin ditolak"),
}

private data class ShortcutAction(
    val id: String,
    val name: String,
    val category: String,
    val app: String,
)

private val actionCategories = listOf("Semua", "Komunikasi", "Media", "Rumah", "Dokumen")

private val galleryActions = listOf(
    ShortcutAction("act_pesan", "Kirim Pesan", "Komunikasi", "Pesan"),
    ShortcutAction("act_panggil", "Mulai Panggilan", "Komunikasi", "Telepon"),
    ShortcutAction("act_email", "Kirim Email", "Komunikasi", "Mail"),
    ShortcutAction("act_lokasi", "Bagikan Lokasi", "Komunikasi", "Peta"),
    ShortcutAction("act_musik", "Putar Musik", "Media", "Musik"),
    ShortcutAction("act_rekam", "Rekam Suara", "Media", "Diktafon"),
    ShortcutAction("act_foto", "Ambil Foto", "Media", "Kamera"),
    ShortcutAction("act_lampu", "Nyalakan Lampu", "Rumah", "Rumah"),
    ShortcutAction("act_scene", "Jalankan Scene", "Rumah", "Rumah"),
    ShortcutAction("act_dokumen", "Buat Dokumen", "Dokumen", "File"),
    ShortcutAction("act_pdf", "Ekspor PDF", "Dokumen", "File"),
    ShortcutAction("act_catatan", "Tulis Catatan", "Dokumen", "Catatan"),
)

private val workspaceSeed = listOf(
    galleryActions[0],
    galleryActions[4],
)

private data class AutomationRule(
    val id: String,
    val kind: String,
    val triggerTitle: String,
    val triggerDetail: String,
    val actionTitle: String,
    val enabled: Boolean,
)

private val automationSeed = listOf(
    AutomationRule(
        id = "auto_1",
        kind = "Waktu",
        triggerTitle = "Setiap hari 06:30",
        triggerDetail = "Waktu (jadwal)",
        actionTitle = "Jalankan shortcut “Selamat Pagi”",
        enabled = true,
    ),
    AutomationRule(
        id = "auto_2",
        kind = "Lokasi",
        triggerTitle = "Saat tiba di Rumah",
        triggerDetail = "Lokasi (simulasi, tanpa GPS)",
        actionTitle = "Nyalakan Lampu Ruang Tamu",
        enabled = true,
    ),
    AutomationRule(
        id = "auto_3",
        kind = "Aplikasi",
        triggerTitle = "Saat membuka Musik",
        triggerDetail = "Aplikasi",
        actionTitle = "Aktifkan mode Fokus Kerja",
        enabled = false,
    ),
    AutomationRule(
        id = "auto_4",
        kind = "Siri",
        triggerTitle = "Frasa Siri: “Pulang sekarang”",
        triggerDetail = "Frasa Siri (simulasi)",
        actionTitle = "Jalankan scene Waktunya Tidur",
        enabled = true,
    ),
)

private val automationPool = listOf(
    AutomationRule(
        id = "auto_5",
        kind = "Waktu",
        triggerTitle = "Setiap Jumat 17:00",
        triggerDetail = "Waktu (jadwal)",
        actionTitle = "Kirim pesan “Pulang sekarang”",
        enabled = false,
    ),
    AutomationRule(
        id = "auto_6",
        kind = "Lokasi",
        triggerTitle = "Saat meninggalkan Kantor",
        triggerDetail = "Lokasi (simulasi, tanpa GPS)",
        actionTitle = "Putar daftar putar “Santai”",
        enabled = false,
    ),
)

private data class AppShortcutPreview(
    val id: String,
    val app: String,
    val title: String,
    val detail: String,
)

private val appPreviews = listOf(
    AppShortcutPreview("prev_pesan", "Pesan", "Kirim Ucapan", "Template pesan ke grup keluarga"),
    AppShortcutPreview("prev_musik", "Musik", "Putar Lagu Favorit", "Daftar putar “Santai Sore”"),
    AppShortcutPreview("prev_rumah", "Rumah", "Matikan Semua Lampu", "Scene Waktunya Tidur"),
    AppShortcutPreview("prev_peta", "Peta", "Rute Pulang", "Estimasi 24 menit (simulasi)"),
)

// ---------------------------------------------------------------------------
// Layar utama
// ---------------------------------------------------------------------------

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShortcutsLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val density = LocalDensity.current

    var tab by remember { mutableStateOf(ShortcutTab.EDITOR) }

    // --- Editor: pencarian, galeri, ruang kerja, drag ghost ---
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    val workspace = remember { mutableStateListOf<ShortcutAction>().apply { addAll(workspaceSeed) } }
    var draggedAction by remember { mutableStateOf<ShortcutAction?>(null) }
    var dragPos by remember { mutableStateOf(Offset.Zero) }
    var overlayOrigin by remember { mutableStateOf(Offset.Zero) }
    var workspaceTopY by remember { mutableFloatStateOf(0f) }
    var liftedId by remember { mutableStateOf<String?>(null) }
    var reorderAccum by remember { mutableFloatStateOf(0f) }
    var editorStatus by remember { mutableStateOf("Seret langkah ke ruang kerja, atau ketuk tombol tambah.") }

    val reorderThresholdPx = remember(density) { with(density) { 64.dp.toPx() } }

    val filteredActions = remember(query, selectedCategory) {
        galleryActions.filter { action ->
            val matchesCategory = selectedCategory == "Semua" || action.category == selectedCategory
            val matchesQuery = query.isBlank() ||
                action.name.contains(query, ignoreCase = true) ||
                action.app.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    // --- Otomasi ---
    val rules = remember { mutableStateListOf<AutomationRule>().apply { addAll(automationSeed) } }
    val rulePool = remember { mutableStateListOf<AutomationRule>().apply { addAll(automationPool) } }
    var createState by remember { mutableStateOf(CreateState.IDLE) }
    var createTick by remember { mutableIntStateOf(0) }
    var simulateCreateFail by remember { mutableStateOf(false) }

    // --- Pratinjau & izin aktivasi ---
    var izinState by remember { mutableStateOf(IzinState.BELUM) }
    var izinTick by remember { mutableIntStateOf(0) }
    var simulateIzinDitolak by remember { mutableStateOf(false) }
    var runStatus by remember { mutableStateOf("Belum ada shortcut yang dijalankan.") }

    LaunchedEffect(createTick) {
        if (createTick > 0) {
            createState = CreateState.LOADING
            delay(1100)
            if (simulateCreateFail) {
                createState = CreateState.ERROR
            } else {
                val next = rulePool.removeFirstOrNull()
                if (next != null) rules.add(next)
                createState = CreateState.SUCCESS
            }
        }
    }

    LaunchedEffect(izinTick) {
        if (izinTick > 0) {
            izinState = IzinState.MEMINTA
            delay(1400)
            izinState = if (simulateIzinDitolak) IzinState.DITOLAK else IzinState.DIBERIKAN
        }
    }

    fun addToWorkspace(action: ShortcutAction) {
        if (workspace.any { it.id == action.id }) {
            editorStatus = "“${action.name}” sudah ada di ruang kerja."
        } else {
            workspace.add(action)
            editorStatus = "“${action.name}” ditambahkan ke ruang kerja."
        }
    }

    fun dropGhost() {
        val action = draggedAction
        if (action != null) {
            if (dragPos.y >= workspaceTopY) {
                addToWorkspace(action)
            } else {
                editorStatus = "Drop di luar ruang kerja — “${action.name}” batal ditambahkan."
            }
        }
        draggedAction = null
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Shortcuts & Automation",
                subtitle = "Galeri aksi, drag reorder, trigger — Simulasi",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .onGloballyPositioned { overlayOrigin = it.positionInRoot() },
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ShortcutTab.entries.forEach { option ->
                        val selected = option == tab
                        Surface(
                            onClick = { tab = option },
                            shape = RoundedCornerShape(999.dp),
                            color = if (selected) palette.primary else palette.surface,
                            border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .semantics { contentDescription = "Tab ${option.label}" },
                        ) {
                            Text(
                                text = option.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = if (selected) palette.onPrimary else palette.textSecondary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 9.dp),
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (tab) {
                        ShortcutTab.EDITOR -> EditorTab(
                        query = query,
                        onQueryChange = { query = it },
                        selectedCategory = selectedCategory,
                        onCategoryChange = { selectedCategory = it },
                        filteredActions = filteredActions,
                        editorStatus = editorStatus,
                        workspace = workspace,
                        liftedId = liftedId,
                        onChipDragStart = { action, rootPos ->
                            draggedAction = action
                            dragPos = rootPos
                        },
                        onChipDrag = { amount ->
                            dragPos = Offset(dragPos.x + amount.x, dragPos.y + amount.y)
                        },
                        onChipDragEnd = { dropGhost() },
                        onChipDragCancel = { draggedAction = null },
                        onAdd = { addToWorkspace(it) },
                        onClearQuery = {
                            query = ""
                            selectedCategory = "Semua"
                        },
                        onWorkspaceTop = { workspaceTopY = it },
                        onHandleDragStart = { liftedId = it },
                        onHandleDragEnd = {
                            liftedId = null
                            reorderAccum = 0f
                        },
                        onHandleDragAmount = { item, amountY ->
                            reorderAccum += amountY
                            val index = workspace.indexOfFirst { it.id == item.id }
                            if (reorderAccum < -reorderThresholdPx && index > 0) {
                                workspace.removeAt(index)
                                workspace.add(index - 1, item)
                                reorderAccum = 0f
                                editorStatus = "Urutan langkah diubah (animasi reorder)."
                            } else if (reorderAccum > reorderThresholdPx && index in 0 until workspace.size - 1) {
                                workspace.removeAt(index)
                                workspace.add(index + 1, item)
                                reorderAccum = 0f
                                editorStatus = "Urutan langkah diubah (animasi reorder)."
                            }
                        },
                        onRemove = { item ->
                            workspace.removeAll { it.id == item.id }
                            editorStatus = "“${item.name}” dihapus dari ruang kerja."
                        },
                    )

                    ShortcutTab.OTOMASI -> OtomasiTab(
                        rules = rules,
                        createState = createState,
                        simulateCreateFail = simulateCreateFail,
                        onSimulateFailChange = { simulateCreateFail = it },
                        onCreate = { createTick++ },
                        onToggleRule = { rule, enabled ->
                            val index = rules.indexOfFirst { it.id == rule.id }
                            if (index >= 0) rules[index] = rules[index].copy(enabled = enabled)
                        },
                        onDeleteRule = { rule ->
                            rules.removeAll { it.id == rule.id }
                            rulePool.add(rule.copy(enabled = false))
                        },
                        canCreate = rulePool.isNotEmpty(),
                    )

                    ShortcutTab.PRATINJAU -> PratinjauTab(
                        previews = appPreviews,
                        izinState = izinState,
                        simulateIzinDitolak = simulateIzinDitolak,
                        onSimulateIzinChange = { simulateIzinDitolak = it },
                        onRequestIzin = { izinTick++ },
                        onRevokeIzin = { izinState = IzinState.BELUM },
                        runStatus = runStatus,
                        onRunPreview = { preview ->
                            runStatus = "“${preview.title}” (${preview.app}) dijalankan — simulasi, tanpa membuka aplikasi asli."
                        },
                    )
                    }
                }

                // Bottom bar aksi
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .background(palette.surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(palette.border),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${workspace.size} langkah di ruang kerja",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary,
                            )
                            Text(
                                text = if (workspace.isEmpty()) {
                                    "Ruang kerja kosong — tombol jalankan dinonaktifkan."
                                } else {
                                    "Urutan dieksekusi sesuai posisi daftar."
                                },
                                fontSize = 10.sp,
                                color = palette.textMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        SimulasiPill()
                    }
                    Surface(
                        onClick = {
                            val chain = workspace.joinToString(" → ") { it.name }
                            runStatus = "Shortcut dijalankan (Simulasi): $chain"
                        },
                        enabled = workspace.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (workspace.isNotEmpty()) {
                            palette.primary
                        } else {
                            palette.primary.copy(alpha = 0.4f)
                        },
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "Jalankan shortcut" },
                    ) {
                        Text(
                            text = "Jalankan Shortcut (Simulasi)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                    }
                }
            }

            // Ghost drag yang mengikuti jari saat langkah diseret ke ruang kerja.
            draggedAction?.let { action ->
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = palette.primary,
                    border = BorderStroke(1.dp, palette.primary),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (dragPos.x - overlayOrigin.x).roundToInt(),
                                (dragPos.y - overlayOrigin.y).roundToInt(),
                            )
                        }
                        .graphicsLayer {
                            alpha = 0.92f
                            scaleX = 1.06f
                            scaleY = 1.06f
                        },
                ) {
                    Text(
                        text = action.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.onPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Tab Editor
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditorTab(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    filteredActions: List<ShortcutAction>,
    editorStatus: String,
    workspace: List<ShortcutAction>,
    liftedId: String?,
    onChipDragStart: (ShortcutAction, Offset) -> Unit,
    onChipDrag: (Offset) -> Unit,
    onChipDragEnd: () -> Unit,
    onChipDragCancel: () -> Unit,
    onAdd: (ShortcutAction) -> Unit,
    onClearQuery: () -> Unit,
    onWorkspaceTop: (Float) -> Unit,
    onHandleDragStart: (String) -> Unit,
    onHandleDragEnd: () -> Unit,
    onHandleDragAmount: (ShortcutAction, Float) -> Unit,
    onRemove: (ShortcutAction) -> Unit,
) {
    val palette = LocalAppPalette.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        FlatTextField(
            label = "Cari langkah",
            value = query,
            onValueChange = onQueryChange,
            placeholder = "mis. Kirim Pesan",
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            actionCategories.forEach { category ->
                val selected = category == selectedCategory
                Surface(
                    onClick = { onCategoryChange(category) },
                    shape = RoundedCornerShape(999.dp),
                    color = if (selected) palette.primaryContainer else palette.surfaceMuted,
                    border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier.semantics { contentDescription = "Kategori $category" },
                ) {
                    Text(
                        text = category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) palette.onPrimaryContainer else palette.textSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
        }

        Text(
            text = "Galeri Langkah — seret kartu ke ruang kerja, atau ketuk tombol tambah",
            fontSize = 11.sp,
            color = palette.textMuted,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (filteredActions.isEmpty()) {
                LabCard {
                    Text(
                        "Tidak ada langkah yang cocok",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Text(
                        "Kosong: pencarian “$query” pada kategori $selectedCategory tidak menemukan hasil.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                    SmallActionButton(text = "Bersihkan Pencarian", onClick = onClearQuery)
                }
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    filteredActions.forEach { action ->
                        GalleryActionChip(
                            action = action,
                            onDragStart = { rootPos -> onChipDragStart(action, rootPos) },
                            onDrag = onChipDrag,
                            onDragEnd = onChipDragEnd,
                            onDragCancel = onChipDragCancel,
                            onAdd = { onAdd(action) },
                        )
                    }
                }
            }
        }

        Text(
            text = editorStatus,
            fontSize = 11.sp,
            color = palette.textSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        // Ruang kerja shortcut (drag reorder dengan animasi)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { onWorkspaceTop(it.positionInRoot().y) }
                .clip(RoundedCornerShape(16.dp))
                .background(palette.surface)
                .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LabSectionTitle(
                title = "Ruang Kerja Shortcut",
                subtitle = "Seret gagang untuk mengurutkan ulang",
            )
            if (workspace.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Ruang kerja kosong — seret langkah ke sini (state kosong).",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(workspace, key = { it.id }) { item ->
                        WorkspaceStepRow(
                            item = item,
                            lifted = liftedId == item.id,
                            onDragStart = { onHandleDragStart(item.id) },
                            onDragEnd = onHandleDragEnd,
                            onDragAmount = { amountY -> onHandleDragAmount(item, amountY) },
                            onRemove = { onRemove(item) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryActionChip(
    action: ShortcutAction,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onAdd: () -> Unit,
) {
    val palette = LocalAppPalette.current
    var chipRootPos by remember { mutableStateOf(Offset.Zero) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .width(168.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.surface)
                .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                .onGloballyPositioned { chipRootPos = it.positionInRoot() }
                .pointerInput(action.id) {
                    detectDragGestures(
                        onDragStart = { onDragStart(chipRootPos) },
                        onDragEnd = onDragEnd,
                        onDragCancel = onDragCancel,
                        onDrag = { change, amount ->
                            change.consume()
                            onDrag(amount)
                        },
                    )
                }
                .padding(10.dp)
                .semantics {
                    contentDescription =
                        "Langkah ${action.name} dari aplikasi ${action.app}, seret untuk ditambahkan ke ruang kerja"
                },
        ) {
            ActionCategoryIcon(category = action.category, tint = palette.primary)
            Column {
                Text(
                    text = action.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${action.app} · ${action.category}",
                    fontSize = 9.sp,
                    color = palette.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(palette.primary)
                .clickable { onAdd() }
                .semantics { contentDescription = "Tambah ${action.name} ke ruang kerja" },
            contentAlignment = Alignment.Center,
        ) {
            Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
        }
    }
}

@Composable
private fun LazyItemScope.WorkspaceStepRow(
    item: ShortcutAction,
    lifted: Boolean,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDragAmount: (Float) -> Unit,
    onRemove: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val liftScale by animateLiftScale(lifted)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateItem()
            .graphicsLayer {
                scaleX = liftScale
                scaleY = liftScale
            }
            .clip(RoundedCornerShape(12.dp))
            .background(if (lifted) palette.primaryContainer else palette.surfaceMuted)
            .border(
                BorderStroke(if (lifted) 2.dp else 1.dp, if (lifted) palette.primary else palette.border),
                RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 10.dp, vertical = 10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .pointerInput(item.id) {
                    detectDragGestures(
                        onDragStart = { onDragStart() },
                        onDragEnd = onDragEnd,
                        onDragCancel = onDragEnd,
                        onDrag = { change, amount ->
                            change.consume()
                            onDragAmount(amount.y)
                        },
                    )
                }
                .semantics {
                    contentDescription = "Gagang urutan langkah ${item.name}, seret untuk mengubah urutan"
                },
            contentAlignment = Alignment.Center,
        ) {
            DragHandleIcon(tint = palette.textMuted)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (lifted) "Sedang diangkat — seret ke atas/bawah" else item.app,
                fontSize = 10.sp,
                color = palette.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = "Hapus",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = palette.danger,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onRemove() }
                .padding(6.dp)
                .semantics { contentDescription = "Hapus langkah ${item.name}" },
        )
    }
}

@Composable
private fun animateLiftScale(lifted: Boolean): androidx.compose.runtime.State<Float> {
    return androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (lifted) 1.04f else 1f,
        animationSpec = androidx.compose.animation.core.spring(
            stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow,
        ),
        label = "workspaceLift",
    )
}

// ---------------------------------------------------------------------------
// Tab Otomasi
// ---------------------------------------------------------------------------

@Composable
private fun OtomasiTab(
    rules: List<AutomationRule>,
    createState: CreateState,
    simulateCreateFail: Boolean,
    onSimulateFailChange: (Boolean) -> Unit,
    onCreate: () -> Unit,
    onToggleRule: (AutomationRule, Boolean) -> Unit,
    onDeleteRule: (AutomationRule) -> Unit,
    canCreate: Boolean,
) {
    val palette = LocalAppPalette.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        LabIntroCard(
            title = "Otomasi (Simulasi)",
            body = "Trigger waktu, lokasi, aplikasi, dan frasa Siri berjalan penuh di perangkat mock — tanpa GPS, tanpa integrasi sistem.",
        )

        if (rules.isEmpty()) {
            LabCard {
                Text(
                    "Belum ada otomasi",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )
                Text(
                    "State kosong: buat otomasi pertama lewat tombol di bawah.",
                    fontSize = 11.sp,
                    color = palette.textSecondary,
                )
            }
        }

        rules.forEach { rule ->
            LabCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    AutomationKindIcon(kind = rule.kind, tint = palette.primary)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            rule.triggerTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            "${rule.triggerDetail} · ${rule.actionTitle}",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = palette.textSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = if (rule.enabled) "Status: Aktif" else "Status: Nonaktif",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (rule.enabled) palette.success else palette.textMuted,
                        )
                    }
                    FlatToggle(
                        checked = rule.enabled,
                        onCheckedChange = { onToggleRule(rule, it) },
                        modifier = Modifier.semantics {
                            contentDescription = "Otomasi ${rule.triggerTitle}"
                        },
                    )
                }
                Text(
                    text = "Hapus otomasi",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.danger,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onDeleteRule(rule) }
                        .padding(6.dp)
                        .semantics { contentDescription = "Hapus otomasi ${rule.triggerTitle}" },
                )
            }
        }

        LabCard {
            LabSectionTitle(
                title = "Buat Otomasi Baru",
                subtitle = "Penyimpanan mock, tanpa layanan sistem",
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Simulasikan kegagalan simpan",
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                    modifier = Modifier.weight(1f),
                )
                FlatToggle(
                    checked = simulateCreateFail,
                    onCheckedChange = onSimulateFailChange,
                    modifier = Modifier.semantics {
                        contentDescription = "Simulasikan kegagalan simpan otomasi"
                    },
                )
            }
            val statusText = when (createState) {
                CreateState.IDLE -> "Siap membuat otomasi baru."
                CreateState.LOADING -> "Menyimpan otomasi… (loading)"
                CreateState.SUCCESS -> "Otomasi baru tersimpan (simulasi sukses)."
                CreateState.ERROR -> "Gagal menyimpan otomasi (simulasi gagal)."
            }
            val statusColor = when (createState) {
                CreateState.ERROR -> palette.danger
                CreateState.SUCCESS -> palette.success
                else -> palette.textMuted
            }
            Text(statusText, fontSize = 11.sp, color = statusColor)
            SmallActionButton(
                text = when (createState) {
                    CreateState.LOADING -> "Menyimpan…"
                    CreateState.ERROR -> "Coba Lagi"
                    else -> "Buat Otomasi (Simulasi)"
                },
                onClick = onCreate,
                enabled = createState != CreateState.LOADING && (canCreate || createState == CreateState.ERROR),
            )
            if (!canCreate && createState != CreateState.ERROR) {
                Text(
                    "Semua contoh otomasi sudah dibuat — tombol dinonaktifkan (disabled).",
                    fontSize = 11.sp,
                    color = palette.textMuted,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Tab Pratinjau & Izin
// ---------------------------------------------------------------------------

@Composable
private fun PratinjauTab(
    previews: List<AppShortcutPreview>,
    izinState: IzinState,
    simulateIzinDitolak: Boolean,
    onSimulateIzinChange: (Boolean) -> Unit,
    onRequestIzin: () -> Unit,
    onRevokeIzin: () -> Unit,
    runStatus: String,
    onRunPreview: (AppShortcutPreview) -> Unit,
) {
    val palette = LocalAppPalette.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        LabCard {
            LabSectionTitle(
                title = "Galeri Shortcut Lintas Aplikasi",
                subtitle = "Pratinjau kartu dari berbagai aplikasi mock",
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                previews.forEach { preview ->
                    PreviewCard(preview = preview, onRun = { onRunPreview(preview) })
                }
            }
            Text(
                text = runStatus,
                fontSize = 11.sp,
                color = palette.textSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }

        LabCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LabSectionTitle(
                    title = "Izin Aktivasi",
                    subtitle = "Izin otomasi latar belakang (simulasi)",
                    modifier = Modifier.weight(1f),
                )
                SimulasiPill()
            }

            val izinColor = when (izinState) {
                IzinState.DIBERIKAN -> palette.success
                IzinState.DITOLAK -> palette.danger
                IzinState.MEMINTA -> palette.info
                IzinState.BELUM -> palette.textMuted
            }
            StatusPill(text = izinState.label, color = izinColor)

            Text(
                text = when (izinState) {
                    IzinState.BELUM -> "Otomasi belum dapat berjalan di latar belakang sampai izin diberikan."
                    IzinState.MEMINTA -> "Menunggu keputusan izin (loading)…"
                    IzinState.DIBERIKAN -> "Izin diberikan — otomasi simulasi boleh berjalan di latar belakang."
                    IzinState.DITOLAK -> "Izin ditolak (gagal) — aktifkan manual atau coba lagi."
                },
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = palette.textSecondary,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Simulasikan izin ditolak",
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                    modifier = Modifier.weight(1f),
                )
                FlatToggle(
                    checked = simulateIzinDitolak,
                    onCheckedChange = onSimulateIzinChange,
                    modifier = Modifier.semantics {
                        contentDescription = "Simulasikan izin ditolak"
                    },
                )
            }

            if (izinState == IzinState.DIBERIKAN) {
                SmallActionButton(text = "Cabut Izin (Simulasi)", onClick = onRevokeIzin)
            } else {
                SmallActionButton(
                    text = when (izinState) {
                        IzinState.MEMINTA -> "Meminta Izin…"
                        IzinState.DITOLAK -> "Coba Lagi"
                        else -> "Minta Izin Aktivasi (Simulasi)"
                    },
                    onClick = onRequestIzin,
                    enabled = izinState != IzinState.MEMINTA,
                )
            }
        }
    }
}

@Composable
private fun PreviewCard(preview: AppShortcutPreview, onRun: () -> Unit) {
    val palette = LocalAppPalette.current
    Column(
        modifier = Modifier
            .width(190.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppTileIcon(app = preview.app, tint = palette.primary)
            Column {
                Text(
                    preview.app,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    preview.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Text(
            preview.detail,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            color = palette.textMuted,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        SmallActionButton(
            text = "Jalankan",
            onClick = onRun,
            modifier = Modifier.semantics {
                contentDescription = "Jalankan pratinjau ${preview.title}"
            },
        )
    }
}

// ---------------------------------------------------------------------------
// Ikon Canvas & pill
// ---------------------------------------------------------------------------

@Composable
private fun ActionCategoryIcon(category: String, tint: Color) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (category) {
            "Komunikasi" -> {
                // Gelembung chat
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.12f, h * 0.18f),
                    size = Size(w * 0.76f, h * 0.5f),
                    cornerRadius = CornerRadius(w * 0.16f, w * 0.16f),
                    style = stroke,
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.3f, h * 0.72f),
                    end = Offset(w * 0.28f, h * 0.88f),
                    strokeWidth = w * 0.09f,
                    cap = StrokeCap.Round,
                )
            }
            "Media" -> {
                // Segitiga play
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.35f, h * 0.22f)
                    lineTo(w * 0.78f, h * 0.5f)
                    lineTo(w * 0.35f, h * 0.78f)
                    close()
                }
                drawPath(path = path, color = tint, style = stroke)
            }
            "Rumah" -> {
                // Atap + badan rumah
                drawLine(
                    color = tint,
                    start = Offset(w * 0.15f, h * 0.48f),
                    end = Offset(w * 0.5f, h * 0.18f),
                    strokeWidth = w * 0.09f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.5f, h * 0.18f),
                    end = Offset(w * 0.85f, h * 0.48f),
                    strokeWidth = w * 0.09f,
                    cap = StrokeCap.Round,
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.22f, h * 0.46f),
                    size = Size(w * 0.56f, h * 0.38f),
                    cornerRadius = CornerRadius(w * 0.06f, w * 0.06f),
                    style = stroke,
                )
            }
            else -> {
                // Dokumen dengan garis teks
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.22f, h * 0.14f),
                    size = Size(w * 0.56f, h * 0.72f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
                    style = stroke,
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.34f, h * 0.42f),
                    end = Offset(w * 0.66f, h * 0.42f),
                    strokeWidth = w * 0.08f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.34f, h * 0.6f),
                    end = Offset(w * 0.66f, h * 0.6f),
                    strokeWidth = w * 0.08f,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
private fun AutomationKindIcon(kind: String, tint: Color) {
    Canvas(modifier = Modifier.size(34.dp)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.07f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (kind) {
            "Waktu" -> {
                drawCircle(color = tint, radius = w * 0.34f, center = Offset(w / 2f, h / 2f), style = stroke)
                drawLine(
                    color = tint,
                    start = Offset(w / 2f, h / 2f),
                    end = Offset(w / 2f, h * 0.32f),
                    strokeWidth = w * 0.07f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = tint,
                    start = Offset(w / 2f, h / 2f),
                    end = Offset(w * 0.66f, h / 2f),
                    strokeWidth = w * 0.07f,
                    cap = StrokeCap.Round,
                )
            }
            "Lokasi" -> {
                drawArc(
                    color = tint,
                    startAngle = 30f,
                    sweepAngle = 300f,
                    useCenter = false,
                    topLeft = Offset(w * 0.22f, h * 0.12f),
                    size = Size(w * 0.56f, h * 0.56f),
                    style = stroke,
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.3f, h * 0.5f),
                    end = Offset(w * 0.5f, h * 0.88f),
                    strokeWidth = w * 0.07f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.7f, h * 0.5f),
                    end = Offset(w * 0.5f, h * 0.88f),
                    strokeWidth = w * 0.07f,
                    cap = StrokeCap.Round,
                )
            }
            "Aplikasi" -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.18f, h * 0.18f),
                    size = Size(w * 0.64f, h * 0.64f),
                    cornerRadius = CornerRadius(w * 0.18f, w * 0.18f),
                    style = stroke,
                )
                drawCircle(color = tint, radius = w * 0.08f, center = Offset(w / 2f, h / 2f))
            }
            else -> {
                // Siri: gelombang suara
                val xs = floatArrayOf(0.3f, 0.42f, 0.54f, 0.66f)
                val hs = floatArrayOf(0.18f, 0.34f, 0.26f, 0.16f)
                for (index in xs.indices) {
                    drawLine(
                        color = tint,
                        start = Offset(xs[index] * w, h * (0.5f - hs[index])),
                        end = Offset(xs[index] * w, h * (0.5f + hs[index])),
                        strokeWidth = w * 0.07f,
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

@Composable
private fun DragHandleIcon(tint: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val w = this.size.width
        val h = this.size.height
        for (row in 0 until 3) {
            val y = h * (0.3f + row * 0.2f)
            drawLine(
                color = tint,
                start = Offset(w * 0.15f, y),
                end = Offset(w * 0.85f, y),
                strokeWidth = h * 0.1f,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun AppTileIcon(app: String, tint: Color) {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(tint.copy(alpha = 0.12f))
            .border(1.dp, tint, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = app.take(1),
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = tint,
        )
    }
}

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
