package com.example.uiapp.ui.parallaxhero

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LuxuryColors
import com.example.uiapp.ui.components.icons.MgIosBackButton

private val HeroHeight = 340.dp
private val BarFadeDistance = 340f

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ParallaxHeroScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val heroHeightPx = with(density) { HeroHeight.toPx() }
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LuxuryColors.Background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            Hero(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HeroHeight)
                    .graphicsLayer {
                        val s = scrollState.value.toFloat()
                        translationY = if (s < 0f) s else s * 0.5f
                        val overscroll = (-s).coerceAtLeast(0f)
                        val stretch = 1f + overscroll / heroHeightPx
                        scaleX = stretch
                        scaleY = stretch
                        transformOrigin = TransformOrigin(0.5f, 0f)
                    },
            )

            DetailSheet(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-28).dp)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(LuxuryColors.SurfaceWhite)
                    .padding(horizontal = 24.dp, vertical = 26.dp),
            )
        }

        CollapsingBar(
            title = "The Obsidian Escape",
            scrollState = scrollState,
            statusBarTop = statusBarTop,
            onBack = onBack,
        )
    }
}

@Composable
private fun Hero(modifier: Modifier) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F3D34), Color(0xFF0A332C), Color(0xFF05201B)),
                ),
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.06f),
                radius = size.minDimension * 0.55f,
                center = Offset(size.width * 0.86f, size.height * 0.24f),
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.04f),
                radius = size.minDimension * 0.34f,
                center = Offset(size.width * 0.12f, size.height * 0.78f),
            )
            drawLine(
                color = Color.White.copy(alpha = 0.07f),
                start = Offset(0f, size.height * 0.62f),
                end = Offset(size.width, size.height * 0.38f),
                strokeWidth = 2f,
            )
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(0f, size.height * 0.78f),
                end = Offset(size.width, size.height * 0.54f),
                strokeWidth = 2f,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 54.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                text = "CURATED STAY",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                color = Color.White.copy(alpha = 0.72f),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "The Obsidian\nEscape",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                lineHeight = 38.sp,
                letterSpacing = (-0.5).sp,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeroPill("5 Hari")
                HeroPill("4 Tamu")
                HeroPill("Private")
            }
        }
    }
}

@Composable
private fun HeroPill(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.14f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = Color.White,
    )
}

@Composable
private fun CollapsingBar(
    title: String,
    scrollState: ScrollState,
    statusBarTop: Dp,
    onBack: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = (scrollState.value / BarFadeDistance).coerceIn(0f, 1f)
                },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(statusBarTop + 56.dp)
                    .background(LuxuryColors.SurfaceWhite),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(LuxuryColors.SurfaceBorder),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MgIosBackButton(
                onClick = onBack,
                backgroundColor = Color.White.copy(alpha = 0.9f),
                borderColor = LuxuryColors.SurfaceBorder,
                iconTint = LuxuryColors.TextPrimary,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                modifier = Modifier.graphicsLayer {
                    alpha = (scrollState.value / BarFadeDistance).coerceIn(0f, 1f)
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = LuxuryColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun DetailSheet(modifier: Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "RINGKASAN",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
            color = LuxuryColors.TextMuted,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Vila tebing privat dengan panorama laut lepas.",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 23.sp,
            lineHeight = 29.sp,
            letterSpacing = (-0.3).sp,
            color = LuxuryColors.TextPrimary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "\u2605 4.9",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = LuxuryColors.TextPrimary,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "(128 ulasan)",
                fontSize = 13.sp,
                color = LuxuryColors.TextMuted,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "\u00B7",
                fontSize = 13.sp,
                color = LuxuryColors.TextMuted,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Uluwatu, Bali",
                fontSize = 13.sp,
                color = LuxuryColors.TextSecondary,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard("3", "Kamar")
            StatCard("5", "Hari")
            StatCard("8", "Fasilitas")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Fasilitas",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = LuxuryColors.TextPrimary,
        )
        Spacer(modifier = Modifier.height(6.dp))
        FeatureRow("Private infinity pool menghadap samudra")
        FeatureRow("Chef pribadi & butler 24 jam")
        FeatureRow("Helipad dan transfer VIP bandara")
        FeatureRow("Spa, sauna, dan yoga deck panorama")

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Tersembunyi di antara tebing kapur Uluwatu, vila ini " +
                "menggabungkan arsitektur tropis modern dengan ketenangan " +
                "samudra Hindia. Setiap sudut dirancang untuk privasi mutlak, " +
                "dari kolam tanpa batas hingga dek matahari yang menghadap " +
                "langsung ke cakrawala.",
            fontSize = 13.sp,
            lineHeight = 21.sp,
            color = LuxuryColors.TextSecondary,
        )

        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
private fun RowScope.StatCard(value: String, label: String) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
        color = LuxuryColors.SurfaceWhite,
        border = BorderStroke(1.dp, LuxuryColors.SurfaceBorder),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = LuxuryColors.TextPrimary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = LuxuryColors.TextMuted,
            )
        }
    }
}

@Composable
private fun FeatureRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(LuxuryColors.TealLight),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(14.dp)) {
                val path = Path().apply {
                    moveTo(size.width * 0.16f, size.height * 0.55f)
                    lineTo(size.width * 0.42f, size.height * 0.78f)
                    lineTo(size.width * 0.86f, size.height * 0.24f)
                }
                drawPath(
                    path = path,
                    color = LuxuryColors.TealPrimary,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = LuxuryColors.TextSecondary,
        )
    }
}
