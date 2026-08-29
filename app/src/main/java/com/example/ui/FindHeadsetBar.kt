package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FindHeadsetBar(viewModel: MainViewModel) {
    var playing by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
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
            modifier = Modifier.weight(1f)
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
    }
}
