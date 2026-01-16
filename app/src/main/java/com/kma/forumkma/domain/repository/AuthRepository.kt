package com.kma.forumkma.domain.repository

import com.kma.forumkma.core.base.Resource
import com.kma.forumkma.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface cho Authentication
 * Interface nằm trong domain layer, implementation trong data layer
 */
interface AuthRepository {
    
    /**
     * Login với username và password
     */
    suspend fun login(username: String, password: String): Resource<User>
    
    /**
     * Register tài khoản mới
     */
    suspend fun register(
        username: String,
        email: String,
        password: String,
        displayName: String
    ): Resource<User>
    
    /**
     * Logout
     */
    suspend fun logout(): Resource<Unit>
    
    /**
     * Lấy thông tin user hiện tại
     */
    suspend fun getCurrentUser(): Resource<User?>
    
    /**
     * Kiểm tra đã login chưa
     */
    suspend fun isLoggedIn(): Boolean
    
    /**
     * Lưu auth token
     */
    suspend fun saveAuthToken(token: String)
    
    /**
     * Lấy auth token
     */
    suspend fun getAuthToken(): String?
    
    /**
     * Clear auth data
     */
    suspend fun clearAuthData()
    
    /**
     * Observe user state (Flow)
     */
    fun observeCurrentUser(): Flow<User?>
}
