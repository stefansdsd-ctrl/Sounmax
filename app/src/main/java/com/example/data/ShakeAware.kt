package com.example.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import kotlin.math.sqrt

/**
 * Schud de telefoon → 12 s muziek naar 25% (omgeving horen).
 */
object ShakeAware : SensorEventListener {
    private const val PREFS = "sounmax_shake_aware"
    private const val DURATION_MS = 12_000L
    private const val TARGET_PCT = 25
    private const val THRESHOLD = 22f
    private const val COOLDOWN_MS = 2_500L

    @Volatile private var registered = false
    @Volatile private var lastShakeAt = 0L
    private var appCtx: Context? = null

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) ensure(context) else {
            stop(context)
            release(context)
        }
        return label(context)
    }

    fun ensure(context: Context) {
        appCtx = context.applicationContext
        if (!enabled(context) || registered) return
        val sm = context.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ?: return
        sm.registerListener(this, acc, SensorManager.SENSOR_DELAY_GAME)
        registered = true
    }

    fun release(context: Context) {
        if (!registered) return
        val sm = context.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sm.unregisterListener(this)
        registered = false
    }

    fun active(context: Context): Boolean {
        if (!prefs(context).getBoolean("active", false)) return false
        if (remainingMs(context) <= 0L) {
            stop(context)
            return false
        }
        return true
    }

    fun apply(context: Context): Boolean {
        if (!prefs(context).getBoolean("active", false)) return false
        if (remainingMs(context) <= 0L) {
            stop(context)
            return true
        }
        return false
    }

    fun start(context: Context) {
        val am = audio(context)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val target = (max * TARGET_PCT) / 100
        val prev = if (prefs(context).getBoolean("active", false)) {
            prefs(context).getInt("prev", cur)
        } else cur
        prefs(context).edit()
            .putBoolean("active", true)
            .putLong("until", System.currentTimeMillis() + DURATION_MS)
            .putInt("prev", prev)
            .apply()
        if (cur > target) am.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
    }

    fun stop(context: Context) {
        val prev = prefs(context).getInt("prev", -1)
        prefs(context).edit().putBoolean("active", false).remove("until").remove("prev").apply()
        if (prev >= 0) {
            val am = audio(context)
            val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, prev.coerceIn(0, max), 0)
        }
    }

    fun label(context: Context): String = when {
        !enabled(context) -> "Schud uit"
        active(context) -> "Schud (${(remainingMs(context) / 1000).toInt()}s · 25%)"
        else -> "Schud aan"
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val e = event ?: return
        if (e.sensor.type != Sensor.TYPE_ACCELEROMETER) return
        val ctx = appCtx ?: return
        if (!enabled(ctx)) return
        val x = e.values.getOrNull(0) ?: return
        val y = e.values.getOrNull(1) ?: return
        val z = e.values.getOrNull(2) ?: return
        val g = sqrt(x * x + y * y + z * z)
        if (g < THRESHOLD) return
        val now = System.currentTimeMillis()
        if (now - lastShakeAt < COOLDOWN_MS) return
        lastShakeAt = now
        if (TalkSoft.active(ctx) || DoorListen.active(ctx) || StreetListen.active(ctx)) return
        if (PocketGuard.isPocketed()) return
        start(ctx)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun remainingMs(context: Context): Long =
        prefs(context).getLong("until", 0L) - System.currentTimeMillis()

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
