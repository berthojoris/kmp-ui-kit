package com.example.uiapp.ui.postcomposer

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Brush
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val MediaGrad = listOf(
    Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF93C5FD))),
    Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFFDBA74))),
    Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF6EE7B7))),
)

private val Audiences = listOf("Publik", "Teman", "Hanya Saya")

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PostComposerLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    var audience by remember { mutableIntStateOf(0) }
    var loc by remember { mutableStateOf("") }
    var poll by remember { mutableStateOf(false) }
    var mediaCount by remember { mutableIntStateOf(2) }
    var uploading by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var status by remember { mutableStateOf("Draf") }

    Scaffold(
        topBar = { UiTopBar(title = "Komposer Posting", onBack = onBack) },
        containerColor = palette.background,
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
                .imePadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LabIntroCard(
                title = "Simulasi unggahan",
                body = "Pratinjau langsung, label simulasi (tanpa jaringan).",
            )
            LabCard {
                LabSectionTitle("Media", "Carousel gradien mock")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items((1..mediaCount).toList(), key = { it }) { i ->
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .background(MediaGrad[(i - 1) % MediaGrad.size], RoundedCornerShape(12.dp)),
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { if (mediaCount < 5) mediaCount++ }) { Text("Tambah") }
                    OutlinedButton(onClick = { if (mediaCount > 0) mediaCount-- }) { Text("Hapus") }
                }
            }
            LabCard {
                LabSectionTitle("Teks", "Hitungan karakter real-time")
                OutlinedTextField(
                    value = text,
                    onValueChange = { if (it.length <= 300) text = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Tulis sesuatu...") },
                    minLines = 3,
                    maxLines = 6,
                )
                Text("${text.length}/300", color = palette.textMuted, fontSize = 11.sp)
            }
            LabCard {
                LabSectionTitle("Poll", "Opsional")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(selected = poll, onClick = { poll = !poll }, label = { Text(if (poll) "Poll aktif" else "Aktifkan poll") })
                }
                if (poll) {
                    OutlinedTextField(value = "", onValueChange = {}, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Pilihan 1") })
                }
            }
            LabCard {
                LabSectionTitle("Audiens & Lokasi", "Target & tempat")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(Audiences, key = { it }) { a ->
                        FilterChip(selected = Audiences.indexOf(a) == audience, onClick = { audience = Audiences.indexOf(a) }, label = { Text(a) })
                    }
                }
                OutlinedTextField(value = loc, onValueChange = { loc = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Lokasi (opsional)") })
            }
            LabCard {
                LabSectionTitle("Pratinjau", "Tampilan post langsung")
                Surface(shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, palette.border), color = palette.surfaceMuted) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text.ifBlank { "Pratinjau teks..." }, color = palette.textPrimary, maxLines = 4, overflow = TextOverflow.Ellipsis)
                        if (poll) Text("Poll: 2 pilihan", color = palette.textSecondary, fontSize = 11.sp)
                        Text("Audiens: ${Audiences[audience]}", color = palette.textMuted, fontSize = 11.sp)
                    }
                }
            }
            if (uploading) {
                LabCard {
                    Text("Mengunggah ${"%.0f".format(progress * 100)}%", color = palette.textPrimary)
                    Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(palette.surfaceMuted, RoundedCornerShape(3.dp))) {
                        Box(modifier = Modifier.fillMaxWidth(progress).height(6.dp).background(palette.primary, RoundedCornerShape(3.dp)))
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { text = ""; mediaCount = 0; poll = false; loc = ""; status = "Draf"; PerformHapticFeedback(HapticKind.SELECTION) },
                    modifier = Modifier.weight(1f),
                ) { Text("Batal") }
                Button(
                    onClick = {
                        if (!uploading) {
                            uploading = true
                            progress = 0f
                            scope.launch {
                                status = "Mengunggah"
                                for (i in 1..20) { delay(80); progress = i / 20f }
                                uploading = false
                                status = "Berhasil"
                                PerformHapticFeedback(HapticKind.SUCCESS)
                            }
                        }
                    },
                    enabled = !uploading,
                    modifier = Modifier.weight(1f),
                ) { Text("Unggah") }
            }
            StatusPill(text = status, color = when (status) {
                "Berhasil" -> palette.success
                "Mengunggah" -> palette.info
                else -> palette.textMuted
            })
        }
    }
}
