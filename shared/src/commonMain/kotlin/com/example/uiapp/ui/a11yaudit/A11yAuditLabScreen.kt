package com.example.uiapp.ui.a11yaudit

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
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import com.example.uiapp.ui.components.FlatPrimaryButton

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun A11yAuditLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current

    var smallTarget by remember { mutableStateOf(true) }
    var lowContrast by remember { mutableStateOf(true) }
    var noDesc by remember { mutableStateOf(true) }
    var showTree by remember { mutableStateOf(false) }

    val findings = remember(smallTarget, lowContrast, noDesc) {
        mutableListOf<String>().apply {
            if (smallTarget) add("Target sentuh 32dp < 48dp pada tombol \"Simpan\"")
            if (lowContrast) add("Kontras teks abu 3,2:1 di bawah AA")
            if (noDesc) add("Ikon tanpa contentDescription pada menu")
            if (isEmpty()) add("Tidak ada temuan")
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Accessibility Audit",
                subtitle = "Audit overlay simulasi",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                LabIntroCard(
                    title = "Simulasi audit aksesibilitas",
                    body = "Sorot temuan: target kecil, kontras rendah, tanpa deskripsi. Label simulasi, bukan layanan nyata.",
                )
            }
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Temuan (${findings.size})", fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        findings.forEach { f ->
                            StatusPill(text = "TEMUAN", color = palette.warning)
                            Text(f, color = palette.textSecondary, fontSize = 12.sp)
                        }
                        androidx.compose.material3.TextButton(
                            onClick = { showTree = !showTree },
                        ) {
                            Text(if (showTree) "Sembunyikan pohon semantik" else "Tampilkan pohon semantik", color = palette.primary)
                        }
                        if (showTree) {
                            Text("Button [desc=\"Simpan\"]", color = palette.textMuted, fontSize = 11.sp)
                            Text("Image [desc=MISSING]", color = palette.textMuted, fontSize = 11.sp)
                        }
                    }
                }
            }
            item {
                RowToggles(
                    smallTarget = smallTarget, onSmall = { smallTarget = it; PerformHapticFeedback(HapticKind.SELECTION) },
                    lowContrast = lowContrast, onLow = { lowContrast = it; PerformHapticFeedback(HapticKind.SELECTION) },
                    noDesc = noDesc, onNoDesc = { noDesc = it; PerformHapticFeedback(HapticKind.SELECTION) },
                    palette = palette,
                )
            }
            item {
                FlatPrimaryButton(
                    text = "Jalankan Audit (simulasi)",
                    onClick = { PerformHapticFeedback(HapticKind.SUCCESS) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun RowToggles(
    smallTarget: Boolean,
    onSmall: (Boolean) -> Unit,
    lowContrast: Boolean,
    onLow: (Boolean) -> Unit,
    noDesc: Boolean,
    onNoDesc: (Boolean) -> Unit,
    palette: com.example.uiapp.theme.AppPalette,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MiniToggle("Target kecil", smallTarget, onSmall, palette)
        MiniToggle("Kontras rendah", lowContrast, onLow, palette)
        MiniToggle("Tanpa deskripsi", noDesc, onNoDesc, palette)
    }
}

@Composable
private fun MiniToggle(label: String, checked: Boolean, onChange: (Boolean) -> Unit, palette: com.example.uiapp.theme.AppPalette) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(label, color = palette.textPrimary)
        androidx.compose.material3.Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = androidx.compose.material3.SwitchDefaults.colors(checkedThumbColor = palette.onPrimary, checkedTrackColor = palette.primary),
        )
    }
}
