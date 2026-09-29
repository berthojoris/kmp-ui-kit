package com.example.uiapp.ui.commandpalette

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

private data class CommandAction(
    val id: String,
    val title: String,
    val category: String,
    val shortcut: String,
    val description: String,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CommandPaletteLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }

    val palette = LocalAppPalette.current
    var isPaletteOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var lastExecutedAction by remember { mutableStateOf<String?>("Buka Tema Gelap") }

    val allCommands = remember {
        listOf(
            CommandAction("theme_dark", "Ubah Tema ke Gelap", "Tampilan", "⌘ D", "Terapkan palet dark mode global"),
            CommandAction("theme_light", "Ubah Tema ke Terang", "Tampilan", "⌘ L", "Terapkan palet light mode"),
            CommandAction("nav_commerce", "Buka Commerce & Cart", "Navigasi", "⌘ C", "Menuju ke lab belanja & checkout"),
            CommandAction("nav_media", "Buka Pemutar Video & PiP", "Navigasi", "⌘ M", "Menuju ke lab media & player"),
            CommandAction("nav_cards", "Buka Kalender & Kanban", "Navigasi", "⌘ K", "Menuju ke lab kartu & data"),
            CommandAction("sec_lock", "Kunci Aplikasi Sekarang", "Keamanan", "⌘ Q", "Aktifkan penguncian passcode/biometrik"),
            CommandAction("cache_clear", "Bersihkan Cache Memori", "Sistem", "⌘ ⌫", "Kosongkan 14.2 MB cache sementara"),
            CommandAction("export_code", "Ekspor Komponen Multiplatform", "Developer", "⌘ E", "Generate boilerplate Compose"),
        )
    }

    val filteredCommands = remember(searchQuery) {
        if (searchQuery.isBlank()) allCommands
        else allCommands.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = palette.background,
            topBar = {
                UiTopBar(
                    title = "Command Palette",
                    subtitle = "Spotlight Quick Search (Cmd+K)",
                    onBack = onBack,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Info card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Mobile Spotlight & Command Launcher", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = palette.textPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Pola pencarian perintah global instan yang populer di aplikasi modern seperti Linear, Raycast, dan Notion.", fontSize = 12.sp, color = palette.textMuted)
                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            onClick = { isPaletteOpen = true },
                            shape = RoundedCornerShape(12.dp),
                            color = palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.border),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🔍", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Cari perintah, navigasi, atau aksi...", fontSize = 13.sp, color = palette.textMuted)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(palette.surface)
                                        .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
                                ) {
                                    Text("⌘ K", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = palette.textPrimary)
                                }
                            }
                        }

                        if (lastExecutedAction != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = palette.primaryContainer,
                                border = BorderStroke(1.dp, palette.primary),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Box(modifier = Modifier.padding(12.dp)) {
                                    Text("Aksi Terakhir Dieksekusi: '$lastExecutedAction' (Berhasil)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = palette.onPrimaryContainer)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Command Palette Modal Sheet
        AnimatedVisibility(
            visible = isPaletteOpen,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable { isPaletteOpen = false },
                contentAlignment = Alignment.TopCenter,
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = palette.surface,
                    border = BorderStroke(1.dp, palette.border),
                    modifier = Modifier
                        .padding(top = 70.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                        .clickable(enabled = false) {},
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Search Input Field
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = palette.surfaceMuted,
                            border = BorderStroke(1.dp, palette.primary),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("⌘", fontSize = 14.sp, color = palette.primary, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                TextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Ketik perintah apa saja...", fontSize = 13.sp, color = palette.textMuted) },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                    ),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                )
                                if (searchQuery.isNotEmpty()) {
                                    Text("✕", fontSize = 12.sp, color = palette.textMuted, modifier = Modifier.clickable { searchQuery = "" })
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Results list
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(filteredCommands, key = { it.id }) { cmd ->
                                Surface(
                                    onClick = {
                                        lastExecutedAction = cmd.title
                                        isPaletteOpen = false
                                        searchQuery = ""
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = palette.surfaceMuted,
                                    border = BorderStroke(1.dp, palette.border),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(cmd.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = palette.textPrimary)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(palette.surface)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                                ) {
                                                    Text(cmd.category, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = palette.textMuted)
                                                }
                                            }
                                            Text(cmd.description, fontSize = 11.sp, color = palette.textSecondary)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(palette.surface)
                                                .border(BorderStroke(1.dp, palette.border), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp),
                                        ) {
                                            Text(cmd.shortcut, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = palette.primary)
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
