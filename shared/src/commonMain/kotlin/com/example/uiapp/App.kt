package com.example.uiapp

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.tooling.preview.Preview
import com.example.uiapp.navigation.LocalNavController
import com.example.uiapp.navigation.rememberAppNavController
import com.example.uiapp.theme.UiAppTheme
import com.example.uiapp.ui.home.HomeSection
import com.example.uiapp.ui.adaptive.AdaptiveScreen
import com.example.uiapp.ui.advancedinput.AdvancedInputLabScreen
import com.example.uiapp.ui.advancedlist.AdvancedListLabScreen
import com.example.uiapp.ui.auth.AuthScreen
import com.example.uiapp.ui.biometric.BiometricScreen
import com.example.uiapp.ui.blurscroll.AutoBlurScrollScreen
import com.example.uiapp.ui.bottomsheet.BottomSheetScreen
import com.example.uiapp.ui.charts.ChartsLabScreen
import com.example.uiapp.ui.commandpalette.CommandPaletteLabScreen
import com.example.uiapp.ui.commerce.CommerceLabScreen
import com.example.uiapp.ui.datacards.DataCardsLabScreen
import com.example.uiapp.ui.emptystate.EmptyStateScreen
import com.example.uiapp.ui.feedback.FeedbackLabScreen
import com.example.uiapp.ui.formlab.FormLabScreen
import com.example.uiapp.ui.gallery.GalleryScreen
import com.example.uiapp.ui.home.HomeMenuScreen
import com.example.uiapp.ui.infinitescroll.InfiniteScrollScreen
import com.example.uiapp.ui.media.MediaLabScreen
import com.example.uiapp.ui.motionlab.MotionLabScreen
import com.example.uiapp.ui.multiselect.MultiSelectScreen
import com.example.uiapp.ui.navlab.NavLabScreen
import com.example.uiapp.ui.navstructure.NavStructureLabScreen
import com.example.uiapp.ui.onboarding.OnboardingScreen
import com.example.uiapp.ui.overlay.OverlayLabScreen
import com.example.uiapp.ui.parallaxhero.ParallaxHeroScreen
import com.example.uiapp.ui.passcode.PasscodeScreen
import com.example.uiapp.ui.permissions.PermissionsScreen
import com.example.uiapp.ui.profilesetup.ProfileSetupScreen
import com.example.uiapp.ui.pulldismiss.PullDownDismissLabScreen
import com.example.uiapp.ui.search.SearchAutocompleteScreen
import com.example.uiapp.ui.settings.SettingsLabScreen
import com.example.uiapp.ui.sharedelement.SharedElementScreen
import com.example.uiapp.ui.shimmer.ShimmerSkeletonScreen
import com.example.uiapp.ui.splash.SplashScreen
import com.example.uiapp.ui.stats.StatsLabScreen
import com.example.uiapp.ui.stickyheader.StickyHeaderScreen
import com.example.uiapp.ui.systemplatform.SystemPlatformLabScreen
import com.example.uiapp.ui.themelab.ThemeLabScreen
import com.example.uiapp.ui.tickercounter.TickerCounterLabScreen
import com.example.uiapp.ui.balancemasking.BalanceMaskingLabScreen
import com.example.uiapp.ui.confetti.ConfettiParticlesLabScreen
import com.example.uiapp.ui.morphingfab.MorphingFabLabScreen
import com.example.uiapp.ui.privacymasking.PrivacyMaskingLabScreen
import com.example.uiapp.ui.reactions.ReactionsBarLabScreen
import com.example.uiapp.ui.scratchcard.ScratchCardLabScreen
import com.example.uiapp.ui.streakheatmap.StreakHeatmapLabScreen
import com.example.uiapp.ui.swipeactions.SwipeActionsLabScreen
import com.example.uiapp.ui.swipecards.SwipeCardsLabScreen
import com.example.uiapp.ui.holdtoconfirm.HoldToConfirmLabScreen
import com.example.uiapp.ui.slidetoconfirm.SlideToConfirmLabScreen
import com.example.uiapp.ui.splitcomparison.SplitComparisonLabScreen
import com.example.uiapp.ui.bentogrid.BentoGridLabScreen
import com.example.uiapp.ui.perforatedticket.PerforatedTicketLabScreen
import com.example.uiapp.ui.aivoiceorb.AiVoiceOrbLabScreen
import com.example.uiapp.ui.aichat.AiChatLabScreen
import com.example.uiapp.ui.selectiontoolbar.SelectionToolbarLabScreen
import com.example.uiapp.ui.undoqueue.UndoQueueLabScreen
import com.example.uiapp.ui.adaptivenav.AdaptiveNavLabScreen
import com.example.uiapp.ui.expressivecontrols.ExpressiveControlsLabScreen
import com.example.uiapp.ui.accessibilitylab.AccessibilityLabScreen
import com.example.uiapp.ui.activityinbox.ActivityInboxLabScreen
import com.example.uiapp.ui.resumeform.ResumeFormLabScreen
import com.example.uiapp.ui.nativesurfaces.NativeSurfacesLabScreen
import com.example.uiapp.ui.focustimer.FocusTimerLabScreen
import com.example.uiapp.ui.masonrygrid.MasonryGridLabScreen
import com.example.uiapp.ui.amountkeypad.AmountKeypadLabScreen
import com.example.uiapp.ui.categoryscroll.CategoryScrollLabScreen
import com.example.uiapp.ui.datatable.DataTableLabScreen
import com.example.uiapp.ui.gamification.GamificationLabScreen
import com.example.uiapp.ui.dataviz.DataVizLabScreen
import com.example.uiapp.ui.scrollmotion.ScrollMotionLabScreen
import com.example.uiapp.ui.createlab.CreateLabScreen
import com.example.uiapp.ui.sharesheet.ShareSheetLabScreen
import com.example.uiapp.ui.controlcenter.ControlCenterLabScreen
import com.example.uiapp.ui.activityrings.ActivityRingsLabScreen
import com.example.uiapp.ui.lockscreen.LockScreenLabScreen
import com.example.uiapp.ui.notificationbanner.NotificationBannerLabScreen
import com.example.uiapp.ui.rulerpicker.RulerPickerLabScreen
import com.example.uiapp.ui.searchtokens.SearchTokensLabScreen
import com.example.uiapp.ui.photoszoom.PhotosZoomLabScreen
import com.example.uiapp.ui.keyboardaccessory.KeyboardAccessoryLabScreen
import com.example.uiapp.ui.widgetgallery.WidgetGalleryLabScreen
import com.example.uiapp.ui.editmenu.EditMenuLabScreen
import com.example.uiapp.ui.liveactivity.LiveActivityLabScreen
import com.example.uiapp.ui.precisionfinder.PrecisionFinderLabScreen
import com.example.uiapp.ui.livecaptions.LiveCaptionsLabScreen
import com.example.uiapp.ui.pagecurl.PageCurlLabScreen
import com.example.uiapp.ui.standbymode.StandbyModeLabScreen
import com.example.uiapp.ui.msgeffects.MsgEffectsLabScreen
import com.example.uiapp.ui.callui.CallUiLabScreen
import com.example.uiapp.ui.weatherapp.WeatherAppLabScreen
import com.example.uiapp.ui.stocksapp.StocksAppLabScreen
import com.example.uiapp.ui.healthapp.HealthAppLabScreen
import com.example.uiapp.ui.clockkit.ClockKitLabScreen
import com.example.uiapp.ui.shortcutslab.ShortcutsLabScreen
import com.example.uiapp.ui.homeapp.HomeAppLabScreen
import com.example.uiapp.ui.walletstack.WalletStackLabScreen
import com.example.uiapp.ui.appstoretoday.AppstoreTodayLabScreen
import com.example.uiapp.ui.translateapp.TranslateAppLabScreen
import com.example.uiapp.ui.freeformcanvas.FreeformCanvasLabScreen
import com.example.uiapp.ui.writingtools.WritingToolsLabScreen
import com.example.uiapp.ui.genmoji.GenmojiLabScreen
import com.example.uiapp.ui.focusmodes.FocusModesLabScreen
import com.example.uiapp.ui.passkeys_passwords.PasskeysPasswordsLabScreen
import com.example.uiapp.ui.voiceoverlab.VoiceoverLabScreen
import com.example.uiapp.ui.continuity.ContinuityLabScreen
import com.example.uiapp.ui.screentime.ScreenTimeLabScreen
import com.example.uiapp.ui.pointerscribble.PointerScribbleLabScreen
import com.example.uiapp.ui.audioequalizer.AudioEqualizerLabScreen
import com.example.uiapp.ui.blockeditor.BlockEditorLabScreen
import com.example.uiapp.ui.flightitinerary.FlightItineraryLabScreen
import com.example.uiapp.ui.ganttroadmap.GanttRoadmapLabScreen
import com.example.uiapp.ui.photocropper.PhotoCropperLabScreen
import com.example.uiapp.ui.seatselector.SeatSelectorLabScreen
import com.example.uiapp.ui.splitbill.SplitBillLabScreen
import com.example.uiapp.ui.syncedlyrics.SyncedLyricsLabScreen
import com.example.uiapp.ui.treeexplorer.TreeExplorerLabScreen
import com.example.uiapp.ui.triptracker.TripTrackerLabScreen


private const val RouteHome = "home"
private const val RouteAutoBlurScroll = "auto_blur_scroll"
private const val RouteStickyHeader = "sticky_header"
private const val RouteParallaxHero = "parallax_hero"
private const val RouteShimmer = "shimmer"
private const val RouteSharedElement = "shared_element"
private const val RouteBottomSheet = "bottom_sheet"
private const val RouteEmptyState = "empty_state"
private const val RouteMotionLab = "motion_lab"
private const val RouteFormLab = "form_lab"
private const val RouteNavLab = "nav_lab"
private const val RouteAdaptive = "adaptive"
private const val RouteGallery = "gallery"
private const val RouteFeedback = "feedback"
private const val RouteStats = "stats"
private const val RouteCharts = "charts"
private const val RouteInfiniteScroll = "infinite_scroll"
private const val RouteMultiSelect = "multi_select"
private const val RouteSearch = "search"
private const val RouteThemeLab = "theme_lab"
private const val RouteSplash = "splash"
private const val RouteOnboarding = "onboarding"
private const val RouteAuth = "auth"
private const val RouteBiometric = "biometric"
private const val RoutePasscode = "passcode"
private const val RoutePermissions = "permissions"
private const val RouteProfileSetup = "profile_setup"

// 9 New Labs
private const val RouteNavStructure = "nav_structure"
private const val RouteAdvancedList = "advanced_list"
private const val RouteDataCards = "data_cards"
private const val RouteAdvancedInput = "advanced_input"
private const val RouteOverlay = "overlay_lab"
private const val RouteMedia = "media_lab"
private const val RouteSettings = "settings_lab"
private const val RouteCommerce = "commerce_lab"
private const val RouteSystemPlatform = "system_platform"

// 3 Priority World-Class UI Labs
private const val RouteTickerCounter = "ticker_counter"
private const val RoutePullDismiss = "pull_dismiss"
private const val RouteCommandPalette = "command_palette"

// 9 Additional World-Class UI Labs
private const val RouteConfettiParticles = "confetti_particles"
private const val RouteReactionsBar = "reactions_bar"
private const val RouteSwipeCards = "swipe_cards"
private const val RouteSwipeActions = "swipe_actions"
private const val RouteMorphingFab = "morphing_fab"
private const val RoutePrivacyMasking = "privacy_masking"
private const val RouteBalanceMasking = "balance_masking"
private const val RouteScratchCard = "scratch_card"
private const val RouteStreakHeatmap = "streak_heatmap"

// 6 Trending Mobile UI Labs (X & Reddit)
private const val RouteHoldToConfirm = "hold_to_confirm"
private const val RouteSlideToConfirm = "slide_to_confirm"
private const val RouteSplitComparison = "split_comparison"
private const val RouteBentoGrid = "bento_grid"
private const val RoutePerforatedTicket = "perforated_ticket"
private const val RouteAiVoiceOrb = "ai_voice_orb"

// 9 Modern Components (Section 6)
private const val RouteAiChat = "ai_chat"
private const val RouteSelectionToolbar = "selection_toolbar"
private const val RouteUndoQueue = "undo_queue"
private const val RouteAdaptiveNavigation = "adaptive_navigation"
private const val RouteExpressiveControls = "expressive_controls"
private const val RouteAccessibilityLab = "accessibility_lab"
private const val RouteActivityInbox = "activity_inbox"
private const val RouteResumeForm = "resume_form"
private const val RouteNativeSurfaces = "native_surfaces"

// 5 Modern & Professional Labs (Section 7)
private const val RouteFocusTimer = "focus_timer"
private const val RouteMasonryGrid = "masonry_grid"
private const val RouteAmountKeypad = "amount_keypad"
private const val RouteCategoryScroll = "category_scroll"
private const val RouteDataTable = "data_table"

// 4 New Labs (Section 8)
private const val RouteGamification = "gamification_lab"
private const val RouteDataViz = "dataviz_lab"
private const val RouteScrollMotion = "scroll_motion_lab"
private const val RouteCreateLab = "create_lab"

// 16 iOS Pattern Labs (Section 9)
private const val RouteShareSheet = "share_sheet"
private const val RouteControlCenter = "control_center"
private const val RouteActivityRings = "activity_rings"
private const val RouteLockScreen = "lock_screen"
private const val RouteNotificationBanner = "notification_banner"
private const val RouteRulerPicker = "ruler_picker"
private const val RouteSearchTokens = "search_tokens"
private const val RoutePhotosZoom = "photos_zoom"
private const val RouteKeyboardAccessory = "keyboard_accessory"
private const val RouteWidgetGallery = "widget_gallery"
private const val RouteEditMenu = "edit_menu"
private const val RouteLiveActivity = "live_activity"
private const val RoutePrecisionFinder = "precision_finder"
private const val RouteLiveCaptions = "live_captions"
private const val RoutePageCurl = "page_curl"
private const val RouteStandbyMode = "standby_mode"

// 20 iOS Apps Labs (Section 10)
private const val RouteMsgEffects = "msg_effects"
private const val RouteCallUi = "call_ui"
private const val RouteWeatherApp = "weather_app"
private const val RouteStocksApp = "stocks_app"
private const val RouteHealthApp = "health_app"
private const val RouteClockKit = "clock_kit"
private const val RouteShortcutsLab = "shortcuts_lab"
private const val RouteHomeApp = "home_app"
private const val RouteWalletStack = "wallet_stack"
private const val RouteAppstoreToday = "appstore_today"
private const val RouteTranslateApp = "translate_app"
private const val RouteFreeformCanvas = "freeform_canvas"
private const val RouteWritingTools = "writing_tools"
private const val RouteGenmoji = "genmoji"
private const val RouteFocusModes = "focus_modes"
private const val RoutePasskeysPasswords = "passkeys_passwords"
private const val RouteVoiceoverLab = "voiceover_lab"
private const val RouteContinuity = "continuity"
private const val RouteScreenTime = "screen_time"
private const val RoutePointerScribble = "pointer_scribble"

// 10 Komponen Industri, Spasial & Produktivitas (Section 11)
private const val RouteSeatSelector = "seat_selector"
private const val RouteTripTracker = "trip_tracker"
private const val RouteFlightItinerary = "flight_itinerary"
private const val RouteSyncedLyrics = "synced_lyrics"
private const val RouteAudioEqualizer = "audio_equalizer"
private const val RoutePhotoCropper = "photo_cropper"
private const val RouteBlockEditor = "block_editor"
private const val RouteGanttRoadmap = "gantt_roadmap"
private const val RouteTreeExplorer = "tree_explorer"
private const val RouteSplitBill = "split_bill"

private val KnownRoutes = setOf(
    RouteAutoBlurScroll,
    RouteStickyHeader,
    RouteParallaxHero,
    RouteShimmer,
    RouteSharedElement,
    RouteBottomSheet,
    RouteEmptyState,
    RouteMotionLab,
    RouteFormLab,
    RouteNavLab,
    RouteAdaptive,
    RouteGallery,
    RouteFeedback,
    RouteStats,
    RouteCharts,
    RouteInfiniteScroll,
    RouteMultiSelect,
    RouteSearch,
    RouteThemeLab,
    RouteSplash,
    RouteOnboarding,
    RouteAuth,
    RouteBiometric,
    RoutePasscode,
    RoutePermissions,
    RouteProfileSetup,
    RouteNavStructure,
    RouteAdvancedList,
    RouteDataCards,
    RouteAdvancedInput,
    RouteOverlay,
    RouteMedia,
    RouteSettings,
    RouteCommerce,
    RouteSystemPlatform,
    RouteTickerCounter,
    RoutePullDismiss,
    RouteCommandPalette,
    RouteConfettiParticles,
    RouteReactionsBar,
    RouteSwipeCards,
    RouteSwipeActions,
    RouteMorphingFab,
    RoutePrivacyMasking,
    RouteBalanceMasking,
    RouteScratchCard,
    RouteStreakHeatmap,
    RouteHoldToConfirm,
    RouteSlideToConfirm,
    RouteSplitComparison,
    RouteBentoGrid,
    RoutePerforatedTicket,
    RouteAiVoiceOrb,
    RouteAiChat,
    RouteSelectionToolbar,
    RouteUndoQueue,
    RouteAdaptiveNavigation,
    RouteExpressiveControls,
    RouteAccessibilityLab,
    RouteActivityInbox,
    RouteResumeForm,
    RouteNativeSurfaces,
    RouteFocusTimer,
    RouteMasonryGrid,
    RouteAmountKeypad,
    RouteCategoryScroll,
    RouteDataTable,
    RouteGamification,
    RouteDataViz,
    RouteScrollMotion,
    RouteCreateLab,
    RouteShareSheet,
    RouteControlCenter,
    RouteActivityRings,
    RouteLockScreen,
    RouteNotificationBanner,
    RouteRulerPicker,
    RouteSearchTokens,
    RoutePhotosZoom,
    RouteKeyboardAccessory,
    RouteWidgetGallery,
    RouteEditMenu,
    RouteLiveActivity,
    RoutePrecisionFinder,
    RouteLiveCaptions,
    RoutePageCurl,
    RouteStandbyMode,
    RouteMsgEffects,
    RouteCallUi,
    RouteWeatherApp,
    RouteStocksApp,
    RouteHealthApp,
    RouteClockKit,
    RouteShortcutsLab,
    RouteHomeApp,
    RouteWalletStack,
    RouteAppstoreToday,
    RouteTranslateApp,
    RouteFreeformCanvas,
    RouteWritingTools,
    RouteGenmoji,
    RouteFocusModes,
    RoutePasskeysPasswords,
    RouteVoiceoverLab,
    RouteContinuity,
    RouteScreenTime,
    RoutePointerScribble,
    RouteSeatSelector,
    RouteTripTracker,
    RouteFlightItinerary,
    RouteSyncedLyrics,
    RouteAudioEqualizer,
    RoutePhotoCropper,
    RouteBlockEditor,
    RouteGanttRoadmap,
    RouteTreeExplorer,
    RouteSplitBill,
)


@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() {
    UiAppTheme {
        val navController = rememberAppNavController(RouteHome)
        val homeGridState = rememberLazyGridState()
        var selectedHomeSection by remember { mutableStateOf(HomeSection.ALL) }
        var lastTestedRoute by remember { mutableStateOf<String?>(null) }

        // Setiap kembali ke Home mengembalikan filter ke Semua (107), bukan section lab yang dibuka.
        val handleBack: () -> Unit = {
            selectedHomeSection = HomeSection.ALL
            navController.pop()
        }
        val handleExitToHome: () -> Unit = {
            selectedHomeSection = HomeSection.ALL
            navController.popToRoot()
        }

        CompositionLocalProvider(LocalNavController provides navController) {
            BackHandler(enabled = navController.canPop) {
                handleBack()
            }

            when (navController.currentRoute) {
                RouteAutoBlurScroll -> AutoBlurScrollScreen(
                    onBack = handleBack,
                )

                RouteStickyHeader -> StickyHeaderScreen(
                    onBack = handleBack,
                )

                RouteParallaxHero -> ParallaxHeroScreen(
                    onBack = handleBack,
                )

                RouteShimmer -> ShimmerSkeletonScreen(
                    onBack = handleBack,
                )

                RouteSharedElement -> SharedElementScreen(
                    onBack = handleBack,
                )

                RouteBottomSheet -> BottomSheetScreen(
                    onBack = handleBack,
                )

                RouteEmptyState -> EmptyStateScreen(
                    onBack = handleBack,
                )

                RouteMotionLab -> MotionLabScreen(
                    onBack = handleBack,
                )

                RouteFormLab -> FormLabScreen(
                    onBack = handleBack,
                )

                RouteNavLab -> NavLabScreen(
                    onBack = handleBack,
                )

                RouteAdaptive -> AdaptiveScreen(
                    onBack = handleBack,
                )

                RouteGallery -> GalleryScreen(
                    onBack = handleBack,
                )

                RouteFeedback -> FeedbackLabScreen(
                    onBack = handleBack,
                )

                RouteStats -> StatsLabScreen(
                    onBack = handleBack,
                )

                RouteCharts -> ChartsLabScreen(
                    onBack = handleBack,
                )

                RouteInfiniteScroll -> InfiniteScrollScreen(
                    onBack = handleBack,
                )

                RouteMultiSelect -> MultiSelectScreen(
                    onBack = handleBack,
                )

                RouteSearch -> SearchAutocompleteScreen(
                    onBack = handleBack,
                )

                RouteThemeLab -> ThemeLabScreen(
                    onBack = handleBack,
                )

                RouteSplash -> SplashScreen(
                    onBack = handleExitToHome,
                    onFinish = handleExitToHome,
                    onNavigateToOnboarding = { navController.navigate(RouteOnboarding) },
                )

                RouteOnboarding -> OnboardingScreen(
                    onBack = handleExitToHome,
                    onFinish = { navController.navigate(RouteAuth) },
                )

                RouteAuth -> AuthScreen(
                    onBack = handleBack,
                    onFinish = { navController.navigate(RoutePermissions) },
                )

                RouteBiometric -> BiometricScreen(
                    onBack = handleBack,
                )

                RoutePasscode -> PasscodeScreen(
                    onBack = handleBack,
                )

                RoutePermissions -> PermissionsScreen(
                    onBack = handleBack,
                    onFinish = { navController.navigate(RouteProfileSetup) },
                )

                RouteProfileSetup -> ProfileSetupScreen(
                    onBack = handleBack,
                    onFinish = handleExitToHome,
                )

                RouteNavStructure -> NavStructureLabScreen(
                    onBack = handleBack,
                )

                RouteAdvancedList -> AdvancedListLabScreen(
                    onBack = handleBack,
                )

                RouteDataCards -> DataCardsLabScreen(
                    onBack = handleBack,
                )

                RouteAdvancedInput -> AdvancedInputLabScreen(
                    onBack = handleBack,
                )

                RouteOverlay -> OverlayLabScreen(
                    onBack = handleBack,
                )

                RouteMedia -> MediaLabScreen(
                    onBack = handleBack,
                )

                RouteSettings -> SettingsLabScreen(
                    onBack = handleBack,
                )

                RouteCommerce -> CommerceLabScreen(
                    onBack = handleBack,
                )

                RouteSystemPlatform -> SystemPlatformLabScreen(
                    onBack = handleBack,
                )

                RouteTickerCounter -> TickerCounterLabScreen(
                    onBack = handleBack,
                )

                RoutePullDismiss -> PullDownDismissLabScreen(
                    onBack = handleBack,
                )

                RouteCommandPalette -> CommandPaletteLabScreen(
                    onBack = handleBack,
                )

                RouteConfettiParticles -> ConfettiParticlesLabScreen(
                    onBack = handleBack,
                )

                RouteReactionsBar -> ReactionsBarLabScreen(
                    onBack = handleBack,
                )

                RouteSwipeCards -> SwipeCardsLabScreen(
                    onBack = handleBack,
                )

                RouteSwipeActions -> SwipeActionsLabScreen(
                    onBack = handleBack,
                )

                RouteMorphingFab -> MorphingFabLabScreen(
                    onBack = handleBack,
                )

                RoutePrivacyMasking -> PrivacyMaskingLabScreen(
                    onBack = handleBack,
                )

                RouteBalanceMasking -> BalanceMaskingLabScreen(
                    onBack = handleBack,
                )

                RouteScratchCard -> ScratchCardLabScreen(
                    onBack = handleBack,
                )

                RouteStreakHeatmap -> StreakHeatmapLabScreen(
                    onBack = handleBack,
                )

                RouteHoldToConfirm -> HoldToConfirmLabScreen(
                    onBack = handleBack,
                )

                RouteSlideToConfirm -> SlideToConfirmLabScreen(
                    onBack = handleBack,
                )

                RouteSplitComparison -> SplitComparisonLabScreen(
                    onBack = handleBack,
                )

                RouteBentoGrid -> BentoGridLabScreen(
                    onBack = handleBack,
                )

                RoutePerforatedTicket -> PerforatedTicketLabScreen(
                    onBack = handleBack,
                )

                RouteAiVoiceOrb -> AiVoiceOrbLabScreen(
                    onBack = handleBack,
                )

                RouteAiChat -> AiChatLabScreen(
                    onBack = handleBack,
                )

                RouteSelectionToolbar -> SelectionToolbarLabScreen(
                    onBack = handleBack,
                )

                RouteUndoQueue -> UndoQueueLabScreen(
                    onBack = handleBack,
                )

                RouteAdaptiveNavigation -> AdaptiveNavLabScreen(
                    onBack = handleBack,
                )

                RouteExpressiveControls -> ExpressiveControlsLabScreen(
                    onBack = handleBack,
                )

                RouteAccessibilityLab -> AccessibilityLabScreen(
                    onBack = handleBack,
                )

                RouteActivityInbox -> ActivityInboxLabScreen(
                    onBack = handleBack,
                )

                RouteResumeForm -> ResumeFormLabScreen(
                    onBack = handleBack,
                )

                RouteNativeSurfaces -> NativeSurfacesLabScreen(
                    onBack = handleBack,
                )

                RouteFocusTimer -> FocusTimerLabScreen(
                    onBack = handleBack,
                )

                RouteMasonryGrid -> MasonryGridLabScreen(
                    onBack = handleBack,
                )

                RouteAmountKeypad -> AmountKeypadLabScreen(
                    onBack = handleBack,
                )

                RouteCategoryScroll -> CategoryScrollLabScreen(
                    onBack = handleBack,
                )

                RouteDataTable -> DataTableLabScreen(
                    onBack = handleBack,
                )

                RouteGamification -> GamificationLabScreen(
                    onBack = handleBack,
                )

                RouteDataViz -> DataVizLabScreen(
                    onBack = handleBack,
                )

                RouteScrollMotion -> ScrollMotionLabScreen(
                    onBack = handleBack,
                )

                RouteCreateLab -> CreateLabScreen(
                    onBack = handleBack,
                )

                RouteShareSheet -> ShareSheetLabScreen(
                    onBack = handleBack,
                )

                RouteControlCenter -> ControlCenterLabScreen(
                    onBack = handleBack,
                )

                RouteActivityRings -> ActivityRingsLabScreen(
                    onBack = handleBack,
                )

                RouteLockScreen -> LockScreenLabScreen(
                    onBack = handleBack,
                )

                RouteNotificationBanner -> NotificationBannerLabScreen(
                    onBack = handleBack,
                )

                RouteRulerPicker -> RulerPickerLabScreen(
                    onBack = handleBack,
                )

                RouteSearchTokens -> SearchTokensLabScreen(
                    onBack = handleBack,
                )

                RoutePhotosZoom -> PhotosZoomLabScreen(
                    onBack = handleBack,
                )

                RouteKeyboardAccessory -> KeyboardAccessoryLabScreen(
                    onBack = handleBack,
                )

                RouteWidgetGallery -> WidgetGalleryLabScreen(
                    onBack = handleBack,
                )

                RouteEditMenu -> EditMenuLabScreen(
                    onBack = handleBack,
                )

                RouteLiveActivity -> LiveActivityLabScreen(
                    onBack = handleBack,
                )

                RoutePrecisionFinder -> PrecisionFinderLabScreen(
                    onBack = handleBack,
                )

                RouteLiveCaptions -> LiveCaptionsLabScreen(
                    onBack = handleBack,
                )

                RoutePageCurl -> PageCurlLabScreen(
                    onBack = handleBack,
                )

                RouteStandbyMode -> StandbyModeLabScreen(
                    onBack = handleBack,
                )

                RouteMsgEffects -> MsgEffectsLabScreen(
                    onBack = handleBack,
                )

                RouteCallUi -> CallUiLabScreen(
                    onBack = handleBack,
                )

                RouteWeatherApp -> WeatherAppLabScreen(
                    onBack = handleBack,
                )

                RouteStocksApp -> StocksAppLabScreen(
                    onBack = handleBack,
                )

                RouteHealthApp -> HealthAppLabScreen(
                    onBack = handleBack,
                )

                RouteClockKit -> ClockKitLabScreen(
                    onBack = handleBack,
                )

                RouteShortcutsLab -> ShortcutsLabScreen(
                    onBack = handleBack,
                )

                RouteHomeApp -> HomeAppLabScreen(
                    onBack = handleBack,
                )

                RouteWalletStack -> WalletStackLabScreen(
                    onBack = handleBack,
                )

                RouteAppstoreToday -> AppstoreTodayLabScreen(
                    onBack = handleBack,
                )

                RouteTranslateApp -> TranslateAppLabScreen(
                    onBack = handleBack,
                )

                RouteFreeformCanvas -> FreeformCanvasLabScreen(
                    onBack = handleBack,
                )

                RouteWritingTools -> WritingToolsLabScreen(
                    onBack = handleBack,
                )

                RouteGenmoji -> GenmojiLabScreen(
                    onBack = handleBack,
                )

                RouteFocusModes -> FocusModesLabScreen(
                    onBack = handleBack,
                )

                RoutePasskeysPasswords -> PasskeysPasswordsLabScreen(
                    onBack = handleBack,
                )

                RouteVoiceoverLab -> VoiceoverLabScreen(
                    onBack = handleBack,
                )

                RouteContinuity -> ContinuityLabScreen(
                    onBack = handleBack,
                )

                RouteScreenTime -> ScreenTimeLabScreen(
                    onBack = handleBack,
                )

                RoutePointerScribble -> PointerScribbleLabScreen(
                    onBack = handleBack,
                )

                RouteSeatSelector -> SeatSelectorLabScreen(
                    onBack = handleBack,
                )

                RouteTripTracker -> TripTrackerLabScreen(
                    onBack = handleBack,
                )

                RouteFlightItinerary -> FlightItineraryLabScreen(
                    onBack = handleBack,
                )

                RouteSyncedLyrics -> SyncedLyricsLabScreen(
                    onBack = handleBack,
                )

                RouteAudioEqualizer -> AudioEqualizerLabScreen(
                    onBack = handleBack,
                )

                RoutePhotoCropper -> PhotoCropperLabScreen(
                    onBack = handleBack,
                )

                RouteBlockEditor -> BlockEditorLabScreen(
                    onBack = handleBack,
                )

                RouteGanttRoadmap -> GanttRoadmapLabScreen(
                    onBack = handleBack,
                )

                RouteTreeExplorer -> TreeExplorerLabScreen(
                    onBack = handleBack,
                )

                RouteSplitBill -> SplitBillLabScreen(
                    onBack = handleBack,
                )

                else -> HomeMenuScreen(
                    gridState = homeGridState,
                    selectedSection = selectedHomeSection,
                    onSectionSelected = { selectedHomeSection = it },
                    lastTestedRoute = lastTestedRoute,
                    onMenuClick = { id ->
                        if (id in KnownRoutes) {
                            lastTestedRoute = id
                            navController.navigate(id)
                        }
                    },
                )
            }
        }
    }
}
