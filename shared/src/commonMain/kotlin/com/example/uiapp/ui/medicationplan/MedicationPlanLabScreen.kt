package com.example.uiapp.ui.medicationplan

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
fun MedicationPlanLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = com.example.uiapp.theme.LocalAppPalette.current

    val meds = remember {
        listOf(
            Triple("Paracetamol 500mg", "08:00", "belum"),
            Triple("Vitamin C", "12:00", "belum"),
            Triple("Amoxicillin", "20:00", "belum"),
        )
    }
    val state = remember { mutableStateOf(meds.map { it.third }.toMutableList()) }

    Scaffold(containerColor = palette.background, topBar = { UiTopBar(title = "Jadwal Obat", subtitle = "Kepatuhan simulasi", onBack = onBack) }) { innerPadding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(innerPadding).navigationBarsPadding(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { LabIntroCard(title = "Medication Schedule", body = "State: belum dijadwalkan, jatuh tempo, diminum, terlewat.") }
            item {
                Surface(shape = RoundedCornerShape(14.dp), color = palette.surface, border = androidx.compose.foundation.BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), Arrangement.spacedBy(8.dp)) {
                        Text("Streak kepatuhan: 5 hari", fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        meds.forEachIndexed { i, m ->
                            val st = state.value[i]
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(m.first, color = palette.textPrimary, fontWeight = FontWeight.SemiBold)
                                    Text(m.second, color = palette.textMuted, fontSize = 12.sp)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    StatusPill(text = st.uppercase(), color = when (st) { "diminum" -> palette.success; "terlewat" -> palette.danger; else -> palette.warning })
                                    androidx.compose.material3.TextButton(onClick = {
                                        state.value[i] = if (st == "diminum") "terlewat" else "diminum"
                                        state.value = state.value.toMutableList()
                                        PerformHapticFeedback(HapticKind.SELECTION)
                                    }) { Text(if (st == "diminum") "Tandai terlewat" else "Tandai diminum", color = palette.primary, fontSize = 11.sp) }
                                }
                            }
                        }
                    }
                }
            }
            item {
                FlatPrimaryButton("Tambah Pengingat Mock", { PerformHapticFeedback(HapticKind.MEDIUM) }, Modifier.fillMaxWidth())
            }
        }
    }
}
