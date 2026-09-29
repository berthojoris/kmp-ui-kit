package com.example.uiapp.ui.privacymasking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PrivacyMaskingLabScreen(onBack: () -> Unit) {
    var isAppSwitcherSimulated by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        if (isAppSwitcherSimulated) {
            isAppSwitcherSimulated = false
        } else {
            onBack()
        }
    }

    val palette = LocalAppPalette.current
    var autoPrivacyEnabled by remember { mutableStateOf(true) }
    var blurSensitiveCards by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = palette.background,
            topBar = {
                UiTopBar(
                    title = "App Switcher Privacy",
                    subtitle = "Sensitive Screen Blur & Privacy Shield",
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
                // Controls Panel
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Konfigurasi Keamanan Privasi", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Otomatis Masking saat Multitask", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = palette.textPrimary)
                                Text("Melindungi tampilan saat berpindah ke app switcher", fontSize = 11.sp, color = palette.textMuted)
                            }
                            Switch(
                                checked = autoPrivacyEnabled,
                                onCheckedChange = { autoPrivacyEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = palette.primary, checkedTrackColor = palette.primaryContainer),
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Blur Kartu Rekening Sensitif", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = palette.textPrimary)
                                Text("Berikan efek blur langsung di layar utama", fontSize = 11.sp, color = palette.textMuted)
                            }
                            Switch(
                                checked = blurSensitiveCards,
                                onCheckedChange = { blurSensitiveCards = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = palette.primary, checkedTrackColor = palette.primaryContainer),
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            onClick = { isAppSwitcherSimulated = true },
                            shape = RoundedCornerShape(10.dp),
                            color = palette.primary,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = "Simulasi Masuk App Switcher / Recent Apps 🔒",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                            )
                        }
                    }
                }

                // Sensitive Bank Account Card (Can be blurred)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (blurSensitiveCards) Modifier.blur(12.dp) else Modifier),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Platinum Debit Priority", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                            Text("VISA", fontWeight = FontWeight.Black, fontSize = 16.sp, color = palette.primary)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Saldo Rekening Utama", fontSize = 11.sp, color = palette.textMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Rp 184.520.000", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = palette.textPrimary)

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("4532 •••• •••• 9821", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = palette.textSecondary)
                            Text("EXP 08/29", fontSize = 11.sp, color = palette.textMuted)
                        }
                    }
                }

                // Recent Confidential Transactions
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Mutasi Sensitif:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)

                        listOf(
                            Triple("Dividen Saham Portofolio", "+Rp 12.450.000", "26 Sep 2026"),
                            Triple("Payroll Gaji Karyawan", "-Rp 45.000.000", "25 Sep 2026"),
                            Triple("Setoran Pajak Perusahaan", "-Rp 8.750.000", "22 Sep 2026"),
                        ).forEach { (title, amt, date) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = palette.textPrimary)
                                    Text(date, fontSize = 10.sp, color = palette.textMuted)
                                }
                                Text(
                                    amt,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (amt.startsWith("+")) palette.success else palette.textPrimary,
                                )
                            }
                        }
                    }
                }

                // Specs Explanation
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Standar Keamanan Layar Finansial:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• FLAG_SECURE (Android): Mencegah screenshot & snapshot di task switcher", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Privacy Shield (iOS): Menempatkan view penutup saat sceneDidEnterBackground", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Zero Shadow: Desain flat murni dengan border tegas untuk konsistensi visual", fontSize = 12.sp, color = palette.textSecondary)
                    }
                }
            }
        }

        // Fullscreen Privacy Masking Shield (App Switcher Overlay)
        androidx.compose.animation.AnimatedVisibility(
            visible = isAppSwitcherSimulated,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(palette.background.copy(alpha = 0.96f))
                    .clickable { isAppSwitcherSimulated = false },
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.padding(32.dp).fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(palette.primaryContainer)
                                .border(1.dp, palette.primary, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("🛡️", fontSize = 32.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Layar Dilindungi", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Konten perbankan disembunyikan demi menjaga kerahasiaan data finansial saat multitasking.",
                            fontSize = 12.sp,
                            color = palette.textMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        Surface(
                            onClick = { isAppSwitcherSimulated = false },
                            shape = RoundedCornerShape(10.dp),
                            color = palette.primary,
                        ) {
                            Text(
                                "Buka Kunci Tampilan",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
