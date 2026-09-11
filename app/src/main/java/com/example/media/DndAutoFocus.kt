package com.example.media

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Als de gebruiker systeem-DND aanzet → Focus starten.
 * Als DND uitgaat en Focus door ons gold → Focus stoppen.
 */
class DndAutoFocus : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED) return
        if (!DndFocusFilter.enabled(context)) return
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        val filter = nm.currentInterruptionFilter
        val dndOn = filter == NotificationManager.INTERRUPTION_FILTER_PRIORITY ||
            filter == NotificationManager.INTERRUPTION_FILTER_ALARMS ||
            filter == NotificationManager.INTERRUPTION_FILTER_NONE
        val prefs = context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)
        val ours = prefs.getBoolean(DndFocusFilter.KEY_APPLIED, false)
        when {
            dndOn && !FocusSession.isActive(context) && !ours -> {
                prefs.edit().putBoolean(KEY_FROM_SYSTEM, true).apply()
                FocusSession.start(context, 25)
            }
            !dndOn && FocusSession.isActive(context) && prefs.getBoolean(KEY_FROM_SYSTEM, false) -> {
                prefs.edit().putBoolean(KEY_FROM_SYSTEM, false).apply()
                FocusSession.cancel(context)
            }
        }
    }

    companion object {
        const val KEY_FROM_SYSTEM = "dnd_autofocus_from_system"
    }
}
