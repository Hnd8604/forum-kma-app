package com.kma.base

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kma.base.model.AppTheme
import com.kma.base.screen.*
import com.kma.base.viewmodel.AuthViewModel

// Navigation Routes
object NavRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val POST_DETAIL = "post/{postId}"
    const val CREATE_POST = "create_post"
    const val NOTIFICATIONS = "notifications"
    const val FRIENDS = "friends"
    const val GROUPS = "groups"
    const val CREATE_GROUP = "create_group"
    const val PROFILE = "profile/{userId}"
    const val CHAT_DETAIL = "chat/{conversationId}/{conversationName}"
    
    fun postDetail(postId: String) = "post/$postId"
    fun profile(userId: String) = "profile/$userId"
    fun chatDetail(conversationId: String, conversationName: String) = 
        "chat/$conversationId/${java.net.URLEncoder.encode(conversationName, "UTF-8")}"
}

@Composable
fun AppNavigation(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    
    // Determine start destination based on login status
    val startDestination = if (isLoggedIn) NavRoutes.MAIN else NavRoutes.LOGIN
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login Screen
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavRoutes.MAIN) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.REGISTER)
                },
                viewModel = authViewModel
            )
        }
        
        // Register Screen
        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.MAIN) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }
        
        // Main Screen (with bottom navigation)
        composable(NavRoutes.MAIN) {
            MainScreen(
                currentTheme = currentTheme,
                onThemeSelected = onThemeSelected,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.MAIN) { inclusive = true }
                    }
                },
                onNavigateToPostDetail = { postId ->
                    navController.navigate(NavRoutes.postDetail(postId))
                },
                onNavigateToCreatePost = {
                    navController.navigate(NavRoutes.CREATE_POST)
                },
                onNavigateToNotifications = {
                    navController.navigate(NavRoutes.NOTIFICATIONS)
                },
                onNavigateToFriends = {
                    navController.navigate(NavRoutes.FRIENDS)
                },
                onNavigateToGroups = {
                    navController.navigate(NavRoutes.GROUPS)
                },
                onNavigateToChatDetail = { conversationId, conversationName ->
                    navController.navigate(NavRoutes.chatDetail(conversationId, conversationName))
                }
            )
        }
        
        // Post Detail Screen
        composable(
            route = NavRoutes.POST_DETAIL,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PostDetailScreen(
                postId = postId,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Create Post Screen
        composable(NavRoutes.CREATE_POST) {
            CreatePostScreen(
                onBackClick = { navController.popBackStack() },
                onPostCreated = { navController.popBackStack() }
            )
        }
        
        // Notifications Screen
        composable(NavRoutes.NOTIFICATIONS) {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() },
                onNotificationClick = { notification ->
                    // Navigate based on notification type
                    notification.postId?.let { postId ->
                        navController.navigate(NavRoutes.postDetail(postId))
                    }
                }
            )
        }
        
        // Friends Screen
        composable(NavRoutes.FRIENDS) {
            FriendsScreen(
                onBackClick = { navController.popBackStack() },
                onFriendClick = { userId ->
                    navController.navigate(NavRoutes.profile(userId))
                }
            )
        }
        
        // Groups Screen
        composable(NavRoutes.GROUPS) {
            GroupsScreen(
                onBackClick = { navController.popBackStack() },
                onGroupClick = { groupId ->
                    // TODO: Navigate to group detail
                },
                onCreateGroup = {
                    navController.navigate(NavRoutes.CREATE_GROUP)
                }
            )
        }
        
        // Create Group Screen
        composable(NavRoutes.CREATE_GROUP) {
            CreateGroupScreen(
                onBackClick = { navController.popBackStack() },
                onGroupCreated = { groupId ->
                    navController.popBackStack()
                    // TODO: Navigate to group detail
                }
            )
        }
        
        // Chat Detail Screen
        composable(
            route = NavRoutes.CHAT_DETAIL,
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType },
                navArgument("conversationName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            val conversationName = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("conversationName") ?: "",
                "UTF-8"
            )
            ChatDetailScreen(
                conversationId = conversationId,
                conversationName = conversationName,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Profile Screen
        composable(
            route = NavRoutes.PROFILE,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            // TODO: Profile screen with userId
            // ProfileScreen(userId = userId, onBackClick = { navController.popBackStack() })
        }
    }
}

