package com.example.uiapp.ui.notificationbanner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.FlatSecondaryButton
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DURASI_MS = 6000L
private const val MAKS_ANTREAN = 3

private enum class StatusUnduh { SIAP, MEMUAT, GAGAL, SUKSES }

private data class BannerNotif(
    val id: Int,
    val inisial: String,
    val nama: String,
    val waktu: String,
    val judul: String,
    val isi: String,
    val aksiUtama: String,
    val aksiKedua: String,
    val richJudul: String,
    val richIsi: String,
    val punyaLampiran: Boolean,
)

private val ContohNotifikasi: List<BannerNotif> = listOf(
    BannerNotif(
        id = 1,
        inisial = "SR",
        nama = "Sari Rahmawati",
        waktu = "Baru saja",
        judul = "Rapat dimajukan",
        isi = "“Rapat mingguan dimajukan ke pukul 10.00.”",
        aksiUtama = "Balas",
        aksiKedua = "Nanti",
        richJudul = "Grup Tim · Rapat Proyek",
        richIsi = "Sari menjadwalkan ulang rapat mingguan ke Kamis pukul 10.00 di Ruang Melati. Agenda: demo prototipe, pembagian tugas, dan tinjauan anggaran.",
        punyaLampiran = true,
    ),
    BannerNotif(
        id = 2,
        inisial = "TR",
        nama = "Tim Relasi",
        waktu = "2 mnt lalu",
        judul = "3 foto baru dibagikan",
        isi = "Dewi membagikan foto dari acara kantor.",
        aksiUtama = "Lihat",
        aksiKedua = "Tutup",
        richJudul = "Album Bagikan · Acara Kantor",
        richIsi = "Dewi membagikan 3 foto dari acara kantor ke album bersama. Semua foto adalah ilustrasi gradien lokal, bukan gambar nyata.",
        punyaLampiran = false,
    ),
    BannerNotif(
        id = 3,
        inisial = "PG",
        nama = "Pengingat Anggaran",
        waktu = "5 mnt lalu",
        judul = "Laporan bulanan siap",
        isi = "Rekap pengeluaran September menunggu tinjauanmu.",
        aksiUtama = "Buka",
        aksiKedua = "Ingatkan",
        richJudul = "Keuangan · Laporan September",
        richIsi = "Laporan pengeluaran September disusun dari data contoh lokal. Tinjau sebelum akhir pekan lalu tandai sudah diperiksa.",
        punyaLampiran = true,
    ),
    BannerNotif(
        id = 4,
        inisial = "AK",
        nama = "Aplikasi Kesehatan",
        waktu = "8 mnt lalu",
        judul = "Target langkah tercapai 🎉",
        isi = "8.420 langkah hari ini — rekor baru (Simulasi)!",
        aksiUtama = "Lihat Ringkasan",
        aksiKedua = "Nanti",
        richJudul = "Kesehatan · Langkah Harian",
        richIsi = "Target 8.000 langkah tercapai dengan 8.420 langkah dari data simulasi. Ringkasan mingguan tersedia di tab Kesehatan.",
        punyaLampiran = false,
    ),
    BannerNotif(
        id = 5,
        inisial = "KV",
        nama = "Kuis Vocab",
        waktu = "12 mnt lalu",
        judul = "Kuis harian menanti",
        isi = "5 soal siap — selesaikan sebelum tengah malam.",
        aksiUtama = "Mulai",
        aksiKedua = "Nanti",
        richJudul = "Kuis · Latihan Harian",
        richIsi = "Lima soal latihan harian disusun dari bank soal contoh. Menyelesaikan kuis menjaga streak belajarmu tetap menyala.",
        punyaLampiran = true,
    ),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NotificationBannerLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()

    var aktif by remember { mutableStateOf<BannerNotif?>(ContohNotifikasi[0]) }
    var kursor by remember { mutableIntStateOf(3) }
    val antrean = remember {
        mutableStateListOf<BannerNotif>().apply {
            add(ContohNotifikasi[1])
            add(ContohNotifikasi[2])
        }
    }
    val ringkasan = remember { mutableStateListOf<BannerNotif>() }
    var dnd by remember { mutableStateOf(false) }
    var jeda by remember { mutableStateOf(false) }
    var sisaMs by remember { mutableStateOf(DURASI_MS) }
    var diperluas by remember { mutableStateOf(false) }
    var statusUnduh by remember { mutableStateOf(StatusUnduh.SIAP) }
    var percobaanUnduh by remember { mutableIntStateOf(0) }
    var kirimMemuat by remember { mutableStateOf(false) }
    var pesanAksi by remember { mutableStateOf<String?>(null) }
    var pesanSukses by remember { mutableStateOf(true) }
    val geser = remember { Animatable(0f) }

    fun tampilkan(n: BannerNotif) {
        aktif = n
        sisaMs = DURASI_MS
        diperluas = false
        statusUnduh = StatusUnduh.SIAP
        percobaanUnduh = 0
    }

    fun buang(buangKeRingkasan: Boolean) {
        val keluar = aktif
        aktif = null
        diperluas = false
        statusUnduh = StatusUnduh.SIAP
        if (buangKeRingkasan && keluar != null) {
            ringkasan.add(0, keluar)
        }
        if (antrean.isNotEmpty()) {
            if (dnd) {
                ringkasan.addAll(antrean)
                antrean.clear()
            } else {
                tampilkan(antrean.removeAt(0))
            }
        }
    }

    // Hitung mundur auto-dismiss — berhenti saat dijeda dan lanjut saat dilanjutkan
    LaunchedEffect(aktif?.id, jeda) {
        if (aktif != null && !jeda) {
            while (sisaMs > 0L && !jeda) {
                delay(100L)
                sisaMs -= 100L
            }
            if (sisaMs <= 0L) {
                buang(buangKeRingkasan = false)
            }
        }
    }

    // Banner meluncur turun dari atas setiap kali konten berganti
    LaunchedEffect(aktif?.id) {
        if (aktif != null) {
            geser.snapTo(-340f)
            geser.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            )
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "In-App Notification Banner",
                subtitle = "Banner masuk, expand, antrean — Simulasi",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Ruang kosong di bawah banner overlay yang meluncur dari atas
                    Spacer(modifier = Modifier.height(168.dp))

                    LabIntroCard(
                        title = "Perilaku Banner yang Masuk",
                        body = "Fokus lab ini adalah banner yang meluncur dari atas: tarik ke atas untuk membuang, " +
                            "tarik ke bawah untuk memperluas ke konten kaya. Berbeda dari Activity Inbox yang menampilkan " +
                            "daftar riwayat. Seluruh notifikasi adalah contoh lokal — bukan push notification nyata (Simulasi).",
                    )

                    // Status & aksi ringkas
                    LabCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Status Banner",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = palette.textPrimary,
                            )
                            Text(
                                text = "Antrean ${antrean.size}/$MAKS_ANTREAN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (antrean.size >= MAKS_ANTREAN) palette.danger else palette.primary,
                            )
                        }
                        Text(
                            text = "Auto-dismiss 6 detik · ${if (jeda) "hitung mundur DIJEDA" else "hitung mundur berjalan"} · " +
                                "Diredam: ${ringkasan.size} di ringkasan",
                            fontSize = 11.sp,
                            color = palette.textSecondary,
                        )
                        if (pesanAksi != null) {
                            PilStatus(pesan = pesanAksi ?: "", sukses = pesanSukses)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FlatSecondaryButton(
                                text = "Buka Semua Ringkasan",
                                onClick = {
                                    dnd = false
                                    antrean.addAll(ringkasan)
                                    ringkasan.clear()
                                    pesanSukses = true
                                    pesanAksi = "Ringkasan dipindah balik ke antrean (Simulasi)"
                                    if (aktif == null && antrean.isNotEmpty()) {
                                        tampilkan(antrean.removeAt(0))
                                    }
                                },
                                enabled = ringkasan.isNotEmpty(),
                                modifier = Modifier.weight(1f),
                            )
                            FlatSecondaryButton(
                                text = "Bersihkan Antrean",
                                onClick = {
                                    antrean.clear()
                                    pesanSukses = true
                                    pesanAksi = "Antrean dikosongkan (Simulasi)"
                                },
                                enabled = antrean.isNotEmpty(),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    // Antrean bertumpuk
                    LabCard {
                        Text(
                            text = "Antrean Banner Bertumpuk",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = palette.textPrimary,
                        )
                        if (antrean.isEmpty()) {
                            Text(
                                text = "Antrean kosong — banner berikutnya langsung tampil saat dikirim.",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                            )
                        } else {
                            antrean.forEachIndexed { idx, n ->
                                BarisAntrean(
                                    indeks = idx + 1,
                                    notif = n,
                                    onKeluarkan = {
                                        antrean.removeAt(idx)
                                        pesanSukses = true
                                        pesanAksi = "«${n.judul}» dikeluarkan dari antrean (Simulasi)"
                                    },
                                )
                            }
                        }
                    }

                    // Ringkasan Jangan Ganggu
                    LabCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Ringkasan Jangan Ganggu",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = palette.textPrimary,
                            )
                            Text(
                                text = if (dnd) "Mode DND AKTIF" else "DND nonaktif",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (dnd) palette.warning.copy(alpha = 0.15f) else palette.surfaceMuted,
                                    )
                                    .border(
                                        1.dp,
                                        if (dnd) palette.warning else palette.border,
                                        RoundedCornerShape(50),
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                        Text(
                            text = "Saat Jangan Ganggu aktif, banner diredam dan dipindah ke ringkasan ini.",
                            fontSize = 11.sp,
                            color = palette.textSecondary,
                        )
                        if (ringkasan.isEmpty()) {
                            Text(
                                text = "Ringkasan kosong — belum ada notifikasi yang diredam.",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                            )
                        } else {
                            ringkasan.take(4).forEach { n ->
                                BarisRingkasan(
                                    notif = n,
                                    onTampilkan = {
                                        if (aktif == null) {
                                            ringkasan.remove(n)
                                            tampilkan(n)
                                            pesanSukses = true
                                            pesanAksi = "«${n.judul}» dipaksa tampil (Simulasi)"
                                        } else {
                                            pesanSukses = false
                                            pesanAksi = "Masih ada banner aktif — selesaikan dulu"
                                        }
                                    },
                                )
                            }
                            if (ringkasan.size > 4) {
                                Text(
                                    text = "…${ringkasan.size - 4} notifikasi lain diredam",
                                    fontSize = 11.sp,
                                    color = palette.textMuted,
                                )
                            }
                        }
                    }

                    // Catatan pola
                    LabCard {
                        Text(
                            text = "Manfaat Pola In-App Banner:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = "• Interruption minimal: pengguna membuang, menunda, atau memperluas tanpa meninggalkan layar",
                            fontSize = 12.sp,
                            color = palette.textSecondary,
                        )
                        Text(
                            text = "• Antrean tertib: maksimal tiga banner menunggu agar tidak membanjiri layar",
                            fontSize = 12.sp,
                            color = palette.textSecondary,
                        )
                        Text(
                            text = "• Jangan Ganggu: banner diredam menjadi ringkasan, bukan hilang selamanya",
                            fontSize = 12.sp,
                            color = palette.textSecondary,
                        )
                        Text(
                            text = "• Zero Shadow Flat UI: banner dibedakan oleh border 1px dan gradien avatar, bukan bayangan",
                            fontSize = 12.sp,
                            color = palette.textSecondary,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Action bar tetap di dasar layar
                Column(modifier = Modifier.fillMaxWidth().background(palette.surface)) {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        FlatPrimaryButton(
                            text = if (kirimMemuat) "Mengirim…" else "Kirim Uji (Simulasi)",
                            onClick = {
                                if (!kirimMemuat) {
                                    kirimMemuat = true
                                    scope.launch {
                                        delay(700)
                                        val next = ContohNotifikasi[kursor % ContohNotifikasi.size]
                                        kursor++
                                        when {
                                            dnd -> {
                                                ringkasan.add(0, next)
                                                pesanSukses = true
                                                pesanAksi = "«${next.judul}» diredam ke ringkasan (Simulasi)"
                                            }
                                            aktif == null -> tampilkan(next)
                                            antrean.size < MAKS_ANTREAN -> {
                                                antrean.add(next)
                                                pesanSukses = true
                                                pesanAksi = "«${next.judul}» masuk antrean (Simulasi)"
                                            }
                                            else -> {
                                                pesanSukses = false
                                                pesanAksi = "Antrean penuh — banner dibuang (Simulasi)"
                                            }
                                        }
                                        kirimMemuat = false
                                    }
                                }
                            },
                            enabled = !kirimMemuat,
                            modifier = Modifier.weight(1.4f),
                        )
                        FlatSecondaryButton(
                            text = if (jeda) "Lanjut" else "Jeda",
                            onClick = {
                                jeda = !jeda
                                pesanSukses = true
                                pesanAksi = if (jeda) {
                                    "Hitung mundur dijeda pada ${(sisaMs / 1000L).toInt()} detik (Simulasi)"
                                } else {
                                    "Hitung mundur dilanjutkan (Simulasi)"
                                }
                            },
                            enabled = aktif != null,
                            modifier = Modifier.weight(1f),
                        )
                        FlatSecondaryButton(
                            text = if (dnd) "DND Aktif" else "DND Mati",
                            onClick = {
                                dnd = !dnd
                                if (dnd) {
                                    aktif?.let { ringkasan.add(0, it) }
                                    antrean.forEach { ringkasan.add(it) }
                                    antrean.clear()
                                    aktif = null
                                    diperluas = false
                                    pesanSukses = true
                                    pesanAksi = "Jangan Ganggu aktif — banner diredam ke ringkasan (Simulasi)"
                                } else {
                                    pesanSukses = true
                                    pesanAksi = "Jangan Ganggu nonaktif (Simulasi)"
                                }
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Overlay banner di atas konten
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Tumpukan antrean di belakang banner aktif
                antrean.take(2).reversed().forEach { n ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (n.id % 2 == 0) 0.86f else 0.93f)
                            .height(12.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                            .background(palette.surfaceMuted)
                            .border(1.dp, palette.border, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                    )
                }

                val n = aktif
                if (n != null) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(0, geser.value.roundToInt()) }
                            .pointerInput(n.id) {
                                detectVerticalDragGestures(
                                    onVerticalDrag = { change, dragAmount ->
                                        change.consume()
                                        scope.launch {
                                            geser.snapTo((geser.value + dragAmount).coerceIn(-420f, 320f))
                                        }
                                    },
                                    onDragEnd = {
                                        when {
                                            geser.value < -90f -> {
                                                scope.launch {
                                                    geser.animateTo(-440f, tween(180))
                                                    pesanSukses = true
                                                    pesanAksi = "Banner dibuang dengan tarik ke atas (Simulasi)"
                                                    buang(buangKeRingkasan = false)
                                                }
                                            }
                                            geser.value > 90f -> {
                                                diperluas = true
                                                scope.launch {
                                                    geser.animateTo(
                                                        0f,
                                                        spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                                    )
                                                }
                                            }
                                            else -> {
                                                scope.launch {
                                                    geser.animateTo(
                                                        0f,
                                                        spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                                    )
                                                }
                                            }
                                        }
                                    },
                                )
                            }
                            .semantics {
                                role = Role.Button
                                contentDescription = "Banner dari ${n.nama}: ${n.judul}. Tarik ke atas untuk membuang, " +
                                    "tarik ke bawah untuk memperluas ke konten kaya."
                            },
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(palette.primaryContainer)
                                        .border(1.dp, palette.primary, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = n.inisial,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = palette.onPrimaryContainer,
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = n.nama,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = n.waktu + " · Simulasi",
                                        fontSize = 10.sp,
                                        color = palette.textMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                Text(
                                    text = if (diperluas) "Ringkas" else "Perluas",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.primary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            diperluas = !diperluas
                                            scope.launch {
                                                geser.animateTo(
                                                    0f,
                                                    spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                                )
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                        .semantics {
                                            role = Role.Button
                                            contentDescription = if (diperluas) {
                                                "Ringkas konten kaya banner"
                                            } else {
                                                "Perluas banner ke konten kaya"
                                            }
                                        },
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(palette.surfaceMuted)
                                        .border(1.dp, palette.border, CircleShape)
                                        .clickable {
                                            pesanSukses = true
                                            pesanAksi = "Banner ditutup manual (Simulasi)"
                                            buang(buangKeRingkasan = false)
                                        }
                                        .semantics {
                                            role = Role.Button
                                            contentDescription = "Tutup notifikasi"
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("✕", fontSize = 11.sp, color = palette.textSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = n.judul,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = palette.textPrimary,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = n.isi,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = palette.textSecondary,
                            )

                            // Konten kaya saat ditarik ke bawah / diperluas
                            AnimatedVisibility(visible = diperluas) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = n.richJudul,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.textMuted,
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = n.richIsi,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = palette.textSecondary,
                                    )
                                    if (n.punyaLampiran) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        val labelUnduh = when (statusUnduh) {
                                            StatusUnduh.SIAP -> "Unduh Lampiran (Simulasi)"
                                            StatusUnduh.MEMUAT -> "Mengunduh lampiran…"
                                            StatusUnduh.GAGAL -> "Coba Lagi Unduh"
                                            StatusUnduh.SUKSES -> "Lampiran Tersimpan ✓"
                                        }
                                        Surface(
                                            onClick = {
                                                if (statusUnduh == StatusUnduh.SIAP || statusUnduh == StatusUnduh.GAGAL) {
                                                    percobaanUnduh++
                                                    statusUnduh = StatusUnduh.MEMUAT
                                                    scope.launch {
                                                        delay(900)
                                                        statusUnduh = if (percobaanUnduh <= 1) {
                                                            StatusUnduh.GAGAL
                                                        } else {
                                                            StatusUnduh.SUKSES
                                                        }
                                                    }
                                                }
                                            },
                                            enabled = statusUnduh != StatusUnduh.MEMUAT && statusUnduh != StatusUnduh.SUKSES,
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (statusUnduh == StatusUnduh.SUKSES) {
                                                palette.success.copy(alpha = 0.15f)
                                            } else {
                                                palette.surfaceMuted
                                            },
                                            border = BorderStroke(
                                                1.dp,
                                                if (statusUnduh == StatusUnduh.GAGAL) palette.danger else palette.border,
                                            ),
                                            shadowElevation = 0.dp,
                                            tonalElevation = 0.dp,
                                        ) {
                                            Text(
                                                text = labelUnduh,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = palette.textPrimary,
                                                modifier = Modifier.padding(
                                                    horizontal = 12.dp,
                                                    vertical = 9.dp,
                                                ),
                                            )
                                        }
                                        if (statusUnduh == StatusUnduh.GAGAL) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Gagal: unduhan lampiran terputus (Simulasi)",
                                                fontSize = 10.sp,
                                                color = palette.danger,
                                            )
                                        }
                                        if (statusUnduh == StatusUnduh.SUKSES) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Sukses: lampiran tersimpan ke perangkat (Simulasi)",
                                                fontSize = 10.sp,
                                                color = palette.textSecondary,
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Surface(
                                    onClick = {
                                        pesanSukses = true
                                        pesanAksi = "Aksi «${n.aksiUtama}» dijalankan (Simulasi)"
                                        buang(buangKeRingkasan = false)
                                    },
                                    enabled = statusUnduh != StatusUnduh.MEMUAT,
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (statusUnduh != StatusUnduh.MEMUAT) {
                                        palette.primary
                                    } else {
                                        palette.primary.copy(alpha = 0.4f)
                                    },
                                    shadowElevation = 0.dp,
                                    tonalElevation = 0.dp,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        text = n.aksiUtama,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.onPrimary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp),
                                    )
                                }
                                Surface(
                                    onClick = {
                                        pesanSukses = true
                                        pesanAksi = "Ditunda ke Nanti (Simulasi)"
                                        buang(buangKeRingkasan = false)
                                    },
                                    enabled = statusUnduh != StatusUnduh.MEMUAT,
                                    shape = RoundedCornerShape(10.dp),
                                    color = palette.surface,
                                    border = BorderStroke(1.dp, palette.border),
                                    shadowElevation = 0.dp,
                                    tonalElevation = 0.dp,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        text = n.aksiKedua,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.textPrimary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp),
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(palette.surfaceMuted),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(
                                                fraction = (sisaMs / DURASI_MS.toFloat())
                                                    .coerceIn(0.02f, 1f),
                                            )
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(palette.primary),
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (jeda) {
                                        "Dijeda · ${ringkasan.size + antrean.size} menunggu"
                                    } else {
                                        "${(sisaMs / 1000L).toInt()} dtk lagi"
                                    },
                                    fontSize = 10.sp,
                                    color = palette.textMuted,
                                )
                            }
                        }
                    }
                } else if (dnd && ringkasan.isNotEmpty()) {
                    // Banner diredam oleh mode Jangan Ganggu
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.55f)
                            .semantics {
                                contentDescription = "Banner diredam oleh mode Jangan Ganggu, dipindah ke ringkasan"
                            },
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                            Text(
                                text = "Diredam oleh Jangan Ganggu",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "«${ringkasan.first().judul}» dipindah ke ringkasan · ${ringkasan.size} notifikasi menunggu",
                                fontSize = 11.sp,
                                color = palette.textSecondary,
                            )
                        }
                    }
                } else {
                    // Placeholder: tidak ada banner aktif
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(84.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Tidak ada banner aktif",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Tekan «Kirim Uji (Simulasi)» di bawah untuk memunculkan banner",
                                    fontSize = 10.sp,
                                    color = palette.textMuted,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PilStatus(pesan: String, sukses: Boolean) {
    val palette = LocalAppPalette.current
    val warna = if (sukses) palette.success else palette.danger
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(warna.copy(alpha = 0.12f))
            .border(1.dp, warna, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Text(
            text = if (sukses) "✓ Sukses: $pesan" else "✕ Gagal: $pesan",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textPrimary,
        )
    }
}

@Composable
private fun BarisAntrean(indeks: Int, notif: BannerNotif, onKeluarkan: () -> Unit) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(palette.primaryContainer)
                .border(1.dp, palette.primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = notif.inisial,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = palette.onPrimaryContainer,
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Antrean $indeks · ${notif.nama}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = notif.judul,
                fontSize = 10.sp,
                color = palette.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(palette.surface)
                .border(1.dp, palette.border, CircleShape)
                .clickable { onKeluarkan() }
                .semantics {
                    role = Role.Button
                    contentDescription = "Keluarkan ${notif.judul} dari antrean"
                },
            contentAlignment = Alignment.Center,
        ) {
            Text("✕", fontSize = 10.sp, color = palette.textSecondary)
        }
    }
}

@Composable
private fun BarisRingkasan(notif: BannerNotif, onTampilkan: () -> Unit) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(palette.warning),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notif.judul,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "Diredam dari ${notif.nama}",
                fontSize = 10.sp,
                color = palette.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = "Tampilkan",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.primary,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onTampilkan() }
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .semantics {
                    role = Role.Button
                    contentDescription = "Tampilkan banner ${notif.judul}"
                },
        )
    }
}
