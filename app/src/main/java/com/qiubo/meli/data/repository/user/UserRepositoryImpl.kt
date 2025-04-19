package com.qiubo.meli.data.repository.user

import com.qiubo.meli.data.auth.AuthPreferences
import com.qiubo.meli.data.remote.UserApi
import com.qiubo.meli.data.remote.model.UserResponse
import com.qiubo.meli.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val authPreferences: AuthPreferences
) : UserRepository {

    override suspend fun getCurrentUser(): UserResponse {
        val cachedUserId = authPreferences.getUserId()
        val cachedNickname = authPreferences.getUserNickname()

        return if (!cachedUserId.isNullOrBlank() && cachedNickname != null) {
            UserResponse(id = cachedUserId.toLong(), nickname = cachedNickname)
        } else {
            val user = userApi.getAuthenticatedUser()
            authPreferences.saveUser(user.id.toString(), user.nickname)
            user
        }
    }
}
