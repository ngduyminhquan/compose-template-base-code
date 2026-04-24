package com.project.composeproject.ads.utils

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.admob.next.gen.ads.ITGAdsBanner
import com.admob.next.gen.ads.ITGAdsRewarded
import com.admob.next.gen.ads.ITGIAdsInter
import com.admob.next.gen.banner.ITGBannerError
import com.admob.next.gen.callback.ITGBannerCallback
import com.admob.next.gen.callback.ITGInterCallback
import com.admob.next.gen.callback.ITGRewardedCallback
import com.admob.next.gen.inter.ITGInterError
import com.admob.next.gen.native.ITGAdsNative
import com.admob.next.gen.native.ITGNativeError
import com.admob.next.gen.native.PreloadCallback
import com.admob.next.gen.rewarded.ITGRewardedError
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.project.composeproject.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val TAG = "AdLoader"

object AdLoader {

    private val adsPool: MutableMap<String, MutableStateFlow<AdState>> = mutableMapOf()

    fun getAdState(adPlacement: String): MutableStateFlow<AdState> {
        return adsPool.getOrPut(adPlacement) { MutableStateFlow(AdState.Empty) }
    }


    fun loadNative(
        placement: String,
        layoutRes: Int = com.admob.next.gen.R.layout.layout_native_large,
        forceRefresh: Boolean = false
    ) {
        if (!MobileAds.isInitialized) {
            val errorMessage = "Mobile ads not initialized"
            debugLog("loadNative - $placement - $errorMessage")
            adsPool[placement]?.value = AdState.Error(errorMessage)
            return
        }

        if (adsPool[placement] == null) adsPool[placement] = MutableStateFlow(AdState.Empty)

        val state = adsPool[placement]?.value
        if (state is AdState.Loading) {
            debugLog("loadNative - $placement - Already loading")
            return
        }
        if (state is AdState.Loaded && !forceRefresh) {
            debugLog("loadNative - $placement - Already loaded")
            return
        }

        adsPool[placement]?.update { AdState.Loading }
        debugLog("loadNative - $placement - Loading")
        ITGAdsNative.preload(
            placement = placement,
            layoutRes = layoutRes,
            callback = object : PreloadCallback {
                override fun onPreloadSuccess(placement: String) {
                    debugLog("loadNative - $placement - Success")
                    adsPool[placement]?.update { AdState.Loaded(placement) }
                }

                override fun onPreloadFailed(error: ITGNativeError) {
                    debugLog("loadNative - $placement - Error: ${error.message}")
                    adsPool[placement]?.update { AdState.Error(error.message) }
                }
            }
        )
    }

    fun loadInterstitial(
        placement: String,
        forceRefresh: Boolean = false
    ) {
        if (!MobileAds.isInitialized) {
            val errorMessage = "Mobile ads not initialized"
            debugLog("loadInterstitial - $placement - $errorMessage")
            adsPool[placement]?.value = AdState.Error(errorMessage)
            return
        }

        if (adsPool[placement] == null) adsPool[placement] = MutableStateFlow(AdState.Empty)

        val state = adsPool[placement]?.value
        if (state is AdState.Loading) {
            debugLog("loadInterstitial - $placement - Already loading")
            return
        }
        if (state is AdState.Loaded && !forceRefresh) {
            debugLog("loadInterstitial - $placement - Already loaded")
            return
        }

        adsPool[placement]?.update { AdState.Loading }
        debugLog("loadNative - $placement - Loading")
        ITGIAdsInter.preload(
            placement = placement,
            forceRefresh = forceRefresh,
            callback = object : ITGInterCallback() {
                override fun onLoaded(placement: String) {
                    debugLog("loadInterstitial - $placement - Success")
                    adsPool[placement]?.update { AdState.Loaded(placement) }
                }

                override fun onFailed(placement: String, error: ITGInterError) {
                    debugLog("loadInterstitial - $placement - Error: ${error.message}")
                    adsPool[placement]?.update { AdState.Error(error.message) }
                }
            }
        )
    }

    fun loadBanner(
        placement: String,
        forceRefresh: Boolean = false
    ) {
        if (!MobileAds.isInitialized) {
            val errorMessage = "Mobile ads not initialized"
            debugLog("loadBanner - $placement - $errorMessage")
            adsPool[placement]?.value = AdState.Error(errorMessage)
            return
        }

        if (adsPool[placement] == null) adsPool[placement] = MutableStateFlow(AdState.Empty)

        val state = adsPool[placement]?.value
        if (state is AdState.Loading) {
            debugLog("loadBanner - $placement - Already loading")
            return
        }
        if (state is AdState.Loaded && !forceRefresh) {
            debugLog("loadBanner - $placement - Already loaded")
            return
        }

        adsPool[placement]?.update { AdState.Loading }
        debugLog("loadNative - $placement - Loading")
        ITGAdsBanner.preload(
            placement = placement,
            forceRefresh = forceRefresh,
            callback = object : ITGBannerCallback() {
                override fun onLoaded(adView: AdView, placement: String) {
                    debugLog("loadBanner - $placement - Success")
                    adsPool[placement]?.update { AdState.Loaded(placement) }
                }

                override fun onFailed(placement: String, error: ITGBannerError) {
                    debugLog("loadBanner - $placement - Error: ${error.message}")
                    adsPool[placement]?.update { AdState.Error(error.message) }
                }
            }
        )
    }

    fun autoReloadBanner(
        placement: String,
        lifecycleOwner: LifecycleOwner,
        intervalReloadTime: Long = 30_000L
    ) {
        if (!MobileAds.isInitialized) {
            val errorMessage = "Mobile ads not initialized"
            debugLog("autoReloadBanner - $placement - $errorMessage")
            adsPool[placement]?.value = AdState.Error(errorMessage)
            return
        }

        debugLog("autoReloadBanner - $placement - start auto reload")
        lifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                    loadBanner(placement, true)
                }
                delay(intervalReloadTime)
            }
        }
    }

    fun loadReward(
        placement: String,
        forceRefresh: Boolean = false
    ) {
        if (!MobileAds.isInitialized) {
            val errorMessage = "Mobile ads not initialized"
            debugLog("loadReward - $placement - $errorMessage")
            adsPool[placement]?.value = AdState.Error(errorMessage)
            return
        }

        if (adsPool[placement] == null) adsPool[placement] = MutableStateFlow(AdState.Empty)

        val state = adsPool[placement]?.value
        if (state is AdState.Loading) {
            debugLog("loadReward - $placement - Already loading")
            return
        }
        if (state is AdState.Loaded && !forceRefresh) {
            debugLog("loadReward - $placement - Already loaded")
            return
        }

        adsPool[placement]?.update { AdState.Loading }
        debugLog("loadNative - $placement - Loading")
        ITGAdsRewarded.preload(
            placement = placement,
            forceRefresh = forceRefresh,
            callback = object : ITGRewardedCallback() {
                override fun onLoaded(placement: String) {
                    debugLog("loadReward - $placement - Success")
                    adsPool[placement]?.update { AdState.Loaded(placement) }
                }

                override fun onFailed(placement: String, error: ITGRewardedError) {
                    debugLog("loadReward - $placement - Error: ${error.message}")
                    adsPool[placement]?.update { AdState.Error(error.message) }
                }
            }
        )
    }

    private fun debugLog(message: String) {
        if (!BuildConfig.DEBUG) return
        Log.d(TAG, message)
    }
}