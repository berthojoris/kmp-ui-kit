package com.example.uiapp.ui.passkeys_passwords

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.AppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.FlatSecondaryButton
import com.example.uiapp.ui.components.FlatTextField
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlin.random.Random

private enum class SheetState { TUTUP, ISI, MEMINDAI, GAGAL, SUKSES }

private enum class SiteJenis { WEB, BELANJA, SUREL, BANK }

private enum class AuditTingkat { BAHAYA, PERINGATAN, AMAN }

private data class PasskeyItem(
    val id: String,
    val domain: String,
    val username: String,
    val ditambahkan: String,
)

private data class SandiItem(
    val id: String,
    val situs: String,
    val username: String,
    val sandi: String,
    val jenis: SiteJenis,
    val seed: Long,
)

private data class AuditItem(
    val id: String,
    val judul: String,
    val detail: String,
    val tingkat: AuditTingkat,
    val situs: String,
)

private data class FlagItem(
    val id: String,
    val judul: String,
    var status: String,
    val perluTinjau: Boolean,
)

private val passkeyAwal = listOf(
    PasskeyItem("pk1", "nusantara.id", "rani.dewi", "Ditambahkan 12 Sep"),
    PasskeyItem("pk2", "toko-murah.id", "rani.dewi", "Ditambahkan 28 Sep"),
)

private val sandiAwal = listOf(
    SandiItem("s1", "nusantara.id", "rani.dewi", "Nusantara#2026", SiteJenis.WEB, 11L),
    SandiItem("s2", "bank-demo.id", "rani.dewi", "demo123", SiteJenis.BANK, 22L),
    SandiItem("s3", "toko-murah.id", "rani.dewi", "belanja123", SiteJenis.BELANJA, 33L),
    SandiItem("s4", "surel-kerja.id", "rani.dewi", "belanja123", SiteJenis.SUREL, 44L),
)

private val auditAwal = listOf(
    AuditItem("a1", "Sandi dipakai berulang", "Toko Murah & Surel Kerja memakai sandi yang sama. Status: bahaya.", AuditTingkat.BAHAYA, "toko-murah.id"),
    AuditItem("a2", "Sandi lemah", "Bank Demo memakai \"demo123\" yang mudah ditebak. Status: peringatan.", AuditTingkat.PERINGATAN, "bank-demo.id"),
    AuditItem("a3", "2 akun memakai passkey", "nusantara.id & toko-murah.id bebas sandi. Status: aman.", AuditTingkat.AMAN, "nusantara.id"),
)

private val flagAwal = listOf(
    FlagItem("f1", "Ketentuan Layanan v2.1", "Perlu ditinjau", true),
    FlagItem("f2", "Pemrosesan Data", "Disetujui", false),
    FlagItem("f3", "Komunikasi Pemasaran", "Belum disetujui", true),
)

private fun kodeVerifikasi(seed: Long, tick: Int): String {
    val rand = Random(seed + tick.toLong() * 7L)
    val digits = (0 until 6).map { rand.nextInt(10) }.joinToString("")
    return digits.substring(0, 3) + " " + digits.substring(3, 6)
}

private fun warnaTingkat(tingkat: AuditTingkat, palette: AppPalette): Color {
    return when (tingkat) {
        AuditTingkat.BAHAYA -> palette.danger
        AuditTingkat.PERINGATAN -> palette.warning
        AuditTingkat.AMAN -> palette.success
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasskeysPasswordsLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    val passkeys = remember { mutableStateListOf<PasskeyItem>().apply { addAll(passkeyAwal) } }
    val sandiList = remember { mutableStateListOf<SandiItem>().apply { addAll(sandiAwal) } }
    val flags = remember { mutableStateListOf<FlagItem>().apply { addAll(flagAwal.map { it.copy() }) } }

    var sheet by remember { mutableStateOf(SheetState.TUTUP) }
    var sheetUsername by remember { mutableStateOf("rani.dewi") }
    var revealedId by remember { mutableStateOf<String?>(null) }
    var kodeTick by remember { mutableIntStateOf(0) }
    var detikSisa by remember { mutableIntStateOf(30) }
    var banner by remember { mutableStateOf<String?>(null) }
    var popupAudit by remember { mutableStateOf<AuditItem?>(null) }
    var modalTerms by remember { mutableStateOf(false) }
    var termsChecked by remember { mutableStateOf(false) }

    // Hitung mundur rotasi kode verifikasi (30 detik) — simulasi
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            detikSisa -= 1
            if (detikSisa <= 0) {
                detikSisa = 30
                kodeTick += 1
            }
        }
    }

    LaunchedEffect(banner) {
        if (banner != null) {
            delay(2200)
            banner = null
        }
    }

    // Verifikasi Face ID simulasi: sukses otomatis setelah 1,8 detik kecuali dibatalkan
    LaunchedEffect(sheet) {
        if (sheet == SheetState.MEMINDAI) {
            delay(1800)
            if (sheet == SheetState.MEMINDAI) {
                passkeys.add(
                    PasskeyItem(
                        id = "pk-${passkeys.size + 1}",
                        domain = "nusantara-baru.id",
                        username = sheetUsername.trim(),
                        ditambahkan = "Ditambahkan hari ini",
                    ),
                )
                sheet = SheetState.SUKSES
                banner = "Passkey disimpan (Simulasi — bukan kredensial nyata)"
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = palette.background,
            topBar = {
                UiTopBar(
                    title = "Passkeys & Passwords",
                    subtitle = "Face ID, kode verifikasi, audit — Simulasi",
                    onBack = onBack,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                LabIntroCard(
                    title = "Passkeys & Passwords — Simulasi",
                    body = "Buat passkey dengan prompt Face ID tiruan, salin kode verifikasi, jalankan audit keamanan, dan lengkapi registrasi. Tidak ada Keychain, Face ID, atau kredensial sungguhan yang disimpan.",
                )

                // ===== Sheet passkey trigger + daftar passkey =====
                LabCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LabSectionTitle("Passkey", "Kredensial tanpa sandi")
                        SimulasiPill("Simulasi")
                    }
                    FlatPrimaryButton(text = "Buat Passkey Baru", onClick = { sheet = SheetState.ISI })
                    passkeys.forEach { pk ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                                .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            SiteGlyph(jenis = SiteJenis.WEB, tint = palette.primary)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pk.domain, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                Text(pk.username, fontSize = 11.sp, color = palette.textSecondary)
                                Text(pk.ditambahkan, fontSize = 10.sp, color = palette.textMuted)
                            }
                            StatusPill("PASSKEY", palette.success)
                        }
                    }
                }

                // ===== Daftar sandi + kode verifikasi =====
                LabCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LabSectionTitle("Sandi & Kode Verifikasi", "Ketuk kode untuk menyalin")
                        SimulasiPill("Simulasi")
                    }
                    sandiList.forEach { s ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                                .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            SiteGlyph(jenis = s.jenis, tint = palette.info)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(s.situs, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                Text(s.username, fontSize = 11.sp, color = palette.textSecondary)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = if (revealedId == s.id) s.sandi else "••••••••",
                                        fontSize = 11.sp,
                                        color = palette.textMuted,
                                    )
                                    Text(
                                        text = if (revealedId == s.id) "Sembunyikan" else "Lihat",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.primary,
                                        modifier = Modifier
                                            .clickable {
                                                revealedId = if (revealedId == s.id) null else s.id
                                            }
                                            .semantics {
                                                role = Role.Button
                                                contentDescription =
                                                    if (revealedId == s.id) "Sembunyikan sandi ${s.situs}" else "Lihat sandi ${s.situs}"
                                            },
                                    )
                                }
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                            ) {
                                val kode = kodeVerifikasi(s.seed, kodeTick)
                                Surface(
                                    onClick = { banner = "Kode $kode disalin ke papan klip (Simulasi)" },
                                    shape = RoundedCornerShape(10.dp),
                                    color = palette.surface,
                                    border = BorderStroke(1.dp, palette.border),
                                    shadowElevation = 0.dp,
                                    tonalElevation = 0.dp,
                                    modifier = Modifier.semantics {
                                        role = Role.Button
                                        contentDescription = "Salin kode verifikasi ${s.situs}, nilai $kode"
                                    },
                                ) {
                                    Text(
                                        text = kode,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.textPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    )
                                }
                                Text("diperbarui ${detikSisa}s", fontSize = 9.sp, color = palette.textMuted)
                            }
                        }
                    }
                }

                // ===== Audit keamanan =====
                LabCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LabSectionTitle("Audit Keamanan", "Rekomendasi perbaikan sandi")
                        SimulasiPill("Simulasi")
                    }
                    auditAwal.forEach { audit ->
                        val tint = warnaTingkat(audit.tingkat, palette)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                                .border(1.dp, tint.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AuditGlyph(audit.tingkat, tint)
                            Column(modifier = Modifier.weight(1f)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(audit.judul, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                    StatusPill(
                                        when (audit.tingkat) {
                                            AuditTingkat.BAHAYA -> "BAHAYA"
                                            AuditTingkat.PERINGATAN -> "PERINGATAN"
                                            AuditTingkat.AMAN -> "AMAN"
                                        },
                                        tint,
                                    )
                                }
                                Text(audit.detail, fontSize = 10.sp, color = palette.textSecondary)
                            }
                            if (audit.tingkat != AuditTingkat.AMAN) {
                                Surface(
                                    onClick = { popupAudit = audit },
                                    shape = RoundedCornerShape(8.dp),
                                    color = palette.primary,
                                    shadowElevation = 0.dp,
                                    tonalElevation = 0.dp,
                                ) {
                                    Text(
                                        text = "Ubah di Situs",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.onPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                // ===== Registrasi Saat Ini =====
                LabCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LabSectionTitle("Registrasi Saat Ini", "Persetujuan akun & dokumen hukum")
                        SimulasiPill("Simulasi")
                    }
                    flags.forEach { flag ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f),
                            ) {
                                if (flag.perluTinjau) {
                                    FlagGlyph()
                                }
                                Column {
                                    Text(flag.judul, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                                    Text(
                                        "Status: ${flag.status}",
                                        fontSize = 10.sp,
                                        color = if (flag.perluTinjau) palette.warning else palette.textMuted,
                                    )
                                }
                            }
                            StatusPill(
                                if (flag.perluTinjau) "PERLU DITINJAU" else "LENGKAP",
                                if (flag.perluTinjau) palette.warning else palette.success,
                            )
                        }
                    }
                    FlatSecondaryButton(
                        text = "Buka Syarat & Ketentuan",
                        onClick = {
                            modalTerms = true
                            termsChecked = false
                        },
                    )
                }

                // ===== Banner =====
                AnimatedVisibility(visible = banner != null, enter = fadeIn(), exit = fadeOut()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = palette.info.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, palette.info),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = banner ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.info,
                            modifier = Modifier.padding(10.dp),
                        )
                    }
                }

                // ===== Manfaat =====
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Manfaat Pola Passkeys & Passwords:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                        Text("• Sheet passkey khas iOS + prompt Face ID membangun rasa aman tanpa API biometrik", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Kode verifikasi berputar tiap 30 detik mengajarkan pola TOTP sekali ketuk untuk menyalin", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Audit berlabel teks (BAHAYA/PERINGATAN/AMAN) tidak mengandalkan warna saja", fontSize = 12.sp, color = palette.textSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // ===== Bottom sheet Simpan Passkey =====
        AnimatedVisibility(visible = sheet != SheetState.TUTUP, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(palette.textPrimary.copy(alpha = 0.45f))
                    .clickable { sheet = SheetState.TUTUP },
                contentAlignment = Alignment.BottomCenter,
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .pointerInput(Unit) { detectTapGestures { /* serap ketukan di dalam sheet */ } },
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(palette.border),
                        )
                        when (sheet) {
                            SheetState.ISI -> {
                                SiteGlyph(jenis = SiteJenis.WEB, tint = palette.primary, besar = true)
                                Text("Simpan Passkey", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                Text("nusantara-baru.id", fontSize = 12.sp, color = palette.textMuted)
                                FlatTextField(
                                    label = "Nama Pengguna",
                                    value = sheetUsername,
                                    onValueChange = { sheetUsername = it },
                                    placeholder = "mis. rani.dewi",
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    FaceOutlineIcon()
                                    Text(
                                        "Face ID akan dipakai untuk konfirmasi (Simulasi).",
                                        fontSize = 11.sp,
                                        color = palette.textSecondary,
                                    )
                                }
                                FlatPrimaryButton(
                                    text = "Simpan dengan Face ID",
                                    onClick = { sheet = SheetState.MEMINDAI },
                                    enabled = sheetUsername.trim().isNotEmpty(),
                                )
                                FlatSecondaryButton(text = "Batal", onClick = { sheet = SheetState.TUTUP })
                            }
                            SheetState.MEMINDAI -> {
                                FaceScanIcon()
                                Text("Memindai Wajah…", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                Text(
                                    "Verifikasi biometrik tiruan (Simulasi) — tidak ada data wajah yang diakses.",
                                    fontSize = 11.sp,
                                    color = palette.textSecondary,
                                )
                                FlatSecondaryButton(
                                    text = "Batalkan Verifikasi",
                                    onClick = { sheet = SheetState.GAGAL },
                                )
                            }
                            SheetState.GAGAL -> {
                                GagalIcon()
                                Text("Verifikasi Gagal", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.danger)
                                Text(
                                    "Status: gagal — wajah tidak dikenali. Ubah posisi lalu coba lagi.",
                                    fontSize = 11.sp,
                                    color = palette.textSecondary,
                                )
                                FlatPrimaryButton(text = "Coba Lagi", onClick = { sheet = SheetState.MEMINDAI })
                                FlatSecondaryButton(text = "Tutup", onClick = { sheet = SheetState.TUTUP })
                            }
                            SheetState.SUKSES -> {
                                BerhasilIcon()
                                Text("Passkey Tersimpan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.success)
                                Text(
                                    "Status: sukses — passkey nusantara-baru.id ditambahkan (Simulasi).",
                                    fontSize = 11.sp,
                                    color = palette.textSecondary,
                                )
                                FlatPrimaryButton(text = "Selesai", onClick = { sheet = SheetState.TUTUP })
                            }
                            SheetState.TUTUP -> Unit
                        }
                    }
                }
            }
        }

        // ===== Popup rekomendasi audit =====
        val auditPop = popupAudit
        if (auditPop != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(palette.textPrimary.copy(alpha = 0.45f))
                    .clickable { popupAudit = null },
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .padding(24.dp)
                        .pointerInput(Unit) { detectTapGestures { /* serap ketukan di dalam popup */ } },
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        LabSectionTitle("Ubah Sandi di ${auditPop.situs}", "Rekomendasi otomatis")
                        Text(auditPop.judul, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        Text(
                            "1. Buka halaman keamanan ${auditPop.situs} (Simulasi)\n2. Ganti sandi dengan minimal 12 karakter\n3. Aktifkan verifikasi dua langkah",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = palette.textSecondary,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FlatSecondaryButton(
                                text = "Nanti Saja",
                                onClick = { popupAudit = null },
                                modifier = Modifier.weight(1f),
                            )
                            FlatPrimaryButton(
                                text = "Buka (Simulasi)",
                                onClick = {
                                    popupAudit = null
                                    banner = "Halaman ${auditPop.situs} dibuka (Simulasi)"
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }

        // ===== Modal Syarat & Ketentuan =====
        if (modalTerms) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(palette.textPrimary.copy(alpha = 0.45f))
                    .clickable { modalTerms = false },
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .padding(20.dp)
                        .pointerInput(Unit) { detectTapGestures { /* serap ketukan di dalam modal */ } },
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            FlagGlyph()
                            LabSectionTitle("Syarat & Ketentuan", "Registrasi akun · klausul berflag perlu perhatian")
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 220.dp)
                                .verticalScroll(rememberScrollState())
                                .clip(RoundedCornerShape(10.dp))
                                .background(palette.surfaceMuted, RoundedCornerShape(10.dp))
                                .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                "1.1 Dengan membuat akun, pengguna menyetujui kebijakan layanan yang berlaku di wilayah Republik Indonesia.",
                                fontSize = 11.sp,
                                lineHeight = 17.sp,
                                color = palette.textSecondary,
                            )
                            Text(
                                "2.3 Data profil hanya dipakai untuk keperluan autentikasi dan pemulihan akun.",
                                fontSize = 11.sp,
                                lineHeight = 17.sp,
                                color = palette.textSecondary,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FlagGlyph()
                                Text(
                                    "4.2 ⚑ Perlu perhatian: riwayat keamanan disimpan selama 24 bulan untuk keperluan audit.",
                                    fontSize = 11.sp,
                                    lineHeight = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.warning,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            Text(
                                "5.1 Pengguna dapat mencabut persetujuan kapan saja melalui halaman registrasi.",
                                fontSize = 11.sp,
                                lineHeight = 17.sp,
                                color = palette.textSecondary,
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { termsChecked = !termsChecked }
                                .padding(4.dp)
                                .semantics {
                                    role = Role.Checkbox
                                    contentDescription = "Persetujuan syarat dan ketentuan"
                                },
                        ) {
                            CheckBoxKustom(checked = termsChecked)
                            Text(
                                "Saya telah membaca dan menyetujui (status: ${if (termsChecked) "dicentang" else "belum dicentang"})",
                                fontSize = 11.sp,
                                color = palette.textPrimary,
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FlatSecondaryButton(
                                text = "Tutup",
                                onClick = { modalTerms = false },
                                modifier = Modifier.weight(1f),
                            )
                            FlatPrimaryButton(
                                text = "Setujui & Lanjutkan",
                                onClick = {
                                    val idx1 = flags.indexOfFirst { it.id == "f1" }
                                    if (idx1 >= 0) flags[idx1] = flags[idx1].copy(status = "Disetujui", perluTinjau = false)
                                    val idx3 = flags.indexOfFirst { it.id == "f3" }
                                    if (idx3 >= 0) flags[idx3] = flags[idx3].copy(status = "Disetujui", perluTinjau = false)
                                    modalTerms = false
                                    banner = "Registrasi diperbarui (Simulasi)"
                                },
                                enabled = termsChecked,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimulasiPill(text: String) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = palette.info.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, palette.info),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = palette.info,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun StatusPill(label: String, tint: Color) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = tint.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, tint),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = tint,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun SiteGlyph(jenis: SiteJenis, tint: Color, besar: Boolean = false) {
    val ukuran = if (besar) 56.dp else 36.dp
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .size(ukuran)
            .clip(RoundedCornerShape(10.dp))
            .background(tint.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
            .border(1.dp, tint, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(ukuran * 0.6f)) {
            val w = size.width
            val h = size.height
            val garis = Stroke(width = 2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            when (jenis) {
                SiteJenis.WEB -> {
                    drawCircle(tint, radius = w * 0.45f, center = center, style = garis)
                    drawLine(tint, Offset(w * 0.05f, h * 0.5f), Offset(w * 0.95f, h * 0.5f), 2f, StrokeCap.Round)
                    drawArc(
                        color = tint,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(w * 0.28f, h * 0.05f),
                        size = Size(w * 0.44f, h * 0.9f),
                        style = garis,
                    )
                }
                SiteJenis.BELANJA -> {
                    drawRoundRect(
                        color = tint,
                        topLeft = Offset(w * 0.15f, h * 0.3f),
                        size = Size(w * 0.7f, h * 0.55f),
                        cornerRadius = CornerRadius(3f, 3f),
                        style = garis,
                    )
                    val pegangan = Path().apply {
                        moveTo(w * 0.33f, h * 0.3f)
                        quadraticBezierTo(w * 0.5f, h * 0.08f, w * 0.67f, h * 0.3f)
                    }
                    drawPath(pegangan, color = tint, style = garis)
                }
                SiteJenis.SUREL -> {
                    drawRoundRect(
                        color = tint,
                        topLeft = Offset(w * 0.08f, h * 0.22f),
                        size = Size(w * 0.84f, h * 0.56f),
                        cornerRadius = CornerRadius(3f, 3f),
                        style = garis,
                    )
                    val lipat = Path().apply {
                        moveTo(w * 0.08f, h * 0.28f)
                        lineTo(w * 0.5f, h * 0.55f)
                        lineTo(w * 0.92f, h * 0.28f)
                    }
                    drawPath(lipat, color = tint, style = garis)
                }
                SiteJenis.BANK -> {
                    drawLine(tint, Offset(w * 0.1f, h * 0.35f), Offset(w * 0.9f, h * 0.35f), 2f, StrokeCap.Round)
                    drawLine(tint, Offset(w * 0.25f, h * 0.42f), Offset(w * 0.25f, h * 0.75f), 2f, StrokeCap.Round)
                    drawLine(tint, Offset(w * 0.5f, h * 0.42f), Offset(w * 0.5f, h * 0.75f), 2f, StrokeCap.Round)
                    drawLine(tint, Offset(w * 0.75f, h * 0.42f), Offset(w * 0.75f, h * 0.75f), 2f, StrokeCap.Round)
                    drawLine(tint, Offset(w * 0.1f, h * 0.82f), Offset(w * 0.9f, h * 0.82f), 2f, StrokeCap.Round)
                }
            }
        }
    }
}

@Composable
private fun AuditGlyph(tingkat: AuditTingkat, tint: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val w = size.width
        val h = size.height
        val garis = Stroke(width = 2.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (tingkat) {
            AuditTingkat.BAHAYA -> {
                drawCircle(tint, radius = w * 0.42f, center = center, style = garis)
                drawLine(tint, Offset(w * 0.35f, h * 0.35f), Offset(w * 0.65f, h * 0.65f), 2.4f, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.65f, h * 0.35f), Offset(w * 0.35f, h * 0.65f), 2.4f, StrokeCap.Round)
            }
            AuditTingkat.PERINGATAN -> {
                val segitiga = Path().apply {
                    moveTo(w * 0.5f, h * 0.14f)
                    lineTo(w * 0.9f, h * 0.82f)
                    lineTo(w * 0.1f, h * 0.82f)
                    close()
                }
                drawPath(segitiga, color = tint, style = garis)
                drawLine(tint, Offset(w * 0.5f, h * 0.38f), Offset(w * 0.5f, h * 0.58f), 2.4f, StrokeCap.Round)
                drawCircle(tint, radius = 1.6f, center = Offset(w * 0.5f, h * 0.7f))
            }
            AuditTingkat.AMAN -> {
                drawCircle(tint, radius = w * 0.42f, center = center, style = garis)
                val centang = Path().apply {
                    moveTo(w * 0.3f, h * 0.52f)
                    lineTo(w * 0.45f, h * 0.66f)
                    lineTo(w * 0.72f, h * 0.36f)
                }
                drawPath(centang, color = tint, style = garis)
            }
        }
    }
}

@Composable
private fun FlagGlyph() {
    val palette = LocalAppPalette.current
    Canvas(modifier = Modifier.size(16.dp)) {
        val garis = Stroke(width = 2.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = size.width
        val h = size.height
        drawLine(palette.warning, Offset(w * 0.25f, h * 0.1f), Offset(w * 0.25f, h * 0.92f), 2.2f, StrokeCap.Round)
        val bendera = Path().apply {
            moveTo(w * 0.25f, h * 0.16f)
            lineTo(w * 0.88f, h * 0.3f)
            lineTo(w * 0.25f, h * 0.52f)
            close()
        }
        drawPath(bendera, color = palette.warning, style = garis)
    }
}

@Composable
private fun FaceOutlineIcon() {
    val palette = LocalAppPalette.current
    Canvas(modifier = Modifier.size(20.dp)) {
        val garis = Stroke(width = 2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = palette.textSecondary,
            topLeft = Offset(w * 0.1f, h * 0.1f),
            size = Size(w * 0.8f, h * 0.8f),
            cornerRadius = CornerRadius(w * 0.22f, w * 0.22f),
            style = garis,
        )
        drawLine(palette.textSecondary, Offset(w * 0.35f, h * 0.32f), Offset(w * 0.35f, h * 0.45f), 2f, StrokeCap.Round)
        drawLine(palette.textSecondary, Offset(w * 0.65f, h * 0.32f), Offset(w * 0.65f, h * 0.45f), 2f, StrokeCap.Round)
        val mulut = Path().apply {
            moveTo(w * 0.35f, h * 0.62f)
            quadraticBezierTo(w * 0.5f, h * 0.75f, w * 0.65f, h * 0.62f)
        }
        drawPath(mulut, color = palette.textSecondary, style = garis)
    }
}

@Composable
private fun FaceScanIcon() {
    val palette = LocalAppPalette.current
    val transition = rememberInfiniteTransition(label = "faceScan")
    val fase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "scanLine",
    )
    Canvas(modifier = Modifier.size(72.dp)) {
        val garis = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = palette.primary,
            topLeft = Offset(w * 0.08f, h * 0.08f),
            size = Size(w * 0.84f, h * 0.84f),
            cornerRadius = CornerRadius(w * 0.22f, w * 0.22f),
            style = garis,
        )
        drawLine(palette.primary, Offset(w * 0.33f, h * 0.3f), Offset(w * 0.33f, h * 0.45f), 3f, StrokeCap.Round)
        drawLine(palette.primary, Offset(w * 0.67f, h * 0.3f), Offset(w * 0.67f, h * 0.45f), 3f, StrokeCap.Round)
        val mulut = Path().apply {
            moveTo(w * 0.35f, h * 0.6f)
            quadraticBezierTo(w * 0.5f, h * 0.74f, w * 0.65f, h * 0.6f)
        }
        drawPath(mulut, color = palette.primary, style = garis)
        val y = h * (0.2f + 0.6f * fase)
        drawLine(
            palette.info,
            Offset(w * 0.15f, y),
            Offset(w * 0.85f, y),
            3.5f,
            StrokeCap.Round,
        )
    }
}

@Composable
private fun GagalIcon() {
    val palette = LocalAppPalette.current
    Canvas(modifier = Modifier.size(56.dp)) {
        val garis = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = palette.danger,
            topLeft = Offset(w * 0.08f, h * 0.08f),
            size = Size(w * 0.84f, h * 0.84f),
            cornerRadius = CornerRadius(w * 0.22f, w * 0.22f),
            style = garis,
        )
        drawLine(palette.danger, Offset(w * 0.35f, h * 0.35f), Offset(w * 0.45f, h * 0.45f), 3f, StrokeCap.Round)
        drawLine(palette.danger, Offset(w * 0.45f, h * 0.35f), Offset(w * 0.35f, h * 0.45f), 3f, StrokeCap.Round)
        drawLine(palette.danger, Offset(w * 0.55f, h * 0.35f), Offset(w * 0.65f, h * 0.45f), 3f, StrokeCap.Round)
        drawLine(palette.danger, Offset(w * 0.65f, h * 0.35f), Offset(w * 0.55f, h * 0.45f), 3f, StrokeCap.Round)
        val mulut = Path().apply {
            moveTo(w * 0.35f, h * 0.68f)
            quadraticBezierTo(w * 0.5f, h * 0.58f, w * 0.65f, h * 0.68f)
        }
        drawPath(mulut, color = palette.danger, style = garis)
    }
}

@Composable
private fun BerhasilIcon() {
    val palette = LocalAppPalette.current
    Canvas(modifier = Modifier.size(56.dp)) {
        val garis = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = size.width
        val h = size.height
        drawCircle(palette.success, radius = w * 0.42f, center = center, style = garis)
        val centang = Path().apply {
            moveTo(w * 0.3f, h * 0.52f)
            lineTo(w * 0.45f, h * 0.66f)
            lineTo(w * 0.72f, h * 0.36f)
        }
        drawPath(centang, color = palette.success, style = garis)
    }
}

@Composable
private fun CheckBoxKustom(checked: Boolean) {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (checked) palette.primary else palette.surface, RoundedCornerShape(6.dp))
            .border(1.dp, if (checked) palette.primary else palette.border, RoundedCornerShape(6.dp)),
    ) {
        if (checked) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val centang = Path().apply {
                    moveTo(w * 0.25f, h * 0.52f)
                    lineTo(w * 0.44f, h * 0.7f)
                    lineTo(w * 0.76f, h * 0.3f)
                }
                drawPath(
                    centang,
                    color = palette.onPrimary,
                    style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
            }
        }
    }
}
