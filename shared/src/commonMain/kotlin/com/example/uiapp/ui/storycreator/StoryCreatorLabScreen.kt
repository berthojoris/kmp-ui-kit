package com.example.uiapp.ui.storycreator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Brush
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

private val Bgs = listOf(
    Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFFEC4899))),
    Brush.linearGradient(listOf(Color(0xFF0EA5E9), Color(0xFF22C55E))),
    Brush.linearGradient(listOf(Color(0xFF111827), Color(0xFF374151))),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StoryCreatorLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var bg by remember { mutableIntStateOf(0) }
    var tool by remember { mutableIntStateOf(0) }
    var saving by remember { mutableStateOf(false) }
    var published by remember { mutableStateOf(false) }
    var stickers by remember { mutableIntStateOf(2) }

    Column(modifier = Modifier.fillMaxSize().background(palette.background)) {
        UiTopBar(title = "Pembuat Story", onBack = onBack, subtitle = "Label simulasi")
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LabIntroCard(title = "Kanvas story vertikal", body = "Stiker/teks dapat digeser & diputar (mock).")
            LabCard {
                LabSectionTitle("Latar", "Pilih gradien")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (Bgs.indices).forEach { i ->
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Bgs[i], RoundedCornerShape(12.dp))
                                .border(2.dp, if (bg == i) palette.primary else palette.border, RoundedCornerShape(12.dp)),
                        ) {
                            // click
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Bgs.indices.forEach { i ->
                        FilterChip(selected = bg == i, onClick = { bg = i }, label = { Text("Latar ${i + 1}") })
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Bgs[bg], RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (tool == 0) "Menggambar…" else if (tool == 1) "Teks aktif" else "Stiker aktif", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Stiker: $stickers", color = Color.White.copy(alpha = .8f), fontSize = 11.sp)
                }
            }
            LabCard {
                LabSectionTitle("Alat", "Pilih mode")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Pensil", "Teks", "Stiker").forEachIndexed { i, t ->
                        FilterChip(selected = tool == i, onClick = { tool = i }, label = { Text(t) })
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(onClick = { if (stickers < 6) stickers++ }) { Text("Tambah Stiker") }
                    OutlinedButton(onClick = { if (stickers > 0) stickers-- }) { Text("Hapus Stiker") }
                }
            }
            LabCard {
                LabSectionTitle("Poll Sticky", "Opsional")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) { Text("Poll: Pilih A atau B", color = palette.textPrimary, fontSize = 12.sp) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { stickers = 0; published = false; PerformHapticFeedback(HapticKind.SELECTION) },
                    modifier = Modifier.weight(1f),
                ) { Text("Reset") }
                Button(
                    onClick = {
                        saving = true
                        published = false
                    },
                    modifier = Modifier.weight(1f),
                ) { Text(if (saving) "Menyimpan..." else "Terbitkan") }
            }
            if (saving) {
                StatusPill(text = "Menyimpan (simulasi)", color = palette.info)
            } else if (published) {
                StatusPill(text = "Terbit", color = palette.success)
            } else {
                StatusPill(text = "Draf", color = palette.textMuted)
            }
        }
    }
}