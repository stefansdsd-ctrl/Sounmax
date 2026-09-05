package com.example.media

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

object WifiPlaceAdvisor {
    const val KEY_ENABLED = "wifi_place"
    const val KEY_HOME = "wifi_ssid_home"
    const val KEY_WORK = "wifi_ssid_work"

    fun enabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun homeSsid(context: Context): String? = prefs(context).getString(KEY_HOME, null)?.ifBlank { null }
    fun workSsid(context: Context): String? = prefs(context).getString(KEY_WORK, null)?.ifBlank { null }

    fun pinCurrentAsHome(context: Context): String? {
        val ssid = currentSsid(context) ?: return null
        prefs(context).edit().putString(KEY_HOME, ssid).apply()
        return ssid
    }

    fun pinCurrentAsWork(context: Context): String? {
        val ssid = currentSsid(context) ?: return null
        prefs(context).edit().putString(KEY_WORK, ssid).apply()
        return ssid
    }

    fun currentSsid(context: Context): String? {
        return runCatching {
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val raw = wm.connectionInfo?.ssid
            raw?.trim('"')?.takeIf { it.isNotBlank() && it != "<unknown ssid>" }
        }.getOrNull()
    }

    fun adjust(context: Context, scene: ListeningScene): ListeningScene {
        if (!enabled(context)) return scene
        val ssid = currentSsid(context) ?: return scene
        val home = homeSsid(context)
        val work = workSsid(context)
        return when (ssid) {
            work -> SceneLookup.byId("office") ?: SceneLookup.byId("wfh") ?: scene
            home -> {
                val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                val id = if (hour in 9..17) "wfh" else "thuisavond"
                SceneLookup.byId(id) ?: scene
            }
            else -> scene
        }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
}
