package com.example.wearapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.ambient.AmbientLifecycleObserver
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WearMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            var status by remember { mutableStateOf(WearStatus()) }
            var ambient by remember { mutableStateOf(false) }
            DisposableEffect(Unit) {
                val observer = AmbientLifecycleObserver(
                    this@WearMainActivity,
                    object : AmbientLifecycleObserver.AmbientLifecycleCallback {
                        override fun onEnterAmbient(ambientDetails: AmbientLifecycleObserver.AmbientDetails) {
                            ambient = true
                        }
                        override fun onExitAmbient() {
                            ambient = false
                        }
                    }
                )
                lifecycle.addObserver(observer)
                onDispose { lifecycle.removeObserver(observer) }
            }
            LaunchedEffect(ambient) {
                while (true) {
                    runCatching { status = WearClient.readStatus(context) }
                    delay(if (ambient) 8_000 else 1_500)
                }
            }
            Column(
                modifier = Modifier.fillMaxSize().padding(12.dp).alpha(if (ambient) 0.72f else 1f),
                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("${status.sceneEmoji} ${status.sceneName}")
                Text(
                    buildString {
                        append(if (status.dsp) "DSP aan" else "DSP uit")
                        append(" · ANC ${ancLabel(status.anc)}")
                        if (status.battery in 0..100) append(" · 🎧${status.battery}%")
                        if (status.phoneBattery in 0..100) append(" · 📱${status.phoneBattery}%")
                        if (status.sleepMin > 0) append(" · slaap ${status.sleepMin}m")
                        if (status.quiet) append(" · stil")
                    }
                )
                if (!ambient) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_PREV_SCENE) } }) {
                            Text("◀")
                        }
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_CYCLE_SLEEP) } }) {
                            Text(if (status.sleepMin > 0) "${status.sleepMin}m" else "Slaap")
                        }
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_NEXT_SCENE) } }) {
                            Text("▶")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_CALL) } }) {
                            Text("Bel")
                        }
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_PODCAST) } }) {
                            Text("Podcast")
                        }
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_PLANE) } }) {
                            Text("Vlieg")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_CAR) } }) {
                            Text("Auto")
                        }
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_METRO) } }) {
                            Text("Metro")
                        }
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_CONCERT) } }) {
                            Text("Live")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_COLLEGE) } }) {
                            Text("College")
                        }
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_MUSEUM) } }) {
                            Text("Museum")
                        }
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_BEACH) } }) {
                            Text("Strand")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_RUN) } }) {
                            Text("Loop")
                        }
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_CLINIC) } }) {
                            Text("Arts")
                        }
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_TRAFFIC) } }) {
                            Text("File")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_IKEA) } }) {
                            Text("IKEA")
                        }
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_SALON) } }) {
                            Text("Kapper")
                        }
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_NS) } }) {
                            Text("NS")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_BABY) } }) {
                            Text("Baby")
                        }
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_LOWBATT) } }) {
                            Text("Accu")
                        }
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_ONE_TAP_OFF) } }) {
                            Text("Uit")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_CYCLE_ANC) } }) {
                            Text("ANC ${ancLabel(status.anc)}")
                        }
                        Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_FIND_HEADSET) } }) {
                            Text("Zoek")
                        }
                    }
                    Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_TOGGLE_DSP) } }) {
                        Text(if (status.dsp) "Pauzeer DSP" else "Start DSP")
                    }
                    Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_CYCLE_SPATIAL) } }) {
                        Text(
                            when {
                                status.headTrack -> "Head-track aan"
                                status.spatial -> "Spatial aan"
                                else -> "Spatial uit"
                            }
                        )
                    }
                    Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_FOCUS) } }) {
                        Text(if (status.focus) "Focus ${status.focusLeft}m" else "Focus")
                    }
                    Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_UNDO) } }) {
                        Text("Undo scene")
                    }
                    Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_SUGGEST) } }) {
                        Text("Suggestie")
                    }
                }
            }
        }
    }

    companion object {
        fun ancLabel(anc: String): String = when (anc) {
            "OFF" -> "uit"
            "STRONG" -> "max"
            "ADAPTIVE" -> "auto"
            "AMBIENT" -> "omgeving"
            "WIND_GUARD" -> "wind"
            else -> anc.lowercase()
        }
    }
}
