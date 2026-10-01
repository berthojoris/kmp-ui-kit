package com.example.uiapp.ui.precisionfinder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private const val MAX_DISTANCE = 12f
private const val MIN_DISTANCE = 0.3f

private data class PrecisionTarget(
    val id: String,
    val name: String,
    val detail: String,
    val bearing: Float,
    val startDistance: Float,
)

private val precisionDemoTargets = listOf(
    PrecisionTarget("kunci", "Kunci Rumah", "Penanda tiruan #A1", 118f, 9.6f),
    PrecisionTarget("dompet", "Dompet Kulit", "Penanda tiruan #B7", 262f, 5.4f),
    PrecisionTarget("ransel", "Ransel Kantor", "Penanda tiruan #C3", 35f, 11.8f),
)

private enum class FinderPhase(val statusLabel: String) {
    SEARCHING("Mencari sinyal"),
    WEAK("Sinyal lemah"),
    TRACKING("Melacak target"),
    NEAR("Sangat dekat"),
    IDLE("Pelacakan dijeda"),
}

private fun formatMeter(value: Float): String {
    val tenth = (value * 10).toInt()
    val whole = tenth / 10
    val frac = tenth % 10
    return "$whole,$frac m"
}

private fun formatDegree(value: Float): String {
    val rounded = ((value + 360f) % 360f).toInt()
    return "$rounded°"
}

private fun feedbackLabel(level: Int): String = when (level) {
    1 -> "Umpan level 1 · Jauh — getar pelan (Simulasi)"
    2 -> "Umpan level 2 · Mendekat — getar sedang (Simulasi)"
    3 -> "Umpan level 3 · Dekat — getar kuat (Simulasi)"
    else -> "Umpan level 4 · Sangat dekat — getar terus-menerus (Simulasi)"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PrecisionFinderLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var targetId by remember { mutableStateOf(precisionDemoTargets.first().id) }
    val target = precisionDemoTargets.first { it.id == targetId }

    var distance by remember(targetId) { mutableStateOf(target.startDistance) }
    var heading by remember(targetId) { mutableStateOf(0f) }
    var scanToken by remember { mutableStateOf(0) }
    var scanning by remember { mutableStateOf(true) }
    var weakSignal by remember { mutableStateOf(false) }
    var paused by remember { mutableStateOf(false) }

    // Fase "Mencari sinyal" setiap ganti target / pindai ulang
    LaunchedEffect(targetId, scanToken) {
        scanning = true
        delay(1600)
        scanning = false
    }

    val phase = when {
        paused -> FinderPhase.IDLE
        scanning -> FinderPhase.SEARCHING
        weakSignal -> FinderPhase.WEAK
        distance <= 1.0f -> FinderPhase.NEAR
        else -> FinderPhase.TRACKING
    }

    val relativeBearing = (target.bearing - heading + 360f) % 360f
    val controlsEnabled = !scanning && !paused

    val feedbackLevel = when {
        distance >= 8f -> 1
        distance >= 4f -> 2
        distance >= 1.5f -> 3
        else -> 4
    }

    val directionHint = when {
        phase == FinderPhase.SEARCHING -> "Arah belum pasti sampai sinyal ditemukan"
        phase == FinderPhase.WEAK -> "Arah perkiraan — sinyal terlalu lemah"
        relativeBearing < 12f || relativeBearing > 348f -> "Lurus ke depan — target di depan Anda"
        relativeBearing < 180f -> "Belok kanan ${formatDegree(relativeBearing)}"
        else -> "Belok kiri ${formatDegree(360f - relativeBearing)}"
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Precision Finding",
                subtitle = "Panah arah, cincin jarak — Simulasi",
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LabSectionTitle(
                    title = "Pelacakan Presisi",
                    subtitle = "Panah arah & cincin jarak semu",
                    modifier = Modifier.weight(1f),
                )
                SimulasiBadge()
            }

            LabIntroCard(
                title = "Cari objek dengan panduan arah",
                body = "Panah menunjuk target, cincin mengecil saat Anda mendekat, dan umpan bertingkat memberi isyarat jarak. Tanpa UWB, Bluetooth, atau lokasi sungguhan — seluruh sinyal dibangkitkan di aplikasi (Simulasi).",
            )

            // Pemilihan target mock
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
                    Text(
                        text = "Target penanda",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        precisionDemoTargets.forEach { item ->
                            TargetChip(
                                target = item,
                                selected = item.id == targetId,
                                enabled = !scanning,
                                onSelect = { targetId = item.id },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = target.detail,
                            fontSize = 11.sp,
                            color = palette.textMuted,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        SmallActionButton(
                            label = "Pindai ulang",
                            enabled = !scanning,
                            onClick = { scanToken++ },
                        )
                        SmallActionButton(
                            label = if (weakSignal) "Pulihkan sinyal" else "Sinyal lemah",
                            enabled = controlsEnabled,
                            onClick = { weakSignal = !weakSignal },
                        )
                        SmallActionButton(
                            label = if (paused) "Lanjut" else "Jeda",
                            enabled = !scanning,
                            onClick = { paused = !paused },
                        )
                    }
                }
            }

            FinderStage(
                phase = phase,
                distance = distance,
                arrowAngle = relativeBearing,
                modifier = Modifier.fillMaxWidth(),
            )

            // Status & umpan bertingkat
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
                        PhasePill(phase = phase)
                        Text(
                            text = "Belok ${formatDegree(relativeBearing)} · Arah perangkat ${formatDegree(heading)}",
                            fontSize = 11.sp,
                            color = palette.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        text = directionHint,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    FeedbackMeter(level = feedbackLevel, enabled = phase != FinderPhase.IDLE)
                    Text(
                        text = feedbackLabel(feedbackLevel),
                        fontSize = 11.sp,
                        color = palette.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            // Kontrol simulasi (disabled saat memindai / dijeda)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (controlsEnabled) 1f else 0.45f),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Kontrol simulasi",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    if (!controlsEnabled) {
                        Text(
                            text = if (scanning) "Kontrol dikunci selama pencarian sinyal." else "Kontrol dikunci saat pelacakan dijeda.",
                            fontSize = 11.sp,
                            color = palette.textMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        text = "Jarak ${formatMeter(distance)} — geser rel untuk maju / mundur",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    DistanceSlider(
                        distance = distance,
                        enabled = controlsEnabled,
                        onDelta = { delta ->
                            distance = (distance + delta).coerceIn(MIN_DISTANCE, MAX_DISTANCE)
                        },
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SmallActionButton(
                            label = "Dekati 0,5 m",
                            enabled = controlsEnabled,
                            onClick = { distance = (distance - 0.5f).coerceIn(MIN_DISTANCE, MAX_DISTANCE) },
                            modifier = Modifier.weight(1f),
                        )
                        SmallActionButton(
                            label = "Jauhi 0,5 m",
                            enabled = controlsEnabled,
                            onClick = { distance = (distance + 0.5f).coerceIn(MIN_DISTANCE, MAX_DISTANCE) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SmallActionButton(
                            label = "Putar kiri 15°",
                            enabled = controlsEnabled,
                            onClick = { heading = (heading - 15f + 360f) % 360f },
                            modifier = Modifier.weight(1f),
                        )
                        SmallActionButton(
                            label = "Putar kanan 15°",
                            enabled = controlsEnabled,
                            onClick = { heading = (heading + 15f) % 360f },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Catatan spesifikasi & simulasi
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
                        text = "Catatan pola Precision Finding:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "• Panah & cincin digambar Canvas murni: cincin mengecil proporsional terhadap jarak",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        text = "• Umpan bertingkat (empat level) selalu disertai label status, bukan hanya warna",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        text = "• State: Mencari sinyal, Sinyal lemah, Melacak, Sangat dekat, dan Dijeda",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        text = "• Simulasi: tidak ada akses UWB, Bluetooth, kompas, maupun lokasi perangkat",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun FinderStage(
    phase: FinderPhase,
    distance: Float,
    arrowAngle: Float,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    val proximity = (1f - (distance - MIN_DISTANCE) / (MAX_DISTANCE - MIN_DISTANCE)).coerceIn(0f, 1f)

    val arrowTint = when (phase) {
        FinderPhase.NEAR -> palette.success
        FinderPhase.WEAK -> palette.textMuted
        else -> palette.primary
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surfaceMuted)
            .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(16.dp))
            .semantics {
                contentDescription = "Peta arah. Status ${phase.statusLabel}, jarak ${formatMeter(distance)}"
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(26.dp)) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val maxRadius = minOf(centerX, centerY) - 2.dp.toPx()

            val ringAlpha = if (phase == FinderPhase.WEAK) 0.35f else 1f
            for (i in 1..3) {
                val radius = maxRadius * (0.32f + 0.23f * i) * (0.55f + 0.45f * proximity)
                drawCircle(
                    color = palette.border.copy(alpha = ringAlpha),
                    radius = radius,
                    style = Stroke(width = 1.dp.toPx()),
                )
            }

            drawCircle(
                color = arrowTint.copy(alpha = 0.30f),
                radius = 5.dp.toPx(),
                center = Offset(centerX, centerY),
            )

            if (phase == FinderPhase.NEAR) {
                drawCircle(
                    color = palette.success.copy(alpha = 0.45f),
                    radius = maxRadius * 0.14f,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                )
            }

            rotate(degrees = arrowAngle, pivot = Offset(centerX, centerY)) {
                val shaft = maxRadius * (0.34f + 0.30f * proximity)
                val headHalf = maxRadius * 0.15f
                val headHeight = maxRadius * 0.20f
                val stemHalf = maxRadius * 0.05f
                val tail = shaft * 0.35f

                val arrow = Path().apply {
                    moveTo(centerX, centerY - shaft)
                    lineTo(centerX + headHalf, centerY - shaft + headHeight)
                    lineTo(centerX + stemHalf, centerY - shaft + headHeight)
                    lineTo(centerX + stemHalf, centerY + tail)
                    lineTo(centerX - stemHalf, centerY + tail)
                    lineTo(centerX - stemHalf, centerY - shaft + headHeight)
                    lineTo(centerX - headHalf, centerY - shaft + headHeight)
                    close()
                }
                drawPath(path = arrow, color = arrowTint)
                drawPath(
                    path = arrow,
                    color = palette.border,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = phase.statusLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textSecondary,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = formatMeter(distance),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = palette.textPrimary,
            )
            Text(
                text = "jarak perkiraan — Simulasi",
                fontSize = 10.sp,
                color = palette.textMuted,
            )
        }
    }
}

@Composable
private fun FeedbackMeter(
    level: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(4) { idx ->
            val active = enabled && idx < level
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        if (active) palette.warning.copy(alpha = 0.35f + 0.18f * idx) else palette.surfaceMuted,
                    )
                    .border(
                        BorderStroke(1.dp, if (active) palette.warning else palette.border),
                        RoundedCornerShape(5.dp),
                    )
                    .semantics {
                        contentDescription = "Segmen umpan ${idx + 1} ${if (active) "aktif" else "nonaktif"}"
                    },
            )
        }
    }
}

@Composable
private fun PhasePill(
    phase: FinderPhase,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    val container = when (phase) {
        FinderPhase.SEARCHING -> palette.info.copy(alpha = 0.12f)
        FinderPhase.WEAK -> palette.warning.copy(alpha = 0.15f)
        FinderPhase.TRACKING -> palette.primaryContainer
        FinderPhase.NEAR -> palette.success.copy(alpha = 0.15f)
        FinderPhase.IDLE -> palette.surfaceMuted
    }
    val content = when (phase) {
        FinderPhase.SEARCHING -> palette.info
        FinderPhase.WEAK -> palette.warning
        FinderPhase.TRACKING -> palette.onPrimaryContainer
        FinderPhase.NEAR -> palette.success
        FinderPhase.IDLE -> palette.textSecondary
    }
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = container,
        border = BorderStroke(1.dp, content),
        modifier = modifier,
    ) {
        Text(
            text = phase.statusLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = content,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun TargetChip(
    target: PrecisionTarget,
    selected: Boolean,
    enabled: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onSelect,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) palette.primaryContainer else palette.surfaceMuted,
        border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = target.name,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (selected) palette.onPrimaryContainer else palette.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (selected) "Dilacak" else "Pilih",
                fontSize = 10.sp,
                color = palette.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = palette.surfaceMuted,
        border = BorderStroke(1.dp, palette.border),
        modifier = modifier,
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) palette.textPrimary else palette.textMuted,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun DistanceSlider(
    distance: Float,
    enabled: Boolean,
    onDelta: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    val thumbFraction = (1f - (distance - MIN_DISTANCE) / (MAX_DISTANCE - MIN_DISTANCE)).coerceIn(0.02f, 0.98f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surfaceMuted)
            .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(12.dp))
            .pointerInput(enabled) {
                if (enabled) {
                    detectDragGestures { _, dragAmount ->
                        onDelta(-dragAmount.x / 28f)
                    }
                }
            }
            .semantics {
                contentDescription = "Rel jarak simulasi ${formatMeter(distance)}, geser ke kiri untuk mendekat"
            },
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp)) {
            Box(modifier = Modifier.weight(thumbFraction).fillMaxSize())
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .fillMaxSize()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (enabled) palette.primary else palette.textMuted)
                    .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(9.dp)),
            )
            Box(modifier = Modifier.weight(1f - thumbFraction).fillMaxSize())
        }
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
