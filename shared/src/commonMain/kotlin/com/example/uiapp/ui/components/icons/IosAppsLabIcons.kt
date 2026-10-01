package com.example.uiapp.ui.components.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.uiapp.theme.LightAppPalette

// Ikon Efek Pesan: dua gelembung chat tumpuk dengan tiga titik confetti kecil.
@Composable
fun MsgEffectsMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Gelembung belakang dengan ekor kecil.
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.04f, h * 0.08f),
            size = Size(w * 0.58f, h * 0.42f),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
            style = stroke,
        )
        val tail = Path().apply {
            moveTo(w * 0.14f, h * 0.44f)
            lineTo(w * 0.14f, h * 0.64f)
            lineTo(w * 0.3f, h * 0.44f)
            close()
        }
        drawPath(tail, tint.copy(alpha = 0.45f))

        // Gelembung depan.
        drawRoundRect(
            color = tint.copy(alpha = 0.45f),
            topLeft = Offset(w * 0.36f, h * 0.5f),
            size = Size(w * 0.6f, h * 0.4f),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
        )

        // Tiga titik confetti kecil.
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.84f, h * 0.12f))
        drawCircle(color = tint.copy(alpha = 0.6f), radius = w * 0.04f, center = Offset(w * 0.94f, h * 0.3f))
        drawCircle(color = tint.copy(alpha = 0.75f), radius = w * 0.035f, center = Offset(w * 0.7f, h * 0.05f))
    }
}

// Ikon Antarmuka Telepon: handset telepon miring di dalam lingkaran.
@Composable
fun CallUiMenuIcon(
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
            radius = w * 0.42f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = stroke,
        )

        // Badan handset diagonal dengan dua ujung pendengar/mulut.
        val barWidth = 2.4.dp.toPx()
        drawLine(
            color = tint,
            start = Offset(w * 0.34f, h * 0.34f),
            end = Offset(w * 0.66f, h * 0.66f),
            strokeWidth = barWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.26f, h * 0.42f),
            end = Offset(w * 0.42f, h * 0.26f),
            strokeWidth = barWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.58f, h * 0.74f),
            end = Offset(w * 0.74f, h * 0.58f),
            strokeWidth = barWidth,
            cap = StrokeCap.Round,
        )
    }
}

// Ikon Aplikasi Cuaca: matahari (lingkaran + sinar) setengah tertutup awan gumpalan bulat.
@Composable
fun WeatherAppMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Matahari dan sinarnya.
        drawCircle(
            color = tint.copy(alpha = 0.85f),
            radius = w * 0.13f,
            center = Offset(w * 0.34f, h * 0.3f),
            style = stroke,
        )
        drawLine(
            color = tint.copy(alpha = 0.85f),
            start = Offset(w * 0.34f, h * 0.1f),
            end = Offset(w * 0.34f, h * 0.02f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint.copy(alpha = 0.85f),
            start = Offset(w * 0.2f, h * 0.16f),
            end = Offset(w * 0.14f, h * 0.1f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint.copy(alpha = 0.85f),
            start = Offset(w * 0.14f, h * 0.3f),
            end = Offset(w * 0.05f, h * 0.3f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint.copy(alpha = 0.85f),
            start = Offset(w * 0.48f, h * 0.16f),
            end = Offset(w * 0.54f, h * 0.1f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round,
        )

        // Awan dari gumpalan bulat yang menutupi sebagian matahari.
        val cloudColor = tint.copy(alpha = 0.85f)
        drawCircle(color = cloudColor, radius = w * 0.16f, center = Offset(w * 0.5f, h * 0.62f))
        drawCircle(color = cloudColor, radius = w * 0.2f, center = Offset(w * 0.7f, h * 0.56f))
        drawCircle(color = cloudColor, radius = w * 0.13f, center = Offset(w * 0.87f, h * 0.68f))
        drawRoundRect(
            color = cloudColor,
            topLeft = Offset(w * 0.34f, h * 0.66f),
            size = Size(w * 0.62f, h * 0.22f),
            cornerRadius = CornerRadius(h * 0.11f, h * 0.11f),
        )
    }
}

// Ikon Aplikasi Saham: garis tren naik dengan 3 titik data dan batang candlestick mini.
@Composable
fun StocksAppMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Garis tren naik.
        val trend = Path().apply {
            moveTo(w * 0.05f, h * 0.7f)
            lineTo(w * 0.3f, h * 0.48f)
            lineTo(w * 0.5f, h * 0.58f)
            lineTo(w * 0.78f, h * 0.22f)
        }
        drawPath(trend, tint, style = stroke)

        // Tiga titik data.
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.3f, h * 0.48f))
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.58f))
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.78f, h * 0.22f))

        // Dua batang candlestick mini.
        drawLine(
            color = tint.copy(alpha = 0.6f),
            start = Offset(w * 0.66f, h * 0.68f),
            end = Offset(w * 0.66f, h * 0.94f),
            strokeWidth = 1.3.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.6f),
            topLeft = Offset(w * 0.62f, h * 0.76f),
            size = Size(w * 0.08f, h * 0.1f),
            cornerRadius = CornerRadius(w * 0.02f, w * 0.02f),
        )
        drawLine(
            color = tint.copy(alpha = 0.4f),
            start = Offset(w * 0.86f, h * 0.62f),
            end = Offset(w * 0.86f, h * 0.94f),
            strokeWidth = 1.3.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.4f),
            topLeft = Offset(w * 0.82f, h * 0.7f),
            size = Size(w * 0.08f, h * 0.12f),
            cornerRadius = CornerRadius(w * 0.02f, w * 0.02f),
        )
    }
}

// Ikon Aplikasi Kesehatan: bentuk hati (dua lengkung + V) dengan garis denyut jantung melintas.
@Composable
fun HealthAppMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Bentuk hati: dua arc atas menyatu ke ujung bawah.
        val heart = Path().apply {
            moveTo(w * 0.5f, h * 0.86f)
            cubicTo(w * 0.08f, h * 0.58f, w * 0.16f, h * 0.14f, w * 0.5f, h * 0.36f)
            cubicTo(w * 0.84f, h * 0.14f, w * 0.92f, h * 0.58f, w * 0.5f, h * 0.86f)
            close()
        }
        drawPath(heart, tint, style = stroke)

        // Garis denyut jantung melintas.
        val pulse = Path().apply {
            moveTo(w * 0.16f, h * 0.56f)
            lineTo(w * 0.34f, h * 0.56f)
            lineTo(w * 0.42f, h * 0.42f)
            lineTo(w * 0.52f, h * 0.68f)
            lineTo(w * 0.6f, h * 0.56f)
            lineTo(w * 0.84f, h * 0.56f)
        }
        drawPath(pulse, tint.copy(alpha = 0.8f), style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

// Ikon Stopwatch: lingkaran jam dengan jarum dan tombol atas kecil.
@Composable
fun ClockKitMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Tombol atas kecil dan tangkainya.
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.44f, h * 0.04f),
            size = Size(w * 0.12f, h * 0.12f),
            cornerRadius = CornerRadius(w * 0.03f, w * 0.03f),
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.5f, h * 0.14f),
            end = Offset(w * 0.5f, h * 0.24f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )

        // Lingkaran stopwatch dengan jarum.
        val center = Offset(w * 0.5f, h * 0.58f)
        drawCircle(
            color = tint,
            radius = w * 0.34f,
            center = center,
            style = stroke,
        )
        drawLine(
            color = tint,
            start = center,
            end = Offset(w * 0.5f, h * 0.34f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = center,
            end = Offset(w * 0.68f, h * 0.58f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

// Ikon Pintasan: dua kotak aksi rounded dihubungkan panah melengkung.
@Composable
fun ShortcutsLabMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.04f, h * 0.08f),
            size = Size(w * 0.3f, h * 0.3f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
            style = stroke,
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.75f),
            topLeft = Offset(w * 0.62f, h * 0.58f),
            size = Size(w * 0.3f, h * 0.3f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
            style = stroke,
        )

        // Panah melengkung menghubungkan kedua kotak.
        val arrow = Path().apply {
            moveTo(w * 0.36f, h * 0.3f)
            cubicTo(w * 0.58f, h * 0.26f, w * 0.74f, h * 0.34f, w * 0.68f, h * 0.54f)
        }
        drawPath(arrow, tint, style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Kepala panah kecil di ujung.
        val head = Path().apply {
            moveTo(w * 0.62f, h * 0.46f)
            lineTo(w * 0.68f, h * 0.58f)
            lineTo(w * 0.76f, h * 0.48f)
        }
        drawPath(head, tint, style = stroke)
    }
}

// Ikon Rumah: atap segitiga, badan rumah rounded, dan titik aksesori kecil.
@Composable
fun HomeAppMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Atap segitiga.
        val roof = Path().apply {
            moveTo(w * 0.5f, h * 0.08f)
            lineTo(w * 0.92f, h * 0.46f)
            lineTo(w * 0.08f, h * 0.46f)
            close()
        }
        drawPath(roof, tint, style = stroke)

        // Badan rumah.
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.2f, h * 0.46f),
            size = Size(w * 0.6f, h * 0.44f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
            style = stroke,
        )

        // Titik aksesori kecil.
        drawCircle(color = tint, radius = w * 0.06f, center = Offset(w * 0.5f, h * 0.68f))
    }
}

// Ikon Dompet: dua kartu rounded bertumpuk dengan garis magnetik pada kartu depan.
@Composable
fun WalletStackMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Kartu belakang (offset).
        drawRoundRect(
            color = tint.copy(alpha = 0.55f),
            topLeft = Offset(w * 0.06f, h * 0.12f),
            size = Size(w * 0.68f, h * 0.42f),
            cornerRadius = CornerRadius(w * 0.1f, w * 0.1f),
            style = stroke,
        )

        // Kartu depan.
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.22f, h * 0.44f),
            size = Size(w * 0.72f, h * 0.44f),
            cornerRadius = CornerRadius(w * 0.1f, w * 0.1f),
            style = stroke,
        )

        // Garis magnetik.
        drawRoundRect(
            color = tint.copy(alpha = 0.85f),
            topLeft = Offset(w * 0.22f, h * 0.56f),
            size = Size(w * 0.72f, h * 0.1f),
            cornerRadius = CornerRadius(h * 0.02f, h * 0.02f),
        )
    }
}

// Ikon App Store Today: lingkaran berisi huruf "A" stylised dari dua kaki melengkung dan palang.
@Composable
fun AppstoreTodayMenuIcon(
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
            radius = w * 0.42f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = stroke,
        )

        // Kaki kiri melengkung, kaki kanan melengkung, dan palang membentuk "A".
        val leftLeg = Path().apply {
            moveTo(w * 0.32f, h * 0.72f)
            cubicTo(w * 0.38f, h * 0.54f, w * 0.44f, h * 0.44f, w * 0.5f, h * 0.28f)
        }
        drawPath(leftLeg, tint, style = stroke)
        val rightLeg = Path().apply {
            moveTo(w * 0.5f, h * 0.28f)
            cubicTo(w * 0.56f, h * 0.44f, w * 0.62f, h * 0.54f, w * 0.68f, h * 0.72f)
        }
        drawPath(rightLeg, tint, style = stroke)
        drawLine(
            color = tint,
            start = Offset(w * 0.4f, h * 0.56f),
            end = Offset(w * 0.6f, h * 0.56f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

// Ikon Terjemahan: dua gelembung berbeda ukuran, satu berisi huruf "A", satu berisi titik dan garis.
@Composable
fun TranslateAppMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Gelembung besar dengan ekor.
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.04f, h * 0.08f),
            size = Size(w * 0.56f, h * 0.5f),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
            style = stroke,
        )
        val tail = Path().apply {
            moveTo(w * 0.14f, h * 0.52f)
            lineTo(w * 0.14f, h * 0.7f)
            lineTo(w * 0.3f, h * 0.52f)
            close()
        }
        drawPath(tail, tint.copy(alpha = 0.45f))

        // Huruf "A" dari tiga goresan.
        val letterA = Path().apply {
            moveTo(w * 0.2f, h * 0.46f)
            lineTo(w * 0.32f, h * 0.18f)
            lineTo(w * 0.44f, h * 0.46f)
        }
        drawPath(letterA, tint, style = stroke)
        drawLine(
            color = tint,
            start = Offset(w * 0.25f, h * 0.38f),
            end = Offset(w * 0.39f, h * 0.38f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round,
        )

        // Gelembung kecil berisi titik dan garis.
        drawRoundRect(
            color = tint.copy(alpha = 0.7f),
            topLeft = Offset(w * 0.52f, h * 0.54f),
            size = Size(w * 0.44f, h * 0.38f),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
            style = stroke,
        )
        drawCircle(color = tint.copy(alpha = 0.7f), radius = w * 0.05f, center = Offset(w * 0.64f, h * 0.73f))
        drawLine(
            color = tint.copy(alpha = 0.7f),
            start = Offset(w * 0.74f, h * 0.73f),
            end = Offset(w * 0.88f, h * 0.73f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

// Ikon Kanvas Freeform: sticky note kotak dengan titik konektor dan garis lengkung penghubung.
@Composable
fun FreeformCanvasMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Sticky note kotak dengan satu garis isi.
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.05f, h * 0.06f),
            size = Size(w * 0.4f, h * 0.4f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
            style = stroke,
        )
        drawLine(
            color = tint.copy(alpha = 0.5f),
            start = Offset(w * 0.13f, h * 0.26f),
            end = Offset(w * 0.37f, h * 0.26f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round,
        )

        // Titik-titik konektor.
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.8f, h * 0.18f))
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.86f, h * 0.64f))
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.48f, h * 0.84f))

        // Garis lengkung penghubung.
        val link1 = Path().apply {
            moveTo(w * 0.46f, h * 0.22f)
            cubicTo(w * 0.58f, h * 0.1f, w * 0.66f, h * 0.12f, w * 0.74f, h * 0.18f)
        }
        drawPath(link1, tint.copy(alpha = 0.6f), style = stroke)
        val link2 = Path().apply {
            moveTo(w * 0.28f, h * 0.48f)
            cubicTo(w * 0.3f, h * 0.68f, w * 0.38f, h * 0.76f, w * 0.42f, h * 0.82f)
        }
        drawPath(link2, tint.copy(alpha = 0.6f), style = stroke)
        val link3 = Path().apply {
            moveTo(w * 0.83f, h * 0.24f)
            cubicTo(w * 0.94f, h * 0.36f, w * 0.94f, h * 0.5f, w * 0.86f, h * 0.58f)
        }
        drawPath(link3, tint.copy(alpha = 0.6f), style = stroke)
    }
}

// Ikon Alat Tulis: pena diagonal dengan nib segitiga dan satu kilau empat garis.
@Composable
fun WritingToolsMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Badan pena diagonal.
        drawLine(
            color = tint,
            start = Offset(w * 0.22f, h * 0.72f),
            end = Offset(w * 0.62f, h * 0.26f),
            strokeWidth = 2.4.dp.toPx(),
            cap = StrokeCap.Round,
        )

        // Nib pena berbentuk segitiga di ujung bawah.
        val nib = Path().apply {
            moveTo(w * 0.12f, h * 0.92f)
            lineTo(w * 0.3f, h * 0.84f)
            lineTo(w * 0.22f, h * 0.68f)
            close()
        }
        drawPath(nib, tint.copy(alpha = 0.85f))

        // Kilau/sparkle dari empat garis pendek.
        val sparkleCenter = Offset(w * 0.8f, h * 0.18f)
        drawLine(
            color = tint,
            start = sparkleCenter,
            end = Offset(w * 0.8f, h * 0.06f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = sparkleCenter,
            end = Offset(w * 0.8f, h * 0.3f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = sparkleCenter,
            end = Offset(w * 0.68f, h * 0.18f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = sparkleCenter,
            end = Offset(w * 0.92f, h * 0.18f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

// Ikon Genmoji: wajah smiley (lingkaran, dua mata, senyum) dengan sparkle kecil.
@Composable
fun GenmojiMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Wajah.
        val faceCenter = Offset(w * 0.44f, h * 0.54f)
        drawCircle(
            color = tint,
            radius = w * 0.34f,
            center = faceCenter,
            style = stroke,
        )

        // Dua titik mata.
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.33f, h * 0.44f))
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.55f, h * 0.44f))

        // Senyum berupa arc.
        val smile = Rect(center = faceCenter, radius = w * 0.2f)
        drawArc(
            color = tint,
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = smile.topLeft,
            size = smile.size,
            style = stroke,
        )

        // Sparkle kecil di sudut kanan-atas.
        val sparkleCenter = Offset(w * 0.86f, h * 0.14f)
        drawLine(
            color = tint,
            start = sparkleCenter,
            end = Offset(w * 0.86f, h * 0.05f),
            strokeWidth = 1.3.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = sparkleCenter,
            end = Offset(w * 0.86f, h * 0.23f),
            strokeWidth = 1.3.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = sparkleCenter,
            end = Offset(w * 0.77f, h * 0.14f),
            strokeWidth = 1.3.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = sparkleCenter,
            end = Offset(w * 0.95f, h * 0.14f),
            strokeWidth = 1.3.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

// Ikon Mode Fokus: bulan sabit berupa arc tebal di dalam lingkaran.
@Composable
fun FocusModesMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        drawCircle(
            color = tint.copy(alpha = 0.55f),
            radius = w * 0.42f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = stroke,
        )

        // Bulan sabit berupa arc besar dengan ujung tumpul.
        val moon = Rect(center = Offset(w * 0.5f, h * 0.5f), radius = w * 0.2f)
        drawArc(
            color = tint,
            startAngle = 55f,
            sweepAngle = 250f,
            useCenter = false,
            topLeft = moon.topLeft,
            size = moon.size,
            style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

// Ikon Kunci Sandi: kunci (lingkaran + batang + dua gigi) dengan perisai kecil.
@Composable
fun PasskeysPasswordsMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Kepala kunci dan batang dengan dua gigi.
        drawCircle(
            color = tint,
            radius = w * 0.15f,
            center = Offset(w * 0.28f, h * 0.38f),
            style = stroke,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.43f, h * 0.38f),
            end = Offset(w * 0.92f, h * 0.38f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.72f, h * 0.38f),
            end = Offset(w * 0.72f, h * 0.56f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.86f, h * 0.38f),
            end = Offset(w * 0.86f, h * 0.52f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )

        // Perisai kecil di kiri-bawah.
        val shield = Path().apply {
            moveTo(w * 0.1f, h * 0.6f)
            lineTo(w * 0.38f, h * 0.6f)
            lineTo(w * 0.38f, h * 0.76f)
            lineTo(w * 0.24f, h * 0.92f)
            lineTo(w * 0.1f, h * 0.76f)
            close()
        }
        drawPath(shield, tint.copy(alpha = 0.7f), style = stroke)
    }
}

// Ikon VoiceOver: speaker trapesium dengan dua arc gelombang suara dan cincin fokus terputus.
@Composable
fun VoiceoverLabMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Speaker: trapesium corong dengan badan.
        val speaker = Path().apply {
            moveTo(w * 0.12f, h * 0.4f)
            lineTo(w * 0.26f, h * 0.4f)
            lineTo(w * 0.42f, h * 0.24f)
            lineTo(w * 0.42f, h * 0.76f)
            lineTo(w * 0.26f, h * 0.6f)
            lineTo(w * 0.12f, h * 0.6f)
            close()
        }
        drawPath(speaker, tint.copy(alpha = 0.9f))

        // Dua arc gelombang suara.
        drawArc(
            color = tint,
            startAngle = -55f,
            sweepAngle = 110f,
            useCenter = false,
            topLeft = Offset(w * 0.3f, h * 0.38f),
            size = Size(w * 0.24f, h * 0.24f),
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        drawArc(
            color = tint.copy(alpha = 0.7f),
            startAngle = -55f,
            sweepAngle = 110f,
            useCenter = false,
            topLeft = Offset(w * 0.22f, h * 0.26f),
            size = Size(w * 0.48f, h * 0.48f),
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )

        // Cincin fokus terputus mengelilingi ikon.
        val ring = Rect(center = Offset(w * 0.5f, h * 0.5f), radius = w * 0.4f)
        drawArc(
            color = tint.copy(alpha = 0.5f),
            startAngle = 30f,
            sweepAngle = 110f,
            useCenter = false,
            topLeft = ring.topLeft,
            size = ring.size,
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        drawArc(
            color = tint.copy(alpha = 0.5f),
            startAngle = 210f,
            sweepAngle = 110f,
            useCenter = false,
            topLeft = ring.topLeft,
            size = ring.size,
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

// Ikon Kontinuitas: dua persegi perangkat berbeda ukuran dihubungkan panah melengkung.
@Composable
fun ContinuityMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Perangkat besar.
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.05f, h * 0.34f),
            size = Size(w * 0.36f, h * 0.56f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
            style = stroke,
        )
        // Perangkat kecil.
        drawRoundRect(
            color = tint.copy(alpha = 0.75f),
            topLeft = Offset(w * 0.66f, h * 0.48f),
            size = Size(w * 0.28f, h * 0.42f),
            cornerRadius = CornerRadius(w * 0.07f, w * 0.07f),
            style = stroke,
        )

        // Panah melengkung menghubungkan keduanya.
        val arc = Path().apply {
            moveTo(w * 0.42f, h * 0.38f)
            cubicTo(w * 0.5f, h * 0.1f, w * 0.66f, h * 0.08f, w * 0.72f, h * 0.32f)
        }
        drawPath(arc, tint, style = stroke)

        // Kepala panah kecil di ujung.
        val head = Path().apply {
            moveTo(w * 0.64f, h * 0.24f)
            lineTo(w * 0.73f, h * 0.36f)
            lineTo(w * 0.8f, h * 0.24f)
        }
        drawPath(head, tint, style = stroke)
    }
}

// Ikon Waktu Layar: jam (lingkaran + jarum) dengan tiga batang bar pendek di bawahnya.
@Composable
fun ScreenTimeMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Lingkaran jam dengan jarum.
        val center = Offset(w * 0.5f, h * 0.38f)
        drawCircle(
            color = tint,
            radius = w * 0.28f,
            center = center,
            style = stroke,
        )
        drawLine(
            color = tint,
            start = center,
            end = Offset(w * 0.5f, h * 0.2f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = center,
            end = Offset(w * 0.62f, h * 0.38f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )

        // Tiga batang bar pendek dengan tinggi berbeda di bawah jam.
        drawRoundRect(
            color = tint.copy(alpha = 0.6f),
            topLeft = Offset(w * 0.22f, h * 0.84f),
            size = Size(w * 0.1f, h * 0.1f),
            cornerRadius = CornerRadius(w * 0.03f, w * 0.03f),
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.85f),
            topLeft = Offset(w * 0.45f, h * 0.78f),
            size = Size(w * 0.1f, h * 0.16f),
            cornerRadius = CornerRadius(w * 0.03f, w * 0.03f),
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.6f),
            topLeft = Offset(w * 0.68f, h * 0.82f),
            size = Size(w * 0.1f, h * 0.12f),
            cornerRadius = CornerRadius(w * 0.03f, w * 0.03f),
        )
    }
}

// Ikon Penunjuk & Scribble: kursor panah segitiga outline dengan garis coretan melengkung.
@Composable
fun PointerScribbleMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Kursor panah berupa segitiga outline.
        val cursor = Path().apply {
            moveTo(w * 0.16f, h * 0.08f)
            lineTo(w * 0.16f, h * 0.56f)
            lineTo(w * 0.44f, h * 0.34f)
            close()
        }
        drawPath(cursor, tint, style = stroke)

        // Garis coretan melengkung (squiggle).
        val squiggle = Path().apply {
            moveTo(w * 0.34f, h * 0.88f)
            cubicTo(w * 0.44f, h * 0.64f, w * 0.58f, h * 0.68f, w * 0.62f, h * 0.84f)
            cubicTo(w * 0.66f, h * 0.98f, w * 0.78f, h * 0.9f, w * 0.88f, h * 0.7f)
        }
        drawPath(squiggle, tint.copy(alpha = 0.8f), style = stroke)
    }
}
