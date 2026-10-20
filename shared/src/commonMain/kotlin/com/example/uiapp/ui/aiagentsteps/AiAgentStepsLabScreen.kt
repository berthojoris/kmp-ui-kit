package com.example.uiapp.ui.aiagentsteps

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
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

private enum class StepKind { PLAN, TOOL, THINK, RISKY, SUMMARY }

private data class AgentStep(
    val id: Int,
    val kind: StepKind,
    val title: String,
    val detail: String,
    val result: String,
    val durationMs: Long = 900,
)

private val AgentPlan = listOf(
    AgentStep(1, StepKind.PLAN, "Menyusun rencana riset", "Membagi tugas: kumpulkan data kompetitor, ringkas ulasan, hitung gap harga.", "Rencana 5 langkah disetujui otomatis."),
    AgentStep(2, StepKind.TOOL, "Tool: web_search", "query: \"aplikasi booking villa terpopuler Indonesia 2026\"", "8 hasil ditemukan (data mock)."),
    AgentStep(3, StepKind.THINK, "Menyaring sumber", "Hanya domain resmi & ulasan ≥50 dibaca.", "4 sumber relevan dipertahankan."),
    AgentStep(4, StepKind.TOOL, "Tool: spreadsheet_read", "file: harga_kompetitor.xlsx • sheet: Q3", "32 baris harga terbaca."),
    AgentStep(5, StepKind.RISKY, "Kirim email ke tim", "Menulis ringkasan & mengirim ke daftar distribusi 12 orang.", "—"),
    AgentStep(6, StepKind.TOOL, "Tool: chart_generator", "tipe: bar • data: gap harga per region", "1 grafik dihasilkan."),
    AgentStep(7, StepKind.SUMMARY, "Kesimpulan akhir", "Gap harga 12–18% di 3 region; rekomendasi uji harga musim tinggi.", "Run selesai."),
)

private enum class RunState { IDLE, RUNNING, PAUSED, AWAITING, DECLINED, FAILED, DONE }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AiAgentStepsLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current

    var runState by remember { mutableStateOf(RunState.IDLE) }
    var currentStep by remember { mutableIntStateOf(0) } // jumlah langkah yang sudah tereksekusi
    var expandedId by remember { mutableStateOf<Int?>(null) }
    var forceFail by remember { mutableStateOf(false) }
    val toolCalls = remember { mutableStateListOf<Int>() }
    val isRunning = runState == RunState.RUNNING

    // Eksekusi langkah demi langkah (simulasi AI lokal); jeda membatalkan loop dan melanjutkan dari currentStep.
    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect
        while (currentStep < AgentPlan.size) {
            val step = AgentPlan[currentStep]
            if (forceFail && step.id == 6) {
                runState = RunState.FAILED
                PerformHapticFeedback(HapticKind.WARNING)
                return@LaunchedEffect
            }
            if (step.kind == StepKind.RISKY) {
                runState = RunState.AWAITING
                PerformHapticFeedback(HapticKind.MEDIUM)
                return@LaunchedEffect
            }
            delay(step.durationMs)
            currentStep++
            if (step.kind == StepKind.TOOL) toolCalls.add(step.id)
            PerformHapticFeedback(HapticKind.LIGHT)
        }
        runState = RunState.DONE
        PerformHapticFeedback(HapticKind.SUCCESS)
    }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "AI Agent Run Steps", subtitle = "Simulasi log langkah agent", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item(key = "intro") {
                    LabIntroCard(
                        title = "Menjalankan agent (AI simulasi)",
                        body = "Reasoning, tool-call, dan hasil di bawah adalah data mock lokal — bukan model nyata. " +
                            "Agent berhenti meminta persetujuan sebelum aksi berisiko, bisa dijeda, dan bila gagal " +
                            "dapat dilanjutkan dari langkah terakhir.",
                    )
                }
                item(key = "status") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Text("Run #A-113 • Riset kompetitor villa", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                StatusPill(
                                    text = when (runState) {
                                        RunState.IDLE -> "Belum mulai"
                                        RunState.RUNNING -> "Berjalan"
                                        RunState.PAUSED -> "Jeda"
                                        RunState.AWAITING -> "Menunggu izin"
                                        RunState.DECLINED -> "Ditolak"
                                        RunState.FAILED -> "Gagal"
                                        RunState.DONE -> "Selesai"
                                    },
                                    color = when (runState) {
                                        RunState.IDLE -> palette.textMuted
                                        RunState.RUNNING -> palette.info
                                        RunState.PAUSED -> palette.warning
                                        RunState.AWAITING -> palette.warning
                                        RunState.DECLINED -> palette.textMuted
                                        RunState.FAILED -> palette.danger
                                        RunState.DONE -> palette.success
                                    },
                                )
                            }
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(palette.surfaceMuted),
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxWidth(currentStep.toFloat() / AgentPlan.size)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(palette.primary),
                                )
                            }
                            Text("$currentStep/${AgentPlan.size} langkah • ${toolCalls.size} tool-call", fontSize = 10.sp, color = palette.textMuted)
                        }
                    }
                }
                item(key = "awaiting") {
                    if (runState == RunState.AWAITING) {
                        val step = AgentPlan.getOrNull(currentStep)
                        if (step != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = palette.warning.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, palette.warning),
                                shadowElevation = 0.dp,
                                tonalElevation = 0.dp,
                            ) {
                                Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Persetujuan diperlukan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                    Text("Langkah #${step.id} \"${step.title}\" bersifat berisiko: ${step.detail}", fontSize = 11.sp, color = palette.textSecondary)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                currentStep++
                                                toolCalls.add(step.id)
                                                runState = RunState.RUNNING
                                                PerformHapticFeedback(HapticKind.SUCCESS)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = palette.primary, contentColor = palette.onPrimary),
                                            modifier = Modifier.weight(1f),
                                        ) { Text("Setujui", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                                        OutlinedButton(
                                            onClick = {
                                                runState = RunState.DECLINED
                                                PerformHapticFeedback(HapticKind.WARNING)
                                            },
                                            modifier = Modifier.weight(1f),
                                        ) { Text("Tolak", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                                    }
                                }
                            }
                        }
                    }
                }
                item(key = "failed") {
                    if (runState == RunState.FAILED) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = palette.danger.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, palette.danger),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                        ) {
                            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Gagal di langkah #6", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                Text("Tool chart_generator mengembalikan error (mode gagal aktif). Lanjutkan dari langkah terakhir tanpa mengulang dari awal.", fontSize = 11.sp, color = palette.textSecondary)
                                Button(
                                    onClick = {
                                        runState = RunState.RUNNING
                                        PerformHapticFeedback(HapticKind.MEDIUM)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = palette.primary, contentColor = palette.onPrimary),
                                ) { Text("Lanjutkan dari langkah ini") }
                            }
                        }
                    }
                }
                item(key = "done") {
                    if (runState == RunState.DONE) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = palette.primaryContainer,
                            border = BorderStroke(1.dp, palette.border),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                        ) {
                            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Ringkasan run", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.onPrimaryContainer)
                                Text(
                                    "7 langkah selesai • ${toolCalls.size} tool-call • 1 persetujuan • durasi ±6 detik (mock). " +
                                        "Rekomendasi: uji kenaikan harga 10% pada 3 region gap terbesar.",
                                    fontSize = 11.sp,
                                    color = palette.onPrimaryContainer,
                                )
                            }
                        }
                    }
                }
                items(AgentPlan, key = { it.id }) { step ->
                    val executed = step.id <= currentStep
                    val isCurrent = runState == RunState.RUNNING && step.id == currentStep + 1
                    StepRow(
                        step = step,
                        executed = executed,
                        isCurrent = isCurrent,
                        declined = runState == RunState.DECLINED && step.kind == StepKind.RISKY,
                        expanded = expandedId == step.id,
                        onToggle = {
                            expandedId = if (expandedId == step.id) null else step.id
                            PerformHapticFeedback(HapticKind.SELECTION)
                        },
                    )
                }
                item(key = "forcefail") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text("Mode gagal di tengah", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                                Text("Paksa error di langkah #6", fontSize = 10.sp, color = palette.textMuted, maxLines = 1)
                            }
                            Switch(
                                checked = forceFail,
                                onCheckedChange = { forceFail = it; PerformHapticFeedback(HapticKind.SELECTION) },
                                modifier = Modifier.semantics { contentDescription = "Mode gagal agent di tengah run" },
                            )
                        }
                    }
                }
                item(key = "space") { Spacer(Modifier.height(4.dp)) }
            }

            // Kontrol run bawah
            Surface(color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    when {
                        runState == RunState.IDLE || runState == RunState.DECLINED || runState == RunState.DONE -> {
                            Button(
                                onClick = {
                                    currentStep = 0
                                    toolCalls.clear()
                                    runState = RunState.RUNNING
                                    PerformHapticFeedback(HapticKind.LIGHT)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = palette.primary, contentColor = palette.onPrimary),
                                modifier = Modifier.weight(1f),
                            ) { Text(if (runState == RunState.IDLE) "Mulai Run" else "Mulai Ulang", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        }
                        runState == RunState.RUNNING -> {
                            OutlinedButton(
                                onClick = { runState = RunState.PAUSED; PerformHapticFeedback(HapticKind.MEDIUM) },
                                modifier = Modifier.weight(1f),
                            ) { Text("Jeda", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        }
                        runState == RunState.PAUSED -> {
                            Button(
                                onClick = { runState = RunState.RUNNING; PerformHapticFeedback(HapticKind.LIGHT) },
                                colors = ButtonDefaults.buttonColors(containerColor = palette.primary, contentColor = palette.onPrimary),
                                modifier = Modifier.weight(1f),
                            ) { Text("Lanjutkan", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        }
                        else -> {
                            Text(
                                "Menunggu keputusan…",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                                modifier = Modifier.weight(1f).padding(top = 12.dp),
                                maxLines = 1,
                            )
                        }
                    }
                    TextButton(
                        onClick = {
                            currentStep = 0
                            toolCalls.clear()
                            expandedId = null
                            runState = RunState.IDLE
                            PerformHapticFeedback(HapticKind.MEDIUM)
                        },
                        enabled = currentStep > 0,
                    ) { Text("Reset") }
                }
            }
        }
    }
}

@Composable
private fun StepRow(
    step: AgentStep,
    executed: Boolean,
    isCurrent: Boolean,
    declined: Boolean,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(12.dp),
        color = if (isCurrent) palette.info.copy(alpha = 0.08f) else palette.surface,
        border = BorderStroke(
            1.dp,
            when {
                isCurrent -> palette.info
                declined -> palette.danger
                executed -> palette.border
                else -> palette.border
            },
        ),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = Modifier.semantics {
            contentDescription = "Langkah ${step.id} ${step.title}, status ${if (executed) "selesai" else if (isCurrent) "berjalan" else "menunggu"}"
        },
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (executed) palette.primary else palette.surfaceMuted),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "${step.id}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (executed) palette.onPrimary else palette.textMuted,
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        step.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (executed || isCurrent) palette.textPrimary else palette.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        when (step.kind) {
                            StepKind.PLAN -> "Rencana"
                            StepKind.TOOL -> "Tool-call"
                            StepKind.THINK -> "Reasoning"
                            StepKind.RISKY -> "Aksi berisiko"
                            StepKind.SUMMARY -> "Kesimpulan"
                        },
                        fontSize = 10.sp,
                        color = if (isCurrent) palette.info else palette.textMuted,
                        maxLines = 1,
                    )
                }
                StatusPill(
                    text = when {
                        declined -> "Ditolak"
                        executed -> "Selesai"
                        isCurrent -> "Berjalan"
                        else -> "Menunggu"
                    },
                    color = when {
                        declined -> palette.danger
                        executed -> palette.success
                        isCurrent -> palette.info
                        else -> palette.textMuted
                    },
                )
            }
            if (expanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.surfaceMuted, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                ) {
                    Text(step.detail, fontSize = 11.sp, color = palette.textSecondary)
                    if (executed && step.result != "—") {
                        Text("Hasil: ${step.result}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = palette.textPrimary)
                    }
                }
            }
        }
    }
}
