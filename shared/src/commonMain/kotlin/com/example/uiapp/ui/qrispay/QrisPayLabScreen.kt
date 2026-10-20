package com.example.uiapp.ui.qrispay

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.ExperimentalComposeUiApi
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

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun QrisPayLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var scanned by remember { mutableStateOf(false) }
    var expired by remember { mutableStateOf(false) }
    var amount by remember { mutableIntStateOf(50000) }
    var status by remember { mutableStateOf("idle") } // idle, paying, success, failed

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "QRIS Scan-to-Pay", subtitle = "Simulasi pembayaran Indonesia", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item(key = "intro") {
                    LabIntroCard(
                        title = "Scan QRIS",
                        body = "Buka/pindai QR merchant, masukkan nominal, konfirmasi, lalu bukti bayar. Semua mock, tidak ada pembayaran nyata.",
                    )
                }
                item(key = "viewfinder") {
                    LabCard {
                        QrViewfinder(expired = expired)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { scanned = true; expired = false; status = "idle"; PerformHapticFeedback(HapticKind.SUCCESS) },
                                modifier = Modifier.weight(1f),
                            ) { Text("Pindai sukses", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            OutlinedButton(
                                onClick = { expired = true; scanned = false; status = "failed" },
                                modifier = Modifier.weight(1f),
                            ) { Text("QR kadaluarsa", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        }
                    }
                }
                if (scanned) {
                    item(key = "merchant") {
                        LabCard {
                            Text("Merchant: Toko Sejahtera", fontWeight = FontWeight.Bold, color = palette.textPrimary)
                            Text("QRIS • valid • simulasi", fontSize = 11.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    item(key = "amount") {
                        LabCard {
                            LabSectionTitle("Nominal", "Gunakan chip atau tombol +/-")
                            Text("Rp ${formatRupiah(amount)}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(10000, 25000, 50000, 100000).forEach { v ->
                                    OutlinedButton(onClick = { amount = v }) { Text("$v", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { amount = (amount - 5000).coerceAtLeast(0) }, modifier = Modifier.weight(1f)) { Text("-", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                                Button(onClick = { amount = (amount + 5000).coerceAtMost(999999) }, modifier = Modifier.weight(1f)) { Text("+", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            }
                        }
                    }
                    item(key = "confirm") {
                        LabCard {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { status = "paying" },
                                    enabled = amount > 0 && status != "paying",
                                    modifier = Modifier.weight(1f),
                                ) { Text("Bayar", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                                OutlinedButton(
                                    onClick = { scanned = false; status = "idle"; amount = 50000 },
                                    modifier = Modifier.weight(1f),
                                ) { Text("Batal", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            }
                            if (status == "paying") {
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { status = "success"; PerformHapticFeedback(HapticKind.SUCCESS) }, modifier = Modifier.weight(1f)) { Text("Sukses", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                                    OutlinedButton(onClick = { status = "failed" }, modifier = Modifier.weight(1f)) { Text("Gagal", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                                }
                            }
                        }
                    }
                }
                if (status == "success") {
                    item(key = "success") {
                        LabCard {
                            StatusPill(text = "PEMBAYARAN BERHASIL", color = palette.success)
                            Spacer(Modifier.height(6.dp))
                            Text("Bukti bayar: TRX-${amount}", color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
                if (status == "failed") {
                    item(key = "failed") {
                        LabCard {
                            StatusPill(text = "GAGAL / QR TIDAK VALID", color = palette.danger)
                            Spacer(Modifier.height(6.dp))
                            Text("Coba pindai ulang atau periksa koneksi.", color = palette.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
            Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                Text("Simulasi khas Indonesia, tanpa pembayaran nyata", color = palette.textMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun QrViewfinder(expired: Boolean) {
    val palette = LocalAppPalette.current
    Box(
        Modifier.fillMaxWidth().height(180.dp).background(palette.surfaceMuted, RoundedCornerShape(16.dp)).border(1.dp, palette.border, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(130.dp)) {
            val color = if (expired) Color(0xFFEF4444) else palette.primary
            drawRoundRect(
                color = color,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
            )
            // laser
            drawLine(color = color.copy(alpha = 0.7f), start = androidx.compose.ui.geometry.Offset(0f, size.height / 2), end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2), strokeWidth = 2.dp.toPx())
        }
        Text(if (expired) "Kadaluarsa" else "Arahkan ke QR", color = palette.textMuted, fontSize = 11.sp)
    }
}

private fun formatRupiah(amount: Int): String {
    val s = amount.toString()
    val sb = StringBuilder()
    var c = 0
    for (i in s.indices.reversed()) {
        if (c > 0 && c % 3 == 0) sb.insert(0, '.')
        sb.insert(0, s[i])
        c++
    }
    return sb.toString()
}