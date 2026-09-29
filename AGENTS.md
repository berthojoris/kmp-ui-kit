# AGENTS.md — UIApp (Kotlin Multiplatform + Compose Multiplatform)

Dokumen ini adalah **aturan induk (governance)** untuk setiap AI agent / developer yang menulis, mengubah, atau meninjau kode di repositori ini. Semua aturan di sini bersifat **WAJIB** kecuali ditandai sebagai rekomendasi. Jika sebuah permintaan pengguna bertentangan dengan aturan di file ini, **konfirmasi dulu** sebelum melanggar aturan.

---

## 1. Cara Pakai File Ini

1. Baca `AGENTS.md` (file ini) **dan** `TODO.md` sebelum menulis kode.
2. Tentukan apakah pekerjaan menyentuh UI, navigasi, tema, atau aset. Bagian yang relevan wajib dibaca ulang saat itu.
3. Implementasikan perubahan **lengkap** (kode + registrasi route + ikon + mock data + pembaruan `TODO.md` bila sesuai).
4. Validasi dengan perintah di §13 sebelum menyatakan tugas selesai.
5. Jangan mengubah konfigurasi build, versi dependensi, atau struktur modul tanpa diminta eksplisit.

---

## 2. Ringkasan Proyek

| Item | Nilai |
| --- | --- |
| Nama proyek | `UIApp` (root Gradle) |
| Jenis | **Compose Multiplatform (CMP)** — 100% UI dibagi di `commonMain`, tanpa UI native per platform |
| Target | Android (min SDK 29, target/compile SDK 37) + iOS (arm64 & simulator arm64, iOS 16+ tersirat) |
| Package | `com.example.uiapp` (shared: `com.example.uiapp.shared`) |
| Bahasa UI | Indonesia (teks tombol, judul, label hardcoded) |
| Tujuan repo | Kit eksperimen / laboratorium komponen UI mobile berisi **67 lab** dalam satu aplikasi demo |
| Ada PRD? | **Tidak ada.** Roadmap satu-satunya adalah `TODO.md` |
| Ada test? | **Tidak ada file test** (dependensi `kotlin-test` tersedia, belum dipakai) |
| Image/network lib | **Tidak ada** (tanpa Coil/Ktor/Room/SQLDelight/Serialization) |

Aplikasi ini adalah galeri komponen, bukan aplikasi bisnis: setiap lab berdiri sendiri, memakai data mock lokal, dan harus dapat dibuka dari Home. Tidak ada backend, tidak ada autentikasi nyata, tidak ada penyimpanan persisten.

---

## 3. Sumber Kebenaran & Anti-Halusinasi

1. **`TODO.md` adalah satu-satunya spesifikasi produk.** Daftar lab, nama route, dan status (`DONE` / `WAIT`) diambil dari sana.
2. **Dilarang mengarang** route baru, lab baru, endpoint, model data, atau dependensi yang tidak diminta pengguna. Jika lab yang diminta belum ada di `TODO.md`, tanyakan lebih dulu atau tambahkan entri `TODO.md` terlebih dahulu.
3. **Dilarang mengklaim integrasi nyata.** Semua perilaku yang menyerupai layanan platform (kamera, biometrik, push notification, pembayaran, AI, peta) wajib diberi label **simulasi** di UI dan tidak boleh diklaim terhubung ke layanan sungguhan.
4. Jika nanti repositori ini berkembang menjadi produk nyata dan sebuah PRD ditambahkan (`docs/PRD.md` atau `PRD.md`), **PRD tersebut mengambil alih** posisi `TODO.md` sebagai sumber kebenaran, dan setiap field/model/aturan di PRD harus diikuti byte-for-byte.
5. `TODO.md` saat ini **belum sinkron** dengan kode: seluruh item §6 sudah diimplementasikan dan terdaftar di `App.kt`, tetapi masih ditandai `WAIT`. Setiap kali mengerjakan/menutup sebuah lab, **perbarui statusnya di `TODO.md` pada commit yang sama**.

---

## 4. Topologi & Batas Modul

```
testingUI/
├─ androidApp/                     # HANYA entry point Android
│  └─ src/main/kotlin/com/example/uiapp/MainActivity.kt
├─ iosApp/                         # HANYA entry point iOS (SwiftUI host)
│  └─ iosApp/ContentView.swift     # ComposeView + .ignoresSafeArea()
├─ shared/                         # SELURUH kode aplikasi
│  ├─ src/commonMain/kotlin/com/example/uiapp/
│  │  ├─ App.kt                    # root composable + tabel routing
│  │  ├─ navigation/AppNavigation.kt
│  │  ├─ theme/{UiAppTheme,AppPalette,LuxuryColors}.kt
│  │  └─ ui/
│  │     ├─ components/            # komponen bersama (UiTopBar, UiIcons, AuthKit)
│  │     └─ <feature>/<Feature>Screen.kt   # satu folder per lab
│  ├─ src/androidMain/             # actual Android (Platform.android.kt)
│  └─ src/iosMain/                 # actual iOS + MainViewController.kt
├─ gradle/libs.versions.toml       # version catalog (satu-satunya sumber versi)
├─ AGENTS.md                       # file ini
└─ TODO.md                         # roadmap lab
```

Aturan batas modul:

- **Seluruh UI wajib di `commonMain`.** Dilarang menambah layar Compose/SwiftUI baru khusus satu platform.
- `androidApp/` dan `iosApp/` hanya boleh berisi kode bootstrap. Jangan menaruh logika atau layar di sana.
- Kode `androidMain` / `iosMain` hanya untuk `expect`/`actual` yang benar-benar dibutuhkan (saat ini hanya `getPlatform()` di `Platform.kt`).
- Setiap lab baru = **satu folder** `ui/<namalab>/` berisi satu file layar utama (dan opsional satu file data mock), tidak menyentuh folder lab lain.

---

## 5. Toolchain & Dependensi

Versi bersumber dari `gradle/libs.versions.toml` — **jangan diubah tanpa permintaan eksplisit**:

| Komponen | Versi |
| --- | --- |
| Kotlin | 2.4.20 |
| Compose Multiplatform | 1.12.0 |
| Material 3 (multiplatform) | 1.12.0-alpha03 |
| Android Gradle Plugin | 9.1.1 |
| Lifecycle (JetBrains) | 2.11.0 |
| JVM target | 11 |
| compileSdk / targetSdk / minSdk | 37 / 37 / 29 |

Dependensi yang tersedia di `commonMain`: `compose.runtime`, `compose.animation`, `compose.foundation`, `compose.material3`, `compose.ui`, `compose.uiBackhandler`, `compose.components.resources`, `compose.uiToolingPreview`, `androidx.lifecycle.viewmodelCompose`, `androidx.lifecycle.runtimeCompose`.

Aturan:

- **Dilarang menambah dependensi baru** (Coil, Ktor, Room, Navigation-Compose, Koin, dsb.) tanpa persetujuan pengguna. Seluruh lab dapat diselesaikan dengan Compose Foundation + Canvas.
- `org.gradle.configuration-cache=true` aktif: jangan menulis logika build yang memecah configuration cache (akses environment/file saat konfigurasi, task non-serializable).
- Plugin yang dipakai: `kotlinMultiplatform`, `androidMultiplatformLibrary`, `androidApplication`, `composeMultiplatform`, `composeCompiler`. iOS framework: `baseName = "Shared"`, `isStatic = true`.
- Tidak ada linter/formatter (detekt/ktlint) di repo ini. Kerapian kode bergantung pada disiplin manual mengikuti §11.

---

## 6. Arsitektur & Pola Wajib

Repositori ini **belum memakai Clean Architecture berlapis** (tanpa domain/data/presentation, tanpa repository). Jangan memperkenalkan lapisan arsitektur baru untuk sebuah lab demo. Yang berlaku sekarang:

### 6.1 Navigasi & Routing (kustom, tanpa Navigation-Compose)

Navigasi dikelola manual di `navigation/AppNavigation.kt`:

- `AppNavController` menyimpan `backstack: List<String>` sebagai `mutableStateOf`, dengan API: `navigate`, `pop`, `popTo`, `replace`, `popToRoot`, `resetTo`, `canPop`, `currentRoute`.
- `LocalNavController` (`staticCompositionLocalOf`) menyediakan controller ke seluruh pohon.
- Root `App()` di `App.kt` melakukan routing dengan `when (navController.currentRoute) { ... else -> HomeMenuScreen(...) }`.

**Checklist wajib menambah route/lab baru (semua langkah harus dikerjakan):**

1. Buat file `ui/<namalab>/<Nama>Screen.kt` dengan signature `fun <Nama>Screen(onBack: () -> Unit)`.
2. Tambahkan `private const val Route<Nama> = "<snake_case_route>"` di `App.kt` (ikuti pola penamaan yang ada).
3. Tambahkan route tersebut ke himpunan `KnownRoutes` di `App.kt`.
4. Tambahkan cabang `Route<Nama> -> <Nama>Screen(onBack = { navController.pop() })` di dalam `when`.
5. Tambahkan `MenuEntry(id, title, subtitle, enabled = true)` di `HomeMenuScreen.kt` (`MenuEntries`), lengkap dengan `subtitle` berbahasa Indonesia.
6. Tambahkan ikon menu khusus `<Nama>MenuIcon(tint: Color)` di `ui/components/UiIcons.kt` dan pasang cabangnya di `when (entry.id)` pada `MenuGridCard`.
7. Perbarui status lab di `TODO.md`.
8. Jangan mengubah signature screen lain atau menghapus route yang sudah ada.

Aturan tambahan:

- Nama route memakai `snake_case` dan **sama persis** dengan `MenuEntry.id`.
- Satu file layar hanya boleh memiliki satu composable publik (layar utama). Semua sub-komponen wajib `private`.
- Dilarang memanggil `navigate()` langsung dari dalam layar tanpa `LocalNavController`/callback; layar hanya menerima `onBack`/`onFinish` dari `App.kt`, kecuali layar tersebut memang memegang `LocalNavController` untuk kasus khusus (mis. command palette).

### 6.2 Back Handling (WAJIB, dua lapis)

- Root: `BackHandler(enabled = navController.canPop) { navController.pop() }` di `App.kt`.
- Per layar: `BackHandler(enabled = true) { onBack() }` (atau `enabled = <kondisi>`) **wajib hadir** di setiap layar lab, dengan import `androidx.compose.ui.backhandler.BackHandler` (Compose Multiplatform).
- Modal/dialog/sheet yang terbuka **harus** menangani back untuk menutup dirinya, bukan menutup layar.
- Dilarang memanggil `finish()`/`Activity` dari `commonMain`.
- iOS: gesture back Compose Multiplatform aktif secara default (`enableBackGesture = true` pada `ComposeUIViewController`). Jangan menonaktifkannya, karena swipe-back iOS memanggil `BackHandler` yang sama sehingga perilakunya konsisten dengan Android. `MainViewController.kt` hanya boleh diberi `configure = { ... }` bila diminta.

### 6.3 Tema & Warna (WAJIB)

Sistem tema: `UiAppTheme` menyediakan `LocalAppPalette` (`AppPalette`) dan `LocalThemeController` (`ThemeMode.LIGHT|DARK|SYSTEM`), lalu meneruskannya ke `MaterialTheme`.

- **Wajib**: ambil warna lewat `val palette = LocalAppPalette.current` di setiap composable yang butuh warna. Gunakan field `AppPalette` (`background`, `surface`, `surfaceMuted`, `border`, `textPrimary`, `textSecondary`, `textMuted`, `primary`, `onPrimary`, `primaryContainer`, `onPrimaryContainer`, `success`, `warning`, `info`, `danger`).
- **Dilarang** menulis warna hardcoded baru (`Color(0xFF...)`) untuk permukaan, teks, atau border pada kode baru. Warna hardcoded hanya boleh untuk kasus sangat spesifik: data visual buatan (gradien ilustrasi mock, palet chart) dan warna status pill pada data mock.
- `theme/LuxuryColors.kt` adalah **palet legacy statis (light-only)**. Jangan gunakan pada kode baru. Saat menyentuh file yang masih memakainya, migrasikan ke `LocalAppPalette` (§14).
- Setiap layar baru **wajib** benar di mode Terang dan Gelap. Uji dengan mengubah mode di `ThemeLabScreen` (`theme_lab`).

### 6.4 State Management

- Pola yang berlaku: state lokal Compose — `remember { mutableStateOf(...) }` / `mutableIntStateOf`, turunan `derivedStateOf` bila perlu, efek samping `LaunchedEffect`, korutinitas `rememberCoroutineScope()`, animasi `animate*AsState` / `Animatable` / `rememberInfiniteTransition`.
- **Dilarang** `GlobalScope`, `runBlocking`, dan thread blocking di composable.
- `rememberCoroutineScope()` tidak boleh dipakai untuk pekerjaan yang harus bertahan melewati perubahan konfigurasi.
- ViewModel hanya boleh dibuat bila sebuah lab benar-benar butuh state yang bertahan (`androidx.lifecycle.ViewModel` + `viewModel()` dari `androidx.lifecycle.viewmodel-compose`, sudah tersedia di `commonMain`). Jika dipakai, state harus `@Immutable` dan dibaca dengan `collectAsStateWithLifecycle()`.
- Tidak ada DI framework. Jangan menambahkan service locator/singleton global.

### 6.5 Komponen Bersama

Sebelum membuat komponen baru, periksa `ui/components/`:

- `UiTopBar(title, subtitle, onBack, action)` — **wajib** dipakai sebagai top bar standar setiap layar. Jangan membuat top bar baru.
- `UiIcons.kt` — seluruh ikon berupa vektor `Canvas` (`StrokeCap.Round`, `StrokeJoin.Round`), tidak memakai font/ikon bitmap. Setiap entry Home punya ikon sendiri; ikon wajib menerima parameter `tint`.
- `MgIosBackButton` / `MgIosBackChevron` — tombol kembali bergaya iOS (38dp, 1px border, zero shadow, `contentDescription = "Kembali"`).
- `AuthKit.kt` (`FlatTextField`, `FlatPrimaryButton`, `FlatSecondaryButton`, `FlatToggle`) — komponen form Flat UI. Catatan: file ini masih terikat `LuxuryColors` statis.

Larangan: memakai komponen Material3 bergaya default yang membawa elevasi/shadow (`ElevatedCard`, `Card` dengan elevation, `ElevatedButton`, `FilledTonalButton` dengan tonal elevation). Bila memakai `Surface`, **wajib** menetapkan `shadowElevation = 0.dp` dan `tonalElevation = 0.dp`.

### 6.6 Data Mock

- Data mock dideklarasikan lokal: `private val` di dalam file layar, atau file `*DemoData.kt` di folder lab (contoh: `ui/blurscroll/AutoBlurDemoData.kt`).
- Konten mock (judul kartu, nama orang, nominal, tanggal) ditulis dalam **Bahasa Indonesia** dan konsisten dengan domain lab (transaksi, villa, tamu, dsb.).
- Dilarang melakukan panggilan jaringan, membaca data dari disk, atau menambahkan seed database.
- Setiap daftar mock wajib memiliki `key` stabil pada item lazy list (`items(list, key = { it.id })`).

---

## 7. Desain Visual: Flat UI Universal Zero-Shadow (WAJIB)

1. **Dilarang total**: `Modifier.shadow(...)`, `shadowElevation > 0.dp`, `cardElevation > 0.dp`, `tonalElevation > 0.dp`, dan efek "kaca"/blur sebagai lapisan wajib.
2. Rel/edge harus dibentuk dari **border 1px** (`BorderStroke(1.dp, palette.border)` atau `Modifier.border(1.dp, ...)`).
3. Permukaan memakai `palette.surface` / `palette.surfaceMuted`; mode terang = putih/mendekati putih.
4. Radius sudut konsisten: kecil `10–12dp`, kartu `16–20dp`, pill/chip penuh.
5. Tipografi: judul layar Home memakai `FontFamily.Serif` untuk kesan editorial; label/body memakai font default dengan `FontWeight.SemiBold` untuk judul kartu dan `textSecondary`/`textMuted` untuk metadata.
6. Setiap kartu wajib punya padding internal yang konsisten (12–16dp) dan `maxLines` + `TextOverflow.Ellipsis` pada teks yang bisa panjang.
7. Efek kaca/translucent hanya sebagai **opsi demo** (mis. `native_surfaces`), bukan standar seluruh aplikasi, dan wajib memiliki fallback kontras tinggi.

---

## 8. Layout, Insets, Adaptive, IME, Anti-Clipping

### 8.1 Insets & edge-to-edge

- Android: `MainActivity` memanggil `enableEdgeToEdge()`. iOS: `ContentView` memakai `.ignoresSafeArea()`. Artinya **konten mengalir di bawah system bar** dan insets harus ditangani manual.
- Top bar: `UiTopBar` sudah menangani `statusBarsPadding()`. Layar yang tidak memakai `UiTopBar` (splash, gallery full screen, dsb.) wajib menambahkan `statusBarsPadding()` atau membaca `WindowInsets.statusBars` sendiri.
- **Wajib**: setiap elemen aksi/konten yang menyentuh dasar layar (bottom bar, action bar, tombol submit, FAB, sheet) harus memakai `Modifier.navigationBarsPadding()` agar tidak tertutup navigation bar 3-tombol / home indicator. Jangan mengandalkan padding tetap seperti `padding(bottom = 16.dp)` saja.
- Alternatif yang diterima: `Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))` seperti pada `MotionLabScreen`, `GalleryScreen`, `FeedbackLabScreen`.

### 8.2 IME / Keyboard

- Setiap layar dengan input teks (form, chat, search, komentar) **wajib** memberi `Modifier.imePadding()` pada container yang menampung tombol aksi/konten bawah. Contoh acuan: `FormLabScreen`, `AiChatLabScreen`.
- Di Compose Multiplatform, `Modifier.safeDrawingPadding()` juga mencakup insets IME (keyboard) pada iOS. Di Android tetap gunakan `imePadding()`. Jangan menumpuk keduanya pada container yang sama agar padding tidak dobel.
- `AndroidManifest.xml` saat ini **belum** menetapkan `android:windowSoftInputMode="adjustResize"`; lihat §14.

### 8.3 Adaptive & breakpoint

- Gunakan `BoxWithConstraints` untuk mendeteksi `maxWidth`/`maxHeight` dan memilih layout (contoh: `AdaptiveScreen`, `AdaptiveNavLabScreen`).
- Breakpoint yang dipakai repo: `< 480dp` compact (1 kolom), `< 760dp` medium (2 kolom), `< 1040dp` expanded (3 kolom), sisanya large (4 kolom).
- Layar harus tetap wajar di mode landscape, split-screen, dan font besar.
- Grid Home memakai `GridCells.Fixed(2)` dan **sengaja** tetap 2 kolom; jangan mengubahnya tanpa diminta.

### 8.4 Anti-clipping

- Konten statis (bukan lazy) yang berisi teks + tombol **wajib** dibungkus `Modifier.verticalScroll(rememberScrollState())`.
- `LazyColumn`/`LazyVerticalGrid` **tidak boleh** bersarang di dalam `verticalScroll` (menyebabkan crash); pilih salah satu.
- Bottom sheet/modal harus dibatasi tinggi maksimum dan tetap dapat di-scroll.

### 8.5 Tombol & target sentuh

- Tinggi tombol utama mengikuti pola repo: `padding(vertical = 14–16dp)` dengan `maxLines = 1`, `overflow = TextOverflow.Ellipsis`.
- Target sentuh minimum 48dp (Android) / 44pt (iOS). Untuk ikon kecil, bungkus dengan `Box(size = 44–48dp)`.
- Grup chip/tag dinamis memakai `FlowRow`, bukan `Row` yang bisa terpotong.

---

## 9. Aksesibilitas (WAJIB)

- Setiap elemen interaktif yang tidak punya teks nyata (ikon, canvas, swipe area) **wajib** memiliki `contentDescription` atau memakai `semantics { role = ...; contentDescription = ... }` (lihat `MgIosBackButton`).
- Informasi status (PAID/PENDING/ERROR, aktif/nonaktif, sukses/gagal) **tidak boleh** dibedakan hanya dengan warna; sertakan label teks.
- Jangan menghapus kemampuan `FontFamily`/`fontSize` adaptif; teks harus tetap terbaca pada skala font besar (uji hingga 150–200%).
- Animasi harus tetap dapat dinikmati pada reduce-motion; sediakan jalur non-animasi bila relevan.
- `ThemeLabScreen` (mode gelap), `AccessibilityLabScreen` (skala teks/kontras/reduce motion) adalah tempat uji standar perubahan UI.

---

## 10. Ikon & Aset

- **Semua ilustrasi/ikon adalah vektor Canvas lokal.** Tidak ada aset piksel, tidak ada image loader, tidak ada akses jaringan. Pertahankan keadaan ini.
- Ikon baru: fungsi `@Composable fun <Nama>MenuIcon(tint: Color = ..., size: Dp = ...)` di `ui/components/UiIcons.kt`, digambar dengan `Canvas`/`Path`/`drawPath`, memakai `StrokeCap.Round` & `StrokeJoin.Round`, dan memakai `tint` (bukan warna hardcoded).
- Ilustrasi mock (foto pengganti) dibuat dengan gradien (`Brush.linearGradient`) + bentuk geometris, bukan gambar bitmap.
- **Deteksi field gambar API**: setiap field model/entitas yang bernama `imageUrl`, `avatarUrl`, `thumbnailUrl`, `bannerUrl`, `photoUrl`, `iconUrl`, atau sejenisnya **tidak boleh** dibiarkan `null`/`""`.
  - Selama repo tanpa jaringan: isi dengan **sumber visual mock lokal** (gradien/palet) dan tandai sebagai simulasi.
  - Bila suatu saat image loader ditambahkan (butuh persetujuan pengguna, Coil 3 di `commonMain`), seluruh mock wajib memakai URL Unsplash terkurasi sesuai domain dengan parameter hemat RAM: `?auto=format&fit=crop&w={width}&q=80` (`w=256` avatar, `w=600` kartu, `w=1080` banner), plus fallback URL Unsplash saat nilai kosong, shimmer placeholder, dan state error.
- Dilarang mengunduh/menyimpan aset gambar ke repositori.

---

## 11. Performa & Memori (Anggaran 2GB–3GB RAM)

1. Dilarang mengalokasikan koleksi baru (`listOf()`, `mapOf()`, `List(n)` besar, builder) di dalam body `@Composable` tanpa `remember`.
2. `remember` wajib untuk perhitungan mahal, `Brush`, `Path`, `TextStyle`, dan state turunan.
3. Lazy list wajib memakai `key` stabil.
4. Efek yang mendaftarkan listener/timer/animation loop harus dibersihkan: gunakan `DisposableEffect { onDispose { ... } }`.
5. `LaunchedEffect` harus memakai key yang tepat agar tidak restart di setiap recomposition; hindari key berbentuk objek baru setiap recompose.
6. Dilarang menyimpan referensi `Activity`, `Context`, `UIViewController`, atau view platform di state global/singleton.
7. Animasi tak berujung (`rememberInfiniteTransition`) hanya untuk elemen kecil (shimmer, pulse, laser line), bukan untuk seluruh layar.
8. Jangan memuat/menyimpan seluruh daftar besar tanpa lazy layout.
9. Tidak ada `Modifier.graphicsLayer`/blur/`renderEffect` pada seluruh layar secara terus-menerus; batasi pada area kecil, dan hitung dengan lambda `graphicsLayer { }` (bukan nilai langsung) untuk menghindari recomposition.

---

## 12. Konvensi Kode & Penamaan

- Navigasi file: folder lab = `ui/<namalab>/` (contoh `ui/bentogrid/`), file layar = `<Nama>LabScreen.kt` untuk lab baru (§3/§5 TODO) atau nama fungsional untuk layar alur (mis. `AuthScreen.kt`, `SplashScreen.kt`).
- Composable publik: `@Composable fun <Nama>Screen(onBack: () -> Unit)`; sub-komponen `private` dengan nama deskriptif.
- Tipe data mock: `private data class` + `private val` di file yang sama; enum status memakai `enum class` (`DemoStatus`, `DemoState`).
- Konstanta route: `private const val Route<X> = "..."` di `App.kt`.
- Warna: akses via `palette.<field>`; `snake_case` untuk id route, `camelCase` untuk nama field, `PascalCase` untuk tipe.
- Komentar: gunakan komentar singkat berbahasa Indonesia seperlunya; jangan menambah komentar yang hanya mengulang kode.
- Import: eksplisit per simbol (tidak ada wildcard), dikelompokkan `androidx.*` lalu `com.example.uiapp.*` (lihat file yang ada sebagai acuan).
- Teks UI: hardcoded literal berbahasa Indonesia mengikuti gaya repo; jangan memperkenalkan sistem resource/string baru tanpa persetujuan.
- Dilarang menambahkan kode `TODO`/`FIXME` baru tanpa alasan yang jelas; jangan menambahkan abstraksi yang belum diminta (interface untuk satu implementasi, factory, dsb.).
- Hapus kode mati yang sudah tidak dipakai, kecuali file template (`Greeting.kt`, `GreetingUtil.kt`) yang masih dipertahankan sebagai contoh.

---

## 13. Build, Validasi, dan Test

Jalankan perintah dari root repositori (Windows: gunakan `.\gradlew.bat`, jangan `&&`):

```powershell
.\gradlew.bat :androidApp:assembleDebug              # build APK debug (validasi utama Android)
.\gradlew.bat :shared:compileAndroidMain             # kompilasi cepat shared untuk Android
.\gradlew.bat :shared:compileKotlinIosSimulatorArm64 # validasi iOS simulator (Apple Silicon)
.\gradlew.bat :shared:compileKotlinIosArm64          # validasi iOS device
.\gradlew.bat :shared:check                           # semua check yang tersedia
.\gradlew.bat :shared:testAndroidHostTest             # unit test host Android (kompilasi hostTest)
.\gradlew.bat :shared:allTests                        # semua test target (butuh simulator iOS)
```

Aturan validasi:

- **Minimal** jalankan `:androidApp:assembleDebug` untuk perubahan apa pun pada `commonMain`.
- Untuk perubahan UI, lakukan juga pengecekan manual di preview/emulator: mode terang & gelap, font besar, layar kecil, keyboard terbuka (bila ada input).
- iOS divalidasi dengan membuka `iosApp/iosApp.xcodeproj` di Xcode; framework `Shared` (static) dibangun oleh Gradle. Jangan mengubah `Config.xcconfig`/`project.pbxproj` tanpa permintaan.
- Tidak ada test otomatis saat ini. Jika menambahkan test baru, letakkan di `shared/src/commonTest/kotlin/...` dan pastikan `:shared:allTests` hijau. Jangan membuat test yang membutuhkan jaringan.
- Jangan menyatakan pekerjaan selesai bila kompilasi gagal. Laporkan perintah yang dijalankan, hasilnya, dan yang dilewati secara jujur.
- Jangan commit artefak build (`build/`, `.gradle/`, `.kotlin/`, `local.properties`, `xcuserdata` sudah di-`.gitignore`).

---

## 14. Definition of Done — Lab/Komponen Baru

Sebuah lab dinyatakan selesai hanya bila **semua** poin berikut terpenuhi:

- [ ] Route terdaftar di `App.kt` (`Route<X>`, `KnownRoutes`, cabang `when`) dan dapat dibuka dari Home.
- [ ] Entry `MenuEntry` ada di `HomeMenuScreen.kt` dengan judul + subtitle Indonesia.
- [ ] Ikon khusus tersedia di `UiIcons.kt` dan terpasang di `MenuGridCard`.
- [ ] `BackHandler` aktif dan `onBack` bekerja.
- [ ] Demo memiliki interaksi nyata dengan state: normal, loading, kosong, sukses, gagal, dan disabled (sesuai relevansi).
- [ ] Warna berasal dari `LocalAppPalette`; tampilan benar di mode terang **dan** gelap.
- [ ] Zero shadow: tidak ada elevasi/shadow > 0; border 1px.
- [ ] Insets: `navigationBarsPadding()` pada elemen bawah, `imePadding()` pada layar berinput, `statusBarsPadding()` bila tanpa `UiTopBar`.
- [ ] Layout wajar di layar kecil, lebar, landscape, dan font besar; konten dapat di-scroll (tidak terpotong).
- [ ] Elemen interaktif punya label/semantik bermakna; status tidak dibedakan hanya dengan warna.
- [ ] Data mock lokal dan diberi label simulasi bila menyerupai layanan platform.
- [ ] Status lab diperbarui di `TODO.md`.
- [ ] `.\gradlew.bat :androidApp:assembleDebug` sukses.

---

## 15. Larangan Keras (Prohibited)

1. Menambah/mengubah versi dependensi, plugin, atau konfigurasi Gradle tanpa permintaan eksplisit.
2. Membuat UI native terpisah untuk Android/iOS, atau memindahkan UI keluar dari `commonMain`.
3. Memakai `Modifier.shadow`, `shadowElevation > 0.dp`, `cardElevation > 0.dp`, `tonalElevation > 0.dp`.
4. Menulis warna hardcoded baru untuk surface/teks/border, atau memakai `LuxuryColors` pada kode baru.
5. Memanggil jaringan, filesystem, database, atau API platform dari `commonMain`.
6. `GlobalScope`, `runBlocking`, blocking I/O di main thread.
7. Menghapus/mengubah route, lab, komponen bersama, atau signature layar yang sudah ada tanpa diminta.
8. Menambah dependensi image loader/network tanpa persetujuan (§10).
9. Mengklaim fitur simulasi sebagai integrasi nyata.
10. Melakukan `git push`, menulis ke layanan eksternal, atau mengirim data pengguna tanpa izin eksplisit.
11. Mengubah `master` secara destruktif (force push, reset --hard, hapus file pengguna).

---

## 16. Utang Teknis & Deviasi yang Diketahui

Ini kondisi **saat ini** yang menyimpang dari aturan di atas. Jangan menganggapnya sebagai pola yang boleh ditiru; perbaiki saat menyentuh file terkait, dan jangan memperluas penyimpangannya.

| # | Deviasi | Dampak | Tindakan saat menyentuh file |
| --- | --- | --- | --- |
| 1 | 27 file di `ui/` masih memakai `LuxuryColors` (light-only) — termasuk `home`, `auth`, `permissions`, `passcode`, `onboarding`, `biometric`, `profilesetup`, `adaptive`, `charts`, `formlab`, `emptystate`, `multiselect`, `components/AuthKit`, `components/UiIcons`, dll. | Lab tersebut tidak ikut mode gelap walau `TODO.md` mengklaim rollout penuh | Migrasikan ke `LocalAppPalette` |
| 2 | `AndroidManifest.xml` belum menetapkan `android:windowSoftInputMode="adjustResize"` dan memakai tema `Theme.Material.Light.NoActionBar` (light-only, bukan Material3) | Keyboard bisa menutupi input; status bar Android tidak mengikuti tema app | Tambahkan `adjustResize` dan tema Material3 saat tugas menyentuh Android |
| 3 | `navigationBarsPadding()` / `imePadding()` belum diterapkan merata (baru ±20 file lab) | Tombol di dasar layar berisiko tertutup navigation bar / keyboard pada lab lama | Terapkan saat menyentuh layar terkait |
| 4 | `TODO.md` menandai item §6 sebagai `WAIT` padahal sudah diimplementasikan (`ai_chat`, `selection_toolbar`, `undo_queue`, `adaptive_navigation`, `expressive_controls`, `accessibility_lab`, `activity_inbox`, `resume_form`, `native_surfaces`) | Roadmap menyesatkan | Perbarui status pada perubahan berikutnya |
| 5 | Belum ada satu pun test; `kotlin-test` + konfigurasi host/device test sudah disiapkan | Regresi tidak terdeteksi otomatis | Tambahkan test `commonTest` bila mengerjakan logika non-UI |
| 6 | `Greeting.kt`, `GreetingUtil.kt` adalah sisa template; `platform` hanya dipakai untuk teks header Home | Kode mati | Hapus hanya jika diminta |
| 7 | `iosArm64X64` dan `iosX64` tidak ditargetkan (hanya arm64 + simulator arm64) | Build iOS di Mac Intel tidak didukung | Jangan tambah target tanpa diminta |
| 8 | Komponen hanya didefinisikan di file layar (banyak `private` duplikat antar lab, mis. bottom bar/segmented control dibuat ulang) | Duplikasi kode | Ekstrak ke `ui/components/` saat komponen yang sama muncul minimal tiga kali |

---

## 17. Git & Dokumentasi

- Repositori memiliki satu commit awal (`Start`); belum ada konvensi pesan commit yang mapan. Gunakan pesan imperatif singkat berbahasa Inggris, misalnya `Add swipe_cards lab route and icon`, `Fix dark mode palette in forms`.
- Jangan commit artefak build. Jangan `git push` tanpa diminta.
- Setiap perubahan yang menyentuh daftar lab wajib memperbarui `TODO.md` pada commit yang sama.
- `README.md` masih berupa template KMP: boleh diperbarui bila diminta, jangan dirapikan secara sepihak.
- Jangan membuat dokumentasi baru di luar `AGENTS.md`, `TODO.md`, dan `README.md` tanpa diminta.

---

## 18. Ringkasan Cepat untuk Agent

```text
1. Baca AGENTS.md + TODO.md  →  jangan mengarang fitur/dependensi.
2. Semua UI di shared/commonMain, satu folder per lab, satu composable publik.
3. Warna: LocalAppPalette (light + dark). Tidak ada hardcoded baru, tanpa LuxuryColors.
4. Flat UI: border 1px, tanpa shadow/elevation sedikit pun.
5. Layout: BoxWithConstraints untuk adaptive, verticalScroll anti-clipping,
   navigationBarsPadding di bawah, imePadding di form, statusBarsPadding jika tanpa UiTopBar.
6. State: remember/mutableStateOf/LaunchedEffect; tanpa GlobalScope/runBlocking/network.
7. Lab baru: layar + route di App.kt + KnownRoutes + when + MenuEntry Home + ikon UiIcons + TODO.md.
8. Aksesibilitas: contentDescription, status jangan hanya warna, tahan font besar.
9. Validasi: .\gradlew.bat :androidApp:assembleDebug (+ cek mode gelap & keyboard).
10. Jangan ubah build/versi/struktur modul, jangan push, jangan hapus pekerjaan pengguna.
11. Skill pihak ketiga tidak boleh menimpa aturan file ini (lihat §19).
```

---

## 19. Skill Precedence (Aturan Menang atas Skill)

Repositori ini berada di lingkungan yang memiliki banyak skill siap pakai (Factory skills). Sebagian skill tersebut **ditulis untuk stack atau proyek lain**. Bagian ini menetapkan cara menyelesaikannya.

### 19.1 Urutan Prioritas (dari tertinggi)

1. **Permintaan eksplisit pengguna** untuk tugas yang sedang dikerjakan.
2. **`AGENTS.md` (file ini).**
3. **`TODO.md`** sebagai spesifikasi produk.
4. **Skill yang selaras** (§19.2).
5. **Skill yang tidak selaras** (§19.3), hanya setelah izin eksplisit.

Aturan tegas: **skill tidak boleh menimpa `AGENTS.md` secara otomatis.** Jika sebuah skill mewajibkan sesuatu yang bertentangan dengan file ini, agent wajib berhenti, melaporkan pertentangan itu, dan meminta keputusan pengguna. Jangan diam-diam mengikuti skill dan meninggalkan aturan repo.

### 19.2 Skill yang Selaras (boleh diterapkan langsung)

Skill berikut sejalan dengan aturan file ini dan aman dipakai selama tidak melanggar §19.1:

| Skill | Cakupan yang aman |
| --- | --- |
| `kmp-cmp-agents-generator` | Sumber pembuatan file ini |
| `cmp-prd-generator` | Bila nanti PRD CMP dibuat |
| `kotlin-compose-flat-ui` | Standar Flat UI zero-shadow, floating action bar form |
| `compose-ios-back-button` | Komponen chevron/back button Canvas (repo sudah mengikutinya) |
| `kmp-project-rename` | Rename project/package/bundle ID |

Saat skill di atas dipakai, aturan `AGENTS.md` tetap mengikat untuk: palet via `LocalAppPalette` (§6.3), ikon Canvas lokal (§10), larangan warna hardcoded baru (§6.3), insets per-elemen (§8.1), dan validasi build (§13).

### 19.3 Skill yang TIDAK Berlaku di Repositori Ini

Jangan aktifkan atau terapkan skill berikut tanpa izin eksplisit. Jika diaktifkan, abaikan mandat stack-nya dan tetap ikuti `AGENTS.md`.

**a. Stack berbeda (proyek lain):** `button-loading-state` dan `claude-design` mengasumsikan proyek Laravel + Inertia + React + Tailwind dengan primitif shadcn (`@/components/ui`) dan merujuk "AGENTS.md" milik proyek lain. `android-prd-generator` untuk Android native. `flutter-prd-generator`, `flutter-type-safety-handler`, `flutter-to-cmp-porting`, `convert-to-offline-mock` untuk Flutter. `nextjs-mysql-prd-generator`, `nestjs-prisma-api-prd-generator`, `wails-windows-prd-generator`, `landing-page-design` di luar cakupan mobile.

**b. Mandat API yang usang terhadap Compose Multiplatform 1.12:**

- `compose-back-press-handler` dan `compose-luxury-clean-ui` §5 mewajibkan `expect/actual BackHandler` dengan iOS no-op. Repo memakai artefak resmi `compose.uiBackhandler` (`androidx.compose.ui.backhandler.BackHandler`) yang menangani iOS secara native. **Pertahankan yang ada.** Diverifikasi lewat Context7: `ComposeUIViewController` mengaktifkan gesture back secara default, jadi `BackHandler` sudah berfungsi di iOS tanpa bridge `expect/actual`.
- Skill tersebut juga mengasumsikan `NavHost`, `popUpTo(0)`, dan `finishAffinity()`. Repo memakai `AppNavController` berbasis string route (§6.1) dan melarang pemanggilan `finish()` dari `commonMain` (§6.2). Padanan yang berlaku: `popToRoot()` dan `BackHandler(enabled = navController.canPop)`.

**c. Bertentangan langsung dengan aturan wajib file ini:**

| Skill | Mandat skill | Aturan repo yang menang |
| --- | --- | --- |
| `kmp-cmp-edge-blur-scroll` | Dilarang `safeDrawingPadding()`/`statusBarsPadding()` pada list edge-to-edge | §8.1: insets per-elemen; larangan itu sudah tercakup di sana untuk kasus edge-blur. Untuk list edge-blur, ikuti skill ini karena tidak bertentangan dengan repo |
| `flutter-to-cmp-porting` | Deferred-build: dilarang menjalankan Gradle sampai semua tugas selesai; `TODO.md` wajib `[PENDING]/[IN_PROGRESS]/[COMPLETED]` | §13: `assembleDebug` wajib dijalankan untuk setiap perubahan `commonMain`; `TODO.md` repo memakai `DONE`/`WAIT` |
| `kotlin-compose-ios-notification-banner` | "Pure light theme aesthetics" dan `#FFFFFF` hardcoded | §6.3: wajib `LocalAppPalette` dan benar di mode gelap |
| `compose-flat-modal-notification`, `compose-luxury-clean-ui` | Konstanta hex tetap dan `Icons.Rounded.*` / Material Icon | §6.3 warna via `LocalAppPalette`; §10 ikon wajib Canvas lokal, tanpa `material-icons-extended` |
| `cmp-prd-generator` | Gradle 9.6 + AGP 9.4+, modul `composeApp/`, `composeResources/` | §5: versi mengikuti `libs.versions.toml` (AGP 9.1.1) dan modul `shared/` |

**d. Boleh dipakai dengan adaptasi (bukan ditolak):** resep komponen dari `compose-luxury-clean-ui`, `compose-flat-modal-notification`, dan `kotlin-compose-ios-notification-banner` tetap berguna untuk struktur/layout. Adaptasi wajib: ganti seluruh warna hardcoded ke `LocalAppPalette`, ganti `Icons.*` ke ikon Canvas di `UiIcons.kt`, dan pastikan komponen benar di mode terang dan gelap.

### 19.4 Kewajiban Melaporkan

Setiap kali sebuah skill diaktifkan dan sebagian mandatnya tidak diterapkan di repo ini, agent wajib menyebutkannya di ringkasan akhir: skill apa yang dipakai, mandat mana yang diabaikan, dan aturan `AGENTS.md` mana yang menggantikannya. Jangan melaporkan "selesai" seolah seluruh mandat skill diterapkan.

### 19.5 Sumber Verifikasi Eksternal

Aturan teknis di file ini bersumber dari dua hal: kode repositori yang sudah terbukti kompilasi (bukti utama) dan dokumentasi resmi yang diverifikasi lewat Context7 MCP untuk Compose Multiplatform (`/jetbrains/compose-multiplatform` dan `/websites/kotlinlang_multiplatform`). Yang sudah diverifikasi:

- `Modifier.safeDrawingPadding()` mencakup insets IME (keyboard) pada iOS.
- `ComposeUIViewController` mengaktifkan gesture back secara default, sehingga `BackHandler` bekerja di iOS tanpa bridge `expect/actual`.
- `Modifier.navigationBarsPadding()` dan `imePadding()` tersedia di `commonMain` melalui `compose.foundation`.

Bila agent ragu tentang ketersediaan sebuah API Compose Multiplatform, verifikasi lewat Context7 MCP sebelum mengubah pola yang sudah ada di repo, lalu catat hasilnya di sini.
