package com.project.composeproject.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import com.project.composeproject.ui.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LauncherActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
            val controller = WindowInsetsControllerCompat(window, view)
            hideSystemBars(controller)
            applyLightAppearance(controller)
            insets
        }

        setContent { AppNavigation() }
    }

    private fun applyLightAppearance(controller: WindowInsetsControllerCompat) {
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
    }

    private fun hideSystemBars(controller: WindowInsetsControllerCompat) {
        val navigationType = WindowInsetsCompat.Type.navigationBars()
        controller.hide(navigationType)
        controller.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}