package com.qiubo.meli.model

import com.squareup.moshi.Json

data class TokenResponse(
    @Json(name = "access_token")
    val accessToken: String,

    @Json(name = "token_type")
    val tokenType: String,

    @Json(name = "expires_in")
    val expiresIn: Int,

    @Json(name = "scope")
    val scope: String,

    @Json(name = "user_id")
    val userId: Long,

    @Json(name = "refresh_token")
    val refreshToken: String
)
