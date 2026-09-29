package com.example.uiapp.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LuxuryColors
import com.example.uiapp.ui.components.FlatPrimaryButton
import kotlinx.coroutines.launch

private enum class OnboardArt { EXPLORE, SAVE, CONFIRM }

private data class OnboardPage(val title: String, val desc: String, val art: OnboardArt)

private val Pages = listOf(
    OnboardPage(
        "Jelajahi destinasi terbaik",
        "Temukan vila, glamping, dan penginapan unik di seluruh Nusantara dalam satu aplikasi.",
        OnboardArt.EXPLORE,
    ),
    OnboardPage(
        "Simpan dan bandingkan",
        "Kumpulkan properti favorit Anda dalam satu daftar, lalu bandingkan dengan mudah.",
        OnboardArt.SAVE,
    ),
    OnboardPage(
        "Pesan dalam hitungan detik",
        "Konfirmasi instan, pembayaran aman, dan tanpa perlu antre di depan.",
        OnboardArt.CONFIRM,
    ),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OnboardingScreen(onBack: () -> Unit, onFinish: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val pagerState = rememberPagerState(pageCount = { Pages.size })
    val scope = rememberCoroutineScope()
    val isLast = pagerState.currentPage == Pages.lastIndex

    Scaffold(containerColor = LuxuryColors.Background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = "Lewati",
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onFinish() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LuxuryColors.TextMuted,
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { index ->
                OnboardPageContent(page = Pages[index])
            }

            PagerIndicator(
                count = Pages.size,
                current = pagerState.currentPage,
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(22.dp))

            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                FlatPrimaryButton(
                    text = if (isLast) "Mulai Sekarang" else "Lanjut",
                    onClick = {
                        if (isLast) {
                            onFinish()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Sudah punya akun?",
                    fontSize = 13.sp,
                    color = LuxuryColors.TextSecondary,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Masuk",
                    modifier = Modifier.clickable { onFinish() },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = LuxuryColors.TealPrimary,
                )
            }
        }
    }
}

@Composable
private fun OnboardPageContent(page: OnboardPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(184.dp)
                .clip(RoundedCornerShape(48.dp))
                .background(LuxuryColors.TealLight),
            contentAlignment = Alignment.Center,
        ) {
            OnboardArtwork(art = page.art)
        }

        Spacer(modifier = Modifier.height(34.dp))

        Text(
            text = page.title,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            letterSpacing = (-0.3).sp,
            color = LuxuryColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = page.desc,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = LuxuryColors.TextSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PagerIndicator(count: Int, current: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val active = index == current
            val width by animateDpAsState(
                targetValue = if (active) 22.dp else 8.dp,
                animationSpec = spring(),
                label = "dotWidth",
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .width(width)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (active) LuxuryColors.TealPrimary
                        else LuxuryColors.SurfaceBorder,
                    ),
            )
        }
    }
}

@Composable
private fun OnboardArtwork(art: OnboardArt) {
    Canvas(modifier = Modifier.size(96.dp)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(
            width = 3.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        when (art) {
            OnboardArt.EXPLORE -> {
                drawCircle(
                    color = LuxuryColors.TealPrimary,
                    radius = w * 0.42f,
                    center = Offset(w / 2, h / 2),
                    style = stroke,
                )
                val north = Path().apply {
                    moveTo(w * 0.50f, h * 0.20f)
                    lineTo(w * 0.64f, h * 0.50f)
                    lineTo(w * 0.36f, h * 0.50f)
                    close()
                }
                drawPath(north, LuxuryColors.TealPrimary)
                val south = Path().apply {
                    moveTo(w * 0.50f, h * 0.80f)
                    lineTo(w * 0.36f, h * 0.50f)
                    lineTo(w * 0.64f, h * 0.50f)
                    close()
                }
                drawPath(south, LuxuryColors.TealPrimary.copy(alpha = 0.35f))
            }

            OnboardArt.SAVE -> {
                val bookmark = Path().apply {
                    moveTo(w * 0.28f, h * 0.14f)
                    lineTo(w * 0.72f, h * 0.14f)
                    lineTo(w * 0.72f, h * 0.86f)
                    lineTo(w * 0.50f, h * 0.66f)
                    lineTo(w * 0.28f, h * 0.86f)
                    close()
                }
                drawPath(bookmark, LuxuryColors.TealPrimary.copy(alpha = 0.16f))
                drawPath(bookmark, LuxuryColors.TealPrimary, style = stroke)
            }

            OnboardArt.CONFIRM -> {
                drawCircle(
                    color = LuxuryColors.TealPrimary.copy(alpha = 0.16f),
                    radius = w * 0.42f,
                    center = Offset(w / 2, h / 2),
                )
                drawCircle(
                    color = LuxuryColors.TealPrimary,
                    radius = w * 0.42f,
                    center = Offset(w / 2, h / 2),
                    style = stroke,
                )
                val check = Path().apply {
                    moveTo(w * 0.32f, h * 0.52f)
                    lineTo(w * 0.46f, h * 0.66f)
                    lineTo(w * 0.70f, h * 0.36f)
                }
                drawPath(check, LuxuryColors.TealPrimary, style = stroke)
            }
        }
    }
}
