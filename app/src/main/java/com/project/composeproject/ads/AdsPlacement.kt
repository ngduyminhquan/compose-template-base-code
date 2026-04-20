package com.project.composeproject.ads

import com.project.composeproject.ads.utils.AdLoader
import com.project.composeproject.ads.utils.AdState
import kotlinx.coroutines.flow.StateFlow

object AdsPlacement {
    const val INTER_SPLASH = "inter_splash"
    const val OPEN_RESUME = "open_resume"
    const val NATIVE_LANGUAGE_1 = "native_language_1"
    const val NATIVE_LANGUAGE_1_CLICK = "native_language_1_click"
    const val NATIVE_LANGUAGE_2 = "native_language_2"
    const val NATIVE_LANGUAGE_2_CLICK = "native_language_2_click"
    const val NATIVE_ONBOARDING_1_1 = "native_onboarding_1_1"
    const val NATIVE_ONBOARDING_2_1 = "native_onboarding_2_1"
    const val NATIVE_ONBOARDING_1_4 = "native_onboarding_1_4"
    const val NATIVE_ONBOARDING_2_4 = "native_onboarding_2_4"
    const val NATIVE_ONBOARDING_FULLSCREEN_1_2 = "native_onboarding_fullscreen_1_2"
    const val NATIVE_ONBOARDING_FULLSCREEN_2_2 = "native_onboarding_fullscreen_2_2"
    const val INTER_ONBOARDING = "inter_onboarding"
    const val BANNER_ALL = "banner_all"

    val interSplashState: StateFlow<AdState>
        get() = AdLoader.getAdState(INTER_SPLASH)

    val nativeLanguageState: StateFlow<AdState>
        get() = run {
            val nativeLanguage1 = AdLoader.getAdState(NATIVE_LANGUAGE_1)
            val nativeLanguage2 = AdLoader.getAdState(NATIVE_LANGUAGE_2)
            if (nativeLanguage1.value !is AdState.Empty) nativeLanguage1 else nativeLanguage2
        }

    val nativeLanguageClickState: StateFlow<AdState>
        get() = run {
            val nativeLanguageClick1 = AdLoader.getAdState(NATIVE_LANGUAGE_1_CLICK)
            val nativeLanguageClick2 = AdLoader.getAdState(NATIVE_LANGUAGE_2_CLICK)
            if (nativeLanguageClick1.value !is AdState.Empty) nativeLanguageClick1 else nativeLanguageClick2
        }

    val nativeOnboarding1State: StateFlow<AdState>
        get() = run {
            val nativeOnboarding11 = AdLoader.getAdState(NATIVE_ONBOARDING_1_1)
            val nativeOnboarding21 = AdLoader.getAdState(NATIVE_ONBOARDING_2_1)
            if (nativeOnboarding11.value !is AdState.Empty) nativeOnboarding11 else nativeOnboarding21
        }

    val nativeOnboarding4State: StateFlow<AdState>
        get() = run {
            val nativeOnboarding14 = AdLoader.getAdState(NATIVE_ONBOARDING_1_4)
            val nativeOnboarding24 = AdLoader.getAdState(NATIVE_ONBOARDING_2_4)
            if (nativeOnboarding14.value !is AdState.Empty) nativeOnboarding14 else nativeOnboarding24
        }

    val nativeOnboardingFullscreenState: StateFlow<AdState>
        get() = run {
            val nativeOnboardingFullscreen12 =
                AdLoader.getAdState(NATIVE_ONBOARDING_FULLSCREEN_1_2)
            val nativeOnboardingFullscreen22 =
                AdLoader.getAdState(NATIVE_ONBOARDING_FULLSCREEN_2_2)
            if (nativeOnboardingFullscreen12.value !is AdState.Empty) nativeOnboardingFullscreen12 else nativeOnboardingFullscreen22
        }

    val interOnboardingState: StateFlow<AdState>
        get() = AdLoader.getAdState(INTER_ONBOARDING)

    val bannerAll: StateFlow<AdState>
        get() = AdLoader.getAdState(BANNER_ALL)
}