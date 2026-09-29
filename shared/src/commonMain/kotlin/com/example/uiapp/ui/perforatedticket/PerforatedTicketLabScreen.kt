package com.example.uiapp.ui.perforatedticket

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

@Composable
fun PerforatedTicketLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current
    var isTornOff by remember { mutableStateOf(false) }
    var showBreakdown by remember { mutableStateOf(false) }
    var copyNotice by remember { mutableStateOf<String?>(null) }

    val tearRotation by animateFloatAsState(
        targetValue = if (isTornOff) 8f else 0f,
        animationSpec = tween(400),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.surface)
            .navigationBarsPadding(),
    ) {
        UiTopBar(
            title = "Perforated Ticket",
            subtitle = "Boarding pass, cutouts & tear perforation",
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Deskripsi Konsep
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .padding(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "🎟️ Trend FinTech & Travel (Revolut / Apple Wallet)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "Komponen tiket digital dengan lekukan lingkaran (circular notches) di kedua tepi dan garis perforasi putus-putus (dashed perforation). Memberikan estetika fisik yang tak lekang waktu.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = palette.textSecondary,
                    )
                }
            }

            // Copy Feedback Banner
            AnimatedVisibility(
                visible = copyNotice != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                copyNotice?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                    ) {
                        Text(
                            text = msg,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF10B981),
                        )
                    }
                }
            }

            // KARTU TIKET BOARDING PASS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .rotate(tearRotation),
            ) {
                // BAGIAN ATAS: Informasi Penerbangan
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .padding(20.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "GARUDA AIRWAYS ✈️",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(palette.surface)
                                    .border(1.dp, palette.border, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                            ) {
                                Text("KELAS BISNIS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.textSecondary)
                            }
                        }

                        // Airport codes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text("CGK", fontSize = 28.sp, fontWeight = FontWeight.Black, color = palette.textPrimary)
                                Text("Jakarta (Soekarno-Hatta)", fontSize = 11.sp, color = palette.textSecondary)
                            }
                            Text("➔", fontSize = 20.sp, color = palette.primary)
                            Column(horizontalAlignment = Alignment.End) {
                                Text("HND", fontSize = 28.sp, fontWeight = FontWeight.Black, color = palette.textPrimary)
                                Text("Tokyo (Haneda Intl)", fontSize = 11.sp, color = palette.textSecondary)
                            }
                        }

                        // Detail Kursi & Gate
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text("PENUMPANG", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                                Text("BERTHO JORIS", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                            }
                            Column {
                                Text("GATE", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                                Text("B-14", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("KURSI", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
                                Text("02A (Jendela)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                            }
                        }
                    }
                }

                // GARIS PERFORASI DENGAN DUAL NOTCH KANVAS
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .background(palette.surfaceMuted),
                    contentAlignment = Alignment.Center,
                ) {
                    val borderColor = palette.border
                    val surfaceColor = palette.surface

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val notchRadius = 15.dp.toPx()
                        val halfHeight = size.height / 2

                        // Lingkaran Lekukan Kiri
                        drawCircle(
                            color = surfaceColor,
                            radius = notchRadius,
                            center = Offset(0f, halfHeight),
                        )
                        // Border Lekukan Kiri
                        drawCircle(
                            color = borderColor,
                            radius = notchRadius,
                            center = Offset(0f, halfHeight),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()),
                        )

                        // Lingkaran Lekukan Kanan
                        drawCircle(
                            color = surfaceColor,
                            radius = notchRadius,
                            center = Offset(size.width, halfHeight),
                        )
                        // Border Lekukan Kanan
                        drawCircle(
                            color = borderColor,
                            radius = notchRadius,
                            center = Offset(size.width, halfHeight),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()),
                        )

                        // Garis Putus-putus Perforasi di Tengah
                        drawLine(
                            color = borderColor,
                            start = Offset(notchRadius + 8.dp.toPx(), halfHeight),
                            end = Offset(size.width - notchRadius - 8.dp.toPx(), halfHeight),
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f),
                        )
                    }
                }

                // BAGIAN BAWAH: Barcode & Stub Tiket
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        .padding(20.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        // Canvas Barcode Similator
                        val barColor = palette.textPrimary
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                        ) {
                            val barWidths = listOf(
                                2f, 4f, 1f, 6f, 3f, 2f, 8f, 2f, 4f, 3f, 6f, 1f, 3f, 7f, 2f, 4f,
                                5f, 2f, 1f, 6f, 4f, 2f, 7f, 3f, 1f, 5f, 4f, 2f, 6f, 3f, 2f, 4f,
                                3f, 5f, 2f, 7f, 1f, 4f, 3f, 6f, 2f, 5f, 1f, 3f, 6f, 2f, 4f, 3f,
                            )
                            var currentX = 10f
                            val step = (size.width - 20f) / barWidths.size
                            barWidths.forEach { weight ->
                                drawLine(
                                    color = barColor,
                                    start = Offset(currentX, 0f),
                                    end = Offset(currentX, size.height),
                                    strokeWidth = weight * 1.3f,
                                )
                                currentX += step
                            }
                        }

                        Text(
                            text = "GA-99214-774-883-CGK-HND",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = palette.textSecondary,
                        )

                        // Breakdown Expandable Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(Unit) {
                                    detectTapGestures { showBreakdown = !showBreakdown }
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = if (showBreakdown) "Sembunyikan Rincian Biaya ▲" else "Lihat Rincian Biaya ▼",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.primary,
                            )
                            Text(
                                text = "Rp 14.850.000",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                            )
                        }

                        AnimatedVisibility(visible = showBreakdown) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(palette.surface, RoundedCornerShape(8.dp))
                                    .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Tarif Dasar Tiket", fontSize = 11.sp, color = palette.textSecondary)
                                    Text("Rp 13.200.000", fontSize = 11.sp, color = palette.textPrimary)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Fuel Surcharge & Airport Tax", fontSize = 11.sp, color = palette.textSecondary)
                                    Text("Rp 1.450.000", fontSize = 11.sp, color = palette.textPrimary)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Asuransi Perjalanan", fontSize = 11.sp, color = palette.textSecondary)
                                    Text("Rp 200.000", fontSize = 11.sp, color = palette.textPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // Tombol Interaksi: Robek Tiket & Salin Barcode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isTornOff) palette.primary else palette.surfaceMuted)
                        .border(1.dp, if (isTornOff) palette.primary else palette.border, RoundedCornerShape(10.dp))
                        .pointerInput(isTornOff) {
                            detectTapGestures {
                                isTornOff = !isTornOff
                                copyNotice = if (isTornOff) "Tiket telah dirobek dan divalidasi oleh petugas!" else "Tiket disatukan kembali."
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (isTornOff) "Satukan Tiket ↺" else "Robek Tiket ✂️",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isTornOff) Color.White else palette.textPrimary,
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                        .pointerInput(Unit) {
                            detectTapGestures {
                                copyNotice = "Nomor tiket GA-99214-774 disalin ke clipboard!"
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Salin Kode 📋",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
