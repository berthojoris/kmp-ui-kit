package com.example.uiapp.ui.blockeditor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private enum class BlockType(val label: String) {
    PARAGRAPH("Paragraf"),
    TODO("To-do"),
    H1("Heading 1"),
    H2("Heading 2"),
    CALLOUT("Callout"),
    QUOTE("Kutipan"),
}

private data class Block(
    val id: Int,
    val type: BlockType,
    val text: String,
    val checked: Boolean = false,
)

private val InitialBlocks = listOf(
    Block(1, BlockType.H1, "Catatan Proyek"),
    Block(2, BlockType.PARAGRAPH, "Ini adalah contoh editor blok bergaya Notion. Setiap baris adalah blok mandiri yang dapat dipindahkan, dihapus, atau diubah jenisnya."),
    Block(3, BlockType.TODO, "Desain UI selesai", checked = true),
    Block(4, BlockType.TODO, "Implementasi backend", checked = false),
    Block(5, BlockType.TODO, "Testing integrasi", checked = false),
    Block(6, BlockType.CALLOUT, "Penting: Deadline proyek minggu depan! Pastikan semua tugas diselesaikan tepat waktu."),
    Block(7, BlockType.QUOTE, "Kode yang baik adalah dokumentasi dirinya sendiri."),
    Block(8, BlockType.H2, "Langkah Selanjutnya"),
    Block(9, BlockType.PARAGRAPH, "Perlu review dari tim sebelum melanjutkan ke tahap deployment dan rilis versi beta."),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BlockEditorLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var blocks by remember { mutableStateOf(InitialBlocks) }
    var selectedIdx by remember { mutableStateOf(-1) }
    var showAddMenu by remember { mutableStateOf(false) }
    var nextId by remember { mutableStateOf(10) }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Block Editor", subtitle = "${blocks.size} blok", onBack = onBack) },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                itemsIndexed(blocks, key = { _, b -> b.id }) { idx, block ->
                    val isSelected = idx == selectedIdx
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) palette.primaryContainer.copy(alpha = 0.2f) else palette.surface,
                        border = if (isSelected) BorderStroke(1.5.dp, palette.primary) else BorderStroke(0.5.dp, palette.border.copy(alpha = 0.3f)),
                        shadowElevation = 0.dp, tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            // Drag handle
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { selectedIdx = if (isSelected) -1 else idx }
                                    .semantics { contentDescription = "Pegangan blok" },
                                contentAlignment = Alignment.Center,
                            ) {
                                Canvas(modifier = Modifier.size(12.dp, 16.dp)) {
                                    val dotR = 2.dp.toPx()
                                    for (r in 0..2) {
                                        for (c in 0..1) {
                                            drawCircle(palette.textMuted, dotR, Offset(c * size.width, r * size.height / 2))
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.width(6.dp))
                            // Block content
                            Column(modifier = Modifier.weight(1f)) {
                                BlockContent(block, onToggle = {
                                    blocks = blocks.toMutableList().also { it[idx] = block.copy(checked = !block.checked) }
                                })
                            }
                            // Actions when selected
                            if (isSelected) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    if (idx > 0) {
                                        MiniAction("↑") {
                                            val list = blocks.toMutableList()
                                            val item = list.removeAt(idx)
                                            list.add(idx - 1, item)
                                            blocks = list
                                            selectedIdx = idx - 1
                                        }
                                    }
                                    if (idx < blocks.lastIndex) {
                                        MiniAction("↓") {
                                            val list = blocks.toMutableList()
                                            val item = list.removeAt(idx)
                                            list.add(idx + 1, item)
                                            blocks = list
                                            selectedIdx = idx + 1
                                        }
                                    }
                                    MiniAction("×") {
                                        blocks = blocks.toMutableList().also { it.removeAt(idx) }
                                        selectedIdx = -1
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom bar
            Surface(
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp, tonalElevation = 0.dp,
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(12.dp)) {
                    if (showAddMenu) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            BlockType.entries.forEach { type ->
                                Surface(
                                    onClick = {
                                        blocks = blocks + Block(nextId++, type, when(type) {
                                            BlockType.PARAGRAPH -> "Paragraf baru..."
                                            BlockType.TODO -> "Tugas baru"
                                            BlockType.H1 -> "Judul Baru"
                                            BlockType.H2 -> "Sub Judul Baru"
                                            BlockType.CALLOUT -> "Informasi penting di sini"
                                            BlockType.QUOTE -> "Kutipan inspiratif"
                                        })
                                        showAddMenu = false
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = palette.surfaceMuted,
                                    border = BorderStroke(1.dp, palette.border),
                                    shadowElevation = 0.dp, tonalElevation = 0.dp,
                                ) {
                                    Text(type.label, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            onClick = { showAddMenu = !showAddMenu },
                            shape = CircleShape,
                            color = palette.primary,
                            shadowElevation = 0.dp, tonalElevation = 0.dp,
                            modifier = Modifier.size(40.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
                            }
                        }
                        Text("Ketuk blok untuk memilih · + untuk menambah", fontSize = 11.sp, color = palette.textMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun BlockContent(block: Block, onToggle: () -> Unit) {
    val palette = LocalAppPalette.current
    when (block.type) {
        BlockType.H1 -> Text(block.text, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
        BlockType.H2 -> Text(block.text, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
        BlockType.PARAGRAPH -> Text(block.text, fontSize = 14.sp, color = palette.textSecondary, lineHeight = 20.sp)
        BlockType.TODO -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = block.checked,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(checkedColor = palette.primary, uncheckedColor = palette.border, checkmarkColor = palette.onPrimary),
                )
                Text(
                    text = block.text,
                    fontSize = 14.sp,
                    color = if (block.checked) palette.textMuted else palette.textPrimary,
                    textDecoration = if (block.checked) TextDecoration.LineThrough else TextDecoration.None,
                )
            }
        }
        BlockType.CALLOUT -> {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = palette.warning.copy(alpha = 0.08f),
                shadowElevation = 0.dp, tonalElevation = 0.dp,
            ) {
                Row(modifier = Modifier.padding(10.dp)) {
                    Box(Modifier.width(3.dp).height(36.dp).clip(RoundedCornerShape(2.dp)).background(palette.warning))
                    Spacer(Modifier.width(10.dp))
                    Text(block.text, fontSize = 13.sp, color = palette.textPrimary, lineHeight = 18.sp)
                }
            }
        }
        BlockType.QUOTE -> {
            Row {
                Box(Modifier.width(3.dp).height(30.dp).clip(RoundedCornerShape(2.dp)).background(palette.primary))
                Spacer(Modifier.width(10.dp))
                Text(block.text, fontSize = 14.sp, fontStyle = FontStyle.Italic, color = palette.textSecondary, lineHeight = 20.sp)
            }
        }
    }
}

@Composable
private fun MiniAction(label: String, onClick: () -> Unit) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = palette.surfaceMuted,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp, tonalElevation = 0.dp,
        modifier = Modifier.size(24.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
        }
    }
}
