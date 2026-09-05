package com.example.media

import android.content.Context
import com.example.dsp.LdacQualityMode
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/** Zwak BT-signaal → stabielere LDAC 330-scene. */
object RssiCodecAdvisor {
    const val KEY_ENABLED = "rssi_codec_advisor"
    const val KEY_RSSI = "last_rssi_dbm"

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun remember(context: Context, rssiDbm: Int) {
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .edit().putInt(KEY_RSSI, rssiDbm).apply()
    }

    fun adjust(context: Context, scene: ListeningScene): ListeningScene {
        if (!enabled(context)) return scene
        val rssi = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_RSSI, 0)
        if (rssi == 0 || rssi > -75) return scene
        if (scene.preferredLdac == LdacQualityMode.CONNECTION_330) return scene
        return SceneLookup.byId("saver") ?: scene.copy(preferredLdac = LdacQualityMode.CONNECTION_330)
    }
}
