package com.example.uiapp.ui.sentencebuilder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.HapticKind
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.UiTopBar

private data class Puzzle(val id: Int, val hint: String, val target: String, val distractors: List<String>)

private val Puzzles = listOf(
    Puzzle(1, "Kegiatan pagi di sawah", "Paman memanen padi di sawah pagi ini", listOf("malam", "kota", "menanam", "kemarin")),
    Puzzle(2, "Rencana akhir pekan", "Kami akan mendaki gunung akhir pekan ini", listOf("laut", "kemarin", "berenang", "sekolah")),
    Puzzle(3, "Kebiasaan sehat", "Adik minum air putih delapan gelas setiap hari", listOf("kopi", "dua", "minggu", "makan")),
)

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun SentenceBuilderLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var round by remember { mutableIntStateOf(0) }
    var finishedCount by remember { mutableIntStateOf(0) }
    var wrongCount by remember { mutableIntStateOf(0) }

    val puzzle = Puzzles[round.coerceIn(Puzzles.indices)]
    // Bank kata: target + distractor, diacak deterministik per ronde.
    val bank = remember(round) { shuffleBank(puzzle) }
    // Slot terisi: indeks bank per posisi slot (-1 = kosong).
    var slots by remember(round) { mutableStateOf(List(puzzle.target.split(' ').size) { -1 }) }
    var result by remember(round) { mutableStateOf<String?>(null) }
    var resultOk by remember(round) { mutableStateOf(false) }
    var hintsUsed by remember(round) { mutableIntStateOf(0) }

    val targetWords = puzzle.target.split(' ')
    val allDone = finishedCount >= Puzzles.size

    fun resetRound(next: Int) {
        round = next
        slots = List(Puzzles[next].target.split(' ').size) { -1 }
        result = null
        resultOk = false
        hintsUsed = 0
    }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Sentence Builder", subtitle = "Simulasi puzzle susun kalimat", onBack = onBack) },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).imePadding(),
        ) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                LabIntroCard(
                    title = "Susun kalimat",
                    body = "Ketuk kata di bank untuk mengisi slot berikutnya, ketuk slot untuk mengembalikan kata. " +
                        "Gunakan petunjuk bila macet, lalu periksa jawaban.",
                )

                // Progres ronde
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Ronde ${round + 1} dari ${Puzzles.size}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                            Text("Selesai: $finishedCount • Salah: $wrongCount", fontSize = 11.sp, color = palette.textMuted)
                        }
                        LinearProgressIndicator(
                            progress = { finishedCount.toFloat() / Puzzles.size },
                            color = palette.primary,
                            trackColor = palette.surfaceMuted,
                            modifier = Modifier.fillMaxWidth().height(6.dp).background(palette.surfaceMuted, RoundedCornerShape(999.dp)),
                        )
                        Text("Petunjuk tema: ${puzzle.hint}", fontSize = 11.sp, color = palette.textSecondary)
                    }
                }

                if (allDone) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = palette.primaryContainer,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Semua ronde selesai!", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.onPrimaryContainer)
                            Text("Total salah: $wrongCount kali, petunjuk dipakai $hintsUsed kali di ronde ini.", fontSize = 11.sp, color = palette.onPrimaryContainer)
                        }
                    }
                    OutlinedButton(onClick = { finishedCount = 0; wrongCount = 0; resetRound(0) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Main lagi dari awal")
                    }
                } else {
                    // Slot jawaban
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            slots.forEachIndexed { slotIdx, bankIdx ->
                                val word = bankIdx.takeIf { it >= 0 }?.let { bank[it] } ?: ""
                                SlotChip(
                                    word = word,
                                    empty = bankIdx < 0,
                                    onClick = {
                                        if (bankIdx >= 0) {
                                            slots = slots.toMutableList().also { it[slotIdx] = -1 }
                                            PerformHapticFeedback(HapticKind.SELECTION)
                                        }
                                    },
                                )
                            }
                        }
                    }

                    result?.let { msg ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (resultOk) palette.success.copy(alpha = 0.12f) else palette.danger.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, if (resultOk) palette.success else palette.danger),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Box(
                                    modifier = Modifier.size(10.dp).background(if (resultOk) palette.success else palette.danger, RoundedCornerShape(999.dp)),
                                )
                                Text(msg, fontSize = 12.sp, color = palette.textPrimary, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // Bank kata
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            bank.forEachIndexed { idx, word ->
                                val used = slots.contains(idx)
                                BankChip(
                                    word = word,
                                    used = used,
                                    onClick = {
                                        val emptySlot = slots.indexOfFirst { it == -1 }
                                        if (emptySlot >= 0) {
                                            slots = slots.toMutableList().also { it[emptySlot] = idx }
                                            PerformHapticFeedback(HapticKind.LIGHT)
                                        } else {
                                            PerformHapticFeedback(HapticKind.WARNING)
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // Action bar bawah
            if (!allDone) {
                Surface(color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedButton(
                            onClick = {
                                val slotIdx = slots.indexOfFirst { it == -1 }
                                if (slotIdx >= 0) {
                                    val targetWord = targetWords[slotIdx]
                                    val freeIdx = bank.withIndex().firstOrNull { (i, w) -> w == targetWord && !slots.contains(i) }?.index
                                    if (freeIdx != null) {
                                        slots = slots.toMutableList().also { it[slotIdx] = freeIdx }
                                        hintsUsed++
                                        PerformHapticFeedback(HapticKind.MEDIUM)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                        ) { Text("Petunjuk", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        Button(
                            onClick = {
                                val answer = slots.mapNotNull { i -> bank.getOrNull(i) }.joinToString(" ")
                                if (slots.any { it == -1 }) {
                                    result = "Masih ada slot kosong. Lengkapi dulu."
                                    resultOk = false
                                    PerformHapticFeedback(HapticKind.WARNING)
                                } else if (answer == puzzle.target) {
                                    result = "Benar! Kalimat tersusun sempurna."
                                    resultOk = true
                                    finishedCount++
                                    PerformHapticFeedback(HapticKind.SUCCESS)
                                } else {
                                    result = "Belum tepat. Ketuk kata yang salah untuk mengembalikannya."
                                    resultOk = false
                                    wrongCount++
                                    PerformHapticFeedback(HapticKind.WARNING)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = palette.primary, contentColor = palette.onPrimary),
                            modifier = Modifier.weight(1f),
                        ) { Text("Periksa", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        if (resultOk) {
                            Button(
                                onClick = {
                                    if (round + 1 < Puzzles.size) resetRound(round + 1)
                                    else { finishedCount = Puzzles.size }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = palette.primary, contentColor = palette.onPrimary),
                                modifier = Modifier.weight(1f),
                            ) { Text("Lanjut", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotChip(word: String, empty: Boolean, onClick: () -> Unit) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (empty) palette.surfaceMuted else palette.primary.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, if (empty) palette.border else palette.primary),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = Modifier
            .clickable(onClick = onClick)
            .semantics { contentDescription = if (empty) "Slot kosong" else "Kata terpilih $word, ketuk untuk mengembalikan" },
    ) {
        Text(
            if (empty) "· · ·" else word,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (empty) palette.textMuted else palette.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun BankChip(word: String, used: Boolean, onClick: () -> Unit) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = Modifier
            .clickable(enabled = !used, onClick = onClick)
            .semantics { contentDescription = if (used) "Kata $word sudah terpakai" else "Kata $word" },
    ) {
        Text(
            word,
            fontSize = 13.sp,
            fontWeight = if (used) FontWeight.Normal else FontWeight.SemiBold,
            color = if (used) palette.textMuted else palette.textPrimary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        )
    }
}

/** Acak bank kata deterministik (tanpa Random baru per recompose, dipanggil dalam remember). */
private fun shuffleBank(puzzle: Puzzle): List<String> {
    val words = puzzle.target.split(' ') + puzzle.distractors
    // Fisher-Yates dengan seed tetap dari id agar stabil per ronde.
    var seed = 7 + puzzle.id * 31
    val out = words.toMutableList()
    for (i in out.indices.reversed()) {
        seed = (seed * 1103515245 + 12345) and 0x7FFFFFFF
        val j = seed % (i + 1)
        val tmp = out[i]; out[i] = out[j]; out[j] = tmp
    }
    return out
}
