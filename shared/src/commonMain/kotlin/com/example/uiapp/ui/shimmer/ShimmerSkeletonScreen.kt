package com.example.uiapp.ui.shimmer

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShimmerSkeletonScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    BackHandler(enabled = true) { onBack() }

    val shimmer = rememberShimmerOffset()
    var reloadKey by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(reloadKey) {
        isLoading = true
        delay(2300)
        isLoading = false
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Shimmer Skeleton",
                subtitle = "Placeholder loading berkilau",
                onBack = onBack,
                action = {
                    Text(
                        text = "Muat Ulang",
                        modifier = Modifier
                            .clip(RoundedCornerShape(11.dp))
                            .background(palette.surfaceMuted)
                            .clickable { reloadKey++ }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                        maxLines = 1,
                    )
                },
            )
        },
    ) { innerPadding ->
        Crossfade(
            targetState = isLoading,
            animationSpec = tween(durationMillis = 450),
            label = "shimmer-content",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) { loading ->
            if (loading) {
                SkeletonList(shimmer)
            } else {
                ContentList()
            }
        }
    }
}

@Composable
private fun rememberShimmerOffset(): State<Float> {
    val transition = rememberInfiniteTransition(label = "shimmer")
    return transition.animateFloat(
        initialValue = 0f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerX",
    )
}

@Composable
private fun ShimmerBlock(
    shimmer: State<Float>,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .drawBehind {
                val x = shimmer.value
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFE7EAEE), Color(0xFFF5F6F8), Color(0xFFE7EAEE)),
                        start = Offset(x - 700f, 0f),
                        end = Offset(x, 0f),
                    ),
                )
            },
    )
}

@Composable
private fun SkeletonList(shimmer: State<Float>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp, bottom = 28.dp),
    ) {
        FeaturedSkeleton(
            shimmer = shimmer,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(modifier = Modifier.height(14.dp))
        repeat(6) {
            SkeletonCard(
                shimmer = shimmer,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun FeaturedSkeleton(shimmer: State<Float>, modifier: Modifier) {
    val palette = LocalAppPalette.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ShimmerBlock(
                shimmer = shimmer,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp),
            )
            Spacer(modifier = Modifier.height(14.dp))
            ShimmerBlock(
                shimmer = shimmer,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(15.dp),
            )
            Spacer(modifier = Modifier.height(9.dp))
            ShimmerBlock(
                shimmer = shimmer,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(11.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            ShimmerBlock(
                shimmer = shimmer,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(11.dp),
            )
        }
    }
}

@Composable
private fun SkeletonCard(shimmer: State<Float>, modifier: Modifier) {
    val palette = LocalAppPalette.current

    Surface(
        modifier = modifier.fillMaxWidth(),
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
            ShimmerBlock(
                shimmer = shimmer,
                shape = RoundedCornerShape(13.dp),
                modifier = Modifier.size(46.dp),
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                ShimmerBlock(
                    shimmer = shimmer,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(13.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                ShimmerBlock(
                    shimmer = shimmer,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(10.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            ShimmerBlock(
                shimmer = shimmer,
                shape = RoundedCornerShape(9.dp),
                modifier = Modifier
                    .width(54.dp)
                    .height(22.dp),
            )
        }
    }
}

private data class FeedItem(
    val id: Int,
    val name: String,
    val message: String,
    val time: String,
    val tag: String,
    val tagColor: Color,
    val tagBackground: Color,
)

private val Feed: List<FeedItem> = listOf(
    FeedItem(1, "Ayu Prameswari", "Menyelesaikan pembayaran untuk Villa Ubud.", "2 mnt", "PAID", Color(0xFF10B981), Color(0xFFE8F5E9)),
    FeedItem(2, "Bagas Nugroho", "Menunggu konfirmasi jadwal kunjungan.", "18 mnt", "PENDING", Color(0xFFF59E0B), Color(0xFFFFF7ED)),
    FeedItem(3, "Citra Halim", "Mengajukan refund untuk pemesanan.", "1 jam", "REFUND", Color(0xFF3B82F6), Color(0xFFEFF6FF)),
    FeedItem(4, "Damar Wibowo", "Menambahkan 2 tamu ke reservasi.", "3 jam", "UPDATE", Color(0xFF0A332C), Color(0xFFE8F5E9)),
    FeedItem(5, "Eka Lestari", "Mengirim ulasan 5 bintang.", "5 jam", "REVIEW", Color(0xFF0A332C), Color(0xFFF3F4F6)),
    FeedItem(6, "Fajar Ramadhan", "Menjadwalkan ulang transfer VIP.", "Kemarin", "UPDATE", Color(0xFF0A332C), Color(0xFFE8F5E9)),
    FeedItem(7, "Gita Maharani", "Pembayaran berhasil diverifikasi.", "Kemarin", "PAID", Color(0xFF10B981), Color(0xFFE8F5E9)),
)

@Composable
private fun ContentList() {
    val palette = LocalAppPalette.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "feed-header") {
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = "Aktivitas Terbaru",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Data berhasil dimuat \u00B7 7 pembaruan",
                    fontSize = 12.sp,
                    color = palette.textMuted,
                )
            }
        }
        items(Feed, key = { it.id }) { item ->
            FeedCard(item)
        }
    }
}

@Composable
private fun FeedCard(item: FeedItem) {
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
