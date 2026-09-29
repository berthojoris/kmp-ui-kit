package com.example.uiapp.ui.undoqueue

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ActionType(val label: String, val badge: String, val color: Color) {
    DELETE("Dihapus", "🗑️", Color(0xFFEF4444)),
    ARCHIVE("Diarsipkan", "📦", Color(0xFFF59E0B)),
    STAR("Ditandai Bintang", "★", Color(0xFF3B82F6)),
}

data class OptimisticItem(
    val id: String,
    val sender: String,
    val title: String,
    val time: String,
    var isStarred: Boolean = false,
)

data class QueuedAction(
    val id: String,
    val item: OptimisticItem,
    val type: ActionType,
    var remainingSeconds: Int = 5,
    val totalSeconds: Int = 5,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UndoQueueLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()

    var simulateFailure by remember { mutableStateOf(false) }
    var rollbackNotice by remember { mutableStateOf<String?>(null) }

    val activeItems = remember {
        mutableStateListOf(
            OptimisticItem("1", "DevOps Alert", "Deployment v2.4 berhasil ke production cluster", "10:15"),
            OptimisticItem("2", "Security Team", "Permintaan login baru terdeteksi dari browser macOS", "11:20"),
            OptimisticItem("3", "Figma Notification", "Andi mengomentari frame 'Floating Dock High Contrast'", "12:05"),
            OptimisticItem("4", "Billing Engine", "Faktur langganan bulanan #INV-889 telah terbit", "13:30"),
            OptimisticItem("5", "GitHub Bot", "Pull Request #412 disetujui oleh code reviewer", "14:10"),
            OptimisticItem("6", "Slack Workspace", "Meeting sinkronisasi mingguan dimulai dalam 15 menit", "14:45"),
        )
    }

    val actionQueue = remember { mutableStateListOf<QueuedAction>() }

    // Countdown and automatic commit timer
    LaunchedEffect(actionQueue.size) {
        while (actionQueue.isNotEmpty()) {
            delay(1000)
            val iterator = actionQueue.listIterator()
            while (iterator.hasNext()) {
                val action = iterator.next()
                if (action.remainingSeconds > 1) {
                    iterator.set(action.copy(remainingSeconds = action.remainingSeconds - 1))
                } else {
                    // Check if failure simulation is turned on
                    if (simulateFailure && action.type == ActionType.DELETE) {
                        // Rollback!
                        rollbackNotice = "Gagal menghapus \"${action.item.title}\" (Koneksi Server Gagal). Mengembalikan data..."
                        activeItems.add(0, action.item)
                    }
                    iterator.remove()
                }
            }
        }
    }

    fun triggerAction(item: OptimisticItem, type: ActionType) {
        // Optimistic UI removal
        activeItems.remove(item)

        // Add to undo queue
        val queueItem = QueuedAction(
            id = "act_${item.id}_${type.name}_${actionQueue.size}",
            item = item,
            type = type,
            remainingSeconds = 5,
            totalSeconds = 5,
        )
        actionQueue.add(0, queueItem)
    }

    fun undoAction(action: QueuedAction) {
        actionQueue.remove(action)
        activeItems.add(0, action.item)
        rollbackNotice = "Aksi ${action.type.label} untuk \"${action.item.title}\" dibatalkan."
        scope.launch {
            delay(2500)
            if (rollbackNotice?.startsWith("Aksi") == true) rollbackNotice = null
        }
    }

    fun undoAll() {
        actionQueue.forEach { act ->
            activeItems.add(0, act.item)
        }
        actionQueue.clear()
        rollbackNotice = "Seluruh aksi antrean berhasil dibatalkan."
    }

    fun resetDemoData() {
        actionQueue.clear()
        activeItems.clear()
        activeItems.addAll(
            listOf(
                OptimisticItem("1", "DevOps Alert", "Deployment v2.4 berhasil ke production cluster", "10:15"),
                OptimisticItem("2", "Security Team", "Permintaan login baru terdeteksi dari browser macOS", "11:20"),
                OptimisticItem("3", "Figma Notification", "Andi mengomentari frame 'Floating Dock High Contrast'", "12:05"),
                OptimisticItem("4", "Billing Engine", "Faktur langganan bulanan #INV-889 telah terbit", "13:30"),
                OptimisticItem("5", "GitHub Bot", "Pull Request #412 disetujui oleh code reviewer", "14:10"),
                OptimisticItem("6", "Slack Workspace", "Meeting sinkronisasi mingguan dimulai dalam 15 menit", "14:45"),
            )
        )
        rollbackNotice = null
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Optimistic & Undo Queue",
                subtitle = "Antrean aksi instan & rollback state",
                onBack = onBack,
                action = {
                    Text(
                        text = "Reset",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                        modifier = Modifier.clickable { resetDemoData() },
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
        ) {
            // Control Banner: Simulate Failure Toggle & Explainer
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Simulasi Kegagalan Server (Rollback)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = if (simulateFailure) "Aktif: Aksi hapus akan gagal dan memicu rollback" else "Nonaktif: Seluruh aksi sukses secara normal",
                            fontSize = 11.sp,
                            color = if (simulateFailure) palette.danger else palette.textMuted,
                        )
                    }

                    Surface(
                        onClick = { simulateFailure = !simulateFailure },
                        shape = RoundedCornerShape(20.dp),
                        color = if (simulateFailure) palette.danger else palette.surfaceMuted,
                        border = BorderStroke(1.dp, if (simulateFailure) palette.danger else palette.border),
                    ) {
                        Text(
                            text = if (simulateFailure) "ON" else "OFF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (simulateFailure) Color.White else palette.textSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }

            // Notification / Rollback Banner
            AnimatedVisibility(visible = rollbackNotice != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                        .padding(10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = rollbackNotice ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = palette.textPrimary,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "✕",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textMuted,
                            modifier = Modifier.clickable { rollbackNotice = null },
                        )
                    }
                }
            }

            // Interactive Item List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (activeItems.isEmpty() && actionQueue.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📭", fontSize = 38.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Semua item telah diproses",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary,
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Klik 'Reset' di kanan atas untuk memuat ulang data.",
                                    fontSize = 12.sp,
                                    color = palette.textMuted,
                                )
                            }
                        }
                    }
                }

                items(activeItems, key = { it.id }) { item ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = item.sender,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.primary,
                                )
                                Text(
                                    text = item.time,
                                    fontSize = 11.sp,
                                    color = palette.textMuted,
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = palette.textPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Surface(
                                    onClick = { triggerAction(item, ActionType.ARCHIVE) },
                                    shape = RoundedCornerShape(6.dp),
                                    color = palette.surfaceMuted,
                                    border = BorderStroke(1.dp, palette.border),
                                ) {
                                    Text(
                                        text = "📦 Arsipkan",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.textSecondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    onClick = { triggerAction(item, ActionType.DELETE) },
                                    shape = RoundedCornerShape(6.dp),
                                    color = palette.danger.copy(alpha = 0.1f),
                                    border = BorderStroke(1.dp, palette.danger.copy(alpha = 0.4f)),
                                ) {
                                    Text(
                                        text = "🗑️ Hapus",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.danger,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Undo Queue Stacking Dock
            AnimatedVisibility(
                visible = actionQueue.isNotEmpty(),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(palette.primary),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "${actionQueue.size}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.onPrimary,
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Antrean Aksi Aktif (${actionQueue.size})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                )
                            }

                            if (actionQueue.size > 1) {
                                Text(
                                    text = "Urungkan Semua",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.danger,
                                    modifier = Modifier.clickable { undoAll() },
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Stacked Queue List
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            actionQueue.forEach { queueItem ->
                                val progress = queueItem.remainingSeconds.toFloat() / queueItem.totalSeconds.toFloat()
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(palette.surfaceMuted)
                                        .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = queueItem.type.badge,
                                                    fontSize = 12.sp,
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${queueItem.type.label}: \"${queueItem.item.title}\"",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = palette.textPrimary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Otomatis tersimpan dalam ${queueItem.remainingSeconds}s",
                                                fontSize = 10.sp,
                                                color = palette.textMuted,
                                            )
                                        }

                                        Surface(
                                            onClick = { undoAction(queueItem) },
                                            shape = RoundedCornerShape(6.dp),
                                            color = palette.primary,
                                            border = BorderStroke(1.dp, palette.primary),
                                        ) {
                                            Text(
                                                text = "Urungkan",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = palette.onPrimary,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Countdown Progress Bar
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = palette.primary,
                                        trackColor = palette.border,
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
