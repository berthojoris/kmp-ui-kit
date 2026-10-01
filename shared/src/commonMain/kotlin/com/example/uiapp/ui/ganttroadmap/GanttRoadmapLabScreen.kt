package com.example.uiapp.ui.ganttroadmap

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private data class GanttTask(
    val id: Int,
    val name: String,
    val section: String,
    val startWeek: Int,
    val endWeek: Int,
    val progress: Float,
    val isMilestone: Boolean = false,
)

private val Tasks = listOf(
    GanttTask(1, "Wireframe", "Desain", 1, 2, 1f),
    GanttTask(2, "UI Mockup", "Desain", 2, 4, 0.8f),
    GanttTask(3, "Prototipe", "Desain", 4, 5, 0.5f),
    GanttTask(0, "Review Desain", "Desain", 5, 5, 0f, isMilestone = true),
    GanttTask(4, "Frontend", "Pengembangan", 4, 7, 0.6f),
    GanttTask(5, "Backend", "Pengembangan", 3, 7, 0.7f),
    GanttTask(6, "API Integrasi", "Pengembangan", 6, 8, 0.3f),
    GanttTask(7, "Unit Test", "Pengujian", 7, 9, 0.2f),
    GanttTask(8, "UAT", "Pengujian", 9, 10, 0f),
    GanttTask(9, "Bug Fix", "Pengujian", 10, 11, 0f),
    GanttTask(10, "Staging", "Peluncuran", 11, 11, 0f),
    GanttTask(11, "Rilis Produksi", "Peluncuran", 12, 12, 0f),
    GanttTask(12, "Go-Live", "Peluncuran", 12, 12, 0f, isMilestone = true),
)

private const val TotalWeeks = 12
private val WeekWidth = 56.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GanttRoadmapLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    val scrollState = rememberScrollState()

    val overallProgress = remember {
        val nonMilestone = Tasks.filter { !it.isMilestone }
        if (nonMilestone.isEmpty()) 0f else nonMilestone.sumOf { it.progress.toDouble() }.toFloat() / nonMilestone.size
    }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Gantt Roadmap", subtitle = "Timeline proyek interaktif", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Summary bar
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp, tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Progres Keseluruhan", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                        Text("${(overallProgress * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                    }
                    Spacer(Modifier.height(6.dp))
                    Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(palette.surfaceMuted)) {
                        Box(Modifier.fillMaxWidth(overallProgress).height(6.dp).clip(RoundedCornerShape(3.dp)).background(palette.primary))
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("1 Okt — 24 Des 2026", fontSize = 11.sp, color = palette.textMuted)
                }
            }

            // Gantt chart
            Row(modifier = Modifier.weight(1f)) {
                // Left: Task names
                LazyColumn(
                    modifier = Modifier.width(120.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 30.dp),
                ) {
                    var lastSection = ""
                    itemsIndexed(Tasks, key = { _, t -> t.id * 100 + t.startWeek }) { _, task ->
                        if (task.section != lastSection) {
                            lastSection = task.section
                            Text(task.section, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.primary, modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 2.dp))
                        }
                        Box(modifier = Modifier.height(32.dp).padding(start = 12.dp), contentAlignment = Alignment.CenterStart) {
                            Text(
                                text = if (task.isMilestone) "◆ ${task.name}" else task.name,
                                fontSize = 11.sp,
                                fontWeight = if (task.isMilestone) FontWeight.Bold else FontWeight.Normal,
                                color = if (task.isMilestone) palette.warning else palette.textPrimary,
                                maxLines = 1, overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }

                // Right: Timeline
                Box(modifier = Modifier.weight(1f).horizontalScroll(scrollState)) {
                    Column {
                        // Week headers
                        Row(modifier = Modifier.height(28.dp)) {
                            for (w in 1..TotalWeeks) {
                                Box(modifier = Modifier.width(WeekWidth).height(28.dp), contentAlignment = Alignment.Center) {
                                    Text("M$w", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = palette.textMuted)
                                }
                            }
                        }
                        // Bars
                        LazyColumn(contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 2.dp)) {
                            var lastSection = ""
                            itemsIndexed(Tasks, key = { _, t -> t.id * 100 + t.startWeek }) { _, task ->
                                if (task.section != lastSection) {
                                    lastSection = task.section
                                    Spacer(Modifier.height(18.dp))
                                }
                                Box(modifier = Modifier.height(32.dp).width(WeekWidth * TotalWeeks)) {
                                    // Grid lines
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        for (w in 0..TotalWeeks) {
                                            val x = w * (size.width / TotalWeeks)
                                            drawLine(palette.border.copy(alpha = 0.3f), Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                                        }
                                    }
                                    // Bar
                                    if (task.isMilestone) {
                                        val cx = ((task.startWeek - 0.5f) / TotalWeeks) * (WeekWidth * TotalWeeks).value
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val diamondSize = 10.dp.toPx()
                                            val path = Path().apply {
                                                moveTo(cx.dp.toPx(), size.height / 2 - diamondSize)
                                                lineTo(cx.dp.toPx() + diamondSize, size.height / 2)
                                                lineTo(cx.dp.toPx(), size.height / 2 + diamondSize)
                                                lineTo(cx.dp.toPx() - diamondSize, size.height / 2)
                                                close()
                                            }
                                            drawPath(path, palette.warning, style = Fill)
                                        }
                                    } else {
                                        val startX = ((task.startWeek - 1f) / TotalWeeks)
                                        val barWidth = ((task.endWeek - task.startWeek + 1f) / TotalWeeks)
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val x = startX * size.width
                                            val w = barWidth * size.width
                                            // Background bar
                                            drawRoundRect(palette.surfaceMuted, Offset(x, size.height * 0.3f), Size(w, size.height * 0.4f), CornerRadius(4.dp.toPx()))
                                            // Progress fill
                                            if (task.progress > 0) {
                                                val fillColor = if (task.progress >= 1f) palette.success else palette.primary
                                                drawRoundRect(fillColor, Offset(x, size.height * 0.3f), Size(w * task.progress, size.height * 0.4f), CornerRadius(4.dp.toPx()))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Legend
            Surface(color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    LegendItem("Selesai", palette.success)
                    LegendItem("Berjalan", palette.primary)
                    LegendItem("Belum", palette.surfaceMuted)
                    LegendItem("Milestone", palette.warning)
                }
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(color))
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, color = LocalAppPalette.current.textSecondary)
    }
}
