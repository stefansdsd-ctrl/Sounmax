package com.example.media

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.example.dsp.LdacQualityMode
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/** Lage telefoonaccu: stabiele 330 kbps + saver-scene. */
object PhoneBatteryAdvisor {
    const val KEY_ENABLED = "phone_battery_advisor"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun level(context: Context): Int? {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            ?: return null
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100).coerceAtLeast(1)
        if (level < 0) return null
        return (level * 100) / scale
    }

    fun adjust(context: Context, scene: ListeningScene): ListeningScene {
        if (!enabled(context)) return scene
        val pct = level(context) ?: return scene
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putInt("last_phone_battery", pct).apply()
        if (pct > 15) return scene
        val saver = SceneLookup.byId("saver")
        return (saver ?: scene).copy(
            description = "${scene.description} · telefoon $pct%",
            preferredLdac = LdacQualityMode.CONNECTION_330
        )
    }
}
