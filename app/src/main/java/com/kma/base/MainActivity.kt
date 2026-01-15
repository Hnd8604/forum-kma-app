package com.kma.base

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.edit
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kma.base.data.network.NetworkModule
import com.kma.base.model.AppTheme
import com.kma.base.screen.OnboardingScreen
import com.kma.base.ui.theme.BaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize NetworkModule
        NetworkModule.initialize(this)
        
        // Read theme preference before splash screen
        val sharedPreferences = getSharedPreferences(
            "app_preferences",
            MODE_PRIVATE
        )
        val themeMode = sharedPreferences.getString("theme_mode", "system") ?: "system"

        // Install splash screen before super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Customize splash screen animation
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView.iconView,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.iconView.height.toFloat()
            )
            slideUp.interpolator = AccelerateDecelerateInterpolator()
            slideUp.duration = 500L

            val fadeOut = ObjectAnimator.ofFloat(
                splashScreenView.view,
                View.ALPHA,
                1f,
                0f
            )
            fadeOut.interpolator = AccelerateDecelerateInterpolator()
            fadeOut.duration = 500L

            fadeOut.doOnEnd { splashScreenView.remove() }

            slideUp.start()
            fadeOut.start()
        }

        val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)

        setContent {
            var currentTheme by remember {
                mutableStateOf(
                    when (themeMode) {
                        "dark" -> AppTheme.DARK
                        "light" -> AppTheme.LIGHT
                        else -> AppTheme.SYSTEM
                    }
                )
            }

            var showOnboarding by remember { mutableStateOf(isFirstLaunch) }

            BaseTheme(userTheme = currentTheme) {
                when {
                    showOnboarding -> {
                        OnboardingScreen(
                            currentTheme = currentTheme,
                            onThemeSelected = { theme ->
                                val themeStr = when (theme) {
                                    AppTheme.DARK -> "dark"
                                    AppTheme.LIGHT -> "light"
                                    AppTheme.SYSTEM -> "system"
                                }
                                sharedPreferences.edit {
                                    putString("theme_mode", themeStr)
                                }
                                currentTheme = theme
                            },
                            onFinish = {
                                sharedPreferences.edit {
                                    putBoolean("is_first_launch", false)
                                }
                                showOnboarding = false
                            }
                        )
                    }

                    else -> {
                        AppNavigation(
                            currentTheme = currentTheme,
                            onThemeSelected = { theme ->
                                val themeStr = when (theme) {
                                    AppTheme.DARK -> "dark"
                                    AppTheme.LIGHT -> "light"
                                    AppTheme.SYSTEM -> "system"
                                }
                                sharedPreferences.edit {
                                    putString("theme_mode", themeStr)
                                }
                                currentTheme = theme
                            }
                        )
                    }
                }
            }
        }
    }
}