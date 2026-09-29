package com.example.uiapp.ui.themelab

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.AppPalette
import com.example.uiapp.theme.DarkAppPalette
import com.example.uiapp.theme.LightAppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.theme.LocalThemeController
import com.example.uiapp.theme.ThemeMode
import com.example.uiapp.ui.components.MgIosBackButton

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ThemeLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val themeController = LocalThemeController.current
    val currentMode = themeController.currentMode
    val modeIndex = ThemeMode.entries.indexOf(currentMode).coerceAtLeast(0)
    val palette = LocalAppPalette.current

    Scaffold(containerColor = palette.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            ThemeTopBar(
                palette = palette,
                onBack = onBack,
                subtitle = "Mode aktif: ${currentMode.label}",
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                ModeSelector(
                    palette = palette,
                    selected = modeIndex,
                    onSelect = { index ->
                        themeController.setMode(ThemeMode.entries[index])
                    },
                )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Pratinjau komponen",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = palette.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PreviewSample(palette = palette)

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Token warna",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = palette.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    TokenGrid(palette = palette)

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }


@Composable
private fun ThemeTopBar(
    palette: AppPalette,
    onBack: () -> Unit,
    subtitle: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.surface)
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
                backgroundColor = palette.surfaceMuted,
                borderColor = palette.border,
                iconTint = palette.textPrimary,
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = "Theme & Dark Mode",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = palette.textMuted,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.border),
        )
    }
}

@Composable
private fun ModeSelector(
    palette: AppPalette,
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val segmentWidth = maxWidth / 3
            val offset by animateDpAsState(
                targetValue = segmentWidth * selected,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "themeOffset",
            )
            Box(
                modifier = Modifier
                    .offset(x = offset)
                    .width(segmentWidth)
                    .fillMaxHeight()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(9.dp)),
            )
            Row(modifier = Modifier.fillMaxSize()) {
                ThemeMode.entries.forEachIndexed { index, entry ->
                    val active = index == selected
                    val color by animateColorAsState(
                        targetValue = if (active) palette.textPrimary else palette.textMuted,
                        animationSpec = tween(200),
                        label = "themeTint",
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onSelect(index) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = entry.label,
                            fontSize = 13.sp,
                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                            color = color,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewSample(palette: AppPalette) {
    var switchOn by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ringkasan pendapatan",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Pendapatan naik 18% bulan ini",
                fontSize = 12.sp,
                color = palette.textMuted,
            )

            Spacer(modifier = Modifier.height(14.dp))

            MiniBars(palette = palette)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Notifikasi push",
                    modifier = Modifier.weight(1f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = palette.textSecondary,
                )
                PreviewSwitch(
                    checked = switchOn,
                    onCheckedChange = { switchOn = it },
                    palette = palette,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip(text = "Direct", active = true, palette = palette)
                Chip(text = "Marketplace", active = false, palette = palette)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = palette.primary,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Text(
                    text = "Tombol utama",
                    modifier = Modifier.padding(vertical = 14.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.onPrimary,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Text(
                    text = "Tombol sekunder",
                    modifier = Modifier.padding(vertical = 14.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceMuted)
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "nama@email.com",
                    modifier = Modifier.weight(1f),
                    fontSize = 13.sp,
                    color = palette.textMuted,
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(palette.primary),
                )
            }
        }
    }
}

@Composable
private fun MiniBars(palette: AppPalette) {
    val values = listOf(0.5f, 0.72f, 0.44f, 0.9f, 0.66f, 0.8f)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp),
    ) {
        val gap = 10.dp.toPx()
        val barWidth = (size.width - gap * (values.size - 1)) / values.size
        values.forEachIndexed { index, value ->
            val barHeight = size.height * value
            drawRoundRect(
                color = if (index == values.lastIndex) palette.primary else palette.primary.copy(alpha = 0.28f),
                topLeft = Offset((barWidth + gap) * index, size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2.6f, barWidth / 2.6f),
            )
        }
    }
}

@Composable
private fun PreviewSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    palette: AppPalette,
) {
    val track by animateColorAsState(
        targetValue = if (checked) palette.primary else palette.border,
        animationSpec = tween(200),
        label = "switchTrack",
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 18.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "switchThumb",
    )
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 26.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(track)
            .clickable { onCheckedChange(!checked) }
            .padding(3.dp),
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(20.dp)
                .clip(CircleShape)
                .background(palette.surface),
        )
    }
}

@Composable
private fun Chip(text: String, active: Boolean, palette: AppPalette) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (active) palette.primaryContainer else palette.surface,
        border = BorderStroke(1.dp, if (active) palette.primary else palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (active) palette.onPrimaryContainer else palette.textSecondary,
        )
    }
}

@Composable
private fun TokenGrid(palette: AppPalette) {
    val tokens = listOf(
        "background" to palette.background,
        "surface" to palette.surface,
        "surfaceMuted" to palette.surfaceMuted,
        "border" to palette.border,
        "textPrimary" to palette.textPrimary,
        "textSecondary" to palette.textSecondary,
        "textMuted" to palette.textMuted,
        "primary" to palette.primary,
        "primaryContainer" to palette.primaryContainer,
        "success" to palette.success,
        "warning" to palette.warning,
        "danger" to palette.danger,
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        tokens.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (name, color) ->
                    TokenSwatch(name = name, color = color, palette = palette, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TokenSwatch(
    name: String,
    color: Color,
    palette: AppPalette,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color)
                    .border(1.dp, palette.border, RoundedCornerShape(8.dp)),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = palette.textSecondary,
                maxLines = 1,
            )
        }
    }
}
