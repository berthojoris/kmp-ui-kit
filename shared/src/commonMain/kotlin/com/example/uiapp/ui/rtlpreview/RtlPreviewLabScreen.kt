package com.example.uiapp.ui.rtlpreview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
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

private val LongSamples = listOf(
    "Tombol simpan perubahan sekarang",
    "Lihat riwayat transaksi bulan ini",
    "Kata sandi harus minimal 8 karakter",
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RtlPreviewLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var rtl by remember { mutableStateOf(false) }
    var pseudo by remember { mutableStateOf(false) }
    val dir = if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides dir) {
        Column(modifier = Modifier.fillMaxSize().background(palette.background)) {
            UiTopBar(title = "Pratinjau RTL", onBack = onBack, subtitle = "Simulasi")
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                LabIntroCard(title = "Mirror tata letak", body = "Toggle LTR/RTL via CompositionLocalProvider.")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { rtl = !rtl; PerformHapticFeedback(HapticKind.SELECTION) }) { Text(if (rtl) "Mode RTL" else "Mode LTR") }
                    Button(onClick = { pseudo = !pseudo }) { Text(if (pseudo) "Pseudo panjang" else "Pseudo normal") }
                }
                StatusPill(text = if (rtl) "RTL aktif" else "LTR aktif", color = if (rtl) palette.info else palette.textMuted)
                LabCard {
                    LabSectionTitle("Contoh String", "Tabel singkat")
                    LongSamples.forEach { s ->
                        val txt = if (pseudo) s + " (" + "x".repeat(24) + ")" else s
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(palette.surfaceMuted, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(txt, color = palette.textPrimary, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                            Text("ID", color = palette.textMuted, fontSize = 10.sp)
                        }
                    }
                }
                LabCard {
                    LabSectionTitle("Penanda Meluap", "Label panjang")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f).background(palette.primary, RoundedCornerShape(8.dp)).padding(8.dp)) {
                            Text("Simpan Perubahan Sekarang Juga", color = palette.onPrimary, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        }
                        Box(modifier = Modifier.weight(1f).background(palette.surfaceMuted, RoundedCornerShape(8.dp)).padding(8.dp)) {
                            Text("Batal", color = palette.textPrimary)
                        }
                    }
                }
                LabCard {
                    LabSectionTitle("Pseudo-locale", "Stres panjang")
                    Text(if (pseudo) "Dies_ist_ein_sehr_langer_pseudo_locale_Text_zur_Prüfung_von_Truncation_und_Wrapping_in_allen_Komponenten" else "Teks normal pendek.", color = palette.textSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}