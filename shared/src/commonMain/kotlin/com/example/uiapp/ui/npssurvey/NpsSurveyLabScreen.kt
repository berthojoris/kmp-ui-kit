package com.example.uiapp.ui.npssurvey

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.foundation.ExperimentalFoundationApi
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.HapticKind
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.FlatTextField
import com.example.uiapp.ui.components.UiTopBar
import com.example.uiapp.ui.components.FlatPrimaryButton

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun NpsSurveyLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = com.example.uiapp.theme.LocalAppPalette.current
    var score by remember { mutableStateOf<Int?>(null) }
    var reason by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }
    var skipped by remember { mutableStateOf(false) }

    Scaffold(containerColor = palette.background, topBar = { UiTopBar(title = "NPS Survey", subtitle = "Survei simulasi", onBack = onBack) }) { innerPadding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(innerPadding).navigationBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { LabIntroCard(title = "NPS/CSAT", body = "Validasi wajib pilih skor. Pertanyaan lanjutan bercabang.") }
            item {
                Surface(shape = RoundedCornerShape(14.dp), color = palette.surface, border = androidx.compose.foundation.BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), Arrangement.spacedBy(8.dp)) {
                        Text("Seberapa puas Anda? (0-10)", fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            (0..10).forEach { n ->
                                androidx.compose.material3.FilterChip(
                                    selected = score == n,
                                    onClick = { score = n; PerformHapticFeedback(HapticKind.SELECTION) },
                                    label = { Text(n.toString()) },
                                )
                            }
                        }
                        val branch = when (score) {
                            null -> "Pilih skor terlebih dahulu"
                            in 0..6 -> "Detractor: apa yang perlu diperbaiki?"
                            in 7..8 -> "Passive: apa yang bisa lebih baik?"
                            else -> "Promoter: apa yang paling Anda suka?"
                        }
                        Text(branch, color = palette.textSecondary, fontSize = 12.sp)
                        FlatTextField("Alasan", reason, { reason = it }, placeholder = "Tulis alasan")
                        if (sent) Text("✓ Terkirim", color = palette.success, fontWeight = FontWeight.Bold)
                        if (skipped) Text("Dilewati", color = palette.textMuted)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FlatPrimaryButton("Kirim", {
                                if (score == null) PerformHapticFeedback(HapticKind.WARNING) else { sent = true; PerformHapticFeedback(HapticKind.SUCCESS) }
                            }, Modifier.weight(1f))
                            androidx.compose.material3.TextButton(onClick = { skipped = true; PerformHapticFeedback(HapticKind.MEDIUM) }) {
                                Text("Lewati", color = palette.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
