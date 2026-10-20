package com.example.uiapp.ui.reportflow

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
fun ReportFlowLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    val reasons = listOf("Spam", "Ujaran kebencian", "Kekerasan", "Lainnya")
    var selected by remember { mutableStateOf<String?>(null) }
    var detail by remember { mutableStateOf("") }
    var block by remember { mutableStateOf(false) }
    var sending by remember { mutableStateOf(false) }
    var sent by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }

    Scaffold(containerColor = palette.background, topBar = { UiTopBar(title = "Lapor & Blokir", subtitle = "Moderasi simulasi", onBack = onBack) }) { innerPadding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(innerPadding).navigationBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { LabIntroCard(title = "Alur laporan", body = "State: alasan kosong, mengirim, gagal. Semua aksi simulasi.") }
            item {
                Surface(shape = RoundedCornerShape(14.dp), color = palette.surface, border = androidx.compose.foundation.BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), Arrangement.spacedBy(8.dp)) {
                        Text("Pilih alasan", fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        reasons.forEach { r ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(r, color = palette.textPrimary)
                                androidx.compose.material3.RadioButton(selected = selected == r, onClick = { selected = r; PerformHapticFeedback(HapticKind.SELECTION) })
                            }
                        }
                        if (selected == null) Text("Alasan wajib dipilih", color = palette.danger, fontSize = 11.sp)
                        FlatTextField("Detail", detail, { detail = it }, placeholder = "Tambahan")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material3.Checkbox(checked = block, onCheckedChange = { block = it })
                            Text("Blokir pengguna", color = palette.textPrimary)
                        }
                        if (failed) Text("Gagal mengirim, coba lagi", color = palette.danger, fontSize = 12.sp)
                        if (sent) Text("✓ Telah dilaporkan", color = palette.success, fontWeight = FontWeight.Bold)
                        FlatPrimaryButton(if (sending) "Mengirim..." else "Kirim Laporan", {
                            if (selected == null) {
                                PerformHapticFeedback(HapticKind.WARNING)
                            } else {
                                sending = true
                                sent = false
                                failed = false
                                PerformHapticFeedback(HapticKind.MEDIUM)
                                sending = false
                                sent = true
                            }
                        }, Modifier.fillMaxWidth(), enabled = !sending)
                    }
                }
            }
        }
    }
}
