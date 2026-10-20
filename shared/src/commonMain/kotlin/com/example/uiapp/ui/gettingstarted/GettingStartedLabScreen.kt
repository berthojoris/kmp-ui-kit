package com.example.uiapp.ui.gettingstarted

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
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.uiapp.ui.components.UiTopBar
import com.example.uiapp.ui.components.FlatPrimaryButton

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun GettingStartedLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = com.example.uiapp.theme.LocalAppPalette.current

    val tasks = listOf("Buat akun", "Lengkapi profil", "Ajak rekan", "Coba fitur pertama")
    val done = remember { mutableStateOf(mutableSetOf<String>()) }

    val progress = done.value.size.toFloat() / tasks.size

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Getting Started", subtitle = "Checklist awal", onBack = onBack) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).navigationBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                LabIntroCard(title = "Checklist progres", body = "State: belum mulai, sebagian, selesai (simulasi).")
            }
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(14.dp), Arrangement.spacedBy(8.dp)) {
                        Text("Progres ${(progress * 100).toInt()}%", fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        LinearProgressIndicator(
                            progress = { progress },
                            color = palette.primary,
                            trackColor = palette.surfaceMuted,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        tasks.forEach { t ->
                            val isDone = t in done.value
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(t, color = palette.textPrimary)
                                androidx.compose.material3.Button(
                                    onClick = {
                                        if (isDone) done.value.remove(t) else done.value.add(t)
                                        done.value = done.value.toMutableSet()
                                        PerformHapticFeedback(HapticKind.SELECTION)
                                    },
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = if (isDone) palette.success else palette.surfaceMuted,
                                        contentColor = if (isDone) palette.onSuccess else palette.textPrimary,
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, palette.border),
                                ) { Text(if (isDone) "✓ Selesai" else "Tandai") }
                            }
                        }
                    }
                }
            }
            if (progress >= 1f) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = palette.primaryContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("🎉 Semua tugas selesai", fontWeight = FontWeight.Bold, color = palette.onPrimaryContainer)
                            Text("Perayaan ringan, bukan confetti penuh layar.", color = palette.textSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
            item {
                FlatPrimaryButton("Minimalkan ke kartu ringkas", { PerformHapticFeedback(HapticKind.MEDIUM) }, Modifier.fillMaxWidth())
            }
        }
    }
}
