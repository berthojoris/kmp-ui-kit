package com.example.uiapp.ui.flightitinerary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar

private data class FlightSegment(
    val id: String,
    val airline: String,
    val flightCode: String,
    val from: String,
    val fromCity: String,
    val to: String,
    val toCity: String,
    val departure: String,
    val arrival: String,
    val duration: String,
    val aircraft: String,
    val status: FlightStatus,
    val seat: String,
    val gate: String,
)

private enum class FlightStatus(val label: String) {
    ON_TIME("Tepat Waktu"),
    DELAYED("Tertunda 30m"),
    CANCELLED("Dibatalkan"),
}

private data class Layover(val airport: String, val duration: String, val terminal: String, val baggageClaim: String)

private data class Itinerary(val name: String, val segments: List<FlightSegment>, val layovers: List<Layover>)

private val Itineraries = listOf(
    Itinerary("Jakarta → Bali", listOf(
        FlightSegment("1", "Garuda Indonesia", "GA-404", "CGK", "Jakarta", "DPS", "Bali", "08:00", "10:45", "2j 45m", "Boeing 737-800", FlightStatus.ON_TIME, "12A", "A3"),
    ), emptyList()),
    Itinerary("Jakarta → Tokyo", listOf(
        FlightSegment("2a", "Singapore Airlines", "SQ-957", "CGK", "Jakarta", "SIN", "Singapura", "06:30", "09:15", "1j 45m", "Airbus A350", FlightStatus.ON_TIME, "22C", "B5"),
        FlightSegment("2b", "ANA", "NH-842", "SIN", "Singapura", "NRT", "Tokyo", "11:45", "19:30", "7j 45m", "Boeing 787", FlightStatus.DELAYED, "31A", "C12"),
    ), listOf(Layover("SIN", "2j 30m", "Terminal 3 → Terminal 2", "Tidak perlu ambil bagasi"))),
    Itinerary("Jakarta → London", listOf(
        FlightSegment("3a", "Singapore Airlines", "SQ-961", "CGK", "Jakarta", "SIN", "Singapura", "07:00", "09:45", "1j 45m", "Airbus A380", FlightStatus.ON_TIME, "15F", "A2"),
        FlightSegment("3b", "Emirates", "EK-349", "SIN", "Singapura", "DXB", "Dubai", "12:30", "17:00", "7j 30m", "Boeing 777", FlightStatus.ON_TIME, "28A", "D8"),
        FlightSegment("3c", "Emirates", "EK-003", "DXB", "Dubai", "LHR", "London", "20:00", "00:30+1", "7j 30m", "Airbus A380", FlightStatus.CANCELLED, "8K", "B22"),
    ), listOf(
        Layover("SIN", "2j 45m", "Terminal 3", "Bagasi transit otomatis"),
        Layover("DXB", "3j 00m", "Terminal 3 → Terminal 3", "Bagasi transit otomatis"),
    )),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FlightItineraryLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var selectedIdx by remember { mutableStateOf(0) }
    val itinerary = remember(selectedIdx) { Itineraries[selectedIdx] }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Jadwal Penerbangan", subtitle = "Simulasi itinerari multi-kota", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Tab chips
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Itineraries.forEachIndexed { idx, it ->
                    Surface(
                        onClick = { selectedIdx = idx },
                        shape = RoundedCornerShape(10.dp),
                        color = if (idx == selectedIdx) palette.primary else palette.surface,
                        border = BorderStroke(1.dp, if (idx == selectedIdx) palette.primary else palette.border),
                        shadowElevation = 0.dp, tonalElevation = 0.dp,
                    ) {
                        Text(it.name, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (idx == selectedIdx) palette.onPrimary else palette.textSecondary)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                val segments = itinerary.segments
                val layovers = itinerary.layovers
                items(segments, key = { it.id }) { segment ->
                    val segIndex = segments.indexOf(segment)
                    FlightSegmentCard(segment, isFirst = segIndex == 0, isLast = segIndex == segments.lastIndex)
                    if (segIndex < layovers.size) {
                        LayoverCard(layovers[segIndex])
                    }
                }
                item(key = "baggage") {
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp, tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🧳", fontSize = 20.sp)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Bagasi", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                                Text("Kabin 7 kg · Bagasi 20 kg", fontSize = 12.sp, color = palette.textSecondary)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Simulasi — bukan data penerbangan sungguhan", fontSize = 10.sp, color = palette.textMuted, modifier = Modifier.padding(bottom = 8.dp))
                }
            }

            // Boarding pass
            val firstSeg = itinerary.segments.first()
            Surface(
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp, tonalElevation = 0.dp,
            ) {
                Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                    Text("Boarding Pass", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.textMuted)
                    Spacer(Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Penumpang", fontSize = 10.sp, color = palette.textMuted)
                            Text("Ahmad Rizki", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Kursi", fontSize = 10.sp, color = palette.textMuted)
                            Text(firstSeg.seat, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Gate", fontSize = 10.sp, color = palette.textMuted)
                            Text(firstSeg.gate, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Boarding", fontSize = 10.sp, color = palette.textMuted)
                            Text(firstSeg.departure, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    // Barcode mock
                    Canvas(modifier = Modifier.fillMaxWidth().height(28.dp)) {
                        val barWidth = size.width / 60f
                        for (i in 0 until 60) {
                            val w = if (i % 3 == 0) barWidth * 0.6f else barWidth * 0.3f
                            drawLine(
                                color = palette.textPrimary,
                                start = Offset(i * barWidth + barWidth / 2, 0f),
                                end = Offset(i * barWidth + barWidth / 2, size.height),
                                strokeWidth = w, cap = StrokeCap.Butt,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlightSegmentCard(segment: FlightSegment, isFirst: Boolean, isLast: Boolean) {
    val palette = LocalAppPalette.current
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(28.dp)) {
            if (!isFirst) { Box(Modifier.width(2.dp).height(8.dp).background(palette.border)) }
            Box(Modifier.size(12.dp).clip(CircleShape).background(palette.primary))
            Box(Modifier.width(2.dp).weight(1f).background(palette.border))
        }
        Spacer(Modifier.width(10.dp))
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = 0.dp, tonalElevation = 0.dp,
            modifier = Modifier.weight(1f).padding(bottom = 4.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("${segment.airline} · ${segment.flightCode}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    StatusPill(segment.status)
                }
                Spacer(Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(segment.from, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        Text(segment.fromCity, fontSize = 11.sp, color = palette.textMuted)
                        Text(segment.departure, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(segment.duration, fontSize = 11.sp, color = palette.textMuted)
                        Text("→", fontSize = 18.sp, color = palette.primary)
                        Text(segment.aircraft, fontSize = 10.sp, color = palette.textMuted)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(segment.to, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        Text(segment.toCity, fontSize = 11.sp, color = palette.textMuted)
                        Text(segment.arrival, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusPill(status: FlightStatus) {
    val palette = LocalAppPalette.current
    val color = when (status) {
        FlightStatus.ON_TIME -> palette.success
        FlightStatus.DELAYED -> palette.warning
        FlightStatus.CANCELLED -> palette.danger
    }
    StatusPill(text = status.label, color = color)
}

@Composable
private fun LayoverCard(layover: Layover) {
    val palette = LocalAppPalette.current
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(28.dp)) {
            Box(Modifier.width(2.dp).height(8.dp).background(palette.border))
            Box(Modifier.size(8.dp).clip(CircleShape).background(palette.warning))
            Box(Modifier.width(2.dp).height(8.dp).background(palette.border))
        }
        Spacer(Modifier.width(10.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = palette.warning.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, palette.warning.copy(alpha = 0.3f)),
            shadowElevation = 0.dp, tonalElevation = 0.dp,
            modifier = Modifier.weight(1f).padding(bottom = 4.dp),
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("Transit ${layover.duration} di ${layover.airport}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.warning)
                Text(layover.terminal, fontSize = 11.sp, color = palette.textSecondary)
                Text(layover.baggageClaim, fontSize = 11.sp, color = palette.textMuted)
            }
        }
    }
}
