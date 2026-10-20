package com.example.uiapp.ui.versionhistory

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

private data class Rev(val idx: Int, val note: String, val time: String)
private val Revisions = listOf(
    Rev(0, "Draf awal", "09:00"),
    Rev(1, "Tambah paragraf", "09:12"),
    Rev(2, "Ubah judul", "09:25"),
    Rev(3, "Final review", "09:40"),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VersionHistoryLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var pos by remember { mutableIntStateOf(Revisions.lastIndex) }
    var comparing by remember { mutableStateOf(false) }
    var compareA by remember { mutableIntStateOf(0) }
    var compareB by remember { mutableIntStateOf(Revisions.lastIndex) }
    var restoring by remember { mutableStateOf(false) }
    val current = Revisions[pos]
    Column(modifier = Modifier.fillMaxSize().background(palette.background)) {
        UiTopBar(title = "Riwayat Versi", onBack = onBack, subtitle = "Simulasi")
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LabIntroCard(title = "Timeline versi", body = "Slider waktu, bandingkan dua versi, pulihkan.")
            LabCard {
                LabSectionTitle("Preview", current.note)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                ) {
                    Text("Konten versi ${current.idx + 1}: ${current.note} (${current.time})", color = palette.textPrimary)
                }
            }
            LabCard {
                LabSectionTitle("Slider Waktu", "Geser untuk melihat")
                Slider(value = pos.toFloat(), onValueChange = { pos = it.toInt().coerceIn(0, Revisions.lastIndex) }, valueRange = 0f..Revisions.lastIndex.toFloat(), steps = Revisions.size - 2)
                Text("Versi ${pos + 1} — ${current.time}", color = palette.textMuted, fontSize = 11.sp)
            }
            LabCard {
                LabSectionTitle("Bandingkan", "Dua versi")
                androidx.compose.material3.FilterChip(selected = comparing, onClick = { comparing = !comparing }, label = { Text(if (comparing) "Mode bandingkan" else "Bandikan dua versi") })
                if (comparing) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Versi A", color = palette.textPrimary, fontSize = 11.sp)
                            Slider(value = compareA.toFloat(), onValueChange = { compareA = it.toInt().coerceIn(0, Revisions.lastIndex) }, valueRange = 0f..Revisions.lastIndex.toFloat())
                            Text(Revisions[compareA].note, color = palette.textMuted, fontSize = 10.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Versi B", color = palette.textPrimary, fontSize = 11.sp)
                            Slider(value = compareB.toFloat(), onValueChange = { compareB = it.toInt().coerceIn(0, Revisions.lastIndex) }, valueRange = 0f..Revisions.lastIndex.toFloat())
                            Text(Revisions[compareB].note, color = palette.textMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { pos = Revisions.lastIndex; PerformHapticFeedback(HapticKind.SELECTION) }, modifier = Modifier.weight(1f)) { Text("Terbaru") }
                Button(onClick = { restoring = true; PerformHapticFeedback(HapticKind.SUCCESS) }, modifier = Modifier.weight(1f)) { Text("Pulihkan Versi Ini") }
            }
            if (restoring) StatusPill(text = "Versi ${pos + 1} dipulihkan (simulasi)", color = palette.success)
            if (Revisions.size <= 1) StatusPill(text = "Riwayat kosong", color = palette.textMuted)
        }
    }
}