package com.example.uiapp.ui.datacards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private enum class CardSubTab(val title: String) {
    CALENDAR("Kalender"),
    KANBAN("Kanban"),
    PRICING("Pricing"),
    REVIEWS("Review"),
    ORDER_TRACK("Order"),
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DataCardsLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var activeSubTab by remember { mutableStateOf(CardSubTab.CALENDAR) }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Kartu & Data",
                subtitle = "Kalender, Kanban, Pricing, Review, Order",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface)
                    .border(BorderStroke(1.dp, palette.border))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CardSubTab.entries.forEach { tab ->
                    val isSelected = activeSubTab == tab
                    Surface(
                        onClick = { activeSubTab = tab },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) palette.primary else palette.surfaceMuted,
                        border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                    ) {
                        Text(
                            text = tab.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else palette.textPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (activeSubTab) {
                    CardSubTab.CALENDAR -> MonthCalendarView(palette)
                    CardSubTab.KANBAN -> KanbanBoardView(palette)
                    CardSubTab.PRICING -> PricingTableView(palette)
                    CardSubTab.REVIEWS -> RatingReviewView(palette)
                    CardSubTab.ORDER_TRACK -> OrderTrackingStepperView(palette)
                }
            }
        }
    }
}

@Composable
private fun MonthCalendarView(palette: com.example.uiapp.theme.AppPalette) {
    var selectedDay by remember { mutableIntStateOf(16) }
    val daysInMonth = (1..31).toList()
    val eventDays = mapOf(
        5 to listOf(palette.primary),
        12 to listOf(palette.warning, palette.info),
        16 to listOf(palette.primary, palette.danger),
        24 to listOf(palette.success),
        28 to listOf(palette.info),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("September 2026", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
                    Text("Pilih Tanggal", fontSize = 12.sp, color = palette.primary, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(14.dp))

                // Days of week header
                val weekdays = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    weekdays.forEach { wd ->
                        Text(text = wd, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.textMuted)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Calendar Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    userScrollEnabled = false,
                ) {
                    items(daysInMonth) { day ->
                        val isSelected = selectedDay == day
                        val dots = eventDays[day] ?: emptyList()
                        Column(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) palette.primary else Color.Transparent)
                                .clickable { selectedDay = day }
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = day.toString(),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else palette.textPrimary,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                dots.forEach { color ->
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White else color),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Date Summary Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Agenda: $selectedDay September 2026",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = palette.textPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (eventDays.containsKey(selectedDay)) {
                    Text("• 09:00 AM - Sprint Planning KMP Multiplatform", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• 02:00 PM - Evaluasi Performa Flat UI & Compose", fontSize = 12.sp, color = palette.textSecondary)
                } else {
                    Text("Tidak ada jadwal agenda untuk tanggal ini.", fontSize = 12.sp, color = palette.textMuted)
                }
            }
        }
    }
}

private data class KanbanTask(val id: Int, val title: String, var column: String)

@Composable
private fun KanbanBoardView(palette: com.example.uiapp.theme.AppPalette) {
    val tasks = remember {
        mutableStateListOf(
            KanbanTask(1, "Riset iOS Skiko Vector", "TODO"),
            KanbanTask(2, "Optimasi Flat UI Modals", "PROGRESS"),
            KanbanTask(3, "Unit Test State Flow", "PROGRESS"),
            KanbanTask(4, "Audit Keystore Security", "DONE"),
        )
    }

    val columns = listOf("TODO" to "Rencana", "PROGRESS" to "Dikerjakan", "DONE" to "Selesai")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Multi-Column Kanban Board", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
        Text("Pindahkan status tugas antar kolom dengan tap tombol status", fontSize = 12.sp, color = palette.textMuted)

        columns.forEach { (colKey, colTitle) ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = colTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                        val count = tasks.count { it.column == colKey }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(palette.surfaceMuted)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        ) {
                            Text(text = "$count tugas", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.textSecondary)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    val colTasks = tasks.filter { it.column == colKey }
                    if (colTasks.isEmpty()) {
                        Text("Belum ada tugas di kolom ini.", fontSize = 11.sp, color = palette.textMuted)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            colTasks.forEach { task ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = palette.surfaceMuted,
                                    border = BorderStroke(1.dp, palette.border),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(text = task.title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = palette.textPrimary)
                                        Surface(
                                            onClick = {
                                                val nextCol = when (task.column) {
                                                    "TODO" -> "PROGRESS"
                                                    "PROGRESS" -> "DONE"
                                                    else -> "TODO"
                                                }
                                                val idx = tasks.indexOfFirst { it.id == task.id }
                                                if (idx >= 0) {
                                                    tasks[idx] = tasks[idx].copy(column = nextCol)
                                                }
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            color = palette.primary,
                                        ) {
                                            Text(
                                                text = if (task.column == "DONE") "Ulang" else "Lanjut →",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PricingTableView(palette: com.example.uiapp.theme.AppPalette) {
    var isAnnual by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Paket & Perbandingan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
                Text("Pilih skema berlangganan yang sesuai", fontSize = 12.sp, color = palette.textMuted)
            }
            Surface(
                onClick = { isAnnual = !isAnnual },
                shape = RoundedCornerShape(12.dp),
                color = palette.surfaceMuted,
                border = BorderStroke(1.dp, palette.border),
            ) {
                Text(
                    text = if (isAnnual) "Tahunan (Hemat 20%)" else "Bulanan",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
        }

        val plans = listOf(
            Triple("Starter", if (isAnnual) "Rp 79.000/bln" else "Rp 99.000/bln", listOf("1 Project Aktif", "Export Basic", "Support Komunitas")),
            Triple("Professional", if (isAnnual) "Rp 199.000/bln" else "Rp 249.000/bln", listOf("Project Tak Terbatas", "Full KMP Export", "Priority Support 24/7", "Cloud Sync")),
            Triple("Enterprise", if (isAnnual) "Rp 499.000/bln" else "Rp 599.000/bln", listOf("Custom SDK Integration", "Dedicated Engineer", "SLA 99.9%", "Keystore White-label")),
        )

        plans.forEachIndexed { index, (name, price, features) ->
            val isPro = index == 1
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isPro) palette.primaryContainer else palette.surface,
                border = BorderStroke(1.dp, if (isPro) palette.primary else palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (isPro) palette.onPrimaryContainer else palette.textPrimary)
                        if (isPro) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(palette.primary)
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                            ) {
                                Text("POPULER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(price, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = palette.primary)
                    Spacer(modifier = Modifier.height(10.dp))
                    features.forEach { feat ->
                        Text("✓ $feat", fontSize = 12.sp, color = if (isPro) palette.onPrimaryContainer else palette.textSecondary)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isPro) palette.primary else palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Pilih Paket $name",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPro) Color.White else palette.textPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingReviewView(palette: com.example.uiapp.theme.AppPalette) {
    var userRating by remember { mutableIntStateOf(5) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("4.9", fontWeight = FontWeight.Bold, fontSize = 38.sp, color = palette.textPrimary)
                Text("Berdasarkan 1.280 ulasan pengguna", fontSize = 12.sp, color = palette.textMuted)
                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Star Rating
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..5).forEach { star ->
                        Text(
                            text = if (star <= userRating) "★" else "☆",
                            fontSize = 28.sp,
                            color = if (star <= userRating) palette.warning else palette.textMuted,
                            modifier = Modifier.clickable { userRating = star },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Beri nilai Anda: $userRating dari 5 bintang",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.primary,
                )
            }
        }

        // Reviews list
        val sampleReviews = listOf(
            Triple("Ahmad Fauzi", "5 bintang", "Komponen sangat rapi, smooth 60fps tanpa lagging."),
            Triple("Dewi Sartika", "5 bintang", "Pola zero-shadow memberikan kesan elegan dan sangat profesional."),
            Triple("Kevin Wijaya", "4 bintang", "Sangat membantu proses prototyping mobile multiplatform."),
        )

        sampleReviews.forEach { (author, rating, reviewText) ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(author, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                        Text(rating, fontSize = 11.sp, color = palette.warning, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(reviewText, fontSize = 12.sp, color = palette.textSecondary)
                }
            }
        }
    }
}

@Composable
private fun OrderTrackingStepperView(palette: com.example.uiapp.theme.AppPalette) {
    var activeStep by remember { mutableIntStateOf(2) }
    val steps = listOf(
        "Pesanan Diterima" to "08:30 WIB - Sistem menerima pesanan Anda",
        "Diproses di Gudang" to "09:15 WIB - Produk sedang dikemas",
        "Dalam Pengiriman" to "11:00 WIB - Kurir menuju lokasi pengiriman",
        "Pesanan Terkirim" to "Menunggu konfirmasi penerimaan",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Order Tracking Timeline", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
                Text("Nomor Resi: #TRK-2026-99882", fontSize = 12.sp, color = palette.textMuted)
                Spacer(modifier = Modifier.height(16.dp))

                steps.forEachIndexed { index, (title, desc) ->
                    val isDone = index <= activeStep
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(28.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) palette.primary else palette.surfaceMuted)
                                    .border(BorderStroke(1.dp, if (isDone) palette.primary else palette.border), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isDone) {
                                    Text("✓", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (index < steps.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(44.dp)
                                        .background(if (index < activeStep) palette.primary else palette.border),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.padding(bottom = 14.dp)) {
                            Text(
                                text = title,
                                fontWeight = if (isDone) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isDone) palette.textPrimary else palette.textMuted,
                            )
                            Text(text = desc, fontSize = 11.sp, color = palette.textSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Surface(
                        onClick = { if (activeStep > 0) activeStep-- },
                        shape = RoundedCornerShape(10.dp),
                        color = palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier.weight(1f),
                        enabled = activeStep > 0,
                    ) {
                        Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                            Text("← Step Sebelumnya", fontSize = 12.sp, color = palette.textPrimary)
                        }
                    }
                    Surface(
                        onClick = { if (activeStep < steps.size - 1) activeStep++ },
                        shape = RoundedCornerShape(10.dp),
                        color = palette.primary,
                        modifier = Modifier.weight(1f),
                        enabled = activeStep < steps.size - 1,
                    ) {
                        Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                            Text("Step Selanjutnya →", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
