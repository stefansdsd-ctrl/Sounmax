package com.example.media

import android.content.Context
import java.util.Calendar

/** Tussen 22:00 en 07:00 automatisch veilig volume, tenzij uitgeschakeld. */
object QuietHours {
    const val KEY_ENABLED = "quiet_hours"
    const val KEY_SAFE = "safe_volume"

    fun isQuietNow(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour >= 22 || hour < 7
    }

    fun enforce(context: Context) {
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_ENABLED, true)) return
        if (isQuietNow()) {
            prefs.edit().putBoolean(KEY_SAFE, true).apply()
        }
    }
}
