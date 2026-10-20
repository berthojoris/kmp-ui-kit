package com.example.uiapp.ui.rulerpicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.FlatSecondaryButton
import com.example.uiapp.ui.components.FlatTextField
import com.example.uiapp.ui.components.FlatToggle
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

private const val MIN_CM = 30f
private const val MAX_CM = 260f

private enum class Satuan(val label: String) { CM("cm"), FT_IN("ft-in") }

private data class PresetUkuran(val id: String, val label: String, val cm: Float)

private val DaftarPreset: List<PresetUkuran> = listOf(
    PresetUkuran("p1", "Meja kerja 75 cm", 75f),
    PresetUkuran("p2", "Kursi 45 cm", 45f),
    PresetUkuran("p3", "Tinggi rata-rata 165 cm", 165f),
    PresetUkuran("p4", "Pintu 200 cm", 200f),
    PresetUkuran("p5", "Sofa 90 cm", 90f),
)

private fun formatDesimal(nilai: Float): String {
    val dibulatkan = (nilai * 10f).roundToInt()
    val bulat = dibulatkan / 10
    val sisa = dibulatkan % 10
    return if (sisa == 0) "$bulat" else "$bulat,$sisa"
}

private fun formatCm(nilaiCm: Float): String = "${formatDesimal(nilaiCm)} cm"

private fun formatFtIn(nilaiCm: Float): String {
    val totalIn = nilaiCm / 2.54f
    val ft = (totalIn / 12f).toInt()
    val inch = totalIn - ft * 12f
    return "$ft ft ${formatDesimal(inch)} in"
}

private fun labelTick(i: Int, satuan: Satuan): String = when (satuan) {
    Satuan.CM -> "$i"
    Satuan.FT_IN -> {
        val ft = i / 12
        val inch = i % 12
        if (inch == 0) "${ft}′" else "${ft}′${inch}″"
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun RulerPickerLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var nilaiCm by remember { mutableStateOf(172f) }
    var satuan by remember { mutableStateOf(Satuan.CM) }
    var terkunci by remember { mutableStateOf(false) }
    var hapticAktif by remember { mutableStateOf(true) }
    var hitungHaptic by remember { mutableIntStateOf(0) }
    var kilatHaptic by remember { mutableStateOf(false) }
    var inputTeks by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }
    var tersimpan by remember { mutableStateOf<String?>(null) }

    // Umpan haptic per tick disimulasikan sebagai kilau indikator + hitungan pemicu
    LaunchedEffect(hitungHaptic) {
        if (hitungHaptic > 0) {
            kilatHaptic = true
            delay(220)
            kilatHaptic = false
        }
    }

    val langkahCm = if (satuan == Satuan.CM) 1f else 2.54f
    val langkahBesar = if (satuan == Satuan.CM) 10 else 12
    val jarakTick = if (satuan == Satuan.CM) 18.dp else 30.dp
    val nilaiSatuan = nilaiCm / langkahCm

    fun pindahKe(target: Float, denganHaptic: Boolean = true) {
        val baru = target.coerceIn(MIN_CM, MAX_CM)
        val tickSebelum = (nilaiCm / langkahCm).roundToInt()
        val tickSesudah = (baru / langkahCm).roundToInt()
        nilaiCm = baru
        if (denganHaptic && hapticAktif && tickSesudah != tickSebelum) {
            hitungHaptic++
        }
    }

    fun geserLangkah(deltaTick: Int) {
        pindahKe(((nilaiCm / langkahCm).roundToInt() + deltaTick) * langkahCm)
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Ruler Picker Pengukuran",
                subtitle = "Tick, snapping, satuan cm & ft-in",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LabIntroCard(
                title = "Picker Pengukuran ala Aplikasi Kesehatan",
                body = "Ruler horizontal dengan tick besar tiap sepuluh dan tick kecil tiap satu, indikator tengah, " +
                    "snapping ke tick terdekat, serta umpan haptic per tick (Simulasi). Nilai cm dan ft-in saling " +
                    "mengonversi tanpa kehilangan presisi karena satu nilai kanonik yang sama.",
            )

            // Nilai besar + toggle satuan
            LabCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (satuan == Satuan.CM) formatCm(nilaiCm) else formatFtIn(nilaiCm),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = "≈ " + (
                                if (satuan == Satuan.CM) {
                                    formatFtIn(nilaiCm)
                                } else {
                                    formatCm(nilaiCm)
                                }
                                ),
                            fontSize = 13.sp,
                            color = palette.textSecondary,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(palette.surfaceMuted)
                            .border(1.dp, palette.border, RoundedCornerShape(50))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Satuan.entries.forEach { s ->
                            val aktifSatuan = s == satuan
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(if (aktifSatuan) palette.primary else palette.surface)
                                    .clickable(enabled = !terkunci) { satuan = s }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                                    .semantics {
                                        role = Role.Button
                                        contentDescription = "Tampilkan dalam satuan ${s.label}"
                                    },
                            ) {
                                Text(
                                    text = s.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (aktifSatuan) palette.onPrimary else palette.textPrimary,
                                )
                            }
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (kilatHaptic) palette.primary else palette.border),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hapticAktif) {
                            "Umpan haptic per tick · ${hitungHaptic} pemicu (Simulasi)"
                        } else {
                            "Umpan haptic dinonaktifkan"
                        },
                        fontSize = 11.sp,
                        color = palette.textMuted,
                        modifier = Modifier.weight(1f),
                    )
                    FlatToggle(
                        checked = hapticAktif,
                        onCheckedChange = { hapticAktif = it },
                        accessibilityLabel = "Umpan haptic per tick",
                    )
                }
            }

            // Ruler Canvas
            LabCard {
                Text(
                    text = "Ruler Pengukuran",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = palette.textPrimary,
                )
                Text(
                    text = "Geser ruler ke kiri/kanan — nilai snapping ke tick terdekat${if (terkunci) " · terkunci" else ""}",
                    fontSize = 11.sp,
                    color = palette.textMuted,
                )

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(116.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                        .alpha(if (terkunci) 0.5f else 1f)
                        .then(
                            if (terkunci) {
                                Modifier
                            } else {
                                Modifier.pointerInput(satuan) {
                                    detectHorizontalDragGestures { change, dragAmount ->
                                        change.consume()
                                        val pxPerCm = jarakTick.toPx() / langkahCm
                                        val raw = nilaiCm - dragAmount / pxPerCm
                                        val snapped = (raw / langkahCm).roundToInt() * langkahCm
                                        pindahKe(snapped)
                                    }
                                }
                            },
                        )
                        .semantics {
                            contentDescription = "Ruler pengukuran, geser horizontal untuk mengubah nilai. " +
                                "Nilai saat ini " + formatCm(nilaiCm) + " atau " + formatFtIn(nilaiCm)
                        },
                ) {
                    val pathIndikator = remember { Path() }

                    Canvas(modifier = Modifier.fillMaxWidth().height(116.dp)) {
                        val centerX = size.width / 2f
                        val pxPer = jarakTick.toPx()
                        val baselineY = size.height - 28.dp.toPx()

                        drawLine(
                            color = palette.border,
                            start = Offset(0f, baselineY),
                            end = Offset(size.width, baselineY),
                            strokeWidth = 2f,
                            cap = StrokeCap.Round,
                        )

                        val setengah = (size.width / 2f / pxPer).toInt() + 2
                        val idxMulai = floor(nilaiSatuan).toInt() - setengah
                        val idxAkhir = ceil(nilaiSatuan).toInt() + setengah
                        for (i in idxMulai..idxAkhir) {
                            if (i < 0) continue
                            val x = centerX + (i - nilaiSatuan) * pxPer
                            val besar = i % langkahBesar == 0
                            val tinggi = if (besar) 30.dp.toPx() else 16.dp.toPx()
                            drawLine(
                                color = if (besar) palette.textSecondary else palette.border,
                                start = Offset(x, baselineY),
                                end = Offset(x, baselineY - tinggi),
                                strokeWidth = if (besar) 3f else 2f,
                                cap = StrokeCap.Round,
                            )
                        }

                        // Indikator tengah
                        drawLine(
                            color = palette.primary,
                            start = Offset(centerX, 8.dp.toPx()),
                            end = Offset(centerX, baselineY + 10.dp.toPx()),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round,
                        )
                        pathIndikator.reset()
                        pathIndikator.moveTo(centerX - 7.dp.toPx(), 10.dp.toPx())
                        pathIndikator.lineTo(centerX + 7.dp.toPx(), 10.dp.toPx())
                        pathIndikator.lineTo(centerX, 22.dp.toPx())
                        pathIndikator.close()
                        drawPath(pathIndikator, color = palette.primary, style = Fill)
                    }

                    // Label angka tiap tick besar
                    val labelAwal = floor(nilaiSatuan).toInt() - 14
                    val labelAkhir = ceil(nilaiSatuan).toInt() + 14
                    for (i in labelAwal..labelAkhir) {
                        if (i <= 0 || i % langkahBesar != 0) continue
                        val dx = maxWidth / 2 + jarakTick * (i - nilaiSatuan)
                        Text(
                            text = labelTick(i, satuan),
                            fontSize = 10.sp,
                            color = palette.textMuted,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .width(44.dp)
                                .offset { IntOffset(dx.roundToPx() - 22.dp.roundToPx(), 0) },
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        onClick = { geserLangkah(-1) },
                        enabled = !terkunci && nilaiCm > MIN_CM,
                        shape = RoundedCornerShape(10.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        modifier = Modifier.semantics {
                            role = Role.Button
                            contentDescription = "Kurangi satu tick"
                        },
                    ) {
                        Text(
                            text = "− 1",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!terkunci && nilaiCm > MIN_CM) palette.textPrimary else palette.textMuted,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        )
                    }
                    Surface(
                        onClick = { geserLangkah(1) },
                        enabled = !terkunci && nilaiCm < MAX_CM,
                        shape = RoundedCornerShape(10.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        modifier = Modifier.semantics {
                            role = Role.Button
                            contentDescription = "Tambah satu tick"
                        },
                    ) {
                        Text(
                            text = "+ 1",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!terkunci && nilaiCm < MAX_CM) palette.textPrimary else palette.textMuted,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        )
                    }
                    Text(
                        text = when {
                            nilaiCm <= MIN_CM -> "Batas minimum 30 cm"
                            nilaiCm >= MAX_CM -> "Batas maksimum 260 cm"
                            else -> "Tick kecil 1 · tick besar ${if (satuan == Satuan.CM) "10 cm" else "1 ft"}"
                        },
                        fontSize = 11.sp,
                        color = palette.textMuted,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Preset cepat
            LabCard {
                Text(
                    text = "Preset Cepat",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = palette.textPrimary,
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DaftarPreset.forEach { preset ->
                        val presetAktif = nilaiCm == preset.cm
                        Box(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (presetAktif) palette.primaryContainer else palette.surface)
                                .border(
                                    1.dp,
                                    if (presetAktif) palette.primary else palette.border,
                                    RoundedCornerShape(50),
                                )
                                .clickable(enabled = !terkunci) { pindahKe(preset.cm) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .semantics {
                                    role = Role.Button
                                    contentDescription = "Preset ${preset.label}"
                                },
                        ) {
                            Text(
                                text = preset.label + if (presetAktif) " · dipilih" else "",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (presetAktif) palette.onPrimaryContainer else palette.textPrimary,
                            )
                        }
                    }
                }
            }

            // Input manual + simpan
            LabCard {
                Text(
                    text = "Input Manual & Penyimpanan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = palette.textPrimary,
                )
                FlatTextField(
                    label = "Ketik nilai dalam cm (30–260)",
                    value = inputTeks,
                    onValueChange = {
                        inputTeks = it
                        inputError = null
                    },
                    placeholder = "mis. 178 atau 178,5",
                    keyboardType = KeyboardType.Decimal,
                    error = inputError,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FlatSecondaryButton(
                        text = "Terapkan Nilai",
                        onClick = {
                            val parsed = inputTeks.replace(',', '.').toFloatOrNull()
                            when {
                                parsed == null -> {
                                    inputError = "Masukkan angka yang valid (mis. 178 atau 178,5)"
                                }
                                parsed < MIN_CM || parsed > MAX_CM -> {
                                    inputError = "Nilai harus di antara 30 cm dan 260 cm"
                                }
                                else -> {
                                    pindahKe(parsed)
                                    inputTeks = ""
                                    inputError = null
                                    tersimpan = null
                                }
                            }
                        },
                        enabled = !terkunci,
                        modifier = Modifier.weight(1f),
                    )
                    FlatSecondaryButton(
                        text = "Bersihkan",
                        onClick = {
                            inputTeks = ""
                            inputError = null
                        },
                        enabled = inputTeks.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                    )
                }
                FlatPrimaryButton(
                    text = "Simpan Pengukuran (Simulasi)",
                    onClick = {
                        val utama = if (satuan == Satuan.CM) formatCm(nilaiCm) else formatFtIn(nilaiCm)
                        val dua = if (satuan == Satuan.CM) formatFtIn(nilaiCm) else formatCm(nilaiCm)
                        tersimpan = "$utama ($dua)"
                    },
                    enabled = !terkunci,
                )
                if (tersimpan != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(palette.success.copy(alpha = 0.12f))
                            .border(1.dp, palette.success, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = "✓ Sukses: pengukuran tersimpan — $tersimpan (Simulasi)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary,
                        )
                    }
                }
            }

            // Kunci (state disabled)
            LabCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Kunci Pengukuran",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = if (terkunci) {
                                "Terkunci: ruler, preset, dan tombol nilai nonaktif (state disabled)"
                            } else {
                                "Aktifkan untuk memperlihatkan state disabled pada seluruh kontrol"
                            },
                            fontSize = 11.sp,
                            color = palette.textSecondary,
                        )
                    }
                    FlatToggle(
                        checked = terkunci,
                        onCheckedChange = { terkunci = it },
                        accessibilityLabel = "Kunci ruler",
                    )
                }
                if (terkunci) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(palette.warning.copy(alpha = 0.12f))
                            .border(1.dp, palette.warning, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = "Status: TERKUNCI — input dan tombol dinonaktifkan, nilai tetap terbaca",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary,
                        )
                    }
                }
            }

            // Catatan
            LabCard {
                Text(
                    text = "Manfaat Ruler Picker:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = palette.textPrimary,
                )
                Text(
                    text = "• Presisi cepat: geser panjang lebih cepat daripada mengetik angka",
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                )
                Text(
                    text = "• Konversi cm ↔ ft-in presisi dari satu nilai kanonik, tanpa pembulatan berlapis",
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                )
                Text(
                    text = "• Snapping + haptic per tick (Simulasi) memberi ritme saat menggeser",
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                )
                Text(
                    text = "• Melengkapi wheel picker di lab Advanced Input untuk pola tinggi/berat",
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
