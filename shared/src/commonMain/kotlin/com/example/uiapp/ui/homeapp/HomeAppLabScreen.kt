package com.example.uiapp.ui.homeapp

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.FlatToggle
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

// ---------------------------------------------------------------------------
// Data mock lokal (tanpa HomeKit / Matter / jaringan)
// ---------------------------------------------------------------------------

private enum class AccessoryKind(val label: String) {
    LAMPU("Lampu"),
    TERMOSTAT("Termostat"),
    MIKROFON("Mikrofon"),
    SUHU("Sensor Suhu"),
    KELEMBAPAN("Sensor Kelembapan"),
    KONTAK("Kontak Jendela"),
}

private enum class AccessoryConn(val label: String) {
    OK("Terhubung"),
    CONNECTING("Menghubungkan…"),
    ERROR("Tidak merespons"),
}

private data class Accessory(
    val id: String,
    val name: String,
    val room: String,
    val kind: AccessoryKind,
    val controllable: Boolean,
    val isOn: Boolean,
    val brightness: Int,
    val temperature: Int,
    val readValue: String,
    val conn: AccessoryConn,
)

private val roomNames = listOf("Semua", "Ruang Tamu", "Kamar Tidur", "Dapur", "Garasi")

private val accessorySeed = listOf(
    Accessory(
        id = "acc_lampu_tamu",
        name = "Lampu Ruang Tamu",
        room = "Ruang Tamu",
        kind = AccessoryKind.LAMPU,
        controllable = true,
        isOn = true,
        brightness = 70,
        temperature = 24,
        readValue = "",
        conn = AccessoryConn.OK,
    ),
    Accessory(
        id = "acc_lampu_tidur",
        name = "Lampu Kamar Tidur",
        room = "Kamar Tidur",
        kind = AccessoryKind.LAMPU,
        controllable = true,
        isOn = false,
        brightness = 35,
        temperature = 24,
        readValue = "",
        conn = AccessoryConn.OK,
    ),
    Accessory(
        id = "acc_thermo",
        name = "Termostat Utama",
        room = "Ruang Tamu",
        kind = AccessoryKind.TERMOSTAT,
        controllable = true,
        isOn = true,
        brightness = 0,
        temperature = 24,
        readValue = "",
        conn = AccessoryConn.OK,
    ),
    Accessory(
        id = "acc_mic_dapur",
        name = "Mikrofon Dapur",
        room = "Dapur",
        kind = AccessoryKind.MIKROFON,
        controllable = true,
        isOn = false,
        brightness = 0,
        temperature = 24,
        readValue = "",
        conn = AccessoryConn.OK,
    ),
    Accessory(
        id = "acc_suhu_tidur",
        name = "Sensor Suhu Kamar",
        room = "Kamar Tidur",
        kind = AccessoryKind.SUHU,
        controllable = false,
        isOn = true,
        brightness = 0,
        temperature = 24,
        readValue = "23,5°",
        conn = AccessoryConn.OK,
    ),
    Accessory(
        id = "acc_kelembapan",
        name = "Sensor Kelembapan",
        room = "Dapur",
        kind = AccessoryKind.KELEMBAPAN,
        controllable = false,
        isOn = true,
        brightness = 0,
        temperature = 24,
        readValue = "58%",
        conn = AccessoryConn.OK,
    ),
    Accessory(
        id = "acc_kontak",
        name = "Kontak Jendela",
        room = "Ruang Tamu",
        kind = AccessoryKind.KONTAK,
        controllable = false,
        isOn = true,
        brightness = 0,
        temperature = 24,
        readValue = "Tertutup",
        conn = AccessoryConn.OK,
    ),
)

private val garageAccessory = Accessory(
    id = "acc_lampu_garasi",
    name = "Lampu Garasi",
    room = "Garasi",
    kind = AccessoryKind.LAMPU,
    controllable = true,
    isOn = false,
    brightness = 50,
    temperature = 24,
    readValue = "",
    conn = AccessoryConn.ERROR,
)

private enum class SceneStyle { PAGI, TIDUR, KERJA, NONTON }

private data class Scene(
    val id: String,
    val name: String,
    val detail: String,
    val style: SceneStyle,
)

private val sceneSeed = listOf(
    Scene("sc_pagi", "Selamat Pagi", "Lampu 70% · Termostat 24°", SceneStyle.PAGI),
    Scene("sc_tidur", "Waktunya Tidur", "Lampu mati · Termostat 22°", SceneStyle.TIDUR),
    Scene("sc_kerja", "Pulang Kerja", "Lampu tamu 50%", SceneStyle.KERJA),
    Scene("sc_film", "Nonton Film", "Lampu redup 20%", SceneStyle.NONTON),
)

private val temperatureChoices = listOf(16, 18, 20, 22, 24, 26, 28)

private enum class HomeLoadState { IDLE, LOADING, SUCCESS, ERROR }

// ---------------------------------------------------------------------------
// Layar utama
// ---------------------------------------------------------------------------

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeAppLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var roomFilter by remember { mutableStateOf("Semua") }
    val accessories = remember { mutableStateListOf<Accessory>().apply { addAll(accessorySeed) } }
    val garageAvailable = remember { mutableStateOf(true) }
    var selectedAccessoryId by remember { mutableStateOf<String?>(null) }
    var homeStatus by remember { mutableStateOf("Ketuk tile untuk toggle cepat, atau buka detail aksesori.") }

    var loadState by remember { mutableStateOf(HomeLoadState.SUCCESS) }
    var loadTick by remember { mutableIntStateOf(0) }
    var simulateLoadFail by remember { mutableStateOf(false) }

    var connectingId by remember { mutableStateOf<String?>(null) }
    var connectTick by remember { mutableIntStateOf(0) }

    // Grid 2 kolom per ruangan — derivasi snapshot, bukan alokasi tiap recomposition.
    val visibleRows by remember {
        derivedStateOf {
            accessories
                .filter { roomFilter == "Semua" || it.room == roomFilter }
                .chunked(2)
        }
    }
    val visibleCount by remember {
        derivedStateOf { accessories.count { roomFilter == "Semua" || it.room == roomFilter } }
    }
    val roomStatus by remember {
        derivedStateOf {
            val suhu = accessories.firstOrNull { it.kind == AccessoryKind.SUHU }?.readValue ?: "—"
            val kelembapan = accessories.firstOrNull { it.kind == AccessoryKind.KELEMBAPAN }?.readValue ?: "—"
            val kontak = accessories.firstOrNull { it.kind == AccessoryKind.KONTAK }?.readValue ?: "—"
            val nyala = accessories.count { it.controllable && it.isOn }
            val total = accessories.count { it.controllable }
            listOf(
                Triple("Suhu ruangan", suhu, "Baca-saja"),
                Triple("Kelembapan", kelembapan, "Baca-saja"),
                Triple("Kontak jendela", kontak, "Baca-saja"),
                Triple("Aksesori nyala", "$nyala dari $total", if (nyala > 0) "Status: sebagian nyala" else "Status: semua mati"),
            )
        }
    }

    // Muat ulang data rumah (simulasi).
    LaunchedEffect(loadTick) {
        if (loadTick > 0) {
            loadState = HomeLoadState.LOADING
            delay(1200)
            loadState = if (simulateLoadFail) HomeLoadState.ERROR else HomeLoadState.SUCCESS
        }
    }

    // Sambungkan ulang aksesori yang tidak merespons (simulasi).
    LaunchedEffect(connectTick) {
        if (connectTick > 0) {
            val id = connectingId
            if (id != null) {
                updateAccessory(accessories, id) { it.copy(conn = AccessoryConn.CONNECTING) }
                delay(1300)
                updateAccessory(accessories, id) { it.copy(conn = AccessoryConn.OK) }
                val name = accessories.firstOrNull { it.id == id }?.name ?: "Aksesori"
                homeStatus = "“$name” terhubung kembali (Simulasi)."
            }
        }
    }

    fun toggleAccessory(id: String) {
        val current = accessories.firstOrNull { it.id == id } ?: return
        if (!current.controllable || current.conn != AccessoryConn.OK) {
            homeStatus = "“${current.name}” tidak dapat diubah — ${current.conn.label.lowercase()}."
            return
        }
        updateAccessory(accessories, id) { it.copy(isOn = !it.isOn) }
        homeStatus = "“${current.name}” kini ${if (current.isOn) "mati" else "nyala"} (Simulasi)."
    }

    fun runScene(scene: Scene) {
        when (scene.style) {
            SceneStyle.PAGI -> {
                updateAccessory(accessories, "acc_lampu_tamu") { it.copy(isOn = true, brightness = 70) }
                updateAccessory(accessories, "acc_thermo") { it.copy(isOn = true, temperature = 24) }
            }
            SceneStyle.TIDUR -> {
                updateAccessory(accessories, "acc_lampu_tamu") { it.copy(isOn = false) }
                updateAccessory(accessories, "acc_lampu_tidur") { it.copy(isOn = false) }
                updateAccessory(accessories, "acc_thermo") { it.copy(temperature = 22) }
                updateAccessory(accessories, "acc_mic_dapur") { it.copy(isOn = false) }
            }
            SceneStyle.KERJA -> {
                updateAccessory(accessories, "acc_lampu_tamu") { it.copy(isOn = true, brightness = 50) }
            }
            SceneStyle.NONTON -> {
                updateAccessory(accessories, "acc_lampu_tamu") { it.copy(isOn = true, brightness = 20) }
            }
        }
        homeStatus = "Scene “${scene.name}” dijalankan (Simulasi) — ${scene.detail}."
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Home & Scenes",
                subtitle = "Tile aksesori, ruangan, scene — Simulasi",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    LabIntroCard(
                        title = "Rumah & Scene (Simulasi)",
                        body = "Tanpa HomeKit/Matter: seluruh aksesori, sensor, dan scene adalah mock lokal. Tidak ada perangkat rumah nyata yang dikendalikan.",
                        modifier = Modifier.weight(1f),
                    )
                    SimulasiPill()
                }

                // Kontrol muat ulang
                LabCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Simulasikan gagal muat",
                            fontSize = 12.sp,
                            color = palette.textSecondary,
                            modifier = Modifier.weight(1f),
                        )
                        FlatToggle(
                            checked = simulateLoadFail,
                            onCheckedChange = { simulateLoadFail = it },
                            modifier = Modifier.semantics {
                                contentDescription = "Simulasikan gagal memuat aksesori"
                            },
                        )
                    }
                    SmallActionButton(
                        text = if (loadState == HomeLoadState.LOADING) "Memuat Rumah…" else "Segarkan Rumah (Simulasi)",
                        onClick = { loadTick++ },
                        enabled = loadState != HomeLoadState.LOADING,
                    )
                }

                // Tab ruangan horizontal
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    roomNames.forEach { room ->
                        val selected = room == roomFilter
                        Surface(
                            onClick = { roomFilter = room },
                            shape = RoundedCornerShape(999.dp),
                            color = if (selected) palette.primary else palette.surface,
                            border = BorderStroke(1.dp, if (selected) palette.primary else palette.border),
                            shadowElevation = 0.dp,
                            tonalElevation = 0.dp,
                            modifier = Modifier.semantics { contentDescription = "Ruangan $room" },
                        ) {
                            Text(
                                text = room,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) palette.onPrimary else palette.textSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            )
                        }
                    }
                }

                // Carousel scene
                LabCard {
                    LabSectionTitle(
                        title = "Scene",
                        subtitle = "Ketuk untuk menjalankan scene (simulasi)",
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        sceneSeed.forEach { scene ->
                            SceneCard(scene = scene, onRun = { runScene(scene) })
                        }
                    }
                }

                // Grid aksesori
                LabCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LabSectionTitle(
                            title = "Aksesori — $roomFilter",
                            subtitle = "Tombol lingkaran toggle & sensor baca-saja",
                            modifier = Modifier.weight(1f),
                        )
                        StatusPill(text = "$visibleCount item", color = palette.info)
                    }

                    when (loadState) {
                        HomeLoadState.LOADING -> {
                            Text(
                                "Memuat tile aksesori… (loading)",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                            )
                            repeat(2) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(96.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(palette.surfaceMuted)
                                        .border(1.dp, palette.border, RoundedCornerShape(14.dp)),
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        HomeLoadState.ERROR -> {
                            StatusPill(text = "Gagal", color = palette.danger)
                            Text(
                                "Gagal memuat aksesori rumah (simulasi gagal). Matikan opsi kegagalan lalu coba lagi.",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = palette.textSecondary,
                            )
                            SmallActionButton(text = "Coba Lagi", onClick = { loadTick++ })
                        }
                        else -> {
                            if (roomFilter == "Garasi" && visibleCount == 0) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        "Belum ada aksesori di Garasi (state kosong).",
                                        fontSize = 12.sp,
                                        color = palette.textSecondary,
                                    )
                                    SmallActionButton(
                                        text = "Tambah Aksesori Contoh",
                                        onClick = {
                                            if (garageAvailable.value) {
                                                accessories.add(garageAccessory)
                                                garageAvailable.value = false
                                                homeStatus = "“${garageAccessory.name}” ditambahkan — sedang mencoba menyambung."
                                                connectingId = garageAccessory.id
                                                connectTick++
                                            }
                                        },
                                        enabled = garageAvailable.value,
                                    )
                                    if (!garageAvailable.value) {
                                        Text(
                                            "Aksesori contoh sudah ditambahkan — tombol dinonaktifkan (disabled).",
                                            fontSize = 11.sp,
                                            color = palette.textMuted,
                                        )
                                    }
                                }
                            } else if (visibleCount == 0) {
                                Text(
                                    "Tidak ada aksesori pada ruangan ini (state kosong).",
                                    fontSize = 12.sp,
                                    color = palette.textSecondary,
                                )
                            } else {
                                visibleRows.forEach { rowItems ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        rowItems.forEach { accessory ->
                                            AccessoryTile(
                                                accessory = accessory,
                                                onToggle = { toggleAccessory(accessory.id) },
                                                onOpenDetail = { selectedAccessoryId = accessory.id },
                                                onRetry = {
                                                    connectingId = accessory.id
                                                    connectTick++
                                                },
                                                modifier = Modifier.weight(1f),
                                            )
                                        }
                                        if (rowItems.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = homeStatus,
                        fontSize = 11.sp,
                        color = palette.textSecondary,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Status kamar
                LabCard {
                    LabSectionTitle(
                        title = "Status Kamar",
                        subtitle = "Ringkasan sensor & aksesori $roomFilter",
                    )
                    roomStatus.forEach { (label, value, note) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                label,
                                fontSize = 12.sp,
                                color = palette.textSecondary,
                                modifier = Modifier.weight(1f),
                            )
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    value,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary,
                                )
                                Text(note, fontSize = 10.sp, color = palette.textMuted)
                            }
                        }
                    }
                }
            }

            // Detail aksesori (modal bawah)
            val selected = accessories.firstOrNull { it.id == selectedAccessoryId }
            if (selected != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(palette.textPrimary.copy(alpha = 0.45f))
                        .clickable { selectedAccessoryId = null }
                        .semantics { contentDescription = "Tutup detail aksesori" },
                )
                AccessoryDetailSheet(
                    accessory = selected,
                    onClose = { selectedAccessoryId = null },
                    onToggle = { toggleAccessory(selected.id) },
                    onBrightnessChange = { value ->
                        updateAccessory(accessories, selected.id) { it.copy(brightness = value) }
                    },
                    onTemperatureChange = { value ->
                        updateAccessory(accessories, selected.id) { it.copy(temperature = value) }
                    },
                    onRetry = {
                        connectingId = selected.id
                        connectTick++
                    },
                    roomStatus = roomStatus,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

private fun updateAccessory(
    accessories: MutableList<Accessory>,
    id: String,
    transform: (Accessory) -> Accessory,
) {
    val index = accessories.indexOfFirst { it.id == id }
    if (index >= 0) accessories[index] = transform(accessories[index])
}

// ---------------------------------------------------------------------------
// Tile aksesori
// ---------------------------------------------------------------------------

@Composable
private fun AccessoryTile(
    accessory: Accessory,
    onToggle: () -> Unit,
    onOpenDetail: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    val lit = accessory.controllable && accessory.isOn && accessory.conn == AccessoryConn.OK

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(palette.surfaceMuted)
            .border(1.dp, palette.border, RoundedCornerShape(14.dp))
            .clickable { onOpenDetail() }
            .padding(12.dp)
            .semantics {
                contentDescription = "Detail aksesori ${accessory.name}, ${accessory.kind.label}"
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (lit) palette.primaryContainer else palette.surface)
                .border(
                    BorderStroke(if (lit) 2.dp else 1.dp, if (lit) palette.primary else palette.border),
                    CircleShape,
                )
                .clickable(enabled = accessory.controllable && accessory.conn == AccessoryConn.OK) { onToggle() }
                .semantics {
                    role = Role.Button
                    contentDescription = if (accessory.controllable) {
                        "Toggle ${accessory.name}, saat ini ${if (accessory.isOn) "nyala" else "mati"}"
                    } else {
                        "${accessory.name} baca-saja, nilai ${accessory.readValue}"
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            AccessoryKindIcon(
                kind = accessory.kind,
                tint = if (lit) palette.primary else palette.textMuted,
            )
        }

        Text(
            text = accessory.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = palette.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        val statusText = when {
            accessory.conn == AccessoryConn.ERROR -> "Tidak merespons"
            accessory.conn == AccessoryConn.CONNECTING -> "Menghubungkan…"
            !accessory.controllable -> "${accessory.readValue} · Baca-saja"
            accessory.kind == AccessoryKind.LAMPU && accessory.isOn -> "Nyala · ${accessory.brightness}%"
            accessory.kind == AccessoryKind.TERMOSTAT && accessory.isOn -> "Nyala · ${accessory.temperature}°"
            accessory.isOn -> "Nyala"
            else -> "Mati"
        }
        val statusColor = when {
            accessory.conn == AccessoryConn.ERROR -> palette.danger
            accessory.conn == AccessoryConn.CONNECTING -> palette.info
            !accessory.controllable -> palette.textMuted
            accessory.isOn -> palette.success
            else -> palette.textMuted
        }
        Text(
            text = statusText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = statusColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (accessory.conn == AccessoryConn.ERROR) {
            Text(
                text = "Coba Lagi",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = palette.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onRetry() }
                    .padding(4.dp)
                    .semantics { contentDescription = "Coba sambungkan ${accessory.name}" },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Detail aksesori (sheet modal)
// ---------------------------------------------------------------------------

@Composable
private fun AccessoryDetailSheet(
    accessory: Accessory,
    onClose: () -> Unit,
    onToggle: () -> Unit,
    onBrightnessChange: (Int) -> Unit,
    onTemperatureChange: (Int) -> Unit,
    onRetry: () -> Unit,
    roomStatus: List<Triple<String, String, String>>,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current

    Surface(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    AccessoryKindIcon(kind = accessory.kind, tint = palette.primary)
                    Column {
                        Text(
                            accessory.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            "${accessory.room} · ${accessory.kind.label}",
                            fontSize = 11.sp,
                            color = palette.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Text(
                    text = "Tutup",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onClose() }
                        .padding(8.dp)
                        .semantics { contentDescription = "Tutup detail aksesori" },
                )
            }

            val connColor = when (accessory.conn) {
                AccessoryConn.OK -> palette.success
                AccessoryConn.CONNECTING -> palette.info
                AccessoryConn.ERROR -> palette.danger
            }
            StatusPill(text = "Koneksi: ${accessory.conn.label}", color = connColor)

            if (accessory.conn == AccessoryConn.ERROR) {
                Text(
                    "Aksesori tidak merespons (simulasi). Tidak ada perangkat nyata — coba sambungkan ulang.",
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = palette.textSecondary,
                )
                SmallActionButton(text = "Coba Sambungkan Ulang", onClick = onRetry)
            } else if (!accessory.controllable) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Sensor baca-saja",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary,
                        )
                        Text(
                            "Nilai saat ini: ${accessory.readValue} (tidak dapat diubah)",
                            fontSize = 11.sp,
                            color = palette.textSecondary,
                        )
                    }
                    StatusPill(text = "Baca-saja", color = palette.textMuted)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (accessory.isOn) "Status: nyala" else "Status: mati",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary,
                        )
                        Text(
                            "Toggle daya aksesori (Simulasi)",
                            fontSize = 11.sp,
                            color = palette.textMuted,
                        )
                    }
                    FlatToggle(
                        checked = accessory.isOn,
                        onCheckedChange = { onToggle() },
                        modifier = Modifier.semantics {
                            contentDescription = "Daya ${accessory.name}"
                        },
                    )
                }

                if (accessory.kind == AccessoryKind.LAMPU) {
                    Text(
                        "Kecerahan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                    )
                    BrightnessSlider(
                        value = accessory.brightness,
                        enabled = accessory.isOn,
                        onChange = onBrightnessChange,
                    )
                    Text(
                        text = if (accessory.isOn) {
                            "Kecerahan ${accessory.brightness}% — geser slider untuk mengubah."
                        } else {
                            "Slider dinonaktifkan selama lampu mati (disabled)."
                        },
                        fontSize = 11.sp,
                        color = palette.textMuted,
                    )
                }

                if (accessory.kind == AccessoryKind.TERMOSTAT) {
                    Text(
                        "Suhu yang diminta",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        temperatureChoices.forEach { value ->
                            val selected = value == accessory.temperature
                            Surface(
                                onClick = { onTemperatureChange(value) },
                                enabled = accessory.isOn,
                                shape = RoundedCornerShape(999.dp),
                                color = when {
                                    !accessory.isOn -> palette.surfaceMuted
                                    selected -> palette.primary
                                    else -> palette.surface
                                },
                                border = BorderStroke(
                                    1.dp,
                                    if (selected && accessory.isOn) palette.primary else palette.border,
                                ),
                                shadowElevation = 0.dp,
                                tonalElevation = 0.dp,
                                modifier = Modifier.semantics {
                                    contentDescription = "Suhu $value derajat"
                                },
                            ) {
                                Text(
                                    text = "${value}°",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected && accessory.isOn) palette.onPrimary else palette.textSecondary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceMuted)
                    .border(1.dp, palette.border, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    "Status ruangan — ${accessory.room}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                )
                roomStatus.forEach { (label, value, note) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            "$label ($note)",
                            fontSize = 11.sp,
                            color = palette.textSecondary,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            value,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary,
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Slider kecerahan kustom (tanpa elevasi)
// ---------------------------------------------------------------------------

@Composable
private fun BrightnessSlider(value: Int, enabled: Boolean, onChange: (Int) -> Unit) {
    val palette = LocalAppPalette.current
    val fraction = (value / 100f).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(150),
        label = "brightnessFraction",
    )
    var trackWidthPx by remember { mutableIntStateOf(1) }

    fun setFromX(x: Float) {
        val newValue = ((x / trackWidthPx) * 100).roundToInt().coerceIn(0, 100)
        onChange(newValue)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (enabled) palette.surfaceMuted else palette.background)
                .border(1.dp, palette.border, RoundedCornerShape(999.dp))
                .onGloballyPositioned { trackWidthPx = it.size.width.coerceAtLeast(1) }
                .pointerInput(enabled) {
                    if (enabled) {
                        detectTapGestures { offset -> setFromX(offset.x) }
                    }
                }
                .pointerInput(enabled) {
                    if (enabled) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            setFromX(change.position.x)
                        }
                    }
                },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (enabled) palette.primary else palette.border),
            )
        }
        Box(
            modifier = Modifier
                .offset { IntOffset((trackWidthPx * animatedFraction - 12.dp.toPx()).toInt(), 0) }
                .size(24.dp)
                .clip(CircleShape)
                .background(if (enabled) palette.primary else palette.textMuted)
                .border(1.dp, palette.border, CircleShape)
                .semantics { contentDescription = "Kecerahan $value persen" },
        )
    }
}

// ---------------------------------------------------------------------------
// Scene card dengan gradien + ikon Canvas
// ---------------------------------------------------------------------------

@Composable
private fun SceneCard(scene: Scene, onRun: () -> Unit) {
    val palette = LocalAppPalette.current
    // Gradien ilustrasi scene diturunkan dari warna palet aktif (tema terang/gelap).
    val brush = remember(scene.id, palette) {
        Brush.linearGradient(
            colors = when (scene.style) {
                SceneStyle.PAGI -> listOf(
                    palette.warning.copy(alpha = 0.35f),
                    palette.info.copy(alpha = 0.25f),
                )
                SceneStyle.TIDUR -> listOf(
                    palette.info.copy(alpha = 0.40f),
                    palette.primary.copy(alpha = 0.30f),
                )
                SceneStyle.KERJA -> listOf(
                    palette.success.copy(alpha = 0.30f),
                    palette.info.copy(alpha = 0.20f),
                )
                SceneStyle.NONTON -> listOf(
                    palette.primary.copy(alpha = 0.35f),
                    palette.warning.copy(alpha = 0.20f),
                )
            },
        )
    }

    Column(
        modifier = Modifier
            .width(170.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(brush)
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .clickable { onRun() }
            .padding(14.dp)
            .semantics { contentDescription = "Jalankan scene ${scene.name}" },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SceneIcon(style = scene.style, tint = palette.primary)
        Text(
            scene.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = palette.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            scene.detail,
            fontSize = 10.sp,
            color = palette.textSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            "Ketuk untuk menjalankan",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = palette.primary,
        )
    }
}

// ---------------------------------------------------------------------------
// Ikon Canvas
// ---------------------------------------------------------------------------

@Composable
private fun SceneIcon(style: SceneStyle, tint: Color) {
    Canvas(modifier = Modifier.size(34.dp)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
        when (style) {
            SceneStyle.PAGI -> {
                // Matahari terbit: setengah lingkaran + garis ufuk + sinar
                drawArc(
                    color = tint,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.22f, h * 0.28f),
                    size = Size(w * 0.56f, h * 0.44f),
                    style = stroke,
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.1f, h * 0.74f),
                    end = Offset(w * 0.9f, h * 0.74f),
                    strokeWidth = w * 0.08f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.5f, h * 0.08f),
                    end = Offset(w * 0.5f, h * 0.2f),
                    strokeWidth = w * 0.08f,
                    cap = StrokeCap.Round,
                )
            }
            SceneStyle.TIDUR -> {
                // Bulan sabit
                drawArc(
                    color = tint,
                    startAngle = 30f,
                    sweepAngle = 300f,
                    useCenter = false,
                    topLeft = Offset(w * 0.18f, h * 0.14f),
                    size = Size(w * 0.64f, h * 0.64f),
                    style = stroke,
                )
                drawCircle(color = tint, radius = w * 0.05f, center = Offset(w * 0.8f, h * 0.2f))
            }
            SceneStyle.KERJA -> {
                // Map/jalan pulang
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.16f, h * 0.22f),
                    size = Size(w * 0.68f, h * 0.5f),
                    cornerRadius = CornerRadius(w * 0.1f, w * 0.1f),
                    style = stroke,
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.3f, h * 0.82f),
                    end = Offset(w * 0.7f, h * 0.82f),
                    strokeWidth = w * 0.08f,
                    cap = StrokeCap.Round,
                )
            }
            SceneStyle.NONTON -> {
                // Layar + play
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.12f, h * 0.2f),
                    size = Size(w * 0.76f, h * 0.52f),
                    cornerRadius = CornerRadius(w * 0.1f, w * 0.1f),
                    style = stroke,
                )
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.44f, h * 0.34f)
                    lineTo(w * 0.62f, h * 0.46f)
                    lineTo(w * 0.44f, h * 0.58f)
                    close()
                }
                drawPath(path = path, color = tint, style = stroke)
            }
        }
    }
}

@Composable
private fun AccessoryKindIcon(kind: AccessoryKind, tint: Color) {
    Canvas(modifier = Modifier.size(26.dp)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round)
        when (kind) {
            AccessoryKind.LAMPU -> {
                // Bohlam: lingkaran + dasar
                drawCircle(color = tint, radius = w * 0.26f, center = Offset(w * 0.5f, h * 0.42f), style = stroke)
                drawLine(
                    color = tint,
                    start = Offset(w * 0.36f, h * 0.74f),
                    end = Offset(w * 0.64f, h * 0.74f),
                    strokeWidth = w * 0.09f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.42f, h * 0.86f),
                    end = Offset(w * 0.58f, h * 0.86f),
                    strokeWidth = w * 0.09f,
                    cap = StrokeCap.Round,
                )
            }
            AccessoryKind.TERMOSTAT -> {
                // Termometer: tabung + bulb
                drawLine(
                    color = tint,
                    start = Offset(w * 0.5f, h * 0.16f),
                    end = Offset(w * 0.5f, h * 0.62f),
                    strokeWidth = w * 0.1f,
                    cap = StrokeCap.Round,
                )
                drawCircle(color = tint, radius = w * 0.16f, center = Offset(w * 0.5f, h * 0.76f))
            }
            AccessoryKind.MIKROFON -> {
                // Kapsul mic + kaki
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.34f, h * 0.12f),
                    size = Size(w * 0.32f, h * 0.5f),
                    cornerRadius = CornerRadius(w * 0.16f, w * 0.16f),
                    style = stroke,
                )
                drawArc(
                    color = tint,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.22f, h * 0.3f),
                    size = Size(w * 0.56f, h * 0.44f),
                    style = stroke,
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.5f, h * 0.76f),
                    end = Offset(w * 0.5f, h * 0.88f),
                    strokeWidth = w * 0.09f,
                    cap = StrokeCap.Round,
                )
            }
            AccessoryKind.SUHU -> {
                drawLine(
                    color = tint,
                    start = Offset(w * 0.28f, h * 0.72f),
                    end = Offset(w * 0.72f, h * 0.28f),
                    strokeWidth = w * 0.09f,
                    cap = StrokeCap.Round,
                )
                drawCircle(color = tint, radius = w * 0.1f, center = Offset(w * 0.72f, h * 0.28f))
            }
            AccessoryKind.KELEMBAPAN -> {
                // Tetes air
                drawArc(
                    color = tint,
                    startAngle = -60f,
                    sweepAngle = 300f,
                    useCenter = false,
                    topLeft = Offset(w * 0.24f, h * 0.26f),
                    size = Size(w * 0.52f, h * 0.52f),
                    style = stroke,
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.5f, h * 0.1f),
                    end = Offset(w * 0.5f, h * 0.3f),
                    strokeWidth = w * 0.09f,
                    cap = StrokeCap.Round,
                )
            }
            AccessoryKind.KONTAK -> {
                // Jendela: kotak dengan palang
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.18f, h * 0.18f),
                    size = Size(w * 0.64f, h * 0.64f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f),
                    style = stroke,
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.5f, h * 0.18f),
                    end = Offset(w * 0.5f, h * 0.82f),
                    strokeWidth = w * 0.08f,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Pill & tombol kecil
// ---------------------------------------------------------------------------

@Composable
private fun SimulasiPill() {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = palette.info.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, palette.info),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Text(
            text = "Simulasi",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = palette.info,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}

@Composable
private fun StatusPill(text: String, color: Color) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, color),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun SmallActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = if (enabled) palette.primary else palette.primary.copy(alpha = 0.4f),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = palette.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 11.dp, horizontal = 12.dp),
        )
    }
}
