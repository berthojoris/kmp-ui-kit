package com.example.uiapp.ui.perfhud

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.HapticKind
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private const val SampleCount = 96
private const val JankMs = 16.7f
private const val HeavyJankMs = 33.3f

private class RecomposeCounter(var count: Int = 0)

private data class HeavyRow(val id: Int, val label: String, val value: String)

private val HeavyRows = (1..600).map {
    HeavyRow(it, "Baris render $it", "kompositor • ${it * 37} tx")
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PerfHudLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    var running by remember { mutableStateOf(true) }
    var heavyList by remember { mutableStateOf(false) }
    var heavyAnim by remember { mutableStateOf(false) }
    val samples = remember { FloatArray(SampleCount) }
    var version by remember { mutableIntStateOf(0) }
    val counter = remember { RecomposeCounter() }
    var recomposeDisplay by remember { mutableIntStateOf(0) }

    BackHandler(enabled = true) { onBack() }

    // Pengukur frame murni commonMain: delta antar withFrameNanos.
    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        var last = -1L
        while (true) {
            val now = withFrameNanos { it }
            if (last >= 0) {
                val ms = (now - last) / 1_000_000f
                samples[version % SampleCount] = ms
                version++
            }
            last = now
        }
    }

    // Sampling penghitung recomposition tiap 500 ms.
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            recomposeDisplay = counter.count
        }
    }

    SideEffect { counter.count++ }

    val window = if (version < SampleCount) samples.take(version.coerceAtLeast(1)) else samples.toList()
    val avg = if (window.isEmpty()) 0f else window.average().toFloat()
    val jankCount = window.count { it > JankMs }
    val heavyCount = window.count { it > HeavyJankMs }
    val fps = if (avg > 0f) 1000f / avg else 0f

    val (statusText, statusColor, statusCause) = when {
        avg <= 0f -> Triple("Mengukur…", palette.textMuted, "Menunggu frame pertama.")
        avg < 17f -> Triple("Mulus", palette.success, "Rata-rata frame di bawah 16,7 ms (60 fps).")
        avg < 25f -> Triple("Jank ringan", palette.warning, "Sebagian frame melewati 16,7 ms; animasi terasa tersendat sesekali.")
        else -> Triple("Jank berat", palette.danger, "Banyak frame di atas 33 ms — turunkan beban demo di bawah.")
    }

    val infinite = rememberInfiniteTransition(label = "perf")
    val pulse by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background),
    ) {
        UiTopBar(
            title = "Performance HUD",
            subtitle = "Jank meter berbasis withFrameNanos",
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LabIntroCard(
                title = "Pengukuran murni commonMain",
                body = "Selisih waktu antar callback withFrameNanos dipakai sebagai perkiraan frame time. " +
                    "Tanpa API profiling platform, jadi hasilnya indikatif — bukan pengganti systrace.",
            )

            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    StatCell("FPS", if (fps > 0) fps.toInt().toString() else "—")
                    StatCell("Frame", if (avg > 0) fmt1(avg) + " ms" else "—")
                    StatCell("Jank", jankCount.toString())
                    StatCell("Recomp.", recomposeDisplay.toString())
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    StatusPill(statusText, statusColor)
                    Text(statusCause, fontSize = 11.sp, color = palette.textSecondary)
                }
            }

            LabSectionTitle("Grafik frame time", "Penanda: hijau mulus, kuning > 16 ms, merah > 33 ms")

            FrameChart(
                samples = samples,
                count = version.coerceAtMost(SampleCount),
                version = version,
                heavyList = heavyList,
                heavyAnim = heavyAnim,
                pulse = pulse,
            )

            LabCard {
                Text("Beban demo", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                ToggleRow(
                    label = "List besar (600 baris)",
                    hint = "LazyColumn aktif di bawah layar ini",
                    checked = heavyList,
                ) { heavyList = it }
                ToggleRow(
                    label = "Animasi berat",
                    hint = "Beberapa Canvas digambar ulang tiap frame",
                    checked = heavyAnim,
                ) { heavyAnim = it }
                ToggleRow(
                    label = "HUD berjalan",
                    hint = "Matikan untuk membekukan pengukuran",
                    checked = running,
                ) { running = it }
                Text(
                    "Nyalakan keduanya lalu gulir list untuk memicu drop frame.",
                    fontSize = 11.sp,
                    color = palette.textMuted,
                )
            }
        }

        if (heavyList) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(palette.surface)
                    .border(1.dp, palette.border),
            ) {
                items(HeavyRows, key = { it.id }) { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(palette.surface)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(row.label, fontSize = 11.sp, color = palette.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(row.value, fontSize = 11.sp, color = palette.textMuted, maxLines = 1)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.surface)
                .border(1.dp, palette.border)
                .clickable {
                    PerformHapticFeedback(HapticKind.SELECTION)
                    version = 0
                }
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Text(
                "Reset pengukuran",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.primary,
            )
        }
    }
}

@Composable
private fun StatCell(label: String, value: String) {
    val palette = LocalAppPalette.current
    Column {
        Text(value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary, maxLines = 1)
        Text(label, fontSize = 10.sp, color = palette.textMuted)
    }
}

@Composable
private fun ToggleRow(label: String, hint: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                PerformHapticFeedback(HapticKind.SELECTION)
                onToggle(!checked)
            }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, fontSize = 12.sp, color = palette.textPrimary)
            Text(hint, fontSize = 10.sp, color = palette.textMuted)
        }
        Box(
            modifier = Modifier
                .background(
                    if (checked) palette.primary else palette.surfaceMuted,
                    RoundedCornerShape(999.dp),
                )
                .border(1.dp, if (checked) palette.primary else palette.border, RoundedCornerShape(999.dp))
                .padding(horizontal = 12.dp, vertical = 5.dp),
        ) {
            Text(if (checked) "Aktif" else "Mati", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (checked) palette.onPrimary else palette.textMuted)
        }
    }
}

@Composable
private fun FrameChart(
    samples: FloatArray,
    count: Int,
    version: Int,
    heavyList: Boolean,
    heavyAnim: Boolean,
    pulse: Float,
) {
    val palette = LocalAppPalette.current
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(palette.surface, RoundedCornerShape(12.dp))
            .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
    ) {
        version // baca state agar grafik digambar ulang tiap frame.
        val w = size.width
        val h = size.height
        val step = w / SampleCount

        // Garis acuan 16 ms.
        val y16 = h * (JankMs / 40f)
        drawLine(palette.border, Offset(0f, y16), Offset(w, y16), strokeWidth = 1.dp.toPx())

        val n = count.coerceAtMost(SampleCount)
        for (i in 0 until n) {
            val idx = (version - n + i).mod(SampleCount)
            val ms = samples[idx]
            val barH = (ms.coerceAtMost(40f) / 40f) * (h - 4f)
            val color = when {
                ms > HeavyJankMs -> palette.danger
                ms > JankMs -> palette.warning
                else -> palette.success
            }
            drawLine(
                color = color,
                start = Offset(i * step + step / 2, h),
                end = Offset(i * step + step / 2, h - barH),
                strokeWidth = step * 0.6f,
                cap = StrokeCap.Round,
            )
        }

        // Beban animasi berat: cincin yang digambar ulang tiap frame.
        if (heavyAnim) {
            val r = size.minDimension * 0.32f
            repeat(3) { ring ->
                drawArc(
                    color = palette.info.copy(alpha = 0.5f - ring * 0.12f),
                    startAngle = pulse * 360f + ring * 120f,
                    sweepAngle = 80f + pulse * 60f,
                    useCenter = false,
                    topLeft = Offset(w / 2 - r, h / 2 - r),
                    size = androidx.compose.ui.geometry.Size(r * 2, r * 2),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                )
            }
        }
        if (heavyList) {
            drawRect(
                color = palette.warning.copy(alpha = 0.06f + pulse * 0.05f),
                size = androidx.compose.ui.geometry.Size(w, h),
            )
        }
    }
}

private fun fmt1(value: Float): String = ((value * 10).toInt() / 10.0).toString()
