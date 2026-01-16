package com.kma.base.data.repository

import com.kma.base.data.api.*
import com.kma.base.data.model.*
import com.kma.base.data.network.NetworkModule

class AuthRepository {
    private val api = NetworkModule.authApi
    private val userApi = NetworkModule.userApi
    private val tokenManager = NetworkModule.getTokenManager()
    
    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(username, password))
            if (response.code == "200" && response.result != null) {
                // Save tokens
                tokenManager.saveTokens(
                    accessToken = response.result.accessToken,
                    refreshToken = response.result.refreshToken,
                    sessionId = response.result.sessionId
                )
                
                // Fetch user info after login
                try {
                    val userResponse = userApi.getMe()
                    if (userResponse.code == "200" && userResponse.result != null) {
                        tokenManager.saveUserInfo(
                            userId = userResponse.result.id,
                            email = userResponse.result.email,
                            name = userResponse.result.fullName
                        )
                    }
                } catch (e: Exception) {
                    // Continue even if user fetch fails
                }
                
                Result.success(response.result)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String?,
        lastName: String?
    ): Result<AuthResponse> {
        return try {
            val response = api.register(
                RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    firstName = firstName,
                    lastName = lastName
                )
            )
            if (response.code == "200" && response.result != null) {
                // Save tokens
                tokenManager.saveTokens(
                    accessToken = response.result.accessToken,
                    refreshToken = response.result.refreshToken,
                    sessionId = response.result.sessionId
                )
                
                // Fetch user info after register
                try {
                    val userResponse = userApi.getMe()
                    if (userResponse.code == "200" && userResponse.result != null) {
                        tokenManager.saveUserInfo(
                            userId = userResponse.result.id,
                            email = userResponse.result.email,
                            name = userResponse.result.fullName
                        )
                    }
                } catch (e: Exception) {
                    // Continue even if user fetch fails
                }
                
                Result.success(response.result)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout(): Result<Boolean> {
        return try {
            val sessionId = tokenManager.getSessionIdSync()
            if (sessionId != null) {
                val response = api.logout(mapOf("sessionId" to sessionId))
                tokenManager.clearAll()
                Result.success(response.code == "200")
            } else {
                tokenManager.clearAll()
                Result.success(true)
            }
        } catch (e: Exception) {
            // Clear tokens even if API call fails
            tokenManager.clearAll()
            Result.failure(e)
        }
    }
    
    suspend fun refreshToken(): Result<AuthResponse> {
        return try {
            val refreshToken = tokenManager.getRefreshTokenSync()
            if (refreshToken != null) {
                val response = api.refreshToken(mapOf("refreshToken" to refreshToken))
                if (response.code == "200" && response.result != null) {
                    // Update tokens
                    tokenManager.saveTokens(
                        accessToken = response.result.accessToken,
                        refreshToken = response.result.refreshToken,
                        sessionId = response.result.sessionId
                    )
                    Result.success(response.result)
                } else {
                    Result.failure(Exception(response.message))
                }
            } else {
                Result.failure(Exception("No refresh token available"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedInSync()
    }
}

class UserRepository {
    private val api = NetworkModule.userApi
    
    suspend fun getMe(): Result<UserResponse> {
        return try {
            val response = api.getMe()
            if (response.code == "200" && response.result != null) {
                Result.success(response.result)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserById(userId: String): Result<UserResponse> {
        return try {
            val response = api.getUserById(userId)
            if (response.code == "200" && response.result != null) {
                Result.success(response.result)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchUsers(keyword: String?, page: Int = 0): Result<PageResponse<UserResponse>> {
        return try {
            val response = api.searchUsers(keyword, page)
            if (response.code == "200" && response.result != null) {
                Result.success(response.result)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class PostRepository {
    private val api = NetworkModule.postApi
    
    suspend fun getFeed(page: Int = 0, limit: Int = 10, sort: String = "createdAt,DESC"): Result<PageResponse<PostWithInteractionResponse>> {
        return try {
            val response = api.getFeed(page, limit, sort)
            if (response.code == "200" && response.result != null) {
                Result.success(response.result)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getGroupFeed(groupId: String, page: Int = 0, limit: Int = 10): Result<PageResponse<PostWithInteractionResponse>> {
        return try {
            val response = api.getGroupFeed(groupId, page, limit)
            if (response.code == "200" && response.result != null) {
                Result.success(response.result)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPostById(postId: String): Result<PostResponse> {
        return try {
            val response = api.getPostById(postId)
            if (response.code == "200" && response.result != null) {
                Result.success(response.result)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createPost(request: CreatePostRequest): Result<PostResponse> {
        return try {
            val response = api.createPost(request)
            if (response.code == "200" && response.result != null) {
                Result.success(response.result)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deletePost(postId: String): Result<Boolean> {
        return try {
            val response = api.deletePost(postId)
            Result.success(response.code == "200")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class ChatRepository {
    private val api = NetworkModule.chatApi
    
    suspend fun getConversations(): Result<List<ConversationResponse>> {
        return try {
            val response = api.getConversations()
            if (response.code == "200" && response.result != null) {
                Result.success(response.result)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMessages(conversationId: String): Result<List<ChatMessageResponse>> {
        return try {
            val response = api.getMessages(conversationId)
            if (response.code == "200" && response.result != null) {
                Result.success(response.result.content)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sendMessage(
        conversationId: String?,
        receiverId: String?,
        groupId: String?,
        message: String,
        type: String = "TEXT",
        resourceUrls: List<String>? = null
    ): Result<ChatMessageResponse> {
        return try {
            val request = SendMessageRequest(
                conversationId = conversationId,
                receiverId = receiverId,
                groupId = groupId,
                message = message,
                type = type,
                resourceUrls = resourceUrls
            )
            val response = api.sendMessage(request)
            if (response.code == "200" && response.result != null) {
                Result.success(response.result)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun markAsRead(conversationId: String): Result<Boolean> {
        return try {
            val response = api.markAsRead(conversationId)
            Result.success(response.code == "200")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
