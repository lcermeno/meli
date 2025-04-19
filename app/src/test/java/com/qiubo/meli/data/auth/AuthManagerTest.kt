package com.qiubo.meli.data.auth

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
        val code = "auth_code_123"

        coEvery { tokenRefresher.getAccessToken(code) } just Runs

        authManager.fetchAccessToken(code)

        coVerify(exactly = 1) { tokenRefresher.getAccessToken(code) }
    }

    @Test
    fun `getAccessToken should return current token from flow`() = runTest {
        val token = "access_token_abc"
        authManager.loadTokenIntoMemory()
        coEvery { authPreferences.getAccessToken() } returns token

        authManager.loadTokenIntoMemory()
        val result = authManager.getAccessToken()

        assertEquals(token, result)
    }

    @Test
    fun `loadTokenIntoMemory should update the flow value from preferences`() = runTest {
        val token = "access_token_xyz"
        coEvery { authPreferences.getAccessToken() } returns token

        authManager.loadTokenIntoMemory()

        assertEquals(token, authManager.getAccessToken())
    }

    @Test
    fun `logout should clear token and preferences`() = runTest {
        val token = "token_to_clear"
        coEvery { authPreferences.getAccessToken() } returns token
        coEvery { authPreferences.clearTokens() } just Runs

        authManager.loadTokenIntoMemory()
        authManager.logout()

        assertNull(authManager.getAccessToken())
        coVerify { authPreferences.clearTokens() }
    }
}
