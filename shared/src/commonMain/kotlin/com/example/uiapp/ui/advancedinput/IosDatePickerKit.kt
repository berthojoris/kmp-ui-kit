package com.example.uiapp.ui.advancedinput

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.AppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.icons.MgIosBackChevron
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Tanggal mock "hari ini" untuk seluruh demo picker. Lab ini offline dan tidak
 * membaca jam perangkat, jadi nilai ini adalah simulasi yang jelas.
 */
private val MockToday = CalDate(2026, 9, 29)

private val WheelItemHeight = 34.dp
private val WheelHeight = WheelItemHeight * 5

private val YearStart = 2020
private val YearCount = 16

private val StripDayCount = 60
private val StripDaysBeforeToday = 7

private val CalendarPageCount = 37
private val CalendarAnchorPage = 18

private enum class CalendarMode(val label: String) {
    SINGLE("Satu Tanggal"),
    RANGE("Rentang"),
}

private fun twoDigits(value: Int): String = value.toString().padStart(2, '0')

// ---------- 1 & 2. Wheel picker (iOS) ----------

/**
 * Satu kolom roda bergaya iOS: daftar yang di-snap per baris, band seleksi 2 garis
 * 1px, dan item yang jauh dari tengah diredupkan tanpa recomposition per frame.
 */
@Composable
private fun WheelColumn(
    itemCount: Int,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    label: (Int) -> String,
    palette: AppPalette,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val safeSelected = selectedIndex.coerceIn(0, itemCount - 1)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = safeSelected)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    var lastNotified by remember { mutableIntStateOf(safeSelected) }

    // Dibaca hanya di graphicsLayer/effect sehingga scroll tidak memicu recomposition.
    val centerIndexState = remember(listState) {
        derivedStateOf {
            val info = listState.layoutInfo
            val center = info.viewportSize.height / 2
            var best = 0
            var bestDistance = Int.MAX_VALUE
            for (item in info.visibleItemsInfo) {
                val distance = abs(item.offset + item.size / 2 - center)
                if (distance < bestDistance) {
                    bestDistance = distance
                    best = item.index
                }
            }
            best
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { centerIndexState.value }
            .distinctUntilChanged()
            .collect { index ->
                if (index != lastNotified) {
                    lastNotified = index
                    onSelectedIndexChange(index)
                }
            }
    }

    // Perubahan nilai dari luar roda (mis. dari strip atau kalender) menggeser roda.
    LaunchedEffect(safeSelected) {
        if (safeSelected != lastNotified) {
            lastNotified = safeSelected
            listState.animateScrollToItem(safeSelected)
        }
    }

    Box(
        modifier = modifier
            .height(WheelHeight)
            .semantics {
                this.contentDescription = contentDescription
                progressBarRangeInfo = ProgressBarRangeInfo(
                    current = safeSelected.toFloat(),
                    range = 0f..(itemCount - 1).toFloat(),
                    steps = (itemCount - 2).coerceAtLeast(0),
                )
                setProgress { target ->
                    val index = target.roundToInt().coerceIn(0, itemCount - 1)
                    onSelectedIndexChange(index)
                    true
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = (WheelHeight - WheelItemHeight) / 2),
        ) {
            items(itemCount, key = { it }) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(WheelItemHeight)
                        .graphicsLayer {
                            val distance = abs(index - centerIndexState.value)
                            alpha = when (distance) {
                                0 -> 1f
                                1 -> 0.7f
                                2 -> 0.42f
                                else -> 0.22f
                            }
                            val scale = if (distance == 0) 1f else 0.92f
                            scaleX = scale
                            scaleY = scale
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label(index),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = palette.textPrimary,
                        maxLines = 1,
                    )
                }
            }
        }

        // Band seleksi: dua garis 1px, zero shadow.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(WheelItemHeight),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(palette.border),
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(palette.border),
            )
        }
    }
}

@Composable
private fun IosWheelDatePicker(
    selected: CalDate,
    onSelectedChange: (CalDate) -> Unit,
    palette: AppPalette,
) {
    val daysInMonth = CalendarMath.daysInMonth(selected.year, selected.month)
    val dayIndex = (selected.day - 1).coerceIn(0, daysInMonth - 1)
    val monthIndex = (selected.month - 1).coerceIn(0, 11)
    val yearIndex = (selected.year - YearStart).coerceIn(0, YearCount - 1)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WheelColumn(
            itemCount = daysInMonth,
            selectedIndex = dayIndex,
            onSelectedIndexChange = { index ->
                onSelectedChange(selected.copy(day = index + 1))
            },
            label = { twoDigits(it + 1) },
            palette = palette,
            contentDescription = "Kolom tanggal",
            modifier = Modifier.weight(0.8f),
        )
        WheelColumn(
            itemCount = 12,
            selectedIndex = monthIndex,
            onSelectedIndexChange = { index ->
                val month = index + 1
                onSelectedChange(
                    selected.copy(
                        month = month,
                        day = selected.day.coerceAtMost(CalendarMath.daysInMonth(selected.year, month)),
                    ),
                )
            },
            label = { CalendarMath.monthShort[it] },
            palette = palette,
            contentDescription = "Kolom bulan",
            modifier = Modifier.weight(1.1f),
        )
        WheelColumn(
            itemCount = YearCount,
            selectedIndex = yearIndex,
            onSelectedIndexChange = { index ->
                val year = YearStart + index
                onSelectedChange(
                    selected.copy(
                        year = year,
                        day = selected.day.coerceAtMost(CalendarMath.daysInMonth(year, selected.month)),
                    ),
                )
            },
            label = { (YearStart + it).toString() },
            palette = palette,
            contentDescription = "Kolom tahun",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun IosWheelTimePicker(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    palette: AppPalette,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WheelColumn(
            itemCount = 24,
            selectedIndex = hour.coerceIn(0, 23),
            onSelectedIndexChange = onHourChange,
            label = { twoDigits(it) },
            palette = palette,
            contentDescription = "Kolom jam",
            modifier = Modifier.weight(1f),
        )
        Text(
            text = ":",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textMuted,
        )
        WheelColumn(
            itemCount = 60,
            selectedIndex = minute.coerceIn(0, 59),
            onSelectedIndexChange = onMinuteChange,
            label = { twoDigits(it) },
            palette = palette,
            contentDescription = "Kolom menit",
            modifier = Modifier.weight(1f),
        )
    }
}

// ---------- 3. Horizontal date strip ----------

@Composable
private fun IosHorizontalDateStrip(
    selected: CalDate,
    today: CalDate,
    onSelect: (CalDate) -> Unit,
    palette: AppPalette,
) {
    val dates = remember(today) {
        val start = CalendarMath.plusDays(today, -StripDaysBeforeToday.toLong())
        (0 until StripDayCount).map { CalendarMath.plusDays(start, it.toLong()) }
    }
    val listState = rememberLazyListState()
    val selectedIndex = remember(selected, dates) {
        dates.indexOfFirst { it == selected }.coerceAtLeast(0)
    }

    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem((selectedIndex - 1).coerceAtLeast(0))
    }

    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(dates, key = { "${it.year}-${it.month}-${it.day}" }) { date ->
            val isSelected = date == selected
            val isToday = date == today
            // Minggu = hari libur, ditandai merah seperti kebiasaan kalender Indonesia.
            val isSunday = CalendarMath.weekdayIndex(date) == 6

            Surface(
                onClick = { onSelect(date) },
                modifier = Modifier
                    .width(62.dp)
                    .semantics {
                        contentDescription = buildString {
                            append(CalendarMath.formatLong(date))
                            if (isSunday) append(", hari Minggu")
                            if (isToday) append(", hari ini")
                            if (isSelected) append(", terpilih")
                        }
                    },
                shape = RoundedCornerShape(14.dp),
                color = when {
                    isSunday -> palette.danger
                    isSelected -> palette.primary
                    else -> palette.surface
                },
                border = when {
                    // Minggu terpilih tetap merah; penanda pilihannya berupa ring putih 2px.
                    isSunday && isSelected -> BorderStroke(2.dp, Color.White)
                    isSunday -> BorderStroke(1.dp, palette.danger)
                    isSelected -> BorderStroke(1.dp, palette.primary)
                    else -> BorderStroke(1.dp, palette.border)
                },
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                val labelColor = when {
                    isSunday -> Color.White
                    isSelected -> palette.onPrimary
                    else -> palette.textSecondary
                }
                val dayColor = when {
                    isSunday -> Color.White
                    isSelected -> palette.onPrimary
                    else -> palette.textPrimary
                }
                val monthColor = when {
                    isSunday -> Color.White
                    isSelected -> palette.onPrimary
                    isToday -> palette.primary
                    else -> palette.textMuted
                }

                Column(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = CalendarMath.weekdayShort[CalendarMath.weekdayIndex(date)],
                        fontSize = 10.sp,
                        fontWeight = if (isSunday || isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = labelColor,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date.day.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = dayColor,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = CalendarMath.monthShort[date.month - 1],
                        fontSize = 10.sp,
                        color = monthColor,
                        fontWeight = if (isToday || isSunday) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

// ---------- 4. Inline graphical calendar ----------

@Composable
private fun IosInlineCalendar(
    mode: CalendarMode,
    selected: CalDate,
    rangeStart: CalDate?,
    rangeEnd: CalDate?,
    today: CalDate,
    onSelect: (CalDate) -> Unit,
    onTodayClick: () -> Unit,
    palette: AppPalette,
) {
    val scope = rememberCoroutineScope()
    val anchor = remember { today }
    val pagerState = rememberPagerState(initialPage = CalendarAnchorPage) { CalendarPageCount }
    val targetPage = CalendarAnchorPage + CalendarMath.monthsBetween(anchor, selected)

    LaunchedEffect(targetPage) {
        if (targetPage in 0 until CalendarPageCount && pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    val shownMonth = CalendarMath.plusMonths(anchor.year, anchor.month, pagerState.currentPage - CalendarAnchorPage)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MonthNavButton(
                label = "Bulan sebelumnya",
                flipped = false,
                palette = palette,
                onClick = {
                    if (pagerState.currentPage > 0) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    }
                },
            )
            Text(
                text = CalendarMath.formatMonthYear(shownMonth.first, shownMonth.second),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            MonthNavButton(
                label = "Bulan berikutnya",
                flipped = true,
                palette = palette,
                onClick = {
                    if (pagerState.currentPage < CalendarPageCount - 1) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            CalendarMath.weekdayShort.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textMuted,
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
        ) { page ->
            val month = CalendarMath.plusMonths(anchor.year, anchor.month, page - CalendarAnchorPage)
            MonthGrid(
                year = month.first,
                month = month.second,
                mode = mode,
                selected = selected,
                rangeStart = rangeStart,
                rangeEnd = rangeEnd,
                today = today,
                onSelect = onSelect,
                palette = palette,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                onClick = onTodayClick,
                shape = RoundedCornerShape(10.dp),
                color = palette.surfaceMuted,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Text(
                    text = "Hari Ini",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                )
            }
            Text(
                text = selectionSummary(mode, selected, rangeStart, rangeEnd),
                fontSize = 11.sp,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun selectionSummary(
    mode: CalendarMode,
    selected: CalDate,
    rangeStart: CalDate?,
    rangeEnd: CalDate?,
): String = when {
    mode == CalendarMode.SINGLE -> CalendarMath.formatShort(selected)
    rangeStart == null -> "Pilih tanggal mulai"
    rangeEnd == null -> "Mulai ${CalendarMath.formatShort(rangeStart)} · pilih tanggal selesai"
    else -> {
        val nights = CalendarMath.daysBetween(rangeStart, rangeEnd)
        "${CalendarMath.formatShort(rangeStart)} – ${CalendarMath.formatShort(rangeEnd)} · $nights malam"
    }
}

@Composable
private fun MonthNavButton(
    label: String,
    flipped: Boolean,
    palette: AppPalette,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(10.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .semantics {
                role = Role.Button
                contentDescription = label
            },
        contentAlignment = Alignment.Center,
    ) {
        MgIosBackChevron(
            modifier = if (flipped) Modifier.graphicsLayer { rotationZ = 180f } else Modifier,
            size = 14.dp,
            strokeWidth = 2.2.dp,
            tint = palette.textPrimary,
        )
    }
}

@Composable
private fun MonthGrid(
    year: Int,
    month: Int,
    mode: CalendarMode,
    selected: CalDate,
    rangeStart: CalDate?,
    rangeEnd: CalDate?,
    today: CalDate,
    onSelect: (CalDate) -> Unit,
    palette: AppPalette,
) {
    val weeks = remember(year, month) { CalendarMath.monthGrid(year, month).chunked(7) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Kalender ${CalendarMath.formatMonthYear(year, month)}"
            },
    ) {
        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    val isSelected = when {
                        mode == CalendarMode.SINGLE -> date == selected
                        else -> date == rangeStart || date == rangeEnd
                    }
                    val isInRange = mode == CalendarMode.RANGE &&
                        rangeStart != null && rangeEnd != null &&
                        date > rangeStart && date < rangeEnd
                    DayCell(
                        date = date,
                        inMonth = date.month == month,
                        isSelected = isSelected,
                        isToday = date == today,
                        showBand = isInRange || (mode == CalendarMode.RANGE && (date == rangeStart || date == rangeEnd)),
                        onClick = { onSelect(date) },
                        palette = palette,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: CalDate,
    inMonth: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    showBand: Boolean,
    onClick: () -> Unit,
    palette: AppPalette,
    modifier: Modifier = Modifier,
) {
    val bandColor = if (showBand) palette.primary.copy(alpha = 0.14f) else Color.Transparent
    val ringColor = if (isToday && !isSelected) palette.primary else Color.Transparent

    Box(
        modifier = modifier
            .height(40.dp)
            .background(bandColor)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics {
                role = Role.Button
                contentDescription = buildString {
                    append("${date.day} ${CalendarMath.monthNames[date.month - 1]} ${date.year}")
                    if (!inMonth) append(", bulan lain")
                    if (isToday) append(", hari ini")
                    if (isSelected) append(", terpilih")
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isSelected) palette.primary else Color.Transparent)
                .border(1.dp, ringColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = date.day.toString(),
                fontSize = 13.sp,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isSelected -> palette.onPrimary
                    !inMonth -> palette.textMuted
                    isToday -> palette.primary
                    else -> palette.textPrimary
                },
            )
        }
    }
}

// ---------- 5. Compact picker ----------

@Composable
private fun IosCompactDatePicker(
    selected: CalDate,
    today: CalDate,
    onSelect: (CalDate) -> Unit,
    palette: AppPalette,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                onClick = { expanded = !expanded },
                shape = RoundedCornerShape(10.dp),
                color = palette.surfaceMuted,
                border = BorderStroke(1.dp, if (expanded) palette.primary else palette.border),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CalendarGlyph(tint = palette.primary)
                    Text(
                        text = CalendarMath.formatShort(selected),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.primary,
                    )
                }
            }
            Text(
                text = if (expanded) "Tutup" else "Ubah tanggal",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textSecondary,
                modifier = Modifier.clickable { expanded = !expanded },
            )
        }

        AnimatedVisibility(visible = expanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .padding(10.dp),
            ) {
                IosInlineCalendar(
                    mode = CalendarMode.SINGLE,
                    selected = selected,
                    rangeStart = null,
                    rangeEnd = null,
                    today = today,
                    onSelect = {
                        onSelect(it)
                        expanded = false
                    },
                    onTodayClick = {
                        onSelect(today)
                        expanded = false
                    },
                    palette = palette,
                )
            }
        }
    }
}

@Composable
private fun CalendarGlyph(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    tint: Color,
) {
    Canvas(modifier = modifier.size(size)) {
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        val bodyWidth = this.size.width * 0.84f
        val bodyHeight = this.size.height * 0.78f
        val left = (this.size.width - bodyWidth) / 2f
        val top = this.size.height - bodyHeight
        drawRoundRect(
            color = tint,
            topLeft = Offset(left, top),
            size = Size(bodyWidth, bodyHeight),
            cornerRadius = CornerRadius(this.size.width * 0.16f),
            style = stroke,
        )
        drawLine(
            color = tint,
            start = Offset(left, top + bodyHeight * 0.32f),
            end = Offset(left + bodyWidth, top + bodyHeight * 0.32f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(this.size.width * 0.3f, top - this.size.height * 0.1f),
            end = Offset(this.size.width * 0.3f, top + this.size.height * 0.02f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(this.size.width * 0.7f, top - this.size.height * 0.1f),
            end = Offset(this.size.width * 0.7f, top + this.size.height * 0.02f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

// ---------- Section ----------

@Composable
internal fun IosDatePickerSection() {
    val palette = LocalAppPalette.current

    var wheelDate by remember { mutableStateOf(MockToday) }
    var wheelHour by remember { mutableIntStateOf(9) }
    var wheelMinute by remember { mutableIntStateOf(30) }

    var stripDate by remember { mutableStateOf(MockToday) }
    var stripCalendarOpen by remember { mutableStateOf(false) }

    var calendarMode by remember { mutableStateOf(CalendarMode.SINGLE) }
    var singleDate by remember { mutableStateOf(MockToday) }
    var rangeStart by remember { mutableStateOf<CalDate?>(null) }
    var rangeEnd by remember { mutableStateOf<CalDate?>(null) }

    var compactDate by remember { mutableStateOf(MockToday) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        LabCard {
            LabSectionTitle(
                title = "1. Wheel Date Picker (iOS)",
                subtitle = "Kolom tanggal, bulan, dan tahun dengan snapping serta band seleksi",
            )
            IosWheelDatePicker(
                selected = wheelDate,
                onSelectedChange = { wheelDate = it },
                palette = palette,
            )
            Text(
                text = "Terpilih: ${CalendarMath.formatLong(wheelDate)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.primary,
            )
        }

        LabCard {
            LabSectionTitle(
                title = "2. Wheel Time Picker (iOS)",
                subtitle = "Jam dan menit diputar dan di-snap per baris, bukan tombol +/-",
            )
            IosWheelTimePicker(
                hour = wheelHour,
                minute = wheelMinute,
                onHourChange = { wheelHour = it },
                onMinuteChange = { wheelMinute = it },
                palette = palette,
            )
            Text(
                text = "Pukul ${twoDigits(wheelHour)}:${twoDigits(wheelMinute)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.primary,
            )
        }

        // Strip dibiarkan full-bleed: hanya judul yang diberi padding horizontal.
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LabSectionTitle(
                    title = "3. Horizontal Date Strip",
                    subtitle = "Scroll tanggal dua arah dengan kalender penuh",
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = if (stripCalendarOpen) "Tutup Kalender" else "Kalender",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { stripCalendarOpen = !stripCalendarOpen }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }

            IosHorizontalDateStrip(
                selected = stripDate,
                today = MockToday,
                onSelect = { stripDate = it },
                palette = palette,
            )

            AnimatedVisibility(visible = stripCalendarOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                ) {
                    IosInlineCalendar(
                        mode = CalendarMode.SINGLE,
                        selected = stripDate,
                        rangeStart = null,
                        rangeEnd = null,
                        today = MockToday,
                        onSelect = { stripDate = it },
                        onTodayClick = { stripDate = MockToday },
                        palette = palette,
                    )
                }
            }
        }

        LabCard {
            LabSectionTitle(
                title = "4. Inline Graphical Calendar",
                subtitle = "Geser antar bulan, tombol Hari Ini, dan mode rentang tanggal",
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalendarMode.entries.forEach { mode ->
                    val isSelected = calendarMode == mode
                    Surface(
                        onClick = { calendarMode = mode },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) palette.primary else palette.surfaceMuted,
                        border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Text(
                            text = mode.label,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) palette.onPrimary else palette.textPrimary,
                        )
                    }
                }
            }
            IosInlineCalendar(
                mode = calendarMode,
                selected = if (calendarMode == CalendarMode.SINGLE) singleDate else (rangeEnd ?: rangeStart ?: MockToday),
                rangeStart = rangeStart,
                rangeEnd = rangeEnd,
                today = MockToday,
                onSelect = { date ->
                    if (calendarMode == CalendarMode.SINGLE) {
                        singleDate = date
                    } else {
                        val start = rangeStart
                        when {
                            start == null || rangeEnd != null -> {
                                rangeStart = date
                                rangeEnd = null
                            }
                            date < start -> {
                                rangeStart = date
                                rangeEnd = start
                            }
                            else -> rangeEnd = date
                        }
                    }
                },
                onTodayClick = {
                    if (calendarMode == CalendarMode.SINGLE) {
                        singleDate = MockToday
                    } else {
                        rangeStart = MockToday
                        rangeEnd = null
                    }
                },
                palette = palette,
            )
            Text(
                text = if (calendarMode == CalendarMode.SINGLE) {
                    "Terpilih: ${CalendarMath.formatLong(singleDate)}"
                } else {
                    when {
                        rangeStart == null -> "Belum ada rentang dipilih"
                        rangeEnd == null -> "Mulai ${CalendarMath.formatShort(rangeStart!!)} · pilih tanggal selesai"
                        else -> "Rentang: ${CalendarMath.formatShort(rangeStart!!)} – ${CalendarMath.formatShort(rangeEnd!!)}"
                    }
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        LabCard {
            LabSectionTitle(
                title = "5. Compact Picker (Popover)",
                subtitle = "Tombol tanggal ringkas yang membuka kalender, lalu menutup otomatis",
            )
            IosCompactDatePicker(
                selected = compactDate,
                today = MockToday,
                onSelect = { compactDate = it },
                palette = palette,
            )
            Text(
                text = "Terpilih: ${CalendarMath.formatLong(compactDate)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.primary,
            )
        }
    }
}
