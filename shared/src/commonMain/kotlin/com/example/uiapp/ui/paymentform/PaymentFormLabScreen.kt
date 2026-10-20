package com.example.uiapp.ui.paymentform

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.HapticKind
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.FlatSecondaryButton
import com.example.uiapp.ui.components.FlatTextField
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

// ponytail: expiry check memakai bulan/tahun statis demo (10/2026); ganti ke tanggal sistem saat dipakai produksi.
private const val DemoNowMonth = 10
private const val DemoNowYear = 2026

private enum class CardBrand(val label: String) { VISA("Visa"), MASTERCARD("Mastercard"), AMEX("Amex"), UNKNOWN("Tidak dikenal") }

private fun brandOf(digits: String): CardBrand = when {
    digits.startsWith("4") -> CardBrand.VISA
    digits.startsWith("5") || digits.startsWith("2") -> CardBrand.MASTERCARD
    digits.startsWith("34") || digits.startsWith("37") -> CardBrand.AMEX
    digits.length >= 1 -> CardBrand.UNKNOWN
    else -> CardBrand.UNKNOWN
}

private fun groupNumber(digits: String): String {
    val groups = if (brandOf(digits) == CardBrand.AMEX) listOf(4, 6, 5) else listOf(4, 4, 4, 4)
    val sb = StringBuilder()
    var i = 0
    for (g in groups) {
        if (i >= digits.length) break
        if (sb.isNotEmpty()) sb.append(' ')
        sb.append(digits.substring(i, minOf(i + g, digits.length)))
        i += g
    }
    return sb.toString()
}

private fun digitsOnly(s: String): String = s.filter { it.isDigit() }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PaymentFormLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current

    var number by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }
    var manualFlip by remember { mutableStateOf(false) }
    var scanning by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }

    val digits = remember(number) { digitsOnly(number) }
    val brand = remember(digits) { brandOf(digits) }
    val expectedLen = if (brand == CardBrand.AMEX) 15 else 16

    val numberError = when {
        number.isEmpty() -> null
        brand == CardBrand.UNKNOWN -> "Nomor BIN tidak dikenali"
        digits.length < expectedLen -> "Nomor belum lengkap (${digits.length}/$expectedLen)"
        else -> null
    }
    val nameError = if (name.isEmpty()) null else if (name.length < 3) "Nama minimal 3 karakter" else null
    val expParts = digitsOnly(expiry).let { if (it.length >= 3) it.substring(0, 2) to it.substring(2).take(2) else "" to "" }
    val expiryError = when {
        expiry.isEmpty() -> null
        expParts.first.toIntOrNull() !in 1..12 -> "Bulan tidak valid (01–12)"
        expParts.second.length < 2 -> "Tahun belum lengkap"
        else -> {
            val mm = expParts.first.toInt()
            val yy = 2000 + expParts.second.toInt()
            if (yy < DemoNowYear || (yy == DemoNowYear && mm < DemoNowMonth)) "Kartu kedaluwarsa" else null
        }
    }
    val cvcError = when {
        cvc.isEmpty() -> null
        digitsOnly(cvc).length < 3 -> "CVC minimal 3 digit"
        else -> null
    }
    val formValid = listOf(numberError, nameError, expiryError, cvcError).all { it == null } &&
        digits.length == expectedLen && name.length >= 3 && digitsOnly(expiry).length == 4 && digitsOnly(cvc).length >= 3

    // flip otomatis saat CVC sedang diisi (belum 3 digit) + toggle manual dengan ketukan
    val flipped = manualFlip xor (digitsOnly(cvc).length in 1..2)
    val flip by animateFloatAsState(if (flipped) 180f else 0f, tween(400), label = "cardFlip")

    LaunchedEffect(scanning) {
        if (scanning) {
            delay(1200)
            number = "4000 1234 5678 9010"; name = "PUTRI MELATI"; expiry = "09/29"; cvc = "123"
            scanning = false
            PerformHapticFeedback(HapticKind.SUCCESS)
        }
    }
    LaunchedEffect(saving) {
        if (saving) { delay(1000); saving = false; saved = true; PerformHapticFeedback(HapticKind.SUCCESS) }
    }

    Scaffold(containerColor = palette.background, topBar = { UiTopBar(title = "Payment Form", subtitle = "Form kartu & kartu 3D — simulasi", onBack = onBack) }) { inner ->
        Column(Modifier.fillMaxSize().padding(inner).verticalScroll(rememberScrollState()).padding(16.dp).imePadding(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            LabIntroCard(title = " Kartu Kredit & Pratinjau 3D", body = "Pratinjau kartu berputar ke belakang saat CVC difokuskan. Deteksi jenis kartu dari awalan nomor (BIN). Seluruh pembayaran adalah simulasi.")

            CardFaces(digits, name, digitsOnly(expiry), digitsOnly(cvc), brand, flip, onToggle = { manualFlip = !manualFlip; PerformHapticFeedback(HapticKind.SELECTION) })

            if (saved) {
                Surface(shape = RoundedCornerShape(12.dp), color = palette.success.copy(alpha = 0.12f), border = BorderStroke(1.dp, palette.success), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Kartu berhasil disimpan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.success)
                        Text("${brand.label} •••• ${digits.takeLast(4)}", fontSize = 12.sp, color = palette.textSecondary)
                        FlatSecondaryButton(text = "Simpan kartu lain", onClick = { saved = false; number = ""; name = ""; expiry = ""; cvc = "" })
                    }
                }
            } else {
                FlatSecondaryButton(
                    text = if (scanning) "Memindai kartu…" else "Pindai Kartu (Simulasi)",
                    onClick = { if (!scanning) { scanning = true; PerformHapticFeedback(HapticKind.MEDIUM) } },
                    enabled = !scanning,
                )
                FlatTextField(
                    label = "Nomor Kartu", value = groupNumber(digits),
                    onValueChange = { number = digitsOnly(it).take(expectedLen) },
                    placeholder = if (brand == CardBrand.AMEX) "3782 822463 10005" else "4000 1234 5678 9010",
                    error = numberError, keyboardType = KeyboardType.Number,
                )
                FlatTextField(label = "Nama di Kartu", value = name, onValueChange = { name = it.uppercase().take(26) }, placeholder = "PUTRI MELATI", error = nameError)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) {
                        FlatTextField(
                            label = "Berlaku (MM/YY)", value = if (digitsOnly(expiry).length >= 3) "${expParts.first}/${expParts.second}" else digitsOnly(expiry),
                            onValueChange = { v -> val d = digitsOnly(v).take(4); expiry = if (d.length >= 3) "${d.substring(0, 2)}/${d.substring(2)}" else d },
                            placeholder = "09/29", error = expiryError, keyboardType = KeyboardType.Number,
                        )
                    }
                    Box(Modifier.weight(1f)) {
                        FlatTextField(
                            label = "CVC", value = cvc, onValueChange = { cvc = digitsOnly(it).take(4) },
                            placeholder = "123", error = cvcError, keyboardType = KeyboardType.Number,
                            isPassword = true, imeAction = ImeAction.Done,
                        )
                    }
                }
                Text(
                    text = if (!formValid) "Simpan aktif setelah semua bidang valid" else "Semua bidang valid",
                    fontSize = 11.sp, color = if (formValid) palette.success else palette.textMuted,
                )
                FlatPrimaryButton(text = if (saving) "Menyimpan…" else "Simpan Kartu", onClick = { if (formValid) saving = true }, enabled = formValid && !saving)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CardFaces(digits: String, name: String, expiryDigits: String, cvc: String, brand: CardBrand, flip: Float, onToggle: () -> Unit) {
    val palette = LocalAppPalette.current
    val gradient = remember {
        Brush.linearGradient(listOf(Color(0xFF1F5C4E), Color(0xFF2E8B74), Color(0xFF173F36)))
    }
    val shownDigits = remember(digits) {
        buildString {
            for (g in 0 until 4) {
                if (g > 0) append(' ')
                for (i in g * 4 until g * 4 + 4) {
                    if (i < digits.length) append(digits[i]) else append('•')
                }
            }
        }
    }
    val showingBack = flip > 90f

    Box(
        Modifier
            .fillMaxWidth()
            .height(200.dp)
            .graphicsLayer { rotationY = flip; cameraDistance = 12f * density }
            .clip(RoundedCornerShape(18.dp))
            .background(gradient)
            .border(1.dp, palette.border, RoundedCornerShape(18.dp))
            .clickable(role = Role.Button, onClickLabel = "Balik kartu", onClick = onToggle)
            .semantics { contentDescription = if (showingBack) "Sisi belakang kartu, ketuk untuk melihat depan" else "Pratinjau kartu, ketuk untuk melihat belakang" },
    ) {
        if (!showingBack) {
            Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(brand.label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE8FFF7))
                    StatusPill(text = "Simulasi", color = palette.warning)
                }
                Text(shownDigits, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.5.sp, color = Color(0xFFE8FFF7))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("NAMA", fontSize = 9.sp, color = Color(0xFFBDE8DC))
                        Text(if (name.isEmpty()) "NAMA DI KARTU" else name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFE8FFF7), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("BERLAKU", fontSize = 9.sp, color = Color(0xFFBDE8DC))
                        Text(if (expiryDigits.length == 4) "${expiryDigits.substring(0, 2)}/${expiryDigits.substring(2)}" else "••/••", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFE8FFF7))
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                Spacer(Modifier.height(20.dp))
                Box(Modifier.fillMaxWidth().height(40.dp).background(Color(0xFF10231F)))
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.weight(1f).height(30.dp).background(Color(0xFFE8FFF7), RoundedCornerShape(6.dp)))
                        Text(if (cvc.isEmpty()) "•••" else cvc, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10231F), modifier = Modifier.background(palette.surface, RoundedCornerShape(6.dp)).padding(horizontal = 10.dp, vertical = 6.dp))
                    }
                    Text("CVC — 3 digit di belakang kartu", fontSize = 10.sp, color = Color(0xFFBDE8DC))
                }
            }
        }
    }
}
