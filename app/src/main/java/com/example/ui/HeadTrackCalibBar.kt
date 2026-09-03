package com.example.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun HeadTrackCalibBar(viewModel: MainViewModel) {
    val context = LocalContext.current
    val tracker = viewModel.dspManager.headTracker ?: return
    val available by tracker.available.collectAsStateWithLifecycle()
    val enabled by tracker.enabled.collectAsStateWithLifecycle()
    val yaw by tracker.yawDeg.collectAsStateWithLifecycle()
    val pitch by tracker.pitchDeg.collectAsStateWithLifecycle()
    val calibrated by tracker.calibrated.collectAsStateWithLifecycle()
    val prefs = remember { context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE) }
    var trackingPref by remember { mutableStateOf(prefs.getBoolean("head_tracking", false)) }

    if (!available && !trackingPref) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(
                onClick = {
                    tracker.calibrateNeutral()
                    if (!enabled) tracker.start()
                },
                enabled = available,
                modifier = Modifier.weight(1f).testTag("head_track_calibrate")
            ) {
                Icon(Icons.Default.CenterFocusStrong, contentDescription = null)
                Text(
                    text = if (calibrated) "Herkalibreren" else "Kalibreer kijkrichting",
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
            FilledTonalButton(
                onClick = { tracker.resetCalibration() },
                enabled = calibrated,
                modifier = Modifier.testTag("head_track_reset")
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = null)
                Text("Reset", fontSize = 13.sp, modifier = Modifier.padding(start = 6.dp))
            }
        }
        Text(
            text = buildString {
                append(if (enabled) "Head-track" else "Sensor klaar")
                append(" · yaw ${yaw.toInt()}° pitch ${pitch.toInt()}°")
                if (calibrated) append(" · nulpunt gezet")
                else append(" · kijk recht vooruit en tik kalibreer")
            },
            color = ImmersiveTextSecondary,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 4.dp).testTag("head_track_pose")
        )
    }
}
