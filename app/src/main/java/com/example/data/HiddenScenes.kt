package com.example.data

import android.content.Context
import com.example.dsp.SceneGroups
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Verberg zelden gebruikte scene-mappen. Favorieten en Alles blijven zichtbaar. */
object HiddenScenes {
    private const val PREFS = "sounmax_hidden_scenes"
    private const val KEY = "hidden_groups"
    private const val KEY_AUTO = "auto_slim"
    private const val KEY_LAST_SLIM = "last_slim_day"
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

    fun showAll(context: Context) {
        save(context, emptySet())
    }

    fun autoSlimEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_AUTO, false)

    fun setAutoSlim(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_AUTO, on).apply()
        if (on) maybeAutoSlim(context, force = true)
    }

    fun toggleAutoSlim(context: Context): Boolean {
        val next = !autoSlimEnabled(context)
        setAutoSlim(context, next)
        return next
    }

    fun maybeAutoSlim(context: Context, force: Boolean = false) {
        if (!autoSlimEnabled(context) && !force) return
        val day = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!force && prefs.getString(KEY_LAST_SLIM, "") == day) return
        hideUnused(context, minUses = 1)
        prefs.edit().putString(KEY_LAST_SLIM, day).apply()
    }

    fun chipLabel(context: Context): String {
        val n = hidden(context).size
        return if (autoSlimEnabled(context)) "Compact aan ($n)" else "Compact uit"
    }

    private fun save(context: Context, groups: Set<String>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, groups.joinToString(","))
            .apply()
    }
}
