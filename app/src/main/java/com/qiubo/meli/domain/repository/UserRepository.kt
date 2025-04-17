package com.qiubo.meli.domain.repository

import com.qiubo.meli.data.remote.model.UserResponse

interface UserRepository {
    suspend fun getCurrentUser(): UserResponse
}