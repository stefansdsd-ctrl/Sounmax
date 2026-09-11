package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.OutdoorSafetyAdvisor
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun OutdoorSafetyBar() {
    val context = LocalContext.current
    var hint by remember { mutableStateOf(OutdoorSafetyAdvisor.hint(context)) }
    if (hint == null) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp)
            .testTag("outdoor_safety_bar")
            .clickable {
                OutdoorSafetyAdvisor.setEnabled(context, false)
                hint = null
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(hint!!, color = ImmersiveLavenderAccent, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text("uit", color = ImmersiveTextSecondary, fontSize = 12.sp)
    }
}
