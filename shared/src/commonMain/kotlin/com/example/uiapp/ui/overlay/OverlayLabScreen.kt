package com.example.uiapp.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OverlayLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var showActionSheet by remember { mutableStateOf(false) }
    var showSpotlight by remember { mutableStateOf(false) }
    var activeTooltip by remember { mutableStateOf<String?>("Atas") }
    var actionSheetFeedback by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = palette.background,
            topBar = {
                UiTopBar(
                    title = "Overlay & Feedback Lanjutan",
                    subtitle = "Action Sheet, Spotlight, Tooltip",
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
                // Section 1: iOS Action Sheet
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("1. iOS Cupertino-Style Action Sheet", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                        Text("Menu tindakan melayang dari bawah dengan tombol destruktif", fontSize = 12.sp, color = palette.textMuted)
                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            onClick = { showActionSheet = true },
                            shape = RoundedCornerShape(10.dp),
                            color = palette.primary,
                        ) {
                            Text(
                                "Tampilkan Action Sheet",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            )
                        }

                        if (actionSheetFeedback != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(actionSheetFeedback ?: "", fontSize = 12.sp, color = palette.primary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Section 2: Coach Marks / Spotlight Onboarding
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("2. Spotlight & Coach Marks Onboarding", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                        Text("Menyorot elemen penting dengan lubang fokus dan balon panduan", fontSize = 12.sp, color = palette.textMuted)
                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            onClick = { showSpotlight = true },
                            shape = RoundedCornerShape(10.dp),
                            color = palette.primaryContainer,
                            border = BorderStroke(1.dp, palette.primary),
                        ) {
                            Text(
                                "Aktifkan Mode Spotlight",
                                color = palette.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            )
                        }
                    }
                }

                // Section 3: Directional Tooltips & Popovers
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("3. Directional Tooltips & Popovers", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                        Text("Balon petunjuk arah (Atas, Bawah, Kiri, Kanan)", fontSize = 12.sp, color = palette.textMuted)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Atas", "Bawah", "Kiri", "Kanan").forEach { direction ->
                                val isSelected = activeTooltip == direction
                                Surface(
                                    onClick = { activeTooltip = direction },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) palette.primary else palette.surfaceMuted,
                                    border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                                ) {
                                    Text(
                                        text = direction,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isSelected) Color.White else palette.textPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))

                        // Tooltip Render Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.surfaceMuted)
                                .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = palette.primary,
                                shadowElevation = 0.dp,
                            ) {
                                Text(
                                    text = "Tooltip Arah: $activeTooltip \u00B7 Flat UI Zero-Shadow",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        // Overlay 1: iOS Action Sheet Modal
        AnimatedVisibility(
            visible = showActionSheet,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showActionSheet = false },
                contentAlignment = Alignment.BottomCenter,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Action items capsule
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("Pilih Tindakan", fontSize = 12.sp, color = palette.textMuted, fontWeight = FontWeight.SemiBold)
                            }
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        actionSheetFeedback = "Tindakan: 'Simpan ke Bookmark' Berhasil"
                                        showActionSheet = false
                                    }
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("Simpan ke Bookmark", fontSize = 14.sp, color = palette.primary, fontWeight = FontWeight.SemiBold)
                            }
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        actionSheetFeedback = "Tindakan: 'Bagikan Link' Berhasil"
                                        showActionSheet = false
                                    }
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("Bagikan Konten", fontSize = 14.sp, color = palette.primary, fontWeight = FontWeight.SemiBold)
                            }
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        actionSheetFeedback = "Tindakan: 'Hapus Item (Destruktif)' Diproses"
                                        showActionSheet = false
                                    }
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("Hapus Permanen", fontSize = 14.sp, color = palette.danger, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Cancel button capsule
                    Surface(
                        onClick = { showActionSheet = false },
                        shape = RoundedCornerShape(14.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("Batal", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        }
                    }
                }
            }
        }

        // Overlay 2: Spotlight / Coach Marks Onboarding
        AnimatedVisibility(
            visible = showSpotlight,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .clickable { showSpotlight = false },
            ) {
                // Focus Hole
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .border(BorderStroke(3.dp, palette.primary), CircleShape),
                )

                // Tooltip Card beside the hole
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 180.dp)
                        .padding(horizontal = 24.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Coach Mark: Fitur Penting", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Ketuk area ini kapan saja untuk mengakses kontrol cepat. Sentuh di mana saja untuk menutup panduan.",
                            fontSize = 12.sp,
                            color = palette.textSecondary,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            onClick = { showSpotlight = false },
                            shape = RoundedCornerShape(8.dp),
                            color = palette.primary,
                        ) {
                            Text("Saya Mengerti", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                        }
                    }
                }
            }
        }
    }
}
