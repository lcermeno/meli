package com.qiubo.meli.ui.product

import com.qiubo.meli.common.UiFeedback
import com.qiubo.meli.ui.model.ProductViewData
import kotlinx.coroutines.flow.StateFlow

interface ProductStateProvider {
    val uiFeedback:  StateFlow<UiFeedback>
    val product: StateFlow<ProductViewData?>
    fun clearUiFeedback()
}
