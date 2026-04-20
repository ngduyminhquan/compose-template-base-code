package com.project.composeproject.ads.component

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.admob.next.gen.callback.ITGNativeCallback
import com.admob.next.gen.native.ITGAdsNative
import com.project.composeproject.ads.utils.AdState

@Composable
fun NativeAdView(
    modifier: Modifier = Modifier,
    adState: AdState,

    @LayoutRes layoutRes: Int = com.admob.next.gen.R.layout.layout_native_large,
    @LayoutRes shimmerLayoutRes: Int = com.admob.next.gen.R.layout.ads_shimmer_native_large,

    forcePopulate: Boolean = false,
    fillMaxHeight: Boolean = false
) {
    var isPopulated by remember { mutableStateOf(false) }

    fun getAdContainer(context: Context): FrameLayout {
        return FrameLayout(context).apply {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height =
                if (fillMaxHeight) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
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
            AndroidView(
                modifier = modifier.fillMaxWidth(),
                factory = ::getAdContainer,
                update = { container ->
                    if (!isPopulated || forcePopulate) {
                        ITGAdsNative.showLarge(
                            placement = adState.placement,
                            layoutRes = layoutRes,
                            viewGroup = container,
                            callback = object : ITGNativeCallback() {}
                        )
                    }
                    isPopulated = true
                }
            )
        }

        is AdState.Error, AdState.Empty -> {}
    }
}