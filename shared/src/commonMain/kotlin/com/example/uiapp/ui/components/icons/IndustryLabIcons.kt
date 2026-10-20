package com.example.uiapp.ui.components.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.uiapp.theme.LightAppPalette

// Ikon pemilih kursi: sandaran bundar, kaki, dan alas kursi.
@Composable
fun SeatSelectorMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.24f, h * 0.10f),
            size = Size(w * 0.52f, h * 0.42f),
            cornerRadius = CornerRadius(w * 0.14f, w * 0.14f),
            style = stroke,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.30f, h * 0.52f),
            end = Offset(w * 0.30f, h * 0.76f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.70f, h * 0.52f),
            end = Offset(w * 0.70f, h * 0.76f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.18f, h * 0.80f),
            end = Offset(w * 0.82f, h * 0.80f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

// Ikon pelacakan perjalanan: pin lokasi dengan titik tengah.
@Composable
fun TripTrackerMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        drawCircle(
            color = tint,
            radius = w * 0.24f,
            center = Offset(w * 0.5f, h * 0.38f),
            style = stroke,
        )
        drawCircle(
            color = tint,
            radius = w * 0.06f,
            center = Offset(w * 0.5f, h * 0.38f),
        )
        val tail = Path().apply {
            moveTo(w * 0.35f, h * 0.55f)
            lineTo(w * 0.5f, h * 0.86f)
            lineTo(w * 0.65f, h * 0.55f)
        }
        drawPath(tail, tint, style = stroke)
    }
}

// Ikon itinerary penerbangan: pesawat kertas.
@Composable
fun FlightItineraryMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        val plane = Path().apply {
            moveTo(w * 0.10f, h * 0.52f)
            lineTo(w * 0.90f, h * 0.14f)
            lineTo(w * 0.62f, h * 0.88f)
            lineTo(w * 0.50f, h * 0.58f)
            close()
        }
        drawPath(plane, tint, style = stroke)
        drawLine(
            color = tint,
            start = Offset(w * 0.50f, h * 0.58f),
            end = Offset(w * 0.90f, h * 0.14f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

// Ikon lirik tersinkron: not musik dengan garis lirik.
@Composable
fun SyncedLyricsMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawCircle(
            color = tint,
            radius = w * 0.12f,
            center = Offset(w * 0.32f, h * 0.76f),
            style = Stroke(width = 1.6.dp.toPx()),
        )
        drawCircle(
            color = tint,
            radius = w * 0.12f,
            center = Offset(w * 0.74f, h * 0.68f),
            style = Stroke(width = 1.6.dp.toPx()),
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.44f, h * 0.76f),
            end = Offset(w * 0.44f, h * 0.24f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.86f, h * 0.68f),
            end = Offset(w * 0.86f, h * 0.18f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.44f, h * 0.24f),
            end = Offset(w * 0.86f, h * 0.18f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

// Ikon equalizer audio: palang batang vertikal berbeda tinggi.
@Composable
fun AudioEqualizerMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val xs = floatArrayOf(0.18f, 0.34f, 0.50f, 0.66f, 0.82f)
        val tops = floatArrayOf(0.44f, 0.22f, 0.36f, 0.14f, 0.30f)
        val bottoms = floatArrayOf(0.64f, 0.78f, 0.58f, 0.84f, 0.70f)
        for (i in xs.indices) {
            drawLine(
                color = tint,
                start = Offset(w * xs[i], h * tops[i]),
                end = Offset(w * xs[i], h * bottoms[i]),
                strokeWidth = 1.8.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

// Ikon pemotong foto: dua sudut crop berlawanan.
@Composable
fun PhotoCropperMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        val topRight = Path().apply {
            moveTo(w * 0.30f, h * 0.08f)
            lineTo(w * 0.30f, h * 0.70f)
            lineTo(w * 0.92f, h * 0.70f)
        }
        val bottomLeft = Path().apply {
            moveTo(w * 0.08f, h * 0.30f)
            lineTo(w * 0.70f, h * 0.30f)
            lineTo(w * 0.70f, h * 0.92f)
        }
        drawPath(topRight, tint, style = stroke)
        drawPath(bottomLeft, tint, style = stroke)
    }
}

// Ikon editor blok: tumpukan blok dengan lebar bervariasi.
@Composable
fun BlockEditorMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.16f, h * 0.16f),
            size = Size(w * 0.68f, h * 0.16f),
            cornerRadius = CornerRadius(w * 0.05f, w * 0.05f),
            style = stroke,
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.16f, h * 0.42f),
            size = Size(w * 0.46f, h * 0.16f),
            cornerRadius = CornerRadius(w * 0.05f, w * 0.05f),
            style = stroke,
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.16f, h * 0.68f),
            size = Size(w * 0.60f, h * 0.16f),
            cornerRadius = CornerRadius(w * 0.05f, w * 0.05f),
            style = stroke,
        )
    }
}

// Ikon roadmap Gantt: tiga batang jadwal pada sumbu waktu.
@Composable
fun GanttRoadmapMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        drawLine(
            color = tint,
            start = Offset(w * 0.14f, h * 0.14f),
            end = Offset(w * 0.14f, h * 0.88f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        val bars = listOf(
            Triple(0.22f, 0.26f, 0.58f),
            Triple(0.44f, 0.48f, 0.82f),
            Triple(0.60f, 0.70f, 0.90f),
        )
        for ((startX, y, endX) in bars) {
            drawRoundRect(
                color = tint,
                topLeft = Offset(w * startX, h * y - h * 0.06f),
                size = Size(w * (endX - startX), h * 0.12f),
                cornerRadius = CornerRadius(w * 0.06f, w * 0.06f),
                style = stroke,
            )
        }
    }
}

// Ikon pohon berkas: folder dengan cabang anak.
@Composable
fun TreeExplorerMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        val folder = Path().apply {
            moveTo(w * 0.12f, h * 0.28f)
            lineTo(w * 0.12f, h * 0.72f)
            lineTo(w * 0.58f, h * 0.72f)
            lineTo(w * 0.58f, h * 0.36f)
            lineTo(w * 0.42f, h * 0.36f)
            lineTo(w * 0.34f, h * 0.28f)
            close()
        }
        drawPath(folder, tint, style = stroke)
        drawLine(
            color = tint,
            start = Offset(w * 0.58f, h * 0.54f),
            end = Offset(w * 0.84f, h * 0.54f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawCircle(color = tint, radius = w * 0.07f, center = Offset(w * 0.88f, h * 0.54f))
    }
}

// Ikon bagi tagihan: lingkaran terbagi dua dengan titik tiap sisi.
@Composable
fun SplitBillMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawCircle(
            color = tint,
            radius = w * 0.36f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = Stroke(width = 1.6.dp.toPx()),
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.5f, h * 0.14f),
            end = Offset(w * 0.5f, h * 0.86f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawCircle(color = tint, radius = w * 0.06f, center = Offset(w * 0.36f, h * 0.5f))
        drawCircle(color = tint, radius = w * 0.06f, center = Offset(w * 0.64f, h * 0.5f))
    }
}

// Ikon floating dock: kapsul dock dengan satu tombol menonjol ( minimized ).
@Composable
fun FloatingDockMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Dock pill lebar di bawah
        drawRoundRect(
            color = tint.copy(alpha = 0.55f),
            topLeft = Offset(w * 0.06f, h * 0.62f),
            size = Size(w * 0.88f, h * 0.3f),
            cornerRadius = CornerRadius(h * 0.15f),
            style = stroke,
        )
        drawCircle(color = tint.copy(alpha = 0.55f), radius = w * 0.05f, center = Offset(w * 0.22f, h * 0.77f))
        drawCircle(color = tint.copy(alpha = 0.55f), radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.77f))
        // Tombol aktif terlepas ke atas ( menandakan minimize ke satu tombol )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.62f, h * 0.06f),
            size = Size(w * 0.32f, h * 0.3f),
            cornerRadius = CornerRadius(h * 0.15f),
            style = stroke,
        )
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.78f, h * 0.21f))
        drawLine(
            color = tint,
            start = Offset(w * 0.2f, h * 0.44f),
            end = Offset(w * 0.8f, h * 0.44f),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun BudgetEnvelopesMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.32f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.2f,h*0.5f), end = Offset(w*0.8f,h*0.5f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun QuestPathMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.32f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.2f,h*0.5f), end = Offset(w*0.8f,h*0.5f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun SentenceBuilderMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.32f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.2f,h*0.5f), end = Offset(w*0.8f,h*0.5f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun PredictiveBackMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w*0.32f, center = Offset(w*0.5f,h*0.5f), style = stroke)
        drawLine(color = tint, start = Offset(w*0.2f,h*0.5f), end = Offset(w*0.8f,h*0.5f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}
