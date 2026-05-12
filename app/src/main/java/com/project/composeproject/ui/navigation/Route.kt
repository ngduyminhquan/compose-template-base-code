package com.project.composeproject.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Route : NavKey {

    @Serializable
    data class Language(
        val fromSetting: Boolean
    ) : Route

    @Serializable
    data object Onboarding : Route
}