package com.example.uiapp.ui.adaptivenav

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

enum class SimulatedWindowSize(val label: String, val minWidth: String, val badge: String) {
    COMPACT("Compact Phone", "< 600dp", "📱"),
    MEDIUM("Foldable / Tablet", "600–840dp", "📖"),
    EXPANDED("Desktop / Multi-Pane", "> 840dp", "💻"),
}

enum class FoldPosture(val label: String, val angle: String) {
    FLAT("Flat / Terbuka Rata", "180°"),
    BOOK("Book Posture (Lipatan)", "110°"),
    TABLETOP("Tabletop Tent", "90°"),
}

data class EmailConversation(
    val id: String,
    val sender: String,
    val email: String,
    val subject: String,
    val preview: String,
    val body: String,
    val time: String,
    val unread: Boolean = false,
    val category: String = "Pekerjaan",
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AdaptiveNavLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var simulatedSize by remember { mutableStateOf(SimulatedWindowSize.MEDIUM) }
    var foldPosture by remember { mutableStateOf(FoldPosture.BOOK) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Inbox, 1: Berbintang, 2: Arsip, 3: Setelan
    var selectedEmailId by remember { mutableStateOf<String?>("em_1") }
    var showCompactDetail by remember { mutableStateOf(false) }

    val conversations = remember {
        listOf(
            EmailConversation(
                id = "em_1",
                sender = "Arsitektur KMP Core",
                email = "team@kmp.dev",
                subject = "Desain Navigasi Adaptive & Foldables",
                preview = "Rangkuman pola penyesuaian layout dari single pane ke dual pane...",
                body = "Halo Tim,\n\nKetika aplikasi dijalankan pada perangkat lipat (Foldable) atau tablet, panduan arsitektur modern merekomendasikan transisi otomatis:\n1. Layar Compact (<600dp): Menggunakan Bottom Navigation Bar dengan Single Pane stack navigation.\n2. Layar Medium & Foldable (600-840dp): Menggunakan Navigation Rail di sisi kiri dan Dual-Pane List-Detail yang memisahkan konten master di kiri dan preview isi di kanan.\n3. Mengakomodasi garis lipatan fisik (separating fold hinge) agar teks tidak terpotong engsel.\n\nSalam,\nKMP Team",
                time = "14:20",
                unread = true,
                category = "Arsitektur",
            ),
            EmailConversation(
                id = "em_2",
                sender = "Desain Sistem Flat UI",
                email = "design@system.ui",
                subject = "Audit Kontras & Zero-Shadow Policy",
                preview = "Seluruh elemen kartu telah mematuhi aturan 1px crisp border murni...",
                body = "Pemberitahuan Audit:\n\nSemua kartu, dialog, dan bar navigasi telah diverifikasi tanpa drop shadow ataupun shadow elevation. Penggunaan warna berlatar bersih dengan border 1px memastikan kontras optimal di segala rasio pencahayaan.",
                time = "12:05",
                unread = false,
                category = "Design",
            ),
            EmailConversation(
                id = "em_3",
                sender = "Jetpack Compose Engine",
                email = "updates@compose.org",
                subject = "Dukungan WindowSizeClass Multiplatform",
                preview = "Perhitungan window insets dan boundary deteksi hinge lipatan...",
                body = "Update Rilis:\nWindowSizeClass kini terintegrasi penuh untuk membaca dimensi dinamis saat aplikasi di-split screen atau dirotasi landscape.",
                time = "Kemarin",
                unread = false,
                category = "Engine",
            ),
            EmailConversation(
                id = "em_4",
                sender = "Finance & Security Bot",
                email = "alerts@fintech.id",
                subject = "Laporan Aktivitas Transaksi Bulanan",
                preview = "Faktur tagihan API cloud telah diverifikasi dan lunas...",
                body = "Ringkasan Pembayaran:\nFaktur tagihan cloud server untuk cluster September 2026 telah diverifikasi aman.",
                time = "3 hari lalu",
                unread = false,
                category = "Finance",
            ),
        )
    }

    val selectedEmail = conversations.find { it.id == selectedEmailId }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Adaptive & Foldables",
                subtitle = "Bottom bar, rail, & dual-pane list-detail",
                onBack = onBack,
                action = {
                    Surface(
                        onClick = {
                            // Deep link simulator: directly choose email 3
                            selectedEmailId = "em_3"
                            if (simulatedSize == SimulatedWindowSize.COMPACT) {
                                showCompactDetail = true
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                    ) {
                        Text(
                            text = "🔗 Deep Link",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
        ) {
            // Simulator Controls: Window Size & Fold Posture Selectors
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp,
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Simulasi Ukuran Layar (Adaptive Window Class):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textMuted,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SimulatedWindowSize.entries.forEach { sizeOption ->
                            val isSel = simulatedSize == sizeOption
                            Surface(
                                onClick = {
                                    simulatedSize = sizeOption
                                    if (sizeOption != SimulatedWindowSize.COMPACT) {
                                        showCompactDetail = false
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) palette.primary else palette.surfaceMuted,
                                border = BorderStroke(1.dp, if (isSel) palette.primary else palette.border),
                                modifier = Modifier.weight(1f),
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text = "${sizeOption.badge} ${sizeOption.minWidth}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) palette.onPrimary else palette.textPrimary,
                                    )
                                    Text(
                                        text = sizeOption.label,
                                        fontSize = 9.sp,
                                        color = if (isSel) palette.onPrimary.copy(alpha = 0.8f) else palette.textMuted,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }

                    if (simulatedSize == SimulatedWindowSize.MEDIUM) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Postur Lipatan (Fold Posture):",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FoldPosture.entries.forEach { posture ->
                                    val isSel = foldPosture == posture
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSel) palette.primaryContainer else palette.surfaceMuted)
                                            .border(1.dp, if (isSel) palette.primary else palette.border, RoundedCornerShape(6.dp))
                                            .clickable { foldPosture = posture }
                                            .padding(horizontal = 8.dp, vertical = 3.dp),
                                    ) {
                                        Text(
                                            text = "${posture.label} (${posture.angle})",
                                            fontSize = 10.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSel) palette.onPrimaryContainer else palette.textSecondary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Adaptive Content Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(16.dp)),
            ) {
                when (simulatedSize) {
                    SimulatedWindowSize.COMPACT -> {
                        // Compact Phone Simulation: Single Pane with Bottom Navigation Bar
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Pane Body
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                            ) {
                                if (showCompactDetail && selectedEmail != null) {
                                    // Detail View in Single Pane
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(14.dp),
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Surface(
                                                onClick = { showCompactDetail = false },
                                                shape = RoundedCornerShape(6.dp),
                                                color = palette.surfaceMuted,
                                                border = BorderStroke(1.dp, palette.border),
                                            ) {
                                                Text(
                                                    text = "← Kembali ke List",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = palette.textPrimary,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = selectedEmail.category,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = palette.primary,
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = selectedEmail.subject,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.textPrimary,
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Dari: ${selectedEmail.sender} <${selectedEmail.email}>",
                                            fontSize = 12.sp,
                                            color = palette.textMuted,
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(palette.border),
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = selectedEmail.body,
                                            fontSize = 13.sp,
                                            color = palette.textSecondary,
                                            lineHeight = 20.sp,
                                        )
                                    }
                                } else {
                                    // Master List
                                    LazyColumn(
                                        contentPadding = PaddingValues(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        item {
                                            Text(
                                                text = "Kotak Masuk (Compact Mode)",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = palette.textPrimary,
                                                modifier = Modifier.padding(bottom = 4.dp),
                                            )
                                        }
                                        items(conversations) { item ->
                                            EmailListItem(
                                                item = item,
                                                isSelected = false,
                                                palette = palette,
                                                onClick = {
                                                    selectedEmailId = item.id
                                                    showCompactDetail = true
                                                },
                                            )
                                        }
                                    }
                                }
                            }

                            // Bottom Navigation Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .background(palette.surfaceMuted)
                                    .border(BorderStroke(1.dp, palette.border)),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                listOf("📥 Inbox", "★ Berbintang", "📦 Arsip", "⚙ Setelan").forEachIndexed { idx, tabTitle ->
                                    val isTabSel = selectedTab == idx
                                    Text(
                                        text = tabTitle,
                                        fontSize = 11.sp,
                                        fontWeight = if (isTabSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isTabSel) palette.primary else palette.textMuted,
                                        modifier = Modifier.clickable { selectedTab = idx },
                                    )
                                }
                            }
                        }
                    }

                    SimulatedWindowSize.MEDIUM, SimulatedWindowSize.EXPANDED -> {
                        // Foldable / Tablet / Multi-Pane: Navigation Rail + Dual-Pane List-Detail
                        Row(modifier = Modifier.fillMaxSize()) {
                            // Left Navigation Rail
                            Column(
                                modifier = Modifier
                                    .width(72.dp)
                                    .fillMaxHeight()
                                    .background(palette.surfaceMuted)
                                    .border(BorderStroke(1.dp, palette.border))
                                    .padding(vertical = 14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                listOf("📥", "★", "📦", "⚙").forEachIndexed { idx, icon ->
                                    val isTabSel = selectedTab == idx
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isTabSel) palette.primary else Color.Transparent)
                                            .clickable { selectedTab = idx },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = icon,
                                            fontSize = 18.sp,
                                        )
                                    }
                                }
                            }

                            // Master List Pane (Left Column)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(palette.surface),
                            ) {
                                Text(
                                    text = "Daftar Percakapan",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                    modifier = Modifier.padding(14.dp),
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(palette.border),
                                )

                                LazyColumn(
                                    contentPadding = PaddingValues(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    items(conversations) { item ->
                                        EmailListItem(
                                            item = item,
                                            isSelected = selectedEmailId == item.id,
                                            palette = palette,
                                            onClick = { selectedEmailId = item.id },
                                        )
                                    }
                                }
                            }

                            // Fold Hinge Crease Separator (Simulated Book Spine)
                            if (foldPosture == FoldPosture.BOOK) {
                                Box(
                                    modifier = Modifier
                                        .width(16.dp)
                                        .fillMaxHeight()
                                        .background(palette.surfaceMuted)
                                        .border(BorderStroke(1.dp, palette.border)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .fillMaxHeight(0.8f)
                                            .background(palette.textMuted.copy(alpha = 0.5f)),
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .fillMaxHeight()
                                        .background(palette.border),
                                )
                            }

                            // Detail Preview Pane (Right Column)
                            Box(
                                modifier = Modifier
                                    .weight(1.3f)
                                    .fillMaxHeight()
                                    .background(palette.surface)
                                    .padding(16.dp),
                            ) {
                                if (selectedEmail != null) {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(palette.primaryContainer)
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                            ) {
                                                Text(
                                                    text = selectedEmail.category,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = palette.onPrimaryContainer,
                                                )
                                            }
                                            Text(
                                                text = selectedEmail.time,
                                                fontSize = 11.sp,
                                                color = palette.textMuted,
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = selectedEmail.subject,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.textPrimary,
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "${selectedEmail.sender} • ${selectedEmail.email}",
                                            fontSize = 12.sp,
                                            color = palette.textMuted,
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(palette.border),
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = selectedEmail.body,
                                            fontSize = 13.sp,
                                            color = palette.textSecondary,
                                            lineHeight = 21.sp,
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = "Pilih percakapan dari daftar di sebelah kiri.",
                                            fontSize = 12.sp,
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

@Composable
private fun EmailListItem(
    item: EmailConversation,
    isSelected: Boolean,
    palette: com.example.uiapp.theme.AppPalette,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) palette.primaryContainer.copy(alpha = 0.5f) else palette.surface,
        border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.sender,
                    fontSize = 12.sp,
                    fontWeight = if (item.unread) FontWeight.Bold else FontWeight.SemiBold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = item.time,
                    fontSize = 10.sp,
                    color = palette.textMuted,
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = item.subject,
                fontSize = 12.sp,
                fontWeight = if (item.unread) FontWeight.Bold else FontWeight.Normal,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.preview,
                fontSize = 11.sp,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
