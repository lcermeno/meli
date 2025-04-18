package com.qiubo.meli.data.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthTokenProvider {
    val isLoggedIn: StateFlow<Boolean>

    suspend fun fetchAccessToken(code: String)
    suspend fun getAccessToken(): String?
    suspend fun loadTokenIntoMemory()
    suspend fun logout()

}
