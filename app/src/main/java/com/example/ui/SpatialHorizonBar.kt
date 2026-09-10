package com.example.ui

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dsp.HeadTrackerHolder
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveTextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpatialHorizonBar() {
    val context = LocalContext.current
    val tracker = remember { HeadTrackerHolder.get(context) }
    val enabled by tracker.enabled.collectAsStateWithLifecycle()
    val yaw by tracker.yawDeg.collectAsStateWithLifecycle()
    val pitch by tracker.pitchDeg.collectAsStateWithLifecycle()
    val prefs = remember { context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE) }
    if (!enabled && !prefs.getBoolean("head_tracking", false) && !prefs.getBoolean("spatializer_on", false)) return

    Text(
        text = "Spatial horizon · yaw ${yaw.toInt()}° · pitch ${pitch.toInt()}°",
        color = ImmersiveTextSecondary,
        fontSize = 11.sp,
        modifier = Modifier.padding(horizontal = 16.dp).testTag("spatial_horizon_label")
    )
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .padding(horizontal = 20.dp)
            .testTag("spatial_horizon")
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension / 2f - 2f
        drawLine(Color.White.copy(alpha = 0.25f), Offset(0f, cy), Offset(size.width, cy), 2f)
        val rad = Math.toRadians(yaw.toDouble()).toFloat()
        val tx = cx + sin(rad) * r
        val ty = cy - cos(rad) * r + (pitch / 45f) * 8f
        drawCircle(ImmersiveLavenderAccent, radius = 6f, center = Offset(tx, ty))
    }
}
