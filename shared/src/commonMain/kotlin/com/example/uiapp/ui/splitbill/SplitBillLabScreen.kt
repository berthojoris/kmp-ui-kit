package com.example.uiapp.ui.splitbill

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LightAppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private data class BillItem(val id: Int, val name: String, val price: Int, val qty: Int = 1)
private data class Person(val name: String, val initial: String, val color: Color)

private val BillItems = listOf(
    BillItem(1, "Nasi Goreng Spesial", 35_000),
    BillItem(2, "Ayam Bakar Madu", 45_000),
    BillItem(3, "Es Teh Manis", 8_000, 3),
    BillItem(4, "Sate Ayam 10 Tusuk", 30_000),
    BillItem(5, "Gado-Gado", 25_000),
    BillItem(6, "Jus Alpukat", 15_000, 2),
    BillItem(7, "Kerupuk Udang", 5_000),
    BillItem(8, "Es Campur", 12_000),
)

private val People = listOf(
    Person("Andi", "A", LightAppPalette.primary),
    Person("Budi", "B", LightAppPalette.info),
    Person("Citra", "C", LightAppPalette.success),
    Person("Diana", "D", LightAppPalette.warning),
)

private fun formatRupiah(amount: Int): String {
    val str = amount.toString()
    val sb = StringBuilder()
    var cnt = 0
    for (i in str.indices.reversed()) { if (cnt > 0 && cnt % 3 == 0) sb.insert(0, '.'); sb.insert(0, str[i]); cnt++ }
    return "Rp $sb"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SplitBillLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    // Map: itemId -> set of person names
    var assignments by remember { mutableStateOf(mapOf<Int, Set<String>>()) }
    var includeTax by remember { mutableStateOf(true) }
    var includeService by remember { mutableStateOf(true) }
    var showSummary by remember { mutableStateOf(false) }

    val subtotal = remember { BillItems.sumOf { it.price * it.qty } }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Split Bill", subtitle = "Simulasi bagi tagihan", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(BillItems, key = { it.id }) { item ->
                    val assigned = assignments[item.id] ?: emptySet()
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, if (assigned.isEmpty()) palette.warning.copy(alpha = 0.5f) else palette.border),
                        shadowElevation = 0.dp, tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(if (item.qty > 1) "${item.qty}x ${formatRupiah(item.price)}" else formatRupiah(item.price), fontSize = 12.sp, color = palette.textSecondary)
                                }
                                Text(formatRupiah(item.price * item.qty), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                People.forEach { person ->
                                    val isOn = person.name in assigned
                                    Surface(
                                        onClick = {
                                            val cur = assignments[item.id] ?: emptySet()
                                            assignments = assignments + (item.id to (if (isOn) cur - person.name else cur + person.name))
                                        },
                                        shape = CircleShape,
                                        color = if (isOn) person.color else palette.surfaceMuted,
                                        border = BorderStroke(1.dp, if (isOn) person.color else palette.border),
                                        shadowElevation = 0.dp, tonalElevation = 0.dp,
                                        modifier = Modifier.size(32.dp),
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(person.initial, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isOn) palette.onPrimary else palette.textMuted)
                                        }
                                    }
                                }
                                Spacer(Modifier.weight(1f))
                                Text(
                                    if (assigned.isEmpty()) "Belum diisi" else "${assigned.size} orang",
                                    fontSize = 11.sp, color = if (assigned.isEmpty()) palette.warning else palette.textMuted,
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                )
                            }
                        }
                    }
                }

                // Charges toggles
                item(key = "charges") {
                    Spacer(Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Pajak 10%", fontSize = 13.sp, color = palette.textPrimary)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(formatRupiah((subtotal * 0.1).toInt()), fontSize = 12.sp, color = palette.textSecondary)
                                    Spacer(Modifier.width(8.dp))
                                    Switch(checked = includeTax, onCheckedChange = { includeTax = it }, colors = SwitchDefaults.colors(checkedTrackColor = palette.primary, checkedThumbColor = palette.onPrimary))
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Biaya Layanan 5%", fontSize = 13.sp, color = palette.textPrimary)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(formatRupiah((subtotal * 0.05).toInt()), fontSize = 12.sp, color = palette.textSecondary)
                                    Spacer(Modifier.width(8.dp))
                                    Switch(checked = includeService, onCheckedChange = { includeService = it }, colors = SwitchDefaults.colors(checkedTrackColor = palette.primary, checkedThumbColor = palette.onPrimary))
                                }
                            }
                        }
                    }
                }

                // Summary
                item(key = "summary") {
                    Spacer(Modifier.height(8.dp))
                    val taxAmt = if (includeTax) (subtotal * 0.1).toInt() else 0
                    val svcAmt = if (includeService) (subtotal * 0.05).toInt() else 0
                    val totalBill = subtotal + taxAmt + svcAmt

                    // Per-person calculation
                    val perPerson = remember(assignments, includeTax, includeService) {
                        val map = mutableMapOf<String, Int>()
                        People.forEach { map[it.name] = 0 }
                        BillItems.forEach { item ->
                            val people = assignments[item.id] ?: emptySet()
                            if (people.isNotEmpty()) {
                                val share = (item.price * item.qty) / people.size
                                people.forEach { map[it] = (map[it] ?: 0) + share }
                            }
                        }
                        // Add proportional charges
                        val assignedTotal = map.values.sum()
                        if (assignedTotal > 0) {
                            map.forEach { (name, amount) ->
                                val ratio = amount.toFloat() / assignedTotal
                                map[name] = amount + (taxAmt * ratio).toInt() + (svcAmt * ratio).toInt()
                            }
                        }
                        map
                    }

                    Surface(shape = RoundedCornerShape(12.dp), color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Tagihan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                Text(formatRupiah(totalBill), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                            }
                            Spacer(Modifier.height(10.dp))
                            People.forEach { person ->
                                val amt = perPerson[person.name] ?: 0
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(24.dp).clip(CircleShape).background(person.color), contentAlignment = Alignment.Center) {
                                        Text(person.initial, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(person.name, fontSize = 13.sp, color = palette.textPrimary, modifier = Modifier.weight(1f))
                                    Text(formatRupiah(amt), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Simulasi — bukan aplikasi pembayaran sungguhan", fontSize = 10.sp, color = palette.textMuted)
                    Spacer(Modifier.height(80.dp))
                }
            }

            // Bottom action
            Surface(color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp), contentAlignment = Alignment.Center) {
                    Surface(
                        onClick = { showSummary = !showSummary },
                        shape = RoundedCornerShape(12.dp),
                        color = palette.primary,
                        shadowElevation = 0.dp, tonalElevation = 0.dp,
                    ) {
                        Text("Salin Ringkasan", modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
                    }
                }
            }
        }
    }
}
