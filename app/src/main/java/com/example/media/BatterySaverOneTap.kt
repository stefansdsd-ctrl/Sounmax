package com.example.media

import android.content.Context
import android.media.AudioManager
import android.widget.Toast
import com.example.ble.RealAncController
import com.example.dsp.AncMode
import com.example.widget.SoundMaxWidget

/** Handmatige accu-spaarstand: ANC uit + volume-cap 50%. */
object BatterySaverOneTap {
    private const val PREFS = "sounmax_anc_saver"
    private const val KEY_MANUAL = "manual_saver"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_MANUAL, false)

    fun toggle(context: Context): Boolean {
        val on = !isOn(context)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_MANUAL, on).apply()
        val wellness = context.getSharedPreferences("soundmax_wellness", Context.MODE_PRIVATE)
        if (on) {
            wellness.edit().putString("last_anc", AncMode.OFF.name).apply()
            RealAncController.apply(context, AncMode.OFF)
            capVolume(context, 50)
            Toast.makeText(context, "Accu-spaar: ANC uit, 50% volume", Toast.LENGTH_SHORT).show()
        } else {
            wellness.edit().putString("last_anc", AncMode.ADAPTIVE.name).apply()
            RealAncController.apply(context, AncMode.ADAPTIVE)
            Toast.makeText(context, "Accu-spaar uit", Toast.LENGTH_SHORT).show()
        }
        SoundMaxWidget.refreshAll(context)
        return on
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
