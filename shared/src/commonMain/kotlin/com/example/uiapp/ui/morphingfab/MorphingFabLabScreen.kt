package com.example.uiapp.ui.morphingfab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private data class QuickActionItem(
    val title: String,
    val subtitle: String,
    val emoji: String,
    val badgeColor: Color,
)

private val ACTION_ITEMS = listOf(
    QuickActionItem("Kirim Uang", "Transfer instan ke bank & e-wallet", "💸", Color(0xFF10B981)),
    QuickActionItem("Pindai QRIS", "Bayar merchant via kamera", "📷", Color(0xFF6366F1)),
    QuickActionItem("Minta Dana", "Bagikan tautan penagihan cepat", "📥", Color(0xFFF59E0B)),
    QuickActionItem("Beli Investasi", "Reksa dana & emas otomatis", "📈", Color(0xFFEC4899)),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MorphingFabLabScreen(onBack: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        if (isExpanded) {
            isExpanded = false
        } else {
            onBack()
        }
    }

    val palette = LocalAppPalette.current
    var lastActionClicked by remember { mutableStateOf<String?>(null) }

    // Morphing Dimension Animations
    val fabWidth by animateDpAsState(
        targetValue = if (isExpanded) 340.dp else 56.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
    )
    val fabHeight by animateDpAsState(
        targetValue = if (isExpanded) 320.dp else 56.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
    )
    val cornerRadius by animateDpAsState(
        targetValue = if (isExpanded) 20.dp else 28.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
    )
    val iconRotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = palette.background,
            topBar = {
                UiTopBar(
                    title = "Morphing FAB",
                    subtitle = "Dynamic Container Transform Sheet",
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
                // Intro Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Morphing Floating Action Button", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Ketuk tombol FAB di pojok kanan bawah untuk melihat transformasi fluida menjadi panel menu tindakan tanpa jeda.",
                            fontSize = 12.sp,
                            color = palette.textSecondary,
                            lineHeight = 17.sp,
                        )

                        if (lastActionClicked != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = palette.primaryContainer,
                                border = BorderStroke(1.dp, palette.primary),
                            ) {
                                Text(
                                    text = "Aksi dipilih: $lastActionClicked",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.primary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                )
                            }
                        }
                    }
                }

                // Sample Dummy Activity Items
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Aktivitas Terbaru:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)

                        listOf(
                            Triple("Kopi Kenangan", "-Rp 38.000", "Pembayaran QRIS Berhasil"),
                            Triple("Top Up Saldo", "+Rp 500.000", "Transfer Virtual Account"),
                            Triple("Tagihan Internet", "-Rp 399.000", "Auto-debit Bulanan"),
                        ).forEach { (title, amt, desc) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = palette.textPrimary)
                                    Text(desc, fontSize = 11.sp, color = palette.textMuted)
                                }
                                Text(
                                    amt,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (amt.startsWith("+")) palette.success else palette.textPrimary,
                                )
                            }
                        }
                    }
                }

                // Design Philosophy Specs
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Karakteristik Container Transform:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Seamless Continuity: Satu elemen kontainer bermutasi ukuran dan radius", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Zero Shadow: Bingkai 1px tegas selaras panduan Flat UI", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Responsive Dismissal: Mengetuk backdrop atau tombol X mengembalikan FAB", fontSize = 12.sp, color = palette.textSecondary)
                    }
                }
            }
        }

        // Dimmer Backdrop when expanded
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        isExpanded = false
                    },
            )
        }

        // The Morphing FAB / Container
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(cornerRadius),
                color = if (isExpanded) palette.surface else palette.primary,
                border = BorderStroke(1.dp, if (isExpanded) palette.border else palette.primary),
                modifier = Modifier
                    .size(width = fabWidth, height = fabHeight),
            ) {
                if (!isExpanded) {
                    // Collapsed FAB Circle
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { isExpanded = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "+",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Light,
                            color = Color.White,
                            modifier = Modifier.rotate(iconRotation),
                        )
                    }
                } else {
                    // Expanded Menu Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    ) {
                        // Header with Title & Close Icon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "Tindakan Cepat",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = palette.textPrimary,
                            )

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(palette.surfaceMuted)
                                    .clickable { isExpanded = false },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    "✕",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textSecondary,
                                    modifier = Modifier.rotate(iconRotation - 45f),
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action List
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            ACTION_ITEMS.forEach { item ->
                                Surface(
                                    onClick = {
                                        lastActionClicked = item.title
                                        isExpanded = false
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = palette.surfaceMuted,
                                    border = BorderStroke(1.dp, palette.border),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(item.badgeColor.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(item.emoji, fontSize = 16.sp)
                                        }

                                        Column {
                                            Text(
                                                item.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = palette.textPrimary,
                                            )
                                            Text(
                                                item.subtitle,
                                                fontSize = 10.sp,
                                                color = palette.textMuted,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
