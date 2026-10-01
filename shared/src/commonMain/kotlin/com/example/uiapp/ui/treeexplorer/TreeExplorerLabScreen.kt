package com.example.uiapp.ui.treeexplorer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private data class TreeNode(
    val id: String,
    val name: String,
    val isFolder: Boolean,
    val size: String = "",
    val children: List<TreeNode> = emptyList(),
    val depth: Int = 0,
)

private val FileTree = TreeNode("root", "Proyek Aplikasi", true, children = listOf(
    TreeNode("src", "src", true, children = listOf(
        TreeNode("main", "main", true, children = listOf(
            TreeNode("kotlin", "kotlin", true, children = listOf(
                TreeNode("app", "App.kt", false, "4.2 KB"),
                TreeNode("main_act", "MainActivity.kt", false, "2.1 KB"),
                TreeNode("utils", "Utils.kt", false, "1.5 KB"),
            )),
            TreeNode("res", "resources", true, children = listOf(
                TreeNode("logo", "gambar_logo.png", false, "45 KB"),
                TreeNode("icon", "ikon_app.svg", false, "8 KB"),
            )),
        )),
        TreeNode("test", "test", true, children = listOf(
            TreeNode("apptest", "AppTest.kt", false, "3.3 KB"),
        )),
    )),
    TreeNode("docs", "docs", true, children = listOf(
        TreeNode("readme", "README.md", false, "2.8 KB"),
        TreeNode("panduan", "PANDUAN.md", false, "5.1 KB"),
        TreeNode("api", "API_DOCS.md", false, "12.4 KB"),
    )),
    TreeNode("build", "build.gradle.kts", false, "1.2 KB"),
    TreeNode("settings", "settings.gradle.kts", false, "0.8 KB"),
))

private fun flattenTree(node: TreeNode, depth: Int, expanded: Set<String>): List<TreeNode> {
    val result = mutableListOf<TreeNode>()
    result.add(node.copy(depth = depth))
    if (node.isFolder && node.id in expanded) {
        node.children.forEach { child -> result.addAll(flattenTree(child, depth + 1, expanded)) }
    }
    return result
}

private fun countItems(node: TreeNode): Pair<Int, Int> {
    if (!node.isFolder) return Pair(1, 0)
    var files = 0; var folders = 1
    node.children.forEach { val (f, d) = countItems(it); files += f; folders += d }
    return Pair(files, folders)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TreeExplorerLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    var expanded by remember { mutableStateOf(setOf("root", "src", "main", "kotlin")) }
    var selectedId by remember { mutableStateOf<String?>(null) }

    val flatList = remember(expanded) { flattenTree(FileTree, 0, expanded) }
    val counts = remember { countItems(FileTree) }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "File Explorer", subtitle = "Pohon direktori hierarkis", onBack = onBack) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Breadcrumb
            Surface(
                color = palette.surfaceMuted,
                shadowElevation = 0.dp, tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "📂 Proyek Aplikasi",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 12.sp, color = palette.textSecondary,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp),
            ) {
                items(flatList, key = { it.id }) { node ->
                    val isSelected = node.id == selectedId
                    val chevronRotation by animateFloatAsState(if (node.id in expanded) 90f else 0f)

                    Surface(
                        color = if (isSelected) palette.primaryContainer.copy(alpha = 0.2f) else palette.surface,
                        shadowElevation = 0.dp, tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (node.isFolder) {
                                        expanded = if (node.id in expanded) expanded - node.id else expanded + node.id
                                    } else {
                                        selectedId = if (isSelected) null else node.id
                                    }
                                }
                                .padding(start = (16 + node.depth * 16).dp, end = 16.dp, top = 6.dp, bottom = 6.dp)
                                .semantics { contentDescription = "${if (node.isFolder) "Folder" else "Berkas"} ${node.name}" },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (node.isFolder) {
                                // Chevron
                                Canvas(
                                    modifier = Modifier.size(16.dp).graphicsLayer { rotationZ = chevronRotation },
                                ) {
                                    val path = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(size.width * 0.3f, size.height * 0.2f)
                                        lineTo(size.width * 0.7f, size.height * 0.5f)
                                        lineTo(size.width * 0.3f, size.height * 0.8f)
                                    }
                                    drawPath(path, palette.textMuted, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
                                }
                                Spacer(Modifier.width(4.dp))
                                // Folder icon
                                Canvas(modifier = Modifier.size(18.dp)) {
                                    drawRoundRect(palette.warning, Offset(0f, size.height * 0.15f), Size(size.width * 0.5f, size.height * 0.2f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()))
                                    drawRoundRect(palette.warning, Offset(0f, size.height * 0.25f), Size(size.width, size.height * 0.65f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()))
                                }
                            } else {
                                Spacer(Modifier.width(20.dp))
                                // File icon
                                Canvas(modifier = Modifier.size(18.dp)) {
                                    drawRoundRect(palette.textMuted, Offset(size.width * 0.1f, 0f), Size(size.width * 0.8f, size.height), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()), style = Stroke(1.5.dp.toPx()))
                                    drawLine(palette.textMuted, Offset(size.width * 0.55f, 0f), Offset(size.width * 0.9f, size.height * 0.25f), strokeWidth = 1.dp.toPx())
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(node.name, fontSize = 13.sp, fontWeight = if (node.isFolder) FontWeight.SemiBold else FontWeight.Normal, color = palette.textPrimary, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            if (!node.isFolder) {
                                Text(node.size, fontSize = 11.sp, color = palette.textMuted)
                            }
                        }
                    }

                    // Actions when selected
                    if (isSelected && !node.isFolder) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = (32 + node.depth * 16).dp, end = 16.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            listOf("Info", "Pindahkan", "Hapus").forEach { action ->
                                Surface(
                                    onClick = { selectedId = null },
                                    shape = RoundedCornerShape(8.dp),
                                    color = palette.surfaceMuted,
                                    border = BorderStroke(1.dp, palette.border),
                                    shadowElevation = 0.dp, tonalElevation = 0.dp,
                                ) {
                                    Text(action, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (action == "Hapus") palette.danger else palette.textPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // Stats
            Surface(color = palette.surface, border = BorderStroke(1.dp, palette.border), shadowElevation = 0.dp, tonalElevation = 0.dp) {
                Text(
                    text = "${counts.first} berkas, ${counts.second} folder",
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 10.dp),
                    fontSize = 12.sp, color = palette.textMuted,
                )
            }
        }
    }
}
