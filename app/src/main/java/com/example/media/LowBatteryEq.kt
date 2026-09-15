package com.example.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.BatteryManager
import android.widget.Toast
import com.example.dsp.SceneLookup

/** Telefoon- of headset-accu <20%: zachtere bass, volume-cap, één toast per bron. */
object LowBatteryEq {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ARMED = "low_batt_eq_armed"
    const val KEY_TOASTED = "low_batt_eq_toast"
    const val KEY_TOASTED_HS = "low_batt_eq_toast_hs"
    const val THRESHOLD = 20
    private const val WELLNESS = "soundmax_wellness"

    @Volatile private var registered = false

    fun start(context: Context) {
        if (registered) {
            applyIfNeeded(context)
            return
        }
        registered = true
        val app = context.applicationContext
        app.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent?) {
                applyIfNeeded(ctx)
            }
        }, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        applyIfNeeded(app)
    }

    fun applyIfNeeded(context: Context) {
        val phone = phoneLevel(context)
        val headset = headsetLevel(context)
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val phoneLow = phone != null && phone < THRESHOLD
        val hsLow = headset != null && headset < THRESHOLD
        if (!phoneLow && !hsLow) {
            prefs.edit()
                .putBoolean(KEY_ARMED, false)
                .putBoolean(KEY_TOASTED, false)
                .putBoolean(KEY_TOASTED_HS, false)
                .apply()
            return
        }
        capVolume(context, 55)
        prefs.edit().putBoolean(KEY_ARMED, true).apply()
        SceneLookup.byId("saver")?.let { saver ->
            prefs.edit()
                .putString("last_scene_id", saver.id)
                .putBoolean("pending_widget_scene", true)
                .apply()
        }
        if (phoneLow && !prefs.getBoolean(KEY_TOASTED, false)) {
            prefs.edit().putBoolean(KEY_TOASTED, true).apply()
            Toast.makeText(context, "Telefoon $phone% · zachtere bass + volume-cap", Toast.LENGTH_SHORT).show()
        }
        if (hsLow && !prefs.getBoolean(KEY_TOASTED_HS, false)) {
            prefs.edit().putBoolean(KEY_TOASTED_HS, true).apply()
            Toast.makeText(context, "Headset $headset% · spaar-EQ + volume-cap", Toast.LENGTH_SHORT).show()
        }
        DspControlService.start(context)
    }

    private fun phoneLevel(context: Context): Int? {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager ?: return null
        val pct = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return if (pct in 0..100) pct else null
    }

    fun headsetLevel(context: Context): Int? {
        val pct = context.getSharedPreferences(WELLNESS, Context.MODE_PRIVATE)
            .getInt("headset_battery", -1)
        return pct.takeIf { it in 0..100 }
    }

    private fun capVolume(context: Context, pct: Int) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val target = (max * pct.coerceIn(10, 100) / 100f).toInt().coerceAtLeast(1)
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) > target) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
        }
    }
}
