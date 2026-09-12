package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.DailyHearingBudget
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveTextSecondary
import kotlinx.coroutines.delay

@Composable
fun DailyBudgetBar() {
    val context = LocalContext.current
    var on by remember { mutableStateOf(DailyHearingBudget.enabled(context)) }
    var label by remember { mutableStateOf(DailyHearingBudget.chipLabel(context)) }
    LaunchedEffect(on) {
        while (on) {
            label = DailyHearingBudget.chipLabel(context)
            DailyHearingBudget.applySoftCap(context)
            delay(30_000)
        }
    }
    if (!on) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp)
            .testTag("daily_budget_bar")
            .clickable {
                DailyHearingBudget.setEnabled(context, false)
                on = false
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = ImmersiveLavenderAccent, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text("uit", color = ImmersiveTextSecondary, fontSize = 12.sp)
    }
}
