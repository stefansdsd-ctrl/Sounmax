package com.example.media

import android.content.Context
import android.media.AudioManager
import android.widget.Toast

object KidSafe {
    private const val PREFS = "soundmax_wellness"
    const val KEY_ON = "kid_safe"
    const val KEY_PIN = "kid_safe_pin"
    private const val CAP_RATIO = 0.60f

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun pin(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_PIN, "") ?: ""

    fun setPin(context: Context, pin: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_PIN, pin.filter { it.isDigit() }.take(6)).apply()
    }

    fun checkPin(context: Context, attempt: String): Boolean {
        val expected = pin(context)
        if (expected.isBlank()) return true
        return attempt == expected
    }

    fun setEnabled(context: Context, on: Boolean, pinAttempt: String = "") {
        if (!on && !checkPin(context, pinAttempt)) {
            Toast.makeText(context, "Pincode onjuist", Toast.LENGTH_SHORT).show()
            return
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ON, on).apply()
        if (on) enforce(context)
        Toast.makeText(context, if (on) "Kindveilig aan" else "Kindveilig uit", Toast.LENGTH_SHORT).show()
    }

    fun toggle(context: Context, pinAttempt: String = "") {
        setEnabled(context, !enabled(context), pinAttempt)
    }

    fun enforce(context: Context): Boolean {
        if (!enabled(context)) return false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return false
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        val cap = (max * CAP_RATIO).toInt().coerceAtLeast(1)
        if (am.getStreamVolume(AudioManager.STREAM_MUSIC) > cap) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, cap, 0)
            return true
        }
        return false
    }
}
