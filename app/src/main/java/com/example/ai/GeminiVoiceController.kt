package com.example.ai

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Gemini Voice / Agent Commands v1
 * Natural language control for ANC, EQ, Spatial, scenes.
 * Maps common intents to existing controllers. Ready for AppFunctions / Gemini overlay.
 */
class GeminiVoiceController(
    private val context: Context,
    private val onAnc: (Int) -> Unit,
    private val onEqPreset: (String) -> Unit,
    private val onSpatial: (Boolean) -> Unit,
    private val onScene: (String) -> Unit
) {
    fun handleCommand(text: String) {
        val lower = text.lowercase()
        CoroutineScope(Dispatchers.Main).launch {
            when {
                lower.contains("anc") && (lower.contains("hoog") || lower.contains("high") || lower.contains("max")) -> onAnc(3)
                lower.contains("anc") && (lower.contains("laag") || lower.contains("low") || lower.contains("uit") || lower.contains("off")) -> onAnc(0)
                lower.contains("transparantie") || lower.contains("transparency") || lower.contains("praat") -> onAnc(0)
                lower.contains("anc") && lower.contains("medium") -> onAnc(2)
                lower.contains("spatial") && (lower.contains("aan") || lower.contains("on")) -> onSpatial(true)
                lower.contains("spatial") && (lower.contains("uit") || lower.contains("off")) -> onSpatial(false)
                lower.contains("rock") || lower.contains("metal") -> onEqPreset("Rock")
                lower.contains("hip") || lower.contains("hop") || lower.contains("bass") -> onEqPreset("Hip-Hop")
                lower.contains("podcast") || lower.contains("spraak") -> onEqPreset("Podcast")
                lower.contains("film") || lower.contains("movie") -> onEqPreset("Movie")
                lower.contains("focus") || lower.contains("werk") -> onScene("Focus")
                lower.contains("avond") || lower.contains("night") || lower.contains("zacht") -> onScene("Night")
                lower.contains("reis") || lower.contains("commute") -> onScene("Commute")
                else -> {
                    // fallback to AI tuner for complex requests
                    GeminiAudioTuner().generateAcousticProfile(text, "TAH6519").onSuccess { rec ->
                        onEqPreset(rec.presetName)
                    }
                }
            }
        }
    }
}
