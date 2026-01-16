package com.kma.base.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.base.data.model.FriendInfo
import com.kma.base.data.model.FriendRequest
import com.kma.base.data.model.FriendResponse
import com.kma.base.data.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FriendsState(
    val isLoading: Boolean = false,
    val friends: List<FriendInfo> = emptyList(),
    val requests: List<FriendRequest> = emptyList(),
    val suggestions: List<FriendInfo> = emptyList(),
    val error: String? = null,
    val actionMessage: String? = null  // Success/error message for actions
)

class FriendsViewModel : ViewModel() {
    private val friendApi = NetworkModule.friendApi
    
    private val _state = MutableStateFlow(FriendsState())
    val state: StateFlow<FriendsState> = _state.asStateFlow()
    
    init {
        loadAllData()
    }
    
    fun loadAllData() {
        loadFriends()
        loadRequests()
        loadSuggestions()
    }
    
    fun loadFriends() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val response = friendApi.getFriends()
                Log.d("FriendsVM", "getFriends response: code=${response.code}, result=${response.result}")
                if (response.code == "200" && response.result != null) {
                    val friendInfoList = response.result.map { it.toFriendInfo() }
                    _state.value = _state.value.copy(
                        friends = friendInfoList,
                        isLoading = false,
                        error = null
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = response.message ?: "Không thể tải danh sách bạn bè"
                    )
                }
            } catch (e: Exception) {
                Log.e("FriendsVM", "Error loading friends", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Lỗi kết nối"
                )
            }
        }
    }
    
    fun loadRequests() {
        viewModelScope.launch {
            try {
                val response = friendApi.getFriendRequests()
                Log.d("FriendsVM", "getFriendRequests response: code=${response.code}, result=${response.result}")
                if (response.code == "200" && response.result != null) {
                    val requestList = response.result.map { it.toFriendRequest() }
                    _state.value = _state.value.copy(requests = requestList)
                }
            } catch (e: Exception) {
                Log.e("FriendsVM", "Error loading requests", e)
            }
        }
    }
    
    fun loadSuggestions() {
        viewModelScope.launch {
            try {
                val response = friendApi.getSuggestedUsers(limit = 10)
                Log.d("FriendsVM", "getSuggestions response: code=${response.code}, result=${response.result}")
                if (response.code == "200" && response.result != null) {
                    val suggestionList = response.result.map { it.toFriendInfo() }
                    _state.value = _state.value.copy(suggestions = suggestionList)
                }
            } catch (e: Exception) {
                Log.e("FriendsVM", "Error loading suggestions", e)
            }
        }
    }
    
    fun acceptRequest(request: FriendRequest) {
        viewModelScope.launch {
            try {
                val response = friendApi.acceptFriendRequest(request.id)
                Log.d("FriendsVM", "acceptRequest response: code=${response.code}")
                if (response.code == "200") {
                    _state.value = _state.value.copy(
                        actionMessage = "Đã chấp nhận lời mời kết bạn"
                    )
                    // Reload data
                    loadFriends()
                    loadRequests()
                } else {
                    _state.value = _state.value.copy(
                        actionMessage = response.message ?: "Không thể chấp nhận lời mời"
                    )
                }
            } catch (e: Exception) {
                Log.e("FriendsVM", "Error accepting request", e)
                _state.value = _state.value.copy(
                    actionMessage = "Lỗi: ${e.message}"
                )
            }
        }
    }
    
    fun rejectRequest(request: FriendRequest) {
        viewModelScope.launch {
            try {
                val response = friendApi.rejectFriendRequest(request.id)
                Log.d("FriendsVM", "rejectRequest response: code=${response.code}")
                if (response.code == "200") {
                    _state.value = _state.value.copy(
                        actionMessage = "Đã từ chối lời mời kết bạn"
                    )
                    loadRequests()
                } else {
                    _state.value = _state.value.copy(
                        actionMessage = response.message ?: "Không thể từ chối lời mời"
                    )
                }
            } catch (e: Exception) {
                Log.e("FriendsVM", "Error rejecting request", e)
                _state.value = _state.value.copy(
                    actionMessage = "Lỗi: ${e.message}"
                )
            }
        }
    }
    
    fun sendFriendRequest(userId: String) {
        viewModelScope.launch {
            try {
                val response = friendApi.sendFriendRequest(userId)
                Log.d("FriendsVM", "sendFriendRequest response: code=${response.code}")
                if (response.code == "200") {
                    _state.value = _state.value.copy(
                        actionMessage = "Đã gửi lời mời kết bạn"
                    )
                    // Remove from suggestions
                    val updatedSuggestions = _state.value.suggestions.filter { it.userId != userId }
                    _state.value = _state.value.copy(suggestions = updatedSuggestions)
                } else {
                    _state.value = _state.value.copy(
                        actionMessage = response.message ?: "Không thể gửi lời mời"
                    )
                }
            } catch (e: Exception) {
                Log.e("FriendsVM", "Error sending request", e)
                _state.value = _state.value.copy(
                    actionMessage = "Lỗi: ${e.message}"
                )
            }
        }
    }
    
    fun removeFriend(userId: String) {
        viewModelScope.launch {
            try {
                val response = friendApi.removeFriend(userId)
                Log.d("FriendsVM", "removeFriend response: code=${response.code}")
                if (response.code == "200") {
                    _state.value = _state.value.copy(
                        actionMessage = "Đã hủy kết bạn"
                    )
                    loadFriends()
                } else {
                    _state.value = _state.value.copy(
                        actionMessage = response.message ?: "Không thể hủy kết bạn"
                    )
                }
            } catch (e: Exception) {
                Log.e("FriendsVM", "Error removing friend", e)
                _state.value = _state.value.copy(
                    actionMessage = "Lỗi: ${e.message}"
                )
            }
        }
    }
    
    fun clearMessage() {
        _state.value = _state.value.copy(actionMessage = null)
    }
    
    // Extension function to convert FriendResponse to FriendInfo
    private fun FriendResponse.toFriendInfo(): FriendInfo {
        return FriendInfo(
            friendshipId = this.id,
            userId = this.userId,
            username = this.username,
            firstName = this.firstName,
            lastName = this.lastName,
            avatarUrl = this.avatarUrl,
            status = this.status,
            isFriend = this.status == "ACCEPTED",
            mutualFriendsCount = 0
        )
    }
    
    // Extension function to convert FriendResponse to FriendRequest
    private fun FriendResponse.toFriendRequest(): FriendRequest {
        return FriendRequest(
            id = this.id,
            fromUserId = this.userId,
            fromUserName = this.fullName,
            fromUserAvatar = this.avatarUrl,
            toUserId = "",  // Not needed for display
            status = this.status,
            createdAt = this.createdAt ?: ""
        )
    }
}
