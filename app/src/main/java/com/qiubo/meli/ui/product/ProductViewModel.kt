package com.qiubo.meli.ui.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiubo.meli.R
import com.qiubo.meli.common.UiFeedback
import com.qiubo.meli.common.normalizeUrl
import com.qiubo.meli.domain.repository.ProductRepository
import com.qiubo.meli.ui.model.ProductViewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(), ProductStateProvider {

    private val _product = MutableStateFlow<ProductViewData?>(null)
    override val product: StateFlow<ProductViewData?> = _product.asStateFlow()

    private val _uiFeedback = MutableStateFlow<UiFeedback>(UiFeedback.Idle)
    override val uiFeedback: StateFlow<UiFeedback> = _uiFeedback.asStateFlow()

    init {
        val productId = checkNotNull(savedStateHandle["productId"]) as String
        fetchProduct(productId)
    }

    private fun fetchProduct(productId: String) {
        viewModelScope.launch {
            try {
                val item = productRepository.getItemById(productId)
                item.let {
                    _product.value = ProductViewData(
                        id = it.id,
                        name = it.title,
                        description = it.permalink,
                        imageUrl = it.thumbnail,
                        pictures = it.pictures.map { pic -> pic.url.normalizeUrl() }
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading product $productId")
                _uiFeedback.value = UiFeedback.Error(R.string.product_loading_error)
            }
        }
    }

    override fun clearUiFeedback() {
        _uiFeedback.value = UiFeedback.Idle
    }
}
