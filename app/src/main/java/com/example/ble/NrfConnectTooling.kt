package com.example.ble

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import java.io.File

/**
 * Bridging naar Nordic nRF Connect (MCP) voor TAH6519 GATT reverse-engineering.
 *
 * nRF Connect package: no.nordicsemi.android.mcp
 * Play: https://play.google.com/store/apps/details?id=no.nordicsemi.android.mcp
 */
object NrfConnectTooling {
    const val PKG = "no.nordicsemi.android.mcp"
    const val PLAY =
        "https://play.google.com/store/apps/details?id=no.nordicsemi.android.mcp"

    fun isInstalled(context: Context): Boolean =
        runCatching {
            context.packageManager.getPackageInfo(PKG, 0)
            true
        }.getOrDefault(false)

    fun openOrInstall(context: Context) {
        if (isInstalled(context)) {
            val launch = context.packageManager.getLaunchIntentForPackage(PKG)
            if (launch != null) {
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launch)
                Toast.makeText(context, "nRF Connect geopend", Toast.LENGTH_SHORT).show()
                return
            }
        }
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$PKG")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        } catch (_: ActivityNotFoundException) {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(PLAY)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
        Toast.makeText(context, "Installeer nRF Connect", Toast.LENGTH_SHORT).show()
    }

    fun workflowHint(): String = """
        nRF Connect workflow (TAH6519 ANC):
        1. Open nRF Connect → Scanner → verbind headset (LE)
        2. Discover services; noteer FE/FD vendor services
        3. Enable NOTIFY op write-kandidaten indien aanwezig
        4. Druk fysieke ANC-knop (ANC / Normaal / Transparantie)
        5. Lees opnieuw characteristics — noteer Value-wijzigingen
        6. Export / screenshot → plak in Sounmax (Import nRF-tekst)
        7. Of: Sounmax Dump-baseline → knop → Dump-anc en vergelijk
    """.trimIndent()

    /** Importeer platte tekst uit nRF Connect (share/export). */
    fun importText(context: Context, raw: String, label: String = "nrf-import"): Boolean {
        if (raw.isBlank() || raw.length < 40) return false
        val cleaned = raw.trim()
        try {
            File(context.filesDir, "nrf_import_${label}.txt").writeText(cleaned)
            File(context.filesDir, "nrf_gatt_history.txt").appendText(
                "\n\n===== import $label =====\n$cleaned\n"
            )
            // probeer UUID's te extraheren voor RealAnc leerpad
            extractWriteHints(cleaned)?.let { (svc, ch) ->
                context.getSharedPreferences("soundmax_anc_hw", Context.MODE_PRIVATE)
                    .edit()
                    .putString("learned_svc", svc)
                    .putString("learned_char", ch)
                    .apply()
            }
            return true
        } catch (_: Exception) {
            return false
        }
    }

    fun importFromClipboard(context: Context): Boolean {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
        val raw = cm?.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString().orEmpty()
        val ok = importText(context, raw, "clipboard")
        Toast.makeText(
            context,
            if (ok) "nRF-tekst geïmporteerd (${raw.length} tekens)" else "Clipboard leeg/ongeldig",
            Toast.LENGTH_SHORT
        ).show()
        return ok
    }

    /** Eenvoudige diff tussen twee history-secties of files. */
    fun diffDumps(a: String, b: String): String {
        val la = a.lines().filter { it.contains("Value:") || it.contains("Characteristic:") }
        val lb = b.lines().filter { it.contains("Value:") || it.contains("Characteristic:") }
        val setA = la.toSet()
        val setB = lb.toSet()
        val onlyA = setA - setB
        val onlyB = setB - setA
        return buildString {
            appendLine("# Diff (Value/Characteristic regels)")
            appendLine("# alleen in A: ${onlyA.size}")
            onlyA.take(40).forEach { appendLine("- $it") }
            appendLine("# alleen in B: ${onlyB.size}")
            onlyB.take(40).forEach { appendLine("+ $it") }
        }
    }

    fun shareDiff(context: Context, labelA: String, labelB: String) {
        val hist = NrfStyleGattDump.history(context)
        if (hist.isBlank()) {
            Toast.makeText(context, "Geen history — maak eerst dumps", Toast.LENGTH_SHORT).show()
            return
        }
        val parts = hist.split("=====").filter { it.isNotBlank() }
        fun section(label: String) = parts.firstOrNull { it.contains(label, ignoreCase = true) }.orEmpty()
        val diff = diffDumps(section(labelA), section(labelB))
        try {
            context.startActivity(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Sounmax GATT diff $labelA vs $labelB")
                    putExtra(Intent.EXTRA_TEXT, diff)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.let {
                    Intent.createChooser(it, "Deel GATT-diff")
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        } catch (_: Exception) {
            Toast.makeText(context, diff.take(200), Toast.LENGTH_LONG).show()
        }
    }

    private fun extractWriteHints(text: String): Pair<String, String>? {
        val uuid =
            Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")
        val lines = text.lines()
        var lastSvc: String? = null
        for (line in lines) {
            val l = line.lowercase()
            if (l.contains("service")) {
                uuid.find(line)?.value?.let { lastSvc = it }
            }
            if ((l.contains("write") || l.contains("wn")) && lastSvc != null) {
                uuid.find(line)?.value?.let { return lastSvc!! to it }
            }
        }
        // fallback: eerste FE-service + eerste char UUID erna
        val fe = lines.firstOrNull { it.lowercase().contains("0000fe") } ?: return null
        val svc = uuid.find(fe)?.value ?: return null
        val idx = lines.indexOf(fe)
        for (i in idx + 1 until minOf(idx + 20, lines.size)) {
            uuid.find(lines[i])?.value?.let { return svc to it }
        }
        return null
    }
}
