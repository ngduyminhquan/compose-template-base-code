package com.project.composeproject.ui.utils

import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

fun AppCompatActivity.applySystemBarsSetting() {
    enableEdgeToEdge()
    ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
        val controller = WindowInsetsControllerCompat(window, view)
        hideSystemBars(controller)
        applyLightAppearance(controller)
        insets
    }
}

fun ComponentActivity.applySystemBarsSetting() {
    enableEdgeToEdge()
    ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
        val controller = WindowInsetsControllerCompat(window, view)
        hideSystemBars(controller)
        applyLightAppearance(controller)
        insets
    }
}

private fun applyLightAppearance(controller: WindowInsetsControllerCompat) {
    controller.isAppearanceLightStatusBars = true
    controller.isAppearanceLightNavigationBars = true
}

private fun hideSystemBars(controller: WindowInsetsControllerCompat) {
    val navigationType = WindowInsetsCompat.Type.navigationBars()
    controller.hide(navigationType)
    controller.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}