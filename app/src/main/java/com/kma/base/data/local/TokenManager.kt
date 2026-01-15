package com.kma.base.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenManager(private val context: Context) {
    
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val SESSION_ID_KEY = stringPreferencesKey("session_id")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
    }
    
    // Save tokens
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        sessionId: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[SESSION_ID_KEY] = sessionId
        }
    }
    
    // Save user info
    suspend fun saveUserInfo(
        userId: String,
        email: String,
        name: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_NAME_KEY] = name
        }
    }
    
    // Get access token
    fun getAccessToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }
    }
    
    // Get access token synchronously (for interceptor)
    suspend fun getAccessTokenSync(): String? {
        return context.dataStore.data.first()[ACCESS_TOKEN_KEY]
    }
    
    // Get refresh token
    fun getRefreshToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }
    }
    
    suspend fun getRefreshTokenSync(): String? {
        return context.dataStore.data.first()[REFRESH_TOKEN_KEY]
    }
    
    // Get session ID
    fun getSessionId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[SESSION_ID_KEY]
        }
    }
    
    suspend fun getSessionIdSync(): String? {
        return context.dataStore.data.first()[SESSION_ID_KEY]
    }
    
    // Get user info
    fun getUserId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }
    }
    
    fun getUserEmail(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_EMAIL_KEY]
        }
    }
    
    fun getUserName(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_NAME_KEY]
        }
    }
    
    // Check if user is logged in
    fun isLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY] != null
        }
    }
    
    suspend fun isLoggedInSync(): Boolean {
        return context.dataStore.data.first()[ACCESS_TOKEN_KEY] != null
    }
    
    // Clear all data (logout)
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    // Update access token (for refresh)
    suspend fun updateAccessToken(newAccessToken: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = newAccessToken
        }
    }
}
