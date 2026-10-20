# TODO — Testing UI Component Roadmap

Status legend: **DONE** = sudah tersedia di app · **WAIT** = belum dikerjakan.

Total: **166 lab `DONE`** (§1–§12) · **0 lab `WAIT`**.

> Catatan sinkronisasi: §11 (termasuk 118–121) dan §12 (40 ide) kini `DONE` (layar, route, `MenuEntry`, ikon Canvas, dan `BackHandler` sudah terdaftar di kode).

---

## 1. Labs Eksperimen Inti (DONE)

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 1 | Auto-blur Scroll | `auto_blur_scroll` | DONE |
| 2 | Sticky Header | `sticky_header` | DONE |
| 3 | Parallax Hero | `parallax_hero` | DONE |
| 4 | Shimmer Skeleton | `shimmer` | DONE |
| 5 | Shared Element Transition | `shared_element` | DONE |
| 6 | Bottom Sheet (multi-detent) | `bottom_sheet` | DONE |
| 7 | Empty & Error State | `empty_state` | DONE |
| 8 | Motion Lab (collapsing bar, pull-refresh, swipe) | `motion_lab` | DONE |
| 9 | Form Lab (validasi, slider, chip, OTP) | `form_lab` | DONE |
| 10 | Navigation Lab (segmented, bottom nav, speed-dial) | `nav_lab` | DONE |
| 11 | Adaptive Layout | `adaptive` | DONE |
| 12 | Swipeable Gallery (zoom) | `gallery` | DONE |
| 13 | Feedback Lab (toast bertumpuk, banner) | `feedback` | DONE |
| 14 | Stats Lab (progress ring, expandable card) | `stats` | DONE |
| 15 | Charts Lab (line, bar, donut, sparkline) | `charts` | DONE |
| 16 | Infinite Scroll + Pagination (load-more, end/retry) | `infinite_scroll` | DONE |
| 17 | Multi-select & Bulk Action (edit mode, bulk bar) | `multi_select` | DONE |
| 18 | Search / Autocomplete (debounce, riwayat, highlight) | `search` | DONE |
| 19 | Theme / Dark Mode Lab (light/dark/system global) | `theme_lab` | DONE |

---

## 2. Onboarding & Auth Flow (DONE)

| Komponen | Route | Status |
|----------|-------|--------|
| Splash / animated logo reveal | `splash` | DONE |
| Onboarding carousel + page dots | `onboarding` | DONE |
| Login, register, social sign-in (Apple/Google) | `auth` | DONE |
| Biometric / Face ID prompt sheet | `biometric` | DONE |
| Passcode (PIN keypad, biometrik, swipe-to-unlock) | `passcode` | DONE |
| Permission request (notif, lokasi, kamera) | `permissions` | DONE |
| Profile setup wizard | `profile_setup` | DONE |

Alur terhubung: `splash → onboarding → auth → permissions → profile_setup → home`
(Biometric & Passcode berdiri sendiri dari grid Home.)

---

## 3. Laboratorium Komponen Lanjutan Baru (DONE)

### 3.1 Navigasi & Struktur Lanjutan (`nav_structure`)
| Komponen | Status |
|----------|--------|
| Large / collapsing top app bar | DONE (Motion Lab) |
| Search app bar + animated voice search visualizer | DONE (`nav_structure`) |
| Modal navigation drawer & adaptive navigation rail | DONE (`nav_structure`) |
| iOS floating tab bar (pill) + auto-minimize on scroll | DONE (`nav_structure`) |
| Badge pada tab / icon (numerik & live dot) | DONE (`nav_structure`) |
| Deep link / universal link handler simulator | DONE (`nav_structure`) |
| Multi-pane list-detail adaptive | DONE (`adaptive`) |

### 3.2 Konten & List Interaktif (`advanced_list`)
| Komponen | Status |
|----------|--------|
| Drag-to-reorder list (interactive item ordering) | DONE (`advanced_list`) |
| Sectioned list + A–Z index scrubber | DONE (`advanced_list`) |
| Chat bubbles + message input bar + typing indicator | DONE (`advanced_list`) |
| Timeline / activity feed | DONE (`advanced_list`) |
| Comment thread + expand/collapse hierarkis | DONE (`advanced_list`) |
| Context menu / peek-pop preview | DONE (`advanced_list`) |

### 3.3 Kartu & Data (`data_cards`)
| Komponen | Status |
|----------|--------|
| Calendar month grid + multi-colored event dots | DONE (`data_cards`) |
| Multi-column Kanban board (To Do, In Progress, Done) | DONE (`data_cards`) |
| Tiered Pricing / comparison table (monthly/annual toggle) | DONE (`data_cards`) |
| Rating stars input + customer review summary card | DONE (`data_cards`) |
| Order tracking timeline & multi-stage status stepper | DONE (`data_cards`) |

### 3.4 Input & Kontrol Lanjutan (`advanced_input`)
| Komponen | Status |
|----------|--------|
| iOS wheel date picker (kolom tanggal/bulan/tahun, snapping + band seleksi) | DONE (`advanced_input`) |
| iOS wheel time picker (jam & menit diputar, bukan tombol +/-) | DONE (`advanced_input`) |
| Horizontal date strip + kalender penuh tersinkron | DONE (`advanced_input`) |
| Inline graphical calendar (geser bulan, tombol Hari Ini, mode rentang) | DONE (`advanced_input`) |
| Compact date picker (field ringkas + popover kalender) | DONE (`advanced_input`) |
| Dual-thumb range slider, stepper & quantity control | DONE (`advanced_input`) |
| Tag/chip input with deletion + @mention autocomplete | DONE (`advanced_input`) |
| Canvas signature pad (interactive finger drawing) | DONE (`advanced_input`) |
| Color picker palette & emoji selector | DONE (`advanced_input`) |
| Image/camera picker & 1:1 crop frame mockup | DONE (`advanced_input`) |
| File/document uploader with animated progress bar | DONE (`advanced_input`) |

### 3.5 Overlay & Feedback Lanjutan (`overlay_lab`)
| Komponen | Status |
|----------|--------|
| iOS Cupertino action sheet with destructive button | DONE (`overlay_lab`) |
| Coach marks / spotlight onboarding with focus hole | DONE (`overlay_lab`) |
| Directional tooltips & popovers (Top, Bottom, Left, Right) | DONE (`overlay_lab`) |

### 3.6 Media & Immersive (`media_lab`)
| Komponen | Status |
|----------|--------|
| Video player simulator with PiP window toggle & controls | DONE (`media_lab`) |
| Sticky mini-player + expandable Now-Playing sheet | DONE (`media_lab`) |
| Stories / reels vertical full-screen pager with timer bars | DONE (`media_lab`) |
| Audio waveform voice message player with scrubber | DONE (`media_lab`) |

### 3.7 Settings & Akun (`settings_lab`)
| Komponen | Status |
|----------|--------|
| Grouped iOS settings list + switches & chevrons | DONE (`settings_lab`) |
| Language & region locale selector (ID, EN, JA, FR, DE) | DONE (`settings_lab`) |
| Notification preferences matrix (Push, Email, SMS) | DONE (`settings_lab`) |
| Pro membership subscription paywall with plan cards | DONE (`settings_lab`) |
| About application, legal terms, & FAQ accordion | DONE (`settings_lab`) |

### 3.8 Commerce & Transaksi (`commerce_lab`)
| Komponen | Status |
|----------|--------|
| E-commerce product card with size & color chips | DONE (`commerce_lab`) |
| Shopping cart bottom sheet with quantity adjusters | DONE (`commerce_lab`) |
| Multi-step checkout stepper (Alamat, Pengiriman, Bayar) | DONE (`commerce_lab`) |
| Payment method selector (Cards, Apple Pay, VA, Wallet) | DONE (`commerce_lab`) |
| Digital wallet balance card with top-up & mutations | DONE (`commerce_lab`) |
| Voucher / coupon input with instant validation | DONE (`commerce_lab`) |

### 3.9 Sistem & Platform (`system_platform`)
| Komponen | Status |
|----------|--------|
| QR & barcode camera scanner viewfinder with laser line | DONE (`system_platform`) |
| Schematics map canvas with draggable bottom sheet | DONE (`system_platform`) |
| Haptic feedback & sensory pattern simulator | DONE (`system_platform`) |
| iOS Dynamic Island & Android persistent widget preview | DONE (`system_platform`) |
| Accessibility inspector with dynamic font scaling (80%-150%) | DONE (`system_platform`) |

---

## 4. Rekomendasi Teknis & Arsitektur (DONE)

- [x] **Global Dark Mode Rollout**: Diimplementasikan penuh melalui `AppPalette`, `LocalAppPalette`, dan `LocalThemeController` di `UiAppTheme`. Perubahan tema di `ThemeLabScreen` langsung berdampak ke seluruh 35 modul, Top Bar, latar, kartu, dan teks.
- [x] **Scalable Backstack Navigation**: Diimplementasikan melalui `AppNavController` dan `LocalNavController` di `AppNavigation.kt` dengan `BackHandler` multiplatform root terpusat.
- [x] **State Holder & Flat UI Zero-Shadow Policy**: Seluruh modul mematuhi aturan Flat UI murni (1px crisp border, zero drop shadow / zero elevation, dan pure Canvas vector rendering).

---

## 5. Komponen Aplikasi Skala Besar (World-Class UI)

### 5.1 Prioritas Terbaik (DONE)
| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 36 | Rolling Numbers / Ticker Counter (Odometer Effect) | `ticker_counter` | DONE |
| 37 | Pull-Down to Dismiss Media / Lightbox (Elastic Drag) | `pull_dismiss` | DONE |
| 38 | Mobile Command Palette & Quick Launcher (`Cmd+K`) | `command_palette` | DONE |


### 5.2 Antrean Roadmap Lanjutan (DONE)
| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 39 | Celebration Confetti Burst & Dynamic Particles | `confetti_particles` | DONE |
| 40 | Interactive Reactions Bar (Bouncy Emoji Spring Pop) | `reactions_bar` | DONE |
| 41 | Tinder-Style Swipeable Decision Card Stack | `swipe_cards` | DONE |
| 42 | Cupertino Swipe-to-Reveal Multi-Actions with Full-Swipe | `swipe_actions` | DONE |
| 43 | Morphing Floating Action Button (FAB to Menu Sheet) | `morphing_fab` | DONE |
| 44 | App Switcher Privacy Masking & Sensitive Screen Blur | `privacy_masking` | DONE |
| 45 | Mask Sensitive Balance & Shake-to-Hide Privacy Gesture | `balance_masking` | DONE |
| 46 | Interactive Scratch Card (Finger Erase Canvas Reveal) | `scratch_card` | DONE |
| 47 | Daily Streak & Contribution Activity Heatmap | `streak_heatmap` | DONE |


### 5.3 Trending Mobile UI 2025/2026 (Viral on X, Reddit & Modern Apps)
| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 48 | Hold-to-Confirm / Long-Press Progress Button (Linear & Cash App style) | `hold_to_confirm` | DONE |
| 49 | Slide-to-Confirm / Swipe-to-Action Bar (Slide to Pay / Unlock) | `slide_to_confirm` | DONE |
| 50 | Interactive Before-and-After Split Comparison Slider (AI/Photo Splitter) | `split_comparison` | DONE |
| 51 | Bento Grid Dashboard with Interactive Modular Widgets (Apple & SaaS Bento) | `bento_grid` | DONE |
| 52 | Perforated Receipt & Digital Boarding Pass Ticket (Fintech & Apple Wallet) | `perforated_ticket` | DONE |
| 53 | AI Voice Intelligence Orb & Audio Reactive Waveform (ChatGPT & Gemini Live) | `ai_voice_orb` | DONE |
| 163 | Staggered Reveal List & Cascade Animation (Editorial Feed Onboarding) | `staggered_reveal` | DONE |
| 164 | Animated Success Checkmark & Morphing Confirmation (Payment Success) | `success_check` | DONE |
| 165 | Floating Pill Toolbar & Expanding Quick Actions (iOS 26 & Notion Editor) | `pill_toolbar` | DONE |
| 166 | Morphing Search Icon to Field (iOS 26 News & Instagram Search) | `search_morph` | DONE |


## 6. Ide Komponen UI Modern Berikutnya (DONE)

Daftar ini menambah pola interaksi yang belum menjadi fokus lab saat ini. Prioritasnya adalah komponen yang bisa dicoba langsung, punya state yang jelas, dan tetap nyaman digunakan di Android maupun iOS. Inspirasi adaptif dan aksesibilitas mengikuti panduan platform terbaru; efek visual seperti kaca diperlakukan sebagai material opsional, bukan lapisan wajib di seluruh layar.

### Prioritas tinggi

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 54 | AI Chat Composer & Streaming Response | `ai_chat` | DONE |
| 55 | Contextual Selection Toolbar | `selection_toolbar` | DONE |
| 56 | Optimistic Action & Undo Queue | `undo_queue` | DONE |
| 57 | Adaptive Navigation & List-Detail for Foldables | `adaptive_navigation` | DONE |
| 58 | Material 3 Expressive Controls Playground | `expressive_controls` | DONE |
| 59 | Accessibility Preferences Playground | `accessibility_lab` | DONE |

#### 54. AI Chat Composer & Streaming Response (`ai_chat`)
- Composer yang tumbuh mengikuti isi, tombol kirim/stop, lampiran gambar/file, dan pilihan mode teks/voice.
- Tampilkan jawaban seolah diketik bertahap, blok kode, kartu sumber/citation, salin, regenerate, dan rating respons.
- Sediakan state menunggu, gagal dengan retry, respons kosong, serta keyboard terbuka; gunakan data lokal, tanpa klaim terhubung ke model AI sungguhan.
- Fokus: hierarki percakapan dan kontrol yang tetap mudah dijangkau saat keyboard muncul.

#### 55. Contextual Selection Toolbar (`selection_toolbar`)
- Tekan lama untuk memilih item, lalu munculkan toolbar yang mengikuti konteks dengan aksi pin, bagikan, arsip, hapus, dan menu lainnya.
- Dukung pemilihan banyak item, jumlah pilihan, select all, batal, serta perubahan toolbar saat aksi tidak tersedia.
- Tunjukkan variasi toolbar untuk list, teks, dan media; jangan menutupi konten yang dipilih.

#### 56. Optimistic Action & Undo Queue (`undo_queue`)
- Demo aksi cepat seperti mengarsipkan, menghapus, atau menandai item tanpa menunggu server: UI berubah langsung dan menyediakan snackbar Undo.
- Tampilkan antrean beberapa aksi, hitung mundur, pembatalan satu aksi, serta state gagal yang mengembalikan item dengan pesan yang jelas.
- Jelaskan perilaku saat aksi bertumpuk agar snackbar tidak saling menutupi atau kehilangan tombol.

#### 57. Adaptive Navigation & List-Detail for Foldables (`adaptive_navigation`)
- Satu skenario konten yang sama berubah dari bottom bar pada layar compact ke navigation rail dan pane list-detail pada layar lebih lebar.
- Sertakan simulasi lipatan/fold posture, rotasi, split-screen, dan perpindahan ukuran jendela; pertahankan pilihan dan scroll saat layout berubah.
- Perlihatkan batas ukuran yang memicu perubahan layout dan state ketika detail dibuka langsung melalui deep link.

#### 58. Material 3 Expressive Controls Playground (`expressive_controls`)
- Galeri tombol dengan bentuk dan ukuran yang bervariasi, segmented control, progress indicator, slider, serta feedback animasi sentuh.
- Setiap contoh harus memperlihatkan state normal, ditekan, dipilih, dinonaktifkan, loading, dan error.
- Sediakan intensitas animasi rendah/tinggi dan variasi bentuk agar eksperimen terasa hidup tetapi tetap dapat dibaca.

#### 59. Accessibility Preferences Playground (`accessibility_lab`)
- Kontrol demo untuk skala teks, kontras tinggi, pengurangan gerak, transparansi rendah, dan target sentuh besar.
- Tampilkan dampak preferensi pada komponen yang sama secara langsung, termasuk teks panjang, tombol berdekatan, dan animasi transisi.
- Pastikan informasi tidak dibedakan hanya dengan warna atau gerakan; sediakan label dan status teks untuk setiap contoh.

### Prioritas menengah

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 60 | Activity Inbox & Notification Center | `activity_inbox` | DONE |
| 61 | Save-and-Resume Form Flow | `resume_form` | DONE |
| 62 | Native Material Surface & Floating Controls | `native_surfaces` | DONE |

#### 60. Activity Inbox & Notification Center (`activity_inbox`)
- Inbox notifikasi dengan kategori, filter belum dibaca, grouping berdasarkan hari, dan aksi tandai sudah dibaca.
- Sertakan empty state, loading skeleton, notifikasi baru yang masuk, dan swipe action dengan undo.
- Gunakan contoh lokal agar pola state dan kepadatan informasi dapat dinilai tanpa integrasi push notification.

#### 61. Save-and-Resume Form Flow (`resume_form`)
- Form panjang dengan indikator langkah, autosave draft, keluar lalu lanjutkan, ringkasan sebelum kirim, serta konfirmasi saat draft dibuang.
- Perlihatkan status tersimpan/menyimpan/gagal menyimpan dan validasi yang tidak menghapus input pengguna.
- Bedakan dari wizard profil yang sudah ada dengan fokus pada pemulihan draft dan state form yang tahan perubahan konfigurasi.

#### 62. Native Material & Floating Controls (`native_surfaces`)
- Demo toolbar/tab bar mengambang dengan material blur/translucent, perubahan ukuran saat scroll, dan konten yang melewati belakang kontrol.
- Bandingkan material transparan, solid, dan kontras tinggi pada latar terang/gelap serta latar gambar.
- Sediakan fallback Reduce Transparency dan Reduced Motion; kontrol harus tetap terbaca di atas konten yang ramai.

### Kriteria selesai untuk setiap lab baru

- Route terdaftar di `App.kt` dan dapat dibuka dari Home; kartu menu memiliki ikon khusus.
- Demo memiliki interaksi nyata serta state normal, loading, kosong, berhasil, gagal, dan disabled yang relevan.
- Layout tetap dapat dipakai pada layar kecil dan lebar, tema terang/gelap, serta ukuran teks besar.
- Komponen interaktif memiliki label/semantik yang bermakna dan tidak mengandalkan warna atau animasi saja.
- Perilaku yang membutuhkan backend atau layanan platform diberi label simulasi dengan jelas.

### Referensi inspirasi

- [Material 3 Expressive — Android](https://blog.google/products-and-platforms/platforms/android/material-3-expressive-android-wearos-launch/)
- [Adaptive layouts — Android Developers](https://developer.android.com/develop/adaptive-apps/guides/support-different-display-sizes)
- [Liquid Glass — Apple Developer](https://developer.apple.com/documentation/technologyoverviews/liquid-glass)
- [Motion dan adaptasi aksesibilitas — Apple HIG](https://developer.apple.com/design/human-interface-guidelines/motion)

---

## 7. Ide Komponen UI Modern & Profesional (DONE)

Fokus batch ini adalah komponen produktivitas, fintech, dan tampilan padat-data profesional yang belum tercakup di bagian 1–6. Semua lab berjalan offline dengan data mock lokal, mematuhi Flat UI zero-shadow, dan benar di mode terang/gelap.

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 63 | Focus Timer & Circular Session Ring | `focus_timer` | DONE |
| 64 | Masonry Waterfall Grid | `masonry_grid` | DONE |
| 65 | Fintech Amount Keypad & Currency Input | `amount_keypad` | DONE |
| 66 | Scroll-Sync Category Tabs (Menu Restoran) | `category_scroll` | DONE |
| 67 | Data Table Pro (Sticky Column & Sorting) | `data_table` | DONE |

#### 63. Focus Timer & Circular Session Ring (`focus_timer`)
- Ring progres Canvas dengan countdown mm:ss, preset durasi 15/25/45 menit, dan fase Fokus/Istirahat.
- Kontrol mulai, jeda, lanjutkan, dan reset; state siap, berjalan, jeda, dan selesai dengan kartu penyelesaian sesi.
- Statistik sesi selesai dan total menit fokus; timer berjalan memakai coroutine, bukan thread blocking.

#### 64. Masonry Waterfall Grid (`masonry_grid`)
- Grid dua kolom dengan tinggi kartu bervariasi ala Pinterest memakai lazy staggered grid dengan key stabil.
- Kartu memakai ilustrasi gradien lokal (tanpa gambar jaringan), judul, jumlah suka, dan toggle simpan dengan label teks.
- Aksi acak ulang urutan dari top bar; tata letak tetap wajar di layar kecil dan mode gelap.

#### 65. Fintech Amount Keypad & Currency Input (`amount_keypad`)
- Keypad numerik kustom 3x4 dengan format ribuan Rupiah otomatis, tombol 00, dan hapus mundur.
- Chip nominal cepat, info saldo, peringatan nominal melebihi saldo, dan CTA yang dinonaktifkan saat tidak valid.
- Alur konfirmasi berlabel simulasi dengan ringkasan transaksi dan reset; tidak mengklaim terhubung ke layanan pembayaran nyata.

#### 66. Scroll-Sync Category Tabs (`category_scroll`)
- Chip kategori horizontal yang tersinkron dua arah dengan scroll list vertikal (pola menu restoran/e-commerce).
- Mengetuk chip menggulir list ke section terkait; menggulir list memperbarui chip aktif dan menjaga chip tetap terlihat.
- Ringkasan keranjang di bottom bar dengan `navigationBarsPadding()`, harga, dan jumlah item.

#### 67. Data Table Pro (`data_table`)
- Tabel data transaksi dengan kolom pertama lengket (sticky) dan kolom lain yang dapat digulir horizontal.
- Header dapat diurutkan (naik/turun) dengan indikator panah, baris zebra, dan pill status berlabel teks.
- Footer ringkasan total, toggle state kosong dengan empty state, serta tombol muat ulang data.

---

## 8. Gamifikasi, Visualisasi & Media (DONE)

Batch ini menutup celah yang belum tersentuh: mekanik reward, grafik lanjutan di luar line/bar/donut, gerak berbasis waktu, dan alat pembuatan media. Semua lab berjalan offline dengan data mock lokal, mematuhi Flat UI zero-shadow, dan benar di mode terang/gelap. Helper kartu bersama diekstrak ke `ui/components/LabKit.kt`.

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 68 | Gamifikasi & Reward (roda undian, flashcard, kuis, polling, papan skor) | `gamification_lab` | DONE |
| 69 | Data Viz Lanjutan (radar, gauge, candlestick, waterfall) | `dataviz_lab` | DONE |
| 70 | Motion & Scroll Lanjutan (marquee, carousel, tab morphing, flip clock) | `scroll_motion_lab` | DONE |
| 71 | Media Creation (filter foto, QR generator, perekam suara, editor anotasi) | `create_lab` | DONE |

#### 68. Gamifikasi & Reward (`gamification_lab`)
- Roda undian Canvas enam segmen dengan animasi deselerasi, jarum penunjuk, legenda warna, dan riwayat putaran.
- Flashcard 3D flip, kuis dengan penjelasan dan skor, polling satu suara dengan bar hasil animasi, serta papan skor berindenai naik/turun yang tidak membedakan status hanya lewat warna.

#### 69. Data Viz Lanjutan (`dataviz_lab`)
- Radar chart dua seri dengan toggle, gauge/speedometer berzona ambang, candlestick yang dapat diketik untuk memilih periode, dan waterfall chart kumulatif menuju laba bersih.
- Setiap grafik memakai `Animatable` agar animasi reveal dapat diputar ulang secara konsisten.

#### 70. Motion & Scroll Lanjutan (`scroll_motion_lab`)
- Marquee ticker yang dapat dijeda, carousel hero auto-loop dengan efek parallax dan dots, indikator tab yang bermorfosis mengikuti pilihan, dan flip clock hitung mundur dengan animasi digit.
- Seluruh gerak dikendalikan coroutine/animasi Compose; tidak ada timer thread.

#### 71. Media Creation (`create_lab`)
- Filter foto dengan pratinjau gradien mock dan state memproses, QR generator dari pola pseudo-acak berlabel simulasi, perekam suara dengan waveform langsung dan daftar rekaman tiruan, serta editor anotasi dengan stiker yang dapat digeser.
- Tidak ada akses kamera, mikrofon, atau jaringan; seluruh perilaku diberi label simulasi.

---

## 9. Pola Khas iOS Modern (DONE)

Batch ini menutup dua kategori yang paling tipis pada taksonomi komponen Apple Human Interface Guidelines, yaitu **System experiences** (Control Center, Lock Screen, widget, Live Activity, StandBy, Live Captions) dan **Menus and actions** (activity view / share sheet, edit menu teks). Sisanya melengkapi celah kecil di **Status** (activity rings), **Navigation and search** (token field), **Selection and input** (ruler picker, keyboard accessory), dan **Presentation/Content** (page curl, zoom foto).

Semua lab di §9 dirancang selesai hanya dengan Compose Foundation + Canvas: tanpa dependensi baru, tanpa jaringan, tanpa akses kamera/mikrofon/lokasi. Setiap layar wajib mematuhi Flat UI zero-shadow (§7), mengambil warna dari `LocalAppPalette` termasuk `palette.onPrimary` untuk konten di atas `palette.primary` (§6.3), menangani insets (§8), dan menampilkan state normal, loading, kosong, sukses, gagal, serta disabled sesuai relevansi.

### 9.1 Prioritas Utama (DONE)

Enam lab yang paling terasa sebagai aplikasi iOS dan paling cepat terlihat hasilnya. Mencakup kategori HIG *Menus and actions*, *System experiences*, *Status*, *Presentation*, dan *Selection and input*.

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 72 | Share Sheet & Activity View (AirDrop, app tujuan, daftar aksi) | `share_sheet` | DONE |
| 73 | Control Center & Home Screen Edit Mode (jiggle, drag, resize) | `control_center` | DONE |
| 74 | Activity Rings & Ringkasan Mingguan (tiga cincin bergerak) | `activity_rings` | DONE |
| 75 | Lock Screen Customization (jam, slot widget, wallpaper) | `lock_screen` | DONE |
| 76 | In-App Notification Banner (turun dari atas, expand, antrean) | `notification_banner` | DONE |
| 77 | Ruler Picker Pengukuran (tick, snapping, satuan cm/ft-in) | `ruler_picker` | DONE |

#### 72. Share Sheet & Activity View (`share_sheet`)
- Sheet berbagi dengan header pratinjau konten (thumbnail gradien, judul, domain), baris horizontal app tujuan (AirDrop dengan riak radar, Pesan, Mail, Salin), dan daftar aksi vertikal (Tambahkan ke Bacaan, Simpan ke File, Cetak, Tandai) beserta tombol "Opsi…".
- State: memilih tujuan, mengirim dengan progres persen, sukses bercentang, gagal dengan tombol coba lagi, dan dibatalkan; sheet dapat dibuang dengan drag ke bawah dan kembali ke posisi semula bila tarikan kurang.
- Melengkapi aksi "bagikan" pada `selection_toolbar` yang saat ini hanya memunculkan toast; seluruh tujuan app adalah data mock berlabel simulasi.

#### 73. Control Center & Home Screen Edit Mode (`control_center`)
- Grid kontrol empat kolom berisi kontrol 2x2 dan 2x1 (Wi-Fi, Bluetooth, Musik, Kecerahan, Timer) dengan dua halaman dan page dots.
- Tekan lama memicu mode edit: ikon bergetar halus, tombol minus untuk menghapus, tombol plus membuka galeri kontrol, drag untuk menyusun ulang antar halaman, dan tombol "Selesai".
- State normal, editing, dragging, dan resize; tampilkan batas maksimum kontrol per halaman saat grid penuh.

#### 74. Activity Rings & Ringkasan Mingguan (`activity_rings`)
- Tiga cincin konsentris Canvas (Bergerak, Olahraga, Berdiri) dengan animasi penutupan berurutan, target di tengah, dan legenda berlabel teks agar status tidak dibedakan hanya lewat warna.
- Riwayat tujuh hari sebagai bar mini, ketuk cincin membuka detail harian, serta badge pencapaian yang terbuka saat target tuntas.
- State: belum ada data, sebagian tercapai, tuntas 100%, dan rentetan hari berturut-turut.

#### 75. Lock Screen Customization (`lock_screen`)
- Pratinjau layar kunci dengan jam besar dan pemilih font, ukuran, serta warna (grid delapan warna).
- Slot widget di bawah jam (dua slot) dan di atas jam (empat slot) yang dipilih dari galeri mock, plus galeri wallpaper gradien.
- Notifikasi bertumpuk, pill Live Activity, varian "Selalu Aktif" yang meredup, serta state terkunci dan terbuka; tidak ada integrasi wallpaper atau notifikasi sistem sungguhan.

#### 76. In-App Notification Banner (`notification_banner`)
- Banner meluncur dari atas berisi avatar, judul, isi, dua tombol aksi, dan tombol tutup; tarik ke atas untuk membuang dan tarik ke bawah untuk memperluas ke konten kaya.
- Antrean tiga notifikasi bertumpuk dengan hitung mundur auto-dismiss yang dapat dijeda, plus state "Jangan Ganggu" yang meredam banner dan memindahkannya ke ringkasan.
- Berbeda dari `activity_inbox` yang menampilkan daftar: lab ini fokus pada perilaku banner yang masuk; gunakan contoh lokal tanpa push notification.

#### 77. Ruler Picker Pengukuran (`ruler_picker`)
- Ruler horizontal Canvas dengan tick besar tiap sepuluh dan tick kecil tiap satu, indikator tengah, snapping, dan umpan haptic per tick.
- Nilai besar di atas ruler, tombol satuan cm maupun ft/in yang mengonversi tanpa kehilangan presisi, serta beberapa preset cepat.
- Melengkapi wheel picker `advanced_input` dengan pola picker pengukuran (tinggi/berat) yang umum di aplikasi kesehatan.

### 9.2 Prioritas Lanjutan (DONE)

Sepuluh lab berikut melengkapi kategori *Navigation and search* (token field, path control), *Selection and input* (keyboard accessory), *System experiences* (widget gallery, Live Activity, StandBy, Live Captions), *Menus and actions* (edit menu teks), dan *Content* (photos zoom, page curl).

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 78 | Search Token Field (token kunci-nilai di dalam field) | `search_tokens` | DONE |
| 79 | Photos Year to Month to Day Zoom (pinch antar tingkat agregasi) | `photos_zoom` | DONE |
| 80 | Keyboard Accessory & Keypad Kustom (toolbar di atas keyboard) | `keyboard_accessory` | DONE |
| 81 | Widget Gallery & Sizes (small, medium, large, varian tampilan) | `widget_gallery` | DONE |
| 82 | Text Loupe & Edit Menu (kaca pembesar, caret, menu teks) | `edit_menu` | DONE |
| 83 | Live Activity Lifecycle (compact, minimal, expanded, selesai) | `live_activity` | DONE |
| 84 | Precision Finding (panah arah, cincin jarak, umpan haptic) | `precision_finder` | DONE |
| 85 | Live Captions & Transkripsi (transkrip bergulir, diarisasi) | `live_captions` | DONE |
| 86 | Page Curl Reader (balik halaman dengan deformasi Canvas) | `page_curl` | DONE |
| 87 | StandBy Nightstand (mode lanskap, jam besar, mode malam) | `standby_mode` | DONE |

#### 78. Search Token Field (`search_tokens`)
- Field pencarian berisi token kunci-nilai (`dari:`, `tanggal:`, `label:`) yang dapat diketuk untuk mengubah nilai dan dihapus dengan backspace.
- Saran nilai muncul sebagai daftar saat token aktif, plus hasil terfilter real-time yang menyorot kata kunci.
- State: kosong dengan saran populer, token belum lengkap, hasil kosong, dan hasil banyak.

#### 79. Photos Year to Month to Day Zoom (`photos_zoom`)
- Pinch untuk berpindah tingkat agregasi konten (Semua Foto, Tahun, Bulan, Hari) dengan transisi skala dan judul tingkat yang berganti.
- Grid dinamis: tahun menampilkan kartu besar, bulan menampilkan kelompok, hari menampilkan grid rapat; ketuk untuk masuk satu tingkat dan cubit masuk untuk keluar.
- Header "Kenangan" dengan kartu hero, mode pilih dengan aksi massal, dan seluruh ilustrasi berupa gradien lokal.

#### 80. Keyboard Accessory & Keypad Kustom (`keyboard_accessory`)
- Toolbar di atas keyboard (Selesai, panah sebelumnya dan berikutnya, tombol format) yang mengikuti bidang aktif dan tetap terlihat saat keyboard terbuka.
- Saran prediktif dari data mock dan keypad numerik kustom dengan operasi aritmetika sederhana beserta riwayat perhitungan.
- Seluruh elemen adalah simulasi di dalam Compose, bukan keyboard sistem; state normal, keyboard tertutup, dan saran kosong.

#### 81. Widget Gallery & Sizes (`widget_gallery`)
- Galeri dengan varian ukuran small, medium, large, dan lock screen yang dapat dipratinjau langsung memakai data mock.
- Varian tampilan terang, gelap, tinted, dan clear; slider kedalaman untuk efek berlapis serta tombol "Tambah Widget".
- Kartu widget interaktif (toggle, progress, tombol) tanpa membuka layar lain; tidak ada widget sistem sungguhan.

#### 82. Text Loupe & Edit Menu (`edit_menu`)
- Tekan lama pada teks memunculkan kaca pembesar (loupe) yang mengikuti jari beserta caret dan dua handle seleksi yang dapat digeser.
- Menu edit mengambang berisi Potong, Salin, Tempel, Tebal, Miring, Garis bawah, Cari, dan Terjemahkan; plus sheet "Look Up" dengan definisi mock.
- Berbeda dari `selection_toolbar` yang berfokus pada seleksi item daftar: lab ini khusus seleksi teks, caret, dan loupe.

#### 83. Live Activity Lifecycle (`live_activity`)
- Siklus hidup aktivitas (pesanan atau perjalanan) mulai dari dimulai, pembaruan progres real-time, hingga berakhir, dengan tiga presentasi: compact, minimal, dan expanded.
- Kartu kunci berisi tahapan, estimasi tiba, dan tombol aksi; riwayat aktivitas yang sudah selesai ditampilkan di bagian bawah.
- Melengkapi pratinjau Dynamic Island di `system_platform` yang masih statis; tidak ada integrasi ActivityKit sungguhan.

#### 84. Precision Finding (`precision_finder`)
- Panah arah Canvas yang menunjuk target, cincin jarak yang mengecil saat mendekat, dan label jarak dalam meter.
- Umpan bertingkat saat makin dekat (indikator visual dan perubahan label status), plus state "Mencari sinyal" dan "Sinyal lemah".
- Tandai sebagai simulasi: tidak ada akses UWB, Bluetooth, atau lokasi sungguhan.

#### 85. Live Captions & Transkripsi (`live_captions`)
- Transkrip bergulir dengan sorotan kata aktif, pemisahan pembicara bertanda warna dan label teks, serta penanda suara non-ucapan.
- Kontrol ukuran teks, mode jendela mengambang, dan tombol mulai maupun berhenti dengan state mendengarkan.
- Seluruh transkrip adalah data mock berlabel simulasi; tidak ada akses mikrofon atau mesin pengenal suara.

#### 86. Page Curl Reader (`page_curl`)
- Membalik halaman dengan drag dari tepi kanan atau kiri memakai deformasi Canvas, sehingga halaman melengkung dan menampilkan bayangan lipatan.
- Progress baca, penanda halaman, pilihan mode gulir atau mode halaman, serta pengaturan ukuran huruf.
- Berbeda dari flashcard 3D di `gamification_lab`: di sini gerak halaman mengikuti jari dan dapat dibatalkan di tengah jalan.

#### 87. StandBy Nightstand (`standby_mode`)
- Mode lanskap dengan jam besar, tanggal, dan widget bergantian (kalender, kompas, foto, cuaca mock) yang dapat digeser.
- Mode malam merah yang menurunkan kecerahan konten dan menyembunyikan warna, plus pratinjau selalu aktif.
- Layout menyesuaikan lebar layar dan tetap terbaca pada ukuran font besar.

### Catatan §9

- Lab `liquid_glass` (material iOS 26: kaca dinamis, scroll edge effect, tombol glass maupun prominent) sengaja **tidak** dimasukkan ke daftar ini karena bertentangan dengan §7 Flat UI zero-shadow. Bila nanti diinginkan, ia harus berupa **lab opsi demo** dengan fallback kontras tinggi (§7.7) dan tetap memakai `LocalAppPalette`.
- `share_sheet`, `notification_banner`, `live_activity`, `standby_mode`, `live_captions`, dan `precision_finder` menyerupai layanan platform sehingga **wajib** diberi label simulasi di UI (§3.3).
- Setiap lab baru mengikuti checklist lengkap §6.1 dan Definition of Done §14: route di `App.kt` (`Route<X>`, `KnownRoutes`, cabang `when`), `MenuEntry` di `HomeMenuScreen.kt`, ikon Canvas khusus di `ui/components/icons/`, `BackHandler`, lalu statusnya diubah dari `WAIT` menjadi `DONE` pada commit yang sama.
- Label jumlah pada Home (`HomeSection.ALL` dan `shortLabel`) kini **117 lab** ("Semua (117)"), dengan section `IOS_PATTERN` ("iOS (16)"), `IOS_APPS` ("iOS+ (20)") dari §9–§10, dan `INDUSTRY` ("Industri (10)") dari §11.

---

## 10. Ide Lab Khas iOS Lanjutan (DONE)

Batch ini melengkapi pola iOS yang masih kosong dan paling ikonik: **aplikasi buatan Apple** (Weather, Stocks, Health, Clock, Home, Wallet, App Store, Translate, Freeform), **perilaku khas Messages** (bubble & screen effects), **Apple Intelligence** (Writing Tools, Genmoji), serta **kontinuity dan aksesibilitas spesifik iOS** (Handoff/AirPlay, VoiceOver, iPad Pointer & Scribble). Terinspirasi oleh antarmuka Apple seperti [Pesan berbubble effect](https://support.apple.com/en-us/guide/iphone/iph3d4cb79c9/ios), [interaksi pass di Wallet](https://installpass.es/blogs/design-tips/interaction-design-in-wallet-passes), [Smart Stacks WidgetKit](https://developer.apple.com/documentation/WidgetKit/Widget-Suggestions-In-Smart-Stacks), [Live Text/VisionKit](https://developer.apple.com/documentation/visionkit), dan klon populer seperti [Weather](https://github.com/j-labbe/weather-app-clone) serta [Messages](https://github.com/GetStream/SwiftUIMessagesUIClone).

Sama seperti §9: seluruh lab selesai hanya dengan Compose Foundation + Canvas — tanpa dependensi baru, tanpa jaringan, tanpa kamera/mikrofon/lokasi nyata. Semua wajib mematuhi Flat UI zero-shadow (§7), warna dari `LocalAppPalette` termasuk `palette.onPrimary` di atas `palette.primary` (§6.3), insets per-elemen (§8), `BackHandler` (§6.2), dan state normal, loading, kosong, sukses, gagal, serta disabled sesuai relevansi. Lab yang menyerupai layanan platform (telepon, Wallet, HomeKit, translate, screen time) wajib berlabel **simulasi** di UI (§3.3).

### 10.1 Komunikasi & Sosial

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 88 | Messages Bubble & Screen Effects (slam, loud, gentle, confetti, invisible ink, tapback) | `msg_effects` | DONE |
| 89 | Incoming Call & FaceTime UI (layar panggilan masuk, grid peserta) | `call_ui` | DONE |

#### 88. Messages Bubble & Screen Effects (`msg_effects`)
- Dua mode demo: efek pada **bubble** (slam, loud, gentle — skala + getar sesaat) dan efek **layar penuh** (confetti, laser, kembang api, echo, spotlight, invisible ink yang buram sampai diketuk).
- Tapback reaction (love, like, haha, dll.) sebagai mini badge pada bubble, plus daftar percakapan yang dipin dan balasan thread.
- Semua data mock lokal berbahasa Indonesia; animasi dihentikan saat reduce-motion (`accessibility_lab`); label jelas bahwa ini simulasi, bukan layanan iMessage asli.

#### 89. Incoming Call & FaceTime UI (`call_ui`)
- Layar panggilan masuk bergaya iOS (avatar besar, nama, aksi terima/tolak) dengan aksi meredup sebelum dijawab lalu transisi ke layar panggilan aktif.
- Kontrol aktif: bisu, keypad, speaker, rekam, tambah peserta, flip kamera; plus varian FaceTime dengan grid/grid 4-tile atau grid 2x2 peserta.
- State: berderang, terhubung, menunggu jaringan, panggilan berakhir, serta simulasi koneksi lemah yang diberi label.

### 10.2 Aplikasi Apple Ikonik

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 90 | Apple Weather (grafik jam, 10 hari, kartu modul) | `weather_app` | DONE |
| 91 | Apple Stocks (watchlist, chart crosshair, berita) | `stocks_app` | DONE |
| 92 | Health Summary & Trends (dashboard + tren) | `health_app` | DONE |
| 93 | Clock Kit (world clock, alarm, stopwatch, timer) | `clock_kit` | DONE |
| 94 | Shortcuts & Automation (galeri aksi, drag reorder, trigger) | `shortcuts_lab` | DONE |
| 95 | Home & Scenes (tile aksesori, ruangan, scene) | `home_app` | DONE |
| 96 | Apple Wallet Pass Stack (kartu berkipas, drag through) | `wallet_stack` | DONE |
| 97 | App Store Today & Product Page (kartu editorial, pager screenshot) | `appstore_today` | DONE |
| 98 | Translate (mode percakapan, side-by-side, kamera) | `translate_app` | DONE |
| 99 | Freeform Infinite Canvas (sticky note, konektor, pan/zoom) | `freeform_canvas` | DONE |

#### 90. Apple Weather (`weather_app`)
- Header suhu besar dengan latar gradien sesuai kondisi, grafik jam 24 jam (suhu + caps), grafik presipitasi, dan daftar 10 hari dengan bar hi/lo relatif.
- Kartu modul: angin (garis arah), UV, matahari terbit/terbenam, indeks kualitas udara, kelembapan, visibilitas — semua data mock; simulasi jaringan untuk "memuat" kota.

#### 91. Apple Stocks (`stocks_app`)
- Watchlist baris saham dengan pill perubahan berlabel naik/turun (warna + teks, bukan warna saja), chart garis dengan **crosshair** interaktif dan chip rentang 1D–1M–3M–6M–1Y–2Y–5Y.
- Kartu berita terkait saham, plus state loading, error "Tidak dapat memuat kuotasi", dan bursa tutup/buka; label simulasi (tidak ada data pasar nyata).

#### 92. Health Summary & Trends (`health_app`)
- Dashboard ringkasan: kartu favorit (Langkah, Tidur, Selang Detak), kartu **Highlights** naratif, panel **Tren** dengan panah naik/turun dan level badge.
- Detail depan per metrik: grafik periode, rata-rata, rentang; state tak ada data saat izin tidak diberikan; label simulasi (tanpa HealthKit).

#### 93. Clock Kit (`clock_kit`)
- World Clock (matahari/hari-malam per slot), Alarm (toggle jadwal mati/hidup dengan cepat), Stopwatch (waktu ketuk, daftar lap dengan delta warna + teks), Timer (lingkaran memanjang + klik hapus/bekukan).
- Semua berbasis coroutine `remember`/`LaunchedEffect` untuk tick; state lap terpanjang ditandai dengan label teks.

#### 94. Shortcuts & Automation (`shortcuts_lab`)
- Galeri langkah berdasarkan kategori dengan pencarian, "drag" langkah ke ruang kerja Shortcut (reorder dengan animasi), dan kartu otomation (waktu, lokasi, app, frasa Siri).
- Pratinjau galeri shortcut lintas aplikasi + izin aktivasi simulasi; berbeda dari `advanced_list` karena fokus pada **bucket aksi + trigger**.

#### 95. Home & Scenes (`home_app`)
- Tab ruangan horizontal, grid aksesori: tombol lingkaran dengan toggle menyala (mic, lampu, termostat), sensor baca-hanya, dan carousel scene (Selamat Pagi, Waktunya Tidur) dengan ikon Canvas gradien.
- Detail aksesori: slider kecerahan, pilihan suhu, status sudut/kamar; semuanya mock, diberi label simulasi (tanpa HomeKit/Matter).

#### 96. Apple Wallet Pass Stack (`wallet_stack`)
- Tumpukan kartu berkipas (tiket, boarding pass, membership, voucher) yang bisa dibuka dengan drag ke bawah, di-swipe antar kartu, diurut ulang, dihapus, dan ditandai kedaluwarsa.
- Kartu pass mendukung toggle "Express Transit", tampilan "Selesai" setelah sebuah aksi, plus bottom bar "Tambah ke Wallet"; melengkapi `perforated_ticket` yang masih statis.

#### 97. App Store Today & Product Page (`appstore_today`)
- Kartu editorial bertumpuk dengan banner kartu besar, lalu halaman produk: pager screenshot, statistik rating (bintang + histogram), catatan versi, dan tombol **GET/BUKA** dengan state mengunduh persen (progress circular).
- Tab "Today/Games/Apps/Arcade/Search" sederhana, daftar "Top Gratis" berperingkat; semua konten mock.

#### 98. Translate (`translate_app`)
- Mode percakapan: gelembung besar dua bahasa, tombol mikrofon dengan gelombang aktif, tombol flip bahasa dengan animasi; mode kamera: overlay "terjemahan langsung" di atas ilustrasi.
- Mode perangkat (side-by-side) dengan simpanan frasa favorit, riwayat, dan deteksi bahasa; label simulasi jelas — tidak ada mesin terjemahan atau mikrofon nyata.

#### 99. Freeform Infinite Canvas (`freeform_canvas`)
- Kanvas tak terbatas yang bisa dipan/zoom (gesture + kontrol), obyek sticky note (buat, geser, ubah warna), shape/konektor sederhana, dan teks; fitur lasso select untuk mengelompokkan/menggeser bersama.
- Seleksi menampilkan toolbar kontekstual; obyek lebih dari 50 memakai struktur ringan agar tetap lancar di perangkat kelas menengah.

### 10.3 Apple Intelligence & Privasi

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 100 | Writing Tools (rewrite, proofread, summarize, tabel) | `writing_tools` | DONE |
| 101 | Genmoji & Image Playground (emoji buatan perintah) | `genmoji` | DONE |
| 102 | Focus Modes & Notification Filtering (DND, aktivitas) | `focus_modes` | DONE |
| 103 | Passkeys & Passwords (verifikasi kode Face ID, audit) | `passkeys_passwords` | DONE |

#### 100. Writing Tools (`writing_tools`)
- Toolbar melayang di atas teks terpilih: Rewrite, Proofread, Summarize, Make List, Make Table, plus "Change Tone" — menampilkan hasil transformasi beranimasi (typing) dan opsi "Balik ke Asal".
- Sertakan panel proofread dengan saran bergaris bawah (typo, ejaan) yang diketuk untuk menerima; hasilnya deterministic dari data mock, diberi label "AI simulasi".

#### 101. Genmoji & Image Playground (`genmoji`)
- Layar pembuatan: kolom prompt berbahasa Indonesia, chip gaya (Sketsa, Ilustrasi, Animasi), grid hasil procedural yang dibuat dari Canvas (bentuk + gradien acak tergantung prompt) sebagai pengganti generator gambar.
- Genmoji dapat disimpan sebagai stiker, dipakai di "pesan" mock, dan dibagikan; state memuat, kosong (tanpa prompt), gagal/ menghasilkan tidak sesuai dengan tombol coba lagi; label simulasi.

#### 102. Focus Modes (`focus_modes`)
- Daftar focus (Kerja, Tidur, Pribadi, Driving, Fitness + "Custom") dengan ikon dan warna khas; aktivasi menampilkan preview beranda (notifikasi yang dibisukan, aplikasi yang diizinkan, orang yang diizinkan).
- Sertakan status focus aktif di layar kunci mock, pilihan "Ulangi Jadwal", "Notification Summary", dan filter percakapan; label simulasi (tanpa integrasi sistem).

#### 103. Passkeys & Passwords (`passkeys_passwords`)
- Sheet "Simpan passkey" khas iOS (ikon situs, nama pengguna, tombol Simpan dengan prompt Face ID animasi), daftar sandi dengan kode verifikasi yang bisa diketuk untuk menyalin.
- Audit keamanan: rekomendasi popup "sandi dipakai berulang/lemah" dengan tombol "Ubah di Situs", kolom "Registrasi Saat Ini" dengan modal *terms and conditions* flag; seluruhnya simulasi.

### 10.4 Sistem, Aksesibilitas & iPad

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 104 | VoiceOver Simulator (rotor, focus ring, hint) | `voiceover_lab` | DONE |
| 105 | Handoff & AirPlay Picker (saran beranda modal perute) | `continuity` | DONE |
| 106 | Screen Time Report & App Limits | `screen_time` | DONE |
| 107 | iPad Pointer & Apple Pencil Scribble | `pointer_scribble` | DONE |

#### 104. VoiceOver Simulator (`voiceover_lab`)
- Demo layar baca dengan VoiceOver aktif: cincin fokus indigo, pembacaan teks narasi, hint di bawah elemen, kontrol rotor (karakter/kata/baris/heading) dan gestur swipe untuk memilih item.
- Mode "Screen Curtain" (blackscreen + gesture) dan bantuan gerak; sangat mendidik untuk pembaca yang memakai TalkBack/VoiceOver asli — jelas bahwa ini simulator, bukan layanan baca layar.

#### 105. Handoff & AirPlay Picker (`continuity`)
- Panel "Berkelanjutan" di layar beranda aplikasi yang bisa dibuang (gesture swipe) dan modal AirPlay daftar output (TV, speaker, "iPhone saya") dengan penanda ceklis dan pemisah "Kontrol Speaker Volume".
- State: tidak ada perangkat terhubung, sedang mencari, terhubung (ambil alih audio media mini-player `media_lab`), satu riwayat handoff aktif; label simulasi.

#### 106. Screen Time Report & App Limits (`screen_time`)
- Kartu laporan harian/mingguan dengan bar waktu pakai, rata-rata, penyusun kategori sosial/produktivitas/kreativitas dengan legenden berlabel; batas aplikasi (pilih aplikasi, waktu limit, jadwal) dan "Always Allowed".
- Tampilan downtime + permintaan tambahan waktu (tombol "One More Minute" berprogress 30 detik) — semua mock, tanpa akses statistik sistem.

#### 107. iPad Pointer & Apple Pencil Scribble (`pointer_scribble`)
- Kursor trackpad lingkuler dengan **click target** yang auto-menyorot (membesar, mengikuti kontras), tombol dock "shape/pointer style", dan interaksi klik kanan menampilkan menu.
- Pencil mode: gesture double-tap mengganti alat (pen/marker/eraser), **Scribble** — perluas ke teks saat menulis tangan perkiraan di field, dan gesture swipe untuk memilih teks; seluruhnya Compose Canvas, tanpa PencilKit/UIKit.

### Catatan §10

- `msg_effects`, `call_ui`, `wallet_stack`, `home_app`, `translate_app`, `focus_modes`, `passkeys_passwords`, `screen_time`, `continuity`, dan `voiceover_lab` menyerupai layanan platform sehingga **wajib** diberi label simulasi di UI (§3.3).
- Lab aplikasi Apple (`weather_app`, `stocks_app`, `health_app`, `clock_kit`, `shortcuts_lab`, `appstore_today`, `freeform_canvas`) adalah **klon UI, bukan layanan**: data lokal mock, tanpa jaringan/API apa pun.
- Saat sebuah lab §10 dikerjakan, ikuti checklist §6.1 (route `Route<X>` + `KnownRoutes` + cabang `when`, `MenuEntry` Home, ikon Canvas di `ui/components/icons/` — buat file baru `IosAppsLabIcons.kt` bila melewati ±800 baris per file), Definition of Done §14, dan perbarui status `WAIT` → `DONE` di commit yang sama.
- `weather_app`, `stocks_app`, dan `health_app` memakai banyak Canvas + gradien: tetap ingat anggaran performa §11 (hindari alokasi koleksi di body composable, pakai `remember` untuk `Path`/`Brush`).


---

## 11. Komponen Industri, Spasial & Produktivitas (sebagian DONE)

Batch ini merencanakan pola interaksi yang belum tercakup: komponen spasial/booking (denah kursi, pelacakan perjalanan), audio/media lanjutan (karaoke, equalizer), produktivitas dokumen (editor blok, Gantt, pohon berkas), fintech kolaboratif (split bill, budget envelope), edukasi gamifikasi (peta quest, puzzle kata), dan fitur platform Android modern (predictive back, floating dock). Semua lab dirancang berjalan offline dengan data mock lokal berbahasa Indonesia, mematuhi Flat UI zero-shadow, dan benar di mode terang/gelap.

**Status nyata:** lab 108–122 **sudah ada di kode** (termasuk budget_envelopes, quest_path, sentence_builder, predictive_back) dan terdaftar.

### 11.1 Mobility, Travel & Spatial Booking

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 108 | Interactive Airplane / Cinema Seat Selector | `seat_selector` | DONE |
| 109 | Ride-Hailing Live Trip & Route Tracking Sheet | `trip_tracker` | DONE |
| 110 | Multi-City Flight Itinerary & Layover Timeline | `flight_itinerary` | DONE |

### 11.2 Audio, Media & Kreatif Lanjutan

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 111 | Synced Karaoke Lyrics Scroller | `synced_lyrics` | DONE |
| 112 | Interactive Audio Equalizer & 10-Band Visualizer | `audio_equalizer` | DONE |
| 113 | Photo Crop, Straighten Dial & Rule-of-Thirds Grid | `photo_cropper` | DONE |

### 11.3 Produktivitas, Dokumen & Hirarki

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 114 | Block-Based Document Editor (Notion-style) | `block_editor` | DONE |
| 115 | Roadmap & Interactive Gantt Timeline | `gantt_roadmap` | DONE |
| 116 | Hierarchical Tree & Folder File Explorer | `tree_explorer` | DONE |

### 11.4 Fintech & Kolaborasi Keuangan

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 117 | Smart Split-Bill & Receipt Item Breakdown | `split_bill` | DONE |
| 118 | Budget Envelopes & Cash Allocation Drag | `budget_envelopes` | DONE |

### 11.5 Edukasi & Gamifikasi Interaktif

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 119 | Gamified Winding Quest Map / Level Path | `quest_path` | DONE |
| 120 | Word Tile Sentence Builder & Word Puzzle | `sentence_builder` | DONE |

### 11.6 Fitur Platform Android Modern

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 121 | Predictive Back Navigation Simulator | `predictive_back` | DONE |
| 122 | Floating Multitasking App Dock | `floating_dock` | DONE |

### Catatan §11

- Saat sebuah lab §11 dikerjakan, ikuti checklist §6.1 (route `Route<X>` + `KnownRoutes` + cabang `when` di `App.kt`, `MenuEntry` di `HomeMenuScreen.kt`, ikon Canvas di `ui/components/icons/` — buat file baru bila melewati ±800 baris) dan Definition of Done §14, lalu ubah status `WAIT` → `DONE` pada commit yang sama.
- `seat_selector`, `trip_tracker`, `flight_itinerary`, `synced_lyrics`, `audio_equalizer`, dan `photo_cropper` menyerupai layanan platform (peta, audio, kamera) sehingga **wajib** berlabel **simulasi** di UI (§3.3).

---

## 12. Ide Komponen Baru — Gap Analysis (DONE)

Batch ini berisi pola interaksi yang **belum** tercakup oleh 117 lab yang sudah ada di kode (§1–§11). Semua lab tetap mengikuti aturan repo: 100% Compose Foundation + Canvas di `commonMain`, **tanpa dependensi baru**, **tanpa jaringan/kamera/mikrofon/lokasi nyata**, Flat UI zero-shadow (§7), warna lewat `LocalAppPalette` termasuk `palette.onPrimary` di atas `palette.primary` (§6.3), insets per-elemen (§8), `BackHandler` (§6.2), serta state normal, loading, kosong, sukses, gagal, dan disabled sesuai relevansi. Perilaku yang menyerupai layanan platform wajib berlabel **simulasi** (§3.3).

### 12.1 Prioritas Utama (Tier 1)

Kategori yang paling tipis: kolaborasi realtime, resiliensi/live data, AI agentik, dan *tooling* desain/kinerja.

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 123 | Realtime Collaboration & Presence (kursor rekan, presence, follow mode) | `collab_presence` | DONE |
| 124 | Live Dashboard & Streaming Metrics (KPI berdetak, sparkline live, feed) | `live_dashboard` | DONE |
| 125 | Offline & Connection Resilience (offline, reconnecting, konflik sinkron) | `offline_resilience` | DONE |
| 126 | AI Agent Run Steps (log langkah, tool-call, pause/resume/approve) | `ai_agent_steps` | DONE |
| 127 | AI Inline Ghost-text & Diff Review (accept/reject per hunk) | `ai_inline_edit` | DONE |
| 128 | Design Token & Theme Editor (palet live, cek kontras, ekspor token) | `design_tokens` | DONE |
| 129 | Performance HUD & Jank Meter (frame time, recomposition, memori) | `perf_hud` | DONE |
| 130 | Appointment Slot & Availability Picker (zona waktu, heatstrip tim) | `appointment_slots` | DONE |

#### 123. Realtime Collaboration & Presence (`collab_presence`)
- Kursor rekan yang bergerak halus di kanvas bersama avatar presence, indikator "sedang mengetik", dan mode follow (mengikuti viewport rekan).
- Baris komentar yang dipin ke elemen, status koneksi rekan, serta state masuk/keluar ruangan; melengkapi `freeform_canvas` dan `data_cards` yang masih single-user.
- Simulasi gerak rekan memakai coroutine, bukan jaringan; tanpa backend kolaborasi nyata.

#### 124. Live Dashboard & Streaming Metrics (`live_dashboard`)
- Sekumpulan KPI yang bertambah nilainya secara live, sparkline yang bergulir, dan feed event yang masuk; tombol pause/live serta pemilih rentang waktu.
- State tersambung, terhenti (paused), memuat ulang, dan data basi (stale) dengan label waktu terakhir diperbarui; berbeda dari `stats` dan `charts` yang statis.
- Seluruh angka dihasilkan dari generator lokal ber-`remember`/`LaunchedEffect`; tidak ada koneksi streaming sungguhan.

#### 125. Offline & Connection Resilience (`offline_resilience`)
- Banner status koneksi: offline, menyambung ulang, sinyal lemah/degraded, dan normal; aksi pengguna masuk ke antrean lalu dikirim saat kembali online.
- Demo konflik sinkron dua versi data dengan pilihan "Pakai Lokal / Pakai Server / Gabungkan", plus state gagal permanen dengan retry.
- Melengkapi `undo_queue` yang hanya membahas rollback aksi, bukan kondisi jaringan.

#### 126. AI Agent Run Steps (`ai_agent_steps`)
- Log tugas agentik berisi langkah-langkah yang dapat diperluas: rencana, pemanggilan tool, hasil, dan kesimpulan; tombol pause, resume, dan approve sebelum langkah berisiko.
- State berjalan, menunggu persetujuan, gagal di tengah dengan opsi lanjutkan dari langkah, dan selesai dengan ringkasan; berbeda dari `ai_chat` yang hanya streaming teks.
- Reasoning dan tool-call adalah data mock; label "AI simulasi" wajib (§3.3).

#### 127. AI Inline Ghost-text & Diff Review (`ai_inline_edit`)
- Saran ghost-text yang muncul saat mengetik dan dapat diterima dengan Tab atau ditolak; panel diff per-hunk dengan accept/reject, dan mode side-by-side.
- State tanpa saran, saran panjang, menerapkan, gagal, serta "Balik ke Asal"; melengkapi `writing_tools` yang hanya menampilkan hasil akhir.
- Tidak ada model AI nyata; transformasi bersifat deterministik dari data mock.

#### 128. Design Token & Theme Editor (`design_tokens`)
- Editor palet langsung (permukaan, teks, border, `primary`, status) dengan pengukur kontras WCAG AA/AAA real-time dan peringatan kombinasi yang gagal.
- Pratinjau komponen hidup (kartu, tombol, pill, input) di mode terang maupun gelap, ekspor token sebagai ringkasan teks, dan reset ke palet bawaan `LightAppPalette`.
- Berbeda dari `theme_lab` yang hanya mengganti light/dark/system; mengubah palet harus tetap lewat API tema (§6.3).

#### 129. Performance HUD & Jank Meter (`perf_hud`)
- Bar frame time berjalan dengan penanda frame > 16 ms, grafik jank, dan penghitung recomposition sederhana; simulasi beban (list besar, animasi berat) untuk memicu drop frame.
- State mulus, jank ringan, dan berat dengan penjelasan penyebab; berguna sebagai bukti klaim 60fps lab lain.
- Diukur murni dari `withFrameNanos` di `commonMain`: akurasi perkiraan, tanpa API profiling platform.

#### 130. Appointment Slot & Availability Picker (`appointment_slots`)
- Pilih tanggal, zona waktu, dan slot jam dengan state tersedia/terisi/hampir penuh; heatstrip ketersediaan tim dan pilihan "peserta mana".
- Ringkasan pemesanan di bottom sheet dengan tombol konfirmasi mengikuti `navigationBarsPadding()`, plus state jadwal bentrok dan berhasil.
- Melengkapi date picker `advanced_input` dengan dimensi ketersediaan & zona waktu.

### 12.2 Prioritas Menengah (Tier 2)

Memperluas visualisasi, commerce, dan konten sosial.

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 131 | Flow Viz Pack II (Sankey, treemap, funnel, cohort grid) | `flow_viz` | DONE |
| 132 | Network Graph & Node–Link Explorer | `graph_explorer` | DONE |
| 133 | Map Cluster & Store Locator (list ↔ peta sinkron) | `store_locator` | DONE |
| 134 | Credit Card Form & 3D Card (BIN, format, scanner mock) | `payment_form` | DONE |
| 135 | Phone & Address Autocomplete (kode negara, places mock) | `address_input` | DONE |
| 136 | Recurrence & Schedule Picker (RRULE) | `recurrence_picker` | DONE |
| 137 | Live Auction / Bidding Room (countdown, outbid) | `live_auction` | DONE |
| 138 | Flash Sale & Scarcity (countdown, bar stok, waitlist) | `flash_sale` | DONE |
| 139 | Subscription Management & Cancel-Retention | `subscription_mgmt` | DONE |
| 140 | Queue / Waitlist Ticket (posisi live, ETA) | `queue_ticket` | DONE |
| 141 | Social Post Composer (carousel, poll, audience) | `post_composer` | DONE |
| 142 | Story Creator (stiker, teks, poll, coret) | `story_creator` | DONE |

#### 131. Flow Viz Pack II (`flow_viz`)
- Sankey diagram alur dana/trafik, treemap proporsi, funnel konversi bertingkat, dan cohort retention grid berwarna + label persen.
- Setiap grafik dapat diketuk untuk memilih segmen dengan tooltip ringkas; memakai `Animatable` agar reveal dapat diulang.
- Melanjutkan `dataviz_lab` (radar/gauge/candlestick/waterfall) dengan keluarga visualisasi aliran & agregasi.

#### 132. Network Graph & Node–Link Explorer (`graph_explorer`)
- Graf relasi (node + edge) yang dapat di-pan/zoom, dengan tata letak gaya force sederhana, penyorotan tetangga saat node dipilih, dan filter berdasarkan tipe.
- Panel detail node, pencarian node, serta state kosong dan terlalu padat; seluruh graf adalah data mock lokal.

#### 133. Map Cluster & Store Locator (`store_locator`)
- Peta skematik dengan marker terklaster, zoom bertingkat, dan sinkron dua arah antara daftar toko dan peta (ketuk daftar → peta menyorot).
- Editor radius geofence yang dapat ditarik, estimasi jarak, serta state "lokasi tidak diizinkan"; melengkapi peta statis di `system_platform`.

#### 134. Credit Card Form & 3D Card (`payment_form`)
- Form kartu dengan pratinjau kartu yang berputar ke belakang saat mengisi CVC, deteksi jenis kartu dari BIN, dan format nomor/kedaluwarsa otomatis.
- Scanner kartu mock (bingkai kamera bergaya overlay), state valid/invalid/kedaluwarsa, dan tombol simpan yang disabled saat belum lengkap; label simulasi.

#### 135. Phone & Address Autocomplete (`address_input`)
- Input telepon dengan pemilih kode negara dan format lokal, serta input alamat dengan saran mock saat mengetik dan penyorotan kata kunci.
- Validasi per-bidang, state saran kosong, alamat tidak ditemukan, dan pemilihan saran yang mengisi beberapa bidang sekaligus; melengkapi `advanced_input`.
- Data alamat/negara adalah mock lokal; tanpa layanan peta/geocoding nyata.

#### 136. Recurrence & Schedule Picker (`recurrence_picker`)
- Pemilih pengulangan: harian, mingguan dengan chip hari, bulanan (tanggal/nth-weekday), interval kustom, dan akhir pengulangan (tanggal/jumlah).
- Ringkasan kalimat hasil ("Setiap Senin & Rabu, 10 kali") yang diperbarui live; state rentang tidak valid.
- Melengkapi picker tanggal/jam `advanced_input` dengan dimensi pengulangan.

#### 137. Live Auction / Bidding Room (`live_auction`)
- Kartu lot dengan countdown, harga terkini, riwayat bid, dan tombol bid cepat; state "Anda tertinggi" dan "Anda ter-outbid" ditandai warna + label teks.
- Nyalakan notifikasi bid mock, perpanjangan waktu anti-sniping, dan lelang berakhir (menang/kalah); seluruh nilai adalah simulasi.

#### 138. Flash Sale & Scarcity (`flash_sale`)
- Countdown besar, progress bar stok terjual, daftar produk kilat, dan tombol "Ingatkan Saya" saat stok habis.
- State sebelum mulai, berlangsung, hampir habis, dan sudah berakhir; tombol beli disabled saat habis (dengan alasan teks).

#### 139. Subscription Management & Cancel-Retention (`subscription_mgmt`)
- Kelola langganan aktif: ubah paket (upgrade/downgrade), jeda, dan ganti metode bayar.
- Alur pembatalan dengan tawaran retensi bertingkat (diskon, downgrade), survei alasan, dan konfirmasi; melengkapi paywall `settings_lab` yang hanya sisi pembelian.

#### 140. Queue / Waitlist Ticket (`queue_ticket`)
- Tiket antrean dengan nomor, posisi live ("3 antrean di depan"), estimasi waktu, dan tombol batalkan; notifikasi "giliran Anda" mock.
- State menunggu, hampir giliran, dipanggil, terlewat, dan selesai; progres antrean dihitung dari coroutine lokal.

#### 141. Social Post Composer (`post_composer`)
- Komposer unggahan berisi carousel media (gradien mock), teks dengan hitungan karakter, poll, penanda lokasi, dan pemilih audiens.
- Pratinjau tampilan post secara live dan state mengunggah persen dengan opsi batal; melengkapi `ai_chat` composer dengan konteks media/sosial.

#### 142. Story Creator (`story_creator`)
- Kanvas story vertikal dengan stiker/tulisan yang dapat digeser & diputar, pilihan latar gradien, dan sticky poll; berbeda dari `media_lab` yang hanya *viewer* story.
- Editor alat sederhana (pensil, teks, stiker), pratinjau progress bar, dan state menyimpan/menerbitkan; seluruh media adalah mock lokal.

### 12.3 Produktivitas, Dokumen, Kesehatan & Platform (Tier 3)

| # | Lab / Komponen | Route | Status |
|---|----------------|-------|--------|
| 143 | Document Scanner & OCR (deteksi tepi, perspektif) | `doc_scanner` | DONE |
| 144 | PDF/Doc Viewer + Anotasi (thumbnail, cari, lompat) | `pdf_viewer` | DONE |
| 145 | Code Editor & Diff Viewer (syntax, fold, inline/side-by-side) | `code_editor` | DONE |
| 146 | Version History & Restore (slider waktu, bandingkan) | `version_history` | DONE |
| 147 | Localization & RTL Preview (mirror, pseudo-locale) | `rtl_preview` | DONE |
| 148 | Accessibility Audit Overlay (semantics, kontras, hit target) | `a11y_audit` | DONE |
| 149 | Getting-Started Checklist & Progress | `getting_started` | DONE |
| 150 | What's New / Release Notes Carousel | `whats_new` | DONE |
| 151 | Referral & Invite Flow (kode, share, reward) | `referral_invite` | DONE |
| 152 | NPS/CSAT Survey + follow-up | `nps_survey` | DONE |
| 153 | Report/Block & Moderation Flow | `report_flow` | DONE |
| 154 | Secure Vault (folder ter-gate biometrik) | `secure_vault` | DONE |
| 155 | Medication Schedule & Adherence | `medication_plan` | DONE |
| 156 | Nutrition Diary & Macro Rings | `nutrition_diary` | DONE |
| 157 | Sleep Stages Hypnogram | `sleep_stages` | DONE |
| 158 | QRIS Scan-to-Pay (khas Indonesia) | `qris_pay` | DONE |
| 159 | Virtual Card Management (freeze, limit, rotate) | `virtual_card` | DONE |
| 160 | Multi-currency Exchange Converter | `currency_exchange` | DONE |
| 161 | Loan/EMI & Amortization Simulator (KPR) | `loan_simulator` | DONE |
| 162 | Cross-app Drag & Drop Simulator | `drag_drop` | DONE |

#### 143. Document Scanner & OCR (`doc_scanner`)
- Bingkai pemindai dengan deteksi tepi dan koreksi perspektif (mode manual 4 titik), lalu daftar halaman berthumbnail.
- State mendeteksi, gagal deteksi tepi, dan hasil OCR mock dengan penyorotan kata; tidak ada akses kamera/OCR nyata.

#### 144. PDF/Doc Viewer + Anotasi (`pdf_viewer`)
- Viewer dokumen dengan thumbnail halaman, pencarian, lompat halaman, dan mode gulir/halaman; lapisan anotasi (sorot, catatan) di atas halaman.
- State memuat, galat render, dan dokumen kosong; melengkapi `create_lab` yang hanya anotasi gambar.
- Konten dokumen adalah mock lokal (Canvas), bukan PDF biner.

#### 145. Code Editor & Diff Viewer (`code_editor`)
- Editor dengan nomor baris, penyorotan sintaks sederhana, dan fold blok; penampil diff inline maupun side-by-side dengan penanda tambah/hapus.
- State tidak ada perubahan, banyak perubahan, dan konflik; berguna sebagai pola tooling tanpa dependensi editor.

#### 146. Version History & Restore (`version_history`)
- Linimasa versi dengan slider waktu untuk melihat pratinjau setiap revisi, daftar perubahan, dan tombol "Pulihkan".
- Dialog konfirmasi pemulihan, state membandingkan dua versi, dan riwayat kosong.

#### 147. Localization & RTL Preview (`rtl_preview`)
- Toggle arah tata letak LTR/RTL yang mencerminkan seluruh pratinjau, plus pseudo-locale untuk uji stres panjang teks dan pemotongan label.
- Tabel contoh string per bahasa dan penanda bagian yang meluap; melengkapi pemilih bahasa `settings_lab` dengan uji tata letak.
- Implementasi murni `CompositionLocalProvider(LocalLayoutDirection)` di `commonMain`.

#### 148. Accessibility Audit Overlay (`a11y_audit`)
- Overlay yang menyorot elemen dengan masalah: target sentuh < 48dp, kontras teks di bawah AA, dan tanpa `contentDescription`.
- Daftar temuan dengan lompat ke elemen, serta toggle tampilan pohon semantik; melengkapi `accessibility_lab` yang hanya menampilkan preferensi.

#### 149. Getting-Started Checklist & Progress (`getting_started`)
- Daftar tugas awal dengan progress meter, aksi yang dapat diselesaikan langsung, dan minimalkan ke kartu ringkas.
- State belum mulai, sebagian, dan selesai (perayaan ringan, bukan confetti penuh layar); berbeda dari `onboarding` sekali jalan.

#### 150. What's New / Release Notes Carousel (`whats_new`)
- Carousel fitur baru per versi dengan ilustrasi Canvas, changelog ringkas, dan tombol "Mengerti"; badge versi baru.
- State belum dilihat, sudah dilihat, dan tanpa release notes.

#### 151. Referral & Invite Flow (`referral_invite`)
- Kartu kode referral dengan tombol salin, sheet berbagi (mock), progres undangan (mis. 2/5) menuju reward, dan riwayat reward.
- State kode belum aktif, berhasil disalin, dan reward tercapai; memakai ulang pola `share_sheet` secara mock.

#### 152. NPS/CSAT Survey (`nps_survey`)
- Skor 0–10 dengan skala berlabel, kotak alasan, dan pertanyaan lanjutan yang bercabang sesuai skor (detractor/passive/promoter).
- State terkirim, dilewati, dan validasi wajib memilih skor sebelum lanjut.

#### 153. Report/Block & Moderation Flow (`report_flow`)
- Alur laporan bertahap: pilih alasan, detail tambahan, opsi blokir, lalu konfirmasi dengan status "telah dilaporkan".
- State alasan kosong, sedang mengirim, dan gagal; semua aksi bersifat simulasi tanpa moderasi nyata.

#### 154. Secure Vault (`secure_vault`)
- Folder terkunci berisi item sensitif dengan gerbang biometrik/PIN dan kartu terkabur sebelum dibuka (memakai ulang pola `privacy_masking`).
- State terkunci, autentikasi gagal, dan terbuka dengan aksi keluar-otomatis saat idle; biometrik adalah simulasi.

#### 155. Medication Schedule & Adherence (`medication_plan`)
- Jadwal obat harian dengan dosis, penanda diminum/terlewat, streak kepatuhan, dan pengingat mock.
- State belum dijadwalkan, jatuh tempo, diminum, dan terlewat; melengkapi `health_app` yang hanya ringkasan metrik.

#### 156. Nutrition Diary & Macro Rings (`nutrition_diary`)
- Log makan per waktu dengan pencarian makanan mock, cincin makro (protein/karbohidrat/lemak), dan target kalori.
- State belum ada entri, sebagian target, dan melebihi target (peringatan, bukan hanya warna); berbeda dari `activity_rings` yang kebugaran.

#### 157. Sleep Stages Hypnogram (`sleep_stages`)
- Grafik hipnogram tahapan tidur (terjaga/REM/ringan/dalam) per malam dengan scrubber waktu dan ringkasan skor tidur.
- State tanpa data, satu malam, dan tren mingguan; seluruh data mock.

#### 158. QRIS Scan-to-Pay (`qris_pay`)
- Alur khas Indonesia: pindai/buka QR merchant, masukkan nominal (memakai ulang pola `amount_keypad`), konfirmasi, lalu bukti bayar.
- State QR tidak valid, kedaluwarsa, berhasil, dan gagal dengan tombol coba lagi; label simulasi jelas (tanpa pembayaran nyata).

#### 159. Virtual Card Management (`virtual_card`)
- Kartu virtual dengan toggle freeze instan, batas transaksi, rotasi nomor kartu, dan riwayat pemakaian.
- State aktif, dibekukan, dan limit tercapai; melengkapi dompet `commerce_lab` dengan kontrol kartu.

#### 160. Multi-currency Exchange Converter (`currency_exchange`)
- Konverter dengan pemilih mata uang, kurs mock, grafik tren mini, dan riwayat konversi; swap mata uang beranimasi.
- State kurs sedang dimuat, kurs basi, dan nilai tidak valid.

#### 161. Loan/EMI & Amortization Simulator (`loan_simulator`)
- Simulator KPR/pinjaman dengan slider pokok, tenor, dan bunga; ringkasan cicilan serta tabel amortisasi yang dapat digulir.
- State skenario pelunasan dipercepat, perbandingan dua skenario, dan input tidak valid; seluruh perhitungan lokal.

#### 162. Cross-app Drag & Drop Simulator (`drag_drop`)
- Simulasi menyeret item keluar dari satu panel "app" ke panel lain (atau dock) dengan bayangan seret dan drop zone yang menyala.
- State draf, ditolak (drop invalid), dan berhasil dengan undo; melengkapi reorder `advanced_list` & `block_editor` yang masih dalam satu list.

### Catatan §12

- **Sinkronisasi §11:** lab 108–122 kini **ada di kode** dan terdaftar (15 lab §11); Lab §5.3 baru 163–166 juga `DONE`. `TODO.md` sinkron dengan kode.
- Seluruh lab §12 kini `DONE` dan terdaftar.
- Setiap lab §12 mengikuti checklist lengkap §6.1 dan Definition of Done §14: route `Route<X>` + `KnownRoutes` + cabang `when` di `App.kt`, `MenuEntry` di `HomeMenuScreen.kt`, ikon Canvas khusus di `ui/components/icons/` (buat file baru bila sebuah file melewati ±800 baris), `BackHandler`, insets, lalu perbarui status `WAIT` → `DONE` pada commit yang sama.
- Beberapa ide memerlukan perhatian khusus:
  - `performance_hud` (129) diukur dari `withFrameNanos` karena tidak memakai API profiling platform; hasilnya perkiraan, bukan FPS sistem sebenarnya.
  - `rtl_preview` (147) aman di `commonMain` lewat `LocalLayoutDirection`.
  - Kolaborasi realtime (123), live dashboard (124), offline (125), dan seluruh ide bertema AI adalah **simulasi** dengan data mock lokal.
- Ikon baru untuk batch ini sebaiknya masuk file baru (mis. `Idea2026LabIcons.kt`) agar file ikon yang ada tidak melewati ±800 baris (§16.10).

### Referensi inspirasi

- [Material 3 Expressive — Android](https://blog.google/products-and-platforms/platforms/android/material-3-expressive-android-wearos-launch/)
- [Adaptive layouts — Android Developers](https://developer.android.com/develop/adaptive-apps/guides/support-different-display-sizes)
- [Apple Human Interface Guidelines — Motion](https://developer.apple.com/design/human-interface-guidelines/motion)
- [WCAG 2.2 — Contrast (Minimum)](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html)
