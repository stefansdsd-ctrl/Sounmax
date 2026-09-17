package com.example.ui

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
import com.example.media.ListenStreak
import com.example.ui.theme.ImmersiveLavenderAccent
import kotlinx.coroutines.delay

@Composable
fun ListenStreakBar() {
    val context = LocalContext.current
    var label by remember { mutableStateOf(ListenStreak.label(context)) }
    LaunchedEffect(Unit) {
        while (true) {
            label = ListenStreak.label(context)
            delay(60_000)
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp)
            .testTag("listen_streak_bar"),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = ImmersiveLavenderAccent, fontSize = 12.sp)
    }
}
