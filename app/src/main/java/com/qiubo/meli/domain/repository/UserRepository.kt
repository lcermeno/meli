package com.qiubo.meli.domain.repository

import com.qiubo.meli.model.UserResponse

interface UserRepository {
    suspend fun getCurrentUser(): UserResponse
}