package com.example.uiapp.ui.pilltoolbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private data class NoteParagraph(
    val id: Int,
    val text: String,
    val highlighted: Boolean = false,
    val marker: Int = 0,
)

private val NoteSeed = listOf(
    NoteParagraph(1, "Sprint 15 fokus menuntaskan alur pemesanan ulang tamu langganan, termasuk penyimpanan preferensi kamar dan catatan khusus dari kunjungan sebelumnya."),
    NoteParagraph(2, "Tim desain menyetujui komponen pill toolbar melayang sebagai pola standar aksi cepat pada layar editor catatan internal villa."),
    NoteParagraph(3, "Backend memastikan sinkronisasi catatan tamu berjalan dua arah; konflik penulisan diselesaikan dengan stempel waktu terbaru menang."),
    NoteParagraph(4, "Catatan risiko: durasi uji coba di perangkat k low-end masih melebihi target dua ratus milidetik untuk pembukaan layar."),
    NoteParagraph(5, "Keputusan rilis: fitur penyimpanan draft ditunda ke sprint berikutnya agar fokus kualitas tetap pada stabilisasi pencarian."),
    NoteParagraph(6, "Tindak lanjut: demo alur lengkap dijadwalkan Kamis depan bersama tim operasional dan resepsionis lapangan."),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PillToolbarScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current
    val paragraphs = remember { mutableStateListOf<NoteParagraph>().apply { addAll(NoteSeed) } }
    var selectedId by remember { mutableIntStateOf(NoteSeed.first().id) }
    var moreOpen by remember { mutableStateOf(false) }
    var sortedAz by remember { mutableStateOf(false) }
    var nextId by remember { mutableIntStateOf(NoteSeed.size + 1) }

    BackHandler(enabled = true) {
        if (moreOpen) moreOpen = false else onBack()
    }

    val displayList by remember {
        derivedStateOf { if (sortedAz) paragraphs.sortedBy { it.text } else paragraphs.toList() }
    }
    val markerColors = remember(palette) {
        listOf(palette.primary, palette.info, palette.warning, palette.danger)
    }
    val markerNames = remember { listOf("hijau", "biru", "kuning", "merah") }
    val selected = displayList.firstOrNull { it.id == selectedId }
    val selectedMarkerColor = markerColors[(selected?.marker ?: 0).coerceIn(0, markerColors.lastIndex)]

    fun mutateSelected(transform: (NoteParagraph) -> NoteParagraph) {
        val index = paragraphs.indexOfFirst { it.id == selectedId }
        if (index >= 0) paragraphs[index] = transform(paragraphs[index])
    }

    Box(modifier = Modifier.fillMaxSize().background(palette.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 170.dp),
        ) {
            Text(
                text = "Catatan Rapat Sprint 15",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
            )
            Text(
                text = "Diperbarui hari ini · 6 paragraf · sinkron",
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 12.sp,
                color = palette.textMuted,
            )
            Text(
                text = "Ketuk paragraf untuk memilihnya, lalu gunakan toolbar di bawah.",
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
                fontSize = 12.sp,
                color = palette.textSecondary,
            )
            displayList.forEach { paragraph ->
                ParagraphBlock(
                    paragraph = paragraph,
                    selected = paragraph.id == selectedId,
                    markerColor = markerColors[paragraph.marker.coerceIn(0, markerColors.lastIndex)],
                    onClick = { selectedId = paragraph.id },
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
        }
        ToolbarPill(
            moreOpen = moreOpen,
            sortedAz = sortedAz,
            highlightActive = selected?.highlighted == true,
            markerColor = selectedMarkerColor,
            markerName = markerNames[(selected?.marker ?: 0).coerceIn(0, markerNames.lastIndex)],
            onSort = { sortedAz = !sortedAz },
            onMark = { mutateSelected { it.copy(highlighted = !it.highlighted) } },
            onColor = { mutateSelected { it.copy(marker = (it.marker + 1) % markerColors.size) } },
            onInsert = {
                val index = paragraphs.indexOfFirst { it.id == selectedId }
                val at = if (index >= 0) index + 1 else paragraphs.size
                paragraphs.add(at, NoteParagraph(nextId, "Paragraf baru: tuliskan hasil keputusan tindak lanjut di sini sebelum direviews pimpinan rapat."))
                selectedId = nextId
                nextId += 1
            },
            onMore = { moreOpen = !moreOpen },
            onDuplicate = {
                val index = paragraphs.indexOfFirst { it.id == selectedId }
                if (index >= 0) {
                    paragraphs.add(index + 1, paragraphs[index].copy(id = nextId, text = paragraphs[index].text + " (salinan)"))
                    selectedId = nextId
                    nextId += 1
                }
            },
            onClearMarks = {
                for (i in paragraphs.indices) {
                    if (paragraphs[i].highlighted) paragraphs[i] = paragraphs[i].copy(highlighted = false)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun ParagraphBlock(
    paragraph: NoteParagraph,
    selected: Boolean,
    markerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (paragraph.highlighted) markerColor.copy(alpha = 0.10f) else palette.surface,
        border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AnimatedVisibility(visible = paragraph.highlighted) {
                Box(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .width(3.dp)
                        .heightIn(min = 20.dp)
                        .clip(CircleShape)
                        .background(markerColor),
                )
            }
            Text(
                text = paragraph.text,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = palette.textPrimary,
                modifier = Modifier.semantics {
                    stateDescription = if (paragraph.highlighted) "Ditandai" else "Tanpa tanda"
                    if (selected) contentDescription = "Paragraf terpilih"
                },
            )
        }
    }
}

@Composable
private fun ToolbarPill(
    moreOpen: Boolean,
    sortedAz: Boolean,
    highlightActive: Boolean,
    markerColor: Color,
    markerName: String,
    onSort: () -> Unit,
    onMark: () -> Unit,
    onColor: () -> Unit,
    onInsert: () -> Unit,
    onMore: () -> Unit,
    onDuplicate: () -> Unit,
    onClearMarks: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    val chevronRotation by animateFloatAsState(
        targetValue = if (moreOpen) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pillMoreChevron",
    )
    Surface(
        modifier = modifier
            .navigationBarsPadding()
            .padding(bottom = 12.dp)
            .animateContentSize(spring(stiffness = Spring.StiffnessMediumLow)),
        shape = RoundedCornerShape(percent = 50),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ToolButton(
                label = "Urutkan paragraf",
                stateDescription = if (sortedAz) "Urutan A sampai Z aktif" else "Urutan asli",
                onClick = onSort,
            ) { ToolbarSortIcon(tint = palette.textSecondary) }
            ToolButton(
                label = "Tandai paragraf terpilih",
                stateDescription = if (highlightActive) "Penanda aktif" else "Penanda mati",
                onClick = onMark,
            ) { ToolbarBookmarkIcon(tint = if (highlightActive) palette.primary else palette.textSecondary) }
            ToolButton(
                label = "Ganti warna penanda",
                stateDescription = "Warna saat ini $markerName",
                onClick = onColor,
            ) { ToolbarSwatchIcon(tint = markerColor) }
            ToolButton(label = "Sisipkan paragraf baru", onClick = onInsert) {
                ToolbarPlusIcon(tint = palette.textSecondary)
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .width(1.dp)
                    .height(22.dp)
                    .background(palette.border),
            )
            ToolButton(
                label = if (moreOpen) "Sembunyikan aksi lain" else "Lihat aksi lain",
                onClick = onMore,
            ) {
                Box(modifier = Modifier.rotate(chevronRotation)) {
                    ToolbarChevronIcon(tint = palette.textSecondary)
                }
            }
            AnimatedVisibility(
                visible = moreOpen,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally(),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ToolbarTextAction(text = "Salin", onClick = onDuplicate)
                    ToolbarTextAction(text = "Bersihkan", onClick = onClearMarks)
                }
            }
        }
    }
}

@Composable
private fun ToolButton(
    label: String,
    onClick: () -> Unit,
    stateDescription: String? = null,
    icon: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClickLabel = label) { onClick() }
            .semantics {
                contentDescription = label
                if (stateDescription != null) this.stateDescription = stateDescription
            },
        contentAlignment = Alignment.Center,
    ) {
        icon()
    }
}

@Composable
private fun ToolbarTextAction(text: String, onClick: () -> Unit) {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .sizeIn(minHeight = 48.dp)
            .clip(RoundedCornerShape(percent = 50))
            .clickable(role = Role.Button, onClickLabel = text) { onClick() }
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ToolbarSortIcon(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.7f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // batang kiri + panah atas
        drawLine(tint, Offset(0.3f * w, 0.2f * h), Offset(0.3f * w, 0.8f * h), stroke.width, stroke.cap)
        drawLine(tint, Offset(0.18f * w, 0.32f * h), Offset(0.3f * w, 0.2f * h), stroke.width, stroke.cap)
        drawLine(tint, Offset(0.42f * w, 0.32f * h), Offset(0.3f * w, 0.2f * h), stroke.width, stroke.cap)
        // batang kanan + panah bawah
        drawLine(tint, Offset(0.7f * w, 0.2f * h), Offset(0.7f * w, 0.8f * h), stroke.width, stroke.cap)
        drawLine(tint, Offset(0.58f * w, 0.68f * h), Offset(0.7f * w, 0.8f * h), stroke.width, stroke.cap)
        drawLine(tint, Offset(0.82f * w, 0.68f * h), Offset(0.7f * w, 0.8f * h), stroke.width, stroke.cap)
    }
}

@Composable
private fun ToolbarBookmarkIcon(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val body = Path().apply {
            moveTo(0.26f * w, 0.1f * h)
            lineTo(0.74f * w, 0.1f * h)
            lineTo(0.74f * w, 0.9f * h)
            lineTo(0.5f * w, 0.68f * h)
            lineTo(0.26f * w, 0.9f * h)
            close()
        }
        drawPath(body, tint, style = Stroke(width = 1.7f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun ToolbarSwatchIcon(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        drawCircle(
            tint,
            radius = 0.38f * w,
            center = Offset(0.5f * w, 0.5f * h),
            style = Stroke(width = 1.7f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        drawCircle(tint, radius = 0.16f * w, center = Offset(0.5f * w, 0.5f * h))
    }
}

@Composable
private fun ToolbarPlusIcon(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.7f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawLine(tint, Offset(0.5f * w, 0.16f * h), Offset(0.5f * w, 0.84f * h), stroke.width, stroke.cap)
        drawLine(tint, Offset(0.16f * w, 0.5f * h), Offset(0.84f * w, 0.5f * h), stroke.width, stroke.cap)
    }
}

@Composable
private fun ToolbarChevronIcon(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val chevron = Path().apply {
            moveTo(0.36f * w, 0.24f * h)
            lineTo(0.66f * w, 0.5f * h)
            lineTo(0.36f * w, 0.76f * h)
        }
        drawPath(chevron, tint, style = Stroke(width = 1.7f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
