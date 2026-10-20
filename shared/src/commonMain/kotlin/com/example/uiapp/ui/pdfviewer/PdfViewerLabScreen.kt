package com.example.uiapp.ui.pdfviewer

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
fun PdfViewerLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var page by remember { mutableIntStateOf(1) }
    val pages = 8
    var view by remember { mutableIntStateOf(0) } // 0 scroll 1 halaman
    var query by remember { mutableStateOf("") }
    var annot by remember { mutableStateOf(false) }
    var highlight by remember { mutableStateOf("") }
    var loadState by remember { mutableIntStateOf(0) } // 0 load 1 error 2 empty

    Column(modifier = Modifier.fillMaxSize().background(palette.background)) {
        UiTopBar(title = "Penampil PDF", onBack = onBack, subtitle = "Mock dokumen")
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LabIntroCard(title = "Mock PDF", body = "Thumbnail, cari, lompat, anotasi di atas halaman.")
            LabCard {
                LabSectionTitle("State Render", "Muat/Galat/Kosong")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    androidx.compose.material3.FilterChip(selected = loadState == 0, onClick = { loadState = 0 }, label = { Text("Muat") })
                    androidx.compose.material3.FilterChip(selected = loadState == 1, onClick = { loadState = 1 }, label = { Text("Galat") })
                    androidx.compose.material3.FilterChip(selected = loadState == 2, onClick = { loadState = 2 }, label = { Text("Kosong") })
                }
            }
            if (loadState == 1) {
                StatusPill(text = "Galat render dokumen", color = palette.danger)
            } else if (loadState == 2) {
                StatusPill(text = "Dokumen kosong", color = palette.textMuted)
            } else {
                LabCard {
                    LabSectionTitle("Halaman $page dari $pages", "Navigasi")
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(onClick = { if (page > 1) page-- }) { Text("Prev") }
                        OutlinedButton(onClick = { if (page < pages) page++ }) { Text("Next") }
                        androidx.compose.material3.FilterChip(selected = view == 0, onClick = { view = 0 }, label = { Text("Gulir") })
                        androidx.compose.material3.FilterChip(selected = view == 1, onClick = { view = 1 }, label = { Text("Halaman") })
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Halaman $page", color = palette.textPrimary, fontWeight = FontWeight.Bold)
                        if (annot) {
                            Box(modifier = Modifier.size(120.dp).background(palette.primaryContainer, RoundedCornerShape(4.dp)).padding(6.dp)) {
                                Text("Anotasi: ${highlight.ifBlank { "catatan" }}", fontSize = 10.sp, color = palette.textPrimary)
                            }
                        }
                    }
                }
                LabCard {
                    LabSectionTitle("Thumbnail", "Lompat halaman")
                    LazyColumn(modifier = Modifier.height(120.dp)) {
                        items((1..pages).toList(), key = { it }) { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(if (p == page) palette.primaryContainer else palette.surface, RoundedCornerShape(8.dp))
                                    .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("Hal. $p", color = palette.textPrimary)
                            }
                        }
                    }
                }
                LabCard {
                    LabSectionTitle("Cari", "Teks dalam dokumen")
                    OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Cari kata...") })
                    if (query.isNotBlank()) Text("Ditemukan: 3 hasil (mock)", color = palette.textSecondary, fontSize = 11.sp)
                }
                LabCard {
                    LabSectionTitle("Anotasi", "Sorot & catatan")
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        androidx.compose.material3.FilterChip(selected = annot, onClick = { annot = !annot }, label = { Text("Anotasi aktif") })
                    }
                    OutlinedTextField(value = highlight, onValueChange = { highlight = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Catatan...") })
                }
            }
        }
    }
}