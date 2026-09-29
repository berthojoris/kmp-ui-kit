package com.example.uiapp.ui.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.FlatTextField
import com.example.uiapp.ui.components.UiTopBar

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthScreen(onBack: () -> Unit, onFinish: () -> Unit) {
    val palette = LocalAppPalette.current

    BackHandler(enabled = true) { onBack() }

    var mode by remember { mutableIntStateOf(0) }
    val isRegister = mode == 1

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var agree by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

    val emailValid = email.contains("@") && email.substringAfter("@").contains(".")
    val passwordValid = password.length >= 6
    val nameValid = name.trim().length >= 2
    val confirmValid = confirm.isNotEmpty() && confirm == password
    val canSubmit = if (isRegister) {
        nameValid && emailValid && passwordValid && confirmValid && agree
    } else {
        emailValid && passwordValid
    }

    fun submit() {
        submitted = true
        if (canSubmit) onFinish()
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = if (isRegister) "Buat Akun" else "Masuk",
                subtitle = "Login, register, social sign-in",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            ModeSwitch(
                selected = mode,
                onSelect = {
                    mode = it
                    submitted = false
                },
            )

            Spacer(modifier = Modifier.height(22.dp))

            if (isRegister) {
                FlatTextField(
                    label = "Nama lengkap",
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Ayu Prameswari",
                    error = if (submitted && !nameValid) "Nama minimal 2 karakter" else null,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            FlatTextField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                placeholder = "nama@email.com",
                keyboardType = KeyboardType.Email,
                error = if (submitted && !emailValid) "Masukkan email yang valid" else null,
            )

            Spacer(modifier = Modifier.height(16.dp))

            FlatTextField(
                label = "Kata sandi",
                value = password,
                onValueChange = { password = it },
                placeholder = "Minimal 6 karakter",
                isPassword = true,
                error = if (submitted && !passwordValid) "Kata sandi minimal 6 karakter" else null,
            )

            if (isRegister) {
                Spacer(modifier = Modifier.height(16.dp))
                FlatTextField(
                    label = "Konfirmasi kata sandi",
                    value = confirm,
                    onValueChange = { confirm = it },
                    placeholder = "Ulangi kata sandi",
                    isPassword = true,
                    imeAction = ImeAction.Done,
                    error = if (submitted && !confirmValid) "Kata sandi tidak sama" else null,
                )
                Spacer(modifier = Modifier.height(14.dp))
                CheckRow(
                    checked = agree,
                    onCheckedChange = { agree = it },
                    label = "Saya setuju dengan Syarat & Ketentuan",
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CheckRow(
                        checked = agree,
                        onCheckedChange = { agree = it },
                        label = "Ingat saya",
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "Lupa sandi?",
                        modifier = Modifier.clickable { },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.primary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            FlatPrimaryButton(
                text = if (isRegister) "Daftar" else "Masuk",
                onClick = { submit() },
            )

            Spacer(modifier = Modifier.height(22.dp))

            OrDivider()

            Spacer(modifier = Modifier.height(18.dp))

            SocialButton(
                kind = SocialKind.APPLE,
                onClick = { onFinish() },
            )
            Spacer(modifier = Modifier.height(10.dp))
            SocialButton(
                kind = SocialKind.GOOGLE,
                onClick = { onFinish() },
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ModeSwitch(selected: Int, onSelect: (Int) -> Unit) {
    val palette = LocalAppPalette.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val segmentWidth = maxWidth / 2
            val offset by animateDpAsState(
                targetValue = segmentWidth * selected,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "authMode",
            )
            Box(
                modifier = Modifier
                    .offset(x = offset)
                    .width(segmentWidth)
                    .fillMaxHeight()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(palette.surface)
                    .border(1.dp, palette.border, RoundedCornerShape(9.dp)),
            )
            Row(modifier = Modifier.fillMaxSize()) {
                listOf("Masuk", "Daftar").forEachIndexed { index, label ->
                    val active = index == selected
                    val color by animateColorAsState(
                        targetValue = if (active) palette.textPrimary else palette.textMuted,
                        animationSpec = tween(200),
                        label = "authTint",
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onSelect(index) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                            color = color,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current

    Row(
        modifier = modifier.clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(modifier = Modifier.size(20.dp)) {
            val radius = size.minDimension / 2f
            if (checked) {
                drawCircle(color = palette.primary, radius = radius)
                val check = Path().apply {
                    moveTo(size.width * 0.28f, size.height * 0.52f)
                    lineTo(size.width * 0.44f, size.height * 0.68f)
                    lineTo(size.width * 0.74f, size.height * 0.34f)
                }
                drawPath(
                    path = check,
                    color = Color.White,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
            } else {
                drawCircle(
                    color = palette.border,
                    radius = radius - 1.dp.toPx(),
                    style = Stroke(width = 1.6.dp.toPx()),
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = palette.textSecondary,
        )
    }
}

@Composable
private fun OrDivider() {
    val palette = LocalAppPalette.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(palette.border),
        )
        Text(
            text = "atau",
            modifier = Modifier.padding(horizontal = 12.dp),
            fontSize = 11.sp,
            color = palette.textMuted,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(palette.border),
        )
    }
}

private enum class SocialKind(val label: String) { APPLE("Lanjutkan dengan Apple"), GOOGLE("Lanjutkan dengan Google") }

@Composable
private fun SocialButton(kind: SocialKind, onClick: () -> Unit) {
    val palette = LocalAppPalette.current

    val isApple = kind == SocialKind.APPLE
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (isApple) Color(0xFF111827) else palette.surface,
        border = if (isApple) null else BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isApple) {
                AppleLogo(tint = Color.White)
            } else {
                GoogleLogo()
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = kind.label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isApple) Color.White else palette.textPrimary,
            )
        }
    }
}

@Composable
private fun AppleLogo(tint: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        drawCircle(color = tint, radius = w * 0.32f, center = Offset(w * 0.48f, h * 0.6f))
        drawCircle(color = Color(0xFF111827), radius = w * 0.13f, center = Offset(w * 0.76f, h * 0.52f))
        val leaf = Path().apply {
            moveTo(w * 0.5f, h * 0.3f)
            cubicTo(w * 0.52f, h * 0.14f, w * 0.68f, h * 0.12f, w * 0.72f, h * 0.16f)
            cubicTo(w * 0.68f, h * 0.3f, w * 0.58f, h * 0.34f, w * 0.5f, h * 0.3f)
            close()
        }
        drawPath(leaf, tint)
    }
}

@Composable
private fun GoogleLogo() {
    Canvas(modifier = Modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = w * 0.2f, cap = StrokeCap.Butt)
        val topLeft = Offset(w * 0.14f, h * 0.14f)
        val arcSize = Size(w * 0.72f, h * 0.72f)
        drawArc(Color(0xFFEA4335), -120f, 72f, false, topLeft, arcSize, style = stroke)
        drawArc(Color(0xFFFBBC05), 176f, 72f, false, topLeft, arcSize, style = stroke)
        drawArc(Color(0xFF34A853), 90f, 72f, false, topLeft, arcSize, style = stroke)
        drawArc(Color(0xFF4285F4), 12f, 66f, false, topLeft, arcSize, style = stroke)
        drawRect(
            color = Color(0xFF4285F4),
            topLeft = Offset(w * 0.5f, h * 0.42f),
            size = Size(w * 0.34f, h * 0.16f),
        )
    }
}
