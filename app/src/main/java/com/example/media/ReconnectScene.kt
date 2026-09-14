package com.example.media

import android.content.Context
import com.example.data.SceneHistory
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/**
 * Herstelt de laatst gebruikte luister-scene wanneer de headset weer verbindt.
 * Aanroepen vanuit de BT-connect receiver / DSP-service.
 */
object ReconnectScene {
    fun last(context: Context): ListeningScene? =
        SceneLookup.byId(SceneHistory(context).current())

    fun label(context: Context): String =
        last(context)?.let { "Herstel: ${it.title}" } ?: "Geen scene om te herstellen"
}
