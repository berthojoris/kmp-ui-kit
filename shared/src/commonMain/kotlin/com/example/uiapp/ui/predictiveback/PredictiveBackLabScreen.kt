package com.example.uiapp.ui.predictiveback

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
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
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.launch

/**
 * Simulasi predictive back gesture: layar aktif mengecil & memudar sementara
 * layar "Home" menengok dari belakang. Murni Compose — tanpa API platform.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PredictiveBackLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val gestureWidth = with(density) { 220.dp.toPx() } // jarak tarik penuh 220dp

    // 0f..1f progres gesture; komit bila > 0.5 saat dilepas.
    var progress by remember { mutableStateOf(0f) }
    var committed by remember { mutableStateOf(false) }
    var notice by remember { mutableStateOf<String?>(null) }
    var sliderValue by remember { mutableStateOf(0f) }

    fun snapTo(target: Float, commit: Boolean) {
        scope.launch {
            var p = progress
            val dir = if (target > p) 1 else -1
            while ((dir > 0 && p < target) || (dir < 0 && p > target)) {
                p += dir * 0.08f
                progress = p.coerceIn(0f, 1f)
                kotlinx.coroutines.delay(16)
            }
            progress = target
            if (commit) {
                committed = true
                PerformHapticFeedback(HapticKind.SUCCESS)
                kotlinx.coroutines.delay(700)
                progress = 0f
                committed = false
            }
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Predictive Back", subtitle = "Simulasi gestur kembali (scale + fade)", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                LabIntroCard(
                    title = "Menengok layar sebelumnya",
                    body = "Tarik pegangan dari tepi kiri area demo ke kanan. Saat progres melewati 50%, lepas " +
                        "untuk \"komit kembali\" — layar mengecil dan memudar memperlihatkan Home di belakangnya. " +
                        "Lepas sebelum 50% untuk batal. Semua murni animasi Compose, bukan API sistem.",
                )

                notice?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = palette.info.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, palette.info),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) { Text(msg, fontSize = 12.sp, color = palette.textPrimary, modifier = Modifier.fillMaxWidth().padding(10.dp)) }
                }

                // Area demo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(16.dp)),
                ) {
                    // Lapisan "Home" di belakang: ikut membesar saat progres naik.
                    HomePeekLayer(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .graphicsLayer {
                                scaleX = 0.90f + 0.10f * progress
                                scaleY = 0.90f + 0.10f * progress
                                alpha = 0.35f + 0.65f * progress
                            },
                    )
                    // Lapisan "Detail" di depan: mengecil, memudar, sedikit bergeser kanan.
                    DetailLayer(
                        title = "Halaman Detail Villa",
                        body = "Ini halaman yang akan ditutup oleh gestur kembali. Skala turun ke 88% dan opasitas " +
                            "menurun mengikuti progres tarikan.",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .graphicsLayer {
                                scaleX = 1f - 0.12f * progress
                                scaleY = 1f - 0.12f * progress
                                translationX = 24f * progress
                                alpha = 1f - 0.75f * progress
                            },
                    )
                    // Pegangan tepi kiri: zona gesture drag.
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragEnd = {
                                        if (progress > 0.5f) {
                                            notice = "Komit: kembali dieksekusi (progres ${"%.0f".format(progress * 100)}%)."
                                            snapTo(1f, commit = true)
                                        } else {
                                            notice = "Batal: dilepas di bawah 50%, layar kembali penuh."
                                            PerformHapticFeedback(HapticKind.SELECTION)
                                            snapTo(0f, commit = false)
                                        }
                                    },
                                ) { change, dragAmount ->
                                    change.consume()
                                    // Hanya gerakan ke kanan yang menambah progres.
                                    progress = (progress + (dragAmount.x.coerceAtLeast(0f)) / gestureWidth).coerceIn(0f, 1f)
                                    sliderValue = progress
                                }
                            },
                    )
                    // Pegangan visual
                    Surface(
                        shape = RoundedCornerShape(topEnd = 999.dp, bottomEnd = 999.dp),
                        color = palette.primary,
                        border = BorderStroke(1.dp, palette.primary),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(end = 0.dp)
                            .semantics {
                                contentDescription =
                                    "Pegangan gestur kembali. Tarik ke kanan melewati setengah jarak lalu lepas untuk kembali."
                            },
                    ) {
                        Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 18.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("▶", color = palette.onPrimary, fontSize = 12.sp)
                                Text("Tarik", color = palette.onPrimary, fontSize = 9.sp)
                            }
                        }
                    }
                    // Indikator progres
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp),
                    ) {
                        Text(
                            text = if (committed) "Kembali!" else "Progres ${"%.0f".format(progress * 100)}% • ${if (progress > 0.5f) "Lepas untuk kembali" else "Lepas untuk batal"}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (progress > 0.5f) palette.primary else palette.textMuted,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }

                // Kontrol alternatif a11y: slider pengganti gesture.
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Mode aksesibilitas: geser slider", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        Slider(
                            value = sliderValue,
                            onValueChange = {
                                sliderValue = it
                                progress = it
                                notice = null
                            },
                            valueRange = 0f..1f,
                            modifier = Modifier.semantics { contentDescription = "Progres predictive back 0 sampai 100 persen" },
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    notice = "Komit: kembali dieksekusi dari slider."
                                    snapTo(1f, commit = true)
                                    sliderValue = 0f
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = palette.primary, contentColor = palette.onPrimary),
                                modifier = Modifier.weight(1f),
                            ) { Text("Jalankan Kembali", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            androidx.compose.material3.OutlinedButton(
                                onClick = { snapTo(0f, commit = false); sliderValue = 0f; notice = "Dibatalkan." },
                                modifier = Modifier.weight(1f),
                            ) { Text("Batal", maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

/** Mock layar Home yang "menengok" dari belakang. */
@Composable
private fun HomePeekLayer(modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(palette.background)
            .border(1.dp, palette.border, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Beranda", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Lab", "Tema", "Profil").forEach { label ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) { Text(label, fontSize = 10.sp, color = palette.textMuted, maxLines = 1) }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..4).forEach { i ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surface)
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) { Text("Lab $i", fontSize = 10.sp, color = palette.textSecondary) }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().height(90.dp).clip(RoundedCornerShape(12.dp)).background(palette.surface),
            contentAlignment = Alignment.Center,
        ) { Text("117 lab UI", fontSize = 11.sp, color = palette.textMuted) }
    }
}

/** Mock layar detail yang akan ditutup gestur. */
@Composable
private fun DetailLayer(title: String, body: String, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
        Text(body, fontSize = 11.sp, lineHeight = 16.sp, color = palette.textSecondary)
        Box(
            modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp)).background(palette.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) { Text("Ilustrasi villa (mock)", fontSize = 11.sp, color = palette.primary, fontWeight = FontWeight.SemiBold) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Harga: Rp1,2 jt/malam", fontSize = 11.sp, color = palette.textSecondary, modifier = Modifier.weight(1f), maxLines = 1)
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = palette.primary,
                border = BorderStroke(1.dp, palette.primary),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Text("Pesan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
            }
        }
    }
}
