package com.example.uiapp.ui.searchmorph

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private data class DestinationItem(
    val id: Int,
    val name: String,
    val island: String,
    val tag: String,
)

private val Destinations = listOf(
    DestinationItem(1, "Raja Ampat", "Papua Barat Daya", "Selam"),
    DestinationItem(2, "Labuan Bajo", "Nusa Tenggara Timur", "Kapal phinisi"),
    DestinationItem(3, "Danau Toba", "Sumatera Utara", "Danau vulkanik"),
    DestinationItem(4, "Gunung Bromo", "Jawa Timur", "Sunrise"),
    DestinationItem(5, "Candi Borobudur", "Jawa Tengah", "Sejarah"),
    DestinationItem(6, "Candi Prambanan", "Yogyakarta", "Sejarah"),
    DestinationItem(7, "Mandalika", "Nusa Tenggara Barat", "Balap & pantai"),
    DestinationItem(8, "Wakatobi", "Sulawesi Tenggara", "Taman laut"),
    DestinationItem(9, "Kepulauan Derawan", "Kalimantan Timur", "Penyu"),
    DestinationItem(10, "Dataran Tinggi Dieng", "Jawa Tengah", "Kabut & kawah"),
    DestinationItem(11, "Tana Toraja", "Sulawesi Selatan", "Budaya"),
    DestinationItem(12, "Belitung", "Bangka Belitung", "Granit pantai"),
    DestinationItem(13, "Karimunjawa", "Jawa Tengah", "Island hopping"),
    DestinationItem(14, "Ubud", "Bali", "Sawah & seni"),
    DestinationItem(15, "Nusa Penida", "Bali", "Tebing karang"),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchMorphScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current
    var searchOpen by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    BackHandler(enabled = true) {
        if (searchOpen) {
            searchOpen = false
            query = ""
        } else {
            onBack()
        }
    }

    LaunchedEffect(searchOpen) {
        if (searchOpen) focusRequester.requestFocus()
    }

    val trimmed = query.trim()
    val results = remember(query) {
        if (trimmed.isEmpty()) Destinations
        else Destinations.filter {
            it.name.contains(trimmed, ignoreCase = true) || it.island.contains(trimmed, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(palette.background)) {
        UiTopBar(
            title = "Search Morph",
            subtitle = "Ikon cari berubah menjadi kolom",
            onBack = onBack,
        )
        Box(modifier = Modifier.fillMaxSize()) {
            if (results.isEmpty()) {
                EmptyResult(
                    query = trimmed,
                    onReset = { query = "" },
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(results, key = { it.id }) { item ->
                        DestinationCard(item = item)
                    }
                }
            }
        }
        SearchBottomBar(
            searchOpen = searchOpen,
            query = query,
            onQueryChange = { query = it },
            onOpen = { searchOpen = true },
            onClose = {
                searchOpen = false
                query = ""
            },
            focusRequester = focusRequester,
        )
    }
}

@Composable
private fun DestinationCard(item: DestinationItem) {
    val palette = LocalAppPalette.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = item.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${item.island} · ${item.tag}",
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 12.sp,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun EmptyResult(query: String, onReset: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        SearchGlyphIcon(tint = palette.textMuted, size = 44.dp)
        Text(
            text = "Tidak ada hasil",
            modifier = Modifier.padding(top = 16.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textPrimary,
        )
        Text(
            text = "Tidak ditemukan destinasi untuk \"$query\". Coba kata kunci lain.",
            modifier = Modifier.padding(top = 6.dp),
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = palette.textSecondary,
            textAlign = TextAlign.Center,
        )
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .sizeIn(minHeight = 48.dp)
                .clip(RoundedCornerShape(percent = 50))
                .clickable(role = Role.Button, onClickLabel = "Hapus pencarian") { onReset() }
                .semantics { contentDescription = "Hapus kata kunci pencarian" },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Hapus pencarian",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.primary,
            )
        }
    }
}

@Composable
private fun SearchBottomBar(
    searchOpen: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onOpen: () -> Unit,
    onClose: () -> Unit,
    focusRequester: FocusRequester,
) {
    // safeDrawing.only(Bottom) mencakup insets IME dan navigation bar tanpa padding ganda.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = searchOpen,
            transitionSpec = {
                (fadeIn(tween(160)) + slideInVertically(tween(160)) { it / 6 })
                    .togetherWith(fadeOut(tween(120)) + slideOutVertically(tween(120)) { -it / 6 })
                    .using(SizeTransform(clip = false))
            },
            label = "searchMorph",
        ) { open ->
            if (open) {
                SearchFieldRow(
                    query = query,
                    onQueryChange = onQueryChange,
                    onClose = onClose,
                    focusRequester = focusRequester,
                )
            } else {
                SearchPill(onOpen = onOpen)
            }
        }
    }
}

@Composable
private fun SearchPill(onOpen: () -> Unit) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onOpen,
        shape = RoundedCornerShape(percent = 50),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = Modifier.semantics { contentDescription = "Buka pencarian destinasi" },
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SearchGlyphIcon(tint = palette.textSecondary)
            Text(
                text = "Cari",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = palette.textSecondary,
            )
        }
    }
}

@Composable
private fun SearchFieldRow(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    focusRequester: FocusRequester,
) {
    val palette = LocalAppPalette.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SearchGlyphIcon(tint = palette.textMuted)
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
                    .heightIn(min = 44.dp)
                    .focusRequester(focusRequester),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = palette.textPrimary,
                    fontWeight = FontWeight.Medium,
                ),
                cursorBrush = SolidColor(palette.primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Cari destinasi atau pulau…",
                                fontSize = 14.sp,
                                color = palette.textMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button) {
                        if (query.isEmpty()) onClose() else onQueryChange("")
                    }
                    .semantics {
                        contentDescription = if (query.isEmpty()) "Tutup pencarian" else "Kosongkan pencarian"
                    },
                contentAlignment = Alignment.Center,
            ) {
                CloseGlyphIcon(tint = palette.textSecondary)
            }
        }
    }
}

@Composable
private fun SearchGlyphIcon(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(
            tint,
            radius = 0.32f * w,
            center = Offset(0.42f * w, 0.42f * h),
            style = stroke,
        )
        drawLine(tint, Offset(0.64f * w, 0.64f * h), Offset(0.9f * w, 0.9f * h), stroke.width, stroke.cap)
    }
}

@Composable
private fun CloseGlyphIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawLine(tint, Offset(0.22f * w, 0.22f * h), Offset(0.78f * w, 0.78f * h), stroke.width, stroke.cap)
        drawLine(tint, Offset(0.78f * w, 0.22f * h), Offset(0.22f * w, 0.78f * h), stroke.width, stroke.cap)
    }
}
