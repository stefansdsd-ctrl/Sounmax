package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.BatteryEta
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveTextSecondary
import kotlinx.coroutines.delay

@Composable
fun BatteryHistoryBar() {
    val context = LocalContext.current
    var samples by remember { mutableStateOf(BatteryEta.samples(context)) }
    var eta by remember {
        mutableStateOf(BatteryEta.label(BatteryEta.etaMinutes(context, samples.lastOrNull()?.second)))
    }

    LaunchedEffect(Unit) {
        while (true) {
            samples = BatteryEta.samples(context)
            eta = BatteryEta.label(BatteryEta.etaMinutes(context, samples.lastOrNull()?.second))
            delay(30_000)
        }
    }

    if (samples.size < 2) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("battery_history_bar")
    ) {
        Text(
            text = buildString {
                append("Accu ")
                samples.lastOrNull()?.second?.let { append("$it%  ") }
                eta?.let { append(it) }
            },
            fontSize = 11.sp,
            color = ImmersiveTextSecondary
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .padding(top = 4.dp)
        ) {
            val minT = samples.first().first.toFloat()
            val maxT = samples.last().first.toFloat().coerceAtLeast(minT + 1f)
            val path = Path()
            samples.forEachIndexed { i, (t, p) ->
                val x = ((t - minT) / (maxT - minT)) * size.width
                val y = size.height - (p.coerceIn(0, 100) / 100f) * size.height
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
                path = path,
                color = ImmersiveLavenderAccent,
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )
            samples.lastOrNull()?.let { (t, p) ->
                val x = ((t - minT) / (maxT - minT)) * size.width
                val y = size.height - (p.coerceIn(0, 100) / 100f) * size.height
                drawCircle(ImmersiveLavenderAccent, radius = 4f, center = Offset(x, y))
            }
        }
    }
}
