package com.qiubo.meli.data.remote

import com.qiubo.meli.model.ItemDetailsResponse
import com.qiubo.meli.model.UserItemsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    @GET("users/{user_id}/items/search")
    suspend fun getUserItems(
        @Path("user_id") userId: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 20
    ): UserItemsResponse

    @GET("items")
    suspend fun getItemDetails(
        @Query("ids") ids: String // Comma-separated item IDs (e.g. MLA2017715060,MLA1979193484)
    ): List<ItemDetailsResponse>
}
