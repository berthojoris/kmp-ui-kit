package com.example.uiapp.ui.subscriptionmgmt

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.HapticKind
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatPrimaryButton
import com.example.uiapp.ui.components.FlatSecondaryButton
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private data class Plan(val id: String, val name: String, val price: Int, val perks: String)

private val Plans = listOf(
    Plan("basic", "Basic", 29_000, "1 pengguna · 5 proyek"),
    Plan("pro", "Pro", 79_000, "5 pengguna · proyek tanpa batas"),
    Plan("team", "Team", 199_000, "15 pengguna · admin & audit"),
)

private val PayMethods = listOf("GoPay ••4821", "BCA ••3391", "Kartu Visa ••9010")

private val CancelReasons = listOf("Terlalu mahal", "Sangat jarang dipakai", "Pindah ke aplikasi lain", "Fitur kurang lengkap")

private fun formatRupiah(amount: Int): String = "Rp " + amount.toString().replace(Regex("\\B(?=(\\d{3})+(?!\\d))"), ".")

// Alur retensi: OFFER (tawaran) → SURVEY (alasan) → CONFIRM (konfirmasi) → dibatalkan
private enum class CancelStep { NONE, OFFER, SURVEY, CONFIRM }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SubscriptionMgmtLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current

    var planId by remember { mutableStateOf("pro") }
    var paused by remember { mutableStateOf(false) }
    var payMethod by remember { mutableStateOf(PayMethods[0]) }
    var showPlanSheet by remember { mutableStateOf(false) }
    var showPaySheet by remember { mutableStateOf(false) }
    var changing by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }
    var cancelStep by remember { mutableStateOf(CancelStep.NONE) }
    var discountTaken by remember { mutableStateOf(false) }
    var reason by remember { mutableStateOf<String?>(null) }
    var cancelled by remember { mutableStateOf(false) }

    val plan = remember(planId) { Plans.first { it.id == planId } }

    LaunchedEffect(changing) {
        if (changing) { delay(900); changing = false; toast = "Perubahan paket tersimpan (simulasi)"; PerformHapticFeedback(HapticKind.SUCCESS) }
    }

    // sheet/flow terbuka menutup dirinya dulu sebelum back keluar layar
    BackHandler(enabled = showPlanSheet || showPaySheet || cancelStep != CancelStep.NONE || cancelled) {
        when {
            showPlanSheet -> showPlanSheet = false
            showPaySheet -> showPaySheet = false
            cancelled -> cancelled = false
            else -> cancelStep = CancelStep.NONE
        }
    }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "Subscription", subtitle = "Kelola langganan & retensi — simulasi", onBack = onBack) },
    ) { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "intro") {
                    LabIntroCard(title = "Manajemen Langganan", body = "Ubah paket, jeda penagihan, ganti metode bayar, dan uji alur pembatalan dengan tawaran retensi bertingkat. Tanpa penagihan nyata — semua simulasi.")
                }
                item(key = "toast") {
                    if (toast != null) {
                        Surface(shape = RoundedCornerShape(12.dp), color = palette.success.copy(alpha = 0.12f), border = BorderStroke(1.dp, palette.success), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                            Text(toast!!, fontSize = 12.sp, color = palette.textPrimary, modifier = Modifier.fillMaxWidth().padding(12.dp))
                        }
                    }
                }
                item(key = "status") {
                    Surface(shape = RoundedCornerShape(16.dp), color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(if (cancelled) "Langganan dibatalkan" else plan.name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                                    Text("Perpanjangan berikutnya: 1 Nov 2026", fontSize = 11.sp, color = palette.textMuted)
                                }
                                when {
                                    cancelled -> StatusPill(text = "Berakhir 1 Nov", color = palette.warning)
                                    paused -> StatusPill(text = "Dijeda", color = palette.info)
                                    else -> StatusPill(text = "Aktif", color = palette.success)
                                }
                            }
                            Text(
                                when {
                                    cancelled -> "Akses premium berlanjut sampai akhir periode tagihan."
                                    paused -> "Penagihan dijeda — akses premium tetap aktif hingga 1 Nov 2026."
                                    discountTaken -> "Diskon retensi 30% aktif 3 bulan: ${formatRupiah((plan.price * 7 / 10))}/bulan via $payMethod."
                                    else -> "Penagihan otomatis ${formatRupiah(plan.price)}/bulan via $payMethod."
                                },
                                fontSize = 12.sp, color = palette.textSecondary,
                            )
                        }
                    }
                }
                item(key = "actions") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FlatPrimaryButton(
                            text = if (changing) "Menyimpan…" else "Ubah Paket",
                            onClick = { showPlanSheet = true },
                            enabled = !cancelled && !changing,
                        )
                        FlatSecondaryButton(
                            text = if (paused) "Lanjutkan Penagihan" else "Jeda Penagihan 1 Bulan",
                            onClick = {
                                paused = !paused
                                toast = if (paused) "Penagihan dijeda (simulasi)" else "Penagihan dilanjutkan"
                                PerformHapticFeedback(HapticKind.MEDIUM)
                            },
                            enabled = !cancelled,
                        )
                        FlatSecondaryButton(text = "Ganti Metode Bayar", onClick = { showPaySheet = true }, enabled = !cancelled)
                        FlatSecondaryButton(
                            text = if (cancelled) "Aktifkan Ulang Langganan" else "Batalkan Langganan",
                            onClick = {
                                if (cancelled) { cancelled = false; reason = null; toast = "Langganan diaktifkan kembali" }
                                else { cancelStep = CancelStep.OFFER; PerformHapticFeedback(HapticKind.MEDIUM) }
                            },
                        )
                    }
                }
                item(key = "hint") {
                    if (!cancelled) Text("Alur pembatalan: tawaran retensi → survei alasan → konfirmasi.", fontSize = 11.sp, color = palette.textMuted)
                }
                item(key = "spacer") { Spacer(Modifier.height(4.dp)) }
            }

            when {
                showPlanSheet -> OptionSheet(
                    title = "Pilih Paket",
                    options = Plans.map { "${it.name} — ${formatRupiah(it.price)}/bln · ${it.perks}" },
                    selectedIdx = Plans.indexOfFirst { it.id == planId },
                    onSelect = { i -> planId = Plans[i].id; showPlanSheet = false; changing = true },
                    onDismiss = { showPlanSheet = false },
                )
                showPaySheet -> OptionSheet(
                    title = "Metode Pembayaran",
                    options = PayMethods,
                    selectedIdx = PayMethods.indexOf(payMethod),
                    onSelect = { i -> payMethod = PayMethods[i]; showPaySheet = false; toast = "Metode bayar diganti ke ${PayMethods[i]}" },
                    onDismiss = { showPaySheet = false },
                )
                cancelStep == CancelStep.OFFER -> RetentionOffer(
                    onKeep = {
                        discountTaken = true; cancelStep = CancelStep.NONE
                        toast = "Diskon retensi 30% diterapkan 3 bulan (simulasi)"
                        PerformHapticFeedback(HapticKind.SUCCESS)
                    },
                    onContinue = { cancelStep = CancelStep.SURVEY },
                    onDismiss = { cancelStep = CancelStep.NONE },
                )
                cancelStep == CancelStep.SURVEY -> CancelSurvey(
                    selected = reason,
                    onPick = { reason = it },
                    onContinue = {
                        if (reason == null) toast = "Pilih satu alasan untuk lanjut"
                        else { cancelStep = CancelStep.CONFIRM; PerformHapticFeedback(HapticKind.MEDIUM) }
                    },
                    onDismiss = { cancelStep = CancelStep.NONE },
                )
                cancelStep == CancelStep.CONFIRM -> CancelConfirm(
                    reason = reason,
                    onConfirm = { cancelled = true; cancelStep = CancelStep.NONE; PerformHapticFeedback(HapticKind.WARNING) },
                    onDismiss = { cancelStep = CancelStep.NONE },
                )
            }
        }
    }
}

@Composable
private fun OptionSheet(title: String, options: List<String>, selectedIdx: Int, onSelect: (Int) -> Unit, onDismiss: () -> Unit) {
    val palette = LocalAppPalette.current
    BottomSheet(title = title, onDismiss = onDismiss) {
        options.forEachIndexed { i, opt ->
            val selected = i == selectedIdx
            Surface(
                onClick = { onSelect(i) },
                shape = RoundedCornerShape(12.dp), color = palette.surface,
                border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
                shadowElevation = 0.dp, tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth().semantics { contentDescription = "$opt ${if (selected) "dipilih" else "tidak dipilih"}" },
            ) {
                Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(opt, fontSize = 13.sp, color = palette.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    if (selected) StatusPill(text = "Dipilih", color = palette.primary)
                }
            }
        }
    }
}

@Composable
private fun RetentionOffer(onKeep: () -> Unit, onContinue: () -> Unit, onDismiss: () -> Unit) {
    val palette = LocalAppPalette.current
    BottomSheet(title = "Sebelum pergi… (1/3)", subtitle = "Tawaran khusus untuk pelanggan setia — berlaku hari ini.", onDismiss = onDismiss) {
        Surface(shape = RoundedCornerShape(12.dp), color = palette.primaryContainer, border = BorderStroke(1.dp, palette.primary), shadowElevation = 0.dp, tonalElevation = 0.dp) {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Diskon 30% selama 3 bulan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = palette.onPrimaryContainer)
                Text("Hemat total Rp 70.800 dibanding harga normal.", fontSize = 12.sp, color = palette.onPrimaryContainer)
            }
        }
        FlatPrimaryButton(text = "Ambil Diskon, Tetap Langganan", onClick = onKeep)
        FlatSecondaryButton(text = "Tetap Ingin Membatalkan", onClick = onContinue)
    }
}

@Composable
private fun CancelSurvey(selected: String?, onPick: (String) -> Unit, onContinue: () -> Unit, onDismiss: () -> Unit) {
    val palette = LocalAppPalette.current
    BottomSheet(title = "Alasan pembatalan (2/3)", subtitle = "Bantu kami memperbaiki layanan.", onDismiss = onDismiss) {
        CancelReasons.forEach { r ->
            val on = r == selected
            Surface(
                onClick = { onPick(r); PerformHapticFeedback(HapticKind.SELECTION) },
                shape = RoundedCornerShape(12.dp), color = palette.surface,
                border = BorderStroke(1.dp, if (on) palette.primary else palette.border),
                shadowElevation = 0.dp, tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Alasan $r ${if (on) "dipilih" else "tidak dipilih"}" },
            ) {
                Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(r, fontSize = 13.sp, color = palette.textPrimary)
                    if (on) StatusPill(text = "Dipilih", color = palette.primary) else Text("", fontSize = 13.sp)
                }
            }
        }
        FlatPrimaryButton(text = if (selected == null) "Pilih alasan dulu" else "Lanjutkan", onClick = onContinue, enabled = true)
    }
}

@Composable
private fun CancelConfirm(reason: String?, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    BottomSheet(title = "Konfirmasi (3/3)", subtitle = "Langganan berakhir 1 Nov 2026, tidak diperpanjang otomatis.", onDismiss = onDismiss) {
        Text("Alasan Anda: ${reason ?: "-"}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text("Data proyek tetap tersimpan 90 hari setelah langganan berakhir.", fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f)) { FlatSecondaryButton(text = "Kembali", onClick = onDismiss) }
            Box(Modifier.weight(1f)) { FlatPrimaryButton(text = "Ya, Batalkan", onClick = onConfirm) }
        }
    }
}

@Composable
private fun BottomSheet(title: String, onDismiss: () -> Unit, subtitle: String? = null, content: @Composable ColumnScope.() -> Unit) {
    val palette = LocalAppPalette.current
    Box(
        Modifier
            .fillMaxSize()
            .background(palette.background.copy(alpha = 0.55f))
            .clickable { onDismiss() }
            .semantics { contentDescription = "Latar sheet, ketuk untuk menutup" },
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = palette.surface, border = BorderStroke(1.dp, palette.border),
            shadowElevation = 0.dp, tonalElevation = 0.dp,
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                if (subtitle != null) Text(subtitle, fontSize = 12.sp, color = palette.textSecondary)
                Column(
                    Modifier.fillMaxWidth().heightIn(max = 300.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) { content() }
            }
        }
    }
}
