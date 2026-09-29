package com.example.uiapp.ui.masonrygrid

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private data class MasonryItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val heightDp: Int,
    val gradientStart: Color,
    val gradientEnd: Color,
    val likes: Int,
)

// Ilustrasi mock lokal berbasis gradien (tanpa gambar jaringan)
private val MasonryItems = listOf(
    MasonryItem(1, "Villa Tepi Sawah Ubud", "Bali · mulai Rp 1,2 jt/malam", 190, Color(0xFF0A332C), Color(0xFF2E7D5B), 248),
    MasonryItem(2, "Kopi Gayo Single Origin", "Aceh · 200g sangrai medium", 140, Color(0xFF4E342E), Color(0xFF8D6E63), 96),
    MasonryItem(3, "Sneakers Lokal Seri Kota", "Bandung · edisi terbatas", 220, Color(0xFF1A237E), Color(0xFF3949AB), 512),
    MasonryItem(4, "Tas Rotan Anyaman Bali", "Handmade · diameter 30cm", 160, Color(0xFF6D4C41), Color(0xFFA1887F), 187),
    MasonryItem(5, "Sunset Pantai Tanjung Aan", "Lombok · spot foto barat", 250, Color(0xFFBF360C), Color(0xFFF4511E), 731),
    MasonryItem(6, "Batik Tulis Parang", "Yogyakarta · kain katun primis", 180, Color(0xFF3E2723), Color(0xFF5D4037), 154),
    MasonryItem(7, "Terrarium Mini Kantor", "Jakarta · perawatan mudah", 150, Color(0xFF1B5E20), Color(0xFF43A047), 88),
    MasonryItem(8, "Keramik Stoneware Set", "Yogyakarta · 4 pcs glasir matte", 200, Color(0xFF37474F), Color(0xFF607D8B), 203),
    MasonryItem(9, "Lilin Aromaterapi Sereh", "Bandung · 40 jam nyala", 145, Color(0xFF4A148C), Color(0xFF7B1FA2), 66),
    MasonryItem(10, "Kalung Perak Kotagede", "Yogyakarta · perak 925", 175, Color(0xFF263238), Color(0xFF546E7A), 342),
    MasonryItem(11, "Mad Hutan Sumbawa", "NTB · 250ml murni", 155, Color(0xFFE65100), Color(0xFFF9A825), 129),
    MasonryItem(12, "Poster Ilustrasi Kota Tua", "Jakarta · cetak A3 art paper", 185, Color(0xFF880E4F), Color(0xFFC2185B), 274),
)

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun MasonryGridLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var items by remember { mutableStateOf(MasonryItems) }
    var savedIds by remember { mutableStateOf(setOf<Int>()) }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Masonry Waterfall Grid",
                subtitle = "Ketinggian bervariasi ala Pinterest",
                onBack = onBack,
                action = {
                    Text(
                        text = "Acak",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { items = items.shuffled() }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                    )
                },
            )
        },
    ) { innerPadding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp,
        ) {
            item(span = StaggeredGridItemSpan.FullLine, key = "intro") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Grid Air Terjun (Waterfall)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = "Setiap kartu punya tinggi berbeda dan ditata otomatis oleh staggered grid. Ketuk kartu untuk menyimpan; status tersimpan ditandai border dan label teks, bukan warna saja. Seluruh visual adalah gradien mock lokal.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = palette.textSecondary,
                        )
                    }
                }
            }

            items(items, key = { it.id }) { item ->
                MasonryCard(
                    item = item,
                    isSaved = item.id in savedIds,
                    onToggleSave = {
                        savedIds = if (item.id in savedIds) savedIds - item.id else savedIds + item.id
                    },
                )
            }
        }
    }
}

@Composable
private fun MasonryCard(
    item: MasonryItem,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val gradient = remember(item.id) {
        Brush.linearGradient(listOf(item.gradientStart, item.gradientEnd))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surface)
            .border(
                width = if (isSaved) 1.5.dp else 1.dp,
                color = if (isSaved) palette.primary else palette.border,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onToggleSave),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(item.heightDp.dp)
                .background(gradient),
        ) {
            if (isSaved) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "Tersimpan",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
        }
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.subtitle,
                fontSize = 11.sp,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${item.likes} suka",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = palette.textMuted,
                )
                Text(
                    text = if (isSaved) "Hapus" else "Simpan",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSaved) palette.danger else palette.primary,
                )
            }
        }
    }
}
