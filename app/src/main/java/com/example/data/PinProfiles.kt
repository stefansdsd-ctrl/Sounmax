package com.example.data

import android.content.Context
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/** Wisselbare pin-sets: Werk, Thuis, Onderweg. */
object PinProfiles {
    private const val PREFS = "sounmax_pin_profiles"
    private const val KEY_ACTIVE = "active"

    data class Profile(val id: String, val name: String, val ids: List<String>)

    val ALL = listOf(
        Profile("work", "Werk", listOf("focus", "zolderwerk", "kantinehal", "afterwork")),
        Profile("home", "Thuis", listOf("night", "thuisfilmlaat", "oorpauze", "avondcollege")),
        Profile("travel", "Onderweg", listOf("commute", "regenperron", "ovchippoortplus", "windfiets")),
    )

    fun activeId(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_ACTIVE, "home") ?: "home"

    fun active(context: Context): Profile =
        ALL.firstOrNull { it.id == activeId(context) } ?: ALL[1]

    fun apply(context: Context, profile: Profile): List<String> {
        val valid = profile.ids.mapNotNull { SceneLookup.byId(it)?.id }.distinct().take(4)
        context.getSharedPreferences("sounmax_home_pins", Context.MODE_PRIVATE)
            .edit().putString("ids", valid.joinToString(",")).apply()
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_ACTIVE, profile.id).apply()
        return valid
    }

    fun cycle(context: Context): Profile {
        val i = ALL.indexOfFirst { it.id == activeId(context) }.coerceAtLeast(0)
        val next = ALL[(i + 1) % ALL.size]
        apply(context, next)
        return next
    }

    fun label(context: Context): String = "Set: ${active(context).name}"

    fun scenes(context: Context): List<ListeningScene> =
        active(context).ids.mapNotNull { SceneLookup.byId(it) }
}
