package com.example.uiapp.ui.adaptive

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private data class ResponsiveItem(
    val id: Int,
    val title: String,
    val region: String,
    val price: String,
    val start: Color,
    val end: Color,
)

private val ResponsiveItems = listOf(
    ResponsiveItem(1, "The Obsidian Escape", "Uluwatu", "Rp 8.4jt", Color(0xFF0F3D34), Color(0xFF05201B)),
    ResponsiveItem(2, "Aurora Hillside", "Ubud", "Rp 5.1jt", Color(0xFF1E3A5F), Color(0xFF0B1B2E)),
    ResponsiveItem(3, "Solstice Loft", "Senopati", "Rp 3.2jt", Color(0xFF4A2F27), Color(0xFF1F130F)),
    ResponsiveItem(4, "Palm Cove Suite", "Sanur", "Rp 2.7jt", Color(0xFF2F5D4E), Color(0xFF0E2B23)),
    ResponsiveItem(5, "Mirage Pavilion", "Nusa Dua", "Rp 6.9jt", Color(0xFF5A4632), Color(0xFF2A1F14)),
    ResponsiveItem(6, "Northern Light Lodge", "Lembang", "Rp 4.0jt", Color(0xFF3B3550), Color(0xFF171426)),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AdaptiveScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    BackHandler(enabled = true) { onBack() }

    var selectedId by remember { mutableIntStateOf(1) }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Adaptive Layout",
                subtitle = "Tata letak menyesuaikan ukuran layar",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            val width = maxWidth
            val columns = when {
                width < 480.dp -> 1
                width < 760.dp -> 2
                width < 1040.dp -> 3
                else -> 4
            }
            val breakpoint = when {
                width < 480.dp -> "Compact"
                width < 760.dp -> "Medium"
                width < 1040.dp -> "Expanded"
                else -> "Large"
            }
            val isWide = width >= 760.dp

            Row(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        BreakpointBanner(
                            breakpoint = breakpoint,
                            widthDp = width.value.toInt(),
                            columns = columns,
                        )
                    }
                    items(ResponsiveItems, key = { it.id }) { item ->
                        ResponsiveCard(
                            item = item,
                            selected = item.id == selectedId,
                            onClick = { selectedId = item.id },
                        )
                    }
                }

                if (isWide) {
                    val selected = ResponsiveItems.firstOrNull { it.id == selectedId }
                        ?: ResponsiveItems.first()
                    DetailPane(
                        item = selected,
                        modifier = Modifier
                            .width(320.dp)
                            .fillMaxHeight(),
                    )
                }
            }
        }
    }
}

@Composable
private fun BreakpointBanner(breakpoint: String, widthDp: Int, columns: Int) {
    val palette = LocalAppPalette.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = palette.primaryContainer,
        border = BorderStroke(1.dp, palette.success.copy(alpha = 0.25f)),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "BREAKPOINT: $breakpoint",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.4.sp,
                    color = palette.primary,
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Lebar ${widthDp}dp \u00B7 $columns kolom",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = palette.textPrimary,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Putar perangkat atau ubah ukuran jendela untuk melihat adaptasi.",
                    fontSize = 11.sp,
                    color = palette.textSecondary,
                )
            }
            Canvas(modifier = Modifier.size(46.dp)) {
                val w = size.width
                val h = size.height
                repeat(columns.coerceAtMost(4)) { index ->
                    val colWidth = (w - 8f) / 4
                    drawRoundRect(
                        color = palette.primary.copy(alpha = if (index < columns) 0.85f else 0.18f),
                        topLeft = Offset(index * (colWidth + 2f), h * 0.2f),
                        size = androidx.compose.ui.geometry.Size(colWidth, h * 0.6f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ResponsiveCard(
    item: ResponsiveItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current

    val border by animateColorAsState(
        targetValue = if (selected) palette.primary else palette.border,
        animationSpec = tween(220),
        label = "cardBorder",
    )

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = palette.surface,
        border = BorderStroke(if (selected) 1.6.dp else 1.dp, border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(brush = Brush.verticalGradient(listOf(item.start, item.end)))
                    drawCircle(
                        color = Color.White.copy(alpha = 0.12f),
                        radius = size.minDimension * 0.4f,
                        center = Offset(size.width * 0.76f, size.height * 0.3f),
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = item.region,
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                    Text(
                        text = item.price,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailPane(item: ResponsiveItem, modifier: Modifier) {
    val palette = LocalAppPalette.current

    Column(
        modifier = modifier
            .background(palette.surface)
            .padding(20.dp),
    ) {
        Text(
            text = "DETAIL",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.6.sp,
            color = palette.textMuted,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp)),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(brush = Brush.verticalGradient(listOf(item.start, item.end)))
                drawCircle(
                    color = Color.White.copy(alpha = 0.12f),
                    radius = size.minDimension * 0.42f,
                    center = Offset(size.width * 0.76f, size.height * 0.3f),
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = item.title,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = palette.textPrimary,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${item.region} \u00B7 ${item.price} / malam",
            fontSize = 12.sp,
            color = palette.textSecondary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Panel ini hanya muncul pada breakpoint Medium ke atas. Di ponsel " +
                "portrait, daftar tampil satu kolom penuh.",
            fontSize = 12.sp,
            lineHeight = 19.sp,
            color = palette.textSecondary,
        )
    }
}
