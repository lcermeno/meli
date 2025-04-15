package com.qiubo.meli.ui.dashboard


import androidx.paging.PagingData
import com.qiubo.meli.common.UiFeedback
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface DashboardStateProvider {
    val uiState: StateFlow<UiFeedback>
    val pagedProducts: Flow<PagingData<DashboardUiItem>>
    val searchQuery: StateFlow<String>
    fun updateSearchQuery(query: String)
    fun clearUiState()
    fun retry()
}
