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

/** Album art via iTunes Search. Alleen op validated net, niet metered/roaming/portal. Met cache. */
object AlbumArt {
    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    private val cache = ConcurrentHashMap<String, String>()

    suspend fun artUrl(context: Context, title: String, artist: String): String? = withContext(Dispatchers.IO) {
        val key = "$title|$artist".lowercase().trim()
        if (key.isBlank()) return@withContext null
        cache[key]?.let { return@withContext it }

        if (!TunerNet.validated(context) || TunerNet.metered(context) || TunerNet.roaming(context) || TunerNet.portal(context)) return@withContext null

        val q = "$title $artist".trim()
        val url = "https://itunes.apple.com/search?term=${java.net.URLEncoder.encode(q, "UTF-8")}&entity=song&limit=1"
        try {
            val req = Request.Builder().url(url).build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return@withContext null
                val body = resp.body?.string() ?: return@withContext null
                val json = JSONObject(body)
                val results = json.optJSONArray("results") ?: return@withContext null
                if (results.length() == 0) return@withContext null
                val art = results.getJSONObject(0).optString("artworkUrl100").takeIf { it.isNotBlank() }?.replace("100x100", "300x300")
                art?.let { cache[key] = it }
                art
            }
        } catch (_: Exception) {
            null
        }
    }

    fun clearCache() = cache.clear()

    fun status(context: Context): String =
        if (TunerNet.validated(context) && !TunerNet.metered(context)) "AlbumArt klaar (cache ${cache.size})" else "AlbumArt offline/data"
}
