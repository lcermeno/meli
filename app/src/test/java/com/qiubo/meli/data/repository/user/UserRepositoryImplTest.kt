package com.qiubo.meli.data.repository.user

import com.qiubo.meli.data.auth.AuthPreferences
import com.qiubo.meli.data.remote.UserApi
import com.qiubo.meli.data.remote.model.UserResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class UserRepositoryImplTest {

    private val userApi = mockk<UserApi>()
    private val authPreferences = mockk<AuthPreferences>(relaxed = true)
    private lateinit var repository: UserRepositoryImpl

    @BeforeEach
    fun setUp() {
        repository = UserRepositoryImpl(userApi, authPreferences)
    }

    @Test
    fun `getCurrentUser should return cached user if available`() = runTest {
        // Given
        coEvery { authPreferences.getUserId() } returns "123"
        coEvery { authPreferences.getUserNickname() } returns "qiubo"

        // When
        val result = repository.getCurrentUser()

        // Then
        assertEquals(UserResponse(id = 123, nickname = "qiubo"), result)
        coVerify(exactly = 0) { userApi.getAuthenticatedUser() }
    }

    @Test
    fun `getCurrentUser should fetch from API and cache when no local user`() = runTest {
        // Given
        coEvery { authPreferences.getUserId() } returns null
        coEvery { authPreferences.getUserNickname() } returns null
        val expectedUser = UserResponse(id = 123, nickname = "qiubo")
        coEvery { userApi.getAuthenticatedUser() } returns expectedUser

        // When
        val result = repository.getCurrentUser()

        // Then
        assertEquals(expectedUser, result)
        coVerify { userApi.getAuthenticatedUser() }
        coVerify { authPreferences.saveUser("123", "qiubo") }
    }
}

