package com.example.uiapp.ui.standbymode

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.AppPalette
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val DEMO_DATE = "Rabu, 30 September 2026"
private const val DEMO_START_MINUTES = 21 * 60 + 45

private enum class StandbyWidget(val label: String) {
    KALENDER("Kalender"),
    KOMPAS("Kompas"),
    FOTO("Foto"),
    CUACA("Cuaca"),
}

private enum class WeatherState(val label: String) {
    LOADED("Data tersedia"),
    LOADING("Memuat…"),
    ERROR("Gagal memuat"),
}

private data class AgendaItem(
    val id: String,
    val time: String,
    val title: String,
)

private data class WeatherHour(
    val id: String,
    val hour: String,
    val temp: String,
    val label: String,
)

private data class PhotoCaption(
    val id: String,
    val title: String,
    val meta: String,
)

private fun buildAgenda(): List<AgendaItem> = listOf(
    AgendaItem("agenda-1", "08.30", "Rapat persiapan pameran"),
    AgendaItem("agenda-2", "13.00", "Antar karya ke gedung serbaguna"),
    AgendaItem("agenda-3", "19.30", "Makan malam keluarga"),
)

private val standbyWeatherHours = listOf(
    WeatherHour("jam-18", "18.00", "29°", "Cerah"),
    WeatherHour("jam-19", "19.00", "28°", "Berawan"),
    WeatherHour("jam-20", "20.00", "27°", "Berawan"),
    WeatherHour("jam-21", "21.00", "26°", "Hujan ringan"),
)

private val standbyPhotos = listOf(
    PhotoCaption("foto-1", "Dermaga Sore", "Album tiruan · Simulasi"),
    PhotoCaption("foto-2", "Kebun Belakang", "Album tiruan · Simulasi"),
    PhotoCaption("foto-3", "Puncak Kabut", "Album tiruan · Simulasi"),
)

private fun pad2(value: Int): String = if (value < 10) "0$value" else "$value"

private fun formatClock(totalMinutes: Int): String {
    val h = (totalMinutes / 60) % 24
    val m = totalMinutes % 60
    return pad2(h) + ":" + pad2(m)
}

private fun cardinal(heading: Float): String {
    val names = listOf("Utara", "Timur Laut", "Timur", "Tenggara", "Selatan", "Barat Daya", "Barat", "Barat Laut")
    val idx = (((heading % 360f) + 360f) % 360f / 45f).toInt() % 8
    return names[idx]
}

private fun photoBrush(palette: AppPalette, index: Int, night: Boolean): Brush {
    if (night) {
        return Brush.linearGradient(
            colors = listOf(palette.danger.copy(alpha = 0.35f), palette.danger.copy(alpha = 0.12f)),
        )
    }
    return when (index % 3) {
        0 -> Brush.linearGradient(
            colors = listOf(palette.primary.copy(alpha = 0.45f), palette.info.copy(alpha = 0.30f)),
        )
        1 -> Brush.linearGradient(
            colors = listOf(palette.warning.copy(alpha = 0.40f), palette.danger.copy(alpha = 0.25f)),
        )
        else -> Brush.linearGradient(
            colors = listOf(palette.info.copy(alpha = 0.40f), palette.success.copy(alpha = 0.30f)),
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StandbyModeLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current

    var clockMinutes by remember { mutableIntStateOf(DEMO_START_MINUTES) }
    var widgetIndex by remember { mutableIntStateOf(0) }
    var nightMode by remember { mutableStateOf(false) }
    var alwaysOn by remember { mutableStateOf(false) }
    var bigFont by remember { mutableStateOf(false) }
    var heading by remember { mutableFloatStateOf(0f) }
    var photoIndex by remember { mutableIntStateOf(0) }
    var widgetDragAccum by remember { mutableFloatStateOf(0f) }

    val agenda = remember { mutableStateListOf<AgendaItem>().apply { addAll(buildAgenda()) } }
    var weatherState by remember { mutableStateOf(WeatherState.LOADED) }
    var weatherToken by remember { mutableIntStateOf(0) }
    var weatherFailNext by remember { mutableStateOf(false) }

    val widget = StandbyWidget.entries.getOrElse(widgetIndex) { StandbyWidget.KALENDER }

    // Jam tiruan: maju satu menit tiap tiga detik (Simulasi)
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            clockMinutes = (clockMinutes + 1) % (24 * 60)
        }
    }

    LaunchedEffect(weatherToken) {
        if (weatherToken > 0) {
            weatherState = WeatherState.LOADING
            delay(1200)
            weatherState = if (weatherFailNext) WeatherState.ERROR else WeatherState.LOADED
        }
    }

    val clockText = formatClock(clockMinutes)

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "StandBy Nightstand",
                subtitle = "Jam besar lanskap & mode malam",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LabSectionTitle(
                    title = "Meja Samping Tempat Tidur",
                    subtitle = "Jam besar, widget bergantian",
                    modifier = Modifier.weight(1f),
                )
                SimulasiBadge()
            }

            LabIntroCard(
                title = "Tampilan duduk untuk ponsel terpasang di dock",
                body = "Jam besar plus widget yang dapat digeser (kalender, kompas, foto, cuaca). Mode malam merah menurunkan kecerahan dan menyembunyikan warna. Jam, agenda, foto, dan cuaca seluruhnya tiruan — tidak ada layanan sistem yang diakses (Simulasi).",
            )

            // Panggung StandBy — adaptif lebar layar
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.background,
                border = BorderStroke(1.dp, if (nightMode) palette.danger else palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                ) {
                    val wide = maxWidth >= 600.dp
                    val clockSp = (if (wide) 64f else 48f) * (if (bigFont) 1.35f else 1f)
                    val dateSp = (if (wide) 15f else 13f) * (if (bigFont) 1.3f else 1f)

                    if (wide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ClockPanel(
                                clockText = clockText,
                                clockSp = clockSp,
                                dateSp = dateSp,
                                night = nightMode,
                                modifier = Modifier.weight(1f),
                            )
                            WidgetPanel(
                                widget = widget,
                                night = nightMode,
                                bigFont = bigFont,
                                heading = heading,
                                onHeading = { delta ->
                                    heading = ((heading + delta) % 360f + 360f) % 360f
                                },
                                photoIndex = photoIndex,
                                onPhotoIndex = { photoIndex = it },
                                agenda = agenda,
                                weatherState = weatherState,
                                onRetryWeather = {
                                    weatherFailNext = false
                                    weatherToken++
                                },
                                onDragWidget = { delta ->
                                    widgetDragAccum += delta
                                    if (widgetDragAccum < -40f) {
                                        widgetIndex = (widgetIndex + 1) % StandbyWidget.entries.size
                                        widgetDragAccum = 0f
                                    } else if (widgetDragAccum > 40f) {
                                        widgetIndex = (widgetIndex + StandbyWidget.entries.size - 1) % StandbyWidget.entries.size
                                        widgetDragAccum = 0f
                                    }
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            ClockPanel(
                                clockText = clockText,
                                clockSp = clockSp,
                                dateSp = dateSp,
                                night = nightMode,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            WidgetPanel(
                                widget = widget,
                                night = nightMode,
                                bigFont = bigFont,
                                heading = heading,
                                onHeading = { delta ->
                                    heading = ((heading + delta) % 360f + 360f) % 360f
                                },
                                photoIndex = photoIndex,
                                onPhotoIndex = { photoIndex = it },
                                agenda = agenda,
                                weatherState = weatherState,
                                onRetryWeather = {
                                    weatherFailNext = false
                                    weatherToken++
                                },
                                onDragWidget = { delta ->
                                    widgetDragAccum += delta
                                    if (widgetDragAccum < -40f) {
                                        widgetIndex = (widgetIndex + 1) % StandbyWidget.entries.size
                                        widgetDragAccum = 0f
                                    } else if (widgetDragAccum > 40f) {
                                        widgetIndex = (widgetIndex + StandbyWidget.entries.size - 1) % StandbyWidget.entries.size
                                        widgetDragAccum = 0f
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }

            // Pemilih widget + dots
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SmallActionButton(
                            label = "◀ Widget",
                            enabled = true,
                            onClick = {
                                widgetIndex = (widgetIndex + StandbyWidget.entries.size - 1) % StandbyWidget.entries.size
                            },
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = widget.label,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (nightMode) palette.danger else palette.textPrimary,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        SmallActionButton(
                            label = "Widget ▶",
                            enabled = true,
                            onClick = { widgetIndex = (widgetIndex + 1) % StandbyWidget.entries.size },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        StandbyWidget.entries.forEach { entry ->
                            val selected = entry == widget
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(if (selected) 10.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (selected) palette.primary else palette.border,
                                    )
                                    .border(
                                        BorderStroke(1.dp, palette.border),
                                        CircleShape,
                                    )
                                    .semantics {
                                        contentDescription = "Widget ${entry.label} ${if (selected) "aktif" else "tidak aktif"}"
                                    },
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SmallActionButton(
                            label = if (nightMode) "Mode Malam Merah: Aktif" else "Mode Malam Merah: Nonaktif",
                            enabled = true,
                            onClick = { nightMode = !nightMode },
                            modifier = Modifier.weight(1f),
                        )
                        SmallActionButton(
                            label = if (alwaysOn) "Selalu Aktif: Aktif" else "Selalu Aktif: Nonaktif",
                            enabled = true,
                            onClick = { alwaysOn = !alwaysOn },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SmallActionButton(
                            label = if (bigFont) "Huruf: Besar" else "Huruf: Normal",
                            enabled = true,
                            onClick = { bigFont = !bigFont },
                            modifier = Modifier.weight(1f),
                        )
                        SmallActionButton(
                            label = if (agenda.isEmpty()) "Muat agenda" else "Kosongkan agenda",
                            enabled = true,
                            onClick = {
                                if (agenda.isEmpty()) {
                                    agenda.addAll(buildAgenda())
                                } else {
                                    agenda.clear()
                                }
                            },
                            modifier = Modifier.weight(1f),
                        )
                        SmallActionButton(
                            label = if (weatherFailNext) "Cuaca: gagal" else "Cuaca: normal",
                            enabled = true,
                            onClick = { weatherFailNext = !weatherFailNext },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Pratinjau selalu aktif
            if (alwaysOn) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, if (nightMode) palette.danger else palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Pratinjau Selalu Aktif — Simulasi",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (nightMode) palette.danger else palette.textPrimary,
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.background)
                                .border(
                                    BorderStroke(1.dp, if (nightMode) palette.danger else palette.border),
                                    RoundedCornerShape(12.dp),
                                )
                                .alpha(0.65f),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = clockText,
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (nightMode) palette.danger else palette.textPrimary,
                                )
                                Text(
                                    text = DEMO_DATE,
                                    fontSize = 11.sp,
                                    color = if (nightMode) palette.danger.copy(alpha = 0.8f) else palette.textMuted,
                                )
                            }
                        }
                        Text(
                            text = "Tampilan diredupkan otomatis saat layar dikunci — tiruan, tanpa akses layar kunci sistem",
                            fontSize = 10.sp,
                            color = palette.textMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "Catatan pola StandBy Nightstand:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = "• Layout adaptif: lebar ≥ 600dp tampil berdampingan (lanskap), di bawah itu menumpuk",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        text = "• Mode malam merah memakai turunan palette.danger, meredupkan konten, dan menyembunyikan warna lain",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        text = "• Seluruh teks memakai satuan sp sehingga tetap terbaca pada skala huruf sistem yang besar",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                    Text(
                        text = "• Simulasi: jam, kalender, kompas, foto, dan cuaca tiruan tanpa layanan sistem",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = palette.textSecondary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun ClockPanel(
    clockText: String,
    clockSp: Float,
    dateSp: Float,
    night: Boolean,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    val timeColor = if (night) palette.danger else palette.textPrimary
    val dateColor = if (night) palette.danger.copy(alpha = 0.8f) else palette.textSecondary

    Column(
        modifier = modifier
            .alpha(if (night) 0.85f else 1f)
            .semantics { contentDescription = "Jam $clockText, $DEMO_DATE" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = clockText,
            fontSize = clockSp.sp,
            fontWeight = FontWeight.ExtraBold,
            color = timeColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = DEMO_DATE,
            fontSize = dateSp.sp,
            fontWeight = FontWeight.SemiBold,
            color = dateColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "Jam tiruan — Simulasi",
            fontSize = 10.sp,
            color = if (night) palette.danger.copy(alpha = 0.6f) else palette.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun WidgetPanel(
    widget: StandbyWidget,
    night: Boolean,
    bigFont: Boolean,
    heading: Float,
    onHeading: (Float) -> Unit,
    photoIndex: Int,
    onPhotoIndex: (Int) -> Unit,
    agenda: List<AgendaItem>,
    weatherState: WeatherState,
    onRetryWeather: () -> Unit,
    onDragWidget: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    val titleColor = if (night) palette.danger else palette.textPrimary
    val bodyColor = if (night) palette.danger.copy(alpha = 0.75f) else palette.textSecondary
    val mutedColor = if (night) palette.danger.copy(alpha = 0.55f) else palette.textMuted
    val accent = if (night) palette.danger else palette.primary
    val titleSp = 14f * (if (bigFont) 1.3f else 1f)
    val bodySp = 12f * (if (bigFont) 1.3f else 1f)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surfaceMuted)
            .border(BorderStroke(1.dp, if (night) palette.danger else palette.border), RoundedCornerShape(12.dp))
            .pointerInput(widget) {
                detectDragGestures(
                    onDragEnd = { },
                    onDragCancel = { },
                ) { change, dragAmount ->
                    change.consume()
                    onDragWidget(dragAmount.x)
                }
            }
            .alpha(if (night) 0.85f else 1f)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = widget.label,
                fontSize = titleSp.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "Geser untuk ganti — Simulasi",
                fontSize = 10.sp,
                color = mutedColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
        ) {
            when (widget) {
                StandbyWidget.KALENDER -> AgendaWidget(
                    agenda = agenda,
                    titleColor = titleColor,
                    bodyColor = bodyColor,
                    mutedColor = mutedColor,
                    accent = accent,
                    titleSp = titleSp,
                    bodySp = bodySp,
                    modifier = Modifier.fillMaxSize(),
                )
                StandbyWidget.KOMPAS -> CompassWidget(
                    heading = heading,
                    onHeading = onHeading,
                    bodyColor = bodyColor,
                    mutedColor = mutedColor,
                    accent = accent,
                    bodySp = bodySp,
                    modifier = Modifier.fillMaxSize(),
                )
                StandbyWidget.FOTO -> PhotoWidget(
                    photoIndex = photoIndex,
                    onPhotoIndex = onPhotoIndex,
                    night = night,
                    titleColor = titleColor,
                    mutedColor = mutedColor,
                    titleSp = titleSp,
                    bodySp = bodySp,
                    modifier = Modifier.fillMaxSize(),
                )
                StandbyWidget.CUACA -> WeatherWidget(
                    state = weatherState,
                    onRetry = onRetryWeather,
                    night = night,
                    titleColor = titleColor,
                    bodyColor = bodyColor,
                    mutedColor = mutedColor,
                    accent = accent,
                    titleSp = titleSp,
                    bodySp = bodySp,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun AgendaWidget(
    agenda: List<AgendaItem>,
    titleColor: Color,
    bodyColor: Color,
    mutedColor: Color,
    accent: Color,
    titleSp: Float,
    bodySp: Float,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = DEMO_DATE,
            fontSize = titleSp.sp,
            fontWeight = FontWeight.SemiBold,
            color = titleColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (agenda.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.surface)
                    .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Tidak ada agenda hari ini",
                    fontSize = bodySp.sp,
                    color = mutedColor,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else {
            agenda.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = accent.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, accent),
                    ) {
                        Text(
                            text = item.time,
                            fontSize = (bodySp - 1f).sp,
                            fontWeight = FontWeight.Bold,
                            color = accent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        )
                    }
                    Text(
                        text = item.title,
                        fontSize = bodySp.sp,
                        color = bodyColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Text(
                text = "Agenda tiruan — Simulasi",
                fontSize = 10.sp,
                color = mutedColor,
            )
        }
    }
}

@Composable
private fun CompassWidget(
    heading: Float,
    onHeading: (Float) -> Unit,
    bodyColor: Color,
    mutedColor: Color,
    accent: Color,
    bodySp: Float,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(104.dp)
                .clip(CircleShape)
                .background(palette.surface)
                .border(BorderStroke(1.dp, palette.border), CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onHeading(dragAmount.x / 3f)
                    }
                }
                .semantics {
                    contentDescription = "Kompas simulasi, derajat ${heading.toInt()} arah ${cardinal(heading)}"
                },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = minOf(center.x, center.y) - 4.dp.toPx()
                drawCircle(color = palette.border, radius = radius, style = Stroke(width = 1.dp.toPx()))
                drawCircle(color = palette.border, radius = radius * 0.62f, style = Stroke(width = 1.dp.toPx()))

                for (deg in 0 until 360 step 15) {
                    val radians = (deg - 90) * PI / 180.0
                    val outer = Offset(
                        x = center.x + (cos(radians) * radius).toFloat(),
                        y = center.y + (sin(radians) * radius).toFloat(),
                    )
                    val innerScale = if (deg % 90 == 0) 0.72f else 0.84f
                    val inner = Offset(
                        x = center.x + (cos(radians) * radius * innerScale).toFloat(),
                        y = center.y + (sin(radians) * radius * innerScale).toFloat(),
                    )
                    drawLine(
                        color = if (deg % 90 == 0) bodyColor else mutedColor,
                        start = inner,
                        end = outer,
                        strokeWidth = if (deg % 90 == 0) 2.dp.toPx() else 1.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }

                rotate(degrees = heading, pivot = center) {
                    val north = Path().apply {
                        moveTo(center.x, center.y - radius * 0.62f)
                        lineTo(center.x + radius * 0.13f, center.y)
                        lineTo(center.x - radius * 0.13f, center.y)
                        close()
                    }
                    val south = Path().apply {
                        moveTo(center.x, center.y + radius * 0.62f)
                        lineTo(center.x + radius * 0.13f, center.y)
                        lineTo(center.x - radius * 0.13f, center.y)
                        close()
                    }
                    drawPath(path = north, color = accent)
                    drawPath(path = south, color = mutedColor)
                }
            }
        }
        Text(
            text = "${heading.toInt()}° · ${cardinal(heading)}",
            fontSize = bodySp.sp,
            fontWeight = FontWeight.SemiBold,
            color = bodyColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "Kompas tiruan — Simulasi (geser untuk memutar)",
            fontSize = 10.sp,
            color = mutedColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun PhotoWidget(
    photoIndex: Int,
    onPhotoIndex: (Int) -> Unit,
    night: Boolean,
    titleColor: Color,
    mutedColor: Color,
    titleSp: Float,
    bodySp: Float,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    val photo = standbyPhotos.getOrElse(photoIndex) { standbyPhotos.first() }
    val brush = remember(photoIndex, night) { photoBrush(palette, photoIndex, night) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(brush)
                .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(10.dp))
                .semantics { contentDescription = "Foto tiruan ${photo.title}" },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = photo.title,
                    fontSize = titleSp.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = photo.meta,
                    fontSize = (bodySp - 1f).sp,
                    color = mutedColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            SmallActionButton(
                label = "Foto lain",
                enabled = true,
                onClick = { onPhotoIndex((photoIndex + 1) % standbyPhotos.size) },
            )
        }
    }
}

@Composable
private fun WeatherWidget(
    state: WeatherState,
    onRetry: () -> Unit,
    night: Boolean,
    titleColor: Color,
    bodyColor: Color,
    mutedColor: Color,
    accent: Color,
    titleSp: Float,
    bodySp: Float,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when (state) {
            WeatherState.LOADING -> {
                Text(
                    text = "Memuat cuaca…",
                    fontSize = titleSp.sp,
                    fontWeight = FontWeight.Bold,
                    color = mutedColor,
                )
                Text(
                    text = "Mengambil data tiruan, mohon tunggu",
                    fontSize = bodySp.sp,
                    color = mutedColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            WeatherState.ERROR -> {
                Text(
                    text = "Gagal memuat cuaca",
                    fontSize = titleSp.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.danger,
                )
                Text(
                    text = "Sumber data tiruan tidak merespons",
                    fontSize = bodySp.sp,
                    color = bodyColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                SmallActionButton(
                    label = "Coba lagi",
                    enabled = true,
                    onClick = onRetry,
                )
            }
            WeatherState.LOADED -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "28°",
                        fontSize = (titleSp * 1.6f).sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (night) palette.danger else accent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Column {
                        Text(
                            text = "Berawan",
                            fontSize = bodySp.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = bodyColor,
                        )
                        Text(
                            text = "Jakarta · tiruan",
                            fontSize = (bodySp - 1f).sp,
                            color = mutedColor,
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    standbyWeatherHours.forEach { hour ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(hour.hour, fontSize = (bodySp - 2f).sp, color = mutedColor)
                            Text(
                                hour.temp,
                                fontSize = bodySp.sp,
                                fontWeight = FontWeight.Bold,
                                color = titleColor,
                            )
                            Text(
                                hour.label,
                                fontSize = (bodySp - 2f).sp,
                                color = bodyColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                SmallActionButton(
                    label = "Muat ulang cuaca",
                    enabled = true,
                    onClick = onRetry,
                )
                Text(
                    text = "Cuaca tiruan — Simulasi",
                    fontSize = 10.sp,
                    color = mutedColor,
                )
            }
        }
    }
}

@Composable
private fun SmallActionButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        modifier = modifier,
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) palette.textPrimary else palette.textMuted,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun SimulasiBadge(modifier: Modifier = Modifier) {
    val palette = LocalAppPalette.current
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = palette.info.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, palette.info),
        modifier = modifier,
    ) {
        Text(
            text = "Simulasi",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = palette.info,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}
