package com.example.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.data.HomeToolProfiles
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

/** Extra home-balken achter “Meer”, gefilterd op Sport / Werk / Slaap. */
@Composable
fun HomeExtrasBar(content: @Composable (profileId: String) -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("soundmax_ui", Context.MODE_PRIVATE) }
    var open by remember { mutableStateOf(prefs.getBoolean("home_extras_open", false)) }
    var profileId by remember { mutableStateOf(HomeToolProfiles.activeId(context)) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = open,
                onClick = {
                    open = !open
                    prefs.edit().putBoolean("home_extras_open", open).apply()
                },
                label = { Text(if (open) "Minder" else "Meer", fontSize = 11.sp) },
                colors = chipColors(),
                modifier = Modifier.testTag("home_meer_chip")
            )
            if (open) {
                HomeToolProfiles.ALL.forEach { p ->
                    FilterChip(
                        selected = profileId == p.id,
                        onClick = {
                            HomeToolProfiles.set(context, p.id)
                            profileId = p.id
                        },
                        label = { Text(p.name, fontSize = 11.sp) },
                        colors = chipColors(),
                        modifier = Modifier.testTag("home_profiel_${p.id}")
                    )
                }
            }
        }
        if (open) content(profileId)
    }
}

@Composable
private fun chipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
    containerColor = ImmersiveSurfaceActive,
    labelColor = ImmersiveTextSecondary,
    selectedLabelColor = ImmersiveLavenderAccent
)
