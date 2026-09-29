package com.example.uiapp.ui.navlab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LuxuryColors
import com.example.uiapp.ui.components.UiTopBar

private data class NavTab(val label: String, val glyph: NavGlyph)

private enum class SegTab(val label: String) { EXPLORE("Jelajahi"), SAVED("Simpan"), PROFILE("Profil") }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NavLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    var segment by remember { mutableIntStateOf(0) }
    var navIndex by remember { mutableIntStateOf(0) }

    val navTabs = listOf(
        NavTab("Jelajahi", NavGlyph.EXPLORE),
        NavTab("Simpan", NavGlyph.SAVED),
        NavTab("Notifikasi", NavGlyph.BELL),
        NavTab("Profil", NavGlyph.PROFILE),
    )

    Scaffold(
        containerColor = LuxuryColors.Background,
        topBar = {
            UiTopBar(
                title = "Navigation Lab",
                subtitle = "Segmented, bottom nav, speed-dial",
                onBack = onBack,
            )
        },
        bottomBar = {
            BottomNavBar(
                tabs = navTabs,
                selected = navIndex,
                onSelect = { navIndex = it },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SegmentedControl(
                    tabs = SegTab.entries.map { it.label },
                    selected = segment,
                    onSelect = { segment = it },
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    when (segment) {
                        0 -> ExploreContent()
                        1 -> SavedContent()
                        else -> ProfileContent()
                    }
                }
            }

            SpeedDial(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp),
            )
        }
    }
}

@Composable
private fun SegmentedControl(
    tabs: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(LuxuryColors.SurfaceMuted)
            .border(1.dp, LuxuryColors.SurfaceBorder, RoundedCornerShape(12.dp)),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val segmentWidth = maxWidth / tabs.size
            val indicatorOffset by animateDpAsState(
                targetValue = segmentWidth * selected,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "segmentOffset",
            )

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(segmentWidth)
                    .fillMaxHeight()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(LuxuryColors.SurfaceWhite)
                    .border(1.dp, LuxuryColors.SurfaceBorder, RoundedCornerShape(9.dp)),
            )

            Row(modifier = Modifier.fillMaxSize()) {
                tabs.forEachIndexed { index, label ->
                    val active = index == selected
                    val textColor by animateColorAsState(
                        targetValue = if (active) LuxuryColors.TextPrimary else LuxuryColors.TextMuted,
                        animationSpec = tween(200),
                        label = "segmentTint",
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { onSelect(index) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(5) { index ->
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
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(LuxuryColors.TealLight),
                        contentAlignment = Alignment.Center,
                    ) {
                        NavGlyphIcon(glyph = NavGlyph.EXPLORE, tint = LuxuryColors.TealPrimary)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Destinasi pilihan ${index + 1}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LuxuryColors.TextPrimary,
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Kurasi minggu ini \u00B7 12 properti",
                            fontSize = 12.sp,
                            color = LuxuryColors.TextMuted,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(3) { index ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = LuxuryColors.SurfaceWhite,
                border = BorderStroke(1.dp, LuxuryColors.SurfaceBorder),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Wishlist ${index + 1}",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = LuxuryColors.TextPrimary,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${(index + 2) * 3} properti tersimpan",
                        fontSize = 12.sp,
                        color = LuxuryColors.TextMuted,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(LuxuryColors.SurfaceMuted),
            contentAlignment = Alignment.Center,
        ) {
            NavGlyphIcon(glyph = NavGlyph.PROFILE, tint = LuxuryColors.TealPrimary, size = 40.dp)
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Ayu Prameswari",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = LuxuryColors.TextPrimary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "ayu@email.com",
            fontSize = 13.sp,
            color = LuxuryColors.TextMuted,
        )
    }
}

@Composable
private fun BottomNavBar(
    tabs: List<NavTab>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LuxuryColors.SurfaceWhite),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(LuxuryColors.SurfaceBorder),
        )
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp),
        ) {
            val itemWidth = maxWidth / tabs.size
            val indicatorOffset by animateDpAsState(
                targetValue = itemWidth * selected,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "navOffset",
            )

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(itemWidth)
                    .fillMaxHeight(),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 7.dp)
                        .size(width = 54.dp, height = 30.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(LuxuryColors.TealLight),
                )
            }

            Row(modifier = Modifier.fillMaxSize()) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = index == selected
                    val tint by animateColorAsState(
                        targetValue = if (isSelected) LuxuryColors.TealPrimary else LuxuryColors.TextMuted,
                        animationSpec = tween(220),
                        label = "navTint",
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onSelect(index) }
                            .padding(top = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .width(54.dp)
                                .height(30.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            NavGlyphIcon(glyph = tab.glyph, tint = tint, size = 21.dp)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = tint,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpeedDial(modifier: Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "fabRotation",
    )

    val actions = listOf(
        Triple("Tulis", NavGlyph.WRITE, LuxuryColors.AccentInfo),
        Triple("Foto", NavGlyph.CAMERA, LuxuryColors.AccentWarning),
        Triple("Suara", NavGlyph.VOICE, LuxuryColors.Accent),
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
    ) {
        actions.forEachIndexed { index, action ->
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(160, delayMillis = index * 40)) +
                    slideInVertically(tween(220, delayMillis = index * 40)) { it / 2 },
                exit = fadeOut(tween(120)) +
                    slideOutVertically(tween(160, delayMillis = (actions.size - index) * 30)) { it / 2 },
            ) {
                Row(
                    modifier = Modifier.padding(bottom = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = action.first,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(LuxuryColors.SurfaceWhite)
                            .border(1.dp, LuxuryColors.SurfaceBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LuxuryColors.TextPrimary,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        onClick = { expanded = false },
                        modifier = Modifier.size(46.dp),
                        shape = CircleShape,
                        color = action.third,
                        border = BorderStroke(1.dp, LuxuryColors.SurfaceBorder),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            NavGlyphIcon(glyph = action.second, tint = Color.White, size = 20.dp)
                        }
                    }
                }
            }
        }

        Surface(
            onClick = { expanded = !expanded },
            modifier = Modifier.size(58.dp),
            shape = CircleShape,
            color = LuxuryColors.TealPrimary,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer { rotationZ = rotation },
                ) {
                    drawLine(
                        color = Color.White,
                        start = Offset(size.width / 2, size.height * 0.18f),
                        end = Offset(size.width / 2, size.height * 0.82f),
                        strokeWidth = 2.6.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(size.width * 0.18f, size.height / 2),
                        end = Offset(size.width * 0.82f, size.height / 2),
                        strokeWidth = 2.6.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

private enum class NavGlyph { EXPLORE, SAVED, BELL, PROFILE, WRITE, CAMERA, VOICE }

@Composable
private fun NavGlyphIcon(
    glyph: NavGlyph,
    tint: Color,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 22.dp,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(
            width = 1.7.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )

        when (glyph) {
            NavGlyph.EXPLORE -> {
                drawCircle(
                    color = tint,
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
                drawPath(north, tint)
                val south = Path().apply {
                    moveTo(w * 0.50f, h * 0.80f)
                    lineTo(w * 0.36f, h * 0.50f)
                    lineTo(w * 0.64f, h * 0.50f)
                    close()
                }
                drawPath(south, tint.copy(alpha = 0.35f))
            }

            NavGlyph.SAVED -> {
                val bookmark = Path().apply {
                    moveTo(w * 0.30f, h * 0.16f)
                    lineTo(w * 0.70f, h * 0.16f)
                    lineTo(w * 0.70f, h * 0.84f)
                    lineTo(w * 0.50f, h * 0.66f)
                    lineTo(w * 0.30f, h * 0.84f)
                    close()
                }
                drawPath(bookmark, tint, style = stroke)
            }

            NavGlyph.BELL -> {
                val bell = Path().apply {
                    moveTo(w * 0.25f, h * 0.70f)
                    lineTo(w * 0.34f, h * 0.60f)
                    lineTo(w * 0.34f, h * 0.44f)
                    cubicTo(w * 0.34f, h * 0.25f, w * 0.66f, h * 0.25f, w * 0.66f, h * 0.44f)
                    lineTo(w * 0.66f, h * 0.60f)
                    lineTo(w * 0.75f, h * 0.70f)
                    close()
                }
                drawPath(bell, tint, style = stroke)
                val clapper = Path().apply {
                    moveTo(w * 0.43f, h * 0.76f)
                    cubicTo(w * 0.45f, h * 0.84f, w * 0.55f, h * 0.84f, w * 0.57f, h * 0.76f)
                }
                drawPath(clapper, tint, style = stroke)
            }

            NavGlyph.PROFILE -> {
                drawCircle(
                    color = tint,
                    radius = w * 0.17f,
                    center = Offset(w / 2, h * 0.34f),
                    style = stroke,
                )
                val body = Path().apply {
                    moveTo(w * 0.24f, h * 0.82f)
                    cubicTo(w * 0.26f, h * 0.60f, w * 0.74f, h * 0.60f, w * 0.76f, h * 0.82f)
                }
                drawPath(body, tint, style = stroke)
            }

            NavGlyph.WRITE -> {
                val pencil = Path().apply {
                    moveTo(w * 0.16f, h * 0.84f)
                    lineTo(w * 0.29f, h * 0.77f)
                    lineTo(w * 0.82f, h * 0.24f)
                    lineTo(w * 0.76f, h * 0.18f)
                    lineTo(w * 0.23f, h * 0.71f)
                    close()
                }
                drawPath(pencil, tint, style = stroke)
                drawLine(
                    color = tint,
                    start = Offset(w * 0.29f, h * 0.77f),
                    end = Offset(w * 0.23f, h * 0.71f),
                    strokeWidth = 1.7.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }

            NavGlyph.CAMERA -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.10f, h * 0.30f),
                    size = Size(w * 0.80f, h * 0.52f),
                    cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
                    style = stroke,
                )
                drawCircle(
                    color = tint,
                    radius = w * 0.14f,
                    center = Offset(w / 2, h * 0.56f),
                    style = stroke,
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.35f, h * 0.17f),
                    size = Size(w * 0.30f, h * 0.13f),
                    cornerRadius = CornerRadius(w * 0.05f, w * 0.05f),
                    style = stroke,
                )
            }

            NavGlyph.VOICE -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.39f, h * 0.12f),
                    size = Size(w * 0.22f, h * 0.42f),
                    cornerRadius = CornerRadius(w * 0.11f, w * 0.11f),
                    style = stroke,
                )
                val arc = Path().apply {
                    moveTo(w * 0.28f, h * 0.48f)
                    cubicTo(w * 0.28f, h * 0.76f, w * 0.72f, h * 0.76f, w * 0.72f, h * 0.48f)
                }
                drawPath(arc, tint, style = stroke)
                drawLine(
                    color = tint,
                    start = Offset(w * 0.50f, h * 0.72f),
                    end = Offset(w * 0.50f, h * 0.86f),
                    strokeWidth = 1.7.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}
