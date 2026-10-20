package com.example.uiapp.ui.questpath

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.uiapp.ui.components.UiTopBar

private data class QuestNode(val id: Int, val title: String, val xp: Int, val kind: String)

private val QuestNodes = listOf(
    QuestNode(1, "Desa Awal", 10, "Cerita"),
    QuestNode(2, "Jembatan Tali", 15, "Puzzle"),
    QuestNode(3, "Hutan Kabut", 20, "Eksplorasi"),
    QuestNode(4, "Gua Gema", 25, "Puzzle"),
    QuestNode(5, "Bukit Bintang", 30, "Cerita"),
    QuestNode(6, "Menara Lonceng", 35, "Boss"),
    QuestNode(7, "Rawa Sunyi", 30, "Eksplorasi"),
    QuestNode(8, "Gerbang Saga", 50, "Boss"),
)

private val NodeSpacing = 104.dp
private val NodeSize = 60.dp
private val SideInset = 20.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun QuestPathLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var cleared by remember { mutableIntStateOf(2) }
    var xp by remember { mutableIntStateOf(95) }
    var dialogNode by remember { mutableStateOf<QuestNode?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }

    val currentId = cleared + 1
    val finished = cleared >= QuestNodes.size

    dialogNode?.let { node ->
        AlertDialog(
            onDismissRequest = { dialogNode = null },
            containerColor = palette.surface,
            title = { Text(node.title, color = palette.textPrimary) },
            text = {
                Text(
                    "Tantangan ${node.kind} • Hadiah ${node.xp} XP.\nSelesaikan misi untuk membuka node berikutnya?",
                    color = palette.textSecondary,
                    fontSize = 13.sp,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        cleared = node.id
                        xp += node.xp
                        notice = "\"${node.title}\" selesai! +${node.xp} XP"
                        dialogNode = null
                        PerformHapticFeedback(HapticKind.SUCCESS)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.primary, contentColor = palette.onPrimary),
                ) { Text("Selesaikan") }
            },
            dismissButton = {
                TextButton(onClick = { dialogNode = null }) { Text("Nanti dulu", color = palette.textMuted) }
            },
        )
    }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Quest Path", subtitle = "Simulasi peta level berkelok", onBack = onBack) },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LabIntroCard(
                title = "Peta petualangan",
                body = "Jalur node berkelok kiri-kanan. Ketuk node \"Main\" untuk menyelesaikan misi dan membuka " +
                    "node berikutnya. Node terkunci tidak dapat diketuk (disabled).",
            )

            if (notice != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = palette.success.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, palette.success),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(palette.success))
                        Text(notice ?: "", fontSize = 12.sp, color = palette.textPrimary, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                        TextButton(onClick = { notice = null }) { Text("Tutup", color = palette.textMuted, fontSize = 11.sp) }
                    }
                }
            }

            // XP bar
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Petualang Level ${cleared.coerceAtLeast(1)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        Text("$xp XP", fontSize = 12.sp, color = palette.textSecondary)
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(999.dp)).background(palette.surfaceMuted),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((cleared.toFloat() / QuestNodes.size).coerceIn(0f, 1f))
                                .height(8.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(palette.primary),
                        )
                    }
                    Text("${cleared.coerceAtMost(QuestNodes.size)}/${QuestNodes.size} node selesai", fontSize = 10.sp, color = palette.textMuted)
                }
            }

            QuestMap(
                cleared = cleared,
                currentId = currentId,
                onNodeTap = { node ->
                    when {
                        node.id <= cleared -> notice = "\"${node.title}\" sudah selesai sebelumnya."
                        node.id == currentId -> { dialogNode = node; PerformHapticFeedback(HapticKind.LIGHT) }
                        else -> { notice = "Node \"${node.title}\" masih terkunci. Selesaikan node sebelumnya dulu."; PerformHapticFeedback(HapticKind.WARNING) }
                    }
                },
            )

            if (finished) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = palette.primaryContainer,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Petualangan selesai!", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.onPrimaryContainer)
                        Text("Semua node terbuka. Total XP: $xp. Reset untuk memutar ulang jalur.", fontSize = 11.sp, color = palette.onPrimaryContainer)
                    }
                }
            }
            OutlinedButton(onClick = { cleared = 2; xp = 95; notice = null }, modifier = Modifier.fillMaxWidth()) {
                Text("Reset progres peta")
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun QuestMap(cleared: Int, currentId: Int, onNodeTap: (QuestNode) -> Unit) {
    val palette = LocalAppPalette.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val stepPx = with(density) { NodeSpacing.toPx() }
    val nodePx = with(density) { NodeSize.toPx() }
    val insetPx = with(density) { SideInset.toPx() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(NodeSpacing * QuestNodes.size)
            .background(palette.surface, RoundedCornerShape(16.dp))
            .padding(horizontal = 4.dp),
    ) {
        // Jalur berkelok di belakang node.
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path()
            val remembered = Path()
            QuestNodes.forEachIndexed { i, _ ->
                val cx = if (i % 2 == 0) insetPx + nodePx / 2 else size.width - insetPx - nodePx / 2
                val cy = stepPx * i + stepPx / 2
                if (i == 0) path.moveTo(cx, cy) else path.cubicTo(cx, cy - stepPx / 2, cx, cy - stepPx / 2, cx, cy)
                if (i <= cleared) {
                    val px = cx
                    val py = cy
                    if (i == 0) remembered.moveTo(px, py) else remembered.cubicTo(px, py - stepPx / 2, px, py - stepPx / 2, px, py)
                }
            }
            drawPath(path, color = palette.border, style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            if (cleared > 0) {
                drawPath(remembered, color = palette.primary, style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
        QuestNodes.forEachIndexed { i, node ->
            val state = when {
                node.id <= cleared -> "selesai"
                node.id == currentId -> "sekarang"
                else -> "terkunci"
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (i % 2 == 0) Arrangement.Start else Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(NodeSpacing)
                    .padding(horizontal = SideInset)
                    .offset(y = NodeSpacing * i),
            ) {
                if (i % 2 == 1) NodeLabel(node, state, Modifier.weight(1f).padding(end = 10.dp))
                NodeButton(
                    node = node,
                    state = state,
                    onTap = { onNodeTap(node) },
                )
                if (i % 2 == 0) NodeLabel(node, state, Modifier.weight(1f).padding(start = 10.dp))
            }
        }
    }
}

@Composable
private fun NodeButton(node: QuestNode, state: String, onTap: () -> Unit) {
    val palette = LocalAppPalette.current
    val done = state == "selesai"
    val current = state == "sekarang"
    val locked = state == "terkunci"
    Surface(
        onClick = onTap,
        enabled = !locked,
        shape = CircleShape,
        color = when {
            done -> palette.primary
            current -> palette.surface
            else -> palette.surfaceMuted
        },
        border = BorderStroke(
            if (current) 2.dp else 1.dp,
            if (current) palette.primary else palette.border,
        ),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = Modifier
            .size(NodeSize)
            .semantics { contentDescription = "Node ${node.title}, status $state, hadiah ${node.xp} XP" },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    when {
                        done -> "✓"
                        current -> "▶"
                        else -> "·"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        done -> palette.onPrimary
                        current -> palette.primary
                        else -> palette.textMuted
                    },
                )
                Text("${node.xp}", fontSize = 9.sp, color = if (done) palette.onPrimary else palette.textMuted, maxLines = 1)
            }
        }
    }
}

@Composable
private fun NodeLabel(node: QuestNode, state: String, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    val locked = state == "terkunci"
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Text(
            node.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (locked) palette.textMuted else palette.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            when (state) {
                "selesai" -> "Selesai • ${node.kind}"
                "sekarang" -> "Main sekarang • ${node.kind}"
                else -> "Terkunci • ${node.kind}"
            },
            fontSize = 10.sp,
            color = when (state) {
                "sekarang" -> palette.primary
                "selesai" -> palette.textSecondary
                else -> palette.textMuted
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
