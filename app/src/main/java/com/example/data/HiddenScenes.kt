package com.example.data

import android.content.Context
import com.example.dsp.SceneGroups

/** Verberg zelden gebruikte scene-mappen. Favorieten en Alles blijven zichtbaar. */
object HiddenScenes {
    private const val PREFS = "sounmax_hidden_scenes"
    private const val KEY = "hidden_groups"
    private val locked = setOf("Alles", "Favorieten")

    fun hidden(context: Context): Set<String> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, "") ?: ""
        return raw.split(',').map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }

    fun isHidden(context: Context, group: String): Boolean =
        group !in locked && group in hidden(context)

    fun hide(context: Context, group: String) {
        if (group in locked) return
        save(context, hidden(context) + group)
    }

    fun show(context: Context, group: String) {
        save(context, hidden(context) - group)
    }

    fun toggle(context: Context, group: String) {
        if (isHidden(context, group)) show(context, group) else hide(context, group)
    }

    fun visibleLabels(context: Context): List<Pair<String, Set<String>>> =
        SceneGroups.LABELS.filter { it.first !in hidden(context) }

    fun unusedGroups(context: Context, minUses: Int = 1): List<String> =
        SceneGroups.LABELS.map { it.first }
            .filter { it !in locked }
            .filter { group ->
                val ids = SceneGroups.ids(group).orEmpty()
                ids.isNotEmpty() && ids.sumOf { SceneUsage.count(context, it) } < minUses
            }

    fun hideUnused(context: Context, minUses: Int = 1) {
        unusedGroups(context, minUses).forEach { hide(context, it) }
    }

    private fun save(context: Context, groups: Set<String>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, groups.joinToString(","))
            .apply()
    }
}
