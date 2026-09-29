package com.example.uiapp.ui.scratchcard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.hypot

private const val GRID_ROWS = 12
private const val GRID_COLS = 16

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScratchCardLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    // Set of covered cells in grid (0 until GRID_ROWS * GRID_COLS)
    val coveredCells = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(GRID_ROWS * GRID_COLS) { add(true) }
        }
    }
    var isRevealed by remember { mutableStateOf(false) }

    val scratchedCount = coveredCells.count { !it }
    val scratchProgress = (scratchedCount.toFloat() / coveredCells.size).coerceIn(0f, 1f)

    fun resetCard() {
        for (i in 0 until coveredCells.size) {
            coveredCells[i] = true
        }
        isRevealed = false
    }

    fun revealAll() {
        for (i in 0 until coveredCells.size) {
            coveredCells[i] = false
        }
        isRevealed = true
    }

    fun handleTouch(offset: Offset, cardWidth: Float, cardHeight: Float) {
        if (isRevealed || cardWidth <= 0 || cardHeight <= 0) return
        val colWidth = cardWidth / GRID_COLS
        val rowHeight = cardHeight / GRID_ROWS
        val radius = 32f

        for (r in 0 until GRID_ROWS) {
            for (c in 0 until GRID_COLS) {
                val cellCenterX = (c + 0.5f) * colWidth
                val cellCenterY = (r + 0.5f) * rowHeight
                val dist = hypot(cellCenterX - offset.x, cellCenterY - offset.y)
                if (dist <= radius) {
                    val idx = r * GRID_COLS + c
                    if (idx in 0 until coveredCells.size) {
                        coveredCells[idx] = false
                    }
                }
            }
        }

        if (coveredCells.count { !it } > (coveredCells.size * 0.65f)) {
            revealAll()
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Interactive Scratch Card",
                subtitle = "Touch Erase Gesture & Reward Reveal",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header stats
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Gosok Kupon Hadiah", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                            Text(
                                text = if (isRevealed) "🎉 Selamat! Kupon Terbuka Penuh!" else "Gosok minimal 65% area kartu",
                                fontSize = 11.sp,
                                color = if (isRevealed) palette.success else palette.textMuted,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = palette.primaryContainer,
                            border = BorderStroke(1.dp, palette.primary),
                        ) {
                            Text(
                                text = "${(scratchProgress * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            onClick = { resetCard() },
                            shape = RoundedCornerShape(10.dp),
                            color = palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Tutup Kembali 🔄", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp))
                        }

                        Surface(
                            onClick = { revealAll() },
                            shape = RoundedCornerShape(10.dp),
                            color = palette.primary,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Buka Otomatis ✨", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp))
                        }
                    }
                }
            }

            // Scratch Card Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(palette.surface)
                    .border(BorderStroke(1.5.dp, if (isRevealed) palette.success else palette.border), RoundedCornerShape(20.dp)),
            ) {
                // Secret Reward Content underneath
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(palette.success.copy(alpha = 0.15f))
                            .border(1.dp, palette.success, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("🎁", fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Cashback Saldo Rp 75.000", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = palette.textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Kode Promo: KMP-PRO-2026", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Berlaku untuk seluruh transaksi multiplatform", fontSize = 11.sp, color = palette.textMuted)
                }

                // Scratch Overlay Grid
                if (!isRevealed) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        handleTouch(offset, size.width.toFloat(), size.height.toFloat())
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        handleTouch(change.position, size.width.toFloat(), size.height.toFloat())
                                    },
                                )
                            },
                    ) {
                        val colW = size.width / GRID_COLS
                        val rowH = size.height / GRID_ROWS

                        for (r in 0 until GRID_ROWS) {
                            for (c in 0 until GRID_COLS) {
                                val idx = r * GRID_COLS + c
                                if (idx < coveredCells.size && coveredCells[idx]) {
                                    drawRect(
                                        color = Color(0xFF6B7280),
                                        topLeft = Offset(c * colW, r * rowH),
                                        size = Size(colW + 0.5f, rowH + 0.5f),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Specs
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Karakteristik Scratch Card UI:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Cross-Platform Grid Erase: Kompatibel 100% Android & iOS tanpa issue blend mode GPU", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Auto-Reveal Threshold: Otomatis membuka penuh bila di atas 65% area tergosok", fontSize = 12.sp, color = palette.textSecondary)
                    Text("• Flat Zero-Shadow UI: Border 1px presisi dan background adaptif tema terang/gelap", fontSize = 12.sp, color = palette.textSecondary)
                }
            }
        }
    }
}
