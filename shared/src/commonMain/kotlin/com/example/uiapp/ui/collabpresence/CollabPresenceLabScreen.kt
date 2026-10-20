package com.example.uiapp.ui.collabpresence

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.HapticKind
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.theme.LightAppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

private data class Peer(
    val id: Int,
    val name: String,
    val color: Color,
    val typing: Boolean,
    val online: Boolean,
)

private data class CommentPin(val id: Int, val author: String, val message: String, val replies: Int)

private val CommentPins = listOf(
    CommentPin(1, "Rina", "Judul bagian ini terlalu panjang, padatkan jadi dua kata?", 2),
    CommentPin(2, "Dedi", "Angka kunjungan sudah saya cek ulang, sumbernya BPS 2025.", 5),
    CommentPin(3, "Sari", "Tambahkan catatan metodologi di bawah tabel.", 0),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CollabPresenceLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current

    var peers by remember {
        mutableStateOf(
            listOf(
                Peer(1, "Rina", LightAppPalette.info, typing = true, online = true),
                Peer(2, "Dedi", LightAppPalette.success, typing = false, online = true),
                Peer(3, "Sari", LightAppPalette.warning, typing = false, online = false),
            ),
        )
    }
    // Posisi kursor rekan dalam ruang kanvas (0..1).
    var positions by remember { mutableStateOf(listOf(Offset(0.3f, 0.2f), Offset(0.6f, 0.5f), Offset(0.45f, 0.75f))) }
    var targets by remember { mutableStateOf(listOf(Offset(0.7f, 0.3f), Offset(0.25f, 0.7f), Offset(0.5f, 0.4f))) }
    var followMode by remember { mutableStateOf(false) }
    var followIdx by remember { mutableStateOf(0) }
    var openPin by remember { mutableStateOf<CommentPin?>(null) }
    val rng = remember { Random(42) }

    // Simulasi gerak rekan: coroutine menggeser kursor menuju target lalu memilih target baru.
    LaunchedEffect(peers) {
        while (true) {
            delay(60)
            val online = peers.filter { it.online }
            if (online.isEmpty()) continue
            positions = positions.mapIndexed { i, p ->
                val t = targets[i]
                val dx = (t.x - p.x) * 0.06f
                val dy = (t.y - p.y) * 0.06f
                Offset(p.x + dx, p.y + dy)
            }
            val arrived = positions.withIndex().any { (i, p) -> kotlin.math.abs(p.x - targets[i].x) < 0.02f && kotlin.math.abs(p.y - targets[i].y) < 0.02f }
            if (arrived) {
                targets = targets.map { Offset(0.1f + rng.nextFloat() * 0.8f, 0.1f + rng.nextFloat() * 0.8f) }
            }
        }
    }

    openPin?.let { pin ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { openPin = null },
            containerColor = palette.surface,
            title = { Text("Komentar • ${pin.author}", color = palette.textPrimary, fontSize = 15.sp) },
            text = { Text(pin.message, color = palette.textSecondary, fontSize = 13.sp) },
            confirmButton = {
                Button(
                    onClick = { openPin = null; PerformHapticFeedback(HapticKind.SELECTION) },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.primary, contentColor = palette.onPrimary),
                ) { Text("Selesai") }
            },
        )
    }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Collab Presence", subtitle = "Simulasi kolaborasi realtime", onBack = onBack) },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                LabIntroCard(
                    title = "Kanvas bersama (simulasi)",
                    body = "Kursor rekan bergerak lewat coroutine lokal, bukan jaringan. Coba mode ikuti untuk mengejar " +
                        "viewport rekan, matikan rekan untuk melihat status keluar ruangan, dan buka pin komentar.",
                )

                // Kanvas bersama
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth().height(280.dp).semantics {
                        contentDescription = "Kanvas dokumen bersama dengan ${peers.count { it.online }} rekan aktif dan ${CommentPins.size} pin komentar"
                    },
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        androidx.compose.foundation.layout.BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                            val innerW = maxWidth - 32.dp
                            val innerH = maxHeight - 32.dp
                            // Ilustrasi "dokumen": garis-garis teks mock.
                            Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                val lineH = size.height / 12f
                                val indent = size.width * 0.12f
                                for (i in 0 until 12) {
                                    val w = if (i % 4 == 0) size.width * 0.4f else size.width - indent
                                    drawLine(
                                        color = palette.border,
                                        start = Offset(indent, lineH * i + lineH / 2),
                                        end = Offset(indent + w, lineH * i + lineH / 2),
                                        strokeWidth = 4f,
                                        cap = StrokeCap.Round,
                                    )
                                }
                                // Kursor rekan; mode ikut menggeser seluruh kanvas.
                                val followed = if (followMode) positions[followIdx.coerceIn(positions.indices)] else null
                                val shiftX = if (followed != null) -followed.x * size.width * 0.3f else 0f
                                val shiftY = if (followed != null) -followed.y * size.height * 0.3f else 0f
                                translate(shiftX, shiftY) {
                                    drawPeers(peers, positions, size.width, size.height)
                                }
                            }
                            // Label nama kursor rekan.
                            peers.forEachIndexed { i, peer ->
                                if (peer.online) {
                                    val p = positions[i]
                                    val followShift = if (followMode) {
                                        val f = positions[followIdx.coerceIn(positions.indices)]
                                        Offset(-f.x * 0.3f, -f.y * 0.3f)
                                    } else Offset.Zero
                                    PeerLabel(
                                        peer = peer,
                                        modifier = Modifier
                                            .padding(start = 16.dp, top = 16.dp)
                                            .offset {
                                                IntOffset(
                                                    ((p.x + followShift.x).coerceIn(0f, 1f) * innerW.toPx()).roundToInt() + 6,
                                                    ((p.y + followShift.y).coerceIn(0f, 1f) * innerH.toPx()).roundToInt() + 4,
                                                )
                                            },
                                    )
                                }
                            }
                            // Pin komentar di posisi tetap.
                            listOf(Offset(0.15f, 0.15f), Offset(0.55f, 0.42f), Offset(0.3f, 0.68f)).forEachIndexed { i, pos ->
                                CommentPinBadge(
                                    pin = CommentPins[i],
                                    onClick = { openPin = CommentPins[i]; PerformHapticFeedback(HapticKind.LIGHT) },
                                    modifier = Modifier
                                        .padding(start = 16.dp, top = 16.dp)
                                        .offset { IntOffset((pos.x * innerW.toPx()).roundToInt(), (pos.y * innerH.toPx()).roundToInt()) },
                                )
                            }
                            if (followMode) {
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = palette.primary,
                                    border = BorderStroke(1.dp, palette.primary),
                                    shadowElevation = 0.dp,
                                    tonalElevation = 0.dp,
                                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
                                ) {
                                    Text(
                                        "Mengikuti ${peers[followIdx.coerceIn(peers.indices)].name}",
                                        fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                // Daftar presence
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Di dalam ruangan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                            Text("${peers.count { it.online }} / ${peers.size} rekan", fontSize = 11.sp, color = palette.textMuted)
                        }
                        peers.forEachIndexed { i, peer ->
                            PeerRow(
                                peer = peer,
                                followed = followMode && followIdx == i,
                                onToggleOnline = {
                                    peers = peers.toMutableList().also { it[i] = it[i].copy(online = !it[i].online) }
                                    PerformHapticFeedback(HapticKind.MEDIUM)
                                },
                                onFollow = {
                                    followMode = !(followMode && followIdx == i)
                                    followIdx = i
                                    PerformHapticFeedback(HapticKind.SELECTION)
                                },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPeers(
    peers: List<Peer>,
    positions: List<Offset>,
    w: Float,
    h: Float,
) {
    peers.forEachIndexed { i, peer ->
        if (!peer.online) return@forEachIndexed
        val p = Offset(positions[i].x * w, positions[i].y * h)
        // Gagang kursor
        drawLine(peer.color, p, Offset(p.x, p.y + 26f), strokeWidth = 3f, cap = StrokeCap.Round)
        // Kepala kursor (segitiga)
        val path = Path().apply {
            moveTo(p.x, p.y)
            lineTo(p.x + 10f, p.y + 16f)
            lineTo(p.x + 2f, p.y + 16f)
            close()
        }
        drawPath(path, peer.color)
        // Ring status mengetik
        if (peer.typing) {
            drawCircle(peer.color, radius = 22f, center = p, style = Stroke(width = 2f))
        }
    }
}

@Composable
private fun PeerLabel(peer: Peer, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = peer.color,
        border = BorderStroke(1.dp, peer.color),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = modifier,
    ) {
        Text(
            if (peer.typing) "${peer.name} mengetik…" else peer.name,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = LightAppPalette.onPrimary,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun PeerRow(
    peer: Peer,
    followed: Boolean,
    onToggleOnline: () -> Unit,
    onFollow: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (peer.online) peer.color else palette.surfaceMuted)
                .border(1.dp, palette.border, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(peer.name.take(1), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (peer.online) LightAppPalette.onPrimary else palette.textMuted)
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(peer.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                StatusPill(
                    text = when {
                        !peer.online -> "Keluar"
                        peer.typing -> "Mengetik"
                        else -> "Aktif"
                    },
                    color = when {
                        !peer.online -> palette.textMuted
                        peer.typing -> palette.warning
                        else -> palette.success
                    },
                )
            }
            Text(if (peer.online) "Terakhir aktif baru saja (mock)" else "Terakhir aktif 12 menit lalu (mock)", fontSize = 10.sp, color = palette.textMuted, maxLines = 1)
        }
        OutlinedButton(onClick = onFollow, enabled = peer.online) {
            Text(if (followed) "Diikuti" else "Ikuti", fontSize = 11.sp, maxLines = 1)
        }
        Switch(
            checked = peer.online,
            onCheckedChange = { onToggleOnline() },
            modifier = Modifier.semantics { contentDescription = "Ubah kehadiran ${peer.name}" },
        )
    }
}

@Composable
private fun CommentPinBadge(pin: CommentPin, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.primary),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = modifier
            .clickable(onClick = onClick)
            .semantics { contentDescription = "Komentar dari ${pin.author}, ${pin.replies} balasan" },
    ) {
        Text(
            "${pin.author.take(1)} • ${pin.replies}",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = palette.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}
