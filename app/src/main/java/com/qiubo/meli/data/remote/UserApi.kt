package com.qiubo.meli.data.remote

import com.qiubo.meli.data.remote.model.UserResponse
import retrofit2.http.GET

interface UserApi {
    @GET("users/me")
    suspend fun getAuthenticatedUser(): UserResponse
}