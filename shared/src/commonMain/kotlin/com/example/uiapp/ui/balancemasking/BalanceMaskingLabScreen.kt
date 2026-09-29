package com.example.uiapp.ui.balancemasking

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private data class AccountWallet(
    val name: String,
    val type: String,
    val balance: String,
    val number: String,
    val emoji: String,
    val color: Color,
)

private val SAMPLE_WALLETS = listOf(
    AccountWallet("Rekening Utama Bisnis", "Giro Rupiah", "Rp 842.150.000", "5240-9912-441", "💼", Color(0xFF6366F1)),
    AccountWallet("Tabungan Darurat (High Yield)", "Deposito Fleksibel", "Rp 125.000.000", "7710-1829-002", "🛡️", Color(0xFF10B981)),
    AccountWallet("Portofolio Saham & ETF", "Reksadana Terproteksi", "Rp 340.670.500", "PORT-ID-9921", "📈", Color(0xFFF59E0B)),
    AccountWallet("E-Wallet Harian", "Digital Pocket", "Rp 2.450.000", "0812-9988-7711", "📱", Color(0xFFEC4899)),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BalanceMaskingLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()

    var isMasked by remember { mutableStateOf(false) }
    var shakeFeedback by remember { mutableStateOf<String?>(null) }
    val shakeOffset = remember { Animatable(0f) }

    fun triggerShakeGesture() {
        isMasked = true
        shakeFeedback = "Goyangan terdeteksi! Semua saldo otomatis disembunyikan."
        scope.launch {
            // Quick oscillation to simulate physical shake feedback
            repeat(3) {
                shakeOffset.animateTo(-16f, spring(stiffness = Spring.StiffnessHigh))
                shakeOffset.animateTo(16f, spring(stiffness = Spring.StiffnessHigh))
            }
            shakeOffset.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Balance Masking",
                subtitle = "Sensitive FinTech Privacy & Shake-to-Hide",
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
            // Master Toggle & Shake Simulation Action Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Mode Privasi Saldo FinTech", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                            Text(
                                text = if (isMasked) "Status: Tersembunyi (Masked)" else "Status: Terlihat (Visible)",
                                fontSize = 11.sp,
                                color = if (isMasked) palette.danger else palette.success,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }

                        // Eye Toggle Button
                        Surface(
                            onClick = { isMasked = !isMasked },
                            shape = CircleShape,
                            color = if (isMasked) palette.primaryContainer else palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.size(44.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(if (isMasked) "👁️" else "🙈", fontSize = 20.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            onClick = { triggerShakeGesture() },
                            shape = RoundedCornerShape(10.dp),
                            color = palette.primary,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                "Simulasi Goyang HP 📳",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                            )
                        }

                        Surface(
                            onClick = { isMasked = false },
                            shape = RoundedCornerShape(10.dp),
                            color = palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.border),
                        ) {
                            Text(
                                "Buka Semua",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp),
                            )
                        }
                    }

                    if (shakeFeedback != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(shakeFeedback!!, fontSize = 11.sp, color = palette.primary, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Wallet Accounts List
            Text("Dompet & Rekening Keuangan:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)

            SAMPLE_WALLETS.forEach { wallet ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(wallet.color.copy(alpha = 0.15f))
                                .border(1.dp, wallet.color, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(wallet.emoji, fontSize = 22.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(wallet.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = palette.textPrimary)
                            Text(
                                text = if (isMasked) "•••• •••• ••••" else wallet.number,
                                fontSize = 11.sp,
                                color = palette.textMuted,
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            AnimatedContent(
                                targetState = isMasked,
                                transitionSpec = { fadeIn(spring(stiffness = Spring.StiffnessHigh)) togetherWith fadeOut() },
                            ) { masked ->
                                Text(
                                    text = if (masked) "Rp ••••••••" else wallet.balance,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (masked) palette.textMuted else palette.textPrimary,
                                )
                            }
                        }
                    }
                }
            }

            // Architecture Standards
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Pola Desain Keamanan Saldo:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Shake to Hide: Memanfaatkan accelerometer untuk menyembunyikan saldo saat mendadak ada orang melirik", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Zero Shadow Flat UI: Tampilan bersih tanpa efek bayangan untuk performa rendering 60 FPS", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Animated Content Transition: Transisi mulus saat saldo ditutup atau dibuka tanpa layout jank", fontSize = 12.sp, color = palette.textSecondary)
                }
            }
        }
    }
}
