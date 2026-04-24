package com.project.composeproject.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.project.composeproject.ui.navigation.Route
import com.project.composeproject.ui.screen.home.HomeScreen
import com.project.composeproject.ui.screen.setting.SettingScreen
import com.project.composeproject.ui.theme.ComposeProjectTheme
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

        setContent {
            ComposeProjectTheme { AppContent() }
        }
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

@Composable
fun AppContent() {
    Box {
        val backStack = rememberNavBackStack(Route.Home)
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry(Route.Home) {
                    HomeScreen(
                        onNavigateToSetting = {
                            backStack.add(Route.Setting)
                        }
                    )
                }

                entry(Route.Setting) {
                    SettingScreen()
                }
            }
        )
    }
}

@Preview
@Composable
private fun AppContentPreview() {
    AppContent()
}