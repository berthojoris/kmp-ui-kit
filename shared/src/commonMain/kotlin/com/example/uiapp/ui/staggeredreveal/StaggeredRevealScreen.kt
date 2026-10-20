package com.example.uiapp.ui.staggeredreveal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay

private data class RevealItem(
    val id: String,
    val title: String,
    val body: String,
    val time: String,
    val tag: String,
)

private val RevealItems = listOf(
    RevealItem("sr-01", "Selamat, streak 7 hari!", "Kamu sudah belajar bahasa Spanyol seminggu penuh tanpa putus.", "2 menit lalu", "Kursus"),
    RevealItem("sr-02", "Pelajaran baru tersedia", "Unit 5: Membahas kata kerja tidak beraturan sudah dibuka.", "12 menit lalu", "Kursus"),
    RevealItem("sr-03", "Pengingat latihan", "Sesi mendengarkan 10 menit belum selesai hari ini.", "25 menit lalu", "Pengingat"),
    RevealItem("sr-04", "Kuis mendatang", "Kuis Unit 4 dibuka besok pukul 08.00 WIB. Siapkan dirimu.", "1 jam lalu", "Kursus"),
    RevealItem("sr-05", "Lencana baru", "Kamu mendapat lencana \"Konsisten\" karena 7 hari berlatih berturut-turut.", "3 jam lalu", "Pencapaian"),
    RevealItem("sr-06", "Tantangan liga mingguan", "Sisa 2 kemenangan lagi untuk naik ke divisi Emas.", "5 jam lalu", "Liga"),
    RevealItem("sr-07", "Kartu hafalan diulang", "12 kata yang hampir kamu lupakan siap diulang hari ini.", "Kemarin", "Pengingat"),
    RevealItem("sr-08", "Leaderboard diperbarui", "Kamu naik ke peringkat 4 dari 30 peserta liga minggu ini.", "Kemarin", "Liga"),
    RevealItem("sr-09", "Undang teman", "Ajak teman bergabung dan dapatkan 1 minggu premium gratis.", "2 hari lalu", "Promo"),
    RevealItem("sr-10", "Rangkuman mingguan", "Total 142 menit belajar minggu ini, naik 18% dari minggu lalu.", "2 hari lalu", "Laporan"),
    RevealItem("sr-11", "Target harian tercapai", "Sasaran 20 poin XP hari ini sudah terpenuhi. Kerja bagus!", "2 hari lalu", "Pencapaian"),
    RevealItem("sr-12", "Kelas percakapan live", "Kelas dengan tutor native dibuka Sabtu pukul 19.00 WIB.", "3 hari lalu", "Kursus"),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StaggeredRevealScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var animationEnabled by remember { mutableStateOf(true) }
    var replayToken by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .navigationBarsPadding(),
    ) {
        UiTopBar(
            title = "Staggered Reveal",
            subtitle = "Animasi kemunculan berjenjang ala Duolingo",
            onBack = onBack,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimationToggle(
                enabled = animationEnabled,
                onChange = { animationEnabled = it },
                modifier = Modifier.weight(1f),
            )
            ReplayButton(
                onReplay = { replayToken++ },
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            itemsIndexed(RevealItems, key = { _, item -> item.id }) { index, item ->
                StaggeredRevealRow(
                    item = item,
                    index = index,
                    replayToken = replayToken,
                    animationEnabled = animationEnabled,
                )
            }
        }
    }
}

@Composable
private fun StaggeredRevealRow(
    item: RevealItem,
    index: Int,
    replayToken: Int,
    animationEnabled: Boolean,
) {
    val palette = LocalAppPalette.current
    val reveal = remember { Animatable(if (animationEnabled) 0f else 1f) }

    // Reveal pertama saja per token; item yang discroll kemudian tetap ikut cascade.
    LaunchedEffect(replayToken, animationEnabled) {
        if (animationEnabled) {
            reveal.snapTo(0f)
            delay(index * 40L)
            reveal.animateTo(1f, tween(durationMillis = 250))
        } else {
            reveal.snapTo(1f)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = reveal.value
                translationY = (1f - reveal.value) * 24.dp.toPx()
            }
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = item.tag,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(palette.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                )
                Text(
                    text = item.time,
                    fontSize = 11.sp,
                    color = palette.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.body,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = palette.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun AnimationToggle(
    enabled: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .pointerInput(Unit) { detectTapGestures { onChange(!enabled) } }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = "Animasi",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
            )
            Text(
                text = if (enabled) "Aktif — reveal berjenjang" else "Nonaktif — tampil langsung",
                fontSize = 11.sp,
                color = palette.textMuted,
            )
        }

        // Toggle pill (status juga ditulis sebagai teks di kiri, bukan warna saja)
        Box(
            modifier = Modifier
                .width(46.dp)
                .height(26.dp)
                .clip(CircleShape)
                .background(if (enabled) palette.primary else palette.surfaceMuted)
                .border(1.dp, if (enabled) palette.primary else palette.border, CircleShape)
                .semantics {
                    role = Role.Switch
                    contentDescription = if (enabled) "Animasi aktif" else "Animasi nonaktif"
                },
            contentAlignment = if (enabled) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (enabled) palette.onPrimary else palette.textMuted),
            )
        }
    }
}

@Composable
private fun ReplayButton(
    onReplay: () -> Unit,
) {
    val palette = LocalAppPalette.current

    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(palette.primary)
            .border(1.dp, palette.primary, RoundedCornerShape(12.dp))
            .pointerInput(Unit) { detectTapGestures { onReplay() } }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Putar Ulang",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = palette.onPrimary,
            maxLines = 1,
        )
    }
}
