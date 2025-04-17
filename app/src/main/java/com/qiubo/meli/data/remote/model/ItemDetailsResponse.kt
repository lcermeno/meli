package com.qiubo.meli.data.remote.model

import com.squareup.moshi.Json

data class ItemDetailsResponse(
    val code: Int,
    val body: ItemDetail
)

data class ItemDetail(
    val id: String,
    @Json(name = "site_id") val siteId: String,
    val title: String,
    val price: Double,
    @Json(name = "currency_id") val currencyId: String,
    @Json(name = "available_quantity") val availableQuantity: Int,
    @Json(name = "sold_quantity") val soldQuantity: Int,
    val condition: String,
    val permalink: String,
    val thumbnail: String,
    val pictures: List<Picture>,
    @Json(name = "sale_terms") val saleTerms: List<SaleTerm> = emptyList(),
    val attributes: List<Attribute> = emptyList()
)

data class Picture(
    val id: String,
    val url: String,
    @Json(name = "secure_url") val secureUrl: String
)

data class SaleTerm(
    val id: String,
    val name: String,
    @Json(name = "value_name") val valueName: String?,
    @Json(name = "value_struct") val valueStruct: ValueStruct?,
    @Json(name = "value_type") val valueType: String
)

data class ValueStruct(
    val number: Double?,
    val unit: String?
)

data class Attribute(
    val id: String,
    val name: String,
    @Json(name = "value_name") val valueName: String?,
    @Json(name = "value_type") val valueType: String
)
