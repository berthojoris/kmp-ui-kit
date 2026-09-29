package com.example.uiapp.ui.resumeform

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MerchantFormDraft(
    var businessName: String = "PT Teknologi Cipta Bersama",
    var businessType: String = "Teknologi & SaaS",
    var email: String = "admin@ciptatech.id",
    var phone: String = "+62 812-3456-7890",
    var address: String = "Gedung Cyber 2 Lantai 15, Jakarta Selatan",
    var taxId: String = "09.123.456.7-012.000",
    var bankAccount: String = "8821093120 - Bank Mandiri",
    var currentStep: Int = 1,
    var lastSavedTime: String = "15:30",
)

enum class AutosaveStatus(val label: String, val color: Color) {
    SAVED("Tersimpan Otomatis", Color(0xFF10B981)),
    SAVING("Menyimpan Draft...", Color(0xFFF59E0B)),
    ERROR("Gagal Menyimpan", Color(0xFFEF4444)),
}

@Composable
fun ResumeFormLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()

    var formDraft by remember { mutableStateOf(MerchantFormDraft()) }
    var autosaveStatus by remember { mutableStateOf(AutosaveStatus.SAVED) }
    var showDraftPrompt by remember { mutableStateOf(true) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }

    fun triggerAutosave() {
        autosaveStatus = AutosaveStatus.SAVING
        scope.launch {
            delay(600)
            formDraft = formDraft.copy(lastSavedTime = "15:44")
            autosaveStatus = AutosaveStatus.SAVED
        }
    }

    fun discardDraft() {
        formDraft = MerchantFormDraft(
            businessName = "",
            businessType = "",
            email = "",
            phone = "",
            address = "",
            taxId = "",
            bankAccount = "",
            currentStep = 1,
            lastSavedTime = "Baru saja",
        )
        showDiscardDialog = false
        showDraftPrompt = false
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Save & Resume Form",
                subtitle = "Autosave draft & pemulihan formulir",
                onBack = onBack,
                action = {
                    Text(
                        text = "Buang Draft",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.danger,
                        modifier = Modifier.clickable { showDiscardDialog = true },
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
        ) {
            // Restore Draft Prompt Banner
            AnimatedVisibility(visible = showDraftPrompt) {
                Surface(
                    shape = RoundedCornerShape(0.dp),
                    color = palette.primaryContainer,
                    border = BorderStroke(1.dp, palette.primary),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Draft Formulir Ditemukan",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.onPrimaryContainer,
                            )
                            Text(
                                text = "Tersimpan otomatis terakhir pukul ${formDraft.lastSavedTime}.",
                                fontSize = 11.sp,
                                color = palette.onPrimaryContainer.copy(alpha = 0.8f),
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                onClick = { showDraftPrompt = false },
                                shape = RoundedCornerShape(6.dp),
                                color = palette.primary,
                                border = BorderStroke(1.dp, palette.primary),
                            ) {
                                Text(
                                    text = "Lanjutkan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.onPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                )
                            }
                            Text(
                                text = "Mulai Baru",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.onPrimaryContainer,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .clickable { discardDraft() },
                            )
                        }
                    }
                }
            }

            // Autosave Status Bar & Step Indicator
            Surface(
                shape = RoundedCornerShape(0.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Langkah ${formDraft.currentStep} dari 4: ${
                                when (formDraft.currentStep) {
                                    1 -> "Profil Usaha"
                                    2 -> "Kontak & Alamat"
                                    3 -> "Pajak & Rekening"
                                    else -> "Ringkasan & Konfirmasi"
                                }
                            }",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                        )

                        // Autosave Status Pill
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(autosaveStatus.color),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = autosaveStatus.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = autosaveStatus.color,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Step Progress Dots
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        for (step in 1..4) {
                            val isCompleted = step < formDraft.currentStep
                            val isCurrent = step == formDraft.currentStep
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        when {
                                            isCompleted -> palette.primary
                                            isCurrent -> palette.primary.copy(alpha = 0.5f)
                                            else -> palette.surfaceMuted
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        if (isCompleted || isCurrent) palette.primary else palette.border,
                                        RoundedCornerShape(2.dp)
                                    ),
                            )
                        }
                    }
                }
            }

            // Form Body per Step
            if (isSubmitted) {
                // Submission Success Card
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        shadowElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text("🎉", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Pengajuan Berhasil Dikirim!",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Data ${formDraft.businessName} telah tersimpan di sistem terpusat.",
                                fontSize = 13.sp,
                                color = palette.textSecondary,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(
                                onClick = {
                                    isSubmitted = false
                                    formDraft = formDraft.copy(currentStep = 1)
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = palette.primary,
                                border = BorderStroke(1.dp, palette.primary),
                            ) {
                                Text(
                                    text = "Ubah Data Formulir",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.onPrimary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                )
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    when (formDraft.currentStep) {
                        1 -> {
                            item {
                                FormInputField(
                                    label = "Nama Badan Usaha / Brand",
                                    value = formDraft.businessName,
                                    placeholder = "Contoh: PT Harapan Jaya",
                                    palette = palette,
                                    onValueChange = {
                                        formDraft = formDraft.copy(businessName = it)
                                        triggerAutosave()
                                    },
                                )
                            }
                            item {
                                FormInputField(
                                    label = "Kategori Bisnis & Industri",
                                    value = formDraft.businessType,
                                    placeholder = "Contoh: E-Commerce / Retail",
                                    palette = palette,
                                    onValueChange = {
                                        formDraft = formDraft.copy(businessType = it)
                                        triggerAutosave()
                                    },
                                )
                            }
                        }

                        2 -> {
                            item {
                                FormInputField(
                                    label = "Email Korespondensi",
                                    value = formDraft.email,
                                    placeholder = "admin@usaha.com",
                                    palette = palette,
                                    onValueChange = {
                                        formDraft = formDraft.copy(email = it)
                                        triggerAutosave()
                                    },
                                )
                            }
                            item {
                                FormInputField(
                                    label = "Nomor Telepon Kantor / WA",
                                    value = formDraft.phone,
                                    placeholder = "+62 812...",
                                    palette = palette,
                                    onValueChange = {
                                        formDraft = formDraft.copy(phone = it)
                                        triggerAutosave()
                                    },
                                )
                            }
                            item {
                                FormInputField(
                                    label = "Alamat Domisili Perusahaan",
                                    value = formDraft.address,
                                    placeholder = "Jalan, Gedung, Kota",
                                    palette = palette,
                                    onValueChange = {
                                        formDraft = formDraft.copy(address = it)
                                        triggerAutosave()
                                    },
                                )
                            }
                        }

                        3 -> {
                            item {
                                FormInputField(
                                    label = "NPWP Perusahaan (16 Digit)",
                                    value = formDraft.taxId,
                                    placeholder = "00.000.000.0-000.000",
                                    palette = palette,
                                    onValueChange = {
                                        formDraft = formDraft.copy(taxId = it)
                                        triggerAutosave()
                                    },
                                )
                            }
                            item {
                                FormInputField(
                                    label = "Rekening Bank Pencairan Dana",
                                    value = formDraft.bankAccount,
                                    placeholder = "Nomor Rekening - Nama Bank",
                                    palette = palette,
                                    onValueChange = {
                                        formDraft = formDraft.copy(bankAccount = it)
                                        triggerAutosave()
                                    },
                                )
                            }
                        }

                        4 -> {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = palette.surface,
                                    border = BorderStroke(1.dp, palette.border),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "Ringkasan Sebelum Kirim",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.textPrimary,
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        SummaryRow("Nama Usaha", formDraft.businessName, palette)
                                        SummaryRow("Kategori", formDraft.businessType, palette)
                                        SummaryRow("Email", formDraft.email, palette)
                                        SummaryRow("Telepon", formDraft.phone, palette)
                                        SummaryRow("NPWP", formDraft.taxId, palette)
                                        SummaryRow("Rekening", formDraft.bankAccount, palette)
                                    }
                                }
                            }
                        }
                    }
                }

                // Step Navigation Controls
                Surface(
                    shape = RoundedCornerShape(0.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (formDraft.currentStep > 1) {
                            Surface(
                                onClick = {
                                    formDraft = formDraft.copy(currentStep = formDraft.currentStep - 1)
                                    triggerAutosave()
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = palette.surfaceMuted,
                                border = BorderStroke(1.dp, palette.border),
                            ) {
                                Text(
                                    text = "← Sebelumnya",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = palette.textPrimary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        Surface(
                            onClick = {
                                if (formDraft.currentStep < 4) {
                                    formDraft = formDraft.copy(currentStep = formDraft.currentStep + 1)
                                    triggerAutosave()
                                } else {
                                    isSubmitted = true
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = palette.primary,
                            border = BorderStroke(1.dp, palette.primary),
                        ) {
                            Text(
                                text = if (formDraft.currentStep < 4) "Lanjutkan →" else "Kirim Formulir",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.onPrimary,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                            )
                        }
                    }
                }
            }

            // Discard Confirmation Dialog
            if (showDiscardDialog) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { showDiscardDialog = false },
                    color = Color.Transparent,
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = palette.surface,
                            border = BorderStroke(1.dp, palette.danger),
                            shadowElevation = 0.dp,
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .clickable(enabled = false) {},
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Buang Seluruh Draft?",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Data formulir yang sudah tersimpan otomatis akan dibersihkan secara permanen.",
                                    fontSize = 13.sp,
                                    color = palette.textSecondary,
                                    lineHeight = 18.sp,
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "Batal",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.textMuted,
                                        modifier = Modifier.clickable { showDiscardDialog = false },
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Surface(
                                        onClick = { discardDraft() },
                                        shape = RoundedCornerShape(8.dp),
                                        color = palette.danger,
                                        border = BorderStroke(1.dp, palette.danger),
                                    ) {
                                        Text(
                                            text = "Buang Data",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormInputField(
    label: String,
    value: String,
    placeholder: String,
    palette: com.example.uiapp.theme.AppPalette,
    onValueChange: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textPrimary,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = 0.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                textStyle = TextStyle(
                    fontSize = 13.sp,
                    color = palette.textPrimary,
                ),
                cursorBrush = SolidColor(palette.primary),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 13.sp,
                            color = palette.textMuted,
                        )
                    }
                    innerTextField()
                },
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    palette: com.example.uiapp.theme.AppPalette,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, fontSize = 12.sp, color = palette.textMuted)
        Text(
            text = value.ifEmpty { "-" },
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textPrimary,
        )
    }
}
