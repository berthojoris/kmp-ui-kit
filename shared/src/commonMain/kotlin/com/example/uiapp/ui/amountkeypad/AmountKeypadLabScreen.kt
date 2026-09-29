package com.example.uiapp.ui.amountkeypad

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private const val WalletBalance = 2_450_000L
private const val MaxAmount = 99_999_999L
private val QuickAmounts = listOf(50_000L, 100_000L, 200_000L, 500_000L)

private fun formatRupiah(amount: Long): String {
    val digits = amount.toString()
    val builder = StringBuilder()
    digits.reversed().forEachIndexed { index, c ->
        if (index > 0 && index % 3 == 0) builder.append('.')
        builder.append(c)
    }
    return "Rp " + builder.reverse().toString()
}

private fun formatShort(amount: Long): String = when {
    amount >= 1_000_000L -> "Rp ${amount / 1_000_000L} jt"
    amount >= 1_000L -> "Rp ${amount / 1_000L} rb"
    else -> formatRupiah(amount)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AmountKeypadLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var amount by remember { mutableLongStateOf(0L) }
    var transferDone by remember { mutableStateOf(false) }
    var doneAmount by remember { mutableLongStateOf(0L) }

    val exceedsBalance = amount > WalletBalance
    val canTransfer = amount > 0L && !exceedsBalance && !transferDone

    fun appendDigits(digits: String) {
        if (transferDone) return
        val current = if (amount == 0L) "" else amount.toString()
        val combined = (current + digits).take(9)
        amount = combined.toLongOrNull()?.coerceAtMost(MaxAmount) ?: amount
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Amount Keypad",
                subtitle = "Input nominal transfer fintech",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Info saldo + label simulasi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .padding(14.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Saldo Dompet (Simulasi)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = formatRupiah(WalletBalance),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary,
                        )
                    }
                    Text(
                        text = "Seluruh transaksi di layar ini adalah simulasi lokal dan tidak terhubung ke layanan pembayaran nyata.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = palette.textSecondary,
                    )
                }
            }

            // Display nominal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Nominal Transfer",
                    fontSize = 12.sp,
                    color = palette.textMuted,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formatRupiah(amount),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (exceedsBalance) palette.danger else palette.textPrimary,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = when {
                        transferDone -> "Transfer ${formatRupiah(doneAmount)} berhasil (simulasi)"
                        exceedsBalance -> "Nominal melebihi saldo yang tersedia"
                        amount == 0L -> "Masukkan nominal menggunakan keypad"
                        else -> "Penerima: Rekening Utama · BI-FAST"
                    },
                    fontSize = 12.sp,
                    fontWeight = if (exceedsBalance || transferDone) FontWeight.SemiBold else FontWeight.Normal,
                    color = when {
                        transferDone -> palette.success
                        exceedsBalance -> palette.danger
                        else -> palette.textSecondary
                    },
                )
            }

            // Chip nominal cepat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                QuickAmounts.forEach { quick ->
                    val selected = amount == quick
                    Surface(
                        onClick = { if (!transferDone) amount = quick },
                        enabled = !transferDone,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = if (selected) palette.primaryContainer else palette.surface,
                        border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = formatShort(quick),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selected) palette.onPrimaryContainer else palette.textSecondary,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }

            if (transferDone) {
                // Kartu sukses (simulasi)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.primaryContainer, RoundedCornerShape(12.dp))
                        .border(1.dp, palette.primary, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Transfer Berhasil (Simulasi)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.onPrimaryContainer,
                        )
                        KeypadSummaryRow("Nominal", formatRupiah(doneAmount))
                        KeypadSummaryRow("Biaya admin", "Rp 0 (gratis)")
                        KeypadSummaryRow("Sisa saldo", formatRupiah(WalletBalance - doneAmount))
                    }
                }
                Surface(
                    onClick = {
                        transferDone = false
                        amount = 0L
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Transaksi Baru",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary,
                        )
                    }
                }
            } else {
                // Keypad 3x4
                val keypadRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("00", "0", "DEL"),
                )
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    keypadRows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            row.forEach { key ->
                                Surface(
                                    onClick = {
                                        when (key) {
                                            "DEL" -> amount = amount / 10L
                                            else -> appendDigits(key)
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    color = palette.surface,
                                    border = BorderStroke(1.dp, palette.border),
                                    shadowElevation = 0.dp,
                                    tonalElevation = 0.dp,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1.9f),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = if (key == "DEL") "Hapus" else key,
                                            fontSize = if (key == "DEL") 12.sp else 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (key == "DEL") palette.danger else palette.textPrimary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // CTA transfer
                Surface(
                    onClick = {
                        if (canTransfer) {
                            doneAmount = amount
                            transferDone = true
                        }
                    },
                    enabled = canTransfer,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = if (canTransfer) palette.primary else palette.surfaceMuted,
                    border = BorderStroke(1.dp, if (canTransfer) palette.primary else palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 15.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Transfer Sekarang (Simulasi)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canTransfer) palette.onPrimary else palette.textMuted,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadSummaryRow(label: String, value: String) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, fontSize = 12.sp, color = palette.onPrimaryContainer)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.onPrimaryContainer)
    }
}
