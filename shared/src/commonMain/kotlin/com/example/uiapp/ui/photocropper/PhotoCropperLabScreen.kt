package com.example.uiapp.ui.photocropper

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.roundToInt

private data class AspectOption(val label: String, val ratio: Float?)

private val AspectOptions = listOf(
    AspectOption("Bebas", null),
    AspectOption("1:1", 1f),
    AspectOption("4:5", 4f / 5f),
    AspectOption("16:9", 16f / 9f),
    AspectOption("9:16", 9f / 16f),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PhotoCropperLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var rotation by remember { mutableFloatStateOf(0f) }
    var selectedAspect by remember { mutableStateOf("Bebas") }
    var applied by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Pemotong Foto", subtitle = "Simulasi editor foto", onBack = onBack) },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()),
        ) {
            // Photo area with crop overlay
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                // Mock photo (gradient)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationZ = rotation }
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.linearGradient(listOf(palette.primary.copy(alpha = 0.4f), palette.info.copy(alpha = 0.3f), palette.success.copy(alpha = 0.2f)))),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Foto Simulasi", fontSize = 16.sp, color = palette.onPrimary.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold)
                }

                // Rule of thirds overlay
                Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    val w = size.width; val h = size.height
                    val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
                    // Vertical thirds
                    drawLine(palette.onPrimary.copy(alpha = 0.5f), Offset(w / 3, 0f), Offset(w / 3, h), strokeWidth = 1.dp.toPx(), pathEffect = dash)
                    drawLine(palette.onPrimary.copy(alpha = 0.5f), Offset(2 * w / 3, 0f), Offset(2 * w / 3, h), strokeWidth = 1.dp.toPx(), pathEffect = dash)
                    // Horizontal thirds
                    drawLine(palette.onPrimary.copy(alpha = 0.5f), Offset(0f, h / 3), Offset(w, h / 3), strokeWidth = 1.dp.toPx(), pathEffect = dash)
                    drawLine(palette.onPrimary.copy(alpha = 0.5f), Offset(0f, 2 * h / 3), Offset(w, 2 * h / 3), strokeWidth = 1.dp.toPx(), pathEffect = dash)
                    // Border frame
                    drawRect(palette.primary, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()))
                    // Corner handles
                    val cSize = 16.dp.toPx()
                    val cStroke = 3.dp.toPx()
                    listOf(Offset(0f, 0f), Offset(w, 0f), Offset(0f, h), Offset(w, h)).forEach { corner ->
                        val dx = if (corner.x == 0f) 1f else -1f
                        val dy = if (corner.y == 0f) 1f else -1f
                        drawLine(palette.primary, corner, Offset(corner.x + cSize * dx, corner.y), strokeWidth = cStroke, cap = StrokeCap.Round)
                        drawLine(palette.primary, corner, Offset(corner.x, corner.y + cSize * dy), strokeWidth = cStroke, cap = StrokeCap.Round)
                    }
                }
            }

            // Rotation value
            Text(
                text = "Rotasi: ${rotation.roundToInt()}°",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))

            // Rotation dial
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp, tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                rotation = (rotation + dragAmount / 8f).coerceIn(-45f, 45f)
                                applied = false
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(vertical = 8.dp)) {
                        val w = size.width; val h = size.height
                        val centerX = w / 2
                        // Ticks
                        for (deg in -45..45) {
                            val x = centerX + (deg.toFloat() / 45f) * (w / 2 - 16.dp.toPx())
                            val isMajor = deg % 15 == 0
                            val tickH = if (isMajor) h * 0.6f else if (deg % 5 == 0) h * 0.35f else h * 0.2f
                            val tickColor = if (deg == 0) palette.primary else palette.textMuted
                            drawLine(tickColor, Offset(x, (h - tickH) / 2), Offset(x, (h + tickH) / 2), strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx(), cap = StrokeCap.Round)
                        }
                        // Center indicator
                        drawLine(palette.danger, Offset(centerX, 0f), Offset(centerX, h), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Aspect ratio chips
            Text("Rasio Aspek", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AspectOptions.forEach { opt ->
                    Surface(
                        onClick = { selectedAspect = opt.label; applied = false },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedAspect == opt.label) palette.primary else palette.surface,
                        border = BorderStroke(1.dp, if (selectedAspect == opt.label) palette.primary else palette.border),
                        shadowElevation = 0.dp, tonalElevation = 0.dp,
                    ) {
                        Text(opt.label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (selectedAspect == opt.label) palette.onPrimary else palette.textSecondary)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (applied) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = palette.success.copy(alpha = 0.1f),
                    shadowElevation = 0.dp, tonalElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                ) {
                    Text("Potongan berhasil diterapkan (simulasi)", modifier = Modifier.padding(12.dp), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.success)
                }
                Spacer(Modifier.height(8.dp))
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Surface(
                    onClick = { rotation = 0f; selectedAspect = "Bebas"; applied = false },
                    shape = RoundedCornerShape(12.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp, tonalElevation = 0.dp,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Reset", modifier = Modifier.padding(vertical = 14.dp), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary, textAlign = TextAlign.Center)
                }
                Surface(
                    onClick = { applied = true },
                    shape = RoundedCornerShape(12.dp),
                    color = palette.primary,
                    shadowElevation = 0.dp, tonalElevation = 0.dp,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Terapkan", modifier = Modifier.padding(vertical = 14.dp), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary, textAlign = TextAlign.Center)
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
