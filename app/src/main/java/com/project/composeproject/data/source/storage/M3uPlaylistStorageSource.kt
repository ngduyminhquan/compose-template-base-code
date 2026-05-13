package com.project.composeproject.data.source.storage

import android.content.ContentResolver
import android.net.Uri
import com.project.composeproject.data.core.m3u.M3UParser
import com.project.composeproject.data.core.m3u.M3uChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class M3uPlaylistStorageSource @Inject constructor(
    private val contentResolver: ContentResolver,
) {

    suspend fun fetchChannels(uri: Uri): List<M3uChannel> = withContext(Dispatchers.IO) {
        runCatching {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    M3UParser.parse(reader.readText())
                }
            }.orEmpty()
        }.getOrDefault(emptyList())
    }
}
