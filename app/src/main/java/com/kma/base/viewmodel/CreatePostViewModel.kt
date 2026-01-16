package com.kma.base.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

private const val TAG = "CreatePostViewModel"

data class CreatePostUiState(
    val title: String = "",
    val content: String = "",
    val selectedGroup: GroupResponse? = null,
    val selectedImages: List<Uri> = emptyList(),
    val availableGroups: List<GroupResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingGroups: Boolean = false,
    val isUploadingImages: Boolean = false,
    val uploadProgress: String = "",
    val isSuccess: Boolean = false,
    val error: String? = null
)

class CreatePostViewModel : ViewModel() {
    private val postRepository = PostRepository()
    private val groupApi = NetworkModule.groupApi
    private val fileApi = NetworkModule.fileApi
    
    private var appContext: Context? = null
    
    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()
    
    fun setContext(context: Context) {
        appContext = context.applicationContext
    }
    
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
        val context = appContext
        
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
                // Upload images first if any
                val resourceUrls = mutableListOf<String>()
                
                if (state.selectedImages.isNotEmpty() && context != null) {
                    _uiState.update { it.copy(isUploadingImages = true, uploadProgress = "Đang tải ảnh...") }
                    
                    for ((index, uri) in state.selectedImages.withIndex()) {
                        _uiState.update { 
                            it.copy(uploadProgress = "Đang tải ảnh ${index + 1}/${state.selectedImages.size}...") 
                        }
                        
                        try {
                            val url = uploadImage(context, uri)
                            if (url != null) {
                                resourceUrls.add(url)
                                Log.d(TAG, "Uploaded image: $url")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to upload image", e)
                        }
                    }
                    
                    _uiState.update { it.copy(isUploadingImages = false, uploadProgress = "") }
                }
                
                val postType = if (resourceUrls.isNotEmpty()) "IMAGE" else "TEXT"
                
                val request = CreatePostRequest(
                    title = state.title,
                    content = state.content,
                    groupId = state.selectedGroup.groupId,
                    type = postType,
                    resourceUrls = resourceUrls.ifEmpty { null }
                )
                
                Log.d(TAG, "Creating post with ${resourceUrls.size} images")
                val result = postRepository.createPost(request)
                
                result.onSuccess {
                    Log.d(TAG, "Post created successfully")
                    _uiState.update { 
                        it.copy(isLoading = false, isSuccess = true)
                    }
                }.onFailure { error ->
                    Log.e(TAG, "Failed to create post: ${error.message}")
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = error.message ?: "Không thể tạo bài viết"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating post", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Đã xảy ra lỗi"
                    )
                }
            }
        }
    }
    
    private suspend fun uploadImage(context: Context, uri: Uri): String? {
        return try {
            // Get input stream from URI
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            
            // Create temp file
            val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()
            
            // Create multipart
            val requestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", tempFile.name, requestBody)
            
            // Upload
            val response = fileApi.uploadImage(part)
            
            // Delete temp file
            tempFile.delete()
            
            if (response.code == "200" && response.result != null) {
                response.result.resourceUrl
            } else {
                Log.e(TAG, "Upload failed: ${response.message}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Upload exception", e)
            null
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
