
package com.qiubo.meli.domain.repository

import androidx.paging.PagingData
import com.qiubo.meli.data.remote.model.ItemDetail
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getUserItemsWithDetails(userId: Long): Flow<PagingData<ItemDetail>>
    suspend fun getItemById(id: String): ItemDetail
}
