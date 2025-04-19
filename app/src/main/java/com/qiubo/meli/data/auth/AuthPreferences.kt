package com.qiubo.meli.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthPreferences @Inject constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_NICKNAME_KEY = stringPreferencesKey("user_nickname")
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            prefs[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun getAccessToken(): String? = dataStore.data.first()[ACCESS_TOKEN]

    suspend fun getRefreshToken(): String? = dataStore.data.first()[REFRESH_TOKEN]

    suspend fun clearTokens() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN)
            it.remove(REFRESH_TOKEN)
            it.remove(USER_ID_KEY)
            it.remove(USER_NICKNAME_KEY)
        }
    }

    suspend fun saveUser(userId: String, nickname: String) {
        dataStore.edit {
            it[USER_ID_KEY] = userId
            it[USER_NICKNAME_KEY] = nickname
        }
    }

    suspend fun getUserId(): String? = dataStore.data.first()[USER_ID_KEY]

    suspend fun getUserNickname(): String? = dataStore.data.first()[USER_NICKNAME_KEY]
}

