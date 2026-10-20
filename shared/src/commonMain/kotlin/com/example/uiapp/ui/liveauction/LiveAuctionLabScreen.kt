package com.example.uiapp.ui.liveauction

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
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

private data class BidEntry(val id: Int, val bidder: String, val amount: Long, val isMine: Boolean)

private val RivalBidders = listOf("Bidder_88", "KolektorJKT", "Seniman87", "Auction_Hunter")

private fun formatRupiah(amount: Long): String {
    val s = amount.toString()
    val sb = StringBuilder()
    var c = 0
    for (i in s.indices.reversed()) { if (c > 0 && c % 3 == 0) sb.insert(0, '.'); sb.insert(0, s[i]); c++ }
    return "Rp $sb"
}

private fun formatCountdown(sec: Long): String {
    val m = sec / 60; val s = sec % 60
    return "%02d:%02d".format(m, s)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LiveAuctionLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current

    var remaining by remember { mutableLongStateOf(120L) }
    var currentBid by remember { mutableLongStateOf(1_500_000L) }
    var bidCount by remember { mutableIntStateOf(0) }
    var history by remember { mutableStateOf(listOf(BidEntry(0, "KolektorJKT", 1_500_000L, false))) }
    var topIsMine by remember { mutableStateOf(false) }
    var notif by remember { mutableStateOf<String?>(null) }
    var ended by remember { mutableStateOf<Boolean?>(null) } // null = berjalan, true = menang, false = kalah
    var lastSnipeExtension by remember { mutableIntStateOf(0) }
    var nextId by remember { mutableIntStateOf(1) }

    val bidStep = 100_000L
    val youAreTop = topIsMine && ended == null

    // detik berjalan + rival otomatis menawar (simulasi lokal)
    LaunchedEffect(ended) {
        if (ended != null) return@LaunchedEffect
        while (true) {
            delay(1000)
            remaining -= 1
            if (remaining <= 0) { ended = topIsMine; notif = if (topIsMine) "Lelang berakhir — Anda menang!" else "Lelang berakhir — Anda kalah"; PerformHapticFeedback(if (topIsMine) HapticKind.SUCCESS else HapticKind.WARNING); break }
            // penawar lain menawar tiap ±7 detik selama Anda bukan penawar tertinggi
            if ((remaining % 7L) == 0L && !topIsMine) {
                val rival = RivalBidders[(nextId + remaining).toInt() % RivalBidders.size]
                currentBid += bidStep
                history = history + BidEntry(nextId, rival, currentBid, false)
                nextId++; bidCount++
                if (remaining <= 10 && lastSnipeExtension < 3) {
                    remaining += 15; lastSnipeExtension++
                    notif = "Penawaran detik akhir — waktu diperpanjang 15 detik (anti-sniping)"
                } else {
                    notif = "$rival menawar ${formatRupiah(currentBid)}"
                }
                PerformHapticFeedback(HapticKind.LIGHT)
            }
        }
    }

    fun placeBid() {
        if (ended != null) return
        currentBid += bidStep
        history = history + BidEntry(nextId, "Anda", currentBid, true)
        nextId++; bidCount++
        topIsMine = true
        if (remaining <= 10 && lastSnipeExtension < 3) {
            remaining += 15; lastSnipeExtension++
            notif = "Penawaran Anda di detik akhir — waktu diperpanjang 15 detik"
        } else {
            notif = "Penawaran Anda ${formatRupiah(currentBid)} tercatat"
        }
        PerformHapticFeedback(HapticKind.SUCCESS)
    }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Live Auction", subtitle = "Ruang penawaran langsung — simulasi", onBack = onBack) },
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "intro") {
                    LabIntroCard(title = "Lelang Langsung", body = "Tawar cepat, pantau riwayat penawaran, dan perpanjangan waktu anti-sniping. Penawar lain dijalankan otomatis secara lokal — seluruh nilai adalah simulasi.")
                }
                item(key = "lot") {
                    Surface(shape = RoundedCornerShape(16.dp), color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp, modifier = Modifier.animateContentSize()) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("Lot 12 — Lukisan \"Senja di Pelabuhan\"", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("Seni & Dekorasi · penutupan otomatis", fontSize = 11.sp, color = palette.textMuted)
                                }
                                StatusPill(text = "Simulasi", color = palette.warning)
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                                Column {
                                    Text("PENAWARAN TERTINGGI", fontSize = 9.sp, color = palette.textMuted)
                                    Text(formatRupiah(currentBid), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("SISA WAKTU", fontSize = 9.sp, color = palette.textMuted)
                                    Text(
                                        formatCountdown(remaining.coerceAtLeast(0)),
                                        fontSize = 22.sp, fontWeight = FontWeight.Bold,
                                        color = if (remaining <= 15 && ended == null) palette.danger else palette.textPrimary,
                                        modifier = Modifier.semantics { contentDescription = "Sisa waktu ${formatCountdown(remaining)}" },
                                    )
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                when {
                                    ended == true -> StatusPill(text = "Anda menang", color = palette.success)
                                    ended == false -> StatusPill(text = "Lelang berakhir — Anda kalah", color = palette.danger)
                                    youAreTop -> StatusPill(text = "Anda tertinggi", color = palette.success)
                                    else -> StatusPill(text = "Anda ter-outbid", color = palette.danger)
                                }
                                StatusPill(text = "$bidCount penawaran", color = palette.info)
                            }
                            if (ended != null) {
                                Text(
                                    if (ended == true) "Selamat! Lot dialokasikan untuk Anda. Petugas akan menghubungi via pesan (simulasi)." else "Lot dimenangkan penawar lain. Coba lot berikutnya.",
                                    fontSize = 12.sp, color = palette.textSecondary,
                                )
                            }
                        }
                    }
                }
                if (notif != null) {
                    item(key = "notif") {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = palette.info.copy(alpha = 0.12f), border = BorderStroke(1.dp, palette.info),
                            shadowElevation = 0.dp, tonalElevation = 0.dp,
                            modifier = Modifier.semantics { contentDescription = "Notifikasi: ${notif!!}" },
                        ) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(notif!!, fontSize = 12.sp, color = palette.textPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                Text(
                                    "Tutup", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.info,
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .clickable(role = Role.Button) { notif = null }
                                        .semantics { contentDescription = "Tutup notifikasi lelang" },
                                )
                            }
                        }
                    }
                }
                item(key = "history-header") {
                    Text("Riwayat Penawaran", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                }
                if (history.isEmpty()) {
                    item(key = "history-empty") { EmptyRow(text = "Belum ada penawaran") }
                } else {
                    items(history.reversed(), key = { it.id }) { bid ->
                        Surface(shape = RoundedCornerShape(12.dp), color = palette.surface, border = BorderStroke(1.dp, if (bid.isMine) palette.primary else palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(bid.bidder, fontSize = 13.sp, fontWeight = if (bid.isMine) FontWeight.Bold else FontWeight.Medium, color = palette.textPrimary)
                                    if (bid.isMine) StatusPill(text = "Anda", color = palette.primary)
                                }
                                Text(formatRupiah(bid.amount), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                            }
                        }
                    }
                }
                item(key = "spacer") { Spacer(Modifier.height(4.dp)) }
            }

            // action bar bawah
            Surface(color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        QuickBid(text = "Tawar +Rp 100.000", enabled = ended == null, modifier = Modifier.weight(1f), onClick = { placeBid() })
                        QuickBid(text = "Tawar +Rp 250.000", enabled = false, modifier = Modifier.weight(1f), onClick = {}) // contoh tombol dinonaktifkan (step belum tersedia)
                    }
                    Text(
                        if (ended != null) "Lelang berakhir — tombol bid dinonaktifkan" else "Tombol bid aktif hingga waktu habis",
                        fontSize = 11.sp, color = if (ended != null) palette.textMuted else palette.success,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickBid(text: String, enabled: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        color = if (enabled) palette.primary else palette.surfaceMuted,
        border = BorderStroke(1.dp, if (enabled) palette.primary else palette.border),
        shadowElevation = 0.dp, tonalElevation = 0.dp,
        modifier = modifier.semantics { contentDescription = if (enabled) text else "$text tidak tersedia" },
    ) {
        Text(
            text,
            modifier = Modifier.padding(vertical = 15.dp).fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
            color = if (enabled) palette.onPrimary else palette.textMuted,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun EmptyRow(text: String) {
    val palette = LocalAppPalette.current
    Surface(shape = RoundedCornerShape(12.dp), color = palette.surfaceMuted, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
        Text(text, fontSize = 12.sp, color = palette.textMuted, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(16.dp))
    }
}
