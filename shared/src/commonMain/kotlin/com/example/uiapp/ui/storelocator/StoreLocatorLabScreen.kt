package com.example.uiapp.ui.storelocator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.HapticKind
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabCard
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.LabSectionTitle
import com.example.uiapp.ui.components.StatusPill
import com.example.uiapp.ui.components.UiTopBar
import kotlin.math.roundToInt

private data class Store(
    val id: Int,
    val name: String,
    val region: String,
    val x: Float, // 0..1 pada peta skematik.
    val y: Float,
    val open: String,
    val distanceKm: Float,
)

// Peta skematik 6 x 4 km; koordinat dinormalisasi. Seluruh data mock.
private val Stores = listOf(
    Store(1, "Toko Sudirman", "Sudirman", 0.28f, 0.30f, "08.00–21.00", 1.2f),
    Store(2, "Toko Thamrin", "Thamrin", 0.36f, 0.26f, "08.00–21.00", 1.6f),
    Store(3, "Toko Senayan", "Senayan", 0.22f, 0.20f, "09.00–20.00", 2.1f),
    Store(4, "Toko Kemang", "Kemang", 0.70f, 0.34f, "09.00–21.00", 2.8f),
    Store(5, "Toko Tebet", "Tebet", 0.76f, 0.42f, "08.00–20.00", 3.4f),
    Store(6, "Toko Kelapa Gading", "Gading", 0.62f, 0.72f, "10.00–21.00", 4.6f),
    Store(7, "TokoBSD Serpong", "Serpong", 0.16f, 0.78f, "10.00–20.00", 5.2f),
    Store(8, "Toko Bekasi Square", "Bekasi", 0.88f, 0.24f, "09.00–21.00", 5.8f),
)

// "Lokasi Anda" (simulasi) di tengah peta.
private const val UserX = 0.5f
private const val UserY = 0.5f

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StoreLocatorLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    var zoom by remember { mutableFloatStateOf(1f) }
    var selectedId by remember { mutableStateOf<Int?>(null) }
    var radiusKm by remember { mutableFloatStateOf(2f) }
    var permissionDenied by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    BackHandler(enabled = true) { onBack() }

    val transformState = rememberTransformableState { zoomChange, _, _ ->
        zoom = (zoom * zoomChange).coerceIn(1f, 3f)
    }

    // Sinkron daftar → peta: memilih dari daftar menggulir bukan diperlukan, tapi
    // peta → daftar: pemilihan marker menggulirkan daftar ke toko terkait.
    LaunchedEffect(selectedId) {
        val id = selectedId ?: return@LaunchedEffect
        val index = Stores.indexOfFirst { it.id == id }
        if (index >= 0) listState.animateScrollToItem(index)
    }

    val inRadius = Stores.count { it.distanceKm <= radiusKm && !permissionDenied }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background),
    ) {
        UiTopBar(
            title = "Store Locator",
            subtitle = "Peta skematik & daftar toko — simulasi",
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            LabIntroCard(
                title = "Peta & lokasi adalah simulasi",
                body = "Peta digambar sebagai skema lokal; jarak dihitung dari koordinat mock. " +
                    "Cubit atau geser slider untuk zoom; ketuk marker atau daftar untuk sinkronisasi dua arah.",
            )

            if (permissionDenied) {
                LabCard {
                    Text(
                        "Lokasi tidak diizinkan",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.danger,
                    )
                    Text(
                        "Estimasi jarak dan geofence dinonaktifkan. Aktifkan izin lokasi (simulasi) untuk melanjutkan.",
                        fontSize = 11.sp,
                        color = palette.textSecondary,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .background(palette.primary, RoundedCornerShape(10.dp))
                                .clickable {
                                    PerformHapticFeedback(HapticKind.MEDIUM)
                                    permissionDenied = false
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Text("Izinkan (simulasi)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = palette.onPrimary)
                        }
                        Box(
                            modifier = Modifier
                                .background(palette.surfaceMuted, RoundedCornerShape(10.dp))
                                .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                                .clickable { }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Text("Buka Pengaturan", fontSize = 11.sp, color = palette.textMuted)
                        }
                    }
                }
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(palette.surfaceMuted, RoundedCornerShape(16.dp))
                    .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .transformable(transformState),
            ) {
                val density = LocalDensity.current
                val w = with(density) { maxWidth.toPx() }
                val h = with(density) { 280.dp.toPx() }

                fun px(x: Float): Float = w / 2f + (x - UserX) * w * zoom
                fun py(y: Float): Float = h / 2f + (y - UserY) * h * zoom

                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Jalan skematik: grid garis tipis.
                    val step = 56f * zoom
                    var xLine = step
                    while (xLine < size.width) {
                        drawLine(palette.border, Offset(xLine, 0f), Offset(xLine, size.height), 1f)
                        xLine += step
                    }
                    var yLine = step
                    while (yLine < size.height) {
                        drawLine(palette.border, Offset(0f, yLine), Offset(size.width, yLine), 1f)
                        yLine += step
                    }

                    // Lingkaran geofence di sekitar pengguna.
                    if (!permissionDenied) {
                        val radiusPx = (radiusKm / 6f) * w * zoom
                        drawCircle(
                            color = palette.primary.copy(alpha = 0.08f),
                            radius = radiusPx,
                            center = Offset(px(UserX), py(UserY)),
                        )
                        drawCircle(
                            color = palette.primary.copy(alpha = 0.5f),
                            radius = radiusPx,
                            center = Offset(px(UserX), py(UserY)),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f),
                        )
                    }

                    // Titik pengguna.
                    drawCircle(palette.info, radius = 7f * zoom.coerceAtLeast(1f), center = Offset(px(UserX), py(UserY)))

                    // Marker toko: klaster per region saat zoom rendah.
                    if (zoom < 1.8f) {
                        Stores.groupBy { it.region }.forEach { (_, group) ->
                            val cx = group.map { px(it.x) }.average().toFloat()
                            val cy = group.map { py(it.y) }.average().toFloat()
                            drawCircle(palette.primary, radius = 14f, center = Offset(cx, cy))
                            drawCircle(palette.onPrimary, radius = 14f, center = Offset(cx, cy), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
                        }
                    } else {
                        Stores.forEach { s ->
                            val active = s.id == selectedId
                            drawCircle(
                                color = if (active) palette.danger else palette.primary,
                                radius = if (active) 12f else 8f,
                                center = Offset(px(s.x), py(s.y)),
                            )
                            if (active) {
                                drawCircle(
                                    color = palette.danger,
                                    radius = 18f,
                                    center = Offset(px(s.x), py(s.y)),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f),
                                )
                            }
                        }
                    }
                }

                // Overlay target sentuh: klaster saat zoom rendah, toko saat zoom tinggi.
                if (zoom < 1.8f) {
                    Stores.groupBy { it.region }.forEach { (region, group) ->
                        val cx = group.map { px(it.x) }.average().toFloat()
                        val cy = group.map { py(it.y) }.average().toFloat()
                        Box(
                            modifier = Modifier
                                .offset { IntOffset((cx - 22f).roundToInt(), (cy - 22f).roundToInt()) }
                                .size(44.dp)
                                .semantics { contentDescription = "Klaster $region, ${group.size} toko" }
                                .clickable {
                                    PerformHapticFeedback(HapticKind.SELECTION)
                                    zoom = 2.4f // Perbesar untuk membuka klaster.
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                group.size.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.onPrimary,
                            )
                        }
                    }
                } else {
                    Stores.forEach { s ->
                        Box(
                            modifier = Modifier
                                .offset { IntOffset((px(s.x) - 22f).roundToInt(), (py(s.y) - 22f).roundToInt()) }
                                .size(44.dp)
                                .semantics { contentDescription = "Toko ${s.name}, ${s.distanceKm} km" }
                                .clickable {
                                    PerformHapticFeedback(HapticKind.SELECTION)
                                    selectedId = if (selectedId == s.id) null else s.id
                                },
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(palette.surface.copy(alpha = 0.9f), RoundedCornerShape(10.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text("Zoom ${(zoom * 100).toInt()}% · ${if (zoom < 1.8f) "mode klaster" else "mode toko"}", fontSize = 10.sp, color = palette.textSecondary)
                }
            }

            LabSectionTitle("Geofence radius", "Tarik slider untuk mengubah radius pencarian")
            if (permissionDenied) {
                StatusPill("Geofence dinonaktifkan (lokasi tidak diizinkan)", palette.textMuted)
            } else {
                LabCard {
                    Text(
                        "Radius: ${radiusKm.toInt()} km — $inRadius toko terjangkau",
                        fontSize = 12.sp,
                        color = palette.textPrimary,
                    )
                    Slider(
                        value = radiusKm,
                        onValueChange = { radiusKm = it },
                        valueRange = 1f..6f,
                        enabled = !permissionDenied,
                    )
                    Text(
                        "Estimasi jarak dihitung dari koordinat skematik (simulasi), bukan GPS.",
                        fontSize = 10.sp,
                        color = palette.textMuted,
                    )
                }
            }

            LabSectionTitle("Daftar toko", "Ketuk untuk menyorot di peta — dua arah")
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(palette.background)
                .navigationBarsPadding(),
        ) {
            items(Stores, key = { it.id }) { store ->
                val active = store.id == selectedId
                val within = !permissionDenied && store.distanceKm <= radiusKm
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(palette.surface, RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            if (active) palette.primary else palette.border,
                            RoundedCornerShape(12.dp),
                        )
                        .clickable {
                            PerformHapticFeedback(HapticKind.SELECTION)
                            selectedId = if (selectedId == store.id) null else store.id
                        }
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            store.name,
                            fontSize = 13.sp,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (active) palette.primary else palette.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (active) StatusPill("Dipilih di peta", palette.primary)
                    }
                    Text(
                        "${store.region} · buka ${store.open}" +
                            (if (permissionDenied) "" else " · ${store.distanceKm} km dari Anda"),
                        fontSize = 11.sp,
                        color = palette.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!permissionDenied && !within) {
                        Text("Di luar radius ${radiusKm.toInt()} km", fontSize = 10.sp, color = palette.textMuted)
                    }
                }
            }
        }
    }
}
