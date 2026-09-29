package com.example.uiapp.ui.activityinbox

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class InboxCategory(val label: String) {
    ALL("Semua"),
    MENTIONS("Sebutan (@)"),
    TRANSACTIONS("Transaksi"),
    SYSTEM("Sistem"),
}

data class ActivityNotification(
    val id: String,
    val category: InboxCategory,
    val groupDate: String, // "Hari Ini", "Kemarin", "Minggu Ini"
    val title: String,
    val message: String,
    val timestamp: String,
    var isRead: Boolean = false,
    val avatarEmoji: String,
)

@Composable
fun ActivityInboxLabScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current
    val scope = rememberCoroutineScope()

    var selectedCategory by remember { mutableStateOf(InboxCategory.ALL) }
    var unreadOnly by remember { mutableStateOf(false) }
    var isLoadingSkeleton by remember { mutableStateOf(false) }
    var undoNotificationNotice by remember { mutableStateOf<Pair<ActivityNotification, Int>?>(null) }

    val notifications = remember {
        mutableStateListOf(
            ActivityNotification(
                id = "n1",
                category = InboxCategory.MENTIONS,
                groupDate = "Hari Ini",
                title = "Budi menaruh mention pada modul Auth",
                message = "@andi Mohon cek parameter validasi OTP untuk nomor telepon luar negeri.",
                timestamp = "10 menit lalu",
                isRead = false,
                avatarEmoji = "💬",
            ),
            ActivityNotification(
                id = "n2",
                category = InboxCategory.TRANSACTIONS,
                groupDate = "Hari Ini",
                title = "Top-Up Saldo Dompet Sukses",
                message = "Penambahan saldo Rp 500.000 via Virtual Account BCA telah berhasil.",
                timestamp = "1 jam lalu",
                isRead = false,
                avatarEmoji = "💳",
            ),
            ActivityNotification(
                id = "n3",
                category = InboxCategory.SYSTEM,
                groupDate = "Hari Ini",
                title = "Pembaruan Versi SDK Tersedia",
                message = "Compose Multiplatform 1.12.0 telah siap untuk diunduh.",
                timestamp = "3 jam lalu",
                isRead = true,
                avatarEmoji = "⚡",
            ),
            ActivityNotification(
                id = "n4",
                category = InboxCategory.MENTIONS,
                groupDate = "Kemarin",
                title = "Siti mengundang Anda ke Proyek Starterpack",
                message = "Anda ditambahkan sebagai Lead Reviewer di repository.",
                timestamp = "Kemarin 16:40",
                isRead = true,
                avatarEmoji = "👤",
            ),
            ActivityNotification(
                id = "n5",
                category = InboxCategory.TRANSACTIONS,
                groupDate = "Kemarin",
                title = "Pembayaran Layanan Cloud Selesai",
                message = "Faktur #INV-9921 telah dibayar secara otomatis via kartu kredit.",
                timestamp = "Kemarin 09:12",
                isRead = true,
                avatarEmoji = "🧾",
            ),
            ActivityNotification(
                id = "n6",
                category = InboxCategory.SYSTEM,
                groupDate = "Minggu Ini",
                title = "Peringatan Keamanan Akun",
                message = "Kunci otentikasi biometrik berhasil didaftarkan pada perangkat baru.",
                timestamp = "3 hari lalu",
                isRead = true,
                avatarEmoji = "🛡️",
            ),
        )
    }

    val filteredList = notifications.filter {
        (selectedCategory == InboxCategory.ALL || it.category == selectedCategory) &&
                (!unreadOnly || !it.isRead)
    }

    // Grouping by Date
    val groupedNotifications = filteredList.groupBy { it.groupDate }

    fun markAllAsRead() {
        notifications.forEachIndexed { index, notif ->
            notifications[index] = notif.copy(isRead = true)
        }
    }

    fun dismissItem(item: ActivityNotification) {
        val index = notifications.indexOf(item)
        if (index != -1) {
            notifications.removeAt(index)
            undoNotificationNotice = item to index
            scope.launch {
                delay(4000)
                if (undoNotificationNotice?.first?.id == item.id) {
                    undoNotificationNotice = null
                }
            }
        }
    }

    fun undoDismiss() {
        undoNotificationNotice?.let { (item, index) ->
            val safeIndex = index.coerceIn(0, notifications.size)
            notifications.add(safeIndex, item)
            undoNotificationNotice = null
        }
    }

    fun simulateIncomingNotification() {
        val newId = "sim_${notifications.size + 1}"
        val simulated = ActivityNotification(
            id = newId,
            category = InboxCategory.SYSTEM,
            groupDate = "Hari Ini",
            title = "Pemberitahuan Sistem Otomatis",
            message = "Sinkronisasi data latar belakang selesai pada ${notifications.size}:00.",
            timestamp = "Baru saja",
            isRead = false,
            avatarEmoji = "🔔",
        )
        notifications.add(0, simulated)
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Activity Inbox",
                subtitle = "Pusat notifikasi & riwayat aktivitas",
                onBack = onBack,
                action = {
                    Surface(
                        onClick = { simulateIncomingNotification() },
                        shape = RoundedCornerShape(8.dp),
                        color = palette.primary,
                        border = BorderStroke(1.dp, palette.primary),
                    ) {
                        Text(
                            text = "+ Simulasi",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.onPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
        ) {
            // Category Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                InboxCategory.entries.forEach { cat ->
                    val isSel = selectedCategory == cat
                    Surface(
                        onClick = { selectedCategory = cat },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSel) palette.primary else palette.surface,
                        border = BorderStroke(1.dp, if (isSel) palette.primary else palette.border),
                        shadowElevation = 0.dp,
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = cat.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) palette.onPrimary else palette.textSecondary,
                            )
                        }
                    }
                }
            }

            // Quick Filters Bar: Unread Only & Mark All Read
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { unreadOnly = !unreadOnly },
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (unreadOnly) palette.primary else palette.surface)
                            .border(1.dp, if (unreadOnly) palette.primary else palette.border, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (unreadOnly) {
                            Text("✓", fontSize = 10.sp, color = palette.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Hanya yang belum dibaca",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isLoadingSkeleton) "Sembunyikan Skeleton" else "Coba Skeleton",
                        fontSize = 11.sp,
                        color = palette.textMuted,
                        modifier = Modifier.clickable { isLoadingSkeleton = !isLoadingSkeleton },
                    )
                    Text(
                        text = "Tandai Semua Dibaca",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primary,
                        modifier = Modifier.clickable { markAllAsRead() },
                    )
                }
            }

            // Undo Banner
            AnimatedVisibility(
                visible = undoNotificationNotice != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.surfaceMuted)
                        .border(1.dp, palette.border, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Notifikasi dihapus",
                            fontSize = 12.sp,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = "Urungkan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primary,
                            modifier = Modifier.clickable { undoDismiss() },
                        )
                    }
                }
            }

            // Notification List or Skeleton
            if (isLoadingSkeleton) {
                // Skeleton loading state
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    items(4) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = palette.surface,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(palette.surfaceMuted),
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.5f)
                                            .height(12.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(palette.surfaceMuted),
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.85f)
                                            .height(10.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(palette.surfaceMuted),
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (groupedNotifications.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔕", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tidak Ada Notifikasi",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textPrimary,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Semua aktivitas terbaru sudah dibaca atau tidak cocok dengan filter.",
                            fontSize = 12.sp,
                            color = palette.textMuted,
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    groupedNotifications.forEach { (dateGroup, itemsInGroup) ->
                        item {
                            Text(
                                text = dateGroup.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textMuted,
                                modifier = Modifier.padding(vertical = 4.dp),
                            )
                        }

                        items(itemsInGroup, key = { it.id }) { notif ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (notif.isRead) palette.surface else palette.surfaceMuted,
                                border = BorderStroke(1.dp, if (notif.isRead) palette.border else palette.primary.copy(alpha = 0.4f)),
                                shadowElevation = 0.dp,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.Top,
                                ) {
                                    // Avatar Emoji
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(palette.surface)
                                            .border(1.dp, palette.border, CircleShape),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = notif.avatarEmoji, fontSize = 16.sp)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f),
                                            ) {
                                                if (!notif.isRead) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(7.dp)
                                                            .clip(CircleShape)
                                                            .background(palette.primary),
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                }
                                                Text(
                                                    text = notif.title,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (notif.isRead) FontWeight.SemiBold else FontWeight.Bold,
                                                    color = palette.textPrimary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }

                                            Text(
                                                text = notif.timestamp,
                                                fontSize = 10.sp,
                                                color = palette.textMuted,
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(3.dp))

                                        Text(
                                            text = notif.message,
                                            fontSize = 12.sp,
                                            color = palette.textSecondary,
                                            lineHeight = 17.sp,
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            if (!notif.isRead) {
                                                Text(
                                                    text = "Tandai Dibaca",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = palette.primary,
                                                    modifier = Modifier.clickable {
                                                        val idx = notifications.indexOf(notif)
                                                        if (idx != -1) {
                                                            notifications[idx] = notif.copy(isRead = true)
                                                        }
                                                    },
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                            }
                                            Text(
                                                text = "Hapus",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = palette.danger,
                                                modifier = Modifier.clickable { dismissItem(notif) },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
