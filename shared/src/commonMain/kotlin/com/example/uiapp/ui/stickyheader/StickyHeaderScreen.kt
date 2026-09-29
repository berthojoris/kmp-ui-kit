package com.example.uiapp.ui.stickyheader

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette
import com.example.uiapp.ui.components.UiTopBar

private enum class RowStatus { PAID, PENDING, REFUND }

private data class ActivityRow(
    val id: Int,
    val title: String,
    val subtitle: String,
    val amount: String,
    val status: RowStatus,
)

private data class Section(
    val title: String,
    val rows: List<ActivityRow>,
)

private fun activityRow(n: Int): ActivityRow = ActivityRow(
    id = n,
    title = "Transaksi #INV-${3000 + n}",
    subtitle = "12 Sep 2026 \u00B7 09:${(10 + n * 3 % 45).toString().padStart(2, '0')}",
    amount = "Rp ${(120 + n * 45) * 1000}",
    status = when (n % 3) {
        0 -> RowStatus.PAID
        1 -> RowStatus.PENDING
        else -> RowStatus.REFUND
    },
)

private val Sections: List<Section> = listOf(
    Section("Hari Ini", (1..4).map(::activityRow)),
    Section("Minggu Ini", (5..9).map(::activityRow)),
    Section("Bulan Ini", (10..16).map(::activityRow)),
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StickyHeaderScreen(onBack: () -> Unit) {
    val palette = LocalAppPalette.current

    BackHandler(enabled = true) { onBack() }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            UiTopBar(
                title = "Sticky Header",
                subtitle = "Kategori menempel saat di-scroll",
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 28.dp),
        ) {
            item(key = "summary") {
                BalanceSummary()
            }

            Sections.forEach { section ->
                stickyHeader(key = "header-${section.title}") {
                    SectionHeader(section)
                }
                items(section.rows, key = { it.id }) { row ->
                    ActivityCard(row)
                }
            }
        }
    }
}

@Composable
private fun BalanceSummary() {
    val palette = LocalAppPalette.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp),
        shape = RoundedCornerShape(22.dp),
        color = palette.primary,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                text = "TOTAL BULAN INI",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = Color.White.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Rp 18.540.000",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                letterSpacing = (-0.5).sp,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                SummaryStat("Masuk", "Rp 12,4 jt")
                SummaryStat("Keluar", "Rp 6,1 jt")
                SummaryStat("Pending", "3 item")
            }
        }
    }
}

@Composable
private fun SummaryStat(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.6f),
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    }
}

@Composable
private fun SectionHeader(section: Section) {
    val palette = LocalAppPalette.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = section.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary,
            )
            Text(
                text = "${section.rows.size} item",
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(palette.surfaceMuted)
                    .padding(horizontal = 10.dp, vertical = 3.dp),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = palette.textMuted,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.border),
        )
    }
}

@Composable
private fun ActivityCard(row: ActivityRow) {
    val palette = LocalAppPalette.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = palette.surface,
        border = BorderStroke(1.dp, palette.border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceMuted),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${row.id}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = row.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = row.subtitle,
                    fontSize = 12.sp,
                    color = palette.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = row.amount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(5.dp))
                StatusPill(row.status)
            }
        }
    }
}

@Composable
private fun StatusPill(status: RowStatus) {
    val palette = LocalAppPalette.current

    val background: Color
    val foreground: Color
    val label: String
    when (status) {
        RowStatus.PAID -> {
            background = Color(0xFFE8F5E9); foreground = palette.success; label = "PAID"
        }
        RowStatus.PENDING -> {
            background = Color(0xFFFFF7ED); foreground = palette.warning; label = "PENDING"
        }
        RowStatus.REFUND -> {
            background = Color(0xFFEFF6FF); foreground = palette.info; label = "REFUND"
        }
    }
    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = foreground,
    )
}
