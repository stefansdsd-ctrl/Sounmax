package com.example.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager

/**
 * Zak-modus: proximity dicht → volume niet omhoog.
 */
object PocketGuard : SensorEventListener {
    private const val PREFS = "sounmax_pocket_guard"

    @Volatile private var near = false
    @Volatile private var registered = false

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) ensure(context) else release(context)
        return label(context)
    }

    fun ensure(context: Context) {
        if (!enabled(context) || registered) return
        val sm = context.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val prox = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY) ?: return
        sm.registerListener(this, prox, SensorManager.SENSOR_DELAY_NORMAL)
        registered = true
    }

    fun release(context: Context) {
        if (!registered) return
        val sm = context.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sm.unregisterListener(this)
        registered = false
        near = false
    }

    fun isPocketed(): Boolean = near

    fun clampUp(context: Context) {
        if (!enabled(context) || !near) return
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val cap = prefs(context).getInt("cap", am.getStreamVolume(AudioManager.STREAM_MUSIC))
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur > cap) am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
    }

    fun rememberVolume(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        prefs(context).edit().putInt("cap", am.getStreamVolume(AudioManager.STREAM_MUSIC)).apply()
    }

    fun label(context: Context): String = when {
        !enabled(context) -> "Zak uit"
        near -> "Zak: dicht"
        else -> "Zak aan"
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val e = event ?: return
        if (e.sensor.type != Sensor.TYPE_PROXIMITY) return
        val max = e.sensor.maximumRange
        near = e.values.firstOrNull()?.let { it < max.coerceAtMost(5f) } == true
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
