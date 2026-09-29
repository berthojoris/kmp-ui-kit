package com.example.uiapp.ui.blurscroll

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LuxuryColors
import com.example.uiapp.ui.components.MgIosBackButton

private val HeaderClearance = 72.dp
private val FadeZone = 72.dp
private val MaxBlur = 18.dp
private val MinScale = 0.92f
private val MaxDrift = 12.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AutoBlurScrollScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val density = LocalDensity.current
    val fadePx = with(density) { FadeZone.toPx() }
    val maxBlurPx = with(density) { MaxBlur.toPx() }
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val topClearance = statusBarTop + HeaderClearance
    val driftPx = with(density) { MaxDrift.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LuxuryColors.Background),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = topClearance,
                bottom = 40.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(DemoTransactions, key = { it.id }) { item ->
                AutoBlurEdgeItem(fadePx = fadePx, maxBlurPx = maxBlurPx, driftPx = driftPx) {
                    TransactionCard(item)
                }
            }
        }

        TransparentHeader(onBack = onBack)
    }
}

private fun smoothStep(t: Float): Float = t * t * (3f - 2f * t)

@Composable
private fun AutoBlurEdgeItem(
    fadePx: Float,
    maxBlurPx: Float,
    driftPx: Float,
    content: @Composable () -> Unit,
) {
    var topInList by remember { mutableFloatStateOf(fadePx) }
    val blurCache = remember { mutableMapOf<Int, BlurEffect>() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                val linear = (topInList / fadePx).coerceIn(0f, 1f)
                val progress = smoothStep(linear)
                val dissolve = 1f - progress

                alpha = progress

                val shrink = 1f - dissolve * (1f - MinScale)
                scaleX = shrink
                scaleY = shrink
                translationY = -dissolve * driftPx

                val radius = dissolve * maxBlurPx
                renderEffect = if (radius > 0.5f) {
                    val key = radius.toInt()
                    blurCache.getOrPut(key) { BlurEffect(radius, radius, TileMode.Clamp) }
                } else {
                    null
                }
            }
            .onGloballyPositioned { coordinates ->
                topInList = coordinates.positionInParent().y
            },
    ) {
        content()
    }
}

@Composable
private fun TransparentHeader(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MgIosBackButton(
                onClick = onBack,
                backgroundColor = Color.White.copy(alpha = 0.85f),
                borderColor = LuxuryColors.SurfaceBorder,
                iconTint = LuxuryColors.TextPrimary,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Auto-blur Scroll",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = LuxuryColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Data memudar di tepi atas layar",
                    fontSize = 11.sp,
                    color = LuxuryColors.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun TransactionCard(item: DemoItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = LuxuryColors.SurfaceWhite,
        border = BorderStroke(1.dp, LuxuryColors.SurfaceBorder),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(LuxuryColors.SurfaceMuted),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${item.id}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LuxuryColors.TextPrimary,
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LuxuryColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.subtitle,
                    fontSize = 12.sp,
                    color = LuxuryColors.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = item.amount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LuxuryColors.TextPrimary,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(5.dp))
                StatusBadge(item.status)
            }
        }
    }
}

@Composable
private fun StatusBadge(status: DemoStatus) {
    val background: Color
    val foreground: Color
    val label: String
    when (status) {
        DemoStatus.PAID -> {
            background = Color(0xFFE8F5E9); foreground = LuxuryColors.Accent; label = "PAID"
        }
        DemoStatus.PENDING -> {
            background = Color(0xFFFFF7ED); foreground = LuxuryColors.AccentWarning; label = "PENDING"
        }
        DemoStatus.REFUND -> {
            background = Color(0xFFEFF6FF); foreground = LuxuryColors.AccentInfo; label = "REFUND"
        }
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = foreground,
        )
    }
}
