package com.qiubo.meli.data.repository.product

import com.qiubo.meli.data.auth.AuthManager
import com.qiubo.meli.data.auth.AuthPreferences
import com.qiubo.meli.data.auth.TokenRefresher
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import kotlin.test.BeforeTest
import kotlin.test.Test

class AuthManagerTest {

    private val authPreferences = mockk<AuthPreferences>(relaxed = true)
    private val tokenRefresher = mockk<TokenRefresher>(relaxed = true)
    private val testScope = CoroutineScope(StandardTestDispatcher())

    private lateinit var authManager: AuthManager

    @BeforeTest
    fun setUp() {
        authManager = AuthManager(authPreferences, tokenRefresher, testScope)
    }

    @Test
    fun `fetchAccessToken should delegate to tokenRefresher`() = runTest {
        // Given
        val code = "auth_code_123"
        coEvery { tokenRefresher.getAccessToken(code) } just Runs

        // When
        authManager.fetchAccessToken(code)

        // Then
        coVerify(exactly = 1) { tokenRefresher.getAccessToken(code) }
    }

    @Test
    fun `getAccessToken should return current token from flow`() = runTest {
        // Given
        val token = "access_token_abc"
        coEvery { authPreferences.getAccessToken() } returns token
        authManager.loadTokenIntoMemory()

        // When
        val result = authManager.getAccessToken()

        // Then
        assertEquals(token, result)
    }

    @Test
    fun `loadTokenIntoMemory should update the flow value from preferences`() = runTest {
        // Given
        val token = "access_token_xyz"
        coEvery { authPreferences.getAccessToken() } returns token

        // When
        authManager.loadTokenIntoMemory()

        // Then
        assertEquals(token, authManager.getAccessToken())
    }

    @Test
    fun `logout should clear token and preferences`() = runTest {
        // Given
        val token = "access_token_logout"
        coEvery { authPreferences.getAccessToken() } returns token
        authManager.loadTokenIntoMemory()

        // When
        authManager.logout()

        // Then
        assertNull(authManager.getAccessToken())
        coVerify { authPreferences.clearTokens() }
    }
}

