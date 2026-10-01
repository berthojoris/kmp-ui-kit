package com.example.uiapp.ui.msgeffects

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.delay

private enum class BubbleEffect(val label: String, val emoji: String) {
    SLAM("Slam", "💥"),
    LOUD("Loud", "📢"),
    GENTLE("Gentle", "🪶"),
}

private enum class ScreenEffect(val label: String, val emoji: String) {
    CONFETTI("Confetti", "🎊"),
    LASER("Laser", "⚡"),
    FIREWORKS("Kembang api", "🎆"),
    ECHO("Echo", "🔁"),
    SPOTLIGHT("Sorot", "🔦"),
    INK("Tinta tersembunyi", "🌫"),
}

private enum class EffectMode(val label: String) {
    BUBBLE("Efek Bubble"),
    SCREEN("Efek Layar Penuh"),
}

private data class ChatMessage(
    val id: Int,
    val text: String,
    val fromMe: Boolean,
    val sender: String,
    val replyTo: String? = null,
    val inkHidden: Boolean = false,
)

private data class PinnedChat(val id: Int, val name: String, val snippet: String, val unread: Int)

private data class ThreadReply(val id: Int, val sender: String, val text: String)

private data class InkDot(val x: Float, val y: Float, val r: Float, val alpha: Float)

private data class ConfettiParticle(
    val x: Float,
    val startY: Float,
    val delay: Float,
    val spin: Float,
    val drift: Float,
    val colorIdx: Int,
)

private val mockPinnedChats = listOf(
    PinnedChat(1, "Sari Wijaya", "Oke, nanti aku kabari lagi ya", 2),
    PinnedChat(2, "Tim Desain", "Revisi banner sudah dikirim", 0),
    PinnedChat(3, "Keluarga Besar", "Makan malam jam 7 di rumah", 5),
)

private val mockThreadReplies = listOf(
    ThreadReply(1, "Bagas", "Setuju, kita pakai versi yang pertama."),
    ThreadReply(2, "Sari", "Aku tambahkan catatan revisi malam ini."),
    ThreadReply(3, "Anda", "Sip, terima kasih semuanya!"),
)

private val tapbackOptions = listOf(
    "❤️" to "Cinta",
    "👍" to "Suka",
    "😂" to "Tertawa",
    "‼️" to "Tanda seru",
    "❓" to "Tanda tanya",
    "😢" to "Sedih",
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MsgEffectsLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var effectMode by remember { mutableStateOf(EffectMode.BUBBLE) }
    var bubbleEffect by remember { mutableStateOf(BubbleEffect.SLAM) }
    var screenEffect by remember { mutableStateOf(ScreenEffect.CONFETTI) }
    var reduceMotion by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf("") }
    var nextId by remember { mutableIntStateOf(100) }
    var animateMsgId by remember { mutableIntStateOf(-1) }
    var animToken by remember { mutableIntStateOf(0) }
    var tapbackTargetId by remember { mutableIntStateOf(-1) }
    var overlayEffect by remember { mutableStateOf<ScreenEffect?>(null) }
    var overlayTrigger by remember { mutableIntStateOf(0) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(1, "Halo! Jangan lupa rapat tim jam 10 ya.", false, "Sari"),
            ChatMessage(2, "Siap, sudah aku catat di agenda.", true, "Anda"),
            ChatMessage(3, "File presentasinya sudah kukirim ke grup.", false, "Bagas", replyTo = "Siap, sudah aku catat di agenda."),
            ChatMessage(4, "Mantap! Nanti kita review bareng-bareng.", true, "Anda"),
        )
    }
    val reactions = remember { mutableStateMapOf<Int, String>() }
    val inkRevealed = remember { mutableStateMapOf<Int, Boolean>() }
    val chatScroll = rememberScrollState()

    LaunchedEffect(messages.size) {
        chatScroll.animateScrollTo(chatScroll.maxValue)
    }

    fun sendMessage(text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        val id = nextId
        nextId = id + 1
        val ink = effectMode == EffectMode.SCREEN && screenEffect == ScreenEffect.INK
        messages.add(ChatMessage(id = id, text = clean, fromMe = true, sender = "Anda", inkHidden = ink))
        draft = ""
        tapbackTargetId = -1
        if (effectMode == EffectMode.BUBBLE) {
            animateMsgId = id
            animToken++
        } else {
            overlayEffect = screenEffect
            overlayTrigger++
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Messages Bubble & Screen Effects",
                subtitle = "Simulasi efek pesan, bukan iMessage",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                MsgEffectsControls(
                    effectMode = effectMode,
                    bubbleEffect = bubbleEffect,
                    screenEffect = screenEffect,
                    reduceMotion = reduceMotion,
                    onModeChange = { effectMode = it },
                    onBubbleEffectChange = { bubbleEffect = it },
                    onScreenEffectChange = { screenEffect = it },
                    onReduceMotionChange = { reduceMotion = it },
                    onSendDemo = {
                        val label = if (effectMode == EffectMode.BUBBLE) bubbleEffect.label else screenEffect.label
                        sendMessage("Hore, selamat! Efek $label (demo simulasi)")
                    },
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(chatScroll)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        LabIntroCard(
                            title = "Simulasi efek pesan — bukan iMessage",
                            body = "Seluruh bubble, efek layar penuh, tapback, dan percakapan adalah demo lokal. " +
                                "Tidak ada pesan nyata yang dikirim atau diterima.",
                        )

                        PinnedConversationsCard()

                        messages.forEach { message ->
                            ChatBubbleItem(
                                message = message,
                                reaction = reactions[message.id],
                                inkRevealed = inkRevealed[message.id] == true,
                                animToken = animToken,
                                shouldAnimate = message.id == animateMsgId,
                                bubbleEffect = bubbleEffect,
                                reduceMotion = reduceMotion,
                                tapbackOpen = tapbackTargetId == message.id,
                                onToggleTapback = {
                                    tapbackTargetId = if (tapbackTargetId == message.id) -1 else message.id
                                },
                                onReact = { emoji ->
                                    reactions[message.id] = emoji
                                    tapbackTargetId = -1
                                },
                                onRevealInk = { inkRevealed[message.id] = true },
                                onReplay = {
                                    animateMsgId = message.id
                                    animToken++
                                },
                            )
                        }

                        ThreadCard()
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    overlayEffect?.let { effect ->
                        ScreenEffectOverlay(
                            effect = effect,
                            trigger = overlayTrigger,
                            reduceMotion = reduceMotion,
                            onDone = { overlayEffect = null },
                        )
                    }
                }

                MsgComposer(
                    draft = draft,
                    onDraftChange = { draft = it },
                    onSend = { sendMessage(draft) },
                )
            }
        }
    }
}

@Composable
private fun MsgEffectsControls(
    effectMode: EffectMode,
    bubbleEffect: BubbleEffect,
    screenEffect: ScreenEffect,
    reduceMotion: Boolean,
    onModeChange: (EffectMode) -> Unit,
    onBubbleEffectChange: (BubbleEffect) -> Unit,
    onScreenEffectChange: (ScreenEffect) -> Unit,
    onReduceMotionChange: (Boolean) -> Unit,
    onSendDemo: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val screenRows = remember { ScreenEffect.entries.chunked(3) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.surface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            EffectMode.entries.forEach { mode ->
                EffectChip(
                    label = mode.label,
                    selected = effectMode == mode,
                    modifier = Modifier.weight(1f),
                    onClick = { onModeChange(mode) },
                )
            }
        }

        Text(
            text = if (effectMode == EffectMode.BUBBLE) "Pilih efek bubble (skala + getar sesaat)" else "Pilih efek layar penuh",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textMuted,
        )

        if (effectMode == EffectMode.BUBBLE) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BubbleEffect.entries.forEach { effect ->
                    EffectChip(
                        label = "${effect.emoji} ${effect.label}",
                        selected = bubbleEffect == effect,
                        modifier = Modifier.weight(1f),
                        onClick = { onBubbleEffectChange(effect) },
                    )
                }
            }
        } else {
            screenRows.forEach { rowEffects ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowEffects.forEach { effect ->
                        EffectChip(
                            label = "${effect.emoji} ${effect.label}",
                            selected = screenEffect == effect,
                            modifier = Modifier.weight(1f),
                            onClick = { onScreenEffectChange(effect) },
                        )
                    }
                }
            }
        }

        ReduceMotionToggle(reduceMotion = reduceMotion, onChange = onReduceMotionChange)

        Surface(
            onClick = onSendDemo,
            shape = RoundedCornerShape(10.dp),
            color = palette.primary,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Kirim pesan demo dengan efek",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = palette.onPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 11.dp),
            )
        }
    }
}

@Composable
private fun EffectChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (selected) palette.primary else palette.surface,
        border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
        modifier = modifier,
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (selected) palette.onPrimary else palette.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 9.dp),
        )
    }
}

@Composable
private fun ReduceMotionToggle(reduceMotion: Boolean, onChange: (Boolean) -> Unit) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, palette.border, RoundedCornerShape(10.dp))
            .clickable { onChange(!reduceMotion) }
            .semantics {
                role = Role.Switch
                contentDescription = "Mode kurangi gerak"
            }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Mode Kurangi Gerak (Reduce Motion)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
            Text(
                text = if (reduceMotion) "Aktif — animasi efek dihentikan" else "Nonaktif — animasi efek berjalan",
                fontSize = 10.sp,
                color = if (reduceMotion) palette.success else palette.textMuted,
            )
        }
        Box(
            modifier = Modifier
                .width(44.dp)
                .height(26.dp)
                .clip(CircleShape)
                .background(if (reduceMotion) palette.primary else palette.surfaceMuted)
                .border(1.dp, palette.border, CircleShape)
                .padding(3.dp),
            contentAlignment = if (reduceMotion) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (reduceMotion) palette.onPrimary else palette.textMuted),
            )
        }
    }
}

@Composable
private fun PinnedConversationsCard() {
    val palette = LocalAppPalette.current
    LabCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LabSectionTitle(
                title = "Percakapan Terpin",
                subtitle = "Daftar mock lokal — Simulasi, bukan iMessage",
            )
            SimulasiPill()
        }
        mockPinnedChats.forEach { chat ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SmallAvatar(name = chat.name)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📌 ${chat.name}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = chat.snippet,
                        fontSize = 11.sp,
                        color = palette.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (chat.unread > 0) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = palette.info.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, palette.info),
                    ) {
                        Text(
                            text = "${chat.unread} baru",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.info,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        )
                    }
                } else {
                    Text("Dibaca", fontSize = 10.sp, color = palette.textMuted)
                }
            }
        }
    }
}

@Composable
private fun ThreadCard() {
    val palette = LocalAppPalette.current
    LabCard {
        LabSectionTitle(title = "Balasan Thread", subtitle = "Rantai balasan mock dalam satu topik")
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = palette.surfaceMuted,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Bagas", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                Text("Ada usulan untuk judul banner kampanye?", fontSize = 12.sp, color = palette.textSecondary)
            }
        }
        mockThreadReplies.forEach { reply ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(34.dp)
                        .background(palette.border),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(reply.sender, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    Text(reply.text, fontSize = 12.sp, color = palette.textSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(
    message: ChatMessage,
    reaction: String?,
    inkRevealed: Boolean,
    animToken: Int,
    shouldAnimate: Boolean,
    bubbleEffect: BubbleEffect,
    reduceMotion: Boolean,
    tapbackOpen: Boolean,
    onToggleTapback: () -> Unit,
    onReact: (String) -> Unit,
    onRevealInk: () -> Unit,
    onReplay: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val scale = remember { Animatable(1f) }
    val shake = remember { Animatable(0f) }

    LaunchedEffect(animToken) {
        if (shouldAnimate && !reduceMotion) {
            when (bubbleEffect) {
                BubbleEffect.SLAM -> {
                    scale.animateTo(1.85f, tween(130))
                    shake.animateTo(12f, tween(60))
                    scale.animateTo(0.82f, tween(90))
                    shake.animateTo(-12f, tween(70))
                    shake.animateTo(0f, tween(80))
                    scale.animateTo(1f, tween(160))
                }
                BubbleEffect.LOUD -> {
                    scale.animateTo(1.3f, tween(320))
                    scale.animateTo(1f, tween(360))
                }
                BubbleEffect.GENTLE -> {
                    scale.animateTo(0.92f, tween(220))
                    scale.animateTo(1f, tween(240))
                }
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        if (!message.fromMe) {
            SmallAvatar(name = message.sender)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(
            modifier = Modifier.widthIn(max = 270.dp),
            horizontalAlignment = if (message.fromMe) Alignment.End else Alignment.Start,
        ) {
            if (!message.fromMe) {
                Text(message.sender, fontSize = 10.sp, color = palette.textMuted)
            }
            Box {
                Box(
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        translationX = if (message.fromMe) shake.value else -shake.value
                    },
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (message.fromMe) palette.primaryContainer else palette.surfaceMuted,
                        border = BorderStroke(1.dp, if (message.fromMe) palette.primary else palette.border),
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (message.replyTo != null) {
                                    Text(
                                        text = "↩ Membalas: \"${message.replyTo}\"",
                                        fontSize = 10.sp,
                                        color = palette.textSecondary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                Text(
                                    text = message.text,
                                    fontSize = 13.sp,
                                    color = if (message.fromMe) palette.onPrimaryContainer else palette.textPrimary,
                                )
                            }
                            if (message.inkHidden && !inkRevealed) {
                                InkCover(
                                    seed = message.id,
                                    modifier = Modifier.matchParentSize(),
                                    onReveal = onRevealInk,
                                )
                            }
                        }
                    }
                }

                if (reaction != null) {
                    Surface(
                        shape = CircleShape,
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 8.dp, y = 8.dp),
                    ) {
                        Text(
                            text = reaction,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SmallActionButton(label = "Reaksi", onClick = onToggleTapback)
                SmallActionButton(label = "▶ Efek", onClick = onReplay)
                if (message.inkHidden && !inkRevealed) {
                    SmallActionButton(label = "Ungkap", onClick = onRevealInk)
                }
            }

            if (tapbackOpen) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                ) {
                    Row(
                        modifier = Modifier.padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        tapbackOptions.forEach { (emoji, name) ->
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(palette.surfaceMuted)
                                    .border(1.dp, palette.border, CircleShape)
                                    .clickable { onReact(emoji) }
                                    .semantics {
                                        role = Role.Button
                                        contentDescription = "Kirim reaksi $name"
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(emoji, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallActionButton(label: String, onClick: () -> Unit) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textSecondary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
        )
    }
}

@Composable
private fun SmallAvatar(name: String) {
    val palette = LocalAppPalette.current
    val brush = remember(palette) {
        Brush.linearGradient(listOf(palette.primary.copy(alpha = 0.85f), palette.info.copy(alpha = 0.6f)))
    }
    val initials = remember(name) { name.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString("") }
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(brush)
            .border(1.dp, palette.border, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(initials, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.onPrimary)
    }
}

@Composable
private fun InkCover(seed: Int, modifier: Modifier = Modifier, onReveal: () -> Unit) {
    val palette = LocalAppPalette.current
    val dots = remember(seed) {
        val random = Random(seed)
        List(46) {
            InkDot(
                x = random.nextFloat(),
                y = random.nextFloat(),
                r = 3f + random.nextFloat() * 5f,
                alpha = 0.35f + random.nextFloat() * 0.4f,
            )
        }
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(palette.surfaceMuted)
            .clickable { onReveal() }
            .semantics {
                role = Role.Button
                contentDescription = "Ketuk untuk mengungkap pesan tersembunyi"
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            dots.forEach { dot ->
                drawCircle(
                    color = palette.textMuted.copy(alpha = dot.alpha),
                    radius = dot.r,
                    center = Offset(dot.x * size.width, dot.y * size.height),
                )
            }
        }
        Text("Ketuk untuk mengungkap", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = palette.textSecondary)
    }
}

@Composable
private fun MsgComposer(draft: String, onDraftChange: (String) -> Unit, onSend: () -> Unit) {
    val palette = LocalAppPalette.current
    val canSend = draft.isNotBlank()
    Column(modifier = Modifier.fillMaxWidth().background(palette.surface)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.border),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.surfaceMuted)
                    .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (draft.isEmpty()) {
                    Text("Tulis pesan simulasi…", fontSize = 12.sp, color = palette.textMuted)
                }
                BasicTextField(
                    value = draft,
                    onValueChange = onDraftChange,
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 13.sp, color = palette.textPrimary),
                    cursorBrush = SolidColor(palette.primary),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Surface(
                onClick = onSend,
                enabled = canSend,
                shape = RoundedCornerShape(10.dp),
                color = if (canSend) palette.primary else palette.surfaceMuted,
                border = BorderStroke(1.dp, if (canSend) palette.primary else palette.border),
            ) {
                Text(
                    text = "Kirim",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canSend) palette.onPrimary else palette.textMuted,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun SimulasiPill() {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = palette.info.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, palette.info),
    ) {
        Text(
            text = "Simulasi",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = palette.info,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun ScreenEffectOverlay(
    effect: ScreenEffect,
    trigger: Int,
    reduceMotion: Boolean,
    onDone: () -> Unit,
) {
    val palette = LocalAppPalette.current

    if (effect == ScreenEffect.INK) {
        InvisibleInkOverlay(trigger = trigger, reduceMotion = reduceMotion, onDone = onDone)
        return
    }

    if (reduceMotion) {
        LaunchedEffect(trigger) {
            delay(1100)
            onDone()
        }
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
            ) {
                Text(
                    text = "Efek ${effect.label} dilewati — Mode Kurangi Gerak aktif",
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                    modifier = Modifier.padding(14.dp),
                )
            }
        }
        return
    }

    val progress = remember(trigger) { Animatable(0f) }
    LaunchedEffect(trigger) {
        progress.animateTo(1f, tween(durationMillis = 1700, easing = LinearEasing))
        onDone()
    }

    val particles = remember(effect) {
        val random = Random(7)
        List(26) {
            ConfettiParticle(
                x = random.nextFloat(),
                startY = random.nextFloat() * 0.35f,
                delay = random.nextFloat() * 0.35f,
                spin = 1f + random.nextFloat() * 2f,
                drift = 12f + random.nextFloat() * 28f,
                colorIdx = random.nextInt(5),
            )
        }
    }
    val accentColors = remember(palette) {
        listOf(palette.primary, palette.success, palette.warning, palette.info, palette.danger)
    }
    val burstPos = remember { floatArrayOf(0.25f, 0.30f, 0.70f, 0.45f, 0.45f, 0.20f) }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val p = progress.value
            when (effect) {
                ScreenEffect.CONFETTI -> {
                    particles.forEach { pt ->
                        val t = ((p - pt.delay) / (1f - pt.delay)).coerceIn(0f, 1f)
                        if (t > 0f && t < 1f) {
                            val y = pt.startY * size.height + t * size.height * 1.3f
                            val x = pt.x * size.width + sin(t * pt.spin * PI).toFloat() * pt.drift
                            rotate(degrees = t * 360f * pt.spin, pivot = Offset(x, y)) {
                                drawRect(
                                    color = accentColors[pt.colorIdx],
                                    topLeft = Offset(x - 5f, y - 7f),
                                    size = Size(10f, 14f),
                                )
                            }
                        }
                    }
                }
                ScreenEffect.LASER -> {
                    val x = p * size.width
                    drawLine(
                        color = palette.danger.copy(alpha = 0.25f),
                        start = Offset(x - 70f, -20f),
                        end = Offset(x + 70f, size.height + 20f),
                        strokeWidth = 14f,
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = palette.danger,
                        start = Offset(x - 60f, -20f),
                        end = Offset(x + 60f, size.height + 20f),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = palette.warning,
                        start = Offset(x - 40f, -20f),
                        end = Offset(x + 40f, size.height + 20f),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round,
                    )
                }
                ScreenEffect.FIREWORKS -> {
                    for (i in 0 until 3) {
                        val local = ((p - i * 0.18f) / 0.6f).coerceIn(0f, 1f)
                        if (local > 0f) {
                            val cx = size.width * burstPos[i * 2]
                            val cy = size.height * burstPos[i * 2 + 1]
                            val color = accentColors[(i + 1) % accentColors.size]
                            drawCircle(
                                color = color.copy(alpha = (1f - local) * 0.5f),
                                radius = local * 80f,
                                center = Offset(cx, cy),
                                style = Stroke(width = 3f),
                            )
                            for (j in 0 until 10) {
                                val angle = j * 2.0 * PI / 10.0
                                val dx = cos(angle).toFloat()
                                val dy = sin(angle).toFloat()
                                val r0 = local * 30f
                                val r1 = local * 95f
                                drawLine(
                                    color = color.copy(alpha = 1f - local),
                                    start = Offset(cx + dx * r0, cy + dy * r0),
                                    end = Offset(cx + dx * r1, cy + dy * r1),
                                    strokeWidth = 3f,
                                    cap = StrokeCap.Round,
                                )
                            }
                        }
                    }
                }
                ScreenEffect.SPOTLIGHT -> {
                    drawRect(color = palette.background.copy(alpha = 0.45f * (1f - p)))
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val pulse = 1f + sin(p * 12f) * 0.05f
                    drawCircle(color = palette.warning.copy(alpha = 0.18f), radius = 110f * pulse, center = Offset(cx, cy))
                    drawCircle(
                        color = palette.warning,
                        radius = 110f * pulse,
                        center = Offset(cx, cy),
                        style = Stroke(width = 2f),
                    )
                }
                ScreenEffect.ECHO -> Unit
                ScreenEffect.INK -> Unit
            }
        }

        when (effect) {
            ScreenEffect.ECHO -> EchoText(progress = progress.value)
            ScreenEffect.CONFETTI -> OverlayCaption("🎊 Confetti — simulasi efek layar penuh")
            ScreenEffect.LASER -> OverlayCaption("⚡ Laser menyapu layar")
            ScreenEffect.FIREWORKS -> OverlayCaption("🎆 Kembang api")
            ScreenEffect.SPOTLIGHT -> OverlayCaption("🔦 Sorot pusat panggung")
            ScreenEffect.INK -> Unit
        }
    }
}

@Composable
private fun EchoText(progress: Float) {
    val palette = LocalAppPalette.current
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        repeat(4) { i ->
            val local = ((progress - i * 0.12f) / 0.55f).coerceIn(0f, 1f)
            Text(
                text = "Hore, selamat! 🎉",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = palette.textPrimary,
                modifier = Modifier.graphicsLayer {
                    alpha = (1f - local) * (1f - i * 0.15f)
                    scaleX = 1f + local * (0.5f + i * 0.25f)
                    scaleY = 1f + local * (0.5f + i * 0.25f)
                },
            )
        }
    }
}

@Composable
private fun OverlayCaption(text: String) {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier.fillMaxSize().padding(top = 12.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
        ) {
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun InvisibleInkOverlay(trigger: Int, reduceMotion: Boolean, onDone: () -> Unit) {
    val palette = LocalAppPalette.current
    var revealed by remember(trigger) { mutableStateOf(reduceMotion) }

    LaunchedEffect(trigger) {
        if (reduceMotion) {
            delay(1400)
            onDone()
        }
    }
    LaunchedEffect(revealed) {
        if (revealed) {
            delay(900)
            onDone()
        }
    }
    LaunchedEffect(trigger) {
        delay(8000)
        onDone()
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { revealed = true }
                .semantics {
                    role = Role.Button
                    contentDescription = if (revealed) "Pesan sudah terungkap" else "Ketuk untuk mengungkap pesan tersembunyi"
                },
        ) {
            Box(modifier = Modifier.padding(18.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Pesan disembunyikan — Simulasi", fontSize = 10.sp, color = palette.textMuted)
                    Text(
                        text = "Aku sudah sampai rumah, jangan lupa kunci pintu ya. ❤️",
                        fontSize = 14.sp,
                        color = palette.textPrimary,
                    )
                }
                if (!revealed) {
                    InkCover(
                        seed = 42,
                        modifier = Modifier.matchParentSize(),
                        onReveal = { revealed = true },
                    )
                }
            }
        }
    }
}
