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
        if (!raw.contains("\"kind\":\"scene\"") && !raw.contains("\"kind\":\"scene\"")) {
            if (!raw.contains("sounmax") || !raw.contains("\"id\"")) return null
        }
        val marker = "\"id\":"
        val i = raw.indexOf(marker)
        if (i < 0) return null
        return raw.substring(i + marker.length).trimStart().removePrefix("\"").substringBefore("\"").ifBlank { null }
    }

    private fun esc(s: String): String = s.replace("\"", "'")
}
