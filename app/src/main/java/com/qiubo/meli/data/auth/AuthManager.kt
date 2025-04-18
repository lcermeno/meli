package com.qiubo.meli.data.auth

import com.qiubo.meli.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthManager @Inject constructor(
    private val authPreferences: AuthPreferences,
    private val tokenRefresher: TokenRefresher,
    @ApplicationScope private val appScope: CoroutineScope
) : AuthTokenProvider {

    private val _accessTokenFlow = MutableStateFlow<String?>(null)

    override val isLoggedIn: StateFlow<Boolean> = _accessTokenFlow
        .map { !it.isNullOrBlank() }
        .stateIn(appScope, SharingStarted.Eagerly, false)

    override suspend fun fetchAccessToken(code: String) {
        tokenRefresher.getAccessToken(code)
    }

    override suspend fun getAccessToken(): String? = _accessTokenFlow.first()

    override suspend fun loadTokenIntoMemory() {
        _accessTokenFlow.value = authPreferences.getAccessToken()
    }

    override suspend fun logout() {
        _accessTokenFlow.value = null
        authPreferences.clearTokens()
    }
}


