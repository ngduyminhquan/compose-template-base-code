package com.project.composeproject.ui.screen.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.admob.next.gen.ads.ITGAdsSDK
import com.project.composeproject.ads.AdsPlacement
import com.project.composeproject.ads.component.NativeAdView
import com.project.composeproject.ads.utils.AdLoader

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToSetting: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        Log.d("thuongok", "HomeScreen: recreate")
        ITGAdsSDK.onAdMobReady {
            AdLoader.loadNative(
                AdsPlacement.NATIVE_LANGUAGE_2,
                com.admob.next.gen.R.layout.layout_native_large
            )
        }
    }

    val adState by AdsPlacement.nativeLanguageState.collectAsStateWithLifecycle()

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = onNavigateToSetting
        ) { }
        NativeAdView(adState = adState)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}