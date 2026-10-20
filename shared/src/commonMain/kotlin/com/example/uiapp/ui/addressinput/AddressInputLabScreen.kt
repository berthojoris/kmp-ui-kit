package com.example.uiapp.ui.addressinput

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.HapticKind
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import androidx.compose.ui.text.AnnotatedString

private data class Country(val id: String, val name: String, val code: String, val flag: String, val digits: Int)

private val Countries = listOf(
    Country("id", "Indonesia", "+62", "ID", 12),
    Country("my", "Malaysia", "+60", "MY", 10),
    Country("sg", "Singapura", "+65", "SG", 8),
    Country("jp", "Jepang", "+81", "JP", 10),
    Country("us", "Amerika Serikat", "+1", "US", 10),
)

private data class PlaceSuggestion(val id: Int, val label: String, val street: String, val city: String, val postal: String)

private val Places = listOf(
    PlaceSuggestion(1, "Jalan Kebon Jeruk Raya No. 12", "Jalan Kebon Jeruk Raya No. 12", "Jakarta Barat", "11530"),
    PlaceSuggestion(2, "Jalan Kebon Sirih No. 45", "Jalan Kebon Sirih No. 45", "Jakarta Pusat", "10110"),
    PlaceSuggestion(3, "Jalan Kemang Raya Selatan I No. 8", "Jalan Kemang Raya Selatan I No. 8", "Jakarta Selatan", "12730"),
    PlaceSuggestion(4, "Jalan Surya Sumantri No. 91", "Jalan Surya Sumantri No. 91", "Bandung", "40164"),
    PlaceSuggestion(5, "Jalan Gajah Mada No. 3", "Jalan Gajah Mada No. 3", "Surabaya", "60271"),
    PlaceSuggestion(6, "Jalan Diponegoro No. 27", "Jalan Diponegoro No. 27", "Yogyakarta", "55223"),
)

private fun digitsOnly(s: String): String = s.filter { it.isDigit() }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddressInputLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current

    var countryId by remember { mutableStateOf("id") }
    var phone by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postal by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    var showCountries by remember { mutableStateOf(false) }

    val country = remember(countryId) { Countries.first { it.id == countryId } }
    val phoneDigits = remember(phone) { digitsOnly(phone) }
    val phoneError = when {
        phoneDigits.isEmpty() -> null
        phoneDigits.length < country.digits -> "Nomor belum lengkap (${phoneDigits.length}/${country.digits} digit)"
        else -> null
    }
    val suggestions = remember(query) {
        if (query.isBlank()) emptyList() else Places.filter { it.label.contains(query.trim(), ignoreCase = true) }
    }
    val addressError = when {
        query.isBlank() -> null
        street.isNotEmpty() -> null // sudah dipilih dari saran
        suggestions.isEmpty() -> "Alamat tidak ditemukan — coba kata kunci lain"
        else -> null
    }
    val formValid = phoneDigits.length >= country.digits && street.isNotEmpty() && city.isNotEmpty() && postal.length >= 4

    Column(Modifier.fillMaxSize()) {
        Scaffold(            containerColor = palette.background,
            topBar = { UiTopBar(title = "Address Input", subtitle = "Telepon & alamat otomatis — simulasi", onBack = onBack) },
        ) { inner ->
            Column(
                Modifier.fillMaxSize().padding(inner).verticalScroll(rememberScrollState()).padding(16.dp).imePadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                LabIntroCard(title = "Telepon & Autocomplete Alamat", body = "Ketik awalan alamat untuk melihat saran (mis. \"kebon\"). Memilih saran mengisi jalan, kota, dan kode pos sekaligus. Tanpa layanan peta nyata — data mock lokal.")

                LabSectionTitle(title = "Nomor Telepon", subtitle = "Kode negara + format lokal")
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surface)
                        .heightIn(min = 48.dp)
                        .border(1.dp, if (phoneError != null) palette.danger else palette.border, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${country.flag} ${country.code}",
                        fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(role = Role.Button) { showCountries = !showCountries }
                            .semantics { contentDescription = "Kode negara ${country.name} ${country.code}, ketuk untuk mengubah" }
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                    )
                    Box(Modifier.weight(1f).padding(start = 8.dp)) {
                        if (phoneDigits.isEmpty()) {
                            Text("81234567890", fontSize = 14.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        BasicTextField(
                            value = phoneDigits,
                            onValueChange = { phone = digitsOnly(it).take(country.digits) },
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 14.sp, color = palette.textPrimary, fontWeight = FontWeight.Medium),
                            cursorBrush = SolidColor(palette.primary),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Nomor telepon" },
                        )
                    }
                    if (phoneDigits.length >= country.digits) {
                        StatusPill(text = "Valid", color = palette.success)
                    }
                }
                if (phoneError != null) Text(phoneError, fontSize = 11.sp, color = palette.danger, modifier = Modifier.padding(start = 2.dp))

                if (showCountries) {
                    Surface(shape = RoundedCornerShape(12.dp), color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                        Column {
                            Countries.forEach { c ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable(role = Role.Button) {
                                            countryId = c.id; phone = phoneDigits.take(c.digits); showCountries = false
                                            PerformHapticFeedback(HapticKind.SELECTION)
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text("${c.flag}  ${c.name}", fontSize = 13.sp, color = palette.textPrimary)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(c.code, fontSize = 13.sp, color = palette.textSecondary)
                                        if (c.id == countryId) StatusPill(text = "Dipilih", color = palette.primary)
                                    }
                                }
                            }
                        }
                    }
                }

                LabSectionTitle(title = "Alamat", subtitle = "Saran muncul saat mengetik")
                FlatQueryField(
                    value = query,
                    onChange = {
                        query = it; street = ""; city = ""; postal = ""
                    },
                )
                if (addressError != null) Text(addressError, fontSize = 11.sp, color = palette.danger, modifier = Modifier.padding(start = 2.dp))

                if (query.isNotBlank() && street.isEmpty()) {
                    if (suggestions.isEmpty()) {
                        Surface(shape = RoundedCornerShape(12.dp), color = palette.surfaceMuted, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                            Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Tidak ada saran", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                                Text("Coba \"jalan\", \"kebon\", atau \"kemang\".", fontSize = 11.sp, color = palette.textSecondary)
                            }
                        }
                    } else {
                        Surface(shape = RoundedCornerShape(12.dp), color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                            Column {
                                Text(
                                    "${suggestions.size} saran ditemukan",
                                    fontSize = 10.sp, color = palette.textMuted,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                )
                                suggestions.forEach { s ->
                                    val hl = remember(query, s.id) { Highlight.build(s.label, query.trim()) }
                                    Text(
                                        hl,
                                        fontSize = 13.sp, color = palette.textPrimary,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(role = Role.Button) {
                                                query = s.label; street = s.street; city = s.city; postal = s.postal
                                                PerformHapticFeedback(HapticKind.MEDIUM)
                                            }
                                            .semantics { contentDescription = "Pilih alamat ${s.label}" }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                LabSectionTitle(title = "Detail Alamat", subtitle = "Terkisi otomatis dari saran")
                ReadOnlyField(label = "Jalan", value = street)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) { ReadOnlyField(label = "Kota", value = city) }
                    Box(Modifier.weight(1f)) { ReadOnlyField(label = "Kode Pos", value = postal) }
                }

                if (submitted) {
                    Surface(shape = RoundedCornerShape(12.dp), color = palette.success.copy(alpha = 0.12f), border = BorderStroke(1.dp, palette.success), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                        Text(
                            "Alamat tersimpan: ${street}, ${city} ${postal}",
                            fontSize = 12.sp, color = palette.textPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(12.dp),
                        )
                    }
                } else {
                    Text(
                        if (formValid) "Semua bidang valid" else "Lengkapi telepon & pilih alamat dari saran",
                        fontSize = 11.sp, color = if (formValid) palette.success else palette.textMuted,
                    )
                    FlatPrimaryButton(text = "Simpan Alamat", onClick = { submitted = true; PerformHapticFeedback(HapticKind.SUCCESS) }, enabled = formValid)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

private object Highlight {
    fun build(text: String, query: String): AnnotatedString = buildAnnotatedString {
        val idx = text.indexOf(query, ignoreCase = true)
        if (query.isEmpty() || idx < 0) {
            append(text); return@buildAnnotatedString
        }
        append(text.substring(0, idx))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(text.substring(idx, idx + query.length)) }
        append(text.substring(idx + query.length))
    }
}

@Composable
private fun FlatQueryField(value: String, onChange: (String) -> Unit) {
    val palette = LocalAppPalette.current
    Column {
        Text("Cari Alamat", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary, modifier = Modifier.padding(bottom = 7.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(palette.surface)
                .heightIn(min = 48.dp)
                .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.weight(1f)) {
                if (value.isEmpty()) Text("Ketik: kebon, kemang, surya…", fontSize = 14.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                BasicTextField(
                    value = value,
                    onValueChange = onChange,
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, color = palette.textPrimary, fontWeight = FontWeight.Medium),
                    cursorBrush = SolidColor(palette.primary),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Kolom cari alamat" },
                )
            }
        }
    }
}

@Composable
private fun ReadOnlyField(label: String, value: String) {
    val palette = LocalAppPalette.current
    val filled = value.isNotEmpty()
    Column(Modifier.fillMaxWidth()) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary, modifier = Modifier.padding(bottom = 7.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (filled) palette.surface else palette.surfaceMuted)
                .heightIn(min = 48.dp)
                .border(BorderStroke(1.dp, if (filled) palette.border else palette.border.copy(alpha = 0.6f)), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                if (value.isEmpty()) "—" else value,
                fontSize = 14.sp,
                color = if (filled) palette.textPrimary else palette.textMuted,
                fontWeight = if (filled) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.semantics { contentDescription = "$label: ${if (value.isEmpty()) "belum terisi" else value}" },
            )
        }
    }
}
