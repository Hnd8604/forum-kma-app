package com.kma.forumkma.presentation.features.auth.login

import androidx.lifecycle.viewModelScope
import com.kma.forumkma.core.base.BaseViewModel
import com.kma.forumkma.core.base.Resource
import com.kma.forumkma.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.launch

/**
 * ViewModel cho Login Screen
 * 
 * Note: Add @HiltViewModel and @Inject when Hilt is configured
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginState, LoginEvent>(LoginState()) {
    
    override fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UsernameChanged -> {
                updateState { 
                    copy(
                        username = event.username,
                        usernameError = null
                    )
                }
            }
            
            is LoginEvent.PasswordChanged -> {
                updateState { 
                    copy(
                        password = event.password,
                        passwordError = null
                    )
                }
            }
            
            is LoginEvent.TogglePasswordVisibility -> {
                updateState { 
                    copy(isPasswordVisible = !isPasswordVisible)
                }
            }
            
            is LoginEvent.LoginClicked -> {
                login()
            }
            
            is LoginEvent.NavigateToRegister -> {
                // Navigation sẽ được xử lý ở Screen layer
            }
            
            is LoginEvent.ClearError -> {
                updateState { copy(error = null) }
            }
        }
    }
    
    private fun login() {
        val currentState = uiState.value
        
        // Validation
        if (currentState.username.isBlank()) {
            updateState { copy(usernameError = "Username không được để trống") }
            return
        }
        
        if (currentState.password.isBlank()) {
            updateState { copy(passwordError = "Password không được để trống") }
            return
        }
        
        // Call use case
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            
            val params = LoginUseCase.Params(
                username = currentState.username,
                password = currentState.password
            )
            
            when (val result = loginUseCase(params)) {
                is Resource.Success -> {
                    updateState { 
                        copy(
                            isLoading = false,
                            user = result.data,
                            isLoginSuccess = true,
                            error = null
                        )
                    }
                }
                
                is Resource.Error -> {
                    updateState { 
                        copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                
                is Resource.Loading -> {
                    updateState { copy(isLoading = true) }
                }
            }
        }
    }
}
