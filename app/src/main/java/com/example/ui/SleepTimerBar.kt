package com.example.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.SleepFade
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary
import com.example.widget.SoundMaxWidget
import kotlinx.coroutines.delay

@Composable
fun SleepTimerBar() {
    val context = LocalContext.current
    var left by remember { mutableIntStateOf(0) }
    var afterTrack by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            val prefs = context.getSharedPreferences("soundmax_wellness", android.content.Context.MODE_PRIVATE)
            left = SoundMaxWidget.remainingSleepMinutes(prefs.getLong(SoundMaxWidget.KEY_SLEEP_END, 0L))
            afterTrack = prefs.getBoolean(SleepFade.KEY_AFTER_TRACK, false)
            delay(15_000)
        }
    }

    fun setMinutes(min: Int) {
        val prefs = context.getSharedPreferences("soundmax_wellness", android.content.Context.MODE_PRIVATE)
        val end = if (min <= 0) 0L else System.currentTimeMillis() + min * 60_000L
        prefs.edit()
            .putLong(SoundMaxWidget.KEY_SLEEP_END, end)
            .putInt(SoundMaxWidget.KEY_SLEEP_MINUTES, min)
            .apply()
        if (min <= 0) SleepFade.cancel(context) else SleepFade.schedule(context, end)
        left = min
        SoundMaxWidget.refreshAll(context)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("sleep_timer_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Timer", fontSize = 11.sp, color = ImmersiveTextSecondary)
        FilterChip(
            selected = afterTrack,
            onClick = {
                afterTrack = !afterTrack
                context.getSharedPreferences("soundmax_wellness", android.content.Context.MODE_PRIVATE)
                    .edit().putBoolean(SleepFade.KEY_AFTER_TRACK, afterTrack).apply()
            },
            label = { Text("Na nummer", fontSize = 11.sp, maxLines = 1) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveTextSecondary,
                selectedLabelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("sleep_after_track")
        )
        listOf(0, 10, 15, 30, 45, 60, 90).forEach { min ->
            val selected = if (min == 0) left == 0 else left in (min - 7)..(min + 7)
            FilterChip(
                selected = selected,
                onClick = { setMinutes(min) },
                label = {
                    Text(
                        if (min == 0) {
                            if (left > 0) "Uit ($left)" else "Uit"
                        } else "${min}m",
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveTextSecondary,
                    selectedLabelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("sleep_chip_$min")
            )
        }
    }
}
