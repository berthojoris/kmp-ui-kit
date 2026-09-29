package com.example.uiapp.ui.motionlab

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.icons.MgIosBackButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val HeaderHeight = 246.dp
private val RefreshThreshold = 76.dp

private data class InboxItem(
    val id: Int,
    val sender: String,
    val subject: String,
    val preview: String,
    val time: String,
)

private val SeedInbox = listOf(
    InboxItem(1, "Ayu Prameswari", "Konfirmasi reservasi villa", "Pembayaran sudah kami terima, terima kasih.", "2 mnt"),
    InboxItem(2, "Bagas Nugroho", "Jadwal kunjungan", "Bisa dijadwalkan Sabtu pagi sekitar jam 10.", "18 mnt"),
    InboxItem(3, "Citra Halim", "Permintaan khusus", "Mohon siapkan kursi bayi di ruang makan.", "1 jam"),
    InboxItem(4, "Damar Wibowo", "Tambahan tamu", "Kami akan membawa 2 tamu tambahan.", "3 jam"),
    InboxItem(5, "Eka Lestari", "Ulasan bintang lima", "Menginap yang luar biasa, akan kembali lagi.", "5 jam"),
    InboxItem(6, "Fajar Ramadhan", "Transfer bandara", "Berapa biaya transfer VIP dari Denpasar?", "Kemarin"),
    InboxItem(7, "Gita Maharani", "Reschedule", "Apakah bisa geser tanggal keberangkatan?", "Kemarin"),
    InboxItem(8, "Hendra Saputra", "Permintaan faktur", "Tolong kirimkan faktur perusahaan kami.", "2 hari"),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MotionLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    BackHandler(enabled = true) { onBack() }

    val items = remember { mutableStateListOf<InboxItem>().apply { addAll(SeedInbox) } }
    val listState = rememberLazyListState()
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val density = LocalDensity.current
    val headerPx = with(density) { HeaderHeight.toPx() }
    val thresholdPx = with(density) { RefreshThreshold.toPx() }
    val maxPullPx = thresholdPx * 1.7f

    val pullOffset = remember { mutableFloatStateOf(0f) }
    val refreshing = remember { mutableStateOf(false) }

    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0f && pullOffset.floatValue > 0f) {
                    val consumed = available.y.coerceAtLeast(-pullOffset.floatValue)
                    pullOffset.floatValue += consumed
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (!refreshing.value &&
                    pullOffset.floatValue < maxPullPx &&
                    available.y > 0f &&
                    !listState.canScrollBackward
                ) {
                    val delta = available.y.coerceAtMost(maxPullPx - pullOffset.floatValue)
                    pullOffset.floatValue += delta
                    return Offset(0f, delta)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (refreshing.value) return Velocity.Zero
                if (pullOffset.floatValue >= thresholdPx) {
                    refreshing.value = true
                    scope.launch {
                        animate(
                            initialValue = pullOffset.floatValue,
                            targetValue = thresholdPx,
                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                        ) { value, _ -> pullOffset.floatValue = value }
                        delay(1500)
                        refreshing.value = false
                        animate(
                            initialValue = pullOffset.floatValue,
                            targetValue = 0f,
                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                        ) { value, _ -> pullOffset.floatValue = value }
                    }
                } else {
                    scope.launch {
                        animate(
                            initialValue = pullOffset.floatValue,
                            targetValue = 0f,
                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                        ) { value, _ -> pullOffset.floatValue = value }
                    }
                }
                return Velocity.Zero
            }
        }
    }

    val collapse = (
        (listState.firstVisibleItemIndex * headerPx + listState.firstVisibleItemScrollOffset) / headerPx
        ).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background),
    ) {
        CollapsingHero(
            collapse = collapse,
            modifier = Modifier
                .fillMaxWidth()
                .height(HeaderHeight),
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(connection),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = HeaderHeight + 4.dp,
                bottom = 96.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(key = "motion-subheader") {
                Text(
                    text = "Geser kartu ke kiri untuk mengarsipkan \u00B7 tarik ke bawah untuk menyegarkan",
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    color = palette.textMuted,
                )
            }
            items(items, key = { it.id }) { item ->
                SwipeRow(
                    item = item,
                    onArchive = {
                        val index = items.indexOfFirst { it.id == item.id }
                        if (index >= 0) {
                            items.removeAt(index)
                            scope.launch {
                                val result = snackbarHost.showSnackbar(
                                    message = "\"${item.subject}\" diarsipkan",
                                    actionLabel = "Urungkan",
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    items.add(index.coerceAtMost(items.size), item)
                                }
                            }
                        }
                    },
                )
            }
        }

        CompactTopBar(
            collapse = collapse,
            onBack = onBack,
        )

        PullIndicator(
            pull = pullOffset.floatValue,
            threshold = thresholdPx,
            refreshing = refreshing.value,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 58.dp),
        )

        SnackbarHost(
            hostState = snackbarHost,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
                )
                .padding(16.dp),
        ) { data ->
            Snackbar(
                snackbarData = data,
                shape = RoundedCornerShape(14.dp),
                containerColor = palette.textPrimary,
                contentColor = Color.White,
                actionColor = palette.success,
            )
        }
    }
}

@Composable
private fun CollapsingHero(collapse: Float, modifier: Modifier) {
    Box(
        modifier = modifier.graphicsLayer {
            translationY = -collapse * 60.dp.toPx()
            alpha = 1f - collapse * 0.25f
        },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F3D34), Color(0xFF0A332C), Color(0xFF05201B)),
                ),
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.07f),
                radius = size.minDimension * 0.5f,
                center = Offset(size.width * 0.84f, size.height * 0.22f),
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = size.minDimension * 0.3f,
                center = Offset(size.width * 0.14f, size.height * 0.72f),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                text = "MOTION LAB",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                color = Color.White.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Kotak Masuk",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                letterSpacing = (-0.5).sp,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${SeedInbox.size} pesan \u00B7 3 belum dibaca",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.72f),
            )
            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}

@Composable
private fun CompactTopBar(collapse: Float, onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                palette.surface.copy(alpha = collapse),
            )
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MgIosBackButton(
                onClick = onBack,
                backgroundColor = palette.surfaceMuted.copy(alpha = 0.4f + collapse * 0.6f),
                borderColor = palette.border,
                iconTint = palette.textPrimary,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Kotak Masuk",
                modifier = Modifier.graphicsLayer { alpha = collapse },
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.border.copy(alpha = collapse)),
        )
    }
}

@Composable
private fun PullIndicator(
    pull: Float,
    threshold: Float,
    refreshing: Boolean,
    modifier: Modifier,
) {
    val palette = LocalAppPalette.current

    val transition = rememberInfiniteTransition(label = "pull-spin")
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
        ),
        label = "spin",
    )

    val fraction = (pull / threshold).coerceIn(0f, 1f)
    if (fraction <= 0.001f && !refreshing) return

    Surface(
        modifier = modifier
            .graphicsLayer {
                translationY = pull
                alpha = if (refreshing) 1f else fraction
                scaleX = 0.7f + fraction * 0.3f
                scaleY = 0.7f + fraction * 0.3f
            }
            .size(44.dp),
        shape = CircleShape,
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(24.dp)) {
                val stroke = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round)
                val inset = 1.5.dp.toPx()
                if (refreshing) {
                    drawArc(
                        color = palette.surfaceMuted,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = androidx.compose.ui.geometry.Size(
                            size.width - inset * 2,
                            size.height - inset * 2,
                        ),
                        style = stroke,
                    )
                    drawArc(
                        color = palette.primary,
                        startAngle = spin,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = androidx.compose.ui.geometry.Size(
                            size.width - inset * 2,
                            size.height - inset * 2,
                        ),
                        style = stroke,
                    )
                } else {
                    drawArc(
                        color = palette.primary,
                        startAngle = -90f,
                        sweepAngle = 320f * fraction,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = androidx.compose.ui.geometry.Size(
                            size.width - inset * 2,
                            size.height - inset * 2,
                        ),
                        style = stroke,
                    )
                }
            }
        }
    }
}

@Composable
private fun SwipeRow(item: InboxItem, onArchive: () -> Unit) {
    val palette = LocalAppPalette.current

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onArchive()
                true
            } else {
                false
            }
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(palette.textPrimary)
                    .padding(horizontal = 22.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ArchiveGlyph(tint = Color.White)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Arsipkan",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            }
        },
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(palette.surfaceMuted),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.sender.take(1),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.sender,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.subject,
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.preview,
                        fontSize = 11.sp,
                        color = palette.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = item.time,
                    fontSize = 11.sp,
                    color = palette.textMuted,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun ArchiveGlyph(tint: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
        drawRect(
            color = tint,
            topLeft = Offset(w * 0.08f, h * 0.24f),
            size = androidx.compose.ui.geometry.Size(w * 0.84f, h * 0.66f),
            style = stroke,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.08f, h * 0.42f),
            end = Offset(w * 0.92f, h * 0.42f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.08f, h * 0.24f),
            end = Offset(w * 0.28f, h * 0.08f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.28f, h * 0.08f),
            end = Offset(w * 0.72f, h * 0.08f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.72f, h * 0.08f),
            end = Offset(w * 0.92f, h * 0.24f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}
