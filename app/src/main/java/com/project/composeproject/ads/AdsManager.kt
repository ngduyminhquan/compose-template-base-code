package com.project.composeproject.ads

import android.app.Application
import com.admob.next.gen.ads.ITGAdsOpenResume
import com.admob.next.gen.ads.ITGAdsSDK
import com.admob.next.gen.ads.ITGAdsSDKConfig
import com.project.composeproject.BuildConfig

object AdsManager {

    fun initialize(application: Application) {
        val appId = BuildConfig.APP_ID

        val config = ITGAdsSDKConfig(
            appId = appId,
            debug = BuildConfig.DEBUG,
            testDevices = listOf(),
            adjustToken = "thuongok",
            adjustTokenTiktok = "thuongok",
            facebookId = "thuongok",
            facebookClientToken = "thuongok"
        )

        ITGAdsSDK.apply {
            initialize(application, config)
            onAdMobReady {
                ITGAdsOpenResume.registerAndLoad(AdsPlacement.OPEN_RESUME)
            }
        }
    }
}