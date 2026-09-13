package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.SealCheck
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SealCheckBar() {
    val context = LocalContext.current
    var label by remember { mutableStateOf(SealCheck.lastLabel(context) ?: "Pasvorm") }
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .testTag("seal_check_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = {
                scope.launch {
                    val r = withContext(Dispatchers.IO) { SealCheck.run(context) }
                    label = "${r.label} ${r.score}"
                }
            },
            label = { Text(label, fontSize = 11.sp, maxLines = 1) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("seal_check_chip")
        )
    }
}
