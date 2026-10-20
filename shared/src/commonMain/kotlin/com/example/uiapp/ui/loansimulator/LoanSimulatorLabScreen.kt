package com.example.uiapp.ui.loansimulator

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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.ExperimentalComposeUiApi
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

private data class AmortRow(val month: Int, val principal: Double, val interest: Double, val balance: Double)

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun LoanSimulatorLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var principal by remember { mutableDoubleStateOf(500_000_000.0) }
    var tenor by remember { mutableIntStateOf(20) }
    var rate by remember { mutableDoubleStateOf(9.5) }
    var prepay by remember { mutableDoubleStateOf(0.0) }
    val monthly = emi(principal, rate, tenor * 12)
    val totalInterest = monthly * tenor * 12 - principal
    val invalid = principal <= 0 || rate <= 0 || tenor <= 0
    val rows = remember(principal, rate, tenor) { amortization(principal, rate, tenor * 12).take(12) }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Loan Simulator", subtitle = "Simulasi pinjaman KPR, perhitungan lokal", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item(key = "intro") {
                    LabIntroCard(title = "Simulator pinjaman", body = "Atur pokok, tenor, bunga; lihat cicilan, bunga total, dan tabel amortisasi.")
                }
                item(key = "inputs") {
                    LabCard {
                        LabSectionTitle("Parameter", "Slider")
                        Text("Pokok: ${fmt(principal)}", color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Slider(value = principal.toFloat(), onValueChange = { principal = it.toDouble() }, valueRange = 50_000_000f..1_500_000_000f)
                        Text("Tenor: ${tenor} tahun", color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Slider(value = tenor.toFloat(), onValueChange = { tenor = it.toInt() }, valueRange = 1f..30f)
                        Text("Bunga: ${String.format("%.1f", rate)}% / tahun", color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Slider(value = rate.toFloat(), onValueChange = { rate = it.toDouble() }, valueRange = 3f..15f)
                        Text("Pelunasan dipercepat: ${fmt(prepay)} / bulan", color = palette.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Slider(value = (prepay / 5000000.0).toFloat().coerceIn(0f, 4f), onValueChange = { prepay = it.toDouble() * 5000000.0 }, valueRange = 0f..4f)
                    }
                }
                item(key = "summary") {
                    LabCard {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text("Cicilan bulanan", fontSize = 11.sp, color = palette.textMuted)
                                Text(fmt(monthly), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (invalid) palette.danger else palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Column {
                                Text("Total bunga", fontSize = 11.sp, color = palette.textMuted)
                                Text(fmt(totalInterest), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = palette.warning, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        StatusPill(
                            text = if (invalid) "INPUT TIDAK VALID" else if (prepay > 0) "PELUNASAN DIPERCEPAT" else "SKENARIO NORMAL",
                            color = if (invalid) palette.danger else if (prepay > 0) palette.info else palette.success,
                        )
                    }
                }
                item(key = "table") {
                    LabCard {
                        LabSectionTitle("Amortisasi (12 bulan pertama)", "Pokok vs bunga")
                        rows.forEach { r ->
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                                Text("Bulan ${r.month}", color = palette.textPrimary, fontSize = 11.sp)
                                Text("P ${fmt(r.principal)}", color = palette.success, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("B ${fmt(r.interest)}", color = palette.danger, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("Sisa ${fmt(r.balance)}", color = palette.textMuted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
            Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                Text("Semua perhitungan lokal (simulasi)", color = palette.textMuted, fontSize = 11.sp)
            }
        }
    }
}

private fun emi(p: Double, annualRate: Double, months: Int): Double {
    if (p <= 0 || annualRate <= 0 || months <= 0) return 0.0
    val r = annualRate / 100.0 / 12.0
    return p * r * Math.pow(1 + r, months.toDouble()) / (Math.pow(1 + r, months.toDouble()) - 1)
}

private fun amortization(p: Double, annualRate: Double, months: Int): List<AmortRow> {
    val r = annualRate / 100.0 / 12.0
    val m = emi(p, annualRate, months)
    var bal = p
    val out = mutableListOf<AmortRow>()
    for (i in 1..months) {
        val interest = bal * r
        val principal = m - interest
        bal = (bal - principal).coerceAtLeast(0.0)
        out.add(AmortRow(i, principal, interest, bal))
    }
    return out
}

private fun fmt(value: Double): String {
    val v = value.toLong()
    val s = v.toString()
    val sb = StringBuilder()
    var c = 0
    for (i in s.indices.reversed()) {
        if (c > 0 && c % 3 == 0) sb.insert(0, '.')
        sb.insert(0, s[i])
        c++
    }
    return "Rp$sb"
}