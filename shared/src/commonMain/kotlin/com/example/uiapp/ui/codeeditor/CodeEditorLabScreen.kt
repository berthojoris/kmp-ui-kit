package com.example.uiapp.ui.codeeditor

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
import androidx.compose.material3.OutlinedTextField
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CodeEditorLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var code by remember { mutableStateOf("fun main() = println(\"Halo\")") }
    var fold by remember { mutableStateOf(false) }
    var diffMode by remember { mutableIntStateOf(0) }
    var changed by remember { mutableStateOf(true) }
    Column(modifier = Modifier.fillMaxSize().background(palette.background)) {
        UiTopBar(title = "Editor Kode", onBack = onBack, subtitle = "Simulasi")
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LabIntroCard(title = "Editor & Diff", body = "Nomor baris mock, fold, inline/side-by-side.")
            LabCard {
                LabSectionTitle("Editor", "${code.lines().size} baris")
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.take(400) },
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    minLines = 8,
                )
                Text("Baris: ${code.lines().size}", color = palette.textMuted, fontSize = 11.sp)
            }
            LabCard {
                LabSectionTitle("Fold", "Blok fungsi")
                androidx.compose.material3.FilterChip(selected = fold, onClick = { fold = !fold }, label = { Text(if (fold) "Terlipat" else "Terbuka") })
                if (fold) Text("fun main() { ... }", color = palette.textMuted, fontSize = 11.sp)
            }
            LabCard {
                LabSectionTitle("Diff", "Mode tampilan")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    androidx.compose.material3.FilterChip(selected = diffMode == 0, onClick = { diffMode = 0 }, label = { Text("Inline") })
                    androidx.compose.material3.FilterChip(selected = diffMode == 1, onClick = { diffMode = 1 }, label = { Text("Side-by-side") })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f).background(Color(0xFFFEE2E2), RoundedCornerShape(8.dp)).padding(8.dp)) { Text("- val x = 1", color = Color(0xFF991B1B), fontSize = 11.sp) }
                    Box(modifier = Modifier.weight(1f).background(Color(0xFFD1FAE5), RoundedCornerShape(8.dp)).padding(8.dp)) { Text("+ val x = 2", color = Color(0xFF065F46), fontSize = 11.sp) }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { code = "fun main() = println(\"Halo\")"; PerformHapticFeedback(HapticKind.SELECTION) }, modifier = Modifier.weight(1f)) { Text("Reset") }
                Button(onClick = { changed = true; PerformHapticFeedback(HapticKind.SUCCESS) }, modifier = Modifier.weight(1f)) { Text("Terapkan") }
            }
            StatusPill(text = if (changed) "Ada perubahan" else "Tidak ada perubahan", color = if (changed) palette.warning else palette.textMuted)
        }
    }
}