package com.example.uiapp.ui.permissions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.UiTopBar

private enum class PermStatus { NOT_REQUESTED, GRANTED, DENIED }

private enum class PermGlyph { NOTIFICATION, LOCATION, CAMERA }

private data class PermissionItem(
    val title: String,
    val rationale: String,
    val glyph: PermGlyph,
)

private val Permissions = listOf(
    PermissionItem(
        "Notifikasi",
        "Kirim pengingat jadwal, status pesanan, dan promo penting.",
        PermGlyph.NOTIFICATION,
    ),
    PermissionItem(
        "Lokasi",
        "Temukan properti terdekat dan estimasi jarak perjalanan Anda.",
        PermGlyph.LOCATION,
    ),
    PermissionItem(
        "Kamera",
        "Pindai dokumen dan unggah foto properti langsung dari aplikasi.",
        PermGlyph.CAMERA,
    ),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PermissionsScreen(onBack: () -> Unit, onFinish: () -> Unit) {
    val palette = LocalAppPalette.current

    BackHandler(enabled = true) { onBack() }

    val statuses = remember {
        mutableStateListOf(PermStatus.NOT_REQUESTED, PermStatus.NOT_REQUESTED, PermStatus.NOT_REQUESTED)
    }
    var pendingIndex by remember { mutableStateOf(-1) }

    val granted = statuses.count { it == PermStatus.GRANTED }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = palette.background,
            topBar = {
                UiTopBar(
                    title = "Izin Aplikasi",
                    subtitle = "Permission request",
                    onBack = onBack,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                ProgressHeader(granted = granted, total = Permissions.size)

                Spacer(modifier = Modifier.height(18.dp))

                Permissions.forEachIndexed { index, item ->
                    PermissionCard(
                        item = item,
                        status = statuses[index],
                        onAllow = { pendingIndex = index },
                        onOpenSettings = { statuses[index] = PermStatus.NOT_REQUESTED },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                FlatPrimaryButton(
                    text = "Selesai",
                    onClick = onFinish,
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        if (pendingIndex >= 0) {
            val item = Permissions[pendingIndex]
            SystemPermissionDialog(
                item = item,
                onDeny = {
                    statuses[pendingIndex] = PermStatus.DENIED
                    pendingIndex = -1
                },
                onGrant = {
                    statuses[pendingIndex] = PermStatus.GRANTED
                    pendingIndex = -1
                },
            )
        }
    }
}

@Composable
private fun ProgressHeader(granted: Int, total: Int) {
    val palette = LocalAppPalette.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$granted dari $total diizinkan",
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                )
                Text(
                    text = "${(granted * 100) / total}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(palette.surfaceMuted),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(granted.toFloat() / total)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(palette.primary),
                )
            }
        }
    }
}

@Composable
private fun PermissionCard(
    item: PermissionItem,
    status: PermStatus,
    onAllow: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    PermissionIcon(glyph = item.glyph, tint = palette.primary)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = item.rationale,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = palette.textSecondary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (status) {
                PermStatus.NOT_REQUESTED -> ActionChip(
                    label = "Izinkan",
                    background = palette.primary,
                    content = Color.White,
                    onClick = onAllow,
                )

                PermStatus.DENIED -> Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(label = "Ditolak", color = palette.danger, background = Color(0xFFFEF2F2))
                    Spacer(modifier = Modifier.width(10.dp))
                    ActionChip(
                        label = "Buka Pengaturan",
                        background = palette.surface,
                        content = palette.textPrimary,
                        outlined = true,
                        onClick = onOpenSettings,
                    )
                }

                PermStatus.GRANTED -> StatusChip(
                    label = "Diizinkan",
                    color = palette.success,
                    background = palette.primaryContainer,
                )
            }
        }
    }
}

@Composable
private fun ActionChip(
    label: String,
    background: Color,
    content: Color,
    onClick: () -> Unit,
    outlined: Boolean = false,
) {
    val palette = LocalAppPalette.current

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = background,
        border = if (outlined) BorderStroke(1.dp, palette.border) else null,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = content,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun StatusChip(label: String, color: Color, background: Color) {
    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = color,
    )
}

@Composable
private fun SystemPermissionDialog(
    item: PermissionItem,
    onDeny: () -> Unit,
    onGrant: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(enabled = false) {}
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(palette.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    PermissionIcon(glyph = item.glyph, tint = Color.White, size = 26.dp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Izinkan \u201CTesting UI\u201D mengakses ${item.title.lowercase()}?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.rationale,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = palette.textSecondary,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(22.dp))
                FlatPrimaryButton(text = "Izinkan", onClick = onGrant)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    onClick = onDeny,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Text(
                        text = "Jangan izinkan",
                        modifier = Modifier.padding(vertical = 14.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionIcon(
    glyph: PermGlyph,
    tint: Color,
    size: androidx.compose.ui.unit.Dp = 22.dp,
) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (glyph) {
            PermGlyph.NOTIFICATION -> {
                val bell = Path().apply {
                    moveTo(w * 0.25f, h * 0.70f)
                    lineTo(w * 0.34f, h * 0.60f)
                    lineTo(w * 0.34f, h * 0.44f)
                    cubicTo(w * 0.34f, h * 0.25f, w * 0.66f, h * 0.25f, w * 0.66f, h * 0.44f)
                    lineTo(w * 0.66f, h * 0.60f)
                    lineTo(w * 0.75f, h * 0.70f)
                    close()
                }
                drawPath(bell, tint, style = stroke)
                val clapper = Path().apply {
                    moveTo(w * 0.43f, h * 0.76f)
                    cubicTo(w * 0.45f, h * 0.84f, w * 0.55f, h * 0.84f, w * 0.57f, h * 0.76f)
                }
                drawPath(clapper, tint, style = stroke)
            }

            PermGlyph.LOCATION -> {
                val pin = Path().apply {
                    moveTo(w * 0.5f, h * 0.88f)
                    cubicTo(w * 0.22f, h * 0.58f, w * 0.24f, h * 0.16f, w * 0.5f, h * 0.16f)
                    cubicTo(w * 0.76f, h * 0.16f, w * 0.78f, h * 0.58f, w * 0.5f, h * 0.88f)
                    close()
                }
                drawPath(pin, tint, style = stroke)
                drawCircle(
                    color = tint,
                    radius = w * 0.11f,
                    center = Offset(w * 0.5f, h * 0.4f),
                    style = stroke,
                )
            }

            PermGlyph.CAMERA -> {
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.12f, h * 0.30f),
                    size = Size(w * 0.76f, h * 0.5f),
                    cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
                    style = stroke,
                )
                drawCircle(
                    color = tint,
                    radius = w * 0.14f,
                    center = Offset(w * 0.5f, h * 0.55f),
                    style = stroke,
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.36f, h * 0.18f),
                    size = Size(w * 0.28f, h * 0.12f),
                    cornerRadius = CornerRadius(w * 0.05f, w * 0.05f),
                    style = stroke,
                )
            }
        }
    }
}
