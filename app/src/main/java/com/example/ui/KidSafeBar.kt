package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.KidSafe
import com.example.ui.theme.ImmersiveLavenderAccent
import com.example.ui.theme.ImmersiveSurfaceActive
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun KidSafeBar() {
    val context = LocalContext.current
    var on by remember { mutableStateOf(KidSafe.enabled(context)) }
    var pin by remember { mutableStateOf("") }
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp).testTag("kid_safe_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = on,
            onClick = {
                KidSafe.toggle(context, pin)
                on = KidSafe.enabled(context)
                if (on) KidSafe.enforce(context)
            },
            label = { Text(if (on) "Kindveilig aan" else "Kindveilig", fontSize = 11.sp, maxLines = 1) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ImmersiveLavenderAccent.copy(alpha = 0.35f),
                containerColor = ImmersiveSurfaceActive,
                labelColor = ImmersiveTextSecondary,
                selectedLabelColor = ImmersiveLavenderAccent
            ),
            modifier = Modifier.testTag("kid_safe_chip")
        )
        OutlinedTextField(
            value = pin,
            onValueChange = {
                pin = it.filter { ch -> ch.isDigit() }.take(6)
                KidSafe.setPin(context, pin)
            },
            modifier = Modifier.width(96.dp).testTag("kid_safe_pin"),
            singleLine = true,
            label = { Text("pin", fontSize = 10.sp) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
        )
    }
}
