package com.project.composeproject.ui.screen.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingItem(
    @param:DrawableRes val imageResId: Int,
    @param:StringRes val titleResId: Int,
    @param:StringRes val subtitleResId: Int
)