package com.kma.base.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.base.data.model.PostWithInteractionResponse
import com.kma.base.data.network.NetworkModule
import com.kma.base.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val posts: List<PostWithInteractionResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val hasMore: Boolean = true
)

class HomeViewModel : ViewModel() {
    private val postRepository = PostRepository()
    private val interactionApi = NetworkModule.interactionApi
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun loadPosts(refresh: Boolean = false) {
        if (_uiState.value.isLoading) return
        
        val page = if (refresh) 0 else _uiState.value.currentPage
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = postRepository.getFeed(page = page)
            
            result.onSuccess { pageResponse ->
                _uiState.update { state ->
                    val newPosts = if (refresh) {
                        pageResponse.content
                    } else {
                        state.posts + pageResponse.content
                    }
                    
                    state.copy(
                        posts = newPosts,
                        isLoading = false,
                        currentPage = page + 1,
                        hasMore = pageResponse.content.isNotEmpty() && page < pageResponse.totalPages - 1,
                        error = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Không thể tải bài viết"
                    )
                }
            }
        }
    }
    
    fun loadMorePosts() {
        if (!_uiState.value.hasMore || _uiState.value.isLoading) return
        loadPosts(refresh = false)
    }
    
    fun refreshPosts() {
        loadPosts(refresh = true)
    }
    
    fun toggleLike(postId: String, isCurrentlyLiked: Boolean) {
        viewModelScope.launch {
            // Optimistic update
            _uiState.update { state ->
                state.copy(
                    posts = state.posts.map { post ->
                        if (post.id == postId) {
                            post.copy(
                                reactionCount = if (isCurrentlyLiked) post.reactionCount - 1 else post.reactionCount + 1,
                                userReaction = if (isCurrentlyLiked) null else "LIKE"
                            )
                        } else post
                    }
                )
            }
            
            try {
                if (isCurrentlyLiked) {
                    // Remove like
                    val response = interactionApi.removeReaction(postId)
                    Log.d("HomeVM", "Remove like response: ${response.code}")
                    if (response.code != "200") {
                        // Revert if failed
                        revertLike(postId, isCurrentlyLiked)
                    }
                } else {
                    // Add like
                    val response = interactionApi.reactToPost(mapOf(
                        "postId" to postId,
                        "type" to "LIKE"
                    ))
                    Log.d("HomeVM", "Add like response: ${response.code}")
                    if (response.code != "200") {
                        // Revert if failed
                        revertLike(postId, isCurrentlyLiked)
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeVM", "Error toggling like", e)
                // Revert on error
                revertLike(postId, isCurrentlyLiked)
            }
        }
    }
    
    private fun revertLike(postId: String, wasLiked: Boolean) {
        _uiState.update { state ->
            state.copy(
                posts = state.posts.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            reactionCount = if (wasLiked) post.reactionCount + 1 else post.reactionCount - 1,
                            userReaction = if (wasLiked) "LIKE" else null
                        )
                    } else post
                }
            )
        }
    }
}
