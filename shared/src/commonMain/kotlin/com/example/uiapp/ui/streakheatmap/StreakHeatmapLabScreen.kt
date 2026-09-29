package com.example.uiapp.ui.streakheatmap

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlin.random.Random

private const val HEATMAP_WEEKS = 18
private const val DAYS_IN_WEEK = 7

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StreakHeatmapLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var streakCount by remember { mutableIntStateOf(48) }
    var streakProtected by remember { mutableStateOf(true) }
    var selectedCellInfo by remember { mutableStateOf<String?>("Ketuk salah satu kotak heatmap untuk info") }

    // Heatmap grid (7 rows x 18 cols)
    val activityLevels = remember {
        mutableStateListOf<Int>().apply {
            val random = Random(42)
            repeat(DAYS_IN_WEEK * HEATMAP_WEEKS) {
                val r = random.nextFloat()
                val level = when {
                    r < 0.35f -> 0
                    r < 0.60f -> 1
                    r < 0.80f -> 2
                    r < 0.93f -> 3
                    else -> 4
                }
                add(level)
            }
        }
    }

    val dayLabels = listOf("S", "S", "R", "K", "J", "S", "M")
    val currentWeekDays = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

    fun getHeatmapColor(level: Int): Color {
        return when (level) {
            1 -> palette.success.copy(alpha = 0.30f)
            2 -> palette.success.copy(alpha = 0.55f)
            3 -> palette.success.copy(alpha = 0.80f)
            4 -> palette.success
            else -> palette.surfaceMuted
        }
    }

    fun incrementTodayActivity() {
        val todayIdx = activityLevels.lastIndex
        if (todayIdx >= 0) {
            val cur = activityLevels[todayIdx]
            activityLevels[todayIdx] = (cur + 1).coerceAtMost(4)
            streakCount++
            selectedCellInfo = "Aktivitas hari ini ditambahkan! Level saat ini: ${activityLevels[todayIdx]}"
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Streak & Activity Heatmap",
                subtitle = "Duolingo Flame & GitHub Heatmap Grid",
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Flame Streak Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(palette.warning.copy(alpha = 0.15f))
                                    .border(1.dp, palette.warning, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("🔥", fontSize = 28.sp)
                            }

                            Column {
                                Text("$streakCount Hari", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = palette.textPrimary)
                                Text("Streak Belajar Aktif", fontSize = 12.sp, color = palette.textMuted)
                            }
                        }

                        if (streakProtected) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = palette.info.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, palette.info),
                            ) {
                                Text(
                                    text = "🧊 Beku Aktif",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.info,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 7-Day Current Week Flame Circles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        currentWeekDays.forEachIndexed { idx, day ->
                            val isCompleted = idx < 5
                            val isToday = idx == 4

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isCompleted) palette.warning.copy(alpha = 0.2f) else palette.surfaceMuted,
                                        )
                                        .border(
                                            BorderStroke(
                                                if (isToday) 2.dp else 1.dp,
                                                if (isToday) palette.warning else if (isCompleted) palette.warning else palette.border,
                                            ),
                                            CircleShape,
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        if (isCompleted) "🔥" else "•",
                                        fontSize = if (isCompleted) 16.sp else 12.sp,
                                    )
                                }
                                Text(day, fontSize = 11.sp, color = if (isToday) palette.textPrimary else palette.textMuted, fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }

            // GitHub Contribution Heatmap Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Heatmap Kontribusi 18 Minggu", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                        Text("524 Total", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.primary)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Scrollable Horizontal Grid of Cells
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        // Day labels column
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(end = 4.dp),
                        ) {
                            dayLabels.forEach { label ->
                                Box(
                                    modifier = Modifier.size(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(label, fontSize = 9.sp, color = palette.textMuted)
                                }
                            }
                        }

                        // 18 Weeks Columns
                        for (w in 0 until HEATMAP_WEEKS) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                for (d in 0 until DAYS_IN_WEEK) {
                                    val idx = w * DAYS_IN_WEEK + d
                                    val lvl = if (idx < activityLevels.size) activityLevels[idx] else 0

                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(getHeatmapColor(lvl))
                                            .border(BorderStroke(0.5.dp, palette.border), RoundedCornerShape(3.dp))
                                            .clickable {
                                                selectedCellInfo = "Minggu ke-${w + 1}, Hari ${currentWeekDays[d]}: Level Aktivitas $lvl"
                                            },
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Heatmap Legend & Selection Banner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = selectedCellInfo ?: "",
                            fontSize = 11.sp,
                            color = palette.textSecondary,
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Sedikit", fontSize = 10.sp, color = palette.textMuted)
                            (0..4).forEach { lvl ->
                                Box(
                                    modifier = Modifier
                                        .size(11.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(getHeatmapColor(lvl))
                                        .border(BorderStroke(0.5.dp, palette.border), RoundedCornerShape(2.dp)),
                                )
                            }
                            Text("Banyak", fontSize = 10.sp, color = palette.textMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        onClick = { incrementTodayActivity() },
                        shape = RoundedCornerShape(10.dp),
                        color = palette.primary,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "+ Tambah Aktivitas Hari Ini ⚡",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp),
                        )
                    }
                }
            }

            // Specs
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Manfaat Pola Visual Streak & Heatmap:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Gamifikasi & Retensi Tinggi: Efek komitmen psikologis mendorong pengguna membuka aplikasi setiap hari", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Performa Grid Ringan: Rendering cell terkomposisi tanpa library chart eksternal", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Zero Shadow Flat UI: Konsistensi border 1px dan warna tema otomatis", fontSize = 12.sp, color = palette.textSecondary)
                }
            }
        }
    }
}
