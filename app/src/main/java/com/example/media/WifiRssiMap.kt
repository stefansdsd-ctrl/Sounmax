package com.example.media

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import com.example.dsp.ListeningScene
import com.example.dsp.SceneLookup
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Indoor plaats via wifi-RSSI-fingerprint.
 * Meerdere pins per scene (kamers). Match wint op hoogste score.
 */
object WifiRssiMap {
    const val KEY_ENABLED = "wifi_rssi_map"
    private const val KEY_PRINTS = "wifi_rssi_prints_json"
    private const val KEY_LAST = "wifi_rssi_last_match"
    private const val MAX_PRINTS = 48
    private const val MIN_SCORE = 0.32f

    data class RoomPin(val id: String, val label: String, val sceneId: String)

    val ROOMS = listOf(
        RoomPin("woonkamer", "Woonkamer", "thuisavond"),
        RoomPin("keuken", "Keuken", "thuiskids"),
        RoomPin("slaapkamer", "Slaapkamer", "sleep"),
        RoomPin("studeerkamer", "Studeerkamer", "focus"),
        RoomPin("kantoor", "Kantoorhoek", "kantooropen"),
        RoomPin("sportschool", "Sportschool", "sportschool"),
        RoomPin("koffie", "Koffietent", "koffietent"),
        RoomPin("trein", "Trein", "intercity")
    )

    fun enabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLED, true)

    fun setEnabled(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, on).apply()
    }

    fun pinCurrent(context: Context, sceneId: String, label: String? = null): Boolean {
        val vector = scanVector(context) ?: return false
        if (vector.isEmpty()) return false
        val name = label ?: sceneId
        val prints = load(context).toMutableList()
        prints.removeAll { it.label.equals(name, ignoreCase = true) }
        prints.add(
            0,
            Fingerprint(
                sceneId = sceneId,
                label = name,
                bssids = vector,
                ts = System.currentTimeMillis()
            )
        )
        while (prints.size > MAX_PRINTS) prints.removeAt(prints.lastIndex)
        save(context, prints)
        return true
    }

    fun pinRoom(context: Context, roomId: String): Boolean {
        val room = ROOMS.firstOrNull { it.id == roomId } ?: return false
        return pinCurrent(context, room.sceneId, room.label)
    }

    fun removeLabel(context: Context, label: String) {
        val next = load(context).filterNot { it.label.equals(label, ignoreCase = true) }
        save(context, next)
    }

    fun clear(context: Context) {
        prefs(context).edit().remove(KEY_PRINTS).remove(KEY_LAST).apply()
    }

    fun list(context: Context): List<Fingerprint> = load(context)

    fun adjust(context: Context, scene: ListeningScene): ListeningScene {
        if (!enabled(context)) return scene
        val prints = load(context)
        if (prints.isEmpty()) return scene
        val vector = scanVector(context) ?: return scene
        if (vector.isEmpty()) return scene

        var best: Fingerprint? = null
        var bestScore = 0f
        for (fp in prints) {
            val s = similarity(vector, fp.bssids)
            if (s > bestScore) {
                bestScore = s
                best = fp
            }
        }
        if (best == null || bestScore < MIN_SCORE) return scene
        prefs(context).edit()
            .putString(KEY_LAST, "${best.label} (${"%.0f".format(bestScore * 100)}%)")
            .apply()
        return SceneLookup.byId(best.sceneId) ?: scene
    }

    fun lastMatch(context: Context): String =
        prefs(context).getString(KEY_LAST, "") ?: ""

    private fun scanVector(context: Context): Map<String, Int>? {
        return runCatching {
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            @Suppress("DEPRECATION")
            val results: List<ScanResult> = wm.scanResults.orEmpty()
            if (results.isEmpty()) {
                runCatching { @Suppress("DEPRECATION") wm.startScan() }
                return null
            }
            results
                .filter { !it.BSSID.isNullOrBlank() }
                .sortedByDescending { it.level }
                .take(16)
                .associate { it.BSSID.lowercase() to it.level }
        }.getOrNull()
    }

    private fun similarity(a: Map<String, Int>, b: Map<String, Int>): Float {
        val keys = a.keys.intersect(b.keys)
        if (keys.isEmpty()) return 0f
        var num = 0.0
        var denA = 0.0
        var denB = 0.0
        for (k in keys) {
            val va = (a[k] ?: -100) + 100.0
            val vb = (b[k] ?: -100) + 100.0
            val close = 1.0 - (abs(va - vb) / 60.0).coerceIn(0.0, 1.0)
            num += va * vb * close
            denA += va * va
            denB += vb * vb
        }
        val base = if (denA <= 0 || denB <= 0) 0.0 else num / (sqrt(denA) * sqrt(denB))
        val coverage = keys.size.toDouble() / maxOf(3, minOf(a.size, b.size))
        return (base * (0.55 + 0.45 * coverage.coerceIn(0.0, 1.0))).toFloat().coerceIn(0f, 1f)
    }

    private fun load(context: Context): List<Fingerprint> {
        val raw = prefs(context).getString(KEY_PRINTS, null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val b = o.getJSONObject("b")
                    val map = mutableMapOf<String, Int>()
                    b.keys().forEach { map[it] = b.getInt(it) }
                    add(
                        Fingerprint(
                            sceneId = o.getString("s"),
                            label = o.optString("l", o.getString("s")),
                            bssids = map,
                            ts = o.optLong("t", 0L)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun save(context: Context, list: List<Fingerprint>) {
        val arr = JSONArray()
        list.forEach { fp ->
            val b = JSONObject()
            fp.bssids.forEach { (k, v) -> b.put(k, v) }
            arr.put(
                JSONObject()
                    .put("s", fp.sceneId)
                    .put("l", fp.label)
                    .put("b", b)
                    .put("t", fp.ts)
            )
        }
        prefs(context).edit().putString(KEY_PRINTS, arr.toString()).apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(SceneAutomation.PREFS, Context.MODE_PRIVATE)

    data class Fingerprint(
        val sceneId: String,
        val label: String,
        val bssids: Map<String, Int>,
        val ts: Long
    )
}
