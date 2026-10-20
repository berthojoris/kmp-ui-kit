package com.example.uiapp.ui.currencyexchange

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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

private data class RateHistory(val pair: String, val rate: Double, val time: String)

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun CurrencyExchangeLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var from by remember { mutableStateOf("IDR") }
    var to by remember { mutableStateOf("USD") }
    var amount by remember { mutableFloatStateOf(1_000_000f) }
    var rate by remember { mutableFloatStateOf(0.000063f) }
    var stale by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    val rotated = animateFloatAsState(if (stale) 180f else 0f).value
    val result = (amount * rate).toDouble()
    val swapRotation = animateFloatAsState(0f).value

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Currency Exchange", subtitle = "Simulasi kurs, bukan layanan forex", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item(key = "intro") {
                    LabIntroCard(title = "Konverter", body = "Pilih mata uang, kurs mock, mini tren, dan riwayat konversi. Swap beranimasi.")
                }
                item(key = "conv") {
                    LabCard {
                        LabSectionTitle("Konversi", "Ketuk swap untuk menukar arah")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(from, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = palette.textPrimary)
                                Text("${amount.toInt()}", color = palette.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Button(onClick = { val t = from; from = to; to = t; rate = 1f / rate; PerformHapticFeedback(HapticKind.HEAVY) }, modifier = Modifier.size(40.dp)) {
                                Text("↔", maxLines = 1)
                            }
                            Column(Modifier.weight(1f)) {
                                Text(to, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = palette.textPrimary)
                                Text(String.format("%.2f", result), color = palette.success, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(onClick = { amount = (amount + 100000f).coerceAtMost(9_999_999f) }) { Text("+100k", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            OutlinedButton(onClick = { amount = (amount - 100000f).coerceAtLeast(0f) }) { Text("-100k", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            OutlinedButton(onClick = { stale = true }) { Text("Kurs basi", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            OutlinedButton(onClick = { stale = false; loading = false; rate = 0.000063f }) { Text("Refresh", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        }
                        Spacer(Modifier.height(6.dp))
                        StatusPill(
                            text = when {
                                loading -> "Memuat kurs"
                                stale -> "Kurs basi"
                                amount <= 0 -> "Nilai tidak valid"
                                else -> "Kurs terbaru"
                            },
                            color = when {
                                loading -> palette.info
                                stale -> palette.warning
                                amount <= 0 -> palette.danger
                                else -> palette.success
                            },
                        )
                    }
                }
                item(key = "trend") {
                    LabCard {
                        LabSectionTitle("Mini tren", "Gerak kurs simulasi")
                        Canvas(Modifier.fillMaxWidth().height(100.dp)) {
                            val pts = listOf(0.9f, 0.95f, 1f, 0.98f, 1.02f, 0.99f, 1.03f, 1.01f)
                            val step = size.width / (pts.size - 1)
                            for (i in 1 until pts.size) {
                                drawLine(
                                    color = palette.primary,
                                    start = Offset((i - 1) * step, size.height - pts[i - 1] * size.height * 0.5f),
                                    end = Offset(i * step, size.height - pts[i] * size.height * 0.5f),
                                    strokeWidth = 2.dp.toPx(),
                                    cap = StrokeCap.Round,
                                )
                            }
                        }
                    }
                }
                item(key = "history") {
                    LabCard {
                        LabSectionTitle("Riwayat", "Konversi terbaru")
                        listOf(
                            RateHistory("IDR → USD", rate.toDouble(), "10:24"),
                            RateHistory("USD → EUR", 0.92, "09:11"),
                        ).forEach { h ->
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Text(h.pair, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${String.format("%.6f", h.rate)} @ ${h.time}", color = palette.textMuted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
            Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                Text("Simulasi kurs, tanpa forex nyata", color = palette.textMuted, fontSize = 11.sp)
            }
        }
    }
}