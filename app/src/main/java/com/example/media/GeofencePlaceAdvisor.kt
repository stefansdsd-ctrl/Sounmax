package com.example.media

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import java.util.Calendar

/**
 * Fallback wanneer wifi-SSID "<unknown ssid>" is (Android 13+).
 * Pinnen van thuis/werk slaat ook lat/lng op; binnen ~180 m geldt dezelfde scene.
 */
object GeofencePlaceAdvisor {
    const val KEY_HOME_LAT = "geo_home_lat"
    const val KEY_HOME_LNG = "geo_home_lng"
    const val KEY_WORK_LAT = "geo_work_lat"
    const val KEY_WORK_LNG = "geo_work_lng"
    private const val RADIUS_M = 180f

    fun pinHome(context: Context): Boolean {
        val loc = lastLocation(context) ?: return false
        prefs(context).edit()
            .putFloat(KEY_HOME_LAT, loc.latitude.toFloat())
            .putFloat(KEY_HOME_LNG, loc.longitude.toFloat())
            .apply()
        return true
    }

    fun pinWork(context: Context): Boolean {
        val loc = lastLocation(context) ?: return false
        prefs(context).edit()
            .putFloat(KEY_WORK_LAT, loc.latitude.toFloat())
            .putFloat(KEY_WORK_LNG, loc.longitude.toFloat())
            .apply()
        return true
    }

    fun hasHome(context: Context) = prefs(context).contains(KEY_HOME_LAT)
    fun hasWork(context: Context) = prefs(context).contains(KEY_WORK_LAT)

    fun adjust(context: Context, scene: ListeningScene): ListeningScene {
        if (!WifiPlaceAdvisor.enabled(context)) return scene
        val here = lastLocation(context) ?: return scene
        val work = saved(context, KEY_WORK_LAT, KEY_WORK_LNG)
        if (work != null && here.distanceTo(work) <= RADIUS_M) {
            return SceneLookup.byId("office") ?: SceneLookup.byId("wfh") ?: scene
        }
        val home = saved(context, KEY_HOME_LAT, KEY_HOME_LNG)
        if (home != null && here.distanceTo(home) <= RADIUS_M) {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val id = if (hour in 9..17) "wfh" else "thuisavond"
            return SceneLookup.byId(id) ?: scene
        }
        return scene
    }

    private fun saved(context: Context, latKey: String, lngKey: String): Location? {
        val p = prefs(context)
        if (!p.contains(latKey) || !p.contains(lngKey)) return null
        return Location("saved").apply {
            latitude = p.getFloat(latKey, 0f).toDouble()
            longitude = p.getFloat(lngKey, 0f).toDouble()
        }
    }

    @SuppressLint("MissingPermission")
    fun lastLocation(context: Context): Location? {
        return runCatching {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gps = runCatching { lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) }.getOrNull()
            val net = runCatching { lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) }.getOrNull()
            when {
                gps != null && net != null -> if (gps.time >= net.time) gps else net
                else -> gps ?: net
            }
        }.getOrNull()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
}
