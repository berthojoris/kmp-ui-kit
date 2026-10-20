package com.example.uiapp.ui.whatsnew

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.foundation.ExperimentalFoundationApi
import com.example.uiapp.PerformHapticFeedback
import com.example.uiapp.HapticKind
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.LabIntroCard
import com.example.uiapp.ui.components.UiTopBar
import com.example.uiapp.ui.components.FlatPrimaryButton

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun WhatsNewLabScreen(onBack: () -> Unit) {
    BackHandler(enabled = true) { onBack() }
    val palette = LocalAppPalette.current
    val versions = listOf("v1.2 Fitur Baru", "v1.1 Perbaikan", "v1.0 Rilis")
    val pagerState = rememberPagerState(pageCount = { versions.size })
    var seen by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = palette.background,
        topBar = { UiTopBar(title = "What's New", subtitle = "Catatan rilis", onBack = onBack) },
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).navigationBarsPadding()) {
            LabIntroCard(title = "Carousel release notes", body = "State: belum dilihat, sudah dilihat, tanpa release notes.", modifier = Modifier.padding(16.dp))
            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, palette.border),
                    shadowElevation = 0.dp,
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                ) {
                    Column(Modifier.padding(20.dp), Arrangement.spacedBy(10.dp)) {
                        Text(versions[page], fontSize = 20.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
                        Text("• Peningkatan performa\n• Perbaikan bug\n• Label simulasi", color = palette.textSecondary)
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(16.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("Halaman ${pagerState.currentPage + 1}/${versions.size}", color = palette.textMuted)
                FlatPrimaryButton(if (seen) "Mengerti" else "Tandai dilihat", {
                    seen = true
                    PerformHapticFeedback(HapticKind.SUCCESS)
                }, Modifier)
            }
        }
    }
}
