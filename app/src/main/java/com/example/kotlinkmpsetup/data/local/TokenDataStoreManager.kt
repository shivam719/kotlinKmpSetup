package com.example.kotlinkmpsetup.data.local

import android.content.Context
import androidx.core.content.edit
import com.example.kotlinkmpsetup.domain.model.AuthToken
import com.example.kotlinkmpsetup.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking

/**
 * ====================================================================================
 * CLEAN ARCHITECTURE LAYER: DATA (Local Storage Manager)
 * ====================================================================================
 * Handles Bearer token & user profile session persistence, retrieval, and auto-logout on session expiry.
 */
class TokenDataStoreManager(private val context: Context) {

    private val prefs by lazy {
        context.getSharedPreferences("aura_auth_prefs", Context.MODE_PRIVATE)
    }

    private val _accessTokenFlow = MutableStateFlow<String?>(null)
    val accessTokenFlow: Flow<String?> = _accessTokenFlow.asStateFlow()

    private val _refreshTokenFlow = MutableStateFlow<String?>(null)
    val refreshTokenFlow: Flow<String?> = _refreshTokenFlow.asStateFlow()

    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent: SharedFlow<Unit> = _sessionExpiredEvent.asSharedFlow()

    init {
        _accessTokenFlow.value = getAccessToken()
        _refreshTokenFlow.value = getRefreshToken()
    }

    suspend fun saveUserSession(user: User) {
        saveTokens(user.token.accessToken, user.token.refreshToken)
        prefs.edit {
            putString("KEY_USER_ID", user.id)
            putString("KEY_USER_NAME", user.fullName)
            putString("KEY_USER_EMAIL", user.email)
        }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        _accessTokenFlow.value = accessToken
        _refreshTokenFlow.value = refreshToken
        prefs.edit {
            putString("KEY_ACCESS_TOKEN", accessToken)
            putString("KEY_REFRESH_TOKEN", refreshToken)
        }
    }

    fun getAccessToken(): String? {
        return _accessTokenFlow.value ?: prefs.getString("KEY_ACCESS_TOKEN", null)
    }

    fun getRefreshToken(): String? {
        return _refreshTokenFlow.value ?: prefs.getString("KEY_REFRESH_TOKEN", null)
    }

    fun isLoggedIn(): Boolean {
        return !getAccessToken().isNull_or_blank_compat()
    }

    private fun String?.isNull_or_blank_compat(): Boolean = this == null || this.trim().isEmpty()

    fun getSavedUser(): User? {
        val id = prefs.getString("KEY_USER_ID", null) ?: return null
        val name = prefs.getString("KEY_USER_NAME", "User") ?: "User"
        val email = prefs.getString("KEY_USER_EMAIL", "") ?: ""
        val token = getAccessToken() ?: ""
        val refresh = getRefreshToken() ?: ""

        return User(
            id = id,
            fullName = name,
            email = email,
            token = AuthToken(accessToken = token, refreshToken = refresh)
        )
    }

    fun notifySessionExpired() {
        runBlocking {
            clearTokens()
        }
        _sessionExpiredEvent.tryEmit(Unit)
    }

    suspend fun clearTokens() {
        _accessTokenFlow.value = null
        _refreshTokenFlow.value = null
        prefs.edit {
            clear()
        }
    }
}
