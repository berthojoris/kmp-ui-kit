package com.example.uiapp.ui.aichat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.uiapp.theme.AppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Jenis lampiran mock. Warna diambil dari palet aktif, bukan hex tetap. */
enum class AttachmentType(
    val extension: String,
    val label: String,
) {
    PDF("PDF", "Dokumen PDF"),
    EXCEL("XLS", "Spreadsheet Excel"),
    WORD("DOC", "Dokumen Word"),
    IMAGE("JPG", "Gambar"),
    CODE("KT", "Kode Kotlin"),
}

private fun AttachmentType.tint(palette: AppPalette): Color = when (this) {
    AttachmentType.PDF -> palette.danger
    AttachmentType.EXCEL -> palette.success
    AttachmentType.WORD -> palette.info
    AttachmentType.IMAGE -> palette.warning
    AttachmentType.CODE -> palette.primary
}

data class ChatAttachment(
    val id: String,
    val name: String,
    val sizeLabel: String,
    val type: AttachmentType,
)

/** Data mock lampiran; tidak ada file sungguhan yang dibaca dari perangkat. */
private val AttachmentSamples = listOf(
    ChatAttachment("att_pdf", "laporan_keuangan_Q3.pdf", "2,4 MB", AttachmentType.PDF),
    ChatAttachment("att_xls", "proyeksi_budget_2027.xlsx", "860 KB", AttachmentType.EXCEL),
    ChatAttachment("att_doc", "spesifikasi_ui_composer.docx", "1,1 MB", AttachmentType.WORD),
    ChatAttachment("att_img", "mockup_composer.jpg", "3,2 MB", AttachmentType.IMAGE),
    ChatAttachment("att_kt", "ChatComposer.kt", "12 KB", AttachmentType.CODE),
)

private const val MaxAttachments = 3

data class ChatCitation(
    val title: String,
    val source: String,
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    var text: String,
    val codeSnippet: String? = null,
    val citations: List<ChatCitation> = emptyList(),
    val attachments: List<ChatAttachment> = emptyList(),
    val isStreaming: Boolean = false,
    val isError: Boolean = false,
    var rating: Int = 0, // 0 = none, 1 = up, -1 = down
    val timestamp: String = "15:42",
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AiChatLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    var isVoiceMode by remember { mutableStateOf(false) }
    val attachedFiles = remember { mutableStateListOf<ChatAttachment>() }
    var attachMenuOpen by remember { mutableStateOf(false) }
    var isThinking by remember { mutableStateOf(false) }
    var activeStreamingJob by remember { mutableStateOf<Job?>(null) }
    var noticeText by remember { mutableStateOf<String?>(null) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = "m1",
                isUser = false,
                text = "Halo! Saya asisten arsitektur mobile. Tanyakan apa saja seputar Compose Multiplatform, Flat UI, atau optimasi memori.",
                codeSnippet = null,
                citations = listOf(
                    ChatCitation("Jetpack Compose Guidelines", "developer.android.com"),
                    ChatCitation("Compose Multiplatform Overview", "jetbrains.com/compose-multiplatform"),
                ),
            ),
        )
    }

    val quickPrompts = listOf(
        "Kelebihan Flat UI tanpa Shadow",
        "Contoh arsitektur Clean KMP",
        "Optimasi Recomposition Compose",
        "Cara cegah OOM di Android 3GB",
    )

    fun startSimulatedStreaming(userPrompt: String) {
        val messageId = "ai_${messages.size + 1}"
        val (fullResponse, code, citations) = when {
            userPrompt.contains("Flat UI", ignoreCase = true) -> Triple(
                "Desain Flat UI murni mengutamakan kejelasan hierarki melalui border 1px yang tegas, kontras warna yang presisi, dan tipografi berbobot.\nDengan meniadakan drop shadow, GPU tidak perlu melakukan komputasi blur render effect yang membebani memori pada perangkat berspesifikasi hemat.",
                "Surface(\n    border = BorderStroke(1.dp, palette.border),\n    shadowElevation = 0.dp,\n    tonalElevation = 0.dp\n)",
                listOf(ChatCitation("Flat UI System Rules", "ui-kit/docs"), ChatCitation("GPU Pipeline Performance", "developer.android.com")),
            )
            userPrompt.contains("Clean", ignoreCase = true) -> Triple(
                "Clean Architecture di KMP memisahkan kode menjadi layer domain (Entities, UseCases), data (Repositories, Ktor/Room), dan presentation (Compose Multiplatform).\nLogika bisnis murni ditempatkan di commonMain tanpa ketergantungan pada Android context.",
                "class GetUserUseCase(private val repository: UserRepository) {\n    suspend operator fun invoke(): Result<User> = repository.fetchUser()\n}",
                listOf(ChatCitation("Clean Architecture KMP", "kmp-docs.dev")),
            )
            else -> Triple(
                "Compose Multiplatform mengompilasi deklarasi UI Kotlin ke platform native dengan efisiensi tinggi. Pada Android memanfaatkan Jetpack Compose, sedangkan di iOS merender langsung via Metal Canvas Skiko dengan 60-120 FPS.",
                "expect class PlatformInfo {\n    val model: String\n}",
                listOf(ChatCitation("Multiplatform Engine", "jetbrains.com")),
            )
        }

        isThinking = true
        activeStreamingJob = scope.launch {
            delay(1200) // Thinking delay
            isThinking = false

            val aiMsg = ChatMessage(
                id = messageId,
                isUser = false,
                text = "",
                codeSnippet = code,
                citations = citations,
                isStreaming = true,
            )
            messages.add(aiMsg)
            val targetIndex = messages.lastIndex

            val words = fullResponse.split(" ")
            var currentAccumulated = ""
            for (word in words) {
                currentAccumulated += if (currentAccumulated.isEmpty()) word else " $word"
                messages[targetIndex] = messages[targetIndex].copy(text = currentAccumulated)
                delay(40)
                listState.animateScrollToItem(messages.lastIndex)
            }
            messages[targetIndex] = messages[targetIndex].copy(isStreaming = false)
            activeStreamingJob = null
        }
    }

    fun showNotice(text: String) {
        noticeText = text
        scope.launch {
            delay(2200)
            if (noticeText == text) noticeText = null
        }
    }

    fun toggleAttachment(file: ChatAttachment) {
        val existing = attachedFiles.indexOfFirst { it.id == file.id }
        when {
            existing != -1 -> attachedFiles.removeAt(existing)
            attachedFiles.size >= MaxAttachments -> showNotice("Maksimal $MaxAttachments lampiran dalam satu pesan")
            else -> attachedFiles.add(file)
        }
    }

    fun sendMessage() {
        if (inputText.isBlank() && attachedFiles.isEmpty()) return
        val textToSend = inputText.trim()
        val filesToSend = attachedFiles.toList()
        inputText = ""
        attachedFiles.clear()
        attachMenuOpen = false

        val userMsg = ChatMessage(
            id = "user_${messages.size + 1}",
            isUser = true,
            text = textToSend,
            attachments = filesToSend,
        )
        messages.add(userMsg)

        scope.launch {
            listState.animateScrollToItem(messages.lastIndex)
        }

        startSimulatedStreaming(if (textToSend.isBlank()) "lampiran" else textToSend)
    }

    fun stopStreaming() {
        activeStreamingJob?.cancel()
        activeStreamingJob = null
        isThinking = false
        if (messages.isNotEmpty() && messages.last().isStreaming) {
            val lastIdx = messages.lastIndex
            messages[lastIdx] = messages[lastIdx].copy(isStreaming = false)
        }
    }

    Scaffold(
        containerColor = palette.background,
        // Inset dasar ditangani sekali di Column lewat safeDrawing. Kalau Scaffold juga
        // memberi innerPadding bawah, composer jadi mengambang di atas navigation bar.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            UiTopBar(
                title = "AI Chat Composer",
                subtitle = "Streaming response & composer adaptif",
                onBack = onBack,
                action = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (activeStreamingJob != null || isThinking) palette.warning.copy(alpha = 0.15f) else palette.surfaceMuted)
                            .border(1.dp, if (activeStreamingJob != null || isThinking) palette.warning else palette.border, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = if (activeStreamingJob != null || isThinking) "STREAMING" else "IDLE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeStreamingJob != null || isThinking) palette.warning else palette.textMuted,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
                ),
        ) {
            // Notification toast if copied
            AnimatedVisibility(visible = noticeText != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.primary)
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = noticeText ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.onPrimary,
                    )
                }
            }

            // Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubbleItem(
                        message = msg,
                        palette = palette,
                        onCopyText = { _ ->
                            showNotice("Teks disalin ke clipboard")
                        },
                        onRate = { ratingVal ->
                            val idx = messages.indexOfFirst { it.id == msg.id }
                            if (idx != -1) {
                                messages[idx] = messages[idx].copy(rating = ratingVal)
                            }
                        },
                        onRegenerate = {
                            startSimulatedStreaming(msg.text)
                        },
                    )
                }

                if (isThinking) {
                    item {
                        AiThinkingBubble(palette = palette)
                    }
                }
            }

            // Quick Prompt Pills
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(quickPrompts) { prompt ->
                    Surface(
                        onClick = {
                            inputText = prompt
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                    ) {
                        Text(
                            text = prompt,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = palette.textSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }

            // Lampiran terpilih: kartu file dengan logo per jenis, seperti chatbox WhatsApp
            if (attachedFiles.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    attachedFiles.forEach { file ->
                        AttachedFileRow(
                            file = file,
                            palette = palette,
                            onRemove = { attachedFiles.remove(file) },
                        )
                    }
                }
            }

            // Panel pilih jenis file
            AnimatedVisibility(visible = attachMenuOpen) {
                AttachPickerPanel(
                    palette = palette,
                    attached = attachedFiles,
                    onPick = { toggleAttachment(it) },
                    onClose = { attachMenuOpen = false },
                )
            }

            // Adaptive Composer Input
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                shape = RoundedCornerShape(18.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp,
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    if (isVoiceMode) {
                        // Voice mode simulator
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.surfaceMuted)
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(palette.danger),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Mendengarkan input suara...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = palette.textPrimary,
                                )
                            }
                            Text(
                                text = "Kembali ke teks",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary,
                                modifier = Modifier.clickable { isVoiceMode = false },
                            )
                        }
                    } else {
                        // Multiline text input
                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 38.dp, max = 110.dp),
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                color = palette.textPrimary,
                                lineHeight = 20.sp,
                            ),
                            cursorBrush = SolidColor(palette.primary),
                            decorationBox = { innerTextField ->
                                if (inputText.isEmpty()) {
                                    Text(
                                        text = "Tanyakan sesuatu pada asisten...",
                                        fontSize = 14.sp,
                                        color = palette.textMuted,
                                    )
                                }
                                innerTextField()
                            },
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Attachment and Voice Toggle Actions
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (attachMenuOpen || attachedFiles.isNotEmpty()) palette.primary.copy(alpha = 0.12f)
                                        else palette.surfaceMuted,
                                    )
                                    .border(
                                        1.dp,
                                        if (attachMenuOpen || attachedFiles.isNotEmpty()) palette.primary else palette.border,
                                        RoundedCornerShape(8.dp),
                                    )
                                    .clickable(role = Role.Button, onClick = { attachMenuOpen = !attachMenuOpen })
                                    .semantics {
                                        role = Role.Button
                                        contentDescription = if (attachedFiles.isEmpty()) {
                                            "Lampirkan file"
                                        } else {
                                            "Lampirkan file, ${attachedFiles.size} terpilih"
                                        }
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📎", fontSize = 14.sp)
                                    if (attachedFiles.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = attachedFiles.size.toString(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.primary,
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isVoiceMode) palette.primary else palette.surfaceMuted)
                                    .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                                    .clickable { isVoiceMode = !isVoiceMode },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "🎙️",
                                    fontSize = 14.sp,
                                )
                            }
                        }

                        // Send / Stop Generation Button
                        if (activeStreamingJob != null || isThinking) {
                            Surface(
                                onClick = { stopStreaming() },
                                shape = RoundedCornerShape(10.dp),
                                color = palette.danger,
                                border = BorderStroke(1.dp, palette.danger),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(palette.surface, RoundedCornerShape(2.dp)),
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Hentikan",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                    )
                                }
                            }
                        } else {
                            Surface(
                                onClick = { sendMessage() },
                                shape = RoundedCornerShape(10.dp),
                                color = if (inputText.isNotBlank() || attachedFiles.isNotEmpty()) palette.primary else palette.surfaceMuted,
                                border = BorderStroke(1.dp, palette.border),
                                enabled = inputText.isNotBlank() || attachedFiles.isNotEmpty(),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "Kirim",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (inputText.isNotBlank() || attachedFiles.isNotEmpty()) palette.onPrimary else palette.textMuted,
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

/** Logo file Canvas per jenis, dipakai di preview lampiran dan bubble pesan. */
@Composable
private fun FileTypeBadge(
    type: AttachmentType,
    tint: Color,
    modifier: Modifier = Modifier,
    badgeSize: Dp = 40.dp,
) {
    Box(
        modifier = modifier
            .size(badgeSize)
            .clip(RoundedCornerShape(10.dp))
            .background(tint.copy(alpha = 0.12f))
            .border(1.dp, tint.copy(alpha = 0.45f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            DocumentGlyph(tint = tint, width = badgeSize * 0.32f)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = type.extension,
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                color = tint,
                maxLines = 1,
            )
        }
    }
}

/** Glyph dokumen vektor: halaman dengan lipatan sudut dan dua baris teks. */
@Composable
private fun DocumentGlyph(
    tint: Color,
    modifier: Modifier = Modifier,
    width: Dp = 16.dp,
) {
    Canvas(modifier = modifier.size(width = width, height = width * 1.25f)) {
        val strokeWidth = 1.4.dp.toPx()
        val w = size.width
        val h = size.height
        val fold = w * 0.36f
        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)

        drawPath(
            path = Path().apply {
                moveTo(0f, 0f)
                lineTo(w - fold, 0f)
                lineTo(w, fold)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            },
            color = tint,
            style = stroke,
        )
        drawPath(
            path = Path().apply {
                moveTo(w - fold, 0f)
                lineTo(w - fold, fold)
                lineTo(w, fold)
            },
            color = tint,
            style = stroke,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.20f, h * 0.58f),
            end = Offset(w * 0.80f, h * 0.58f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.20f, h * 0.74f),
            end = Offset(w * 0.60f, h * 0.74f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}

/** Kartu lampiran di atas composer: logo, nama file, ukuran, dan tombol hapus. */
@Composable
private fun AttachedFileRow(
    file: ChatAttachment,
    palette: com.example.uiapp.theme.AppPalette,
    onRemove: () -> Unit,
    compact: Boolean = false,
) {
    val tint = file.type.tint(palette)
    val titleColor = if (compact) palette.onPrimary else palette.textPrimary
    val metaColor = if (compact) palette.onPrimary.copy(alpha = 0.7f) else palette.textMuted

    Row(
        modifier = Modifier
            .then(if (compact) Modifier.widthIn(max = 232.dp) else Modifier.fillMaxWidth())
            .background(
                color = if (compact) palette.onPrimary.copy(alpha = 0.12f) else palette.surfaceMuted,
                shape = RoundedCornerShape(12.dp),
            )
            .border(
                width = 1.dp,
                color = if (compact) palette.onPrimary.copy(alpha = 0.28f) else palette.border,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FileTypeBadge(
            type = file.type,
            tint = if (compact) palette.onPrimary else tint,
            badgeSize = 38.dp,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = file.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${file.type.label} · ${file.sizeLabel}",
                fontSize = 10.sp,
                color = metaColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (!compact) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onRemove)
                    .semantics {
                        role = Role.Button
                        contentDescription = "Hapus lampiran ${file.name}"
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text("✕", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.danger)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AttachPickerPanel(
    palette: com.example.uiapp.theme.AppPalette,
    attached: List<ChatAttachment>,
    onPick: (ChatAttachment) -> Unit,
    onClose: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Lampirkan file",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )
                Text(
                    text = "Tutup",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textSecondary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(role = Role.Button, onClick = onClose)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Contoh simulasi · tidak ada file yang benar-benar diunggah",
                fontSize = 10.sp,
                color = palette.textMuted,
            )
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AttachmentSamples.forEach { sample ->
                    val isAttached = attached.any { it.id == sample.id }
                    Surface(
                        onClick = { onPick(sample) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isAttached) palette.primaryContainer else palette.surfaceMuted,
                        border = BorderStroke(
                            1.dp,
                            if (isAttached) palette.primary else palette.border,
                        ),
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        modifier = Modifier.semantics {
                            role = Role.Button
                            contentDescription = buildString {
                                append("Lampirkan ${sample.name} (${sample.type.label})")
                                if (isAttached) append(", sudah terpilih")
                            }
                        },
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            FileTypeBadge(
                                type = sample.type,
                                tint = sample.type.tint(palette),
                                badgeSize = 30.dp,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = sample.type.extension,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                )
                                Text(
                                    text = sample.sizeLabel,
                                    fontSize = 9.sp,
                                    color = palette.textMuted,
                                )
                            }
                            if (isAttached) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "✓",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.primary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(
    message: ChatMessage,
    palette: com.example.uiapp.theme.AppPalette,
    onCopyText: (String) -> Unit,
    onRate: (Int) -> Unit,
    onRegenerate: () -> Unit,
) {
    if (message.isUser) {
        // User Message Bubble (Right aligned)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Surface(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                color = palette.primary,
                border = BorderStroke(1.dp, palette.primary),
                shadowElevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .widthIn(max = 280.dp),
                ) {
                    if (message.attachments.isNotEmpty()) {
                        message.attachments.forEach { file ->
                            AttachedFileRow(
                                file = file,
                                palette = palette,
                                onRemove = {},
                                compact = true,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                    if (message.text.isNotBlank()) {
                        Text(
                            text = message.text,
                            fontSize = 14.sp,
                            color = palette.onPrimary,
                            lineHeight = 20.sp,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        text = message.timestamp,
                        fontSize = 10.sp,
                        color = palette.onPrimary.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.End),
                    )
                }
            }
        }
    } else {
        // AI Response Bubble (Left aligned)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            Surface(
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(0.92f),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(palette.primary),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("✦", fontSize = 11.sp, color = palette.onPrimary)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Assistant",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                            )
                        }

                        if (message.isStreaming) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(palette.surfaceMuted)
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "Mengetik...",
                                    fontSize = 10.sp,
                                    color = palette.textMuted,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = message.text.ifEmpty { "..." },
                        fontSize = 14.sp,
                        color = palette.textPrimary,
                        lineHeight = 21.sp,
                    )

                    // Code Snippet Block if present
                    if (!message.codeSnippet.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.border),
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "KOTLIN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = palette.textMuted,
                                    )
                                    Text(
                                        text = "Salin Kode",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.primary,
                                        modifier = Modifier.clickable { onCopyText(message.codeSnippet) },
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = message.codeSnippet,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = palette.textPrimary,
                                    lineHeight = 17.sp,
                                )
                            }
                        }
                    }

                    // Citation Cards
                    if (message.citations.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Sumber & Rujukan:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textMuted,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        message.citations.forEach { cit ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(palette.surfaceMuted)
                                    .border(1.dp, palette.border, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = cit.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = palette.textPrimary,
                                )
                                Text(
                                    text = cit.source,
                                    fontSize = 10.sp,
                                    color = palette.textMuted,
                                )
                            }
                        }
                    }

                    // Action bar: Copy, Rate, Regenerate
                    if (!message.isStreaming) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(palette.border),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Salin",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = palette.textSecondary,
                                    modifier = Modifier.clickable { onCopyText(message.text) },
                                )
                                Text(
                                    text = "Regenerate",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = palette.textSecondary,
                                    modifier = Modifier.clickable { onRegenerate() },
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = if (message.rating == 1) "👍 Disukai" else "👍",
                                    fontSize = 12.sp,
                                    color = if (message.rating == 1) palette.primary else palette.textMuted,
                                    modifier = Modifier.clickable { onRate(if (message.rating == 1) 0 else 1) },
                                )
                                Text(
                                    text = if (message.rating == -1) "👎 Kurang" else "👎",
                                    fontSize = 12.sp,
                                    color = if (message.rating == -1) palette.danger else palette.textMuted,
                                    modifier = Modifier.clickable { onRate(if (message.rating == -1) 0 else -1) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AiThinkingBubble(palette: com.example.uiapp.theme.AppPalette) {
    val infiniteTransition = rememberInfiniteTransition()
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(palette.primary.copy(alpha = dotAlpha)),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(palette.primary.copy(alpha = 1f - dotAlpha)),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(palette.primary.copy(alpha = dotAlpha)),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Sedang merumuskan jawaban...",
                fontSize = 12.sp,
                color = palette.textMuted,
            )
        }
    }
}
