package com.example.uiapp.ui.emptystate

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private enum class DemoState(val label: String) {
    LOADING("Loading"),
    EMPTY("Empty"),
    ERROR("Error"),
    CONTENT("Content"),
}

private data class ActivityItem(
    val id: Int,
    val name: String,
    val message: String,
    val time: String,
    val tag: String,
    val tagColor: Color,
    val tagBackground: Color,
)

private val Activities = listOf(
    ActivityItem(1, "Ayu Prameswari", "Pembayaran villa terverifikasi.", "2 mnt", "PAID", Color(0xFF10B981), Color(0xFFE8F5E9)),
    ActivityItem(2, "Bagas Nugroho", "Jadwal kunjungan dikonfirmasi.", "18 mnt", "DONE", Color(0xFF0A332C), Color(0xFFE8F5E9)),
    ActivityItem(3, "Citra Halim", "Pemesanan baru untuk 4 tamu.", "1 jam", "NEW", Color(0xFF3B82F6), Color(0xFFEFF6FF)),
    ActivityItem(4, "Damar Wibowo", "Menambahkan paket sarapan.", "3 jam", "UPDATE", Color(0xFFF59E0B), Color(0xFFFFF7ED)),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmptyStateScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    BackHandler(enabled = true) { onBack() }

    var state by remember { mutableStateOf(DemoState.EMPTY) }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Empty & Error State",
                subtitle = "Kondisi kosong, gagal, dan berhasil",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            StateSwitcher(
                selected = state,
                onSelect = { state = it },
            )

            Crossfade(
                targetState = state,
                animationSpec = tween(durationMillis = 320),
                label = "empty-state",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { target ->
                when (target) {
                    DemoState.LOADING -> LoadingState()
                    DemoState.EMPTY -> EmptyState(onRefresh = { state = DemoState.CONTENT })
                    DemoState.ERROR -> ErrorState(
                        onRetry = { state = DemoState.LOADING },
                        onSupport = { state = DemoState.EMPTY },
                    )
                    DemoState.CONTENT -> ContentState()
                }
            }
        }
    }
}

@Composable
private fun StateSwitcher(
    selected: DemoState,
    onSelect: (DemoState) -> Unit,
) {
    val palette = LocalAppPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DemoState.entries.forEach { entry ->
            val isSelected = entry == selected
            Surface(
                onClick = { onSelect(entry) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) palette.primary else palette.surface,
                border = BorderStroke(
                    1.dp,
                    if (isSelected) palette.primary else palette.border,
                ),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = entry.label,
                    modifier = Modifier.padding(vertical = 9.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else palette.textSecondary,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 780, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PulseBlock(alpha = alpha, modifier = Modifier.fillMaxWidth().height(132.dp))
        repeat(5) {
            PulseBlock(alpha = alpha, modifier = Modifier.fillMaxWidth().height(74.dp))
        }
    }
}

@Composable
private fun PulseBlock(alpha: Float, modifier: Modifier) {
    val palette = LocalAppPalette.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(palette.surfaceMuted.copy(alpha = alpha)),
    )
}

@Composable
private fun EmptyState(onRefresh: () -> Unit) {
    CenteredState(
        illustration = { StatusIllustration(kind = IllustrationKind.EMPTY) },
        eyebrow = "KOSONG",
        title = "Belum ada penginapan",
        message = "Simpan properti favorit Anda dan kelola semuanya dalam satu tempat.",
        primaryLabel = "Tambah Penginapan",
        onPrimary = onRefresh,
        secondaryLabel = "Jelajahi Dulu",
        onSecondary = onRefresh,
    )
}

@Composable
private fun ErrorState(onRetry: () -> Unit, onSupport: () -> Unit) {
    CenteredState(
        illustration = { StatusIllustration(kind = IllustrationKind.ERROR) },
        eyebrow = "GAGAL MEMUAT",
        title = "Koneksi terputus",
        message = "Kami tidak bisa mengambil data. Periksa jaringan Anda lalu coba lagi.",
        primaryLabel = "Coba Lagi",
        onPrimary = onRetry,
        secondaryLabel = "Hubungi Dukungan",
        onSecondary = onSupport,
    )
}

@Composable
private fun CenteredState(
    illustration: @Composable () -> Unit,
    eyebrow: String,
    title: String,
    message: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    secondaryLabel: String,
    onSecondary: () -> Unit,
) {
    val palette = LocalAppPalette.current

    val enter by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessLow,
        ),
        label = "stateEnter",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .drawBehind {
                val a = enter.coerceIn(0f, 1f)
                drawCircle(
                    color = palette.primaryContainer.copy(alpha = a * 0.5f),
                    radius = size.minDimension * 0.34f,
                    center = Offset(size.width * 0.5f, size.height * 0.34f),
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        illustration()

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = eyebrow,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.6.sp,
            color = palette.textMuted,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            letterSpacing = (-0.3).sp,
            color = palette.textPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            color = palette.textSecondary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(28.dp))

        PrimaryButton(label = primaryLabel, onClick = onPrimary)

        Spacer(modifier = Modifier.height(12.dp))

        GhostButton(label = secondaryLabel, onClick = onSecondary)
    }
}

@Composable
private fun PrimaryButton(label: String, onClick: () -> Unit) {
    val palette = LocalAppPalette.current

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = palette.primary,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(vertical = 15.dp),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    }
}

@Composable
private fun GhostButton(label: String, onClick: () -> Unit) {
    val palette = LocalAppPalette.current

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(vertical = 15.dp),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textPrimary,
        )
    }
}

private enum class IllustrationKind { EMPTY, ERROR }

@Composable
private fun StatusIllustration(kind: IllustrationKind) {
    val palette = LocalAppPalette.current

    Surface(
        shape = CircleShape,
        color = palette.surfaceMuted,
        modifier = Modifier.size(122.dp),
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(56.dp)) {
                val w = size.width
                val h = size.height
                val stroke = Stroke(
                    width = 2.4.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                )

                when (kind) {
                    IllustrationKind.EMPTY -> {
                        drawRoundRect(
                            color = palette.textMuted.copy(alpha = 0.75f),
                            topLeft = Offset(w * 0.06f, h * 0.24f),
                            size = Size(w * 0.88f, h * 0.62f),
                            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
                            style = stroke,
                        )
                        drawLine(
                            color = palette.textMuted.copy(alpha = 0.75f),
                            start = Offset(w * 0.06f, h * 0.42f),
                            end = Offset(w * 0.94f, h * 0.42f),
                            strokeWidth = 2.4.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                        drawLine(
                            color = palette.primary,
                            start = Offset(w * 0.5f, h * 0.52f),
                            end = Offset(w * 0.5f, h * 0.76f),
                            strokeWidth = 2.8.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                        drawLine(
                            color = palette.primary,
                            start = Offset(w * 0.36f, h * 0.64f),
                            end = Offset(w * 0.64f, h * 0.64f),
                            strokeWidth = 2.8.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }

                    IllustrationKind.ERROR -> {
                        drawCircle(
                            color = palette.danger.copy(alpha = 0.9f),
                            radius = w * 0.44f,
                            center = Offset(w * 0.5f, h * 0.5f),
                            style = stroke,
                        )
                        drawLine(
                            color = palette.danger,
                            start = Offset(w * 0.5f, h * 0.28f),
                            end = Offset(w * 0.5f, h * 0.56f),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                        drawCircle(
                            color = palette.danger,
                            radius = 2.2.dp.toPx(),
                            center = Offset(w * 0.5f, h * 0.72f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentState() {
    val palette = LocalAppPalette.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "content-header") {
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = "Aktivitas Terbaru",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Data berhasil dimuat \u00B7 4 pembaruan",
                    fontSize = 12.sp,
                    color = palette.textMuted,
                )
            }
        }
        items(Activities, key = { it.id }) { item ->
            ActivityRow(item)
        }
    }
}

@Composable
private fun ActivityRow(item: ActivityItem) {
    val palette = LocalAppPalette.current

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
                    text = item.name.take(1),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.message,
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = item.time,
                    fontSize = 11.sp,
                    color = palette.textMuted,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.tag,
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(item.tagBackground)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = item.tagColor,
                )
            }
        }
    }
}
