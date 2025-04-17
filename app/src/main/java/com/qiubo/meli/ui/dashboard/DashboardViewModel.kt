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
import com.qiubo.meli.data.remote.model.ItemDetail
import com.qiubo.meli.domain.repository.ProductRepository
import com.qiubo.meli.domain.repository.UserRepository
import com.qiubo.meli.ui.model.ProductViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
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

    private val pagingEventFlow = MutableSharedFlow<PagingEvent>(extraBufferCapacity = 1)

    override fun retry() {
        pagingEventFlow.tryEmit(PagingEvent.RETRY)
    }

    fun refresh() {
        pagingEventFlow.tryEmit(PagingEvent.REFRESH)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val pagingFlow: Flow<PagingData<ItemDetail>> = pagingEventFlow
        .onStart { emit(PagingEvent.REFRESH) }
        .flatMapLatest {
            flow {
                try {
                    clearUiState()
                    val userId = userRepository.getCurrentUser().id
                    val pagingData = productRepository.getUserItemsWithDetails(userId).cachedIn(viewModelScope)
                    emitAll(pagingData)
                } catch (e: Exception) {
                    _uiState.value = UiFeedback.Error(R.string.dashboard_loading_error)
                }
            }
        }

    override fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    override fun clearUiState() {
        _uiState.value = UiFeedback.Idle
    }

    override val pagedProducts: Flow<PagingData<DashboardUiItem>> =
        combine(pagingFlow, _searchQuery) { pagingData, query ->
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

    enum class PagingEvent {
        RETRY,
        REFRESH
    }

}
