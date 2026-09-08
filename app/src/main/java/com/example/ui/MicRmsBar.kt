package com.example.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.dsp.AmbientNoiseFloor
import com.example.dsp.MicRmsProbe
import com.example.ui.theme.ImmersiveTextSecondary
import kotlin.math.roundToInt

private const val PREFS = "scene_automation"

@Composable
fun MicRmsBar() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS, Context.MODE_PRIVATE) }
    var enabled by remember { mutableStateOf(prefs.getBoolean(MicRmsProbe.PREF_ENABLED, false)) }
    var hasPerm by remember { mutableStateOf(MicRmsProbe.hasPermission(context)) }
    var hint by remember { mutableStateOf(statusLine(enabled, hasPerm)) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPerm = granted
        if (!granted) {
            enabled = false
            prefs.edit().putBoolean(MicRmsProbe.PREF_ENABLED, false).apply()
        }
        hint = statusLine(enabled && granted, granted)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Mic-RMS ruisvloer", fontSize = 14.sp)
                Text(
                    hint,
                    color = ImmersiveTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.testTag("mic_rms_status")
                )
            }
            Switch(
                checked = enabled && hasPerm,
                onCheckedChange = { on ->
                    if (on) {
                        val ok = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                        if (ok) {
                            enabled = true
                            hasPerm = true
                            prefs.edit().putBoolean(MicRmsProbe.PREF_ENABLED, true).apply()
                            AmbientNoiseFloor.estimate(context)
                            hint = statusLine(true, true)
                        } else {
                            launcher.launch(Manifest.permission.RECORD_AUDIO)
                            prefs.edit().putBoolean(MicRmsProbe.PREF_ENABLED, true).apply()
                            enabled = true
                        }
                    } else {
                        enabled = false
                        prefs.edit().putBoolean(MicRmsProbe.PREF_ENABLED, false).apply()
                        hint = statusLine(false, hasPerm)
                    }
                },
                modifier = Modifier.testTag("mic_rms_switch")
            )
        }
    }
}

private fun statusLine(enabled: Boolean, hasPerm: Boolean): String {
    if (!enabled) return "Uit · proxy-schatting"
    if (!hasPerm) return "Wacht op microfoon-toestemming"
    val src = AmbientNoiseFloor.lastSource
    val pct = (AmbientNoiseFloor.lastIntensity * 100f).roundToInt()
    val mic = if (MicRmsProbe.lastIntensity >= 0f) {
        "mic ${(MicRmsProbe.lastIntensity * 100f).roundToInt()}%"
    } else {
        "mic nog niet gemeten"
    }
    return "Aan · $src $pct% · $mic · ${AmbientNoiseFloor.lastLabel}"
}
