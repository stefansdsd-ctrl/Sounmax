package com.example.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PresetBackup {
    suspend fun exportToClipboard(context: Context) {
        val dao = SoundMaxDatabase.getDatabase(context).eqPresetDao()
        val presets = withContext(Dispatchers.IO) { dao.getAllPresetsOnce() }
        val body = presets.joinToString(",") { p ->
            val name = p.name.replace("\"", "'")
            """{"name":"$name","cat":"${p.category}","bands":"${p.bandGains}","bass":${p.bassBoost},"virt":${p.virtualizer},"loud":${p.loudness},"clarity":${p.clarity},"fav":${p.isFavorite}}"""
        }
        val json = """{"app":"sounmax","v":1,"presets":[$body]}"""
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("sounmax-backup", json))
        try {
            context.startActivity(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Sounmax preset-backup")
                    putExtra(Intent.EXTRA_TEXT, json)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.let { Intent.createChooser(it, "Deel Sounmax backup").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            )
        } catch (_: Exception) {}
        Toast.makeText(context, "Backup: ${presets.size} presets gekopieerd", Toast.LENGTH_SHORT).show()
    }

    suspend fun importFromClipboard(context: Context) {
        val raw = context.getSystemService(ClipboardManager::class.java)
            ?.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString().orEmpty()
        if (!raw.contains("\"presets\"") && !raw.contains("\"bands\"")) {
            Toast.makeText(context, "Plak eerst een Sounmax-backup JSON", Toast.LENGTH_SHORT).show()
            return
        }
        val dao = SoundMaxDatabase.getDatabase(context).eqPresetDao()
        val blocks = raw.split("{").drop(1)
        var imported = 0
        withContext(Dispatchers.IO) {
            blocks.forEach { block ->
                val name = extract(block, "name") ?: return@forEach
                val bands = extract(block, "bands") ?: return@forEach
                if (bands.split(",").mapNotNull { it.toFloatOrNull() }.size != 10 &&
                    !bands.contains(",")
                ) return@forEach
                dao.insertPreset(
                    EqPresetEntity(
                        name = name,
                        isCustom = true,
                        category = extract(block, "cat") ?: "Backup",
                        bandGains = bands,
                        bassBoost = extractInt(block, "bass"),
                        virtualizer = extractInt(block, "virt"),
                        loudness = extractInt(block, "loud"),
                        clarity = extractFloat(block, "clarity"),
                        isFavorite = block.contains("\"fav\":true")
                    )
                )
                imported++
            }
        }
        Toast.makeText(
            context,
            if (imported > 0) "$imported presets hersteld" else "Geen geldige presets gevonden",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun extract(block: String, key: String): String? {
        val marker = "\"$key\":"
        val i = block.indexOf(marker)
        if (i < 0) return null
        val after = block.substring(i + marker.length).trimStart()
        return if (after.startsWith("\"")) after.removePrefix("\"").substringBefore("\"")
        else after.takeWhile { it != ',' && it != '}' }.trim()
    }

    private fun extractInt(block: String, key: String): Int =
        extract(block, key)?.toIntOrNull() ?: 0

    private fun extractFloat(block: String, key: String): Float =
        extract(block, key)?.toFloatOrNull() ?: 0f
}
