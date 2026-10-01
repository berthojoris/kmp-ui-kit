package com.example.uiapp.ui.photoszoom

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.FlatSecondaryButton
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.UiTopBar
import kotlin.random.Random
import kotlinx.coroutines.delay

private enum class Tingkat(val label: String) {
    SEMUA("Semua Foto"),
    TAHUN("Tahun"),
    BULAN("Bulan"),
    HARI("Hari"),
}

private enum class GayaKartu { BESAR, KELOMPOK, RAPAT }

private data class ItemGrid(
    val id: String,
    val judul: String,
    val sub: String,
    val seed: Int,
    val gaya: GayaKartu,
)

private val NamaBulan = listOf(
    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
    "Juli", "Agustus", "September", "Oktober", "November", "Desember",
)

private fun jumlahFotoHari(tahun: Int, bulanIdx: Int, hari: Int): Int =
    Random(tahun * 37 + bulanIdx * 101 + hari * 17).nextInt(24)

private fun jumlahFotoBulan(tahun: Int, bulanIdx: Int): Int =
    (1..30).sumOf { jumlahFotoHari(tahun, bulanIdx, it) }

private fun jumlahFotoTahun(tahun: Int): Int =
    (0..11).sumOf { jumlahFotoBulan(tahun, it) }

private fun buatItem(tingkat: Tingkat, tahun: Int, bulanIdx: Int, hari: Int): List<ItemGrid> =
    when (tingkat) {
        Tingkat.SEMUA -> (2026 downTo 2023).map { th ->
            ItemGrid(
                id = "thn-$th",
                judul = "$th",
                sub = "${jumlahFotoTahun(th)} foto · 12 bulan",
                seed = th,
                gaya = GayaKartu.BESAR,
            )
        }
        Tingkat.TAHUN -> NamaBulan.mapIndexed { i, nama ->
            ItemGrid(
                id = "bln-$tahun-$i",
                judul = nama,
                sub = "${jumlahFotoBulan(tahun, i)} foto",
                seed = tahun + i * 3,
                gaya = GayaKartu.KELOMPOK,
            )
        }
        Tingkat.BULAN -> (1..30).map { h ->
            val jumlah = jumlahFotoHari(tahun, bulanIdx, h)
            ItemGrid(
                id = "hr-$tahun-$bulanIdx-$h",
                judul = "$h",
                sub = if (jumlah == 0) "tanpa foto" else "$jumlah foto",
                seed = h * 5 + tahun,
                gaya = GayaKartu.RAPAT,
            )
        }
        Tingkat.HARI -> {
            val jumlah = jumlahFotoHari(tahun, bulanIdx, hari)
            (1..jumlah).map { f ->
                ItemGrid(
                    id = "ftr-$tahun-$bulanIdx-$hari-$f",
                    judul = "Foto $f",
                    sub = "",
                    seed = f * 11 + hari + tahun,
                    gaya = GayaKartu.RAPAT,
                )
            }
        }
    }

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun PhotosZoomLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var tingkat by remember { mutableStateOf(Tingkat.SEMUA) }
    var tahunAktif by remember { mutableIntStateOf(2026) }
    var bulanAktif by remember { mutableIntStateOf(2) }
    var hariAktif by remember { mutableIntStateOf(12) }
    var memuat by remember { mutableStateOf(true) }
    var pilihMode by remember { mutableStateOf(false) }
    val terpilih = remember { mutableStateListOf<String>() }
    val dihapus = remember { mutableStateListOf<String>() }
    var statusAksi by remember { mutableStateOf<String?>(null) }
    var statusGagal by remember { mutableStateOf(false) }
    var percobaanBagikan by remember { mutableIntStateOf(0) }
    var akumulasiZoom by remember { mutableStateOf(1f) }
    val skalaTransisi = remember { Animatable(0.9f) }

    val itemList by remember {
        derivedStateOf {
            buatItem(tingkat, tahunAktif, bulanAktif, hariAktif).filter { it.id !in dihapus }
        }
    }

    val judulTingkat = when (tingkat) {
        Tingkat.SEMUA -> "Semua Foto · ${itemList.size} tahun"
        Tingkat.TAHUN -> "$tahunAktif · ${jumlahFotoTahun(tahunAktif)} foto"
        Tingkat.BULAN -> "${NamaBulan[bulanAktif]} $tahunAktif · ${jumlahFotoBulan(tahunAktif, bulanAktif)} foto"
        Tingkat.HARI -> "$hariAktif ${NamaBulan[bulanAktif]} $tahunAktif · ${itemList.size} foto"
    }

    // Loading singkat + transisi skala saat berganti tingkat agregasi
    LaunchedEffect(tingkat, tahunAktif, bulanAktif, hariAktif) {
        memuat = true
        skalaTransisi.snapTo(0.9f)
        delay(320)
        memuat = false
        skalaTransisi.animateTo(
            targetValue = 1f,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        )
    }

    fun pindahTingkat(masuk: Boolean) {
        val baru = if (masuk) {
            when (tingkat) {
                Tingkat.SEMUA -> Tingkat.TAHUN
                Tingkat.TAHUN -> Tingkat.BULAN
                Tingkat.BULAN -> Tingkat.HARI
                Tingkat.HARI -> null
            }
        } else {
            when (tingkat) {
                Tingkat.SEMUA -> null
                Tingkat.TAHUN -> Tingkat.SEMUA
                Tingkat.BULAN -> Tingkat.TAHUN
                Tingkat.HARI -> Tingkat.BULAN
            }
        }
        if (baru != null) {
            tingkat = baru
            statusAksi = null
        }
    }

    fun masukDari(item: ItemGrid) {
        if (pilihMode) {
            if (terpilih.contains(item.id)) terpilih.remove(item.id) else terpilih.add(item.id)
            return
        }
        when (tingkat) {
            Tingkat.SEMUA -> {
                tahunAktif = item.judul.toIntOrNull() ?: tahunAktif
                tingkat = Tingkat.TAHUN
                statusAksi = null
            }
            Tingkat.TAHUN -> {
                bulanAktif = NamaBulan.indexOf(item.judul).coerceAtLeast(0)
                tingkat = Tingkat.BULAN
                statusAksi = null
            }
            Tingkat.BULAN -> {
                hariAktif = item.judul.toIntOrNull() ?: 1
                tingkat = Tingkat.HARI
                statusAksi = null
            }
            Tingkat.HARI -> {
                statusGagal = false
                statusAksi = "Pratinjau «${item.judul}» dibuka (Simulasi)"
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = palette.background,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            UiTopBar(
                title = "Photos Zoom Tahun ke Hari",
                subtitle = "Pinch antar tingkat agregasi",
                onBack = onBack,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                LabIntroCard(
                    title = "Zoom Tahun → Bulan → Hari",
                    body = "Cubit melebar untuk masuk satu tingkat dan cubit merapat untuk keluar. Tombol + dan − " +
                        "menyediakan jalur non-gesture, dan ketuk kartu untuk masuk satu tingkat. Seluruh foto adalah " +
                        "ilustrasi gradien lokal (Simulasi), bukan galeri perangkat.",
                )

                // Header "Kenangan" dengan kartu hero
                if (tingkat == Tingkat.SEMUA || tingkat == Tingkat.TAHUN) {
                    val gradasiKenangan = gradasiItem(seed = 777)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        onClick = { pindahTingkat(masuk = true) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                role = Role.Button
                                contentDescription =
                                    "Kenangan: Akhir Pekan di Bandung. Ketuk untuk masuk satu tingkat."
                            },
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(96.dp)
                                .background(gradasiKenangan),
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(palette.surface)
                                    .border(1.dp, palette.border, RoundedCornerShape(50))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "Kenangan",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(palette.surface)
                                    .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    text = "Akhir Pekan di Bandung · Simulasi",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }

                // Judul tingkat yang berganti + kontrol zoom non-gesture
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Crossfade(targetState = judulTingkat, modifier = Modifier.weight(1f)) { judul ->
                        Text(
                            text = judul,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    TombolIkon(
                        label = "−",
                        deskripsi = "Perkecil: keluar satu tingkat",
                        aktif = tingkat != Tingkat.SEMUA && !memuat,
                    ) { pindahTingkat(masuk = false) }
                    Spacer(modifier = Modifier.width(8.dp))
                    TombolIkon(
                        label = "+",
                        deskripsi = "Perbesar: masuk satu tingkat",
                        aktif = tingkat != Tingkat.HARI && !memuat,
                    ) { pindahTingkat(masuk = true) }
                }

                // Lompat tingkat + mode pilih
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Tingkat.entries.forEach { t ->
                        val aktifT = t == tingkat
                        Box(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (aktifT) palette.primary else palette.surfaceMuted)
                                .border(
                                    1.dp,
                                    if (aktifT) palette.primary else palette.border,
                                    RoundedCornerShape(50),
                                )
                                .clickable(enabled = !memuat) {
                                    tingkat = t
                                    statusAksi = null
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                                .semantics {
                                    role = Role.Button
                                    contentDescription = "Lompat ke tingkat ${t.label}"
                                },
                        ) {
                            Text(
                                text = t.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (aktifT) palette.onPrimary else palette.textPrimary,
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (pilihMode) palette.primaryContainer else palette.surface)
                            .border(
                                1.dp,
                                if (pilihMode) palette.primary else palette.border,
                                RoundedCornerShape(50),
                            )
                            .clickable {
                                pilihMode = !pilihMode
                                if (!pilihMode) terpilih.clear()
                                statusAksi = if (pilihMode) {
                                    "Mode pilih aktif — ketuk foto untuk menandai (Simulasi)"
                                } else {
                                    null
                                }
                                statusGagal = false
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .semantics {
                                role = Role.Button
                                contentDescription = if (pilihMode) {
                                    "Keluar dari mode pilih"
                                } else {
                                    "Masuk mode pilih"
                                }
                            },
                    ) {
                        Text(
                            text = if (pilihMode) "Selesai Memilih" else "Pilih",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pilihMode) palette.onPrimaryContainer else palette.textPrimary,
                        )
                    }
                }

                // Status aksi massal
                if (statusAksi != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (statusGagal) {
                                    palette.danger.copy(alpha = 0.12f)
                                } else {
                                    palette.success.copy(alpha = 0.12f)
                                },
                            )
                            .border(
                                1.dp,
                                if (statusGagal) palette.danger else palette.success,
                                RoundedCornerShape(10.dp),
                            )
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (statusGagal) {
                                    "✕ Gagal: $statusAksi"
                                } else {
                                    "✓ Sukses: $statusAksi"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary,
                                modifier = Modifier.weight(1f),
                            )
                            if (statusGagal) {
                                Text(
                                    text = "Coba Lagi",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.primary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            percobaanBagikan++
                                            if (percobaanBagikan <= 1) {
                                                statusGagal = true
                                                statusAksi =
                                                    "berbagi ${terpilih.size} item gagal (Simulasi) — sinyal putus"
                                            } else {
                                                statusGagal = false
                                                statusAksi =
                                                    "${terpilih.size} item dibagikan (Simulasi)"
                                                terpilih.clear()
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .semantics {
                                            role = Role.Button
                                            contentDescription = "Coba bagikan lagi"
                                        },
                                )
                            }
                        }
                    }
                }
            }

            // Area grid dengan pinch zoom
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .pointerInput(tingkat) {
                        detectTransformGestures { _, _, zoom, _ ->
                            akumulasiZoom *= zoom
                            when {
                                akumulasiZoom > 1.3f -> {
                                    akumulasiZoom = 1f
                                    pindahTingkat(masuk = true)
                                }
                                akumulasiZoom < 0.77f -> {
                                    akumulasiZoom = 1f
                                    pindahTingkat(masuk = false)
                                }
                            }
                        }
                    },
            ) {
                when {
                    itemList.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Tidak ada foto di tingkat ini",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Semua item dihapus dari tampilan (Simulasi).",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            FlatSecondaryButton(
                                text = "Pulihkan Item (Simulasi)",
                                onClick = {
                                    dihapus.clear()
                                    statusGagal = false
                                    statusAksi = "Item dipulihkan ke tampilan (Simulasi)"
                                },
                            )
                        }
                    }
                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(
                                when (tingkat) {
                                    Tingkat.SEMUA -> 2
                                    Tingkat.TAHUN -> 2
                                    Tingkat.BULAN -> 3
                                    Tingkat.HARI -> 4
                                },
                            ),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(top = 4.dp, bottom = 20.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = skalaTransisi.value
                                    scaleY = skalaTransisi.value
                                },
                        ) {
                            items(itemList, key = { it.id }) { item ->
                                KartuFoto(
                                    item = item,
                                    dipilih = terpilih.contains(item.id),
                                    pilihMode = pilihMode,
                                    onClick = { masukDari(item) },
                                )
                            }
                        }
                    }
                }

                // Overlay loading antar tingkat
                if (memuat) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(palette.background.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        val pulse = rememberInfiniteTransition(label = "pulse")
                        val alphaPulse by pulse.animateFloat(
                            initialValue = 0.35f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600),
                                repeatMode = RepeatMode.Reverse,
                            ),
                            label = "alphaPulse",
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(palette.primary)
                                    .graphicsLayer { alpha = alphaPulse },
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Menyiapkan tingkat ${tingkat.label}…",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary,
                            )
                            Text(
                                text = "Simulasi — tanpa pustaka foto sistem",
                                fontSize = 10.sp,
                                color = palette.textMuted,
                            )
                        }
                    }
                }
            }

            // Bar aksi massal saat mode pilih
            if (pilihMode) {
                Column(modifier = Modifier.fillMaxWidth().background(palette.surface)) {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${terpilih.size} item dipilih",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = "Semua aksi adalah Simulasi",
                                fontSize = 10.sp,
                                color = palette.textMuted,
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FlatPrimaryButton(
                                text = "Hapus (${terpilih.size})",
                                onClick = {
                                    val jumlah = terpilih.size
                                    dihapus.addAll(terpilih)
                                    terpilih.clear()
                                    pilihMode = false
                                    statusGagal = false
                                    statusAksi = "$jumlah item dihapus dari tampilan (Simulasi)"
                                },
                                enabled = terpilih.isNotEmpty(),
                                modifier = Modifier.weight(1f),
                            )
                            FlatSecondaryButton(
                                text = "Bagikan (${terpilih.size})",
                                onClick = {
                                    percobaanBagikan++
                                    if (percobaanBagikan <= 1) {
                                        statusGagal = true
                                        statusAksi = "berbagi ${terpilih.size} item gagal (Simulasi) — sinyal putus"
                                    } else {
                                        statusGagal = false
                                        statusAksi = "${terpilih.size} item dibagikan (Simulasi)"
                                        terpilih.clear()
                                    }
                                },
                                enabled = terpilih.isNotEmpty(),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            } else {
                // Catatan pola di dasar layar
                Column(modifier = Modifier.fillMaxWidth().background(palette.surface)) {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                    Text(
                        text = "Grid dinamis: tahun kartu besar · bulan kelompok · hari grid rapat · cubit merapat untuk keluar",
                        fontSize = 10.sp,
                        color = palette.textMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun gradasiItem(seed: Int): Brush {
    val palette = LocalAppPalette.current
    val warna = remember(seed, palette) {
        val basis = listOf(
            palette.primary,
            palette.info,
            palette.warning,
            palette.success,
            palette.danger,
            palette.onPrimaryContainer,
        )
        val a = ((seed % basis.size) + basis.size) % basis.size
        val b = (((seed / 3) + 2) % basis.size + basis.size) % basis.size
        listOf(
            basis[a].copy(alpha = 0.85f),
            basis[b].copy(alpha = 0.45f),
        )
    }
    return remember(warna) { Brush.linearGradient(warna) }
}

@Composable
private fun TombolIkon(
    label: String,
    deskripsi: String,
    aktif: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (aktif) palette.surface else palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .clickable(enabled = aktif) { onClick() }
            .semantics {
                role = Role.Button
                contentDescription = deskripsi
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (aktif) palette.textPrimary else palette.textMuted,
        )
    }
}

@Composable
private fun KartuFoto(
    item: ItemGrid,
    dipilih: Boolean,
    pilihMode: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val gradasi = gradasiItem(item.seed)
    val isiTinggi = when (item.gaya) {
        GayaKartu.BESAR -> 110.dp
        GayaKartu.KELOMPOK -> 68.dp
        GayaKartu.RAPAT -> 0.dp
    }

    if (item.gaya == GayaKartu.RAPAT) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(gradasi)
                .border(
                    BorderStroke(if (dipilih) 2.dp else 1.dp, if (dipilih) palette.primary else palette.border),
                    RoundedCornerShape(12.dp),
                )
                .clickable { onClick() }
                .semantics {
                    role = Role.Button
                    contentDescription = "${item.judul} ${item.sub}".trim() + ", " +
                        (if (dipilih) "Dipilih" else "Belum dipilih")
                },
        ) {
            if (item.sub.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(palette.surface)
                        .border(1.dp, palette.border, RoundedCornerShape(50))
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "${item.judul} · ${item.sub}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (pilihMode) {
                LencanaPilih(dipilih = dipilih, modifier = Modifier.align(Alignment.TopEnd))
            }
        }
    } else {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(if (dipilih) 2.dp else 1.dp, if (dipilih) palette.primary else palette.border),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
            onClick = { onClick() },
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    role = Role.Button
                    contentDescription = "${item.judul}, ${item.sub}, " +
                        (if (dipilih) "Dipilih" else "Belum dipilih") + ". Ketuk untuk masuk satu tingkat."
                },
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(isiTinggi)
                        .background(gradasi),
                ) {
                    if (pilihMode) {
                        LencanaPilih(dipilih = dipilih, modifier = Modifier.align(Alignment.TopEnd))
                    }
                }
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = item.judul,
                        fontSize = if (item.gaya == GayaKartu.BESAR) 16.sp else 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = item.sub,
                        fontSize = 10.sp,
                        color = palette.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun LencanaPilih(dipilih: Boolean, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Box(
        modifier = modifier
            .padding(6.dp)
            .size(22.dp)
            .clip(CircleShape)
            .background(if (dipilih) palette.primary else palette.surface)
            .border(1.dp, if (dipilih) palette.primary else palette.border, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (dipilih) "✓" else "",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = palette.onPrimary,
        )
    }
}
