package com.project.composeproject.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.admob.next.gen.ads.ITGAdsSDK
import com.project.composeproject.ads.AdsPlacement
import com.project.composeproject.ads.component.BannerAdView
import com.project.composeproject.ads.utils.AdLoader

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToSetting: () -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        ITGAdsSDK.onAdMobReady {
//            AdLoader.loadNative(
//                placement = AdsPlacement.NATIVE_LANGUAGE_2,
//                layoutRes = com.admob.next.gen.R.layout.layout_native_large
//            )

            AdLoader.loadBanner(
                placement = AdsPlacement.BANNER_ALL
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        Button(
            onClick = onNavigateToSetting
        ) {
            Text(
                text = "Setting"
            )
        }

//        val adState by AdsPlacement.nativeLanguageState.collectAsStateWithLifecycle()
//        NativeAdView(
//            adState = adState
//        )

        val adState by AdsPlacement.bannerAll.collectAsStateWithLifecycle()
        BannerAdView(
            adState = adState,
            isCollapse = true
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}