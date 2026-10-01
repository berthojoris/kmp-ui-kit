package com.example.uiapp.ui.seatselector

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette

import com.example.uiapp.ui.components.UiTopBar

private enum class SeatState { AVAILABLE, SELECTED, OCCUPIED, VIP }

private data class Seat(
    val row: Int,
    val col: String,
    val state: SeatState,
)

private val Columns = listOf("A", "B", "C", "", "D", "E", "F")
private const val TotalRows = 12
private const val VipRows = 2
private const val RegularPrice = 150_000
private const val VipPrice = 250_000

private val OccupiedSeats = setOf(
    "1B", "1E", "2C", "2D", "3A", "3F", "4B", "4D",
    "5C", "5E", "6A", "6F", "7B", "7D", "8C", "8E",
    "9A", "9F", "10B", "10D", "11C", "11E", "12A", "12F",
)

private fun formatRupiah(amount: Int): String {
    val str = amount.toString()
    val result = StringBuilder()
    var count = 0
    for (i in str.indices.reversed()) {
        if (count > 0 && count % 3 == 0) result.insert(0, '.')
        result.insert(0, str[i])
        count++
    }
    return "Rp $result"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SeatSelectorLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var selectedSeats by remember { mutableStateOf(setOf<String>()) }
    var showConfirmation by remember { mutableStateOf(false) }

    val totalPrice = remember(selectedSeats) {
        selectedSeats.sumOf { id ->
            val row = id.dropLast(1).toIntOrNull() ?: 0
            if (row <= VipRows) VipPrice else RegularPrice
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Pilih Kursi",
                subtitle = "Simulasi denah kursi pesawat",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                SeatLegend("Tersedia", palette.surfaceMuted, palette.border)
                SeatLegend("Dipilih", palette.primary, palette.primary)
                SeatLegend("Terisi", palette.surface, palette.textMuted)
                SeatLegend("VIP", palette.primaryContainer, palette.primary)
            }

            // Seat map
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Cockpit indicator
                Surface(
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp, bottomStart = 8.dp, bottomEnd = 8.dp),
                    color = palette.surfaceMuted,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                    modifier = Modifier.width(180.dp).height(36.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Kokpit", fontSize = 11.sp, color = palette.textMuted)
                    }
                }
                Spacer(Modifier.height(12.dp))

                // Column headers
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(Modifier.width(28.dp)) // row number space
                    Columns.forEach { col ->
                        Box(Modifier.size(38.dp), contentAlignment = Alignment.Center) {
                            if (col.isNotEmpty()) {
                                Text(col, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.textSecondary)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))

                // Rows
                for (row in 1..TotalRows) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(Modifier.width(28.dp), contentAlignment = Alignment.Center) {
                            Text("$row", fontSize = 11.sp, color = palette.textMuted)
                        }
                        Columns.forEach { col ->
                            if (col.isEmpty()) {
                                Spacer(Modifier.size(38.dp)) // aisle
                            } else {
                                val seatId = "$row$col"
                                val isOccupied = seatId in OccupiedSeats
                                val isSelected = seatId in selectedSeats
                                val isVip = row <= VipRows
                                val state = when {
                                    isOccupied -> SeatState.OCCUPIED
                                    isSelected -> SeatState.SELECTED
                                    isVip -> SeatState.VIP
                                    else -> SeatState.AVAILABLE
                                }
                                SeatItem(
                                    seatId = seatId,
                                    state = state,
                                    onClick = {
                                        if (!isOccupied) {
                                            selectedSeats = if (isSelected) {
                                                selectedSeats - seatId
                                            } else {
                                                selectedSeats + seatId
                                            }
                                        }
                                    },
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Simulasi — bukan sistem reservasi sungguhan",
                    fontSize = 11.sp,
                    color = palette.textMuted,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            // Bottom bar
            Surface(
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                ) {
                    if (selectedSeats.isNotEmpty()) {
                        Text(
                            text = "Kursi: ${selectedSeats.sorted().joinToString(", ")}",
                            fontSize = 13.sp,
                            color = palette.textSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = if (selectedSeats.isEmpty()) "Belum ada kursi dipilih" else "${selectedSeats.size} kursi",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary,
                            )
                            if (selectedSeats.isNotEmpty()) {
                                Text(
                                    text = formatRupiah(totalPrice),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.primary,
                                )
                            }
                        }
                        Surface(
                            onClick = { if (selectedSeats.isNotEmpty()) showConfirmation = true },
                            enabled = selectedSeats.isNotEmpty(),
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedSeats.isNotEmpty()) palette.primary else palette.surfaceMuted,
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                        ) {
                            Text(
                                text = "Konfirmasi",
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedSeats.isNotEmpty()) palette.onPrimary else palette.textMuted,
                            )
                        }
                    }
                }
            }

            // Confirmation overlay
            if (showConfirmation) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(palette.background.copy(alpha = 0.9f))
                        .clickable { showConfirmation = false },
                    contentAlignment = Alignment.Center,
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        modifier = Modifier.padding(32.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text("Konfirmasi Berhasil", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                            Spacer(Modifier.height(8.dp))
                            Text("Kursi: ${selectedSeats.sorted().joinToString(", ")}", fontSize = 14.sp, color = palette.textSecondary)
                            Spacer(Modifier.height(4.dp))
                            Text("Total: ${formatRupiah(totalPrice)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                            Spacer(Modifier.height(4.dp))
                            Text("(Simulasi)", fontSize = 11.sp, color = palette.textMuted)
                            Spacer(Modifier.height(16.dp))
                            Surface(
                                onClick = {
                                    showConfirmation = false
                                    selectedSeats = emptySet()
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = palette.surface,
                                border = BorderStroke(1.dp, palette.border),
                                shadowElevation = 0.dp,
                                tonalElevation = 0.dp,
                            ) {
                                Text("Kembali", modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeatItem(seatId: String, state: SeatState, onClick: () -> Unit) {
    val palette = LocalAppPalette.current
    val isSelected = state == SeatState.SELECTED
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 300f),
    )
    val bgColor = when (state) {
        SeatState.AVAILABLE -> palette.surfaceMuted
        SeatState.SELECTED -> palette.primary
        SeatState.OCCUPIED -> palette.surface
        SeatState.VIP -> palette.primaryContainer
    }
    val borderColor = when (state) {
        SeatState.AVAILABLE -> palette.border
        SeatState.SELECTED -> palette.primary
        SeatState.OCCUPIED -> palette.textMuted
        SeatState.VIP -> palette.primary
    }
    val textColor = when (state) {
        SeatState.SELECTED -> palette.onPrimary
        SeatState.OCCUPIED -> palette.textMuted
        SeatState.VIP -> palette.onPrimaryContainer
        else -> palette.textPrimary
    }

    Box(
        modifier = Modifier
            .size(38.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = state != SeatState.OCCUPIED) { onClick() }
            .semantics { contentDescription = "Kursi $seatId, ${when(state) { SeatState.AVAILABLE -> "tersedia"; SeatState.SELECTED -> "dipilih"; SeatState.OCCUPIED -> "terisi"; SeatState.VIP -> "VIP tersedia" }}" },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (state == SeatState.OCCUPIED) "×" else seatId.takeLast(1),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SeatLegend(label: String, bgColor: androidx.compose.ui.graphics.Color, borderColor: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(bgColor)
                .border(1.dp, borderColor, RoundedCornerShape(3.dp)),
        )
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, color = LocalAppPalette.current.textSecondary)
    }
}
