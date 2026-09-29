package com.example.uiapp.ui.settings

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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

private enum class SettingsSubTab(val title: String) {
    GENERAL("Umum"),
    LANGUAGE("Bahasa"),
    NOTIFICATIONS("Notifikasi"),
    PAYWALL("Langganan"),
    ABOUT("Tentang & FAQ"),
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var activeSubTab by remember { mutableStateOf(SettingsSubTab.GENERAL) }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Pengaturan & Akun",
                subtitle = "Grouped List, Bahasa, Notifikasi, Paywall",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface)
                    .border(BorderStroke(1.dp, palette.border))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SettingsSubTab.entries.forEach { tab ->
                    val isSelected = activeSubTab == tab
                    Surface(
                        onClick = { activeSubTab = tab },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) palette.primary else palette.surfaceMuted,
                        border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                    ) {
                        Text(
                            text = tab.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else palette.textPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (activeSubTab) {
                    SettingsSubTab.GENERAL -> GeneralSettingsView(palette)
                    SettingsSubTab.LANGUAGE -> LanguagePickerView(palette)
                    SettingsSubTab.NOTIFICATIONS -> NotificationMatrixView(palette)
                    SettingsSubTab.PAYWALL -> SubscriptionPaywallView(palette)
                    SettingsSubTab.ABOUT -> AboutFaqView(palette)
                }
            }
        }
    }
}

@Composable
private fun GeneralSettingsView(palette: com.example.uiapp.theme.AppPalette) {
    var biometricEnabled by remember { mutableStateOf(true) }
    var offlineSync by remember { mutableStateOf(true) }
    var analyticSharing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Group 1: Keamanan & Akun
        Text("KEAMANAN & AUTENTIKASI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.textMuted)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                SettingsSwitchRow(
                    title = "Kunci Biometrik / Face ID",
                    subtitle = "Gunakan sensor bawaan untuk autentikasi",
                    checked = biometricEnabled,
                    onCheckedChange = { biometricEnabled = it },
                    palette = palette,
                )
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                SettingsLinkRow(title = "Ubah Passcode 6-Digit", value = "Telah Disetel", palette = palette)
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                SettingsLinkRow(title = "Autentikasi Dua Faktor (2FA)", value = "Aktif", palette = palette)
            }
        }

        // Group 2: Data & Jaringan
        Text("DATA & PENYIMPANAN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.textMuted)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                SettingsSwitchRow(
                    title = "Sinkronisasi Offline",
                    subtitle = "Simpan cache data di memori lokal",
                    checked = offlineSync,
                    onCheckedChange = { offlineSync = it },
                    palette = palette,
                )
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                SettingsSwitchRow(
                    title = "Bagi Data Analitik Anonim",
                    subtitle = "Membantu peningkatan performa aplikasi",
                    checked = analyticSharing,
                    onCheckedChange = { analyticSharing = it },
                    palette = palette,
                )
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                SettingsLinkRow(title = "Bersihkan Cache Memori", value = "14.2 MB", palette = palette)
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    palette: com.example.uiapp.theme.AppPalette,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = palette.textMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = palette.primary,
                uncheckedThumbColor = palette.textMuted,
                uncheckedTrackColor = palette.surfaceMuted,
            ),
        )
    }
}

@Composable
private fun SettingsLinkRow(
    title: String,
    value: String,
    palette: com.example.uiapp.theme.AppPalette,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = palette.textPrimary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, fontSize = 12.sp, color = palette.textMuted)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "›", fontSize = 16.sp, color = palette.textMuted)
        }
    }
}

@Composable
private fun LanguagePickerView(palette: com.example.uiapp.theme.AppPalette) {
    var selectedLanguage by remember { mutableStateOf("Bahasa Indonesia (ID)") }
    val languages = listOf(
        "Bahasa Indonesia (ID)",
        "English (US)",
        "English (UK)",
        "日本語 - Japanese (JA)",
        "Français - French (FR)",
        "Deutsch - German (DE)",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Bahasa & Wilayah", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
        Text("Pilih bahasa tampilan untuk aplikasi", fontSize = 12.sp, color = palette.textMuted)

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                languages.forEachIndexed { index, lang ->
                    val isSelected = selectedLanguage == lang
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedLanguage = lang }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = lang,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) palette.primary else palette.textPrimary,
                        )
                        if (isSelected) {
                            Text("✓", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                        }
                    }
                    if (index < languages.size - 1) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationMatrixView(palette: com.example.uiapp.theme.AppPalette) {
    val matrix = remember {
        mutableStateMapOf(
            "push_transaksi" to true,
            "push_promo" to false,
            "email_transaksi" to true,
            "email_promo" to true,
            "sms_keamanan" to true,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Matriks Preferensi Notifikasi", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
        Text("Atur kanal saluran pesan secara granular", fontSize = 12.sp, color = palette.textMuted)

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                val items = listOf(
                    "push_transaksi" to ("Push Notifikasi Transaksi" to "Notifikasi langsung status transaksi"),
                    "push_promo" to ("Push Notifikasi Promosi" to "Diskon & voucher bulanan"),
                    "email_transaksi" to ("Email Faktur / Kwitansi" to "Pengiriman tanda terima ke email"),
                    "email_promo" to ("Email Newsletter Mingguan" to "Rangkuman tren desain"),
                    "sms_keamanan" to ("SMS Kode Verifikasi OTP" to "Wajib aktif demi perlindungan akun"),
                )

                items.forEachIndexed { index, (key, info) ->
                    val (title, desc) = info
                    val isChecked = matrix[key] ?: false
                    SettingsSwitchRow(
                        title = title,
                        subtitle = desc,
                        checked = isChecked,
                        onCheckedChange = { matrix[key] = it },
                        palette = palette,
                    )
                    if (index < items.size - 1) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscriptionPaywallView(palette: com.example.uiapp.theme.AppPalette) {
    var selectedPlan by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.primaryContainer,
            border = BorderStroke(1.dp, palette.primary),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("★ PRO MEMBERSHIP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Akses Seluruh Fitur Tanpa Batas", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = palette.onPrimaryContainer)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Ekspor kode tanpa watermark, cloud backup, dan prioritas dukungan 24/7.", fontSize = 12.sp, color = palette.onPrimaryContainer.copy(alpha = 0.8f))
            }
        }

        // Plan selectors
        val plans = listOf(
            Triple("1 Bulan Pro", "Rp 89.000 / bln", "Ditagih bulanan"),
            Triple("12 Bulan Pro", "Rp 59.000 / bln", "Hemat 35% - Bayar Rp 708.000/thn"),
        )

        plans.forEachIndexed { index, (name, price, sub) ->
            val isSelected = selectedPlan == index
            Surface(
                onClick = { selectedPlan = index },
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(2.dp, if (isSelected) palette.primary else palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                        Text(sub, fontSize = 11.sp, color = palette.textMuted)
                    }
                    Text(price, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.primary)
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = palette.primary,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(modifier = Modifier.padding(14.dp), contentAlignment = Alignment.Center) {
                Text("Lanjutkan Berlangganan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun AboutFaqView(palette: com.example.uiapp.theme.AppPalette) {
    var expandedFaq by remember { mutableStateOf<Int?>(null) }
    val faqs = listOf(
        "Apakah aplikasi ini 100% mendukung iOS & Android?" to "Ya, menggunakan Compose Multiplatform 1.12 dan Kotlin 2.4.",
        "Mengapa menggunakan pola Flat UI Zero-Shadow?" to "Demi menjamin performa rendering 60fps yang ringan di HP 2GB-3GB tanpa beban shadow GPU rendering.",
        "Bagaimana cara mengadopsi dark mode?" to "Buka Theme & Dark Mode Lab, pilih mode Gelap, dan seluruh token warna akan otomatis beradaptasi.",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Tentang Aplikasi & Legal", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Testing UI Component Kit v1.0", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Dibuat menggunakan Kotlin Multiplatform & Jetpack Compose Multiplatform.", fontSize = 12.sp, color = palette.textSecondary)
            }
        }

        Text("PERTANYAAN UMUM (FAQ)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.textMuted)

        faqs.forEachIndexed { index, (q, a) ->
            val isExpanded = expandedFaq == index
            Surface(
                onClick = { expandedFaq = if (isExpanded) null else index },
                shape = RoundedCornerShape(12.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(q, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = palette.textPrimary, modifier = Modifier.weight(1f))
                        Text(if (isExpanded) "▲" else "▼", fontSize = 11.sp, color = palette.primary)
                    }
                    AnimatedVisibility(visible = isExpanded) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(a, fontSize = 11.sp, color = palette.textSecondary)
                        }
                    }
                }
            }
        }
    }
}
