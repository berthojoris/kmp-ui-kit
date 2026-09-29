package com.example.uiapp.ui.confetti

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.launch
import kotlin.random.Random

private data class Particle(
    val initialX: Float,
    val initialY: Float,
    val vx: Float,
    val vy: Float,
    val size: Float,
    val color: Color,
    val isCircle: Boolean,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ConfettiParticlesLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()
    val progress = remember { Animatable(0f) }
    val particles = remember { mutableStateListOf<Particle>() }
    var triggerCount by remember { mutableIntStateOf(0) }

    fun triggerConfetti() {
        particles.clear()
        val colors = listOf(
            palette.primary,
            palette.success,
            palette.warning,
            palette.info,
            palette.danger,
            Color(0xFF8B5CF6),
            Color(0xFFEC4899),
        )
        for (i in 0..120) {
            val angle = Random.nextDouble(0.0, kotlin.math.PI * 2)
            val speed = Random.nextDouble(100.0, 750.0).toFloat()
            particles.add(
                Particle(
                    initialX = 0.5f,
                    initialY = 0.45f,
                    vx = (kotlin.math.cos(angle) * speed).toFloat(),
                    vy = (kotlin.math.sin(angle) * speed - 200f).toFloat(),
                    size = Random.nextDouble(6.0, 16.0).toFloat(),
                    color = colors.random(),
                    isCircle = Random.nextBoolean(),
                ),
            )
        }
        triggerCount++
        scope.launch {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(2400, easing = LinearEasing))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = palette.background,
            topBar = {
                UiTopBar(
                    title = "Confetti & Partikel",
                    subtitle = "Celebration Burst, Canvas Physics",
                    onBack = onBack,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Celebration Confetti Burst", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = palette.textPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Ledakan partikel deklaratif pada Canvas tanpa library eksternal", fontSize = 12.sp, color = palette.textMuted)
                        Spacer(modifier = Modifier.height(20.dp))

                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .background(palette.primaryContainer, RoundedCornerShape(24.dp))
                                .border(BorderStroke(1.dp, palette.primary), RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("🎉", fontSize = 48.sp)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = if (triggerCount == 0) "Ketuk tombol untuk merayakan!" else "Partikel diluncurkan ($triggerCount kali)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.primary,
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                onClick = { triggerConfetti() },
                                shape = RoundedCornerShape(12.dp),
                                color = palette.primary,
                            ) {
                                Text(
                                    text = "Picu Confetti! ✨",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                )
                            }
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Contoh Penggunaan Nyata:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• Duolingo: Menyelesaikan target belajar harian", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Revolut / Cash App: Pembayaran / transfer berhasil", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• E-Commerce: Order belanja checkout selesai", fontSize = 12.sp, color = palette.textSecondary)
                    }
                }
            }
        }

        // Confetti Canvas Overlay
        if (progress.value > 0f && progress.value < 1f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val t = progress.value
                val alpha = (1f - t).coerceIn(0f, 1f)
                val gravity = 900f

                particles.forEach { p ->
                    val x = (p.initialX * w) + (p.vx * t)
                    val y = (p.initialY * h) + (p.vy * t) + (0.5f * gravity * t * t)

                    if (x in 0f..w && y in 0f..h) {
                        if (p.isCircle) {
                            drawCircle(
                                color = p.color.copy(alpha = alpha),
                                radius = p.size / 2,
                                center = Offset(x, y),
                            )
                        } else {
                            drawRect(
                                color = p.color.copy(alpha = alpha),
                                topLeft = Offset(x, y),
                                size = Size(p.size, p.size * 0.6f),
                            )
                        }
                    }
                }
            }
        }
    }
}
