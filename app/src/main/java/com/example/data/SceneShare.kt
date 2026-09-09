package com.example.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup

/** Deel / importeer één luister-scene als compacte JSON. */
object SceneShare {
    fun toJson(scene: ListeningScene): String {
        val codec = scene.preferredCodec?.name ?: ""
        val ldac = scene.preferredLdac?.name ?: ""
        return """{"app":"sounmax","kind":"scene","id":"${esc(scene.id)}","name":"${esc(scene.name)}","emoji":"${esc(scene.emoji)}","preset":"${esc(scene.presetName)}","anc":"${scene.ancMode.name}","safe":${scene.safeVolume},"codec":"$codec","ldac":"$ldac"}"""
    }

    fun share(context: Context, scene: ListeningScene) {
        val json = toJson(scene)
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("sounmax-scene", json))
        try {
            context.startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Sounmax scene ${scene.name}")
                        putExtra(Intent.EXTRA_TEXT, json)
                    },
                    "Deel scene"
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (_: Exception) {}
        Toast.makeText(context, "Scene ${scene.name} gekopieerd", Toast.LENGTH_SHORT).show()
    }

    fun shareById(context: Context, sceneId: String) {
        val scene = SceneLookup.byId(sceneId)
        if (scene == null) {
            Toast.makeText(context, "Onbekende scene", Toast.LENGTH_SHORT).show()
            return
        }
        share(context, scene)
    }

    fun parseId(raw: String): String? {
        val text = raw.trim()
        if (text.isBlank()) return null
        if (!text.contains("sounmax", ignoreCase = true) && !text.contains("\"kind\":\"scene\"")) {
            return null
        }
        val marker = "\"id\":"
        val i = text.indexOf(marker)
        if (i < 0) return null
        return text.substring(i + marker.length).trimStart().removePrefix("\"").substringBefore("\"").ifBlank { null }
    }

    fun clipboardText(context: Context): String? {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = cm.primaryClip ?: return null
        if (clip.itemCount == 0) return null
        return clip.getItemAt(0).coerceToText(context)?.toString()
    }

    fun parseFromIntent(intent: Intent?): String? {
        if (intent == null) return null
        val extra = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return null
        return parseId(extra)
    }

    /** Leest klembord, zoekt scene-id, of null. */
    fun parseFromClipboard(context: Context): String? {
        val raw = clipboardText(context) ?: return null
        return parseId(raw)
    }

    /**
     * Importeer scene-JSON vanaf klembord.
     * @return scene-id of null als niets geldigs.
     */
    fun importFromClipboard(context: Context, apply: (ListeningScene) -> Unit): String? {
        val id = parseFromClipboard(context)
        if (id == null) {
            Toast.makeText(context, "Geen Sounmax-scene op klembord", Toast.LENGTH_SHORT).show()
            return null
        }
        val scene = SceneLookup.byId(id)
        if (scene == null) {
            Toast.makeText(context, "Onbekende scene: $id", Toast.LENGTH_SHORT).show()
            return null
        }
        apply(scene)
        Toast.makeText(context, "Geïmporteerd: ${scene.emoji} ${scene.name}", Toast.LENGTH_SHORT).show()
        return id
    }

    private fun esc(s: String): String = s.replace("\"", "'")
}
