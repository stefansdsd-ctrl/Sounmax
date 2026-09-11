package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WeeklyListenReport
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun WeeklyListenBar() {
    val context = LocalContext.current
    val days = remember { WeeklyListenReport.last7Days(context) }
    val today = days.lastOrNull()?.minutes ?: 0
    val week = days.sumOf { it.minutes }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("weekly_listen_bar"),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("7 dagen: ${week} min", color = ImmersiveTextSecondary, fontSize = 13.sp)
        Text(WeeklyListenReport.hint(today), color = ImmersiveTextSecondary, fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            days.forEach { d ->
                LinearProgressIndicator(
                    progress = { (d.minutes / 180f).coerceIn(0f, 1f) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Text(days.joinToString("  ") { "${it.label}${it.minutes}" }, color = ImmersiveTextSecondary, fontSize = 11.sp)
    }
}
