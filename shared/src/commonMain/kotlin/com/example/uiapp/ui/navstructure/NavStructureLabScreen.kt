package com.example.uiapp.ui.navstructure

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.launch

private data class NavItem(val id: String, val title: String, val badgeCount: Int = 0, val hasDot: Boolean = false)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NavStructureLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var activeTab by remember { mutableIntStateOf(0) }
    var voiceListening by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var deepLinkResult by remember { mutableStateOf<String?>(null) }
    var showRailDemo by remember { mutableStateOf(false) }

    val navItems = remember {
        listOf(
            NavItem("explore", "Eksplorasi", badgeCount = 3),
            NavItem("activity", "Aktivitas", hasDot = true),
            NavItem("bookmarks", "Disimpan"),
            NavItem("messages", "Pesan", badgeCount = 12),
        )
    }

    val listState = rememberLazyListState()
    val isScrollingDown by remember {
        derivedStateOf {
            listState.firstVisibleItemScrollOffset > 30 || listState.firstVisibleItemIndex > 0
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = palette.surface,
                drawerContentColor = palette.textPrimary,
                modifier = Modifier.width(300.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                ) {
                    Text(
                        text = "Navigasi Drawer",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "Drawer menu terstruktur & Flat UI",
                        fontSize = 12.sp,
                        color = palette.textMuted,
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    navItems.forEachIndexed { index, item ->
                        Surface(
                            onClick = {
                                activeTab = index
                                scope.launch { drawerState.close() }
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (activeTab == index) palette.primaryContainer else Color.Transparent,
                            border = BorderStroke(
                                1.dp,
                                if (activeTab == index) palette.primary else Color.Transparent,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = item.title,
                                    fontSize = 14.sp,
                                    fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (activeTab == index) palette.onPrimaryContainer else palette.textPrimary,
                                )
                                if (item.badgeCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(palette.danger)
                                            .padding(horizontal = 7.dp, vertical = 2.dp),
                                    ) {
                                        Text(
                                            text = item.badgeCount.toString(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                        )
                                    }
                                } else if (item.hasDot) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(palette.primary),
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Surface(
                        onClick = { scope.launch { drawerState.close() } },
                        shape = RoundedCornerShape(12.dp),
                        color = palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                            Text("Tutup Drawer", fontSize = 13.sp, color = palette.textPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        },
    ) {
        Scaffold(
            containerColor = palette.background,
            topBar = {
                UiTopBar(
                    title = "Navigasi & Struktur",
                    subtitle = "Drawer, Rail, Floating Pill, Badges",
                    onBack = onBack,
                    action = {
                        Surface(
                            onClick = { scope.launch { drawerState.open() } },
                            shape = RoundedCornerShape(8.dp),
                            color = palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.border),
                        ) {
                            Text("Menu", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 12.sp, color = palette.textPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    },
                )
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    if (showRailDemo) {
                        NavigationRail(
                            containerColor = palette.surface,
                            contentColor = palette.textPrimary,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(72.dp)
                                .border(BorderStroke(1.dp, palette.border)),
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            navItems.forEachIndexed { index, item ->
                                NavigationRailItem(
                                    selected = activeTab == index,
                                    onClick = { activeTab = index },
                                    icon = {
                                        Box {
                                            Text(
                                                text = item.title.take(1),
                                                fontWeight = FontWeight.Bold,
                                                color = if (activeTab == index) palette.primary else palette.textSecondary,
                                            )
                                            if (item.badgeCount > 0) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(palette.danger)
                                                        .align(Alignment.TopEnd),
                                                )
                                            }
                                        }
                                    },
                                    label = null,
                                )
                            }
                        }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        // Section 1: Search App Bar + Voice Search
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = palette.surface,
                                border = BorderStroke(1.dp, palette.border),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("1. Search App Bar & Voice Search", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = palette.surfaceMuted,
                                        border = BorderStroke(1.dp, if (voiceListening) palette.primary else palette.border),
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Text(
                                                text = if (voiceListening) "Mendengarkan suara..." else if (searchQuery.isNotEmpty()) searchQuery else "Ketik pencarian atau tekan mic...",
                                                fontSize = 13.sp,
                                                color = if (voiceListening) palette.primary else palette.textMuted,
                                                modifier = Modifier.weight(1f),
                                            )
                                            Surface(
                                                onClick = {
                                                    voiceListening = !voiceListening
                                                    if (voiceListening) {
                                                        searchQuery = "Hasil suara: 'Komponen Compose Multiplatform'"
                                                    }
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (voiceListening) palette.danger else palette.primary,
                                            ) {
                                                Text(
                                                    text = if (voiceListening) "Stop" else "Mic",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                )
                                            }
                                        }
                                    }
                                    if (voiceListening) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        VoiceWaveVisualizer(palette = palette)
                                    }
                                }
                            }
                        }

                        // Section 2: Toggle Adaptive Navigation Rail
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = palette.surface,
                                border = BorderStroke(1.dp, palette.border),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("2. Adaptive Navigation Rail", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                                        Text("Sidebar navigasi vertikal untuk mode tablet / desktop", fontSize = 12.sp, color = palette.textMuted)
                                    }
                                    Surface(
                                        onClick = { showRailDemo = !showRailDemo },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (showRailDemo) palette.primaryContainer else palette.surfaceMuted,
                                        border = BorderStroke(1.dp, if (showRailDemo) palette.primary else palette.border),
                                    ) {
                                        Text(
                                            text = if (showRailDemo) "Tampak" else "Sembunyi",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (showRailDemo) palette.onPrimaryContainer else palette.textPrimary,
                                        )
                                    }
                                }
                            }
                        }

                        // Section 3: Deep Link & Universal Link Dispatcher Simulator
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = palette.surface,
                                border = BorderStroke(1.dp, palette.border),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("3. Deep Link & Universal Link Handler", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                                    Text("Simulasi parsing URL deep link dan routing tujuan", fontSize = 12.sp, color = palette.textMuted)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    val sampleLinks = listOf(
                                        "app://testingui.com/commerce/item/42",
                                        "https://testingui.com/media/player?id=vid_99",
                                        "testingui://settings/privacy",
                                    )
                                    sampleLinks.forEach { link ->
                                        Surface(
                                            onClick = {
                                                val uriPart = link.substringAfter("://").substringAfter("/")
                                                deepLinkResult = "Rute Berhasil Dikenali -> Target: '$uriPart' (Parsed OK)"
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            color = palette.surfaceMuted,
                                            border = BorderStroke(1.dp, palette.border),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp),
                                        ) {
                                            Text(
                                                text = link,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = palette.primary,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                    }
                                    if (deepLinkResult != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(palette.primaryContainer)
                                                .border(1.dp, palette.primary, RoundedCornerShape(8.dp))
                                                .padding(10.dp),
                                        ) {
                                            Text(
                                                text = deepLinkResult ?: "",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = palette.onPrimaryContainer,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Scroll filler cards to test floating pill minimize behavior
                        items((1..10).toList()) { index ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = palette.surface,
                                border = BorderStroke(1.dp, palette.border),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = "Item Konten Demo #$index (${navItems[activeTab].title})",
                                        fontSize = 13.sp,
                                        color = palette.textPrimary,
                                        fontWeight = FontWeight.Medium,
                                    )
                                    Text("Gulir untuk tes pill tab", fontSize = 11.sp, color = palette.textMuted)
                                }
                            }
                        }
                    }
                }

                // Section 4: iOS Floating Tab Bar (Pill) with auto-minimize on scroll
                AnimatedVisibility(
                    visible = !isScrollingDown,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 14.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(30.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            navItems.forEachIndexed { index, item ->
                                val isSelected = activeTab == index
                                Surface(
                                    onClick = { activeTab = index },
                                    shape = RoundedCornerShape(22.dp),
                                    color = if (isSelected) palette.primary else Color.Transparent,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) palette.primary else Color.Transparent,
                                    ),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Text(
                                            text = item.title,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else palette.textSecondary,
                                        )
                                        if (item.badgeCount > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (isSelected) Color.White.copy(alpha = 0.25f) else palette.danger)
                                                    .padding(horizontal = 5.dp, vertical = 1.dp),
                                            ) {
                                                Text(
                                                    text = item.badgeCount.toString(),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
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
}

@Composable
private fun VoiceWaveVisualizer(palette: com.example.uiapp.theme.AppPalette) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedHeight1 by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    )
    val animatedHeight2 by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 32f,
        animationSpec = infiniteRepeatable(tween(300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    )
    val animatedHeight3 by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(palette.surfaceMuted)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf(animatedHeight1, animatedHeight2, animatedHeight3, animatedHeight2, animatedHeight1).forEach { h ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .width(4.dp)
                    .height(h.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(palette.primary),
            )
        }
    }
}
