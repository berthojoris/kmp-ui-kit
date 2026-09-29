package com.example.uiapp.ui.sharedelement

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.icons.MgIosBackButton
import com.example.uiapp.ui.components.UiTopBar

private data class Destination(
    val id: String,
    val name: String,
    val region: String,
    val price: String,
    val rating: String,
    val tag: String,
    val description: String,
    val start: Color,
    val end: Color,
)

private val Destinations = listOf(
    Destination(
        id = "obsidian",
        name = "The Obsidian Escape",
        region = "Uluwatu, Bali",
        price = "Rp 8.400.000",
        rating = "4.9",
        tag = "VILLA",
        description = "Vila tebing privat dengan panorama samudra Hindia dan kolam tanpa batas yang menyatu dengan cakrawala.",
        start = Color(0xFF0F3D34),
        end = Color(0xFF05201B),
    ),
    Destination(
        id = "aurora",
        name = "Aurora Hillside",
        region = "Ubud, Bali",
        price = "Rp 5.150.000",
        rating = "4.8",
        tag = "RESORT",
        description = "Retreat tropis tersembunyi di tengah hutan hujan Ubud dengan yoga deck dan sungai pribadi.",
        start = Color(0xFF1E3A5F),
        end = Color(0xFF0B1B2E),
    ),
    Destination(
        id = "solstice",
        name = "Solstice Loft",
        region = "Senopati, Jakarta",
        price = "Rp 3.200.000",
        rating = "4.7",
        tag = "LOFT",
        description = "Loft industrial dengan langit-langit tinggi, jendela setinggi ruangan, dan koleksi seni kontemporer.",
        start = Color(0xFF4A2F27),
        end = Color(0xFF1F130F),
    ),
    Destination(
        id = "palm",
        name = "Palm Cove Suite",
        region = "Sanur, Bali",
        price = "Rp 2.750.000",
        rating = "4.6",
        tag = "SUITE",
        description = "Suite tepi pantai dengan akses langsung ke pasir putih dan teras matahari menghadap timur.",
        start = Color(0xFF2F5D4E),
        end = Color(0xFF0E2B23),
    ),
    Destination(
        id = "mirage",
        name = "Mirage Pavilion",
        region = "Nusa Dua, Bali",
        price = "Rp 6.900.000",
        rating = "4.9",
        tag = "PAVILION",
        description = "Paviliun arsitektur tropis modern dengan taman privat, air mancur, dan chef pribadi.",
        start = Color(0xFF5A4632),
        end = Color(0xFF2A1F14),
    ),
    Destination(
        id = "northern",
        name = "Northern Light Lodge",
        region = "Lembang, Bandung",
        price = "Rp 4.050.000",
        rating = "4.8",
        tag = "LODGE",
        description = "Lodge kayu di dataran tinggi dengan perapian, jacuzzi outdoor, dan kabut pagi pegunungan.",
        start = Color(0xFF3B3550),
        end = Color(0xFF171426),
    ),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SharedElementScreen(onBack: () -> Unit) {
    var selected by remember { mutableStateOf<Destination?>(null) }

    BackHandler(enabled = true) {
        if (selected != null) selected = null else onBack()
    }

    SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = selected,
            transitionSpec = {
                fadeIn(tween(260)).togetherWith(fadeOut(tween(180)))
            },
            label = "shared-element",
            modifier = Modifier.fillMaxSize(),
        ) { target ->
            if (target == null) {
                DestinationGrid(
                    onSelect = { selected = it },
                    onBack = onBack,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent,
                )
            } else {
                DestinationDetail(
                    destination = target,
                    onBack = { selected = null },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent,
                )
            }
        }
    }
}

@Composable
private fun DestinationGrid(
    onSelect: (Destination) -> Unit,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val palette = LocalAppPalette.current

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Shared Element",
                subtitle = "Kartu membesar mulus ke halaman detail",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(Destinations, key = { it.id }) { destination ->
                DestinationCard(
                    destination = destination,
                    onClick = { onSelect(destination) },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                )
            }
        }
    }
}

@Composable
private fun DestinationCard(
    destination: Destination,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val palette = LocalAppPalette.current

    with(sharedTransitionScope) {
        Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.25f)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "art-${destination.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                        ),
                ) {
                    DestinationArt(
                        destination = destination,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    )
                    Text(
                        text = destination.tag,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.9f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = palette.primary,
                    )
                }
                Column(modifier = Modifier.padding(13.dp)) {
                    Text(
                        text = destination.name,
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "name-${destination.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            enter = fadeIn(),
                            exit = fadeOut(),
                        ),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = destination.region,
                            fontSize = 11.sp,
                            color = palette.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "\u2605 ${destination.rating}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationDetail(
    destination: Destination,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val palette = LocalAppPalette.current

    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(palette.background)
                .verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = "art-${destination.id}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                    ),
            ) {
                DestinationArt(destination = destination, modifier = Modifier.fillMaxSize())
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.28f),
                                    Color.Transparent,
                                    Color.Transparent,
                                ),
                            ),
                        ),
                )
                MgIosBackButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(start = 16.dp, top = 8.dp),
                    backgroundColor = Color.White.copy(alpha = 0.92f),
                    borderColor = palette.border,
                    iconTint = palette.textPrimary,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
            ) {
                Text(
                    text = destination.tag,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.6.sp,
                    color = palette.textMuted,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = destination.name,
                    modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "name-${destination.id}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    letterSpacing = (-0.4).sp,
                    color = palette.textPrimary,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "\u2605 ${destination.rating}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "\u00B7", fontSize = 13.sp, color = palette.textMuted)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = destination.region,
                        fontSize = 13.sp,
                        color = palette.textSecondary,
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = destination.description,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = palette.textSecondary,
                )

                Spacer(modifier = Modifier.height(26.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailChip("Wi-Fi 1 Gbps")
                    DetailChip("Private Pool")
                    DetailChip("Butler")
                }

                Spacer(modifier = Modifier.height(30.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(
                                text = "Mulai dari",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = destination.price,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                            )
                        }
                        Text(
                            text = "Pesan",
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.primary)
                                .padding(horizontal = 22.dp, vertical = 12.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun DetailChip(text: String) {
    val palette = LocalAppPalette.current

    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(palette.surfaceMuted)
            .padding(horizontal = 13.dp, vertical = 7.dp),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = palette.textSecondary,
    )
}

@Composable
private fun DestinationArt(destination: Destination, modifier: Modifier) {
    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(destination.start, destination.end),
            ),
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.10f),
            radius = size.minDimension * 0.34f,
            center = Offset(size.width * 0.78f, size.height * 0.28f),
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.06f),
            radius = size.minDimension * 0.18f,
            center = Offset(size.width * 0.18f, size.height * 0.20f),
        )

        val back = Path().apply {
            moveTo(0f, size.height)
            lineTo(size.width * 0.28f, size.height * 0.58f)
            lineTo(size.width * 0.52f, size.height * 0.78f)
            lineTo(size.width * 0.76f, size.height * 0.48f)
            lineTo(size.width, size.height * 0.70f)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(back, Color.White.copy(alpha = 0.12f))

        val front = Path().apply {
            moveTo(0f, size.height)
            lineTo(size.width * 0.34f, size.height * 0.74f)
            lineTo(size.width * 0.62f, size.height * 0.92f)
            lineTo(size.width, size.height * 0.72f)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(front, Color.Black.copy(alpha = 0.18f))
    }
}
