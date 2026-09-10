package com.example.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsp.DspHolder
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun BalanceMonoBar() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE) }
    var balance by remember { mutableFloatStateOf(prefs.getInt("stereo_balance", 0).toFloat()) }
    var mono by remember { mutableStateOf(prefs.getBoolean("mono_mix", false)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("balance_mono_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("L", fontSize = 11.sp, color = ImmersiveTextSecondary)
            Text(
                if (mono) "Mono" else "Balans ${balance.toInt()}",
                fontSize = 11.sp,
                color = ImmersiveLavenderAccent
            )
            Text("R", fontSize = 11.sp, color = ImmersiveTextSecondary)
        }
        Slider(
            value = balance,
            onValueChange = {
                balance = it
                val v = it.toInt()
                prefs.edit().putInt("stereo_balance", v).apply()
                DspHolder.instance?.setBalance(v)
            },
            valueRange = -100f..100f,
            colors = SliderDefaults.colors(
                thumbColor = ImmersiveLavenderAccent,
                activeTrackColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("balance_slider")
        )
        FilterChip(
            selected = mono,
            onClick = {
                mono = !mono
                prefs.edit().putBoolean("mono_mix", mono).apply()
                DspHolder.instance?.setMonoMix(mono)
            },
            label = { Text(if (mono) "Mono aan" else "Mono", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveTextSecondary,
                selectedLabelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("mono_chip")
        )
    }
}
