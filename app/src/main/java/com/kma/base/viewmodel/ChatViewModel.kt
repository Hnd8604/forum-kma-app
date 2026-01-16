package com.kma.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.base.data.model.ChatMessageResponse
import com.kma.base.data.model.ConversationResponse
import com.kma.base.data.model.SendMessageRequest
import com.kma.base.data.network.NetworkModule
import com.kma.base.data.websocket.ChatMessageWS
import com.kma.base.data.websocket.ChatWebSocketManager
import com.kma.base.data.websocket.ConnectionState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ConversationsState(
    val conversations: List<ConversationResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ChatDetailState(
    val messages: List<ChatMessageResponse> = emptyList(),
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val error: String? = null,
    val conversation: ConversationResponse? = null,
    val currentPage: Int = 0,
    val hasMore: Boolean = true
)

class ChatViewModel : ViewModel() {
    private val chatApi = NetworkModule.chatApi
    private val tokenManager = NetworkModule.getTokenManager()
    
    // WebSocket Manager
    private val webSocketManager = ChatWebSocketManager(tokenManager)
    
    // Conversations list state
    private val _conversationsState = MutableStateFlow(ConversationsState())
    val conversationsState: StateFlow<ConversationsState> = _conversationsState.asStateFlow()
    
    // Current chat detail state
    private val _chatDetailState = MutableStateFlow(ChatDetailState())
    val chatDetailState: StateFlow<ChatDetailState> = _chatDetailState.asStateFlow()
    
    // WebSocket connection state
    val connectionState: StateFlow<ConnectionState> = webSocketManager.connectionState
    
    // Current user ID
    private var currentUserId: String? = null
    
    // Current conversation ID (for filtering incoming messages)
    private var currentConversationId: String? = null
    
    init {
        // Load current user ID
        viewModelScope.launch {
            currentUserId = tokenManager.getUserIdSync()
        }
        
        // Listen for incoming chat messages from WebSocket
        viewModelScope.launch {
            webSocketManager.chatMessages.collect { wsMessage ->
                handleIncomingMessage(wsMessage)
            }
        }
    }
    
    /**
     * Connect to WebSocket
     */
    fun connectWebSocket() {
        viewModelScope.launch {
            webSocketManager.connect()
        }
    }
    
    /**
     * Disconnect WebSocket
     */
    fun disconnectWebSocket() {
        webSocketManager.disconnect()
    }
    
    /**
     * Load all conversations
     */
    fun loadConversations() {
        viewModelScope.launch {
            _conversationsState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = chatApi.getConversations()
                if (response.code == "200" && response.result != null) {
                    _conversationsState.update {
                        it.copy(
                            conversations = response.result,
                            isLoading = false
                        )
                    }
                } else {
                    _conversationsState.update {
                        it.copy(
                            isLoading = false,
                            error = response.message
                        )
                    }
                }
            } catch (e: Exception) {
                _conversationsState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load conversations"
                    )
                }
            }
        }
    }
    
    /**
     * Load messages for a conversation
     */
    fun loadMessages(conversationId: String, loadMore: Boolean = false) {
        currentConversationId = conversationId
        
        viewModelScope.launch {
            val currentState = _chatDetailState.value
            val page = if (loadMore) currentState.currentPage + 1 else 0
            
            _chatDetailState.update { 
                it.copy(
                    isLoading = true,
                    error = null,
                    currentPage = page
                )
            }
            
            try {
                val response = chatApi.getMessages(conversationId, page = page)
                if (response.code == "200" && response.result != null) {
                    val pageResponse = response.result
                    val newMessages = pageResponse.content
                    
                    _chatDetailState.update { state ->
                        val allMessages = if (loadMore) {
                            state.messages + newMessages
                        } else {
                            newMessages
                        }
                        
                        state.copy(
                            messages = allMessages.sortedBy { it.createdAt },
                            isLoading = false,
                            hasMore = pageResponse.page < pageResponse.totalPages - 1
                        )
                    }
                    
                    // Mark as read
                    markAsRead(conversationId)
                } else {
                    _chatDetailState.update {
                        it.copy(
                            isLoading = false,
                            error = response.message
                        )
                    }
                }
            } catch (e: Exception) {
                _chatDetailState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load messages"
                    )
                }
            }
        }
    }
    
    /**
     * Send a message
     */
    fun sendMessage(
        conversationId: String? = null,
        receiverId: String? = null,
        groupId: String? = null,
        message: String,
        type: String = "TEXT",
        resourceUrls: List<String>? = null
    ) {
        viewModelScope.launch {
            _chatDetailState.update { it.copy(isSending = true) }
            
            try {
                val request = SendMessageRequest(
                    conversationId = conversationId,
                    receiverId = receiverId,
                    groupId = groupId,
                    message = message,
                    type = type,
                    resourceUrls = resourceUrls
                )
                
                val response = chatApi.sendMessage(request)
                if (response.code == "200" && response.result != null) {
                    // Add sent message to the list
                    val sentMessage = response.result
                    _chatDetailState.update { state ->
                        state.copy(
                            messages = state.messages + sentMessage,
                            isSending = false
                        )
                    }
                    
                    // Update conversation list with new message
                    updateConversationLastMessage(
                        conversationId ?: sentMessage.conversationId ?: "",
                        message
                    )
                } else {
                    _chatDetailState.update {
                        it.copy(
                            isSending = false,
                            error = response.message
                        )
                    }
                }
            } catch (e: Exception) {
                _chatDetailState.update {
                    it.copy(
                        isSending = false,
                        error = e.message ?: "Failed to send message"
                    )
                }
            }
        }
    }
    
    /**
     * Mark conversation as read
     */
    private fun markAsRead(conversationId: String) {
        viewModelScope.launch {
            try {
                chatApi.markAsRead(conversationId)
                
                // Update unread count in conversations list
                _conversationsState.update { state ->
                    state.copy(
                        conversations = state.conversations.map { conv ->
                            if (conv.id == conversationId) {
                                val updatedUnreadCounts = conv.unreadCounts?.toMutableMap()?.apply {
                                    currentUserId?.let { put(it, 0) }
                                }
                                conv.copy(unreadCounts = updatedUnreadCounts)
                            } else {
                                conv
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                // Ignore error
            }
        }
    }
    
    /**
     * Handle incoming WebSocket message
     */
    private fun handleIncomingMessage(wsMessage: ChatMessageWS) {
        val conversationId = wsMessage.conversationId ?: wsMessage.chatId ?: return
        
        // If we're viewing this conversation, add the message
        if (conversationId == currentConversationId) {
            val newMessage = ChatMessageResponse(
                id = wsMessage.messageId ?: "",
                fromUserId = wsMessage.senderId ?: "",
                toUserId = wsMessage.receiverId,
                groupId = null,
                conversationId = conversationId,
                message = wsMessage.message,
                type = wsMessage.messageType ?: "TEXT",
                resourceUrls = wsMessage.resourceUrls,
                createdAt = wsMessage.sentAt ?: wsMessage.createdAt,
                senderName = wsMessage.senderName
            )
            
            _chatDetailState.update { state ->
                // Avoid duplicates
                if (state.messages.none { it.id == newMessage.id }) {
                    state.copy(messages = state.messages + newMessage)
                } else {
                    state
                }
            }
        }
        
        // Update conversation list
        updateConversationLastMessage(conversationId, wsMessage.message ?: "")
        incrementUnreadCount(conversationId)
    }
    
    /**
     * Update last message in conversation list
     */
    private fun updateConversationLastMessage(conversationId: String, message: String) {
        _conversationsState.update { state ->
            val updatedConversations = state.conversations.map { conv ->
                if (conv.id == conversationId) {
                    conv.copy(
                        lastMessage = message,
                        lastMessageAt = java.time.Instant.now().toString()
                    )
                } else {
                    conv
                }
            }.sortedByDescending { it.lastMessageAt }
            
            state.copy(conversations = updatedConversations)
        }
    }
    
    /**
     * Increment unread count for a conversation
     */
    private fun incrementUnreadCount(conversationId: String) {
        // Only increment if not viewing this conversation
        if (conversationId == currentConversationId) return
        
        _conversationsState.update { state ->
            state.copy(
                conversations = state.conversations.map { conv ->
                    if (conv.id == conversationId) {
                        val updatedUnreadCounts = conv.unreadCounts?.toMutableMap()?.apply {
                            currentUserId?.let { 
                                put(it, (get(it) ?: 0) + 1) 
                            }
                        }
                        conv.copy(unreadCounts = updatedUnreadCounts)
                    } else {
                        conv
                    }
                }
            )
        }
    }
    
    /**
     * Set current conversation for detail view
     */
    fun setCurrentConversation(conversation: ConversationResponse) {
        currentConversationId = conversation.id
        _chatDetailState.update {
            ChatDetailState(conversation = conversation)
        }
    }
    
    /**
     * Clear current conversation when leaving detail view
     */
    fun clearCurrentConversation() {
        currentConversationId = null
        _chatDetailState.update { ChatDetailState() }
    }
    
    /**
     * Get current user ID
     */
    fun getCurrentUserId(): String? = currentUserId
    
    override fun onCleared() {
        super.onCleared()
        disconnectWebSocket()
    }
}
