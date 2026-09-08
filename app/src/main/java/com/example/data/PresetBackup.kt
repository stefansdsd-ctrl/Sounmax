package com.example.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object PresetBackup {
    private const val SNAPSHOT_NAME = "sounmax-backup.json"
    private const val PREFS = "sounmax_backup"
    private const val KEY_TREE = "saf_tree_uri"
    const val MIME = "application/json"

    fun persistTreeUri(context: Context, uri: Uri) {
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        } catch (_: Exception) {}
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_TREE, uri.toString()).apply()
    }

    fun treeUri(context: Context): Uri? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_TREE, null)?.let { Uri.parse(it) }

    suspend fun exportToTree(context: Context): Boolean {
        val tree = treeUri(context) ?: return false
        return withContext(Dispatchers.IO) {
            try {
                val docId = DocumentsContract.getTreeDocumentId(tree)
                val folder = DocumentsContract.buildDocumentUriUsingTree(tree, docId)
                val dest = DocumentsContract.buildDocumentUriUsingTree(tree, "$docId/$SNAPSHOT_NAME")
                val json = buildJson(context)
                writeSnapshot(context, json)
                val out = try {
                    context.contentResolver.openOutputStream(dest, "wt")
                } catch (_: Exception) {
                    val created = DocumentsContract.createDocument(
                        context.contentResolver, folder, MIME, SNAPSHOT_NAME
                    )
                    created?.let { context.contentResolver.openOutputStream(it, "wt") }
                }
                out?.use { it.write(json.toByteArray()) } ?: return@withContext false
                true
            } catch (_: Exception) {
                false
            }
        }.also { ok ->
            Toast.makeText(
                context,
                if (ok) "Backup naar Drive-map geschreven" else "Drive-map schrijven mislukt",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    suspend fun exportToClipboard(context: Context) {
        val json = buildJson(context)
        writeSnapshot(context, json)
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
        Toast.makeText(context, "Backup gekopieerd + snapshot opgeslagen", Toast.LENGTH_SHORT).show()
    }

    suspend fun snapshotNow(context: Context): Boolean {
        val json = buildJson(context)
        writeSnapshot(context, json)
        return json.contains("\"presets\"")
    }

    suspend fun restoreLatestSnapshot(context: Context) {
        val file = File(context.filesDir, SNAPSHOT_NAME)
        if (!file.exists()) {
            Toast.makeText(context, "Geen lokale snapshot", Toast.LENGTH_SHORT).show()
            return
        }
        importJson(context, file.readText())
    }

    suspend fun importFromClipboard(context: Context) {
        val raw = context.getSystemService(ClipboardManager::class.java)
            ?.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString().orEmpty()
        importJson(context, raw)
    }

    suspend fun writeToUri(context: Context, uri: Uri) {
        val json = buildJson(context)
        writeSnapshot(context, json)
        withContext(Dispatchers.IO) {
            context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
        }
        Toast.makeText(context, "Backup naar bestand opgeslagen", Toast.LENGTH_SHORT).show()
    }

    suspend fun importFromUri(context: Context, uri: Uri) {
        val raw = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }.orEmpty()
        }
        importJson(context, raw)
    }

    suspend fun buildJson(context: Context): String {
        val dao = SoundMaxDatabase.getDatabase(context).eqPresetDao()
        val presets = withContext(Dispatchers.IO) { dao.getAllPresetsOnce() }
        val body = presets.joinToString(",") { p ->
            val name = p.name.replace("\"", "'")
            """{\"name\":\"$name\",\"cat\":\"${p.category}\",\"bands\":\"${p.bandGains}\",\"bass\":${p.bassBoost},\"virt\":${p.virtualizer},\"loud\":${p.loudness},\"clarity\":${p.clarity},\"fav\":${p.isFavorite}}"""
        }
        return """{\"app\":\"sounmax\",\"v\":2,\"ts\":${System.currentTimeMillis()},\"presets\":[$body]}"""
    }

    private fun writeSnapshot(context: Context, json: String) {
        try {
            File(context.filesDir, SNAPSHOT_NAME).writeText(json)
        } catch (_: Exception) {}
    }

    private suspend fun importJson(context: Context, raw: String) {
        if (!raw.contains("\"presets\"") && !raw.contains("\"bands\"")) {
            Toast.makeText(context, "Geen geldige Sounmax-backup JSON", Toast.LENGTH_SHORT).show()
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
