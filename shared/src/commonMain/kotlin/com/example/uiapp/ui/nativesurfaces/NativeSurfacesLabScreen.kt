package com.example.uiapp.ui.nativesurfaces

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

enum class SurfaceMaterialStyle(val title: String, val desc: String) {
    TRANSLUCENT("Translucent Frost", "Permukaan semi-transparan dengan border 1px"),
    SOLID("Solid Flat UI", "Permukaan pekat tanpa tembus pandang"),
    HIGH_CONTRAST("High Contrast", "Batas tegas dan kontras tinggi untuk aksesibilitas"),
}

enum class BackgroundType(val title: String) {
    VIBRANT("Gradasi Vibrant"),
    CONTENT("Artikel Teks"),
    MINIMAL("Solid Minimalis"),
}

@Composable
fun NativeSurfacesLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current
    val listState = rememberLazyListState()

    var materialStyle by remember { mutableStateOf(SurfaceMaterialStyle.TRANSLUCENT) }
    var bgType by remember { mutableStateOf(BackgroundType.VIBRANT) }
    var reduceTransparency by remember { mutableStateOf(false) }
    var activeDockTab by remember { mutableStateOf(0) }

    // Scroll state detection to shrink floating toolbar
    val isScrolled by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 40 }
    }

    val floatingBarPadding by animateDpAsState(
        targetValue = if (isScrolled) 8.dp else 16.dp,
        animationSpec = tween(250),
    )

    // Effective styling tokens
    val isEffectiveSolid = reduceTransparency || materialStyle == SurfaceMaterialStyle.SOLID
    val isEffectiveHighContrast = materialStyle == SurfaceMaterialStyle.HIGH_CONTRAST

    val surfaceBackground = when {
        isEffectiveHighContrast -> palette.surface
        isEffectiveSolid -> palette.surface
        else -> palette.surface.copy(alpha = 0.85f)
    }

    val surfaceBorderColor = when {
        isEffectiveHighContrast -> palette.textPrimary
        else -> palette.border
    }

    val surfaceBorderWidth = if (isEffectiveHighContrast) 2.dp else 1.dp

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Native Material Surfaces",
                subtitle = "Kontrol mengambang & edge-to-edge flow",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Background Layer
            when (bgType) {
                BackgroundType.VIBRANT -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF6366F1).copy(alpha = 0.35f),
                                        Color(0xFFEC4899).copy(alpha = 0.25f),
                                        Color(0xFF3B82F6).copy(alpha = 0.2f),
                                        palette.background,
                                    )
                                )
                            ),
                    )
                }

                BackgroundType.CONTENT -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(palette.surfaceMuted),
                    )
                }

                BackgroundType.MINIMAL -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(palette.background),
                    )
                }
            }

            // Scrollable Content Flowing Underneath Floating Controls
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 130.dp, bottom = 100.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    // Explainer Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = surfaceBackground,
                        border = BorderStroke(surfaceBorderWidth, surfaceBorderColor),
                        shadowElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Prinsip Native Material Surfaces",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Permukaan mengambang (Floating Dock & App Bar) dirancang transparan lembut agar konten dapat mengalir di bawahnya (edge-to-edge flow) tanpa terputus. Garis batas 1px menjamin pemisahan visual yang tajam tanpa bayangan kabur.",
                                fontSize = 13.sp,
                                color = palette.textSecondary,
                                lineHeight = 19.sp,
                            )
                        }
                    }
                }

                // Feed Cards to demonstrate scrolling
                items(8) { index ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = surfaceBackground,
                        border = BorderStroke(surfaceBorderWidth, surfaceBorderColor),
                        shadowElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(palette.surfaceMuted)
                                    .border(1.dp, palette.border, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = when (index % 4) {
                                        0 -> "💎"
                                        1 -> "⚡"
                                        2 -> "🎨"
                                        else -> "📦"
                                    },
                                    fontSize = 20.sp,
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Elemen Konten Interaktif #${index + 1}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Scroll ke atas dan ke bawah untuk melihat konten melewati bagian belakang floating bar.",
                                    fontSize = 11.sp,
                                    color = palette.textSecondary,
                                    lineHeight = 16.sp,
                                )
                            }
                        }
                    }
                }
            }

            // Top Floating Control Inspector (Collapsible on scroll)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = surfaceBackground,
                border = BorderStroke(surfaceBorderWidth, surfaceBorderColor),
                shadowElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = floatingBarPadding, vertical = 8.dp)
                    .align(Alignment.TopCenter),
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // Material Style Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        SurfaceMaterialStyle.entries.forEach { style ->
                            val isSel = materialStyle == style
                            Surface(
                                onClick = { materialStyle = style },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) palette.primary else palette.surfaceMuted,
                                border = BorderStroke(1.dp, if (isSel) palette.primary else palette.border),
                                modifier = Modifier.weight(1f),
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = style.title.split(" ")[0],
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) palette.onPrimary else palette.textPrimary,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Background & Reduce Transparency Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Background cycler
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Latar:",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = bgType.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                modifier = Modifier.clickable {
                                    bgType = when (bgType) {
                                        BackgroundType.VIBRANT -> BackgroundType.CONTENT
                                        BackgroundType.CONTENT -> BackgroundType.MINIMAL
                                        BackgroundType.MINIMAL -> BackgroundType.VIBRANT
                                    }
                                },
                            )
                        }

                        // Reduce Transparency Toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { reduceTransparency = !reduceTransparency },
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (reduceTransparency) palette.primary else palette.surfaceMuted)
                                    .border(1.dp, if (reduceTransparency) palette.primary else palette.border, RoundedCornerShape(3.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (reduceTransparency) {
                                    Text("✓", fontSize = 9.sp, color = palette.onPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Matikan Transparansi",
                                fontSize = 11.sp,
                                color = palette.textSecondary,
                            )
                        }
                    }
                }
            }

            // Bottom Floating Dock Pill
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = surfaceBackground,
                border = BorderStroke(surfaceBorderWidth, surfaceBorderColor),
                shadowElevation = 0.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    listOf("🏠 Beranda", "🔍 Cari", "⭐ Favorit", "👤 Profil").forEachIndexed { idx, tabTitle ->
                        val isSel = activeDockTab == idx
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSel) palette.primary else Color.Transparent)
                                .clickable { activeDockTab = idx }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = tabTitle,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) palette.onPrimary else palette.textPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
}
