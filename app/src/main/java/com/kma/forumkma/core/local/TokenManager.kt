package com.kma.forumkma.core.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Quản lý Auth Tokens
 */
class TokenManager(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "forum_kma_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_SESSION_ID = "session_id"
        private const val KEY_USER_ID = "user_id"
    }
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _isLoggedIn = MutableStateFlow(hasAccessToken())
    val isLoggedIn: Flow<Boolean> = _isLoggedIn.asStateFlow()
    
    // Sync methods for interceptor
    fun getAccessTokenSync(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    
    fun getRefreshTokenSync(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)
    
    fun getSessionIdSync(): String? = prefs.getString(KEY_SESSION_ID, null)
    
    fun getUserIdSync(): String? = prefs.getString(KEY_USER_ID, null)
    
    // Async methods
    suspend fun saveTokens(accessToken: String, refreshToken: String, sessionId: String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putString(KEY_SESSION_ID, sessionId)
            .apply()
        _isLoggedIn.value = true
    }
    
    suspend fun saveUserId(userId: String) {
        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .apply()
    }
    
    suspend fun clearTokens() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_SESSION_ID)
            .remove(KEY_USER_ID)
            .apply()
        _isLoggedIn.value = false
    }
    
    fun hasAccessToken(): Boolean = !getAccessTokenSync().isNullOrBlank()
}
