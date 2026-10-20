package com.example.uiapp.ui.offlineresilience

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.HapticKind
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private enum class SyncState { QUEUED, SYNCING, DONE, CONFLICT, FAILED }

private data class PendingAction(
    val id: Int,
    val title: String,
    val detail: String,
    val localVersion: String,
    val serverVersion: String,
    val state: SyncState = SyncState.QUEUED,
)

private val ActionTemplates = listOf(
    "Ubah harga akhir pekan" to "Villa Tebu: Rp1,2jt → Rp1,5jt",
    "Tutup tanggal 20–22" to "Perbaikan kolam (locked)",
    "Balas pesan tamu" to "\"Boleh early check-in jam 12?\"",
    "Tambah catatan housekeeping" to "Lantai 2: sediakan handuk ekstra",
    "Ubah kebijakan pembatalan" to "Refund penuh maks H-3",
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OfflineResilienceLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current

    var syncTrigger by remember { mutableIntStateOf(0) }
    var online by remember { mutableStateOf(true) }
    var syncing by remember { mutableStateOf(false) }
    var forceFail by remember { mutableStateOf(false) }
    var conflictItem by remember { mutableStateOf<PendingAction?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    val queue = remember { mutableStateListOf<PendingAction>() }
    var nextId by remember { mutableStateOf(1) }

    fun enqueue() {
        val tpl = ActionTemplates[(nextId - 1) % ActionTemplates.size]
        queue.add(
            PendingAction(
                id = nextId,
                title = tpl.first,
                detail = tpl.second,
                localVersion = "v${nextId}.0 lokal",
                serverVersion = "v${nextId}.1 server",
            ),
        )
        nextId++
        if (online) syncTrigger++
    }

    // Sinkronisasi berjalan sebagai satu sesi per pemicu (kembali online / enqueue / coba lagi).
    LaunchedEffect(syncTrigger) {
        if (!online) return@LaunchedEffect
        if (queue.none { it.state == SyncState.QUEUED }) return@LaunchedEffect
        syncing = true
        notice = "Menyambung ulang & menyinkronkan antrean…"
        while (true) {
            val idx = queue.indexOfFirst { it.state == SyncState.QUEUED }
            if (idx < 0) break
            val item = queue[idx]
            queue[idx] = item.copy(state = SyncState.SYNCING)
            delay(700)
            // Konflik & gagal dibuat deterministik: konflik pertama kali di item #3, gagal di #5 saat mode gagal.
            val newState = when {
                item.id == 3 && queue.none { it.state == SyncState.CONFLICT } -> SyncState.CONFLICT
                item.id == 5 && forceFail -> SyncState.FAILED
                else -> SyncState.DONE
            }
            queue[idx] = queue[idx].copy(state = newState)
            if (newState == SyncState.CONFLICT) {
                conflictItem = queue[idx]
                PerformHapticFeedback(HapticKind.WARNING)
                syncing = false
                notice = "Konflik sinkron terdeteksi — pilih versi untuk item #3."
                return@LaunchedEffect
            }
            if (newState == SyncState.FAILED) {
                PerformHapticFeedback(HapticKind.WARNING)
                syncing = false
                notice = "Gagal menyinkronkan item #5 (mode gagal aktif). Coba lagi."
                return@LaunchedEffect
            }
            PerformHapticFeedback(HapticKind.LIGHT)
        }
        syncing = false
        notice = "Sinkronisasi selesai. Semua perubahan lokal terkirim."
        PerformHapticFeedback(HapticKind.SUCCESS)
    }

    conflictItem?.let { item ->
        AlertDialog(
            onDismissRequest = { conflictItem = null },
            containerColor = palette.surface,
            title = { Text("Konflik versi", color = palette.textPrimary, fontSize = 15.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("\"${item.title}\" diubah juga dari perangkat lain.", fontSize = 12.sp, color = palette.textSecondary)
                    Surface(shape = RoundedCornerShape(10.dp), color = palette.surfaceMuted, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                        Column(Modifier.fillMaxWidth().padding(8.dp)) {
                            Text("Lokal: ${item.localVersion} — ${item.detail}", fontSize = 11.sp, color = palette.textPrimary)
                            Text("Server: ${item.serverVersion} — versi rekan tim", fontSize = 11.sp, color = palette.textSecondary)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val idx = queue.indexOfFirst { it.id == item.id }
                        if (idx >= 0) queue[idx] = queue[idx].copy(state = SyncState.DONE)
                        conflictItem = null
                        notice = "Konflik #3 selesai: versi server dipertahankan."
                        syncTrigger++
                        PerformHapticFeedback(HapticKind.SUCCESS)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.primary, contentColor = palette.onPrimary),
                ) { Text("Pakai server") }
            },
            dismissButton = {
                TextButton(onClick = {
                    val idx = queue.indexOfFirst { it.id == item.id }
                    if (idx >= 0) queue[idx] = queue[idx].copy(state = SyncState.DONE)
                    conflictItem = null
                    notice = "Konflik #3 selesai: versi lokal menang, server ditimpa."
                    syncTrigger++
                    PerformHapticFeedback(HapticKind.SUCCESS)
                }) { Text("Pakai lokal") }
            },
        )
    }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Offline Resilience", subtitle = "Simulasi antrean & konflik sinkron", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item(key = "intro") {
                    LabIntroCard(
                        title = "Tahan tetap hidup saat offline",
                        body = "Matikan koneksi (simulasi), buat perubahan — semuanya masuk antrean lokal. Nyalakan kembali " +
                            "untuk melihat sinkronisasi berurutan, konflik versi, dan mode gagal dengan coba lagi.",
                    )
                }
                item(key = "banner") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (online) palette.success.copy(alpha = 0.12f) else palette.danger.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, if (online) palette.success else palette.danger),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            StatusPill(
                                text = when {
                                    syncing -> "Menyinkronkan"
                                    online -> "Online"
                                    else -> "Offline"
                                },
                                color = when {
                                    syncing -> palette.info
                                    online -> palette.success
                                    else -> palette.danger
                                },
                            )
                            Text(
                                if (online) "Perubahan langsung terkirim (mock)" else "Perubahan disimpan lokal & masuk antrean",
                                fontSize = 11.sp,
                                color = palette.textSecondary,
                                modifier = Modifier.weight(1f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Switch(
                                checked = online,
                                onCheckedChange = {
                                    online = it
                                    if (it) syncTrigger++
                                    PerformHapticFeedback(if (it) HapticKind.MEDIUM else HapticKind.WARNING)
                                },
                                modifier = Modifier.semantics { contentDescription = "Saklar koneksi jaringan simulasi" },
                            )
                        }
                    }
                }
                item(key = "notice") {
                    notice?.let { msg ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = palette.info.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, palette.info),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(msg, fontSize = 11.sp, color = palette.textPrimary, modifier = Modifier.weight(1f), maxLines = 3, overflow = TextOverflow.Ellipsis)
                                TextButton(onClick = { notice = null }) { Text("Tutup", fontSize = 10.sp, color = palette.textMuted) }
                            }
                        }
                    }
                }
                item(key = "forcefail") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text("Mode gagal jaringan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                                Text("Paksa item #5 gagal saat sinkron", fontSize = 10.sp, color = palette.textMuted, maxLines = 1)
                            }
                            Switch(
                                checked = forceFail,
                                onCheckedChange = { forceFail = it; PerformHapticFeedback(HapticKind.SELECTION) },
                                modifier = Modifier.semantics { contentDescription = "Mode gagal jaringan" },
                            )
                        }
                    }
                }
                item(key = "qhead") {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Antrean perubahan (${queue.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        Text("${queue.count { it.state == SyncState.DONE }} terkirim", fontSize = 11.sp, color = palette.textMuted)
                    }
                }
                if (queue.isEmpty()) {
                    item(key = "empty") {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.border),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                        ) {
                            Column(Modifier.fillMaxWidth().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Antrean kosong", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                                Text("Buat perubahan lewat tombol di bawah", fontSize = 11.sp, color = palette.textMuted)
                            }
                        }
                    }
                } else {
                    items(queue, key = { it.id }) { item ->
                        QueueRow(
                            item = item,
                            onRetry = {
                                val idx = queue.indexOfFirst { it.id == item.id }
                                if (idx >= 0) queue[idx] = queue[idx].copy(state = SyncState.QUEUED)
                                notice = "Mencoba ulang item #${item.id}…"
                                if (online) syncTrigger++
                                PerformHapticFeedback(HapticKind.LIGHT)
                            },
                        )
                    }
                }
                item(key = "space") { Spacer(Modifier.height(4.dp)) }
            }

            Surface(color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = { enqueue(); PerformHapticFeedback(HapticKind.LIGHT) },
                        colors = ButtonDefaults.buttonColors(containerColor = palette.primary, contentColor = palette.onPrimary),
                        modifier = Modifier.weight(1f),
                    ) { Text("Buat Perubahan", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    OutlinedButton(
                        onClick = {
                            queue.clear()
                            notice = "Antrean dikosongkan."
                            PerformHapticFeedback(HapticKind.MEDIUM)
                        },
                        enabled = queue.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                    ) { Text("Kosongkan", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                }
            }
        }
    }
}

@Composable
private fun QueueRow(item: PendingAction, onRetry: () -> Unit) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = palette.surface,
        border = BorderStroke(
            1.dp,
            when (item.state) {
                SyncState.CONFLICT -> palette.warning
                SyncState.FAILED -> palette.danger
                else -> palette.border
            },
        ),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(item.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(item.detail, fontSize = 10.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            StatusPill(
                text = when (item.state) {
                    SyncState.QUEUED -> "Antre"
                    SyncState.SYNCING -> "Kirim…"
                    SyncState.DONE -> "Terkirim"
                    SyncState.CONFLICT -> "Konflik"
                    SyncState.FAILED -> "Gagal"
                },
                color = when (item.state) {
                    SyncState.QUEUED -> palette.textMuted
                    SyncState.SYNCING -> palette.info
                    SyncState.DONE -> palette.success
                    SyncState.CONFLICT -> palette.warning
                    SyncState.FAILED -> palette.danger
                },
            )
            if (item.state == SyncState.FAILED) {
                TextButton(onClick = onRetry) { Text("Coba lagi", fontSize = 11.sp, color = palette.primary) }
            }
        }
    }
}
