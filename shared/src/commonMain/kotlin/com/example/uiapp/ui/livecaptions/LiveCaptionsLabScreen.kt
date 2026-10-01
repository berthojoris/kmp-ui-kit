package com.example.uiapp.ui.livecaptions

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private enum class SpeakerTag(val label: String) {
    NARASUMBER("Narasumber"),
    PEWAWANCARA("Pewawancara"),
}

private enum class CaptionKind(val label: String) {
    UCAPAN("Ucapan"),
    NON_SPEECH("Suara non-ucapan"),
    GALAT("Pengenalan gagal"),
}

private enum class CaptionStatus(val label: String) {
    SIAP("Siap mendengarkan"),
    LISTENING("Mendengarkan…"),
    STOPPED("Dihentikan"),
    SELESAI("Transkrip selesai"),
}

private data class CaptionSegment(
    val id: String,
    val time: String,
    val speaker: SpeakerTag,
    val kind: CaptionKind,
    val note: String?,
    val words: List<String>,
)

private data class CaptionUnit(
    val id: String,
    val segmentId: String,
    val text: String,
    val isNote: Boolean,
    val index: Int,
)

private fun buildCaptionSegments(): List<CaptionSegment> = listOf(
    CaptionSegment(
        "seg-1", "00:03", SpeakerTag.PEWAWANCARA, CaptionKind.UCAPAN, null,
        listOf("Selamat", "malam,", "bagaimana", "persiapan", "pameran", "kerajinan", "besok?"),
    ),
    CaptionSegment(
        "seg-2", "00:09", SpeakerTag.NARASUMBER, CaptionKind.UCAPAN, null,
        listOf("Alhamdulillah", "hampir", "siap.", "Kami", "membawa", "tiga", "puluh", "karya", "anyaman", "dari", "lima", "pengrajin."),
    ),
    CaptionSegment(
        "seg-3", "00:16", SpeakerTag.NARASUMBER, CaptionKind.NON_SPEECH, "[ketukan pintu]", emptyList(),
    ),
    CaptionSegment(
        "seg-4", "00:19", SpeakerTag.PEWAWANCARA, CaptionKind.UCAPAN, null,
        listOf("Boleh", "kami", "lihat", "contoh", "yang", "jadi", "unggulan?"),
    ),
    CaptionSegment(
        "seg-5", "00:24", SpeakerTag.NARASUMBER, CaptionKind.UCAPAN, null,
        listOf("Ini", "tas", "serat", "pandan", "dengan", "pewarna", "alami.", "Butuh", "dua", "minggu", "per", "buah."),
    ),
    CaptionSegment(
        "seg-6", "00:32", SpeakerTag.NARASUMBER, CaptionKind.NON_SPEECH, "[tawa]", emptyList(),
    ),
    CaptionSegment(
        "seg-7", "00:35", SpeakerTag.PEWAWANCARA, CaptionKind.GALAT, "Potongan suara tidak dikenali", emptyList(),
    ),
    CaptionSegment(
        "seg-8", "00:39", SpeakerTag.NARASUMBER, CaptionKind.UCAPAN, null,
        listOf("Target", "kami", "sederhana:", "pengrajin", "lokal", "dikenal", "lebih", "luas."),
    ),
)

private fun buildCaptionUnits(segments: List<CaptionSegment>): List<CaptionUnit> {
    val units = mutableListOf<CaptionUnit>()
    segments.forEach { seg ->
        if (seg.words.isEmpty()) {
            units.add(CaptionUnit(seg.id + "-n", seg.id, seg.note ?: "…", true, units.size))
        } else {
            seg.words.forEachIndexed { idx, word ->
                units.add(CaptionUnit(seg.id + "-w$idx", seg.id, word, false, units.size))
            }
        }
    }
    return units
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun LiveCaptionsLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    val segments = remember { buildCaptionSegments() }
    val allUnits = remember(segments) { buildCaptionUnits(segments) }
    val unitsBySegment = remember(allUnits) { allUnits.groupBy { it.segmentId } }

    var isListening by remember { mutableStateOf(false) }
    var everStarted by remember { mutableStateOf(false) }
    var progress by remember { mutableIntStateOf(0) }
    var fontScale by remember { mutableFloatStateOf(1f) }
    var floatingWindow by remember { mutableStateOf(false) }
    var cleared by remember { mutableStateOf(false) }
    var retryDone by remember { mutableStateOf(false) }
    var floatOffset by remember { mutableStateOf(Offset.Zero) }

    val totalUnits = allUnits.size
    val finished = !cleared && progress >= totalUnits && totalUnits > 0

    // Kata muncul bertahap selama "mendengarkan"
    LaunchedEffect(isListening, cleared) {
        while (isListening && !cleared && progress < totalUnits) {
            delay(280)
            progress++
        }
        if (progress >= totalUnits) {
            isListening = false
        }
    }

    val status = when {
        cleared -> CaptionStatus.SIAP
        isListening -> CaptionStatus.LISTENING
        finished -> CaptionStatus.SELESAI
        everStarted -> CaptionStatus.STOPPED
        else -> CaptionStatus.SIAP
    }

    val visibleSegments = remember(progress, cleared, segments) {
        if (cleared) {
            emptyList()
        } else {
            segments.filter { seg -> unitsBySegment[seg.id].orEmpty().any { it.index < progress } }
        }
    }

    val activeSegmentId = remember(progress) { allUnits.getOrNull(progress)?.segmentId }

    val listState = rememberLazyListState()
    LaunchedEffect(progress, visibleSegments.size) {
        if (isListening && activeSegmentId != null) {
            val targetIndex = visibleSegments.indexOfFirst { it.id == activeSegmentId }
            if (targetIndex >= 0) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Live Captions & Transkripsi",
                subtitle = "Transkrip bergulir & diarisasi — Simulasi",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LabSectionTitle(
                    title = "Teks Langsung",
                    subtitle = "Sorotan kata aktif & pembicara",
                    modifier = Modifier.weight(1f),
                )
                SimulasiBadge()
            }

            LabIntroCard(
                title = "Transkrip waktu nyata dari percakapan",
                body = "Kata aktif disorot, pembicara dipisah warna plus label teks, dan suara non-ucapan diberi penanda. Mikrofon dan mesin pengenal suara tidak diakses — seluruh transkrip adalah data tiruan (Simulasi).",
            )

            // Kontrol
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
                            text = "Ukuran teks ${(fontScale * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = palette.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SmallActionButton(
                                label = "A−",
                                enabled = fontScale > 0.75f,
                                onClick = { fontScale = (fontScale - 0.15f).coerceAtLeast(0.75f) },
                                contentDesc = "Perkecil teks transkrip",
                            )
                            SmallActionButton(
                                label = "A+",
                                enabled = fontScale < 1.95f,
                                onClick = { fontScale = (fontScale + 0.15f).coerceAtMost(1.95f) },
                                contentDesc = "Perbesar teks transkrip",
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SmallActionButton(
                            label = if (floatingWindow) "Jendela mengambang: Aktif" else "Jendela mengambang: Nonaktif",
                            enabled = true,
                            onClick = {
                                floatingWindow = !floatingWindow
                                floatOffset = Offset.Zero
                            },
                            modifier = Modifier.weight(1f),
                        )
                        SmallActionButton(
                            label = "Kosongkan",
                            enabled = everStarted || progress > 0,
                            onClick = {
                                cleared = true
                                isListening = false
                                progress = 0
                            },
                            modifier = Modifier.weight(1f),
                        )
                        SmallActionButton(
                            label = "Muat ulang",
                            enabled = true,
                            onClick = {
                                cleared = false
                                retryDone = false
                                progress = 0
                                isListening = false
                                everStarted = false
                                floatOffset = Offset.Zero
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SpeakerLegend(tag = SpeakerTag.NARASUMBER, color = palette.primary)
                        SpeakerLegend(tag = SpeakerTag.PEWAWANCARA, color = palette.info)
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when {
                    cleared -> EmptyTranscriptPanel(
                        onLoad = {
                            cleared = false
                            retryDone = false
                            progress = 0
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                    visibleSegments.isEmpty() && !isListening -> WaitingPanel(modifier = Modifier.fillMaxSize())
                    else -> TranscriptPanel(
                        segments = visibleSegments,
                        unitsBySegment = unitsBySegment,
                        progress = progress,
                        fontScale = fontScale,
                        retryDone = retryDone,
                        onRetry = { retryDone = true },
                        listState = listState,
                        floating = floatingWindow,
                        onDragOffset = { floatOffset += it },
                        modifier = if (floatingWindow) {
                            Modifier
                                .align(Alignment.Center)
                                .offset { IntOffset(floatOffset.x.roundToInt(), floatOffset.y.roundToInt()) }
                                .fillMaxWidth(0.86f)
                                .height(260.dp)
                        } else {
                            Modifier.fillMaxSize()
                        },
                    )
                }

                if (floatingWindow && !cleared) {
                    Text(
                        text = "Mode jendela mengambang (Simulasi) — geser jendela transkrip untuk memindahkannya",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                    )
                }
            }

            // Bar aksi bawah
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatusPill(status = status)
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        onClick = {
                            if (isListening) {
                                isListening = false
                            } else {
                                if (finished || cleared) {
                                    cleared = false
                                    progress = 0
                                    retryDone = false
                                }
                                isListening = true
                                everStarted = true
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isListening) palette.danger.copy(alpha = 0.12f) else palette.primary,
                        border = BorderStroke(1.dp, if (isListening) palette.danger else palette.primary),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = if (isListening) "Berhenti" else "Mulai Mendengarkan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if (isListening) palette.danger else palette.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        )
                    }
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
                        text = "Catatan pola Live Captions:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "• Kata aktif disorot lalu mengendap — kontras tetap terbaca pada mode terang & gelap",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        text = "• Pembicara ditandai warna + label teks; suara non-ucapan memakai penanda [kurung siku]",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        text = "• Simulasi: tanpa mikrofon, tanpa pengenal suara, tanpa penyimpanan rekaman",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun TranscriptPanel(
    segments: List<CaptionSegment>,
    unitsBySegment: Map<String, List<CaptionUnit>>,
    progress: Int,
    fontScale: Float,
    retryDone: Boolean,
    onRetry: () -> Unit,
    listState: LazyListState,
    floating: Boolean,
    onDragOffset: (Offset) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, if (floating) palette.primary else palette.border),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .pointerInput(floating) {
                if (floating) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onDragOffset(dragAmount)
                    }
                }
            },
    ) {
        if (floating) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(palette.border),
                )
                Text(
                    text = "Jendela mengambang",
                    fontSize = 10.sp,
                    color = palette.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(segments, key = { it.id }) { seg ->
                SegmentBlock(
                    segment = seg,
                    units = unitsBySegment[seg.id].orEmpty(),
                    progress = progress,
                    fontScale = fontScale,
                    retryDone = retryDone,
                    onRetry = onRetry,
                )
            }
            item(key = "tail") {
                Text(
                    text = "… akhir panel transkrip — Simulasi",
                    fontSize = 10.sp,
                    color = palette.textMuted,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SegmentBlock(
    segment: CaptionSegment,
    units: List<CaptionUnit>,
    progress: Int,
    fontScale: Float,
    retryDone: Boolean,
    onRetry: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val speakerColor: Color = when (segment.speaker) {
        SpeakerTag.NARASUMBER -> palette.primary
        SpeakerTag.PEWAWANCARA -> palette.info
    }
    val shownUnits = remember(units, progress) { units.filter { it.index < progress } }
    if (shownUnits.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(speakerColor),
            )
            Text(
                text = segment.speaker.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = speakerColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = segment.time + " · " + segment.kind.label,
                fontSize = 10.sp,
                color = palette.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        when (segment.kind) {
            CaptionKind.NON_SPEECH -> NotePill(
                note = segment.note ?: "…",
                kindLabel = CaptionKind.NON_SPEECH.label,
                tone = NoteTone.INFO,
            )
            CaptionKind.GALAT -> {
                NotePill(
                    note = segment.note ?: "…",
                    kindLabel = if (retryDone) "Segmen diulang (Simulasi)" else CaptionKind.GALAT.label,
                    tone = NoteTone.DANGER,
                )
                if (!retryDone) {
                    SmallActionButton(
                        label = "Coba lagi",
                        enabled = true,
                        onClick = onRetry,
                    )
                }
            }
            CaptionKind.UCAPAN -> FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                shownUnits.forEach { unit ->
                    val active = unit.index == progress - 1
                    WordChip(
                        text = unit.text,
                        active = active,
                        fontSizeSp = 15f * fontScale,
                    )
                }
            }
        }
    }
}

private enum class NoteTone { INFO, DANGER }

@Composable
private fun WordChip(
    text: String,
    active: Boolean,
    fontSizeSp: Float,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (active) palette.primaryContainer else palette.surfaceMuted,
        border = BorderStroke(1.dp, if (active) palette.primary else palette.border),
        modifier = modifier,
    ) {
        Text(
            text = text,
            fontSize = fontSizeSp.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            color = if (active) palette.onPrimaryContainer else palette.textPrimary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun NotePill(
    note: String,
    kindLabel: String,
    tone: NoteTone,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    val accent = if (tone == NoteTone.DANGER) palette.danger else palette.info
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = accent.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, accent),
        ) {
            Text(
                text = note,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = accent,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            )
        }
        Text(
            text = kindLabel,
            fontSize = 10.sp,
            color = palette.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SpeakerLegend(
    tag: SpeakerTag,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Row(
        modifier = modifier.semantics {
            contentDescription = "Pembicara ${tag.label} ditandai label teks"
        },
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = tag.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textSecondary,
        )
    }
}

@Composable
private fun StatusPill(
    status: CaptionStatus,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    val container = when (status) {
        CaptionStatus.SIAP -> palette.surfaceMuted
        CaptionStatus.LISTENING -> palette.danger.copy(alpha = 0.12f)
        CaptionStatus.STOPPED -> palette.warning.copy(alpha = 0.15f)
        CaptionStatus.SELESAI -> palette.success.copy(alpha = 0.15f)
    }
    val content = when (status) {
        CaptionStatus.SIAP -> palette.textSecondary
        CaptionStatus.LISTENING -> palette.danger
        CaptionStatus.STOPPED -> palette.warning
        CaptionStatus.SELESAI -> palette.success
    }
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = container,
        border = BorderStroke(1.dp, content),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (status == CaptionStatus.LISTENING) {
                PulsingDot(color = content)
            }
            Text(
                text = status.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PulsingDot(
    color: Color,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha)),
    )
}

@Composable
private fun WaitingPanel(modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surface)
            .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "Menunggu ucapan…",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
            )
            Text(
                text = "Tekan “Mulai Mendengarkan” untuk memutar transkrip tiruan",
                fontSize = 11.sp,
                color = palette.textMuted,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun EmptyTranscriptPanel(
    onLoad: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surface)
            .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Tidak ada transkrip",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
            )
            Text(
                text = "Panel dikosongkan. Muat contoh percakapan untuk mengisi ulang.",
                fontSize = 11.sp,
                color = palette.textMuted,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            SmallActionButton(
                label = "Muat contoh transkrip",
                enabled = true,
                onClick = onLoad,
            )
        }
    }
}

@Composable
private fun SmallActionButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDesc: String? = null,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = palette.surfaceMuted,
        border = BorderStroke(1.dp, palette.border),
        modifier = modifier.then(
            if (contentDesc != null) {
                Modifier.semantics { contentDescription = contentDesc }
            } else {
                Modifier
            },
        ),
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) palette.textPrimary else palette.textMuted,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun SimulasiBadge(modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = palette.info.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, palette.info),
        modifier = modifier,
    ) {
        Text(
            text = "Simulasi",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = palette.info,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}
