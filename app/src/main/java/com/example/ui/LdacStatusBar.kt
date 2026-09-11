package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.media.BtCodecProbe
import com.example.media.LdacWarn
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun LdacStatusBar(sceneController: SceneController) {
    val status by sceneController.headsetStatus.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val codec = BtCodecProbe.codec(context, null)
    val label = BtCodecProbe.label(context, null) ?: codec?.codecName ?: "codec ?"
    val rssiHint = BtCodecProbe.qualityFromRssi(status.rssiDbm)
    val warn = LdacWarn.shouldWarn(codec, status.batteryPercent, status.rssiDbm)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .testTag("ldac_status_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = listOfNotNull(label, rssiHint, status.batteryPercent?.let { "$it%" }).joinToString(" · "),
            color = if (warn) ImmersiveLavenderAccent else ImmersiveTextSecondary,
            fontSize = 11.sp
        )
        if (warn) {
            Text(LdacWarn.MESSAGE, color = ImmersiveLavenderAccent, fontSize = 11.sp)
        }
    }
}
