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
import com.example.data.ActiveLimitBanner
import com.example.data.MorningBriefing
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveTextSecondary

/** Compacte status: dosis vandaag, accu, actieve limiet. */
@Composable
fun TodayStatusBar(batteryPercent: Int?) {
    val context = LocalContext.current
    val brief = remember(batteryPercent) { MorningBriefing.text(context, batteryPercent) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag("today_status_bar")
            .clickable { },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(brief, color = ImmersiveLavenderAccent, fontSize = 12.sp, maxLines = 1)
        Text(ActiveLimitBanner.text(context).take(18), color = ImmersiveTextSecondary, fontSize = 11.sp, maxLines = 1)
    }
}
