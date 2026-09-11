package com.example.wearapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ComplicationTapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("wear_complic", MODE_PRIVATE)
        val now = System.currentTimeMillis()
        val last = prefs.getLong("last_tap", 0L)
        val count = if (now - last < 1100) prefs.getInt("tap_count", 1) + 1 else 1
        prefs.edit().putLong("last_tap", now).putInt("tap_count", count).apply()
        lifecycleScope.launch {
            val cmd = when {
                count >= 3 -> WearPaths.CMD_CYCLE_ANC
                count == 2 -> WearPaths.CMD_FIND_HEADSET
                else -> WearPaths.CMD_NEXT_SCENE
            }
            runCatching { WearClient.send(this@ComplicationTapActivity, cmd) }
            finish()
        }
    }
}
