package com.example.uiapp.ui.nutritiondiary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.Size
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

private data class Meal(val id: Int, val time: String, val name: String, val kcal: Int, val p: Int, val c: Int, val f: Int)

private val QuickFoods = listOf(
    "Nasi + ayam bakar" to intArrayOf(520, 32, 48, 16),
    "Salad buah" to intArrayOf(210, 4, 42, 6),
    "Oatmeal + pisang" to intArrayOf(330, 12, 60, 8),
    "Roti gandum + telur" to intArrayOf(280, 14, 30, 10),
)

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun NutritionDiaryLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var meals by remember { mutableStateOf(listOf<Meal>()) }
    var calTarget by remember { mutableIntStateOf(1800) }
    var nextId by remember { mutableIntStateOf(1) }
    val eaten = meals.sumOf { it.kcal }
    val p = meals.sumOf { it.p }
    val c = meals.sumOf { it.c }
    val f = meals.sumOf { it.f }
    val exceeded = eaten > calTarget

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Nutrition Diary", subtitle = "Simulasi pencatatan makan, bukan layanan gizi", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item(key = "intro") {
                    LabIntroCard(
                        title = "Diari nutrisi",
                        body = "Catat makan, lihat cincin makro, dan peringatan saat target kalori terlampaui. Semua data mock lokal.",
                    )
                }
                item(key = "rings") {
                    LabCard {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            MacroRing(
                                eaten = eaten,
                                target = calTarget,
                                p = p, c = c, f = f,
                                color = if (exceeded) palette.danger else palette.primary,
                            )
                            Column {
                                Text("Kalori", fontSize = 11.sp, color = palette.textMuted)
                                Text("$eaten / $calTarget kcal", fontWeight = FontWeight.Bold, color = if (exceeded) palette.danger else palette.textPrimary)
                                Spacer(Modifier.height(6.dp))
                                Text("Protein ${p}g  Karbo ${c}g  Lemak ${f}g", fontSize = 11.sp, color = palette.textSecondary)
                                Spacer(Modifier.height(6.dp))
                                StatusPill(
                                    text = when {
                                        meals.isEmpty() -> "Belum ada entri"
                                        exceeded -> "Melebihi target"
                                        eaten > calTarget * 0.8 -> "Hampir penuh"
                                        else -> "Sebagian target"
                                    },
                                    color = when {
                                        meals.isEmpty() -> palette.textMuted
                                        exceeded -> palette.danger
                                        eaten > calTarget * 0.8 -> palette.warning
                                        else -> palette.success
                                    },
                                )
                            }
                        }
                    }
                }
                if (meals.isEmpty()) {
                    item(key = "empty") {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Belum ada makanan tercatat", color = palette.textMuted, fontSize = 12.sp)
                        }
                    }
                } else {
                    items(meals, key = { it.id }) { m ->
                        LabCard {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.weight(1f)) {
                                    Text(m.name, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("${m.time} • ${m.kcal} kcal", fontSize = 11.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                StatusPill(text = "P${m.p} C${m.c} F${m.f}", color = palette.info)
                            }
                        }
                    }
                }
                item(key = "actions") {
                    LabCard {
                        LabSectionTitle("Tambah makanan", "Pilih contoh mock atau reset hari")
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            QuickFoods.forEachIndexed { i, (name, vals) ->
                                OutlinedButton(
                                    onClick = {
                                        val t = when (i) { 0 -> "08:00"; 1 -> "12:30"; else -> "19:00" }
                                        meals = meals + Meal(nextId, t, name, vals[0], vals[1], vals[2], vals[3])
                                        nextId++
                                        PerformHapticFeedback(HapticKind.HEAVY)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) { Text("$name (${vals[0]} kcal)", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { meals = meals.dropLast(1) },
                                    enabled = meals.isNotEmpty(),
                                    modifier = Modifier.weight(1f),
                                ) { Text("Undo", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                                OutlinedButton(
                                    onClick = { meals = emptyList(); nextId = 1 },
                                    modifier = Modifier.weight(1f),
                                ) { Text("Reset", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            }
                        }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                Text("Target bisa disesuaikan via chip", color = palette.textMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun MacroRing(eaten: Int, target: Int, p: Int, c: Int, f: Int, color: Color) {
    val pal = LocalAppPalette.current
    Box(Modifier.size(120.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(120.dp)) {
            val stroke = 12.dp.toPx()
            drawArc(
                color = pal.surfaceMuted,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
                size = Size(size.width, size.height),
                topLeft = Offset(0f, 0f),
            )
            val frac = (eaten / target.toFloat()).coerceIn(0f, 1f)
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = frac * 360f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
                size = Size(size.width, size.height),
                topLeft = Offset(0f, 0f),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${(eaten / target.toFloat() * 100).toInt()}%", fontWeight = FontWeight.Bold, color = pal.textPrimary)
            Text("Target", fontSize = 10.sp, color = pal.textMuted)
        }
    }
}