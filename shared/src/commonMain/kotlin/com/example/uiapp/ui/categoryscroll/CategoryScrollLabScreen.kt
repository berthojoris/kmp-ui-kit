package com.example.uiapp.ui.categoryscroll

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.launch

private data class MenuProduct(
    val id: String,
    val name: String,
    val desc: String,
    val price: Long,
)

private data class MenuCategory(
    val title: String,
    val products: List<MenuProduct>,
)

private val MenuCategories = listOf(
    MenuCategory(
        "Kopi Signature",
        listOf(
            MenuProduct("k1", "Kopi Susu Gula Aren", "Espresso, susu segar, gula aren asli", 24000),
            MenuProduct("k2", "Es Kopi Klepon", "Kopi susu dengan aroma pandan", 27000),
            MenuProduct("k3", "Americano Tubruk", "Kopi hitam robusta tanpa gula", 20000),
            MenuProduct("k4", "Latte Kelapa", "Double shot dengan susu kelapa", 30000),
        ),
    ),
    MenuCategory(
        "Non-Kopi",
        listOf(
            MenuProduct("n1", "Matcha Latte", "Bubuk matcha Uji dan susu", 32000),
            MenuProduct("n2", "Cokelat Panas Rempah", "Cokelat 70% dengan kayu manis", 28000),
            MenuProduct("n3", "Teh Melati Tubruk", "Teh melati klasik diseduh manual", 18000),
        ),
    ),
    MenuCategory(
        "Roti & Pastry",
        listOf(
            MenuProduct("r1", "Croissant Mentega", "Dipanggang tiap 2 jam", 26000),
            MenuProduct("r2", "Roti Bakar Srikaya", "Roti sourdough dengan selai srikaya", 24000),
            MenuProduct("r3", "Pain au Chocolat", "Pastry cokelat batang Belgia", 30000),
            MenuProduct("r4", "Donat Kampung Gula", "Donat klasik tabur gula halus", 15000),
        ),
    ),
    MenuCategory(
        "Makanan Berat",
        listOf(
            MenuProduct("m1", "Nasi Goreng Kecombrang", "Nasi goreng rempah dengan ayam suwir", 42000),
            MenuProduct("m2", "Mie Ayam Jamur", "Mie tarik dengan topping jamur merang", 35000),
            MenuProduct("m3", "Soto Betawi Susu", "Kuah santan susu dengan daging sandung lamur", 45000),
            MenuProduct("m4", "Ayam Gepuk Sambal Ijo", "Ayam goreng pukul dengan sambal ijo", 40000),
            MenuProduct("m5", "Bakso Kuah Komplit", "Bakso sapi, tahu, siomay, dan pangsit", 33000),
        ),
    ),
    MenuCategory(
        "Pencuci Mulut",
        listOf(
            MenuProduct("p1", "Es Cendol Durian", "Cendol pandan dengan durian Medan", 30000),
            MenuProduct("p2", "Pisang Goreng Madu", "Pisang raja dengan madu hutan", 22000),
            MenuProduct("p3", "Panna Cotta Gula Jawa", "Panna cotta saus gula Jawa", 34000),
        ),
    ),
)

private sealed interface MenuRow {
    data class Header(val categoryIndex: Int, val title: String) : MenuRow
    data class ProductRow(val product: MenuProduct) : MenuRow
}

private fun formatHarga(price: Long): String {
    val digits = price.toString()
    val builder = StringBuilder()
    digits.reversed().forEachIndexed { index, c ->
        if (index > 0 && index % 3 == 0) builder.append('.')
        builder.append(c)
    }
    return "Rp " + builder.reverse().toString()
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CategoryScrollLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val chipState = rememberLazyListState()

    // Bangun daftar datar: header kategori diikuti produknya
    val rows = remember {
        buildList {
            MenuCategories.forEachIndexed { categoryIndex, category ->
                add(MenuRow.Header(categoryIndex, category.title))
                category.products.forEach { product -> add(MenuRow.ProductRow(product)) }
            }
        }
    }
    val categoryStartIndex = remember {
        rows.mapIndexedNotNull { index, row ->
            (row as? MenuRow.Header)?.let { it.categoryIndex to index }
        }.toMap()
    }

    // Kategori aktif mengikuti item pertama yang terlihat di list
    val activeCategory by remember {
        derivedStateOf {
            val firstVisible = listState.firstVisibleItemIndex
            var active = 0
            categoryStartIndex.forEach { (categoryIndex, startIndex) ->
                if (firstVisible >= startIndex) active = categoryIndex
            }
            active
        }
    }

    // Jaga chip aktif tetap terlihat di bar horizontal
    LaunchedEffect(activeCategory) {
        chipState.animateScrollToItem(activeCategory)
    }

    var cart by remember { mutableStateOf(mapOf<String, Int>()) }
    val cartCount = cart.values.sum()
    val cartTotal = remember(cart) {
        var total = 0L
        cart.forEach { (id, qty) ->
            MenuCategories.forEach { category ->
                category.products.find { it.id == id }?.let { total += it.price * qty }
            }
        }
        total
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Scroll-Sync Category",
                subtitle = "Tab kategori sinkron dengan list",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Bar chip kategori lengket
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface),
            ) {
                LazyRow(
                    state = chipState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(MenuCategories, key = { _, category -> category.title }) { index, category ->
                        val selected = index == activeCategory
                        Surface(
                            onClick = {
                                scope.launch {
                                    categoryStartIndex[index]?.let { listState.animateScrollToItem(it) }
                                }
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = if (selected) palette.primary else palette.surface,
                            border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = category.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) palette.onPrimary else palette.textSecondary,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(palette.border),
                )
            }

            // List menu dengan header section
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(
                    rows,
                    key = { _, row ->
                        when (row) {
                            is MenuRow.Header -> "header_${row.categoryIndex}"
                            is MenuRow.ProductRow -> row.product.id
                        }
                    },
                ) { _, row ->
                    when (row) {
                        is MenuRow.Header -> Text(
                            text = row.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                            modifier = Modifier.padding(top = 6.dp, bottom = 2.dp),
                        )

                        is MenuRow.ProductRow -> {
                            val qty = cart[row.product.id] ?: 0
                            MenuProductCard(
                                product = row.product,
                                qty = qty,
                                onAdd = { cart = cart + (row.product.id to qty + 1) },
                                onRemove = {
                                    cart = if (qty <= 1) cart - row.product.id else cart + (row.product.id to qty - 1)
                                },
                            )
                        }
                    }
                }
            }

            // Bottom bar ringkasan keranjang
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface)
                    .navigationBarsPadding(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(palette.border),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = if (cartCount == 0) "Keranjang kosong" else "$cartCount item di keranjang",
                            fontSize = 12.sp,
                            color = palette.textMuted,
                        )
                        Text(
                            text = formatHarga(cartTotal),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                        )
                    }
                    Surface(
                        onClick = { cart = emptyMap() },
                        enabled = cartCount > 0,
                        shape = RoundedCornerShape(12.dp),
                        color = if (cartCount > 0) palette.primary else palette.surfaceMuted,
                        border = BorderStroke(1.dp, if (cartCount > 0) palette.primary else palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Pesan (Simulasi)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (cartCount > 0) palette.onPrimary else palette.textMuted,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuProductCard(
    product: MenuProduct,
    qty: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.surface, RoundedCornerShape(14.dp))
            .border(1.dp, palette.border, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = product.desc,
                fontSize = 11.sp,
                color = palette.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = formatHarga(product.price),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = palette.primary,
            )
        }
        Spacer(modifier = Modifier.padding(horizontal = 6.dp))
        if (qty == 0) {
            Surface(
                onClick = onAdd,
                shape = RoundedCornerShape(10.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.primary),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Tambah",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                    )
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                QtyButton(label = "-", onClick = onRemove, contentDesc = "Kurangi ${product.name}")
                Text(
                    text = "$qty",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )
                QtyButton(label = "+", onClick = onAdd, contentDesc = "Tambah ${product.name}")
            }
        }
    }
}

@Composable
private fun QtyButton(
    label: String,
    onClick: () -> Unit,
    contentDesc: String,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = palette.surfaceMuted,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .semantics { contentDescription = contentDesc }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
            )
        }
    }
}
