package com.example.uiapp.ui.swipeactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private data class MailItem(
    val id: String,
    val sender: String,
    val subject: String,
    val preview: String,
    val time: String,
    var isUnread: Boolean = true,
    var isFlagged: Boolean = false,
)

private val INITIAL_MAILS = listOf(
    MailItem("1", "GitHub Notifications", "Release v2.4.0: Kotlin Multiplatform", "New release available with performance optimizations and...", "10:42 AM", true, false),
    MailItem("2", "App Store Connect", "Build 1.4.2 (42) has finished processing", "Your build is now ready to be tested using TestFlight or submitted...", "09:15 AM", true, true),
    MailItem("3", "Stripe Billing", "Invoice #INV-2026-9021 paid successfully", "Thanks for your payment. Your receipt is attached in PDF format...", "Kemarin", false, false),
    MailItem("4", "Linear App", "Cycle 14 completed with 100% velocity", "Great job team! All 24 issues were resolved in the past sprint cycle...", "Kemarin", false, false),
    MailItem("5", "Figma Design System", "New comment on Mobile Design Tokens", "Alex commented: The 1px flat border specs look crisp and clean...", "24 Sep", false, true),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SwipeActionsLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    val mailList = remember { mutableStateListOf<MailItem>().apply { addAll(INITIAL_MAILS) } }
    var actionBanner by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Cupertino Swipe Actions",
                subtitle = "Multi-Actions & Full-Swipe Threshold",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Header info & reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = actionBanner ?: "Geser item ke kiri untuk aksi, atau swipe penuh untuk hapus",
                    fontSize = 12.sp,
                    color = palette.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                )

                Surface(
                    onClick = {
                        mailList.clear()
                        mailList.addAll(INITIAL_MAILS)
                        actionBanner = "Daftar email di-reset"
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = palette.surfaceMuted,
                    border = BorderStroke(1.dp, palette.border),
                ) {
                    Text(
                        text = "Reset List",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }

            // Mail Items List
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(mailList, key = { it.id }) { item ->
                    SwipeableMailRow(
                        item = item,
                        onDelete = {
                            mailList.remove(item)
                            actionBanner = "Dihapus: ${item.sender}"
                        },
                        onToggleFlag = {
                            val index = mailList.indexOf(item)
                            if (index >= 0) {
                                mailList[index] = item.copy(isFlagged = !item.isFlagged)
                                actionBanner = if (!item.isFlagged) "Ditandai Bintang" else "Bintang Dihapus"
                            }
                        },
                        onToggleRead = {
                            val index = mailList.indexOf(item)
                            if (index >= 0) {
                                mailList[index] = item.copy(isUnread = !item.isUnread)
                                actionBanner = if (!item.isUnread) "Ditandai Belum Dibaca" else "Ditandai Sudah Dibaca"
                            }
                        },
                    )
                }
            }

            // Footer Specs
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Mekanisme Cupertino Swipe:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = palette.textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Swipe Kiri Pendek: Membuka 2 tombol aksi (Tandai & Hapus)", fontSize = 11.sp, color = palette.textSecondary)
                    Text("• Swipe Kiri Penuh (>180px): Otomatis memicu aksi Hapus seketika", fontSize = 11.sp, color = palette.textSecondary)
                    Text("• Swipe Kanan: Menandai Belum/Sudah Dibaca dengan cepat", fontSize = 11.sp, color = palette.textSecondary)
                }
            }
        }
    }
}

@Composable
private fun SwipeableMailRow(
    item: MailItem,
    onDelete: () -> Unit,
    onToggleFlag: () -> Unit,
    onToggleRead: () -> Unit,
) {
    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var isDeleted by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = !isDeleted,
        exit = fadeOut(spring(stiffness = Spring.StiffnessMedium)) + shrinkVertically(spring(stiffness = Spring.StiffnessMedium)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(86.dp)
                .clip(RoundedCornerShape(14.dp)),
        ) {
            // Background Action Buttons (Left Reveal: Mark Read)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(palette.primary),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .fillMaxHeight()
                        .clickable {
                            onToggleRead()
                            scope.launch { offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (item.isUnread) "Sudah" else "Belum",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    )
                }
            }

            // Background Action Buttons (Right Reveal: Flag & Delete)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(palette.danger),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                // Flag Button
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .fillMaxHeight()
                        .background(palette.warning)
                        .clickable {
                            onToggleFlag()
                            scope.launch { offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(if (item.isFlagged) "★ Batal" else "⭐ Flag", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // Delete Button
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .fillMaxHeight()
                        .background(palette.danger)
                        .clickable {
                            isDeleted = true
                            onDelete()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🗑️ Hapus", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // Foreground Content Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = palette.surface,
                border = BorderStroke(1.dp, palette.border),
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .pointerInput(item.id) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    if (offsetX.value < -220f) {
                                        // Full swipe threshold reached -> delete!
                                        offsetX.animateTo(-600f, spring(stiffness = Spring.StiffnessMedium))
                                        isDeleted = true
                                        onDelete()
                                    } else if (offsetX.value < -90f) {
                                        // Half swipe -> reveal right actions
                                        offsetX.animateTo(-144f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    } else if (offsetX.value > 90f) {
                                        // Half swipe right -> reveal read action
                                        offsetX.animateTo(72f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    } else {
                                        // Snap back
                                        offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                    }
                                }
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    offsetX.snapTo(offsetX.value + dragAmount)
                                }
                            },
                        )
                    },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Unread Indicator dot
                    if (item.isUnread) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(palette.primary),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = item.sender,
                                fontWeight = if (item.isUnread) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = palette.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = item.time,
                                fontSize = 11.sp,
                                color = if (item.isUnread) palette.primary else palette.textMuted,
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.subject,
                            fontWeight = if (item.isUnread) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 12.sp,
                            color = palette.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.preview,
                            fontSize = 11.sp,
                            color = palette.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    if (item.isFlagged) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("⭐", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
