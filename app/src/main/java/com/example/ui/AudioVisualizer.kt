package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dsp.AncMode
import com.example.dsp.EqPreset
import com.example.ui.theme.ImmersiveActiveGreen
import com.example.ui.theme.ImmersiveBorder
import com.example.ui.theme.ImmersiveBorderSubtle
import com.example.ui.theme.ImmersiveDeepViolet
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurface
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveSurfaceVariant
import com.example.ui.theme.ImmersiveTextMuted
import com.example.ui.theme.ImmersiveTextPrimary
import com.example.ui.theme.ImmersiveTextSecondary
import com.example.ui.theme.ImmersiveYtRed

@Composable
fun AudioVisualizerBar(
    amplitudes: List<Float>,
    isDspEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("audio_visualizer_canvas")
    ) {
        val barCount = amplitudes.size
        val barSpacing = 4.dp.toPx()
        val totalSpacing = (barCount - 1) * barSpacing
        val barWidth = ((size.width - totalSpacing) / barCount).coerceAtLeast(3f)

        val gradient = Brush.verticalGradient(
            colors = if (isDspEnabled) listOf(
                ImmersiveLavenderAccent,
                ImmersiveDeepViolet
            ) else listOf(
                ImmersiveTextMuted,
                Color(0xFF23252A)
            )
        )

        for (i in 0 until barCount) {
            val amp = amplitudes.getOrElse(i) { 0.1f }.coerceIn(0.05f, 1.0f)
            val barHeight = size.height * amp
            val x = i * (barWidth + barSpacing)
            val y = size.height - barHeight

            drawRoundRect(
                brush = gradient,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // Peak cap dot
            if (isDspEnabled) {
                drawCircle(
                    color = ImmersiveLavenderAccent,
                    radius = 2.dp.toPx(),
                    center = Offset(x + barWidth / 2, (y - 3.dp.toPx()).coerceAtLeast(2.dp.toPx()))
                )
            }
        }
    }
}

@Composable
fun MasterDspStatusBar(
    isDspEnabled: Boolean,
    currentPreset: EqPreset,
    activeAnc: AncMode,
    codecName: String,
    onToggleDsp: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("master_dsp_status_bar"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(
                    if (isDspEnabled) ImmersiveLavenderAccent.copy(alpha = 0.4f) else ImmersiveBorderSubtle,
                    if (isDspEnabled) ImmersiveDeepViolet.copy(alpha = 0.5f) else ImmersiveBorderSubtle
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDspEnabled) ImmersiveDeepViolet.copy(alpha = 0.4f) else ImmersiveSurfaceActive,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            listOf(
                                if (isDspEnabled) ImmersiveLavenderAccent.copy(alpha = 0.4f) else ImmersiveBorderSubtle,
                                Color.Transparent
                            )
                        )
                    ),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "DSP Status",
                            tint = if (isDspEnabled) ImmersiveLavenderAccent else ImmersiveTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isDspEnabled) "DSP STUDIO ACTIEF" else "DSP BYPASS (UIT)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = if (isDspEnabled) ImmersiveLavenderAccent else ImmersiveYtRed
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ImmersiveSurfaceActive,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(
                                    listOf(ImmersiveLavenderAccent.copy(alpha = 0.3f), ImmersiveLavenderAccent.copy(alpha = 0.1f))
                                )
                            )
                        ) {
                            Text(
                                text = "YT MUSIC",
                                color = ImmersiveLavenderAccent,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = currentPreset.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveTextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "${activeAnc.displayName} • $codecName",
                        style = MaterialTheme.typography.bodySmall,
                        color = ImmersiveTextSecondary
                    )
                }
            }

            IconButton(
                onClick = { onToggleDsp(!isDspEnabled) },
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = if (isDspEnabled) ImmersiveLavenderAccent else ImmersiveSurfaceActive,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .testTag("toggle_master_dsp_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "Master DSP Schakelaar",
                    tint = if (isDspEnabled) Color(0xFF1A1C1E) else ImmersiveTextPrimary
                )
            }
        }
    }
}

