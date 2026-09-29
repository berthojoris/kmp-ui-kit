package com.example.uiapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uiapp.theme.LocalAppPalette

/**
 * Kartu pengantar untuk menjelaskan maksud sebuah lab. Dipakai bersama oleh
 * lab gamifikasi, data viz, scroll motion, dan media creation.
 */
@Composable
fun LabIntroCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.surfaceMuted, RoundedCornerShape(12.dp))
            .border(1.dp, palette.border, RoundedCornerShape(12.dp))
            .padding(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
            Text(body, fontSize = 11.sp, lineHeight = 16.sp, color = palette.textSecondary)
        }
    }
}

/** Judul bagian di dalam lab, mengikuti gaya header kartu Home. */
@Composable
fun LabSectionTitle(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = palette.textPrimary)
        Text(subtitle, fontSize = 11.sp, color = palette.textMuted)
    }
}

/** Permukaan kartu Flat UI standar lab: border 1px, radius 16dp, zero shadow. */
@Composable
fun LabCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = LocalAppPalette.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.surface, RoundedCornerShape(16.dp))
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}
