package com.example.data

import android.content.Context
import android.media.AudioManager
import android.media.AudioRecordingConfiguration
import android.os.Build
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Andere app neemt op (spraakbericht, vertaler, recorder) terwijl muziek loopt:
 * volume stapsgewijs naar 40%. Voorkomt dat de mic dichtklapt en de piep verloren gaat.
 * Android geeft opname-configs niet op elk toestel vrij; dan blijft de cap stil.
 */
object MicLive {
    private const val PREFS = "sounmax_mic_live"
    private const val CAP_PCT = 40
    private val registered = AtomicBoolean(false)

    @Volatile
    private var hot = false

    fun enabled(context: Context) = prefs(context).getBoolean("on", true)

    fun cycle(context: Context): String {
        val next = !enabled(context)
        prefs(context).edit().putBoolean("on", next).apply()
        if (!next) hot = false else apply(context)
        return label(context)
    }

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < 24) return
        if (!registered.compareAndSet(false, true)) return
        val app = context.applicationContext
        val am = app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.registerAudioRecordingCallback(object : AudioManager.AudioRecordingCallback() {
            override fun onRecordingConfigChanged(configs: MutableList<AudioRecordingConfiguration>) {
                hot = configs.isNotEmpty()
                if (hot) apply(app)
            }
        }, Handler(Looper.getMainLooper()))
    }

    fun recording(context: Context): Boolean {
        if (hot) return true
        if (Build.VERSION.SDK_INT < 24) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.activeRecordingConfigurations.isNotEmpty()
    }

    fun apply(context: Context): Boolean {
        if (!enabled(context) || !recording(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (!am.isMusicActive) return false
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val cap = (max * CAP_PCT) / 100
        prefs(context).edit().putLong("last", System.currentTimeMillis()).apply()
        if (cur <= cap) return false
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (cur - 2).coerceAtLeast(cap), 0)
        return true
    }

    fun active(context: Context): Boolean {
        if (!enabled(context) || !recording(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.isMusicActive
    }

    fun label(context: Context) = when {
        !enabled(context) -> "Mic-cap uit"
        active(context) -> "Mic open, muziek 40%"
        else -> "Mic-cap aan"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
