package com.kma.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.base.data.model.PostWithInteractionResponse
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
            
            // TODO: Call API to toggle like
            // If API fails, revert the optimistic update
        }
    }
}
