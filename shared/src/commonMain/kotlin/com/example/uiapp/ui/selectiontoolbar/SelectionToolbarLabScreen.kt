package com.example.uiapp.ui.selectiontoolbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

enum class ContextSelectionMode(val title: String) {
    LIST("Item & Dokumen"),
    MEDIA("Galeri Media"),
    TEXT("Kutipan Teks"),
}

data class SelectableItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val tag: String,
    var isPinned: Boolean = false,
    var isArchived: Boolean = false,
)

data class MediaItem(
    val id: String,
    val title: String,
    val size: String,
    val colorAccent: Color,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SelectionToolbarLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current
    var selectedMode by remember { mutableStateOf(ContextSelectionMode.LIST) }
    var actionNotification by remember { mutableStateOf<String?>(null) }

    // List State
    val listItems = remember {
        mutableStateListOf(
            SelectableItem("1", "Sprint Review Q3.pdf", "Dokumen perencanaan sprint", "PDF", isPinned = true),
            SelectableItem("2", "Desain Flat Design Tokens.kt", "Source file palet tema", "KOTLIN"),
            SelectableItem("3", "Panduan Safe Insets Compose.md", "Dokumentasi padding navigasi", "DOCS"),
            SelectableItem("4", "Kueri Cache Database.sql", "Migrasi skema pengguna", "SQL"),
            SelectableItem("5", "Laporan Performa GPU.xlsx", "Analisis FPS dan memori", "EXCEL", isPinned = true),
            SelectableItem("6", "Arsitektur KMP Unidirectional.png", "Diagram alur data", "IMAGE"),
        )
    }
    val selectedItemIds = remember { mutableStateListOf<String>() }

    // Media State
    val mediaItems = remember {
        mutableStateListOf(
            MediaItem("m1", "Mockup_Dashboard.png", "2.4 MB", Color(0xFF4A90E2)),
            MediaItem("m2", "Banner_Promo.webp", "1.1 MB", Color(0xFF50E3C2)),
            MediaItem("m3", "Hero_Background.jpg", "3.8 MB", Color(0xFFF5A623)),
            MediaItem("m4", "Icon_App_1024.png", "840 KB", Color(0xFF9013FE)),
            MediaItem("m5", "Chart_Preview.png", "1.6 MB", Color(0xFFE91E63)),
            MediaItem("m6", "Design_System.svg", "420 KB", Color(0xFF009688)),
        )
    }
    val selectedMediaIds = remember { mutableStateListOf<String>() }

    // Text Snippet State
    var textSelectedRange by remember { mutableStateOf<String?>("Compose Multiplatform menyediakan shared UI lintas platform") }

    val isInSelection = when (selectedMode) {
        ContextSelectionMode.LIST -> selectedItemIds.isNotEmpty()
        ContextSelectionMode.MEDIA -> selectedMediaIds.isNotEmpty()
        ContextSelectionMode.TEXT -> textSelectedRange != null
    }

    // Back saat mode seleksi aktif harus keluar dari seleksi dulu, bukan menutup lab.
    BackHandler(enabled = isInSelection) {
        selectedItemIds.clear()
        selectedMediaIds.clear()
        textSelectedRange = null
    }
    BackHandler(enabled = true) { onBack() }

    val selectedCount = when (selectedMode) {
        ContextSelectionMode.LIST -> selectedItemIds.size
        ContextSelectionMode.MEDIA -> selectedMediaIds.size
        ContextSelectionMode.TEXT -> if (textSelectedRange != null) 1 else 0
    }

    fun toggleItem(id: String) {
        if (selectedItemIds.contains(id)) {
            selectedItemIds.remove(id)
        } else {
            selectedItemIds.add(id)
        }
    }

    fun toggleMedia(id: String) {
        if (selectedMediaIds.contains(id)) {
            selectedMediaIds.remove(id)
        } else {
            selectedMediaIds.add(id)
        }
    }

    fun selectAll() {
        when (selectedMode) {
            ContextSelectionMode.LIST -> {
                selectedItemIds.clear()
                selectedItemIds.addAll(listItems.map { it.id })
            }
            ContextSelectionMode.MEDIA -> {
                selectedMediaIds.clear()
                selectedMediaIds.addAll(mediaItems.map { it.id })
            }
            ContextSelectionMode.TEXT -> {}
        }
    }

    fun clearSelection() {
        selectedItemIds.clear()
        selectedMediaIds.clear()
        if (selectedMode == ContextSelectionMode.TEXT) {
            textSelectedRange = null
        }
    }

    fun executeAction(name: String) {
        actionNotification = "$name berhasil pada $selectedCount item!"
        if (name == "Hapus") {
            if (selectedMode == ContextSelectionMode.LIST) {
                listItems.removeAll { selectedItemIds.contains(it.id) }
                selectedItemIds.clear()
            } else if (selectedMode == ContextSelectionMode.MEDIA) {
                mediaItems.removeAll { selectedMediaIds.contains(it.id) }
                selectedMediaIds.clear()
            }
        } else if (name == "Pin / Unpin") {
            if (selectedMode == ContextSelectionMode.LIST) {
                listItems.forEach { item ->
                    if (selectedItemIds.contains(item.id)) {
                        item.isPinned = !item.isPinned
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Contextual Selection",
                subtitle = "Toolbar kontekstual & pemilihan massal",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
        ) {
            // Mode Segmented Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ContextSelectionMode.entries.forEach { mode ->
                    val isSelected = selectedMode == mode
                    Surface(
                        onClick = {
                            selectedMode = mode
                            clearSelection()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) palette.primary else palette.surface,
                        border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                        modifier = Modifier.weight(1f),
                        shadowElevation = 0.dp,
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = mode.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) palette.onPrimary else palette.textSecondary,
                            )
                        }
                    }
                }
            }

            // Notification pill if action triggered
            AnimatedVisibility(visible = actionNotification != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.primaryContainer)
                        .border(1.dp, palette.primary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = actionNotification ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.onPrimaryContainer,
                        )
                        Text(
                            text = "Tutup",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary,
                            modifier = Modifier.clickable { actionNotification = null },
                        )
                    }
                }
            }

            // Contextual Floating Action Toolbar
            AnimatedVisibility(
                visible = isInSelection,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.primary),
                    shadowElevation = 0.dp,
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(palette.primary),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "$selectedCount",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.onPrimary,
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "$selectedCount item terpilih",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (selectedMode != ContextSelectionMode.TEXT) {
                                    Text(
                                        text = "Pilih Semua",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.primary,
                                        modifier = Modifier.clickable { selectAll() },
                                    )
                                }
                                Text(
                                    text = "Batal",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textMuted,
                                    modifier = Modifier.clickable { clearSelection() },
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Contextual Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            if (selectedMode == ContextSelectionMode.LIST) {
                                ActionButton(
                                    label = "Pin",
                                    icon = "📌",
                                    palette = palette,
                                    modifier = Modifier.weight(1f),
                                    onClick = { executeAction("Pin / Unpin") },
                                )
                                ActionButton(
                                    label = "Bagikan",
                                    icon = "↗",
                                    palette = palette,
                                    modifier = Modifier.weight(1f),
                                    onClick = { executeAction("Bagikan") },
                                )
                                ActionButton(
                                    label = "Arsip",
                                    icon = "📦",
                                    palette = palette,
                                    modifier = Modifier.weight(1f),
                                    onClick = { executeAction("Arsip") },
                                )
                                ActionButton(
                                    label = "Hapus",
                                    icon = "🗑️",
                                    palette = palette,
                                    isDestructive = true,
                                    modifier = Modifier.weight(1f),
                                    onClick = { executeAction("Hapus") },
                                )
                            } else if (selectedMode == ContextSelectionMode.MEDIA) {
                                ActionButton(
                                    label = "Unduh",
                                    icon = "⬇",
                                    palette = palette,
                                    modifier = Modifier.weight(1f),
                                    onClick = { executeAction("Unduh") },
                                )
                                ActionButton(
                                    label = "Favorit",
                                    icon = "★",
                                    palette = palette,
                                    modifier = Modifier.weight(1f),
                                    onClick = { executeAction("Favoritkan") },
                                )
                                ActionButton(
                                    label = "Hapus",
                                    icon = "🗑️",
                                    palette = palette,
                                    isDestructive = true,
                                    modifier = Modifier.weight(1f),
                                    onClick = { executeAction("Hapus") },
                                )
                            } else {
                                ActionButton(
                                    label = "Salin",
                                    icon = "📋",
                                    palette = palette,
                                    modifier = Modifier.weight(1f),
                                    onClick = { executeAction("Salin Teks") },
                                )
                                ActionButton(
                                    label = "Sorot",
                                    icon = "🖍️",
                                    palette = palette,
                                    modifier = Modifier.weight(1f),
                                    onClick = { executeAction("Sorot") },
                                )
                                ActionButton(
                                    label = "Kamus",
                                    icon = "📖",
                                    palette = palette,
                                    modifier = Modifier.weight(1f),
                                    onClick = { executeAction("Cari Definisi") },
                                )
                            }
                        }
                    }
                }
            }

            // Main Content Area based on Selected Mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                when (selectedMode) {
                    ContextSelectionMode.LIST -> {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            item {
                                Text(
                                    text = "Tekan lama item atau klik untuk memilih:",
                                    fontSize = 12.sp,
                                    color = palette.textMuted,
                                    modifier = Modifier.padding(bottom = 4.dp),
                                )
                            }
                            items(listItems, key = { it.id }) { item ->
                                val isSelected = selectedItemIds.contains(item.id)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) palette.surfaceMuted else palette.surface,
                                    border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                                    shadowElevation = 0.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onLongPress = { toggleItem(item.id) },
                                                onTap = {
                                                    if (isInSelection) toggleItem(item.id) else toggleItem(item.id)
                                                },
                                            )
                                        },
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f),
                                        ) {
                                            // Checkbox indicator
                                            Box(
                                                modifier = Modifier
                                                    .size(22.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (isSelected) palette.primary else palette.surface)
                                                    .border(1.dp, if (isSelected) palette.primary else palette.border, RoundedCornerShape(6.dp)),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                if (isSelected) {
                                                    Text("✓", fontSize = 12.sp, color = palette.onPrimary, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = item.title,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = palette.textPrimary,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                    )
                                                    if (item.isPinned) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text("📌", fontSize = 11.sp)
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = item.subtitle,
                                                    fontSize = 12.sp,
                                                    color = palette.textSecondary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(palette.surfaceMuted)
                                                .padding(horizontal = 8.dp, vertical = 3.dp),
                                        ) {
                                            Text(
                                                text = item.tag,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = palette.textMuted,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    ContextSelectionMode.MEDIA -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(mediaItems, key = { it.id }) { media ->
                                val isSelected = selectedMediaIds.contains(media.id)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = palette.surface,
                                    border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                                    shadowElevation = 0.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clickable { toggleMedia(media.id) },
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        // Visual thumbnail mockup
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(media.colorAccent.copy(alpha = if (isSelected) 0.35f else 0.15f)),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = "🖼️",
                                                fontSize = 32.sp,
                                            )
                                        }

                                        // Selection tick badge
                                        Box(
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) palette.primary else palette.surface.copy(alpha = 0.8f))
                                                .border(1.dp, if (isSelected) palette.primary else palette.border, CircleShape)
                                                .align(Alignment.TopEnd),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            if (isSelected) {
                                                Text("✓", fontSize = 12.sp, color = palette.onPrimary, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        // Bottom info bar
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomCenter)
                                                .background(palette.surface.copy(alpha = 0.9f))
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                        ) {
                                            Text(
                                                text = media.title,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = palette.textPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                            Text(
                                                text = media.size,
                                                fontSize = 10.sp,
                                                color = palette.textMuted,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    ContextSelectionMode.TEXT -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                        ) {
                            Text(
                                text = "Kutipan Teks Interaktif:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textMuted,
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = palette.surface,
                                border = BorderStroke(1.dp, palette.border),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Arsitektur Flat UI Modern",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.textPrimary,
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Interactive selectable paragraph
                                    val isSelectedParagraph = textSelectedRange != null
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelectedParagraph) palette.primary.copy(alpha = 0.12f) else Color.Transparent)
                                            .border(1.dp, if (isSelectedParagraph) palette.primary else Color.Transparent, RoundedCornerShape(8.dp))
                                            .clickable {
                                                textSelectedRange = if (isSelectedParagraph) null else "Compose Multiplatform menyediakan shared UI lintas platform"
                                            }
                                            .padding(10.dp),
                                    ) {
                                        Text(
                                            text = "Compose Multiplatform menyediakan shared UI lintas platform Android dan iOS dengan 100% kode tampilan di commonMain. Dengan meniadakan bayangan, performa scroll tetap stabil di 60 FPS.",
                                            fontSize = 14.sp,
                                            lineHeight = 22.sp,
                                            color = palette.textPrimary,
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = if (isSelectedParagraph) "Klik paragraf di atas untuk membatalkan seleksi." else "Klik paragraf di atas untuk memilih kutipan teks.",
                                        fontSize = 11.sp,
                                        color = palette.textMuted,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    icon: String,
    palette: com.example.uiapp.theme.AppPalette,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isDestructive) palette.danger.copy(alpha = 0.1f) else palette.surfaceMuted,
        border = BorderStroke(1.dp, if (isDestructive) palette.danger else palette.border),
        modifier = modifier,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = icon, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDestructive) palette.danger else palette.textPrimary,
            )
        }
    }
}
