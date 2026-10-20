package com.example.uiapp.ui.flashsale

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.HapticKind
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private data class FlashProduct(
    val id: Int, val name: String, val price: Int, val oldPrice: Int,
    val stock: Int, val sold: Int, val reminder: Boolean = false,
)

private data class FlashSession(val id: String, val title: String, val startsInSec: Long, val products: List<FlashProduct>, val live: Boolean)

private fun formatRupiah(amount: Int): String {
    val s = amount.toString(); val sb = StringBuilder(); var c = 0
    for (i in s.indices.reversed()) { if (c > 0 && c % 3 == 0) sb.insert(0, '.'); sb.insert(0, s[i]); c++ }
    return "Rp $sb"
}

private fun formatCountdown(sec: Long): String {
    val h = sec / 3600; val m = (sec % 3600) / 60; val s = sec % 60
    return "%02d:%02d:%02d".format(h, m, s)
}

// ponytail: stok & countdown diperbarui per detik dari generator lokal; ganti ke sumber data nyata saat dipakai produksi.
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FlashSaleLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current

    var liveSec by remember { mutableIntStateOf(3600 + 125) }
    var upcomingSec by remember { mutableIntStateOf(7200) }
    // stok terjual bertambah otomatis → state hampir habis & habis muncul sendiri
    var soldBoost by remember { mutableIntStateOf(0) }
    var reminders by remember { mutableStateOf(setOf<Int>()) }
    var bought by remember { mutableStateOf(setOf<Int>()) }
    var toast by remember { mutableStateOf<String?>(null) }

    val liveProducts = remember {
        listOf(
            FlashProduct(1, "Tumbler Vacuum 500ml", 89_000, 149_000, 50, 38),
            FlashProduct(2, "Mouse Wireless Senyap", 129_000, 219_000, 30, 27),
            FlashProduct(3, "Lampu Meja LED Hemat", 79_000, 129_000, 40, 12),
            FlashProduct(4, "Powerbank 10.000mAh", 159_000, 259_000, 20, 19),
        )
    }
    val upcomingProducts = remember {
        listOf(
            FlashProduct(11, "Kopi Single Origin 200g", 75_000, 110_000, 100, 0),
            FlashProduct(12, "Botol Susu Anti Tumpah", 45_000, 79_000, 80, 0),
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            liveSec = (liveSec - 1).coerceAtLeast(0)
            upcomingSec = (upcomingSec - 1).coerceAtLeast(0)
            soldBoost += 1
        }
    }

    val soldOf: (FlashProduct) -> Int = { (it.sold + soldBoost).coerceAtMost(it.stock) }
    val liveEnded = liveSec == 0

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Flash Sale", subtitle = "Countdown & kelangkaan stok — simulasi", onBack = onBack) },
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "intro") {
                    LabIntroCard(title = "Flash Sale & Kelangkaan", body = "Countdown besar, bar stok terjual, dan tombol Ingatkan Saya saat stok habis. Sesi berikutnya belum dibuka hingga hitung mundurnya selesai. Semua transaksi adalah simulasi.")
                }
                item(key = "toast") {
                    if (toast != null) {
                        Surface(shape = RoundedCornerShape(12.dp), color = palette.success.copy(alpha = 0.12f), border = BorderStroke(1.dp, palette.success), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                            Text(toast!!, fontSize = 12.sp, color = palette.textPrimary, modifier = Modifier.fillMaxWidth().padding(12.dp))
                        }
                    }
                }
                item(key = "live-header") { SessionHeader(title = "Serbu Malam — Sedang Berlangsung", countdownSec = liveSec.toLong(), danger = liveSec <= 300, ended = liveEnded) }
                if (liveEnded) {
                    item(key = "live-ended") {
                        Surface(shape = RoundedCornerShape(12.dp), color = palette.surfaceMuted, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                            Text("Sesi berakhir. Produk kembali ke harga normal — pantau sesi berikutnya.", fontSize = 12.sp, color = palette.textSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(16.dp))
                        }
                    }
                } else {
                    items(liveProducts, key = { it.id }) { p ->
                        val sold = soldOf(p)
                        val soldOut = sold >= p.stock
                        val purchased = p.id in bought
                        FlashCard(
                            product = p, sold = sold,
                            state = when { purchased -> CardState.PURCHASED; soldOut -> CardState.SOLD_OUT; sold >= p.stock * 4 / 5 -> CardState.ALMOST_OUT; else -> CardState.AVAILABLE },
                            onBuy = {
                                bought = bought + p.id
                                toast = "Berhasil memesan ${p.name} (simulasi)"
                                PerformHapticFeedback(HapticKind.SUCCESS)
                            },
                            onRemind = {
                                reminders = if (p.id in reminders) reminders - p.id else reminders + p.id
                                toast = if (p.id in reminders) "Pengingat dibatalkan" else "Kami ingatkan saat stok tersedia (simulasi)"
                                PerformHapticFeedback(HapticKind.MEDIUM)
                            },
                            reminderOn = p.id in reminders,
                        )
                    }
                }
                item(key = "upcoming-header") { SessionHeader(title = "Sesi Pagi — Segera", countdownSec = upcomingSec.toLong(), danger = false, ended = false, upcoming = true) }
                items(upcomingProducts, key = { it.id }) { p ->
                    FlashCard(product = p, sold = 0, state = CardState.UPCOMING, onBuy = {}, onRemind = {
                        reminders = if (p.id in reminders) reminders - p.id else reminders + p.id
                        toast = if (p.id in reminders) "Pengingat dibatalkan" else "Kami ingatkan saat sesi dibuka (simulasi)"
                        PerformHapticFeedback(HapticKind.MEDIUM)
                    }, reminderOn = p.id in reminders)
                }
                item(key = "spacer") { Spacer(Modifier.height(4.dp)) }
            }
        }
    }
}

private enum class CardState { AVAILABLE, ALMOST_OUT, SOLD_OUT, PURCHASED, UPCOMING }

@Composable
private fun SessionHeader(title: String, countdownSec: Long, danger: Boolean, ended: Boolean, upcoming: Boolean = false) {
    val palette = LocalAppPalette.current
    Surface(shape = RoundedCornerShape(14.dp), color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
        Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            Text(
                if (ended) "BERAKHIR" else formatCountdown(countdownSec),
                fontSize = 18.sp, fontWeight = FontWeight.Bold,
                color = if (ended) palette.textMuted else if (danger) palette.danger else palette.primary,
                modifier = Modifier.padding(start = 10.dp).semantics { contentDescription = if (ended) "Sesi berakhir" else "Hitung mundur ${formatCountdown(countdownSec)}" },
            )
        }
    }
}

@Composable
private fun FlashCard(product: FlashProduct, sold: Int, state: CardState, onBuy: () -> Unit, onRemind: () -> Unit, reminderOn: Boolean) {
    val palette = LocalAppPalette.current
    val pct = if (product.stock == 0) 100 else (sold * 100 / product.stock).coerceIn(0, 100)
    Surface(shape = RoundedCornerShape(16.dp), color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
        Row(Modifier.fillMaxWidth().heightIn(min = 96.dp).padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // ilustrasi produk: gradien mock, bukan gambar
            val gradient = remember(product.id) {
                Brush.linearGradient(listOf(Color(0xFF2E8B74), Color(0xFF9FD8C6)))
            }
            Box(
                Modifier
                    .size(width = 72.dp, height = 80.dp)
                    .background(gradient, RoundedCornerShape(10.dp))
                    .semantics { contentDescription = "Ilustrasi produk ${product.name}" },
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(product.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(formatRupiah(product.price), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (state == CardState.UPCOMING) palette.textSecondary else palette.primary)
                    Text(formatRupiah(product.oldPrice), fontSize = 10.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                if (state != CardState.UPCOMING) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        val barColor = when (state) { CardState.SOLD_OUT, CardState.ALMOST_OUT -> palette.danger; else -> palette.primary }
                        Box(Modifier.fillMaxWidth().height(6.dp).background(palette.surfaceMuted, RoundedCornerShape(3.dp))) {
                            Box(Modifier.fillMaxWidth(pct / 100f).height(6.dp).background(barColor, RoundedCornerShape(3.dp)))
                        }
                        Text(
                            when (state) {
                                CardState.SOLD_OUT -> "Habis terjual"
                                else -> "Terjual $sold/${product.stock}"
                            },
                            fontSize = 10.sp, color = if (state == CardState.SOLD_OUT) palette.danger else palette.textMuted,
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    when (state) {
                        CardState.AVAILABLE, CardState.ALMOST_OUT -> {
                            SmallAction(text = if (state == CardState.ALMOST_OUT) "Beli Cepat" else "Beli", primary = true, enabled = true, onClick = onBuy)
                            if (state == CardState.ALMOST_OUT) StatusPill(text = "Hampir habis", color = palette.warning)
                        }
                        CardState.SOLD_OUT -> SmallAction(text = "Ingatkan Saya", primary = false, enabled = true, onClick = onRemind, toggled = reminderOn)
                        CardState.UPCOMING -> SmallAction(text = if (reminderOn) "Batal Ingatkan" else "Ingatkan Saya", primary = false, enabled = true, onClick = onRemind, toggled = reminderOn)
                        CardState.PURCHASED -> StatusPill(text = "Berhasil dipesan", color = palette.success)
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallAction(text: String, primary: Boolean, enabled: Boolean, onClick: () -> Unit, toggled: Boolean = false) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick, enabled = enabled,
        shape = RoundedCornerShape(999.dp),
        color = when { toggled -> palette.surfaceMuted; primary -> palette.primary; else -> palette.surface },
        border = BorderStroke(1.dp, when { toggled -> palette.primary; primary -> palette.primary; else -> palette.border }),
        shadowElevation = 0.dp, tonalElevation = 0.dp,
        modifier = Modifier.semantics { contentDescription = "$text${if (toggled) ", aktif" else ""}" },
    ) {
        Text(
            text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            color = when { toggled -> palette.primary; primary -> palette.onPrimary; else -> palette.textPrimary },
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        )
    }
}
