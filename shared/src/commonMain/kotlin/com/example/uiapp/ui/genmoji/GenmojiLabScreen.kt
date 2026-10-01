package com.example.uiapp.ui.genmoji

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.FlatSecondaryButton
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private enum class GayaGenmoji(val label: String) {
    SKETSA("Sketsa"),
    ILUSTRASI("Ilustrasi"),
    ANIMASI("Animasi"),
}

private enum class GenState { KOSONG, MEMBUAT, SUKSES, GAGAL }

private data class GenmojiItem(
    val id: String,
    val seed: Long,
    val gaya: GayaGenmoji,
    val prompt: String,
)

private data class GenmojiModel(
    val bentuk: Int,
    val aksesori: Int,
    val colorA: Color,
    val colorB: Color,
    val eyeShift: Float,
    val mouthOpen: Float,
)

private data class MockMessage(
    val id: String,
    val pengirim: String,
    val teks: String?,
    val genmojiId: String?,
    val milikSaya: Boolean,
)

private val promptContoh = listOf("Kucing astronot", "Naga mungil", "Bunga mekar", "Robot ramah")

private val kataTerlarang = listOf("dilarang", "kekerasan", "senjata", "politik")

private val pesanAwal = listOf(
    MockMessage("m1", "Dina", "Hai! Sudah coba bikin genmoji untuk grup kita?", null, false),
    MockMessage("m2", "Rian", "Aku mau yang bertema kucing astronot dong.", null, false),
)

private fun seedDari(prompt: String, gaya: GayaGenmoji): Long {
    return prompt.lowercase().hashCode().toLong() * 31L + gaya.ordinal.toLong() + 7L
}

private fun hasilkanGenmoji(prompt: String, gaya: GayaGenmoji): List<GenmojiItem> {
    val basis = seedDari(prompt, gaya)
    return (0 until 4).map { i ->
        GenmojiItem(
            id = "gen-$basis-$i",
            seed = basis + i * 101L,
            gaya = gaya,
            prompt = prompt,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun GenmojiLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()

    var prompt by remember { mutableStateOf("") }
    var gaya by remember { mutableStateOf(GayaGenmoji.ILUSTRASI) }
    var state by remember { mutableStateOf(GenState.KOSONG) }
    var hasil by remember { mutableStateOf<List<GenmojiItem>>(emptyList()) }
    var terpilih by remember { mutableStateOf<GenmojiItem?>(null) }
    val stiker = remember { mutableStateListOf<GenmojiItem>() }
    val pesanList = remember { mutableStateListOf<MockMessage>().apply { addAll(pesanAwal) } }
    var draftPesan by remember { mutableStateOf("") }
    var lampiranGenmoji by remember { mutableStateOf<GenmojiItem?>(null) }
    var banner by remember { mutableStateOf<String?>(null) }

    val rows = remember(hasil) { hasil.chunked(2) }

    LaunchedEffect(banner) {
        if (banner != null) {
            delay(2200)
            banner = null
        }
    }

    fun buatGenmoji() {
        val teks = prompt.trim()
        if (teks.isEmpty()) return
        scope.launch {
            state = GenState.MEMBUAT
            terpilih = null
            hasil = emptyList()
            delay(1200)
            val melanggar = kataTerlarang.any { teks.lowercase().contains(it) }
            if (melanggar) {
                state = GenState.GAGAL
            } else {
                hasil = hasilkanGenmoji(teks, gaya)
                state = GenState.SUKSES
                banner = "4 genmoji dibuat dari prompt (Simulasi)"
            }
        }
    }

    fun simpanStiker(item: GenmojiItem) {
        if (stiker.none { it.id == item.id }) {
            stiker.add(item)
            banner = "Genmoji disimpan sebagai stiker (Simulasi)"
        } else {
            banner = "Genmoji sudah ada di koleksi stiker"
        }
    }

    fun kirimPesan() {
        val teks = draftPesan.trim()
        if (teks.isEmpty() && lampiranGenmoji == null) return
        val lampiran = lampiranGenmoji
        pesanList.add(
            MockMessage(
                id = "kirim-${pesanList.size + 1}",
                pengirim = "Saya",
                teks = if (teks.isEmpty()) null else teks,
                genmojiId = lampiran?.id,
                milikSaya = true,
            ),
        )
        draftPesan = ""
        lampiranGenmoji = null
        scope.launch {
            delay(900)
            pesanList.add(
                MockMessage(
                    id = "balas-${pesanList.size + 1}",
                    pengirim = "Dina",
                    teks = if (lampiran != null) {
                        "Wah, genmoji-nya lucu! (balasan otomatis — Simulasi)"
                    } else {
                        "Siap, nanti aku cek ya. (balasan otomatis — Simulasi)"
                    },
                    genmojiId = null,
                    milikSaya = false,
                ),
            )
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Genmoji & Image Playground",
                subtitle = "Emoji buatan perintah — Simulasi",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LabIntroCard(
                title = "Genmoji & Image Playground — Simulasi",
                body = "Tulis perintah berbahasa Indonesia, pilih gaya, lalu hasilkan genmoji prosedural dari Canvas. Tidak ada generator gambar nyata: bentuk dan gradien disusun deterministik dari teks prompt.",
            )

            // ===== Kartu pembuatan =====
            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LabSectionTitle("Buat Genmoji", "Prompt → gaya → hasil prosedural")
                    SimulasiPill("Simulasi")
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(modifier = Modifier.padding(12.dp)) {
                        if (prompt.isEmpty()) {
                            Text(
                                text = "Contoh: kucing astronot di antariksa…",
                                fontSize = 13.sp,
                                color = palette.textMuted,
                            )
                        }
                        BasicTextField(
                            value = prompt,
                            onValueChange = { prompt = it },
                            singleLine = false,
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                color = palette.textPrimary,
                                lineHeight = 19.sp,
                            ),
                            cursorBrush = SolidColor(palette.primary),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    promptContoh.forEach { contoh ->
                        Surface(
                            onClick = { prompt = contoh },
                            shape = RoundedCornerShape(999.dp),
                            color = palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.border),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                        ) {
                            Text(
                                text = contoh,
                                fontSize = 11.sp,
                                color = palette.textSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            )
                        }
                    }
                }

                LabSectionTitle("Gaya", "Pilih satu gaya render")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GayaGenmoji.entries.forEach { opsi ->
                        val aktif = gaya == opsi
                        Surface(
                            onClick = { gaya = opsi },
                            shape = RoundedCornerShape(999.dp),
                            color = if (aktif) palette.primaryContainer else palette.surface,
                            border = BorderStroke(1.dp, if (aktif) palette.primary else palette.border),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                        ) {
                            Text(
                                text = opsi.label,
                                fontSize = 12.sp,
                                fontWeight = if (aktif) FontWeight.Bold else FontWeight.Normal,
                                color = if (aktif) palette.onPrimaryContainer else palette.textSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            )
                        }
                    }
                }

                FlatPrimaryButton(
                    text = if (state == GenState.MEMBUAT) "Membuat…" else "Buat Genmoji",
                    onClick = { buatGenmoji() },
                    enabled = prompt.trim().isNotEmpty() && state != GenState.MEMBUAT,
                )
                Text(
                    text = "Kata yang dibatasi (mis. \"dilarang\", \"kekerasan\") menghasilkan state gagal untuk demo.",
                    fontSize = 10.sp,
                    color = palette.textMuted,
                )
            }

            // ===== Hasil =====
            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LabSectionTitle("Hasil Genmoji", "Bentuk + gradien dari seed prompt")
                    SimulasiPill("Simulasi")
                }

                when (state) {
                    GenState.KOSONG -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.border),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                KosongIlustrasi()
                                Text("Belum ada genmoji", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                Text(
                                    "Tulis prompt lalu tekan Buat Genmoji (state: kosong).",
                                    fontSize = 11.sp,
                                    color = palette.textMuted,
                                )
                            }
                        }
                    }
                    GenState.MEMBUAT -> {
                        Text("Membuat genmoji dari prompt…", fontSize = 12.sp, color = palette.textSecondary)
                        repeat(2) { baris ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                repeat(2) { kolom ->
                                    SkeletonSel(baris * 2 + kolom, Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    GenState.GAGAL -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = palette.danger.copy(alpha = 0.10f),
                            border = BorderStroke(1.dp, palette.danger),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                GagalIlustrasi()
                                Text(
                                    "Hasil tidak sesuai pedoman",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.danger,
                                )
                                Text(
                                    "Status: gagal — prompt mengandung kata yang dibatasi. Ubah perintahmu lalu coba lagi.",
                                    fontSize = 11.sp,
                                    color = palette.textSecondary,
                                )
                                FlatSecondaryButton(text = "Coba Lagi", onClick = { state = GenState.KOSONG })
                            }
                        }
                    }
                    GenState.SUKSES -> {
                        rows.forEach { rowItems ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                rowItems.forEach { item ->
                                    val aktif = terpilih?.id == item.id
                                    Box(modifier = Modifier.weight(1f)) {
                                        Surface(
                                            onClick = { terpilih = item },
                                            shape = RoundedCornerShape(16.dp),
                                            color = palette.surface,
                                            border = BorderStroke(
                                                1.dp,
                                                if (aktif) palette.primary else palette.border,
                                            ),
                                            shadowElevation = 0.dp,
                                            tonalElevation = 0.dp,
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(10.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                            ) {
                                                GenmojiCanvas(
                                                    item = item,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(96.dp)
                                                        .semantics {
                                                            role = Role.Button
                                                            contentDescription =
                                                                "Genmoji ${item.prompt} gaya ${item.gaya.label}"
                                                        },
                                                )
                                                Text(
                                                    text = item.gaya.label,
                                                    fontSize = 10.sp,
                                                    color = if (aktif) palette.primary else palette.textMuted,
                                                    fontWeight = if (aktif) FontWeight.Bold else FontWeight.Normal,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        val sel = terpilih
                        if (sel != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = palette.surfaceMuted,
                                border = BorderStroke(1.dp, palette.border),
                                shadowElevation = 0.dp,
                                tonalElevation = 0.dp,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        GenmojiCanvas(item = sel, modifier = Modifier.size(64.dp))
                                        Column {
                                            Text(sel.prompt, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                            Text("Gaya ${sel.gaya.label} · seed ${sel.seed}", fontSize = 10.sp, color = palette.textMuted)
                                        }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FlatSecondaryButton(
                                            text = "Simpan Stiker",
                                            onClick = { simpanStiker(sel) },
                                            modifier = Modifier.weight(1f),
                                        )
                                        FlatSecondaryButton(
                                            text = "Pakai di Pesan",
                                            onClick = {
                                                lampiranGenmoji = sel
                                                banner = "Genmoji dilampirkan ke pesan (Simulasi)"
                                            },
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                    FlatPrimaryButton(
                                        text = "Bagikan",
                                        onClick = { banner = "Dibagikan ke 3 kontak (Simulasi)" },
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ===== Stiker tersimpan =====
            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LabSectionTitle("Stiker Tersimpan", "Genmoji yang disimpan untuk dipakai ulang")
                    SimulasiPill("Simulasi")
                }
                if (stiker.isEmpty()) {
                    Text(
                        text = "Koleksi stiker kosong — simpan genmoji dari hasil di atas (state: kosong).",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                } else {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        stiker.forEach { item ->
                            Box(modifier = Modifier.size(64.dp)) {
                                GenmojiCanvas(
                                    item = item,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { lampiranGenmoji = item }
                                        .semantics {
                                            role = Role.Button
                                            contentDescription = "Stiker ${item.prompt}"
                                        },
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(palette.danger)
                                        .clickable {
                                            val idx = stiker.indexOfFirst { it.id == item.id }
                                            if (idx >= 0) stiker.removeAt(idx)
                                            banner = "Stiker dihapus"
                                        }
                                        .semantics {
                                            role = Role.Button
                                            contentDescription = "Hapus stiker ${item.prompt}"
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text("✕", fontSize = 10.sp, color = palette.onPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // ===== Pesan mock =====
            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LabSectionTitle("Pesan (Mock)", "Percakapan contoh untuk memakai genmoji")
                    SimulasiPill("Simulasi")
                }

                pesanList.forEach { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.milikSaya) Arrangement.End else Arrangement.Start,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (msg.milikSaya) palette.primaryContainer else palette.surfaceMuted,
                            border = BorderStroke(1.dp, if (msg.milikSaya) palette.primary else palette.border),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                            modifier = Modifier.fillMaxWidth(0.8f),
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = msg.pengirim,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textMuted,
                                )
                                if (msg.teks != null) {
                                    Text(msg.teks, fontSize = 12.sp, lineHeight = 18.sp, color = palette.textPrimary)
                                }
                                val item = msg.genmojiId?.let { id -> stiker.find { it.id == id } ?: hasil.find { it.id == id } }
                                if (item != null) {
                                    GenmojiCanvas(item = item, modifier = Modifier.size(64.dp))
                                }
                            }
                        }
                    }
                }

                lampiranGenmoji?.let { lampiran ->
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = palette.primaryContainer,
                        border = BorderStroke(1.dp, palette.primary),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = "Lampiran: ${lampiran.prompt}",
                                fontSize = 11.sp,
                                color = palette.onPrimaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = "✕",
                                fontSize = 11.sp,
                                color = palette.onPrimaryContainer,
                                modifier = Modifier
                                    .clickable { lampiranGenmoji = null }
                                    .semantics {
                                        role = Role.Button
                                        contentDescription = "Hapus lampiran genmoji"
                                    },
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            if (draftPesan.isEmpty()) {
                                Text("Tulis pesan…", fontSize = 12.sp, color = palette.textMuted)
                            }
                            BasicTextField(
                                value = draftPesan,
                                onValueChange = { draftPesan = it },
                                singleLine = true,
                                textStyle = TextStyle(fontSize = 12.sp, color = palette.textPrimary),
                                cursorBrush = SolidColor(palette.primary),
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                    Surface(
                        onClick = { kirimPesan() },
                        shape = RoundedCornerShape(12.dp),
                        color = palette.primary,
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        enabled = draftPesan.trim().isNotEmpty() || lampiranGenmoji != null,
                    ) {
                        Text(
                            text = "Kirim",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.onPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        )
                    }
                }
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
                    Text("Manfaat Pola Genmoji & Image Playground:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                    Text("• Hasil prosedural Canvas menggantikan generator gambar: ringan untuk perangkat kelas menengah", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Alur simpan stiker → pakai di pesan → bagikan meniru kebiasaan emoji/stiker sosial", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• State memuat, kosong, dan gagal \"tidak sesuai\" + coba lagi membuat pengalaman lengkap", fontSize = 12.sp, color = palette.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
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
private fun GenmojiCanvas(item: GenmojiItem, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    val model = remember(item.id) {
        val rand = Random(item.seed)
        GenmojiModel(
            bentuk = rand.nextInt(3),
            aksesori = rand.nextInt(6),
            colorA = lerp(palette.primary, palette.info, rand.nextFloat()),
            colorB = lerp(palette.warning, palette.success, rand.nextFloat()),
            eyeShift = rand.nextFloat(),
            mouthOpen = rand.nextFloat(),
        )
    }
    val brush = remember(item.id) { Brush.linearGradient(listOf(model.colorA, model.colorB)) }

    Canvas(modifier = modifier.clip(RoundedCornerShape(16.dp))) {
        val w = size.width
        val h = size.height
        val r = minOf(w, h) * 0.36f
        val pusat = Offset(w / 2f, h / 2f)
        val garis = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val wajah = palette.textPrimary
        val putih = palette.surface

        when (item.gaya) {
            GayaGenmoji.SKETSA -> {
                when (model.bentuk) {
                    0 -> drawCircle(wajah, radius = r, center = pusat, style = garis)
                    1 -> drawRoundRect(
                        color = wajah,
                        topLeft = Offset(pusat.x - r, pusat.y - r),
                        size = Size(r * 2f, r * 2f),
                        cornerRadius = CornerRadius(r * 0.4f, r * 0.4f),
                        style = garis,
                    )
                    else -> {
                        val segitiga = Path().apply {
                            moveTo(pusat.x, pusat.y - r)
                            lineTo(pusat.x + r, pusat.y + r * 0.8f)
                            lineTo(pusat.x - r, pusat.y + r * 0.8f)
                            close()
                        }
                        drawPath(segitiga, color = wajah, style = garis)
                    }
                }
                drawCircle(wajah, radius = 3.5f, center = Offset(pusat.x - r * 0.35f, pusat.y - r * 0.15f))
                drawCircle(wajah, radius = 3.5f, center = Offset(pusat.x + r * 0.35f, pusat.y - r * 0.15f))
                drawArc(
                    color = wajah,
                    startAngle = 25f,
                    sweepAngle = 130f,
                    useCenter = false,
                    topLeft = Offset(pusat.x - r * 0.35f, pusat.y - r * 0.2f),
                    size = Size(r * 0.7f, r * 0.7f),
                    style = garis,
                )
            }
            GayaGenmoji.ILUSTRASI -> {
                when (model.bentuk) {
                    0 -> drawCircle(brush = brush, radius = r, center = pusat)
                    1 -> drawRoundRect(
                        brush = brush,
                        topLeft = Offset(pusat.x - r, pusat.y - r),
                        size = Size(r * 2f, r * 2f),
                        cornerRadius = CornerRadius(r * 0.4f, r * 0.4f),
                    )
                    else -> {
                        val segitiga = Path().apply {
                            moveTo(pusat.x, pusat.y - r)
                            lineTo(pusat.x + r, pusat.y + r * 0.8f)
                            lineTo(pusat.x - r, pusat.y + r * 0.8f)
                            close()
                        }
                        drawPath(segitiga, brush = brush)
                    }
                }
                drawCircle(putih, radius = r * 0.18f, center = Offset(pusat.x - r * 0.35f, pusat.y - r * 0.15f))
                drawCircle(putih, radius = r * 0.18f, center = Offset(pusat.x + r * 0.35f, pusat.y - r * 0.15f))
                drawCircle(wajah, radius = r * 0.08f, center = Offset(pusat.x - r * 0.32f + model.eyeShift * 4f, pusat.y - r * 0.15f))
                drawCircle(wajah, radius = r * 0.08f, center = Offset(pusat.x + r * 0.32f + model.eyeShift * 4f, pusat.y - r * 0.15f))
                drawArc(
                    color = wajah,
                    startAngle = 20f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(pusat.x - r * 0.35f, pusat.y - r * 0.25f + model.mouthOpen * 6f),
                    size = Size(r * 0.7f, r * 0.7f),
                    style = garis,
                )
            }
            GayaGenmoji.ANIMASI -> {
                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(pusat.x - r, pusat.y - r),
                    size = Size(r * 2f, r * 2f),
                    cornerRadius = CornerRadius(r * 0.5f, r * 0.5f),
                )
                drawCircle(putih, radius = r * 0.28f, center = Offset(pusat.x - r * 0.38f, pusat.y - r * 0.18f))
                drawCircle(putih, radius = r * 0.28f, center = Offset(pusat.x + r * 0.38f, pusat.y - r * 0.18f))
                drawCircle(wajah, radius = r * 0.12f, center = Offset(pusat.x - r * 0.34f + model.eyeShift * 5f, pusat.y - r * 0.14f))
                drawCircle(wajah, radius = r * 0.12f, center = Offset(pusat.x + r * 0.34f + model.eyeShift * 5f, pusat.y - r * 0.14f))
                val mulut = Path().apply {
                    moveTo(pusat.x - r * 0.35f, pusat.y + r * 0.25f)
                    quadraticBezierTo(pusat.x, pusat.y + r * (0.5f + model.mouthOpen * 0.3f), pusat.x + r * 0.35f, pusat.y + r * 0.25f)
                }
                drawPath(mulut, color = wajah, style = garis)
                drawArc(
                    color = putih.copy(alpha = 0.7f),
                    startAngle = 200f,
                    sweepAngle = 60f,
                    useCenter = false,
                    topLeft = Offset(pusat.x - r * 1.25f, pusat.y - r * 1.25f),
                    size = Size(r * 2.5f, r * 2.5f),
                    style = Stroke(width = 3f, cap = StrokeCap.Round),
                )
            }
        }

        // Aksesori deterministik dari prompt
        when (model.aksesori) {
            0 -> {
                drawLine(wajah, Offset(pusat.x, pusat.y - r), Offset(pusat.x, pusat.y - r * 1.35f), 3f, StrokeCap.Round)
                drawCircle(wajah, radius = r * 0.1f, center = Offset(pusat.x, pusat.y - r * 1.45f))
            }
            1 -> {
                val bintang = Path().apply {
                    moveTo(pusat.x + r * 1.1f, pusat.y - r * 1.1f)
                    lineTo(pusat.x + r * 1.25f, pusat.y - r * 0.75f)
                    lineTo(pusat.x + r * 1.6f, pusat.y - r * 0.75f)
                    lineTo(pusat.x + r * 1.33f, pusat.y - r * 0.52f)
                    lineTo(pusat.x + r * 1.44f, pusat.y - r * 0.2f)
                    lineTo(pusat.x + r * 1.1f, pusat.y - r * 0.42f)
                    close()
                }
                drawPath(bintang, color = putih)
            }
            2 -> {
                drawCircle(wajah, radius = r * 0.22f, center = Offset(pusat.x - r * 0.35f, pusat.y - r * 0.15f), style = garis)
                drawCircle(wajah, radius = r * 0.22f, center = Offset(pusat.x + r * 0.35f, pusat.y - r * 0.15f), style = garis)
                drawLine(wajah, Offset(pusat.x - r * 0.13f, pusat.y - r * 0.15f), Offset(pusat.x + r * 0.13f, pusat.y - r * 0.15f), 3f, StrokeCap.Round)
            }
            3 -> {
                val topi = Path().apply {
                    moveTo(pusat.x - r * 0.55f, pusat.y - r * 0.9f)
                    lineTo(pusat.x, pusat.y - r * 1.5f)
                    lineTo(pusat.x + r * 0.55f, pusat.y - r * 0.9f)
                    close()
                }
                drawPath(topi, color = putih)
            }
            4 -> {
                drawCircle(putih.copy(alpha = 0.6f), radius = r * 0.12f, center = Offset(pusat.x - r * 0.55f, pusat.y + r * 0.15f))
                drawCircle(putih.copy(alpha = 0.6f), radius = r * 0.12f, center = Offset(pusat.x + r * 0.55f, pusat.y + r * 0.15f))
            }
            else -> Unit
        }
    }
}

@Composable
private fun SkeletonSel(idx: Int, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    val transition = rememberInfiniteTransition(label = "skeleton$idx")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "skeletonAlpha$idx",
    )
    Box(
        modifier = modifier
            .height(96.dp)
            .alpha(alpha)
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surfaceMuted, RoundedCornerShape(16.dp))
            .border(1.dp, palette.border, RoundedCornerShape(16.dp)),
    )
}

@Composable
private fun KosongIlustrasi() {
    val palette = LocalAppPalette.current
    Canvas(modifier = Modifier.size(64.dp)) {
        val garis = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 8f)))
        drawRoundRect(
            color = palette.textMuted,
            topLeft = Offset(size.width * 0.1f, size.height * 0.18f),
            size = Size(size.width * 0.8f, size.height * 0.64f),
            cornerRadius = CornerRadius(10f, 10f),
            style = garis,
        )
        drawLine(palette.textMuted, Offset(size.width * 0.3f, size.height * 0.08f), Offset(size.width * 0.3f, size.height * 0.14f), 2.5f, StrokeCap.Round)
        drawLine(palette.textMuted, Offset(size.width * 0.7f, size.height * 0.08f), Offset(size.width * 0.7f, size.height * 0.14f), 2.5f, StrokeCap.Round)
    }
}

@Composable
private fun GagalIlustrasi() {
    val palette = LocalAppPalette.current
    Canvas(modifier = Modifier.size(52.dp)) {
        val garis = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(palette.danger, radius = size.minDimension * 0.42f, center = center, style = garis)
        drawLine(palette.danger, Offset(size.width * 0.34f, size.height * 0.34f), Offset(size.width * 0.66f, size.height * 0.66f), 3f, StrokeCap.Round)
        drawLine(palette.danger, Offset(size.width * 0.66f, size.height * 0.34f), Offset(size.width * 0.34f, size.height * 0.66f), 3f, StrokeCap.Round)
    }
}
