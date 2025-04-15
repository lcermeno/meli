package com.qiubo.meli.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable
    data object Login : Route()
    @Serializable
    data object Dashboard : Route()
    @Serializable
    data object Splash : Route()
    @Serializable
    data class Product(val productId: String) : Route()
}