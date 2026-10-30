package com.example.data

import android.content.Context
import com.example.ai.TunerNet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/** Album art via iTunes Search. Alleen op validated net, niet metered/roaming/portal. Met cache + retry. */
object AlbumArt {
    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    private val cache = ConcurrentHashMap<String, String>()
    private const val MAX_CACHE = 50

    suspend fun artUrl(context: Context, title: String, artist: String): String? = withContext(Dispatchers.IO) {
        val key = "$title|$artist".lowercase().trim()
        if (key.isBlank() || title.isBlank()) return@withContext null
        cache[key]?.let { return@withContext it }

        if (!TunerNet.validated(context) || TunerNet.metered(context) || TunerNet.roaming(context) || TunerNet.portal(context)) return@withContext null

        val q = "$title $artist".trim().ifBlank { title }
        val url = "https://itunes.apple.com/search?term=${java.net.URLEncoder.encode(q, "UTF-8")}&entity=song&limit=1"
        repeat(2) { attempt ->
            try {
                val req = Request.Builder().url(url).build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) return@repeat
                    val body = resp.body?.string() ?: return@repeat
                    val json = JSONObject(body)
                    val results = json.optJSONArray("results") ?: return@repeat
                    if (results.length() == 0) return@repeat
                    val art = results.getJSONObject(0).optString("artworkUrl100").takeIf { it.isNotBlank() }?.replace("100x100", "300x300")
                    if (art != null) {
                        if (cache.size >= MAX_CACHE) cache.clear()
                        cache[key] = art
                        return@withContext art
                    }
                }
            } catch (_: Exception) {
                if (attempt == 1) return@withContext null
            }
        }
        null
    }

    fun clearCache() = cache.clear()

    fun status(context: Context): String =
        if (TunerNet.validated(context) && !TunerNet.metered(context)) "AlbumArt klaar (cache ${cache.size}/$MAX_CACHE)" else "AlbumArt offline/data"
}
