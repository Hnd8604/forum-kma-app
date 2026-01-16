package com.kma.base.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.base.data.model.FriendResponse
import com.kma.base.data.model.PostResponse
import com.kma.base.data.model.UserResponse
import com.kma.base.data.network.NetworkModule
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "ProfileViewModel"

enum class ProfileTab {
    ABOUT, POSTS, FRIENDS
}

data class ProfileState(
    val user: UserResponse? = null,
    val posts: List<PostResponse> = emptyList(),
    val friends: List<FriendResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingPosts: Boolean = false,
    val isLoadingFriends: Boolean = false,
    val error: String? = null,
    val activeTab: ProfileTab = ProfileTab.ABOUT,
    val isOwnProfile: Boolean = true,
    val postCount: Int = 0,
    val friendCount: Int = 0
)

class ProfileViewModel : ViewModel() {
    private val userApi = NetworkModule.userApi
    private val postApi = NetworkModule.postApi
    private val friendApi = NetworkModule.friendApi
    private val tokenManager = NetworkModule.getTokenManager()
    
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()
    
    private var currentUserId: String? = null
    
    init {
        loadCurrentUser()
    }
    
    /**
     * Load current logged-in user profile (own profile)
     */
    fun loadCurrentUser() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, isOwnProfile = true) }
            
            try {
                currentUserId = tokenManager.getUserIdSync()
                
                val response = userApi.getMe()
                if (response.code == "200" && response.result != null) {
                    _state.update {
                        it.copy(
                            user = response.result,
                            isLoading = false,
                            isOwnProfile = true
                        )
                    }
                    
                    // Load posts and friends in parallel
                    loadUserPosts(response.result.id)
                    loadFriends()
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = response.message
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load profile"
                    )
                }
            }
        }
    }
    
    /**
     * Load another user's profile by ID
     */
    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                currentUserId = tokenManager.getUserIdSync()
                val isOwn = userId == currentUserId
                
                val response = if (isOwn) {
                    userApi.getMe()
                } else {
                    userApi.getUserById(userId)
                }
                
                if (response.code == "200" && response.result != null) {
                    _state.update {
                        it.copy(
                            user = response.result,
                            isLoading = false,
                            isOwnProfile = isOwn
                        )
                    }
                    
                    loadUserPosts(userId)
                    if (isOwn) {
                        loadFriends()
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = response.message
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load profile"
                    )
                }
            }
        }
    }
    
    /**
     * Load user's posts
     */
    private fun loadUserPosts(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingPosts = true) }
            Log.d(TAG, "Loading posts for user: $userId")
            
            try {
                val response = postApi.getPostsByAuthor(userId, page = 0, limit = 20)
                Log.d(TAG, "Posts response code: ${response.code}, message: ${response.message}")
                if (response.code == "200" && response.result != null) {
                    val posts = response.result.content
                    // Use content.size if total is 0 (backend bug workaround)
                    val postCount = if (response.result.total > 0) response.result.total.toInt() else posts.size
                    Log.d(TAG, "Loaded ${posts.size} posts, total from API: ${response.result.total}, using count: $postCount")
                    _state.update {
                        it.copy(
                            posts = posts,
                            postCount = postCount,
                            isLoadingPosts = false
                        )
                    }
                } else {
                    Log.w(TAG, "Posts response failed: ${response.message}")
                    _state.update { it.copy(isLoadingPosts = false) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading posts", e)
                _state.update { it.copy(isLoadingPosts = false) }
            }
        }
    }
    
    /**
     * Load friends list
     */
    private fun loadFriends() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingFriends = true) }
            Log.d(TAG, "Loading friends list")
            
            try {
                val response = friendApi.getFriends()
                Log.d(TAG, "Friends response code: ${response.code}, message: ${response.message}")
                if (response.code == "200" && response.result != null) {
                    Log.d(TAG, "Loaded ${response.result.size} friends total")
                    val friends = response.result.filter { it.status == "ACCEPTED" }
                    Log.d(TAG, "Filtered ${friends.size} accepted friends")
                    _state.update {
                        it.copy(
                            friends = friends,
                            friendCount = friends.size,
                            isLoadingFriends = false
                        )
                    }
                } else {
                    Log.w(TAG, "Friends response failed: ${response.message}")
                    _state.update { it.copy(isLoadingFriends = false) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading friends", e)
                _state.update { it.copy(isLoadingFriends = false) }
            }
        }
    }
    
    /**
     * Change active tab
     */
    fun setActiveTab(tab: ProfileTab) {
        _state.update { it.copy(activeTab = tab) }
    }
    
    /**
     * Get display name
     */
    fun getDisplayName(): String {
        val user = _state.value.user ?: return ""
        return if (!user.firstName.isNullOrBlank() && !user.lastName.isNullOrBlank()) {
            "${user.lastName} ${user.firstName}"
        } else {
            user.username
        }
    }
    
    /**
     * Get initials for avatar
     */
    fun getInitials(): String {
        val user = _state.value.user ?: return "U"
        return if (!user.firstName.isNullOrBlank() && !user.lastName.isNullOrBlank()) {
            "${user.lastName.first()}${user.firstName.first()}".uppercase()
        } else {
            user.username.take(2).uppercase()
        }
    }
    
    /**
     * Get gender display text
     */
    fun getGenderText(): String? {
        return when (_state.value.user?.gender) {
            "MALE" -> "Nam"
            "FEMALE" -> "Nữ"
            "OTHER" -> "Khác"
            else -> null
        }
    }
    
    /**
     * Refresh profile data
     */
    fun refresh() {
        val user = _state.value.user
        if (user != null) {
            loadUserProfile(user.id)
        } else {
            loadCurrentUser()
        }
    }
}
