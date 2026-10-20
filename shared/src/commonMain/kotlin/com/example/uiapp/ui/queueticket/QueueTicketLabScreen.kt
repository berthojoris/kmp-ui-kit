package com.example.uiapp.ui.queueticket

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.FlatSecondaryButton
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private enum class QueueState { WAITING, ALMOST, CALLED, MISSED, DONE, CANCELLED }

private data class LaneInfo(val id: String, val name: String, val serving: Int, val behind: Int)

private val Lanes = listOf(
    LaneInfo("umum", "Loket Umum", 41, 23),
    LaneInfo("prioritas", "Loket Prioritas", 12, 4),
)

private fun etaText(position: Int): String {
    val minutes = position * 2
    return if (minutes <= 0) "sekarang" else "± $minutes menit"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun QueueTicketLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current

    var ticketNo by remember { mutableIntStateOf(42) }
    var laneId by remember { mutableStateOf("umum") }
    var state by remember { mutableStateOf(QueueState.WAITING) }
    var notif by remember { mutableStateOf<String?>(null) }
    var servedExtra by remember { mutableIntStateOf(0) } // loket sudah maju seberapa jauh

    val lane = remember(laneId) { Lanes.first { it.id == laneId } }
    val servingNow = lane.serving + servedExtra
    val position = (ticketNo - servingNow).coerceAtLeast(0)

    // antrean berjalan: nomor dilayani bertambah tiap ±3 detik (simulasi lokal)
    LaunchedEffect(state) {
        if (state != QueueState.WAITING && state != QueueState.ALMOST) return@LaunchedEffect
        while (true) {
            delay(3000)
            val pos = ticketNo - (lane.serving + servedExtra)
            if (pos > 3) {
                servedExtra += 1
                PerformHapticFeedback(HapticKind.LIGHT)
            } else {
                // 3 posisi tersisa: tandai hampir giliran, lalu panggil setelah satu tick lagi
                if (state == QueueState.WAITING) {
                    state = QueueState.ALMOST
                    notif = "Hampir giliran Anda — siapkan dokumen"
                    PerformHapticFeedback(HapticKind.WARNING)
                } else {
                    state = QueueState.CALLED
                    notif = "Giliran Anda! Nomor $ticketNo ke ${lane.name}"
                    PerformHapticFeedback(HapticKind.SUCCESS)
                }
                break
            }
        }
    }
    // giliran terlewat bila tidak dikonfirmasi 12 detik
    LaunchedEffect(state) {
        if (state == QueueState.CALLED) {
            delay(12_000)
            if (state == QueueState.CALLED) {
                state = QueueState.MISSED
                notif = "Nomor $ticketNo terlewat — silakan ambil nomor baru"
                PerformHapticFeedback(HapticKind.WARNING)
            }
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Queue Ticket", subtitle = "Tiket antrean & posisi live — simulasi", onBack = onBack) },
    ) { inner ->
        Column(
            Modifier.fillMaxSize().padding(inner).navigationBarsPadding().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            LabIntroCard(title = "Tiket Antrean Digital", body = "Nomor antrean, posisi live, dan estimasi giliran. Antrean berjalan otomatis dari generator lokal; panggilan giliran adalah simulasi, bukan sistem loket nyata.")

            // tiket
            Surface(shape = RoundedCornerShape(18.dp), color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(lane.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                        StatusPill(
                            text = when (state) {
                                QueueState.WAITING -> "Menunggu"
                                QueueState.ALMOST -> "Hampir giliran"
                                QueueState.CALLED -> "Giliran Anda"
                                QueueState.MISSED -> "Terlewat"
                                QueueState.DONE -> "Selesai"
                                QueueState.CANCELLED -> "Dibatalkan"
                            },
                            color = when (state) {
                                QueueState.WAITING -> palette.info
                                QueueState.ALMOST -> palette.warning
                                QueueState.CALLED -> palette.success
                                QueueState.MISSED -> palette.danger
                                QueueState.DONE -> palette.success
                                QueueState.CANCELLED -> palette.textMuted
                            },
                        )
                    }
                    Text(
                        "No. $ticketNo",
                        fontSize = 44.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary,
                        modifier = Modifier.semantics { contentDescription = "Nomor antrean $ticketNo" },
                    )
                    Text("Sedang dilayani: No. $servingNow · ${lane.behind} orang di belakang Anda", fontSize = 11.sp, color = palette.textMuted, textAlign = TextAlign.Center)
                    QueueRing(
                        position = position,
                        color = when (state) { QueueState.CALLED, QueueState.DONE -> palette.success; else -> palette.primary },
                    )
                    Text(
                        when (state) {
                            QueueState.WAITING, QueueState.ALMOST -> "Posisi ke-$position · estimasi ${etaText(position)}"
                            QueueState.CALLED -> "Segera ke ${lane.name} — konfirmasi dalam 12 detik"
                            QueueState.MISSED -> "Nomor terlewat"
                            QueueState.DONE -> "Layanan selesai. Terima kasih!"
                            QueueState.CANCELLED -> "Antrean dibatalkan"
                        },
                        fontSize = 12.sp, color = palette.textSecondary, textAlign = TextAlign.Center,
                    )
                }
            }

            if (notif != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = (if (state == QueueState.MISSED) palette.danger else palette.primary).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, if (state == QueueState.MISSED) palette.danger else palette.primary),
                    shadowElevation = 0.dp, tonalElevation = 0.dp,
                    modifier = Modifier.semantics { contentDescription = "Notifikasi: ${notif!!}" },
                ) {
                    Text(notif!!, fontSize = 12.sp, color = palette.textPrimary, modifier = Modifier.fillMaxWidth().padding(12.dp))
                }
            }

            LabSectionTitle(title = "Aksi", subtitle = "Konfirmasi, pindah loket, atau batalkan antrean")
            when (state) {
                QueueState.CALLED -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) { FlatPrimaryButton(text = "Saya Sudah Sampai", onClick = { state = QueueState.DONE; notif = "Nomor $ticketNo dikonfirmasi — silakan menuju loket" }) }
                    Box(Modifier.weight(1f)) { FlatSecondaryButton(text = "Batalkan", onClick = { state = QueueState.CANCELLED; notif = "Antrean dibatalkan" }) }
                }
                QueueState.WAITING, QueueState.ALMOST -> {
                    FlatSecondaryButton(
                        text = "Pindah ke ${if (laneId == "umum") "Loket Prioritas" else "Loket Umum"}",
                        onClick = {
                            laneId = if (laneId == "umum") "prioritas" else "umum"
                            ticketNo = Lanes.first { it.id == laneId }.serving + 2
                            servedExtra = 0
                            state = QueueState.WAITING
                            notif = "Pindah antrean ke ${Lanes.first { it.id == laneId }.name} (simulasi)"
                            PerformHapticFeedback(HapticKind.MEDIUM)
                        },
                    )
                    FlatSecondaryButton(text = "Batalkan Antrean", onClick = { state = QueueState.CANCELLED; notif = "Antrean dibatalkan — nomor hangus" })
                }
                QueueState.MISSED -> FlatPrimaryButton(
                    text = "Ambil Nomor Baru",
                    onClick = {
                        laneId = "umum"; ticketNo = Lanes[0].serving + 15; servedExtra = 0; state = QueueState.WAITING
                        notif = "Nomor baru No. $ticketNo (simulasi)"
                        PerformHapticFeedback(HapticKind.MEDIUM)
                    },
                )
                QueueState.CANCELLED -> FlatPrimaryButton(
                    text = "Ambil Nomor Antrean",
                    onClick = {
                        ticketNo = Lanes[0].serving + 15; servedExtra = 0; state = QueueState.WAITING
                        notif = "Nomor baru No. $ticketNo (simulasi)"
                    },
                )
                QueueState.DONE -> FlatSecondaryButton(text = "Selesai — Tutup Ringkasan", onClick = { notif = null })
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun QueueRing(position: Int, color: Color) {
    // ring progres antrean: penuh saat jauh, mengecil saat mendekati giliran
    val palette = LocalAppPalette.current
    val progress = (1f - (position.toFloat() / 20f)).coerceIn(0.06f, 1f)
    Canvas(
        Modifier
            .size(88.dp)
            .semantics { contentDescription = "Progres antrean ${if (position <= 3) "hampir tiba" else "menunggu"}, posisi ke-$position" },
    ) {
        val stroke = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawArc(
            color = palette.border,
            startAngle = -90f, sweepAngle = 360f, useCenter = false,
            style = stroke,
        )
        drawArc(
            color = color,
            startAngle = -90f, sweepAngle = 360f * progress, useCenter = false,
            style = stroke,
        )
        // penanda titik giliran
        drawCircle(color = color, radius = 5.dp.toPx(), center = Offset(size.width / 2f, size.height / 2f + 6.dp.toPx()))
    }
}
