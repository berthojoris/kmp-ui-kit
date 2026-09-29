package com.example.uiapp.ui.datatable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.AppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private enum class TxStatus(val label: String) {
    LUNAS("Lunas"),
    TERTUNDA("Tertunda"),
    GAGAL("Gagal"),
}

private data class TxRow(
    val id: String,
    val name: String,
    val date: String,
    val amount: Long,
    val status: TxStatus,
)

private enum class SortColumn(val label: String) {
    NAMA("Nama Pelanggan"),
    TANGGAL("Tanggal"),
    JUMLAH("Jumlah"),
    STATUS("Status"),
}

private val TxRows = listOf(
    TxRow("TRX-0141", "Ayu Lestari", "28 Sep 2026", 1250000, TxStatus.LUNAS),
    TxRow("TRX-0140", "Budi Santoso", "28 Sep 2026", 480000, TxStatus.TERTUNDA),
    TxRow("TRX-0139", "Citra Maharani", "27 Sep 2026", 2350000, TxStatus.LUNAS),
    TxRow("TRX-0138", "Dedi Kurniawan", "27 Sep 2026", 175000, TxStatus.GAGAL),
    TxRow("TRX-0137", "Eka Putri", "26 Sep 2026", 920000, TxStatus.LUNAS),
    TxRow("TRX-0136", "Fajar Nugroho", "26 Sep 2026", 3100000, TxStatus.TERTUNDA),
    TxRow("TRX-0135", "Gita Savitri", "25 Sep 2026", 640000, TxStatus.LUNAS),
    TxRow("TRX-0134", "Hendra Wijaya", "25 Sep 2026", 150000, TxStatus.GAGAL),
    TxRow("TRX-0133", "Intan Permata", "24 Sep 2026", 1980000, TxStatus.LUNAS),
    TxRow("TRX-0132", "Joko Prasetyo", "24 Sep 2026", 750000, TxStatus.TERTUNDA),
)

private fun formatNominal(amount: Long): String {
    val digits = amount.toString()
    val builder = StringBuilder()
    digits.reversed().forEachIndexed { index, c ->
        if (index > 0 && index % 3 == 0) builder.append('.')
        builder.append(c)
    }
    return "Rp " + builder.reverse().toString()
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DataTableLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var sortColumn by remember { mutableStateOf(SortColumn.TANGGAL) }
    var ascending by remember { mutableStateOf(false) }
    var showEmpty by remember { mutableStateOf(false) }

    val sourceRows = if (showEmpty) emptyList() else TxRows
    val sortedRows = remember(sourceRows, sortColumn, ascending) {
        val comparator: Comparator<TxRow> = when (sortColumn) {
            SortColumn.NAMA -> compareBy { it.name }
            SortColumn.TANGGAL -> compareBy { it.id }
            SortColumn.JUMLAH -> compareBy { it.amount }
            SortColumn.STATUS -> compareBy { it.status.ordinal }
        }
        sourceRows.sortedWith(if (ascending) comparator else comparator.reversed())
    }

    val paidTotal = remember(sourceRows) {
        sourceRows.filter { it.status == TxStatus.LUNAS }.sumOf { it.amount }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Data Table Pro",
                subtitle = "Sticky column, sorting & status",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Intro + toggle state kosong
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .padding(14.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Tabel Transaksi Profesional",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "Kolom ID lengket di kiri; geser horizontal untuk kolom lainnya. Ketuk header untuk mengurutkan naik/turun. Status dibedakan dengan label teks, bukan warna saja.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            onClick = { showEmpty = !showEmpty },
                            shape = RoundedCornerShape(10.dp),
                            color = if (showEmpty) palette.primaryContainer else palette.surface,
                            border = BorderStroke(1.dp, if (showEmpty) palette.primary else palette.border),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)) {
                                Text(
                                    text = if (showEmpty) "State: Kosong" else "State: Terisi",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (showEmpty) palette.onPrimaryContainer else palette.textSecondary,
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(palette.surface)
                                .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                        ) {
                            Text(
                                text = "${sourceRows.size} transaksi",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textMuted,
                            )
                        }
                    }
                }
            }

            if (sortedRows.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                        .padding(vertical = 40.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Belum Ada Transaksi",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "Data tabel sedang kosong. Muat ulang untuk menampilkan contoh transaksi lokal.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = palette.textSecondary,
                    )
                    Surface(
                        onClick = { showEmpty = false },
                        shape = RoundedCornerShape(12.dp),
                        color = palette.primary,
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp)) {
                            Text(
                                text = "Muat Ulang Data",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.onPrimary,
                            )
                        }
                    }
                }
            } else {
                // Tabel: kolom ID sticky + kolom lain scroll horizontal
                val tableScroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(14.dp)),
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Kolom sticky: ID
                        Column(modifier = Modifier.width(104.dp)) {
                            TableHeaderCell(
                                label = "ID",
                                isHeader = true,
                                background = palette.surfaceMuted,
                            )
                            sortedRows.forEachIndexed { index, row ->
                                TableTextCell(
                                    text = row.id,
                                    bold = true,
                                    zebra = index % 2 == 1,
                                )
                            }
                        }
                        // Pembatas vertikal sticky
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(44.dp * (sortedRows.size + 1))
                                .background(palette.border),
                        )
                        // Kolom scrollable
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .horizontalScroll(tableScroll),
                        ) {
                            Row {
                                SortableHeaderCell(
                                    column = SortColumn.NAMA,
                                    activeColumn = sortColumn,
                                    ascending = ascending,
                                    width = 170.dp,
                                    onClick = {
                                        if (sortColumn == SortColumn.NAMA) ascending = !ascending
                                        else {
                                            sortColumn = SortColumn.NAMA
                                            ascending = true
                                        }
                                    },
                                )
                                SortableHeaderCell(
                                    column = SortColumn.TANGGAL,
                                    activeColumn = sortColumn,
                                    ascending = ascending,
                                    width = 120.dp,
                                    onClick = {
                                        if (sortColumn == SortColumn.TANGGAL) ascending = !ascending
                                        else {
                                            sortColumn = SortColumn.TANGGAL
                                            ascending = true
                                        }
                                    },
                                )
                                SortableHeaderCell(
                                    column = SortColumn.JUMLAH,
                                    activeColumn = sortColumn,
                                    ascending = ascending,
                                    width = 130.dp,
                                    onClick = {
                                        if (sortColumn == SortColumn.JUMLAH) ascending = !ascending
                                        else {
                                            sortColumn = SortColumn.JUMLAH
                                            ascending = true
                                        }
                                    },
                                )
                                SortableHeaderCell(
                                    column = SortColumn.STATUS,
                                    activeColumn = sortColumn,
                                    ascending = ascending,
                                    width = 130.dp,
                                    onClick = {
                                        if (sortColumn == SortColumn.STATUS) ascending = !ascending
                                        else {
                                            sortColumn = SortColumn.STATUS
                                            ascending = true
                                        }
                                    },
                                )
                            }
                            sortedRows.forEachIndexed { index, row ->
                                Row {
                                    TableValueCell(text = row.name, width = 170.dp, zebra = index % 2 == 1)
                                    TableValueCell(text = row.date, width = 120.dp, zebra = index % 2 == 1)
                                    TableValueCell(text = formatNominal(row.amount), width = 130.dp, zebra = index % 2 == 1, bold = true)
                                    Box(
                                        modifier = Modifier
                                            .width(130.dp)
                                            .height(44.dp)
                                            .background(if (index % 2 == 1) palette.surfaceMuted else palette.surface)
                                            .padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.CenterStart,
                                    ) {
                                        StatusPill(status = row.status, palette = palette)
                                    }
                                }
                            }
                        }
                    }
                }

                // Footer ringkasan
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.surface, RoundedCornerShape(12.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "Total Terbayar (Lunas)",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                            )
                            Text(
                                text = formatNominal(paidTotal),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TxStatus.entries.forEach { status ->
                                val count = sourceRows.count { it.status == status }
                                StatusPill(status = status, palette = palette, suffix = " $count")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableHeaderCell(
    label: String,
    isHeader: Boolean,
    background: Color,
) {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(background)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textSecondary,
            maxLines = 1,
        )
    }
}

@Composable
private fun TableTextCell(
    text: String,
    bold: Boolean,
    zebra: Boolean,
) {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(if (zebra) palette.surfaceMuted else palette.surface)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
            color = palette.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SortableHeaderCell(
    column: SortColumn,
    activeColumn: SortColumn,
    ascending: Boolean,
    width: Dp,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val active = column == activeColumn
    Box(
        modifier = Modifier
            .width(width)
            .height(44.dp)
            .background(if (active) palette.primaryContainer else palette.surfaceMuted)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = column.label + if (active) (if (ascending) " ▲" else " ▼") else "",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (active) palette.onPrimaryContainer else palette.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun TableValueCell(
    text: String,
    width: Dp,
    zebra: Boolean,
    bold: Boolean = false,
) {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .width(width)
            .height(44.dp)
            .background(if (zebra) palette.surfaceMuted else palette.surface)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
            color = palette.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun StatusPill(
    status: TxStatus,
    palette: AppPalette,
    suffix: String = "",
) {
    val color = when (status) {
        TxStatus.LUNAS -> palette.success
        TxStatus.TERTUNDA -> palette.warning
        TxStatus.GAGAL -> palette.danger
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = status.label + suffix,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1,
        )
    }
}
