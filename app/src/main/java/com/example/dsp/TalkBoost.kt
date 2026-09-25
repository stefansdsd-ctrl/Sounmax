package com.example.dsp

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper

/** 20s zachter + spraak-EQ voor een kort gesprek, daarna herstel. */
object TalkBoost {
    private const val PREF = "sounmax_talk_boost"
    private const val KEY_SAVED = "saved_index"
    private const val KEY_UNTIL = "until_ms"
    private const val KEY_EQ = "eq_csv"
    const val DEFAULT_MS = 20_000L
    const val DUCK_PERCENT = 40

    private val main = Handler(Looper.getMainLooper())
    private var restoreTask: Runnable? = null

    fun isActive(context: Context): Boolean =
        prefs(context).getLong(KEY_UNTIL, 0L) > System.currentTimeMillis()

    fun remainingSec(context: Context): Int {
        val left = ((prefs(context).getLong(KEY_UNTIL, 0L) - System.currentTimeMillis()) / 1000L).toInt()
        return left.coerceAtLeast(0)
    }

    fun start(context: Context, dsp: AudioDspManager?, durationMs: Long = DEFAULT_MS): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= 0) return false
        cancelPending()
        val eq = dsp?.bandGains?.value?.joinToString(",") ?: ""
        prefs(context).edit()
            .putInt(KEY_SAVED, cur)
            .putLong(KEY_UNTIL, System.currentTimeMillis() + durationMs)
            .putString(KEY_EQ, eq)
            .apply()
        val target = (max * DUCK_PERCENT / 100).coerceAtLeast(0).coerceAtMost(cur)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
        dsp?.let {
            EqShape172.bumpBass(it, -1.5f)
            EqShape172.bumpMid(it, 1.8f)
            EqShape172.bumpAir(it, -0.8f)
        }
        val app = context.applicationContext
        restoreTask = Runnable { restore(app, dsp) }
        main.postDelayed(restoreTask!!, durationMs)
        return true
    }

    fun restore(context: Context, dsp: AudioDspManager?): Boolean {
        cancelPending()
        val p = prefs(context)
        val saved = p.getInt(KEY_SAVED, -1)
        val eq = p.getString(KEY_EQ, null)
        p.edit().remove(KEY_SAVED).remove(KEY_UNTIL).remove(KEY_EQ).apply()
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        if (saved >= 0) am.setStreamVolume(AudioManager.STREAM_MUSIC, saved.coerceIn(0, max), 0)
        if (dsp != null && !eq.isNullOrBlank()) {
            eq.split(',').mapNotNull { it.toFloatOrNull() }.forEachIndexed { i, v ->
                dsp.updateBandGain(i, v)
            }
        }
        return saved >= 0
    }

    fun toggle(context: Context, dsp: AudioDspManager?): Boolean =
        if (isActive(context)) {
            restore(context, dsp)
            false
        } else {
            start(context, dsp)
            true
        }

    private fun cancelPending() {
        restoreTask?.let { main.removeCallbacks(it) }
        restoreTask = null
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
}
