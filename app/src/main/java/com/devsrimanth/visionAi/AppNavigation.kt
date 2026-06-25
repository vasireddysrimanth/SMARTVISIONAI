package com.devsrimanth.visionAi

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.devsrimanth.visionAi.screens.CameraScreen
import com.devsrimanth.visionAi.screens.OnboardingScreen
import com.devsrimanth.visionAi.screens.SplashScreen
import com.devsrimanth.visionAi.utils.UserPreferences
import kotlinx.coroutines.launch

/**
 * Controls which screen to show based on app state:
 * Splash → Onboarding (first time only) → Camera
 */

private enum class AppScreen {
    SPLASH,
    ONBOARDING,
    CAMERA
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferences = remember { UserPreferences(context) }

    /** Track current screen */
    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

    /** Load onboarding status from DataStore */
    val hasSeenOnboarding by userPreferences.hasSeenOnboarding
        .collectAsState(initial = false)

    when (currentScreen) {

        AppScreen.SPLASH -> {
            SplashScreen(
                onSplashComplete = {
                    /** After splash — go to onboarding or camera */
                    currentScreen = if (hasSeenOnboarding) {
                        AppScreen.CAMERA
                    } else {
                        AppScreen.ONBOARDING
                    }
                }
            )
        }

        AppScreen.ONBOARDING -> {
            OnboardingScreen(
                onOnboardingComplete = {
                    scope.launch {
                        /** Save flag so onboarding never shows again */
                        userPreferences.setOnboardingComplete()
                    }
                    currentScreen = AppScreen.CAMERA
                }
            )
        }

        AppScreen.CAMERA -> {
            CameraScreen()
        }
    }
}