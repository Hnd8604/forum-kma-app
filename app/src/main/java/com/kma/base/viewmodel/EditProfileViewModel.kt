package com.kma.base.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.base.data.model.UserUpdateRequest
import com.kma.base.data.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null,
    
    // Current values
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val username: String = "",
    val dob: String = "",
    val gender: String = "",
    val address: String = "",
    val avatarUrl: String? = null,
    
    // Original values (to track changes)
    val originalFirstName: String = "",
    val originalLastName: String = "",
    val originalDob: String = "",
    val originalGender: String = "",
    val originalAddress: String = ""
) {
    val initials: String
        get() = if (firstName.isNotBlank() || lastName.isNotBlank()) {
            "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}".uppercase()
        } else {
            username.firstOrNull()?.uppercase()?.toString() ?: "U"
        }
    
    val hasChanges: Boolean
        get() = firstName != originalFirstName ||
                lastName != originalLastName ||
                dob != originalDob ||
                gender != originalGender ||
                address != originalAddress
}

class EditProfileViewModel : ViewModel() {
    private val userApi = NetworkModule.userApi
    
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()
    
    fun loadCurrentUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = userApi.getMe()
                if (response.code == "200" && response.result != null) {
                    val user = response.result
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            firstName = user.firstName ?: "",
                            lastName = user.lastName ?: "",
                            email = user.email,
                            username = user.username,
                            dob = user.dob ?: "",
                            gender = user.gender ?: "",
                            address = user.address ?: "",
                            avatarUrl = user.avatarUrl,
                            // Save originals
                            originalFirstName = user.firstName ?: "",
                            originalLastName = user.lastName ?: "",
                            originalDob = user.dob ?: "",
                            originalGender = user.gender ?: "",
                            originalAddress = user.address ?: ""
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = response.message
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("EditProfileVM", "Error loading user", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Không thể tải thông tin người dùng"
                    )
                }
            }
        }
    }
    
    fun updateFirstName(value: String) {
        _uiState.update { it.copy(firstName = value) }
    }
    
    fun updateLastName(value: String) {
        _uiState.update { it.copy(lastName = value) }
    }
    
    fun updateDob(value: String) {
        _uiState.update { it.copy(dob = value) }
    }
    
    fun updateGender(value: String) {
        _uiState.update { it.copy(gender = value) }
    }
    
    fun updateAddress(value: String) {
        _uiState.update { it.copy(address = value) }
    }
    
    fun saveProfile() {
        val state = _uiState.value
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            
            try {
                // Create update request with all editable fields
                val request = UserUpdateRequest(
                    firstName = state.firstName.ifBlank { null },
                    lastName = state.lastName.ifBlank { null },
                    dob = state.dob.ifBlank { null },
                    gender = state.gender.ifBlank { null },
                    address = state.address.ifBlank { null }
                )
                
                Log.d("EditProfileVM", "Saving profile with: $request")
                
                val response = userApi.updateProfile(request)
                if (response.code == "200") {
                    Log.d("EditProfileVM", "Profile updated successfully")
                    _uiState.update { 
                        it.copy(
                            isSaving = false,
                            saveSuccess = true,
                            // Update originals
                            originalFirstName = state.firstName,
                            originalLastName = state.lastName,
                            originalDob = state.dob,
                            originalGender = state.gender,
                            originalAddress = state.address
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isSaving = false,
                            error = response.message
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("EditProfileVM", "Error saving profile", e)
                _uiState.update { 
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Không thể lưu thay đổi"
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun resetSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}
