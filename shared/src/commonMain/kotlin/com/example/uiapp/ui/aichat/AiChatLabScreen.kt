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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val isStreaming: Boolean = false,
    val isError: Boolean = false,
    var rating: Int = 0, // 0 = none, 1 = up, -1 = down
    val timestamp: String = "15:42",
)

@Composable
fun AiChatLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    var isVoiceMode by remember { mutableStateOf(false) }
    var attachedFile by remember { mutableStateOf<String?>(null) }
    var isThinking by remember { mutableStateOf(false) }
    var activeStreamingJob by remember { mutableStateOf<Job?>(null) }
    var copiedNotice by remember { mutableStateOf<String?>(null) }

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

    fun sendMessage() {
        if (inputText.isBlank() && attachedFile == null) return
        val textToSend = inputText.trim()
        val fileToSend = attachedFile
        inputText = ""
        attachedFile = null

        val userMsg = ChatMessage(
            id = "user_${messages.size + 1}",
            isUser = true,
            text = if (fileToSend != null) "[$fileToSend] $textToSend" else textToSend,
        )
        messages.add(userMsg)

        scope.launch {
            listState.animateScrollToItem(messages.lastIndex)
        }

        startSimulatedStreaming(textToSend)
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
                .navigationBarsPadding()
                .imePadding(),
        ) {
            // Notification toast if copied
            AnimatedVisibility(visible = copiedNotice != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.primary)
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = copiedNotice ?: "",
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
                        onCopyText = { txt ->
                            copiedNotice = "Teks disalin ke clipboard"
                            scope.launch {
                                delay(2000)
                                copiedNotice = null
                            }
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

            // Attached preview badge if any
            if (attachedFile != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.surfaceMuted)
                            .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "📎 $attachedFile",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "✕",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.danger,
                                modifier = Modifier.clickable { attachedFile = null },
                            )
                        }
                    }
                }
            }

            // Adaptive Composer Input
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
                                    .background(palette.surfaceMuted)
                                    .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                                    .clickable {
                                        attachedFile = "schema_model.kt"
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("📎", fontSize = 14.sp)
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
                                color = if (inputText.isNotBlank() || attachedFile != null) palette.primary else palette.surfaceMuted,
                                border = BorderStroke(1.dp, palette.border),
                                enabled = inputText.isNotBlank() || attachedFile != null,
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "Kirim",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (inputText.isNotBlank() || attachedFile != null) palette.onPrimary else palette.textMuted,
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
                    Text(
                        text = message.text,
                        fontSize = 14.sp,
                        color = palette.onPrimary,
                        lineHeight = 20.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
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
