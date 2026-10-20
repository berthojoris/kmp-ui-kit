package com.example.uiapp.ui.securevault

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
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import com.example.uiapp.ui.components.FlatPrimaryButton

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun SecureVaultLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = com.example.uiapp.theme.LocalAppPalette.current
    var unlocked by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    var idle by remember { mutableStateOf(false) }

    Scaffold(containerColor = palette.background, topBar = { UiTopBar(title = "Secure Vault", subtitle = "Folder terkunci (simulasi)", onBack = onBack) }) { innerPadding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(innerPadding).navigationBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { LabIntroCard(title = "Vault", body = "State: terkunci, autentikasi gagal, terbuka, auto-lock idle. Biometrik simulasi.") }
            item {
                Surface(shape = RoundedCornerShape(14.dp), color = palette.surface, border = androidx.compose.foundation.BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), Arrangement.spacedBy(8.dp)) {
                        StatusPill(text = if (unlocked) "TERBUKA" else "TERKUNCI", color = if (unlocked) palette.success else palette.warning)
                        if (!unlocked) {
                            Text("Item sensitif disembunyikan (blur simulasi).", color = palette.textSecondary, fontSize = 12.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FlatPrimaryButton("Buka dengan Biometrik", {
                                    unlocked = true
                                    failed = false
                                    PerformHapticFeedback(HapticKind.SUCCESS)
                                }, Modifier.weight(1f))
                                androidx.compose.material3.TextButton(onClick = {
                                    failed = true
                                    PerformHapticFeedback(HapticKind.WARNING)
                                }) { Text("Gagal", color = palette.danger) }
                            }
                            if (failed) Text("Autentikasi gagal", color = palette.danger, fontSize = 12.sp)
                        } else {
                            Text("• KTP (tersimpan)\n• Kata sandi bank\n• Dokumen pajak", color = palette.textPrimary)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FlatPrimaryButton("Kunci Lagi", { unlocked = false; PerformHapticFeedback(HapticKind.MEDIUM) }, Modifier.weight(1f))
                                androidx.compose.material3.TextButton(onClick = { idle = true; if (idle) unlocked = false; PerformHapticFeedback(HapticKind.WARNING) }) {
                                    Text("Idle auto-lock", color = palette.primary)
                                }
                            }
                            if (idle) Text("Auto-lock aktif", color = palette.textMuted, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
