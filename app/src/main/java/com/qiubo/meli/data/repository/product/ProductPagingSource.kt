package com.qiubo.meli.data.repository.product

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.qiubo.meli.data.remote.ProductApi
import com.qiubo.meli.model.ItemDetail

class ProductPagingSource(
    private val api: ProductApi,
    private val userId: String,
) : PagingSource<Int, ItemDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemDetail> {
        return try {
            val offset = params.key ?: 0
            val pageSize = params.loadSize

            val searchResponse = api.getUserItems(
                userId = userId,
                offset = offset,
                limit = pageSize
            )
            val ids = searchResponse.results

            if (ids.isEmpty()) {
                return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
            }

            val idsQuery = ids.joinToString(",")
            val details = api.getItemDetails(idsQuery).map { it.body.normalizeUrls() }

            LoadResult.Page(
                data = details,
                prevKey = if (offset == 0) null else offset - pageSize,
                nextKey = if (ids.size < pageSize) null else offset + pageSize
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ItemDetail>): Int? {
        return state.anchorPosition
    }
}
