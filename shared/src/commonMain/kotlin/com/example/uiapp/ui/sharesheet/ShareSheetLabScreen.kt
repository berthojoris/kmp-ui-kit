package com.example.uiapp.ui.sharesheet

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class ShareDestination(
    val id: String,
    val label: String,
)

private data class ShareAction(
    val id: String,
    val label: String,
    val hint: String,
)

private sealed interface SendState {
    data object Idle : SendState
    data class Sending(val destinationLabel: String, val isRetry: Boolean) : SendState
    data class Success(val destinationLabel: String) : SendState
    data class Failed(val destinationLabel: String) : SendState
    data object Cancelled : SendState
}

private val shareDestinations = listOf(
    ShareDestination("airdrop", "AirDrop"),
    ShareDestination("pesan", "Pesan"),
    ShareDestination("mail", "Mail"),
    ShareDestination("salin", "Salin"),
)

private val shareActions = listOf(
    ShareAction("bacaan", "Tambahkan ke Bacaan", "Simpan artikel untuk dibaca nanti"),
    ShareAction("file", "Simpan ke File", "Arsipkan ke folder unduhan"),
    ShareAction("cetak", "Cetak", "Kirim ke printer terdekat"),
    ShareAction("tandai", "Tandai", "Beri anotasi sebelum dibagikan"),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShareSheetLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var sheetVisible by remember { mutableStateOf(false) }
    var sendState by remember { mutableStateOf<SendState>(SendState.Idle) }
    var sendProgress by remember { mutableFloatStateOf(0f) }
    var sendToken by remember { mutableIntStateOf(0) }
    var failRequested by remember { mutableStateOf(false) }
    var optionsOpen by remember { mutableStateOf(false) }
    var lastNote by remember { mutableStateOf("Belum ada aktivitas berbagi") }

    val sheetOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Mesin progres kirim simulasi: tick coroutine, tanpa timer thread.
    LaunchedEffect(sendToken) {
        val sending = sendState as? SendState.Sending ?: return@LaunchedEffect
        sendProgress = 0f
        while (sendProgress < 1f) {
            delay(140)
            if (sendState !is SendState.Sending) return@LaunchedEffect
            sendProgress = (sendProgress + 0.09f).coerceAtMost(1f)
        }
        delay(180)
        sendState = if (failRequested) {
            SendState.Failed(sending.destinationLabel)
        } else {
            SendState.Success(sending.destinationLabel)
        }
    }

    fun closeSheet() {
        sheetVisible = false
        sendState = SendState.Idle
        optionsOpen = false
        scope.launch { sheetOffset.snapTo(0f) }
    }

    fun startSend(destination: ShareDestination, isRetry: Boolean) {
        failRequested = destination.id == "mail" && !isRetry
        sendState = SendState.Sending(destination.label, isRetry)
        sendToken++
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Share Sheet & Activity View",
                subtitle = "Sheet berbagi ala iOS — Simulasi",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = palette.info.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, palette.info),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Simulasi", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = palette.info)
                        Text(
                            text = "Seluruh tujuan berbagi adalah data mock. Tidak ada aplikasi atau layanan sistem yang dihubungi.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = palette.textSecondary,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                SharePreviewCard()

                Surface(
                    onClick = {
                        sendState = SendState.Idle
                        sendProgress = 0f
                        optionsOpen = false
                        sheetVisible = true
                        scope.launch { sheetOffset.snapTo(0f) }
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = palette.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            role = Role.Button
                            contentDescription = "Buka lembar berbagi simulasi"
                        },
                ) {
                    Text(
                        text = "Bagikan Konten…",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.onPrimary,
                        modifier = Modifier.padding(vertical = 14.dp),
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Status Terakhir", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(lastNote, fontSize = 12.sp, lineHeight = 17.sp, color = palette.textSecondary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Pola Share Sheet & Activity View:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Hierarki tiga tingkat: pratinjau konten, deret app tujuan, lalu daftar aksi vertikal", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• Drag ke bawah membuang sheet; tarikan kurang dari ambang kembali ke posisi semula", fontSize = 12.sp, color = palette.textSecondary)
                        Text("• State kirim eksplisit: progres persen, sukses, gagal dengan coba lagi, dan batal", fontSize = 12.sp, color = palette.textSecondary)
                    }
                }
            }

            if (sheetVisible) {
                ShareSheetOverlay(
                    sendState = sendState,
                    sendProgress = sendProgress,
                    optionsOpen = optionsOpen,
                    sheetOffset = sheetOffset,
                    onDragOffset = { delta ->
                        scope.launch {
                            sheetOffset.snapTo((sheetOffset.value + delta).coerceAtLeast(0f))
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            val threshold = with(density) { 110.dp.toPx() }
                            if (sheetOffset.value > threshold) {
                                sheetOffset.animateTo(
                                    targetValue = 1400f,
                                    animationSpec = tween(220, easing = LinearEasing),
                                )
                                closeSheet()
                            } else {
                                sheetOffset.animateTo(0f, animationSpec = tween(180))
                            }
                        }
                    },
                    onDismiss = { closeSheet() },
                    onSelectDestination = { destination ->
                        if (sendState is SendState.Sending) return@ShareSheetOverlay
                        if (destination.id == "salin") {
                            sendState = SendState.Success(destination.label)
                            lastNote = "Konten disalin ke papan klip (Simulasi)"
                        } else {
                            startSend(destination, isRetry = false)
                        }
                    },
                    onRetry = {
                        val failed = sendState as? SendState.Failed ?: return@ShareSheetOverlay
                        startSend(ShareDestination("mail", failed.destinationLabel), isRetry = true)
                    },
                    onCancelSend = {
                        val sending = sendState as? SendState.Sending ?: return@ShareSheetOverlay
                        sendState = SendState.Cancelled
                        lastNote = "Pengiriman ke ${sending.destinationLabel} dibatalkan (Simulasi)"
                    },
                    onSuccessDone = { label -> lastNote = "Berhasil dibagikan ke $label (Simulasi)" },
                    onFailedDone = { label -> lastNote = "Gagal membagikan ke $label (Simulasi)" },
                    onCancelledDone = { lastNote = "Pengiriman dibatalkan (Simulasi)" },
                    onToggleOptions = { optionsOpen = !optionsOpen },
                    onAction = { action ->
                        lastNote = "Aksi \"${action.label}\" dipilih (Simulasi)"
                        closeSheet()
                    },
                )
            }
        }
    }
}

@Composable
private fun SharePreviewCard() {
    val palette = LocalAppPalette.current
    val thumbBrush = remember {
        Brush.linearGradient(
            colors = listOf(palette.info.copy(alpha = 0.85f), palette.primary.copy(alpha = 0.85f)),
        )
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(thumbBrush)
                    .border(1.dp, palette.border, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) {
                DocGlyph(tint = palette.onPrimary)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Panduan Desain Antarmuka 2026",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = palette.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text("laboratorium.uiapp.local", fontSize = 11.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("PDF · 2,4 MB · konten contoh", fontSize = 11.sp, color = palette.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun ShareSheetOverlay(
    sendState: SendState,
    sendProgress: Float,
    optionsOpen: Boolean,
    sheetOffset: Animatable<Float, *>,
    onDragOffset: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onDismiss: () -> Unit,
    onSelectDestination: (ShareDestination) -> Unit,
    onRetry: () -> Unit,
    onCancelSend: () -> Unit,
    onSuccessDone: (String) -> Unit,
    onFailedDone: (String) -> Unit,
    onCancelledDone: () -> Unit,
    onToggleOptions: () -> Unit,
    onAction: (ShareAction) -> Unit,
) {
    val palette = LocalAppPalette.current
    val thumbBrush = remember {
        Brush.linearGradient(
            colors = listOf(palette.info.copy(alpha = 0.85f), palette.primary.copy(alpha = 0.85f)),
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(palette.textPrimary.copy(alpha = 0.45f))
                .clickable(enabled = sendState !is SendState.Sending) { onDismiss() }
                .semantics { contentDescription = "Tutup lembar berbagi" },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 720.dp)
                .align(Alignment.BottomCenter)
                .offset { IntOffset(0, sheetOffset.value.roundToInt()) }
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(palette.surfaceMuted)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = { onDragEnd() },
                        onVerticalDrag = { _, dragAmount -> onDragOffset(dragAmount) },
                    )
                }
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(palette.border),
            )

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surface)
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(thumbBrush),
                        contentAlignment = Alignment.Center,
                    ) {
                        DocGlyph(tint = palette.onPrimary)
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            "Panduan Desain Antarmuka 2026",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = palette.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text("laboratorium.uiapp.local", fontSize = 11.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    SimulasiPill()
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    shareDestinations.forEach { destination ->
                        DestinationCell(
                            destination = destination,
                            sendState = sendState,
                            onClick = { onSelectDestination(destination) },
                        )
                    }
                }

                when (val state = sendState) {
                    is SendState.Sending -> SendProgressPanel(
                        destinationLabel = state.destinationLabel,
                        progress = sendProgress,
                        onCancelSend = onCancelSend,
                    )
                    is SendState.Success -> SendResultPanel(
                        label = "Terkirim ke ${state.destinationLabel}",
                        detail = "Konten berhasil dibagikan (Simulasi)",
                        accent = palette.success,
                        actionLabel = "Selesai",
                        onAction = { onSuccessDone(state.destinationLabel) },
                    )
                    is SendState.Failed -> SendResultPanel(
                        label = "Gagal mengirim ke ${state.destinationLabel}",
                        detail = "Koneksi tujuan terputus (Simulasi)",
                        accent = palette.danger,
                        actionLabel = "Coba Lagi",
                        onAction = onRetry,
                    )
                    SendState.Cancelled -> SendResultPanel(
                        label = "Pengiriman dibatalkan",
                        detail = "Tidak ada konten yang terkirim (Simulasi)",
                        accent = palette.warning,
                        actionLabel = "Tutup",
                        onAction = onCancelledDone,
                    )
                    SendState.Idle -> Unit
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surface)
                        .border(1.dp, palette.border, RoundedCornerShape(12.dp)),
                ) {
                    shareActions.forEachIndexed { index, action ->
                        if (index > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(palette.border),
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = sendState !is SendState.Sending) { onAction(action) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(action.label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                                Text(action.hint, fontSize = 11.sp, color = palette.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            ChevronGlyph(tint = palette.textMuted)
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        onClick = onToggleOptions,
                        shape = RoundedCornerShape(12.dp),
                        color = palette.surface,
                        border = BorderStroke(1.dp, palette.border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                role = Role.Button
                                contentDescription = "Buka opsi pengiriman"
                            },
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Opsi…", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
                            Text(if (optionsOpen) "Tutup" else "Buka", fontSize = 11.sp, color = palette.primary)
                        }
                    }
                    if (optionsOpen) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OptionsPanel()
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Batal",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(enabled = sendState !is SendState.Sending) { onDismiss() }
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                        .semantics {
                            role = Role.Button
                            contentDescription = "Batalkan dan tutup lembar berbagi"
                        },
                )
            }
        }
    }
}

@Composable
private fun DestinationCell(
    destination: ShareDestination,
    sendState: SendState,
    onClick: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val sendingThis = (sendState as? SendState.Sending)?.destinationLabel == destination.label
    val doneThis = (sendState as? SendState.Success)?.destinationLabel == destination.label

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .width(68.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = sendState !is SendState.Sending) { onClick() }
            .padding(vertical = 6.dp)
            .semantics {
                role = Role.Button
                contentDescription = "Kirim ke ${destination.label} (Simulasi)"
            },
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(palette.primaryContainer)
                .border(
                    BorderStroke(if (sendingThis || doneThis) 2.dp else 1.dp, if (doneThis) palette.success else palette.border),
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            when (destination.id) {
                "airdrop" -> RadarRippleGlyph(tint = palette.onPrimaryContainer, active = sendingThis)
                "pesan" -> BubbleGlyph(tint = palette.onPrimaryContainer)
                "mail" -> EnvelopeGlyph(tint = palette.onPrimaryContainer)
                else -> CopyGlyph(tint = palette.onPrimaryContainer)
            }
        }
        Text(
            destination.label,
            fontSize = 10.sp,
            color = palette.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        when {
            sendingThis -> Text("Mengirim…", fontSize = 9.sp, color = palette.info, fontWeight = FontWeight.Bold, maxLines = 1)
            doneThis -> Text("Terkirim", fontSize = 9.sp, color = palette.success, fontWeight = FontWeight.Bold, maxLines = 1)
            else -> Text("Siap", fontSize = 9.sp, color = palette.textMuted, maxLines = 1)
        }
    }
}

@Composable
private fun SendProgressPanel(
    destinationLabel: String,
    progress: Float,
    onCancelSend: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Mengirim ke $destinationLabel",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text("${(progress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.info)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(palette.surfaceMuted)
                .border(1.dp, palette.border, CircleShape),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress.coerceIn(0.02f, 1f))
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(palette.info),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                onClick = onCancelSend,
                shape = RoundedCornerShape(10.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.danger),
                modifier = Modifier.semantics {
                    role = Role.Button
                    contentDescription = "Batalkan pengiriman"
                },
            ) {
                Text(
                    "Batal",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.danger,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
            Text(
                "Membatalkan tidak mengirim apa pun (Simulasi)",
                fontSize = 10.sp,
                color = palette.textMuted,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SendResultPanel(
    label: String,
    detail: String,
    accent: Color,
    actionLabel: String,
    onAction: () -> Unit,
) {
    val palette = LocalAppPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = 0.12f))
            .border(1.dp, accent, RoundedCornerShape(12.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusMarkGlyph(tint = accent)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(detail, fontSize = 10.sp, color = palette.textSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        Surface(
            onClick = onAction,
            shape = RoundedCornerShape(10.dp),
            color = accent,
            modifier = Modifier.semantics {
                role = Role.Button
                contentDescription = actionLabel
            },
        ) {
            Text(
                actionLabel,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = palette.onPrimary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun OptionsPanel() {
    val palette = LocalAppPalette.current
    var format by remember { mutableStateOf("Asli") }
    var includeNotes by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Opsi Pengiriman (Simulasi)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Asli", "PDF", "Tautan").forEach { option ->
                val selected = format == option
                Surface(
                    onClick = { format = option },
                    shape = CircleShape,
                    color = if (selected) palette.primary else palette.surface,
                    border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
                ) {
                    Text(
                        option,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selected) palette.onPrimary else palette.textSecondary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { includeNotes = !includeNotes }
                .semantics {
                    role = Role.Button
                    contentDescription = if (includeNotes) "Sertakan catatan aktif" else "Sertakan catatan nonaktif"
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Sertakan catatan anotasi", fontSize = 12.sp, color = palette.textSecondary)
            Box(
                modifier = Modifier
                    .size(width = 38.dp, height = 22.dp)
                    .clip(CircleShape)
                    .background(if (includeNotes) palette.primary else palette.surfaceMuted)
                    .border(1.dp, palette.border, CircleShape),
                contentAlignment = if (includeNotes) Alignment.CenterEnd else Alignment.CenterStart,
            ) {
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (includeNotes) palette.onPrimary else palette.textMuted),
                )
            }
        }
    }
}

@Composable
private fun SimulasiPill() {
    val palette = LocalAppPalette.current
    Surface(
        shape = CircleShape,
        color = palette.warning.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, palette.warning),
    ) {
        Text(
            "Simulasi",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun CanvasGlyph(
    modifier: Modifier = Modifier,
    drawBlock: DrawScope.() -> Unit,
) {
    Canvas(modifier = modifier.size(28.dp)) { drawBlock() }
}

@Composable
private fun DocGlyph(tint: Color) {
    CanvasGlyph {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.30f, h * 0.16f)
            lineTo(w * 0.58f, h * 0.16f)
            lineTo(w * 0.72f, h * 0.32f)
            lineTo(w * 0.72f, h * 0.84f)
            lineTo(w * 0.30f, h * 0.84f)
            close()
        }
        drawPath(path, color = tint, style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(tint, Offset(w * 0.38f, h * 0.44f), Offset(w * 0.64f, h * 0.44f), 3f, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.38f, h * 0.58f), Offset(w * 0.64f, h * 0.58f), 3f, StrokeCap.Round)
    }
}

@Composable
private fun BubbleGlyph(tint: Color) {
    CanvasGlyph {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            addOval(Rect(w * 0.16f, h * 0.22f, w * 0.84f, h * 0.66f))
            moveTo(w * 0.36f, h * 0.62f)
            lineTo(w * 0.32f, h * 0.80f)
            lineTo(w * 0.52f, h * 0.64f)
            close()
        }
        drawPath(path, color = tint, style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun EnvelopeGlyph(tint: Color) {
    CanvasGlyph {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.16f, h * 0.28f)
            lineTo(w * 0.84f, h * 0.28f)
            lineTo(w * 0.84f, h * 0.72f)
            lineTo(w * 0.16f, h * 0.72f)
            close()
        }
        drawPath(path, color = tint, style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(tint, Offset(w * 0.16f, h * 0.30f), Offset(w * 0.50f, h * 0.54f), 3f, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.84f, h * 0.30f), Offset(w * 0.50f, h * 0.54f), 3f, StrokeCap.Round)
    }
}

@Composable
private fun CopyGlyph(tint: Color) {
    CanvasGlyph {
        val w = size.width
        val h = size.height
        val pathA = Path().apply {
            moveTo(w * 0.22f, h * 0.18f)
            lineTo(w * 0.58f, h * 0.18f)
            lineTo(w * 0.58f, h * 0.62f)
            lineTo(w * 0.22f, h * 0.62f)
            close()
        }
        val pathB = Path().apply {
            moveTo(w * 0.40f, h * 0.38f)
            lineTo(w * 0.76f, h * 0.38f)
            lineTo(w * 0.76f, h * 0.82f)
            lineTo(w * 0.40f, h * 0.82f)
            close()
        }
        drawPath(pathA, color = tint, style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(pathB, color = tint, style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun RadarRippleGlyph(tint: Color, active: Boolean) {
    val transition = rememberInfiniteTransition(label = "radarRipple")
    val ripple by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable<Float>(tween(1400, easing = LinearEasing)),
        label = "ripple",
    )

    CanvasGlyph {
        val w = size.width
        val center = Offset(w / 2f, size.height / 2f)
        drawCircle(tint, radius = w * 0.10f, center = center)
        val alpha = if (active) (1f - ripple) else 0.35f
        drawCircle(
            color = tint.copy(alpha = alpha.coerceIn(0.15f, 0.8f)),
            radius = w * (0.18f + 0.18f * ripple),
            center = center,
            style = Stroke(width = 3f, cap = StrokeCap.Round),
        )
        drawCircle(
            color = tint.copy(alpha = (alpha * 0.6f).coerceIn(0.12f, 0.5f)),
            radius = w * (0.32f + 0.14f * ripple),
            center = center,
            style = Stroke(width = 3f, cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun ChevronGlyph(tint: Color) {
    CanvasGlyph {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.38f, h * 0.28f)
            lineTo(w * 0.64f, h * 0.50f)
            lineTo(w * 0.38f, h * 0.72f)
        }
        drawPath(path, color = tint, style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun StatusMarkGlyph(tint: Color) {
    CanvasGlyph {
        val w = size.width
        val h = size.height
        drawCircle(tint, radius = w * 0.42f, style = Stroke(width = 3f, cap = StrokeCap.Round))
        val path = Path().apply {
            moveTo(w * 0.30f, h * 0.52f)
            lineTo(w * 0.45f, h * 0.66f)
            lineTo(w * 0.72f, h * 0.36f)
        }
        drawPath(path, color = tint, style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
