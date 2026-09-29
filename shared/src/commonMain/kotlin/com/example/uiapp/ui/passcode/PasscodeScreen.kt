package com.example.uiapp.ui.passcode

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val CorrectPin = "1234"
private const val PinLength = 4
private const val SwipeThreshold = 0.82f

private enum class UnlockMethod(val label: String) {
    PIN("PIN"),
    BIOMETRIC("Biometrik"),
    SWIPE("Swipe"),
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasscodeScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()
    val shake = remember { Animatable(0f) }

    var method by remember { mutableStateOf(UnlockMethod.PIN) }
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }
    var shakeTrigger by remember { mutableIntStateOf(0) }
    var bioScanning by remember { mutableStateOf(false) }

    LaunchedEffect(pin) {
        if (pin.length == PinLength) {
            delay(140)
            if (pin == CorrectPin) {
                success = true
            } else {
                error = true
                shakeTrigger += 1
                delay(520)
                pin = ""
                error = false
            }
        }
    }

    LaunchedEffect(shakeTrigger) {
        if (shakeTrigger == 0) return@LaunchedEffect
        listOf(-12f, 12f, -9f, 9f, -5f, 5f, 0f).forEach { value ->
            shake.snapTo(value)
            delay(38)
        }
    }

    LaunchedEffect(method, bioScanning) {
        if (method == UnlockMethod.BIOMETRIC && bioScanning) {
            delay(1300)
            bioScanning = false
            success = true
        }
    }

    fun switchMethod(next: UnlockMethod) {
        if (success || next == method) return
        method = next
        pin = ""
        error = false
        bioScanning = false
        scope.launch { shake.snapTo(0f) }
    }

    fun appendDigit(digit: String) {
        if (success || pin.length >= PinLength) return
        pin += digit
    }

    fun removeDigit() {
        if (success || pin.isEmpty()) return
        pin = pin.dropLast(1)
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Passcode",
                subtitle = "PIN, biometrik & swipe untuk buka",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = when {
                    success -> "Terkunci dibuka"
                    method == UnlockMethod.PIN -> "Masukkan PIN"
                    method == UnlockMethod.BIOMETRIC -> "Pindai wajah"
                    else -> "Geser untuk buka"
                },
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = if (success) palette.success else palette.textPrimary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when {
                    success -> "Selamat datang kembali"
                    method == UnlockMethod.PIN -> "Masukkan 4 digit untuk membuka"
                    method == UnlockMethod.BIOMETRIC -> "Arahkan wajah ke kamera \u00B7 simulasi"
                    else -> "Tarik tombol ke kanan untuk membuka"
                },
                fontSize = 13.sp,
                color = palette.textSecondary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(22.dp))

            if (!success) {
                MethodSwitcher(
                    selected = method,
                    enabled = !bioScanning,
                    onSelect = ::switchMethod,
                )
            }

            when {
                success -> UnlockedContent(modifier = Modifier.weight(1f))

                method == UnlockMethod.PIN -> PinContent(
                    modifier = Modifier.weight(1f),
                    pin = pin,
                    error = error,
                    shakeOffset = shake.value,
                    onDigit = ::appendDigit,
                    onBackspace = ::removeDigit,
                    onBiometric = { switchMethod(UnlockMethod.BIOMETRIC) },
                )

                method == UnlockMethod.BIOMETRIC -> BiometricContent(
                    modifier = Modifier.weight(1f),
                    scanning = bioScanning,
                    onScan = { bioScanning = true },
                    onUsePin = { switchMethod(UnlockMethod.PIN) },
                )

                else -> SwipeContent(
                    modifier = Modifier.weight(1f),
                    onUnlocked = { success = true },
                    onUsePin = { switchMethod(UnlockMethod.PIN) },
                )
            }
        }
    }
}

@Composable
private fun MethodSwitcher(
    selected: UnlockMethod,
    enabled: Boolean,
    onSelect: (UnlockMethod) -> Unit,
) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        UnlockMethod.entries.forEach { option ->
            val isSelected = option == selected
            Surface(
                onClick = { onSelect(option) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) palette.primary else Color.Transparent,
                contentColor = if (isSelected) palette.onPrimary else palette.textSecondary,
                border = BorderStroke(1.dp, if (isSelected) palette.primary else Color.Transparent),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = option.label,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) palette.onPrimary else palette.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun PinContent(
    modifier: Modifier,
    pin: String,
    error: Boolean,
    shakeOffset: Float,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onBiometric: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.offset(x = shakeOffset.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            repeat(PinLength) { index ->
                PinDot(
                    filled = index < pin.length && pin.isNotEmpty(),
                    error = error,
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Keypad(
            enabled = true,
            onDigit = onDigit,
            onBackspace = onBackspace,
            onBiometric = onBiometric,
        )

        Spacer(modifier = Modifier.height(14.dp))

        LinkText(
            text = "Lupa PIN?",
            color = palette.primary,
            onClick = {},
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun BiometricContent(
    modifier: Modifier,
    scanning: Boolean,
    onScan: () -> Unit,
    onUsePin: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(108.dp)
                .clip(CircleShape)
                .background(palette.surfaceMuted)
                .border(1.dp, palette.border, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            FaceScanIcon(
                scanning = scanning,
                tint = palette.primary,
                modifier = Modifier.size(52.dp),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (scanning) "Memindai\u2026" else "Siap memindai",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textPrimary,
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = if (scanning) "Memindai\u2026" else "Pindai Wajah",
            enabled = !scanning,
            onClick = onScan,
        )

        Spacer(modifier = Modifier.height(6.dp))

        LinkText(
            text = "Gunakan PIN",
            color = palette.primary,
            onClick = onUsePin,
        )
    }
}

@Composable
private fun SwipeContent(
    modifier: Modifier,
    onUnlocked: () -> Unit,
    onUsePin: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(108.dp)
                .clip(CircleShape)
                .background(palette.surfaceMuted)
                .border(1.dp, palette.border, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            LockGlyph(
                tint = palette.textPrimary,
                modifier = Modifier.size(48.dp),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Konten terkunci",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textPrimary,
        )

        Spacer(modifier = Modifier.weight(1f))

        SwipeToUnlockBar(onUnlocked = onUnlocked)

        Spacer(modifier = Modifier.height(6.dp))

        LinkText(
            text = "Gunakan PIN",
            color = palette.primary,
            onClick = onUsePin,
        )
    }
}

@Composable
private fun UnlockedContent(modifier: Modifier) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(palette.success.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            CheckGlyph(tint = palette.success, modifier = Modifier.size(44.dp))
        }
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (enabled) palette.primary else palette.surfaceMuted,
        border = BorderStroke(1.dp, if (enabled) palette.primary else palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 15.dp),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) palette.onPrimary else palette.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun LinkText(
    text: String,
    color: Color,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = color,
    )
}

@Composable
private fun PinDot(filled: Boolean, error: Boolean) {
    val palette = LocalAppPalette.current
    val targetColor = if (error) palette.danger else palette.primary
    val color by animateColorAsState(
        targetValue = if (filled) targetColor else palette.border,
        animationSpec = tween(180),
        label = "pinDot",
    )
    Canvas(modifier = Modifier.size(18.dp)) {
        val radius = size.minDimension / 2f
        if (filled) {
            drawCircle(color = color, radius = radius)
        } else {
            drawCircle(
                color = color,
                radius = radius - 1.5.dp.toPx(),
                style = Stroke(width = 1.8.dp.toPx()),
            )
        }
    }
}

private enum class KeyKind { DIGIT, BIOMETRIC, BACKSPACE }

private data class KeypadKey(val label: String, val kind: KeyKind)

private val KeypadRows = listOf(
    listOf(KeypadKey("1", KeyKind.DIGIT), KeypadKey("2", KeyKind.DIGIT), KeypadKey("3", KeyKind.DIGIT)),
    listOf(KeypadKey("4", KeyKind.DIGIT), KeypadKey("5", KeyKind.DIGIT), KeypadKey("6", KeyKind.DIGIT)),
    listOf(KeypadKey("7", KeyKind.DIGIT), KeypadKey("8", KeyKind.DIGIT), KeypadKey("9", KeyKind.DIGIT)),
    listOf(KeypadKey("", KeyKind.BIOMETRIC), KeypadKey("0", KeyKind.DIGIT), KeypadKey("", KeyKind.BACKSPACE)),
)

@Composable
private fun Keypad(
    enabled: Boolean,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onBiometric: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        KeypadRows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                row.forEach { key ->
                    KeypadButton(
                        key = key,
                        enabled = enabled,
                        onClick = {
                            when (key.kind) {
                                KeyKind.DIGIT -> onDigit(key.label)
                                KeyKind.BACKSPACE -> onBackspace()
                                KeyKind.BIOMETRIC -> onBiometric()
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(key: KeypadKey, enabled: Boolean, onClick: () -> Unit) {
    val palette = LocalAppPalette.current
    val isDigit = key.kind == KeyKind.DIGIT
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(if (isDigit) palette.surface else Color.Transparent)
            .border(
                width = if (isDigit) 1.dp else 0.dp,
                color = if (isDigit) palette.border else Color.Transparent,
                shape = CircleShape,
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when (key.kind) {
            KeyKind.DIGIT -> Text(
                text = key.label,
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                color = palette.textPrimary,
            )

            KeyKind.BIOMETRIC -> FaceGlyph(
                tint = palette.primary.copy(alpha = if (enabled) 1f else 0.4f),
            )

            KeyKind.BACKSPACE -> BackspaceGlyph(tint = palette.textPrimary.copy(alpha = if (enabled) 1f else 0.4f))
        }
    }
}

@Composable
private fun SwipeToUnlockBar(onUnlocked: () -> Unit) {
    val palette = LocalAppPalette.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    var unlocked by remember { mutableStateOf(false) }
    val offsetX = remember { Animatable(0f) }

    val transition = rememberInfiniteTransition(label = "swipeHint")
    val hintAlpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "swipeHintAlpha",
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(CircleShape)
            .background(palette.surface)
            .border(1.dp, palette.border, CircleShape)
            .semantics { contentDescription = "Geser tombol ke kanan untuk membuka" },
    ) {
        val maxDragPx = with(density) { (maxWidth - 64.dp).toPx() }
        val progress = if (maxDragPx > 0f) (offsetX.value / maxDragPx).coerceIn(0f, 1f) else 0f

        if (!unlocked && progress > 0f) {
            Box(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth(progress)
                    .clip(CircleShape)
                    .background(palette.primary.copy(alpha = 0.14f)),
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (unlocked) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CheckGlyph(tint = palette.success, modifier = Modifier.size(18.dp))
                    Text(
                        text = "Membuka\u2026",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.success,
                    )
                }
            } else {
                Row(
                    modifier = Modifier.alpha(hintAlpha * (1f - progress)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "Geser untuk buka",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textSecondary,
                    )
                    Text(
                        text = "\u203A\u203A\u203A",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                    )
                }
            }
        }

        if (!unlocked) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .size(64.dp)
                    .padding(5.dp)
                    .clip(CircleShape)
                    .background(palette.primary)
                    .semantics { contentDescription = "Tombol geser untuk membuka" }
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            val target = (offsetX.value + delta).coerceIn(0f, maxDragPx)
                            scope.launch { offsetX.snapTo(target) }
                        },
                        onDragStopped = {
                            if (maxDragPx > 0f && offsetX.value / maxDragPx >= SwipeThreshold) {
                                scope.launch {
                                    offsetX.animateTo(maxDragPx, tween(160, easing = FastOutSlowInEasing))
                                    unlocked = true
                                    delay(180)
                                    onUnlocked()
                                }
                            } else {
                                scope.launch {
                                    offsetX.animateTo(0f, spring(dampingRatio = 0.7f, stiffness = 600f))
                                }
                            }
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                LockGlyph(
                    tint = palette.onPrimary,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun LockGlyph(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.22f, h * 0.44f),
            size = Size(w * 0.56f, h * 0.42f),
            cornerRadius = CornerRadius(w * 0.1f, w * 0.1f),
            style = stroke,
        )
        val shackle = Path().apply {
            moveTo(w * 0.33f, h * 0.44f)
            lineTo(w * 0.33f, h * 0.3f)
            cubicTo(w * 0.33f, h * 0.1f, w * 0.67f, h * 0.1f, w * 0.67f, h * 0.3f)
            lineTo(w * 0.67f, h * 0.44f)
        }
        drawPath(shackle, tint, style = stroke)
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.64f))
    }
}

@Composable
private fun CheckGlyph(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawCircle(
            color = tint,
            radius = size.minDimension * 0.44f,
            style = Stroke(width = 2.6.dp.toPx()),
        )
        val check = Path().apply {
            moveTo(size.width * 0.3f, size.height * 0.52f)
            lineTo(size.width * 0.45f, size.height * 0.67f)
            lineTo(size.width * 0.72f, size.height * 0.34f)
        }
        drawPath(
            path = check,
            color = tint,
            style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

@Composable
private fun FaceGlyph(tint: Color) {
    Canvas(modifier = Modifier.size(30.dp)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(color = tint, radius = w * 0.04f, center = Offset(w * 0.38f, h * 0.42f))
        drawCircle(color = tint, radius = w * 0.04f, center = Offset(w * 0.62f, h * 0.42f))
        drawLine(tint, Offset(w * 0.5f, h * 0.42f), Offset(w * 0.5f, h * 0.58f), 1.8.dp.toPx(), StrokeCap.Round)
        val smile = Path().apply {
            moveTo(w * 0.4f, h * 0.66f)
            cubicTo(w * 0.46f, h * 0.72f, w * 0.54f, h * 0.72f, w * 0.6f, h * 0.66f)
        }
        drawPath(smile, tint, style = stroke)
    }
}

@Composable
private fun FaceScanIcon(scanning: Boolean, tint: Color, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "faceScan")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "facePulse",
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

        if (scanning) {
            val radius = w * (0.34f + 0.5f * pulse)
            drawCircle(
                color = tint.copy(alpha = (1f - pulse) * 0.4f),
                radius = radius,
                center = Offset(w / 2, h / 2),
                style = Stroke(width = 2.dp.toPx()),
            )
        }

        val inset = w * 0.12f
        val corner = w * 0.2f
        listOf(
            Offset(inset, inset),
            Offset(w - inset, inset),
            Offset(inset, h - inset),
            Offset(w - inset, h - inset),
        ).forEachIndexed { index, point ->
            val dx = if (index % 2 == 0) 1f else -1f
            val dy = if (index < 2) 1f else -1f
            drawLine(tint, point, Offset(point.x + corner * dx, point.y), 2.4.dp.toPx(), StrokeCap.Round)
            drawLine(tint, point, Offset(point.x, point.y + corner * dy), 2.4.dp.toPx(), StrokeCap.Round)
        }

        drawCircle(color = tint, radius = w * 0.035f, center = Offset(w * 0.38f, h * 0.44f))
        drawCircle(color = tint, radius = w * 0.035f, center = Offset(w * 0.62f, h * 0.44f))
        drawLine(tint, Offset(w * 0.5f, h * 0.44f), Offset(w * 0.5f, h * 0.58f), 2.dp.toPx(), StrokeCap.Round)
        val smile = Path().apply {
            moveTo(w * 0.4f, h * 0.66f)
            cubicTo(w * 0.46f, h * 0.72f, w * 0.54f, h * 0.72f, w * 0.6f, h * 0.66f)
        }
        drawPath(smile, tint, style = stroke)
    }
}

@Composable
private fun BackspaceGlyph(tint: Color) {
    Canvas(modifier = Modifier.size(28.dp)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val body = Path().apply {
            moveTo(w * 0.42f, h * 0.2f)
            lineTo(w * 0.92f, h * 0.2f)
            lineTo(w * 0.92f, h * 0.8f)
            lineTo(w * 0.42f, h * 0.8f)
            lineTo(w * 0.08f, h * 0.5f)
            close()
        }
        drawPath(body, tint, style = stroke)
        drawLine(tint, Offset(w * 0.5f, h * 0.38f), Offset(w * 0.74f, h * 0.62f), 2.dp.toPx(), StrokeCap.Round)
        drawLine(tint, Offset(w * 0.74f, h * 0.38f), Offset(w * 0.5f, h * 0.62f), 2.dp.toPx(), StrokeCap.Round)
    }
}
