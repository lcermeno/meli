package com.qiubo.meli.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import java.io.File
import kotlin.test.Test
import kotlin.test.assertNull

class AuthPreferencesTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var authPreferences: AuthPreferences

    @BeforeEach
    fun setUp() {
        val testScope = CoroutineScope(Dispatchers.IO + Job())

        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { File.createTempFile("test_prefs", ".preferences_pb") }
        )

        authPreferences = AuthPreferences(dataStore)
    }

    @Test
    fun `saveTokens should store access and refresh tokens`() = runTest {
        // Given
        val accessToken = "access123"
        val refreshToken = "refresh456"

        // When
        authPreferences.saveTokens(accessToken, refreshToken)

        // Then
        assertEquals(accessToken, authPreferences.getAccessToken())
        assertEquals(refreshToken, authPreferences.getRefreshToken())
    }

    @Test
    fun `clearTokens should remove tokens`() = runTest {
        // Given
        authPreferences.saveTokens("access123", "refresh456")

        // When
        authPreferences.clearTokens()

        // Then
        assertNull(authPreferences.getAccessToken())
        assertNull(authPreferences.getRefreshToken())
    }

    @Test
    fun `saveUserId and getUserId should persist and retrieve value`() = runTest {
        // Given
        val userId = "789"
        val nickname = "qiubo"

        // When
        authPreferences.saveUser(userId, nickname)

        // Then
        assertEquals(userId, authPreferences.getUserId())
    }

    @Test
    fun `saveUserNickname and getUserNickname should persist and retrieve value`() = runTest {
        // Given
        val userId = "789"
        val nickname = "qiubo"

        // When
        authPreferences.saveUser(userId, nickname)

        // Then
        assertEquals(nickname, authPreferences.getUserNickname())
    }
}


