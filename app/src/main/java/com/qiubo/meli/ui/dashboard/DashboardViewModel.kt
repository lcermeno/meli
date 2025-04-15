package com.qiubo.meli.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.map
import com.qiubo.meli.R
import com.qiubo.meli.common.UiFeedback
import com.qiubo.meli.domain.repository.ProductRepository
import com.qiubo.meli.domain.repository.UserRepository
import com.qiubo.meli.model.ItemDetail
import com.qiubo.meli.ui.model.ProductViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) : ViewModel(), DashboardStateProvider {

    private val _searchQuery = MutableStateFlow("")
    override val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<UiFeedback>(UiFeedback.Idle)
    override val uiState: StateFlow<UiFeedback> = _uiState.asStateFlow()

    override fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    override fun clearUiState() {
        _uiState.value = UiFeedback.Idle
    }

    private val basePagingFlow: Flow<PagingData<ItemDetail>> = flow {
        try {
            val userId = userRepository.getCurrentUser().id
            val pagingFlow = productRepository.getUserItemsWithDetails(userId.toString())
            emitAll(pagingFlow)
        } catch (e: CancellationException) {
            Timber.d(e)
        } catch (e: Exception) {
            Timber.e(e)
            _uiState.value = UiFeedback.Error(R.string.dashboard_loading_error)
            emit(PagingData.empty())
        }
    }.cachedIn(viewModelScope)

    override val pagedProducts: Flow<PagingData<DashboardUiItem>> =
        combine(basePagingFlow, _searchQuery) { pagingData, query ->
            val filtered = if (query.isBlank()) {
                pagingData
            } else {
                pagingData.filter {
                    it.title.contains(query, ignoreCase = true)
                }
            }

            filtered.map { item ->
                DashboardUiItem.ProductItem(
                    product = ProductViewData(
                        id = item.id,
                        name = item.title,
                        description = item.permalink,
                        imageUrl = item.thumbnail
                    )
                ) as DashboardUiItem
            }.insertEmptyIfNeeded()
        }

    private fun PagingData<DashboardUiItem>.insertEmptyIfNeeded(): PagingData<DashboardUiItem> {
        return insertSeparators { before, after ->
            if (before == null && after == null) {
                DashboardUiItem.MessageItem(R.string.no_results_found)
            } else {
                null
            }
        }
    }
}
