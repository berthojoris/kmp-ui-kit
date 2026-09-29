package com.example.uiapp.ui.accessibilitylab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.roundToInt

@Composable
fun AccessibilityLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    // Accessibility state modifiers
    var fontScale by remember { mutableStateOf(1.0f) } // 0.8f .. 1.6f
    var isHighContrast by remember { mutableStateOf(false) }
    var isReducedMotion by remember { mutableStateOf(false) }
    var isReducedTransparency by remember { mutableStateOf(false) }
    var isLargeTouchTarget by remember { mutableStateOf(false) }
    var showHitboxPreview by remember { mutableStateOf(false) }

    // Live demo component internal state
    var isBookmarked by remember { mutableStateOf(false) }
    var isNotificationAllowed by remember { mutableStateOf(true) }

    // Computed accessibility styling tokens
    val effectiveBorderColor = if (isHighContrast) palette.textPrimary else palette.border
    val effectiveBorderWidth = if (isHighContrast) 2.dp else 1.dp
    val effectiveSurfaceColor = if (isReducedTransparency) {
        if (isHighContrast) Color.White else palette.surface
    } else {
        palette.surface
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Accessibility Playground",
                subtitle = "Skala teks, kontras tinggi, & target sentuh",
                onBack = onBack,
                action = {
                    Text(
                        text = "Reset",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                        modifier = Modifier.clickable {
                            fontScale = 1.0f
                            isHighContrast = false
                            isReducedMotion = false
                            isReducedTransparency = false
                            isLargeTouchTarget = false
                            showHitboxPreview = false
                        },
                    )
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Control Panel: Accessibility Preference Sliders & Toggles
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Pengaturan Preferensi Aksesibilitas:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Font Scale Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Skala Teks (Font Scale):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary,
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(palette.surfaceMuted)
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "${(fontScale * 100).roundToInt()}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.primary,
                                )
                            }
                        }

                        Slider(
                            value = fontScale,
                            onValueChange = { fontScale = it },
                            valueRange = 0.8f..1.6f,
                            steps = 7,
                            colors = SliderDefaults.colors(
                                thumbColor = palette.primary,
                                activeTrackColor = palette.primary,
                                inactiveTrackColor = palette.surfaceMuted,
                            ),
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Toggle Rows
                        AccessibilityToggleRow(
                            title = "Kontras Tinggi (High Contrast)",
                            desc = "Pertebal batas 2px dan maksimalkan kontras teks",
                            checked = isHighContrast,
                            palette = palette,
                            onCheckedChange = { isHighContrast = it },
                        )

                        AccessibilityToggleRow(
                            title = "Kurangi Gerakan (Reduced Motion)",
                            desc = "Ganti animasi spring dengan perubahan instan",
                            checked = isReducedMotion,
                            palette = palette,
                            onCheckedChange = { isReducedMotion = it },
                        )

                        AccessibilityToggleRow(
                            title = "Kurangi Transparansi (Reduce Transparency)",
                            desc = "Hapus material tembus pandang menjadi warna solid pekat",
                            checked = isReducedTransparency,
                            palette = palette,
                            onCheckedChange = { isReducedTransparency = it },
                        )

                        AccessibilityToggleRow(
                            title = "Target Sentuh Besar (Large Touch Targets)",
                            desc = "Tingkatkan ukuran minimal tap target ke minimal 48dp",
                            checked = isLargeTouchTarget,
                            palette = palette,
                            onCheckedChange = { isLargeTouchTarget = it },
                        )

                        AccessibilityToggleRow(
                            title = "Sorot Area Sentuh (Hitbox Preview)",
                            desc = "Tampilkan garis bantu batas fisik interaksi sentuh",
                            checked = showHitboxPreview,
                            palette = palette,
                            onCheckedChange = { showHitboxPreview = it },
                        )
                    }
                }
            }

            // Live Preview Card Showing Preferences in Action
            item {
                Text(
                    text = "Pratinjau Komponen Nyata (Live Accessibility Impact):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = effectiveSurfaceColor,
                    border = BorderStroke(effectiveBorderWidth, effectiveBorderColor),
                    shadowElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header with Status Badge (Not color alone: includes icon and explicit label)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isHighContrast) palette.textPrimary else palette.primaryContainer)
                                    .border(effectiveBorderWidth, effectiveBorderColor, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("✓ ", fontSize = (11 * fontScale).sp, color = if (isHighContrast) palette.surface else palette.onPrimaryContainer)
                                    Text(
                                        text = "STATUS: TERVERIFIKASI",
                                        fontSize = (10 * fontScale).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isHighContrast) palette.surface else palette.onPrimaryContainer,
                                    )
                                }
                            }

                            Text(
                                text = "Kode: #ACC-902",
                                fontSize = (11 * fontScale).sp,
                                fontWeight = FontWeight.Medium,
                                color = palette.textMuted,
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Scaled Heading and Paragraph
                        Text(
                            text = "Standar Aksesibilitas WCAG AAA pada Compose Multiplatform",
                            fontSize = (17 * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                            lineHeight = (23 * fontScale).sp,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Setiap informasi visual penting wajib dilengkapi dengan teks keterangan eksplisit dan semantik yang dapat dibaca oleh pembaca layar (Screen Reader / TalkBack / VoiceOver). Menghindari penggunaan warna saja sebagai satu-satunya penanda status.",
                            fontSize = (13 * fontScale).sp,
                            color = palette.textSecondary,
                            lineHeight = (19 * fontScale).sp,
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Interactive Control Cluster: Large Touch Targets demonstration
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Bookmark Button
                            val touchHeight = if (isLargeTouchTarget) 54.dp else 38.dp
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = if (showHitboxPreview) 1.dp else 0.dp,
                                        color = if (showHitboxPreview) palette.danger else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp),
                                    ),
                            ) {
                                Surface(
                                    onClick = { isBookmarked = !isBookmarked },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isBookmarked) palette.primary else palette.surfaceMuted,
                                    border = BorderStroke(effectiveBorderWidth, effectiveBorderColor),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(touchHeight)
                                        .semantics {
                                            role = Role.Button
                                            contentDescription = if (isBookmarked) "Hapus dari penanda, tombol, aktif" else "Simpan ke penanda, tombol"
                                        },
                                    shadowElevation = 0.dp,
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize(),
                                    ) {
                                        Text(
                                            text = if (isBookmarked) "★ Tersimpan" else "☆ Simpan",
                                            fontSize = (12 * fontScale).sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isBookmarked) palette.onPrimary else palette.textPrimary,
                                        )
                                    }
                                }
                            }

                            // Notification Permission Switch Demo
                            Box(
                                modifier = Modifier
                                    .weight(1.3f)
                                    .border(
                                        width = if (showHitboxPreview) 1.dp else 0.dp,
                                        color = if (showHitboxPreview) palette.danger else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp),
                                    ),
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = palette.surfaceMuted,
                                    border = BorderStroke(effectiveBorderWidth, effectiveBorderColor),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(touchHeight)
                                        .clickable { isNotificationAllowed = !isNotificationAllowed }
                                        .semantics {
                                            role = Role.Switch
                                            contentDescription = "Notifikasi penting, sakelar, ${if (isNotificationAllowed) "aktif" else "nonaktif"}"
                                        },
                                    shadowElevation = 0.dp,
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = "Notifikasi:",
                                            fontSize = (11 * fontScale).sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = palette.textPrimary,
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (isNotificationAllowed) palette.primary else palette.border)
                                                .padding(horizontal = 6.dp, vertical = 2.dp),
                                        ) {
                                            Text(
                                                text = if (isNotificationAllowed) "ON" else "OFF",
                                                fontSize = (10 * fontScale).sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isNotificationAllowed) palette.onPrimary else palette.textMuted,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (showHitboxPreview) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Garis merah menunjukkan batas fisik target sentuh (Hitbox Touch Target).",
                                fontSize = 10.sp,
                                color = palette.danger,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }

            // Semantics & Screen Reader Feedback Inspector
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = palette.surfaceMuted,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Inspeksi Semantik Pembaca Layar (TalkBack):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textMuted,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Tombol Simpan: \"${if (isBookmarked) "Hapus dari penanda, tombol, aktif" else "Simpan ke penanda, tombol"}\"",
                            fontSize = 11.sp,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = "• Sakelar Notifikasi: \"Notifikasi penting, sakelar, ${if (isNotificationAllowed) "aktif" else "nonaktif"}\"",
                            fontSize = 11.sp,
                            color = palette.textPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccessibilityToggleRow(
    title: String,
    desc: String,
    checked: Boolean,
    palette: com.example.uiapp.theme.AppPalette,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
            )
            Text(
                text = desc,
                fontSize = 10.sp,
                color = palette.textMuted,
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (checked) palette.primary else palette.surfaceMuted)
                .border(1.dp, if (checked) palette.primary else palette.border, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text = if (checked) "AKTIF" else "NONAKTIF",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (checked) palette.onPrimary else palette.textMuted,
            )
        }
    }
}
