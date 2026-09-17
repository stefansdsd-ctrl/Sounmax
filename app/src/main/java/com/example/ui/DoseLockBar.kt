package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.media.DoseLock
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveTextSecondary
import kotlinx.coroutines.delay

@Composable
fun DoseLockBar() {
    val context = LocalContext.current
    var on by remember { mutableStateOf(DoseLock.enabled(context)) }
    var label by remember { mutableStateOf(DoseLock.label(context)) }
    LaunchedEffect(on) {
        while (true) {
            label = DoseLock.label(context)
            if (on) DoseLock.enforce(context)
            delay(20_000)
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp)
            .testTag("dose_lock_bar")
            .clickable {
                DoseLock.setEnabled(context, !on)
                on = DoseLock.enabled(context)
                label = DoseLock.label(context)
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = ImmersiveLavenderAccent, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(if (on) "aan" else "uit", color = ImmersiveTextSecondary, fontSize = 12.sp)
    }
}
