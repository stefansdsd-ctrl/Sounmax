package com.example.media

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.dsp.AncMode
import com.example.dsp.SceneLookup
import com.example.dsp.SoftwareAnc

/** Vliegtuig: max ANC + bass-cut + volume-cap + 3u timer. */
object FlightOneTap {
    const val ACTION_END = "com.example.ACTION_FLIGHT_END"
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ON = "flight_chip_on"
    const val KEY_END = "flight_end_at"
    const val DURATION_MS = 3 * 60 * 60_000L

    fun isOn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val end = prefs.getLong(KEY_END, 0L)
        return prefs.getBoolean(KEY_ON, false) && end > System.currentTimeMillis()
    }

    fun remainingMin(context: Context): Int {
        val end = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong(KEY_END, 0L)
        return ((end - System.currentTimeMillis()).coerceAtLeast(0L) / 60_000L).toInt()
    }

    fun toggle(context: Context) {
        if (isOn(context)) cancel(context) else start(context)
    }

    fun start(context: Context) {
        val end = System.currentTimeMillis() + DURATION_MS
        val scene = SceneLookup.byId("plane")
            ?: SceneLookup.byId("intercity")
            ?: SceneLookup.byId("commute")
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, true)
            .putLong(KEY_END, end)
            .putString("last_scene_id", scene?.id ?: "intercity")
            .putBoolean("pending_widget_scene", true)
            .apply()
        SoftwareAnc.applyWithHardware(context, AncMode.STRONG)
        OneTapProfiles.apply(context, "plane")
        schedule(context, end)
        DspControlService.start(context)
        Toast.makeText(context, "Vliegtuig · max ANC · 3u", Toast.LENGTH_SHORT).show()
    }

    fun cancel(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ON, false)
            .putLong(KEY_END, 0L)
            .apply()
        val am = context.getSystemService(AlarmManager::class.java) ?: return
        am.cancel(pending(context))
        Toast.makeText(context, "Vliegtuig uit", Toast.LENGTH_SHORT).show()
    }

    private fun schedule(context: Context, endMs: Long) {
        val am = context.getSystemService(AlarmManager::class.java) ?: return
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endMs, pending(context))
    }

    private fun pending(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            43,
            Intent(context, FlightEndReceiver::class.java).setAction(ACTION_END),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
}

class FlightEndReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != FlightOneTap.ACTION_END) return
        FlightOneTap.cancel(context)
    }
}
