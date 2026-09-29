package com.example.uiapp.ui.scrollmotion

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.AppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt

// ---------- Data mock lokal ----------

private val TickerHeadlines = listOf(
    "IHSG naik 1,2% ke 7.420, sektor teknologi memimpin",
    "Rupiah menguat ke Rp15.780 per dolar AS",
    "Obligasi SBN 10 tahun turun 3 bps",
    "Harga emas dunia sentuh rekor baru",
)

private data class HeroSlide(val id: String, val title: String, val subtitle: String)

private val HeroSlides = listOf(
    HeroSlide("h1", "Ringkasan Portofolio", "Performa mingguan pada satu layar"),
    HeroSlide("h2", "Insight Pasar", "Tren sektor dan aliran dana asing"),
    HeroSlide("h3", "Rencana Finansial", "Target dan progres tabunganmu"),
)

private val MorphTabs = listOf("Ringkasan", "Aktivitas", "Insight", "Pengaturan")

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScrollMotionLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Motion & Scroll Lanjutan",
                subtitle = "Marquee, carousel, tab morphing, flip clock",
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
                title = "Gerak yang Dikendalikan Waktu",
                body = "Marquee berjalan tanpa henti, carousel berpindah otomatis, indikator tab bergeser mulus, dan digit flip clock dianimasikan per detik. Semua pakai coroutine dan animasi Compose, bukan timer thread.",
            )

            LabSectionTitle("1. Marquee Ticker", "Headline berjalan otomatis, dapat dijeda")
            MarqueeCard()

            LabSectionTitle("2. Auto-Loop Carousel", "Hero berpindah sendiri dengan efek parallax")
            CarouselCard()

            LabSectionTitle("3. Morphing Tab Indicator", "Penanda tab bergeser mengikuti pilihan")
            MorphingTabsCard()

            LabSectionTitle("4. Flip Clock", "Hitung mundur dengan animasi balik digit")
            FlipClockCard()
        }
    }
}

// ---------- 1. Marquee ----------

@Composable
private fun MarqueeCard() {
    val palette = LocalAppPalette.current
    var paused by remember { mutableStateOf(false) }
    var contentWidth by remember { mutableIntStateOf(0) }

    val shift = remember { Animatable(0f) }

    LaunchedEffect(paused, contentWidth) {
        if (paused || contentWidth <= 0) return@LaunchedEffect
        while (true) {
            val remaining = contentWidth - shift.value
            val duration = (remaining / contentWidth * 12000f).roundToInt().coerceAtLeast(32)
            shift.animateTo(
                targetValue = contentWidth.toFloat(),
                animationSpec = tween(durationMillis = duration, easing = LinearEasing),
            )
            shift.snapTo(0f)
        }
    }

    LabCard {
        Text(
            text = "BERITA PASAR",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = palette.primary,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clipToBounds()
                .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                .semantics { contentDescription = "Ticker headline berjalan" },
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer { translationX = -shift.value }
                    .onGloballyPositioned { coords -> contentWidth = coords.size.width },
            ) {
                MarqueeRow(palette)
            }
            // Salinan kedua untuk loop mulus
            Box(
                modifier = Modifier
                    .graphicsLayer { translationX = if (contentWidth == 0) 0f else contentWidth - shift.value },
            ) {
                MarqueeRow(palette)
            }
        }
        Surface(
            onClick = { paused = !paused },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Box(modifier = Modifier.padding(vertical = 11.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = if (paused) "Lanjutkan" else "Jeda",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun MarqueeRow(palette: AppPalette) {
    Row(
        modifier = Modifier
            .height(46.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TickerHeadlines.forEach { headline ->
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clipToBounds()
                    .background(palette.primary, RoundedCornerShape(3.dp)),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = headline,
                fontSize = 12.sp,
                color = palette.textSecondary,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

// ---------- 2. Carousel ----------

@Composable
private fun CarouselCard() {
    val palette = LocalAppPalette.current
    var running by remember { mutableStateOf(true) }
    val pagerState = rememberPagerState(pageCount = { HeroSlides.size })
    val density = LocalDensity.current

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        while (true) {
            delay(3200)
            val next = (pagerState.currentPage + 1) % HeroSlides.size
            pagerState.animateScrollToPage(next)
        }
    }

    LabCard {
        HorizontalPager(
            state = pagerState,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp),
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        ) { page ->
            val offset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
            val scale = 1f - (abs(offset) * 0.12f).coerceAtMost(0.24f)
            val slide = HeroSlides[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = 1f - (abs(offset) * 0.35f).coerceAtMost(0.5f)
                    }
                    .clipToBounds()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                when (page % 3) {
                                    0 -> palette.primary
                                    1 -> palette.info
                                    else -> palette.success
                                },
                                when (page % 3) {
                                    0 -> palette.info
                                    1 -> palette.success
                                    else -> palette.primary
                                },
                            ),
                        ),
                        RoundedCornerShape(16.dp),
                    )
                    .padding(18.dp),
                contentAlignment = Alignment.BottomStart,
            ) {
                // Ilustrasi geometris mock
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(96.dp),
                ) {
                    drawCircle(palette.onPrimary.copy(alpha = 0.18f), radius = size.minDimension * 0.42f)
                    drawCircle(palette.onPrimary.copy(alpha = 0.12f), radius = size.minDimension * 0.26f, center = Offset(size.width * 0.3f, size.height * 0.7f))
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = slide.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.onPrimary,
                    )
                    Text(
                        text = slide.subtitle,
                        fontSize = 12.sp,
                        color = palette.onPrimary.copy(alpha = 0.9f),
                    )
                }
            }
        }

        // Dots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            HeroSlides.indices.forEach { index ->
                val active = index == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .width(if (active) 18.dp else 7.dp)
                        .height(7.dp)
                        .clipToBounds()
                        .background(
                            if (active) palette.primary else palette.border,
                            RoundedCornerShape(4.dp),
                        ),
                )
            }
        }

        Surface(
            onClick = { running = !running },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Box(modifier = Modifier.padding(vertical = 11.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = if (running) "Jeda Auto-Loop" else "Mulai Auto-Loop",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textSecondary,
                )
            }
        }
    }
}

// ---------- 3. Morphing Tabs ----------

@Composable
private fun MorphingTabsCard() {
    val palette = LocalAppPalette.current
    var selected by remember { mutableIntStateOf(0) }
    val positions = remember { mutableStateMapOf<Int, Pair<Float, Float>>() }
    val density = LocalDensity.current

    val targetX = positions[selected]?.first ?: 0f
    val targetW = positions[selected]?.second ?: 0f
    val indicatorX by animateFloatAsState(targetValue = targetX, animationSpec = tween(280), label = "tab_x")
    val indicatorW by animateFloatAsState(targetValue = targetW, animationSpec = tween(280), label = "tab_w")

    LabCard {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (targetW > 0f) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(indicatorX.roundToInt(), 0) }
                        .width(with(density) { indicatorW.toDp() })
                        .height(34.dp)
                        .clipToBounds()
                        .background(palette.primary, RoundedCornerShape(17.dp)),
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                MorphTabs.forEachIndexed { index, tab ->
                    val active = index == selected
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .onGloballyPositioned { coords ->
                                positions[index] = coords.positionInParent().x to coords.size.width.toFloat()
                            }
                            .clickable { selected = index },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = tab,
                            fontSize = 12.sp,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                            color = if (active) palette.onPrimary else palette.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        // Konten berubah mengikuti tab
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                .padding(14.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Konten ${MorphTabs[selected]}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )
                Text(
                    text = when (selected) {
                        0 -> "Ringkasan performa bulan ini dalam beberapa angka kunci."
                        1 -> "Riwayat aktivitas terbaru yang dikelompokkan per hari."
                        2 -> "Insight otomatis dari pola data mock yang tersedia."
                        else -> "Preferensi tampilan dan notifikasi versi demo."
                    },
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = palette.textSecondary,
                )
            }
        }

        // Diagram mini penanda posisi
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MorphTabs.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clipToBounds()
                            .background(
                                if (index == selected) palette.primary else palette.border,
                                RoundedCornerShape(2.dp),
                            ),
                    )
                }
            }
        }
    }
}

// ---------- 4. Flip Clock ----------

@Composable
private fun FlipClockCard() {
    val palette = LocalAppPalette.current
    val presets = listOf(1, 3, 5)
    var totalSeconds by remember { mutableIntStateOf(3 * 60) }
    var secondsLeft by remember { mutableIntStateOf(3 * 60) }
    var running by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }

    LaunchedEffect(running) {
        while (running && secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }
        if (running && secondsLeft <= 0) {
            running = false
            finished = true
        }
    }

    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val minuteTens = minutes / 10
    val minuteOnes = minutes % 10
    val secondTens = seconds / 10
    val secondOnes = seconds % 10

    LabCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            presets.forEach { preset ->
                val selected = totalSeconds == preset * 60
                Surface(
                    onClick = {
                        if (!running) {
                            totalSeconds = preset * 60
                            secondsLeft = preset * 60
                            finished = false
                        }
                    },
                    enabled = !running,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = if (selected) palette.primary else palette.surface,
                    border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Box(modifier = Modifier.padding(vertical = 9.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "$preset mnt",
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) palette.onPrimary else palette.textSecondary,
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Sisa waktu $minutes menit $seconds detik" },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FlipDigit(minuteTens)
            FlipDigit(minuteOnes)
            Text(":", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = palette.textMuted, modifier = Modifier.padding(horizontal = 6.dp))
            FlipDigit(secondTens)
            FlipDigit(secondOnes)
        }

        Text(
            text = when {
                finished -> "Waktu habis"
                running -> "Berjalan"
                secondsLeft < totalSeconds -> "Dijeda"
                else -> "Siap"
            },
            modifier = Modifier.fillMaxWidth(),
            fontSize = 12.sp,
            color = palette.textMuted,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Surface(
                onClick = {
                    if (finished) {
                        secondsLeft = totalSeconds
                        finished = false
                    } else {
                        running = !running
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                color = palette.primary,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Box(modifier = Modifier.padding(vertical = 13.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = when {
                            finished -> "Mulai Lagi"
                            running -> "Jeda"
                            secondsLeft < totalSeconds -> "Lanjutkan"
                            else -> "Mulai"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.onPrimary,
                    )
                }
            }
            Surface(
                onClick = {
                    running = false
                    finished = false
                    secondsLeft = totalSeconds
                },
                enabled = running || secondsLeft < totalSeconds || finished,
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 13.dp), contentAlignment = Alignment.Center) {
                    Text("Reset", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                }
            }
        }
    }
}

@Composable
private fun FlipDigit(value: Int) {
    val palette = LocalAppPalette.current
    val anim = remember { Animatable(0f) }
    LaunchedEffect(value) {
        anim.snapTo(-90f)
        anim.animateTo(0f, tween(durationMillis = 260))
    }
    Box(
        modifier = Modifier
            .size(width = 46.dp, height = 62.dp)
            .graphicsLayer {
                rotationX = anim.value
                cameraDistance = 12f * density
            }
            .clipToBounds()
            .background(palette.surfaceMuted, RoundedCornerShape(10.dp))
            .border(1.dp, palette.border, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text("$value", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
    }
}
