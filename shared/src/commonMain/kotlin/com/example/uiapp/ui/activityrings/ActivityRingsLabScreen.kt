package com.example.uiapp.ui.activityrings

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.uiapp.ui.components.UiTopBar

private enum class RingScenario(val label: String) {
    EMPTY("Belum Ada Data"),
    PARTIAL("Sebagian"),
    COMPLETE("Tuntas 100%"),
    STREAK("Rentetan"),
}

private data class RingMetric(
    val id: String,
    val label: String,
    val value: Float,
    val target: Float,
    val unit: String,
) {
    val fraction: Float
        get() = if (target <= 0f) 0f else (value / target).coerceIn(0f, 1f)
}

private data class DayRecord(
    val id: String,
    val label: String,
    val movePct: Float,
    val exercisePct: Float,
    val standPct: Float,
)

private fun ringMetrics(scenario: RingScenario): List<RingMetric> = when (scenario) {
    RingScenario.EMPTY -> listOf(
        RingMetric("move", "Bergerak", 0f, 500f, "kalori"),
        RingMetric("exercise", "Olahraga", 0f, 30f, "menit"),
        RingMetric("stand", "Berdiri", 0f, 12f, "jam"),
    )
    RingScenario.PARTIAL -> listOf(
        RingMetric("move", "Bergerak", 320f, 500f, "kalori"),
        RingMetric("exercise", "Olahraga", 12f, 30f, "menit"),
        RingMetric("stand", "Berdiri", 8f, 12f, "jam"),
    )
    RingScenario.COMPLETE, RingScenario.STREAK -> listOf(
        RingMetric("move", "Bergerak", 520f, 500f, "kalori"),
        RingMetric("exercise", "Olahraga", 35f, 30f, "menit"),
        RingMetric("stand", "Berdiri", 12f, 12f, "jam"),
    )
}

private fun weekData(scenario: RingScenario): List<DayRecord> {
    val labels = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
    return labels.mapIndexed { index, label ->
        when (scenario) {
            RingScenario.EMPTY -> DayRecord("d$index", label, 0f, 0f, 0f)
            RingScenario.PARTIAL -> {
                val move = 0.35f + 0.09f * index
                val exercise = 0.20f + 0.07f * index
                val stand = 0.55f + 0.05f * index
                DayRecord("d$index", label, move.coerceAtMost(0.85f), exercise.coerceAtMost(0.8f), stand.coerceAtMost(0.95f))
            }
            RingScenario.COMPLETE -> {
                val full = index < 5
                DayRecord(
                    "d$index",
                    label,
                    if (full) 1.05f else 0.62f,
                    if (full) 1.10f else 0.55f,
                    if (full) 1f else 0.75f,
                )
            }
            RingScenario.STREAK -> DayRecord(
                "d$index",
                label,
                if (index < 2) 0.7f else 1.08f,
                if (index < 2) 0.6f else 1.15f,
                if (index < 2) 0.83f else 1f,
            )
        }
    }
}

private fun streakLabel(scenario: RingScenario): String = when (scenario) {
    RingScenario.EMPTY -> "Belum ada rentetan"
    RingScenario.PARTIAL -> "Rentetan 1 hari"
    RingScenario.COMPLETE -> "Rentetan 3 hari"
    RingScenario.STREAK -> "Rentetan 5 hari berturut-turut"
}

private fun statusText(fraction: Float): String = when {
    fraction <= 0f -> "Belum ada data"
    fraction >= 1f -> "Tuntas"
    else -> "Sebagian"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ActivityRingsLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var scenario by remember { mutableStateOf(RingScenario.PARTIAL) }
    var selectedRingIndex by remember { mutableIntStateOf(0) }
    var selectedDayIndex by remember { mutableIntStateOf(4) }
    var replayToken by remember { mutableIntStateOf(0) }

    val metrics = remember(scenario) { ringMetrics(scenario) }
    val week = remember(scenario) { weekData(scenario) }

    val moveAnim = remember { Animatable(0f) }
    val exerciseAnim = remember { Animatable(0f) }
    val standAnim = remember { Animatable(0f) }

    // Animasi penutupan cincin berurutan: Bergerak → Olahraga → Berdiri.
    LaunchedEffect(scenario, replayToken) {
        moveAnim.snapTo(0f)
        exerciseAnim.snapTo(0f)
        standAnim.snapTo(0f)
        moveAnim.animateTo(metrics[0].fraction, tween(650))
        exerciseAnim.animateTo(metrics[1].fraction, tween(650))
        standAnim.animateTo(metrics[2].fraction, tween(650))
    }

    val moveColor = palette.danger
    val exerciseColor = palette.success
    val standColor = palette.info

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Activity Rings",
                subtitle = "Tiga cincin aktivitas & ringkasan mingguan",
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = CircleShape,
                    color = palette.warning.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, palette.warning),
                ) {
                    Text(
                        "Simulasi",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = palette.surfaceMuted,
                    border = BorderStroke(1.dp, palette.border),
                ) {
                    Text(
                        "Data contoh",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = palette.surfaceMuted,
                    border = BorderStroke(1.dp, palette.border),
                ) {
                    Text(
                        streakLabel(scenario),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }

            // Pemilih skenario state
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                RingScenario.entries.forEach { option ->
                    val selected = scenario == option
                    Surface(
                        onClick = { scenario = option },
                        shape = CircleShape,
                        color = if (selected) palette.primary else palette.surface,
                        border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
                        modifier = Modifier
                            .weight(1f)
                            .semantics {
                                role = Role.Button
                                contentDescription = "Tampilkan skenario ${option.label}"
                            },
                    ) {
                        Text(
                            option.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) palette.onPrimary else palette.textSecondary,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        )
                    }
                }
            }

            // Kartu cincin
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    RingsCanvas(
                        moveFraction = moveAnim.value,
                        exerciseFraction = exerciseAnim.value,
                        standFraction = standAnim.value,
                        moveColor = moveColor,
                        exerciseColor = exerciseColor,
                        standColor = standColor,
                        onRingTap = { index ->
                            selectedRingIndex = index
                            selectedDayIndex = 4
                        },
                        centerPercent = ((metrics[0].fraction + metrics[1].fraction + metrics[2].fraction) / 3f * 100f).toInt(),
                        centerLabel = if (scenario == RingScenario.EMPTY) "Belum ada data" else "Rata-rata tuntas",
                    )

                    // Legenda berlabel teks (status tidak hanya lewat warna)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        metrics.forEachIndexed { index, metric ->
                            val color = when (index) {
                                0 -> moveColor
                                1 -> exerciseColor
                                else -> standColor
                            }
                            val selected = selectedRingIndex == index
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) palette.surfaceMuted else Color.Transparent)
                                    .clickable {
                                        selectedRingIndex = index
                                        selectedDayIndex = 4
                                    }
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                                    .semantics {
                                        role = Role.Button
                                        contentDescription = "Lihat detail harian ${metric.label}, ${statusText(metric.fraction)}"
                                    },
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(color),
                                )
                                Text(
                                    metric.label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary,
                                    modifier = Modifier.width(74.dp),
                                )
                                Text(
                                    "${metric.value.toInt()} / ${metric.target.toInt()} ${metric.unit}",
                                    fontSize = 11.sp,
                                    color = palette.textSecondary,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    statusText(metric.fraction),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (metric.fraction >= 1f) palette.success else palette.textMuted,
                                )
                            }
                        }
                    }

                    Surface(
                        onClick = { replayToken++ },
                        shape = RoundedCornerShape(10.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                role = Role.Button
                                contentDescription = "Putar ulang animasi cincin"
                            },
                    ) {
                        Text(
                            "↻ Putar Ulang Animasi",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp),
                        )
                    }
                }
            }

            // Riwayat 7 hari
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Riwayat 7 Hari", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                        Text("Ketuk bar untuk detail", fontSize = 10.sp, color = palette.textMuted)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        week.forEachIndexed { index, day ->
                            val selected = selectedDayIndex == index
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) palette.surfaceMuted else Color.Transparent)
                                    .clickable {
                                        selectedDayIndex = index
                                    }
                                    .padding(horizontal = 6.dp, vertical = 6.dp)
                                    .semantics {
                                        role = Role.Button
                                        contentDescription =
                                            "Detail hari ${day.label}: Bergerak ${(day.movePct * 100).toInt()} persen, Olahraga ${(day.exercisePct * 100).toInt()} persen, Berdiri ${(day.standPct * 100).toInt()} persen"
                                    },
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    verticalAlignment = Alignment.Bottom,
                                ) {
                                    MiniBar(fraction = day.movePct, color = moveColor, selected = selected)
                                    MiniBar(fraction = day.exercisePct, color = exerciseColor, selected = selected)
                                    MiniBar(fraction = day.standPct, color = standColor, selected = selected)
                                }
                                Text(
                                    day.label,
                                    fontSize = 10.sp,
                                    color = if (selected) palette.textPrimary else palette.textMuted,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                )
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        LegendDot("Bergerak", moveColor)
                        LegendDot("Olahraga", exerciseColor)
                        LegendDot("Berdiri", standColor)
                    }
                }
            }

            // Detail harian
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Detail Harian — ${week[selectedDayIndex].label}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = palette.textPrimary,
                    )
                    val day = week[selectedDayIndex]
                    val dayFractions = listOf(day.movePct, day.exercisePct, day.standPct)
                    metrics.forEachIndexed { index, metric ->
                        val fraction = dayFractions[index]
                        val highlighted = selectedRingIndex == index
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (highlighted) palette.primaryContainer else Color.Transparent)
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                metric.label,
                                fontSize = 12.sp,
                                fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal,
                                color = palette.textPrimary,
                            )
                            Text(
                                "${(metric.target * fraction).toInt()} ${metric.unit}",
                                fontSize = 12.sp,
                                color = palette.textSecondary,
                            )
                            Text(
                                statusText(fraction),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (fraction >= 1f) palette.success else palette.textMuted,
                            )
                        }
                    }
                    if (scenario == RingScenario.EMPTY) {
                        Text(
                            "Belum ada data aktivitas pada skenario ini (state kosong).",
                            fontSize = 11.sp,
                            color = palette.textMuted,
                        )
                    }
                }
            }

            // Badge pencapaian
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Badge Pencapaian", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = palette.textPrimary)
                    val allComplete = scenario == RingScenario.COMPLETE || scenario == RingScenario.STREAK
                    if (allComplete) {
                        AchievementBadge(
                            title = "Semua Cincin Tuntas",
                            detail = "Bergerak, Olahraga, dan Berdiri mencapai target",
                            accent = palette.success,
                            visible = true,
                        )
                        if (scenario == RingScenario.STREAK) {
                            AchievementBadge(
                                title = "Rentetan 5 Hari",
                                detail = "Lima hari berturut-turut semua cincin tuntas",
                                accent = palette.warning,
                                visible = true,
                            )
                        }
                    } else {
                        Text(
                            "Belum ada badge terbuka. Tuntaskan ketiga cincin untuk membuka badge pencapaian.",
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = palette.textSecondary,
                        )
                        AchievementBadge(
                            title = "Terkunci",
                            detail = "Badge ini terbuka saat target tuntas",
                            accent = palette.textMuted,
                            visible = false,
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
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Pola Activity Rings:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Tiga cincin konsentris menutup berurutan (Bergerak → Olahraga → Berdiri) dengan target di tengah", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Setiap status punya label teks (Tuntas / Sebagian / Belum ada data) sehingga tidak hanya dibedakan warna", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Ketuk cincin atau bar mingguan untuk membuka detail harian", fontSize = 12.sp, color = palette.textSecondary)
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun RingsCanvas(
    moveFraction: Float,
    exerciseFraction: Float,
    standFraction: Float,
    moveColor: Color,
    exerciseColor: Color,
    standColor: Color,
    onRingTap: (Int) -> Unit,
    centerPercent: Int,
    centerLabel: String,
) {
    val palette = LocalAppPalette.current
    val trackColor = palette.surfaceMuted

    Box(
        modifier = Modifier.size(230.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .size(230.dp)
                .pointerInput(Unit) {
                    detectTapGestures { pos ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val dist = (pos - center).getDistance()
                        val base = size.width / 2f
                        val index = when {
                            dist >= base * 0.70f -> 0
                            dist >= base * 0.52f -> 1
                            dist >= base * 0.34f -> 2
                            else -> -1
                        }
                        if (index in 0..2) onRingTap(index)
                    }
                },
        ) {
            val strokeWidth = 20.dp.toPx()
            val baseRadius = size.minDimension / 2f - strokeWidth
            val ringGap = strokeWidth + 8.dp.toPx()
            val fractions = listOf(moveFraction, exerciseFraction, standFraction)
            val colors = listOf(moveColor, exerciseColor, standColor)
            for (i in 0..2) {
                val radius = baseRadius - i * ringGap
                val arcSize = Size(radius * 2f, radius * 2f)
                val topLeft = Offset(center.x - radius, center.y - radius)
                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
                val sweep = 360f * fractions[i].coerceIn(0f, 1f)
                if (sweep > 0.5f) {
                    drawArc(
                        color = colors[i],
                        startAngle = -90f,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "$centerPercent%",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = palette.textPrimary,
            )
            Text(
                centerLabel,
                fontSize = 10.sp,
                color = palette.textMuted,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MiniBar(
    fraction: Float,
    color: Color,
    selected: Boolean,
) {
    val palette = LocalAppPalette.current
    val heightAnim by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(450),
        label = "miniBar",
    )
    Box(
        modifier = Modifier
            .width(7.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(palette.surfaceMuted),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((48.dp * heightAnim.coerceAtLeast(0.04f)))
                .clip(RoundedCornerShape(4.dp))
                .background(if (selected) color else color.copy(alpha = 0.75f)),
        )
    }
}

@Composable
private fun LegendDot(label: String, color: Color) {
    val palette = LocalAppPalette.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(label, fontSize = 10.sp, color = palette.textSecondary)
    }
}

@Composable
private fun AchievementBadge(
    title: String,
    detail: String,
    accent: Color,
    visible: Boolean,
) {
    val palette = LocalAppPalette.current
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = tween(350),
        label = "badgeScale",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (visible) accent.copy(alpha = 0.12f) else palette.surfaceMuted)
            .border(1.dp, if (visible) accent else palette.border, RoundedCornerShape(12.dp))
            .padding(14.dp)
            .semantics {
                contentDescription = if (visible) "Badge terbuka: $title" else "Badge terkunci: $title"
            },
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (visible) accent.copy(alpha = 0.20f) else palette.border)
                .border(1.dp, if (visible) accent else palette.border, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                if (visible) "★" else "☆",
                fontSize = 18.sp,
                color = if (visible) accent else palette.textMuted,
                modifier = Modifier.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (visible) palette.textPrimary else palette.textMuted,
            )
            Text(
                detail,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = palette.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
