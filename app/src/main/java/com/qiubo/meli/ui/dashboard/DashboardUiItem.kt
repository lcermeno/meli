package com.qiubo.meli.ui.dashboard

import androidx.annotation.StringRes
import com.qiubo.meli.ui.model.ProductViewData

sealed class DashboardUiItem {
    data class ProductItem(val product: ProductViewData) : DashboardUiItem()
    data class MessageItem(@StringRes val message: Int) : DashboardUiItem()
}
