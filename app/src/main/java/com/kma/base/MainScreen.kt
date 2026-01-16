package com.kma.base

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kma.base.model.AppTheme
import com.kma.base.model.BottomNavBarItem

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onLogout: () -> Unit = {},
    onNavigateToPostDetail: (String) -> Unit = {},
    onNavigateToCreatePost: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToFriends: () -> Unit = {},
    onNavigateToGroups: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToChatDetail: (conversationId: String, conversationName: String) -> Unit = { _, _ -> }
) {
    val navController = rememberNavController()
    val startDestination = NavScreen.Home

    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) {
        BottomNavGraph(
            startDestination = startDestination,
            navController = navController,
            currentTheme = currentTheme,
            onThemeSelected = onThemeSelected,
            onLogout = onLogout,
            onNavigateToPostDetail = onNavigateToPostDetail,
            onNavigateToCreatePost = onNavigateToCreatePost,
            onNavigateToNotifications = onNavigateToNotifications,
            onNavigateToFriends = onNavigateToFriends,
            onNavigateToEditProfile = onNavigateToEditProfile,
            onNavigateToChatDetail = onNavigateToChatDetail
        )
    }
}


@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomNavBarItem.Home,
        BottomNavBarItem.Messages,
        BottomNavBarItem.Profile,
        BottomNavBarItem.Settings,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavBarItem,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        label = {
            Text(text = stringResource(id = screen.title))
        },
        icon = {
            val iconVector = screen.icon
            val iconResource = screen.iconRes
            when {
                iconVector != null -> {
                    Icon(imageVector = iconVector, contentDescription = "Navigation Icon")
                }
                iconResource != null -> {
                    Icon(painter = painterResource(id = iconResource), contentDescription = "Navigation Icon")
                }
            }
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route::class.qualifiedName
        } == true,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            selectedTextColor = Color.White,

            unselectedIconColor = Color.Gray,
            unselectedTextColor = Color.Gray
        )
    )
}