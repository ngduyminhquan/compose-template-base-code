package com.project.composeproject.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.project.composeproject.ui.screen.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Button(
            onClick = {
//                val cacheFile = File(context.cacheDir, PLAYLIST_FILE_NAME)
//                context.assets.open(PLAYLIST_FILE_NAME).use { inputStream ->
//                    cacheFile.outputStream().use { outputStream ->
//                        inputStream.copyTo(outputStream)
//                    }
//                }
//                viewModel.createSourceFromFile(Uri.fromFile(cacheFile))

                viewModel.createSourceFromUrl("https://iptv-org.github.io/iptv/categories/family.m3u")
            },
        ) {
            Text(text = "Import playlist")
        }
    }
}

private const val PLAYLIST_FILE_NAME = "playlist_usa.m3u8"
