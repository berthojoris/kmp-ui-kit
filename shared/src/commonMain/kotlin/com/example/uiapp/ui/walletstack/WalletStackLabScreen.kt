package com.example.uiapp.ui.walletstack

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.uiapp.theme.AppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private enum class PassKind(val label: String) {
    TIKET("Tiket"),
    BOARDING("Boarding Pass"),
    MEMBER("Membership"),
    VOUCHER("Voucher"),
}

private data class WalletPass(
    val id: Int,
    val kind: PassKind,
    val title: String,
    val subtitle: String,
    val holder: String,
    val code: String,
    val expired: Boolean = false,
    val express: Boolean = false,
)

private val SEED_PASSES = listOf(
    WalletPass(1, PassKind.TIKET, "Konser Jazz Bintang 2026", "Festival · Area A", "Nadia P.", "TKT-2026-0041"),
    WalletPass(2, PassKind.BOARDING, "CGK → DPS", "GA 420 · Gerbang 7", "Nadia P.", "GA-420-7F"),
    WalletPass(3, PassKind.MEMBER, "Kopi Senja Member", "Kartu Anggota · Gold", "Nadia P.", "MBR-88213"),
    WalletPass(4, PassKind.VOUCHER, "Voucher Diskon 25%", "Berlaku s.d. 30 Nov", "Nadia P.", "VCR-25-OFF"),
)

private val ADD_POOL = listOf(
    WalletPass(101, PassKind.TIKET, "Tiket Bioskop Senja", "Studio 3 · Baris F", "Nadia P.", "CIN-77120"),
    WalletPass(102, PassKind.MEMBER, "Kartu Perpustakaan Kota", "Anggota Aktif", "Nadia P.", "LIB-33410"),
    WalletPass(103, PassKind.VOUCHER, "Voucher Ongkir Gratis", "Berlaku s.d. 31 Des", "Nadia P.", "SHP-FREE1"),
    WalletPass(104, PassKind.BOARDING, "SUB → CGK", "GA 321 · Gerbang 2", "Nadia P.", "GA-321-2A"),
)

private val CARD_HEIGHT = 206.dp
private val COLLAPSED_STEP = 30.dp
private val EXPANDED_STEP = 132.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WalletStackLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    val passes = remember { mutableStateListOf<WalletPass>().apply { addAll(SEED_PASSES) } }
    var expanded by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }
    var nextId by remember { mutableIntStateOf(200) }
    var addTick by remember { mutableIntStateOf(0) }
    var adding by remember { mutableStateOf(false) }
    var failNextAdd by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf<String?>("Tumpukan siap — drag ke bawah untuk membuka") }
    var statusTick by remember { mutableIntStateOf(0) }
    var statusIsError by remember { mutableStateOf(false) }

    val frontPass = passes.lastOrNull()

    fun notify(message: String, isError: Boolean = false) {
        statusText = message
        statusIsError = isError
        statusTick++
    }

    // Tambah pass: loading -> sukses ("Selesai") atau gagal bila simulasi gagal aktif.
    LaunchedEffect(addTick) {
        if (addTick > 0) {
            adding = true
            delay(1300)
            if (failNextAdd) {
                adding = false
                notify("Gagal menambahkan pass — coba lagi (Simulasi)", isError = true)
            } else {
                val template = ADD_POOL[(addTick - 1) % ADD_POOL.size]
                passes.add(template.copy(id = nextId))
                nextId++
                adding = false
                notify("Selesai — " + template.title + " ditambahkan ke Wallet")
            }
        }
    }

    // Banner status otomatis hilang; hentikan aksi berjalan saat layar ditutup.
    LaunchedEffect(statusTick) {
        if (statusText != null) {
            delay(2600)
            statusText = null
        }
    }
    DisposableEffect(Unit) {
        onDispose { adding = false }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Apple Wallet Pass Stack",
                subtitle = "Kartu berkipas & drag through — Simulasi",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LabSectionTitle(
                        title = "Tumpukan Pass",
                        subtitle = "Geser kiri/kanan untuk ganti kartu",
                        modifier = Modifier.weight(1f),
                    )
                    SimulasiPill()
                }

                LabIntroCard(
                    title = "Apple Wallet Pass Stack — Simulasi",
                    body = "Kartu pass lokal buatan demo (tiket, boarding pass, membership, voucher). Tidak terhubung ke Apple Wallet, bank, maskapai, atau layanan pembayaran apa pun.",
                )

                if (passes.isEmpty()) {
                    EmptyWalletCard(onAdd = { addTick++ })
                } else {
                    PassFanStack(
                        passes = passes,
                        expanded = expanded,
                        editMode = editMode,
                        onSetExpanded = { expanded = it },
                        onBringToFront = { id ->
                            val idx = passes.indexOfFirst { it.id == id }
                            if (idx in passes.indices && idx != passes.lastIndex) {
                                val pass = passes.removeAt(idx)
                                passes.add(pass)
                                notify("Selesai — kartu depan: " + pass.title)
                            }
                        },
                        onRotateForward = {
                            if (passes.size > 1) {
                                val pass = passes.removeAt(passes.lastIndex)
                                passes.add(0, pass)
                                notify("Selesai — kartu depan: " + passes.last().title)
                            }
                        },
                        onRotateBackward = {
                            if (passes.size > 1) {
                                val pass = passes.removeAt(0)
                                passes.add(pass)
                                notify("Selesai — kartu depan: " + passes.last().title)
                            }
                        },
                        onMove = { id, delta ->
                            val idx = passes.indexOfFirst { it.id == id }
                            val target = idx + delta
                            if (idx in passes.indices && target in passes.indices) {
                                val pass = passes.removeAt(idx)
                                passes.add(target, pass)
                                notify("Selesai — urutan pass diperbarui")
                            }
                        },
                        onDelete = { id ->
                            val removed = passes.firstOrNull { it.id == id }
                            passes.removeAll { it.id == id }
                            notify("Selesai — " + (removed?.title ?: "pass") + " dihapus")
                        },
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SmallActionButton(
                        label = if (expanded) "Tutup Tumpukan" else "Buka Tumpukan",
                        onClick = {
                            expanded = !expanded
                            notify(if (expanded) "Tumpukan dibuka (drag through)" else "Tumpukan ditutup")
                        },
                        modifier = Modifier.weight(1f),
                    )
                    SmallActionButton(
                        label = if (editMode) "Selesai Ubah" else "Ubah Urutan",
                        onClick = {
                            editMode = !editMode
                            if (editMode) expanded = true
                        },
                        modifier = Modifier.weight(1f),
                        accent = editMode,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SmallActionButton(
                        label = "Kartu Sebelumnya",
                        enabled = passes.size > 1,
                        onClick = {
                            if (passes.size > 1) {
                                val pass = passes.removeAt(0)
                                passes.add(pass)
                                notify("Selesai — kartu depan: " + passes.last().title)
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                    SmallActionButton(
                        label = "Kartu Berikutnya",
                        enabled = passes.size > 1,
                        onClick = {
                            if (passes.size > 1) {
                                val pass = passes.removeAt(passes.lastIndex)
                                passes.add(0, pass)
                                notify("Selesai — kartu depan: " + passes.last().title)
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }

                StatusBanner(message = statusText, isError = statusIsError)

                if (frontPass != null) {
                    PassDetailCard(
                        pass = frontPass,
                        onToggleExpress = { enabled ->
                            val idx = passes.indexOfFirst { it.id == frontPass.id }
                            if (idx in passes.indices) {
                                passes[idx] = passes[idx].copy(express = enabled)
                                notify("Selesai — Express Transit " + (if (enabled) "diaktifkan" else "dinonaktifkan"))
                            }
                        },
                        onToggleExpired = {
                            val idx = passes.indexOfFirst { it.id == frontPass.id }
                            if (idx in passes.indices) {
                                val nowExpired = !passes[idx].expired
                                passes[idx] = passes[idx].copy(
                                    expired = nowExpired,
                                    express = if (nowExpired) false else passes[idx].express,
                                )
                                notify(
                                    "Selesai — pass " +
                                        (if (nowExpired) "ditandai kedaluwarsa" else "diaktifkan kembali"),
                                )
                            }
                        },
                    )
                }

                LabCard {
                    LabSectionTitle(title = "Uji State Gagal", subtitle = "Membatalkan aksi berikutnya secara sengaja")
                    FlatSwitch(
                        label = "Simulasikan gagal saat tambah pass",
                        checked = failNextAdd,
                        enabled = !adding,
                        onCheckedChange = {
                            failNextAdd = it
                            notify(if (it) "Penambahan berikutnya akan gagal (Simulasi)" else "Penambahan kembali normal")
                        },
                    )
                    Text(
                        text = "State demo: normal, loading (Menambahkan…), kosong (hapus semua pass), " +
                            "sukses (banner Selesai), gagal (banner merah), disabled (tombol mati saat loading, " +
                            "toggle Express mati saat kedaluwarsa).",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                }

                LabCard {
                    Text("Catatan Pola:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                    Text(
                        "• Tumpukan berkipas memakai offset + zIndex teranimasi, tanpa shadow/elevation.",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        "• Drag ke bawah membuka tumpukan (drag through), geser horizontal berpindah antar kartu.",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        "• Seluruh pass adalah data mock lokal — tidak ada layanan dompet digital nyata.",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                }
            }

            // Bottom bar "Tambah ke Wallet"
            Surface(
                shape = RoundedCornerShape(0.dp),
                color = palette.surface,
                shadowElevation = 0.dp,
                tonalElevation = 0.dp,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).navigationBarsPadding(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        onClick = { if (!adding) addTick++ },
                        enabled = !adding,
                        shape = RoundedCornerShape(12.dp),
                        color = if (adding) palette.surfaceMuted else palette.primary,
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp,
                        border = BorderStroke(1.dp, if (adding) palette.border else palette.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            if (adding) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    MiniSpinner()
                                    Text(
                                        text = "Menambahkan…",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.textSecondary,
                                    )
                                }
                            } else {
                                Text(
                                    text = "Tambah ke Wallet",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.onPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
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
private fun PassFanStack(
    passes: List<WalletPass>,
    expanded: Boolean,
    editMode: Boolean,
    onSetExpanded: (Boolean) -> Unit,
    onBringToFront: (Int) -> Unit,
    onRotateForward: () -> Unit,
    onRotateBackward: () -> Unit,
    onMove: (Int, Int) -> Unit,
    onDelete: (Int) -> Unit,
) {
    val step = if (expanded) EXPANDED_STEP else COLLAPSED_STEP
    val stackHeight by animateDpAsState(
        targetValue = CARD_HEIGHT + step * (passes.size - 1).coerceAtLeast(0),
        animationSpec = tween(280),
        label = "stackHeight",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(stackHeight)
            .pointerInput(passes.size) {
                var dragAcc = 0f
                detectVerticalDragGestures(
                    onDragStart = { dragAcc = 0f },
                    onVerticalDrag = { _, dragAmount -> dragAcc += dragAmount },
                    onDragEnd = {
                        if (dragAcc > 60f) onSetExpanded(true)
                        if (dragAcc < -60f) onSetExpanded(false)
                    },
                )
            },
    ) {
        passes.forEachIndexed { index, pass ->
            val offsetY by animateDpAsState(
                targetValue = step * index,
                animationSpec = tween(280),
                label = "cardOffset",
            )
            val isFront = index == passes.lastIndex
            Box(modifier = Modifier.zIndex(index.toFloat())) {
                PassCard(
                    pass = pass,
                    expanded = expanded,
                    isFront = isFront,
                    editMode = editMode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = offsetY)
                        .pointerInput(pass.id) {
                            var dragAcc = 0f
                            detectHorizontalDragGestures(
                                onDragStart = { dragAcc = 0f },
                                onHorizontalDrag = { _, dragAmount -> dragAcc += dragAmount },
                                onDragEnd = {
                                    if (isFront && !editMode) {
                                        if (dragAcc < -80f) onRotateForward()
                                        if (dragAcc > 80f) onRotateBackward()
                                    }
                                },
                            )
                        },
                    onTap = { onBringToFront(pass.id) },
                    onMove = onMove,
                    onDelete = onDelete,
                )
            }
        }
    }
}

@Composable
private fun PassCard(
    pass: WalletPass,
    expanded: Boolean,
    isFront: Boolean,
    editMode: Boolean,
    modifier: Modifier = Modifier,
    onTap: () -> Unit,
    onMove: (Int, Int) -> Unit,
    onDelete: (Int) -> Unit,
) {
    val palette = LocalAppPalette.current
    val accent = passAccentColor(palette, pass.kind)

    val faceBrush = remember(pass.kind) {
        Brush.linearGradient(
            colors = listOf(accent.copy(alpha = 0.16f), palette.surface),
        )
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(if (isFront) 1.5.dp else 1.dp, if (isFront) accent else palette.border),
        modifier = modifier
            .height(CARD_HEIGHT)
            .alpha(if (pass.expired) 0.66f else 1f)
            .clickable(enabled = !editMode) { onTap() }
            .semantics {
                role = Role.Button
                contentDescription = "Pass " + pass.title + ", " + pass.kind.label +
                    (if (pass.expired) ", kedaluwarsa" else ", aktif")
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(faceBrush)
                .padding(14.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = 0.15f))
                                .border(1.dp, accent, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            PassGlyph(kind = pass.kind, tint = accent, size = 20.dp)
                        }
                        Text(pass.kind.label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = palette.textSecondary)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (pass.expired) {
                            StatusMiniPill(text = "Kedaluwarsa", tint = palette.danger)
                        }
                        if (pass.express) {
                            StatusMiniPill(text = "Express Transit", tint = palette.info)
                        }
                    }
                }

                Text(
                    text = pass.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = pass.subtitle,
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (expanded || isFront) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(pass.holder, fontSize = 11.sp, color = palette.textMuted)
                        Text(pass.code, fontSize = 11.sp, color = palette.textMuted)
                    }
                }

                if (editMode) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SmallActionButton(label = "Naik", onClick = { onMove(pass.id, 1) }, modifier = Modifier.width(64.dp))
                        SmallActionButton(label = "Turun", onClick = { onMove(pass.id, -1) }, modifier = Modifier.width(64.dp))
                        SmallActionButton(
                            label = "Hapus",
                            onClick = { onDelete(pass.id) },
                            modifier = Modifier.width(72.dp),
                            danger = true,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PassDetailCard(
    pass: WalletPass,
    onToggleExpress: (Boolean) -> Unit,
    onToggleExpired: () -> Unit,
) {
    val palette = LocalAppPalette.current
    LabCard {
        LabSectionTitle(title = "Detail Pass Depan", subtitle = pass.kind.label + " · " + pass.code)
        Text(
            text = pass.title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = if (pass.expired) "Status: Kedaluwarsa" else "Status: Aktif",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (pass.expired) palette.danger else palette.success,
        )

        FlatSwitch(
            label = "Express Transit",
            checked = pass.express,
            enabled = !pass.expired,
            onCheckedChange = onToggleExpress,
        )
        if (pass.expired) {
            Text(
                text = "Express Transit tidak tersedia untuk pass kedaluwarsa (disabled).",
                fontSize = 11.sp,
                color = palette.textMuted,
            )
        }

        SmallActionButton(
            label = if (pass.expired) "Aktifkan Kembali" else "Tandai Kedaluwarsa",
            onClick = onToggleExpired,
            modifier = Modifier.fillMaxWidth(),
            danger = !pass.expired,
        )
    }
}

@Composable
private fun EmptyWalletCard(onAdd: () -> Unit) {
    val palette = LocalAppPalette.current
    LabCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            EmptyWalletGlyph(tint = palette.textMuted)
            Text("Wallet Kosong", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
            Text(
                text = "Semua pass dihapus. Tambahkan pass contoh lewat tombol \"Tambah ke Wallet\" di bawah.",
                fontSize = 12.sp,
                color = palette.textSecondary,
            )
            SmallActionButton(label = "Tambah Pass Contoh", onClick = onAdd, modifier = Modifier.fillMaxWidth())
        }
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
                if (isError) {
                    WarnGlyph(tint = tint)
                } else {
                    CheckGlyph(tint = tint)
                }
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
private fun SmallActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accent: Boolean = false,
    danger: Boolean = false,
) {
    val palette = LocalAppPalette.current
    val tint = when {
        danger -> palette.danger
        accent -> palette.primary
        else -> palette.border
    }
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = when {
            accent -> palette.primary
            danger -> palette.danger.copy(alpha = 0.10f)
            else -> palette.surface
        },
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, tint),
        modifier = modifier.alpha(if (enabled) 1f else 0.5f),
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = when {
                accent -> palette.onPrimary
                danger -> palette.danger
                else -> palette.textPrimary
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
        )
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
private fun CheckGlyph(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.18f, h * 0.52f)
            lineTo(w * 0.42f, h * 0.76f)
            lineTo(w * 0.82f, h * 0.28f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = w * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

@Composable
private fun WarnGlyph(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.14f)
            lineTo(w * 0.9f, h * 0.82f)
            lineTo(w * 0.1f, h * 0.82f)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = w * 0.1f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

@Composable
private fun EmptyWalletGlyph(tint: Color, size: Dp = 44.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.1f, h * 0.3f)
            lineTo(w * 0.9f, h * 0.3f)
            lineTo(w * 0.9f, h * 0.78f)
            lineTo(w * 0.1f, h * 0.78f)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = w * 0.06f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.74f, h * 0.54f))
    }
}

@Composable
private fun PassGlyph(kind: PassKind, tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (kind) {
            PassKind.TIKET -> {
                val path = Path().apply {
                    moveTo(w * 0.08f, h * 0.26f)
                    lineTo(w * 0.92f, h * 0.26f)
                    lineTo(w * 0.92f, h * 0.42f)
                    lineTo(w * 0.82f, h * 0.5f)
                    lineTo(w * 0.92f, h * 0.58f)
                    lineTo(w * 0.92f, h * 0.74f)
                    lineTo(w * 0.08f, h * 0.74f)
                    lineTo(w * 0.08f, h * 0.58f)
                    lineTo(w * 0.18f, h * 0.5f)
                    lineTo(w * 0.08f, h * 0.42f)
                    close()
                }
                drawPath(path = path, color = tint, style = stroke)
            }
            PassKind.BOARDING -> {
                val path = Path().apply {
                    moveTo(w * 0.5f, h * 0.12f)
                    lineTo(w * 0.62f, h * 0.5f)
                    lineTo(w * 0.9f, h * 0.62f)
                    lineTo(w * 0.9f, h * 0.72f)
                    lineTo(w * 0.58f, h * 0.66f)
                    lineTo(w * 0.5f, h * 0.88f)
                    lineTo(w * 0.42f, h * 0.66f)
                    lineTo(w * 0.1f, h * 0.72f)
                    lineTo(w * 0.1f, h * 0.62f)
                    lineTo(w * 0.38f, h * 0.5f)
                    close()
                }
                drawPath(path = path, color = tint, style = stroke)
            }
            PassKind.MEMBER -> {
                drawCircle(
                    color = tint,
                    radius = w * 0.16f,
                    center = Offset(w * 0.5f, h * 0.34f),
                    style = stroke,
                )
                drawArc(
                    color = tint,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.2f, h * 0.5f),
                    size = Size(w * 0.6f, h * 0.52f),
                    style = stroke,
                )
            }
            PassKind.VOUCHER -> {
                drawLine(
                    color = tint,
                    start = Offset(w * 0.24f, h * 0.76f),
                    end = Offset(w * 0.76f, h * 0.24f),
                    strokeWidth = w * 0.1f,
                    cap = StrokeCap.Round,
                )
                drawCircle(
                    color = tint,
                    radius = w * 0.1f,
                    center = Offset(w * 0.3f, h * 0.3f),
                    style = stroke,
                )
                drawCircle(
                    color = tint,
                    radius = w * 0.1f,
                    center = Offset(w * 0.7f, h * 0.7f),
                    style = stroke,
                )
            }
        }
    }
}

private fun passAccentColor(palette: AppPalette, kind: PassKind): Color {
    return when (kind) {
        PassKind.TIKET -> palette.primary
        PassKind.BOARDING -> palette.info
        PassKind.MEMBER -> palette.warning
        PassKind.VOUCHER -> palette.success
    }
}
