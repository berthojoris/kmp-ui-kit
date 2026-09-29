package com.example.uiapp.ui.search

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.uiapp.theme.LuxuryColors
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private data class Suggestion(
    val title: String,
    val region: String,
    val kind: String,
)

private val AllSuggestions = listOf(
    Suggestion("Ubud", "Gianyar, Bali", "Desa"),
    Suggestion("Canggu", "Badung, Bali", "Pesisir"),
    Suggestion("Seminyak", "Badung, Bali", "Pesisir"),
    Suggestion("Uluwatu", "Badung, Bali", "Tebing"),
    Suggestion("Sanur", "Denpasar, Bali", "Pesisir"),
    Suggestion("Nusa Penida", "Klungkung, Bali", "Pulau"),
    Suggestion("Kintamani", "Bangli, Bali", "Pegunungan"),
    Suggestion("Lovina", "Buleleng, Bali", "Pesisir"),
    Suggestion("Amed", "Karangasem, Bali", "Pesisir"),
    Suggestion("Sidemen", "Karangasem, Bali", "Desa"),
    Suggestion("Munduk", "Buleleng, Bali", "Pegunungan"),
    Suggestion("Jimbaran", "Badung, Bali", "Pesisir"),
)

private val RecentSearches = listOf("Villa kolam privat", "Ubud", "Glamping", "Nusa Penida")
private val PopularSearches = listOf("Tebing", "Pegunungan", "Pantai", "Sawah")

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchAutocompleteScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(emptyList<Suggestion>()) }
    var searching by remember { mutableStateOf(false) }

    LaunchedEffect(query) {
        val q = query.trim()
        if (q.isEmpty()) {
            searching = false
            results = emptyList()
            return@LaunchedEffect
        }
        searching = true
        delay(280)
        results = AllSuggestions.filter {
            it.title.contains(q, ignoreCase = true) || it.region.contains(q, ignoreCase = true)
        }
        searching = false
    }

    LaunchedEffect(Unit) {
        delay(150)
        focusRequester.requestFocus()
    }

    fun applySearch(value: String) {
        query = value
        focusManager.clearFocus()
    }

    Scaffold(
        containerColor = LuxuryColors.Background,
        topBar = {
            UiTopBar(
                title = "Pencarian",
                subtitle = "Autocomplete dan riwayat",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                onClear = { query = "" },
                focusRequester = focusRequester,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            )

            when {
                query.isBlank() -> DiscoveryPanel(onPick = ::applySearch)

                searching -> SearchingState()

                results.isEmpty() -> NoResultsState(query = query)

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item(key = "result-count") {
                        Text(
                            text = "${results.size} hasil untuk \"$query\"",
                            modifier = Modifier.padding(vertical = 8.dp),
                            fontSize = 12.sp,
                            color = LuxuryColors.TextMuted,
                        )
                    }
                    items(results, key = { it.title }) { suggestion ->
                        SuggestionRow(
                            suggestion = suggestion,
                            query = query,
                            onClick = { applySearch(suggestion.title) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = LuxuryColors.SurfaceWhite,
        border = BorderStroke(1.dp, LuxuryColors.SurfaceBorder),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Canvas(modifier = Modifier.size(18.dp)) {
                val stroke = Stroke(
                    width = 1.8.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                drawCircle(
                    color = LuxuryColors.TextMuted,
                    radius = size.minDimension * 0.34f,
                    center = Offset(size.width * 0.42f, size.height * 0.42f),
                    style = stroke,
                )
                drawLine(
                    color = LuxuryColors.TextMuted,
                    start = Offset(size.width * 0.66f, size.height * 0.66f),
                    end = Offset(size.width * 0.9f, size.height * 0.9f),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "Cari destinasi atau wilayah\u2026",
                        fontSize = 14.sp,
                        color = LuxuryColors.TextMuted,
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = LuxuryColors.TextPrimary,
                        fontWeight = FontWeight.Medium,
                    ),
                    cursorBrush = SolidColor(LuxuryColors.TealPrimary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (query.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                Canvas(
                    modifier = Modifier
                        .size(18.dp)
                        .clickable(onClick = onClear),
                ) {
                    drawCircle(
                        color = LuxuryColors.SurfaceMuted,
                        radius = size.minDimension * 0.5f,
                    )
                    drawLine(
                        color = LuxuryColors.TextSecondary,
                        start = Offset(size.width * 0.34f, size.height * 0.34f),
                        end = Offset(size.width * 0.66f, size.height * 0.66f),
                        strokeWidth = 1.9.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = LuxuryColors.TextSecondary,
                        start = Offset(size.width * 0.66f, size.height * 0.34f),
                        end = Offset(size.width * 0.34f, size.height * 0.66f),
                        strokeWidth = 1.9.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Canvas(modifier = Modifier.size(18.dp)) {
                val stroke = Stroke(
                    width = 1.8.dp.toPx(),
                    cap = StrokeCap.Round,
                )
                val w = size.width
                val h = size.height
                drawRoundRect(
                    color = LuxuryColors.TealPrimary,
                    topLeft = Offset(w * 0.4f, h * 0.1f),
                    size = androidx.compose.ui.geometry.Size(w * 0.2f, h * 0.42f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.1f, w * 0.1f),
                    style = stroke,
                )
                drawArc(
                    color = LuxuryColors.TealPrimary,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.28f, h * 0.42f),
                    size = androidx.compose.ui.geometry.Size(w * 0.44f, h * 0.34f),
                    style = stroke,
                )
                drawLine(
                    color = LuxuryColors.TealPrimary,
                    start = Offset(w * 0.5f, h * 0.72f),
                    end = Offset(w * 0.5f, h * 0.88f),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
private fun DiscoveryPanel(onPick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        SectionLabel("Pencarian terbaru")
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            RecentSearches.forEach { term ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onPick(term) }
                        .padding(vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Canvas(modifier = Modifier.size(16.dp)) {
                        drawCircle(
                            color = LuxuryColors.TextMuted,
                            radius = size.minDimension * 0.4f,
                            center = Offset(size.width * 0.5f, size.height * 0.5f),
                            style = Stroke(width = 1.6.dp.toPx()),
                        )
                        drawLine(
                            color = LuxuryColors.TextMuted,
                            start = Offset(size.width * 0.5f, size.height * 0.5f),
                            end = Offset(size.width * 0.5f, size.height * 0.28f),
                            strokeWidth = 1.6.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                        drawLine(
                            color = LuxuryColors.TextMuted,
                            start = Offset(size.width * 0.5f, size.height * 0.5f),
                            end = Offset(size.width * 0.68f, size.height * 0.58f),
                            strokeWidth = 1.6.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = term,
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                        color = LuxuryColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Terbaru",
                        fontSize = 11.sp,
                        color = LuxuryColors.TextMuted,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        SectionLabel("Populer")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(PopularSearches) { term ->
                Surface(
                    onClick = { onPick(term) },
                    shape = RoundedCornerShape(20.dp),
                    color = LuxuryColors.SurfaceWhite,
                    border = BorderStroke(1.dp, LuxuryColors.SurfaceBorder),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Text(
                        text = term,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = LuxuryColors.TextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(bottom = 8.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = LuxuryColors.TextMuted,
    )
}

@Composable
private fun SearchingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LuxuryColors.SurfaceMuted.copy(alpha = 0.7f)),
            )
        }
    }
}

@Composable
private fun NoResultsState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Canvas(modifier = Modifier.size(56.dp)) {
            val stroke = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
            drawCircle(
                color = LuxuryColors.TextMuted.copy(alpha = 0.6f),
                radius = size.minDimension * 0.34f,
                center = Offset(size.width * 0.42f, size.height * 0.42f),
                style = stroke,
            )
            drawLine(
                color = LuxuryColors.TextMuted.copy(alpha = 0.6f),
                start = Offset(size.width * 0.66f, size.height * 0.66f),
                end = Offset(size.width * 0.9f, size.height * 0.9f),
                strokeWidth = 2.2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tidak ada hasil",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = LuxuryColors.TextPrimary,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Tidak ditemukan destinasi untuk \"$query\". Coba kata kunci lain.",
            fontSize = 13.sp,
            color = LuxuryColors.TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun SuggestionRow(
    suggestion: Suggestion,
    query: String,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
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
                    .size(38.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(LuxuryColors.TealLight),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(16.dp)) {
                    drawCircle(
                        color = LuxuryColors.TealPrimary,
                        radius = size.minDimension * 0.4f,
                        center = Offset(size.width * 0.5f, size.height * 0.5f),
                        style = Stroke(width = 1.7.dp.toPx()),
                    )
                    drawCircle(
                        color = LuxuryColors.TealPrimary,
                        radius = size.minDimension * 0.12f,
                        center = Offset(size.width * 0.5f, size.height * 0.5f),
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = highlight(suggestion.title, query, palette.primary),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LuxuryColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = suggestion.region,
                    fontSize = 12.sp,
                    color = LuxuryColors.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = suggestion.kind,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(LuxuryColors.SurfaceMuted)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = LuxuryColors.TextSecondary,
            )
        }
    }
}

private fun highlight(text: String, query: String, tint: Color) = buildAnnotatedString {
    val q = query.trim()
    val start = if (q.isEmpty()) -1 else text.indexOf(q, ignoreCase = true)
    if (start < 0) {
        append(text)
        return@buildAnnotatedString
    }
    append(text.substring(0, start))
    withStyle(
        SpanStyle(
            color = tint,
            fontWeight = FontWeight.Bold,
        ),
    ) {
        append(text.substring(start, start + q.length))
    }
    append(text.substring(start + q.length))
}
