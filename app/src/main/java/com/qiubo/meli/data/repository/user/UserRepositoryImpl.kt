package com.qiubo.meli.data.repository.user

import com.qiubo.meli.data.remote.UserApi
import com.qiubo.meli.domain.repository.UserRepository
import com.qiubo.meli.data.remote.model.UserResponse
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
): UserRepository {
    override suspend fun getCurrentUser(): UserResponse {
        return userApi.getAuthenticatedUser()
    }
}