package com.qiubo.meli.data.remote.model

import com.squareup.moshi.Json

data class UserItemsResponse(
    @Json(name = "seller_id")
    val sellerId: String,

    @Json(name = "results")
    val results: List<String>,

    @Json(name = "paging")
    val paging: Paging,

    @Json(name = "query")
    val query: String?,

    @Json(name = "orders")
    val orders: List<SimpleOrder>,

    @Json(name = "available_orders")
    val availableOrders: List<AvailableOrder>
)

data class Paging(
    @Json(name = "limit")
    val limit: Int,

    @Json(name = "offset")
    val offset: Int,

    @Json(name = "total")
    val total: Int
)

data class SimpleOrder(
    @Json(name = "id")
    val id: String,

    @Json(name = "name")
    val name: String
)

data class AvailableOrder(
    @Json(name = "id")
    val id: Any,

    @Json(name = "name")
    val name: String
)
