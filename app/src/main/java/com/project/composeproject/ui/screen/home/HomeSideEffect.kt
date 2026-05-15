package com.project.composeproject.ui.screen.home

sealed class HomeSideEffect {
    data class ShowError(val message: String) : HomeSideEffect()
}
