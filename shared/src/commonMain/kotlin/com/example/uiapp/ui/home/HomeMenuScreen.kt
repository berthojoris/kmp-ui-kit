package com.example.uiapp.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.getPlatform
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.AccessibilityLabMenuIcon
import com.example.uiapp.ui.components.ActivityInboxMenuIcon
import com.example.uiapp.ui.components.AdaptiveMenuIcon
import com.example.uiapp.ui.components.AdaptiveNavMenuIcon
import com.example.uiapp.ui.components.AdvancedInputMenuIcon
import com.example.uiapp.ui.components.AdvancedListMenuIcon
import com.example.uiapp.ui.components.AiChatMenuIcon
import com.example.uiapp.ui.components.AiVoiceOrbMenuIcon
import com.example.uiapp.ui.components.AmountKeypadMenuIcon
import com.example.uiapp.ui.components.AuthMenuIcon
import com.example.uiapp.ui.components.AutoBlurScrollMenuIcon
import com.example.uiapp.ui.components.BalanceMaskingMenuIcon
import com.example.uiapp.ui.components.BentoGridMenuIcon
import com.example.uiapp.ui.components.BiometricMenuIcon
import com.example.uiapp.ui.components.BottomSheetMenuIcon
import com.example.uiapp.ui.components.CategoryScrollMenuIcon
import com.example.uiapp.ui.components.ChartsMenuIcon
import com.example.uiapp.ui.components.CommandPaletteMenuIcon
import com.example.uiapp.ui.components.CommerceMenuIcon
import com.example.uiapp.ui.components.ConfettiMenuIcon
import com.example.uiapp.ui.components.CreateMediaMenuIcon
import com.example.uiapp.ui.components.DataCardsMenuIcon
import com.example.uiapp.ui.components.DataTableMenuIcon
import com.example.uiapp.ui.components.DataVizMenuIcon
import com.example.uiapp.ui.components.EmptyStateMenuIcon
import com.example.uiapp.ui.components.ExpressiveControlsMenuIcon
import com.example.uiapp.ui.components.FeedbackMenuIcon
import com.example.uiapp.ui.components.FocusTimerMenuIcon
import com.example.uiapp.ui.components.FormLabMenuIcon
import com.example.uiapp.ui.components.GalleryMenuIcon
import com.example.uiapp.ui.components.GamificationMenuIcon
import com.example.uiapp.ui.components.HoldToConfirmMenuIcon
import com.example.uiapp.ui.components.InfiniteScrollMenuIcon
import com.example.uiapp.ui.components.MasonryGridMenuIcon
import com.example.uiapp.ui.components.MediaMenuIcon
import com.example.uiapp.ui.components.MorphingFabMenuIcon
import com.example.uiapp.ui.components.MotionLabMenuIcon
import com.example.uiapp.ui.components.MultiSelectMenuIcon
import com.example.uiapp.ui.components.NativeSurfacesMenuIcon
import com.example.uiapp.ui.components.NavLabMenuIcon
import com.example.uiapp.ui.components.NavStructureMenuIcon
import com.example.uiapp.ui.components.OnboardingMenuIcon
import com.example.uiapp.ui.components.OverlayMenuIcon
import com.example.uiapp.ui.components.ParallaxHeroMenuIcon
import com.example.uiapp.ui.components.PasscodeMenuIcon
import com.example.uiapp.ui.components.PerforatedTicketMenuIcon
import com.example.uiapp.ui.components.PermissionsMenuIcon
import com.example.uiapp.ui.components.PlaceholderMenuIcon
import com.example.uiapp.ui.components.PrivacyMaskingMenuIcon
import com.example.uiapp.ui.components.ProfileSetupMenuIcon
import com.example.uiapp.ui.components.PullDismissMenuIcon
import com.example.uiapp.ui.components.ReactionsMenuIcon
import com.example.uiapp.ui.components.ResumeFormMenuIcon
import com.example.uiapp.ui.components.ScratchCardMenuIcon
import com.example.uiapp.ui.components.ScrollMotionMenuIcon
import com.example.uiapp.ui.components.SearchMenuIcon
import com.example.uiapp.ui.components.SelectionToolbarMenuIcon
import com.example.uiapp.ui.components.SettingsMenuIcon
import com.example.uiapp.ui.components.SharedElementMenuIcon
import com.example.uiapp.ui.components.ShimmerMenuIcon
import com.example.uiapp.ui.components.SlideToConfirmMenuIcon
import com.example.uiapp.ui.components.SplashMenuIcon
import com.example.uiapp.ui.components.SplitComparisonMenuIcon
import com.example.uiapp.ui.components.StatsMenuIcon
import com.example.uiapp.ui.components.StickyHeaderMenuIcon
import com.example.uiapp.ui.components.StreakHeatmapMenuIcon
import com.example.uiapp.ui.components.SwipeActionsMenuIcon
import com.example.uiapp.ui.components.SwipeCardsMenuIcon
import com.example.uiapp.ui.components.SystemPlatformMenuIcon
import com.example.uiapp.ui.components.ThemeMenuIcon
import com.example.uiapp.ui.components.TickerCounterMenuIcon
import com.example.uiapp.ui.components.UndoQueueMenuIcon
import kotlinx.coroutines.launch

enum class HomeSection(
    val title: String,
    val shortLabel: String,
) {
    ALL("Semua Lab", "Semua (71)"),
    CORE("1. Labs Eksperimen Inti", "Inti (19)"),
    AUTH("2. Onboarding & Auth Flow", "Auth (7)"),
    ADVANCED("3. Komponen Lanjutan Baru", "Lanjutan (9)"),
    WORLD_CLASS("4. Komponen World-Class UI", "World-Class (12)"),
    TRENDING("5. Trending Mobile UI 2025/2026", "Trending (6)"),
    MODERN("6. Ide Komponen UI Modern 2026", "Modern UI (9)"),
    PRO("7. Komponen Modern & Profesional", "Pro (5)"),
    NEW_LABS("8. Gamifikasi, Viz & Media", "Baru (4)"),
}

data class MenuEntry(
    val id: String,
    val title: String,
    val subtitle: String,
    val enabled: Boolean,
    val section: HomeSection,
)

val MenuEntries = listOf(
    // 1. Labs Eksperimen Inti (19)
    MenuEntry("auto_blur_scroll", "Auto-blur Scroll", "List yang memudar dan blur di tepi atas", true, HomeSection.CORE),
    MenuEntry("sticky_header", "Sticky Header", "Header lengket saat list di-scroll", true, HomeSection.CORE),
    MenuEntry("parallax_hero", "Parallax Hero", "Hero image multi-layer parallax", true, HomeSection.CORE),
    MenuEntry("shimmer", "Shimmer Skeleton", "Placeholder loading berkilau", true, HomeSection.CORE),
    MenuEntry("shared_element", "Shared Element", "Kartu membesar mulus ke detail", true, HomeSection.CORE),
    MenuEntry("bottom_sheet", "Bottom Sheet", "Sheet bertingkat peek ke full", true, HomeSection.CORE),
    MenuEntry("empty_state", "Empty & Error State", "Kondisi kosong, gagal, berhasil", true, HomeSection.CORE),
    MenuEntry("motion_lab", "Motion Lab", "Collapsing bar, pull-refresh, swipe", true, HomeSection.CORE),
    MenuEntry("form_lab", "Form Lab", "Validasi, slider, chip, dan OTP", true, HomeSection.CORE),
    MenuEntry("nav_lab", "Navigation Lab", "Segmented, bottom nav, speed-dial", true, HomeSection.CORE),
    MenuEntry("adaptive", "Adaptive Layout", "Tata letak menyesuaikan layar", true, HomeSection.CORE),
    MenuEntry("gallery", "Swipeable Gallery", "Geser foto dan zoom dua ketuk", true, HomeSection.CORE),
    MenuEntry("feedback", "Feedback Lab", "Toast bertumpuk dan banner", true, HomeSection.CORE),
    MenuEntry("stats", "Stats Lab", "Progress ring dan expandable card", true, HomeSection.CORE),
    MenuEntry("charts", "Charts Lab", "Line, bar, donut, dan sparkline", true, HomeSection.CORE),
    MenuEntry("infinite_scroll", "Infinite Scroll", "Pagination dan load-more", true, HomeSection.CORE),
    MenuEntry("multi_select", "Multi-select", "Edit mode dan bulk action", true, HomeSection.CORE),
    MenuEntry("search", "Pencarian", "Autocomplete dan riwayat", true, HomeSection.CORE),
    MenuEntry("theme_lab", "Theme & Dark Mode", "Terang, gelap, dan sistem", true, HomeSection.CORE),

    // 2. Onboarding & Auth Flow (7)
    MenuEntry("splash", "Splash Screen", "Reveal logo beranimasi", true, HomeSection.AUTH),
    MenuEntry("onboarding", "Onboarding", "Carousel dan page dots", true, HomeSection.AUTH),
    MenuEntry("auth", "Auth", "Login, register, social sign-in", true, HomeSection.AUTH),
    MenuEntry("biometric", "Biometric", "Face ID prompt sheet", true, HomeSection.AUTH),
    MenuEntry("passcode", "Passcode", "PIN, biometrik & swipe unlock", true, HomeSection.AUTH),
    MenuEntry("permissions", "Izin Aplikasi", "Permission request", true, HomeSection.AUTH),
    MenuEntry("profile_setup", "Profile Setup", "Wizard langkah bertahap", true, HomeSection.AUTH),

    // 3. Laboratorium Komponen Lanjutan Baru (9)
    MenuEntry("nav_structure", "Nav & Struktur", "Drawer, Rail, Floating Pill, Badges", true, HomeSection.ADVANCED),
    MenuEntry("advanced_list", "Konten & List", "Reorder, A–Z, Chat, Timeline", true, HomeSection.ADVANCED),
    MenuEntry("data_cards", "Kartu & Data", "Kalender, Kanban, Pricing, Review", true, HomeSection.ADVANCED),
    MenuEntry("advanced_input", "Input Lanjutan", "Wheel Picker, Tag, Signature Pad", true, HomeSection.ADVANCED),
    MenuEntry("overlay_lab", "Overlay & Feedback", "Action Sheet, Spotlight, Tooltip", true, HomeSection.ADVANCED),
    MenuEntry("media_lab", "Media & Immersive", "Video, Mini Player, Stories, Waveform", true, HomeSection.ADVANCED),
    MenuEntry("settings_lab", "Pengaturan & Akun", "Grouped List, Bahasa, Paywall", true, HomeSection.ADVANCED),
    MenuEntry("commerce_lab", "Commerce & Transaksi", "Produk, Keranjang, Checkout, Dompet", true, HomeSection.ADVANCED),
    MenuEntry("system_platform", "Sistem & Platform", "QR, Map Sheet, Haptic, Live Activity", true, HomeSection.ADVANCED),

    // 4. Komponen World-Class UI (12)
    MenuEntry("ticker_counter", "Rolling Ticker", "Odometer digit vertikal, saldo, saham", true, HomeSection.WORLD_CLASS),
    MenuEntry("pull_dismiss", "Pull to Dismiss", "Gestur geser bawah, lightbox media", true, HomeSection.WORLD_CLASS),
    MenuEntry("command_palette", "Command Palette", "Spotlight launcher & quick search (Cmd+K)", true, HomeSection.WORLD_CLASS),
    MenuEntry("confetti_particles", "Confetti & Partikel", "Celebration burst & physics canvas", true, HomeSection.WORLD_CLASS),
    MenuEntry("reactions_bar", "Reaksi Interaktif", "Bouncy emoji dock & floating reactions", true, HomeSection.WORLD_CLASS),
    MenuEntry("swipe_cards", "Swipe Decision Cards", "Tinder gesture stack, physics rotation", true, HomeSection.WORLD_CLASS),
    MenuEntry("swipe_actions", "Cupertino Actions", "Swipe-to-reveal & full-swipe delete", true, HomeSection.WORLD_CLASS),
    MenuEntry("morphing_fab", "Morphing FAB", "Container transform into action sheet", true, HomeSection.WORLD_CLASS),
    MenuEntry("privacy_masking", "App Switcher Privacy", "Sensitive screen blur & frosted shield", true, HomeSection.WORLD_CLASS),
    MenuEntry("balance_masking", "Balance Masking", "FinTech privacy & shake-to-hide", true, HomeSection.WORLD_CLASS),
    MenuEntry("scratch_card", "Scratch Card", "Touch erase & reward card reveal", true, HomeSection.WORLD_CLASS),
    MenuEntry("streak_heatmap", "Streak & Heatmap", "Duolingo flame & GitHub activity grid", true, HomeSection.WORLD_CLASS),

    // 5. Trending Mobile UI 2025/2026 (6)
    MenuEntry("hold_to_confirm", "Hold to Confirm", "Long-press progress & anti-mistake action", true, HomeSection.TRENDING),
    MenuEntry("slide_to_confirm", "Slide to Confirm", "Draggable pill & track lock snap", true, HomeSection.TRENDING),
    MenuEntry("split_comparison", "Split Comparison", "Before & after interactive divider slider", true, HomeSection.TRENDING),
    MenuEntry("bento_grid", "Bento Grid Dashboard", "Apple & SaaS modular interactive cards", true, HomeSection.TRENDING),
    MenuEntry("perforated_ticket", "Perforated Ticket", "Boarding pass, cutouts & tear perforation", true, HomeSection.TRENDING),
    MenuEntry("ai_voice_orb", "AI Voice Orb", "ChatGPT Voice & Siri fluid organic intelligence", true, HomeSection.TRENDING),

    // 6. Ide Komponen UI Modern 2026 (9)
    MenuEntry("ai_chat", "AI Chat Composer", "Composer adaptif, streaming & citation", true, HomeSection.MODERN),
    MenuEntry("selection_toolbar", "Contextual Toolbar", "Toolbar seleksi massal & floating action", true, HomeSection.MODERN),
    MenuEntry("undo_queue", "Optimistic & Undo", "Antrean aksi instan, timer, & rollback", true, HomeSection.MODERN),
    MenuEntry("adaptive_navigation", "Adaptive & Foldables", "Bottom bar, rail, & dual-pane layout", true, HomeSection.MODERN),
    MenuEntry("expressive_controls", "Material 3 Expressive", "Bentuk dinamis, wavy loader, & slider", true, HomeSection.MODERN),
    MenuEntry("accessibility_lab", "Aksesibilitas & Teks", "Skala font, kontras tinggi, & hitbox", true, HomeSection.MODERN),
    MenuEntry("activity_inbox", "Activity Inbox", "Pusat notifikasi, grup hari, & filter", true, HomeSection.MODERN),
    MenuEntry("resume_form", "Save & Resume Form", "Autosave draft, restore & recovery", true, HomeSection.MODERN),
    MenuEntry("native_surfaces", "Native Material", "Permukaan mengambang & edge flow", true, HomeSection.MODERN),

    // 7. Komponen Modern & Profesional (5)
    MenuEntry("focus_timer", "Focus Timer", "Ring sesi Pomodoro & siklus istirahat", true, HomeSection.PRO),
    MenuEntry("masonry_grid", "Masonry Grid", "Waterfall dua kolom ala Pinterest", true, HomeSection.PRO),
    MenuEntry("amount_keypad", "Amount Keypad", "Keypad nominal transfer Rupiah", true, HomeSection.PRO),
    MenuEntry("category_scroll", "Scroll-Sync Category", "Tab kategori sinkron dengan list menu", true, HomeSection.PRO),
    MenuEntry("data_table", "Data Table Pro", "Sticky column, sorting & status pill", true, HomeSection.PRO),

    // 8. Gamifikasi, Viz & Media (4)
    MenuEntry("gamification_lab", "Gamifikasi & Reward", "Roda undian, flashcard, kuis, polling, skor", true, HomeSection.NEW_LABS),
    MenuEntry("dataviz_lab", "Data Viz Lanjutan", "Radar, gauge, candlestick & waterfall", true, HomeSection.NEW_LABS),
    MenuEntry("scroll_motion_lab", "Motion & Scroll", "Marquee, carousel, tab morphing, flip clock", true, HomeSection.NEW_LABS),
    MenuEntry("create_lab", "Media Creation", "Filter foto, QR generator, perekam, editor", true, HomeSection.NEW_LABS),
)

@Composable
fun HomeMenuScreen(
    onMenuClick: (String) -> Unit,
    gridState: LazyGridState = rememberLazyGridState(),
    selectedSection: HomeSection = HomeSection.ALL,
    onSectionSelected: (HomeSection) -> Unit = {},
    lastTestedRoute: String? = null,
) {
    val platform = remember { getPlatform().name }
    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()

    val displayedEntries = remember(selectedSection) {
        if (selectedSection == HomeSection.ALL) {
            MenuEntries
        } else {
            MenuEntries.filter { it.section == selectedSection }
        }
    }

    Scaffold(containerColor = palette.background) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Header
            item(span = { GridItemSpan(maxLineSpan) }) {
                HomeHeader(platform, palette, MenuEntries.size)
            }

            // Section Filter Chips Bar
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionFilterChipsBar(
                    selectedSection = selectedSection,
                    palette = palette,
                    onSectionSelected = { section ->
                        onSectionSelected(section)
                        scope.launch {
                            gridState.scrollToItem(0)
                        }
                    },
                )
            }

            // Content Items: All sections with headers vs Single section
            if (selectedSection == HomeSection.ALL) {
                val sections = listOf(
                    HomeSection.CORE,
                    HomeSection.AUTH,
                    HomeSection.ADVANCED,
                    HomeSection.WORLD_CLASS,
                    HomeSection.TRENDING,
                    HomeSection.MODERN,
                    HomeSection.PRO,
                    HomeSection.NEW_LABS,
                )

                sections.forEach { sec ->
                    val secEntries = MenuEntries.filter { it.section == sec }
                    if (secEntries.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }, key = "sec_header_${sec.name}") {
                            SectionHeader(
                                title = sec.title,
                                count = secEntries.size,
                                palette = palette,
                                onQuickFilter = {
                                    onSectionSelected(sec)
                                    scope.launch {
                                        gridState.scrollToItem(0)
                                    }
                                },
                            )
                        }
                        items(secEntries, key = { it.id }) { entry ->
                            MenuGridCard(
                                entry = entry,
                                palette = palette,
                                isLastTested = entry.id == lastTestedRoute,
                                onClick = { onMenuClick(entry.id) },
                            )
                        }
                    }
                }
            } else {
                item(span = { GridItemSpan(maxLineSpan) }, key = "sec_header_single_${selectedSection.name}") {
                    SectionHeader(
                        title = selectedSection.title,
                        count = displayedEntries.size,
                        palette = palette,
                        isFiltered = true,
                        onClearFilter = {
                            onSectionSelected(HomeSection.ALL)
                            scope.launch {
                                gridState.scrollToItem(0)
                            }
                        },
                    )
                }
                items(displayedEntries, key = { it.id }) { entry ->
                    MenuGridCard(
                        entry = entry,
                        palette = palette,
                        isLastTested = entry.id == lastTestedRoute,
                        onClick = { onMenuClick(entry.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    platform: String,
    palette: com.example.uiapp.theme.AppPalette,
    totalCount: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 4.dp),
    ) {
        Text(
            text = "Testing UI",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            letterSpacing = (-0.5).sp,
            color = palette.textPrimary,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Laboratorium $totalCount Komponen UI · $platform",
            fontSize = 13.sp,
            color = palette.textSecondary,
        )
    }
}

@Composable
private fun SectionFilterChipsBar(
    selectedSection: HomeSection,
    palette: com.example.uiapp.theme.AppPalette,
    onSectionSelected: (HomeSection) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(HomeSection.entries) { section ->
            val isSelected = selectedSection == section
            Surface(
                onClick = { onSectionSelected(section) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) palette.primary else palette.surface,
                border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = section.shortLabel,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) palette.onPrimary else palette.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    palette: com.example.uiapp.theme.AppPalette,
    isFiltered: Boolean = false,
    onQuickFilter: (() -> Unit)? = null,
    onClearFilter: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(palette.primary),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "($count)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textMuted,
            )
        }

        if (isFiltered && onClearFilter != null) {
            Text(
                text = "Tampilkan Semua",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = palette.primary,
                modifier = Modifier.clickable { onClearFilter() },
            )
        } else if (onQuickFilter != null) {
            Text(
                text = "Fokus Section",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = palette.primary,
                modifier = Modifier.clickable { onQuickFilter() },
            )
        }
    }
}

@Composable
private fun MenuGridCard(
    entry: MenuEntry,
    palette: com.example.uiapp.theme.AppPalette,
    isLastTested: Boolean = false,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        enabled = entry.enabled,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .graphicsLayer { alpha = if (entry.enabled) 1f else 0.55f },
        shape = RoundedCornerShape(20.dp),
        color = if (isLastTested) palette.primaryContainer.copy(alpha = 0.35f) else palette.surface,
        border = BorderStroke(if (isLastTested) 1.5.dp else 1.dp, if (isLastTested) palette.primary else palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isLastTested) palette.primary.copy(alpha = 0.15f) else palette.surfaceMuted),
                    contentAlignment = Alignment.Center,
                ) {
                    when (entry.id) {
                        "auto_blur_scroll" -> AutoBlurScrollMenuIcon(tint = palette.textPrimary)
                        "sticky_header" -> StickyHeaderMenuIcon(tint = palette.textPrimary)
                        "parallax_hero" -> ParallaxHeroMenuIcon(tint = palette.textPrimary)
                        "shimmer" -> ShimmerMenuIcon(tint = palette.textPrimary)
                        "shared_element" -> SharedElementMenuIcon(tint = palette.textPrimary)
                        "bottom_sheet" -> BottomSheetMenuIcon(tint = palette.textPrimary)
                        "empty_state" -> EmptyStateMenuIcon(tint = palette.textPrimary)
                        "motion_lab" -> MotionLabMenuIcon(tint = palette.textPrimary)
                        "form_lab" -> FormLabMenuIcon(tint = palette.textPrimary)
                        "nav_lab" -> NavLabMenuIcon(tint = palette.textPrimary)
                        "adaptive" -> AdaptiveMenuIcon(tint = palette.textPrimary)
                        "gallery" -> GalleryMenuIcon(tint = palette.textPrimary)
                        "feedback" -> FeedbackMenuIcon(tint = palette.textPrimary)
                        "stats" -> StatsMenuIcon(tint = palette.textPrimary)
                        "charts" -> ChartsMenuIcon(tint = palette.textPrimary)
                        "infinite_scroll" -> InfiniteScrollMenuIcon(tint = palette.textPrimary)
                        "multi_select" -> MultiSelectMenuIcon(tint = palette.textPrimary)
                        "search" -> SearchMenuIcon(tint = palette.textPrimary)
                        "theme_lab" -> ThemeMenuIcon(tint = palette.textPrimary)
                        "splash" -> SplashMenuIcon(tint = palette.textPrimary)
                        "onboarding" -> OnboardingMenuIcon(tint = palette.textPrimary)
                        "auth" -> AuthMenuIcon(tint = palette.textPrimary)
                        "biometric" -> BiometricMenuIcon(tint = palette.textPrimary)
                        "passcode" -> PasscodeMenuIcon(tint = palette.textPrimary)
                        "permissions" -> PermissionsMenuIcon(tint = palette.textPrimary)
                        "profile_setup" -> ProfileSetupMenuIcon(tint = palette.textPrimary)
                        "nav_structure" -> NavStructureMenuIcon(tint = palette.textPrimary)
                        "advanced_list" -> AdvancedListMenuIcon(tint = palette.textPrimary)
                        "data_cards" -> DataCardsMenuIcon(tint = palette.textPrimary)
                        "advanced_input" -> AdvancedInputMenuIcon(tint = palette.textPrimary)
                        "overlay_lab" -> OverlayMenuIcon(tint = palette.textPrimary)
                        "media_lab" -> MediaMenuIcon(tint = palette.textPrimary)
                        "settings_lab" -> SettingsMenuIcon(tint = palette.textPrimary)
                        "commerce_lab" -> CommerceMenuIcon(tint = palette.textPrimary)
                        "system_platform" -> SystemPlatformMenuIcon(tint = palette.textPrimary)
                        "ticker_counter" -> TickerCounterMenuIcon(tint = palette.textPrimary)
                        "pull_dismiss" -> PullDismissMenuIcon(tint = palette.textPrimary)
                        "command_palette" -> CommandPaletteMenuIcon(tint = palette.textPrimary)
                        "confetti_particles" -> ConfettiMenuIcon(tint = palette.textPrimary)
                        "reactions_bar" -> ReactionsMenuIcon(tint = palette.textPrimary)
                        "swipe_cards" -> SwipeCardsMenuIcon(tint = palette.textPrimary)
                        "swipe_actions" -> SwipeActionsMenuIcon(tint = palette.textPrimary)
                        "morphing_fab" -> MorphingFabMenuIcon(tint = palette.textPrimary)
                        "privacy_masking" -> PrivacyMaskingMenuIcon(tint = palette.textPrimary)
                        "balance_masking" -> BalanceMaskingMenuIcon(tint = palette.textPrimary)
                        "scratch_card" -> ScratchCardMenuIcon(tint = palette.textPrimary)
                        "streak_heatmap" -> StreakHeatmapMenuIcon(tint = palette.textPrimary)
                        "hold_to_confirm" -> HoldToConfirmMenuIcon(tint = palette.textPrimary)
                        "slide_to_confirm" -> SlideToConfirmMenuIcon(tint = palette.textPrimary)
                        "split_comparison" -> SplitComparisonMenuIcon(tint = palette.textPrimary)
                        "bento_grid" -> BentoGridMenuIcon(tint = palette.textPrimary)
                        "perforated_ticket" -> PerforatedTicketMenuIcon(tint = palette.textPrimary)
                        "ai_voice_orb" -> AiVoiceOrbMenuIcon(tint = palette.textPrimary)
                        "ai_chat" -> AiChatMenuIcon(tint = palette.textPrimary)
                        "selection_toolbar" -> SelectionToolbarMenuIcon(tint = palette.textPrimary)
                        "undo_queue" -> UndoQueueMenuIcon(tint = palette.textPrimary)
                        "adaptive_navigation" -> AdaptiveNavMenuIcon(tint = palette.textPrimary)
                        "expressive_controls" -> ExpressiveControlsMenuIcon(tint = palette.textPrimary)
                        "accessibility_lab" -> AccessibilityLabMenuIcon(tint = palette.textPrimary)
                        "activity_inbox" -> ActivityInboxMenuIcon(tint = palette.textPrimary)
                        "resume_form" -> ResumeFormMenuIcon(tint = palette.textPrimary)
                        "native_surfaces" -> NativeSurfacesMenuIcon(tint = palette.textPrimary)
                        "focus_timer" -> FocusTimerMenuIcon(tint = palette.textPrimary)
                        "masonry_grid" -> MasonryGridMenuIcon(tint = palette.textPrimary)
                        "amount_keypad" -> AmountKeypadMenuIcon(tint = palette.textPrimary)
                        "category_scroll" -> CategoryScrollMenuIcon(tint = palette.textPrimary)
                        "data_table" -> DataTableMenuIcon(tint = palette.textPrimary)
                        "gamification_lab" -> GamificationMenuIcon(tint = palette.textPrimary)
                        "dataviz_lab" -> DataVizMenuIcon(tint = palette.textPrimary)
                        "scroll_motion_lab" -> ScrollMotionMenuIcon(tint = palette.textPrimary)
                        "create_lab" -> CreateMediaMenuIcon(tint = palette.textPrimary)
                        else -> PlaceholderMenuIcon(tint = palette.textPrimary)
                    }
                }

                if (isLastTested) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(palette.primary)
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = "DIUJI",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.onPrimary,
                        )
                    }
                } else if (!entry.enabled) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.surfaceMuted)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = "SEGERA",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textMuted,
                        )
                    }
                }
            }

            Column {
                Text(
                    text = entry.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = entry.subtitle,
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
