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
        val doubleTap = now - last < 900
        prefs.edit().putLong("last_tap", now).apply()
        lifecycleScope.launch {
            val cmd = if (doubleTap) WearPaths.CMD_FIND_HEADSET else WearPaths.CMD_NEXT_SCENE
            runCatching { WearClient.send(this@ComplicationTapActivity, cmd) }
            finish()
        }
    }
}
