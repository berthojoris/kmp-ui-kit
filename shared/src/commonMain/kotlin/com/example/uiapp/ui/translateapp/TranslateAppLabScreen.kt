package com.example.uiapp.ui.translateapp

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlin.math.sin

private const val MIC_LISTEN_MS = 2600L
private const val TRANSLATE_MS = 900L

private enum class TranslateMode(val label: String) {
    PERCAKAPAN("Percakapan"),
    KAMERA("Kamera"),
    PERANGKAT("Perangkat"),
}

private enum class MicState(val label: String) {
    SIAP("Ketuk untuk berbicara"),
    MENDENGARKAN("Mendengarkan…"),
    MENERJEMAHKAN("Menerjemahkan…"),
}

private data class PhrasePair(
    val id: Int,
    val sourceText: String,
    val targetText: String,
    val sourceLang: String,
    val targetLang: String,
)

private val CONVERSATION_QUEUE = listOf(
    PhrasePair(1, "Selamat pagi, apa kabar?", "Good morning, how are you?", "Indonesia", "Inggris"),
    PhrasePair(2, "Saya ingin memesan kopi susu.", "I would like to order a milk coffee.", "Indonesia", "Inggris"),
    PhrasePair(3, "Di mana stasiun kereta terdekat?", "Where is the nearest train station?", "Indonesia", "Inggris"),
    PhrasePair(4, "Terima kasih banyak atas bantuannya.", "Thank you very much for your help.", "Indonesia", "Inggris"),
    PhrasePair(5, "Berapa harganya untuk dua orang?", "How much is it for two people?", "Indonesia", "Inggris"),
)

private val MOCK_DICTIONARY = mapOf(
    "selamat pagi" to "Good morning",
    "selamat malam" to "Good evening",
    "terima kasih" to "Thank you",
    "permisi" to "Excuse me",
    "tolong bantu saya" to "Please help me",
    "berapa harganya" to "How much does it cost",
    "saya tersesat" to "I am lost",
    "di mana stasiun" to "Where is the station",
    "kopi susu" to "milk coffee",
    "sampai jumpa" to "See you later",
)

private val INDONESIA_MARKERS = listOf(
    "saya", "anda", "yang", "dan", "di mana", "apa", "kopi", "terima kasih", "berapa", "ini",
)

private fun translateMock(text: String, reversed: Boolean): String {
    val normalized = text.trim().lowercase()
    if (normalized.isEmpty()) return ""
    for ((key, value) in MOCK_DICTIONARY) {
        if (normalized.contains(key)) {
            return if (reversed) value + " (balik)" else value
        }
    }
    return "\"" + text.trim() + "\" (kamus simulasi tidak lengkap)"
}

private fun detectLanguage(text: String): String {
    val normalized = text.trim().lowercase()
    for (marker in INDONESIA_MARKERS) {
        if (normalized.contains(marker)) return "Indonesia"
    }
    return "Inggris (perkiraan)"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TranslateAppLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var mode by remember { mutableStateOf(TranslateMode.PERCAKAPAN) }
    var reversed by remember { mutableStateOf(false) }
    var cameraLive by remember { mutableStateOf(false) }
    var micState by remember { mutableStateOf(MicState.SIAP) }
    var cursor by remember { mutableIntStateOf(0) }
    var nextId by remember { mutableIntStateOf(100) }
    var failNext by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf<String?>("Pilih mode untuk memulai — Simulasi") }
    var statusTick by remember { mutableIntStateOf(0) }
    var statusIsError by remember { mutableStateOf(false) }

    val conversation = remember { mutableStateListOf<PhrasePair>() }
    val favorites = remember { mutableStateListOf<PhrasePair>() }
    val history = remember { mutableStateListOf<PhrasePair>() }

    fun notify(message: String, isError: Boolean = false) {
        statusText = message
        statusIsError = isError
        statusTick++
    }

    // Dengar otomatis berhenti lalu masuk fase terjemahan.
    LaunchedEffect(micState) {
        if (micState == MicState.MENDENGARKAN) {
            delay(MIC_LISTEN_MS)
            micState = MicState.MENERJEMAHKAN
        }
    }
    // Fase terjemahan memasukkan frasa berikutnya dari antrean mock.
    LaunchedEffect(micState) {
        if (micState == MicState.MENERJEMAHKAN) {
            delay(TRANSLATE_MS)
            if (failNext) {
                failNext = false
                micState = MicState.SIAP
                notify("Gagal menerjemahkan — mesin simulasi sibuk, coba lagi", isError = true)
            } else if (cursor >= CONVERSATION_QUEUE.size) {
                micState = MicState.SIAP
                notify("Antrean frasa percakapan habis (Simulasi)", isError = true)
            } else {
                val next = CONVERSATION_QUEUE[cursor]
                cursor++
                val pair = if (reversed) {
                    next.copy(
                        id = nextId++,
                        sourceText = next.targetText,
                        targetText = next.sourceText,
                        sourceLang = next.targetLang,
                        targetLang = next.sourceLang,
                    )
                } else {
                    next.copy(id = nextId++)
                }
                conversation.add(pair)
                history.add(0, pair)
                micState = MicState.SIAP
                notify("Selesai — terjemahan simulasi siap")
            }
        }
    }
    LaunchedEffect(statusTick) {
        if (statusText != null) {
            delay(2800)
            statusText = null
        }
    }
    DisposableEffect(Unit) {
        onDispose { micState = MicState.SIAP }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Translate",
                subtitle = "Percakapan, side-by-side — Simulasi",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LabSectionTitle(
                    title = "Translate",
                    subtitle = "Tanpa mesin terjemahan / mikrofon nyata",
                    modifier = Modifier.weight(1f),
                )
                SimulasiPill()
            }

            // Pemilih mode
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TranslateMode.entries.forEach { item ->
                    ModeChip(
                        label = item.label,
                        selected = item == mode,
                        onClick = { mode = item },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            LanguageBar(
                reversed = reversed,
                onFlip = {
                    reversed = !reversed
                    notify("Bahasa ditukar — " + (if (reversed) "Inggris → Indonesia" else "Indonesia → Inggris"))
                },
            )

            StatusBanner(message = statusText, isError = statusIsError)

            when (mode) {
                TranslateMode.PERCAKAPAN -> ConversationContent(
                    conversation = conversation,
                    micState = micState,
                    onStartMic = { micState = MicState.MENDENGARKAN },
                    onStopMic = { micState = MicState.MENERJEMAHKAN },
                    favorites = favorites,
                    onToggleFavorite = { pair ->
                        val exist = favorites.any { it.sourceText == pair.sourceText }
                        if (exist) {
                            favorites.removeAll { it.sourceText == pair.sourceText }
                            notify("Selesai — frasa dihapus dari favorit")
                        } else {
                            favorites.add(pair)
                            notify("Selesai — frasa disimpan ke favorit")
                        }
                    },
                )
                TranslateMode.KAMERA -> CameraContent(
                    liveOn = cameraLive,
                    scanned = cursor > 0,
                    failNext = failNext,
                    onToggleFail = {
                        failNext = it
                        notify(if (it) "Pemindaian berikutnya akan gagal (Simulasi)" else "Mode simulasi normal")
                    },
                    onToggleLive = {
                        cameraLive = !cameraLive
                        notify(
                            if (cameraLive) "Terjemahan langsung dinyalakan (Simulasi)"
                            else "Terjemahan langsung dimatikan",
                        )
                    },
                    onScan = {
                        if (failNext) {
                            failNext = false
                            notify("Gagal memindai gambar — coba lagi (Simulasi)", isError = true)
                        } else if (cursor >= CONVERSATION_QUEUE.size) {
                            notify("Tidak ada teks baru untuk dipindai (Simulasi)", isError = true)
                        } else {
                            cursor++
                            notify("Selesai — teks papan diterjemahkan (Simulasi)")
                        }
                    },
                )
                TranslateMode.PERANGKAT -> DeviceContent(
                    reversed = reversed,
                    onTranslate = { text ->
                        val result = translateMock(text, reversed)
                        val pair = PhrasePair(
                            id = nextId++,
                            sourceText = text.trim(),
                            targetText = result,
                            sourceLang = if (reversed) "Inggris" else "Indonesia",
                            targetLang = if (reversed) "Indonesia" else "Inggris",
                        )
                        history.add(0, pair)
                        notify("Selesai — hasil terjemahan siap")
                    },
                    onFavorite = { pair ->
                        favorites.add(pair)
                        notify("Selesai — frasa disimpan ke favorit")
                    },
                    history = history,
                    favorites = favorites,
                    onRemoveFavorite = { id ->
                        favorites.removeAll { it.id == id }
                        notify("Selesai — favorit dihapus")
                    },
                    onRemoveHistory = { id ->
                        history.removeAll { it.id == id }
                        notify("Selesai — riwayat dihapus")
                    },
                    onClearHistory = {
                        history.clear()
                        notify("Selesai — riwayat dikosongkan")
                    },
                    failNext = failNext,
                    onToggleFail = {
                        failNext = it
                        notify(if (it) "Percobaan berikutnya akan gagal (Simulasi)" else "Mode simulasi normal")
                    },
                )
            }

            LabCard {
                Text("Catatan Simulasi:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                Text(
                    "• Semua hasil terjemahan berasal dari kamus mock lokal — bukan layanan terjemahan nyata.",
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                )
                Text(
                    "• Tombol mikrofon dan kamera hanya meniru alur UI; tidak ada perangkat keras yang diakses.",
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                )
                Text(
                    "• Deteksi bahasa memakai penanda kata sederhana di perangkat.",
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun ConversationContent(
    conversation: List<PhrasePair>,
    micState: MicState,
    onStartMic: () -> Unit,
    onStopMic: () -> Unit,
    favorites: List<PhrasePair>,
    onToggleFavorite: (PhrasePair) -> Unit,
) {
    val palette = LocalAppPalette.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (conversation.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    EmptyChatGlyph(tint = palette.textMuted)
                    Text("Belum ada percakapan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    Text(
                        "Ketuk mikrofon untuk menambahkan frasa simulasi ke gelembung percakapan.",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                }
            }
        } else {
            conversation.forEach { pair ->
                val isFavorite = favorites.any { it.sourceText == pair.sourceText }
                ConversationBubble(
                    pair = pair,
                    isFavorite = isFavorite,
                    onToggleFavorite = { onToggleFavorite(pair) },
                )
            }
        }

        MicControl(
            micState = micState,
            enabled = micState != MicState.MENERJEMAHKAN,
            onStart = onStartMic,
            onStop = onStopMic,
        )
    }
}

@Composable
private fun ConversationBubble(
    pair: PhrasePair,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.primaryContainer,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
                border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth(0.86f),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = pair.sourceLang + " · terdeteksi: " + detectLanguage(pair.sourceText),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = pair.sourceText,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        color = palette.onPrimaryContainer,
                    )
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surfaceMuted,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(0.86f),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = pair.targetLang,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textMuted,
                            modifier = Modifier.weight(1f),
                        )
                        StarToggle(isFavorite = isFavorite, onToggle = onToggleFavorite)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = pair.targetText,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        color = palette.textPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun StarToggle(isFavorite: Boolean, onToggle: () -> Unit) {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .clickable { onToggle() }
            .semantics {
                role = Role.Button
                contentDescription = if (isFavorite) "Hapus dari frasa favorit" else "Simpan ke frasa favorit"
            },
        contentAlignment = Alignment.Center,
    ) {
        StarGlyph(tint = if (isFavorite) palette.warning else palette.textMuted, filled = isFavorite)
    }
}

@Composable
private fun MicControl(
    micState: MicState,
    enabled: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (micState == MicState.MENDENGARKAN) {
            Waveform()
        }
        Surface(
            onClick = {
                if (!enabled) return@Surface
                if (micState == MicState.SIAP) onStart() else onStop()
            },
            enabled = enabled,
            shape = CircleShape,
            color = when (micState) {
                MicState.SIAP -> palette.primary
                MicState.MENDENGARKAN -> palette.danger.copy(alpha = 0.14f)
                MicState.MENERJEMAHKAN -> palette.surfaceMuted
            },
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
            border = BorderStroke(1.dp, when (micState) {
                MicState.SIAP -> palette.primary
                MicState.MENDENGARKAN -> palette.danger
                else -> palette.border
            }),
            modifier = Modifier
                .size(76.dp)
                .alpha(if (enabled) 1f else 0.6f)
                .semantics {
                    role = Role.Button
                    contentDescription = when (micState) {
                        MicState.SIAP -> "Mulai merekam suara simulasi"
                        MicState.MENDENGARKAN -> "Berhenti merekam suara simulasi"
                        MicState.MENERJEMAHKAN -> "Sedang menerjemahkan, tombol dinonaktifkan"
                    }
                },
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                MicGlyph(
                    tint = when (micState) {
                        MicState.SIAP -> palette.onPrimary
                        MicState.MENDENGARKAN -> palette.danger
                        else -> palette.textMuted
                    },
                )
            }
        }
        Text(
            text = micState.label + " (Simulasi)",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textSecondary,
        )
    }
}

@Composable
private fun Waveform() {
    val palette = LocalAppPalette.current
    val transition = rememberInfiniteTransition(label = "waveform")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2832f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Restart),
        label = "wavePhase",
    )
    Canvas(modifier = Modifier.fillMaxWidth().height(48.dp)) {
        val bars = 24
        val gap = this.size.width / (bars + 1)
        for (i in 0 until bars) {
            val amplitude = (sin(phase.toDouble() + i * 0.55).toFloat() + 1f) / 2f
            val barHeight = 8.dp.toPx() + amplitude * 30.dp.toPx()
            drawLine(
                color = palette.danger,
                start = Offset(gap * (i + 1), this.size.height / 2 - barHeight / 2),
                end = Offset(gap * (i + 1), this.size.height / 2 + barHeight / 2),
                strokeWidth = 5.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun CameraContent(
    liveOn: Boolean,
    scanned: Boolean,
    failNext: Boolean,
    onToggleFail: (Boolean) -> Unit,
    onToggleLive: () -> Unit,
    onScan: () -> Unit,
) {
    val palette = LocalAppPalette.current
    var scanning by remember { mutableStateOf(false) }

    LaunchedEffect(scanning) {
        if (scanning) {
            delay(1100)
            scanning = false
            onScan()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, palette.border, RoundedCornerShape(16.dp)),
        ) {
            SceneIllustration()
            if (liveOn) {
                OverlayTranslation(
                    text = "MENU HARI INI",
                    translated = "TODAY'S MENU",
                    modifier = Modifier.align(Alignment.TopStart).padding(start = 24.dp, top = 34.dp),
                )
                OverlayTranslation(
                    text = "JALAN KENANGA",
                    translated = "KENANGA STREET",
                    modifier = Modifier.align(Alignment.BottomEnd).padding(end = 24.dp, bottom = 60.dp),
                )
                if (scanned) {
                    OverlayTranslation(
                        text = "BUKA 07.00",
                        translated = "OPEN 07.00",
                        modifier = Modifier.align(Alignment.Center).padding(top = 40.dp),
                    )
                }
            }
            Row(
                modifier = Modifier.align(Alignment.BottomStart).padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                StatusMiniPill(text = "Simulasi kamera", tint = palette.info)
                StatusMiniPill(text = if (liveOn) "Langsung: aktif" else "Langsung: nonaktif", tint = if (liveOn) palette.success else palette.textMuted)
            }
        }

        FlatSwitch(
            label = "Terjemahan langsung",
            checked = liveOn,
            enabled = !scanning,
            onCheckedChange = { onToggleLive() },
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PillActionButton(
                label = if (scanning) "Memproses…" else "Pindai Gambar",
                onClick = { if (!scanning) scanning = true },
                enabled = !scanning,
                modifier = Modifier.weight(1f),
                accent = true,
            )
            PillActionButton(
                label = "Batal Pindai",
                onClick = { scanning = false },
                enabled = scanning,
                modifier = Modifier.weight(1f),
                danger = true,
            )
        }
        FlatSwitch(
            label = "Simulasikan kegagalan pemindaian",
            checked = failNext,
            enabled = !scanning,
            onCheckedChange = onToggleFail,
        )
        if (failNext) {
            Text(
                "Pemindaian berikutnya disetel untuk gagal (uji state gagal).",
                fontSize = 11.sp,
                color = palette.danger,
            )
        }
        Text(
            text = if (scanned) {
                "Overlay \"terjemahan langsung\" ditampilkan di atas ilustrasi papan nama — hasil mock, bukan OCR nyata."
            } else {
                "Nyalakan terjemahan langsung lalu tekan \"Pindai Gambar\" untuk melihat overlay terjemahan."
            },
            fontSize = 11.sp,
            color = palette.textMuted,
        )
    }
}

@Composable
private fun OverlayTranslation(text: String, translated: String, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = palette.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, palette.primary),
        modifier = modifier.semantics { contentDescription = "Terjemahan langsung: " + text + " menjadi " + translated },
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Text(text, fontSize = 9.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                translated,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = palette.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun DeviceContent(
    reversed: Boolean,
    onTranslate: (String) -> Unit,
    onFavorite: (PhrasePair) -> Unit,
    history: List<PhrasePair>,
    favorites: List<PhrasePair>,
    onRemoveFavorite: (Int) -> Unit,
    onRemoveHistory: (Int) -> Unit,
    onClearHistory: () -> Unit,
    failNext: Boolean,
    onToggleFail: (Boolean) -> Unit,
) {
    val palette = LocalAppPalette.current
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val sourceLang = if (reversed) "Inggris" else "Indonesia"
    val targetLang = if (reversed) "Indonesia" else "Inggris"

    LaunchedEffect(busy) {
        if (busy) {
            delay(850)
            if (failNext) {
                errorMessage = "Layanan simulasi sedang sibuk — coba lagi"
                result = null
            } else if (input.isBlank()) {
                errorMessage = "Teks kosong — tulis kalimat untuk diterjemahkan"
                result = null
            } else {
                errorMessage = null
                result = translateMock(input, reversed)
                onTranslate(input)
            }
            busy = false
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val sideBySide = maxWidth >= 520.dp
            if (sideBySide) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    InputPanel(
                        lang = sourceLang,
                        input = input,
                        onInputChange = { input = it },
                        detected = detectLanguage(input),
                        modifier = Modifier.weight(1f),
                    )
                    ResultPanel(
                        lang = targetLang,
                        result = result,
                        busy = busy,
                        errorMessage = errorMessage,
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    InputPanel(
                        lang = sourceLang,
                        input = input,
                        onInputChange = { input = it },
                        detected = detectLanguage(input),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    ResultPanel(
                        lang = targetLang,
                        result = result,
                        busy = busy,
                        errorMessage = errorMessage,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        // Aksi bawah dengan imePadding agar tidak tertutup keyboard.
        Column(
            modifier = Modifier.fillMaxWidth().imePadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PillActionButton(
                    label = if (busy) "Menerjemahkan…" else "Terjemahkan",
                    onClick = { if (!busy) busy = true },
                    enabled = !busy && input.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    accent = true,
                )
                PillActionButton(
                    label = "Simpan Favorit",
                    onClick = {
                        val text = result
                        if (text != null) {
                            onFavorite(
                                PhrasePair(
                                    id = input.hashCode(),
                                    sourceText = input.trim(),
                                    targetText = text,
                                    sourceLang = sourceLang,
                                    targetLang = targetLang,
                                ),
                            )
                        }
                    },
                    enabled = result != null && !busy,
                    modifier = Modifier.weight(1f),
                )
                PillActionButton(
                    label = "Bersihkan",
                    onClick = {
                        input = ""
                        result = null
                        errorMessage = null
                    },
                    enabled = !busy,
                    modifier = Modifier.weight(1f),
                )
            }

            FlatSwitch(
                label = "Simulasikan kegagalan terjemahan",
                checked = failNext,
                enabled = !busy,
                onCheckedChange = onToggleFail,
            )
        }

        SavedPhrasesPanel(
            favorites = favorites,
            onRemoveFavorite = onRemoveFavorite,
        )
        HistoryPanel(
            history = history,
            onRemoveHistory = onRemoveHistory,
            onClearHistory = onClearHistory,
        )
    }
}

@Composable
private fun InputPanel(
    lang: String,
    input: String,
    onInputChange: (String) -> Unit,
    detected: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, palette.border),
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(lang, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.textMuted)
            BasicTextField(
                value = input,
                onValueChange = onInputChange,
                textStyle = TextStyle(fontSize = 15.sp, color = palette.textPrimary),
                modifier = Modifier.fillMaxWidth().height(96.dp),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (input.isEmpty()) {
                            Text(
                                "Tulis kalimat di sini…",
                                fontSize = 14.sp,
                                color = palette.textMuted,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            StatusMiniPill(text = "Terdeteksi: " + detected, tint = palette.info)
        }
    }
}

@Composable
private fun ResultPanel(
    lang: String,
    result: String?,
    busy: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, if (errorMessage != null) palette.danger else palette.border),
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(lang, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.textMuted)
            when {
                busy -> {
                    MiniSpinner()
                    Text("Menerjemahkan… (Simulasi)", fontSize = 12.sp, color = palette.textSecondary)
                }
                errorMessage != null -> {
                    Text("Gagal", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.danger)
                    Text(errorMessage, fontSize = 12.sp, color = palette.textSecondary)
                }
                result != null -> {
                    Text("Selesai", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = palette.success)
                    Text(result, fontSize = 15.sp, lineHeight = 21.sp, color = palette.textPrimary)
                }
                else -> {
                    Text("Belum ada hasil", fontSize = 13.sp, color = palette.textMuted)
                    Text(
                        "Hasil terjemahan simulasi akan muncul di sini.",
                        fontSize = 12.sp,
                        color = palette.textMuted,
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedPhrasesPanel(favorites: List<PhrasePair>, onRemoveFavorite: (Int) -> Unit) {
    val palette = LocalAppPalette.current
    LabCard {
        LabSectionTitle(title = "Frasa Favorit", subtitle = "Simpanan lokal demo")
        if (favorites.isEmpty()) {
            Text("Belum ada frasa favorit.", fontSize = 12.sp, color = palette.textMuted)
        } else {
            favorites.forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pair.sourceText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = pair.targetText,
                            fontSize = 11.sp,
                            color = palette.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    PillActionButton(
                        label = "Hapus",
                        onClick = { onRemoveFavorite(pair.id) },
                        enabled = true,
                        modifier = Modifier.width(72.dp),
                        danger = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryPanel(
    history: List<PhrasePair>,
    onRemoveHistory: (Int) -> Unit,
    onClearHistory: () -> Unit,
) {
    val palette = LocalAppPalette.current
    LabCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LabSectionTitle(title = "Riwayat", subtitle = history.size.toString() + " entri")
            PillActionButton(
                label = "Hapus Semua",
                onClick = onClearHistory,
                enabled = history.isNotEmpty(),
                modifier = Modifier.width(110.dp),
            )
        }
        if (history.isEmpty()) {
            Text("Belum ada riwayat terjemahan.", fontSize = 12.sp, color = palette.textMuted)
        } else {
            history.take(8).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pair.sourceLang + " → " + pair.targetLang,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textMuted,
                        )
                        Text(
                            text = pair.sourceText + "  ·  " + pair.targetText,
                            fontSize = 12.sp,
                            color = palette.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    PillActionButton(
                        label = "Hapus",
                        onClick = { onRemoveHistory(pair.id) },
                        enabled = true,
                        modifier = Modifier.width(72.dp),
                        danger = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageBar(reversed: Boolean, onFlip: () -> Unit) {
    val palette = LocalAppPalette.current
    val rotation by animateFloatAsState(
        targetValue = if (reversed) 180f else 0f,
        animationSpec = tween(260),
        label = "flipRotation",
    )
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            LanguagePill(text = if (reversed) "Inggris" else "Indonesia", modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(palette.primary.copy(alpha = 0.12f))
                    .border(1.dp, palette.primary, CircleShape)
                    .clickable { onFlip() }
                    .semantics {
                        role = Role.Button
                        contentDescription = "Tukar bahasa sumber dan bahasa tujuan"
                    },
                contentAlignment = Alignment.Center,
            ) {
                FlipGlyph(tint = palette.primary, rotation = rotation)
            }
            LanguagePill(text = if (reversed) "Indonesia" else "Inggris", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun LanguagePill(text: String, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(999.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ModeChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        color = if (selected) palette.primary else palette.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
        modifier = modifier.semantics {
            role = Role.Button
            contentDescription = "Mode " + label + (if (selected) ", aktif" else "")
        },
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) palette.onPrimary else palette.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        )
    }
}

@Composable
private fun PillActionButton(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
    danger: Boolean = false,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(999.dp),
        color = when {
            accent -> palette.primary
            danger -> palette.danger.copy(alpha = 0.10f)
            else -> palette.surface
        },
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(
            1.dp,
            when {
                accent -> palette.primary
                danger -> palette.danger
                else -> palette.border
            },
        ),
        modifier = modifier.alpha(if (enabled) 1f else 0.5f),
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = when {
                accent -> palette.onPrimary
                danger -> palette.danger
                else -> palette.textPrimary
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 10.dp),
        )
    }
}

@Composable
private fun FlatSwitch(
    label: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val palette = LocalAppPalette.current
    val knobProgress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(180),
        label = "knob",
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (enabled) palette.textPrimary else palette.textMuted,
            modifier = Modifier.weight(1f).padding(end = 8.dp),
        )
        Box(
            modifier = Modifier
                .size(width = 48.dp, height = 28.dp)
                .alpha(if (enabled) 1f else 0.5f)
                .clip(RoundedCornerShape(14.dp))
                .background(if (checked) palette.primary.copy(alpha = 0.25f) else palette.surfaceMuted)
                .border(1.dp, if (checked) palette.primary else palette.border, RoundedCornerShape(14.dp))
                .clickable(enabled = enabled) { onCheckedChange(!checked) }
                .semantics {
                    role = Role.Switch
                    contentDescription = label + ", " + (if (checked) "aktif" else "nonaktif") +
                        (if (enabled) "" else ", tidak tersedia")
                },
            contentAlignment = Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 3.dp)
                    .offset(x = (knobProgress * 20).dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (checked) palette.primary else palette.textMuted),
            )
        }
    }
}

@Composable
private fun StatusMiniPill(text: String, tint: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tint.copy(alpha = 0.14f))
            .border(1.dp, tint, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(text, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = tint, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun SimulasiPill() {
    val palette = LocalAppPalette.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(palette.info.copy(alpha = 0.12f))
            .border(1.dp, palette.info, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text("Simulasi", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.info)
    }
}

@Composable
private fun StatusBanner(message: String?, isError: Boolean) {
    val palette = LocalAppPalette.current
    val alpha by animateFloatAsState(
        targetValue = if (message != null) 1f else 0f,
        animationSpec = tween(200),
        label = "statusAlpha",
    )
    if (message != null) {
        val tint = if (isError) palette.danger else palette.success
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(alpha)
                .clip(RoundedCornerShape(12.dp))
                .background(tint.copy(alpha = 0.12f))
                .border(1.dp, tint, RoundedCornerShape(12.dp))
                .padding(12.dp)
                .semantics {
                    contentDescription = (if (isError) "Status gagal: " else "Status sukses: ") + (message ?: "")
                },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column {
                    Text(
                        text = if (isError) "Gagal" else "Selesai",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = tint,
                    )
                    Text(
                        text = message ?: "",
                        fontSize = 11.sp,
                        color = palette.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniSpinner() {
    val palette = LocalAppPalette.current
    val transition = rememberInfiniteTransition(label = "spinner")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Restart),
        label = "spinnerAngle",
    )
    Canvas(modifier = Modifier.size(18.dp)) {
        val w = this.size.width
        drawArc(
            color = palette.textSecondary,
            startAngle = angle,
            sweepAngle = 280f,
            useCenter = false,
            topLeft = Offset(w * 0.08f, w * 0.08f),
            size = Size(w * 0.84f, w * 0.84f),
            style = Stroke(width = w * 0.1f, cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun SceneIllustration() {
    val palette = LocalAppPalette.current
    val brush = remember {
        Brush.linearGradient(
            colors = listOf(palette.info.copy(alpha = 0.14f), palette.surfaceMuted),
        )
    }
    Box(modifier = Modifier.fillMaxSize().background(brush)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val stroke = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            // Papan menu
            drawRoundRect(
                color = palette.textMuted,
                topLeft = Offset(w * 0.1f, h * 0.12f),
                size = Size(w * 0.45f, h * 0.34f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx()),
                style = stroke,
            )
            drawLine(palette.textMuted, Offset(w * 0.15f, h * 0.22f), Offset(w * 0.42f, h * 0.22f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
            drawLine(palette.textMuted, Offset(w * 0.15f, h * 0.3f), Offset(w * 0.38f, h * 0.3f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
            drawLine(palette.textMuted, Offset(w * 0.15f, h * 0.38f), Offset(w * 0.34f, h * 0.38f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
            // Papan nama jalan
            drawRoundRect(
                color = palette.textMuted,
                topLeft = Offset(w * 0.58f, h * 0.55f),
                size = Size(w * 0.34f, h * 0.16f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
                style = stroke,
            )
            drawLine(palette.textMuted, Offset(w * 0.75f, h * 0.71f), Offset(w * 0.75f, h * 0.95f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
            // Siluet orang sederhana
            drawCircle(palette.textMuted, radius = w * 0.03f, center = Offset(w * 0.24f, h * 0.62f), style = stroke)
            drawLine(palette.textMuted, Offset(w * 0.24f, h * 0.65f), Offset(w * 0.24f, h * 0.8f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
        }
    }
}

@Composable
private fun MicGlyph(tint: Color) {
    Canvas(modifier = Modifier.size(30.dp)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val body = Path().apply {
            moveTo(w * 0.36f, h * 0.14f)
            lineTo(w * 0.64f, h * 0.14f)
            lineTo(w * 0.64f, h * 0.52f)
            lineTo(w * 0.36f, h * 0.52f)
            close()
        }
        drawPath(path = body, color = tint, style = stroke)
        drawArc(
            color = tint,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.24f, h * 0.24f),
            size = Size(w * 0.52f, h * 0.52f),
            style = stroke,
        )
        drawLine(tint, Offset(w * 0.5f, h * 0.76f), Offset(w * 0.5f, h * 0.9f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
    }
}

@Composable
private fun FlipGlyph(tint: Color, rotation: Float) {
    val modifier = Modifier.graphicsLayer { rotationZ = rotation }
    Canvas(modifier = modifier.size(24.dp)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.1f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val top = Path().apply {
            moveTo(w * 0.14f, h * 0.34f)
            lineTo(w * 0.82f, h * 0.34f)
            lineTo(w * 0.68f, h * 0.2f)
            moveTo(w * 0.82f, h * 0.34f)
            lineTo(w * 0.68f, h * 0.48f)
        }
        val bottom = Path().apply {
            moveTo(w * 0.86f, h * 0.66f)
            lineTo(w * 0.18f, h * 0.66f)
            lineTo(w * 0.32f, h * 0.52f)
            moveTo(w * 0.18f, h * 0.66f)
            lineTo(w * 0.32f, h * 0.8f)
        }
        drawPath(path = top, color = tint, style = stroke)
        drawPath(path = bottom, color = tint, style = stroke)
    }
}

@Composable
private fun StarGlyph(tint: Color, filled: Boolean, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.08f)
            lineTo(w * 0.63f, h * 0.38f)
            lineTo(w * 0.95f, h * 0.42f)
            lineTo(w * 0.71f, h * 0.63f)
            lineTo(w * 0.79f, h * 0.94f)
            lineTo(w * 0.5f, h * 0.77f)
            lineTo(w * 0.21f, h * 0.94f)
            lineTo(w * 0.29f, h * 0.63f)
            lineTo(w * 0.05f, h * 0.42f)
            lineTo(w * 0.37f, h * 0.38f)
            close()
        }
        if (filled) {
            drawPath(path = path, color = tint)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

@Composable
private fun EmptyChatGlyph(tint: Color, size: Dp = 42.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.06f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val path = Path().apply {
            moveTo(w * 0.1f, h * 0.2f)
            lineTo(w * 0.9f, h * 0.2f)
            lineTo(w * 0.9f, h * 0.66f)
            lineTo(w * 0.42f, h * 0.66f)
            lineTo(w * 0.24f, h * 0.84f)
            lineTo(w * 0.24f, h * 0.66f)
            lineTo(w * 0.1f, h * 0.66f)
            close()
        }
        drawPath(path = path, color = tint, style = stroke)
    }
}
