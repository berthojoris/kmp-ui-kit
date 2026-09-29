# TODO — Testing UI Component Roadmap

Status legend: **DONE** = sudah tersedia di app · **WAIT** = belum dikerjakan.

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

