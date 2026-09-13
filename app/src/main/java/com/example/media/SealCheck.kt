package com.example.media

import android.content.Context
import android.media.AudioManager
import android.widget.Toast
import com.example.dsp.MicRmsProbe

/**
 * Snelle software-pasvormtest: 3 mic-RMS samples.
 * Lage leakage (weinig omgevingsgeluid in de mic bij gedragen doppen) ≈ betere seal.
 * Geen GATT nodig.
 */
object SealCheck {
    const val PREF_LAST_LABEL = "seal_last_label"
    const val PREF_LAST_AT = "seal_last_at"

    data class Result(val score: Int, val label: String)

    fun run(context: Context): Result {
        if (!MicRmsProbe.hasPermission(context)) {
            val r = Result(0, "Mic-toestemming nodig")
            persist(context, r)
            Toast.makeText(context, r.label, Toast.LENGTH_SHORT).show()
            return r
        }
        val samples = mutableListOf<Float>()
        repeat(3) {
            MicRmsProbe.sample(context, minIntervalMs = 0)?.let { samples.add(it) }
            try { Thread.sleep(90) } catch (_: InterruptedException) { }
        }
        val avg = if (samples.isEmpty()) -1f else samples.average().toFloat()
        val result = when {
            avg < 0f -> Result(0, "Geen mic-sample")
            avg < 0.12f -> Result(85, "Goede seal")
            avg < 0.28f -> Result(60, "Redelijke pasvorm")
            else -> Result(30, "Lek — doppen resetten")
        }
        persist(context, result)
        val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        val vol = am?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: -1
        Toast.makeText(
            context,
            "${result.label} (${result.score}/100)${if (vol >= 0) " · vol $vol" else ""}",
            Toast.LENGTH_SHORT
        ).show()
        return result
    }

    fun lastLabel(context: Context): String? {
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        val at = prefs.getLong(PREF_LAST_AT, 0L)
        if (at == 0L || System.currentTimeMillis() - at > 24 * 60 * 60_000L) return null
        return prefs.getString(PREF_LAST_LABEL, null)
    }

    private fun persist(context: Context, result: Result) {
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE).edit()
            .putString(PREF_LAST_LABEL, result.label)
            .putLong(PREF_LAST_AT, System.currentTimeMillis())
            .apply()
    }
}
