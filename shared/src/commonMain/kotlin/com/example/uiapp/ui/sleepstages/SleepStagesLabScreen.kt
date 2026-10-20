package com.example.uiapp.ui.sleepstages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.HapticKind
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar

private data class StageSample(val minute: Int, val stage: Int)
private data class Night(val id: Int, val label: String, val score: Int, val samples: List<StageSample>)

private val Nights = listOf(
    Night(1, "Semalam", 82, listOf(
        StageSample(0, 2), StageSample(40, 3), StageSample(90, 2), StageSample(130, 1),
        StageSample(175, 2), StageSample(220, 3), StageSample(260, 0), StageSample(300, 2), StageSample(360, 1),
    )),
    Night(2, "Kemarin", 74, listOf(
        StageSample(0, 1), StageSample(60, 2), StageSample(110, 3), StageSample(150, 2),
        StageSample(210, 1), StageSample(260, 2), StageSample(320, 0), StageSample(390, 1),
    )),
)

private val StageColors = listOf(Color(0xFFA78BFA), Color(0xFF60A5FA), Color(0xFF34D399), Color(0xFFF472B6))
private val StageNames = listOf("Tidur dalam", "Tidur ringan", "REM", "Terjaga")

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun SleepStagesLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var selected by remember { mutableIntStateOf(0) }
    var view by remember { mutableIntStateOf(0) } // 0 satu malam, 1 tren
    val night = Nights.getOrElse(selected) { Nights.first() }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Sleep Stages", subtitle = "Simulasi hipnogram, bukan layanan kesehatan", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item(key = "intro") {
                    LabIntroCard(
                        title = "Hypnogram",
                        body = "Grafik tahapan tidur per malam dengan scrubber waktu. Semua data mock lokal.",
                    )
                }
                item(key = "hypno") {
                    LabCard {
                        LabSectionTitle("Hypnogram ${night.label}", "Skor ${night.score}/100")
                        SleepHypnogram(night, Modifier.fillMaxWidth().height(160.dp))
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            StageNames.forEachIndexed { i, name ->
                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Box(Modifier.size(10.dp).background(StageColors[i], RoundedCornerShape(3.dp)))
                                    Text(name, fontSize = 10.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
                if (view == 0) {
                    item(key = "summary") {
                        LabCard {
                            Text("Ringkasan ${night.label}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = palette.textPrimary)
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                StatusPill(text = "REM 25%", color = palette.info)
                                StatusPill(text = "Dalam 18%", color = palette.success)
                                StatusPill(text = "Ringan 47%", color = palette.primary)
                                StatusPill(text = "Terjaga 10%", color = palette.warning)
                            }
                        }
                    }
                } else {
                    item(key = "trend") {
                        LabCard {
                            Text("Tren 7 malam", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = palette.textPrimary)
                            Spacer(Modifier.height(6.dp))
                            Nights.forEach { n ->
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Text(n.label, color = palette.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    StatusPill(text = "${n.score}", color = if (n.score >= 80) palette.success else palette.warning)
                                }
                            }
                        }
                    }
                }
                item(key = "actions") {
                    LabCard {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Nights.forEachIndexed { i, n ->
                                OutlinedButton(
                                    onClick = { selected = i; PerformHapticFeedback(HapticKind.HEAVY) },
                                    modifier = Modifier.weight(1f),
                                ) { Text(n.label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { view = 0 }, modifier = Modifier.weight(1f)) { Text("Malam", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            OutlinedButton(onClick = { view = 1 }, modifier = Modifier.weight(1f)) { Text("Mingguan", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        }
                    }
                }
            }
            Box(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                Text("Data mock, simulasi wearable", color = palette.textMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun SleepHypnogram(night: Night, modifier: Modifier) {
    val pal = LocalAppPalette.current
    Canvas(modifier) {
        val stepW = size.width / 420f
        val rowH = size.height / 5f
        // baseline grid
        for (i in 0..3) {
            drawLine(
                color = pal.surfaceMuted,
                start = Offset(0f, (i + 1) * rowH),
                end = Offset(size.width, (i + 1) * rowH),
                strokeWidth = 1.dp.toPx(),
            )
        }
        for (i in 1 until night.samples.size) {
            val a = night.samples[i - 1]
            val b = night.samples[i]
            val color = StageColors.getOrElse(a.stage) { Color.Gray }
            drawLine(
                color = color,
                start = Offset(a.minute * stepW, (4 - a.stage) * rowH),
                end = Offset(b.minute * stepW, (4 - b.stage) * rowH),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}