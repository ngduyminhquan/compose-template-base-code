package com.project.composeproject.data.source.network

import com.project.composeproject.data.core.m3u.M3UParser
import com.project.composeproject.data.core.m3u.M3uChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class M3uPlaylistNetworkSource @Inject constructor() {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun fetchChannels(playlistUrl: String): List<M3uChannel> = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder()
                .url(playlistUrl)
                .addHeader("User-Agent", "Mozilla/5.0")
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext emptyList()
                }

                val content = response.body?.string() ?: return@withContext emptyList()
                M3UParser.parse(content)
            }
        }.getOrDefault(emptyList())
    }
}
