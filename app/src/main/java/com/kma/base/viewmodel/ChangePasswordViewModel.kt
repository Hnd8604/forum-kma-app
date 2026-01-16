package com.kma.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.base.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State cho màn hình đổi mật khẩu
 */
sealed class ChangePasswordUiState {
    object Idle : ChangePasswordUiState()
    object Loading : ChangePasswordUiState()
    data class Success(val message: String) : ChangePasswordUiState()
    data class Error(val message: String) : ChangePasswordUiState()
}

/**
 * ViewModel cho chức năng đổi mật khẩu
 */
class ChangePasswordViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    
    private val _uiState = MutableStateFlow<ChangePasswordUiState>(ChangePasswordUiState.Idle)
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()
    
    /**
     * Đổi mật khẩu trực tiếp
     */
    fun changePassword(oldPassword: String, newPassword: String) {
        // Validate input
        if (oldPassword.isBlank()) {
            _uiState.value = ChangePasswordUiState.Error("Vui lòng nhập mật khẩu hiện tại")
            return
        }
        
        if (newPassword.isBlank()) {
            _uiState.value = ChangePasswordUiState.Error("Vui lòng nhập mật khẩu mới")
            return
        }
        
        if (newPassword.length < 6) {
            _uiState.value = ChangePasswordUiState.Error("Mật khẩu mới phải có ít nhất 6 ký tự")
            return
        }
        
        if (oldPassword == newPassword) {
            _uiState.value = ChangePasswordUiState.Error("Mật khẩu mới phải khác mật khẩu cũ")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = ChangePasswordUiState.Loading
            
            val result = authRepository.changePassword(oldPassword, newPassword)
            
            result.onSuccess { message ->
                _uiState.value = ChangePasswordUiState.Success(
                    message.ifBlank { "Đổi mật khẩu thành công!" }
                )
            }.onFailure { error ->
                _uiState.value = ChangePasswordUiState.Error(
                    error.message ?: "Mật khẩu hiện tại không chính xác"
                )
            }
        }
    }
    
    /**
     * Reset state
     */
    fun reset() {
        _uiState.value = ChangePasswordUiState.Idle
    }
}
