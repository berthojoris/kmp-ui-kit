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

// Ikon share iOS: kotak persegi rounded dengan panah keluar ke atas.
@Composable
fun ShareSheetMenuIcon(
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
            topLeft = Offset(w * 0.12f, h * 0.42f),
            size = Size(w * 0.76f, h * 0.52f),
            cornerRadius = CornerRadius(w * 0.14f, w * 0.14f),
            style = stroke,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.5f, h * 0.68f),
            end = Offset(w * 0.5f, h * 0.06f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        val arrowHead = Path().apply {
            moveTo(w * 0.32f, h * 0.22f)
            lineTo(w * 0.5f, h * 0.06f)
            lineTo(w * 0.68f, h * 0.22f)
        }
        drawPath(arrowHead, tint, style = stroke)
    }
}

// Ikon Pusat Kontrol: grid 2x2 rounded-rect kecil dengan satu sel berbentuk pill toggle.
@Composable
fun ControlCenterMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val cell = Size(w * 0.34f, h * 0.34f)
        val corner = CornerRadius(w * 0.09f, w * 0.09f)

        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.04f, h * 0.05f),
            size = cell,
            cornerRadius = corner,
            style = stroke,
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.6f),
            topLeft = Offset(w * 0.62f, h * 0.05f),
            size = cell,
            cornerRadius = corner,
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.04f, h * 0.61f),
            size = cell,
            cornerRadius = corner,
            style = stroke,
        )
        // Sel kanan-bawah menjadi pill toggle dengan knob kecil.
        drawRoundRect(
            color = tint.copy(alpha = 0.25f),
            topLeft = Offset(w * 0.52f, h * 0.64f),
            size = Size(w * 0.44f, h * 0.28f),
            cornerRadius = CornerRadius(h * 0.14f, h * 0.14f),
        )
        drawCircle(
            color = tint,
            radius = h * 0.09f,
            center = Offset(w * 0.85f, h * 0.78f),
        )
    }
}

// Ikon Cincin Aktivitas: tiga cincin konsentris berupa arc stroke dengan alpha berbeda.
@Composable
fun ActivityRingsMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val center = Offset(this.size.width * 0.5f, this.size.height * 0.5f)
        val outer = Rect(center = center, radius = this.size.width * 0.42f)
        val middle = Rect(center = center, radius = this.size.width * 0.29f)
        val inner = Rect(center = center, radius = this.size.width * 0.16f)

        drawArc(
            color = tint,
            startAngle = -90f,
            sweepAngle = 300f,
            useCenter = false,
            topLeft = outer.topLeft,
            size = outer.size,
            style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        drawArc(
            color = tint.copy(alpha = 0.6f),
            startAngle = -60f,
            sweepAngle = 300f,
            useCenter = false,
            topLeft = middle.topLeft,
            size = middle.size,
            style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        drawArc(
            color = tint.copy(alpha = 0.35f),
            startAngle = -120f,
            sweepAngle = 300f,
            useCenter = false,
            topLeft = inner.topLeft,
            size = inner.size,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

// Ikon Layar Kunci: persegi rounded "layar" berisi lingkaran jam kecil dan gembok mini.
@Composable
fun LockScreenMenuIcon(
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
            topLeft = Offset(w * 0.1f, h * 0.04f),
            size = Size(w * 0.8f, h * 0.92f),
            cornerRadius = CornerRadius(w * 0.16f, w * 0.16f),
            style = stroke,
        )

        // Lingkaran jam kecil dengan dua jarum.
        val clockCenter = Offset(w * 0.36f, h * 0.38f)
        drawCircle(
            color = tint.copy(alpha = 0.75f),
            radius = w * 0.13f,
            center = clockCenter,
            style = stroke,
        )
        drawLine(
            color = tint.copy(alpha = 0.75f),
            start = clockCenter,
            end = Offset(w * 0.36f, h * 0.28f),
            strokeWidth = 1.3.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint.copy(alpha = 0.75f),
            start = clockCenter,
            end = Offset(w * 0.44f, h * 0.38f),
            strokeWidth = 1.3.dp.toPx(),
            cap = StrokeCap.Round,
        )

        // Gembok mini: setengah lingkaran sebagai shackle + badan rounded terisi.
        drawArc(
            color = tint,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.61f, h * 0.4f),
            size = Size(w * 0.16f, h * 0.24f),
            style = stroke,
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.58f, h * 0.52f),
            size = Size(w * 0.22f, h * 0.16f),
            cornerRadius = CornerRadius(w * 0.05f, w * 0.05f),
        )
    }
}

// Ikon Banner Notifikasi: rounded-rect banner mendatar berisi dua garis teks dan panah kecil.
@Composable
fun NotificationBannerMenuIcon(
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
            topLeft = Offset(w * 0.03f, h * 0.18f),
            size = Size(w * 0.94f, h * 0.64f),
            cornerRadius = CornerRadius(w * 0.14f, w * 0.14f),
            style = stroke,
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.75f),
            topLeft = Offset(w * 0.12f, h * 0.34f),
            size = Size(w * 0.48f, h * 0.09f),
            cornerRadius = CornerRadius(h * 0.045f, h * 0.045f),
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.4f),
            topLeft = Offset(w * 0.12f, h * 0.52f),
            size = Size(w * 0.32f, h * 0.09f),
            cornerRadius = CornerRadius(h * 0.045f, h * 0.045f),
        )
        val chevron = Path().apply {
            moveTo(w * 0.74f, h * 0.36f)
            lineTo(w * 0.84f, h * 0.5f)
            lineTo(w * 0.74f, h * 0.64f)
        }
        drawPath(chevron, tint, style = stroke)
    }
}

// Ikon Ruler Picker: garis horizontal dengan tick pendek rapat, tick panjang tiap 4, dan penunjuk segitiga di tengah.
@Composable
fun RulerPickerMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val baselineY = h * 0.3f
        drawLine(
            color = tint,
            start = Offset(w * 0.04f, baselineY),
            end = Offset(w * 0.96f, baselineY),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )

        val steps = 12
        val span = w * 0.92f
        repeat(steps + 1) { index ->
            val x = w * 0.04f + span * index / steps
            val isLong = index % 4 == 0
            drawLine(
                color = tint.copy(alpha = if (isLong) 0.9f else 0.45f),
                start = Offset(x, baselineY),
                end = Offset(x, baselineY + if (isLong) h * 0.22f else h * 0.12f),
                strokeWidth = if (isLong) 1.6.dp.toPx() else 1.2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }

        val pointer = Path().apply {
            moveTo(w * 0.5f, h * 0.62f)
            lineTo(w * 0.62f, h * 0.9f)
            lineTo(w * 0.38f, h * 0.9f)
            close()
        }
        drawPath(pointer, tint)
    }
}

// Ikon Token Pencarian: kaca pembesar (lingkaran + gagang) dengan pill token kecil di sampingnya.
@Composable
fun SearchTokensMenuIcon(
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
            radius = w * 0.22f,
            center = Offset(w * 0.33f, h * 0.44f),
            style = stroke,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.49f, h * 0.6f),
            end = Offset(w * 0.64f, h * 0.76f),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
        )

        // Pill token kecil dengan knob isian.
        drawRoundRect(
            color = tint.copy(alpha = 0.55f),
            topLeft = Offset(w * 0.6f, h * 0.06f),
            size = Size(w * 0.38f, h * 0.24f),
            cornerRadius = CornerRadius(h * 0.12f, h * 0.12f),
            style = stroke,
        )
        drawCircle(
            color = tint,
            radius = w * 0.045f,
            center = Offset(w * 0.72f, h * 0.18f),
        )
    }
}

// Ikon Zoom Foto: grid kotak kecil dengan satu kotak lebih besar (level zoom) dan tanda plus/minus kecil.
@Composable
fun PhotosZoomMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        val small = Size(w * 0.16f, h * 0.16f)
        drawRoundRect(
            color = tint.copy(alpha = 0.55f),
            topLeft = Offset(w * 0.06f, h * 0.06f),
            size = small,
            cornerRadius = CornerRadius(w * 0.04f, w * 0.04f),
            style = stroke,
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.55f),
            topLeft = Offset(w * 0.26f, h * 0.06f),
            size = small,
            cornerRadius = CornerRadius(w * 0.04f, w * 0.04f),
            style = stroke,
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.55f),
            topLeft = Offset(w * 0.06f, h * 0.26f),
            size = small,
            cornerRadius = CornerRadius(w * 0.04f, w * 0.04f),
            style = stroke,
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.55f),
            topLeft = Offset(w * 0.26f, h * 0.26f),
            size = small,
            cornerRadius = CornerRadius(w * 0.04f, w * 0.04f),
            style = stroke,
        )

        // Kotak zoom yang lebih besar.
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.52f, h * 0.08f),
            size = Size(w * 0.42f, h * 0.42f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
            style = stroke,
        )

        // Tanda plus dan minus kecil di bawah.
        drawLine(
            color = tint,
            start = Offset(w * 0.26f, h * 0.78f),
            end = Offset(w * 0.42f, h * 0.78f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.34f, h * 0.7f),
            end = Offset(w * 0.34f, h * 0.86f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint.copy(alpha = 0.6f),
            start = Offset(w * 0.7f, h * 0.78f),
            end = Offset(w * 0.86f, h * 0.78f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

// Ikon Aksesori Keyboard: toolbar mendatar tipis di atas deretan tiga kotak tombol keyboard.
@Composable
fun KeyboardAccessoryMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Toolbar tipis di atas.
        drawRoundRect(
            color = tint.copy(alpha = 0.35f),
            topLeft = Offset(w * 0.05f, h * 0.06f),
            size = Size(w * 0.9f, h * 0.14f),
            cornerRadius = CornerRadius(h * 0.07f, h * 0.07f),
        )

        // Tiga kotak tombol keyboard.
        val keySize = Size(w * 0.24f, h * 0.34f)
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.06f, h * 0.44f),
            size = keySize,
            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f),
            style = stroke,
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.38f, h * 0.44f),
            size = keySize,
            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f),
            style = stroke,
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.7f, h * 0.44f),
            size = keySize,
            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f),
            style = stroke,
        )
    }
}

// Ikon Galeri Widget: tiga rounded-rect berbeda ukuran (kecil, sedang, besar) tersusun rapi.
@Composable
fun WidgetGalleryMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Widget kecil.
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.04f, h * 0.06f),
            size = Size(w * 0.3f, h * 0.3f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
            style = stroke,
        )
        // Widget sedang (mendatar).
        drawRoundRect(
            color = tint.copy(alpha = 0.7f),
            topLeft = Offset(w * 0.42f, h * 0.06f),
            size = Size(w * 0.54f, h * 0.3f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
            style = stroke,
        )
        // Widget besar.
        drawRoundRect(
            color = tint.copy(alpha = 0.85f),
            topLeft = Offset(w * 0.04f, h * 0.48f),
            size = Size(w * 0.92f, h * 0.44f),
            cornerRadius = CornerRadius(w * 0.1f, w * 0.1f),
            style = stroke,
        )
    }
}

// Ikon Edit Teks: dua handle seleksi mengapit garis teks dengan caret vertikal dan menu popover kecil di atas.
@Composable
fun TextEditMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val row = Rect(
            left = w * 0.24f,
            top = h * 0.5f,
            right = w * 0.76f,
            bottom = h * 0.8f,
        )

        // Menu popover kecil di atas.
        drawRoundRect(
            color = tint.copy(alpha = 0.45f),
            topLeft = Offset(w * 0.3f, h * 0.06f),
            size = Size(w * 0.4f, h * 0.2f),
            cornerRadius = CornerRadius(w * 0.07f, w * 0.07f),
            style = stroke,
        )

        // Garis teks dan caret vertikal.
        drawLine(
            color = tint.copy(alpha = 0.75f),
            start = Offset(row.left, h * 0.65f),
            end = Offset(row.right, h * 0.65f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.5f, row.top),
            end = Offset(w * 0.5f, row.bottom),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
        )

        // Dua handle seleksi dengan titik di ujung.
        drawLine(
            color = tint,
            start = Offset(row.left, h * 0.55f),
            end = Offset(row.left, h * 0.86f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(row.left, h * 0.86f))
        drawLine(
            color = tint,
            start = Offset(row.right, h * 0.55f),
            end = Offset(row.right, h * 0.86f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(row.right, h * 0.86f))
    }
}

// Ikon Live Activity: pill mendatar memanjang berisi bar progres kecil dan titik status.
@Composable
fun LiveActivityMenuIcon(
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
            topLeft = Offset(w * 0.03f, h * 0.26f),
            size = Size(w * 0.94f, h * 0.48f),
            cornerRadius = CornerRadius(h * 0.24f, h * 0.24f),
            style = stroke,
        )

        // Bar progres kecil: rel redup + bagian terisi.
        drawRoundRect(
            color = tint.copy(alpha = 0.25f),
            topLeft = Offset(w * 0.14f, h * 0.44f),
            size = Size(w * 0.5f, h * 0.12f),
            cornerRadius = CornerRadius(h * 0.06f, h * 0.06f),
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.9f),
            topLeft = Offset(w * 0.14f, h * 0.44f),
            size = Size(w * 0.3f, h * 0.12f),
            cornerRadius = CornerRadius(h * 0.06f, h * 0.06f),
        )

        // Titik status.
        drawCircle(
            color = tint,
            radius = w * 0.06f,
            center = Offset(w * 0.82f, h * 0.5f),
        )
    }
}

// Ikon Precision Finder: panah arah di dalam dua arc cincin konsentris yang terputus.
@Composable
fun PrecisionFinderMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w * 0.5f, h * 0.5f)
        val outer = Rect(center = center, radius = w * 0.42f)
        val inner = Rect(center = center, radius = w * 0.3f)

        drawArc(
            color = tint.copy(alpha = 0.55f),
            startAngle = -120f,
            sweepAngle = 240f,
            useCenter = false,
            topLeft = outer.topLeft,
            size = outer.size,
            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        drawArc(
            color = tint.copy(alpha = 0.35f),
            startAngle = 60f,
            sweepAngle = 240f,
            useCenter = false,
            topLeft = inner.topLeft,
            size = inner.size,
            style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )

        val arrow = Path().apply {
            moveTo(w * 0.5f, h * 0.2f)
            lineTo(w * 0.68f, h * 0.72f)
            lineTo(w * 0.5f, h * 0.6f)
            lineTo(w * 0.32f, h * 0.72f)
            close()
        }
        drawPath(arrow, tint)
    }
}

// Ikon Live Captions: rounded-rect caption berisi dua garis teks dan tiga batang gelombang suara kecil.
@Composable
fun LiveCaptionsMenuIcon(
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
            topLeft = Offset(w * 0.04f, h * 0.12f),
            size = Size(w * 0.92f, h * 0.76f),
            cornerRadius = CornerRadius(w * 0.16f, w * 0.16f),
            style = stroke,
        )

        // Dua garis teks caption.
        drawRoundRect(
            color = tint.copy(alpha = 0.75f),
            topLeft = Offset(w * 0.14f, h * 0.28f),
            size = Size(w * 0.5f, h * 0.09f),
            cornerRadius = CornerRadius(h * 0.045f, h * 0.045f),
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.45f),
            topLeft = Offset(w * 0.14f, h * 0.44f),
            size = Size(w * 0.34f, h * 0.09f),
            cornerRadius = CornerRadius(h * 0.045f, h * 0.045f),
        )

        // Tiga batang gelombang suara kecil.
        drawRoundRect(
            color = tint.copy(alpha = 0.6f),
            topLeft = Offset(w * 0.14f, h * 0.68f),
            size = Size(w * 0.07f, h * 0.1f),
            cornerRadius = CornerRadius(w * 0.035f, w * 0.035f),
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.85f),
            topLeft = Offset(w * 0.26f, h * 0.62f),
            size = Size(w * 0.07f, h * 0.16f),
            cornerRadius = CornerRadius(w * 0.035f, w * 0.035f),
        )
        drawRoundRect(
            color = tint.copy(alpha = 0.6f),
            topLeft = Offset(w * 0.38f, h * 0.66f),
            size = Size(w * 0.07f, h * 0.12f),
            cornerRadius = CornerRadius(w * 0.035f, w * 0.035f),
        )
    }
}

// Ikon Page Curl: lembar halaman dengan sudut kanan-bawah terlipat (segitiga curl) dan garis tekuk.
@Composable
fun PageCurlMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Siluet halaman dengan sudut kanan-bawah terpotong.
        val page = Path().apply {
            moveTo(w * 0.12f, h * 0.06f)
            lineTo(w * 0.86f, h * 0.06f)
            lineTo(w * 0.86f, h * 0.58f)
            lineTo(w * 0.54f, h * 0.94f)
            lineTo(w * 0.12f, h * 0.94f)
            close()
        }
        drawPath(page, tint.copy(alpha = 0.45f), style = stroke)

        // Segitiga lipatan (curl) di atas halaman.
        val curl = Path().apply {
            moveTo(w * 0.86f, h * 0.58f)
            lineTo(w * 0.54f, h * 0.94f)
            lineTo(w * 0.52f, h * 0.62f)
            close()
        }
        drawPath(curl, tint.copy(alpha = 0.25f))
        drawPath(curl, tint, style = stroke)

        // Garis tekuk.
        drawLine(
            color = tint.copy(alpha = 0.8f),
            start = Offset(w * 0.86f, h * 0.58f),
            end = Offset(w * 0.54f, h * 0.94f),
            strokeWidth = 1.3.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

// Ikon StandBy: bingkai lanskap rounded berisi lingkaran jam berjarum dan bulan sabit kecil.
@Composable
fun StandbyModeMenuIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    tint: Color = LightAppPalette.textPrimary,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Bingkai lanskap.
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.03f, h * 0.2f),
            size = Size(w * 0.94f, h * 0.6f),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
            style = stroke,
        )

        // Lingkaran jam dengan jarum.
        val clockCenter = Offset(w * 0.34f, h * 0.5f)
        drawCircle(
            color = tint.copy(alpha = 0.85f),
            radius = w * 0.15f,
            center = clockCenter,
            style = stroke,
        )
        drawLine(
            color = tint.copy(alpha = 0.85f),
            start = clockCenter,
            end = Offset(w * 0.34f, h * 0.38f),
            strokeWidth = 1.3.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint.copy(alpha = 0.85f),
            start = clockCenter,
            end = Offset(w * 0.43f, h * 0.54f),
            strokeWidth = 1.3.dp.toPx(),
            cap = StrokeCap.Round,
        )

        // Bulan sabit kecil berupa arc.
        drawArc(
            color = tint,
            startAngle = 60f,
            sweepAngle = 300f,
            useCenter = false,
            topLeft = Offset(w * 0.62f, h * 0.38f),
            size = Size(w * 0.24f, h * 0.24f),
            style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}
