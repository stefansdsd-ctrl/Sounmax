package com.example.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WhyNow
import com.example.ui.theme.ImmersiveTextSecondary

@Composable
fun WhyNowBar() {
    val context = LocalContext.current
    val text = remember { WhyNow.text(context) }
    Text(
        text = text,
        color = ImmersiveTextSecondary,
        fontSize = 11.sp,
        maxLines = 2,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .testTag("why_now_bar")
    )
}
