package com.example.ui

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.media.WeeklyDose
import com.example.ui.theme.ImmersiveTextSecondary
import com.example.ui.theme.SoundActiveGreen
import kotlin.math.roundToInt

@Composable
fun WeeklyDoseBar() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE) }
    var weekMin by remember { mutableStateOf(prefs.getInt(WeeklyDose.KEY_MINUTES, 0)) }
    var dayMin by remember { mutableStateOf(prefs.getInt(WeeklyDose.KEY_DAY_MINUTES, 0)) }
    var db by remember { mutableStateOf(prefs.getInt(WeeklyDose.KEY_DB, 80)) }
    val ratio = WeeklyDose.exposureRatio(weekMin, db).toFloat().coerceIn(0f, 1.4f)
    val pct = (ratio * 100).roundToInt()
    val barColor = when {
        ratio >= 1f -> Color(0xFFFF6B6B)
        ratio >= 0.75f -> Color(0xFFFFC857)
        else -> SoundActiveGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("weekly_dose_bar"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("WHO-luisterdosis", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                WeeklyDose.label(weekMin, dayMin, db),
                color = ImmersiveTextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.testTag("weekly_dose_label")
            )
            LinearProgressIndicator(
                progress = { ratio.coerceAtMost(1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = barColor,
                trackColor = Color(0xFF334155)
            )
            Text("Week $weekMin min · vandaag $dayMin min · ~$db dB · $pct%", color = Color(0xFF94A3B8), fontSize = 12.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        capVolume(context)
                        prefs.edit().putBoolean("safe_volume", true).apply()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = barColor),
                    modifier = Modifier.testTag("dose_cap_volume")
                ) { Text("Veilig volume") }
                Button(
                    onClick = {
                        prefs.edit()
                            .putInt(WeeklyDose.KEY_MINUTES, 0)
                            .putInt(WeeklyDose.KEY_DAY_MINUTES, 0)
                            .putString(WeeklyDose.KEY_LABEL, WeeklyDose.label(0))
                            .apply()
                        weekMin = 0
                        dayMin = 0
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    modifier = Modifier.testTag("dose_reset")
                ) { Text("Reset week") }
            }
        }
    }
}

private fun capVolume(context: Context) {
    try {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val cap = (max * 0.6f).toInt().coerceAtLeast(1)
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
        }
    } catch (_: Exception) {
    }
}
