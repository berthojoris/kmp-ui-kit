package com.example.uiapp.ui.referralinvite

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
fun ReferralInviteLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = com.example.uiapp.theme.LocalAppPalette.current
    var copied by remember { mutableStateOf(false) }
    val progress = 2f / 5f

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Referral", subtitle = "Undang & reward", onBack = onBack) },
    ) { innerPadding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(innerPadding).navigationBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                LabIntroCard(title = "Kode referral", body = "State: kode belum aktif, disalin, reward tercapai (simulasi).")
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
                        Text("Kode: INVITE2026", fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        StatusPill(text = if (copied) "DISALIN" else "BELUM", color = if (copied) palette.success else palette.warning)
                        FlatPrimaryButton("Salin Kode", {
                            copied = true
                            PerformHapticFeedback(HapticKind.SUCCESS)
                        }, Modifier.fillMaxWidth())
                        Text("Progres undangan 2/5", color = palette.textSecondary, fontSize = 12.sp)
                        androidx.compose.material3.LinearProgressIndicator(
                            progress = { progress },
                            color = palette.primary,
                            trackColor = palette.surfaceMuted,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}
