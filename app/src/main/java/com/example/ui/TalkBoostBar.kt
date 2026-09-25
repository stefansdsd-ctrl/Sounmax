package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.dsp.TalkBoost
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary
import kotlinx.coroutines.delay

@Composable
fun TalkBoostBar(viewModel: MainViewModel) {
    val context = LocalContext.current
    var active by remember { mutableStateOf(TalkBoost.isActive(context)) }
    var left by remember { mutableIntStateOf(TalkBoost.remainingSec(context)) }

    LaunchedEffect(active) {
        while (active) {
            delay(500)
            active = TalkBoost.isActive(context)
            left = TalkBoost.remainingSec(context)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("talk_boost_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = active,
            onClick = {
                val on = TalkBoost.toggle(context, viewModel.dspManager)
                active = on
                left = TalkBoost.remainingSec(context)
                Toast.makeText(
                    context,
                    if (on) "Gesprek: 20s zachter + spraak" else "Gesprek hersteld",
                    Toast.LENGTH_SHORT
                ).show()
            },
            label = {
                Text(
                    if (active) "Gesprek ${left}s" else "Gesprek 20s",
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
            modifier = Modifier.testTag("talk_boost_chip")
        )
    }
}
