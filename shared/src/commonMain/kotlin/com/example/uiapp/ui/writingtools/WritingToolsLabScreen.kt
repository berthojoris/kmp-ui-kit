package com.example.uiapp.ui.writingtools

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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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

private enum class WriteAction(val label: String) {
    REWRITE("Rewrite"),
    PROOFREAD("Proofread"),
    SUMMARIZE("Summarize"),
    MAKE_LIST("Make List"),
    MAKE_TABLE("Make Table"),
    TONE("Change Tone"),
}

private enum class ToneOption(val label: String) {
    SANTAI("Santai"),
    FORMAL("Formal"),
    RAMAH("Ramah"),
    RINGKAS("Ringkas"),
}

private enum class ResultKind { TEKS, DAFTAR, TABEL }

private enum class ProsesState { SIAP, MEMPROSES, MENGETIK, SELESAI }

private data class TransformResult(
    val kind: ResultKind,
    val judul: String,
    val lines: List<String>,
    val table: List<List<String>> = emptyList(),
)

private data class ProofSuggestion(
    val id: String,
    val kata: String,
    val ganti: String,
    val alasan: String,
)

private val paragrafAsli = listOf(
    "Tim yang budiman, berikut merupakam ringkasan rapat mingguan proyek Nusantara. Terima kasih sudah menyempatkan hadir tepat waktu dan memberi masukan.",
    "Progres desain sudah mencapai delapan puluh persen, sedangkan tim pengembangan menyelesaikan integrasi pembayaraan. Ada dua kendala kecil yang perlu kita diskusikam besok.",
    "Mohon saran dan masukan sebelum hari Jumat agar kita bisa mengunci ruang lingkup. Dokumen lampirann sudah saya kirim lewat surel grup.",
)

private val saranAwal = listOf(
    ProofSuggestion("s1", "merupakam", "merupakan", "Ketikan salah pada akhiran kata"),
    ProofSuggestion("s2", "pembayaraan", "pembayaran", "Ejaan tidak baku: huruf ganda"),
    ProofSuggestion("s3", "diskusikam", "diskusikan", "Ketikan salah pada akhiran kata"),
    ProofSuggestion("s4", "lampirann", "lampiran", "Ketikan salah: huruf n ganda"),
)

private fun hasilTransformasi(action: WriteAction, tone: ToneOption?, idx: Int): TransformResult {
    if (action == WriteAction.TONE && tone != null) {
        val teks = when (tone) {
            ToneOption.SANTAI -> "Halo tim! Ini ringkasan singkat rapat mingguan proyek Nusantara. Makasih udah hadir dan ngasih masukan, mantap!"
            ToneOption.FORMAL -> "Dengan hormat, berikut disampaikan ringkasan rapat mingguan Proyek Nusantara. Atas perhatian dan masukan Bapak/Ibu, kami ucapkan terima kasih."
            ToneOption.RAMAH -> "Hai semuanya, terima kasih ya sudah sempat hadir di rapat mingguan proyek Nusantara. Ini ringkasannya, semoga membantu!"
            ToneOption.RINGKAS -> "Ringkasan rapat mingguan Proyek Nusantara. Terima kasih atas kehadiran dan masukannya."
        }
        return TransformResult(ResultKind.TEKS, "Change Tone · ${tone.label}", listOf(teks))
    }
    return when (action) {
        WriteAction.REWRITE -> TransformResult(
            ResultKind.TEKS,
            "Rewrite",
            listOf(
                when (idx) {
                    0 -> "Kepada tim, terlampir ringkasan rapat mingguan proyek Nusantara. Apresiasi saya untuk kehadiran tepat waktu serta masukan konstruktif dari semua pihak."
                    1 -> "Desain telah tuntas delapan puluh persen, sementara tim pengembangan mematangkan integrasi pembayaran. Dua kendala kecil akan kita bahas dalam sesi besok."
                    else -> "Masukan diperlukan sebelum Jumat supaya ruang lingkup dapat dikunci. Dokumen pendukung telah saya bagikan melalui surel grup."
                },
            ),
        )
        WriteAction.SUMMARIZE -> TransformResult(
            ResultKind.TEKS,
            "Summarize",
            listOf(
                when (idx) {
                    0 -> "Rapat mingguan proyek Nusantara selesai; ringkasan sudah dibagikan kepada seluruh tim."
                    1 -> "Desain 80% selesai, integrasi pembayaran berjalan, ada dua kendala kecil untuk dibahas besok."
                    else -> "Masukan dibutuhkan sebelum Jumat untuk mengunci ruang lingkup; dokumen sudah dikirim via surel."
                },
            ),
        )
        WriteAction.MAKE_LIST -> TransformResult(
            ResultKind.DAFTAR,
            "Make List",
            listOf(
                when (idx) {
                    0 -> "Ringkasan rapat mingguan proyek Nusantara dibagikan"
                    1 -> "Progres desain mencapai delapan puluh persen"
                    2 -> "Dua kendala kecil dibahas pada sesi besok"
                    else -> "Masukan dikumpulkan sebelum hari Jumat"
                },
                when (idx) {
                    0 -> "Seluruh tim hadir tepat waktu"
                    1 -> "Integrasi pembayaran sedang diselesaikan"
                    2 -> "Ruang lingkup dikunci setelah masukan masuk"
                    else -> "Dokumen lampiran dikirim lewat surel grup"
                },
                when (idx) {
                    0 -> "Masukan tim menjadi bahan revisi berikutnya"
                    else -> "Jadwal tinjauan ulang diatur pekan depan"
                },
            ),
        )
        WriteAction.MAKE_TABLE -> TransformResult(
            ResultKind.TABEL,
            "Make Table",
            lines = emptyList(),
            table = listOf(
                listOf("Aspek", "Status", "Tenggat"),
                when (idx) {
                    0 -> listOf("Ringkasan", "Selesai", "Hari ini")
                    1 -> listOf("Desain", "80%", "Kamis")
                    else -> listOf("Ruang lingkup", "Tinjau", "Jumat")
                },
                when (idx) {
                    0 -> listOf("Revisi", "Menunggu", "Jumat")
                    1 -> listOf("Pembayaran", "Proses", "Jumat")
                    else -> listOf("Lampiran", "Terkirim", "Hari ini")
                },
                listOf("Tinjauan", "Dijadwalkan", "Pekan depan"),
            ),
        )
        else -> TransformResult(ResultKind.TEKS, "Proofread", listOf(paragrafAsli[idx]))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WritingToolsLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()

    val paragrafList = remember { mutableStateListOf<String>().apply { addAll(paragrafAsli) } }
    val saran = remember { mutableStateListOf<ProofSuggestion>().apply { addAll(saranAwal) } }

    var selectedIdx by remember { mutableStateOf<Int?>(null) }
    var activeAction by remember { mutableStateOf<WriteAction?>(null) }
    var toneOpen by remember { mutableStateOf(false) }
    var proses by remember { mutableStateOf(ProsesState.SIAP) }
    var result by remember { mutableStateOf<TransformResult?>(null) }
    var typed by remember { mutableStateOf("") }
    var revealedRows by remember { mutableIntStateOf(0) }
    var proofreadActive by remember { mutableStateOf(false) }
    var banner by remember { mutableStateOf<String?>(null) }

    // Animasi mengetik deterministik untuk hasil transformasi
    LaunchedEffect(result) {
        val r = result ?: return@LaunchedEffect
        typed = ""
        revealedRows = 0
        when (r.kind) {
            ResultKind.TEKS -> {
                val full = r.lines.joinToString("\n")
                for (i in 1..full.length) {
                    typed = full.substring(0, i)
                    delay(if (full[i - 1] == '\n') 70L else 12L)
                }
            }
            else -> {
                val total = if (r.kind == ResultKind.DAFTAR) r.lines.size else r.table.size
                for (i in 1..total) {
                    revealedRows = i
                    delay(180L)
                }
            }
        }
        proses = ProsesState.SELESAI
    }

    LaunchedEffect(banner) {
        if (banner != null) {
            delay(2200)
            banner = null
        }
    }

    fun jalankan(action: WriteAction, tone: ToneOption?) {
        val idx = selectedIdx ?: return
        scope.launch {
            proofreadActive = false
            activeAction = action
            toneOpen = false
            result = null
            typed = ""
            proses = ProsesState.MEMPROSES
            delay(700)
            result = hasilTransformasi(action, tone, idx)
            proses = ProsesState.MENGETIK
        }
    }

    fun bukaProofread() {
        activeAction = WriteAction.PROOFREAD
        toneOpen = false
        result = null
        typed = ""
        proses = ProsesState.SIAP
        proofreadActive = true
    }

    fun terimaSaran(s: ProofSuggestion) {
        val idx = selectedIdx ?: return
        paragrafList[idx] = paragrafList[idx].replace(s.kata, s.ganti)
        val posisi = saran.indexOfFirst { it.id == s.id }
        if (posisi >= 0) saran.removeAt(posisi)
        banner = "\"${s.kata}\" diganti \"${s.ganti}\""
    }

    fun urungkanProofread() {
        val idx = selectedIdx ?: return
        paragrafList[idx] = paragrafAsli[idx]
        saran.clear()
        saran.addAll(saranAwal)
        banner = "Perubahan proofread diurungkan"
    }

    fun balikKeAsal() {
        val idx = selectedIdx ?: return
        paragrafList[idx] = paragrafAsli[idx]
        saran.clear()
        saran.addAll(saranAwal)
        result = null
        typed = ""
        proses = ProsesState.SIAP
        activeAction = null
        proofreadActive = false
        banner = "Teks dikembalikan ke versi asal"
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Writing Tools",
                subtitle = "Rewrite, proofread, summarize — AI simulasi",
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
                title = "Writing Tools — AI simulasi",
                body = "Pilih paragraf, lalu jalankan Rewrite, Proofread, Summarize, Make List, Make Table, atau Change Tone. Seluruh hasil transformasi berasal dari data mock deterministik dan bukan layanan AI sungguhan.",
            )

            // ===== Kartu dokumen + toolbar melayang =====
            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LabSectionTitle("Dokumen Contoh", "Ketuk paragraf untuk memilih teks")
                    SimulasiPill("AI simulasi")
                }

                paragrafList.forEachIndexed { idx, paragraf ->
                    val dipilih = selectedIdx == idx
                    if (dipilih) {
                        FloatingToolbar(
                            enabled = proses == ProsesState.SIAP || proses == ProsesState.SELESAI,
                            activeAction = activeAction,
                            toneOpen = toneOpen,
                            onAction = { action ->
                                if (action == WriteAction.TONE) {
                                    toneOpen = !toneOpen
                                    activeAction = WriteAction.TONE
                                } else if (action == WriteAction.PROOFREAD) {
                                    bukaProofread()
                                } else {
                                    jalankan(action, null)
                                }
                            },
                            onTone = { tone -> jalankan(WriteAction.TONE, tone) },
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (dipilih) palette.primaryContainer else palette.surfaceMuted,
                                RoundedCornerShape(12.dp),
                            )
                            .border(
                                1.dp,
                                if (dipilih) palette.primary else palette.border,
                                RoundedCornerShape(12.dp),
                            )
                            .clickable {
                                selectedIdx = if (dipilih) null else idx
                                toneOpen = false
                                proofreadActive = false
                                result = null
                                proses = ProsesState.SIAP
                            }
                            .padding(12.dp),
                    ) {
                        if (proofreadActive && dipilih) {
                            ProofreadTokens(
                                text = paragraf,
                                saran = saran,
                                onAccept = { terimaSaran(it) },
                            )
                        } else {
                            Text(
                                text = paragraf,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = palette.textPrimary,
                            )
                        }
                    }
                    if (dipilih) {
                        Text(
                            text = "Paragraf terpilih · ${paragraf.length} karakter · ${saran.size} saran proofread",
                            fontSize = 11.sp,
                            color = palette.textMuted,
                        )
                    }
                }
            }

            // ===== Status proses =====
            AnimatedVisibility(visible = proses == ProsesState.MEMPROSES, enter = fadeIn(), exit = fadeOut()) {
                LabCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        TypingDots()
                        Text(
                            text = "Menyusun hasil ${activeAction?.label ?: ""}… (AI simulasi)",
                            fontSize = 13.sp,
                            color = palette.textSecondary,
                        )
                    }
                }
            }

            // ===== Kartu hasil transformasi =====
            AnimatedVisibility(visible = result != null, enter = fadeIn(), exit = fadeOut()) {
                val r = result
                if (r != null) {
                    LabCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            LabSectionTitle(r.judul, "Hasil deterministik dari data mock")
                            SimulasiPill("AI simulasi")
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.border),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                when (r.kind) {
                                    ResultKind.TEKS -> {
                                        Row {
                                            Text(
                                                text = typed,
                                                fontSize = 13.sp,
                                                lineHeight = 20.sp,
                                                color = palette.textPrimary,
                                                modifier = Modifier.weight(1f),
                                            )
                                            if (proses == ProsesState.MENGETIK) {
                                                KetikKursor()
                                            }
                                        }
                                    }
                                    ResultKind.DAFTAR -> {
                                        r.lines.take(revealedRows).forEach { line ->
                                            Row(
                                                modifier = Modifier.padding(vertical = 3.dp),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            ) {
                                                Text("•", fontSize = 13.sp, color = palette.primary, fontWeight = FontWeight.Bold)
                                                Text(
                                                    text = line,
                                                    fontSize = 13.sp,
                                                    lineHeight = 20.sp,
                                                    color = palette.textPrimary,
                                                )
                                            }
                                        }
                                    }
                                    ResultKind.TABEL -> {
                                        r.table.take(revealedRows).forEachIndexed { baris, cells ->
                                            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                                cells.forEach { cell ->
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .border(1.dp, palette.border, RoundedCornerShape(6.dp))
                                                            .padding(horizontal = 6.dp, vertical = 6.dp),
                                                    ) {
                                                        Text(
                                                            text = cell,
                                                            fontSize = 11.sp,
                                                            fontWeight = if (baris == 0) FontWeight.Bold else FontWeight.Normal,
                                                            color = if (baris == 0) palette.textPrimary else palette.textSecondary,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (r.kind == ResultKind.TABEL) {
                            Text(
                                text = "Tabel tidak dapat diterapkan kembali ke paragraf (dinonaktifkan).",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FlatSecondaryButton(
                                text = "Balik ke Asal",
                                onClick = { balikKeAsal() },
                                modifier = Modifier.weight(1f),
                                enabled = proses != ProsesState.MEMPROSES,
                            )
                            FlatPrimaryButton(
                                text = "Terapkan ke Dokumen",
                                onClick = {
                                    val idx = selectedIdx ?: return@FlatPrimaryButton
                                    paragrafList[idx] = when (r.kind) {
                                        ResultKind.TEKS -> r.lines.joinToString(" ")
                                        ResultKind.DAFTAR -> r.lines.joinToString("; ")
                                        ResultKind.TABEL -> paragrafList[idx]
                                    }
                                    banner = "Hasil diterapkan ke paragraf terpilih"
                                },
                                modifier = Modifier.weight(1f),
                                enabled = proses == ProsesState.SELESAI && r.kind != ResultKind.TABEL,
                            )
                        }
                    }
                }
            }

            // ===== Panel proofread =====
            AnimatedVisibility(visible = proofreadActive, enter = fadeIn(), exit = fadeOut()) {
                LabCard {
                    LabSectionTitle("Panel Proofread", "Ketuk kata bergaris bawah di dokumen untuk menerima saran")
                    if (saran.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = palette.success.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, palette.success),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = "Semua saran diterima — dokumen bebas typo (status: selesai).",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.success,
                                modifier = Modifier.padding(10.dp),
                            )
                        }
                    } else {
                        saran.forEach { s ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = s.kata,
                                            fontSize = 13.sp,
                                            color = palette.danger,
                                            textDecoration = TextDecoration.LineThrough,
                                        )
                                        Text("→", fontSize = 13.sp, color = palette.textMuted)
                                        Text(
                                            text = s.ganti,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.success,
                                        )
                                    }
                                    Text(
                                        text = s.alasan + " · status: menunggu",
                                        fontSize = 11.sp,
                                        color = palette.textMuted,
                                    )
                                }
                                Surface(
                                    onClick = { terimaSaran(s) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = palette.primary,
                                    shadowElevation = 0.dp,
                                    tonalElevation = 0.dp,
                                ) {
                                    Text(
                                        text = "Terima",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.onPrimary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    )
                                }
                            }
                        }
                    }
                    FlatSecondaryButton(
                        text = "Urungkan Semua Perubahan",
                        onClick = { urungkanProofread() },
                        enabled = selectedIdx != null,
                    )
                }
            }

            // ===== Banner status =====
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
                    Text("Manfaat Pola Writing Tools:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                    Text("• Toolbar kontekstual melayang menempatkan aksi AI tepat di atas teks terpilih", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Animasi mengetik + opsi Balik ke Asal menjaga rasa aman saat mencoba hasil transformasi", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Panel proofread dengan saran bergaris bawah membuat koreksi typo sekali ketuk", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Zero Shadow Flat UI: seluruh permukaan memakai border 1px dan warna tema", fontSize = 12.sp, color = palette.textSecondary)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProofreadTokens(
    text: String,
    saran: List<ProofSuggestion>,
    onAccept: (ProofSuggestion) -> Unit,
) {
    val palette = LocalAppPalette.current
    val tokens = remember(text) { text.split(" ") }
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        tokens.forEach { token ->
            val bersih = token.trimEnd(',', '.', ';', ':', '!', '?')
            val cocok = saran.firstOrNull { it.kata == bersih }
            if (cocok != null) {
                Text(
                    text = token,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = palette.info,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onAccept(cocok) }
                        .padding(horizontal = 2.dp, vertical = 1.dp)
                        .semantics {
                            role = Role.Button
                            contentDescription = "Terima saran ${cocok.kata} menjadi ${cocok.ganti}"
                        },
                )
            } else {
                Text(
                    text = token,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = palette.textPrimary,
                )
            }
        }
    }
}

@Composable
private fun FloatingToolbar(
    enabled: Boolean,
    activeAction: WriteAction?,
    toneOpen: Boolean,
    onAction: (WriteAction) -> Unit,
    onTone: (ToneOption) -> Unit,
) {
    val palette = LocalAppPalette.current
    val barColor = palette.textPrimary
    val onBarColor = palette.background

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
        Box(modifier = Modifier.padding(start = 22.dp)) {
            Canvas(modifier = Modifier.size(10.dp)) {
                val segitiga = Path().apply {
                    moveTo(size.width / 2f, size.height)
                    lineTo(0f, 0f)
                    lineTo(size.width, 0f)
                    close()
                }
                drawPath(path = segitiga, color = barColor)
            }
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = barColor,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                WriteAction.entries.forEach { action ->
                    val aktif = activeAction == action
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier
                            .alpha(if (enabled) 1f else 0.4f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (aktif) onBarColor.copy(alpha = 0.18f) else Color.Transparent,
                                RoundedCornerShape(10.dp),
                            )
                            .clickable(enabled = enabled) { onAction(action) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .semantics {
                                role = Role.Button
                                contentDescription = "Writing tool ${action.label}"
                            },
                    ) {
                        ActionGlyph(action = action, tint = onBarColor)
                        Text(
                            text = action.label,
                            fontSize = 9.sp,
                            fontWeight = if (aktif) FontWeight.Bold else FontWeight.Medium,
                            color = onBarColor,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
        AnimatedVisibility(visible = toneOpen && enabled, enter = fadeIn(), exit = fadeOut()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ToneOption.entries.forEach { tone ->
                    Surface(
                        onClick = { onTone(tone) },
                        shape = RoundedCornerShape(999.dp),
                        color = palette.primaryContainer,
                        border = BorderStroke(1.dp, palette.primary),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Text(
                            text = "Nada: ${tone.label}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionGlyph(action: WriteAction, tint: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val garis = Stroke(width = 2.0f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val w = size.width
        val h = size.height
        when (action) {
            WriteAction.REWRITE -> {
                drawArc(
                    color = tint,
                    startAngle = 40f,
                    sweepAngle = 285f,
                    useCenter = false,
                    topLeft = Offset(w * 0.17f, h * 0.17f),
                    size = Size(w * 0.66f, h * 0.66f),
                    style = garis,
                )
                drawLine(tint, Offset(w * 0.62f, h * 0.08f), Offset(w * 0.84f, h * 0.2f), 2.0f, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.84f, h * 0.2f), Offset(w * 0.72f, h * 0.4f), 2.0f, StrokeCap.Round)
            }
            WriteAction.PROOFREAD -> {
                val centang = Path().apply {
                    moveTo(w * 0.2f, h * 0.5f)
                    lineTo(w * 0.42f, h * 0.7f)
                    lineTo(w * 0.8f, h * 0.28f)
                }
                drawPath(path = centang, color = tint, style = garis)
                drawLine(tint, Offset(w * 0.2f, h * 0.86f), Offset(w * 0.8f, h * 0.86f), 2.0f, StrokeCap.Round)
            }
            WriteAction.SUMMARIZE -> {
                drawLine(tint, Offset(w * 0.18f, h * 0.3f), Offset(w * 0.82f, h * 0.3f), 2.0f, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.18f, h * 0.52f), Offset(w * 0.64f, h * 0.52f), 2.0f, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.18f, h * 0.74f), Offset(w * 0.46f, h * 0.74f), 2.0f, StrokeCap.Round)
            }
            WriteAction.MAKE_LIST -> {
                drawCircle(tint, radius = 1.8f, center = Offset(w * 0.2f, h * 0.3f))
                drawCircle(tint, radius = 1.8f, center = Offset(w * 0.2f, h * 0.52f))
                drawCircle(tint, radius = 1.8f, center = Offset(w * 0.2f, h * 0.74f))
                drawLine(tint, Offset(w * 0.36f, h * 0.3f), Offset(w * 0.84f, h * 0.3f), 2.0f, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.36f, h * 0.52f), Offset(w * 0.84f, h * 0.52f), 2.0f, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.36f, h * 0.74f), Offset(w * 0.84f, h * 0.74f), 2.0f, StrokeCap.Round)
            }
            WriteAction.MAKE_TABLE -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.15f, h * 0.22f),
                    size = Size(w * 0.7f, h * 0.56f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f),
                    style = garis,
                )
                drawLine(tint, Offset(w * 0.5f, h * 0.22f), Offset(w * 0.5f, h * 0.78f), 2.0f, StrokeCap.Round)
                drawLine(tint, Offset(w * 0.15f, h * 0.5f), Offset(w * 0.85f, h * 0.5f), 2.0f, StrokeCap.Round)
            }
            WriteAction.TONE -> {
                drawCircle(tint, radius = w * 0.33f, center = Offset(w * 0.5f, h * 0.5f), style = garis)
                drawCircle(tint, radius = 1.6f, center = Offset(w * 0.38f, h * 0.42f))
                drawCircle(tint, radius = 1.6f, center = Offset(w * 0.62f, h * 0.42f))
                drawArc(
                    color = tint,
                    startAngle = 25f,
                    sweepAngle = 130f,
                    useCenter = false,
                    topLeft = Offset(w * 0.32f, h * 0.3f),
                    size = Size(w * 0.36f, h * 0.36f),
                    style = garis,
                )
            }
        }
    }
}

@Composable
private fun TypingDots() {
    val palette = LocalAppPalette.current
    val transition = rememberInfiniteTransition(label = "typingDots")
    val fase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Restart),
        label = "typingFase",
    )
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 0 until 3) {
            val jarak = fase - i
            val alpha = if (jarak in 0f..1f) 0.4f + 0.6f * jarak else 0.35f
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .alpha(alpha)
                    .clip(RoundedCornerShape(3.dp))
                    .background(palette.primary, RoundedCornerShape(3.dp)),
            )
        }
    }
}

@Composable
private fun KetikKursor() {
    val palette = LocalAppPalette.current
    val transition = rememberInfiniteTransition(label = "kursor")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(tween(450), RepeatMode.Reverse),
        label = "kursorAlpha",
    )
    Box(
        modifier = Modifier
            .padding(start = 2.dp, top = 3.dp)
            .size(width = 2.dp, height = 15.dp)
            .alpha(alpha)
            .background(palette.primary, RoundedCornerShape(1.dp)),
    )
}
