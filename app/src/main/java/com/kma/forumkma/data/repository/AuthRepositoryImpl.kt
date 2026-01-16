package com.kma.forumkma.data.repository

import com.kma.forumkma.core.base.Resource
import com.kma.forumkma.core.local.TokenManager
import com.kma.forumkma.core.network.RetrofitClient
import com.kma.forumkma.data.mapper.UserMapper
import com.kma.forumkma.data.remote.api.AuthApiService
import com.kma.forumkma.data.remote.dto.auth.LoginRequestDto
import com.kma.forumkma.data.remote.dto.auth.LogoutRequestDto
import com.kma.forumkma.data.remote.dto.auth.RegisterRequestDto
import com.kma.forumkma.domain.model.User
import com.kma.forumkma.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementation của AuthRepository
 */
class AuthRepositoryImpl(
    private val tokenManager: TokenManager
) : AuthRepository {
    
    private val authApiService: AuthApiService by lazy {
        RetrofitClient.createService<AuthApiService>()
    }
    
    private val _currentUser = MutableStateFlow<User?>(null)
    
    override suspend fun login(username: String, password: String): Resource<User> {
        return try {
            val request = LoginRequestDto(username = username, password = password)
            val response = authApiService.login(request)
            
            if (response.isSuccess && response.data != null) {
                // Save tokens
                tokenManager.saveTokens(
                    accessToken = response.data.accessToken,
                    refreshToken = response.data.refreshToken,
                    sessionId = response.data.sessionId
                )
                
                // Get user info
                val userResponse = authApiService.getCurrentUser()
                if (userResponse.isSuccess && userResponse.data != null) {
                    val user = UserMapper.toDomain(userResponse.data)
                    tokenManager.saveUserId(user.id)
                    _currentUser.value = user
                    Resource.Success(user)
                } else {
                    Resource.Error(userResponse.message)
                }
            } else {
                Resource.Error(response.message.ifBlank { "Đăng nhập thất bại" })
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi kết nối")
        }
    }
    
    override suspend fun register(
        username: String,
        email: String,
        password: String,
        displayName: String
    ): Resource<User> {
        return try {
            val names = displayName.split(" ", limit = 2)
            val request = RegisterRequestDto(
                username = username,
                password = password,
                email = email,
                firstName = names.getOrNull(0),
                lastName = names.getOrNull(1)
            )
            
            val response = authApiService.register(request)
            
            if (response.isSuccess && response.data != null) {
                // Save tokens
                tokenManager.saveTokens(
                    accessToken = response.data.accessToken,
                    refreshToken = response.data.refreshToken,
                    sessionId = response.data.sessionId
                )
                
                // Get user info
                val userResponse = authApiService.getCurrentUser()
                if (userResponse.isSuccess && userResponse.data != null) {
                    val user = UserMapper.toDomain(userResponse.data)
                    tokenManager.saveUserId(user.id)
                    _currentUser.value = user
                    Resource.Success(user)
                } else {
                    Resource.Error(userResponse.message)
                }
            } else {
                Resource.Error(response.message.ifBlank { "Đăng ký thất bại" })
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi kết nối")
        }
    }
    
    override suspend fun logout(): Resource<Unit> {
        return try {
            val sessionId = tokenManager.getSessionIdSync()
            if (!sessionId.isNullOrBlank()) {
                authApiService.logout(LogoutRequestDto(sessionId))
            }
            tokenManager.clearTokens()
            _currentUser.value = null
            Resource.Success(Unit)
        } catch (e: Exception) {
            // Still clear local tokens
            tokenManager.clearTokens()
            _currentUser.value = null
            Resource.Success(Unit)
        }
    }
    
    override suspend fun getCurrentUser(): Resource<User?> {
        return try {
            val response = authApiService.getCurrentUser()
            if (response.isSuccess && response.data != null) {
                val user = UserMapper.toDomain(response.data)
                _currentUser.value = user
                Resource.Success(user)
            } else {
                Resource.Success(null)
            }
        } catch (e: Exception) {
            Resource.Success(null)
        }
    }
    
    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.hasAccessToken()
    }
    
    override suspend fun saveAuthToken(token: String) {
        // Token is saved during login
    }
    
    override suspend fun getAuthToken(): String? {
        return tokenManager.getAccessTokenSync()
    }
    
    override suspend fun clearAuthData() {
        tokenManager.clearTokens()
        _currentUser.value = null
    }
    
    override fun observeCurrentUser(): Flow<User?> {
        return _currentUser.asStateFlow()
    }
}
