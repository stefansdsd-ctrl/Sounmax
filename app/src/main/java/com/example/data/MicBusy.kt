package com.example.data

import android.content.Context
import android.media.AudioManager
import android.media.AudioRecordingConfiguration
import android.os.Build
import android.os.Handler
import android.os.Looper

/**
 * Andere app neemt de microfoon: muziek boven 50% → 28%.
 * Zodat opname/video de kamer niet overschreeuwt. VoIP blijft bij CommDuck.
 */
object MicBusy {
    private const val PREFS = "sounmax_mic_busy"
    private const val CAP_PCT = 28
    private const val TRIGGER_PCT = 50
    private var callback: AudioManager.AudioRecordingCallback? = null

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (next) apply(context)
        return label(context)
    }

    private fun getPkgName(cfg: AudioRecordingConfiguration): String? {
        return try {
            cfg.javaClass.getMethod("getClientPackageName").invoke(cfg) as? String
        } catch (_: Exception) {
            null
        }
    }

    fun ensure(context: Context) {
        if (callback != null || Build.VERSION.SDK_INT < 24) return
        val app = context.applicationContext
        val am = app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val cb = object : AudioManager.AudioRecordingCallback() {
            override fun onRecordingConfigChanged(configs: MutableList<AudioRecordingConfiguration>) {
                val ours = app.packageName
                val foreign = configs.any { cfg ->
                    val pkg = getPkgName(cfg)
                    pkg != null && pkg != ours
                }
                prefs(app).edit()
                    .putBoolean("busy", foreign)
                    .putLong("last", System.currentTimeMillis())
                    .apply()
                if (foreign) apply(app)
            }
        }
        callback = cb
        try {
            am.registerAudioRecordingCallback(cb, Handler(Looper.getMainLooper()))
        } catch (_: Exception) {
            callback = null
        }
    }

    fun release(context: Context) {
        val cb = callback ?: return
        val am = context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            am.unregisterAudioRecordingCallback(cb)
        } catch (_: Exception) {
        }
        callback = null
    }

    fun busy(context: Context): Boolean {
        if (prefs(context).getBoolean("busy", false)) return true
        if (Build.VERSION.SDK_INT < 24) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val ours = context.packageName
        return try {
            am.activeRecordingConfigurations.any { cfg ->
                val pkg = getPkgName(cfg)
                pkg != null && pkg != ours
            }
        } catch (_: Exception) {
            false
        }
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !busy(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.mode == AudioManager.MODE_IN_CALL || am.mode == AudioManager.MODE_IN_COMMUNICATION) return false
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (cur <= (max * TRIGGER_PCT) / 100) return false
        val cap = (max * CAP_PCT) / 100
        if (cur > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            prefs(context).edit().putLong("capped", System.currentTimeMillis()).apply()
            return true
        }
        return false
    }

    fun active(context: Context) = enabled(context) && busy(context)

    fun label(context: Context) = when {
        !enabled(context) -> "Mic-cap uit"
        busy(context) -> "Mic-cap → 28%"
        else -> "Mic-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
