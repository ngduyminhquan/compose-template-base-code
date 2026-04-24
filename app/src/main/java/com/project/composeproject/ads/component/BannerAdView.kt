package com.project.composeproject.ads.component

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.admob.next.gen.ads.ITGAdsBanner
import com.admob.next.gen.banner.ITGCollapsiblePosition
import com.admob.next.gen.callback.ITGBannerCallback
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.project.composeproject.ads.utils.AdState

@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adState: AdState,

    @LayoutRes shimmerLayoutRes: Int = com.admob.next.gen.R.layout.ads_shimmer_banner,

    isCollapse: Boolean = false
) {

    fun getAdContainer(context: Context): FrameLayout {
        return FrameLayout(context).apply {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams = ViewGroup.LayoutParams(width, height)
            LayoutInflater.from(context).inflate(shimmerLayoutRes, this, true)
        }
    }

    when (adState) {
        AdState.Loading -> {
            AndroidView(
                modifier = modifier.fillMaxWidth(),
                factory = ::getAdContainer
            )
        }

        is AdState.Loaded -> {
//            AdUtils.tryToCloseCollapsibleBanner()
            AndroidView(
                modifier = modifier.fillMaxWidth(),
                factory = ::getAdContainer,
                update = { container ->
                    val callback = object : ITGBannerCallback() {}
                    if (isCollapse) {
                        ITGAdsBanner.showCollapsible(
                            placement = adState.placement,
                            viewGroup = container,
                            position = ITGCollapsiblePosition.BOTTOM,
                            forceRefresh = true,
                            center = true,
                            callback = callback
                        )
                    } else {
                        ITGAdsBanner.show(
                            placement = adState.placement,
                            viewGroup = container,
                            forceRefresh = true,
                            adSize = AdSize.FULL_BANNER,
                            center = true,
                            callback = callback
                        )
                    }
                }
            )
        }

        is AdState.Error, AdState.Empty -> {}
    }
}