
package com.qiubo.meli.domain.repository

import androidx.paging.PagingData
import com.qiubo.meli.model.ItemDetail
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getUserItemsWithDetails(userId: String): Flow<PagingData<ItemDetail>>
    suspend fun getItemById(id: String): ItemDetail
}
