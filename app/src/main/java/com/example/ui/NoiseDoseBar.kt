package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.NoiseDoseTracker

@Composable
fun NoiseDoseBar(modifier: Modifier = Modifier) {
    val dose by NoiseDoseTracker.dose.collectAsState()
    val warning by NoiseDoseTracker.warning.collectAsState()

    if (dose < 0.05f && warning == null) return

    val color = when {
        dose >= 1.0f -> Color(0xFFD32F2F)
        dose >= 0.8f -> Color(0xFFF57C00)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    imageVector = if (dose >= 0.8f) Icons.Filled.Warning else Icons.Filled.Hearing,
                    contentDescription = null,
                    tint = color
                )
                Text(
                    text = warning ?: "Dagelijkse gehoordosis: ${(dose * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = color
                )
            }
            LinearProgressIndicator(
                progress = { dose.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = color,
                trackColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}
