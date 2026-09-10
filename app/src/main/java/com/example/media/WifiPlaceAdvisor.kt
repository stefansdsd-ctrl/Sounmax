package com.example.media

import android.content.Context
import android.net.wifi.WifiManager
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

object WifiPlaceAdvisor {
    const val KEY_ENABLED = "wifi_place"
    const val KEY_HOME = "wifi_ssid_home"
    const val KEY_WORK = "wifi_ssid_work"
    const val HYSTERESIS_MS = 90_000L

    fun enabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun homeSsid(context: Context): String? = prefs(context).getString(KEY_HOME, null)?.ifBlank { null }
    fun workSsid(context: Context): String? = prefs(context).getString(KEY_WORK, null)?.ifBlank { null }

    fun pinCurrentAsHome(context: Context): String? {
        val ssid = currentSsid(context)
        if (ssid != null) prefs(context).edit().putString(KEY_HOME, ssid).apply()
        GeofencePlaceAdvisor.pinHome(context)
        return ssid
    }

    fun pinCurrentAsWork(context: Context): String? {
        val ssid = currentSsid(context)
        if (ssid != null) prefs(context).edit().putString(KEY_WORK, ssid).apply()
        GeofencePlaceAdvisor.pinWork(context)
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
        val target = targetForSsid(context, ssid, scene) ?: return scene
        val p = prefs(context)
        val lastId = p.getString("wifi_last_scene", null)
        val lastAt = p.getLong("wifi_last_at", 0L)
        val now = System.currentTimeMillis()
        if (lastId != null && lastId != target.id && now - lastAt < HYSTERESIS_MS) {
            return SceneLookup.byId(lastId) ?: scene
        }
        p.edit()
            .putString("wifi_last_scene", target.id)
            .putLong("wifi_last_at", now)
            .apply()
        return target
    }

    private fun targetForSsid(context: Context, ssid: String, scene: ListeningScene): ListeningScene? {
        val home = homeSsid(context)
        val work = workSsid(context)
        return when (ssid) {
            work -> SceneLookup.byId("office") ?: SceneLookup.byId("wfh") ?: scene
            home -> {
                val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                val id = if (hour in 9..17) "wfh" else "thuisavond"
                SceneLookup.byId(id) ?: scene
            }
            else -> null
        }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
}
