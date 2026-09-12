package com.example.ui

import android.content.Context
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsp.StereoDynamics
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun CrossfeedBar() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE) }
    var pct by remember { mutableIntStateOf(prefs.getInt("crossfeed_pct", 0)) }

    fun apply(p: Int) {
        pct = p
        prefs.edit().putInt("crossfeed_pct", p).apply()
        StereoDynamics.init()
        val width = when (p) {
            60 -> 0.40f
            30 -> 0.70f
            else -> 1.0f
        }
        StereoDynamics.stereoWidth(width)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("crossfeed_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Breedte", fontSize = 11.sp, color = ImmersiveTextSecondary)
        listOf(0 to "0%", 30 to "30%", 60 to "60%").forEach { (p, label) ->
            FilterChip(
                selected = pct == p,
                onClick = { apply(p) },
                label = { Text(label, fontSize = 11.sp, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                    containerColor = ImmersiveSurfaceActive,
                    labelColor = ImmersiveTextSecondary,
                    selectedLabelColor = ImmersiveLavenderAccent
                ),
                modifier = Modifier.testTag("crossfeed_chip_$p")
            )
        }
    }
}
