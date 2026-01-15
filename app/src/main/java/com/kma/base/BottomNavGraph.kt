package com.kma.base

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kma.base.model.AppTheme
import com.kma.base.screen.HomeScreen
import com.kma.base.screen.MessagesScreen
import com.kma.base.screen.ProfileScreen
import com.kma.base.screen.SettingsScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    startDestination: NavScreen,
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onLogout: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() }
    ) {
        composable<NavScreen.Home> {
            HomeScreen()
        }
        composable<NavScreen.Messages> {
            MessagesScreen()
        }
        composable<NavScreen.Profile> {
            ProfileScreen(onLogout = onLogout)
        }
        composable<NavScreen.Settings> {
            SettingsScreen(
                currentTheme = currentTheme,
                onThemeSelected = onThemeSelected
            )
        }
    }
}
