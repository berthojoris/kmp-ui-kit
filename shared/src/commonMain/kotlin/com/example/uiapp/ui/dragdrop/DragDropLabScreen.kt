package com.example.uiapp.ui.dragdrop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

private data class DItem(val id: Int, val label: String, val source: Int)

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun DragDropLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var items by remember { mutableStateOf(listOf(
        DItem(1, "Foto A", 0), DItem(2, "Dokumen B", 0), DItem(3, "Video C", 0), DItem(4, "Arsip D", 0),
    )) }
    var dragging by remember { mutableStateOf<Int?>(null) }
    var rejected by remember { mutableStateOf<String?>(null) }
    var moved by remember { mutableStateOf<String?>(null) }
    val left = items.filter { it.source == 0 }
    val right = items.filter { it.source == 1 }

    fun move(id: Int, target: Int, allow: (DItem) -> Boolean) {
        val item = items.firstOrNull { it.id == id } ?: return
        if (!allow(item)) {
            rejected = "${item.label} ditolak oleh drop zone"
            PerformHapticFeedback(HapticKind.HEAVY)
            return
        }
        items = items.map { if (it.id == id) it.copy(source = target) else it }
        moved = "${item.label} dipindah"
        PerformHapticFeedback(HapticKind.SUCCESS)
    }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Drag & Drop", subtitle = "Simulasi seret antar panel", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item(key = "intro") {
                    LabIntroCard(title = "Cross-app drag", body = "Pilih item, seret ke panel tujuan. Drop zone menyala, penolakan diberi label.")
                }
                item(key = "status") {
                    if (rejected != null) {
                        LabCard {
                            StatusPill(text = "DROP DITOLAK", color = palette.danger)
                            Spacer(Modifier.height(4.dp))
                            Text(rejected!!, color = palette.textSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    if (moved != null) {
                        LabCard {
                            StatusPill(text = "DROP BERHASIL", color = palette.success)
                            Spacer(Modifier.height(4.dp))
                            Text(moved!!, color = palette.textSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
                item(key = "panels") {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DropPanel(title = "Panel A", items = left, active = dragging != null, allow = { true }, modifier = Modifier.weight(1f)) { id ->
                            move(id, 1) { it.source == 0 }
                        }
                        DropPanel(title = "Panel B (hanya video)", items = right, active = dragging != null, allow = { it.label.contains("Video") }, modifier = Modifier.weight(1f)) { id ->
                            move(id, 0) { true }
                        }
                    }
                }
                item(key = "items") {
                    LabCard {
                        LabSectionTitle("Item", "Ketuk item untuk mulai menyeret, lalu tujuan di panel")
                        left.forEach { it ->
                            ItemRow(it, selected = dragging == it.id, onClick = { dragging = if (dragging == it.id) null else it.id })
                        }
                        right.forEach { it ->
                            ItemRow(it, selected = dragging == it.id, onClick = { dragging = if (dragging == it.id) null else it.id })
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val id = dragging ?: return@Button
                                    val item = items.firstOrNull { it.id == id } ?: return@Button
                                    val target = if (item.source == 0) 1 else 0
                                    if (target == 1) move(id, target) { it.label.contains("Video") } else move(id, target) { true }
                                    dragging = null
                                },
                                enabled = dragging != null,
                                modifier = Modifier.weight(1f),
                            ) { Text("Drop", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            OutlinedButton(
                                onClick = { items = listOf(DItem(1, "Foto A", 0), DItem(2, "Dokumen B", 0), DItem(3, "Video C", 0), DItem(4, "Arsip D", 0)); moved = null; rejected = null },
                                modifier = Modifier.weight(1f),
                            ) { Text("Reset", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        }
                    }
                }
            }
            Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                Text("Simulasi drag, tanpa sistem drag OS", color = palette.textMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun DropPanel(title: String, items: List<DItem>, active: Boolean, allow: (DItem) -> Boolean, modifier: Modifier, onDrop: (Int) -> Unit) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier
            .background(if (active) palette.primaryContainer else palette.surface, RoundedCornerShape(16.dp))
            .border(1.dp, if (active) palette.primary else palette.border, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (items.isEmpty()) {
            Text("Kosong", color = palette.textMuted, fontSize = 11.sp)
        } else {
            items.forEach { it ->
                SurfaceRow(it.label)
            }
        }
    }
}

@Composable
private fun SurfaceRow(label: String) {
    val palette = LocalAppPalette.current
    Box(Modifier.fillMaxWidth().background(palette.surfaceMuted, RoundedCornerShape(10.dp)).border(1.dp, palette.border, RoundedCornerShape(10.dp)).padding(8.dp)) {
        Text(label, color = palette.textPrimary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun ItemRow(item: DItem, selected: Boolean, onClick: () -> Unit) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) palette.primary.copy(alpha = 0.12f) else palette.surfaceMuted, RoundedCornerShape(10.dp))
            .border(1.dp, if (selected) palette.primary else palette.border, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(item.label, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        OutlinedButton(onClick = onClick) { Text(if (selected) "Dipilih" else "Pilih", maxLines = 1, overflow = TextOverflow.Ellipsis) }
    }
}