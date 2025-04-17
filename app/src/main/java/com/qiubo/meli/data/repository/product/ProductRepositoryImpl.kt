
package com.qiubo.meli.data.repository.product

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.qiubo.meli.data.remote.ProductApi
import com.qiubo.meli.domain.repository.ProductRepository
import com.qiubo.meli.data.remote.model.ItemDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi
) : ProductRepository {

    override suspend fun getUserItemsWithDetails(userId: Long): Flow<PagingData<ItemDetail>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ProductPagingSource(api, userId) }
        ).flow
    }

    override suspend fun getItemById(id: String): ItemDetail {
        return try {
            api.getItemDetails(ids = id)
                .first().body
                .normalizeUrls()
        } catch (e: Exception) {
            throw IllegalStateException("There is no product with the id $id")
        }
    }
}
