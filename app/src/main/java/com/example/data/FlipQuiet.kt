package com.example.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager

/**
 * Telefoon face-down (scherm naar tafel) → muziek naar 18% tot omdraaien.
 * Negeert de broekzak (PocketGuard) en lopende deur/straat/talk-ducks.
 */
object FlipQuiet : SensorEventListener {
    private const val PREFS = "sounmax_flip_quiet"
    private const val TARGET_PCT = 18
    private const val FACE_DOWN_Z = -7.5f

    @Volatile private var registered = false
    @Volatile private var faceDown = false
    private var appCtx: Context? = null

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun isFaceDown() = faceDown

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
        sm.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL)
        registered = true
    }

    fun release(context: Context) {
        if (!registered) return
        val sm = context.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sm.unregisterListener(this)
        registered = false
    }

    fun active(context: Context) = enabled(context) && prefs(context).getBoolean("active", false) && faceDown

    fun apply(context: Context): Boolean {
        if (!enabled(context)) return false
        if (faceDown && !prefs(context).getBoolean("active", false)) {
            start(context)
            return true
        }
        if (!faceDown && prefs(context).getBoolean("active", false)) {
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
        prefs(context).edit().putBoolean("active", true).putInt("prev", prev).apply()
        if (cur > target) am.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
    }

    fun stop(context: Context) {
        val prev = prefs(context).getInt("prev", -1)
        prefs(context).edit().putBoolean("active", false).remove("prev").apply()
        if (prev >= 0) {
            val am = audio(context)
            val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, prev.coerceIn(0, max), 0)
        }
    }

    fun label(context: Context): String = when {
        !enabled(context) -> "Flip uit"
        active(context) -> "Flip (tafel · 18%)"
        else -> "Flip aan"
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val e = event ?: return
        if (e.sensor.type != Sensor.TYPE_ACCELEROMETER) return
        val ctx = appCtx ?: return
        if (!enabled(ctx)) return
        val z = e.values.getOrNull(2) ?: return
        val down = z < FACE_DOWN_Z
        if (down == faceDown) return
        faceDown = down
        if (TalkSoft.active(ctx) || DoorListen.active(ctx) || StreetListen.active(ctx)) return
        if (PocketGuard.isPocketed()) return
        if (down) start(ctx) else stop(ctx)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun audio(context: Context) =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
