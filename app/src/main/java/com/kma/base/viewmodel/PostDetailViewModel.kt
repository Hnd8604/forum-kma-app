package com.kma.base.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.base.data.model.CommentResponse
import com.kma.base.data.model.PostWithInteractionResponse
import com.kma.base.data.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostDetailUiState(
        val post: PostWithInteractionResponse? = null,
        val comments: List<CommentResponse> = emptyList(),
        val isLoading: Boolean = false,
        val isLoadingComments: Boolean = false,
        val isSendingComment: Boolean = false,
        val error: String? = null,
        val userReaction: String? = null,
        val currentUserAvatarUrl: String? = null
)

class PostDetailViewModel : ViewModel() {
    private val postApi = NetworkModule.postApi
    private val commentApi = NetworkModule.commentApi
    private val interactionApi = NetworkModule.interactionApi
    private val userApi = NetworkModule.userApi

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val response = userApi.getMe()
                if (response.code == "200" && response.result != null) {
                    _uiState.update { it.copy(currentUserAvatarUrl = response.result.avatarUrl) }
                }
            } catch (e: Exception) {
                Log.e("PostDetailVM", "Error loading current user", e)
            }
        }
    }

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val response = postApi.getPostById(postId)
                if (response.code == "200" && response.result != null) {
                    // Convert PostResponse to PostWithInteractionResponse
                    val postResult = response.result
                    val postWithInteraction =
                            PostWithInteractionResponse(
                                    id = postResult.id,
                                    title = postResult.title,
                                    content = postResult.content,
                                    authorId = postResult.authorId,
                                    authorName = postResult.authorName,
                                    groupId = postResult.groupId,
                                    groupName = postResult.groupName,
                                    type = postResult.type,
                                    resourceUrls = postResult.resourceUrls,
                                    reactionCount = 0,
                                    commentCount = 0,
                                    userReaction = null,
                                    createdAt = postResult.createdAt,
                                    updatedAt = postResult.updatedAt
                            )

                    _uiState.update {
                        it.copy(
                                post = postWithInteraction,
                                isLoading = false,
                                userReaction = postWithInteraction.userReaction
                        )
                    }

                    // Load comments
                    loadComments(postId)
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Không thể tải bài viết")
                }
            }
        }
    }

    private fun loadComments(postId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingComments = true) }

            try {
                val response = commentApi.getCommentsByPost(postId)
                if (response.code == "200" && response.result != null) {
                    _uiState.update {
                        it.copy(comments = response.result.content, isLoadingComments = false)
                    }
                } else {
                    _uiState.update { it.copy(isLoadingComments = false) }
                }
            } catch (e: Exception) {
                android.util.Log.e("PostDetailVM", "Error loading comments", e)
                _uiState.update { it.copy(isLoadingComments = false) }
            }
        }
    }

    fun sendComment(postId: String, content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSendingComment = true) }

            try {
                val request = mapOf("postId" to postId, "content" to content)
                val response = commentApi.createComment(request)
                if (response.code == "200") {
                    // Reload comments
                    loadComments(postId)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSendingComment = false, error = e.message) }
            }

            _uiState.update { it.copy(isSendingComment = false) }
        }
    }

    fun toggleReaction(postId: String) {
        viewModelScope.launch {
            val currentReaction = _uiState.value.userReaction

            try {
                if (currentReaction != null) {
                    // Remove reaction
                    interactionApi.removeReaction(postId)
                    _uiState.update { state ->
                        state.copy(
                                userReaction = null,
                                post =
                                        state.post?.copy(
                                                reactionCount =
                                                        (state.post.reactionCount - 1)
                                                                .coerceAtLeast(0)
                                        )
                        )
                    }
                } else {
                    // Add reaction
                    interactionApi.reactToPost(mapOf("postId" to postId, "type" to "LIKE"))
                    _uiState.update { state ->
                        state.copy(
                                userReaction = "LIKE",
                                post =
                                        state.post?.copy(
                                                reactionCount = state.post.reactionCount + 1
                                        )
                        )
                    }
                }
            } catch (e: Exception) {
                // Revert on error - do nothing for now
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
