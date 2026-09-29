package com.example.uiapp.ui.tickercounter

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlin.random.Random

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TickerCounterLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var balance by remember { mutableLongStateOf(2450000L) }
    var stockPrice by remember { mutableLongStateOf(48500L) }
    var stepCount by remember { mutableLongStateOf(8420L) }
    var isPriceUp by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Rolling Number Ticker",
                subtitle = "Odometer Vertikal, Saldo, Saham",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Section 1: Saldo Rekening Bank / FinTech Ticker
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Fintech Balance Odometer", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                    Text("Setiap digit bergulir vertikal secara independen saat nominal berubah", fontSize = 12.sp, color = palette.textMuted)
                    Spacer(modifier = Modifier.height(18.dp))

                    Text("Total Saldo Tabungan", fontSize = 11.sp, color = palette.textMuted, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Rp ",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary,
                        )
                        RollingCurrencyTicker(
                            amount = balance,
                            palette = palette,
                            fontSize = 32.sp,
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Action buttons to adjust balance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Surface(
                            onClick = { balance += 50000L },
                            shape = RoundedCornerShape(8.dp),
                            color = palette.primaryContainer,
                            border = BorderStroke(1.dp, palette.primary),
                            modifier = Modifier.weight(1f),
                        ) {
                            Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                Text("+50k", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.onPrimaryContainer)
                            }
                        }
                        Surface(
                            onClick = { balance += 250000L },
                            shape = RoundedCornerShape(8.dp),
                            color = palette.primaryContainer,
                            border = BorderStroke(1.dp, palette.primary),
                            modifier = Modifier.weight(1f),
                        ) {
                            Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                Text("+250k", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.onPrimaryContainer)
                            }
                        }
                        Surface(
                            onClick = { if (balance > 100000L) balance -= 100000L },
                            shape = RoundedCornerShape(8.dp),
                            color = palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.weight(1f),
                        ) {
                            Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                Text("-100k", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.danger)
                            }
                        }
                        Surface(
                            onClick = { balance = Random.nextLong(1000000L, 9999999L) },
                            shape = RoundedCornerShape(8.dp),
                            color = palette.primary,
                            modifier = Modifier.weight(1f),
                        ) {
                            Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                Text("Acak ⚄", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Section 2: Live Stock & Crypto Ticker
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Saham & Crypto Ticker (BBCA)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                            Text("Indikator flash hijau/merah saat harga bergerak", fontSize = 12.sp, color = palette.textMuted)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isPriceUp) palette.success else palette.danger)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = if (isPriceUp) "▲ +2.4%" else "▼ -1.8%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Rp ", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        RollingCurrencyTicker(
                            amount = stockPrice,
                            palette = palette,
                            fontSize = 26.sp,
                            textColor = if (isPriceUp) palette.success else palette.danger,
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            onClick = {
                                stockPrice += Random.nextLong(100L, 500L)
                                isPriceUp = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = palette.success.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, palette.success),
                            modifier = Modifier.weight(1f),
                        ) {
                            Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                                Text("Naik ▲", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.success)
                            }
                        }
                        Surface(
                            onClick = {
                                if (stockPrice > 500L) stockPrice -= Random.nextLong(100L, 500L)
                                isPriceUp = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = palette.danger.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, palette.danger),
                            modifier = Modifier.weight(1f),
                        ) {
                            Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                                Text("Turun ▼", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.danger)
                            }
                        }
                    }
                }
            }

            // Section 3: Daily Steps & Activity Counter
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Langkah Kaki Hari Ini", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                        Text("Target: 10.000 langkah", fontSize = 12.sp, color = palette.textMuted)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RollingCurrencyTicker(
                                amount = stepCount,
                                palette = palette,
                                fontSize = 24.sp,
                                textColor = palette.primary,
                            )
                            Text(" langkah", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                        }
                    }
                    Surface(
                        onClick = { stepCount += 500L },
                        shape = RoundedCornerShape(10.dp),
                        color = palette.primary,
                    ) {
                        Text("+500", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RollingCurrencyTicker(
    amount: Long,
    palette: com.example.uiapp.theme.AppPalette,
    fontSize: androidx.compose.ui.unit.TextUnit,
    textColor: Color = palette.textPrimary,
) {
    val formatted = remember(amount) {
        amount.toString().reversed().chunked(3).joinToString(".").reversed()
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        formatted.forEachIndexed { index, char ->
            if (char.isDigit()) {
                AnimatedDigitSlot(
                    digit = char,
                    textColor = textColor,
                    fontSize = fontSize,
                )
            } else {
                Text(
                    text = char.toString(),
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                )
            }
        }
    }
}

@Composable
private fun AnimatedDigitSlot(
    digit: Char,
    textColor: Color,
    fontSize: androidx.compose.ui.unit.TextUnit,
) {
    AnimatedContent(
        targetState = digit,
        transitionSpec = {
            if (targetState > initialState) {
                slideInVertically(animationSpec = tween(280)) { height -> height } + fadeIn() togetherWith
                        slideOutVertically(animationSpec = tween(280)) { height -> -height } + fadeOut()
            } else {
                slideInVertically(animationSpec = tween(280)) { height -> -height } + fadeIn() togetherWith
                        slideOutVertically(animationSpec = tween(280)) { height -> height } + fadeOut()
            }
        },
    ) { currentDigit ->
        Text(
            text = currentDigit.toString(),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = textColor,
        )
    }
}
