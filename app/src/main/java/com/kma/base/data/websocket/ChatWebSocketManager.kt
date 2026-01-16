package com.kma.base.data.websocket

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.kma.base.data.local.TokenManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.*
import java.util.concurrent.TimeUnit

/**
 * WebSocket Manager for real-time chat and notifications
 * Connects to: ws://72.60.198.235:8090/ws?userId={userId}
 */
class ChatWebSocketManager(
    private val tokenManager: TokenManager
) {
    companion object {
        private const val TAG = "ChatWebSocket"
        private const val WS_BASE_URL = "ws://72.60.198.235:8090"
        private const val RECONNECT_DELAY_MS = 3000L
        private const val MAX_RECONNECT_ATTEMPTS = 5
    }

    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private var webSocket: WebSocket? = null
    private var client: OkHttpClient? = null
    private var reconnectAttempts = 0
    private var isManuallyDisconnected = false
    
    // Connection state
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    // Incoming messages
    private val _incomingMessages = MutableSharedFlow<WebSocketMessage>(extraBufferCapacity = 100)
    val incomingMessages: SharedFlow<WebSocketMessage> = _incomingMessages.asSharedFlow()
    
    // Chat messages specifically
    private val _chatMessages = MutableSharedFlow<ChatMessageWS>(extraBufferCapacity = 100)
    val chatMessages: SharedFlow<ChatMessageWS> = _chatMessages.asSharedFlow()
    
    // Notifications
    private val _notifications = MutableSharedFlow<NotificationWS>(extraBufferCapacity = 100)
    val notifications: SharedFlow<NotificationWS> = _notifications.asSharedFlow()

    /**
     * Connect to WebSocket server
     */
    suspend fun connect() {
        if (_connectionState.value is ConnectionState.Connected ||
            _connectionState.value is ConnectionState.Connecting) {
            Log.d(TAG, "Already connected or connecting")
            return
        }
        
        isManuallyDisconnected = false
        reconnectAttempts = 0
        
        val userId = tokenManager.getUserIdSync()
        if (userId.isNullOrBlank()) {
            Log.e(TAG, "Cannot connect: No userId found")
            _connectionState.value = ConnectionState.Error("User not logged in")
            return
        }
        
        doConnect(userId)
    }
    
    private fun doConnect(userId: String) {
        _connectionState.value = ConnectionState.Connecting
        
        client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .pingInterval(30, TimeUnit.SECONDS)
            .build()
        
        val wsUrl = "$WS_BASE_URL/ws?userId=$userId"
        Log.d(TAG, "Connecting to: $wsUrl")
        
        val request = Request.Builder()
            .url(wsUrl)
            .build()
        
        webSocket = client?.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "✅ WebSocket connected")
                reconnectAttempts = 0
                _connectionState.value = ConnectionState.Connected
            }
            
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "📩 Message received: $text")
                handleMessage(text)
            }
            
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closing: $code - $reason")
                webSocket.close(1000, null)
            }
            
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "❌ WebSocket closed: $code - $reason")
                _connectionState.value = ConnectionState.Disconnected
                
                if (!isManuallyDisconnected) {
                    attemptReconnect(userId)
                }
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "❌ WebSocket error: ${t.message}")
                _connectionState.value = ConnectionState.Error(t.message ?: "Connection failed")
                
                if (!isManuallyDisconnected) {
                    attemptReconnect(userId)
                }
            }
        })
    }
    
    private fun handleMessage(text: String) {
        scope.launch {
            try {
                val jsonElement = JsonParser().parse(text)
                val jsonObject = jsonElement.asJsonObject
                
                // Emit as generic message
                val message = WebSocketMessage(
                    type = jsonObject.get("type")?.asString ?: "UNKNOWN",
                    data = text
                )
                _incomingMessages.emit(message)
                
                // Parse based on message type
                when {
                    // Chat message (has messageId and chatId or conversationId)
                    jsonObject.has("messageId") || jsonObject.has("chatId") -> {
                        val chatMessage = gson.fromJson(text, ChatMessageWS::class.java)
                        _chatMessages.emit(chatMessage)
                    }
                    
                    // Notification (has id and type like NEW_COMMENT, LIKE_POST, etc.)
                    jsonObject.has("type") && jsonObject.has("id") -> {
                        val notification = gson.fromJson(text, NotificationWS::class.java)
                        _notifications.emit(notification)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing message: ${e.message}")
            }
        }
    }
    
    private fun attemptReconnect(userId: String) {
        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            Log.d(TAG, "Max reconnect attempts reached")
            _connectionState.value = ConnectionState.Error("Max reconnect attempts reached")
            return
        }
        
        reconnectAttempts++
        Log.d(TAG, "🔄 Attempting reconnect... ($reconnectAttempts/$MAX_RECONNECT_ATTEMPTS)")
        
        scope.launch {
            delay(RECONNECT_DELAY_MS)
            if (!isManuallyDisconnected) {
                doConnect(userId)
            }
        }
    }
    
    /**
     * Disconnect from WebSocket
     */
    fun disconnect() {
        isManuallyDisconnected = true
        webSocket?.close(1000, "User disconnected")
        webSocket = null
        client?.dispatcher?.executorService?.shutdown()
        client = null
        _connectionState.value = ConnectionState.Disconnected
        Log.d(TAG, "WebSocket disconnected manually")
    }
    
    /**
     * Send a message through WebSocket (if needed)
     */
    fun send(message: String): Boolean {
        return webSocket?.send(message) ?: false
    }
    
    /**
     * Check if connected
     */
    fun isConnected(): Boolean {
        return _connectionState.value is ConnectionState.Connected
    }
}

// Connection states
sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    object Connected : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}

// Generic WebSocket message
data class WebSocketMessage(
    val type: String,
    val data: String
)

// Chat message from WebSocket
data class ChatMessageWS(
    val type: String? = null,
    val messageId: String? = null,
    val chatId: String? = null,
    val conversationId: String? = null,
    val senderId: String? = null,
    val senderName: String? = null,
    val receiverId: String? = null,
    val participantIds: List<String>? = null,
    val message: String? = null,
    val messageType: String? = null, // TEXT, IMAGE, FILE
    val resourceUrls: List<String>? = null,
    val sentAt: String? = null,
    val createdAt: String? = null
)

// Notification from WebSocket
data class NotificationWS(
    val id: String,
    val userId: String? = null,
    val senderId: String? = null,
    val senderName: String? = null,
    val type: String,
    val title: String? = null,
    val content: String? = null,
    val referenceId: String? = null,
    val isRead: Boolean = false,
    val createdAt: String? = null
)
