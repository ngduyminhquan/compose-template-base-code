package com.project.composeproject

import android.app.Application
import com.project.composeproject.ads.AdsManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AdsManager.initialize(this)
    }
}