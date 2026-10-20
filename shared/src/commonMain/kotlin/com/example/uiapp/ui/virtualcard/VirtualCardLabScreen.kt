package com.example.uiapp.ui.virtualcard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
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

private data class Tx(val id: Int, val merchant: String, val amount: Int, val time: String)

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun VirtualCardLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var frozen by remember { mutableStateOf(false) }
    var limit by remember { mutableFloatStateOf(5_000_000f) }
    var rotated by remember { mutableStateOf(false) }
    var used by remember { mutableStateOf(1_250_000f) }
    val history = remember { mutableListOf(Tx(1, "Netflix", 189000, "Kemarin"), Tx(2, "Tokopedia", 340000, "Hari ini")) }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Virtual Card", subtitle = "Simulasi kartu virtual, bukan bank", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item(key = "intro") {
                    LabIntroCard(title = "Kartu virtual", body = "Freeze instan, batas transaksi, rotasi nomor, dan riwayat pemakaian. Semua mock.")
                }
                item(key = "card") {
                    LabCard {
                        VirtualCardVisual(frozen = frozen, rotated = rotated)
                        Spacer(Modifier.height(8.dp))
                        StatusPill(
                            text = if (frozen) "DIBEKUKAN" else if (used >= limit * 0.9f) "LIMIT HAMPIR HABIS" else "AKTIF",
                            color = if (frozen) palette.danger else if (used >= limit * 0.9f) palette.warning else palette.success,
                        )
                    }
                }
                item(key = "controls") {
                    LabCard {
                        LabSectionTitle("Kontrol", "Freeze/limit/rotate")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { frozen = !frozen; PerformHapticFeedback(HapticKind.HEAVY) }, modifier = Modifier.weight(1f)) {
                                Text(if (frozen) "Unfreeze" else "Freeze", maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            OutlinedButton(onClick = { rotated = !rotated }, modifier = Modifier.weight(1f)) {
                                Text("Rotasi nomor", maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Batas bulanan: Rp ${format(long = limit.toLong())}", color = palette.textSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Slider(value = limit, onValueChange = { limit = it }, valueRange = 1_000_000f..10_000_000f)
                        Text("Terpakai: Rp ${format(long = used.toLong())}", color = palette.textMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                item(key = "history") {
                    LabCard {
                        LabSectionTitle("Riwayat", "Pemakaian kartu")
                        history.forEach { t ->
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Column(Modifier.weight(1f)) {
                                    Text(t.merchant, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(t.time, fontSize = 10.sp, color = palette.textMuted)
                                }
                                Text("-Rp ${format(t.amount.toLong())}", color = palette.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
            Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                Text("Simulasi kontrol kartu, tanpa bank nyata", color = palette.textMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun VirtualCardVisual(frozen: Boolean, rotated: Boolean) {
    val pal = LocalAppPalette.current
    val brush = Brush.linearGradient(colors = listOf(pal.primary, pal.primary.copy(alpha = 0.5f)))
    Box(Modifier.fillMaxWidth().height(150.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            drawRoundRect(
                brush = brush,
                cornerRadius = CornerRadius(18.dp.toPx()),
                size = Size(size.width, size.height),
            )
            // simple chip
            drawRoundRect(
                color = Color(0xFFEAB308),
                topLeft = Offset(20.dp.toPx(), 20.dp.toPx()),
                size = Size(45.dp.toPx(), 30.dp.toPx()),
                cornerRadius = CornerRadius(6.dp.toPx()),
            )
        }
        Column(Modifier.padding(20.dp)) {
            Text(if (rotated) "•••• •••• •••• 4821" else "•••• •••• •••• 9917", color = pal.onPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(if (frozen) "DIBEKUKAN" else "Berlaku 12/28", color = pal.onPrimary.copy(alpha = 0.85f), fontSize = 11.sp)
        }
    }
}

private fun format(long: Long): String {
    val s = long.toString()
    val sb = StringBuilder()
    var c = 0
    for (i in s.indices.reversed()) {
        if (c > 0 && c % 3 == 0) sb.insert(0, '.')
        sb.insert(0, s[i])
        c++
    }
    return sb.toString()
}