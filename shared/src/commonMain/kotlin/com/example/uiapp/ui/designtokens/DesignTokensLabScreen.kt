package com.example.uiapp.ui.designtokens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.HapticKind
import com.example.uiapp.theme.DarkAppPalette
import com.example.uiapp.theme.LightAppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.pow
import kotlin.math.roundToInt

private fun fmt2(value: Float): String = (value * 100).roundToInt().let { (it / 100).toString() }

private data class TokenState(
    val primary: Color,
    val radiusDp: Float,
    val spacingScale: Float,
)

// Swatch diambil dari field palet yang sudah ada — tidak ada warna hex baru.
private val Swatches = listOf(
    LightAppPalette.primary,
    LightAppPalette.success,
    LightAppPalette.info,
    DarkAppPalette.primary,
    DarkAppPalette.success,
    DarkAppPalette.info,
    DarkAppPalette.warning,
    LightAppPalette.danger,
)

private fun channel(c: Float): Float =
    if (c <= 0.03928f) c / 12.92f else ((c + 0.055f) / 1.055f).pow(2.4f)

/** Relative luminance WCAG (0..1) dari warna sRGB. */
private fun luminance(color: Color): Float =
    0.2126f * channel(color.red) +
        0.7152f * channel(color.green) +
        0.0722f * channel(color.blue)

/** Rasio kontras WCAG; 1..21. */
private fun contrastRatio(a: Color, b: Color): Float {
    val la = luminance(a)
    val lb = luminance(b)
    val lighter = maxOf(la, lb)
    val darker = minOf(la, lb)
    return (lighter + 0.05f) / (darker + 0.05f)
}

private fun passLabel(ratio: Float): String = when {
    ratio >= 7f -> "Lolos AAA"
    ratio >= 4.5f -> "Lolos AA"
    ratio >= 3f -> "Hanya AA teks besar"
    else -> "Gagal AA"
}

private fun passColor(ratio: Float, ok: Color, warn: Color, bad: Color): Color = when {
    ratio >= 4.5f -> ok
    ratio >= 3f -> warn
    else -> bad
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DesignTokensLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    var tokens by remember {
        mutableStateOf(TokenState(LightAppPalette.primary, radiusDp = 16f, spacingScale = 1f))
    }
    val modified = tokens.primary != LightAppPalette.primary ||
        tokens.radiusDp != 16f ||
        tokens.spacingScale != 1f

    // Kandidat warna teks di atas token primary; pilih rasio tertinggi.
    val onToken = listOf(LightAppPalette.surface, LightAppPalette.textPrimary)
        .maxBy { contrastRatio(tokens.primary, it) }
    val ratio = contrastRatio(tokens.primary, onToken)
    val ratioSurface = contrastRatio(tokens.primary, LightAppPalette.surface)

    BackHandler(enabled = true) { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background),
    ) {
        UiTopBar(
            title = "Design Token Editor",
            subtitle = "Editor token demo lokal — tema global tidak diubah",
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LabIntroCard(
                title = "Editor terbatas di layar demo",
                body = "Semua perubahan token hanya disimpan pada state lokal layar ini. " +
                    "Tombol \u201cTerapkan global\u201d sengaja dinonaktifkan karena sistem tema aplikasi tidak boleh diubah dari lab.",
            )

            LabSectionTitle("Token warna primer", "Pilih swatch, kontras dihitung dengan rumus WCAG")

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Swatches.forEach { sw ->
                    val selected = tokens.primary == sw
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(sw, CircleShape)
                            .border(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) palette.textPrimary else palette.border,
                                shape = CircleShape,
                            )
                            .clickable {
                                PerformHapticFeedback(HapticKind.SELECTION)
                                tokens = tokens.copy(primary = sw)
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (selected) {
                            Box(
                                Modifier
                                    .size(8.dp)
                                    .background(onToken, CircleShape),
                            )
                        }
                    }
                }
            }

            ContrastRow(
                label = "primary vs teks terpilih",
                ratio = ratio,
            )
            ContrastRow(
                label = "primary vs permukaan terang",
                ratio = ratioSurface,
            )

            LabSectionTitle("Token bentuk & jarak", "Radius sudut dan skala jarak")

            LabCard {
                Text("Radius sudut: ${tokens.radiusDp.toInt()} dp", fontSize = 12.sp, color = palette.textSecondary)
                Slider(
                    value = tokens.radiusDp,
                    onValueChange = { tokens = tokens.copy(radiusDp = it) },
                    valueRange = 8f..24f,
                )
                Text(
                    "Skala jarak: ${fmt2(tokens.spacingScale)}×",
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                )
                Slider(
                    value = tokens.spacingScale,
                    onValueChange = { tokens = tokens.copy(spacingScale = it) },
                    valueRange = 0.75f..1.5f,
                )
            }

            LabSectionTitle("Pratinjau komponen", "Kartu, tombol, dan pill memakai token aktif")

            TokenPreviewCard(tokens = tokens, onToken = onToken, ratio = ratio)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .background(
                            if (modified) palette.primary else palette.surfaceMuted,
                            RoundedCornerShape(10.dp),
                        )
                        .border(
                            1.dp,
                            if (modified) palette.primary else palette.border,
                            RoundedCornerShape(10.dp),
                        )
                        .clickable(enabled = modified) {
                            PerformHapticFeedback(HapticKind.MEDIUM)
                            tokens = TokenState(LightAppPalette.primary, 16f, 1f)
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(
                        if (modified) "Reset token" else "Token bawaan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (modified) palette.onPrimary else palette.textMuted,
                    )
                }
                Box(
                    modifier = Modifier
                        .background(palette.surfaceMuted, RoundedCornerShape(10.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(
                        "Terapkan global (dinonaktifkan)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (modified) {
                StatusPill("Token dimodifikasi — hanya berlaku di layar ini", palette.info)
            } else {
                StatusPill("Token bawaan", palette.textMuted)
            }
        }
    }
}

@Composable
private fun ContrastRow(label: String, ratio: Float) {
    val palette = LocalAppPalette.current
    val color = passColor(ratio, palette.success, palette.warning, palette.danger)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(label, fontSize = 12.sp, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Rasio ${fmt2(ratio)}:1", fontSize = 11.sp, color = palette.textSecondary)
        }
        StatusPill(passLabel(ratio), color)
    }
}

@Composable
private fun TokenPreviewCard(tokens: TokenState, onToken: Color, ratio: Float) {
    val palette = LocalAppPalette.current
    val radius = RoundedCornerShape(tokens.radiusDp.dp)
    val pad = (14 * tokens.spacingScale).dp
    val gap = (10 * tokens.spacingScale).dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.surface, radius)
            .border(1.dp, palette.border, radius)
            .padding(pad),
        verticalArrangement = Arrangement.spacedBy(gap),
    ) {
        Text(
            "Kartu pratinjau",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textPrimary,
        )
        Text(
            "Seluruh elemen di kartu ini mengikuti token radius dan jarak yang sedang Anda pilih.",
            fontSize = 11.sp,
            color = palette.textSecondary,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(gap), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .background(tokens.primary, RoundedCornerShape((tokens.radiusDp * 0.6f).dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(
                    "Tombol utama",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onToken,
                )
            }
            Box(
                modifier = Modifier
                    .background(tokens.primary.copy(alpha = 0.15f), RoundedCornerShape(999.dp))
                    .border(1.dp, tokens.primary, RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text("Pill token", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = tokens.primary)
            }
        }
        Text(
            "Kontras tombol: ${fmt2(ratio)}:1 — ${passLabel(ratio)}",
            fontSize = 11.sp,
            color = palette.textSecondary,
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.border),
        )
        Text(
            "Border 1px tetap dipakai di seluruh kartu (aturan Flat UI).",
            fontSize = 10.sp,
            color = palette.textMuted,
        )
    }
}
