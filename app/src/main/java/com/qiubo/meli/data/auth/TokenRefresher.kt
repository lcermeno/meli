package com.qiubo.meli.data.auth

import com.qiubo.meli.BuildConfig
import com.qiubo.meli.data.remote.AuthApi
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefresher @Inject constructor(
    private val authApi: AuthApi,
    private val authPreferences: AuthPreferences
) {

    suspend fun getAccessToken(code: String) {
        return try {
            val response = authApi.getAccessToken(
                grantType = "authorization_code",
                clientId = BuildConfig.APP_ID,
                clientSecret = BuildConfig.CLIENT_SECRET,
                code = code,
                redirectUri = BuildConfig.REDIRECT_URI,
            )
            authPreferences.saveTokens(response.accessToken, response.refreshToken)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun refreshTokenBlocking(): String? = runBlocking {
        val refreshToken = authPreferences.getRefreshToken() ?: return@runBlocking null

        return@runBlocking try {
            val response = authApi.refreshToken(
                grantType = "refresh_token",
                clientId = BuildConfig.APP_ID,
                clientSecret = BuildConfig.CLIENT_SECRET,
                refreshToken = refreshToken
            )
            authPreferences.saveTokens(response.accessToken, response.refreshToken)
            response.accessToken
        } catch (e: Exception) {
            null
        }
    }
}
