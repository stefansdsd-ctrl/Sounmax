package com.example.media

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.example.dsp.AncMode
import com.example.dsp.ListeningScenes
import com.example.dsp.SoftwareAnc
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Software-transparantie tijdens beltoon/gesprek via AudioManager-mode.
 * Geen READ_PHONE_STATE nodig.
 */
object CallTransparencyGuard {
    const val PREFS = "soundmax_wellness"
    const val KEY_ENABLED = "call_transparency"
    const val KEY_ACTIVE = "call_transparency_active"

    private val attached = AtomicBoolean(false)
    private var modeListener: AudioManager.OnModeChangedListener? = null

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, value: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, value).apply()
        if (!value) restore(context)
    }

    fun attach(context: Context) {
        val app = context.applicationContext
        applyForMode(app, audioMode(app))
        if (!attached.compareAndSet(false, true)) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val am = app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val listener = AudioManager.OnModeChangedListener { mode -> applyForMode(app, mode) }
        modeListener = listener
        val executor = Executor { r -> Handler(Looper.getMainLooper()).post(r) }
        try {
            am.addOnModeChangedListener(executor, listener)
        } catch (_: Exception) {
            attached.set(false)
        }
    }

    fun detach(context: Context) {
        val listener = modeListener ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val am = context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                am.removeOnModeChangedListener(listener)
            } catch (_: Exception) {
            }
        }
        modeListener = null
        attached.set(false)
    }

    private fun audioMode(context: Context): Int =
        (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).mode

    private fun isCallMode(mode: Int): Boolean =
        mode == AudioManager.MODE_RINGTONE ||
            mode == AudioManager.MODE_IN_CALL ||
            mode == AudioManager.MODE_IN_COMMUNICATION

    private fun applyForMode(context: Context, mode: Int) {
        if (!enabled(context)) return
        if (isCallMode(mode)) engage(context) else restore(context)
    }

    private fun engage(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_ACTIVE, false)) return
        prefs.edit().putBoolean(KEY_ACTIVE, true).apply()
        runCatching { SoftwareAnc.apply(AncMode.AMBIENT) }
        DspControlService.start(context)
    }

    private fun restore(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_ACTIVE, false)) return
        prefs.edit().putBoolean(KEY_ACTIVE, false).apply()
        val scene = ListeningScenes.byId(prefs.getString("last_scene_id", null))
        val mode = scene?.ancMode ?: AncMode.ADAPTIVE
        runCatching { SoftwareAnc.apply(mode) }
        DspControlService.start(context)
    }
}
