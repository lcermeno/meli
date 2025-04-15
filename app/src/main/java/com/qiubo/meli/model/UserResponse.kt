package com.qiubo.meli.model


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResponse(
    val id: Long,
    val nickname: String,
    val email: String? = null
)
