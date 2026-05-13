package com.project.composeproject.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.project.composeproject.ui.screen.language.LanguageScreen
import com.project.composeproject.ui.screen.onboarding.OnboardingScreen

@Composable
fun AppNavigation() {
    Box {
        val backStack = rememberNavBackStack(Route.Language(fromSetting = false))
        NavDisplay(
            backStack = backStack,
            transitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(300),
                    initialOffsetX = { it }
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(300),
                    targetOffsetX = { -it }
                )
            },
            popTransitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(300),
                    initialOffsetX = { -it }
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(300),
                    targetOffsetX = { it }
                )
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Route.Language> { key ->
                    LanguageScreen(
                        fromSetting = key.fromSetting,
                        onNavigateBack = {
                            backStack.removeAt(backStack.size - 1)
                        },
                        onNavigateToOnboardingScreen = {
                            backStack.add(Route.Onboarding)
                        }
                    )
                }

                entry<Route.Onboarding> {
                    OnboardingScreen()
                }
            }
        )
    }
}

@Preview
@Composable
private fun AppNavigationPreview() {
    AppNavigation()
}