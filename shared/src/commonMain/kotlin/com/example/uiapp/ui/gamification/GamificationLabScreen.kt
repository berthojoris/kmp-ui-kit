package com.example.uiapp.ui.gamification

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.AppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ---------- Data mock lokal (label simulasi) ----------

private data class WheelPrize(val id: String, val label: String, val isZonk: Boolean)

private val WheelPrizes = listOf(
    WheelPrize("p1", "Voucher 50%", false),
    WheelPrize("p2", "Zonk", true),
    WheelPrize("p3", "Poin 500", false),
    WheelPrize("p4", "Gratis Ongkir", false),
    WheelPrize("p5", "Zonk", true),
    WheelPrize("p6", "Voucher 20%", false),
)

private data class Flashcard(val id: String, val front: String, val back: String)

private val Flashcards = listOf(
    Flashcard("f1", "Apa itu Compose?", "Toolkit UI deklaratif dari JetBrains & Google."),
    Flashcard("f2", "Kapan recomposition terjadi?", "Saat state yang dibaca berubah nilainya."),
    Flashcard("f3", "Apa fungsi Modifier?", "Menambah perilaku atau tampilan pada composable."),
    Flashcard("f4", "Kenapa pakai key di LazyList?", "Agar identitas item stabil saat daftar berubah."),
)

private data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
)

private val QuizQuestions = listOf(
    QuizQuestion(
        id = "q1",
        question = "Perangkat mana yang menyimpan state lokal di composable?",
        options = listOf("remember", "NavHost", "TextMeasurer", "Canvas"),
        correctIndex = 0,
        explanation = "remember menjaga nilai tetap selama recomposition.",
    ),
    QuizQuestion(
        id = "q2",
        question = "Warna komponen baru sebaiknya diambil dari?",
        options = listOf("Color(0xFF...)", "LocalAppPalette", "LuxuryColors", "Konstanta bawaan"),
        correctIndex = 1,
        explanation = "LocalAppPalette mendukung mode terang dan gelap.",
    ),
    QuizQuestion(
        id = "q3",
        question = "Berapa nilai elevation yang diizinkan oleh Flat UI?",
        options = listOf("4.dp", "8.dp", "0.dp", "Bebas"),
        correctIndex = 2,
        explanation = "Flat UI melarang shadow, tepi dibentuk border 1px.",
    ),
)

private data class PollOption(val id: String, val label: String, val baseVotes: Int)

private val PollOptions = listOf(
    PollOption("o1", "Bottom bar", 42),
    PollOption("o2", "Navigation rail", 27),
    PollOption("o3", "Floating pill", 19),
    PollOption("o4", "Drawer samping", 12),
)

private data class LeaderEntry(val id: String, val name: String, val score: Int, val delta: Int, val isMe: Boolean)

private val Leaderboard = listOf(
    LeaderEntry("l1", "Sinta Ayu", 9820, 0, false),
    LeaderEntry("l2", "Rangga Pratama", 9540, 2, false),
    LeaderEntry("l3", "Kamu", 9110, -1, true),
    LeaderEntry("l4", "Dewi Lestari", 8870, 1, false),
    LeaderEntry("l5", "Bagas Kurnia", 8450, -2, false),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GamificationLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Gamifikasi & Reward",
                subtitle = "Roda undian, flashcard, kuis, polling, papan skor",
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LabIntroCard(
                title = "Mekanik Reward Interaktif",
                body = "Semua hadiah, skor, dan pertanyaan bersumber dari data mock lokal. Animasi roda dan kuis dijalankan dengan coroutine, bukan koneksi ke layanan hadiah sungguhan.",
            )

            LabSectionTitle("1. Roda Undian", "Putar untuk memenangkan hadiah (simulasi)")
            SpinWheelCard()

            LabSectionTitle("2. Flashcard", "Ketuk kartu untuk membalik dan melihat jawaban")
            FlashcardCard()

            LabSectionTitle("3. Kuis", "Pilih jawaban, lihat penjelasan dan skor")
            QuizCard()

            LabSectionTitle("4. Polling Langsung", "Satu suara per pengguna, hasil tampil seketika")
            PollCard()

            LabSectionTitle("5. Papan Skor", "Peringkat, kenaikan, dan penurunan posisi")
            LeaderboardCard()
        }
    }
}

// ---------- Reusable ----------

// Helper kartu/judul lab diambil dari ui/components/LabKit.kt agar konsisten.

// ---------- 1. Roda Undian ----------

@Composable
private fun SpinWheelCard() {
    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    val wheelColors = remember(palette) {
        listOf(
            palette.primary,
            palette.info,
            palette.success,
            palette.warning,
            palette.danger,
            palette.textSecondary,
        )
    }

    var spinning by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<WheelPrize?>(null) }
    var history by remember { mutableStateOf<List<String>>(emptyList()) }

    val sweep = 360f / WheelPrizes.size

    fun spin() {
        if (spinning) return
        spinning = true
        result = null
        val targetIndex = Random.nextInt(WheelPrizes.size)
        val targetRotation = rotation.value + 360f * 5f + (targetIndex * sweep + sweep / 2f)
        scope.launch {
            rotation.animateTo(
                targetValue = targetRotation,
                animationSpec = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            )
            result = WheelPrizes[targetIndex]
            history = (listOf(WheelPrizes[targetIndex].label) + history).take(4)
            spinning = false
        }
    }

    LabCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .semantics {
                    contentDescription = when {
                        spinning -> "Roda sedang berputar"
                        result != null -> "Hasil putaran: ${result?.label}"
                        else -> "Roda undian siap diputar"
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(
                modifier = Modifier
                    .size(210.dp)
                    .graphicsLayer { rotationZ = rotation.value },
            ) {
                val diameter = size.minDimension
                val radius = diameter / 2f
                val arcTopLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                val arcSize = Size(diameter - 2f, diameter - 2f)
                WheelPrizes.forEachIndexed { index, _ ->
                    val start = -90f + index * sweep
                    drawArc(
                        color = wheelColors[index % wheelColors.size],
                        startAngle = start,
                        sweepAngle = sweep,
                        useCenter = true,
                        topLeft = arcTopLeft,
                        size = arcSize,
                    )
                    // Penanda segmen: titik di tengah tiap potongan
                    val mid = (start + sweep / 2f) * (PI / 180.0)
                    val dotRadius = radius * 0.62f
                    val dx = center.x + dotRadius * cos(mid).toFloat()
                    val dy = center.y + dotRadius * sin(mid).toFloat()
                    drawCircle(Color.White.copy(alpha = 0.85f), radius = 3.dp.toPx(), center = Offset(dx, dy))
                }
                // Ring tepi
                drawCircle(
                    color = palette.border,
                    radius = radius - 1.dp.toPx(),
                    style = Stroke(width = 2.dp.toPx()),
                )
            }

            // Jarum penunjuk statis + hub
            Canvas(modifier = Modifier.size(210.dp)) {
                val cx = size.width / 2f
                val pointer = Path().apply {
                    moveTo(cx - 10.dp.toPx(), 2.dp.toPx())
                    lineTo(cx + 10.dp.toPx(), 2.dp.toPx())
                    lineTo(cx, 26.dp.toPx())
                    close()
                }
                drawPath(pointer, palette.textPrimary)
                drawCircle(palette.surface, radius = 16.dp.toPx(), center = Offset(cx, size.height / 2f))
                drawCircle(
                    color = palette.textPrimary,
                    radius = 16.dp.toPx(),
                    center = Offset(cx, size.height / 2f),
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
        }

        // Legenda
        WheelPrizes.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowItems.forEach { prize ->
                    val index = WheelPrizes.indexOf(prize)
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(wheelColors[index % wheelColors.size]),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = prize.label,
                            fontSize = 11.sp,
                            color = palette.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        result?.let { won ->
            val positive = !won.isZonk
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (positive) palette.success.copy(alpha = 0.14f) else palette.surfaceMuted,
                        RoundedCornerShape(12.dp),
                    )
                    .border(
                        1.dp,
                        if (positive) palette.success else palette.border,
                        RoundedCornerShape(12.dp),
                    )
                    .padding(12.dp),
            ) {
                Text(
                    text = if (positive) "Selamat! Kamu mendapat ${won.label}" else "Belum beruntung. Coba putar lagi.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (positive) palette.success else palette.textSecondary,
                )
            }
        }

        Surface(
            onClick = { spin() },
            enabled = !spinning,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = if (spinning) palette.surfaceMuted else palette.primary,
            border = BorderStroke(1.dp, if (spinning) palette.border else palette.primary),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Box(modifier = Modifier.padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = if (spinning) "Berputar..." else "Putar Roda",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (spinning) palette.textMuted else palette.onPrimary,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Riwayat putaran", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
            if (history.isEmpty()) {
                Text("Belum ada putaran. Hasil empat putaran terakhir muncul di sini.", fontSize = 11.sp, color = palette.textMuted)
            } else {
                history.forEach { item ->
                    Text("• $item", fontSize = 11.sp, color = palette.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

// ---------- 2. Flashcard ----------

@Composable
private fun FlashcardCard() {
    val palette = LocalAppPalette.current
    var index by remember { mutableIntStateOf(0) }
    var flipped by remember { mutableStateOf(false) }

    val flip by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 420),
        label = "flash_flip",
    )
    val card = Flashcards[index]

    LabCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .graphicsLayer {
                    rotationY = flip
                    cameraDistance = 12f * density
                }
                .background(
                    if (flipped) palette.primaryContainer else palette.surface,
                    RoundedCornerShape(16.dp),
                )
                .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                .clickable { flipped = !flipped }
                .padding(18.dp)
                .semantics {
                    contentDescription = if (flipped) "Jawaban: ${card.back}" else "Pertanyaan: ${card.front}"
                },
            contentAlignment = Alignment.Center,
        ) {
            // Saat sisi belakang menghadap layar, putar konten 180 derajat agar tidak mirror
            Column(
                modifier = Modifier.graphicsLayer { if (flipped) rotationY = 180f },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = if (flipped) "JAWABAN" else "PERTANYAAN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (flipped) palette.onPrimaryContainer else palette.textMuted,
                )
                Text(
                    text = if (flipped) card.back else card.front,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (flipped) palette.onPrimaryContainer else palette.textPrimary,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Ketuk kartu untuk membalik",
                    fontSize = 10.sp,
                    color = palette.textMuted,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                onClick = {
                    index = if (index == 0) Flashcards.size - 1 else index - 1
                    flipped = false
                },
                shape = RoundedCornerShape(12.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text("Sebelumnya", fontSize = 12.sp, color = palette.textSecondary)
                }
            }
            Text(
                text = "${index + 1} / ${Flashcards.size}",
                modifier = Modifier.weight(1f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
            )
            Surface(
                onClick = {
                    index = (index + 1) % Flashcards.size
                    flipped = false
                },
                shape = RoundedCornerShape(12.dp),
                color = palette.primary,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text("Berikutnya", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
                }
            }
        }
    }
}

// ---------- 3. Kuis ----------

@Composable
private fun QuizCard() {
    val palette = LocalAppPalette.current
    var current by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }

    if (finished) {
        LabCard {
            Text("Kuis selesai", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
            Text(
                text = "Skor akhir: $score / ${QuizQuestions.size}",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = palette.primary,
            )
            Text(
                text = when {
                    score == QuizQuestions.size -> "Sempurna! Semua jawaban benar."
                    score >= QuizQuestions.size / 2 -> "Kerja bagus, terus berlatih."
                    else -> "Masih bisa ditingkatkan. Coba lagi."
                },
                fontSize = 12.sp,
                color = palette.textSecondary,
            )
            Surface(
                onClick = {
                    current = 0
                    selected = null
                    score = 0
                    finished = false
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = palette.primary,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Box(modifier = Modifier.padding(vertical = 13.dp), contentAlignment = Alignment.Center) {
                    Text("Ulangi Kuis", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
                }
            }
        }
        return
    }

    val question = QuizQuestions[current]
    val answered = selected != null

    LabCard {
        // Indikator langkah
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            QuizQuestions.indices.forEach { i ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            when {
                                i < current -> palette.success
                                i == current -> palette.primary
                                else -> palette.border
                            },
                        ),
                )
            }
        }
        Text(
            text = "Pertanyaan ${current + 1} dari ${QuizQuestions.size}",
            fontSize = 11.sp,
            color = palette.textMuted,
        )
        Text(
            text = question.question,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textPrimary,
        )

        question.options.forEachIndexed { i, option ->
            val isCorrect = i == question.correctIndex
            val isChosen = selected == i
            val bg = when {
                answered && isCorrect -> palette.success.copy(alpha = 0.16f)
                answered && isChosen -> palette.danger.copy(alpha = 0.16f)
                else -> palette.surface
            }
            val borderColor = when {
                answered && isCorrect -> palette.success
                answered && isChosen -> palette.danger
                else -> palette.border
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(bg, RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .clickable(enabled = !answered) {
                        selected = i
                        if (isCorrect) score += 1
                    }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${'A' + i}.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textMuted,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = option,
                    modifier = Modifier.weight(1f),
                    fontSize = 13.sp,
                    color = palette.textPrimary,
                )
                if (answered && isCorrect) {
                    Text("BENAR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.success)
                } else if (answered && isChosen) {
                    Text("SALAH", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.danger)
                }
            }
        }

        if (answered) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .padding(12.dp),
            ) {
                Text(question.explanation, fontSize = 11.sp, lineHeight = 16.sp, color = palette.textSecondary)
            }
        }

        Surface(
            onClick = {
                if (current + 1 < QuizQuestions.size) {
                    current += 1
                    selected = null
                } else {
                    finished = true
                }
            },
            enabled = answered,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = if (answered) palette.primary else palette.surfaceMuted,
            border = BorderStroke(1.dp, if (answered) palette.primary else palette.border),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Box(modifier = Modifier.padding(vertical = 13.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = if (current + 1 < QuizQuestions.size) "Pertanyaan Berikutnya" else "Lihat Hasil",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (answered) palette.onPrimary else palette.textMuted,
                )
            }
        }
    }
}

// ---------- 4. Polling ----------

@Composable
private fun PollCard() {
    val palette = LocalAppPalette.current
    var voted by remember { mutableStateOf<String?>(null) }
    var votes by remember { mutableStateOf(PollOptions.associate { it.id to it.baseVotes }) }

    val total = votes.values.sum().coerceAtLeast(1)

    LabCard {
        Text(
            text = "Navigasi mana yang paling kamu pakai?",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textPrimary,
        )
        PollOptions.forEach { option ->
            val count = votes[option.id] ?: 0
            val fraction = count.toFloat() / total.toFloat()
            val animated by animateFloatAsState(
                targetValue = fraction,
                animationSpec = tween(500),
                label = "poll_${option.id}",
            )
            val chosen = voted == option.id
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surface, RoundedCornerShape(12.dp))
                    .border(1.dp, if (chosen) palette.primary else palette.border, RoundedCornerShape(12.dp))
                    .clickable(enabled = voted == null) {
                        voted = option.id
                        votes = votes.toMutableMap().apply { this[option.id] = count + 1 }
                    }
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = option.label,
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp,
                        fontWeight = if (chosen) FontWeight.Bold else FontWeight.Medium,
                        color = palette.textPrimary,
                    )
                    if (voted != null) {
                        Text("${(fraction * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                    } else if (chosen) {
                        Text("PILIHANMU", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(palette.surfaceMuted),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animated.coerceIn(0f, 1f))
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (chosen) palette.primary else palette.info),
                    )
                }
            }
        }
        Text(
            text = if (voted == null) "$total suara masuk · pilih satu opsi" else "$total suara masuk · suara kamu tercatat",
            fontSize = 11.sp,
            color = palette.textMuted,
        )
        if (voted != null) {
            Surface(
                onClick = {
                    voted = null
                    votes = PollOptions.associate { it.id to it.baseVotes }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Box(modifier = Modifier.padding(vertical = 11.dp), contentAlignment = Alignment.Center) {
                    Text("Reset Polling", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                }
            }
        }
    }
}

// ---------- 5. Papan Skor ----------

@Composable
private fun LeaderboardCard() {
    val palette = LocalAppPalette.current
    val entries = remember { Leaderboard.sortedByDescending { it.score } }

    LabCard {
        entries.forEachIndexed { index, entry ->
            val rankColor = when (index) {
                0 -> palette.warning
                1 -> palette.textSecondary
                2 -> palette.warning.copy(alpha = 0.6f)
                else -> palette.textMuted
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (entry.isMe) palette.primaryContainer else Color.Transparent, RoundedCornerShape(12.dp))
                    .border(1.dp, if (entry.isMe) palette.primary else palette.border, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(palette.surfaceMuted),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("${index + 1}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = rankColor)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.name,
                        fontSize = 13.sp,
                        fontWeight = if (entry.isMe) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (entry.isMe) palette.onPrimaryContainer else palette.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${entry.score} poin",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                }
                val deltaText = when {
                    entry.delta > 0 -> "Naik ${entry.delta}"
                    entry.delta < 0 -> "Turun ${-entry.delta}"
                    else -> "Tetap"
                }
                val deltaColor = when {
                    entry.delta > 0 -> palette.success
                    entry.delta < 0 -> palette.danger
                    else -> palette.textMuted
                }
                Text(
                    text = deltaText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = deltaColor,
                )
            }
        }
    }
}
