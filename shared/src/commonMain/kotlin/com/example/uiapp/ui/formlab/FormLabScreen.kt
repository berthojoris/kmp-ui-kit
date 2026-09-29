package com.example.uiapp.ui.formlab

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LuxuryColors
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.roundToInt

private val AllInterests = listOf(
    "Alam", "Pantai", "Kuliner", "Budaya", "Petualangan", "Belanja", "Spa", "Malam",
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FormLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf(3f..12f) }
    val interests = remember { mutableStateListOf("Alam", "Kuliner") }
    var otp by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val nameError = submitted && name.isBlank()
    val emailError = submitted && !(email.contains("@") && email.substringAfter("@").contains("."))
    val phoneError = submitted && phone.count { it.isDigit() } < 9
    val formValid = !nameError && !emailError && !phoneError &&
        name.isNotBlank() && email.contains("@") && phone.count { it.isDigit() } >= 9
    val showSuccess = submitted && formValid

    Scaffold(
        containerColor = LuxuryColors.Background,
        topBar = {
            UiTopBar(
                title = "Form Lab",
                subtitle = "Input, validasi, slider, chip, dan OTP",
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
                .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            SectionLabel("FLOATING LABEL & VALIDASI")

            FormField(
                value = name,
                onValueChange = { name = it },
                label = "Nama lengkap",
                placeholder = "Mis. Ayu Prameswari",
                isError = nameError,
                errorText = "Nama wajib diisi",
            )
            Spacer(modifier = Modifier.height(14.dp))
            FormField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "nama@email.com",
                isError = emailError,
                errorText = "Format email tidak valid",
                keyboardType = KeyboardType.Email,
            )
            Spacer(modifier = Modifier.height(14.dp))
            FormField(
                value = phone,
                onValueChange = { phone = it.filter { c -> c.isDigit() || c == '+' || c == ' ' } },
                label = "Nomor telepon",
                placeholder = "+62 812 3456 7890",
                isError = phoneError,
                errorText = "Nomor minimal 9 digit",
                keyboardType = KeyboardType.Phone,
            )

            Spacer(modifier = Modifier.height(26.dp))
            SectionLabel("RANGE SLIDER")

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = LuxuryColors.SurfaceWhite,
                border = BorderStroke(1.dp, LuxuryColors.SurfaceBorder),
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text("Anggaran per malam", fontSize = 12.sp, color = LuxuryColors.TextMuted)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Rp ${budget.start.roundToInt()}jt \u2013 Rp ${budget.endInclusive.roundToInt()}jt",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                color = LuxuryColors.TextPrimary,
                            )
                        }
                        Text(
                            text = "0 \u2013 20jt",
                            fontSize = 11.sp,
                            color = LuxuryColors.TextMuted,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    RangeSlider(
                        value = budget,
                        onValueChange = { budget = it },
                        valueRange = 0f..20f,
                        onValueChangeFinished = {},
                        colors = SliderDefaults.colors(
                            thumbColor = LuxuryColors.TealPrimary,
                            activeTrackColor = LuxuryColors.TealPrimary,
                            inactiveTrackColor = LuxuryColors.SurfaceMuted,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))
            SectionLabel("FILTER CHIP")
            FlowChips(
                options = AllInterests,
                selected = interests,
                onToggle = { label ->
                    if (label in interests) interests.remove(label) else interests.add(label)
                },
            )

            Spacer(modifier = Modifier.height(26.dp))
            SectionLabel("OTP / PIN")
            OtpInput(
                value = otp,
                onValueChange = { otp = it },
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (showSuccess) {
                SuccessBanner()
                Spacer(modifier = Modifier.height(14.dp))
            }

            Surface(
                onClick = { submitted = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = LuxuryColors.TealPrimary,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
            ) {
                Text(
                    text = if (showSuccess) "Tersimpan" else "Simpan Perubahan",
                    modifier = Modifier.padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(bottom = 12.dp, start = 2.dp),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.6.sp,
        color = LuxuryColors.TextMuted,
    )
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isError: Boolean,
    errorText: String,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder, color = LuxuryColors.TextMuted) },
        isError = isError,
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next,
        ),
        supportingText = if (isError) {
            { Text(errorText, color = LuxuryColors.AccentDanger) }
        } else {
            null
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LuxuryColors.TealPrimary,
            unfocusedBorderColor = LuxuryColors.SurfaceBorder,
            errorBorderColor = LuxuryColors.AccentDanger,
            focusedLabelColor = LuxuryColors.TealPrimary,
            unfocusedLabelColor = LuxuryColors.TextSecondary,
            cursorColor = LuxuryColors.TealPrimary,
            focusedTextColor = LuxuryColors.TextPrimary,
            unfocusedTextColor = LuxuryColors.TextPrimary,
            focusedContainerColor = LuxuryColors.SurfaceWhite,
            unfocusedContainerColor = LuxuryColors.SurfaceWhite,
            errorContainerColor = LuxuryColors.SurfaceWhite,
        ),
    )
}

@Composable
private fun FlowChips(
    options: List<String>,
    selected: List<String>,
    onToggle: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        options.chunked(3).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowItems.forEach { option ->
                    val isSelected = option in selected
                    val background by animateColorAsState(
                        targetValue = if (isSelected) LuxuryColors.TealPrimary else LuxuryColors.SurfaceWhite,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "chipBg",
                    )
                    val border by animateColorAsState(
                        targetValue = if (isSelected) LuxuryColors.TealPrimary else LuxuryColors.SurfaceBorder,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "chipBorder",
                    )
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.04f else 1f,
                        animationSpec = spring(dampingRatio = 0.5f),
                        label = "chipScale",
                    )
                    Surface(
                        onClick = { onToggle(option) },
                        modifier = Modifier.graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = background,
                        border = BorderStroke(1.dp, border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                    ) {
                        Text(
                            text = option,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else LuxuryColors.TextSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OtpInput(value: String, onValueChange: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val activeIndex = value.length.coerceAtMost(5)

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            repeat(6) { index ->
                val digit = value.getOrNull(index)
                val isActive = index == activeIndex
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = LuxuryColors.SurfaceWhite,
                    border = BorderStroke(
                        if (isActive) 1.6.dp else 1.dp,
                        if (isActive) LuxuryColors.TealPrimary else LuxuryColors.SurfaceBorder,
                    ),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = digit?.toString() ?: "",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = LuxuryColors.TextPrimary,
                        )
                    }
                }
            }
        }

        BasicTextField(
            value = value,
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }
                onValueChange(digits.take(6))
            },
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done,
            ),
            singleLine = true,
        )
    }
}

@Composable
private fun SuccessBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = LuxuryColors.TealLight,
        border = BorderStroke(1.dp, LuxuryColors.Accent.copy(alpha = 0.3f)),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LuxuryColors.Accent),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "\u2713",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Data valid",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LuxuryColors.TealPrimary,
                )
                Text(
                    text = "Formulir siap dikirim.",
                    fontSize = 12.sp,
                    color = LuxuryColors.TextSecondary,
                )
            }
        }
    }
}
