package com.example.uiapp.ui.callui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
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
import kotlinx.coroutines.delay

private enum class CallState(val label: String) {
    IDLE("Siaga"),
    RINGING("Berderang"),
    CONNECTING("Menunggu jaringan"),
    ACTIVE("Terhubung"),
    ENDED("Panggilan berakhir"),
}

private enum class CallVariant(val label: String) {
    PHONE("Telepon"),
    FACETIME("FaceTime Grid"),
}

private enum class GridLayout(val label: String) {
    GRID_2X2("Grid 2×2"),
    TILES_4("4 Tile Sejajar"),
}

private data class Participant(val id: Int, val name: String, val initials: String)

private val mockCaller = Participant(1, "Sari Wijaya", "SW")

private val mockExtraParticipants = listOf(
    Participant(2, "Bagas Pratama", "BP"),
    Participant(3, "Dewi Lestari", "DL"),
    Participant(4, "Rian Saputra", "RS"),
)

private val keypadButtons = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
    listOf("*", "0", "#"),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CallUiLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var callState by remember { mutableStateOf(CallState.IDLE) }
    var variant by remember { mutableStateOf(CallVariant.PHONE) }
    var gridLayout by remember { mutableStateOf(GridLayout.GRID_2X2) }
    var muted by remember { mutableStateOf(false) }
    var speaker by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf(false) }
    var cameraFront by remember { mutableStateOf(true) }
    var weakConnection by remember { mutableStateOf(false) }
    var keypadOpen by remember { mutableStateOf(false) }
    var dialed by remember { mutableStateOf("") }
    var callSeconds by remember { mutableIntStateOf(0) }
    var sessionToken by remember { mutableIntStateOf(0) }
    var answerToken by remember { mutableIntStateOf(0) }
    var nextParticipantId by remember { mutableIntStateOf(10) }

    val participants = remember { mutableStateListOf(mockCaller) }

    // Timer durasi panggilan: berjalan hanya saat terhubung.
    LaunchedEffect(sessionToken) {
        while (callState == CallState.ACTIVE) {
            delay(1000)
            callSeconds++
        }
    }

    // Aksi meredup sebelum dijawab, lalu transisi ke layar panggilan aktif.
    LaunchedEffect(answerToken) {
        if (answerToken > 0) {
            delay(320)
            callState = CallState.CONNECTING
            delay(1300)
            callState = CallState.ACTIVE
            callSeconds = 0
            sessionToken++
        }
    }

    val actionsAlpha by animateFloatAsState(
        targetValue = if (answerToken > 0 && callState == CallState.RINGING) 0.15f else 1f,
        animationSpec = tween(280),
        label = "actionsAlpha",
    )

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Incoming Call & FaceTime UI",
                subtitle = "Simulasi panggilan masuk & peserta",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LabIntroCard(
                title = "Simulasi layar panggilan iOS",
                body = "Seluruh panggilan, peserta, dan koneksi di lab ini adalah SIMULASI dengan data mock lokal. " +
                    "Tidak ada telepon, mikrofon, kamera, atau jaringan nyata yang dipakai.",
            )

            // Pemilih varian & tata letak grid
            LabCard {
                LabSectionTitle(title = "Varian Panggilan", subtitle = "Pilih gaya layar yang ingin didemokan")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CallVariant.entries.forEach { v ->
                        SelectChip(
                            label = v.label,
                            selected = variant == v,
                            modifier = Modifier.weight(1f),
                            onClick = { variant = v },
                        )
                    }
                }
                if (variant == CallVariant.FACETIME) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        GridLayout.entries.forEach { g ->
                            SelectChip(
                                label = g.label,
                                selected = gridLayout == g,
                                modifier = Modifier.weight(1f),
                                onClick = { gridLayout = g },
                            )
                        }
                    }
                }
            }

            // Layar panggilan (mock)
            CallScreenPanel(
                callState = callState,
                variant = variant,
                gridLayout = gridLayout,
                muted = muted,
                speaker = speaker,
                recording = recording,
                cameraFront = cameraFront,
                weakConnection = weakConnection,
                dialed = dialed,
                callSeconds = callSeconds,
                participants = participants,
                actionsAlpha = actionsAlpha,
                onAnswer = {
                    callSeconds = 0
                    answerToken++
                },
                onDecline = {
                    callState = CallState.ENDED
                    callSeconds = 0
                },
                onHangUp = {
                    callState = CallState.ENDED
                },
                onStartRing = {
                    participants.clear()
                    participants.add(mockCaller)
                    dialed = ""
                    muted = false
                    speaker = false
                    recording = false
                    keypadOpen = false
                    callSeconds = 0
                    answerToken = 0
                    callState = CallState.RINGING
                },
            )

            // Kontrol panggilan aktif
            LabCard {
                LabSectionTitle(title = "Kontrol Panggilan", subtitle = "Semua tombol hanya memengaruhi simulasi lokal")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    CallControlButton(
                        emoji = "🎤",
                        label = "Bisu",
                        active = muted,
                        modifier = Modifier.weight(1f),
                        onClick = { muted = !muted },
                    )
                    CallControlButton(
                        emoji = "🔢",
                        label = "Keypad",
                        active = keypadOpen,
                        modifier = Modifier.weight(1f),
                        onClick = { keypadOpen = !keypadOpen },
                    )
                    CallControlButton(
                        emoji = "🔊",
                        label = "Speaker",
                        active = speaker,
                        modifier = Modifier.weight(1f),
                        onClick = { speaker = !speaker },
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    CallControlButton(
                        emoji = "⏺",
                        label = "Rekam",
                        active = recording,
                        modifier = Modifier.weight(1f),
                        onClick = { recording = !recording },
                    )
                    CallControlButton(
                        emoji = "➕",
                        label = "Peserta",
                        active = false,
                        enabled = participants.size < 4 && callState == CallState.ACTIVE,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val poolIdx = (participants.size - 1).coerceIn(0, mockExtraParticipants.size - 1)
                            val extra = mockExtraParticipants[poolIdx]
                            participants.add(extra.copy(id = nextParticipantId))
                            nextParticipantId++
                        },
                    )
                    CallControlButton(
                        emoji = "🔄",
                        label = "Flip Kamera",
                        active = !cameraFront,
                        modifier = Modifier.weight(1f),
                        onClick = { cameraFront = !cameraFront },
                    )
                }
                Text(
                    text = "Status kontrol: " +
                        "Bisu ${if (muted) "aktif" else "nonaktif"} · " +
                        "Speaker ${if (speaker) "aktif" else "nonaktif"} · " +
                        "Rekam ${if (recording) "aktif" else "nonaktif"} · " +
                        "Kamera ${if (cameraFront) "depan" else "belakang"} (Simulasi)",
                    fontSize = 10.sp,
                    color = palette.textSecondary,
                    lineHeight = 15.sp,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SelectChip(
                        label = if (weakConnection) "Koneksi lemah: AKTIF" else "Simulasikan koneksi lemah",
                        selected = weakConnection,
                        modifier = Modifier.weight(1f),
                        onClick = { weakConnection = !weakConnection },
                    )
                    SelectChip(
                        label = "Reset peserta",
                        selected = false,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            participants.clear()
                            participants.add(mockCaller)
                            dialed = ""
                        },
                    )
                }
                Text(
                    text = "Koneksi saat ini: " +
                        if (weakConnection) "Lemah — kualitas audio menurun (Simulasi)" else "Normal (Simulasi)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (weakConnection) palette.warning else palette.textMuted,
                )
            }

            // Keypad
            if (keypadOpen) {
                LabCard {
                    LabSectionTitle(title = "Keypad", subtitle = "Ketuk angka untuk membangun nomor panggilan simulasi")
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = palette.surfaceMuted,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = if (dialed.isEmpty()) "Nomor: (kosong)" else "Nomor: $dialed",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if (dialed.isEmpty()) palette.textMuted else palette.textPrimary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                        )
                    }
                    keypadButtons.forEach { rowKeys ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            rowKeys.forEach { key ->
                                Surface(
                                    onClick = { if (dialed.length < 14) dialed += key },
                                    shape = RoundedCornerShape(12.dp),
                                    color = palette.surface,
                                    border = BorderStroke(1.dp, palette.border),
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        text = key,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        color = palette.textPrimary,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 14.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Catatan pola
            LabCard {
                LabSectionTitle(title = "Catatan Desain", subtitle = "Kenapa pola ini efektif")
                Text("• Aksi utama meredup saat dijawab, memberi sinyal transisi yang halus ke layar aktif", fontSize = 12.sp, color = palette.textSecondary, lineHeight = 17.sp)
                Text("• Status koneksi ditampilkan sebagai label teks, bukan warna saja (aksesibel)", fontSize = 12.sp, color = palette.textSecondary, lineHeight = 17.sp)
                Text("• Grid peserta adaptif memakai BoxWithConstraints untuk layar sempit", fontSize = 12.sp, color = palette.textSecondary, lineHeight = 17.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun CallScreenPanel(
    callState: CallState,
    variant: CallVariant,
    gridLayout: GridLayout,
    muted: Boolean,
    speaker: Boolean,
    recording: Boolean,
    cameraFront: Boolean,
    weakConnection: Boolean,
    dialed: String,
    callSeconds: Int,
    participants: List<Participant>,
    actionsAlpha: Float,
    onAnswer: () -> Unit,
    onDecline: () -> Unit,
    onHangUp: () -> Unit,
    onStartRing: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val panelBrush = remember(palette) {
        Brush.linearGradient(
            listOf(palette.primary.copy(alpha = 0.18f), palette.surfaceMuted),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(panelBrush)
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Status: ${callState.label}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textSecondary,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (recording && callState == CallState.ACTIVE) {
                    RecordPill()
                }
                StatusPill(
                    text = if (weakConnection && callState == CallState.ACTIVE) "Koneksi lemah (Simulasi)" else "Simulasi",
                    tone = if (weakConnection && callState == CallState.ACTIVE) palette.warning else palette.info,
                )
            }
        }

        when (callState) {
            CallState.IDLE -> {
                BigAvatar(initials = mockCaller.initials, pulsing = false)
                Text("Tidak ada panggilan aktif", fontSize = 13.sp, color = palette.textSecondary)
                Text("Semua panggilan di sini adalah simulasi lokal", fontSize = 11.sp, color = palette.textMuted)
                RoundActionButton(
                    emoji = "📞",
                    label = "Simulasikan Panggilan Masuk",
                    tone = palette.primary,
                    contentTone = palette.onPrimary,
                    modifier = Modifier.fillMaxWidth(0.85f),
                    onClick = onStartRing,
                )
            }
            CallState.RINGING -> {
                BigAvatar(initials = mockCaller.initials, pulsing = true)
                Text(mockCaller.name, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = palette.textPrimary)
                Text(
                    text = if (variant == CallVariant.FACETIME) "Panggilan FaceTime masuk · Simulasi" else "Panggilan telepon masuk · Simulasi",
                    fontSize = 12.sp,
                    color = palette.textMuted,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(actionsAlpha),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    RoundActionButton(
                        emoji = "📵",
                        label = "Tolak",
                        tone = palette.danger.copy(alpha = 0.15f),
                        contentTone = palette.danger,
                        onClick = onDecline,
                    )
                    RoundActionButton(
                        emoji = "📞",
                        label = "Terima",
                        tone = palette.success.copy(alpha = 0.15f),
                        contentTone = palette.success,
                        onClick = onAnswer,
                    )
                }
                Text("Aksi akan meredup sebelum tersambung", fontSize = 10.sp, color = palette.textMuted)
            }
            CallState.CONNECTING -> {
                BigAvatar(initials = mockCaller.initials, pulsing = false)
                Text(mockCaller.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                Text("Menunggu jaringan…", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.info)
                Text("Penyambungan simulasi, tanpa jaringan nyata", fontSize = 10.sp, color = palette.textMuted)
            }
            CallState.ACTIVE -> {
                if (variant == CallVariant.FACETIME) {
                    ParticipantGrid(participants = participants, gridLayout = gridLayout)
                    Text(
                        text = "${participants.size} peserta · Kamera ${if (cameraFront) "depan" else "belakang"} (Simulasi)",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                } else {
                    BigAvatar(initials = mockCaller.initials, pulsing = false)
                    Text(mockCaller.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                    Text("Terhubung · ${formatDuration(callSeconds)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.success)
                    if (dialed.isNotEmpty()) {
                        Text("Nomor ditekan: $dialed", fontSize = 11.sp, color = palette.textMuted)
                    }
                }
                if (weakConnection) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = palette.warning.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, palette.warning),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "⚠ Koneksi lemah — audio mungkin terputus (Simulasi)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.warning,
                            modifier = Modifier.padding(10.dp),
                        )
                    }
                }
                RoundActionButton(
                    emoji = "📵",
                    label = "Akhiri Panggilan",
                    tone = palette.danger.copy(alpha = 0.15f),
                    contentTone = palette.danger,
                    modifier = Modifier.fillMaxWidth(0.85f),
                    onClick = onHangUp,
                )
            }
            CallState.ENDED -> {
                BigAvatar(initials = mockCaller.initials, pulsing = false)
                Text("Panggilan berakhir", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                Text("Durasi simulasi: ${formatDuration(callSeconds)}", fontSize = 12.sp, color = palette.textSecondary)
                RoundActionButton(
                    emoji = "📞",
                    label = "Panggilan Masuk Lagi",
                    tone = palette.primary,
                    contentTone = palette.onPrimary,
                    modifier = Modifier.fillMaxWidth(0.85f),
                    onClick = onStartRing,
                )
            }
        }
    }
}

@Composable
private fun BigAvatar(initials: String, pulsing: Boolean) {
    val palette = LocalAppPalette.current
    val brush = remember(palette) {
        Brush.linearGradient(listOf(palette.primary.copy(alpha = 0.9f), palette.info.copy(alpha = 0.55f)))
    }
    val pulse by animateFloatAsState(
        targetValue = if (pulsing) 0.45f else 0f,
        animationSpec = tween(700),
        label = "pulse",
    )
    Box(contentAlignment = Alignment.Center) {
        if (pulsing) {
            Box(
                modifier = Modifier
                    .size(132.dp)
                    .clip(CircleShape)
                    .background(palette.success.copy(alpha = pulse * 0.4f))
                    .border(1.dp, palette.success.copy(alpha = pulse), CircleShape),
            )
        }
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(brush)
                .border(1.dp, palette.border, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(initials, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = palette.onPrimary)
        }
    }
}

@Composable
private fun ParticipantGrid(participants: List<Participant>, gridLayout: GridLayout) {
    val palette = LocalAppPalette.current
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val forcedTwoRow = maxWidth < 320.dp
        val singleRow = gridLayout == GridLayout.TILES_4 && !forcedTwoRow
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (singleRow) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    participants.forEach { p ->
                        ParticipantTile(participant = p, modifier = Modifier.weight(1f))
                    }
                }
            } else {
                val rows = remember(participants.size) { participants.chunked(2) }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    rows.forEach { rowParticipants ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            rowParticipants.forEach { p ->
                                ParticipantTile(participant = p, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            Text(
                text = "Tata letak: ${gridLayout.label}${if (forcedTwoRow) " (disebabkan layar sempit)" else ""}",
                fontSize = 10.sp,
                color = palette.textMuted,
            )
        }
    }
}

@Composable
private fun ParticipantTile(participant: Participant, modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    val brush = remember(palette, participant.id) {
        Brush.linearGradient(
            listOf(
                palette.primary.copy(alpha = 0.75f),
                palette.info.copy(alpha = 0.45f + (participant.id % 3) * 0.12f),
            ),
        )
    }
    Column(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(brush)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(participant.initials, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = palette.onPrimary)
        Text(
            text = participant.name,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CallControlButton(
    emoji: String,
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val bg = when {
        !enabled -> palette.surfaceMuted
        active -> palette.primary
        else -> palette.surface
    }
    val fg = when {
        !enabled -> palette.textMuted
        active -> palette.onPrimary
        else -> palette.textPrimary
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, if (active) palette.primary else palette.border, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .semantics {
                role = Role.Button
                contentDescription = "$label — ${if (active) "aktif" else "nonaktif"}${if (!enabled) " (tidak tersedia)" else ""}"
            }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(emoji, fontSize = 18.sp)
        Text(
            text = if (active) "$label (aktif)" else label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = fg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RoundActionButton(
    emoji: String,
    label: String,
    tone: androidx.compose.ui.graphics.Color,
    contentTone: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(tone)
                .border(1.dp, contentTone.copy(alpha = 0.6f), CircleShape)
                .clickable { onClick() }
                .semantics {
                    role = Role.Button
                    contentDescription = label
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(emoji, fontSize = 24.sp)
        }
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textSecondary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SelectChip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
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
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 9.dp),
        )
    }
}

@Composable
private fun StatusPill(text: String, tone: androidx.compose.ui.graphics.Color) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = tone.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, tone),
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = tone,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun RecordPill() {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = palette.danger.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, palette.danger),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(palette.danger),
            )
            Text("Merekam (Simulasi)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.danger)
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    val mm = if (m < 10) "0$m" else "$m"
    val ss = if (s < 10) "0$s" else "$s"
    return "$mm:$ss"
}
