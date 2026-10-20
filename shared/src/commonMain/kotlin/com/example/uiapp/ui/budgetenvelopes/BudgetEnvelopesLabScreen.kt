package com.example.uiapp.ui.budgetenvelopes

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
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
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar

private data class Envelope(val id: Int, val name: String, val note: String, val cap: Int)

private val Envelopes = listOf(
    Envelope(1, "Makanan", "Belanja dapur & warung", 800_000),
    Envelope(2, "Transportasi", "Bensin & parkir", 400_000),
    Envelope(3, "Tagihan", "Listrik, air, internet", 600_000),
    Envelope(4, "Hiburan", "Streaming & jalan-jalan", 300_000),
    Envelope(5, "Dana Darurat", "Tidak disentuh kecuali urgent", 500_000),
)

private val Steps = listOf(25_000, 50_000, 100_000)
private const val TotalCash = 2_600_000

private fun formatRupiah(amount: Int): String {
    val s = amount.toString()
    val sb = StringBuilder()
    var c = 0
    for (i in s.indices.reversed()) {
        if (c > 0 && c % 3 == 0) sb.insert(0, '.')
        sb.insert(0, s[i])
        c++
    }
    return "Rp$sb"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BudgetEnvelopesLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var allocations by remember { mutableStateOf(mapOf<Int, Int>()) }
    var step by remember { mutableIntStateOf(50_000) }
    var resets by remember { mutableIntStateOf(0) }

    val allocatedTotal = Envelopes.sumOf { allocations[it.id] ?: 0 }
    val pool = TotalCash - allocatedTotal

    fun move(id: Int, delta: Int) {
        val cur = allocations[id] ?: 0
        allocations = allocations + (id to (cur + delta).coerceIn(0, cur + pool))
    }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Budget Envelopes", subtitle = "Simulasi alokasi uang tunai", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item(key = "intro") {
                    LabIntroCard(
                        title = "Amplop anggaran",
                        body = "Salurkan tunai ke amplop per keperluan. Ketuk + / - untuk memindah uang. " +
                            "Amplop boleh melebihi batas untuk melihat state peringatan, tapi total alokasi tidak boleh melebihi kas.",
                    )
                }
                item(key = "pool") {
                    LabCard {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text("Kas tersisa di tangan", fontSize = 11.sp, color = palette.textMuted)
                                Text(formatRupiah(pool), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (pool == 0) palette.success else palette.textPrimary)
                            }
                            StatusPill(
                                text = when {
                                    pool == 0 -> "Tersalur penuh"
                                    allocatedTotal == 0 -> "Belum dialokasi"
                                    else -> "Sebagian tersalur"
                                },
                                color = when {
                                    pool == 0 -> palette.success
                                    else -> palette.info
                                },
                            )
                        }
                        // Bar proporsi alokasi
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .background(palette.surfaceMuted, RoundedCornerShape(999.dp)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(allocatedTotal.toFloat() / TotalCash)
                                    .height(10.dp)
                                    .background(palette.primary, RoundedCornerShape(999.dp)),
                            )
                        }
                        Text(
                            "Terpakai ${formatRupiah(allocatedTotal)} dari ${formatRupiah(TotalCash)}",
                            fontSize = 11.sp,
                            color = palette.textSecondary,
                        )
                    }
                }
                item(key = "step") {
                    LabCard {
                        Text("Nominal langkah", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Steps.forEach { s ->
                                val selected = step == s
                                Surface(
                                    onClick = { step = s; PerformHapticFeedback(HapticKind.SELECTION) },
                                    shape = RoundedCornerShape(999.dp),
                                    color = if (selected) palette.primary else palette.surface,
                                    border = BorderStroke(1.dp, palette.border),
                                    shadowElevation = 0.dp,
                                    tonalElevation = 0.dp,
                                ) {
                                    Text(
                                        formatRupiah(s),
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) palette.onPrimary else palette.textSecondary,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    )
                                }
                            }
                        }
                    }
                }
                items(Envelopes, key = { it.id }) { env ->
                    EnvelopeRow(
                        envelope = env,
                        allocated = allocations[env.id] ?: 0,
                        step = step,
                        canAdd = pool >= step,
                        onAdd = { move(env.id, step); PerformHapticFeedback(HapticKind.LIGHT) },
                        onRemove = {
                            allocations = allocations + (env.id to ((allocations[env.id] ?: 0) - step).coerceAtLeast(0))
                            PerformHapticFeedback(HapticKind.SELECTION)
                        },
                    )
                }
                item(key = "summary") {
                    LabCard {
                        Text("Ringkasan bulan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        val overCount = Envelopes.count { (allocations[it.id] ?: 0) > it.cap }
                        Text(
                            if (overCount == 0)
                                "Semua amplop masih dalam batas. Pertahankan disiplin alokasi."
                            else
                                "$overCount amplop melebihi batas — pertimbangkan pindah dana dari amplop lain.",
                            fontSize = 11.sp,
                            color = if (overCount == 0) palette.textSecondary else palette.warning,
                        )
                        OutlinedButton(
                            onClick = { allocations = emptyMap(); resets++; PerformHapticFeedback(HapticKind.MEDIUM) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Reset alokasi (dipakai ${resets}x)")
                        }
                    }
                }
                item(key = "space") { Spacer(Modifier.height(4.dp)) }
            }

            // Action bar bawah
            Surface(color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (pool == 0) "Semua kas tersalur" else "Sisa ${formatRupiah(pool)} belum dialokasi",
                            fontSize = 12.sp, color = palette.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            if (pool == 0) "Tap amplop lalu tombol - untuk menarik kembali" else "Pilih langkah, lalu ketuk + pada amplop",
                            fontSize = 10.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Button(
                        onClick = { allocations = emptyMap(); PerformHapticFeedback(HapticKind.SUCCESS) },
                        enabled = allocatedTotal > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = palette.primary, contentColor = palette.onPrimary),
                    ) {
                        Text("Salurkan Ulang", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
private fun EnvelopeRow(
    envelope: Envelope,
    allocated: Int,
    step: Int,
    canAdd: Boolean,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val over = allocated > envelope.cap
    val fill = (allocated.toFloat() / envelope.cap).coerceAtMost(1f)
    LabCard {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(envelope.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    StatusPill(
                        text = when {
                            over -> "Over budget"
                            allocated == envelope.cap -> "Pas batas"
                            allocated == 0 -> "Kosong"
                            else -> "Terkunci"
                        },
                        color = when {
                            over -> palette.danger
                            allocated == envelope.cap -> palette.success
                            else -> palette.textMuted
                        },
                    )
                }
                Text(envelope.note, fontSize = 11.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(formatRupiah(allocated), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (over) palette.danger else palette.textPrimary)
        }
        // Bar isi amplop
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(palette.surfaceMuted, RoundedCornerShape(999.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fill)
                    .height(8.dp)
                    .background(if (over) palette.danger else palette.primary, RoundedCornerShape(999.dp)),
            )
        }
        Text("Batas ${formatRupiah(envelope.cap)}", fontSize = 10.sp, color = palette.textSecondary)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            SmallStepButton(text = "- ${formatRupiah(step)}", enabled = allocated > 0, onClick = onRemove)
            SmallStepButton(text = "+ ${formatRupiah(step)}", enabled = canAdd, onClick = onAdd, primary = true)
        }
        if (!canAdd) {
            Text("Kas habis — tarik dari amplop lain dulu", fontSize = 10.sp, color = palette.warning)
        }
    }
}

@Composable
private fun SmallStepButton(text: String, enabled: Boolean, onClick: () -> Unit, primary: Boolean = false) {
    val palette = LocalAppPalette.current
    if (primary) {
        Button(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.primary,
                contentColor = palette.onPrimary,
                disabledContainerColor = palette.surfaceMuted,
                disabledContentColor = palette.textMuted,
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(36.dp),
        ) { Text(text, fontSize = 11.sp, maxLines = 1) }
    } else {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = palette.textSecondary,
                disabledContentColor = palette.textMuted,
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(36.dp),
        ) { Text(text, fontSize = 11.sp, maxLines = 1) }
    }
}
