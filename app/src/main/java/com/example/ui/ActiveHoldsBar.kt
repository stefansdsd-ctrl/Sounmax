package com.example.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HoldPanic
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

/** Chips voor elke actieve hold; tik = stop die hold. */
@Composable
fun ActiveHoldsBar() {
    val context = LocalContext.current
    var tick by remember { mutableIntStateOf(0) }
    val list = remember(tick) { HoldPanic.activeList(context) }
    if (list.isEmpty()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("active_holds_bar"),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        list.forEach { item ->
            FilterChip(
                selected = true,
                onClick = {
                    HoldPanic.stopOne(context, item.id)
                    tick++
                },
                label = { Text("${item.name} ✕", fontSize = 11.sp, maxLines = 1) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                    containerColor = ImmersiveSurfaceActive,
                    selectedLabelColor = ImmersiveLavenderAccent,
                    labelColor = ImmersiveTextSecondary
                ),
                modifier = Modifier.testTag("active_hold_${item.id}")
            )
        }
    }
}
