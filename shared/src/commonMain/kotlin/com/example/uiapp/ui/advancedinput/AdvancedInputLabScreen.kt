package com.example.uiapp.ui.advancedinput

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private enum class InputSubTab(val title: String) {
    PICKERS("Picker & Range"),
    TAGS_MENTIONS("Tag & Mention"),
    SIGNATURE("Signature"),
    IMAGE_FILE("Media & File"),
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AdvancedInputLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var activeSubTab by remember { mutableStateOf(InputSubTab.PICKERS) }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Input & Kontrol Lanjutan",
                subtitle = "Picker, Slider, Tag, Signature, File Upload",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface)
                    .border(BorderStroke(1.dp, palette.border))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InputSubTab.entries.forEach { tab ->
                    val isSelected = activeSubTab == tab
                    Surface(
                        onClick = { activeSubTab = tab },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) palette.primary else palette.surfaceMuted,
                        border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                    ) {
                        Text(
                            text = tab.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else palette.textPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (activeSubTab) {
                    InputSubTab.PICKERS -> PickersAndSlidersView(palette)
                    InputSubTab.TAGS_MENTIONS -> TagsAndMentionsView(palette)
                    InputSubTab.SIGNATURE -> SignatureAndColorPadView(palette)
                    InputSubTab.IMAGE_FILE -> ImageAndFileUploadView(palette)
                }
            }
        }
    }
}

@Composable
private fun PickersAndSlidersView(palette: com.example.uiapp.theme.AppPalette) {
    var selectedHour by remember { mutableIntStateOf(9) }
    var selectedMinute by remember { mutableIntStateOf(30) }
    var quantity by remember { mutableIntStateOf(2) }
    var sliderRange by remember { mutableStateOf(20f..80f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Wheel Time Picker Simulation
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Wheel Time Picker (iOS Style)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                Text("Pilih jam dan menit dengan stepper putar", fontSize = 12.sp, color = palette.textMuted)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Hours Wheel
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Jam", fontSize = 11.sp, color = palette.textMuted)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                onClick = { if (selectedHour > 0) selectedHour-- else selectedHour = 23 },
                                shape = RoundedCornerShape(8.dp),
                                color = palette.surfaceMuted,
                                border = BorderStroke(1.dp, palette.border),
                            ) {
                                Text("-", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = selectedHour.toString().padStart(2, '0'),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                modifier = Modifier.padding(horizontal = 14.dp),
                            )
                            Surface(
                                onClick = { if (selectedHour < 23) selectedHour++ else selectedHour = 0 },
                                shape = RoundedCornerShape(8.dp),
                                color = palette.surfaceMuted,
                                border = BorderStroke(1.dp, palette.border),
                            ) {
                                Text("+", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Text(":", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = palette.textMuted, modifier = Modifier.padding(horizontal = 14.dp))

                    // Minutes Wheel
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Menit", fontSize = 11.sp, color = palette.textMuted)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                onClick = { if (selectedMinute > 0) selectedMinute -= 5 else selectedMinute = 55 },
                                shape = RoundedCornerShape(8.dp),
                                color = palette.surfaceMuted,
                                border = BorderStroke(1.dp, palette.border),
                            ) {
                                Text("-", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = selectedMinute.toString().padStart(2, '0'),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                modifier = Modifier.padding(horizontal = 14.dp),
                            )
                            Surface(
                                onClick = { if (selectedMinute < 55) selectedMinute += 5 else selectedMinute = 0 },
                                shape = RoundedCornerShape(8.dp),
                                color = palette.surfaceMuted,
                                border = BorderStroke(1.dp, palette.border),
                            ) {
                                Text("+", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Stepper Quantity Control
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Quantity Stepper", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                    Text("Jumlah item: $quantity unit", fontSize = 12.sp, color = palette.textMuted)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Surface(
                        onClick = { if (quantity > 1) quantity-- },
                        shape = CircleShape,
                        color = palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                    ) {
                        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                            Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        }
                    }
                    Text(text = quantity.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    Surface(
                        onClick = { quantity++ },
                        shape = CircleShape,
                        color = palette.primary,
                    ) {
                        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                            Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Dual-thumb Range Slider
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Dual-Thumb Range Slider", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                Text(
                    "Rentang terpilih: ${sliderRange.start.toInt()}% - ${sliderRange.endInclusive.toInt()}%",
                    fontSize = 12.sp,
                    color = palette.primary,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(10.dp))
                RangeSlider(
                    value = sliderRange,
                    onValueChange = { sliderRange = it },
                    valueRange = 0f..100f,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagsAndMentionsView(palette: com.example.uiapp.theme.AppPalette) {
    val tags = remember {
        mutableStateListOf("Kotlin", "Compose", "Multiplatform", "Flat UI", "Clean Architecture")
    }
    val mentionSuggestions = listOf("@alex_dev", "@maria_pm", "@dani_ux", "@sarah_qa")
    var selectedMention by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tag & Chip Input (Interactive Deletion)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                Text("Ketuk tombol '✕' pada chip untuk menghapus", fontSize = 12.sp, color = palette.textMuted)
                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.border),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(tag, fontSize = 12.sp, color = palette.textPrimary, fontWeight = FontWeight.Medium)
                                Text(
                                    text = "✕",
                                    fontSize = 11.sp,
                                    color = palette.danger,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { tags.remove(tag) },
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    onClick = { tags.add("Tag #${tags.size + 1}") },
                    shape = RoundedCornerShape(8.dp),
                    color = palette.primaryContainer,
                    border = BorderStroke(1.dp, palette.primary),
                ) {
                    Text(
                        "+ Tambah Tag Baru",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.onPrimaryContainer,
                    )
                }
            }
        }

        // Mention Autocomplete
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("@Mention Autocomplete Suggestion", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                Text("Pilih anggota tim untuk di-mention", fontSize = 12.sp, color = palette.textMuted)
                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    mentionSuggestions.forEach { mention ->
                        val isSelected = selectedMention == mention
                        Surface(
                            onClick = { selectedMention = mention },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) palette.primary else palette.surfaceMuted,
                            border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                        ) {
                            Text(
                                text = mention,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else palette.primary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            )
                        }
                    }
                }
                if (selectedMention != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Mention aktif: $selectedMention",
                        fontSize = 12.sp,
                        color = palette.textPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun SignatureAndColorPadView(palette: com.example.uiapp.theme.AppPalette) {
    val points = remember { mutableStateListOf<Offset>() }
    var selectedColor by remember { mutableStateOf(Color(0xFF111827)) }
    val colors = listOf(Color(0xFF111827), Color(0xFF0A332C), Color(0xFF3B82F6), Color(0xFFEF4444), Color(0xFF10B981))
    val emojis = listOf("👍", "🚀", "🎉", "🔥", "💡", "❤️", "✨")
    var selectedEmoji by remember { mutableStateOf("🚀") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Canvas Signature Pad", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                    Surface(
                        onClick = { points.clear() },
                        shape = RoundedCornerShape(8.dp),
                        color = palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                    ) {
                        Text("Bersihkan", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 11.sp, color = palette.danger)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tanda tangani di area di bawah menggunakan sentuhan jari:", fontSize = 12.sp, color = palette.textMuted)
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surfaceMuted)
                        .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(12.dp))
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                points.add(change.position)
                            }
                        },
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        if (points.size > 1) {
                            for (i in 0 until points.size - 1) {
                                drawLine(
                                    color = selectedColor,
                                    start = points[i],
                                    end = points[i + 1],
                                    strokeWidth = 3.dp.toPx(),
                                    cap = StrokeCap.Round,
                                )
                            }
                        }
                    }
                    if (points.isEmpty()) {
                        Text(
                            text = "Sentuh & geser untuk membuat tanda tangan",
                            fontSize = 12.sp,
                            color = palette.textMuted,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Color picker
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Warna:", fontSize = 12.sp, color = palette.textMuted)
                    colors.forEach { c ->
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(c)
                                .border(BorderStroke(if (selectedColor == c) 2.dp else 1.dp, if (selectedColor == c) palette.primary else palette.border), CircleShape)
                                .clickable { selectedColor = c },
                        )
                    }
                }
            }
        }

        // Emoji Picker
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Emoji Picker: $selectedEmoji", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    emojis.forEach { emoji ->
                        Surface(
                            onClick = { selectedEmoji = emoji },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedEmoji == emoji) palette.primaryContainer else palette.surfaceMuted,
                            border = BorderStroke(1.dp, if (selectedEmoji == emoji) palette.primary else palette.border),
                        ) {
                            Text(emoji, fontSize = 20.sp, modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageAndFileUploadView(palette: com.example.uiapp.theme.AppPalette) {
    var uploadProgress by remember { mutableFloatStateOf(0.72f) }
    var isUploading by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Image crop preview frame
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Image Picker & Crop Frame", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                Text("Simulasi viewfinder crop 1:1 aspek rasio", fontSize = 12.sp, color = palette.textMuted)
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surfaceMuted)
                        .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .border(BorderStroke(2.dp, palette.primary), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Area Crop 1:1", fontSize = 11.sp, color = palette.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // File Uploader with Progress Bar
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("File & Document Upload Progress", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("specification_document.pdf (4.2 MB)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                    Text("${(uploadProgress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(palette.surfaceMuted),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(uploadProgress)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(palette.primary),
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        onClick = { uploadProgress = (uploadProgress + 0.1f).coerceAtMost(1f) },
                        shape = RoundedCornerShape(8.dp),
                        color = palette.primary,
                    ) {
                        Text("+10% Upload", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Surface(
                        onClick = { uploadProgress = 0f },
                        shape = RoundedCornerShape(8.dp),
                        color = palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                    ) {
                        Text("Reset", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 11.sp, color = palette.danger)
                    }
                }
            }
        }
    }
}
