package com.example.wearapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
            LaunchedEffect(Unit) {
                while (true) {
                    runCatching { status = WearClient.readStatus(context) }
                    delay(2_000)
                }
            }
            Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("${status.sceneEmoji} ${status.sceneName}")
                Text(
                    buildString {
                        append(if (status.dsp) "DSP aan" else "DSP uit")
                        if (status.battery in 0..100) append(" · ${status.battery}%")
                        if (status.sleepMin > 0) append(" · slaap ${status.sleepMin}m")
                    }
                )
                Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_TOGGLE_DSP) } }) {
                    Text(if (status.dsp) "Pauzeer DSP" else "Start DSP")
                }
                Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_NEXT_SCENE) } }) {
                    Text("Volgende scene")
                }
                Button(onClick = { scope.launch { WearClient.send(context, WearPaths.CMD_PREV_SCENE) } }) {
                    Text("Vorige scene")
                }
            }
        }
    }
}
