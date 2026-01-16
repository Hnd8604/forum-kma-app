package com.kma.base.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.base.data.model.CreatePostRequest
import com.kma.base.data.model.GroupResponse
import com.kma.base.data.network.NetworkModule
import com.kma.base.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreatePostUiState(
    val title: String = "",
    val content: String = "",
    val selectedGroup: GroupResponse? = null,
    val selectedImages: List<Uri> = emptyList(),
    val availableGroups: List<GroupResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingGroups: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class CreatePostViewModel : ViewModel() {
    private val postRepository = PostRepository()
    private val groupApi = NetworkModule.groupApi
    
    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()
    
    init {
        loadMyGroups()
    }
    
    private fun loadMyGroups() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingGroups = true) }
            
            try {
                val response = groupApi.getMyGroups()
                if (response.code == "200" && response.result != null) {
                    _uiState.update { 
                        it.copy(
                            availableGroups = response.result,
                            isLoadingGroups = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoadingGroups = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingGroups = false) }
            }
        }
    }
    
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }
    
    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }
    
    fun selectGroup(group: GroupResponse?) {
        _uiState.update { it.copy(selectedGroup = group) }
    }
    
    fun addImages(uris: List<Uri>) {
        _uiState.update { state ->
            state.copy(
                selectedImages = (state.selectedImages + uris).take(10)
            )
        }
    }
    
    fun removeImage(uri: Uri) {
        _uiState.update { state ->
            state.copy(
                selectedImages = state.selectedImages - uri
            )
        }
    }
    
    fun createPost() {
        val state = _uiState.value
        
        // Validation
        if (state.content.isBlank()) {
            _uiState.update { it.copy(error = "Vui lòng nhập nội dung bài viết") }
            return
        }
        
        if (state.selectedGroup == null) {
            _uiState.update { it.copy(error = "Vui lòng chọn nhóm để đăng bài") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // TODO: Upload images first if any, get URLs
                val resourceUrls: List<String> = emptyList()
                
                val postType = if (state.selectedImages.isNotEmpty()) "IMAGE" else "TEXT"
                
                val request = CreatePostRequest(
                    title = state.title,
                    content = state.content,
                    groupId = state.selectedGroup.groupId,
                    type = postType,
                    resourceUrls = resourceUrls.ifEmpty { null }
                )
                
                val result = postRepository.createPost(request)
                
                result.onSuccess {
                    _uiState.update { 
                        it.copy(isLoading = false, isSuccess = true)
                    }
                }.onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = error.message ?: "Không thể tạo bài viết"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Đã xảy ra lỗi"
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
