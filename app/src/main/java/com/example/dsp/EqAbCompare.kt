package com.example.dsp

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.media.AncHaptics

/** Live A/B: huidige curve vs vorige snapshot. 8s timeout terug naar A. */
object EqAbCompare {
    private const val TIMEOUT_MS = 8_000L
    private val main = Handler(Looper.getMainLooper())
    private var pending: Runnable? = null
    @Volatile var showingB: Boolean = false
        private set

    fun snapshotCurrentAsA(context: Context, gains: List<Float>) {
        EqSnapshot.saveA(context, gains, toast = false)
    }

    fun toggle(
        context: Context,
        current: List<Float>,
        apply: (List<Float>) -> Unit,
    ) {
        cancelTimeout()
        if (!showingB) {
            EqSnapshot.saveB(context, current, toast = false)
            val a = storedA(context) ?: current.map { 0f }
            apply(a)
            showingB = true
            AncHaptics.tick(context, 1)
            Toast.makeText(context, "EQ B → A (8s)", Toast.LENGTH_SHORT).show()
            val task = Runnable {
                apply(current)
                showingB = false
                Toast.makeText(context, "EQ A", Toast.LENGTH_SHORT).show()
            }
            pending = task
            main.postDelayed(task, TIMEOUT_MS)
        } else {
            val b = storedB(context) ?: current
            apply(b)
            showingB = false
            AncHaptics.tick(context, 2)
            Toast.makeText(context, "EQ A", Toast.LENGTH_SHORT).show()
        }
    }

    fun cancelTimeout() {
        pending?.let { main.removeCallbacks(it) }
        pending = null
    }

    private fun storedA(context: Context): List<Float>? {
        val raw = context.getSharedPreferences("soundmax_eq_ab", Context.MODE_PRIVATE)
            .getString("gains_a", null)
        return parse(raw)
    }

    private fun storedB(context: Context): List<Float>? {
        val raw = context.getSharedPreferences("soundmax_eq_ab", Context.MODE_PRIVATE)
            .getString("gains_b", null)
        return parse(raw)
    }

    private fun parse(raw: String?): List<Float>? {
        if (raw.isNullOrBlank()) return null
        val parts = raw.split(',').mapNotNull { it.toFloatOrNull() }
        return parts.takeIf { it.size >= 5 }
    }
}
