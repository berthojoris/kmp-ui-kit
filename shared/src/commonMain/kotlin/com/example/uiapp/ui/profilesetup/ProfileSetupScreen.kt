package com.example.uiapp.ui.profilesetup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.FlatSecondaryButton
import com.example.uiapp.ui.components.FlatTextField
import com.example.uiapp.ui.components.FlatToggle
import com.example.uiapp.ui.components.UiTopBar

private const val TotalSteps = 3

private val Interests = listOf(
    "Pantai", "Pegunungan", "Kuliner", "Budaya", "Belanja", "Alam", "Hiburan malam", "Keluarga",
)

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupScreen(onBack: () -> Unit, onFinish: () -> Unit) {
    val palette = LocalAppPalette.current

    var step by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    val interests = remember { mutableStateListOf<String>() }
    var emailNotif by remember { mutableStateOf(true) }
    var promoNotif by remember { mutableStateOf(false) }
    var locationAccess by remember { mutableStateOf(true) }

    BackHandler(enabled = true) {
        if (step == 0) onBack() else step -= 1
    }

    fun canContinue(): Boolean = when (step) {
        0 -> name.trim().length >= 2
        1 -> interests.isNotEmpty()
        else -> true
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Profil",
                subtitle = "Profile setup wizard",
                onBack = {
                    if (step == 0) onBack() else step -= 1
                },
            )
        },
        bottomBar = {
            Surface(
                color = palette.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(palette.border),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (step > 0) {
                            Box(modifier = Modifier.weight(1f)) {
                                FlatSecondaryButton(text = "Kembali", onClick = { step -= 1 })
                            }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            FlatPrimaryButton(
                                text = if (step == TotalSteps - 1) "Selesai" else "Lanjut",
                                enabled = canContinue(),
                                onClick = {
                                    if (step == TotalSteps - 1) onFinish() else step += 1
                                },
                            )
                        }
                    }
                }
            }
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

            StepProgress(step = step)

            Spacer(modifier = Modifier.height(24.dp))

            when (step) {
                0 -> StepIdentity(
                    name = name,
                    onNameChange = { name = it },
                    bio = bio,
                    onBioChange = { bio = it },
                )

                1 -> StepInterests(
                    interests = Interests,
                    selected = interests,
                    onToggle = { interest ->
                        if (interest in interests) interests.remove(interest) else interests.add(interest)
                    },
                )

                else -> StepPreferences(
                    emailNotif = emailNotif,
                    onEmailNotif = { emailNotif = it },
                    promoNotif = promoNotif,
                    onPromoNotif = { promoNotif = it },
                    locationAccess = locationAccess,
                    onLocationAccess = { locationAccess = it },
                    name = name.ifBlank { "Ayu" },
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun StepProgress(step: Int) {
    val palette = LocalAppPalette.current

    Column {
        Text(
            text = "Langkah ${step + 1} dari $TotalSteps",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textMuted,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(TotalSteps) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            if (index <= step) palette.primary
                            else palette.surfaceMuted,
                        ),
                )
            }
        }
    }
}

@Composable
private fun StepIdentity(
    name: String,
    onNameChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
) {
    val palette = LocalAppPalette.current

    Column {
        StepTitle(
            title = "Perkenalkan diri",
            subtitle = "Unggah foto dan isi nama tampilan Anda.",
        )
        Spacer(modifier = Modifier.height(22.dp))

        Box(
            modifier = Modifier
                .size(96.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(palette.surfaceMuted)
                    .border(1.dp, palette.border, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = name.trim().take(1).ifBlank { "?" },
                    fontFamily = FontFamily.Serif,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(palette.primary)
                    .border(2.dp, palette.surface, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(15.dp)) {
                    val w = size.width
                    val h = size.height
                    val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(w * 0.1f, h * 0.3f),
                        size = Size(w * 0.8f, h * 0.5f),
                        cornerRadius = CornerRadius(w * 0.14f, w * 0.14f),
                        style = stroke,
                    )
                    drawCircle(
                        color = Color.White,
                        radius = w * 0.14f,
                        center = Offset(w * 0.5f, h * 0.55f),
                        style = stroke,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        FlatTextField(
            label = "Nama tampilan",
            value = name,
            onValueChange = onNameChange,
            placeholder = "Ayu Prameswari",
        )
        Spacer(modifier = Modifier.height(16.dp))
        FlatTextField(
            label = "Bio singkat",
            value = bio,
            onValueChange = onBioChange,
            placeholder = "Pencinta pantai dan kopi",
            imeAction = androidx.compose.ui.text.input.ImeAction.Done,
        )
    }
}

@Composable
private fun StepInterests(
    interests: List<String>,
    selected: List<String>,
    onToggle: (String) -> Unit,
) {
    val palette = LocalAppPalette.current

    Column {
        StepTitle(
            title = "Pilih minat Anda",
            subtitle = "Minimal satu, agar rekomendasi lebih relevan.",
        )
        Spacer(modifier = Modifier.height(22.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            interests.forEach { interest ->
                val active = interest in selected
                Surface(
                    onClick = { onToggle(interest) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (active) palette.primary else palette.surface,
                    border = BorderStroke(
                        1.dp,
                        if (active) palette.primary else palette.border,
                    ),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Text(
                        text = interest,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (active) Color.White else palette.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun StepPreferences(
    emailNotif: Boolean,
    onEmailNotif: (Boolean) -> Unit,
    promoNotif: Boolean,
    onPromoNotif: (Boolean) -> Unit,
    locationAccess: Boolean,
    onLocationAccess: (Boolean) -> Unit,
    name: String,
) {
    val palette = LocalAppPalette.current

    Column {
        StepTitle(
            title = "Atur preferensi",
            subtitle = "Ubah kapan saja dari pengaturan.",
        )
        Spacer(modifier = Modifier.height(22.dp))

        PreferenceRow(
            label = "Notifikasi email",
            description = "Ringkasan pesanan dan pengingat jadwal",
            checked = emailNotif,
            onCheckedChange = onEmailNotif,
        )
        Spacer(modifier = Modifier.height(10.dp))
        PreferenceRow(
            label = "Promo & penawaran",
            description = "Diskon dan rekomendasi spesial",
            checked = promoNotif,
            onCheckedChange = onPromoNotif,
        )
        Spacer(modifier = Modifier.height(10.dp))
        PreferenceRow(
            label = "Akses lokasi",
            description = "Properti terdekat dan estimasi jarak",
            checked = locationAccess,
            onCheckedChange = onLocationAccess,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = palette.primaryContainer,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Canvas(modifier = Modifier.size(26.dp)) {
                    drawCircle(
                        color = palette.primary,
                        radius = size.minDimension * 0.42f,
                        style = Stroke(width = 2.dp.toPx()),
                    )
                    drawLine(
                        color = palette.primary,
                        start = Offset(size.width * 0.32f, size.height * 0.42f),
                        end = Offset(size.width * 0.46f, size.height * 0.56f),
                        strokeWidth = 2.4.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = palette.primary,
                        start = Offset(size.width * 0.46f, size.height * 0.56f),
                        end = Offset(size.width * 0.7f, size.height * 0.3f),
                        strokeWidth = 2.4.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Profil $name siap dibuat.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = palette.primary,
                )
            }
        }
    }
}

@Composable
private fun PreferenceRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val palette = LocalAppPalette.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = palette.textMuted,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            FlatToggle(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun StepTitle(title: String, subtitle: String) {
    val palette = LocalAppPalette.current

    Column {
        Text(
            text = title,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            letterSpacing = (-0.3).sp,
            color = palette.textPrimary,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = palette.textSecondary,
        )
    }
}
