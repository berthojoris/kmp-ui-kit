package com.example.uiapp.ui.docscanner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
fun DocScannerLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var phase by remember { mutableIntStateOf(0) } // 0 deteksi 1 gagal 2 hasil
    var corner by remember { mutableIntStateOf(0) } // 0 auto 1 manual
    var ocr by remember { mutableStateOf(false) }
    var pages by remember { mutableIntStateOf(1) }

    Column(modifier = Modifier.fillMaxSize().background(palette.background)) {
        UiTopBar(title = "Pemindai Dokumen", onBack = onBack, subtitle = "Simulasi kamera")
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LabIntroCard(title = "Pindai & OCR", body = "Bingkai deteksi tepi + koreksi perspektif mock.")
            LabCard {
                LabSectionTitle("Pratinjau", "Mode deteksi")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(150.dp, 190.dp)
                            .border(2.dp, if (phase == 0) palette.primary else palette.warning, RoundedCornerShape(4.dp)),
                    )
                    Text(
                        when (phase) {
                            0 -> "Mendeteksi tepi…"
                            1 -> "Gagal deteksi tepi"
                            else -> "Halaman terdeteksi"
                        },
                        color = palette.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 230.dp.coerceAtMost(10.dp)),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    androidx.compose.material3.FilterChip(selected = phase == 0, onClick = { phase = 0 }, label = { Text("Deteksi") })
                    androidx.compose.material3.FilterChip(selected = phase == 1, onClick = { phase = 1 }, label = { Text("Gagal") })
                    androidx.compose.material3.FilterChip(selected = phase == 2, onClick = { phase = 2 }, label = { Text("Hasil") })
                }
            }
            LabCard {
                LabSectionTitle("Koreksi Tepi", "Auto vs manual 4 titik")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    androidx.compose.material3.FilterChip(selected = corner == 0, onClick = { corner = 0 }, label = { Text("Otomatis") })
                    androidx.compose.material3.FilterChip(selected = corner == 1, onClick = { corner = 1 }, label = { Text("Manual 4 titik") })
                }
            }
            LabCard {
                LabSectionTitle("Halaman", "Daftar thumbnail")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items((1..pages).toList(), key = { it }) { i ->
                        Box(
                            modifier = Modifier
                                .size(60.dp, 80.dp)
                                .background(palette.surfaceMuted, RoundedCornerShape(8.dp))
                                .border(1.dp, palette.border, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center,
                        ) { Text("$i", color = palette.textPrimary) }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(onClick = { if (pages < 6) pages++ }) { Text("Tambah Halaman") }
                    OutlinedButton(onClick = { if (pages > 1) pages-- }) { Text("Hapus Halaman") }
                }
            }
            LabCard {
                LabSectionTitle("OCR", "Mock penyorotan kata")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.FilterChip(selected = ocr, onClick = { ocr = !ocr }, label = { Text(if (ocr) "OCR aktif" else "Aktifkan OCR") })
                }
                if (ocr) {
                    Text(
                        "Ini adalah contoh teks hasil pindai mock yang disorot.",
                        color = palette.textPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(palette.primaryContainer, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { phase = 0; PerformHapticFeedback(HapticKind.SELECTION) }, modifier = Modifier.weight(1f)) { Text("Pindai Ulang") }
                Button(onClick = { PerformHapticFeedback(HapticKind.SUCCESS); phase = 2 }, modifier = Modifier.weight(1f)) { Text("Selesai") }
            }
            StatusPill(
                text = when (phase) {
                    0 -> "Mendeteksi"
                    1 -> "Gagal deteksi"
                    else -> "Berhasil"
                },
                color = when (phase) {
                    0 -> palette.info
                    1 -> palette.danger
                    else -> palette.success
                },
            )
        }
    }
}