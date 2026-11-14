package com.example.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsp.SpatialAudioController
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun SpatialToggleBar() {
    val context = LocalContext.current
    if (!SpatialAudioController.isSupported(context)) return

    var spatialOn by remember { mutableStateOf(SpatialAudioController.isEnabledPref(context)) }
    var headOn by remember { mutableStateOf(SpatialAudioController.isHeadTrackingPref(context)) }

    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Spatial audio", fontSize = 14.sp, color = ImmersiveTextSecondary)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Immersive spatializer", fontSize = 13.sp)
            Switch(
                checked = spatialOn,
                onCheckedChange = {
                    spatialOn = it
                    SpatialAudioController.setEnabled(context, it)
                    if (!it) {
                        headOn = false
                        SpatialAudioController.setHeadTracking(context, false)
                    }
                }
            )
        }
        if (spatialOn) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Head tracking", fontSize = 13.sp)
                Switch(
                    checked = headOn,
                    onCheckedChange = {
                        headOn = it
                        SpatialAudioController.setHeadTracking(context, it)
                    }
                )
            }
        }
        Text(
            SpatialAudioController.getStatus(context),
            fontSize = 11.sp,
            color = ImmersiveTextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
