package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.media.HeadsetLocator
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun FindHeadsetBar(viewModel: MainViewModel) {
    val context = LocalContext.current
    var playing by remember { mutableStateOf(false) }
    val place by HeadsetLocator.place.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { HeadsetLocator.load(context) }

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
                    if (playing) {
                        viewModel.stopFindHeadset()
                        playing = false
                    } else {
                        viewModel.findHeadset()
                        playing = true
                    }
                },
                modifier = Modifier.weight(1f).testTag("find_headset_ping")
            ) {
                Icon(
                    imageVector = if (playing) Icons.Default.Stop else Icons.Default.Campaign,
                    contentDescription = null
                )
                Text(
                    text = if (playing) "Stop zoeken" else "Zoek koptelefoon",
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
            FilledTonalButton(
                onClick = { HeadsetLocator.openMaps(context) },
                enabled = place != null,
                modifier = Modifier.testTag("find_headset_map")
            ) {
                Icon(Icons.Default.Map, contentDescription = null)
                Text("Kaart", fontSize = 13.sp, modifier = Modifier.padding(start = 6.dp))
            }
        }
        place?.let { spot ->
            Text(
                text = "Laatst: ${spot.name} · ${spot.ageLabel()} · ${spot.proximityLabel()}",
                color = ImmersiveTextSecondary,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 4.dp).testTag("find_headset_place")
            )
        }
    }
}
