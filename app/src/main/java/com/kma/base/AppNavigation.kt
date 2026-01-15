package com.kma.base

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kma.base.model.AppTheme
import com.kma.base.screen.LoginScreen
import com.kma.base.screen.RegisterScreen
import com.kma.base.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    
    // Determine start destination based on login status
    val startDestination = if (isLoggedIn) "main" else "login"
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login Screen
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                viewModel = authViewModel
            )
        }
        
        // Register Screen
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }
        
        // Main Screen (with bottom navigation)
        composable("main") {
            MainScreen(
                currentTheme = currentTheme,
                onThemeSelected = onThemeSelected,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}
