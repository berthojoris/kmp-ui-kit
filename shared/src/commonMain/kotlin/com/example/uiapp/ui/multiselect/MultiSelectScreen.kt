package com.example.uiapp.ui.multiselect

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private data class MailItem(
    val id: Int,
    val sender: String,
    val subject: String,
    val preview: String,
    val tag: String,
    val tagColor: Color,
    val tagBackground: Color,
)

private val Inbox = listOf(
    MailItem(1, "Ayu Prameswari", "Konfirmasi reservasi villa", "Terima kasih, pembayaran sudah kami terima dan jadwal terkunci.", "BARU", Color(0xFF3B82F6), Color(0xFFEFF6FF)),
    MailItem(2, "Bagas Nugroho", "Revisi itinerary Bali", "Saya sudah sesuaikan hari ketiga menjadi tur kopi di Kintamani.", "BARU", Color(0xFF3B82F6), Color(0xFFEFF6FF)),
    MailItem(3, "Citra Halim", "Invoice #4821", "Mohon cek lampiran invoice untuk pemesanan 4 tamu bulan depan.", "TAGIHAN", Color(0xFFF59E0B), Color(0xFFFFF7ED)),
    MailItem(4, "Damar Wibowo", "Permintaan sarapan", "Bisa tambahkan paket sarapan untuk seluruh tamu di unit 12?", "PERMINTAAN", Color(0xFF0A332C), Color(0xFFE8F5E9)),
    MailItem(5, "Eka Saputra", "Ulasan bintang lima", "Pengalaman menginap yang luar biasa, kolamnya bersih sekali.", "ULASAN", Color(0xFF10B981), Color(0xFFE8F5E9)),
    MailItem(6, "Fajar Ramadhan", "Jadwal pemeliharaan", "Unit 19 dijadwalkan pengecatan ulang Selasa pagi.", "INTERNAL", Color(0xFF6B7280), Color(0xFFF3F4F6)),
    MailItem(7, "Gita Lestari", "Ketersediaan Agustus", "Apakah masih ada slot untuk tanggal 12-15 Agustus?", "PERMINTAAN", Color(0xFF0A332C), Color(0xFFE8F5E9)),
    MailItem(8, "Hana Pertiwi", "Kontrak mitra korporat", "Berikut draft kontrak untuk kerja sama tahun depan.", "TAGIHAN", Color(0xFFF59E0B), Color(0xFFFFF7ED)),
)

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun MultiSelectScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    var editMode by remember { mutableStateOf(false) }
    val selected = remember { mutableStateListOf<Int>() }

    fun exitEditMode() {
        editMode = false
        selected.clear()
    }

    BackHandler(enabled = true) {
        if (editMode) exitEditMode() else onBack()
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = if (editMode) "${selected.size} dipilih" else "Kotak Masuk",
                subtitle = if (editMode) "Pilih tindakan massal" else "Multi-select \u00B7 bulk action",
                onBack = { if (editMode) exitEditMode() else onBack() },
                action = {
                    Text(
                        text = if (editMode) "Batal" else "Pilih",
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                if (editMode) {
                                    exitEditMode()
                                } else {
                                    editMode = true
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.primary,
                    )
                },
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = editMode,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
            ) {
                BulkActionBar(
                    count = selected.size,
                    allSelected = selected.size == Inbox.size,
                    onSelectAll = {
                        if (selected.size == Inbox.size) {
                            selected.clear()
                        } else {
                            selected.clear()
                            selected.addAll(Inbox.map { it.id })
                        }
                    },
                    onArchive = { exitEditMode() },
                    onDelete = { exitEditMode() },
                    onShare = { exitEditMode() },
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(Inbox, key = { it.id }) { item ->
                val isSelected = item.id in selected
                MailRow(
                    item = item,
                    editMode = editMode,
                    selected = isSelected,
                    onClick = {
                        if (editMode) {
                            if (isSelected) selected.remove(item.id) else selected.add(item.id)
                        }
                    },
                    onLongClick = {
                        editMode = true
                        if (item.id !in selected) selected.add(item.id)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MailRow(
    item: MailItem,
    editMode: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) palette.primaryContainer else palette.surface,
        border = BorderStroke(
            1.dp,
            if (selected) palette.primary else palette.border,
        ),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(visible = editMode) {
                Row {
                    SelectIndicator(selected = selected)
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(palette.surfaceMuted),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item.sender.take(1),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.sender,
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = item.tag,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(item.tagBackground)
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = item.tagColor,
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.subject,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = palette.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.preview,
                    fontSize = 11.sp,
                    color = palette.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun SelectIndicator(selected: Boolean) {
    val palette = LocalAppPalette.current

    Canvas(modifier = Modifier.size(22.dp)) {
        val radius = size.minDimension / 2f
        if (selected) {
            drawCircle(color = palette.primary, radius = radius)
            val check = Path().apply {
                moveTo(size.width * 0.28f, size.height * 0.52f)
                lineTo(size.width * 0.44f, size.height * 0.68f)
                lineTo(size.width * 0.74f, size.height * 0.34f)
            }
            drawPath(
                path = check,
                color = Color.White,
                style = Stroke(
                    width = 2.2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        } else {
            drawCircle(
                color = palette.border,
                radius = radius - 0.8.dp.toPx(),
                style = Stroke(width = 1.6.dp.toPx()),
            )
        }
    }
}

@Composable
private fun BulkActionBar(
    count: Int,
    allSelected: Boolean,
    onSelectAll: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.surface)
            .navigationBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.border),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BulkAction(
                label = if (allSelected) "Batal semua" else "Pilih semua",
                glyph = BulkGlyph.CHECK,
                enabled = true,
                onClick = onSelectAll,
                modifier = Modifier.weight(1f),
            )
            BulkAction(
                label = "Arsip",
                glyph = BulkGlyph.ARCHIVE,
                enabled = count > 0,
                onClick = onArchive,
                modifier = Modifier.weight(1f),
            )
            BulkAction(
                label = "Bagikan",
                glyph = BulkGlyph.SHARE,
                enabled = count > 0,
                onClick = onShare,
                modifier = Modifier.weight(1f),
            )
            BulkAction(
                label = "Hapus",
                glyph = BulkGlyph.DELETE,
                enabled = count > 0,
                danger = true,
                onClick = onDelete,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private enum class BulkGlyph { CHECK, ARCHIVE, SHARE, DELETE }

@Composable
private fun BulkAction(
    label: String,
    glyph: BulkGlyph,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    danger: Boolean = false,
) {
    val palette = LocalAppPalette.current

    val tint = when {
        !enabled -> palette.textMuted.copy(alpha = 0.5f)
        danger -> palette.danger
        else -> palette.textPrimary
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(modifier = Modifier.size(22.dp)) {
            val w = size.width
            val h = size.height
            val stroke = Stroke(
                width = 1.7.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            )
            when (glyph) {
                BulkGlyph.ARCHIVE -> {
                    drawLine(tint, Offset(w * 0.14f, h * 0.34f), Offset(w * 0.86f, h * 0.34f), 1.7.dp.toPx(), StrokeCap.Round)
                    drawRoundRect(
                        color = tint,
                        topLeft = Offset(w * 0.18f, h * 0.42f),
                        size = Size(w * 0.64f, h * 0.42f),
                        cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
                        style = stroke,
                    )
                    drawLine(tint, Offset(w * 0.42f, h * 0.58f), Offset(w * 0.58f, h * 0.58f), 1.7.dp.toPx(), StrokeCap.Round)
                }

                BulkGlyph.DELETE -> {
                    drawLine(tint, Offset(w * 0.22f, h * 0.30f), Offset(w * 0.78f, h * 0.30f), 1.7.dp.toPx(), StrokeCap.Round)
                    drawLine(tint, Offset(w * 0.40f, h * 0.18f), Offset(w * 0.60f, h * 0.18f), 1.7.dp.toPx(), StrokeCap.Round)
                    val can = Path().apply {
                        moveTo(w * 0.30f, h * 0.38f)
                        lineTo(w * 0.34f, h * 0.84f)
                        lineTo(w * 0.66f, h * 0.84f)
                        lineTo(w * 0.70f, h * 0.38f)
                    }
                    drawPath(can, tint, style = stroke)
                    drawLine(tint.copy(alpha = 0.6f), Offset(w * 0.44f, h * 0.48f), Offset(w * 0.45f, h * 0.72f), 1.4.dp.toPx(), StrokeCap.Round)
                    drawLine(tint.copy(alpha = 0.6f), Offset(w * 0.56f, h * 0.48f), Offset(w * 0.55f, h * 0.72f), 1.4.dp.toPx(), StrokeCap.Round)
                }

                BulkGlyph.SHARE -> {
                    drawLine(tint, Offset(w * 0.5f, h * 0.14f), Offset(w * 0.5f, h * 0.62f), 1.7.dp.toPx(), StrokeCap.Round)
                    val arrow = Path().apply {
                        moveTo(w * 0.34f, h * 0.30f)
                        lineTo(w * 0.5f, h * 0.14f)
                        lineTo(w * 0.66f, h * 0.30f)
                    }
                    drawPath(arrow, tint, style = stroke)
                    val tray = Path().apply {
                        moveTo(w * 0.22f, h * 0.50f)
                        lineTo(w * 0.22f, h * 0.86f)
                        lineTo(w * 0.78f, h * 0.86f)
                        lineTo(w * 0.78f, h * 0.50f)
                    }
                    drawPath(tray, tint, style = stroke)
                }

                BulkGlyph.CHECK -> {
                    drawCircle(
                        color = tint,
                        radius = w * 0.42f,
                        style = Stroke(width = 1.7.dp.toPx()),
                    )
                    val check = Path().apply {
                        moveTo(w * 0.32f, h * 0.52f)
                        lineTo(w * 0.46f, h * 0.66f)
                        lineTo(w * 0.70f, h * 0.36f)
                    }
                    drawPath(check, tint, style = stroke)
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
