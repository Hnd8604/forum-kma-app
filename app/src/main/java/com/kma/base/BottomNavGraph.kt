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
    onLogout: () -> Unit = {},
    onNavigateToPostDetail: (String) -> Unit = {},
    onNavigateToCreatePost: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToFriends: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToChatDetail: (conversationId: String, conversationName: String) -> Unit = { _, _ -> }
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
            HomeScreen(
                onPostClick = onNavigateToPostDetail,
                onCreatePostClick = onNavigateToCreatePost,
                onNotificationClick = onNavigateToNotifications,
                onFriendsClick = onNavigateToFriends
            )
        }
        composable<NavScreen.Messages> {
            MessagesScreen(
                onChatClick = onNavigateToChatDetail
            )
        }
        composable<NavScreen.Profile> {
            ProfileScreen(
                onNavigateToEditProfile = onNavigateToEditProfile
            )
        }
        composable<NavScreen.Settings> {
            SettingsScreen(
                currentTheme = currentTheme,
                onThemeSelected = onThemeSelected,
                onLogout = onLogout
            )
        }
    }
}
