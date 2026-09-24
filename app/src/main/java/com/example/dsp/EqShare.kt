package com.example.dsp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/** Compacte EQ-string om te kopiëren/plakken tussen apparaten. */
object EqShare {
    const val PREFIX = "SMX:"

    fun encode(dsp: AudioDspManager): String {
        val bands = dsp.bandGains.value.joinToString(",") { "%.1f".format(it) }
        return "$PREFIX$bands|${dsp.bassBoostStrength.value}|${dsp.virtualizerStrength.value}|${dsp.loudnessGain.value}|${"%.1f".format(dsp.clarityGain.value)}"
    }

    fun decodeAndApply(raw: String, dsp: AudioDspManager): Boolean {
        val text = raw.trim()
        if (!text.startsWith(PREFIX)) return false
        val body = text.removePrefix(PREFIX)
        val parts = body.split("|")
        if (parts.size < 1) return false
        return runCatching {
            val bands = parts[0].split(",").map { it.toFloat() }
            bands.forEachIndexed { i, g -> dsp.updateBandGain(i, g) }
            if (parts.size > 1) dsp.setBassBoost(parts[1].toInt())
            if (parts.size > 2) dsp.setVirtualizer(parts[2].toInt())
            if (parts.size > 3) dsp.setLoudness(parts[3].toInt())
            if (parts.size > 4) dsp.setClarity(parts[4].toFloat())
            true
        }.getOrDefault(false)
    }

    fun copy(context: Context, dsp: AudioDspManager) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("Sounmax EQ", encode(dsp)))
    }

    fun paste(context: Context, dsp: AudioDspManager): Boolean {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = cm.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString().orEmpty()
        return decodeAndApply(text, dsp)
    }

    fun flatten(dsp: AudioDspManager) {
        dsp.bandGains.value.indices.forEach { dsp.updateBandGain(it, 0f) }
        dsp.setBassBoost(0)
        dsp.setVirtualizer(0)
        dsp.setLoudness(0)
        dsp.setClarity(0f)
    }

    fun closestSlot(context: Context, dsp: AudioDspManager): String? {
        val current = dsp.bandGains.value
        var bestName: String? = null
        var best = Float.MAX_VALUE
        NamedEqSlots.NAMES.forEachIndexed { i, name ->
            val bands = NamedEqSlots.bands(context, i) ?: return@forEachIndexed
            val dist = current.zip(bands).sumOf { (a, b) -> kotlin.math.abs(a - b).toDouble() }.toFloat()
            if (dist < best) {
                best = dist
                bestName = name
            }
        }
        return if (best < 8f) bestName else null
    }
}
