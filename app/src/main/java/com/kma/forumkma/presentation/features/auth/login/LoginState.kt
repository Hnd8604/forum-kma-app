package com.kma.forumkma.presentation.features.auth.login

import com.kma.forumkma.domain.model.User

/**
 * UI State cho Login Screen
 */
data class LoginState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val isLoginSuccess: Boolean = false
)

/**
 * UI Events cho Login Screen
 */
sealed class LoginEvent {
    data class UsernameChanged(val username: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    object TogglePasswordVisibility : LoginEvent()
    object LoginClicked : LoginEvent()
    object NavigateToRegister : LoginEvent()
    object ClearError : LoginEvent()
}
