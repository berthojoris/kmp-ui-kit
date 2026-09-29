package com.example.uiapp.ui.advancedlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.launch

private enum class ListSubTab(val title: String) {
    REORDER("Reorder"),
    AZ_SCRUBBER("A–Z List"),
    CHAT("Chat"),
    TIMELINE("Timeline"),
    COMMENTS("Komentar"),
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AdvancedListLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var activeSubTab by remember { mutableStateOf(ListSubTab.REORDER) }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Konten & List Lanjutan",
                subtitle = "Reorder, A–Z, Chat, Timeline, Comments",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Sub-tab selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface)
                    .border(BorderStroke(1.dp, palette.border))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ListSubTab.entries.forEach { tab ->
                    val isSelected = activeSubTab == tab
                    Surface(
                        onClick = { activeSubTab = tab },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) palette.primary else palette.surfaceMuted,
                        border = BorderStroke(1.dp, if (isSelected) palette.primary else palette.border),
                    ) {
                        Text(
                            text = tab.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else palette.textPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (activeSubTab) {
                    ListSubTab.REORDER -> ReorderListView(palette)
                    ListSubTab.AZ_SCRUBBER -> AzScrubberView(palette)
                    ListSubTab.CHAT -> ChatBubbleView(palette)
                    ListSubTab.TIMELINE -> TimelineActivityView(palette)
                    ListSubTab.COMMENTS -> NestedCommentsView(palette)
                }
            }
        }
    }
}

@Composable
private fun ReorderListView(palette: com.example.uiapp.theme.AppPalette) {
    val items = remember {
        mutableStateListOf(
            "1. Priority Task: Audit Security",
            "2. Optimize Startup Frame Rate",
            "3. Implement Flat UI Tokens",
            "4. Add Biometric Keychain Bridge",
            "5. Refactor Navigation History",
            "6. Prepare Release Candidate",
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "Drag / Move to Reorder",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = palette.textPrimary,
        )
        Text(
            text = "Gunakan tombol panah naik / turun untuk simulasi reordering",
            fontSize = 12.sp,
            color = palette.textMuted,
        )
        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(items, key = { _, item -> item }) { index, item ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = palette.textPrimary)
                            Text(text = "Posisi index: $index", fontSize = 11.sp, color = palette.textMuted)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                onClick = {
                                    if (index > 0) {
                                        val temp = items[index]
                                        items[index] = items[index - 1]
                                        items[index - 1] = temp
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = palette.surfaceMuted,
                                border = BorderStroke(1.dp, palette.border),
                                enabled = index > 0,
                            ) {
                                Text(
                                    text = "▲",
                                    fontSize = 12.sp,
                                    color = if (index > 0) palette.textPrimary else palette.textMuted,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                )
                            }
                            Surface(
                                onClick = {
                                    if (index < items.size - 1) {
                                        val temp = items[index]
                                        items[index] = items[index + 1]
                                        items[index + 1] = temp
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = palette.surfaceMuted,
                                border = BorderStroke(1.dp, palette.border),
                                enabled = index < items.size - 1,
                            ) {
                                Text(
                                    text = "▼",
                                    fontSize = 12.sp,
                                    color = if (index < items.size - 1) palette.textPrimary else palette.textMuted,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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
private fun AzScrubberView(palette: com.example.uiapp.theme.AppPalette) {
    val alphabet = ('A'..'Z').toList()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val contacts = remember {
        alphabet.flatMap { char ->
            listOf(
                "$char - Adrian Kusuma",
                "$char - Bima Santoso",
                "$char - Citra Maharani",
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 40.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(contacts) { _, contact ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(palette.primaryContainer),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = contact.take(1),
                                fontWeight = FontWeight.Bold,
                                color = palette.onPrimaryContainer,
                                fontSize = 14.sp,
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = contact, fontSize = 13.sp, color = palette.textPrimary, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // A-Z Scrubber strip on the right edge
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.surface.copy(alpha = 0.85f))
                .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(12.dp))
                .padding(vertical = 4.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            alphabet.forEach { char ->
                Text(
                    text = char.toString(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary,
                    modifier = Modifier
                        .clickable {
                            val targetIndex = contacts.indexOfFirst { it.startsWith(char) }
                            if (targetIndex >= 0) {
                                scope.launch { listState.animateScrollToItem(targetIndex) }
                            }
                        }
                        .padding(horizontal = 4.dp, vertical = 0.5.dp),
                )
            }
        }
    }
}

private data class ChatMsg(val id: Int, val text: String, val isMe: Boolean, val time: String)

@Composable
private fun ChatBubbleView(palette: com.example.uiapp.theme.AppPalette) {
    val messages = remember {
        mutableStateListOf(
            ChatMsg(1, "Halo! Apakah sistem Zero-Shadow Flat UI sudah teruji?", false, "09:30"),
            ChatMsg(2, "Sudah, semua komponen menggunakan border 1px dan 0dp elevation.", true, "09:31"),
            ChatMsg(3, "Bagus sekali. Kinerja rendering Canvas juga sangat optimal!", false, "09:32"),
        )
    }
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            itemsIndexed(messages) { _, msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isMe) Arrangement.End else Arrangement.Start,
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (msg.isMe) 14.dp else 2.dp,
                            bottomEnd = if (msg.isMe) 2.dp else 14.dp,
                        ),
                        color = if (msg.isMe) palette.primary else palette.surface,
                        border = BorderStroke(1.dp, if (msg.isMe) palette.primary else palette.border),
                        modifier = Modifier.fillMaxWidth(0.78f),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.text,
                                fontSize = 13.sp,
                                color = if (msg.isMe) Color.White else palette.textPrimary,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.time,
                                fontSize = 10.sp,
                                color = if (msg.isMe) Color.White.copy(alpha = 0.7f) else palette.textMuted,
                                modifier = Modifier.align(Alignment.End),
                            )
                        }
                    }
                }
            }

            if (isTyping) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = palette.surface,
                            border = BorderStroke(1.dp, palette.border),
                        ) {
                            TypingIndicatorDots(palette = palette)
                        }
                    }
                }
            }
        }

        // Chat Input Bar
        Surface(
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = palette.surfaceMuted,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.weight(1f),
                ) {
                    Box(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (inputText.isEmpty()) "Tulis balasan pesan..." else inputText,
                            fontSize = 13.sp,
                            color = if (inputText.isEmpty()) palette.textMuted else palette.textPrimary,
                        )
                    }
                }
                Surface(
                    onClick = {
                        messages.add(ChatMsg(messages.size + 1, "Pesan interaktif baru #${messages.size + 1}", true, "09:35"))
                        isTyping = !isTyping
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = palette.primary,
                ) {
                    Text(
                        text = "Kirim",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun TypingIndicatorDots(palette: com.example.uiapp.theme.AppPalette) {
    val infiniteTransition = rememberInfiniteTransition()
    val dotAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
    )
    val dotAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500, delayMillis = 150), RepeatMode.Reverse),
    )
    val dotAlpha3 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500, delayMillis = 300), RepeatMode.Reverse),
    )

    Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        listOf(dotAlpha1, dotAlpha2, dotAlpha3).forEach { alpha ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(palette.primary.copy(alpha = alpha)),
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "mengetik...", fontSize = 11.sp, color = palette.textMuted)
    }
}

private data class TimelineItem(val title: String, val time: String, val description: String, val isSuccess: Boolean)

@Composable
private fun TimelineActivityView(palette: com.example.uiapp.theme.AppPalette) {
    val timelineData = listOf(
        TimelineItem("Versi 1.0.0 Resmi Dirilis", "10:00 AM", "Semua modul KMP siap untuk produksi Android & iOS.", true),
        TimelineItem("Menjalankan Security Audit", "08:45 AM", "Verifikasi Keystore & Keychain tanpa plaintext token.", true),
        TimelineItem("Benchmarking Memori 2GB Device", "Kemarin", "Memastikan GC churn di bawah ambang batas aman.", true),
        TimelineItem("Setup PRD & Technical Design", "2 hari lalu", "Penyusunan arsitektur sistem dengan Flat UI zero-shadow.", false),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        itemsIndexed(timelineData) { index, item ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(28.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(if (item.isSuccess) palette.primary else palette.warning),
                    )
                    if (index < timelineData.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(56.dp)
                                .background(palette.border),
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                            Text(text = item.time, fontSize = 10.sp, color = palette.textMuted)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = item.description, fontSize = 12.sp, color = palette.textSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun NestedCommentsView(palette: com.example.uiapp.theme.AppPalette) {
    var expandedLevel2 by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Thread Komentar & Collapse", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
        Text("Dukungan komentar berantai dengan indentasi hierarkis", fontSize = 12.sp, color = palette.textMuted)

        // Root Comment
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Budi Santoso \u00B7 Arsitek KMP", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Implementasi multiplatform Flat UI ini sangat konsisten di Android maupun iOS!", fontSize = 12.sp, color = palette.textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (expandedLevel2) "Sembunyikan 2 balasan ▲" else "Lihat 2 balasan ▼",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                        modifier = Modifier.clickable { expandedLevel2 = !expandedLevel2 },
                    )
                    Text("Balas", fontSize = 11.sp, color = palette.textMuted)
                }
            }
        }

        // Nested Comment level 1
        AnimatedVisibility(visible = expandedLevel2) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = palette.surfaceMuted,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Siti Aisyah", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = palette.textPrimary)
                        Text("Setuju, terutama performa scroll yang tetap stabil di 60fps tanpa GC churn.", fontSize = 11.sp, color = palette.textSecondary)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = palette.surfaceMuted,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Rian Pratama", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = palette.textPrimary)
                        Text("Ditambah lagi navigasi backstack yang sekarang sangat skalabel.", fontSize = 11.sp, color = palette.textSecondary)
                    }
                }
            }
        }
    }
}
