package com.example.data

import android.content.Context

/** Home-tools per modus: Sport / Werk / Slaap. */
object HomeToolProfiles {
    private const val PREFS = "sounmax_home_tools"
    private const val KEY = "profile"

    data class Profile(val id: String, val name: String, val tools: Set<String>)

    val ALL = listOf(
        Profile(
            "sport", "Sport",
            setOf("outdoor", "budget", "focus", "seal", "haptic", "talkthrough", "streak", "reconnect", "saver")
        ),
        Profile(
            "werk", "Werk",
            setOf("meeting", "talkboost", "duck", "focus", "multipoint", "appmix", "quiet", "context", "folder", "saver")
        ),
        Profile(
            "slaap", "Slaap",
            setOf("night", "sleeptimer", "quiet", "kidsafe", "budget", "volcap", "backup", "weekly", "saver")
        ),
    )

    fun activeId(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "werk") ?: "werk"

    fun active(context: Context): Profile =
        ALL.firstOrNull { it.id == activeId(context) } ?: ALL[1]

    fun set(context: Context, id: String) {
        if (ALL.none { it.id == id }) return
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, id).apply()
    }

    fun cycle(context: Context): Profile {
        val ids = ALL.map { it.id }
        val cur = activeId(context)
        val next = ids[(ids.indexOf(cur).coerceAtLeast(0) + 1) % ids.size]
        set(context, next)
        return active(context)
    }

    fun shows(context: Context, tool: String): Boolean = active(context).tools.contains(tool)

    fun shows(profileId: String, tool: String): Boolean =
        (ALL.firstOrNull { it.id == profileId } ?: ALL[1]).tools.contains(tool)
}
