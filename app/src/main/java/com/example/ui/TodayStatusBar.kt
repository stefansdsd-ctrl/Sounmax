package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LastSceneRestore
import com.example.data.WeeklyListenReport
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveTextSecondary

/** Compacte status: dosis vandaag, accu, laatste scene. */
@Composable
fun TodayStatusBar(batteryPercent: Int?) {
    val context = LocalContext.current
    val today = remember { WeeklyListenReport.last7Days(context).lastOrNull()?.minutes ?: 0 }
    val last = remember { LastSceneRestore.label(context) }
    val bat = batteryPercent?.let { "accu $it%" } ?: "accu —"
    val dose = if (today > 0) "${today} min" else "0 min"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag("today_status_bar")
            .clickable { },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Vandaag $dose · $bat", color = ImmersiveLavenderAccent, fontSize = 12.sp)
        Text(last ?: WeeklyListenReport.hint(today), color = ImmersiveTextSecondary, fontSize = 11.sp)
    }
}
