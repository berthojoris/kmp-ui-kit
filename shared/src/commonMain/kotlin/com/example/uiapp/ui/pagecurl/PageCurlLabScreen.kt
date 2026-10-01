package com.example.uiapp.ui.pagecurl

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.PI
import kotlin.math.sin

private const val STAGE_HEIGHT = 320

private enum class ReaderMode(val label: String) {
    HALAMAN("Mode halaman"),
    GULIR("Mode gulir"),
}

private data class BookPage(
    val id: String,
    val title: String,
    val paragraphs: List<String>,
)

private fun buildBookPages(): List<BookPage> = listOf(
    BookPage(
        "hal-1",
        "Bab 1 — Kapal Terakhir",
        listOf(
            "Senja meleleh di atas dermaga kayu ketika kapal terakhir bersandar. Bau garam dan solar bercampur, menemani langkah para buruh yang menghitung sisa muatan.",
            "Aruna berdiri di ujung dermaga, memegang surat yang sudah dua kali ia baca. Isinya singkat: pameran kerajinan dibuka minggu depan di kota seberang.",
        ),
    ),
    BookPage(
        "hal-2",
        "Bab 2 — Toko di Gang Sepi",
        listOf(
            "Pagi berikutnya ia menemui Bu Ratna di toko anyaman gang belakang. Rak-rak bambu penuh tas, tikar, dan kap lampu yang belum sempat dijual.",
            "“Kalau mau ikut pameran, bawa saja yang terbaik,” kata Bu Ratna sambil menata ulang etalase. “Tapi jangan buru-buru. Karya yang tenang justru lebih laku.”",
        ),
    ),
    BookPage(
        "hal-3",
        "Bab 3 — Pewarna dari Hutan",
        listOf(
            "Mereka memilih serat pandan dan pewarna alami dari kulit kayu tingi. Warnanya hangat, cokelat kemerahan yang mengingatkan Aruna pada sore di dermaga.",
            "Setiap helai dianyam tangan. Sebuah tas kecil butuh dua minggu — lama, tapi begitulah cara pengrajin di kampung itu mempertahankan mutu.",
        ),
    ),
    BookPage(
        "hal-4",
        "Bab 4 — Malam Sebelum Berangkat",
        listOf(
            "Malam itu hujan turun pelan. Kardus-kardus ditutupi terpal di ruang tamu, sementara radio mengabarkan cuaca buruk di selat.",
            "“Kita berangkat subuh,” putus Aruna. Ia mencatat tiap isi kardus di buku tulis: tiga puluh karya, lima pengrajin, satu harapan sederhana.",
        ),
    ),
    BookPage(
        "hal-5",
        "Bab 5 — Ruang Pameran",
        listOf(
            "Ruang pameran ramai sejak dibuka. Pengunjung berhenti lama di depan tas serat pandan, mengelus permukaannya, membaca kartu keterangan harga.",
            "Seorang kurator menawarkan kerja sama untuk toko daring. Aruna tersenyum, tapi meminta waktu untuk berunding dengan para pengrajin lebih dulu.",
        ),
    ),
    BookPage(
        "hal-6",
        "Penutup — Pulang",
        listOf(
            "Di perjalanan pulang, kardus-kardus kini jauh lebih ringan. Senja yang sama menemani, tetapi rasanya berbeda: karya mereka kini dikenal orang lebih luas.",
            "Bu Ratna mengirim pesan singkat: “Terima kasih sudah sabar.” Aruna membalasnya dengan foto dermaga, lalu menutup buku catatannya.",
        ),
    ),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PageCurlLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    val pages = remember { buildBookPages() }
    val lastIndex = pages.lastIndex

    var currentPage by remember { mutableIntStateOf(0) }
    var mode by remember { mutableStateOf(ReaderMode.HALAMAN) }
    var fontScale by remember { mutableFloatStateOf(1f) }
    val bookmarks = remember { mutableStateListOf<String>("hal-1") }

    var curlProgress by remember { mutableFloatStateOf(0f) }
    var curlFromRight by remember { mutableStateOf(true) }
    var dragging by remember { mutableStateOf(false) }
    var settleToken by remember { mutableIntStateOf(0) }
    var settleTarget by remember { mutableFloatStateOf(0f) }
    var pendingCommit by remember { mutableStateOf(false) }

    val page = pages[currentPage]
    val readPercent = ((currentPage + 1) * 100) / pages.size
    val bookmarked = bookmarks.contains(page.id)
    val pageMode = mode == ReaderMode.HALAMAN

    // Animasi lipatan: lepas jari di tengah jalan akan kembali rata (dibatalkan)
    LaunchedEffect(settleToken) {
        if (settleToken == 0) return@LaunchedEffect
        val start = curlProgress
        animate(
            initialValue = start,
            targetValue = settleTarget,
            animationSpec = tween(240),
        ) { value, _ -> curlProgress = value }
        if (pendingCommit) {
            currentPage = if (curlFromRight) {
                (currentPage + 1).coerceAtMost(lastIndex)
            } else {
                (currentPage - 1).coerceAtLeast(0)
            }
        }
        curlProgress = 0f
        pendingCommit = false
    }

    fun startCurl(fromRight: Boolean, commit: Boolean) {
        curlFromRight = fromRight
        settleTarget = if (commit) 1f else 0f
        pendingCommit = commit
        settleToken++
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Page Curl Reader",
                subtitle = "Balik halaman dengan deformasi",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            LabSectionTitle(
                title = "Pembaca Page Curl",
                subtitle = "Tarik dari tepi kanan / kiri untuk membalik",
            )

            LabIntroCard(
                title = "Membalik halaman seperti buku sungguhan",
                body = "Halaman melengkung mengikuti jari dan menampilkan bayangan lipatan. Lepas sebelum separuh jalan untuk membatalkan balikan. Isi buku adalah cerita tiruan untuk keperluan demo.",
            )

            // Progress baca & penanda halaman
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Halaman ${currentPage + 1} dari ${pages.size}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = "$readPercent% selesai",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.primary,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(palette.surfaceMuted)
                            .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(4.dp)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = readPercent / 100f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(palette.primary),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        BookmarkButton(
                            bookmarked = bookmarked,
                            onClick = {
                                if (bookmarked) bookmarks.remove(page.id) else bookmarks.add(page.id)
                            },
                            modifier = Modifier.weight(1f),
                        )
                        SmallActionButton(
                            label = if (pageMode) ReaderMode.HALAMAN.label else ReaderMode.GULIR.label,
                            enabled = true,
                            onClick = { mode = if (pageMode) ReaderMode.GULIR else ReaderMode.HALAMAN },
                            modifier = Modifier.weight(1f),
                        )
                        FontScaleControl(
                            fontScale = fontScale,
                            onScale = { fontScale = it },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (bookmarks.isNotEmpty()) {
                        Text(
                            text = "Penanda: " + bookmarks.joinToString(", ") { id ->
                                pages.firstOrNull { it.id == id }?.title ?: id
                            },
                            fontSize = 10.sp,
                            color = palette.textMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            // Area baca
            if (pageMode) {
                PageStage(
                    page = page,
                    fontScale = fontScale,
                    curlProgress = curlProgress,
                    curlFromRight = curlFromRight,
                    onDragStart = { startX, widthPx ->
                        val edge = widthPx * 0.18f
                        dragging = when {
                            startX > widthPx - edge && currentPage < lastIndex -> {
                                curlFromRight = true
                                true
                            }
                            startX < edge && currentPage > 0 -> {
                                curlFromRight = false
                                true
                            }
                            else -> false
                        }
                    },
                    onDrag = { dragX, widthPx ->
                        if (dragging) {
                            val delta = if (curlFromRight) -dragX else dragX
                            curlProgress = (curlProgress + delta / widthPx).coerceIn(0f, 1f)
                        }
                    },
                    onDragEnd = {
                        if (dragging) {
                            startCurl(fromRight = curlFromRight, commit = curlProgress > 0.35f)
                        }
                        dragging = false
                    },
                    onDragCancel = {
                        if (dragging) {
                            startCurl(fromRight = curlFromRight, commit = false)
                        }
                        dragging = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                ScrollReader(
                    pages = pages,
                    fontScale = fontScale,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Kontrol balik halaman
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (pageMode) 1f else 0.45f),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SmallActionButton(
                        label = "◀ Sebelumnya",
                        enabled = pageMode && currentPage > 0 && !pendingCommit,
                        onClick = { startCurl(fromRight = false, commit = true) },
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = if (pageMode) "Tarik dari tepi atau pakai tombol" else "Gulir bebas aktif",
                        fontSize = 10.sp,
                        color = palette.textMuted,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    SmallActionButton(
                        label = "Berikutnya ▶",
                        enabled = pageMode && currentPage < lastIndex && !pendingCommit,
                        onClick = { startCurl(fromRight = true, commit = true) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "Catatan pola Page Curl:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "• Deformasi digambar Canvas: lipatan melengkung (parabola) plus bayangan di sisi lipatan",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        text = "• Gerak mengikuti jari; tarikan kurang dari 35% membatalkan balikan",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        text = "• Berbeda dari flashcard 3D di gamification_lab: di sini halaman benar-benar dibalik sebagian",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun PageStage(
    page: BookPage,
    fontScale: Float,
    curlProgress: Float,
    curlFromRight: Boolean,
    onDragStart: (Float, Float) -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    val baseSizeSp = 13f * fontScale

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(STAGE_HEIGHT.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surface)
            .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(16.dp))
            .pointerInput(page.id) {
                detectDragGestures(
                    onDragStart = { start -> onDragStart(start.x, size.width.toFloat()) },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel,
                ) { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, size.width.toFloat())
                }
            }
            .semantics {
                contentDescription = "Halaman ${page.title}. Tarik dari tepi kanan atau kiri untuk membalik halaman"
            },
    ) {
        // Konten halaman berjalan
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = page.title,
                fontSize = (17f * fontScale).sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            page.paragraphs.forEach { para ->
                Text(
                    text = para,
                    fontSize = baseSizeSp.sp,
                    lineHeight = (baseSizeSp * 1.5f).sp,
                    color = palette.textSecondary,
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (curlProgress > 0.001f) {
            PageCurlOverlay(
                progress = curlProgress,
                fromRight = curlFromRight,
                modifier = Modifier.fillMaxSize(),
            )
        }

        if (curlProgress <= 0.001f) {
            Text(
                text = "Tarik dari tepi kanan / kiri",
                fontSize = 10.sp,
                color = palette.textMuted,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
            )
        }
    }
}

@Composable
private fun PageCurlOverlay(
    progress: Float,
    fromRight: Boolean,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    val revealColor = palette.surfaceMuted
    val lineColor = palette.border
    val shadowColor = palette.textPrimary
    val flapBrushStart = palette.surface
    val flapBrushEnd = palette.surfaceMuted

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        val foldX = if (fromRight) w * (1f - progress) else w * progress
        val flapSpan = (if (fromRight) w - foldX else foldX) * 0.85f

        // 1) Halaman berikutnya yang tersingkap di sisi lipatan
        val reveal = Path().apply {
            if (fromRight) {
                moveTo(0f, 0f)
                lineTo(foldX, 0f)
                lineTo(foldX, h)
                lineTo(0f, h)
            } else {
                moveTo(w, 0f)
                lineTo(foldX, 0f)
                lineTo(foldX, h)
                lineTo(w, h)
            }
            close()
        }
        drawPath(path = reveal, color = revealColor)

        // Baris teks tiruan halaman yang tersingkap
        val lineInset = 14.dp.toPx()
        for (i in 0..6) {
            val ly = h * 0.12f + i * h * 0.11f
            val startX = if (fromRight) lineInset else foldX + lineInset
            val maxLen = if (fromRight) (foldX - lineInset * 2) else (w - foldX - lineInset * 2)
            if (maxLen > 8.dp.toPx()) {
                val len = maxLen * if (i % 3 == 2) 0.55f else 0.92f
                drawLine(
                    color = lineColor,
                    start = Offset(startX, ly),
                    end = Offset(startX + len, ly),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }

        // 2) Bayangan lipatan di sisi yang tersingkap
        val shadowWidth = 44.dp.toPx()
        val shadowStartX = if (fromRight) foldX - shadowWidth else foldX
        drawRect(
            brush = Brush.linearGradient(
                colors = if (fromRight) {
                    listOf(shadowColor.copy(alpha = 0f), shadowColor.copy(alpha = 0.22f))
                } else {
                    listOf(shadowColor.copy(alpha = 0.22f), shadowColor.copy(alpha = 0f))
                },
                start = Offset(shadowStartX, 0f),
                end = Offset(shadowStartX + shadowWidth, 0f),
            ),
            topLeft = Offset(shadowStartX, 0f),
            size = Size(shadowWidth, h),
        )

        // 3) Lipatan halaman: pita melengkung (parabola) dari tepi ke garis lipat
        val flap = Path()
        val steps = 14
        for (i in 0..steps) {
            val t = i / steps.toFloat()
            val bulge = sin(t * PI).toFloat() * flapSpan * 0.35f
            val edgeX = if (fromRight) {
                foldX - flapSpan - bulge
            } else {
                foldX + flapSpan + bulge
            }
            val y = t * h
            if (i == 0) flap.moveTo(edgeX, y) else flap.lineTo(edgeX, y)
        }
        if (fromRight) {
            flap.lineTo(foldX, h)
            flap.lineTo(foldX, 0f)
        } else {
            flap.lineTo(foldX, h)
            flap.lineTo(foldX, 0f)
        }
        flap.close()

        drawPath(
            path = flap,
            brush = Brush.linearGradient(
                colors = listOf(flapBrushStart, flapBrushEnd),
                start = Offset(foldX, 0f),
                end = Offset(if (fromRight) foldX - flapSpan else foldX + flapSpan, 0f),
            ),
        )
        drawPath(
            path = flap,
            color = lineColor,
            style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round),
        )

        // 4) Garis lipat tegas
        drawLine(
            color = lineColor,
            start = Offset(foldX, 0f),
            end = Offset(foldX, h),
            strokeWidth = 1.dp.toPx(),
        )
    }
}

@Composable
private fun ScrollReader(
    pages: List<BookPage>,
    fontScale: Float,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    val baseSizeSp = 13f * fontScale
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        modifier = modifier.height(STAGE_HEIGHT.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            pages.forEach { item ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = item.title,
                        fontSize = (15f * fontScale).sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    item.paragraphs.forEach { para ->
                        Text(
                            text = para,
                            fontSize = baseSizeSp.sp,
                            lineHeight = (baseSizeSp * 1.5f).sp,
                            color = palette.textSecondary,
                        )
                    }
                }
            }
            Text(
                text = "Mode gulir — balikan halaman dinonaktifkan",
                fontSize = 10.sp,
                color = palette.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun BookmarkButton(
    bookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (bookmarked) palette.primaryContainer else palette.surfaceMuted,
        border = BorderStroke(1.dp, if (bookmarked) palette.primary else palette.border),
        modifier = modifier.semantics {
            role = Role.Button
            contentDescription = if (bookmarked) "Hapus penanda halaman" else "Tandai halaman"
        },
    ) {
        Text(
            text = if (bookmarked) "★ Ditandai" else "☆ Tandai",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (bookmarked) palette.onPrimaryContainer else palette.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        )
    }
}

@Composable
private fun FontScaleControl(
    fontScale: Float,
    onScale: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(palette.surfaceMuted)
            .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SmallActionButton(
            label = "A−",
            enabled = fontScale > 0.75f,
            onClick = { onScale((fontScale - 0.15f).coerceAtLeast(0.75f)) },
        )
        Text(
            text = "${(fontScale * 100).toInt()}%",
            fontSize = 10.sp,
            color = palette.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        SmallActionButton(
            label = "A+",
            enabled = fontScale < 1.95f,
            onClick = { onScale((fontScale + 0.15f).coerceAtMost(1.95f)) },
        )
    }
}

@Composable
private fun SmallActionButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = palette.surfaceMuted,
        border = BorderStroke(1.dp, palette.border),
        modifier = modifier,
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) palette.textPrimary else palette.textMuted,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
        )
    }
}
