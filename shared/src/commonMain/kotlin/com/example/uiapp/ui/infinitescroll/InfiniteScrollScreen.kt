package com.example.uiapp.ui.infinitescroll

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PageSize = 12
private const val TotalPages = 6

private data class FeedItem(
    val id: Int,
    val title: String,
    val subtitle: String,
)

private val Cities = listOf(
    "Ubud", "Canggu", "Seminyak", "Uluwatu", "Sanur", "Nusa Penida",
    "Kintamani", "Lovina", "Amed", "Sidemen", "Munduk", "Jimbaran",
)

private fun pageItems(page: Int): List<FeedItem> {
    val start = (page - 1) * PageSize
    return List(PageSize) { offset ->
        val index = start + offset
        val city = Cities[index % Cities.size]
        FeedItem(
            id = index + 1,
            title = "Villa $city #${index + 1}",
            subtitle = "${(index % 5) + 2} kamar \u00B7 ${(index % 4) + 1} kolam \u00B7 bintang 4.${(index % 9)}",
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InfiniteScrollScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    BackHandler(enabled = true) { onBack() }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var items by remember { mutableStateOf(pageItems(1)) }
    var page by remember { mutableIntStateOf(1) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var endReached by remember { mutableStateOf(false) }
    var errorShown by remember { mutableStateOf(false) }

    fun loadMore() {
        if (loading || error || endReached) return
        loading = true
        scope.launch {
            delay(900)
            if (!errorShown) {
                errorShown = true
                error = true
                loading = false
            } else {
                val next = page + 1
                items = items + pageItems(next)
                page = next
                if (next >= TotalPages) endReached = true
                loading = false
            }
        }
    }

    val nearEnd by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= items.size - 3 && !endReached && !error
        }
    }

    LaunchedEffect(nearEnd, loading) {
        if (nearEnd && !loading && !error) loadMore()
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Infinite Scroll",
                subtitle = "Pagination dan load-more",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(key = "summary") {
                Text(
                    text = "$page dari $TotalPages halaman \u00B7 ${items.size} properti",
                    fontSize = 12.sp,
                    color = palette.textMuted,
                    modifier = Modifier.padding(vertical = 10.dp),
                )
            }

            items(items, key = { it.id }) { item ->
                FeedRow(item = item)
            }

            item(key = "footer") {
                LoadMoreFooter(
                    loading = loading,
                    error = error,
                    endReached = endReached,
                    onRetry = {
                        error = false
                        loadMore()
                    },
                )
            }
        }
    }
}

@Composable
private fun FeedRow(item: FeedItem) {
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item.title.removePrefix("Villa ").take(1),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.subtitle,
                    fontSize = 12.sp,
                    color = palette.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun LoadMoreFooter(
    loading: Boolean,
    error: Boolean,
    endReached: Boolean,
    onRetry: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        when {
            loading -> Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = palette.primary,
                    strokeWidth = 2.dp,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Memuat properti lain\u2026",
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                )
            }

            error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Gagal memuat. Periksa koneksi Anda.",
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    onClick = onRetry,
                    shape = RoundedCornerShape(12.dp),
                    color = palette.primary,
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Text(
                        text = "Coba lagi",
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 9.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            }

            endReached -> Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(palette.primary),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Semua data sudah dimuat",
                    fontSize = 12.sp,
                    color = palette.textMuted,
                )
            }
        }
    }
}
