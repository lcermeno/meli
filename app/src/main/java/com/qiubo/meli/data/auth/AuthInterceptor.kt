package com.qiubo.meli.data.auth

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenProvider: AuthTokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val token = runBlocking {
            tokenProvider.getAccessToken()
        }

        token?.takeIf { it.isNotBlank() }?.let {
            requestBuilder.addHeader(AUTHORIZATION_KEY, "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }

    companion object {
        private const val AUTHORIZATION_KEY = "Authorization"
    }
}
