package com.example.media

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast

/**
 * Tijdens Focus: onderbreekfilter PRIORITY (DND-lite).
 * Herstelt vorige filter bij einde. Vereist ACCESS_NOTIFICATION_POLICY.
 */
object DndFocusFilter {
    const val PREFS = SceneAutomation.PREFS
    const val KEY_ENABLED = "dnd_focus_filter"
    const val KEY_PREV = "dnd_prev_filter"
    const val KEY_APPLIED = "dnd_applied"

    private val noisyPrefixes = listOf(
        "com.instagram.", "com.facebook.", "com.twitter.", "com.x.",
        "com.snapchat.", "com.zhiliaoapp.musically", "com.ss.android.ugc.trill",
        "com.whatsapp", "org.telegram.", "com.discord", "com.reddit.",
        "com.google.android.gm", "com.microsoft.office.outlook",
        "com.linkedin.", "com.pinterest.", "com.slack"
    )

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun hasPolicyAccess(context: Context): Boolean {
        val nm = context.getSystemService(NotificationManager::class.java) ?: return false
        return nm.isNotificationPolicyAccessGranted
    }

    fun openPolicySettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun isNoisyPackage(packageName: String?): Boolean {
        val pkg = packageName.orEmpty()
        if (pkg.isBlank()) return false
        return noisyPrefixes.any { pkg.startsWith(it) || pkg == it.trimEnd('.') }
    }

    fun enter(context: Context) {
        if (!enabled(context)) return
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        if (!nm.isNotificationPolicyAccessGranted) {
            Toast.makeText(
                context,
                "Focus-DND: geef ‘Niet storen’-toegang in Instellingen",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_APPLIED, false)) {
            prefs.edit()
                .putInt(KEY_PREV, nm.currentInterruptionFilter)
                .putBoolean(KEY_APPLIED, true)
                .apply()
        }
        nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
    }

    fun exit(context: Context) {
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_APPLIED, false)) return
        val prev = prefs.getInt(KEY_PREV, NotificationManager.INTERRUPTION_FILTER_ALL)
        if (nm.isNotificationPolicyAccessGranted) {
            nm.setInterruptionFilter(
                if (prev == NotificationManager.INTERRUPTION_FILTER_UNKNOWN) {
                    NotificationManager.INTERRUPTION_FILTER_ALL
                } else prev
            )
        }
        prefs.edit().putBoolean(KEY_APPLIED, false).apply()
    }
}
