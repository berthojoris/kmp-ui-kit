package com.example.uiapp.ui.aiinlineedit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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
import kotlinx.coroutines.delay

private enum class GhostPhase { IDLE, THINKING, READY, APPLIED, REJECTED }

private enum class DiffState { EMPTY, LOADING, READY }

private enum class LineKind(val label: String) { SAME(""), ADD("+"), REMOVE("-") }

private data class DiffLine(val kind: LineKind, val text: String)

private data class DiffHunk(val id: Int, val title: String, val lines: List<DiffLine>, var accepted: Boolean = false)

private data class Suggestion(val keyword: String, val ghost: String)

private val Suggestions = listOf(
    Suggestion("rapat", "besok pukul 09.00 di ruang Glass dengan agenda review kuartal."),
    Suggestion("laporan", "menyorot kenaikan pendapatan 12% dibanding kuartal lalu."),
    Suggestion("catatan", "tiga poin tindak lanjut beserta pemilik dan tenggat masing-masing."),
    Suggestion("terima kasih", "Mohon konfirmasi bila ada revisi sebelum saya lanjutkan."),
    Suggestion("", "Tulis kalimat pembuka yang singkat, lalu sambungkan ke konteks utama."),
)

private fun suggestFor(text: String): String =
    (Suggestions.firstOrNull { it.keyword.isNotEmpty() && text.contains(it.keyword, ignoreCase = true) }
        ?: Suggestions.last()).ghost

// Contoh hasil "AI" untuk tinjauan diff; seluruhnya data mock lokal (AI simulasi).
private val SampleDiff = listOf(
    DiffHunk(
        1,
        "Perbaikan pembuka",
        listOf(
            DiffLine(LineKind.REMOVE, "Rapat koordinasi akan dilaksanakan nanti."),
            DiffLine(LineKind.ADD, "Rapat koordinasi berlangsung besok pukul 09.00 WIB."),
            DiffLine(LineKind.SAME, "Peserta: produk, desain, dan engineering."),
        ),
    ),
    DiffHunk(
        2,
        "Penegasan tindak lanjut",
        listOf(
            DiffLine(LineKind.SAME, "Notulen akan dibagikan maksimal hari yang sama."),
            DiffLine(LineKind.ADD, "Setiap pemilik tugas melaporkan progres tiap Jumat."),
        ),
    ),
    DiffHunk(
        3,
        "Penutup lebih ringkas",
        listOf(
            DiffLine(LineKind.REMOVE, "Demikian informasi ini kami sampaikan, terima kasih atas perhatiannya."),
            DiffLine(LineKind.ADD, "Terima kasih."),
        ),
    ),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AiInlineEditLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    var draft by remember { mutableStateOf("Rapat koordinasi akan dilaksanakan nanti.") }
    var ghost by remember { mutableStateOf("") }
    var phase by remember { mutableStateOf(GhostPhase.IDLE) }
    var diffState by remember { mutableStateOf(DiffState.EMPTY) }
    val hunks = remember { mutableStateListOf<DiffHunk>().apply { addAll(SampleDiff) } }

    BackHandler(enabled = true) { onBack() }

    // Debounce sederhana: setelah pengguna berhenti mengetik 600 ms, "AI" menyiapkan ghost text.
    LaunchedEffect(draft) {
        if (draft.isBlank()) {
            phase = GhostPhase.IDLE
            ghost = ""
            return@LaunchedEffect
        }
        phase = GhostPhase.THINKING
        delay(600)
        ghost = suggestFor(draft)
        phase = GhostPhase.READY
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background),
    ) {
        UiTopBar(
            title = "AI Inline Edit",
            subtitle = "Ghost text & tinjauan diff — AI simulasi",
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LabIntroCard(
                title = "AI simulasi",
                body = "Saran ghost text dan diff dihasilkan dari data mock lokal, bukan model AI sungguhan. " +
                    "Tab menerima saran, Esc menolak — di layar sentuh gunakan tombol di bawah kolom.",
            )

            LabSectionTitle("Ghost text inline", "Saran lanjutan muncul redup setelah kursor")

            val fieldShape = RoundedCornerShape(12.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface, fieldShape)
                    .border(1.dp, palette.border, fieldShape)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BasicTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = palette.textPrimary,
                        fontSize = 14.sp,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp),
                    decorationBox = { inner ->
                        Box {
                            if (draft.isBlank()) {
                                Text(
                                    "Tulis draf catatan rapat…",
                                    fontSize = 14.sp,
                                    color = palette.textMuted,
                                )
                            }
                            inner()
                        }
                    },
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(palette.border),
                )
                Text(
                    text = buildAnnotatedString {
                        append(draft.ifBlank { "…" })
                        if (ghost.isNotEmpty()) {
                            withStyle(
                                SpanStyle(
                                    color = palette.textSecondary,
                                    fontStyle = FontStyle.Italic,
                                ),
                            ) { append("  $ghost") }
                        }
                    },
                    fontSize = 12.sp,
                    color = palette.textPrimary,
                    lineHeight = 17.sp,
                )
            }

            when (phase) {
                GhostPhase.IDLE -> StatusPill("Kosong — tulis sesuatu", palette.textMuted)
                GhostPhase.THINKING -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = palette.primary,
                        modifier = Modifier.height(16.dp),
                    )
                    Text("AI sedang berpikir… (simulasi)", fontSize = 11.sp, color = palette.textSecondary)
                }
                GhostPhase.READY -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StatusPill("Saran siap", palette.success)
                    FlatAction(
                        label = "Terima (Tab)",
                        enabled = true,
                        container = palette.primary,
                        content = palette.onPrimary,
                    ) {
                        PerformHapticFeedback(HapticKind.SUCCESS)
                        draft = (draft.trim() + " " + ghost).trim()
                        ghost = ""
                        phase = GhostPhase.APPLIED
                    }
                    FlatAction(
                        label = "Tolak (Esc)",
                        enabled = true,
                        container = palette.surfaceMuted,
                        content = palette.textPrimary,
                    ) {
                        PerformHapticFeedback(HapticKind.WARNING)
                        ghost = ""
                        phase = GhostPhase.REJECTED
                    }
                }
                GhostPhase.APPLIED -> StatusPill("Saran diterapkan ke draf", palette.success)
                GhostPhase.REJECTED -> StatusPill("Saran ditolak — Anda bisa menulis manual", palette.warning)
            }

            LabSectionTitle("Tinjauan diff", "Terima atau tolak per potongan perubahan")

            if (diffState == DiffState.EMPTY) {
                LabCard {
                    Text(
                        "Belum ada diff. Tekan tombol untuk meminta saran penyuntingan (simulasi).",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                    FlatAction(
                        label = "Buat saran penyuntingan",
                        enabled = true,
                        container = palette.primary,
                        content = palette.onPrimary,
                    ) {
                        diffState = DiffState.LOADING
                    }
                }
            }

            if (diffState == DiffState.LOADING) {
                LaunchedEffect(Unit) {
                    delay(900)
                    diffState = DiffState.READY
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CircularProgressIndicator(strokeWidth = 2.dp, color = palette.primary, modifier = Modifier.height(16.dp))
                    Text("Menyusun diff… (simulasi)", fontSize = 11.sp, color = palette.textSecondary)
                }
            }

            if (diffState == DiffState.READY) {
                val acceptedCount = hunks.count { it.accepted }
                val rejectedCount = hunks.size - acceptedCount
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    StatusPill("$acceptedCount diterima", palette.success)
                    StatusPill("$rejectedCount ditolak", palette.danger)
                }
                hunks.forEach { hunk ->
                    DiffHunkCard(
                        hunk = hunk,
                        onToggle = { accept ->
                            PerformHapticFeedback(HapticKind.SELECTION)
                            hunks[hunks.indexOfFirst { it.id == hunk.id }] = hunk.copy(accepted = accept)
                        },
                    )
                }
                if (rejectedCount == hunks.size) {
                    StatusPill("Semua saran ditolak — draf tetap asli", palette.warning)
                } else if (acceptedCount > 0) {
                    StatusPill("Sukses — $acceptedCount potongan siap diterapkan", palette.success)
                }
            }
        }
    }
}

@Composable
private fun DiffHunkCard(hunk: DiffHunk, onToggle: (Boolean) -> Unit) {
    val palette = LocalAppPalette.current
    LabCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                hunk.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (hunk.accepted) {
                StatusPill("Diterima", palette.success)
            } else {
                StatusPill("Belum", palette.textMuted)
            }
        }
        hunk.lines.forEach { line ->
            val bg = when (line.kind) {
                LineKind.ADD -> palette.success.copy(alpha = 0.10f)
                LineKind.REMOVE -> palette.danger.copy(alpha = 0.10f)
                LineKind.SAME -> Color.Transparent
            }
            val fg = when (line.kind) {
                LineKind.ADD -> palette.success
                LineKind.REMOVE -> palette.danger
                LineKind.SAME -> palette.textSecondary
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bg, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(line.kind.label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = fg)
                Text(line.text, fontSize = 11.sp, color = fg, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FlatAction(
                label = "Terima",
                enabled = !hunk.accepted,
                container = palette.primary,
                content = palette.onPrimary,
            ) { onToggle(true) }
            FlatAction(
                label = "Tolak",
                enabled = hunk.accepted,
                container = palette.surfaceMuted,
                content = palette.textPrimary,
            ) { onToggle(false) }
        }
    }
}

@Composable
private fun FlatAction(
    label: String,
    enabled: Boolean,
    container: Color,
    content: Color,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .background(
                if (enabled) container else palette.surfaceMuted,
                RoundedCornerShape(10.dp),
            )
            .border(
                1.dp,
                if (enabled) container else palette.border,
                RoundedCornerShape(10.dp),
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) content else palette.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
