package com.kma.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.base.data.model.AuthResponse
import com.kma.base.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val authResponse: AuthResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    init {
        checkLoginStatus()
    }
    
    private fun checkLoginStatus() {
        viewModelScope.launch {
            _isLoggedIn.value = authRepository.isLoggedIn()
        }
    }
    
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val result = authRepository.login(username, password)
            
            result.onSuccess { authResponse ->
                _authState.value = AuthState.Success(authResponse)
                _isLoggedIn.value = true
            }.onFailure { error ->
                _authState.value = AuthState.Error(
                    error.message ?: "Đăng nhập thất bại"
                )
            }
        }
    }
    
    fun register(
        username: String,
        email: String,
        password: String,
        firstName: String?,
        lastName: String?
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val result = authRepository.register(username, email, password, firstName, lastName)
            
            result.onSuccess { authResponse ->
                _authState.value = AuthState.Success(authResponse)
                _isLoggedIn.value = true
            }.onFailure { error ->
                _authState.value = AuthState.Error(
                    error.message ?: "Đăng ký thất bại"
                )
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isLoggedIn.value = false
            _authState.value = AuthState.Idle
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
