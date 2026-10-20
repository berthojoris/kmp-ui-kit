package com.example.uiapp.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.getPlatform
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.theme.AppPalette
import com.example.uiapp.ui.components.icons.AccessibilityLabMenuIcon
import com.example.uiapp.ui.components.icons.ActivityInboxMenuIcon
import com.example.uiapp.ui.components.icons.AdaptiveMenuIcon
import com.example.uiapp.ui.components.icons.AdaptiveNavMenuIcon
import com.example.uiapp.ui.components.icons.AdvancedInputMenuIcon
import com.example.uiapp.ui.components.icons.AdvancedListMenuIcon
import com.example.uiapp.ui.components.icons.AiChatMenuIcon
import com.example.uiapp.ui.components.icons.AiVoiceOrbMenuIcon
import com.example.uiapp.ui.components.icons.StaggeredRevealMenuIcon
import com.example.uiapp.ui.components.icons.SuccessCheckMenuIcon
import com.example.uiapp.ui.components.icons.PillToolbarMenuIcon
import com.example.uiapp.ui.components.icons.SearchMorphMenuIcon
import com.example.uiapp.ui.components.icons.AmountKeypadMenuIcon
import com.example.uiapp.ui.components.icons.AuthMenuIcon
import com.example.uiapp.ui.components.icons.AutoBlurScrollMenuIcon
import com.example.uiapp.ui.components.icons.BalanceMaskingMenuIcon
import com.example.uiapp.ui.components.icons.BentoGridMenuIcon
import com.example.uiapp.ui.components.icons.BiometricMenuIcon
import com.example.uiapp.ui.components.icons.BottomSheetMenuIcon
import com.example.uiapp.ui.components.icons.CategoryScrollMenuIcon
import com.example.uiapp.ui.components.icons.ChartsMenuIcon
import com.example.uiapp.ui.components.icons.CommandPaletteMenuIcon
import com.example.uiapp.ui.components.icons.CommerceMenuIcon
import com.example.uiapp.ui.components.icons.ConfettiMenuIcon
import com.example.uiapp.ui.components.icons.CreateMediaMenuIcon
import com.example.uiapp.ui.components.icons.DataCardsMenuIcon
import com.example.uiapp.ui.components.icons.DataTableMenuIcon
import com.example.uiapp.ui.components.icons.DataVizMenuIcon
import com.example.uiapp.ui.components.icons.EmptyStateMenuIcon
import com.example.uiapp.ui.components.icons.ExpressiveControlsMenuIcon
import com.example.uiapp.ui.components.icons.FeedbackMenuIcon
import com.example.uiapp.ui.components.icons.FocusTimerMenuIcon
import com.example.uiapp.ui.components.icons.FormLabMenuIcon
import com.example.uiapp.ui.components.icons.GalleryMenuIcon
import com.example.uiapp.ui.components.icons.GamificationMenuIcon
import com.example.uiapp.ui.components.icons.HoldToConfirmMenuIcon
import com.example.uiapp.ui.components.icons.InfiniteScrollMenuIcon
import com.example.uiapp.ui.components.icons.MasonryGridMenuIcon
import com.example.uiapp.ui.components.icons.MediaMenuIcon
import com.example.uiapp.ui.components.icons.MorphingFabMenuIcon
import com.example.uiapp.ui.components.icons.MotionLabMenuIcon
import com.example.uiapp.ui.components.icons.MultiSelectMenuIcon
import com.example.uiapp.ui.components.icons.NativeSurfacesMenuIcon
import com.example.uiapp.ui.components.icons.NavLabMenuIcon
import com.example.uiapp.ui.components.icons.NavStructureMenuIcon
import com.example.uiapp.ui.components.icons.OnboardingMenuIcon
import com.example.uiapp.ui.components.icons.OverlayMenuIcon
import com.example.uiapp.ui.components.icons.ParallaxHeroMenuIcon
import com.example.uiapp.ui.components.icons.PasscodeMenuIcon
import com.example.uiapp.ui.components.icons.PerforatedTicketMenuIcon
import com.example.uiapp.ui.components.icons.PermissionsMenuIcon
import com.example.uiapp.ui.components.icons.PlaceholderMenuIcon
import com.example.uiapp.ui.components.icons.PrivacyMaskingMenuIcon
import com.example.uiapp.ui.components.icons.ProfileSetupMenuIcon
import com.example.uiapp.ui.components.icons.PullDismissMenuIcon
import com.example.uiapp.ui.components.icons.ReactionsMenuIcon
import com.example.uiapp.ui.components.icons.ResumeFormMenuIcon
import com.example.uiapp.ui.components.icons.ScratchCardMenuIcon
import com.example.uiapp.ui.components.icons.ScrollMotionMenuIcon
import com.example.uiapp.ui.components.icons.SearchMenuIcon
import com.example.uiapp.ui.components.icons.SelectionToolbarMenuIcon
import com.example.uiapp.ui.components.icons.SettingsMenuIcon
import com.example.uiapp.ui.components.icons.SharedElementMenuIcon
import com.example.uiapp.ui.components.icons.ShimmerMenuIcon
import com.example.uiapp.ui.components.icons.SlideToConfirmMenuIcon
import com.example.uiapp.ui.components.icons.SplashMenuIcon
import com.example.uiapp.ui.components.icons.SplitComparisonMenuIcon
import com.example.uiapp.ui.components.icons.StatsMenuIcon
import com.example.uiapp.ui.components.icons.StickyHeaderMenuIcon
import com.example.uiapp.ui.components.icons.StreakHeatmapMenuIcon
import com.example.uiapp.ui.components.icons.SwipeActionsMenuIcon
import com.example.uiapp.ui.components.icons.SwipeCardsMenuIcon
import com.example.uiapp.ui.components.icons.SystemPlatformMenuIcon
import com.example.uiapp.ui.components.icons.ThemeMenuIcon
import com.example.uiapp.ui.components.icons.TickerCounterMenuIcon
import com.example.uiapp.ui.components.icons.UndoQueueMenuIcon
import com.example.uiapp.ui.components.icons.ActivityRingsMenuIcon
import com.example.uiapp.ui.components.icons.AppstoreTodayMenuIcon
import com.example.uiapp.ui.components.icons.CallUiMenuIcon
import com.example.uiapp.ui.components.icons.ClockKitMenuIcon
import com.example.uiapp.ui.components.icons.ContinuityMenuIcon
import com.example.uiapp.ui.components.icons.ControlCenterMenuIcon
import com.example.uiapp.ui.components.icons.FocusModesMenuIcon
import com.example.uiapp.ui.components.icons.FreeformCanvasMenuIcon
import com.example.uiapp.ui.components.icons.GenmojiMenuIcon
import com.example.uiapp.ui.components.icons.HealthAppMenuIcon
import com.example.uiapp.ui.components.icons.HomeAppMenuIcon
import com.example.uiapp.ui.components.icons.KeyboardAccessoryMenuIcon
import com.example.uiapp.ui.components.icons.LiveActivityMenuIcon
import com.example.uiapp.ui.components.icons.LiveCaptionsMenuIcon
import com.example.uiapp.ui.components.icons.LockScreenMenuIcon
import com.example.uiapp.ui.components.icons.MsgEffectsMenuIcon
import com.example.uiapp.ui.components.icons.NotificationBannerMenuIcon
import com.example.uiapp.ui.components.icons.PageCurlMenuIcon
import com.example.uiapp.ui.components.icons.PasskeysPasswordsMenuIcon
import com.example.uiapp.ui.components.icons.PhotosZoomMenuIcon
import com.example.uiapp.ui.components.icons.PointerScribbleMenuIcon
import com.example.uiapp.ui.components.icons.PrecisionFinderMenuIcon
import com.example.uiapp.ui.components.icons.RulerPickerMenuIcon
import com.example.uiapp.ui.components.icons.ScreenTimeMenuIcon
import com.example.uiapp.ui.components.icons.SearchTokensMenuIcon
import com.example.uiapp.ui.components.icons.ShareSheetMenuIcon
import com.example.uiapp.ui.components.icons.ShortcutsLabMenuIcon
import com.example.uiapp.ui.components.icons.StandbyModeMenuIcon
import com.example.uiapp.ui.components.icons.StocksAppMenuIcon
import com.example.uiapp.ui.components.icons.TextEditMenuIcon
import com.example.uiapp.ui.components.icons.TranslateAppMenuIcon
import com.example.uiapp.ui.components.icons.VoiceoverLabMenuIcon
import com.example.uiapp.ui.components.icons.WalletStackMenuIcon
import com.example.uiapp.ui.components.icons.WeatherAppMenuIcon
import com.example.uiapp.ui.components.icons.WidgetGalleryMenuIcon
import com.example.uiapp.ui.components.icons.WritingToolsMenuIcon
import com.example.uiapp.ui.components.icons.AudioEqualizerMenuIcon
import com.example.uiapp.ui.components.icons.BlockEditorMenuIcon
import com.example.uiapp.ui.components.icons.FlightItineraryMenuIcon
import com.example.uiapp.ui.components.icons.GanttRoadmapMenuIcon
import com.example.uiapp.ui.components.icons.PhotoCropperMenuIcon
import com.example.uiapp.ui.components.icons.SeatSelectorMenuIcon
import com.example.uiapp.ui.components.icons.SplitBillMenuIcon
import com.example.uiapp.ui.components.icons.FloatingDockMenuIcon
import com.example.uiapp.ui.components.icons.SyncedLyricsMenuIcon
import com.example.uiapp.ui.components.icons.TreeExplorerMenuIcon
import com.example.uiapp.ui.components.icons.TripTrackerMenuIcon
import com.example.uiapp.ui.components.icons.BudgetEnvelopesMenuIcon
import com.example.uiapp.ui.components.icons.QuestPathMenuIcon
import com.example.uiapp.ui.components.icons.SentenceBuilderMenuIcon
import com.example.uiapp.ui.components.icons.PredictiveBackMenuIcon
import com.example.uiapp.ui.components.icons.AiInlineEditMenuIcon
import com.example.uiapp.ui.components.icons.DesignTokensMenuIcon
import com.example.uiapp.ui.components.icons.PerfhudMenuIcon
import com.example.uiapp.ui.components.icons.AppointmentSlotsMenuIcon
import com.example.uiapp.ui.components.icons.FlowVizMenuIcon
import com.example.uiapp.ui.components.icons.GraphExplorerMenuIcon
import com.example.uiapp.ui.components.icons.StoreLocatorMenuIcon
import com.example.uiapp.ui.components.icons.PaymentFormMenuIcon
import com.example.uiapp.ui.components.icons.AddressInputMenuIcon
import com.example.uiapp.ui.components.icons.RecurrencePickerMenuIcon
import com.example.uiapp.ui.components.icons.LiveAuctionMenuIcon
import com.example.uiapp.ui.components.icons.FlashSaleMenuIcon
import com.example.uiapp.ui.components.icons.SubscriptionMgmtMenuIcon
import com.example.uiapp.ui.components.icons.QueueTicketMenuIcon
import com.example.uiapp.ui.components.icons.PostComposerMenuIcon
import com.example.uiapp.ui.components.icons.StoryCreatorMenuIcon
import com.example.uiapp.ui.components.icons.DocScannerMenuIcon
import com.example.uiapp.ui.components.icons.PdfViewerMenuIcon
import com.example.uiapp.ui.components.icons.CodeEditorMenuIcon
import com.example.uiapp.ui.components.icons.VersionHistoryMenuIcon
import com.example.uiapp.ui.components.icons.RtlPreviewMenuIcon
import com.example.uiapp.ui.components.icons.A11yAuditMenuIcon
import com.example.uiapp.ui.components.icons.GettingStartedMenuIcon
import com.example.uiapp.ui.components.icons.WhatsNewMenuIcon
import com.example.uiapp.ui.components.icons.ReferralInviteMenuIcon
import com.example.uiapp.ui.components.icons.NpsSurveyMenuIcon
import com.example.uiapp.ui.components.icons.ReportFlowMenuIcon
import com.example.uiapp.ui.components.icons.SecureVaultMenuIcon
import com.example.uiapp.ui.components.icons.MedicationPlanMenuIcon
import com.example.uiapp.ui.components.icons.NutritionDiaryMenuIcon
import com.example.uiapp.ui.components.icons.SleepStagesMenuIcon
import com.example.uiapp.ui.components.icons.QrisPayMenuIcon
import com.example.uiapp.ui.components.icons.VirtualCardMenuIcon
import com.example.uiapp.ui.components.icons.CurrencyExchangeMenuIcon
import com.example.uiapp.ui.components.icons.LoanSimulatorMenuIcon
import com.example.uiapp.ui.components.icons.DragDropMenuIcon
import com.example.uiapp.ui.components.icons.CollabPresenceMenuIcon
import com.example.uiapp.ui.components.icons.LiveDashboardMenuIcon
import com.example.uiapp.ui.components.icons.OfflineResilienceMenuIcon
import com.example.uiapp.ui.components.icons.AiAgentStepsMenuIcon
import kotlinx.coroutines.launch

enum class HomeSection(
    val title: String,
    val shortLabel: String,
) {
    ALL("Semua Lab", "Semua (166)"),
    CORE("1. Labs Eksperimen Inti", "Inti (19)"),
    AUTH("2. Onboarding & Auth Flow", "Auth (7)"),
    ADVANCED("3. Komponen Lanjutan Baru", "Lanjutan (9)"),
    WORLD_CLASS("4. Komponen World-Class UI", "World-Class (12)"),
    TRENDING("5. Trending Mobile UI 2025/2026", "Trending (10)"),
    MODERN("6. Ide Komponen UI Modern 2026", "Modern UI (9)"),
    PRO("7. Komponen Modern & Profesional", "Pro (5)"),
    NEW_LABS("8. Gamifikasi, Viz & Media", "Baru (4)"),
    IOS_PATTERN("9. Pola Khas iOS Modern", "iOS (16)"),
    IOS_APPS("10. Ide Lab Khas iOS Lanjutan", "iOS+ (20)"),
    INDUSTRY("11. Komponen Industri & Produktivitas", "Industri (15)"),
    IDEA2026("12. Ide Komponen Baru", "Idea 2026 (40)"),
}

private val HomeSectionOrder = listOf(
    HomeSection.CORE,
    HomeSection.AUTH,
    HomeSection.ADVANCED,
    HomeSection.WORLD_CLASS,
    HomeSection.TRENDING,
    HomeSection.MODERN,
    HomeSection.PRO,
    HomeSection.NEW_LABS,
    HomeSection.IOS_PATTERN,
    HomeSection.IOS_APPS,
    HomeSection.INDUSTRY,
    HomeSection.IDEA2026,
)

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
    MenuEntry("advanced_input", "Input Lanjutan", "Date Picker iOS, Slider, Tag, Signature", true, HomeSection.ADVANCED),
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

    // 5. Trending Mobile UI 2025/2026 (10)
    MenuEntry("hold_to_confirm", "Hold to Confirm", "Long-press progress & anti-mistake action", true, HomeSection.TRENDING),
    MenuEntry("slide_to_confirm", "Slide to Confirm", "Draggable pill & track lock snap", true, HomeSection.TRENDING),
    MenuEntry("split_comparison", "Split Comparison", "Before & after interactive divider slider", true, HomeSection.TRENDING),
    MenuEntry("bento_grid", "Bento Grid Dashboard", "Apple & SaaS modular interactive cards", true, HomeSection.TRENDING),
    MenuEntry("perforated_ticket", "Perforated Ticket", "Boarding pass, cutouts & tear perforation", true, HomeSection.TRENDING),
    MenuEntry("ai_voice_orb", "AI Voice Orb", "ChatGPT Voice & Siri fluid organic intelligence", true, HomeSection.TRENDING),
    MenuEntry("staggered_reveal", "Staggered Reveal", "Kartu muncul berurutan dengan delay bertingkat", true, HomeSection.TRENDING),
    MenuEntry("success_check", "Success Check", "Centang sukses menggambar diri lalu berpindah", true, HomeSection.TRENDING),
    MenuEntry("pill_toolbar", "Pill Toolbar", "Toolbar pill melayang dengan aksi melebar", true, HomeSection.TRENDING),
    MenuEntry("search_morph", "Search Morph", "Ikon cari berubah wujud jadi kolom pencarian", true, HomeSection.TRENDING),

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

    // 9. Pola Khas iOS Modern (16)
    MenuEntry("share_sheet", "Share Sheet & Activity View", "Sheet berbagi: AirDrop, app tujuan, aksi", true, HomeSection.IOS_PATTERN),
    MenuEntry("control_center", "Control Center & Edit Mode", "Grid kontrol, jiggle, drag & resize", true, HomeSection.IOS_PATTERN),
    MenuEntry("activity_rings", "Activity Rings", "Tiga cincin aktivitas & ringkasan mingguan", true, HomeSection.IOS_PATTERN),
    MenuEntry("lock_screen", "Lock Screen Customization", "Jam, slot widget, & wallpaper kunci", true, HomeSection.IOS_PATTERN),
    MenuEntry("notification_banner", "In-App Notification Banner", "Banner masuk, expand, & antrean", true, HomeSection.IOS_PATTERN),
    MenuEntry("ruler_picker", "Ruler Picker Pengukuran", "Tick, snapping, satuan cm & ft-in", true, HomeSection.IOS_PATTERN),
    MenuEntry("search_tokens", "Search Token Field", "Token kunci-nilai di dalam field", true, HomeSection.IOS_PATTERN),
    MenuEntry("photos_zoom", "Photos Zoom Tahun-Hari", "Pinch antar tingkat agregasi foto", true, HomeSection.IOS_PATTERN),
    MenuEntry("keyboard_accessory", "Keyboard Accessory & Keypad", "Toolbar di atas keyboard & kalkulator", true, HomeSection.IOS_PATTERN),
    MenuEntry("widget_gallery", "Widget Gallery & Sizes", "Widget small-large & varian tampilan", true, HomeSection.IOS_PATTERN),
    MenuEntry("edit_menu", "Text Loupe & Edit Menu", "Kaca pembesar, caret, & menu teks", true, HomeSection.IOS_PATTERN),
    MenuEntry("live_activity", "Live Activity Lifecycle", "Compact, minimal, & expanded", true, HomeSection.IOS_PATTERN),
    MenuEntry("precision_finder", "Precision Finding", "Panah arah, cincin jarak, & haptic", true, HomeSection.IOS_PATTERN),
    MenuEntry("live_captions", "Live Captions & Transkripsi", "Transkrip bergulir & diarisasi", true, HomeSection.IOS_PATTERN),
    MenuEntry("page_curl", "Page Curl Reader", "Balik halaman dengan deformasi", true, HomeSection.IOS_PATTERN),
    MenuEntry("standby_mode", "StandBy Nightstand", "Jam besar lanskap & mode malam", true, HomeSection.IOS_PATTERN),

    // 10. Ide Lab Khas iOS Lanjutan (20)
    MenuEntry("msg_effects", "Messages Bubble Effects", "Slam, loud, confetti, invisible ink", true, HomeSection.IOS_APPS),
    MenuEntry("call_ui", "Incoming Call & FaceTime", "Panggilan masuk & grid peserta", true, HomeSection.IOS_APPS),
    MenuEntry("weather_app", "Apple Weather", "Grafik jam, 10 hari, kartu modul", true, HomeSection.IOS_APPS),
    MenuEntry("stocks_app", "Apple Stocks", "Watchlist, chart crosshair, berita", true, HomeSection.IOS_APPS),
    MenuEntry("health_app", "Health Summary & Trends", "Dashboard kesehatan & tren", true, HomeSection.IOS_APPS),
    MenuEntry("clock_kit", "Clock Kit", "World clock, alarm, stopwatch, timer", true, HomeSection.IOS_APPS),
    MenuEntry("shortcuts_lab", "Shortcuts & Automation", "Galeri aksi, drag reorder, trigger", true, HomeSection.IOS_APPS),
    MenuEntry("home_app", "Home & Scenes", "Tile aksesori, ruangan, & scene", true, HomeSection.IOS_APPS),
    MenuEntry("wallet_stack", "Wallet Pass Stack", "Kartu berkipas & drag through", true, HomeSection.IOS_APPS),
    MenuEntry("appstore_today", "App Store Today", "Kartu editorial & pager screenshot", true, HomeSection.IOS_APPS),
    MenuEntry("translate_app", "Translate", "Percakapan, side-by-side, kamera", true, HomeSection.IOS_APPS),
    MenuEntry("freeform_canvas", "Freeform Infinite Canvas", "Sticky note, konektor, pan/zoom", true, HomeSection.IOS_APPS),
    MenuEntry("writing_tools", "Writing Tools", "Rewrite, proofread, summarize", true, HomeSection.IOS_APPS),
    MenuEntry("genmoji", "Genmoji & Image Playground", "Emoji buatan dari perintah", true, HomeSection.IOS_APPS),
    MenuEntry("focus_modes", "Focus Modes & Filtering", "DND, filter notifikasi, & aktivitas", true, HomeSection.IOS_APPS),
    MenuEntry("passkeys_passwords", "Passkeys & Passwords", "Face ID, kode verifikasi, & audit", true, HomeSection.IOS_APPS),
    MenuEntry("voiceover_lab", "VoiceOver Simulator", "Rotor, focus ring, & hint", true, HomeSection.IOS_APPS),
    MenuEntry("continuity", "Handoff & AirPlay Picker", "Saran beranda & output audio", true, HomeSection.IOS_APPS),
    MenuEntry("screen_time", "Screen Time & App Limits", "Waktu pakai, batas app, & downtime", true, HomeSection.IOS_APPS),
    MenuEntry("pointer_scribble", "iPad Pointer & Scribble", "Kursor trackpad & coretan Pencil", true, HomeSection.IOS_APPS),

    // 11. Komponen Industri, Spasial & Produktivitas (10)
    MenuEntry("seat_selector", "Seat Selector", "Denah kursi pesawat & bioskop interaktif", true, HomeSection.INDUSTRY),
    MenuEntry("trip_tracker", "Trip Tracker", "Pelacakan perjalanan live & rute", true, HomeSection.INDUSTRY),
    MenuEntry("flight_itinerary", "Flight Itinerary", "Rencana multi-kota & transit", true, HomeSection.INDUSTRY),
    MenuEntry("synced_lyrics", "Synced Lyrics", "Lirik karaoke tersinkron & scrubbing", true, HomeSection.INDUSTRY),
    MenuEntry("audio_equalizer", "Audio Equalizer", "Ekualiser 10-band & visualizer", true, HomeSection.INDUSTRY),
    MenuEntry("photo_cropper", "Photo Cropper", "Crop, putar & grid rule-of-thirds", true, HomeSection.INDUSTRY),
    MenuEntry("block_editor", "Block Editor", "Editor dokumen blok ala Notion", true, HomeSection.INDUSTRY),
    MenuEntry("gantt_roadmap", "Gantt Roadmap", "Linimasa proyek & ketergantungan", true, HomeSection.INDUSTRY),
    MenuEntry("tree_explorer", "Tree Explorer", "Pohon berkas & folder hierarkis", true, HomeSection.INDUSTRY),
    MenuEntry("split_bill", "Split Bill", "Bagi tagihan per item & per orang", true, HomeSection.INDUSTRY),
    MenuEntry("floating_dock", "Floating Dock", "Tab bar mengecil jadi pill saat scroll", true, HomeSection.INDUSTRY),
    MenuEntry("budget_envelopes", "Budget Envelopes", "Alokasi amplop anggaran & geser uang", true, HomeSection.INDUSTRY),
    MenuEntry("quest_path", "Quest Path", "Peta level permainan berkelok & progres", true, HomeSection.INDUSTRY),
    MenuEntry("sentence_builder", "Sentence Builder", "Rakit kalimat dari ubin kata", true, HomeSection.INDUSTRY),
    MenuEntry("predictive_back", "Predictive Back", "Simulator back gesture & animasi pratinjau", true, HomeSection.INDUSTRY),

    // 12. Ide Komponen Baru (40)
    MenuEntry("ai_inline_edit", "Ai Inline Edit", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("design_tokens", "Design Tokens", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("perfhud", "Perfhud", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("appointment_slots", "Appointment Slots", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("flow_viz", "Flow Viz", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("graph_explorer", "Graph Explorer", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("store_locator", "Store Locator", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("payment_form", "Payment Form", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("address_input", "Address Input", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("recurrence_picker", "Recurrence Picker", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("live_auction", "Live Auction", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("flash_sale", "Flash Sale", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("subscription_mgmt", "Subscription Mgmt", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("queue_ticket", "Queue Ticket", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("post_composer", "Post Composer", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("story_creator", "Story Creator", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("doc_scanner", "Doc Scanner", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("pdf_viewer", "Pdf Viewer", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("code_editor", "Code Editor", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("version_history", "Version History", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("rtl_preview", "Rtl Preview", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("a11y_audit", "A11y Audit", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("getting_started", "Getting Started", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("whats_new", "Whats New", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("referral_invite", "Referral Invite", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("nps_survey", "Nps Survey", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("report_flow", "Report Flow", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("secure_vault", "Secure Vault", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("medication_plan", "Medication Plan", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("nutrition_diary", "Nutrition Diary", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("sleep_stages", "Sleep Stages", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("qris_pay", "Qris Pay", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("virtual_card", "Virtual Card", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("currency_exchange", "Currency Exchange", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("loan_simulator", "Loan Simulator", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("drag_drop", "Drag Drop", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("collab_presence", "Collab Presence", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("live_dashboard", "Live Dashboard", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("offline_resilience", "Offline Resilience", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
    MenuEntry("ai_agent_steps", "Ai Agent Steps", "Lab komponen ide 2026", true, HomeSection.IDEA2026),
)

@Composable
fun HomeMenuScreen(
    onMenuClick: (String) -> Unit,
    gridState: LazyGridState = rememberLazyGridState(),
    selectedSection: HomeSection = HomeSection.ALL,
    onSectionSelected: (HomeSection) -> Unit = {},
    lastTestedRoute: String? = null,
    recentRouteIds: List<String> = emptyList(),
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
) {
    val platform = remember { getPlatform().name }
    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()
    val fontScale = LocalDensity.current.fontScale
    val normalizedQuery = searchQuery.trim().lowercase()
    val displayedEntries = remember(selectedSection, normalizedQuery) {
        MenuEntries.filter { entry ->
            (selectedSection == HomeSection.ALL || entry.section == selectedSection) &&
                (normalizedQuery.isEmpty() || entry.title.lowercase().contains(normalizedQuery) ||
                    entry.subtitle.lowercase().contains(normalizedQuery) || entry.id.contains(normalizedQuery))
        }
    }

    val recentEntries = remember(recentRouteIds) {
        recentRouteIds.mapNotNull { route -> MenuEntries.firstOrNull { it.id == route } }
    }
    val entriesBySection = remember {
        HomeSectionOrder.associateWith { section -> MenuEntries.filter { it.section == section } }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val cardAspectRatio = when {
            fontScale >= 1.5f && maxWidth < 480.dp -> 0.82f
            fontScale >= 1.5f && maxWidth < 760.dp -> 1.05f
            fontScale >= 1.5f -> 1.2f
            fontScale >= 1.2f && maxWidth < 480.dp -> 0.9f
            fontScale >= 1.2f && maxWidth < 760.dp -> 1.08f
            fontScale >= 1.2f -> 1.28f
            maxWidth >= 760.dp -> 1.35f
            maxWidth >= 480.dp -> 1.15f
            else -> 1f
        }
        Scaffold(containerColor = palette.background) { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = gridState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
            // Header
            item(span = { GridItemSpan(maxLineSpan) }) {
                HomeHeader(platform, palette, MenuEntries.size)
            }

            item(span = { GridItemSpan(maxLineSpan) }, key = "catalog_search") {
                HomeSearchField(
                    value = searchQuery,
                    palette = palette,
                    onValueChange = onSearchQueryChange,
                    onClear = { onSearchQueryChange("") },
                )
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

            if (selectedSection == HomeSection.ALL && normalizedQuery.isEmpty() && recentEntries.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }, key = "recent_labs") {
                    RecentLabsRow(entries = recentEntries, palette = palette, onMenuClick = onMenuClick)
                }
            }

            // Content Items: All sections with headers vs Single section
            if (displayedEntries.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }, key = "empty_search") {
                    EmptyCatalogResults(
                        query = searchQuery,
                        palette = palette,
                        onClear = { onSearchQueryChange(""); onSectionSelected(HomeSection.ALL) },
                    )
                }
            } else if (normalizedQuery.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }, key = "search_result_header") {
                    SectionHeader(title = "Hasil pencarian", count = displayedEntries.size, palette = palette)
                }
                items(displayedEntries, key = { it.id }) { entry ->
                    MenuGridCard(
                        entry = entry,
                        palette = palette,
                        isLastTested = entry.id == lastTestedRoute,
                        aspectRatio = cardAspectRatio,
                        onClick = { onMenuClick(entry.id) },
                    )
                }
            } else if (selectedSection == HomeSection.ALL) {
                HomeSectionOrder.forEach { sec ->
                    val secEntries = entriesBySection[sec].orEmpty()
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
                                aspectRatio = cardAspectRatio,
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
                        aspectRatio = cardAspectRatio,
                        onClick = { onMenuClick(entry.id) },
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun HomeSearchField(
    value: String,
    palette: AppPalette,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Cari lab") },
        placeholder = { Text("Nama komponen atau fitur") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
        trailingIcon = if (value.isNotEmpty()) {
            {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.semantics { contentDescription = "Hapus pencarian" },
                ) {
                    Canvas(modifier = Modifier.size(20.dp)) { drawClearMark(palette.textSecondary) }
                }
            }
        } else {
            null
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = palette.primary,
            unfocusedBorderColor = palette.border,
            focusedLabelColor = palette.primary,
            unfocusedLabelColor = palette.textSecondary,
            cursorColor = palette.primary,
            focusedTextColor = palette.textPrimary,
            unfocusedTextColor = palette.textPrimary,
            focusedContainerColor = palette.surface,
            unfocusedContainerColor = palette.surface,
        ),
    )
}

private fun DrawScope.drawClearMark(color: androidx.compose.ui.graphics.Color) {
    val path = Path().apply {
        moveTo(size.width * 0.25f, size.height * 0.25f)
        lineTo(size.width * 0.75f, size.height * 0.75f)
        moveTo(size.width * 0.75f, size.height * 0.25f)
        lineTo(size.width * 0.25f, size.height * 0.75f)
    }
    drawPath(path, color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
}

@Composable
private fun RecentLabsRow(
    entries: List<MenuEntry>,
    palette: AppPalette,
    onMenuClick: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Baru dibuka", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(entries, key = { "recent_${it.id}" }) { entry ->
                Surface(
                    onClick = { onMenuClick(entry.id) },
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Text(
                        text = entry.title,
                        modifier = Modifier.heightIn(min = 48.dp).padding(horizontal = 14.dp, vertical = 15.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = palette.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCatalogResults(query: String, palette: AppPalette, onClear: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Lab tidak ditemukan", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
        Text(
            text = "Tidak ada hasil untuk “${query.trim()}”. Coba kata kunci lain atau tampilkan semua lab.",
            fontSize = 13.sp,
            color = palette.textSecondary,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        androidx.compose.material3.TextButton(onClick = onClear) { Text("Tampilkan semua lab") }
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
        items(HomeSection.entries, key = { it.name }) { section ->
            val isSelected = selectedSection == section
            Surface(
                onClick = { onSectionSelected(section) },
                modifier = Modifier.heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) palette.primary else palette.surface,
                border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 15.dp),
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
            TextButton(onClick = onClearFilter) {
                Text("Tampilkan Semua", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.primary)
            }
        } else if (onQuickFilter != null) {
            TextButton(onClick = onQuickFilter) {
                Text("Fokus Section", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = palette.primary)
            }
        }
    }
}

@Composable
private fun MenuGridCard(
    entry: MenuEntry,
    palette: com.example.uiapp.theme.AppPalette,
    isLastTested: Boolean = false,
    aspectRatio: Float = 1f,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        enabled = entry.enabled,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
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
                        "staggered_reveal" -> StaggeredRevealMenuIcon(tint = palette.textPrimary)
                        "success_check" -> SuccessCheckMenuIcon(tint = palette.textPrimary)
                        "pill_toolbar" -> PillToolbarMenuIcon(tint = palette.textPrimary)
                        "search_morph" -> SearchMorphMenuIcon(tint = palette.textPrimary)
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
                        "share_sheet" -> ShareSheetMenuIcon(tint = palette.textPrimary)
                        "control_center" -> ControlCenterMenuIcon(tint = palette.textPrimary)
                        "activity_rings" -> ActivityRingsMenuIcon(tint = palette.textPrimary)
                        "lock_screen" -> LockScreenMenuIcon(tint = palette.textPrimary)
                        "notification_banner" -> NotificationBannerMenuIcon(tint = palette.textPrimary)
                        "ruler_picker" -> RulerPickerMenuIcon(tint = palette.textPrimary)
                        "search_tokens" -> SearchTokensMenuIcon(tint = palette.textPrimary)
                        "photos_zoom" -> PhotosZoomMenuIcon(tint = palette.textPrimary)
                        "keyboard_accessory" -> KeyboardAccessoryMenuIcon(tint = palette.textPrimary)
                        "widget_gallery" -> WidgetGalleryMenuIcon(tint = palette.textPrimary)
                        "edit_menu" -> TextEditMenuIcon(tint = palette.textPrimary)
                        "live_activity" -> LiveActivityMenuIcon(tint = palette.textPrimary)
                        "precision_finder" -> PrecisionFinderMenuIcon(tint = palette.textPrimary)
                        "live_captions" -> LiveCaptionsMenuIcon(tint = palette.textPrimary)
                        "page_curl" -> PageCurlMenuIcon(tint = palette.textPrimary)
                        "standby_mode" -> StandbyModeMenuIcon(tint = palette.textPrimary)
                        "msg_effects" -> MsgEffectsMenuIcon(tint = palette.textPrimary)
                        "call_ui" -> CallUiMenuIcon(tint = palette.textPrimary)
                        "weather_app" -> WeatherAppMenuIcon(tint = palette.textPrimary)
                        "stocks_app" -> StocksAppMenuIcon(tint = palette.textPrimary)
                        "health_app" -> HealthAppMenuIcon(tint = palette.textPrimary)
                        "clock_kit" -> ClockKitMenuIcon(tint = palette.textPrimary)
                        "shortcuts_lab" -> ShortcutsLabMenuIcon(tint = palette.textPrimary)
                        "home_app" -> HomeAppMenuIcon(tint = palette.textPrimary)
                        "wallet_stack" -> WalletStackMenuIcon(tint = palette.textPrimary)
                        "appstore_today" -> AppstoreTodayMenuIcon(tint = palette.textPrimary)
                        "translate_app" -> TranslateAppMenuIcon(tint = palette.textPrimary)
                        "freeform_canvas" -> FreeformCanvasMenuIcon(tint = palette.textPrimary)
                        "writing_tools" -> WritingToolsMenuIcon(tint = palette.textPrimary)
                        "genmoji" -> GenmojiMenuIcon(tint = palette.textPrimary)
                        "focus_modes" -> FocusModesMenuIcon(tint = palette.textPrimary)
                        "passkeys_passwords" -> PasskeysPasswordsMenuIcon(tint = palette.textPrimary)
                        "voiceover_lab" -> VoiceoverLabMenuIcon(tint = palette.textPrimary)
                        "continuity" -> ContinuityMenuIcon(tint = palette.textPrimary)
                        "screen_time" -> ScreenTimeMenuIcon(tint = palette.textPrimary)
                        "pointer_scribble" -> PointerScribbleMenuIcon(tint = palette.textPrimary)
                        "seat_selector" -> SeatSelectorMenuIcon(tint = palette.textPrimary)
                        "trip_tracker" -> TripTrackerMenuIcon(tint = palette.textPrimary)
                        "flight_itinerary" -> FlightItineraryMenuIcon(tint = palette.textPrimary)
                        "synced_lyrics" -> SyncedLyricsMenuIcon(tint = palette.textPrimary)
                        "audio_equalizer" -> AudioEqualizerMenuIcon(tint = palette.textPrimary)
                        "photo_cropper" -> PhotoCropperMenuIcon(tint = palette.textPrimary)
                        "block_editor" -> BlockEditorMenuIcon(tint = palette.textPrimary)
                        "gantt_roadmap" -> GanttRoadmapMenuIcon(tint = palette.textPrimary)
                        "tree_explorer" -> TreeExplorerMenuIcon(tint = palette.textPrimary)
                        "split_bill" -> SplitBillMenuIcon(tint = palette.textPrimary)
                        "floating_dock" -> FloatingDockMenuIcon(tint = palette.textPrimary)
                        "budget_envelopes" -> BudgetEnvelopesMenuIcon(tint = palette.textPrimary)
                        "quest_path" -> QuestPathMenuIcon(tint = palette.textPrimary)
                        "sentence_builder" -> SentenceBuilderMenuIcon(tint = palette.textPrimary)
                        "predictive_back" -> PredictiveBackMenuIcon(tint = palette.textPrimary)
                        "ai_inline_edit" -> AiInlineEditMenuIcon(tint = palette.textPrimary)
                        "design_tokens" -> DesignTokensMenuIcon(tint = palette.textPrimary)
                        "perfhud" -> PerfhudMenuIcon(tint = palette.textPrimary)
                        "appointment_slots" -> AppointmentSlotsMenuIcon(tint = palette.textPrimary)
                        "flow_viz" -> FlowVizMenuIcon(tint = palette.textPrimary)
                        "graph_explorer" -> GraphExplorerMenuIcon(tint = palette.textPrimary)
                        "store_locator" -> StoreLocatorMenuIcon(tint = palette.textPrimary)
                        "payment_form" -> PaymentFormMenuIcon(tint = palette.textPrimary)
                        "address_input" -> AddressInputMenuIcon(tint = palette.textPrimary)
                        "recurrence_picker" -> RecurrencePickerMenuIcon(tint = palette.textPrimary)
                        "live_auction" -> LiveAuctionMenuIcon(tint = palette.textPrimary)
                        "flash_sale" -> FlashSaleMenuIcon(tint = palette.textPrimary)
                        "subscription_mgmt" -> SubscriptionMgmtMenuIcon(tint = palette.textPrimary)
                        "queue_ticket" -> QueueTicketMenuIcon(tint = palette.textPrimary)
                        "post_composer" -> PostComposerMenuIcon(tint = palette.textPrimary)
                        "story_creator" -> StoryCreatorMenuIcon(tint = palette.textPrimary)
                        "doc_scanner" -> DocScannerMenuIcon(tint = palette.textPrimary)
                        "pdf_viewer" -> PdfViewerMenuIcon(tint = palette.textPrimary)
                        "code_editor" -> CodeEditorMenuIcon(tint = palette.textPrimary)
                        "version_history" -> VersionHistoryMenuIcon(tint = palette.textPrimary)
                        "rtl_preview" -> RtlPreviewMenuIcon(tint = palette.textPrimary)
                        "a11y_audit" -> A11yAuditMenuIcon(tint = palette.textPrimary)
                        "getting_started" -> GettingStartedMenuIcon(tint = palette.textPrimary)
                        "whats_new" -> WhatsNewMenuIcon(tint = palette.textPrimary)
                        "referral_invite" -> ReferralInviteMenuIcon(tint = palette.textPrimary)
                        "nps_survey" -> NpsSurveyMenuIcon(tint = palette.textPrimary)
                        "report_flow" -> ReportFlowMenuIcon(tint = palette.textPrimary)
                        "secure_vault" -> SecureVaultMenuIcon(tint = palette.textPrimary)
                        "medication_plan" -> MedicationPlanMenuIcon(tint = palette.textPrimary)
                        "nutrition_diary" -> NutritionDiaryMenuIcon(tint = palette.textPrimary)
                        "sleep_stages" -> SleepStagesMenuIcon(tint = palette.textPrimary)
                        "qris_pay" -> QrisPayMenuIcon(tint = palette.textPrimary)
                        "virtual_card" -> VirtualCardMenuIcon(tint = palette.textPrimary)
                        "currency_exchange" -> CurrencyExchangeMenuIcon(tint = palette.textPrimary)
                        "loan_simulator" -> LoanSimulatorMenuIcon(tint = palette.textPrimary)
                        "drag_drop" -> DragDropMenuIcon(tint = palette.textPrimary)
                        "collab_presence" -> CollabPresenceMenuIcon(tint = palette.textPrimary)
                        "live_dashboard" -> LiveDashboardMenuIcon(tint = palette.textPrimary)
                        "offline_resilience" -> OfflineResilienceMenuIcon(tint = palette.textPrimary)
                        "ai_agent_steps" -> AiAgentStepsMenuIcon(tint = palette.textPrimary)
                        else -> PlaceholderMenuIcon(tint = palette.textPrimary)
                    }
                }

                if (isLastTested) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(palette.primary)
                            .padding(horizontal = 7.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = "TERAKHIR",
                            fontSize = 10.sp,
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
                            fontSize = 10.sp,
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
