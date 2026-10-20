# UIApp — Kotlin Multiplatform UI Laboratory

> Galeri eksperimen **166 lab komponen UI mobile** dalam satu aplikasi demo. 100% Compose Multiplatform (CMP), berjalan di Android & iOS dari satu `commonMain`, tanpa backend, tanpa jaringan — semua data adalah mock lokal berbahasa Indonesia.

<p>
  <img src="https://img.shields.io/badge/Kotlin-2.4.20-7F52FF?style=flat-square" alt="Kotlin 2.4.20" />
  <img src="https://img.shields.io/badge/Compose%20Multiplatform-1.12.0-4285F4?style=flat-square" alt="CMP 1.12.0" />
  <img src="https://img.shields.io/badge/Material%203-1.12.0--alpha03-009688?style=flat-square" alt="Material3" />
  <img src="https://img.shields.io/badge/Android-min%20SDK%2029%20%E2%80%A2%20target%2037-3DDC84?style=flat-square" alt="Android" />
  <img src="https://img.shields.io/badge/iOS-16%2B%20arm64%20%7C%20simulator-000000?style=flat-square" alt="iOS" />
  <img src="https://img.shields.io/badge/Labs-166%20DONE-success?style=flat-square" alt="166 Labs" />
</p>

---

## Daftar Isi

- [Tentang Proyek](#tentang-proyek)
- [Fitur Utama](#fitur-utama)
- [Katalog 166 Lab](#katalog-166-lab)
- [Tech Stack](#tech-stack)
- [Struktur Proyek](#struktur-proyek)
- [Memulai](#memulai)
- [Arsitektur & Konvensi](#arsitektur--konvensi)
- [Desain Visual](#desain-visual)
- [Menambah Lab Baru](#menambah-lab-baru)
- [Build & Validasi](#build--validasi)
- [Roadmap](#roadmap)
- [Lisensi](#lisensi)

---

## Tentang Proyek

**UIApp** (`rootProject.name = "UIApp"`, package `com.example.uiapp`) adalah **kit laboratorium UI** — bukan aplikasi bisnis. Tujuannya mengoleksi pola interaksi mobile modern (fintech, produktivitas, media, gamifikasi, iOS system experiences) sebagai lab yang berdiri sendiri dan dapat dibuka dari **Home** sebagai galeri komponen.

Karakteristik utama:

- **Satu codebase UI** di `shared/src/commonMain` — `androidApp` dan `iosApp` hanya entry point / bootstrap.
- **166 lab `DONE`** (0 `WAIT`), terdaftar di `TODO.md` dan `App.kt`, masing-masing dengan route `snake_case`, `MenuEntry` Home, ikon Canvas khusus, dan `BackHandler`.
- **Offline penuh**: tidak ada Coil/Ktor/Room/SQLDelight/Serialization, tidak ada panggilan jaringan, kamera, mikrofon, atau lokasi nyata. Semua perilaku yang menyerupai layanan platform diberi label **simulasi** di UI.
- **Bahasa UI Indonesia** (judul, subtitle, teks tombol hardcoded).
- **Dua test logika murni** di `shared/src/commonTest` (`CalendarMathTest`, `AppNavigationTest`); UI belum diuji.

> Sumber kebenaran produk adalah [`TODO.md`](./TODO.md). Bila `TODO.md` dan kode tidak sinkron, `TODO.md` yang diperbarui di commit yang sama. Saat ini keduanya sudah sinkron penuh.

---

## Fitur Utama

| Area | Highlight |
|------|-----------|
| **Eksperimen inti** | Auto-blur scroll, sticky header, parallax hero, shimmer, shared element, bottom sheet multi-detent, motion lab, adaptive layout, gallery zoom |
| **Onboarding & Auth** | Splash reveal → onboarding carousel → login/register → permission requests → profile setup wizard → home (alur terhubung) |
| **Komponen lanjutan** | Drag-to-reorder, sectioned A–Z scrubber, chat bubbles, kanban, pricing table, iOS wheel pickers, signature pad, file uploader, video PiP, stories pager |
| **World-class UI** | Ticker odometer, pull-to-dismiss elastic, command palette `Cmd+K`, confetti, swipe-cards, morphing FAB, privacy masking, split comparison, bento grid |
| **Modern 2025/2026** | Hold/slide-to-confirm, AI voice orb, staggered reveal, floating pill toolbar, AI chat streaming, optimistic undo queue, expressive controls |
| **Fintech & produktivitas** | Focus timer, masonry grid, amount keypad Rupiah, data table pro, seat selector, trip tracker, Gantt, split-bill, budget envelopes |
| **iOS system experiences** | Share sheet, Control Center edit mode, Activity Rings, Lock Screen customization, Live Activity lifecycle, StandBy, widget gallery, page curl |
| **Aplikasi tiruan (mock)** | Weather, Stocks (crosshair), Health, Clock Kit, Shortcuts, Home scenes, Wallet pass stack, App Store Today, Translate, Freeform canvas |
| **Industri & platform** | Karaoke lyric scroller, audio equalizer, QRIS pay, virtual card, currency exchange, loan simulator, predictive back, drag & drop lintas-app |

---

## Katalog 166 Lab

Semua lab memakai **route `snake_case`** yang identik dengan `MenuEntry.id` di `HomeMenuScreen.kt`. Daftar lengkap ada di `TODO.md`; ringkasan per batch:

| Batch | Jumlah | Contoh Route |
|-------|--------|--------------|
| **§1 Labs Inti** | 19 | `auto_blur_scroll`, `sticky_header`, `motion_lab`, `charts`, `theme_lab` |
| **§2 Onboarding & Auth** | 7 | `splash`, `onboarding`, `auth`, `biometric`, `passcode`, `permissions`, `profile_setup` |
| **§3 Komponen Lanjutan** | 33 | `nav_structure`, `advanced_list`, `data_cards`, `advanced_input`, `overlay_lab`, `media_lab`, `commerce_lab`, `system_platform` |
| **§5 World-Class & Trending** | 18 | `ticker_counter`, `swipe_cards`, `morphing_fab`, `hold_to_confirm`, `bento_grid`, `ai_voice_orb` |
| **§6 Modern Berikutnya** | 9 | `ai_chat`, `selection_toolbar`, `undo_queue`, `adaptive_navigation`, `expressive_controls`, `accessibility_lab`, `activity_inbox`, `resume_form`, `native_surfaces` |
| **§7 Profesional** | 5 | `focus_timer`, `masonry_grid`, `amount_keypad`, `category_scroll`, `data_table` |
| **§8 Gamifikasi & Media** | 4 | `gamification_lab`, `dataviz_lab`, `scroll_motion_lab`, `create_lab` |
| **§9 Pola Khas iOS** | 16 | `share_sheet`, `control_center`, `activity_rings`, `lock_screen`, `search_tokens`, `live_activity`, `standby_mode` |
| **§10 Aplikasi & Sistem** | 18 | `weather_app`, `stocks_app`, `health_app`, `clock_kit`, `wallet_stack`, `appstore_today`, `passkeys_passwords`, `screen_time` |
| **§11 Industri & Spasial** | 15 | `seat_selector`, `trip_tracker`, `flight_itinerary`, `synced_lyrics`, `block_editor`, `gantt_roadmap`, `quest_path`, `predictive_back`, `floating_dock` |
| **§12 Gap Analysis (Tier 1–3)** | 40 | `collab_presence`, `live_dashboard`, `offline_resilience`, `ai_agent_steps`, `perf_hud`, `store_locator`, `qris_pay`, `loan_simulator`, `drag_drop` |
| **Pola Viral 2026 (§5.3)** | 4 | `staggered_reveal`, `success_check`, `pill_toolbar`, `search_morph` |
| **Total** | **166** | semua `DONE` |

> Setiap lab memiliki **ikon Canvas khusus** di `shared/src/commonMain/kotlin/com/example/uiapp/ui/components/icons/` (dipecah per bagian Home: `CoreLabIcons.kt`, `AuthFlowIcons.kt`, `AdvancedLabIcons.kt`, dst.) dan dipasang di `MenuGridCard` via `when (entry.id)`.

---

## Tech Stack

Versi dikunci di [`gradle/libs.versions.toml`](./gradle/libs.versions.toml) — jangan ubah tanpa permintaan eksplisit.

| Komponen | Versi | Catatan |
|----------|-------|---------|
| **Kotlin** | `2.4.20` | `kotlinMultiplatform` + `composeCompiler` |
| **Compose Multiplatform** | `1.12.0` | `compose.runtime`, `animation`, `foundation`, `ui`, `uiBackhandler`, `components.resources` |
| **Material 3** | `1.12.0-alpha03` | `compose.material3` (multiplatform) |
| **Android Gradle Plugin** | `9.1.1` | `androidApplication` + `androidMultiplatformLibrary` |
| **Lifecycle** | `2.11.0` | `lifecycle.viewmodelCompose` + `lifecycle.runtimeCompose` (JetBrains) |
| **Android SDK** | `compile 37 / target 37 / min 29` | JVM target `11` |
| **iOS** | `arm64` + `simulatorArm64`, iOS 16+ | Framework `Shared` static, `isStatic = true` |
| **Build** | `configuration-cache = true` | Jangan akses env/file saat konfigurasi |

**Dependensi `commonMain` yang tersedia:**

```
compose.runtime, compose.animation, compose.foundation, compose.material3,
compose.ui, compose.uiBackhandler, compose.components.resources,
compose.uiToolingPreview, androidx.lifecycle.viewmodelCompose,
androidx.lifecycle.runtimeCompose
```

> **Tidak ada** Coil/Ktor/Room/SQLDelight/Serialization/Navigation-Compose/Koin. Seluruh lab dibangun dengan Compose Foundation + Canvas. Penambahan dependensi baru butuh persetujuan.

---

## Struktur Proyek

```
testingUI/
├── androidApp/                         # Entry point Android saja
│   └── src/main/kotlin/com/example/uiapp/MainActivity.kt
├── iosApp/
│   └── iosApp/ContentView.swift        # SwiftUI host + ComposeView + .ignoresSafeArea()
├── shared/                             # SELURUH kode aplikasi
│   ├── src/
│   │   ├── commonMain/kotlin/com/example/uiapp/
│   │   │   ├── App.kt                  # Root composable + tabel routing (166 route + KnownRoutes)
│   │   │   ├── navigation/AppNavigation.kt  # AppNavController (backstack List<String>)
│   │   │   ├── theme/
│   │   │   │   ├── AppPalette.kt       # LightAppPalette / DarkAppPalette + LocalAppPalette
│   │   │   │   └── UiAppTheme.kt       # UiAppTheme + LocalThemeController (LIGHT/DARK/SYSTEM)
│   │   │   └── ui/
│   │   │       ├── home/HomeMenuScreen.kt   # Grid 2 kolom + filter section + search
│   │   │       ├── components/
│   │   │       │   ├── UiTopBar.kt     # Top bar standar (wajib dipakai tiap layar)
│   │   │       │   ├── AuthKit.kt      # FlatTextField, FlatPrimaryButton, dll.
│   │   │       │   ├── StatusPill.kt
│   │   │       │   ├── LabKit.kt
│   │   │       │   └── icons/          # Ikon vektor Canvas per bagian Home
│   │   │       └── <lab>/              # Satu folder per lab, satu file layar utama
│   │   │           ├── <Nama>LabScreen.kt
│   │   │           └── *DemoData.kt    # (opsional) data mock lokal
│   │   ├── androidMain/kotlin/         # actual: getPlatform(), ApplySystemBarAppearance()
│   │   ├── iosMain/kotlin/             # actual iOS + MainViewController.kt
│   │   └── commonTest/kotlin/          # CalendarMathTest, AppNavigationTest
│   ├── build.gradle.kts
│   └── ...
├── gradle/libs.versions.toml           # Satu-satunya sumber versi
├── AGENTS.md                           # Governance agent (wajib dibaca sebelum coding)
├── TODO.md                             # Roadmap & status 166 lab
└── README.md                           # File ini
```

**Batas modul (wajib):**

- Semua UI di `commonMain`; dilarang menambah layar Compose/SwiftUI khusus satu platform.
- `androidApp/` & `iosApp/` hanya bootstrap — jangan taruh logika/layar di sana.
- `androidMain`/`iosMain` hanya untuk `expect`/`actual` yang benar-benar perlu (`getPlatform()`, `ApplySystemBarAppearance(isDark)`).
- Satu lab = satu folder `ui/<namalab>/` + satu composable publik `fun <Nama>Screen(onBack: () -> Unit)`.

---

## Memulai

### Prasyarat

- **Android Studio** Ladybug+ / IntelliJ IDEA dengan plugin KMP
- **JDK 17+** (JVM target 11)
- **Xcode 15+** (untuk iOS, di macOS)
- Gradle wrapper sudah termasuk (`gradlew` / `gradlew.bat`)

### Clone & Jalankan (Android)

```powershell
# Clone
git clone <repo-url> testingUI
cd testingUI

# Build debug APK (validasi utama)
.\gradlew.bat :androidApp:assembleDebug

# Install ke emulator/perangkat
.\gradlew.bat :androidApp:installDebug

# Atau pakai Run Configuration di Android Studio: `androidApp`
```

APK debug: `androidApp/build/outputs/apk/debug/androidApp-debug.apk`

### Jalankan di iOS

```bash
# Dari Android Studio
./gradlew :shared:compileKotlinIosSimulatorArm64  # validasi (Apple Silicon)

# Atau buka di Xcode
open iosApp/iosApp.xcodeproj
# Pilih scheme iosApp → Run (simulator iOS 16+)
```

Framework `Shared` (static) dibangun otomatis oleh Gradle saat build Xcode.

### Konfigurasi Lokal

`local.properties` (tidak di-commit) berisi `sdk.dir` Android SDK. `build/`, `.gradle/`, `.kotlin/`, `xcuserdata/` sudah di-`.gitignore`.

---

## Arsitektur & Konvensi

### 1. Navigasi (kustom, tanpa Navigation-Compose)

```kotlin
// navigation/AppNavigation.kt
class AppNavController(initialRoute: String = "home") {
    var backstack: List<String> // mutableStateOf
    fun navigate(route: String)
    fun pop(): Boolean
    fun popTo(route: String): Boolean
    fun popToRoot()
    fun replace(route: String)
    fun resetTo(route: String)
    val canPop: Boolean
    val currentRoute: String
}
val LocalNavController = staticCompositionLocalOf<AppNavController> { error(...) }
```

- Root `App()` di `App.kt` melakukan routing dengan `when (navController.currentRoute) { ... else -> HomeMenuScreen(...) }`.
- `BackHandler(enabled = navController.canPop) { navController.pop() }` di root + `BackHandler { onBack() }` di tiap layar (import `androidx.compose.ui.backhandler.BackHandler`).

### 2. Tema & Warna

```kotlin
val palette = LocalAppPalette.current
// palette.background, surface, surfaceMuted, border,
// textPrimary, textSecondary, textMuted,
// primary, onPrimary, primaryContainer, onPrimaryContainer,
// success/onSuccess, warning/onWarning, info/onInfo, danger/onDanger

val controller = LocalThemeController.current
controller.setMode(ThemeMode.DARK) // LIGHT | DARK | SYSTEM
```

Aturan keras:

- Ambil warna **hanya** lewat `LocalAppPalette.current`.
- Teks/ikon di atas `palette.primary` wajib `palette.onPrimary` — jangan `Color.White` (kontras `onPrimary` ±8,8:1 vs putih ±1,9:1 di mode gelap).
- Dilarang `Color(0xFF...)` baru untuk surface/teks/border; hanya untuk data visual mock (gradien chart, pill status).
- System bar Android mengikuti `ThemeMode` via `ApplySystemBarAppearance(isDark)` di `UiAppTheme` — jangan hapus.

### 3. State

- Lokal Compose: `remember { mutableStateOf(...) }`, `derivedStateOf`, `LaunchedEffect`, `rememberCoroutineScope()`, `animate*AsState`.
- ViewModel hanya bila lab butuh survive config change (`androidx.lifecycle.ViewModel` + `viewModel()` sudah tersedia).
- Dilarang `GlobalScope`, `runBlocking`, blocking I/O di composable.

### 4. Insets, Adaptive & Anti-Clipping

- **Android** `MainActivity.enableEdgeToEdge()` + **iOS** `.ignoresSafeArea()` → konten mengalir di bawah system bar, insets ditangani manual.
- Bottom bar / FAB / tombol bawah: `Modifier.navigationBarsPadding()` (jangan padding tetap 16dp).
- Layar ber-input: `Modifier.imePadding()` pada container bawah.
- Adaptive: `BoxWithConstraints` dengan breakpoint `<480dp` compact · `<760dp` medium · `<1040dp` expanded · `large` 4 kolom.
- Konten statis: `verticalScroll(rememberScrollState())`; `LazyColumn` tidak boleh di dalam `verticalScroll`.

### 5. Ikon & Aset

- Semua ikon adalah **vektor Canvas lokal** (`Path`/`drawPath`, `StrokeCap.Round`, `StrokeJoin.Round`) — tanpa font ikon / bitmap / image loader.
- Tambah ikon: `@Composable fun <Nama>MenuIcon(tint: Color, size: Dp)` di `ui/components/icons/` (file sesuai bagian Home).
- Ilustrasi mock = `Brush.linearGradient` + bentuk geometris; field `imageUrl`/`avatarUrl` diisi gradien mock, bukan `null`.

### 6. Aksesibilitas

- Tiap elemen interaktif tanpa teks wajib `contentDescription` / `semantics { role/contentDescription }`.
- Status (PAID/PENDING/ERROR) tidak boleh hanya warna — sertakan label teks.
- Tahan uji font besar 150–200%, mode gelap/kontras tinggi, dan reduce-motion.

---

## Desain Visual

**Flat UI Universal Zero-Shadow** — berlaku untuk semua lab (§7 `AGENTS.md`):

| Prinsip | Implementasi |
|---------|--------------|
| **Tanpa shadow** | Dilarang `shadowElevation > 0`, `cardElevation > 0`, `tonalElevation > 0`, `Modifier.shadow` |
| **Border 1px** | `BorderStroke(1.dp, palette.border)` / `Modifier.border(1.dp, palette.border)` |
| **Permukaan** | `palette.surface` / `palette.surfaceMuted`; mode terang = putih/mendekati putih |
| **Radius** | Kecil `10–12dp`, kartu `16–20dp`, pill `fully rounded` |
| **Tipografi** | Judul Home `FontFamily.Serif` editorial; body `SemiBold` + `textSecondary`/`textMuted` |
| **Kaca/translucent** | Hanya opsi demo (`native_surfaces`), bukan default — wajib fallback kontras tinggi |

---

## Menambah Lab Baru

Ikuti checklist §6.1 `AGENTS.md` — **semua langkah wajib** dalam satu commit:

1. Buat `ui/<namalab>/<Nama>Screen.kt` dengan `fun <Nama>Screen(onBack: () -> Unit)` (satu composable publik, sisanya `private`).
2. Tambah `private const val Route<Nama> = "<snake_case>"` di `App.kt`.
3. Tambah route ke `KnownRoutes` (`Set<String>`) di `App.kt`.
4. Tambah cabang `Route<Nama> -> <Nama>Screen(onBack = { navController.pop() })` di `when`.
5. Tambah `MenuEntry(id, title, subtitle, enabled = true)` di `HomeMenuScreen.kt` (subtitle Indonesia).
6. Buat `fun <Nama>MenuIcon(tint: Color)` di `ui/components/icons/` (file per bagian Home) & pasang `when (entry.id)` di `MenuGridCard`.
7. Tambah `BackHandler { onBack() }`, tangani `navigationBarsPadding()` / `imePadding()` sesuai kebutuhan.
8. Perbarui status `WAIT` → `DONE` di `TODO.md`.

Lihat Definition of Done lengkap (§14 `AGENTS.md`): interaksi nyata (normal/loading/kosong/sukses/gagal/disabled), benar di terang & gelap, zero-shadow, scroll aman, semantik aksesibilitas, data mock lokal berlabel simulasi, dan build hijau.

---

## Build & Validasi

Jalankan dari root (Windows pakai `.\gradlew.bat`, jangan `&&`):

```powershell
# Validasi utama — wajib hijau untuk tiap perubahan commonMain
.\gradlew.bat :androidApp:assembleDebug

# Build release (unsigned) — JANGAN dianggap rilis siap pakai
.\gradlew.bat :androidApp:assembleRelease

# Tanda tangan APK release (wajib untuk rilis Android)
# ganti path keystore/password sesuai milikmu
apksigner sign --ks release.keystore --out androidApp-release-signed.apk androidApp\build\outputs\apk\release\androidApp-release-unsigned.apk
apksigner verify --verbose androidApp-release-signed.apk   # minimal v2/v3 lulus

# Kompilasi cepat shared (Android)
.\gradlew.bat :shared:compileAndroidMain

# Validasi iOS (butuh macOS)
.\gradlew.bat :shared:compileKotlinIosSimulatorArm64   # simulator Apple Silicon
.\gradlew.bat :shared:compileKotlinIosArm64             # device

# Semua checks
.\gradlew.bat :shared:check

# Unit test logika murni (host JVM, jalan di Windows)
.\gradlew.bat :shared:testAndroidHostTest
# :shared:allTests butuh simulator iOS — tidak jalan di Windows
```

**Pengecekan manual tiap perubahan UI:** mode terang & gelap (via `theme_lab`), font besar, layar kecil/landscape/split-screen, keyboard terbuka (bila ada input).

### Rilis Android

Artefak `*-unsigned.apk` dari `assembleRelease` **tidak boleh** dianggap rilis. Tandatangani dengan `apksigner` (Android SDK build-tools) memakai keystore release, simpan sebagai `androidApp-release-signed.apk`, lalu verifikasi `apksigner verify` (minimal scheme v2/v3).

---

## Roadmap

| Status | Arti |
|--------|------|
| `DONE` | Sudah ada di app (layar + route + MenuEntry + ikon + BackHandler) |
| `WAIT` | Belum dikerjakan |

Saat ini **166 `DONE` · 0 `WAIT`** — sinkron antara `TODO.md` dan `App.kt`.

- Sumber kebenaran: [`TODO.md`](./TODO.md) (§1–§12 + §5.3).
- Bila PRD ditambahkan (`docs/PRD.md` / `PRD.md`), PRD mengambil alih sebagai sumber kebenaran.
- Aturan kontribusi & larangan lengkap: [`AGENTS.md`](./AGENTS.md).

---

## Lisensi

Repositori ini adalah kit eksperimen internal / demo galeri komponen. Belum ada lisensi publik yang ditetapkan. Jangan `git push`, menulis ke layanan eksternal, atau mengirim data pengguna tanpa izin eksplisit (lihat §15 `AGENTS.md`).

---

<p align="center">
  Dibuat dengan ❤️ menggunakan Kotlin Multiplatform & Compose Multiplatform<br/>
  <sub>Flat UI · Zero Shadow · 1px Border · Canvas Icons · Offline Mock</sub>
</p>
